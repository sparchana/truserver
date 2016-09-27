package api;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by adarsh on 20/9/16.
 */
public class InteractionConstants {
    public static final String INTERACTION_RESULT_NEW_LEAD = "New Lead Added";
    public static final String INTERACTION_RESULT_NEW_CANDIDATE = "New Candidate Added";
    public static final String INTERACTION_RESULT_NEW_CANDIDATE_SUPPORT = "New Candidate Added by support";
    public static final String INTERACTION_RESULT_NEW_CANDIDATE_PARTNER = "New Candidate Added by partner";
    public static final String INTERACTION_RESULT_NEW_PARTNER = "New Partner Added";
    public static final String INTERACTION_RESULT_CANDIDATE_INFO_UPDATED_PARTNER = "Candidate Info got updated by Partner";
    public static final String INTERACTION_RESULT_CANDIDATE_INFO_UPDATED_SYSTEM = "Candidate Info got updated by System";
    public static final String INTERACTION_RESULT_CANDIDATE_BASIC_PROFILE_INFO_UPDATED_SELF = "Candidate Self Updated Basic Profile Info";
    public static final String INTERACTION_RESULT_PARTNER_BASIC_PROFILE_INFO_UPDATED_SELF = "Partner Self Updated Basic Profile Info";
    public static final String INTERACTION_RESULT_CANDIDATE_SKILLS_PROFILE_INFO_UPDATED_SELF = "Candidate Self Updated Skill Profile Info";
    public static final String INTERACTION_RESULT_CANDIDATE_EDUCATION_PROFILE_INFO_UPDATED_SELF = "Candidate Self Updated Education Profile Info";
    public static final String INTERACTION_RESULT_CANDIDATE_INFO_UPDATED_SELF = "Candidate Info got updated by Self";
    public static final String INTERACTION_RESULT_PARTNER_INFO_UPDATED_SELF = "Partner Info got updated by Self";
    public static final String INTERACTION_RESULT_SYSTEM_UPDATED_LEADTYPE = "System Updated LeadType to ";
    public static final String INTERACTION_RESULT_EXISTING_CANDIDATE_SIGNUP = "Existing Candidate Tried to Signup";
    public static final String INTERACTION_RESULT_EXISTING_CANDIDATE_VERIFICATION = "Existing Candidate trying to complete self signup";
    public static final String INTERACTION_RESULT_EXISTING_PARTNER_VERIFICATION = "Existing partner trying to complete self signup";
    public static final String INTERACTION_RESULT_EXISTING_PARTNER_SIGNUP = "Existing partner Tried to Signup";
    public static final String INTERACTION_RESULT_EXISTING_LEAD = "Existing lead made contact through website";
    public static final String INTERACTION_RESULT_CANDIDATE_FOLLOWED_UP_REQUEST = "Existing candidate requested for a follow up call on ";
    public static final String INTERACTION_RESULT_LEAD_FOLLOWED_UP_REQUEST = "Existing lead requested for a follow up call on ";
    public static final String INTERACTION_RESULT_EXISTING_LEAD_CALLED_BACK = "Existing Lead Called Back";
    public static final String INTERACTION_RESULT_EXISTING_CANDIDATE_CALLED_BACK = "Existing Candidate Called Back";
    public static final String INTERACTION_RESULT_FIRST_INBOUND_CALL = "First Inbound Call";
    public static final String INTERACTION_RESULT_SELF_SIGNEDIN = "Candidate Self Signed In";
    public static final String INTERACTION_RESULT_PARTNER_SIGNEDIN = "Partner Self Signed In";
    public static final String INTERACTION_RESULT_CANDIDATE_DEACTIVATED= "Candidate Deactivated";
    public static final String INTERACTION_RESULT_CANDIDATE_ACTIVATED= "Candidate Activated";
    public static final String INTERACTION_RESULT_CANDIDATE_VERIFICATION_SUCCESS = "Candidate Successfully Verified";
    public static final String INTERACTION_RESULT_CANDIDATE_TRIED_TO_VERIFY = "Partner tried to verify a Candidate";

