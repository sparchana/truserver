package api;

import scala.Int;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by zero on 27/4/16.
 */
public class ServerConstants {
    // public static final String DEV_API_KEY = "123e4567-e89b-12d3-a456-426655440000";
    // public static final int TYPE_UNKNOW = 0;

    public static final int NO_OF_JOBS_IN_A_PAGE = 5;

    public static final int TYPE_LEAD = 1;
    public static final int TYPE_POTENTIAL_CANDIDATE = 2;
    public static final int TYPE_POTENTIAL_RECRUITER = 3;
    public static final int TYPE_CANDIDATE = 4;
    public static final int TYPE_RECRUITER = 5;
    public static final int TYPE_PARTNER = 6;

    public static final int LEAD_CHANNEL_WEBSITE= 0;
    public static final int LEAD_CHANNEL_KNOWLARITY= 1;
    public static final int LEAD_CHANNEL_SUPPORT = 2;
    public static final int LEAD_CHANNEL_ANDROID  = 3;
    public static final int LEAD_CHANNEL_UNKNOWN  = 4;
    public static final int LEAD_CHANNEL_PARTNER  = 5;
    public static final int LEAD_CHANNEL_RECRUITER  = 6;

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

    public static final int PARTNER_TYPE_PRIVATE = 7;
    public static final int PARTNER_TYPE_PRIVATE_EMPLOYEE = 8;

    public static final int JOB_POST_TYPE_OPEN = 0;
    public static final int JOB_POST_TYPE_PRIVATE = 1;

    public static final int PARTNER_STATUS_NOT_VERIFIED = 0;
    public static final int PARTNER_STATUS_VERIFIED= 1;

    public static final int RECRUITER_STATUS_NOT_VERIFIED = 0;
    public static final int RECRUITER_STATUS_VERIFIED= 1;

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
    public static final int OBJECT_TYPE_JOB_POST_VIEW= 7;
    public static final int OBJECT_TYPE_PARTNER= 8;
    public static final int OBJECT_TYPE_ASSESSMENT_ATTEMPT= 9;

    public static final int JOB_STATUS_NEW= 1;
    public static final int JOB_STATUS_ACTIVE= 2;
    public static final int JOB_STATUS_DEACTIVATED= 3;
    public static final int JOB_STATUS_CLOSED= 4;
    public static final int JOB_STATUS_PAUSED= 5;

    public static final int OBJECT_TYPE_PRESCREEN_ATTEMPT= 10;
    public static final int OBJECT_TYPE_JOB_POST_WORKFLOW= 11;

    public static final int OBJECT_TYPE_NEW_JOB_POST= 12;
    public static final int OBJECT_TYPE_RECRUITER_LEAD= 13;
    public static final int OBJECT_TYPE_SUPPORT= 0;

    public static final boolean FOLLOW_UP_DEACTIVATE = false;
    public static final boolean FOLLOW_UP_ACTIVATE = true;

    public static final int LEAD_SOURCE_UNKNOWN = 1; // 1 is unknown

    public static final int LEAD_SOURCE_WEBSITE = 2;
    public static final int LEAD_SOURCE_CALL_TO_APPLY_WEBSITE = 26;

    public static final String LEAD_INTEREST_UNKNOWN = "Unknown";

    public static final String LOGO_UPLOAD_SUFFIX = "/";
    public static final String AWS_S3_BUCKET_NAME = "trujobs.in";
    public static final String AWS_S3_COMPANY_LOGO_FOLDER = "companyLogos";
    public static final String AWS_S3_CANDIDATE_RESUME_FOLDER = "candidateResumes";

    public static final int CANDIDATE_STATE_ACTIVE = 1;
    public static final int CANDIDATE_STATE_DEACTIVE = 2;

    public static final int CANDIDATE_IS_PRIVATE = 1;
    public static final int CANDIDATE_IS_NOT_PRIVATE = 0;

    public static final int PARTNER_STATE_ACTIVE = 1;
    public static final int PARTNER_STATE_DEACTIVE = 2;

    public static final int RECRUITER_STATE_ACTIVE = 1;
    public static final int RECRUITER_STATE_DEACTIVE = 2;

    public static final String INTERACTION_CREATED_BY_AGENT = "AGENT 1";

