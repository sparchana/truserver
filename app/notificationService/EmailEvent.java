package notificationService;

import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.Email;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.SimpleEmail;
import play.Logger;
import play.Play;

/**
 * Created by dodo on 8/12/16.
 */
public class EmailEvent extends NotificationEvent {
    private String mySubject;
    private boolean isDevMode;

    public EmailEvent(String recipient, String message, String subject) {
        this.setMessage(message);
        this.setRecipient(recipient);
        this.setMySubject(subject);
    }

    @Override
    public String send() {
        this.isDevMode = play.api.Play.isDev(play.api.Play.current()) || play.api.Play.isTest(play.api.Play.current());

        String message = this.getMessage();
        String recipient = this.getRecipient();

        //new thread
        new Thread(() -> {
            try {
                Email email = new SimpleEmail();
                email.setHostName(Play.application().configuration().getString("mail.smtp.host"));
                email.setSmtpPort(465);
                email.setAuthenticator(new DefaultAuthenticator(Play.application().configuration().getString("mail.smtp.user")
                        , Play.application().configuration().getString("mail.smtp.pass")));
                email.setSSLOnConnect(true);
                email.setContent(message, "text/html; charset=utf-8");
                email.setFrom("recruiter.support@trujobs.in", "Trujobs Recruiter");
                email.setSubject(getMySubject());
                email.addTo(recipient);
                if(isDevMode()){
                    Logger.info("DevMode: No Email sent");
                } else {
                    Logger.info("Sending email to " + recipient);
                    email.send();
                }
            } catch (EmailException e) {
                e.printStackTrace();
            }
        }).start();

        return "";
    }

    public String getMySubject() {
        return mySubject;
    }

    public void setMySubject(String mySubject) {
        this.mySubject = mySubject;
    }

    public boolean isDevMode() {
        return isDevMode;
    }

    public void setDevMode(boolean devMode) {
        isDevMode = devMode;
    }
}