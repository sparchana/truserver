package controllers.scheduler.task;

import controllers.scheduler.SchedulerManager;
import dao.CandidateDAO;
import models.entity.Candidate;
import models.entity.OM.JobPostWorkflow;
import models.entity.scheduler.SchedulerStats;
import models.entity.scheduler.Static.SchedulerSubType;
import models.entity.scheduler.Static.SchedulerType;
import models.util.NotificationUtil;
import models.util.SmsUtil;
import play.Logger;

import java.sql.Timestamp;
import java.util.List;
import java.util.TimerTask;

import static controllers.scheduler.SchedulerConstants.*;

/**
 * Created by dodo on 28/12/16.
 */
public class WeeklyAppDownloadAlertTask extends TimerTask {

    private void sendSmsToCandidate(List<Candidate> candidateList){
        new Thread(() -> {
            Logger.info("Sending sms notification to " + candidateList.size() + " candidates to download android app");

            SchedulerSubType subType = SchedulerSubType.find.where()
                    .eq("schedulerSubTypeId", SCHEDULER_SUB_TYPE_CANDIDATE_APP_DOWNLOAD)
                    .findUnique();

            SchedulerType type = SchedulerType.find.where()
                    .eq("schedulerTypeId", SCHEDULER_TYPE_SMS).findUnique();


            Timestamp startTime = new Timestamp(System.currentTimeMillis());

            for(Candidate candidate : candidateList){

                //sending sms
                SmsUtil.sendWeeklySmsToDownloadAndroidApp(candidate);
            }

            //saving stats for sms event
            String note = "SMS alert for candidate to download android app.";

            SchedulerStats newSchedulerStats = new SchedulerStats();
            newSchedulerStats.setStartTimestamp(new Timestamp(System.currentTimeMillis()) );

            Timestamp endTime = new Timestamp(System.currentTimeMillis());
            SchedulerManager.saveNewSchedulerStats(startTime, type, subType, note, endTime, true);

        }).start();
    }

    @Override
    public void run() {
        // fetch all the application which had interviews today
        Logger.info("Starting EOD notify candidates for app download ..");

        sendSmsToCandidate(CandidateDAO.getCandidateWithoutAndroidApp());
    }
}
