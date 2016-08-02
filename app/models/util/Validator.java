package models.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by zero on 7/5/16.
 */
public class Validator {
    public static boolean isValidLocalityName(String localityName){
        String expression = "^[\\.a-zA-Z\\s]+";
        return localityName.matches(expression);
    }
    public static boolean isPhoneNumberValid(String phoneNo){
        if (phoneNo.matches("[7-9]{1}[0-9]{9}")) return true;
        return false;
    }

    public static boolean isNameValid(String name){
        if (name.matches("[a-zA-Z][a-zA-Z ]*") && name.length()>2) {
            return true;
        }
        return false;
    }
    public static boolean isEmailVaild(String email) {
        String ePattern = "^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\])|(([a-zA-Z\\-0-9]+\\.)+[a-zA-Z]{2,}))$";
        Pattern p = java.util.regex.Pattern.compile(ePattern);
        Matcher m = p.matcher(email);
        return m.matches();
    }
}
