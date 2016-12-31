package controllers.scheduler.task;

import api.http.FormValidator;
import controllers.businessLogic.CandidateService;
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
import java.util.ArrayList;
import java.util.List;
import java.util.TimerTask;

import static controllers.scheduler.SchedulerConstants.SCHEDULER_SUB_TYPE_CANDIDATE_PROFILE_COMPLETE;
import static controllers.scheduler.SchedulerConstants.SCHEDULER_TYPE_FCM;
import static controllers.scheduler.SchedulerConstants.SCHEDULER_TYPE_SMS;

/**
 * Created by dodo on 29/12/16.
 */
public class WeeklyCompleteProfileAlertTask extends TimerTask {

    private void sendProfileCompletionAlert(){

        new Thread(() -> {

            List<Candidate> candidateList = new ArrayList<>();

            //calculating candidate profile completion score for all the candidate who were active last week
            for(Candidate candidate : CandidateDAO.getCandidateWhoUpdateProfileSinceIndexDays(7)){
                int scale = (int) Math.pow(10, 2);
                float percentValue = (float) Math.round(CandidateService.getProfileCompletionPercent(FormValidator
                        .convertToIndianMobileFormat(candidate.getCandidateMobile())) * 100 * scale) / scale;
                candidate.setCandidateScore((int) percentValue);

                if(percentValue < 80){
                    candidateList.add(candidate);
                }

                //updating candidate profile score of a candidate
                candidate.update();
            }

            Logger.info("Sending profile completion alert to " + candidateList.size() + " candidates");

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
            }

            //saving stats for sms event
            String note = "SMS alert for candidate to complete profile.";

            Timestamp endTime = new Timestamp(System.currentTimeMillis());
            SchedulerManager.saveNewSchedulerStats(startTime, type, subType, note, endTime, true);

            //saving stats for fcm event
            note = "Android notification alert for candidate to complete profile.";

            endTime = new Timestamp(System.currentTimeMillis());

            SchedulerManager.saveNewSchedulerStats(startTime, typeFcm, subType, note, endTime, true);

        }).start();
    }

    @Override
    public void run() {
        Logger.info("Starting weekly task to notify candidates to complete profile ..");

        //list of candidates whose profile score is less than 80% last 'n' no. of days
        sendProfileCompletionAlert();
    }
}