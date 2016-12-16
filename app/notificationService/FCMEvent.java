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

    private String myTitle;
    private Integer myIntentType;

    public FCMEvent(String recipient, String message, String title, Integer intentType) {
        this.setMessage(message);
        this.setRecipient(recipient);
        this.setMyTitle(title);
        this.setMyIntentType(intentType);
    }

    @Override
    public String send() {
        String messageText = this.getMessage();
        String recipient = this.getRecipient();

        String senderKey = Play.application().configuration().getString("fcm.senderKey");
        final Sender sender = new Sender(senderKey);
        com.google.android.gcm.server.Result result = null;

        final Message message = new Message.Builder().timeToLive(30)
                .delayWhileIdle(true)
                .addData("title", myTitle)
                .addData("message", messageText)
                .addData("type", String.valueOf(myIntentType))
                .build();

        try {
            result = sender.send(message, recipient, 1);
        } catch (final IOException e) {
            e.printStackTrace();
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
}
