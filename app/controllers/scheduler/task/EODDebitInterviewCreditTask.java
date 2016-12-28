package controllers.scheduler.task;

import api.ServerConstants;
import controllers.businessLogic.RecruiterService;
import dao.JobPostWorkFlowDAO;
import dao.RecruiterCreditHistoryDAO;
import dao.RecruiterCreditPackDAO;
import models.entity.OM.JobPostWorkflow;
import models.entity.Recruiter.RecruiterCreditPack;
import models.entity.Recruiter.RecruiterProfile;
import models.entity.Recruiter.Static.RecruiterCreditCategory;
import models.entity.RecruiterCreditHistory;
import play.Logger;

import java.util.*;

import static play.mvc.Controller.session;
import static play.mvc.Results.ok;

/**
 * Created by dodo on 26/12/16.
 */

/**
 * EOD task at 7pm to auto deduct interview credits for all the recruiter who had confirmed interviews today.
 *
 * */

public class EODDebitInterviewCreditTask extends TimerTask {

    private void startCreditDebitTask(Map<RecruiterProfile, Integer> recruiterCreditMap, Boolean isDebit){
        new Thread(() -> {

            RecruiterCreditCategory category = RecruiterCreditCategory.find.where()
                    .eq("recruiter_credit_category_id", ServerConstants.RECRUITER_CATEGORY_INTERVIEW_UNLOCK)
                    .findUnique();

            //in this map, we have recruiterProfile as key and valus as no of credits to be debited
            for (Map.Entry<RecruiterProfile, Integer> entry : recruiterCreditMap.entrySet()) {

                //getting key and value
                RecruiterProfile recruiterProfile = entry.getKey();
                Integer creditCount = entry.getValue();

                Logger.info("Recruiter: " + recruiterProfile.getRecruiterProfileName() + " | Credits: " + creditCount);

                //getting all the credit packs of the recruiter
                List<RecruiterCreditPack> packList = RecruiterCreditPackDAO.getInterviewCreditPackById(recruiterProfile,
                        ServerConstants.RECRUITER_CATEGORY_INTERVIEW_UNLOCK);

                if(isDebit){

                    //debits credits from pack
                    RecruiterService.debitCreditsFromPack(creditCount, packList);
                } else{

                    //credit the oldest active pack
                    for(RecruiterCreditPack pack : packList){
                        if(pack.getCreditsAvailable() > 0){
                            if(!pack.getCreditIsExpired()){
                                pack.setCreditsAvailable(pack.getCreditsAvailable() + creditCount);
                                pack.update();
                                break;
                            }
                        }
                    }
                }

                //making an entry in recruiter credit history table
                RecruiterCreditHistory recruiterCreditHistory = new RecruiterCreditHistory();
                recruiterCreditHistory.setRecruiterProfile(recruiterProfile);

                recruiterCreditHistory.setRecruiterCreditCategory(category);

                //fetching recruiter latest credit history from recruiter credit history table
                RecruiterCreditHistory recruiterCreditHistoryLatest = RecruiterCreditHistoryDAO.getRecruiterLatestCreditHistoryById(
                        recruiterProfile, ServerConstants.RECRUITER_CATEGORY_INTERVIEW_UNLOCK);

                if(recruiterCreditHistoryLatest != null){
                    if(recruiterCreditHistoryLatest.getRecruiterCreditsAvailable() > 0){
                        if(isDebit){

                            //TODO: to discuss negative credit case
                            recruiterCreditHistory.setRecruiterCreditsAvailable(recruiterCreditHistoryLatest.getRecruiterCreditsAvailable() - creditCount);
                            recruiterCreditHistory.setRecruiterCreditsUsed(recruiterCreditHistoryLatest.getRecruiterCreditsUsed() + creditCount);
                            recruiterCreditHistory.setUnits(-creditCount);
                        } else{
                            recruiterCreditHistory.setRecruiterCreditsAvailable(recruiterCreditHistoryLatest.getRecruiterCreditsAvailable() + creditCount);
                            recruiterCreditHistory.setRecruiterCreditsUsed(recruiterCreditHistoryLatest.getRecruiterCreditsUsed());
                            recruiterCreditHistory.setUnits(creditCount);
                        }

                        if(session().get("sessionUsername") != null){
                            recruiterCreditHistory.setRecruiterCreditsAddedBy("Support: " + session().get("sessionUsername"));
                        } else{
                            recruiterCreditHistory.setRecruiterCreditsAddedBy("Not specified");
                        }

                        //saving new row
                        recruiterCreditHistory.save();
                    }
                }
            }
        }).start();
    }

    private void expireRecruiterInterviewCredits(){
        new Thread(() -> {

            List<RecruiterCreditPack> packList = RecruiterCreditPackDAO.getAllPacksExpiringToday();

            RecruiterCreditCategory recruiterCreditCategory = RecruiterCreditCategory.find.where()
                    .eq("recruiter_credit_category_id", ServerConstants.RECRUITER_CATEGORY_INTERVIEW_UNLOCK)
                    .findUnique();

            Integer creditsExpiring;
            for(RecruiterCreditPack pack : packList){
                creditsExpiring = pack.getCreditsAvailable();
                pack.setCreditsAvailable(0);
                pack.setCreditsUsed(pack.getCreditsUsed() + creditsExpiring);
                pack.setCreditIsExpired(true);
                pack.update();

                //making an entry in recruiter credit history table
                RecruiterCreditHistory recruiterCreditHistory = new RecruiterCreditHistory();
                recruiterCreditHistory.setRecruiterProfile(pack.getRecruiterProfile());


                if(recruiterCreditCategory != null){
                    recruiterCreditHistory.setRecruiterCreditCategory(recruiterCreditCategory);
                }

                //getting latest record of the credit history table
                RecruiterCreditHistory recruiterCreditHistoryLatest = RecruiterCreditHistoryDAO.getRecruiterLatestCreditHistoryById(
                        pack.getRecruiterProfile(), ServerConstants.RECRUITER_CATEGORY_INTERVIEW_UNLOCK);

                if(recruiterCreditHistoryLatest != null){
                    if(recruiterCreditHistoryLatest.getRecruiterCreditsAvailable() > 0){
                        recruiterCreditHistory.setRecruiterCreditsAvailable(recruiterCreditHistoryLatest.getRecruiterCreditsAvailable() - creditsExpiring);
                        recruiterCreditHistory.setRecruiterCreditsUsed(recruiterCreditHistoryLatest.getRecruiterCreditsUsed() + creditsExpiring);
                        recruiterCreditHistory.setUnits(-creditsExpiring);

                        if(session().get("sessionUsername") != null){
                            recruiterCreditHistory.setRecruiterCreditsAddedBy("Support: " + session().get("sessionUsername"));
                        } else{
                            recruiterCreditHistory.setRecruiterCreditsAddedBy("Not specified");
                        }

                        //saving/updating all the rows
                        recruiterCreditHistory.save();
                    }
                }
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