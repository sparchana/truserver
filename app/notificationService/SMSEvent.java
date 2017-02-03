package notificationService;

import models.entity.Candidate;
import models.entity.Company;
import models.entity.JobPost;
import models.entity.Recruiter.RecruiterProfile;
import models.entity.Static.SmsType;
import play.Logger;
import play.Play;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;

/**
 * Created by dodo on 8/12/16.
 */
public class SMSEvent extends NotificationEvent {

    private boolean isDevMode;

    public SMSEvent(String recipient, String message) {
        this.setMessage(message);
        this.setRecipient(recipient);

        Logger.info("[SMS Event] smsTo: " + recipient + " - Msg: " + message);
    }

    public SMSEvent(String recipient, String message, Company company
            , RecruiterProfile recruiterProfile, JobPost jobPost, Candidate candidate, SmsType smsType)
    {
        this.setMessage(message);
        this.setRecipient(recipient);
        this.setJobPost(jobPost);
        this.setCompany(company);
        this.setCandidate(candidate);
        this.setRecruiterProfile(recruiterProfile);
        this.setSmsType(smsType);
    }

    @Override
    public String send() {
        this.isDevMode = play.api.Play.isDev(play.api.Play.current()) || play.api.Play.isTest(play.api.Play.current());

        String msg = this.getMessage();
        String recipient = this.getRecipient();

        String uname = Play.application().configuration().getString("sms.gateway.user");
        String id = Play.application().configuration().getString("sms.gateway.password");
        String sender = Play.application().configuration().getString("sms.gateway.sender");
        boolean shouldSendSMS = Play.application().configuration().getBoolean("outbound.sms.enabled");

        try {
            msg = URLEncoder.encode(msg, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            Logger.info("Exception while encoding the message" + e);
            return "MSG_ENCODING_FAILED";
        }

        String requestString = "https://www.smsjust.com/sms/user/urlsms.php?username="
                + uname + "&pass=" + id + "&senderid=" + sender + "&dest_mobileno=" +
                recipient + "&message=" + msg + "&response=Y";

        Logger.info("msg: "+ requestString);

        String smsResponse = "";

        if(shouldSendSMS) {
            if(isDevMode()){
                Logger.info("DevMode: No sms sent [" + requestString + "]");
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
        } else {
            Logger.info("Outbound SMS disabled: No sms sent [" + requestString + "]");
            return "Outbound SMS Disabled. No SMS sent";
        }


    }

    public boolean isDevMode() {
        return isDevMode;
    }

    public void setDevMode(boolean devMode) {
        isDevMode = devMode;
    }
}
