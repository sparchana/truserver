package controllers.scheduler.task;

import api.ServerConstants;
import controllers.businessLogic.RecruiterService;
import dao.JobPostWorkFlowDAO;
import dao.RecruiterCreditHistoryDAO;
import models.entity.OM.JobPostWorkflow;
import models.entity.Recruiter.RecruiterProfile;
import models.entity.RecruiterCreditHistory;
import play.Logger;

import java.util.*;

import static controllers.businessLogic.RecruiterService.addCredits;
import static controllers.businessLogic.RecruiterService.debitCredits;
import static play.mvc.Controller.session;

/**
 * Created by dodo on 26/12/16.
 */

/**
 * EOD task at 7pm to auto deduct interview credits for all the recruiter who had confirmed interviews today.
 *
 * */

public class EODDebitCreditInterviewCreditTask extends TimerTask {
    private String createdBy = "Not specified";

    private void startCreditDebitTask(Map<RecruiterProfile, Integer> recruiterToInterviewCountMap, Boolean isDebit){

        new Thread(() -> {

        //in this map, we have recruiterProfile as key and values as no of credits to be debited
        for (Map.Entry<RecruiterProfile, Integer> entry : recruiterToInterviewCountMap.entrySet()) {

            //getting key and value
            RecruiterProfile recruiterProfile = entry.getKey();
            Integer creditCount = entry.getValue();

            if (isDebit) {
                Logger.info("Debiting " + creditCount + " no. of interview credits for Recruiter: " + recruiterProfile.getRecruiterProfileName()
                                + " | " + recruiterProfile.getRecruiterProfileId());


                //debits credits from pack
                debitCredits(recruiterProfile, ServerConstants.RECRUITER_CATEGORY_INTERVIEW_UNLOCK, creditCount * (-1), createdBy);
            } else {

                Logger.info("Adding " + creditCount + " no. of interview credits for Recruiter: " + recruiterProfile.getRecruiterProfileName()
                        + " | " + recruiterProfile.getRecruiterProfileId());

                RecruiterCreditHistory history = RecruiterCreditHistoryDAO.getOldestActivePack(recruiterProfile);
                if(history != null){

                    //credit the oldest active pack
                    RecruiterService.updateExistingRecruiterPack(recruiterProfile, history.getRecruiterCreditPackNo(),
                            creditCount);

                }
            }
        }

        }).start();

    }

    private static void expireRecruiterInterviewCredits(){
        new Thread(() -> {

            List<RecruiterCreditHistory> packList = RecruiterCreditHistoryDAO.getAllInterviewPacksExpiringToday();

            for(RecruiterCreditHistory pack : packList){
                pack.setCreditIsExpired(true);
                pack.update();
            }
        }).start();
    }


    @Override
    public void run() {
        // fetch all today's interviews
        Logger.info("Starting EOD auto debit interview credits ...");

        if(session().get("sessionUsername") != null){
            createdBy = "Support: " + session().get("sessionUsername");
        }

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