    public static final int DEV_ACCESS_LEVEL_PARTNER_ROLE = 0;
    public static final int DEV_ACCESS_LEVEL_SUPPORT_ROLE = 1;
    public static final int DEV_ACCESS_LEVEL_REC = 2;
    public static final int DEV_ACCESS_LEVEL_ADMIN = 3;
    public static final int DEV_ACCESS_LEVEL_SUPER_ADMIN = 4;

    public static final String SDF_FORMAT = "yyyy-MM-dd hh:mm:ss a";
    public static final String SDF_FORMAT_FOLLOWUP = "d MMM hh:mm a";
    public static final String SDF_FORMAT_ENTRY = "yyyy-MM-dd hh:mm:ss";
    public static final String SDF_FORMAT_HH = "yyyy-MM-dd HH:mm:ss";
    public static final String SDF_FORMAT_YYYYMMDD = "yyyy-MM-dd";
    public static final String SDF_FORMAT_DDMMYYYY = "dd-MM-yyyy";

    public static final String PROD_GOOGLE_FORM_FOR_JOB_APPLICATION = "https://docs.google.com/forms/d/1NIGQC5jmSDuQaGUF0Jw1UG-Dz_3huFtZf9Bo7ncPl4g/formResponse";
    public static final String DEV_GOOGLE_FORM_FOR_JOB_APPLICATION = "https://docs.google.com/forms/d/e/1FAIpQLSdyYKeNROcs8sPAxpQ6PqT7Xd_V8vetjk2HNFYoakPjjX_z5Q/formResponse";
    public static final String PROD_GOOGLE_FORM_FOR_SCRAPPED_JOB_APPLICATION = "https://docs.google.com/a/trujobs.in/forms/d/e/1FAIpQLSdrf0MhV6g9sVBtsCOm5hCGfgFgZ_hdaqAT6EAlf1FnOsslnw/formResponse";

    public static final String PROD_GOOGLE_FORM_FOR_JOB_POSTS = "https://docs.google.com/forms/d/1QVHzqnts0IkD3Wk8in4urqb70BseI9YWZm9B_MgGXUE/formResponse";
    public static final String DEV_GOOGLE_FORM_FOR_JOB_POSTS = "https://docs.google.com/forms/d/e/1FAIpQLSc-Fr7bO7M5HCjNYyC-dnIyzTMiXiywTEaD9twKkCQDeB7Qtg/formResponse";

    public static final String PROD_GOOGLE_FORM_FOR_ASSESSMENT = "https://docs.google.com/a/trujobs.in/forms/d/e/1FAIpQLSeDM-7BUxaK_9ThdN4E5e7zTP-Px3MFudQRPXOZ8K8n1V_Kyg/formResponse";
    public static final String DEV_GOOGLE_FORM_FOR_ASSESSMENT = "https://docs.google.com/a/trujobs.in/forms/d/e/1FAIpQLSewLSho-Sq8MWpN_K96PQJ3GwQ_-ZwPyhI0qrU5H9HADJutPw/formResponse";

    public static final Integer IS_HOT = 1;

    public static final int SHEET_MAIN = 1;
    public static final int SHEET_SCRAPPED = 2;

    public static final int RECRUITER_FIRST_TIME = 1;

    // JobPost Workflow status
    public static final int JWF_STATUS_SELECTED = 1;
    public static final int JWF_STATUS_PRESCREEN_ATTEMPTED = 2;
    public static final int JWF_STATUS_PRESCREEN_FAILED = 3;
    public static final int JWF_STATUS_PRESCREEN_COMPLETED = 4;

    public static final int JWF_STATUS_INTERVIEW_SCHEDULED = 5;
    public static final int JWF_STATUS_INTERVIEW_REJECTED_BY_RECRUITER_SUPPORT = 6;
    public static final int JWF_STATUS_INTERVIEW_REJECTED_BY_CANDIDATE = 7;
    public static final int JWF_STATUS_INTERVIEW_RESCHEDULE = 8;
    public static final int JWF_STATUS_INTERVIEW_CONFIRMED = 9;

    public static final int JWF_STATUS_CANDIDATE_INTERVIEW_STATUS_NOT_GOING = 10;
    public static final int JWF_STATUS_CANDIDATE_INTERVIEW_STATUS_DELAYED = 11;
    public static final int JWF_STATUS_CANDIDATE_INTERVIEW_STATUS_ON_THE_WAY = 12;
    public static final int JWF_STATUS_CANDIDATE_INTERVIEW_STATUS_REACHED = 13;

