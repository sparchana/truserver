package models.util;

import api.http.httpRequest.Recruiter.AddCreditRequest;
import controllers.SharedSettings;
import models.entity.JobPost;
import models.entity.Recruiter.RecruiterProfile;
import notificationService.EmailEvent;
import notificationService.NotificationEvent;
import play.Logger;

import static api.ServerConstants.devTeamEmail;

/**
 * Created by dodo on 15/10/16.
 */
public class EmailUtil {

    private static final String TRUJOBS_LOGO = "https://s3.amazonaws.com/trujobs.in/companyLogos/trujobs.png";
    private static final String LINKEDIN_LOGO = "https://s3.amazonaws.com/trujobs.in/companyLogos/linkedin-circle.png";
    private static final String FACEBOOK_LOGO = "https://s3.amazonaws.com/trujobs.in/companyLogos/facebook-circle.png";
    private static final String WEB_ICON = "https://s3.amazonaws.com/trujobs.in/companyLogos/website-circle.png";

    private static final String MAIL_HEADER = "<center>\n" +
                "            <div style=\"text-align: center; width: 90%; background: #f9f9f9\">\n" +
                "                <a href = \"http://trujobs.in/\" target=\"_blank\">\n" +
                "                <img src='" + TRUJOBS_LOGO + "'> </a>\n" +
                "                <h3>Greetings from TruJobs.in!</h3>\n" +
                "                <hr style=\"background: #ffffff\">\n" + "<br>";

    private static String MESSAGE_DIV_START = "<div style=\" display: inline-block; text-align: left; font-color: #000000; " +
            " letter-spacing: 1.5px; color: #000000;"+
            " line-height: 20px; padding-left: 4%; padding-right: 4%; width: 70%; background: #ffffff\">";

    private static String MESSAGE_HELLO = "Hello ";

    private static String MESSAGE_THANKYOU = "<br> <br> Thank You, <br> Team TruJobs <br> www.trujobs.in <br><br><br>";

    private static String MESSAGE_DIV_END = "</div>";

    private static  String MAIL_FOOTER = "<div style=\" display: inline-block; text-align: center; width: 70%\">" +
            " <br><br> \n" +
            "  <a href=\"https://www.linkedin.com/company/trujobs\" target=\"_blank\">\n" +
            " <img src='" + LINKEDIN_LOGO  + "' height=\"30px\" width=\"30px\"> </a>\n" +
            " <a href=\"https://www.facebook.com/trujobs\" target=\"_blank\">\n" +
            " <img src='" + FACEBOOK_LOGO + "' height=\"30px\" width=\"30px\"> </a>\n" +
            " <a href=\"http://www.trujobs.in\" target=\"_blank\">\n" +
            " <img src='" + WEB_ICON + "' height=\"30px\" width=\"30px\"> </a>\n" +
            " <br><br><br> </div> </center>\n";


    public static void sendRecruiterWelcomeEmailForSelfSignup(RecruiterProfile recruiterProfile)
    {
        String subject = "Welcome to TruJobs! FREE Credits Inside!!";

        String message = MAIL_HEADER + MESSAGE_DIV_START + MESSAGE_HELLO
                + recruiterProfile.getRecruiterProfileName() + "!<br><br>"
                + "Welcome to Trujobs recruitment platform!<br>"
                + "As a welcome gift we have added <b> 3 FREE CONTACT UNLOCK credits</b> to your account!. "
                + "Log in at www.trujobs.in/recruiter to start contacting thousands of verified candidates!.<br><br>"
                + "Happy hiring!"
                + MESSAGE_THANKYOU +  MESSAGE_DIV_END + MAIL_FOOTER;

        Logger.info(" Sending welcome email for recruiter " + recruiterProfile.getRecruiterProfileName());
        addEmailToNotificationQueue(recruiterProfile.getRecruiterProfileEmail(), message, subject);
    }

    public static void sendRecruiterWelcomeEmailForSupportSignup(RecruiterProfile recruiterProfile, String dummyPassword)
    {
        String subject = "Welcome to TruJobs! FREE Credits Inside!!";

        String message = MAIL_HEADER + MESSAGE_DIV_START + MESSAGE_HELLO
                + recruiterProfile.getRecruiterProfileName() + "!<br><br>"
                +  "Welcome to Trujobs recruitment platform!<br>"
                + "Your TruJobs business account is now setup. Your login details are: <br>"
                + " <b> Username: </b> " + recruiterProfile.getRecruiterProfileMobile().substring(3, 13) + "<br>"
                + " <b> Password: </b> " + dummyPassword + "<br><br>"
                + "As a welcome gift we have added <b> 5 FREE candidate contact credits </b>to your account!. "
                + "Log in at www.trujobs.in/recruiter to start contacting thousands of verified candidates!.<br><br>"
                + "Happy hiring!"
                + MESSAGE_THANKYOU +  MESSAGE_DIV_END + MAIL_FOOTER;

        addEmailToNotificationQueue(recruiterProfile.getRecruiterProfileEmail(), message, subject);
    }

    public static void sendRecruiterNewJobPostEmail(RecruiterProfile recruiterProfile, JobPost jobPost)
    {

        String subject = "Trujobs.in : Your job post is one step away from receiving applications!";

        String message = MAIL_HEADER + MESSAGE_DIV_START + MESSAGE_HELLO
                + recruiterProfile.getRecruiterProfileName() + "!<br><br>"
                + "Thanks for posting your job on TruJobs! "
                + "Your job post: <b> '" + jobPost.getJobPostTitle() + "' @ '" + jobPost.getCompany().getCompanyName() + "' </b>"
                + " is being reviewed by our recruitment support team. <br>"
                + "You will be notified once the job post is made live on our website <br>"
                + "You can track status of your job post and all applications at http://trujobs.in/recruiter/allRecruiterJobPosts.<br><br>"
                + "Happy hiring!"
                + MESSAGE_THANKYOU +  MESSAGE_DIV_END + MAIL_FOOTER;

        addEmailToNotificationQueue(recruiterProfile.getRecruiterProfileEmail(), message, subject);
    }

