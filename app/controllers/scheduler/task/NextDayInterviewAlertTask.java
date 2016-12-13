package controllers.scheduler.task;

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
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimerTask;

import static controllers.scheduler.SchedulerConstants.SCHEDULER_SUB_TYPE_NEXT_DAY_INTERVIEW;
import static controllers.scheduler.SchedulerConstants.SCHEDULER_TYPE_SMS;

/**
 * Created by zero on 12/12/16.
 */
public class NextDayInterviewAlertTask extends TimerTask {
    private Date today;
    private Date tomorrow;

    public NextDayInterviewAlertTask(){
        Calendar newCalendar = Calendar.getInstance();
        today = newCalendar.getTime();
        newCalendar.set(Calendar.DAY_OF_MONTH, today.getDate() + 1);
        tomorrow = newCalendar.getTime();
    }

    @Override
    public void run() {
        Logger.info("NDI Alert started...");

        // Determine if this task is required to launch
        boolean shouldRunThisTask = false;


        Scheduler scheduler = Scheduler.find.where()
                .eq("schedulerType.schedulerTypeId", SCHEDULER_TYPE_SMS)
                .eq("schedulerSubType.schedulerSubTypeId", SCHEDULER_SUB_TYPE_NEXT_DAY_INTERVIEW)
                .orderBy().desc("startTimestamp").setMaxRows(1).findUnique();

        if(scheduler == null) {
            // task has definitely not yet running so run it
            shouldRunThisTask = true;

        } else {
            Logger.info("prev task start time "+scheduler.getStartTimestamp().getHours()
                    +"current time: " + (today).getHours());

            if(scheduler.getStartTimestamp().getHours() < (today).getHours()) {
                // last run was 'x++' hr back, hence re run
                shouldRunThisTask = true;
            }
        }

        if(shouldRunThisTask) {

            Scheduler newScheduler = new Scheduler();

            newScheduler.setStartTimestamp(new Timestamp(System.currentTimeMillis()) );

            SimpleDateFormat sdf = new SimpleDateFormat(ServerConstants.SDF_FORMAT_YYYYMMDD);

            // get all jobpostworkflow items whose interview is next day
            List<JobPostWorkflow> jobPostWorkflowList =
                    JobPostWorkflow.find.where()
                            .eq("status.statusId", ServerConstants.JWF_STATUS_INTERVIEW_CONFIRMED)
                            .eq("scheduledInterviewDate", sdf.format(tomorrow))
                            .findList();

            // candidate (can have repetitive candidate) who have interview in 'x' hr from now.
            for(JobPostWorkflow jobPostWorkflow: jobPostWorkflowList) {
                // create sms event and keep appending to the Queue

                Logger.info("next day: adding " +  jobPostWorkflow.getCandidate().getCandidateId() +" to queue");
                NotificationEvent notificationEvent =
                        new SMSEvent(jobPostWorkflow.getCandidate().getCandidateMobile(),
                                SmsUtil.getNextDayInterviewAlertSmsString(jobPostWorkflow));

                Shared.getGlobalSettings().getNotificationHandler().addToQueue(notificationEvent);
            }

            // make entry in db for this.
            newScheduler.setCompletionStatus(true);
            newScheduler.setEndTimestamp(new Timestamp(System.currentTimeMillis()));
            newScheduler.setSchedulerType(SchedulerType.find.where()
                    .eq("schedulerTypeId", SCHEDULER_TYPE_SMS).findUnique());
            newScheduler.setSchedulerSubType(SchedulerSubType.find.where()
                    .eq("schedulerSubTypeId", SCHEDULER_SUB_TYPE_NEXT_DAY_INTERVIEW)
                    .findUnique());

            newScheduler.setNote("Interview SMS alert for next day interview.");
            newScheduler.save();
        }
    }
}
