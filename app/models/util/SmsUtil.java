package models.util;

import play.Logger;
import play.Play;

import java.io.*;
import java.net.*;

/**
 * Created by batcoder1 on 26/4/16.
 */
public class SmsUtil {
    public static String sendSms(String toPhone, String msg) {

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

        try {
            URL url = new URL(requestString);
            BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()));
            smsResponse = br.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return smsResponse;
    }

    public static void sendTryingToCallSms(String mobile) {

        String msg = "Hello! We tried calling you from www.TruJobs.in to help you with job search. "
        + "We will try again in sometime or you can call us on 8880007799";

        sendSms(mobile, msg);

    }

    public static void sendOTPSms(int otp, String mobile) {
        String msg = "Welcome to www.Trujobs.in! Use OTP " + otp + " to register and start your job search";
        sendSms(mobile, msg);
    }

    public static void sendResetPasswordOTPSms(int otp, String mobile) {
        String msg = "Welcome to www.Trujobs.in! Use OTP " + otp + " to reset your password";
        sendSms(mobile, msg);
    }

    public static void sendJobApplicationSms(String candidateName, String jobTitle, String company, String mobile) {
        String msg = "Hi " + candidateName + ", you have successfully applied to " + jobTitle + " job at " + company + ". Call us at +91 8048039089 to know about the status of your job application. All the best! www.trujobs.in";
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