    public static final String INTERACTION_RESULT_CANDIDATE_SELF_APPLIED_JOB = "Candidate applied to a job: ";
    public static final String INTERACTION_RESULT_CANDIDATE_TRIED_TO_APPLY_JOB = "Candidate tried to apply to a job: ";
    public static final String INTERACTION_RESULT_CANDIDATE_TRIED_TO_RESET_PASSWORD = "Candidate tried to reset password";
    public static final String INTERACTION_RESULT_PARTNER_TRIED_TO_RESET_PASSWORD = "Partner tried to reset password";
    public static final String INTERACTION_RESULT_CANDIDATE_RESET_PASSWORD_SUCCESS = "Candidate reset password done";
    public static final String INTERACTION_RESULT_PARTNER_RESET_PASSWORD_SUCCESS = "Partner reset password done";
    public static final String INTERACTION_RESULT_PARTNER_APPLIED_TO_JOB = "Partner on behalf of the candidate applied to a job: ";
    public static final String INTERACTION_CREATED_SELF = "Self";
    public static final String INTERACTION_CREATED_SYSTEM = "System";
    public static final String INTERACTION_CREATED_SYSTEM_KNOWLARITY = "System - Knowlarity";
    public static final String INTERACTION_CREATED_PARTNER = "Partner";

    public static final String INTERACTION_CREATED_ERROR = "Error";
    public static final String INTERACTION_NOTE_DUMMY_PASSWORD_CREATED = "Candidate got Registered with Mandatory Info and dummy password by system";
    public static final String INTERACTION_NOTE_SELF_PASSWORD_CHANGED = "Candidate Self Updated Password";
    public static final String INTERACTION_NOTE_PARTNER_PASSWORD_CHANGED = "Partner Self Updated Password";
    public static final String INTERACTION_NOTE_CREATED_BY_ERROR = "Session Username is null";
    public static final String INTERACTION_NOTE_BLANK = "";

    public static final Map<Integer, String> INTERACTION_TYPE_MAP = new HashMap<>();

    public static final int INTERACTION_TYPE_FOLLOWUP_CALL = 1;
    public static final int INTERACTION_TYPE_APPLIED_JOB = 2;
    public static final int INTERACTION_TYPE_TRIED_JOB_APPLY = 3;
    public static final int INTERACTION_TYPE_CANDIDATE_TRIED_PASSWORD_RESET = 4;
    public static final int INTERACTION_TYPE_CANDIDATE_PASSWORD_RESET_SUCCESS = 5;
    public static final int INTERACTION_TYPE_CANDIDATE_ALERT = 6;
    public static final int INTERACTION_TYPE_SEARCH = 7;
    public static final int INTERACTION_TYPE_JOP_POST_VIEW = 8;
    public static final int INTERACTION_TYPE_CANDIDATE_LOG_IN = 9;
    public static final int INTERACTION_TYPE_CANDIDATE_SIGN_UP = 10;
    public static final int INTERACTION_TYPE_CANDIDATE_PROFILE_UPDATE = 11;
    public static final int INTERACTION_TYPE_CANDIDATE_PROFILE_CREATED = 12;
    public static final int INTERACTION_TYPE_CANDIDATE_NEW_LEAD = 13;
    public static final int INTERACTION_TYPE_CANDIDATE_VERIFIED = 14;
    public static final int INTERACTION_TYPE_CANDIDATE_TRIED_TO_VERIFY = 15;
    public static final int INTERACTION_TYPE_CANDIDATE_PASSWORD_ADDED = 16;
    public static final int INTERACTION_TYPE_CANDIDATE_ACTIVATED = 17;
    public static final int INTERACTION_TYPE_CANDIDATE_DEACTIVATED = 18;
    public static final int INTERACTION_TYPE_LEAD_STATUS_UPDATE = 19;

