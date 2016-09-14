package models.util;

import play.Logger;
import play.Play;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;

import static api.ServerConstants.devTeamMobile;

/**
 * Created by batcoder1 on 26/4/16.
 */

public class SmsUtil {

    public static String sendSms(String toPhone, String msg) {
        boolean isDevMode = play.api.Play.isDev(play.api.Play.current()) || play.api.Play.isTest(play.api.Play.current());

        String uname = Play.application().configuration().getString("sms.gateway.user");
        String id = Play.application().configuration().getString("sms.gateway.password");
        String sender = Play.application().configuration().getString("sms.gateway.sender");

        try {
            msg = URLEncoder.encode(msg, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            Logger.info("Exception while encoding the message" + e);
            return "MSG_ENCODING_FAILED";
        }

        String requestString = "https://www.smsjust.com/sms/user/urlsms.php?username="
                + uname + "&pass=" + id + "&senderid=" + sender + "&dest_mobileno=" +
                toPhone + "&message=" + msg + "&response=Y";

        Logger.info("msg: "+ requestString);

        String smsResponse = "";

        if(isDevMode){
            Logger.info("DevMode: No sms sent");
            return "DevMode: No sms sent";
        } else {
            try {
                URL url = new URL(requestString);
                BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()));
                smsResponse = br.readLine();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return smsResponse;
        }
    }

    public static void sendTryingToCallSms(String mobile) {

        String msg = "Hello! We tried calling you from www.TruJobs.in to help you with job search. "
        + "We will try again in sometime or you can call us on 8880007799";

        sendSms(mobile, msg);

    }

    public static void sendOTPSms(int otp, String mobile) {
        String msg = "Use OTP " + otp + " to register and start your job search. Welcome to www.Trujobs.in!";
        sendSms(mobile, msg);
    }

    public static void sendPartnerOTPSms(int otp, String mobile) {
        String msg = "Use OTP " + otp + " to register as a partner with TruJobs. Welcome to www.Trujobs.in!";
        sendSms(mobile, msg);
    }

    public static void sendResetPasswordOTPSms(int otp, String mobile) {
        String msg = "Use OTP " + otp + " to reset your password. Welcome to www.Trujobs.in!";
        sendSms(mobile, msg);
    }

    public static void sendJobApplicationSms(String candidateName, String jobTitle, String company, String mobile, String prescreenLocation) {
        String msg = "Hi " + candidateName + ", you have successfully applied to " + jobTitle + " job at " + company + " @" + prescreenLocation + ". Call us at +91 8048039089 to know about the status of your job application. All the best! www.trujobs.in";
        sendSms(mobile, msg);
    }

    public static void sendWelcomeSmsFromSupport(String name, String mobile, String password)
    {
        String msg = "Hi " + name + ", Welcome to www.Trujobs.in! Your login details are Username: "
                + mobile.substring(3, 13) + " and password: " + password + ". Use this to login at trujobs.in !!";

        sendSms(mobile, msg);
    }

    public static void sendWelcomeSmsFromWebsite(String name, String mobile)
    {
        String msg = "Hi " + name + ", Welcome to Trujobs.in! "
                + "Complete your profile and skill assessment today to begin your job search";

        sendSms(mobile, msg);
    }

    public static void sendWelcomeSmsToPartnerFromWebsite(String name, String mobile)
    {
        String msg = "Hi " + name + ", Welcome to Trujobs.in! "
                + "You are successfully registered as a partner. Login now to add your candidates and help them find jobs!";
        sendSms(mobile, msg);
    }

    public static void sendDuplicateLeadSmsToDevTeam(String leadMobile)
    {
        // Idea is to keep getting irritated by receiving msg until issue is resolved :D

        String msg = "Hi DevTeam, Duplicate Lead found with phone number " + leadMobile + "! "
                + "Please remove the Duplicate Entry";

        sendSms(devTeamMobile.get("Sandy"), msg);
        sendSms(devTeamMobile.get("Adarsh"), msg);
        sendSms(devTeamMobile.get("Archana"), msg);
    }

    public static void sendDuplicateCandidateSmsToDevTeam(String mobile)
    {
        // Idea is to keep getting irritated by receiving msg until issue is resolved :D

        String msg = "Hi DevTeam, Duplicate Candidate found with phone number " + mobile + "! "
                + "Please remove the Duplicate Entry";

        sendSms(devTeamMobile.get("Sandy"), msg);
        sendSms(devTeamMobile.get("Adarsh"), msg);
        sendSms(devTeamMobile.get("Archana"), msg);
    }

    public static void sendDuplicatePartnerSmsToDevTeam(String mobile)
    {
        // Idea is to keep getting irritated by receiving msg until issue is resolved :D

        String msg = "Hi DevTeam, Duplicate partner found with phone number " + mobile + "! "
                + "Please remove the Duplicate Entry";

        sendSms(devTeamMobile.get("Sandy"), msg);
        sendSms(devTeamMobile.get("Adarsh"), msg);
        sendSms(devTeamMobile.get("Archana"), msg);
    }

    public static void sendLocalityNotResolvedSmsToDevTeam(String unResolvedLocality, String city, String state)
    {
        // Idea is to tweak AddressResolver based on unresolved lat/lng (s)  :D

        String msg = "Bonjour DevTeam !! AddressResolver was not able to resolve PredictedLocality: "+unResolvedLocality+" to a Proper Locality Object! "
                + "Max Resolved Info:- City: "+city+" State:"+state;

        sendSms(devTeamMobile.get("Sandy"), msg);
        sendSms(devTeamMobile.get("Adarsh"), msg);
        sendSms(devTeamMobile.get("Archana"), msg);
    }
    public static void sendDuplicateLeadOrCandidateDeleteActionSmsToDevTeam(String mobile)
    {
        // Idea is to keep getting irritated by receiving msg until issue is resolved :D

        String msg = "Hi DevTeam, This is to inform you that a Duplicate Candidate delete action has been executed for mobile number " + mobile + "! ";

        sendSms(devTeamMobile.get("Sandy"), msg);
        sendSms(devTeamMobile.get("Adarsh"), msg);
        sendSms(devTeamMobile.get("Archana"), msg);
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
}
