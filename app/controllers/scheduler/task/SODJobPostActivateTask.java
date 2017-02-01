package controllers.scheduler.task;

import api.ServerConstants;
import controllers.scheduler.SchedulerManager;
import dao.JobPostDAO;
import models.entity.JobPost;
import models.entity.Static.JobStatus;
import models.entity.scheduler.SchedulerStats;
import models.entity.scheduler.Static.SchedulerSubType;
import models.entity.scheduler.Static.SchedulerType;
import play.Logger;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimerTask;

import static controllers.scheduler.SchedulerConstants.*;

/**
 * Created by dodo on 14/1/17.
 */
public class SODJobPostActivateTask extends TimerTask {

    private final ClassLoader classLoader;

    public SODJobPostActivateTask(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    @Override
    public void run() {
        Logger.info("Daily JobPost Activation Task  started...");

        Thread.currentThread().setContextClassLoader(classLoader);

        // Determine if this task is required to launch
        boolean shouldRunThisTask = false;

        SchedulerStats schedulerStats = SchedulerStats.find.where()
                .eq("schedulerType.schedulerTypeId", SCHEDULER_TYPE_SYSTEM_TASK)
                .eq("schedulerSubType.schedulerSubTypeId", SCHEDULER_SUB_TYPE_JOB_POST_ACTIVATION)
                .orderBy().desc("startTimestamp").setMaxRows(1).findUnique();

        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        Date today = cal.getTime();

        if(schedulerStats == null) {

            // task has definitely not yet running so run it
            Logger.info("scheduler status is null for SOD reactivate a job.");
            shouldRunThisTask = true;

        } else {
            if(schedulerStats.getEndTimestamp().getDate() != today.getDate()) {

                //task was not executed today
                shouldRunThisTask = true;
            }
        }

        if(shouldRunThisTask) {

            // building scheduler stat obj starts
            Timestamp startTime = new Timestamp(System.currentTimeMillis());
            SchedulerType type = SchedulerType.find.where()
                    .eq("schedulerTypeId", SCHEDULER_TYPE_SYSTEM_TASK).findUnique();

            SchedulerSubType subType = SchedulerSubType.find.where()
                    .eq("schedulerSubTypeId", SCHEDULER_SUB_TYPE_JOB_POST_ACTIVATION)
                    .findUnique();

            List<JobPost> jobPostList = JobPostDAO.getAllPausedJobsResumingToday();

            String note = "Daily Job post Activation Task to resume paused jobs. Resuming " + jobPostList.size() + " jobs" ;

            SchedulerStats newSchedulerStats = new SchedulerStats();

            newSchedulerStats.setStartTimestamp(new Timestamp(System.currentTimeMillis()) );
            // building scheduler stat obj breaks

            // Scheduler Task start
            if(jobPostList.isEmpty()) {
                Logger.info("No jobs getting activated today!");
                return;
            }

            for(JobPost jobPost : jobPostList){
                Logger.info("Re activating job post: " + jobPost.getJobPostTitle() + " | " + jobPost.getJobPostId());
                jobPost.setJobPostStatus(JobStatus.find.where().eq("JobStatusId", ServerConstants.JOB_STATUS_ACTIVE).findUnique());
                jobPost.setResumeApplicationDate(null);
                jobPost.update();
            }

            // building scheduler stat obj resume
            Timestamp endTime = new Timestamp(System.currentTimeMillis());
            // building scheduler stat obj ends

            // save scheduler stats
            SchedulerManager.saveNewSchedulerStats(startTime, type, subType, note, endTime, true);

            Logger.info("Daily job post Activation Task Completed and activated " + jobPostList.size() + " jobs");
        } 
    }
}