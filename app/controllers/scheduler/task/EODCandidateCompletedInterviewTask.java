package controllers.scheduler.task;

import api.ServerConstants;
import controllers.scheduler.SchedulerManager;
import models.entity.OM.JobPostWorkflow;
import models.entity.scheduler.SchedulerStats;
import models.entity.scheduler.Static.SchedulerSubType;
import models.entity.scheduler.Static.SchedulerType;
import models.util.NotificationUtil;
import models.util.SmsUtil;
import play.Logger;

import java.sql.Timestamp;
import java.util.*;

import static controllers.scheduler.SchedulerConstants.*;

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
                //recruiter has interview credits
                Timestamp startTime = new Timestamp(System.currentTimeMillis());
                SchedulerSubType subType = SchedulerSubType.find.where()
                        .eq("schedulerSubTypeId", SCHEDULER_SUB_TYPE_CANDIDATE_EOD_RATE_US)
                        .findUnique();

                SchedulerType type = SchedulerType.find.where()
                        .eq("schedulerTypeId", SCHEDULER_TYPE_SMS).findUnique();

                String note = "SMS alert for candidate to rate us on play store after interview.";

                SchedulerStats newSchedulerStats = new SchedulerStats();
                newSchedulerStats.setStartTimestamp(new Timestamp(System.currentTimeMillis()) );

                //sending sms
                SmsUtil.sendEODSmsToCandidatePostInterview(jpwf.getJobPost(), jpwf.getCandidate());

                Timestamp endTime = new Timestamp(System.currentTimeMillis());
                SchedulerManager.saveNewSchedulerStats(startTime, type, subType, note, endTime, true);

                            /* Notification part */
                startTime = new Timestamp(System.currentTimeMillis());
                subType = SchedulerSubType.find.where()
                        .eq("schedulerSubTypeId", SCHEDULER_SUB_TYPE_CANDIDATE_EOD_RATE_US)
                        .findUnique();

                type = SchedulerType.find.where()
                        .eq("schedulerTypeId", SCHEDULER_TYPE_FCM).findUnique();

                note = "Android notification alert for candidate to rate us on play store after interview.";

                newSchedulerStats = new SchedulerStats();
                newSchedulerStats.setStartTimestamp(new Timestamp(System.currentTimeMillis()) );

                //sending notification
                NotificationUtil.sendEODNotificationToCandidatePostInterview(jpwf.getJobPost(), jpwf.getCandidate());

                endTime = new Timestamp(System.currentTimeMillis());
                SchedulerManager.saveNewSchedulerStats(startTime, type, subType, note, endTime, true);


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