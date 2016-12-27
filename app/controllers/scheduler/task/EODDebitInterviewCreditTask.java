package controllers.scheduler.task;

import api.ServerConstants;
import dao.JobPostWorkFlowDAO;
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

    private void startCreditDebitTask(List<RecruiterProfile>recruiterProfileList, Boolean isDebit){
        new Thread(() -> {

            RecruiterCreditCategory recruiterCreditCategory = RecruiterCreditCategory.find.where()
                    .eq("recruiter_credit_category_id", ServerConstants.RECRUITER_CATEGORY_INTERVIEW_UNLOCK)
                    .findUnique();


            for(RecruiterProfile recruiterProfile : recruiterProfileList){
                List<RecruiterCreditPack> packList = RecruiterCreditPack.find.where()
                        .eq("RecruiterProfileId", recruiterProfile.getRecruiterProfileId())
                        .eq("RecruiterCreditCategory", ServerConstants.RECRUITER_CATEGORY_INTERVIEW_UNLOCK)
                        .findList();

                //making an entry in recruiter credit history table
                RecruiterCreditHistory recruiterCreditHistory = new RecruiterCreditHistory();
                recruiterCreditHistory.setRecruiterProfile(recruiterProfile);

                if(recruiterCreditCategory != null){
                    recruiterCreditHistory.setRecruiterCreditCategory(recruiterCreditCategory);
                }


                if(isDebit){
                    for(RecruiterCreditPack pack : packList){
                        if(pack.getCreditsAvailable() > 0){
                            if(!pack.getCreditIsExpired()){
                                pack.setCreditsAvailable(pack.getCreditsAvailable() - 1);
                                pack.setCreditsUsed(pack.getCreditsUsed() + 1);
                                pack.update();
                                break;
                            }
                        }
                    }

                    RecruiterCreditHistory recruiterCreditHistoryLatest = RecruiterCreditHistory.find.where()
                            .eq("RecruiterProfileId", recruiterProfile.getRecruiterProfileId())
                            .eq("RecruiterCreditCategory", ServerConstants.RECRUITER_CATEGORY_INTERVIEW_UNLOCK)
                            .setMaxRows(1)
                            .orderBy("create_timestamp desc")
                            .findUnique();

                    if(recruiterCreditHistoryLatest != null){
                        if(recruiterCreditHistoryLatest.getRecruiterCreditsAvailable() > 0){
                            recruiterCreditHistory.setRecruiterCreditsAvailable(recruiterCreditHistoryLatest.getRecruiterCreditsAvailable() - 1);
                            recruiterCreditHistory.setRecruiterCreditsUsed(recruiterCreditHistoryLatest.getRecruiterCreditsUsed() + 1);
                            recruiterCreditHistory.setUnits(-1);

                            if(session().get("sessionUsername") != null){
                                recruiterCreditHistory.setRecruiterCreditsAddedBy("Support: " + session().get("sessionUsername"));
                            } else{
                                recruiterCreditHistory.setRecruiterCreditsAddedBy("Not specified");
                            }

                            //saving/updating all the rows
                            recruiterCreditHistory.save();
                        }
                    }
                } else{
                    //credit
                    for(RecruiterCreditPack pack : packList){
                        if(pack.getCreditsAvailable() > 0){
                            if(!pack.getCreditIsExpired()){
                                pack.setCreditsAvailable(pack.getCreditsAvailable() + 1);
                                pack.update();
                                break;
                            }
                        }
                    }

                    RecruiterCreditHistory recruiterCreditHistoryLatest = RecruiterCreditHistory.find.where()
                            .eq("RecruiterProfileId", recruiterProfile.getRecruiterProfileId())
                            .eq("RecruiterCreditCategory", ServerConstants.RECRUITER_CATEGORY_INTERVIEW_UNLOCK)
                            .setMaxRows(1)
                            .orderBy("create_timestamp desc")
                            .findUnique();

                    if(recruiterCreditHistoryLatest != null){
                        if(recruiterCreditHistoryLatest.getRecruiterCreditsAvailable() > 0){
                            recruiterCreditHistory.setRecruiterCreditsAvailable(recruiterCreditHistoryLatest.getRecruiterCreditsAvailable() + 1);
                            recruiterCreditHistory.setRecruiterCreditsUsed(recruiterCreditHistoryLatest.getRecruiterCreditsUsed());
                            recruiterCreditHistory.setUnits(1);

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
            }
        }).start();
    }

    private void expireRecruiterInterviewCredits(){
        new Thread(() -> {
            Calendar today = Calendar.getInstance();
            today.set(Calendar.HOUR_OF_DAY, 0);

            String dateToday = today.get(Calendar.YEAR) + "-" + (today.get(Calendar.MONTH)+1) + "-" + today.get(Calendar.DATE);
            List<RecruiterCreditPack> packList = RecruiterCreditPack.find.where()
                    .eq("expiryDate", dateToday)
                    .eq("RecruiterCreditCategory", ServerConstants.RECRUITER_CATEGORY_INTERVIEW_UNLOCK)
                    .findList();

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

                RecruiterCreditCategory recruiterCreditCategory = RecruiterCreditCategory.find.where()
                        .eq("recruiter_credit_category_id", ServerConstants.RECRUITER_CATEGORY_INTERVIEW_UNLOCK)
                        .findUnique();

                if(recruiterCreditCategory != null){
                    recruiterCreditHistory.setRecruiterCreditCategory(recruiterCreditCategory);
                }

                RecruiterCreditHistory recruiterCreditHistoryLatest = RecruiterCreditHistory.find.where()
                        .eq("RecruiterProfileId", pack.getRecruiterProfile().getRecruiterProfileId())
                        .eq("RecruiterCreditCategory", ServerConstants.RECRUITER_CATEGORY_INTERVIEW_UNLOCK)
                        .setMaxRows(1)
                        .orderBy("create_timestamp desc")
                        .findUnique();

                if(recruiterCreditHistoryLatest != null){
                    if(recruiterCreditHistoryLatest.getRecruiterCreditsAvailable() > 0){
                        recruiterCreditHistory.setRecruiterCreditsAvailable(recruiterCreditHistoryLatest.getRecruiterCreditsAvailable() - creditsExpiring);
                        recruiterCreditHistory.setRecruiterCreditsUsed(recruiterCreditHistoryLatest.getRecruiterCreditsUsed() + creditsExpiring);
                        recruiterCreditHistory.setUnits(creditsExpiring);

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

        List<RecruiterProfile> recruiterProfileList = new ArrayList<>();

        Calendar now = Calendar.getInstance();
        String todayDate = now.get(Calendar.YEAR) + "-" + (now.get(Calendar.MONTH) + 1) + "-" + now.get(Calendar.DATE);

        List<JobPostWorkflow> jobPostWorkflowList = JobPostWorkflow.find.where()
                .eq("scheduled_interview_date", todayDate)
                .eq("status_id", ServerConstants.JWF_STATUS_INTERVIEW_CONFIRMED)
                .findList();

        for(JobPostWorkflow jobPostWorkflow : jobPostWorkflowList){
            if(jobPostWorkflow.getJobPost().getRecruiterProfile() != null){
                recruiterProfileList.add(jobPostWorkflow.getJobPost().getRecruiterProfile());
            }
        }

        //debiting credits
        startCreditDebitTask(recruiterProfileList, true);

        jobPostWorkflowList = JobPostWorkFlowDAO.getAllTodaysFeedbackApplications();

        for(JobPostWorkflow jobPostWorkflow : jobPostWorkflowList){
            if(jobPostWorkflow.getJobPost().getRecruiterProfile() != null){
                recruiterProfileList.add(jobPostWorkflow.getJobPost().getRecruiterProfile());
            }
        }

        //crediting credits if feedback provided
        startCreditDebitTask(recruiterProfileList, false);

        //task to expire all the interview credits which are expiring today
        expireRecruiterInterviewCredits();
    }
}