package notificationService;

import api.ServerConstants;
import models.entity.OM.SmsReport;
import models.entity.Static.SmsDeliveryStatus;
import play.Logger;

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

                if(notificationEvent.getSmsType() != null) {
                    SmsReport smsReport = new SmsReport();

                    switch (notificationEvent.getSmsType().getSmsTypeId()) {

                        case ServerConstants.SMS_TYPE_APPLY_JOB_SMS:
                        case ServerConstants.SMS_TYPE_APPLY_INTERVIEW_SMS:
                            smsReport.setJobPost(notificationEvent.getJobPost());
                            smsReport.setCandidate(notificationEvent.getCandidate());
                            break;
                        case ServerConstants.SMS_TYPE_REFERRAL:
                            smsReport.setPartner(notificationEvent.getPartner());
                            break;
                        default: continue;
                    }

                    smsReport.setRecruiterProfile(notificationEvent.getRecruiterProfile());
                    smsReport.setCompany(notificationEvent.getCompany());
                    smsReport.setSmsText(notificationEvent.getMessage());
                    smsReport.setSmsSchedulerId(response);
                    smsReport.setSmsType(notificationEvent.getSmsType());
                    smsReport.setSmsDeliveryStatus(SmsDeliveryStatus.find.where().eq("status_id", SMS_STATUS_PENDING).setUseQueryCache(true).findUnique());

                    Logger.info("Creating entry in SMS report table");
                    smsReport.save();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
