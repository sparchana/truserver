package api.http;

/**
 * Created by batcoder1 on 11/6/16.
 */
public class FormValidator {
    public static String convertToIndianMobileFormat(String phoneNo){
        if(phoneNo.length() == 10){
            phoneNo = "+91" + phoneNo;
        }
        return phoneNo;
    }
}