    public static final int JWF_STATUS_CANDIDATE_FEEDBACK_STATUS_COMPLETE_SELECTED = 14;
    public static final int JWF_STATUS_CANDIDATE_FEEDBACK_STATUS_COMPLETE_REJECTED = 15;
    public static final int JWF_STATUS_CANDIDATE_FEEDBACK_STATUS_NO_SHOW = 16;
    public static final int JWF_STATUS_CANDIDATE_FEEDBACK_STATUS_NOT_QUALIFIED = 17;
    public static final int JWF_STATUS_CANDIDATE_FEEDBACK_STATUS_SELECTED_NEXT_ROUND = 18;

    // Requirements Table
    public static final String PROFILE_REQUIREMENT_TABLE_AGE = "age";
    public static final String PROFILE_REQUIREMENT_TABLE_EXPERIENCE = "experience";
    public static final String PROFILE_REQUIREMENT_TABLE_EDUCATION = "education";
    public static final String PROFILE_REQUIREMENT_TABLE_SALARY = "salary";
    public static final String PROFILE_REQUIREMENT_TABLE_GENDER = "gender";
    public static final String PROFILE_REQUIREMENT_TABLE_LOCATION = "locality";
    public static final String PROFILE_REQUIREMENT_TABLE_WORKTIMINGS = "worktimings";
    public static final int REVIEW_APPLICATION_MANUAL = 0;
    public static final int REVIEW_APPLICATION_AUTO = 1;
    public static Map<String, String> devTeamMobile;

    // SMS report type
    public static final int SMS_TYPE_APPLY_JOB_SMS = 1;
    public static final int SMS_TYPE_APPLY_INTERVIEW_SMS = 2;
    public static final int SMS_TYPE_REFERRAL = 3;


    // company status
    public static final int COMPANY_STATUS_ACTIVE = 2;

    // rec sorting keys
    public static Integer REC_SORT_LASTEST_ACTIVE = 1;
    public static Integer REC_SORT_SALARY_H_TO_L = 2;
    public static Integer REC_SORT_SALARY_L_TO_H = 3;


    /* Various App version */
    public static int CURRENT_APP_VERSION_CODE = 8;
    public static int DEACTIVATION_APP_VERSION_CODE = 8;
    public static int APP_NEW_LOGIN_STATUS_VERSION_CODE = 8;

    static {
        devTeamMobile = new HashMap<String, String>();
        devTeamMobile.put("Archana", "+918197222248");
        devTeamMobile.put("Avishek", "+918886000928");
        devTeamMobile.put("Chillu", "+919035164363");
        devTeamMobile.put("Adarsh", "+918971739586");
        devTeamMobile.put("Sandy", "+919019672209");
    }

    public static Map<String, String> devTeamEmail;
    static {
        devTeamEmail = new HashMap<String, String>();
        devTeamEmail.put("Archana", "archana@trujobs.in");
        devTeamEmail.put("Avishek", "avishek@trujobs.in");
        devTeamEmail.put("recruiter_support", "recruitersupport@trujobs.in");
        devTeamEmail.put("Adarsh", "adarsh.raj@trujobs.in");
        devTeamEmail.put("Sandy", "sandeep.kumar@trujobs.in");
        devTeamEmail.put("Rafik", "rafik.khadar@trujobs.in");
        devTeamEmail.put("Chillu", "sandeep@trujobs.in");
        devTeamEmail.put("techAdmin", "techadmin@trujobs.in");
    }

    public static final Double DEFAULT_MATCHING_ENGINE_RADIUS = 10.0; // In Kilometers
    public static final Double WEB_SEARCH_MATCHING_ENGINE_RADIUS = 25.0; // In Kilometers

    // Job Post DB Constants
    public static final int EXPERIENCE_TYPE_FRESHER_ID = 1;
    public static final int EXPERIENCE_TYPE_ANY_ID = 5;

    // Job Post DB Constants
    public static final int EDUCATION_TYPE_LT_10TH_ID = 1;
    public static final int EDUCATION_TYPE_10TH_PASS_ID = 2;
    public static final int EDUCATION_TYPE_12TH_PASS_ID = 3;
    public static final int EDUCATION_TYPE_UG = 4;
    public static final int EDUCATION_TYPE_PG = 5;
    public static final int EDUCATION_TYPE_ANY = 6;

