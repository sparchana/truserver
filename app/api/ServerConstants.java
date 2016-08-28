package api;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by zero on 27/4/16.
 */
public class ServerConstants {
    // public static final String DEV_API_KEY = "123e4567-e89b-12d3-a456-426655440000";
    // public static final int TYPE_UNKNOW = 0;
    public static final String INTERACTION_RESULT_NEW_LEAD = "New Lead Added";
    public static final String INTERACTION_RESULT_NEW_CANDIDATE = "New Candidate Added";
    public static final String INTERACTION_RESULT_NEW_CANDIDATE_SUPPORT = "New Candidate Added by support";
    public static final String INTERACTION_RESULT_CANDIDATE_INFO_UPDATED_SYSTEM = "Candidate Info got updated by System";
    public static final String INTERACTION_RESULT_CANDIDATE_BASIC_PROFILE_INFO_UPDATED_SELF = "Candidate Self Updated Basic Profile Info";
    public static final String INTERACTION_RESULT_CANDIDATE_SKILLS_PROFILE_INFO_UPDATED_SELF = "Candidate Self Updated Skill Profile Info";
    public static final String INTERACTION_RESULT_CANDIDATE_EDUCATION_PROFILE_INFO_UPDATED_SELF = "Candidate Self Updated Education Profile Info";
    public static final String INTERACTION_RESULT_CANDIDATE_INFO_UPDATED_SELF = "Candidate Info got updated by Self";
    public static final String INTERACTION_RESULT_SYSTEM_UPDATED_LEADTYPE = "System Updated LeadType to ";
    public static final String INTERACTION_RESULT_EXISTING_CANDIDATE_SIGNUP = "Existing Candidate Tried to Signup";
    public static final String INTERACTION_RESULT_EXISTING_CANDIDATE_VERIFICATION = "Existing Candidate trying to complete self signup";
    public static final String INTERACTION_RESULT_EXISTING_LEAD = "Existing lead made contact through website";
    public static final String INTERACTION_RESULT_CANDIDATE_FOLLOWED_UP_REQUEST = "Existing candidate requested for a follow up call on ";
    public static final String INTERACTION_RESULT_LEAD_FOLLOWED_UP_REQUEST = "Existing lead requested for a follow up call on ";
    public static final String INTERACTION_RESULT_EXISTING_LEAD_CALLED_BACK = "Existing Lead Called Back";
    public static final String INTERACTION_RESULT_EXISTING_CANDIDATE_CALLED_BACK = "Existing Candidate Called Back";
    public static final String INTERACTION_RESULT_FIRST_INBOUND_CALL = "First Inbound Call";
    public static final String INTERACTION_RESULT_SELF_SIGNEDIN = "Candidate Self Signed In";
    public static final String INTERACTION_RESULT_CANDIDATE_DEACTIVATED= "Candidate Deactivated";
    public static final String INTERACTION_RESULT_CANDIDATE_ACTIVATED= "Candidate Activated";

    public static final String INTERACTION_RESULT_CANDIDATE_SELF_APPLIED_JOB = "Candidate applied to a job: ";
    public static final String INTERACTION_RESULT_CANDIDATE_TRIED_TO_APPLY_JOB = "Candidate tried to apply to a job: ";
    public static final String INTERACTION_RESULT_CANDIDATE_TRIED_TO_RESET_PASSWORD = "Candidate tried to reset password";
    public static final String INTERACTION_RESULT_CANDIDATE_RESET_PASSWORD_SUCCESS = "Candidate reset password done";
    public static final String INTERACTION_CREATED_SELF = "Self";
    public static final String INTERACTION_CREATED_SYSTEM = "System";
    public static final String INTERACTION_CREATED_SYSTEM_KNOWLARITY = "System - Knowlarity";

