package models.util;

import api.http.httpRequest.Recruiter.AddCreditRequest;
import controllers.SharedSettings;
import models.entity.Candidate;
import models.entity.JobPost;
import models.entity.OM.JobPostWorkflow;
import models.entity.Partner;
import models.entity.Recruiter.RecruiterProfile;
import models.entity.Static.InterviewTimeSlot;
import notificationService.NotificationEvent;
import notificationService.SMSEvent;
import play.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

import static api.InteractionConstants.INTERACTION_CHANNEL_CANDIDATE_WEBSITE;
import static api.ServerConstants.devTeamMobile;

/**
 * Created by batcoder1 on 26/4/16.
 */

public class SmsUtil {

    public static String addSmsToNotificationQueue(String toPhone, String msg) {

        //adding sms to notificationHandler queue
        NotificationEvent notificationEvent = new SMSEvent(toPhone, msg);
        SharedSettings.getGlobalSettings().getMyNotificationHandler().addToQueue(notificationEvent);

        return "";
    }

    public static void sendTryingToCallSms(String mobile) {

        String msg = "Hello! We tried calling you from www.TruJobs.in to help you with job search. "
        + "We will try again in sometime or you can call us on 8880007799. Download Trujobs app at http://bit.ly/2d7zDqR and apply to jobs!";

        addSmsToNotificationQueue(mobile, msg);

    }

    public static void sendOTPSms(int otp, String mobile, int channelType) {
        String msg = "Use OTP " + otp + " to register and start your job search. Welcome to www.Trujobs.in!";
        if(channelType == INTERACTION_CHANNEL_CANDIDATE_WEBSITE){
            msg += " Download Trujobs app at http://bit.ly/2d7zDqR and apply to jobs!";
        }
        addSmsToNotificationQueue(mobile, msg);
    }

    public static void sendPartnerOTPSms(int otp, String mobile) {
        String msg = "Use OTP " + otp + " to register as a partner with TruJobs. Welcome to www.Trujobs.in!";
        addSmsToNotificationQueue(mobile, msg);
    }

    public static void sendResetPasswordOTPSms(int otp, String mobile, int channelType) {
        String msg = "Use OTP " + otp + " to reset your password. Welcome to www.Trujobs.in!";
        if(channelType == INTERACTION_CHANNEL_CANDIDATE_WEBSITE){
            msg += " Download Trujobs app at http://bit.ly/2d7zDqR and apply to jobs!";
        }
        addSmsToNotificationQueue(mobile, msg);
    }

    public static void sendJobApplicationSms(String candidateName, String jobTitle, String company, String mobile, String prescreenLocation, int channelType) {
        String msg = "Hi " + candidateName + ", you have initiated your application for " + jobTitle + " job at " + company + " @" + prescreenLocation + ". Your application is under review " +
                "and you will get a notification once the recruiter shortlists you for interview. All the best! www.trujobs.in.";
        if(channelType == INTERACTION_CHANNEL_CANDIDATE_WEBSITE){
            msg += " Download Trujobs app at http://bit.ly/2d7zDqR and apply to jobs!";
        }
        addSmsToNotificationQueue(mobile, msg);
    }

    public static void sendWelcomeSmsFromSupport(String name, String mobile, String password)
    {
        String msg = "Hi " + name + ", Welcome to www.Trujobs.in! Your login details are Username: "
                + mobile.substring(3, 13) + " and password: " + password + ". Log on to trujobs.in or download Trujobs app at http://bit.ly/2d7zDqR to login and apply to jobs!!";

        addSmsToNotificationQueue(mobile, msg);
    }

    public static void sendWelcomeSmsFromWebsite(String name, String mobile)
    {
        String msg = "Hi " + name + ", Welcome to Trujobs.in! "
                + "Complete your profile today to begin your job search or download Trujobs app at http://bit.ly/2d7zDqR and apply to jobs!";

        addSmsToNotificationQueue(mobile, msg);
    }

