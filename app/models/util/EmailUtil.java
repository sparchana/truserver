package models.util;

import api.ServerConstants;
import api.http.httpRequest.Recruiter.AddCreditRequest;
import models.entity.Recruiter.RecruiterProfile;
import org.apache.commons.lang3.text.WordUtils;
import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.Email;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.SimpleEmail;
import play.Logger;
import play.Play;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

import static api.ServerConstants.devTeamEmail;
import static api.ServerConstants.devTeamMobile;

/**
 * Created by dodo on 15/10/16.
 */
public class EmailUtil {

/*
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
*/

    public static void sendEmail(String to, String message, String subject) throws EmailException {
        boolean isDevMode = play.api.Play.isDev(play.api.Play.current()) || play.api.Play.isTest(play.api.Play.current());

        Email email = new SimpleEmail();
        email.setHostName(Play.application().configuration().getString("mail.smtp.host"));
        email.setSmtpPort(465);
        email.setAuthenticator(new DefaultAuthenticator(Play.application().configuration().getString("mail.smtp.user")
                , Play.application().configuration().getString("mail.smtp.pass")));
        email.setSSLOnConnect(true);
        email.setContent(message, "text/html; charset=utf-8");
        email.setFrom("recruiter.support@trujobs.in", "Trujobs Recruiter");
        email.setSubject(subject);
        email.addTo(to);
        if(isDevMode){
            Logger.info("DevMode: No Email sent");
        } else {
            email.send();
        }

    }

    public static void sendRequestCreditEmail(RecruiterProfile recruiterProfile, AddCreditRequest addCreditRequest) throws EmailException {
        String cat;
        if(addCreditRequest.getCreditCategory() == ServerConstants.RECRUITER_CATEGORY_CONTACT_UNLOCK){
            cat = "contact unlock credits";
        } else{
            cat = "interview unlock credits";
        }

        String message = "Hi " + recruiterProfile.getRecruiterProfileName() + "! We have received your request for " + addCreditRequest.getNoOfCredits() + " " + cat
                + ". Our business team will contact you within 24 hours! For more queries, call +91 9980293925. Thank you.";

        sendEmail(recruiterProfile.getRecruiterProfileEmail(), message, "Contact Unlock Request");

        message = "Hi team, recruiter " + recruiterProfile.getRecruiterProfileName() + " with mobile " + recruiterProfile.getRecruiterProfileMobile() + " has requested for " + addCreditRequest.getNoOfCredits() + " " + cat
                + ". Amount = ₹" + addCreditRequest.getCreditAmount();

        sendEmail(devTeamEmail.get("Adarsh"), message, "Contact Unlock Request");
        sendEmail(devTeamEmail.get("Archana"), message, "Contact Unlock Request");
/*        sendEmail(devTeamEmail.get("Avishek"), message, "Contact Unlock Request");
        sendEmail(devTeamEmail.get("Chillu"), message, "Contact Unlock Request");*/
        sendEmail(devTeamEmail.get("Sandy"), message, "Contact Unlock Request");
    }
}
