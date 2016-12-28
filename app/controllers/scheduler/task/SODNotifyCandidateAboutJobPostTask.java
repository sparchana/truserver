package controllers.scheduler.task;

import api.http.httpResponse.CandidateWorkflowData;
import controllers.businessLogic.JobService;
import controllers.businessLogic.JobWorkflow.JobPostWorkflowEngine;
import controllers.scheduler.SchedulerManager;
import models.entity.JobPost;
import models.entity.scheduler.SchedulerStats;
import models.entity.scheduler.Static.SchedulerSubType;
import models.entity.scheduler.Static.SchedulerType;
import models.util.NotificationUtil;
import models.util.SmsUtil;
import play.Logger;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;
import java.util.TimerTask;

import static controllers.scheduler.SchedulerConstants.*;

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

                    SchedulerSubType subType = SchedulerSubType.find.where()
                            .eq("schedulerSubTypeId", SCHEDULER_SUB_TYPE_CANDIDATE_SOD_JOB_ALERT)
                            .findUnique();

                    SchedulerType type = SchedulerType.find.where()
                            .eq("schedulerTypeId", SCHEDULER_TYPE_SMS).findUnique();

                    SchedulerType typeFcm = SchedulerType.find.where()
                            .eq("schedulerTypeId", SCHEDULER_TYPE_FCM).findUnique();

                    //adding to notification Handler queue
                    for (Map.Entry<Long, CandidateWorkflowData> candidate : candidateSearchMap.entrySet()) {
                        if(hasCredit){

                            //recruiter has interview credits
                            Timestamp startTime = new Timestamp(System.currentTimeMillis());

                            String note = "SMS alert for New Job alert.";

                            SchedulerStats newSchedulerStats = new SchedulerStats();
                            newSchedulerStats.setStartTimestamp(new Timestamp(System.currentTimeMillis()) );

                            //sending sms
                            SmsUtil.sendSODJobPostInfoSmsToCandidate(jobPost, candidate.getValue().getCandidate(), hasCredit);

                            Timestamp endTime = new Timestamp(System.currentTimeMillis());
                            SchedulerManager.saveNewSchedulerStats(startTime, type, subType, note, endTime, true);

                            /* Notification part */
                            startTime = new Timestamp(System.currentTimeMillis());

                            note = "Android notification alert for New Job alert.";

                            newSchedulerStats = new SchedulerStats();
                            newSchedulerStats.setStartTimestamp(new Timestamp(System.currentTimeMillis()) );

                            //sending notification
                            NotificationUtil.sendJobPostNotificationToCandidate(jobPost, candidate.getValue().getCandidate(), hasCredit);

                            endTime = new Timestamp(System.currentTimeMillis());
                            SchedulerManager.saveNewSchedulerStats(startTime, typeFcm, subType, note, endTime, true);
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