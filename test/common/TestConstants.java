package common;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by zero on 7/6/16.
 */
public class TestConstants {
    public static final int TEST_SERVER_PORT = 9001;
    public static final String BASE_URL = "http://localhost:" + TEST_SERVER_PORT;

    // defaults for creating candidate object
    public static final Long    testCandidateId = (long) 100002;
    public static final String  testCandidateUUId = "4079cb54-34b1-4b68-a479-ede86c6f9c58";
    public static final String  testCandidateName= "TEST CANDIDATE";
    public static final String  testCandidateLastName = "LAST NAME";
    public static final String  testCandidateMobile = "+919019672209";
    public static final String  testCandidatePhoneType = "TESTPhoneType";
    public static final String  testCandidateEmail = "test@localhost.com";
    public static final Integer testCandidateGender = 1;
    public static final Integer testCandidateMaritalStatus = 1;
    public static final Integer testCandidateIsEmployed = 1;
    public static final Integer testCandidateTotalExperience = 1;  // data in months
    public static final Integer testCandidateAge = 99;
    public static final Integer testCandidateSalarySlip = 1;
    public static final Integer testCandidateAppointmentLetter = 1;
    public static final List<Integer> testCandidateJobInterest = new ArrayList<Integer>() {{
        add(1);add(2);add(3);
    }};
    public static final List<Integer> testCandidateLocalityPreference = new ArrayList<Integer>() {{
        add(3);add(2);add(1);
    }};
    public static final Integer testLeadSource = 1;
    public static final String  testAdminId = "2209";
    public static final String  testAdminPassword = "TruJobs7";
    public static final String  testCandidateTimeShiftPref = "1, 2";

    public static final Date testCandidateDob = Calendar.getInstance().getTime();
}
