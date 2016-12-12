package NotificationService;

/**
 * Created by dodo on 8/12/16.
 */
public class EmailEvent extends NotificationEvent {

    private String senderEmail;
    private String emailSubject;

    @Override
    void send() {
        //Send email method here
    }

    public String getSenderEmail() {
        return senderEmail;
    }

    public void setSenderEmail(String senderEmail) {
        this.senderEmail = senderEmail;
    }

    public String getEmailSubject() {
        return emailSubject;
    }

    public void setEmailSubject(String emailSubject) {
        this.emailSubject = emailSubject;
    }
}