    public static final String INTERACTION_CREATED_ERROR = "Error";
    public static final String INTERACTION_NOTE_DUMMY_PASSWORD_CREATED = "Candidate got Registered with Mandatory Info and dummy password by system";
    public static final String INTERACTION_NOTE_SELF_PASSWORD_CHANGED = "Candidate Self Updated Password";
    public static final String INTERACTION_NOTE_SELF_SIGNEDUP = "Candidate Self Signed Up";
    public static final String INTERACTION_NOTE_CREATED_BY_ERROR = "Session Username is null";
    public static final String INTERACTION_NOTE_BLANK = "";

    public static final int TYPE_LEAD = 1;
    public static final int TYPE_POTENTIAL_CANDIDATE = 2;
    public static final int TYPE_POTENTIAL_RECRUITER = 3;
    public static final int TYPE_CANDIDATE = 4;
    public static final int TYPE_RECRUITER = 5;

    public static final int LEAD_CHANNEL_WEBSITE= 0;
    public static final int LEAD_CHANNEL_KNOWLARITY= 1;
    public static final int LEAD_CHANNEL_SUPPORT = 2;

    public static final int LEAD_STATUS_NEW= 0;
    public static final int LEAD_STATUS_TTC= 1; // TTC: Trying To Convert
    public static final int LEAD_STATUS_WON= 2; // Converted:
    public static final int LEAD_STATUS_LOST= 3; // LOST:

    // NOTE: Changing the below should reflect in change in option values in signup_support.scala.html file as well
    public static final String CALL_STATUS_BUSY = "busy";
    public static final String CALL_STATUS_NR = "not_reachable";
    public static final String CALL_STATUS_NA = "not_answering";
    public static final String CALL_STATUS_SWITCHED_OFF = "switched_off";
    public static final String CALL_STATUS_DND = "dnd";

    public static final int CANDIDATE_STATUS_NOT_VERIFIED = 0;
    public static final int CANDIDATE_STATUS_VERIFIED= 1;

    public static final int CANDIDATE_NOT_ASSESSED = 0;
    public static final int CANDIDATE_ASSESSED= 1;

    public static final int CANDIDATE_MIN_PROFILE_NOT_COMPLETE = 0;
    public static final int CANDIDATE_MIN_PROFILE_COMPLETE = 1;

    public static final int UPDATE_BASIC_PROFILE = 0;
    public static final int UPDATE_SKILLS_PROFILE = 1;
    public static final int UPDATE_EDUCATION_PROFILE = 2;
    public static final int UPDATE_ALL_BY_SUPPORT = 3;

    public static final int OBJECT_TYPE_LEAD= 1;
    public static final int OBJECT_TYPE_CANDIDATE= 4;
    public static final int OBJECT_TYPE_RECRUTER= 5;
    public static final int OBJECT_TYPE_JOB_POST= 6;

    public static final int INTERACTION_TYPE_CALL_IN= 1;
    public static final int INTERACTION_TYPE_CALL_OUT= 2;
    public static final int INTERACTION_TYPE_SMS_IN= 3;
    public static final int INTERACTION_TYPE_SMS_OUT= 4;
    public static final int INTERACTION_TYPE_WEBSITE= 5;
    public static final int INTERACTION_TYPE_FOLLOWUP_CALL= 6;
    public static final int INTERACTION_TYPE_APPLIED_JOB= 7;
    public static final int INTERACTION_TYPE_TRIED_JOB_APPLY= 8;
    public static final int INTERACTION_TYPE_TRIED_PASSWORD_RESET= 9;
    public static final int INTERACTION_TYPE_PASSWORD_RESET_SUCCESS= 10;

    public static final boolean FOLLOW_UP_DEACTIVATE = false;
    public static final boolean FOLLOW_UP_ACTIVATE = true;

    public static final int LEAD_SOURCE_UNKNOWN = 1; // 1 is unknown


    public static final String LEAD_INTEREST_UNKNOWN = "Unknown";

    public static final String LOGO_UPLOAD_SUFFIX = "/";
    public static final String AWS_S3_BUCKET_NAME = "trujobs.in";
    public static final String AWS_S3_COMPANY_LOGO_FOLDER = "companyLogos";

    public static final int CANDIDATE_STATE_ACTIVE = 1;
    public static final int CANDIDATE_STATE_DEACTIVE = 2;

