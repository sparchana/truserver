package controllers.scheduler.task;

import controllers.scheduler.SchedulerManager;
import dao.JobPostWorkFlowDAO;
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
    private final ClassLoader classLoader;

    public EODCandidateCompletedInterviewTask(ClassLoader classLoader){

        this.classLoader = classLoader;
    }

    private void sendRateUsNotification(List<JobPostWorkflow> jobPostWorkflowList){
        new Thread(() -> {

            Logger.info("Sending alert to " + jobPostWorkflowList.size() + " candidates to rate us on play store");
            
            SchedulerSubType subType = SchedulerSubType.find.where()
                    .eq("schedulerSubTypeId", SCHEDULER_SUB_TYPE_CANDIDATE_EOD_RATE_US)
                    .findUnique();

            SchedulerType type = SchedulerType.find.where()
                    .eq("schedulerTypeId", SCHEDULER_TYPE_SMS).findUnique();


            SchedulerType typeFcm = SchedulerType.find.where()
                    .eq("schedulerTypeId", SCHEDULER_TYPE_FCM).findUnique();

            Timestamp startTime = new Timestamp(System.currentTimeMillis());

            for(JobPostWorkflow jpwf : jobPostWorkflowList){

                //sending sms
                SmsUtil.sendEODCandidateFeedbackSms(jpwf.getJobPost(), jpwf.getCandidate());

                //sending notification
                NotificationUtil.EODCandidateFeedbackNotification(jpwf.getJobPost(), jpwf.getCandidate());

            }

            //saving stats for sms event
            String note = "SMS alert for candidate to rate us on play store after interview.";

            Timestamp endTime = new Timestamp(System.currentTimeMillis());
            SchedulerManager.saveNewSchedulerStats(startTime, type, subType, note, endTime, true);

            //saving stats for fcm event
            note = "Android notification alert for candidate to rate us on play store after interview.";

            endTime = new Timestamp(System.currentTimeMillis());

            SchedulerManager.saveNewSchedulerStats(startTime, typeFcm, subType, note, endTime, true);

            Logger.info("[Completed] Sending alert to " + jobPostWorkflowList.size() + " candidates to rate us on play store");
        }).start();
    }

    @Override
    public void run() {

        Thread.currentThread().setContextClassLoader(classLoader);

        // Determine if this task is required to launch
        boolean shouldRunThisTask = false;

        SchedulerStats schedulerStats = SchedulerStats.find.where()
                .eq("schedulerType.schedulerTypeId", SCHEDULER_TYPE_SMS)
                .eq("schedulerSubType.schedulerSubTypeId", SCHEDULER_SUB_TYPE_CANDIDATE_EOD_RATE_US)
                .orderBy().desc("startTimestamp").setMaxRows(1).findUnique();

        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        Date today = cal.getTime();

        if(schedulerStats == null) {
            // task has definitely not yet running so run it
            Logger.info("scheduler status is null for EOD rate us.");
            shouldRunThisTask = true;

        } else {
            if(schedulerStats.getEndTimestamp().getDate() != today.getDate()) {

                //task was not executed today
                shouldRunThisTask = true;
            }
        }

        if(shouldRunThisTask){
            // fetch all the application which had interviews today
            Logger.info("Starting EOD notify candidates for play store rating ..");

            sendRateUsNotification(JobPostWorkFlowDAO.getTodaysConfirmedInterviews());
        }
    }
}