    // MatchingEngine Sort Type
    public static final int SORT_BY_NEARBY = 1;
    public static final int SORT_BY_DATE_POSTED = 2;
    public static final int SORT_BY_SALARY = 3;
    public static final int SORT_BY_SALARY_MIN_MAX = 4;
    public static final int SORT_DEFAULT = SORT_BY_NEARBY;
    public static final int SORT_BY_RELEVANCE = 5;

    public static final int GENDER_MALE = 0;
    public static final int GENDER_FEMALE = 1;
    public static final int GENDER_ANY= 2;

    /* Constants */
    public static final double EARTH_RADIUS = 6371.0; // kilometers (or 3958.75 in miles)
    /* EXTERNAL API KEYS */
    public static final String GOOGLE_SERVER_API_KEY = "AIzaSyCKHf7GijuzKW84Ggz0fFWWHD0y9_onUhg";

    /* Non Logged In Search Result UUID */
    public static final String TRU_DROID_NOT_LOGGED_UUID = "TRU-DROID-NOT-LOGGEDIN-UUID";
    public static final String TRU_WEB_NOT_LOGGED_UUID = "TRU-WEB-NOT-LOGGEDIN-UUID";
    public static final String SUPPORT_DEFAULT_UUID = "SUPPORT-DEFAULT-UUID";

    public static final String SELF_UNLOCKED_CANDIDATE_CONTACT = "Self unlocked contact";
    public static final String SELF_UNLOCKED_INTEVIEW = "Self unlocked Interview";

    /************************
     *  Scrapped Data Source
     ************************/
    public static int SOURCE_INTERNAL = 0;
    public static int SOURCE_BABAJOBS = 1;

    // is_common status for asset, idPoof

    public static final int IS_NOT_COMMON = 0;
    public static final int IS_COMMON= 1;

    // preScreening category
    public static final int CATEGORY_DOCUMENT = 1;
    public static final int CATEGORY_LANGUAGE = 2;
    public static final int CATEGORY_ASSET = 3;
    public static final int CATEGORY_PROFILE = 4;

    /* ---  Pre Screening Key ---- */
    public static final int PROPERTY_TYPE_DOCUMENT = 0;
    public static final int PROPERTY_TYPE_LANGUAGE = 1;
    public static final int PROPERTY_TYPE_ASSET_OWNED = 2;
    public static final int PROPERTY_TYPE_MAX_AGE = 3;
    public static final int PROPERTY_TYPE_EXPERIENCE = 4;
    public static final int PROPERTY_TYPE_EDUCATION = 5;
    public static final int PROPERTY_TYPE_GENDER = 6;
    public static final int PROPERTY_TYPE_SALARY = 7;
    public static final int PROPERTY_TYPE_LOCALITY = 8;
    public static final int PROPERTY_TYPE_WORK_SHIFT = 9;

    /* ---  Pre Screening Map ---- */
    public static final Map<Integer, String> PROPERTY_TYPE_MAP = new HashMap<>();
    static {
        PROPERTY_TYPE_MAP.put(PROPERTY_TYPE_DOCUMENT, "Document");
        PROPERTY_TYPE_MAP.put(PROPERTY_TYPE_LANGUAGE, "Language");
        PROPERTY_TYPE_MAP.put(PROPERTY_TYPE_ASSET_OWNED, "Asset Owned");
        PROPERTY_TYPE_MAP.put(PROPERTY_TYPE_MAX_AGE, "Max Age");
        PROPERTY_TYPE_MAP.put(PROPERTY_TYPE_EXPERIENCE, "Experience");
        PROPERTY_TYPE_MAP.put(PROPERTY_TYPE_EDUCATION, "Education");
        PROPERTY_TYPE_MAP.put(PROPERTY_TYPE_GENDER, "Gender");
        PROPERTY_TYPE_MAP.put(PROPERTY_TYPE_SALARY, "Salary");
        PROPERTY_TYPE_MAP.put(PROPERTY_TYPE_LOCALITY, "Locality");
        PROPERTY_TYPE_MAP.put(PROPERTY_TYPE_WORK_SHIFT, "Work Shift");
    }

//    public enum PropertyType{
//        DOCUMENT,
//        LANGUAGE,
//        ASSET_OWNED,
//        MAX_AGE,
//        EXPERIENCE,
//        EDUCATION,
//        GENDER,
//        SALARY,
//        LOCALITY,
//        WORK_SHIFT;
//
//        public String toString() {
//            String title = name();
//            if(title.contains("_")){
//                String head = title.split("_")[0];
//                String tail = title.split("_")[1];
//                head = head.charAt(0) + head.substring(1).toLowerCase();
//                tail = tail.charAt(0) + tail.substring(1).toLowerCase();
//                return head +" "+ tail;
//            } else {
//                return name().charAt(0) + name().substring(1).toLowerCase();
//            }
//        }
//    }

