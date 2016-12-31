package controllers.scheduler.task;

import api.ServerConstants;
import dao.JobPostWorkFlowDAO;
import dao.RecruiterCreditHistoryDAO;
import models.entity.OM.JobPostWorkflow;
import models.entity.Recruiter.RecruiterProfile;
import models.entity.Recruiter.Static.RecruiterCreditCategory;
import models.entity.RecruiterCreditHistory;
import play.Logger;

import java.util.*;

import static controllers.businessLogic.RecruiterService.addCredits;
import static controllers.businessLogic.RecruiterService.debitCredits;

/**
 * Created by dodo on 26/12/16.
 */

/**
 * EOD task at 7pm to auto deduct interview credits for all the recruiter who had confirmed interviews today.
 *
 * */

public class EODDebitCreditInterviewCreditTask extends TimerTask {

    private static void startCreditDebitTask(Map<RecruiterProfile, Integer> recruiterCreditMap, Boolean isDebit){
        new Thread(() -> {

            //in this map, we have recruiterProfile as key and valus as no of credits to be debited
            for (Map.Entry<RecruiterProfile, Integer> entry : recruiterCreditMap.entrySet()) {

                //getting key and value
                RecruiterProfile recruiterProfile = entry.getKey();
                Integer creditCount = entry.getValue();

                Logger.info("Recruiter: " + recruiterProfile.getRecruiterProfileName() + " | Credits: " + creditCount);


                if (isDebit) {

                    //debits credits from pack
                    debitCredits(recruiterProfile, ServerConstants.RECRUITER_CATEGORY_INTERVIEW_UNLOCK, creditCount * (-1));
                } else {

                    //credit the oldest active pack
                    addCredits(recruiterProfile, ServerConstants.RECRUITER_CATEGORY_INTERVIEW_UNLOCK, creditCount);
                }
            }
        }).start();
    }

    private static void expireRecruiterInterviewCredits(){
        new Thread(() -> {

            List<RecruiterCreditHistory> packList = RecruiterCreditHistoryDAO.getAllPacksExpiringToday();

            Integer creditsExpiring;
            for(RecruiterCreditHistory pack : packList){
                creditsExpiring = pack.getRecruiterCreditsAvailable();
                pack.setRecruiterCreditsAvailable(0);
                pack.setRecruiterCreditsUsed(pack.getRecruiterCreditsUsed() + creditsExpiring);
                pack.setCreditIsExpired(true);
                pack.update();
            }
        }).start();
    }


    @Override
    public void run() {
        // fetch all today's interviews
        Logger.info("Starting EOD auto debit interview credits ...");

        //getting list of today's confirmed interview
        List<JobPostWorkflow> jobPostWorkflowList = JobPostWorkFlowDAO.getTodaysConfirmedInterviews();

        Map<RecruiterProfile, Integer> recruiterCreditMap = returnMapData(jobPostWorkflowList);

        //debiting credits
        startCreditDebitTask(recruiterCreditMap, true);

        jobPostWorkflowList = JobPostWorkFlowDAO.getAllTodaysFeedbackApplications();

        recruiterCreditMap = returnMapData(jobPostWorkflowList);

        //crediting credits if feedback provided
        startCreditDebitTask(recruiterCreditMap, false);

        //task to expire all the interview credits which are expiring today
        expireRecruiterInterviewCredits();
    }

    public static Map<RecruiterProfile, Integer> returnMapData(List<JobPostWorkflow> jobPostWorkflowList){

        Map<RecruiterProfile, Integer> recruiterCreditMap = new HashMap<RecruiterProfile, Integer>();

        for(JobPostWorkflow jobPostWorkflow : jobPostWorkflowList){
            if(jobPostWorkflow.getJobPost().getRecruiterProfile() != null){
                RecruiterProfile recruiterProfile = jobPostWorkflow.getJobPost().getRecruiterProfile();

                Integer count = recruiterCreditMap.get(recruiterProfile);
                recruiterCreditMap.put(recruiterProfile, (count == null) ? 1 : count + 1);
            }
        }

        return recruiterCreditMap;
    }
}