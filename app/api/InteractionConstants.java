package api;

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
    public static final String INTERACTION_CREATED_SELF = "Self";
    public static final String INTERACTION_CREATED_SELF_ANDROID = "Self";
    public static final String INTERACTION_CREATED_SYSTEM = "System";
    public static final String INTERACTION_CREATED_SYSTEM_KNOWLARITY = "System - Knowlarity";
    public static final String INTERACTION_CREATED_PARTNER = "Partner";

    public static final String INTERACTION_CREATED_ERROR = "Error";
    public static final String INTERACTION_NOTE_DUMMY_PASSWORD_CREATED = "Candidate got Registered with Mandatory Info and dummy password by system";
    public static final String INTERACTION_NOTE_SELF_PASSWORD_CHANGED = "Candidate Self Updated Password";
    public static final String INTERACTION_NOTE_PARTNER_PASSWORD_CHANGED = "Partner Self Updated Password";
    public static final String INTERACTION_NOTE_CREATED_BY_ERROR = "Session Username is null";
    public static final String INTERACTION_NOTE_BLANK = "";

    public static final int INTERACTION_TYPE_CALL_IN= 1;
    public static final int INTERACTION_TYPE_CALL_OUT= 2;
    public static final int INTERACTION_TYPE_SMS_IN= 3;
    public static final int INTERACTION_TYPE_SMS_OUT= 4;
    public static final int INTERACTION_TYPE_FOLLOWUP_CALL= 5;
    public static final int INTERACTION_TYPE_APPLIED_JOB= 6;
    public static final int INTERACTION_TYPE_TRIED_JOB_APPLY= 7;
    public static final int INTERACTION_TYPE_TRIED_PASSWORD_RESET= 8;
    public static final int INTERACTION_TYPE_PASSWORD_RESET_SUCCESS= 9;
    public static final int INTERACTION_TYPE_CANDIDATE_ALERT = 10;
    public static final int INTERACTION_TYPE_ANDROID_SEARCH = 11;
    public static final int INTERACTION_TYPE_ANDROID_JOP_POST_VIEW = 12;
    public static final int INTERACTION_TYPE_LOG_IN = 13;
    public static final int INTERACTION_TYPE_SIGN_UP = 14;
    public static final int INTERACTION_TYPE_PROFILE_UPDATE = 15;
    public static final int INTERACTION_TYPE_CANDIDATE_PROFILE_UPDATE_BY_PARTNER = 16;
    public static final int INTERACTION_TYPE_CANDIDATE_PROFILE_CREATED_BY_PARTNER = 17;
    public static final int INTERACTION_TYPE_NEW_LEAD = 18;
    public static final int INTERACTION_TYPE_CANDIDATE_PROFILE_CREATED_BY_CANDIDATE = 19;
    public static final int INTERACTION_TYPE_CANDIDATE_VERIFIED = 20;
    public static final int INTERACTION_TYPE_CANDIDATE_TRIED_TO_VERIFY = 21;

    public static final int INTERACTION_CHANNEL_CANDIDATE_WEBSITE = 1;
    public static final int INTERACTION_CHANNEL_CANDIDATE_ANDROID = 2;
    public static final int INTERACTION_CHANNEL_PARTNER_WEBSITE = 3;
    public static final int INTERACTION_CHANNEL_SUPPORT_WEBSITE = 4;


}
