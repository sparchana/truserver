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
    public static final String INTERACTION_RESULT_CANDIDATE_INFO_UPDATED_SELF = "Candidate Info got updated by Self";
    public static final String INTERACTION_RESULT_SYSTEM_UPDATED_LEADTYPE = "System Updated LeadType to ";
    public static final String INTERACTION_RESULT_SYSTEM_UPDATED_LEADSTATUS = "System Updated LeadStatus to ";
    public static final String INTERACTION_RESULT_EXISTING_CANDIDATE_SIGNUP = "Existing Candidate Tried to Signup";
    public static final String INTERACTION_RESULT_EXISTING_CANDIDATE_VERIFICATION = "Existing Candidate trying to complete self signup";
    public static final String INTERACTION_RESULT_EXISTING_LEAD = "Existing lead made contact through website";

    public static final String INTERACTION_CREATED_SELF = "Self";
    public static final String INTERACTION_CREATED_SYSTEM = "System";
    public static final String INTERACTION_CREATED_SYSTEM_KNOWLARITY = "System - Knowlarity";
    public static final String INTERACTION_CREATED_ERROR = "Error";

    public static final String INTERACTION_NOTE_DUMMY_PASSWORD_CREATED = "Candidate got Registered with Mandatory Info and dummy password by system";
    public static final String INTERACTION_NOTE_SELF_PASSWORD_CHANGED = "Candidate Self Updated Password";
    public static final String INTERACTION_NOTE_CALL_OUTBOUNDS = "Out Bound Call";
    public static final String INTERACTION_NOTE_LEAD_TYPE_CHANGED = "Lead Type Changed";
    public static final String INTERACTION_NOTE_LEAD_STATUS_CHANGED = "Lead Status Changed";
    public static final String INTERACTION_NOTE_SELF_SIGNEDUP = "Candidate Self Signed Up";
    public static final String INTERACTION_NOTE_CREATED_BY_ERROR = "Session Username is null";
    public static final String INTERACTION_NOTE_SELF_PROFILE_CREATION = "Candidate self updated profile details";
    public static final String INTERACTION_NOTE_EXISTING_LEAD_CALLED_BACK = "Existing Lead Called Back";

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

    public static final int INTERACTION_TYPE_CALL_IN= 1;
    public static final int INTERACTION_TYPE_CALL_OUT= 2;
    public static final int INTERACTION_TYPE_SMS_IN= 3;
    public static final int INTERACTION_TYPE_SMS_OUT= 4;
    public static final int INTERACTION_TYPE_WEBSITE= 5;

    public static final int LEAD_SOURCE_UNKNOWN = 1; // 1 is unknown


    public static final String LEAD_INTEREST_UNKNOWN = "Unknown";

    public static final int CANDIDATE_STATE_NEW = 1;

    public static final String INTERACTION_CREATED_BY_AGENT = "AGENT 1";

    public static final String STREET_LOGIN_ID = "7895123";
    public static final String STREET_LOGIN_PASS = "9875321";

    public static final int DEV_ACCESS_LEVEL_SUPPORT_ROLE = 1;
    public static final int DEV_ACCESS_LEVEL_UPLOADER = 2;

    public static final String SDF_FORMAT = "yyyy-MM-dd hh:mm:ss a";
    public static final String SDF_FORMAT_ENTRY = "yyyy-MM-dd hh:mm:ss";



}
