package controllers.scheduler.task;

import controllers.Global;
import controllers.scheduler.SchedulerConstants;
import api.ServerConstants;
import controllers.scheduler.SchedulerManager;
import models.entity.OM.JobPostWorkflow;
import models.entity.scheduler.SchedulerStats;
import models.entity.scheduler.Static.SchedulerSubType;
import models.entity.scheduler.Static.SchedulerType;
import models.util.SmsUtil;
import notificationService.NotificationEvent;
import notificationService.SMSEvent;
import play.Logger;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.*;

import static api.ServerConstants.SOURCE_INTERNAL;
import static controllers.scheduler.SchedulerConstants.SCHEDULER_SUB_TYPE_SAME_DAY_INTERVIEW;
import static controllers.scheduler.SchedulerConstants.SCHEDULER_TYPE_SMS;

/**
 * Created by zero on 12/12/16.
 */
public class SameDayInterviewAlertTask extends TimerTask {
    private final Date mXHrBack;
    private final int mXHr;
    private final ClassLoader classLoader;

    public SameDayInterviewAlertTask(int x, ClassLoader classLoader){
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.add(Calendar.HOUR, - x);
        mXHrBack = cal.getTime();
        this.mXHr = x;
        this.classLoader = classLoader;
    }

    @Override
    public void run() {
        Logger.info("SDI Alert started...");
        // Determine if this task is required to launch
        boolean shouldRunThisTask = false;

        // ebean already has loaded all the classes we are trying to access. and when we try to get access to that
        // class from a different thread that has a different class loader, it is throwing issues
        Thread.currentThread().setContextClassLoader(classLoader);

        SchedulerStats schedulerStats = SchedulerStats.find.where()
                .eq("schedulerType.schedulerTypeId", SCHEDULER_TYPE_SMS)
                .eq("schedulerSubType.schedulerSubTypeId", SCHEDULER_SUB_TYPE_SAME_DAY_INTERVIEW)
                .orderBy().desc("startTimestamp").setMaxRows(1).findUnique();

        if(schedulerStats == null) {
            // task has definitely not yet running so run it
            shouldRunThisTask = true;

        } else {
            if(schedulerStats.getEndTimestamp().getHours() < mXHrBack.getHours()) {
                // last run was 'x++' hr back, hence re run
                shouldRunThisTask = true;
            }
        }

        if(shouldRunThisTask) {
            Timestamp startTime = new Timestamp(System.currentTimeMillis());
            SchedulerSubType subType = SchedulerSubType.find.where()
                    .eq("schedulerSubTypeId", SCHEDULER_SUB_TYPE_SAME_DAY_INTERVIEW)
                    .findUnique();
            SchedulerType type = SchedulerType.find.where()
                    .eq("schedulerTypeId", SCHEDULER_TYPE_SMS).findUnique();
            String note = "Interview SMS alert for same day interview.";

            SimpleDateFormat sdf = new SimpleDateFormat(ServerConstants.SDF_FORMAT_YYYYMMDD);

            // get all jobpostworkflow items which are having interview due today at 'x' hr from current time

            List<JobPostWorkflow> jobPostWorkflowList =
                    JobPostWorkflow.find.where()
                            .eq("status.statusId", ServerConstants.JWF_STATUS_INTERVIEW_CONFIRMED)
                            .eq("scheduledInterviewDate", sdf.format(new Date()))
                            .eq("scheduledInterviewTimeSlot.interviewTimeSlotId", getSlotIdFromCurrentTime(mXHr))
                            .findList();

            // candidate (can have repetitive candidate) who have interview in 'x' hr from now.
            for(JobPostWorkflow jobPostWorkflow: jobPostWorkflowList) {
                if(jobPostWorkflow.getJobPost().getSource() != SOURCE_INTERNAL){
                    continue;
                }
                // create sms event and keep appending to the Queue
                Logger.info("same day: adding " +  jobPostWorkflow.getCandidate().getCandidateId() +" to queue");

                NotificationEvent notificationEvent =
                        new SMSEvent(jobPostWorkflow.getCandidate().getCandidateMobile(),
                                SmsUtil.getSameDayInterviewAlertSmsString(jobPostWorkflow));

                Global.getmNotificationHandler().addToQueue(notificationEvent);
            }


            Timestamp endTime = new Timestamp(System.currentTimeMillis());

            SchedulerManager.saveNewSchedulerStats(startTime, type, subType, note, endTime, true);
        }
    }

    private int getSlotIdFromCurrentTime(int xHr) {

        // from current time figure out which interview time slot id needs to be returned
        if ((new Date()).getHours() + xHr >= 18){
            return 0;
        } else if((new Date()).getHours() + xHr >= 16) {
            return SchedulerConstants.INTERVIEW_TIME_SLOT_4_PM;
        } else if((new Date()).getHours() + xHr >= 13) {
            return SchedulerConstants.INTERVIEW_TIME_SLOT_1_PM;
        } else if((new Date()).getHours() + xHr >= 10) {
            return SchedulerConstants.INTERVIEW_TIME_SLOT_10_AM;
        } else {
            return 0;
        }
    }
}
