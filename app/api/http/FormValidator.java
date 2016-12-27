package api.http;

import models.util.Validator;
import play.Logger;

/**
 * Created by batcoder1 on 11/6/16.
 */
public class FormValidator {
    public static String convertToIndianMobileFormat(String phoneNo){
        if(phoneNo != null){
            if(phoneNo.length() == 10){
                if(Validator.isPhoneNumberValid(phoneNo)){
                    phoneNo = "+91" + phoneNo;
                } else {
                    Logger.info("Invalid mobile number received ");
                    return null;
                }
            } else {
                phoneNo = phoneNo.trim();
                if(phoneNo.length() == 12 && phoneNo.substring(0,0) != "+") {
                    return "+"+phoneNo;
                }
            }
        } else Logger.info("Null Phone no received");
        return phoneNo;
    }
}
