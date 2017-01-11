package controllers.scheduler.task;

import api.ServerConstants;
import api.http.httpResponse.Recruiter.AddRecruiterResponse;
import controllers.businessLogic.RecruiterService;
import controllers.scheduler.SchedulerManager;
import dao.JobPostWorkFlowDAO;
import dao.RecruiterCreditHistoryDAO;
import models.entity.OM.JobPostWorkflow;
import models.entity.Recruiter.RecruiterProfile;
import models.entity.RecruiterCreditHistory;
import models.entity.scheduler.SchedulerStats;
import models.entity.scheduler.Static.SchedulerSubType;
import models.entity.scheduler.Static.SchedulerType;
import org.apache.commons.logging.Log;
import play.Logger;

import java.sql.Timestamp;
import java.util.*;

import static controllers.businessLogic.RecruiterService.addCredits;
import static controllers.businessLogic.RecruiterService.debitCredits;
import static controllers.scheduler.SchedulerConstants.*;
import static play.mvc.Controller.session;

/**
 * Created by dodo on 26/12/16.
 */

/**
 * EOD task at 7pm to auto deduct interview credits for all the recruiter who had confirmed interviews today.
 *
 * */

public class EODDebitCreditInterviewCreditTask extends TimerTask {
    private String createdBy = "EOD_TASK";
    private final ClassLoader classLoader;

    public EODDebitCreditInterviewCreditTask(ClassLoader classLoader){
        this.classLoader = classLoader;
    }

    private void startCreditDebitTask(Map<RecruiterProfile, Integer> recruiterToInterviewCountMap, Boolean isDebit){

        new Thread(() -> {

            SchedulerSubType subType = SchedulerSubType.find.where()
                    .eq("schedulerSubTypeId", SCHEDULER_SUB_TYPE_EOD_CREDIT_DEBIT_TASK)
                    .findUnique();

            SchedulerType type = SchedulerType.find.where()
                    .eq("schedulerTypeId", SCHEDULER_TYPE_SMS).findUnique();

            Timestamp startTime = new Timestamp(System.currentTimeMillis());

            //in this map, we have recruiterProfile as key and values as no of credits to be debited
            for (Map.Entry<RecruiterProfile, Integer> entry : recruiterToInterviewCountMap.entrySet()) {

                //getting key and value
                RecruiterProfile recruiterProfile = entry.getKey();
                Integer creditCount = entry.getValue();

                RecruiterCreditHistory history = RecruiterCreditHistoryDAO.getOldestActivePackByCategory(recruiterProfile,
                        ServerConstants.RECRUITER_CATEGORY_INTERVIEW_UNLOCK);

                if(history != null){
                    if (isDebit) {
                        Logger.info("Debiting " + creditCount + " no. of interview credits for Recruiter: " + recruiterProfile.getRecruiterProfileName()
                                + " | " + recruiterProfile.getRecruiterProfileId());

                        creditCount = creditCount * (-1);

                    } else {

                        Logger.info("Adding " + creditCount + " no. of interview credits for Recruiter: " + recruiterProfile.getRecruiterProfileName()
                                + " | " + recruiterProfile.getRecruiterProfileId());

                    }

                    AddRecruiterResponse recruiterResponse = RecruiterService.updateExistingRecruiterPack(recruiterProfile,
                            history.getRecruiterCreditPackNo(), creditCount, createdBy);

                    if(recruiterResponse.getStatus() == AddRecruiterResponse.STATUS_SUCCESS){
                        Logger.info("Credit/debit for " + creditCount + " credit units was successful for recruiter: " + recruiterProfile.getRecruiterProfileName()
                        + " - ID: " + recruiterProfile.getRecruiterProfileId());
                    }
                } else{

                    Logger.info("Creating a new interview pack for recruiter: " + recruiterProfile.getRecruiterProfileName() + " as no pack is active");
                    //creating a new interview pack and setting the credit value in negative
                    addCredits(recruiterProfile, ServerConstants.RECRUITER_CATEGORY_INTERVIEW_UNLOCK, creditCount, createdBy);
                }
            }

            //saving stats for sms event
            String note = "Credit/debit of interview credits";
            Logger.info("Credit/debit of interview credits for recruiter completed.");

            Timestamp endTime = new Timestamp(System.currentTimeMillis());
            SchedulerManager.saveNewSchedulerStats(startTime, type, subType, note, endTime, true);

        }).start();
    }

