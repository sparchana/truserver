package controllers.scheduler.entity;

import api.SchedulerConstant;
import api.ServerConstants;
import models.entity.OM.JobPostWorkflow;
import models.entity.scheduler.Scheduler;
import models.entity.scheduler.Static.SchedulerSubType;
import models.entity.scheduler.Static.SchedulerType;

import java.sql.Timestamp;
import java.util.*;

import static api.SchedulerConstant.SCHEDULER_SUB_TYPE_SAME_DAY_INTERVIEW;
import static api.SchedulerConstant.SCHEDULER_TYPE_SMS;

/**
 * Created by zero on 12/12/16.
 */
public class SameDayInterviewAlertTask extends TimerTask {
    private Date xHrBack;

    public SameDayInterviewAlertTask(int x){
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.add(Calendar.HOUR, - x);
        xHrBack = cal.getTime();
    }

    @Override
    public void run() {
        // Determine if this task is required to launch
        boolean shouldRunThisTask = false;

        Scheduler scheduler = Scheduler.find.where()
                .eq("schedulerTypeId", SCHEDULER_TYPE_SMS)
                .eq("schedulerSubTypeId", SCHEDULER_SUB_TYPE_SAME_DAY_INTERVIEW)
                .orderBy().desc("eventStartTimestamp")
                .setMaxRows(1).findUnique();

        if(scheduler == null) {
            // task has definitely no yet run so run it
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
                            .eq("scheduledInterviewTimeSlot.interviewTimeSlotId", SchedulerConstant.INTERVIEW_TIME_SLOT_10_AM)
                            .findList();

            // candidate (can have repetitive candidate) who have interview in 'x' hr from now.
            for(JobPostWorkflow jobPostWorkflow: jobPostWorkflowList) {
                // create sms event and keep appending to the Queue
            }

            // make entry in db for this.
            newScheduler.setCompletionStatus(true);
            newScheduler.setEndTimestamp(new Timestamp(System.currentTimeMillis()));
            newScheduler.setSchedulerTypeId(SchedulerType.find.where()
                    .eq("schedulerTypeId", SCHEDULER_TYPE_SMS).findUnique());
            newScheduler.setSchedulerSubTypeId(SchedulerSubType.find.where()
                    .eq("schedulerSubTypeId", SCHEDULER_SUB_TYPE_SAME_DAY_INTERVIEW)
                    .findUnique());

            newScheduler.setMessage("Interview SMS alert for same day interview.");
            newScheduler.save();
        }
    }
}
