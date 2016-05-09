package controllers.businessLogic;

import models.util.SmsUtil;

/**
 * Created by batcoder1 on 6/5/16.
 */

public class SendOtpService {
    public static void sendSms(String mobile, String msg){
        SmsUtil.sendSms(mobile, msg);
    }
}