    //partner interaction type
    public static final int INTERACTION_TYPE_PARTNER_TRIED_PASSWORD_RESET = 20;
    public static final int INTERACTION_TYPE_PARTNER_PASSWORD_RESET_SUCCESS = 21;
    public static final int INTERACTION_TYPE_PARTNER_LOG_IN = 22;
    public static final int INTERACTION_TYPE_PARTNER_SIGN_UP = 23;
    public static final int INTERACTION_TYPE_PARTNER_PROFILE_UPDATE = 24;
    public static final int INTERACTION_TYPE_PARTNER_NEW_LEAD = 25;
    public static final int INTERACTION_TYPE_PARTNER_PASSWORD_ADDED = 26;

    // existing lead/candidate
    public static final int INTERACTION_TYPE_EXISTING_CANDIDATE_CONTACT = 27;
    public static final int INTERACTION_TYPE_EXISTING_LEAD_CONTACT = 28;
    public static final int INTERACTION_TYPE_EXISTING_CANDIDATE_TRIED_SIGNUP = 29;
    public static final int INTERACTION_TYPE_EXISTING_CANDIDATE_TRIED_SIGNUP_AND_SIGNUP_NOT_ALLOWED = 30;
    public static final int INTERACTION_TYPE_EXISTING_PARTNER_TRIED_SIGNUP = 31;
    public static final int INTERACTION_TYPE_EXISTING_PARTNER_TRIED_SIGNUP_AND_SIGNUP_NOT_ALLOWED = 32;

    public static final Map<Integer, String> INTERACTION_CHANNEL = new HashMap<>();

    public static final int INTERACTION_CHANNEL_CANDIDATE_WEBSITE = 1;
    public static final int INTERACTION_CHANNEL_CANDIDATE_ANDROID = 2;
    public static final int INTERACTION_CHANNEL_PARTNER_WEBSITE = 3;
    public static final int INTERACTION_CHANNEL_SUPPORT_WEBSITE = 4;
    public static final int INTERACTION_CHANNEL_KNOWLARITY = 5;

