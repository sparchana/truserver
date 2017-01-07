package controllers.scheduler.task;

import api.http.httpResponse.CandidateWorkflowData;
import controllers.businessLogic.JobService;
import controllers.businessLogic.JobWorkflow.JobPostWorkflowEngine;
import controllers.scheduler.SchedulerConstants;
import controllers.scheduler.SchedulerManager;
import dao.JobPostDAO;
import models.entity.Candidate;
import models.entity.JobPost;
import models.entity.scheduler.SchedulerStats;
import models.entity.scheduler.Static.SchedulerSubType;
import models.entity.scheduler.Static.SchedulerType;
import models.util.NotificationUtil;
import play.Logger;

import java.sql.Timestamp;
import java.util.*;

import static controllers.scheduler.SchedulerConstants.*;

/**
 * Created by dodo on 5/1/17.
 */
public class EODJobAlertFcmTask extends TimerTask {

    private final ClassLoader classLoader;

    public EODJobAlertFcmTask(ClassLoader classLoader){
        this.classLoader = classLoader;
    }

    private void sendJobPostAlert(List<JobPost> jobPostList){
        new Thread(() -> {

            SchedulerSubType subType = SchedulerSubType.find.where()
                    .eq("schedulerSubTypeId", SCHEDULER_SUB_TYPE_CANDIDATE_EOD_JOB_ALERT)
                    .findUnique();

            SchedulerType typeFcm = SchedulerType.find.where()
                    .eq("schedulerTypeId", SCHEDULER_TYPE_FCM).findUnique();


            //map for storing the no of jobs matched to an individual candidate
            Map<Long, Integer> candidateToCountMap = new HashMap<>();

            Logger.info("Send alert to all the matching candidates of " + jobPostList.size() + " job posts");

            //recruiter has interview credits
            Timestamp startTime = new Timestamp(System.currentTimeMillis());

            Integer totalAlerts = 0;
            Integer candidateCount = 0;

            for(JobPost jobPost : jobPostList){
                Map<Long, CandidateWorkflowData> candidateSearchMap = JobPostWorkflowEngine.getMatchingCandidate(
                        jobPost.getJobPostId(),
                        null, //age
                        null, //min salary
                        null, //max salary
                        jobPost.getGender(), //gender
                        null, //experience
                        jobPost.getJobRole().getJobRoleId(), //jobRole
                        null, //education
                        null, //locality
                        null, //language list,
                        null, //document List
                        null, //asset list
                        SchedulerConstants.NEW_JOB_MATCHING_DEFAULT_DISTANCE_RADIUS);

                if(candidateSearchMap != null){

                    Logger.info("Total matched candidates for the jobPost: " + jobPost.getJobPostTitle()
                            + " of company: " + jobPost.getCompany().getCompanyName() + " : " + candidateSearchMap.size() + " candidates");

                    List<Candidate> candidateList = new ArrayList<>();
                    for (Map.Entry<Long, CandidateWorkflowData> candidate : candidateSearchMap.entrySet()) {
                        candidateList.add(candidate.getValue().getCandidate());
                    }

                    Collections.shuffle(candidateList);

                    Logger.info("Sending notification to random " + SchedulerConstants.JOB_ALERT_DEFAULT_LIMIT + " candidates regarding the jobPost: " + jobPost.getJobPostTitle()
                            + " of company: " + jobPost.getCompany().getCompanyName());

                    candidateCount = 0;

                    //adding to notification Handler queue
                    for (Candidate candidate : candidateList) {

                        if(candidateCount < SchedulerConstants.JOB_ALERT_DEFAULT_LIMIT){
                            candidateCount++;
                            Integer count = candidateToCountMap.get(candidate.getCandidateId());
                            candidateToCountMap.put(candidate.getCandidateId(), (count == null) ? 1 : count + 1);

                            if(candidateToCountMap.get(candidate.getCandidateId())
                                    < SchedulerConstants.CANDIDATE_JOB_POST_ALERT_MAX_LIMIT)
                            {
                                //sending notification
                                NotificationUtil.sendJobAlertNotificationToCandidate(jobPost, candidate, true);
                                totalAlerts++;

                            } else{

                                Logger.info("Not sending as matching crossed more than " +
                                        SchedulerConstants.CANDIDATE_JOB_POST_ALERT_MAX_LIMIT + " for candidate id: " +
                                        candidate.getCandidateId());
                            }
                        }
                    }
                }
            }

            //saving stats for sms event
            Timestamp endTime = new Timestamp(System.currentTimeMillis());

            //saving stats for fcm event
            String note = "Android notification alert for " + jobPostList.size() + " new Job posts. Total alerts: " + totalAlerts;

            SchedulerManager.saveNewSchedulerStats(startTime, typeFcm, subType, note, endTime, true);

            Logger.info("[SOD Job alert task Complete] Total FCM alerts sent: " + totalAlerts);

        }).start();
    }

    @Override
    public void run() {

        Thread.currentThread().setContextClassLoader(classLoader);

        // Determine if this task is required to launch
        boolean shouldRunThisTask = false;

        SchedulerStats schedulerStats = SchedulerStats.find.where()
                .eq("schedulerType.schedulerTypeId", SCHEDULER_TYPE_FCM)
                .eq("schedulerSubType.schedulerSubTypeId", SCHEDULER_SUB_TYPE_CANDIDATE_EOD_JOB_ALERT)
                .orderBy().desc("startTimestamp").setMaxRows(1).findUnique();

        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        Date today = cal.getTime();

        if(schedulerStats == null) {
            // task has definitely not yet running so run it
            Logger.info("scheduler status is null for EOD job alert.");
            shouldRunThisTask = true;

        } else {
            if(schedulerStats.getEndTimestamp().getDate() != today.getDate()) {

                //task was not executed today
                shouldRunThisTask = true;
            }
        }

        if(shouldRunThisTask){

            // fetch all the jobPost whose recruiter has interview credits
            Logger.info("Starting EOD notify candidates about job post ..");

            sendJobPostAlert(JobPostDAO.getAllJobPostWithRecruitersWithInterviewCredits());
        }
    }
}
