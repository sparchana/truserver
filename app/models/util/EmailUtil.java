package models.util;

import org.apache.commons.lang3.text.WordUtils;
import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.Email;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.SimpleEmail;
import play.Play;

/**
 * Created by dodo on 15/10/16.
 */
public class EmailUtil {
    public static void sendTestEmail() throws EmailException {

        String message = "<center>\n" +
                "            <div style=\"text-align: center; width: 60%; background: #f9f9f9\">\n" +
                "                <img src='https://s3.amazonaws.com/trujobs.in/companyLogos/trujobs.png'>\n" +
                "                <br>\n" +
                "                <h3>Welcome to TruJobs.in</h3>\n" +
                "                <hr style=\"background: #00c853\">\n" +
                "                <p style=\"padding: 24px\">\n" +
                "                Hi Recruiter! Welcome to trujobs. some text\n" +
                "                    Hi Recruiter! Welcome to trujobs. some text\n" +
                "                    Hi Recruiter! Welcome to trujobs. some text\n" +
                "                    Hi Recruiter! Welcome to trujobs. some text\n" +
                "                    Hi Recruiter! Welcome to trujobs. some text\n" +
                "                    Hi Recruiter! Welcome to trujobs. some text\n" +
                "                    Hi Recruiter! Welcome to trujobs. some text\n" +
                "                    Hi Recruiter! Welcome to trujobs. some text\n" +
                "                </p>\n" +
                "                <div style=\"background: #434145; padding: 12px\">\n" +
                "                Footer text\n" +
                "                </div>\n" +
                "            </div>\n" +
                "        </center>\n";

        Email email = new SimpleEmail();
        email.setHostName(Play.application().configuration().getString("mail.smtp.host"));
        email.setSmtpPort(465);
        email.setAuthenticator(new DefaultAuthenticator(Play.application().configuration().getString("mail.smtp.user")
                , Play.application().configuration().getString("mail.smtp.pass")));
        email.setSSLOnConnect(true);
        email.setContent(message, "text/html; charset=utf-8");
        email.setFrom("adarsh.raj@trujobs.in");
        email.setSubject("Welcome mail");
        email.addTo("rajshahbvm123@gmail.com");
        email.send();
    }

}
