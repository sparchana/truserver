package notificationService;

import com.google.android.gcm.server.Message;
import com.google.android.gcm.server.Sender;
import play.Play;

import java.io.IOException;

/**
 * Created by dodo on 8/12/16.
 */
public class FCMEvent extends NotificationEvent {

    private String title;
    private Integer intentType;

    public FCMEvent(String recipient, String message) {
        this.setMessage(message);
        this.setRecipient(recipient);
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
                .addData("title", title)
                .addData("message", messageText)
                .addData("type", String.valueOf(intentType))
                .build();

        try {
            result = sender.send(message, recipient, 1);
        } catch (final IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Integer getIntentType() {
        return intentType;
    }

    public void setIntentType(Integer intentType) {
        this.intentType = intentType;
    }
}