    public static void sendWelcomeSmsToPartnerFromWebsite(String name, String mobile)
    {
        String msg = "Hi " + name + ", Welcome to Trujobs.in! "
                + "You are successfully registered as a partner. Login now to add your candidates and help them find jobs!";
        addSmsToNotificationQueue(mobile, msg);
    }

    public static void sendDuplicateLeadSmsToDevTeam(String leadMobile)
    {
        // Idea is to keep getting irritated by receiving msg until issue is resolved :D

        String msg = "Hi DevTeam, Duplicate Lead found with phone number " + leadMobile + "! "
                + "Please remove the Duplicate Entry";

        addSmsToNotificationQueue(devTeamMobile.get("Sandy"), msg);
        addSmsToNotificationQueue(devTeamMobile.get("Adarsh"), msg);
        addSmsToNotificationQueue(devTeamMobile.get("Archana"), msg);
    }

    public static void sendDuplicateCandidateSmsToDevTeam(String mobile)
    {
        // Idea is to keep getting irritated by receiving msg until issue is resolved :D

        String msg = "Hi DevTeam, Duplicate Candidate found with phone number " + mobile + "! "
                + "Please remove the Duplicate Entry";

        addSmsToNotificationQueue(devTeamMobile.get("Sandy"), msg);
        addSmsToNotificationQueue(devTeamMobile.get("Adarsh"), msg);
        addSmsToNotificationQueue(devTeamMobile.get("Archana"), msg);
    }

    public static void sendDuplicatePartnerSmsToDevTeam(String mobile)
    {
        // Idea is to keep getting irritated by receiving msg until issue is resolved :D

        String msg = "Hi DevTeam, Duplicate partner found with phone number " + mobile + "! "
                + "Please remove the Duplicate Entry";

        addSmsToNotificationQueue(devTeamMobile.get("Sandy"), msg);
        addSmsToNotificationQueue(devTeamMobile.get("Adarsh"), msg);
        addSmsToNotificationQueue(devTeamMobile.get("Archana"), msg);
    }

    public static void sendLocalityNotResolvedSmsToDevTeam(String unResolvedLocality, String city, String state)
    {
        // Idea is to tweak AddressResolver based on unresolved lat/lng (s)  :D

        String msg = "Bonjour DevTeam !! AddressResolver was not able to resolve PredictedLocality: "+unResolvedLocality+" to a Proper Locality Object! "
                + "Max Resolved Info:- City: "+city+" State:"+state;

        addSmsToNotificationQueue(devTeamMobile.get("Sandy"), msg);
        addSmsToNotificationQueue(devTeamMobile.get("Adarsh"), msg);
        addSmsToNotificationQueue(devTeamMobile.get("Archana"), msg);
    }

    public static void sendDuplicateLeadOrCandidateDeleteActionSmsToDevTeam(String mobile)
    {
        // Idea is to keep getting irritated by receiving msg until issue is resolved :D

        String msg = "Hi DevTeam, This is to inform you that a Duplicate Candidate delete action has been executed for mobile number " + mobile + "! ";

        addSmsToNotificationQueue(devTeamMobile.get("Sandy"), msg);
        addSmsToNotificationQueue(devTeamMobile.get("Adarsh"), msg);
        addSmsToNotificationQueue(devTeamMobile.get("Archana"), msg);
    }

    public static String checkDeliveryReport(String scheduleId){

        try {
            scheduleId= URLEncoder.encode(scheduleId, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            Logger.info("Exception while encoding the message" + e);
        }

        String requestString = "http://www.smsjust.com/sms/user/response.php?%20Scheduleid=" + scheduleId;
        String deliveryReport = "";
        try {
            URL url = new URL(requestString);
            BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()));
            deliveryReport = br.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return deliveryReport;
    }

    public static void sendOtpToPartnerCreatedCandidate(int otp, String mobile) {
        String msg = "Hi. You have been registered by on TruJobs for job search. Provide OTP: " + otp + " to complete registration. Download Trujobs app at http://bit.ly/2d7zDqR and apply to jobs!";
        addSmsToNotificationQueue(mobile, msg);
    }

