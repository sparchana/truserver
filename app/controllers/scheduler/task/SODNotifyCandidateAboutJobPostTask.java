package controllers.scheduler.task;

import controllers.businessLogic.JobService;
import models.entity.JobPost;
import play.Logger;

import java.util.List;
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
                sendSmsToCandidateMatchingWithJobPost(jobPost);
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