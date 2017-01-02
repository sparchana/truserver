package controllers.scheduler.task;

import api.ServerConstants;
import api.http.httpResponse.CandidateWorkflowData;
import controllers.businessLogic.JobService;
import controllers.businessLogic.JobWorkflow.JobPostWorkflowEngine;
import controllers.scheduler.SchedulerConstants;
import controllers.scheduler.SchedulerManager;
import models.entity.Candidate;
import models.entity.JobPost;
import models.entity.scheduler.Static.SchedulerSubType;
import models.entity.scheduler.Static.SchedulerType;
import models.util.NotificationUtil;
import models.util.SmsUtil;
import play.Logger;

import java.sql.Timestamp;
import java.util.*;

import static controllers.scheduler.SchedulerConstants.*;

/**
 * Created by dodo on 23/12/16.
 */

/**
 * SOD task at 9am for all jobs of recruiters that have interview credits.
 * Send notification and sms to all candidates matching
 *
 * */
public class SODNotifyCandidateAboutJobPostTask extends TimerTask {

    private void sendJobPostAlert(List<JobPost> jobPostList){

        new Thread(() -> {

            SchedulerSubType subType = SchedulerSubType.find.where()
                    .eq("schedulerSubTypeId", SCHEDULER_SUB_TYPE_CANDIDATE_SOD_JOB_ALERT)
                    .findUnique();

            SchedulerType type = SchedulerType.find.where()
                    .eq("schedulerTypeId", SCHEDULER_TYPE_SMS).findUnique();

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
                        20.00);

                if(candidateSearchMap != null){
                    List<Candidate> candidateList = new ArrayList<>();
                    for (Map.Entry<Long, CandidateWorkflowData> candidate : candidateSearchMap.entrySet()) {
                        candidateList.add(candidate.getValue().getCandidate());
                    }

                    Collections.shuffle(candidateList);

                    Logger.info("Sending notification to " + SchedulerConstants.JOB_ALERT_DEFAULT_LIMIT + " candidates regarding the jobPost: " + jobPost.getJobPostTitle()
                    + " of company: " + jobPost.getCompany().getCompanyName());

                    Boolean hasCredit = false;
                    if(jobPost.getRecruiterProfile().totalInterviewCredits() > 0){
                        hasCredit = true;
                    }

                    candidateCount = 0;

                   //adding to notification Handler queue
                    for (Candidate candidate : candidateList) {

                        candidateCount++;
                        Integer count = candidateToCountMap.get(candidate.getCandidateId());
                        candidateToCountMap.put(candidate.getCandidateId(), (count == null) ? 1 : count + 1);

                        if(candidateToCountMap.get(candidate.getCandidateId())
                                < SchedulerConstants.CANDIDATE_JOB_POST_ALERT_MAX_LIMIT)
                        {
                            if(candidateCount <= SchedulerConstants.JOB_ALERT_DEFAULT_LIMIT){
                                totalAlerts++;

                                //sending sms
                                SmsUtil.sendJobAlertSmsToCandidate(jobPost, candidate, hasCredit);

                                //sending notification
                                NotificationUtil.sendJobAlertNotificationToCandidate(jobPost, candidate, hasCredit);

                            }

                        } else{

                            Logger.info("Not sending as matching crossed more than " +
                                    SchedulerConstants.CANDIDATE_JOB_POST_ALERT_MAX_LIMIT + " for candidate id: " +
                                    candidate.getCandidateId());
                        }
                    }
                }
            }

            //saving stats for sms event
            Timestamp endTime = new Timestamp(System.currentTimeMillis());

            String note = "SMS alert for " + jobPostList.size() + " new Job posts. Total alerts: " + totalAlerts;

            SchedulerManager.saveNewSchedulerStats(startTime, type, subType, note, endTime, true);

            //saving stats for fcm event
            note = "Android notification alert for " + jobPostList.size() + " new Job posts. Total alerts: " + totalAlerts;

            SchedulerManager.saveNewSchedulerStats(startTime, typeFcm, subType, note, endTime, true);

        }).start();
    }

    @Override
    public void run() {
        // fetch all the jobPost whose recruiter has interview credits
        Logger.info("Starting SOD notify matching candidates..");

        sendJobPostAlert(JobService.getAllJobPostWithRecruitersWithInterviewCredits());
    }
}