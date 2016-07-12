package api;

/**
 * Created by zero on 27/4/16.
 */
public class ServerConstants {
    // public static final String DEV_API_KEY = "123e4567-e89b-12d3-a456-426655440000";
    // public static final int TYPE_UNKNOW = 0;
    public static final String INTERACTION_RESULT_NEW_LEAD = "New Lead Added";
    public static final String INTERACTION_RESULT_NEW_CANDIDATE = "New Candidate Added";
    public static final String INTERACTION_RESULT_NEW_CANDIDATE_SUPPORT = "New Candidate Added by support";
    public static final String INTERACTION_RESULT_CANDIDATE_UPDATED_LOCALITY_JOBS = "Candidate updated jobPref and locality pref";
    public static final String INTERACTION_RESULT_CANDIDATE_INFO_UPDATED_SYSTEM = "Candidate Info got updated by System";
    public static final String INTERACTION_RESULT_CANDIDATE_BASIC_PROFILE_INFO_UPDATED_SYSTEM = "Candidate Basic Profile Info got updated by System";
    public static final String INTERACTION_RESULT_CANDIDATE_BASIC_PROFILE_INFO_UPDATED_SELF = "Candidate Self Updated Basic Profile Info";
    public static final String INTERACTION_RESULT_CANDIDATE_SKILLS_PROFILE_INFO_UPDATED_SYSTEM = "Candidate Skill Profile Info got updated by System";
    public static final String INTERACTION_RESULT_CANDIDATE_SKILLS_PROFILE_INFO_UPDATED_SELF = "Candidate Self Updated Skill Profile Info";
    public static final String INTERACTION_RESULT_CANDIDATE_EDUCATION_PROFILE_INFO_UPDATED_SYSTEM = "Candidate Education Profile Info got updated by System";
    public static final String INTERACTION_RESULT_CANDIDATE_EDUCATION_PROFILE_INFO_UPDATED_SELF = "Candidate Self Updated Education Profile Info";
    public static final String INTERACTION_RESULT_CANDIDATE_INFO_UPDATED_SELF = "Candidate Info got updated by Self";
    public static final String INTERACTION_RESULT_SYSTEM_UPDATED_LEADTYPE = "System Updated LeadType to ";
    public static final String INTERACTION_RESULT_SYSTEM_UPDATED_LEADSTATUS = "System Updated LeadStatus to ";
    public static final String INTERACTION_RESULT_EXISTING_CANDIDATE_SIGNUP = "Existing Candidate Tried to Signup";
    public static final String INTERACTION_RESULT_EXISTING_CANDIDATE_VERIFICATION = "Existing Candidate trying to complete self signup";
    public static final String INTERACTION_RESULT_EXISTING_LEAD = "Existing lead made contact through website";
    public static final String INTERACTION_RESULT_CANDIDATE_FOLLOWED_UP_REQUEST = "Existing candidate requested for a follow up call on ";
    public static final String INTERACTION_RESULT_LEAD_FOLLOWED_UP_REQUEST = "Existing lead requested for a follow up call on ";
    public static final String INTERACTION_RESULT_FOLLOWUP_DEACTIVATED = "Follow Up deactivated";
    public static final String INTERACTION_RESULT_EXISTING_LEAD_CALLED_BACK = "Existing Lead Called Back";
    public static final String INTERACTION_RESULT_EXISTING_CANDIDATE_CALLED_BACK = "Existing Candidate Called Back";
    public static final String INTERACTION_RESULT_FIRST_INBOUND_CALL = "First Inbound Call";
    public static final String INTERACTION_RESULT_SELF_SIGNEDIN = "Candidate Self Signed In";

    public static final String INTERACTION_RESULT_CANDIDATE_SELF_APPLIED_JOB = "Candidate applied to a job :";
    public static final String INTERACTION_CREATED_SELF = "Self";
    public static final String INTERACTION_CREATED_SYSTEM = "System";
    public static final String INTERACTION_CREATED_SYSTEM_KNOWLARITY = "System - Knowlarity";

    public static final String INTERACTION_CREATED_ERROR = "Error";
    public static final String INTERACTION_NOTE_DUMMY_PASSWORD_CREATED = "Candidate got Registered with Mandatory Info and dummy password by system";
    public static final String INTERACTION_NOTE_SELF_PASSWORD_CHANGED = "Candidate Self Updated Password";
    public static final String INTERACTION_NOTE_CALL_OUTBOUNDS = "Out Bound Call";
    public static final String INTERACTION_NOTE_FOLLOW_UP_CALL_REQUESTED = "Callee requested for a follow up call at ";
    public static final String INTERACTION_NOTE_LEAD_TYPE_CHANGED = "Lead Type Changed";
    public static final String INTERACTION_NOTE_LEAD_STATUS_CHANGED = "Lead Status Changed";
    public static final String INTERACTION_NOTE_SELF_SIGNEDUP = "Candidate Self Signed Up";
    public static final String INTERACTION_NOTE_CREATED_BY_ERROR = "Session Username is null";
    public static final String INTERACTION_NOTE_SELF_PROFILE_CREATION = "Candidate self updated profile details";
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

    public static final boolean FOLLOW_UP_DEACTIVATE = false;
    public static final boolean FOLLOW_UP_ACTIVATE = true;

    public static final int LEAD_SOURCE_UNKNOWN = 1; // 1 is unknown


    public static final String LEAD_INTEREST_UNKNOWN = "Unknown";

    public static final int CANDIDATE_STATE_NEW = 1;

    public static final String INTERACTION_CREATED_BY_AGENT = "AGENT 1";

    public static final String STREET_LOGIN_ID = "7895123";
    public static final String STREET_LOGIN_PASS = "9875321";

    public static final int DEV_ACCESS_LEVEL_SUPPORT_ROLE = 1;
    public static final int DEV_ACCESS_LEVEL_ADMIN = 2;

    public static final String SDF_FORMAT = "yyyy-MM-dd hh:mm:ss a";
    public static final String SDF_FORMAT_FOLLOWUP = "d MMM hh:mm a";
    public static final String SDF_FORMAT_ENTRY = "yyyy-MM-dd hh:mm:ss";

    public static final String PROD_GOOGLE_FORM_FOR_JOB_APPLICATION = "https://docs.google.com/forms/d/1NIGQC5jmSDuQaGUF0Jw1UG-Dz_3huFtZf9Bo7ncPl4g/formResponse";
    public static final String DEV_GOOGLE_FORM_FOR_JOB_APPLICATION = "https://docs.google.com/forms/d/1KI3ZjRtQfduX_MgsZqUr_NqHowf1JLaHFZB4XlO5Kfg/formResponse";

    public static final String PROD_GOOGLE_FORM_FOR_JOB_POSTS = "https://docs.google.com/forms/d/1QVHzqnts0IkD3Wk8in4urqb70BseI9YWZm9B_MgGXUE/formResponse";
    public static final String DEV_GOOGLE_FORM_FOR_JOB_POSTS = "https://docs.google.com/forms/d/e/1FAIpQLSc-Fr7bO7M5HCjNYyC-dnIyzTMiXiywTEaD9twKkCQDeB7Qtg/formResponse";




}
