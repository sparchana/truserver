package NotificationService;

/**
 * Created by dodo on 8/12/16.
 */
public class FCMEvent extends NotificationEvent {

    public FCMEvent(String recipient, String message) {
        this.setMessage(message);
        this.setRecipient(recipient);
    }

    @Override
    public String send() {
        return null;
    }
}
