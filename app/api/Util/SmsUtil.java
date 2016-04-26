package api.Util;

import play.Play;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by batcoder1 on 26/4/16.
 */
public class SmsUtil {
    public static String sendSms(String toPhone, String msg) {

        String uname = Play.application().configuration().getString("sms.gateway.user");
        String id = Play.application().configuration().getString("sms.gateway.password");
        String sender = Play.application().configuration().getString("sms.gateway.sender");

        String requestString = "https://www.smsjust.com/sms/user/urlsms.php?username="
                + uname + "&pass=" + id + "&senderid=" + sender + "&dest_mobileno=" +
                toPhone + "&message=" + msg + "&response=Y";

        URLConnection connection = null;
        try {
            connection = (URLConnection) new URL(requestString).openConnection();
            InputStream response = connection.getInputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return "something";
    }
}