    // Recruiter constants
    public static int RECRUITER_CATEGORY_CONTACT_UNLOCK = 1;
    public static int RECRUITER_CATEGORY_INTERVIEW_UNLOCK = 2;
    public static int RECRUITER_CATEGORY_CTA_CREDIT = 3;

    public static int RECRUITER_FREE_CONTACT_CREDITS = 3;
    public static int RECRUITER_DEFAULT_INTERVIEW_CREDITS = 99999999;

    // pre screen front end ui ids

    public static final String ACTIVE_WITHIN_24_HOURS = "Within 24 hrs";
    public static final String ACTIVE_LAST_3_DAYS = "Last 3 days";
    public static final String ACTIVE_LAST_7_DAYS = "Last 7 days";
    public static final String ACTIVE_LAST_14_DAYS = "Last 14 days";
    public static final String ACTIVE_LAST_1_MONTH = "Last one month";
    public static final String ACTIVE_LAST_2_MONTHS = "Last two months";
    public static final String ACTIVE_BEYOND_2_MONTHS = "Beyond two months";

    public static final String DEFAULT_COMPANY_LOGO = "https://s3.amazonaws.com/trujobs.in/companyLogos/default_company_logo.png";

    // preScreening category
    public static final int INTERVIEW_STATUS_ACCEPTED = 1;
    public static final int INTERVIEW_STATUS_REJECTED_BY_RECRUITER = 2;
    public static final int INTERVIEW_STATUS_RESCHEDULED = 3;
    public static final int INTERVIEW_STATUS_REJECTED_BY_CANDIDATE = 4;

    // is_interview_required response ||  Apply Job Button status

    public static final int ERROR = 0;
    public static final int INTERVIEW_NOT_REQUIRED = 1; // "OK"
    public static final int INTERVIEW_REQUIRED = 2;     // "INTERVIEW"
    public static final int ALREADY_APPLIED = 3;        // "JOB POST ALREADY APPLIED"
    public static final int APPLY = 4;                  // "JOB POST ALREADY APPLIED"
    public static final int INTERVIEW_CLOSED = 5;       // "INTERVIEW CLOSED FOR THE WEEK"
    public static final int DEACTIVE = 6;               // "CANDIDATE DEACTIVATED"
    public static final int CALL_TO_APPLY = 7;          // "CALL TO APPLY"

    // rescheduled Interview status
    public static final int RESCHEULED_INTERVIEW_STATUS_ACCEPTED = 1;
    public static final int RESCHEULED_INTERVIEW_STATUS_REJECTED = 0;

    // candidate Interview tracking status
    public static final int CANDIDATE_INTERVIEW_STATUS_NOT_GOING = 1;
    public static final int CANDIDATE_INTERVIEW_STATUS_DELAYED = 2;
    public static final int CANDIDATE_INTERVIEW_STATUS_STARTED = 3;
    public static final int CANDIDATE_INTERVIEW_STATUS_REACHED = 4;

    // candidate Interview feedback status
    public static final int CANDIDATE_FEEDBACK_COMPLETE_SELECTED = 1;
    public static final int CANDIDATE_FEEDBACK_COMPLETE_REJECTED = 2;
    public static final int CANDIDATE_FEEDBACK_NO_SHOW = 3;
    public static final int CANDIDATE_FEEDBACK_NOT_QUALIFIED = 4;
    public static final int CANDIDATE_FEEDBACK_SELECTED_NEXT_ROUND = 5;

