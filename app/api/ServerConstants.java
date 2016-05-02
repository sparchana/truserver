package api;

/**
 * Created by zero on 27/4/16.
 */
public class ServerConstants {
    public static final String DEV_API_KEY = "123e4567-e89b-12d3-a456-426655440000";
    // public static final int TYPE_UNKNOW = 0;
    public static final int TYPE_LEAD = 1;
    public static final int TYPE_POTENTIAL_CANDIDATE = 2;
    public static final int TYPE_POTENTIAL_RECRUITER = 3;
    public static final int TYPE_CANDIDATE = 4;
    public static final int TYPE_RECRUITER = 5;

    public static final int LEAD_CHANNEL_WEBSITE= 0;
    public static final int LEAD_CHANNEL_KNOWLARITY= 1;

    public static final int LEAD_STATUS_NEW= 0;
    public static final int LEAD_STATUS_TTC= 1; // TTC: Trying To Convert
    public static final int LEAD_STATUS_WON= 2; // Converted:
    public static final int LEAD_STATUS_LOST= 3; // LOST:


    public static final int OBJECT_TYPE_LEAD= 1;
    public static final int OBJECT_TYPE_RECRUTER= 2;
    public static final int OBJECT_TYPE_CANDIDATE= 3;

    public static final int INTERACTION_TYPE_CALL_IN= 1;
    public static final int INTERACTION_TYPE_CALL_OUT= 2;
    public static final int INTERACTION_TYPE_SMS_IN= 3;
    public static final int INTERACTION_TYPE_SMS_OUT= 4;

    public static final int CANDIDATE_STATE_NEW = 1;


    public static final String INTERACTION_CREATED_BY_AGENT = "AGENT 1";

    public static final String STREET_LOGIN_ID = "7895123";
    public static final String STREET_LOGIN_PASS = "9875321";


    public static final int DEV_ACCESS_LEVEL_SUPPORT_ROLE = 1;
    public static final int DEV_ACCESS_LEVEL_UPLOADER = 2;




}
