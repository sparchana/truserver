package notificationService;

/**
 * Created by dodo on 8/12/16.
 */

public abstract class NotificationEvent {
    private String message;
    private String recipient;

    abstract String send();

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