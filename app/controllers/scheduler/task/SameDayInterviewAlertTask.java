package controllers.scheduler.task;

import api.SchedulerConstant;
import api.ServerConstants;
import models.entity.OM.JobPostWorkflow;
import models.entity.scheduler.Scheduler;
import models.entity.scheduler.Static.SchedulerSubType;
import models.entity.scheduler.Static.SchedulerType;
import models.util.SmsUtil;
import notificationService.NotificationEvent;
import notificationService.SMSEvent;
import notificationService.Shared;
import play.Logger;

import java.sql.Timestamp;
import java.util.*;

import static api.SchedulerConstant.SCHEDULER_SUB_TYPE_SAME_DAY_INTERVIEW;
import static api.SchedulerConstant.SCHEDULER_TYPE_SMS;

/**
 * Created by zero on 12/12/16.
 */
public class SameDayInterviewAlertTask extends TimerTask {
    private Date xHrBack;
    private Calendar cal;
    private int xhr;

    public SameDayInterviewAlertTask(int x){
        cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.add(Calendar.HOUR, - x);
        xHrBack = cal.getTime();
        this.xhr = x;
    }

    @Override
    public void run() {
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

            // get all jobpostworkflow items whose interview is today
            List<JobPostWorkflow> jobPostWorkflowList =
                    JobPostWorkflow.find.where()
                            .eq("status.statusId", ServerConstants.JWF_STATUS_INTERVIEW_CONFIRMED)
                            .eq("scheduledInterviewDate", new Date())
                            .eq("scheduledInterviewTimeSlot.interviewTimeSlotId", getSlotIdFromCurrentTime(xhr))
                            .findList();

            // candidate (can have repetitive candidate) who have interview in 'x' hr from now.
            for(JobPostWorkflow jobPostWorkflow: jobPostWorkflowList) {
                // create sms event and keep appending to the Queue
                Logger.info("same day: adding " +  jobPostWorkflow.getCandidate().getCandidateId() +" to queue");

                NotificationEvent notificationEvent =
                        new SMSEvent(jobPostWorkflow.getCandidate().getCandidateMobile(),
                                SmsUtil.getSameDayInterviewAlertSmsString(jobPostWorkflow));

                Shared.getGlobalSettings().getNotificationHandler().addToQueue(notificationEvent);
            }

            // make entry in db for this.
            newScheduler.setCompletionStatus(true);
            newScheduler.setEndTimestamp(new Timestamp(System.currentTimeMillis()));
            newScheduler.setSchedulerType(SchedulerType.find.where()
                    .eq("schedulerTypeId", SCHEDULER_TYPE_SMS).findUnique());
            newScheduler.setSchedulerSubType(SchedulerSubType.find.where()
                    .eq("schedulerSubTypeId", SCHEDULER_SUB_TYPE_SAME_DAY_INTERVIEW)
                    .findUnique());


            newScheduler.save();
        }
    }

    public int getSlotIdFromCurrentTime(int xHr) {

        // from current time figure out which interview time slot id needs to be returned
        if ((new Date()).getHours() + xHr >= 18){
            return 0;
        } else if((new Date()).getHours() + xHr >= 16) {
            return SchedulerConstant.INTERVIEW_TIME_SLOT_4_PM;
        } else if((new Date()).getHours() + xHr >= 13) {
            return SchedulerConstant.INTERVIEW_TIME_SLOT_1_PM;
        } else if((new Date()).getHours() + xHr >= 10) {
            return SchedulerConstant.INTERVIEW_TIME_SLOT_10_AM;
        } else {
            return 0;
        }
    }
}
