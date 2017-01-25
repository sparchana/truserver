package notificationService;

import models.entity.OM.SmsReport;
import models.entity.Static.SmsDeliveryStatus;
import play.Logger;

import java.util.Objects;
import java.util.concurrent.LinkedBlockingQueue;

import static api.ServerConstants.SMS_STATUS_PENDING;

/**
 * Created by dodo on 12/12/16.
 */
public class NotificationHandler implements Runnable {
    private LinkedBlockingQueue<NotificationEvent> queue;

    public NotificationHandler() {
        queue = new LinkedBlockingQueue<>();
    }

    public void addToQueue(NotificationEvent event) {
        queue.add(event);
    }

    @Override
    public void run() {
        while (true) {
            try {
                NotificationEvent notificationEvent = queue.take();
                String response = notificationEvent.send();

                if(notificationEvent.getCompany() != null){
                    //create entry for this notification
                    SmsReport smsReport = new SmsReport();
                    smsReport.setJobPost(notificationEvent.getJobPost());
                    smsReport.setCandidate(notificationEvent.getCandidate());
                    smsReport.setRecruiterProfile(notificationEvent.getRecruiterProfile());
                    smsReport.setCompany(notificationEvent.getCompany());
                    smsReport.setSmsText(notificationEvent.getMessage());
                    smsReport.setSmsSchedulerId(response);
                    smsReport.setSmsDeliveryStatus(SmsDeliveryStatus.find.where().eq("status_id", SMS_STATUS_PENDING).findUnique());

                    Logger.info("Creating entry in SMS report table");
                    smsReport.save();
                }
                if(!Objects.equals(response, "DevMode: No sms sent")){
                } else{
                    Logger.info("No entry in SMS report table");
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
