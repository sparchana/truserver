package controllers.scheduler.task;

import controllers.SharedSettings;
import controllers.scheduler.SchedulerConstants;
import api.ServerConstants;
import models.entity.OM.JobPostWorkflow;
import models.entity.scheduler.Scheduler;
import models.entity.scheduler.Static.SchedulerSubType;
import models.entity.scheduler.Static.SchedulerType;
import models.util.SmsUtil;
import notificationService.NotificationEvent;
import notificationService.SMSEvent;
import play.Logger;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.*;

import static controllers.scheduler.SchedulerConstants.SCHEDULER_SUB_TYPE_SAME_DAY_INTERVIEW;
import static controllers.scheduler.SchedulerConstants.SCHEDULER_TYPE_SMS;

/**
 * Created by zero on 12/12/16.
 */
public class SameDayInterviewAlertTask extends TimerTask {
    private final Date xHrBack;
    private final int xhr;

    public SameDayInterviewAlertTask(int x){
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.add(Calendar.HOUR, cal.getTime().getHours() - x);
        xHrBack = cal.getTime();
        this.xhr = x;
    }

    @Override
    public void run() {
        Logger.info("SDI Alert started...");
        // Determine if this task is required to launch
        boolean shouldRunThisTask = false;

        Scheduler scheduler = Scheduler.find.where()
                .eq("schedulerType.schedulerTypeId", SCHEDULER_TYPE_SMS)
                .eq("schedulerSubType.schedulerSubTypeId", SCHEDULER_SUB_TYPE_SAME_DAY_INTERVIEW)
                .orderBy().desc("startTimestamp").setMaxRows(1).findUnique();

        if(scheduler == null) {
            // task has definitely not yet running so run it
            shouldRunThisTask = true;

        } else {
            if(scheduler.getStartTimestamp().before(xHrBack)) {
                // last run was 'x++' hr back, hence re run
                shouldRunThisTask = true;
            }
        }

        if(shouldRunThisTask) {

            Scheduler newScheduler = new Scheduler();

            newScheduler.setStartTimestamp(new Timestamp(System.currentTimeMillis()) );

            SimpleDateFormat sdf = new SimpleDateFormat(ServerConstants.SDF_FORMAT_YYYYMMDD);

            // get all jobpostworkflow items whose interview is today
            List<JobPostWorkflow> jobPostWorkflowList =
                    JobPostWorkflow.find.where()
                            .eq("status.statusId", ServerConstants.JWF_STATUS_INTERVIEW_CONFIRMED)
                            .eq("scheduledInterviewDate", sdf.format(new Date()))
                            .eq("scheduledInterviewTimeSlot.interviewTimeSlotId", getSlotIdFromCurrentTime(xhr))
                            .findList();

            // candidate (can have repetitive candidate) who have interview in 'x' hr from now.
            for(JobPostWorkflow jobPostWorkflow: jobPostWorkflowList) {
                // create sms event and keep appending to the Queue
                Logger.info("same day: adding " +  jobPostWorkflow.getCandidate().getCandidateId() +" to queue");

                NotificationEvent notificationEvent =
                        new SMSEvent(jobPostWorkflow.getCandidate().getCandidateMobile(),
                                SmsUtil.getSameDayInterviewAlertSmsString(jobPostWorkflow));

                SharedSettings.getGlobalSettings().getMyNotificationHandler().addToQueue(notificationEvent);
            }

            // make entry in db for this.
            newScheduler.setCompletionStatus(true);
            newScheduler.setEndTimestamp(new Timestamp(System.currentTimeMillis()));
            newScheduler.setSchedulerType(SchedulerType.find.where()
                    .eq("schedulerTypeId", SCHEDULER_TYPE_SMS).findUnique());
            newScheduler.setSchedulerSubType(SchedulerSubType.find.where()
                    .eq("schedulerSubTypeId", SCHEDULER_SUB_TYPE_SAME_DAY_INTERVIEW)
                    .findUnique());

            newScheduler.setNote("Interview SMS alert for same day interview.");

            newScheduler.save();
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