    public static final String INTERACTION_CREATED_BY_AGENT = "AGENT 1";

    public static final String STREET_LOGIN_ID = "7895123";
    public static final String STREET_LOGIN_PASS = "9875321";

    public static final int DEV_ACCESS_LEVEL_PARTNER_ROLE = 0;
    public static final int DEV_ACCESS_LEVEL_SUPPORT_ROLE = 1;
    public static final int DEV_ACCESS_LEVEL_REC = 2;
    public static final int DEV_ACCESS_LEVEL_ADMIN = 3;
    public static final int DEV_ACCESS_LEVEL_SUPER_ADMIN = 4;

    public static final String SDF_FORMAT = "yyyy-MM-dd hh:mm:ss a";
    public static final String SDF_FORMAT_FOLLOWUP = "d MMM hh:mm a";
    public static final String SDF_FORMAT_ENTRY = "yyyy-MM-dd hh:mm:ss";
    public static final String SDF_FORMAT_YYYYMMDD = "yyyy-MM-dd";

    public static final String PROD_GOOGLE_FORM_FOR_JOB_APPLICATION = "https://docs.google.com/forms/d/1NIGQC5jmSDuQaGUF0Jw1UG-Dz_3huFtZf9Bo7ncPl4g/formResponse";
    public static final String DEV_GOOGLE_FORM_FOR_JOB_APPLICATION = "https://docs.google.com/forms/d/e/1FAIpQLSe2owE6RMlW8Ynk8p-dWWBuQl0EclR2Go2ua9HGUIXxwMt0cg/formResponse";

    public static final String PROD_GOOGLE_FORM_FOR_JOB_POSTS = "https://docs.google.com/forms/d/1QVHzqnts0IkD3Wk8in4urqb70BseI9YWZm9B_MgGXUE/formResponse";
    public static final String DEV_GOOGLE_FORM_FOR_JOB_POSTS = "https://docs.google.com/forms/d/e/1FAIpQLSc-Fr7bO7M5HCjNYyC-dnIyzTMiXiywTEaD9twKkCQDeB7Qtg/formResponse";

    public static final Integer IS_HOT = 1;

    public static Map<String, String> devTeamMobile;
    static {
        devTeamMobile = new HashMap<String, String>();
        devTeamMobile.put("Archana", "+918197222248");
        devTeamMobile.put("Avishek", "+918886000928");
        devTeamMobile.put("Chillu", "+919035164363");
        devTeamMobile.put("Adarsh", "+918971739586");
        devTeamMobile.put("Sandy", "+919019672209");
    }

    public static final Double DEFAULT_MATCHING_ENGINE_RADIUS = 10.0; // In Kilometers

    // Job Post DB Constants
    public static final int EXPERIENCE_TYPE_FRESHER_ID = 1;
    public static final int EXPERIENCE_TYPE_ANY_ID = 5;

    // Job Post DB Constants
    public static final int EDUCATION_TYPE_ANY = 0; // not in db
    public static final int EDUCATION_TYPE_LT_10TH_ID = 1;
    public static final int EDUCATION_TYPE_10TH_PASS_ID = 2;
    public static final int EDUCATION_TYPE_12TH_PASS_ID = 3;
    public static final int EDUCATION_TYPE_UG = 4;
    public static final int EDUCATION_TYPE_PG = 5;

    // MatchingEngine Sort Type
    public static final int SORT_BY_NEARBY = 1;
    public static final int SORT_BY_DATE_POSTED = 2;
    public static final int SORT_BY_SALARY = 3;
    public static final int SORT_DEFAULT = SORT_BY_NEARBY;

    public static final int GENDER_MALE = 0;
    public static final int GENDER_FEMALE = 1;
    public static final int GENDER_ANY= 2;

    /* Constants */
    public static final double EARTH_RADIUS = 6371.0; // kilometers (or 3958.75 in miles)
    /* EXTERNAL API KEYS */
    public static final String GOOGLE_SERVER_API_KEY = "AIzaSyCKHf7GijuzKW84Ggz0fFWWHD0y9_onUhg";

}
