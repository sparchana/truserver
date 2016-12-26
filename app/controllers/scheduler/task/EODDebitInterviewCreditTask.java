package controllers.scheduler.task;

import api.ServerConstants;
import dao.JobPostWorkFlowDAO;
import models.entity.OM.JobPostWorkflow;
import models.entity.Recruiter.RecruiterProfile;
import play.Logger;

import java.util.*;

/**
 * Created by dodo on 26/12/16.
 */

/**
 * EOD task at 7pm to auto deduct interview credits for all the recruiter who had confirmed interviews today.
 *
 * */

public class EODDebitInterviewCreditTask extends TimerTask {

    private void startCreditDebitTast(List<RecruiterProfile>recruiterProfileList){
        new Thread(() -> {
            //TODO: start auto deduct task
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

        startCreditDebitTast(recruiterProfileList);
    }
}