    public static void sendJobApplicationSmsViaPartner(String candidateFirstName, String jobPostTitle, String companyName, String candidateMobile, String localityName, String partnerName) {
        String msg = "Hi " + candidateFirstName + ", we have received your job application for " + jobPostTitle + " job at " + companyName + " @" + localityName + " from our recruitment partner (" + partnerName + ").  " +
                "Kindly login at www.trujobs.in and access 'View Applied Jobs' section to complete assessment! Download Trujobs app at http://bit.ly/2d7zDqR and apply to jobs!";
        addSmsToNotificationQueue(candidateMobile, msg);
    }

    public static void sendJobApplicationSmsToPartner(String candidateFirstName, String jobPostTitle, String companyName, String partnerMobile, String localityName, String partnerFirstName) {
        String msg = "Hi " + partnerFirstName + ", you have applied to " + jobPostTitle + " job at " + companyName + " @" + localityName + " for your candidate - " + candidateFirstName +". To know more about status of Applications, call us at +91 8880007799. www.trujobs.in";
        addSmsToNotificationQueue(partnerMobile, msg);
    }

    public static void sendRecruiterOTPSms(int otp, String mobile) {
        String msg = "Use OTP " + otp + " to register as a recruiter. Welcome to www.Trujobs.in!";
        addSmsToNotificationQueue(mobile, msg);
    }

    public static void sendRecruiterLeadMsg(String mobile) {
        String msg = "Welcome to www.Trujobs.in! Thank you for getting in touch with us. Our business team will contact you within 24 hours!";
        addSmsToNotificationQueue(mobile, msg);
    }

    public static void sendRecruiterWelcomeSmsForSupportSignup(String name, String mobile, String password)
    {
        String msg = "Hi " + name + ", Your TruJobs business account is now setup and we have added 5 FREE candidate contact credits to your account! "
                + " Your login details are Username: "
                + mobile.substring(3, 13) + " and password: " + password
                + ". Log on to www.trujobs.in/recruiter to access thousands of verified candidate profiles!!!";

        addSmsToNotificationQueue(mobile, msg);
    }

    public static void sendRecruiterWelcomeSmsForSelfSignup(String name, String mobile)
    {
        String msg = "Hi " + name + ", Your TruJobs business account is now setup and we have added 5 FREE candidate contact credits to your account! "
                + ". Log on to www.trujobs.in/recruiter to access thousands of verified candidate profiles!!!";

        addSmsToNotificationQueue(mobile, msg);
    }

    public static void sendResetPasswordOTPSmsToRecruiter(int otp, String mobile) {
        String msg = "Use OTP " + otp + " to reset your password. Welcome to www.Trujobs.in!";
        addSmsToNotificationQueue(mobile, msg);
    }

    public static void sendRecruiterFreeJobPostingSms(String mobile, String name) {
        String msg = "Hi " + name + ", Thanks for posting your job on TruJobs! We are working on your job post request and you will " +
                "receive a notification once the job is made live. For any queries please call +919980293925. Thank you! www.trujobs.in";
        addSmsToNotificationQueue(mobile, msg);
    }

    public static void sendRecruiterJobPostActivationSms(RecruiterProfile recruiterProfile, JobPost jobPost) {
        String msg = "Hi " + recruiterProfile.getRecruiterProfileName() + ", your job post: " + jobPost.getJobPostTitle()
                + " has been verified and successfully posted on www.trujobs.in.! Please view your job post at www.trujobs.in/recruiter/jobPost/" +
                jobPost.getJobPostId() + " and update details if needed." +
                " Log in at www.trujobs.in/recruiter to track job applications";
        addSmsToNotificationQueue(recruiterProfile.getRecruiterProfileMobile(), msg);
    }

