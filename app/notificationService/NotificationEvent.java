package notificationService;

/**
 * Created by dodo on 8/12/16.
 */

public abstract class NotificationEvent implements BaseSender {
    private String message;
    private String recipient;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getRecipient() {
        return recipient;
    }

    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }
}
