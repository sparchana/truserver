package notificationService;

import com.google.android.gcm.server.Message;
import com.google.android.gcm.server.Sender;
import play.Logger;
import play.Play;

import java.io.IOException;

/**
 * Created by dodo on 8/12/16.
 */
public class FCMEvent extends NotificationEvent {

    private boolean isDevMode;
    private String myTitle;
    private Integer myIntentType;
    private Long jobPostId;

    public FCMEvent(String recipient, String message, String title, Integer intentType) {
        this.setMessage(message);
        this.setRecipient(recipient);
        this.setMyTitle(title);
        this.setMyIntentType(intentType);
    }

    public FCMEvent(String recipient, String message, String title, Integer intentType, Long jpId) {
        this.setMessage(message);
        this.setRecipient(recipient);
        this.setMyTitle(title);
        this.setMyIntentType(intentType);
        this.setJobPostId(jpId);
    }

    @Override
    public String send() {
        this.isDevMode = play.api.Play.isDev(play.api.Play.current()) || play.api.Play.isTest(play.api.Play.current());
        String messageText = this.getMessage();
        String recipient = this.getRecipient();

        String senderKey = Play.application().configuration().getString("fcm.senderKey");
        boolean shouldSendFCM = Play.application().configuration().getBoolean("outbound.fcm.enabled");

        final Sender sender = new Sender(senderKey);
        com.google.android.gcm.server.Result result = null;

        final Message message = new Message.Builder().timeToLive(30)
                .delayWhileIdle(true)
                .addData("title", myTitle)
                .addData("message", messageText)
                .addData("type", String.valueOf(myIntentType))
                .addData("jpId", String.valueOf(jobPostId))
                .build();

        if(shouldSendFCM) {
            try {
                if(isDevMode()){
                    Logger.info("DevMode: No Notification sent. Msg: " + messageText);
                } else{
                    result = sender.send(message, recipient, 1);
                }

            } catch (final IOException e) {
                e.printStackTrace();
            }
        } else {
            Logger.info("Outbound FCM Disabled in .conf file. No Notification sent" + messageText);
        }

        return "";
    }

    public String getMyTitle() {
        return myTitle;
    }

    public void setMyTitle(String myTitle) {
        this.myTitle = myTitle;
    }

    public Integer getMyIntentType() {
        return myIntentType;
    }

    public void setMyIntentType(Integer myIntentType) {
        this.myIntentType = myIntentType;
    }

    public Long getJobPostId() {
        return jobPostId;
    }

    public void setJobPostId(Long jobPostId) {
        this.jobPostId = jobPostId;
    }

    public boolean isDevMode() {
        return isDevMode;
    }
}
