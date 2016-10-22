package models.util;

import api.ServerConstants;
import api.http.httpRequest.Recruiter.AddCreditRequest;
import models.entity.JobPost;
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

    private static String mailHeader = "<center>\n" +
                "            <div style=\"text-align: center; width: 90%; background: #f9f9f9\">\n" +
                "                <img src='https://s3.amazonaws.com/trujobs.in/companyLogos/trujobs.png'>\n" +
                "                <h3>Greetings from TruJobs.in!</h3>\n" +
                "                <hr style=\"background: #ffffff\">\n" + "<br>";

    private static String messageDivStart = "<div style=\" display: inline-block; text-align: left; font-color: #000000; " +
            " letter-spacing: 1.5px; "+
            " line-height: 20px; padding-left: 4%; padding-right: 4%; width: 70%; background: #ffffff\">";

    private static String mailHello = "Hello ";

    private static String mailThankYou = "<br> <br> Thank You, <br> Team TruJobs <br> www.trujobs.in <br><br><br>";

    private static String messageDivEnd = "</div>";

    private static  String mailFooter = "<div style=\" display: inline-block; text-align: center; width: 70%\">" +
            " <br><br> \n" +
            "  <a href=\"https://www.linkedin.com/company/trujobs\" target=\"_blank\">\n" +
            " <img src='https://s3.amazonaws.com/trujobs.in/companyLogos/linkedin-circle.png' height=\"30px\" width=\"30px\"> </a>\n" +
            " <a href=\"https://www.facebook.com/trujobs\" target=\"_blank\">\n" +
            " <img src='https://s3.amazonaws.com/trujobs.in/companyLogos/facebook-circle.png' height=\"30px\" width=\"30px\"> </a>\n" +
            " <br><br><br> </div> </center>\n";


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
        Integer contactCredits = addCreditRequest.getNoOfContactCredits();
        Integer interviewCredits = addCreditRequest.getNoOfInterviewCredits();

        String creditMsg;
        if (contactCredits == 0) {
            creditMsg = interviewCredits + " interview unlock credits";
        } else if(interviewCredits == 0){
            creditMsg = contactCredits + " contact unlock credits";
        } else {
            creditMsg = contactCredits + " contact unlock credits and " + interviewCredits + " interview unlock credits";
        }

        String message = mailHeader + messageDivStart + mailHello + recruiterProfile.getRecruiterProfileName() + "!<br><br>"
                + "We have received your request for " + creditMsg
                + ". Our business team will contact you within 24 hours! <br><br> For more queries, call +91 9980293925."
                + mailThankYou +  messageDivEnd + mailFooter;

        sendEmail(recruiterProfile.getRecruiterProfileEmail(), message,
                "Trujobs.in : Your credit unlock request is being processed");

        message = "Hi team, recruiter: " + recruiterProfile.getRecruiterProfileName() + " with mobile " + recruiterProfile.getRecruiterProfileMobile() + " of company: " +
                recruiterProfile.getCompany().getCompanyName() +  " has requested for " + creditMsg
                + ". Thank You";

        sendEmail(devTeamEmail.get("Adarsh"), message, "Contact Unlock Credit Request");
        sendEmail(devTeamEmail.get("Archana"), message, "Contact Unlock Credit Request");
        sendEmail(devTeamEmail.get("recruiter_support"), message, "Contact Unlock Credit Request");
        sendEmail(devTeamEmail.get("Avishek"), message, "Contact Unlock Credit Request");
        sendEmail(devTeamEmail.get("Sandy"), message, "Contact Unlock Credit Request");
    }

    public static void sendSuccessJobPostEmailToRecruiter(RecruiterProfile recruiterProfile, JobPost jobPost) throws EmailException {

        String message = mailHeader + messageDivStart + mailHello + recruiterProfile.getRecruiterProfileName() + "!<br><br>"
                + "Your job post: '" + jobPost.getJobPostTitle() + "'"
                + " has been approved and made live on www.trujobs.in. Log in at www.trujobs.in/recruiter to track job applications.<br><br>"
                + " Happy hiring!"
                + mailThankYou +  messageDivEnd + mailFooter;

        sendEmail(recruiterProfile.getRecruiterProfileEmail(), message, "Trujobs.in : Your job is now live on www.trujobs.in!");
    }

}