    static {
        INTERACTION_TYPE_MAP.put(INTERACTION_TYPE_FOLLOWUP_CALL, "Follow Up Call");
        INTERACTION_TYPE_MAP.put(INTERACTION_TYPE_APPLIED_JOB, "Job Application Successful");
        INTERACTION_TYPE_MAP.put(INTERACTION_TYPE_TRIED_JOB_APPLY, "Tried to Apply");
        INTERACTION_TYPE_MAP.put(INTERACTION_TYPE_CANDIDATE_TRIED_PASSWORD_RESET, "Tried to Reset Password");
        INTERACTION_TYPE_MAP.put(INTERACTION_TYPE_CANDIDATE_PASSWORD_RESET_SUCCESS, "Reset Password Successful");
        INTERACTION_TYPE_MAP.put(INTERACTION_TYPE_CANDIDATE_ALERT, "Clicked Candidate Alert");
        INTERACTION_TYPE_MAP.put(INTERACTION_TYPE_SEARCH, "Job Search");
        INTERACTION_TYPE_MAP.put(INTERACTION_TYPE_JOP_POST_VIEW, "Job Post View");
        INTERACTION_TYPE_MAP.put(INTERACTION_TYPE_CANDIDATE_LOG_IN, "Login");
        INTERACTION_TYPE_MAP.put(INTERACTION_TYPE_CANDIDATE_SIGN_UP, "Sign Up");
        INTERACTION_TYPE_MAP.put(INTERACTION_TYPE_CANDIDATE_PROFILE_UPDATE, "Profile Updated");
        INTERACTION_TYPE_MAP.put(INTERACTION_TYPE_CANDIDATE_PROFILE_CREATED, "Profile Created");
        INTERACTION_TYPE_MAP.put(INTERACTION_TYPE_CANDIDATE_NEW_LEAD, "New Lead");
        INTERACTION_TYPE_MAP.put(INTERACTION_TYPE_CANDIDATE_VERIFIED, "Candidate Verified");
        INTERACTION_TYPE_MAP.put(INTERACTION_TYPE_CANDIDATE_TRIED_TO_VERIFY, "Tried to Verify");
        INTERACTION_TYPE_MAP.put(INTERACTION_TYPE_CANDIDATE_PASSWORD_ADDED, "Password Created");
        INTERACTION_TYPE_MAP.put(INTERACTION_TYPE_CANDIDATE_ACTIVATED, "Candidate Activated");
        INTERACTION_TYPE_MAP.put(INTERACTION_TYPE_CANDIDATE_DEACTIVATED, "Candidate Deactivated");
        INTERACTION_TYPE_MAP.put(INTERACTION_TYPE_LEAD_STATUS_UPDATE, "Lead Status Update");

        INTERACTION_TYPE_MAP.put(INTERACTION_TYPE_PARTNER_TRIED_PASSWORD_RESET, "Tried to reset Password");
        INTERACTION_TYPE_MAP.put(INTERACTION_TYPE_PARTNER_PASSWORD_RESET_SUCCESS, "Reset Password Successful");
        INTERACTION_TYPE_MAP.put(INTERACTION_TYPE_PARTNER_LOG_IN, "Log In");
        INTERACTION_TYPE_MAP.put(INTERACTION_TYPE_PARTNER_SIGN_UP, "Sign Up");
        INTERACTION_TYPE_MAP.put(INTERACTION_TYPE_PARTNER_PROFILE_UPDATE, "Profile Updated");
        INTERACTION_TYPE_MAP.put(INTERACTION_TYPE_PARTNER_NEW_LEAD, "New Lead");
        INTERACTION_TYPE_MAP.put(INTERACTION_TYPE_PARTNER_PASSWORD_ADDED, "Password Created");

        INTERACTION_TYPE_MAP.put(INTERACTION_TYPE_EXISTING_CANDIDATE_CONTACT, "Existing Candidate made contact");
        INTERACTION_TYPE_MAP.put(INTERACTION_TYPE_EXISTING_LEAD_CONTACT, "Existing Lead made contact");
        INTERACTION_TYPE_MAP.put(INTERACTION_TYPE_EXISTING_CANDIDATE_TRIED_SIGNUP, "Existing Candidate tried Sign Up");
        INTERACTION_TYPE_MAP.put(INTERACTION_TYPE_EXISTING_CANDIDATE_TRIED_SIGNUP_AND_SIGNUP_NOT_ALLOWED, "Existing candidate tried Sign Up. Already a Candidate");
        INTERACTION_TYPE_MAP.put(INTERACTION_TYPE_EXISTING_PARTNER_TRIED_SIGNUP, "Existing Partner tried Sign Up");
        INTERACTION_TYPE_MAP.put(INTERACTION_TYPE_EXISTING_PARTNER_TRIED_SIGNUP_AND_SIGNUP_NOT_ALLOWED, "Existing Partner tried Sign Up. Already a Partner");

        //channel map
        INTERACTION_CHANNEL.put(INTERACTION_CHANNEL_CANDIDATE_WEBSITE, "Candidate via Website");
        INTERACTION_CHANNEL.put(INTERACTION_CHANNEL_CANDIDATE_ANDROID, "Candidate via Android");
        INTERACTION_CHANNEL.put(INTERACTION_CHANNEL_PARTNER_WEBSITE, "Partner via Website");
        INTERACTION_CHANNEL.put(INTERACTION_CHANNEL_SUPPORT_WEBSITE, "Support via Website");
        INTERACTION_CHANNEL.put(INTERACTION_CHANNEL_KNOWLARITY, "Knowlarity");
    }
}