    public static void sendRequestCreditSms(RecruiterProfile recruiterProfile, AddCreditRequest addCreditRequest) {
        Integer contactCredits = addCreditRequest.getNoOfContactCredits();
        Integer interviewCredits = addCreditRequest.getNoOfInterviewCredits();

        String creditMsg;
        if(contactCredits == 0){
            creditMsg = interviewCredits + " candidate interview-unlock credits";
        } else if(interviewCredits == 0){
            creditMsg = contactCredits + " candidate contact-unlock credits";
        } else {
            creditMsg = contactCredits + " candidate contact-unlock credits and " + interviewCredits
                    + " candidate interview-unlock credits";
        }

        String msg = "Hi " + recruiterProfile.getRecruiterProfileName() + "! We have received your request for " + creditMsg
                + ". Our business team will contact you within 24 hours! For more queries, call +91 9980293925. Thank you.";
        addSmsToNotificationQueue(recruiterProfile.getRecruiterProfileMobile(), msg);

        msg = "Hi team, recruiter: " + recruiterProfile.getRecruiterProfileName() + " with mobile " + recruiterProfile.getRecruiterProfileMobile() + " of company: " +
                recruiterProfile.getCompany().getCompanyName() +  " has requested for " + creditMsg
                + ". Thank You";

        addSmsToNotificationQueue(devTeamMobile.get("Sandy"), msg);
        addSmsToNotificationQueue(devTeamMobile.get("Adarsh"), msg);
        addSmsToNotificationQueue(devTeamMobile.get("Archana"), msg);
    }

    public static void sendRecruiterCreditTopupSms(RecruiterProfile recruiterProfile, Integer contactCredits, Integer interviewCredits) {

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

        String msg = "Hi " + recruiterProfile.getRecruiterProfileName()
                + "! Congratulations! Your Trujobs account is credited with " + creditMsg
                + ".  Log in at www.trujobs.in/recruiter to start contacting thousands of verified candidates!. Thank you.";
        addSmsToNotificationQueue(recruiterProfile.getRecruiterProfileMobile(), msg);
    }

    public static void sendCandidateUnlockSms(String companyName, String recruiterName, String candidateMobile, String candidateName) {
        String msg = "Hi " + candidateName + ", Recruiter " + recruiterName + " from company " + companyName +
                     " viewed your contact details! Download Trujobs app at http://bit.ly/2d7zDqR " +
                   " or login at www.trujobs.in to update your profile and apply to more jobs!!  Thank you.";
        addSmsToNotificationQueue(candidateMobile, msg);
    }

