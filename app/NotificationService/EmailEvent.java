package NotificationService;

/**
 * Created by dodo on 8/12/16.
 */
public class EmailEvent extends NotificationEvent {
    private String subject;

    public EmailEvent(String recipient, String message, String subject) {
        this.setMessage(message);
        this.setRecipient(recipient);
        this.subject = subject;
    }

    @Override
    public String send() {
        // understand and send this object
        return null;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }
}
