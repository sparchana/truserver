package controllers.scheduler.task;

import api.http.FormValidator;
import controllers.businessLogic.CandidateService;
import controllers.scheduler.SchedulerConstants;
import controllers.scheduler.SchedulerManager;
import dao.CandidateDAO;
import models.entity.Candidate;
import models.entity.scheduler.SchedulerStats;
import models.entity.scheduler.Static.SchedulerSubType;
import models.entity.scheduler.Static.SchedulerType;
import models.util.NotificationUtil;
import models.util.SmsUtil;
import play.Logger;

import java.sql.Timestamp;
import java.util.*;

import static controllers.scheduler.SchedulerConstants.*;

/**
 * Created by dodo on 29/12/16.
 */
public class WeeklyCompleteProfileAlertTask extends TimerTask {
    private final ClassLoader classLoader;

    public WeeklyCompleteProfileAlertTask(ClassLoader classLoader){
        this.classLoader = classLoader;
    }

    private void sendProfileCompletionAlert(){

        new Thread(() -> {

            List<Candidate> candidateList = new ArrayList<>();

            //calculating candidate profile completion score for all the candidate who were active last week
            for(Candidate candidate : CandidateDAO.getCandidateWhoUpdateProfileSinceIndexDays(7)){
                int scale = (int) Math.pow(10, 2);
                float percentValue = (float) Math.round(CandidateService.getProfileCompletionPercent(FormValidator
                        .convertToIndianMobileFormat(candidate.getCandidateMobile())) * 100 * scale) / scale;
                candidate.setCandidateScore((int) percentValue);

                if(percentValue < SchedulerConstants.WEEKLY_TASK_DEFAULT_PROFILE_SCORE){
                    candidateList.add(candidate);
                }

                //updating candidate profile score of a candidate
                candidate.update();
            }

            Logger.info("Sending profile completion alert to " + candidateList.size() + " candidates");

            int totalAlertCount = 0;

            SchedulerSubType subType = SchedulerSubType.find.where()
                    .eq("schedulerSubTypeId", SCHEDULER_SUB_TYPE_CANDIDATE_PROFILE_COMPLETE)
                    .findUnique();

            SchedulerType type = SchedulerType.find.where()
                    .eq("schedulerTypeId", SCHEDULER_TYPE_SMS).findUnique();

            SchedulerType typeFcm = SchedulerType.find.where()
                    .eq("schedulerTypeId", SCHEDULER_TYPE_FCM).findUnique();

            Timestamp startTime = new Timestamp(System.currentTimeMillis());

            for(Candidate candidate : candidateList){

                //sending sms
                SmsUtil.sendWeeklySmsToCompleteProfile(candidate);

                //sending notification
                NotificationUtil.sendWeeklyNotificationToCompleteProfile(candidate);
                totalAlertCount++;
            }

            //saving stats for sms event
            String note = "SMS alert for candidate to complete profile.";

            Timestamp endTime = new Timestamp(System.currentTimeMillis());
            SchedulerManager.saveNewSchedulerStats(startTime, type, subType, note, endTime, true);

            //saving stats for fcm event
            note = "Android notification alert for candidate to complete profile.";

            endTime = new Timestamp(System.currentTimeMillis());

            SchedulerManager.saveNewSchedulerStats(startTime, typeFcm, subType, note, endTime, true);

            Logger.info("[Weekly candidate alert task to complete profile completed] alerts sent: " + totalAlertCount);

        }).start();
    }

    @Override
    public void run() {
        Thread.currentThread().setContextClassLoader(classLoader);

        // Determine if this task is required to launch
        boolean shouldRunThisTask = false;

        SchedulerStats schedulerStats = SchedulerStats.find.where()
                .eq("schedulerType.schedulerTypeId", SCHEDULER_TYPE_SMS)
                .eq("schedulerSubType.schedulerSubTypeId", SCHEDULER_SUB_TYPE_CANDIDATE_PROFILE_COMPLETE)
                .orderBy().desc("startTimestamp").setMaxRows(1).findUnique();

        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        Date today = cal.getTime();

        if(schedulerStats == null) {
            // task has definitely not yet running so run it
            Logger.info("scheduler status is null for Weekly candidate complete profile task.");
            shouldRunThisTask = true;

        } else {
            if(schedulerStats.getEndTimestamp().getDate() != today.getDate()) {

                //task was not executed today
                shouldRunThisTask = true;
            }
        }

        if(shouldRunThisTask){
            Logger.info("Starting weekly task to notify candidates to complete profile ..");

            //list of candidates whose profile score is less than 80% last 'n' no. of days
            sendProfileCompletionAlert();
        }
    }


}