    public static void sendInterviewConfirmationSms(JobPostWorkflow jobApplication, Candidate candidate) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(jobApplication.getScheduledInterviewDate());
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);

        String interviewDate = day + "-" + (month + 1) + "-" + year;

        String msg = "Hi " + candidate.getCandidateFirstName() + ", your interview for " + jobApplication.getJobPost().getJobPostTitle() + " at " + jobApplication.getJobPost().getCompany().getCompanyName() +
                " has been confirmed on " + interviewDate + " between " + jobApplication.getScheduledInterviewTimeSlot().getInterviewTimeSlotName() + ". Please reach the office on time with your documents. All the best!";
        if(jobApplication.getJobPost().getJobPostAddress() != null || !Objects.equals(jobApplication.getJobPost().getJobPostAddress(), "")){
            msg += "\n\nAddress: " + jobApplication.getJobPost().getJobPostAddress();
        }

        if (jobApplication.getInterviewLocationLat() != null) {
            msg += "\nDirections: http://maps.google.com/?q=" + jobApplication.getInterviewLocationLat() + "," + jobApplication.getInterviewLocationLng();
        }
        addSmsToNotificationQueue(candidate.getCandidateMobile(), msg);
    }

    public static String getSameDayInterviewAlertSmsString(JobPostWorkflow jobPostWorkflow){

        String msg = "Hi " + jobPostWorkflow.getCandidate().getCandidateFirstName() + ", your interview for " + jobPostWorkflow.getJobPost().getJobPostTitle() + " at " + jobPostWorkflow.getJobPost().getCompany().getCompanyName() +
                " is scheduled today, between " + jobPostWorkflow.getScheduledInterviewTimeSlot().getInterviewTimeSlotName() + ". Please reach the office on time with your documents. All the best!";
        if(jobPostWorkflow.getJobPost().getJobPostAddress() != null || !Objects.equals(jobPostWorkflow.getJobPost().getJobPostAddress(), "")){
            msg += "\n\nAddress: " + jobPostWorkflow.getJobPost().getJobPostAddress();
        }

        if (jobPostWorkflow.getInterviewLocationLat() != null) {
            msg += "\nDirections: http://maps.google.com/?q=" + jobPostWorkflow.getInterviewLocationLat() + "," + jobPostWorkflow.getInterviewLocationLng();
        }
        return msg;
    }
    public static String getNextDayInterviewAlertSmsString(JobPostWorkflow jobPostWorkflow) {

        String msg = "Hi " + jobPostWorkflow.getCandidate().getCandidateFirstName() + ", your interview for " + jobPostWorkflow.getJobPost().getJobPostTitle() + " at " + jobPostWorkflow.getJobPost().getCompany().getCompanyName() +
                " is scheduled tomorrow, between " + jobPostWorkflow.getScheduledInterviewTimeSlot().getInterviewTimeSlotName() + ". Please reach the office on time with your documents. All the best!";
        if(jobPostWorkflow.getJobPost().getJobPostAddress() != null || !Objects.equals(jobPostWorkflow.getJobPost().getJobPostAddress(), "")){
            msg += "\n\nAddress: " + jobPostWorkflow.getJobPost().getJobPostAddress();
        }

        if (jobPostWorkflow.getInterviewLocationLat() != null) {
            msg += "\nDirections: http://maps.google.com/?q=" + jobPostWorkflow.getInterviewLocationLat() + "," + jobPostWorkflow.getInterviewLocationLng();
        }
        return msg;
    }

    public static void sendInterviewConfirmationSmsToPartner(JobPostWorkflow jobApplication, Candidate candidate, Partner partner) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(jobApplication.getScheduledInterviewDate());
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);

        String interviewDate = day + "-" + (month + 1) + "-" + year;

        String msg = "Hi " + partner.getPartnerFirstName() + ", your candidate, " + candidate.getCandidateFirstName() + "'s interview at " +
                interviewDate + " between " + jobApplication.getScheduledInterviewTimeSlot().getInterviewTimeSlotName() + " for " + jobApplication.getJobPost().getJobPostTitle() +
                " at " + jobApplication.getJobPost().getCompany().getCompanyName() +
                " has been accepted by the recruiter. Please ask your candidate to carry required documents and reach the interview venue on time. ";

        if(jobApplication.getInterviewLocationLat() != null){
            msg += "Address: http://maps.google.com/?q=" + jobApplication.getInterviewLocationLat() + ", " + jobApplication.getInterviewLocationLng();
        }

        msg += ". Thanks for using www.trujobs.in!";

        addSmsToNotificationQueue(partner.getPartnerMobile(), msg);
    }

    public static void sendInterviewShortlistSms(JobPost jobPost, Candidate candidate) {
        String msg = "Hi " + candidate.getCandidateFirstName() + ", your job application for " + jobPost.getJobPostTitle() + " at " + jobPost.getCompany().getCompanyName() +
                " has been shortlisted for the interview. We will get in touch with you shortly to confirm interview date and time. Thank you!";
        addSmsToNotificationQueue(candidate.getCandidateMobile(), msg);
    }

    public static void sendInterviewRejectionSms(JobPostWorkflow jobPost, Candidate candidate) {
        String msg = "Hi " + candidate.getCandidateFirstName() + ", your job application for " + jobPost.getJobPost().getJobPostTitle() + " at " + jobPost.getJobPost().getCompany().getCompanyName() +
                " was not shortlisted for the interview. Please log on to www.trujobs.in and apply to other jobs. Download Trujobs app at http://bit.ly/2d7zDqR!";
        addSmsToNotificationQueue(candidate.getCandidateMobile(), msg);
    }

    public static void sendInterviewRejectionSmsToPartner(JobPostWorkflow jobApplication, Candidate candidate, Partner partner) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(jobApplication.getScheduledInterviewDate());
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);

        String interviewDate = day + "-" + (month + 1) + "-" + year;

        String msg = "Hi " + partner.getPartnerFirstName() + ", your candidate, " + candidate.getCandidateFirstName() + "'s interview at " +
                interviewDate + " between " + jobApplication.getScheduledInterviewTimeSlot().getInterviewTimeSlotName() + " for " + jobApplication.getJobPost().getJobPostTitle() +
                " at " + jobApplication.getJobPost().getCompany().getCompanyName() +
                " has been rejected by the recruiter. Please login at www.trujobs.in and apply to other jobs for this candidate. Thanks!";

        addSmsToNotificationQueue(partner.getPartnerMobile(), msg);
    }


    public static void sendInterviewReschedulingSms(JobPostWorkflow jobApplication, Candidate candidate, Date interviewDate, InterviewTimeSlot slot) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(interviewDate);
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);

        String interviewDateString = day + "-" + (month + 1) + "-" + year;

        String msg = "Hi " + candidate.getCandidateFirstName() + ", your interview for " + jobApplication.getJobPost().getJobPostTitle() + " at " + jobApplication.getJobPost().getCompany().getCompanyName() +
                " has been rescheduled on " + interviewDateString + " between " + slot.getInterviewTimeSlotName() + ". Download Trujobs app at http://bit.ly/2d7zDqR or log on to www.trujobs.in and confirm the new interview date." +
                "and time in \"View Applied Jobs\" section. Thank you!";
        addSmsToNotificationQueue(candidate.getCandidateMobile(), msg);
    }

    public static void sendInterviewReschedulingSmsToPartner(JobPostWorkflow jobApplication, Candidate candidate, Date interviewDate, InterviewTimeSlot slot, Partner partner) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(interviewDate);
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);

        String interviewDateString = day + "-" + (month + 1) + "-" + year;

        String msg = "Hi " + partner.getPartnerFirstName() + ", your candidate, " + candidate.getCandidateFirstName() + "'s interview for " +
                jobApplication.getJobPost().getJobPostTitle() +
                " at " + jobApplication.getJobPost().getCompany().getCompanyName() +
                " has been rescheduled by the recruiter to " + interviewDateString + " between " + slot.getInterviewTimeSlotName() + ".  Please login to www.trujobs.in to accept the interview. Thanks!";

        addSmsToNotificationQueue(partner.getPartnerMobile(), msg);
    }


    public static void sendInterviewCandidateConfirmation(JobPostWorkflow jobApplication, Candidate candidate) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(jobApplication.getScheduledInterviewDate());
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);

        String interviewDate = day + "-" + (month + 1) + "-" + year;

        String msg = "Hi " + jobApplication.getJobPost().getRecruiterProfile().getRecruiterProfileName() + ", candidate: " + candidate.getCandidateFirstName() + " has confirmed the interview for " + jobApplication.getJobPost().getJobPostTitle() +
                " job which was re-scheduled on " + interviewDate + " between " + jobApplication.getScheduledInterviewTimeSlot().getInterviewTimeSlotName() + ". Log on to www.trujobs.in/recruiter and manage your interviews. Thank you!";
        addSmsToNotificationQueue(jobApplication.getJobPost().getRecruiterProfile().getRecruiterProfileMobile(), msg);
    }

    public static void sendInterviewCandidateInterviewReject(JobPostWorkflow jobApplication, Candidate candidate) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(jobApplication.getScheduledInterviewDate());
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);

        String interviewDate = day + "-" + (month + 1) + "-" + year;

        String msg = "Hi " + jobApplication.getJobPost().getRecruiterProfile().getRecruiterProfileName() + ", candidate: " + candidate.getCandidateFirstName() + " has rejected the interview for " + jobApplication.getJobPost().getJobPostTitle() +
                " job which was re-scheduled on " + interviewDate + " between " + jobApplication.getScheduledInterviewTimeSlot().getInterviewTimeSlotName() + ". Log on to www.trujobs.in/recruiter and manage your interviews. Thank you!";
        addSmsToNotificationQueue(jobApplication.getJobPost().getRecruiterProfile().getRecruiterProfileMobile(), msg);
    }

