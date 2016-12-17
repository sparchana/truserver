package controllers.scheduler.task;

import api.ServerConstants;
import controllers.scheduler.SchedulerManager;
import controllers.Global;
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
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimerTask;

import static api.ServerConstants.SOURCE_INTERNAL;
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
        boolean shouldRunThisTask = SchedulerManager.checkIfEODTaskShouldRun(SCHEDULER_TYPE_SMS,
                SCHEDULER_SUB_TYPE_NEXT_DAY_INTERVIEW);


        if(shouldRunThisTask) {
            Timestamp startTime = new Timestamp(System.currentTimeMillis());
            SchedulerSubType subType = SchedulerSubType.find.where()
                    .eq("schedulerSubTypeId", SCHEDULER_SUB_TYPE_NEXT_DAY_INTERVIEW)
                    .findUnique();
            SchedulerType type = SchedulerType.find.where()
                    .eq("schedulerTypeId", SCHEDULER_TYPE_SMS).findUnique();
            String note = "Interview SMS alert for next day interview.";

            SchedulerStats newSchedulerStats = new SchedulerStats();

            newSchedulerStats.setStartTimestamp(new Timestamp(System.currentTimeMillis()) );

            SimpleDateFormat sdf = new SimpleDateFormat(ServerConstants.SDF_FORMAT_YYYYMMDD);

            // get all jobpostworkflow items whose interview is next day
            List<JobPostWorkflow> jobPostWorkflowList =
                    JobPostWorkflow.find.where()
                            .eq("status.statusId", ServerConstants.JWF_STATUS_INTERVIEW_CONFIRMED)
                            .eq("scheduledInterviewDate", sdf.format(tomorrow))
                            .findList();

            // candidate (can have repetitive candidate) who have interview in 'x' hr from now.
            for(JobPostWorkflow jobPostWorkflow: jobPostWorkflowList) {
                if(jobPostWorkflow.getJobPost().getSource() != SOURCE_INTERNAL){
                    continue;
                }
                // create sms event and keep appending to the Queue

                Logger.info("next day: adding " +  jobPostWorkflow.getCandidate().getCandidateId() +" to queue");
                NotificationEvent notificationEvent =
                        new SMSEvent(jobPostWorkflow.getCandidate().getCandidateMobile(),
                                SmsUtil.getNextDayInterviewAlertSmsString(jobPostWorkflow));

                Global.getmNotificationHandler().addToQueue(notificationEvent);
            }

            Timestamp endTime = new Timestamp(System.currentTimeMillis());

            SchedulerManager.saveNewSchedulerStats(startTime, type, subType, note, endTime, true);
        }
    }
}