    //reason type
    public static final int INTERVIEW_REJECT_TYPE_REASON = 1;
    public static final int INTERVIEW_NOT_GOING_TYPE_REASON = 2;
    public static final int INTERVIEW_NOT_SELECED_TYPE_REASON = 3;
    public static final int CANDIDATE_ETA = 4;

    /* android notification intent type */
    public static final int ANDROID_INTENT_ACTIVITY_SEARCH_JOBS = 1;
    public static final int ANDROID_INTENT_ACTIVITY_MY_JOBS_PENDING = 2;
    public static final int ANDROID_INTENT_ACTIVITY_MY_JOBS_CONFIRMED = 3;
    public static final int ANDROID_INTENT_ACTIVITY_MY_JOBS_COMPLETED = 4;
    public static final int ANDROID_INTENT_ACTIVITY_MY_PROFILE = 5;
    public static final int ANDROID_INTENT_ACTIVITY_REFER = 6;
    public static final int ANDROID_INTENT_ACTIVITY_FEEDBACK = 7;
    public static final int ANDROID_INTENT_ACTIVITY_INTERVIEW_TIPS = 8;
    public static final int ANDROID_INTENT_ACTIVITY_JOB_DETAIL = 9;

    public static final String CREATED_BY = "Candidate";

    /* upload resume */
    public  static final int UPLOAD_RESUME_SUCCESS_STATUS = 1;
    public  static final int UPLOAD_RESUME_FAIL_STATUS = 0;
    //reason type
    public static final int FREE_JOB_APPLICATION_DEFAULT_LIMIT_IN_A_WEEK = 7;

    //sms delivery status
    public static final int SMS_STATUS_PENDING = 1;
    public static final int SMS_STATUS_DELIVERED = 2;
    public static final int SMS_STATUS_UNDELIVERED = 3;
    public static final int SMS_STATUS_EXPIRED = 4;
    public static final int SMS_STATUS_DND = 5;
    public static final int SMS_STATUS_FAILED = 6;

    public static final Map<Integer, String> SMS_DELIVERY_RESPONSE = new HashMap<>();
    static {
        SMS_DELIVERY_RESPONSE.put(SMS_STATUS_PENDING, "PENDING");
        SMS_DELIVERY_RESPONSE.put(SMS_STATUS_DELIVERED, "DELIVRD");
        SMS_DELIVERY_RESPONSE.put(SMS_STATUS_UNDELIVERED, "UNDELIV");
        SMS_DELIVERY_RESPONSE.put(SMS_STATUS_EXPIRED, "EXPIRED");
        SMS_DELIVERY_RESPONSE.put(SMS_STATUS_DND, "NCPR");
    }


    // recruiter Access Level
    public static final Integer RECRUITER_ACCESS_LEVEL_OPEN = 0;
    public static final Integer RECRUITER_ACCESS_LEVEL_PRIVATE = 1;
    public static final Integer RECRUITER_ACCESS_LEVEL_PRIVATE_ADMIN = 2;

    // candidate Access Level
    public static final int CANDIDATE_ACCESS_LEVEL_OPEN = 0;
    public static final int CANDIDATE_ACCESS_LEVEL_PRIVATE = 1;

    public static final int PARTNER_TO_COMPANY_VERIFIED = 1;

    // private signup company association status
    public static final int PARTNER_NO_COMPANY_ASSOCIATION = 0;
    public static final int PARTNER_NEED_COMPANY_ASSOCIATION = 1;
    public static final int PARTNER_COMPANY_ASSOCIATION_ALREADY_EXISTS = 2;

    // private partner candidate status check
    public static final int STATUS_NO_CANDIDATE = 0;
    public static final int STATUS_CANDIDATE_EXISTS = 1;
    public static final int STATUS_CANDIDATE_EXISTS_DIFFERENT_COMPANY = 2;
    public static final int STATUS_CANDIDATE_EXISTS_SAME_COMPANY = 3;

    //job application channel
    public static final int APPLICATION_CHANNEL_SELF = 1;
    public static final int APPLICATION_CHANNEL_PARTNER = 2;
    public static final int APPLICATION_CHANNEL_SUPPORT = 3;


    public static String BASE_URL = "https://trujobs.in";

    // recruiter(RMP) view type
    public static final Integer VIEW_TYPE_ALL_JOBS = 1;
    public static final Integer VIEW_TYPE_MY_JOBS = 2;

}
