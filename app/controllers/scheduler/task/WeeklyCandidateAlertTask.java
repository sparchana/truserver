package controllers.scheduler.task;

import api.http.FormValidator;
import controllers.businessLogic.JobSearchService;
import controllers.scheduler.SchedulerConstants;
import controllers.scheduler.SchedulerManager;
import dao.CandidateDAO;
import models.entity.Candidate;
import models.entity.JobPost;
import models.entity.OM.JobPreference;
import models.entity.scheduler.SchedulerStats;
import models.entity.scheduler.Static.SchedulerSubType;
import models.entity.scheduler.Static.SchedulerType;
import models.util.NotificationUtil;
import models.util.SmsUtil;
import play.Logger;

import java.sql.Timestamp;
import java.util.Collections;
import java.util.List;
import java.util.TimerTask;

import static controllers.scheduler.SchedulerConstants.*;

/**
 * Created by dodo on 28/12/16.
 */
public class WeeklyCandidateAlertTask extends TimerTask {

    private void sendAppDownloadSmsToCandidate(List<Candidate> candidateList){
        new Thread(() -> {
            Logger.info("Sending sms notification to " + candidateList.size() + " candidates to download android app");

            SchedulerSubType subType = SchedulerSubType.find.where()
                    .eq("schedulerSubTypeId", SCHEDULER_SUB_TYPE_CANDIDATE_APP_DOWNLOAD)
                    .findUnique();

            SchedulerType type = SchedulerType.find.where()
                    .eq("schedulerTypeId", SCHEDULER_TYPE_SMS).findUnique();


            Timestamp startTime = new Timestamp(System.currentTimeMillis());

            int i;
            for(i = 0; i< SchedulerConstants.CANDIDATE_ALERT_TASK_WEEKLY_LIMIT; i++){

                //sending sms
                SmsUtil.sendAppDownloadSms(candidateList.get(i));
            }

            //saving stats for sms event
            String note = "SMS alert for candidate to download android app.";

            Timestamp endTime = new Timestamp(System.currentTimeMillis());
            SchedulerManager.saveNewSchedulerStats(startTime, type, subType, note, endTime, true);

        }).start();
    }

    private void sendSmsToCandidateToNotifyNearbyJobs(Integer noOfDays){
        new Thread(() -> {

            Logger.info("Sending sms notification to candidates to tell them the no. of matching jobs around them");

            SchedulerSubType subType = SchedulerSubType.find.where()
                    .eq("schedulerSubTypeId", SCHEDULER_SUB_TYPE_CANDIDATE_NOTIFY_NEARBY_JOBS)
                    .findUnique();

            SchedulerType type = SchedulerType.find.where()
                    .eq("schedulerTypeId", SCHEDULER_TYPE_SMS).findUnique();

            SchedulerType typeFcm = SchedulerType.find.where()
                    .eq("schedulerTypeId", SCHEDULER_TYPE_FCM).findUnique();

            Timestamp startTime = new Timestamp(System.currentTimeMillis());

            List<Candidate> candidateList = CandidateDAO.getAllActiveCandidateWithinProvidedDays(noOfDays);

            Collections.shuffle(candidateList);

            int smsCount = 0;
            for(Candidate candidate : candidateList){
                int jobsCount = 0;
                String jobRoles = "";

                //computing list of job preferences of the candidate
                for(JobPreference jobPreference: candidate.getJobPreferencesList()){
                    jobRoles += jobPreference.getJobRole().getJobName() + ", ";
                }

                List<JobPost> jobPostList = JobSearchService.getRelevantJobsPostsForCandidate
                        (FormValidator.convertToIndianMobileFormat(candidate.getCandidateMobile()));

                jobsCount = jobPostList.size();

                if(jobsCount > 0){

                    //checking for daily limit for sms
                    if(smsCount <= SchedulerConstants.CANDIDATE_ALERT_TASK_WEEKLY_LIMIT){
                        //sending sms
                        SmsUtil.sendWeeklySmsToNotifyNoOfMatchingJobs(candidate, jobsCount, jobRoles.substring(0, jobRoles.length() - 2));
                        smsCount++;
                    }

                    //sending notification
                    NotificationUtil.sendWeeklyMatchingJobsNotification(candidate, jobsCount, jobRoles.substring(0, jobRoles.length() - 2));
                }
            }


            //saving stats for sms event
            String note = "SMS alert for candidate to notify about no. of matching jobs.";

            Timestamp endTime = new Timestamp(System.currentTimeMillis());
            SchedulerManager.saveNewSchedulerStats(startTime, type, subType, note, endTime, true);

            //saving stats for fcm event
            note = "Android notification alert for candidate to notify about no. of matching jobs.";

            endTime = new Timestamp(System.currentTimeMillis());

            SchedulerManager.saveNewSchedulerStats(startTime, typeFcm, subType, note, endTime, true);

        }).start();
    }

    @Override
    public void run() {
        // fetch all the application which had interviews today
        Logger.info("Starting EOD notify candidates for app download ..");

        List<Candidate> candidateList = CandidateDAO.getCandidateWithoutAndroidApp();
        Collections.shuffle(candidateList);

        sendAppDownloadSmsToCandidate(candidateList);

        //weekly task to notify no. of matching jobs to the candidate
        sendSmsToCandidateToNotifyNearbyJobs(SchedulerConstants.CANDIDATE_ALERT_TASK_LAST_ACTIVE_DEFAULT_DAYS);
    }
}
