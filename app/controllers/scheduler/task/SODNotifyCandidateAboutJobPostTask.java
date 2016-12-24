package controllers.scheduler.task;

import api.http.httpResponse.CandidateWorkflowData;
import controllers.businessLogic.JobService;
import controllers.businessLogic.JobWorkflow.JobPostWorkflowEngine;
import models.entity.JobPost;
import models.util.NotificationUtil;
import models.util.SmsUtil;
import play.Logger;

import java.util.List;
import java.util.Map;
import java.util.TimerTask;

import static controllers.businessLogic.JobService.sendSmsToCandidateMatchingWithJobPost;

/**
 * Created by dodo on 23/12/16.
 */

/**
 * SOD task at 9am for all jobs of recruiters that have interview credits.
 * Send notification and sms to all candidates matching
 *
 * */
public class SODNotifyCandidateAboutJobPostTask extends TimerTask {

    private void sendJobPostAlert(List<JobPost> jobPostList){
        new Thread(() -> {
            for(JobPost jobPost : jobPostList){
                Map<Long, CandidateWorkflowData> candidateSearchMap = JobPostWorkflowEngine.getCandidateForRecruiterSearch(
                        null, //age
                        null, //min salary
                        null, //max salary
                        jobPost.getGender(), //gender
                        null, //experience
                        jobPost.getJobRole().getJobRoleId(), //jobRole
                        null, //education
                        null, //locality
                        null, //language list
                        20.00);

                if(candidateSearchMap != null){
                    Logger.info("Sending notification to " + candidateSearchMap.size() + " candidates regarding the jobPost: " + jobPost.getJobPostTitle());

                    Boolean hasCredit = false;
                    if(jobPost.getRecruiterProfile().totalInterviewCredits() > 0){
                        hasCredit = true;
                    }

                    //adding to notification Handler queue
                    for (Map.Entry<Long, CandidateWorkflowData> candidate : candidateSearchMap.entrySet()) {
                        if(hasCredit){
                            //recruiter has interview credits
                            SmsUtil.SODSmsToCandidateRecHasCredits(jobPost, candidate.getValue().getCandidate());
                            NotificationUtil.SODNotificationToCandidateRecHasCredits(jobPost, candidate.getValue().getCandidate());
                        }
                    }
                }
            }

        }).start();
    }

    @Override
    public void run() {
        // fetch all the jobPost whose recruiter has interview credits
        Logger.info("Starting SOD notify matching candidates..");

        sendJobPostAlert(JobService.getAllJobPostWithRecruitersWithInterviewCredits());
    }
}