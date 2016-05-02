package models.util;

import play.Logger;
import play.Play;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

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

        try {
            URL url = new URL(requestString);
            HttpURLConnection uc = (HttpURLConnection)url.openConnection();
            System.out.println(uc.getResponseMessage());
            uc.disconnect();

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
/*
        URLConnection connection = null;
        try {
            connection = (URLConnection) new URL(requestString).openConnection();
            InputStream response = connection.getInputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
*/

        return "done";
    }
}