    private static void expireRecruiterInterviewCredits(){
        new Thread(() -> {

            List<RecruiterCreditHistory> packList = RecruiterCreditHistoryDAO.getAllInterviewPacksExpiringToday();

            Logger.info("Expiring total " + packList.size() + " packs today");
            for(RecruiterCreditHistory pack : packList){
                RecruiterCreditHistory newHistory = new RecruiterCreditHistory();
                RecruiterService.copyCreditObject(newHistory, pack);

                newHistory.setCreditIsExpired(true);
                newHistory.setLatest(true);
                newHistory.setUnits(0);

                pack.setLatest(false);
                pack.update();

                newHistory.save();

            }
        }).start();
    }


    @Override
    public void run() {
        Thread.currentThread().setContextClassLoader(classLoader);

        // Determine if this task is required to launch
        boolean shouldRunThisTask = false;

        SchedulerStats schedulerStats = SchedulerStats.find.where()
                .eq("schedulerType.schedulerTypeId", SCHEDULER_TYPE_SMS)
                .eq("schedulerSubType.schedulerSubTypeId", SCHEDULER_SUB_TYPE_EOD_CREDIT_DEBIT_TASK)
                .orderBy().desc("startTimestamp").setMaxRows(1).findUnique();

        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        Date today = cal.getTime();

        if(schedulerStats == null) {
            // task has definitely not yet running so run it
            Logger.info("scheduler status is null for EOD credit/debit/expire credit task...");
            shouldRunThisTask = true;

        } else {
            if(schedulerStats.getEndTimestamp().getDate() != today.getDate()) {

                //task was not executed today
                shouldRunThisTask = true;
            }
        }

        if(shouldRunThisTask) {

            // fetch all today's interviews
            Logger.info("Starting EOD auto debit interview credits ...");

            //getting list of today's confirmed interview
            List<JobPostWorkflow> jobPostWorkflowList = JobPostWorkFlowDAO.getTodaysConfirmedInterviews();

            Map<RecruiterProfile, Integer> recruiterToCreditCountMap = returnMapData(jobPostWorkflowList);

            //debiting credits
            startCreditDebitTask(recruiterToCreditCountMap, true);

            jobPostWorkflowList = JobPostWorkFlowDAO.getAllTodaysFeedbackApplications();

            Map<RecruiterProfile, Integer> recruiterToInterviewCountMap = returnMapData(jobPostWorkflowList);

            //crediting credits if feedback provided
            startCreditDebitTask(recruiterToInterviewCountMap, false);

            //task to expire all the interview credits which are expiring today
            expireRecruiterInterviewCredits();

        }
    }

    private static Map<RecruiterProfile, Integer> returnMapData(List<JobPostWorkflow> jobPostWorkflowList){

        Map<RecruiterProfile, Integer> recruiterToCreditCountMap = new HashMap<RecruiterProfile, Integer>();

        for(JobPostWorkflow jobPostWorkflow : jobPostWorkflowList){
            if(jobPostWorkflow.getJobPost().getRecruiterProfile() != null){
                RecruiterProfile recruiterProfile = jobPostWorkflow.getJobPost().getRecruiterProfile();

                Integer count = recruiterToCreditCountMap.get(recruiterProfile);
                recruiterToCreditCountMap.put(recruiterProfile, (count == null) ? 1 : count + 1);
            }
        }

        return recruiterToCreditCountMap;
    }
}