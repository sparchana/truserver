package controllers.scheduler.entity;

import api.SchedulerConstant;
import api.ServerConstants;
import models.entity.OM.JobPostWorkflow;
import models.entity.scheduler.Scheduler;
import models.entity.scheduler.Static.SchedulerSubType;
import models.entity.scheduler.Static.SchedulerType;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimerTask;

import static api.SchedulerConstant.SCHEDULER_SUB_TYPE_NEXT_DAY_INTERVIEW;
import static api.SchedulerConstant.SCHEDULER_SUB_TYPE_SAME_DAY_INTERVIEW;
import static api.SchedulerConstant.SCHEDULER_TYPE_SMS;

/**
 * Created by zero on 12/12/16.
 */
public class NextDayInterviewAlertTask extends TimerTask {
    private Date today;
    private Date tomorrow;

    public NextDayInterviewAlertTask(){
        Calendar newCalendar = Calendar.getInstance();
        today = newCalendar.getTime();
        newCalendar.set(Calendar.DAY_OF_MONTH, + 1);
        tomorrow = newCalendar.getTime();
    }

    @Override
    public void run() {
        // Determine if this task is required to launch
        boolean shouldRunThisTask = false;

        Scheduler scheduler = Scheduler.find.where()
                .eq("schedulerTypeId", SCHEDULER_TYPE_SMS)
                .eq("schedulerSubTypeId", SCHEDULER_SUB_TYPE_NEXT_DAY_INTERVIEW)
                .orderBy().desc("eventStartTimestamp")
                .setMaxRows(1).findUnique();

        if(scheduler == null) {
            // task has definitely no yet run so run it
            shouldRunThisTask = true;

        } else {
            if(scheduler.getStartTimestamp().before(today)) {
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
                            .eq("scheduledInterviewDate", tomorrow)
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
                    .eq("schedulerSubTypeId", SCHEDULER_SUB_TYPE_NEXT_DAY_INTERVIEW)
                    .findUnique());

            newScheduler.setMessage("Interview SMS alert for next day interview.");
            newScheduler.save();
        }
    }
}
