package controllers.scheduler.task;

import api.ServerConstants;
import dao.JobPostWorkFlowDAO;
import models.entity.Candidate;
import models.entity.OM.JobPostWorkflow;
import models.util.NotificationUtil;
import models.util.SmsUtil;
import play.Logger;

import java.util.*;

/**
 * Created by dodo on 24/12/16.
 */

/**
 * EOD task at 6pm for all the candidate who had interview today.
 * Send notification and sms to rate us on play store
 *
 * */

public class EODCandidateCompletedInterviewTask extends TimerTask {

    private void sendRateUsNotification(List<JobPostWorkflow> jobPostWorkflowList){
        new Thread(() -> {
            for(JobPostWorkflow jpwf : jobPostWorkflowList){
                SmsUtil.EODSmsToCandidatePostInterview(jpwf.getJobPost(), jpwf.getCandidate());
                NotificationUtil.EODNotificationToCandidatePostInterview(jpwf.getJobPost(), jpwf.getCandidate());
            }
        }).start();
    }

    @Override
    public void run() {
        // fetch all the jobPost whose recruiter has interview credits
        Logger.info("Starting EOD notify candidates for play store rating ..");

        Calendar now = Calendar.getInstance();
        String todayDate = now.get(Calendar.YEAR) + "-" + (now.get(Calendar.MONTH) + 1) + "-" + now.get(Calendar.DATE);

        List<JobPostWorkflow> jobPostWorkflowList = JobPostWorkflow.find.where()
                .eq("scheduled_interview_date", todayDate)
                .eq("status_id", ServerConstants.JWF_STATUS_INTERVIEW_CONFIRMED)
                .findList();

        sendRateUsNotification(jobPostWorkflowList);
    }
}