    public static String getEmailHTML(RecruiterProfile recruiterProfile, String message){

        return MAIL_HEADER + MESSAGE_DIV_START + MESSAGE_HELLO
                + recruiterProfile.getRecruiterProfileName() + "!<br><br>"
                + message
                + "Happy hiring!"
                + MESSAGE_THANKYOU +  MESSAGE_DIV_END + MAIL_FOOTER;

    }

    public static void sendRecruiterJobPostLiveEmail(RecruiterProfile recruiterProfile, JobPost jobPost)
    {
        String subject = "Trujobs.in : Your job is now live on www.trujobs.in!";
        String message = MAIL_HEADER + MESSAGE_DIV_START + MESSAGE_HELLO
                + recruiterProfile.getRecruiterProfileName() + "!<br><br>"
                + "Your job post: <b> '" + jobPost.getJobPostTitle() + "' @ '" + jobPost.getCompany().getCompanyName() + "' </b>"
                + "has been approved and made live on www.trujobs.in. "
                + "You can track all applications at http://trujobs.in/recruiter/allRecruiterJobPosts.<br><br>"
                + "Happy hiring!"
                + MESSAGE_THANKYOU +  MESSAGE_DIV_END + MAIL_FOOTER;

        addEmailToNotificationQueue(recruiterProfile.getRecruiterProfileEmail(), message, subject);
    }

    public static void sendRecruiterRequestCreditEmail(RecruiterProfile recruiterProfile, AddCreditRequest addCreditRequest)
    {
        Integer contactCredits = addCreditRequest.getNoOfContactCredits();
        Integer interviewCredits = addCreditRequest.getNoOfInterviewCredits();
        String subject = "Trujobs.in : Your credit unlock request is being processed";
        String internalMailSubject = "New Credit Unlock Request from Recruiter";

        String creditMsg;
        if (contactCredits == 0) {
            creditMsg = interviewCredits + " candidate interview-unlock credits";
        } else if (interviewCredits == 0){
            creditMsg = contactCredits + " candidate contact-unlock credits";
        } else {
            creditMsg = contactCredits + " candidate contact-unlock credits and " + interviewCredits + " candidate interview-unlock credits";
        }

        String message = MAIL_HEADER + MESSAGE_DIV_START + MESSAGE_HELLO
                + recruiterProfile.getRecruiterProfileName() + "!<br><br>"
                + "We have received your request for " + creditMsg
                + ". Our business team will contact you within 24 hours! <br><br> For more queries, call +91 9980293925."
                + MESSAGE_THANKYOU +  MESSAGE_DIV_END + MAIL_FOOTER;

        String internalMessage = "Hi team, recruiter: " + recruiterProfile.getRecruiterProfileName() + " with mobile "
                + recruiterProfile.getRecruiterProfileMobile() + " of company: "
                + recruiterProfile.getCompany().getCompanyName() +  " has requested for " + creditMsg
                + ". Thank You";


        addEmailToNotificationQueue(recruiterProfile.getRecruiterProfileEmail(), message, subject);

        addEmailToNotificationQueue(devTeamEmail.get("Adarsh"), internalMessage, internalMailSubject);
        addEmailToNotificationQueue(devTeamEmail.get("Archana"), internalMessage, internalMailSubject);
        addEmailToNotificationQueue(devTeamEmail.get("recruiter_support"), internalMessage, internalMailSubject);
        addEmailToNotificationQueue(devTeamEmail.get("Avishek"), internalMessage, internalMailSubject);
        addEmailToNotificationQueue(devTeamEmail.get("Sandy"), internalMessage, internalMailSubject);
        addEmailToNotificationQueue(devTeamEmail.get("Rafik"), internalMessage, internalMailSubject);
    }

    public static void sendRecruiterCreditTopupMail(RecruiterProfile recruiterProfile, Integer contactCredits, Integer interviewCredits)
    {
        String subject = "Trujobs.in : Your account is recharged with credits!";

        String creditMsg;
        if (contactCredits != null && contactCredits > 0 && interviewCredits != null && interviewCredits > 0) {
            creditMsg = contactCredits + " candidate contact-unlock credits and " + interviewCredits + " candidate interview-unlock credits";
        }
        else if (contactCredits != null && contactCredits > 0) {
            creditMsg = contactCredits + " candidate contact-unlock credits";
        } else if (interviewCredits != null && interviewCredits > 0){
            creditMsg = interviewCredits + " candidate interview-unlock credits";
        }
        else {
            return;
        }

        String message = MAIL_HEADER + MESSAGE_DIV_START + MESSAGE_HELLO
                + recruiterProfile.getRecruiterProfileName() + "!<br><br>"
                + "Congratulations! Your Trujobs account is credited with " + creditMsg
                + ". Log in at www.trujobs.in/recruiter to start contacting thousands of verified candidates!.<br><br>"
                + MESSAGE_THANKYOU +  MESSAGE_DIV_END + MAIL_FOOTER;

        addEmailToNotificationQueue(recruiterProfile.getRecruiterProfileEmail(), message, subject);
    }

    private static void addEmailToNotificationQueue(String to, String message, String subject) {

        //adding to notificationHandler queue
        NotificationEvent notificationEvent = new EmailEvent(to, message, subject);
        SharedSettings.getGlobalSettings().getMyNotificationHandler().addToQueue(notificationEvent);
    }
}
