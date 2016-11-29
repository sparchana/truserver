package models.util;

import java.sql.Date;

/**
 * Created by zero on 29/11/16.
 */
public class InterviewUtil {

    public static String getDayVal(int day) {
        if(day > 6) {
            return null;
        }
        switch (day){
            case 0: return "Sun";
            case 1: return "Mon";
            case 2: return "Tue";
            case 3: return "Wed";
            case 4: return "Thu";
            case 5: return "Fri";
            case 6: return "Sat";
            default: return null;
        }
    }

    public static String getMonthVal(int month) {
        if(month > 12 || month < 1) {
            return null;
        }
        switch (month){
            case 1: return "Jan";
            case 2: return "Feb";
            case 3: return "Mar";
            case 4: return "Apr";
            case 5: return "May";
            case 6: return "Jun";
            case 7: return "Jul";
            case 8: return "Aug";
            case 9: return "Sep";
            case 10: return"Oct" ;
            case 11: return "Nov";
            case 12: return "Dec";
            default: return null;
        }
    }

    public static boolean checkSlotAvailability(Date x, String interviewDays) {
        if(x.getDay() == 1 && interviewDays.charAt(0) == '1') { //monday
            return true;
        } else if(x.getDay() == 2 && interviewDays.charAt(1) == '1') { //tue
            return true;
        } else if(x.getDay() == 3 && interviewDays.charAt(2) == '1') { //wed
            return true;
        } else if(x.getDay() == 4 && interviewDays.charAt(3) == '1') { //thu
            return true;
        } else if(x.getDay() == 5 && interviewDays.charAt(4) == '1') { //fri
            return true;
        } else if(x.getDay() == 6 && interviewDays.charAt(5) == '1') { //sat
            return true;
        } else if(x.getDay() == 0 && interviewDays.charAt(6) == '1') { //sun
            return true;
        } else {
            return  false;
        }
    }
}