/*
    public static void updateRecruiterWithCandidateStatus(JobPostWorkflow jobApplication, Candidate candidate, Integer status) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(jobApplication.getScheduledInterviewDate());
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);

        String currentStatus = "";
        switch (status){
            case ServerConstants.JWF_STATUS_CANDIDATE_INTERVIEW_STATUS_NOT_GOING: currentStatus = "is not going "; break;
            case ServerConstants.JWF_STATUS_CANDIDATE_INTERVIEW_STATUS_DELAYED: currentStatus = "is delayed "; break;
            case ServerConstants.JWF_STATUS_CANDIDATE_INTERVIEW_STATUS_ON_THE_WAY: currentStatus = "has left "; break;
            case ServerConstants.JWF_STATUS_CANDIDATE_INTERVIEW_STATUS_REACHED: currentStatus = "has reached the office "; break;
        }

        String interviewDate = day + "-" + (month + 1) + "-" + year;

        String msg = "Hi " + jobApplication.getJobPost().getRecruiterProfile().getRecruiterProfileName() + ", candidate: " + candidate.getCandidateFirstName() + ", " + currentStatus
                + " for the interview:  " + jobApplication.getJobPost().getJobPostTitle() +
                " job which is on " + interviewDate + " between " + jobApplication.getScheduledInterviewTimeSlot().getInterviewTimeSlotName() + ". Log on to www.trujobs.in/recruiter and manage your interviews. Thank you!";
        addSmsToNotificationQueue(jobApplication.getJobPost().getRecruiterProfile().getRecruiterProfileMobile(), msg);
    }
*/
    public static void updateRecruiterWithCandidateStatus(JobPostWorkflow jobApplication, Candidate candidate) {
        String msg = "Hi " + jobApplication.getJobPost().getRecruiterProfile().getRecruiterProfileName() + ", candidate: " + candidate.getCandidateFirstName() + " has reached the venue " +
                " for " + jobApplication.getJobPost().getJobPostTitle() +
                " job which is scheduled today between " + jobApplication.getScheduledInterviewTimeSlot().getInterviewTimeSlotName() + ". Log on to www.trujobs.in/recruiter to view status of all scheduled interviews. Thank you!";
        addSmsToNotificationQueue(jobApplication.getJobPost().getRecruiterProfile().getRecruiterProfileMobile(), msg);
    }


    public static void sendSelectedSmsToCandidate(JobPostWorkflow jobApplication) {
        String msg = "Hi " + jobApplication.getCandidate().getCandidateMobile() + ", Congratulations! You have been selected for the job: " + jobApplication.getJobPost().getJobPostTitle()
                + " at " + jobApplication.getJobPost().getCompany().getCompanyName() + ". The recruiter will contact you for further details. You can contact TruJobs at 8880007799 for any queries. www.trujobs.in. Download Trujobs app at http://bit.ly/2d7zDqR";
        addSmsToNotificationQueue(jobApplication.getCandidate().getCandidateMobile(), msg);
    }

    public static void sendRejectedSmsToCandidate(JobPostWorkflow jobApplication) {
        String msg = "Hi " + jobApplication.getCandidate().getCandidateMobile() + ", Unfortunately the recruiter has rejected your application for the job: " + jobApplication.getJobPost().getJobPostTitle()
                + " at " + jobApplication.getJobPost().getCompany().getCompanyName() + ". You can contact TruJobs at 8880007799 for any queries. www.trujobs.in. Download Trujobs app at http://bit.ly/2d7zDqR";
        addSmsToNotificationQueue(jobApplication.getCandidate().getCandidateMobile(), msg);
    }
}
