package NotificationService;

/**
 * Created by dodo on 8/12/16.
 */
public class SMSEvent extends NotificationEvent {

    public SMSEvent(String recipient, String message) {
        this.setMessage(message);
        this.setRecipient(recipient);
    }

    @Override
    public String send() {
        // understand and send this object
        return null;
    }
}
