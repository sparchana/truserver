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
}
