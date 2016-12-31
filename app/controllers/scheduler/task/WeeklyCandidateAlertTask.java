package controllers.scheduler.task;

import api.ServerConstants;
import api.http.FormValidator;
import controllers.businessLogic.JobSearchService;
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
import java.util.ArrayList;
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
            for(i = 0; i< 200; i++){

                //sending sms
                SmsUtil.sendWeeklySmsToDownloadAndroidApp(candidateList.get(i));
            }

            //saving stats for sms event
            String note = "SMS alert for candidate to download android app.";

            Timestamp endTime = new Timestamp(System.currentTimeMillis());
            SchedulerManager.saveNewSchedulerStats(startTime, type, subType, note, endTime, true);

        }).start();
    }

    private void sendSmsToCandidateToNotifyNearbyJobs(){
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

            for(Candidate candidate : CandidateDAO.getCandidateWhoUpdateProfileSinceIndexDays(7)){
                int jobsCount = 0;
                String jobRoles = "";

                //computing list of job preferences of the candidate
                List<Long> jobPrefIds = new ArrayList<>();

                for(JobPreference jobPreference: candidate.getJobPreferencesList()){
                    jobPrefIds.add(jobPreference.getJobRole().getJobRoleId());
                    jobRoles += jobPreference.getJobRole().getJobName() + ", ";
                }

                List<JobPost> jobPostList = JobSearchService.getRelevantJobsPostsForCandidate
                        (FormValidator.convertToIndianMobileFormat(candidate.getCandidateMobile()));

                jobsCount = jobPostList.size();

                if(jobsCount > 0){

                    //sending sms
                    SmsUtil.sendWeeklySmsToNotifyNoOfMatchingJobs(candidate, jobsCount, jobRoles.substring(0, jobRoles.length() - 2));

                    //sending notification
                    NotificationUtil.sendWeeklyMatchingJobsNotification(candidate, jobsCount, jobRoles.substring(0, jobRoles.length() - 2));
                }
            }


            //saving stats for sms event
            String note = "SMS alert for candidate to notify about no. of matching jobs.";

            SchedulerStats newSchedulerStats = new SchedulerStats();
            newSchedulerStats.setStartTimestamp(new Timestamp(System.currentTimeMillis()) );

            Timestamp endTime = new Timestamp(System.currentTimeMillis());
            SchedulerManager.saveNewSchedulerStats(startTime, type, subType, note, endTime, true);

            //saving stats for fcm event
            note = "Android notification alert for candidate to notify about no. of matching jobs.";

            newSchedulerStats = new SchedulerStats();
            newSchedulerStats.setStartTimestamp(new Timestamp(System.currentTimeMillis()) );

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
        sendSmsToCandidateToNotifyNearbyJobs();
    }
}
