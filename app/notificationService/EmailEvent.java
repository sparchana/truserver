package notificationService;

import api.ServerConstants;
import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.Email;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.SimpleEmail;
import play.Logger;
import play.Play;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by dodo on 8/12/16.
 */
public class EmailEvent extends NotificationEvent {
    private String mySubject;
    private boolean isDevMode;
    private Collection<InternetAddress> devEmailIdList;

    public EmailEvent(String recipient, String message, String subject) {
        this.setMessage(message);
        this.setRecipient(recipient);
        this.setMySubject(subject);
        this.devEmailIdList = new ArrayList<>();
        try {
            this.devEmailIdList.add(
                                new InternetAddress(ServerConstants.devTeamEmail.get("techAdmin")));
        } catch (AddressException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String send() {
        this.isDevMode = play.api.Play.isDev(play.api.Play.current()) || play.api.Play.isTest(play.api.Play.current());

        boolean shouldSendEmail = Play.application().configuration().getBoolean("outbound.email.enabled");

        String message = this.getMessage();
        String recipient = this.getRecipient();
        if(recipient == null || recipient.trim().isEmpty() ){
            Logger.error("recipient id is null/empty");
            return null;
        }
        if(message == null || message.trim().isEmpty()){
            Logger.error("email message is null/empty");
            return null;
        }

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
                if(shouldSendEmail){
                    if(isDevMode()){
                        Logger.info("DevMode: No Email sent [Subject] " + mySubject + " [Message] " + message + " [Recipient] " + recipient);
                    } else {
                        Logger.info("Sending email to " + recipient);
                        email.addTo(recipient);
                        email.setBcc(devEmailIdList);
                        email.send();
                    }
                } else {
                    Logger.info("Outbound Email Disabled: No Email sent [Subject] " + mySubject + " [Message] " + message + " [Recipient] " + recipient);
                }
            } catch (EmailException e) {
                Logger.info("couldn't send mail for mailId: " + recipient);
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