package controllers.scheduler;

/**
 * Created by zero on 12/12/16.
 */
public class SchedulerConstants {
    public static int SCHEDULER_TYPE_SMS = 1;
    public static int SCHEDULER_TYPE_EMAIL = 2;
    public static int SCHEDULER_TYPE_FCM = 3;
    public static int SCHEDULER_TYPE_SYSTEM_TASK = 4;

    public static int SCHEDULER_SUB_TYPE_SAME_DAY_INTERVIEW = 1;
    public static int SCHEDULER_SUB_TYPE_NEXT_DAY_INTERVIEW = 2;
    public static int SCHEDULER_SUB_TYPE_RECRUITER_EOD_INTERVIEW_LINEUP = 3;
    public static int SCHEDULER_SUB_TYPE_RECRUITER_EOD_NEXT_DAY_INTERVIEW_LINEUP = 4;
    public static int SCHEDULER_SUB_TYPE_RECRUITER_EOD_SUMMARY = 5;
    public static int SCHEDULER_SUB_TYPE_CANDIDATE_SOD_JOB_ALERT = 6;
    public static int SCHEDULER_SUB_TYPE_CANDIDATE_EOD_RATE_US = 7;
    public static int SCHEDULER_SUB_TYPE_CANDIDATE_APP_DOWNLOAD = 8;
    public static int SCHEDULER_SUB_TYPE_CANDIDATE_PROFILE_COMPLETE = 9;
    public static int SCHEDULER_SUB_TYPE_CANDIDATE_NOTIFY_NEARBY_JOBS = 10;
    public static int SCHEDULER_SUB_TYPE_CANDIDATE_EOD_JOB_ALERT = 11;
    public static int SCHEDULER_SUB_TYPE_EOD_CREDIT_DEBIT_TASK = 12;
    public static int SCHEDULER_SUB_TYPE_CANDIDATE_ACTIVATION = 13;


    public static int INTERVIEW_TIME_SLOT_10_AM = 1;
    public static int INTERVIEW_TIME_SLOT_1_PM = 2;
    public static int INTERVIEW_TIME_SLOT_4_PM = 3;

    public static final int WEEKLY_TASK_DEFAULT_PROFILE_SCORE = 80;
    public static final int NEW_JOB_ALERT_LIMIT = 500;
    public static final int JOB_ALERT_DEFAULT_LIMIT = 100;
    public static final int CANDIDATE_ALERT_TASK_WEEKLY_LIMIT = 200;
    public static final int CANDIDATE_JOB_POST_ALERT_MAX_LIMIT = 3;
    public static final int CANDIDATE_ALERT_TASK_LAST_ACTIVE_DEFAULT_DAYS = 30;
    public static final double NEW_JOB_MATCHING_DEFAULT_DISTANCE_RADIUS = 20.00;


}