package models.util;

import api.ServerConstants;
import controllers.Global;
import models.entity.Candidate;
import models.entity.JobPost;
import models.entity.OM.JobPostToLocality;
import models.entity.OM.JobPostWorkflow;
import models.entity.Static.InterviewTimeSlot;
import notificationService.FCMEvent;
import notificationService.NotificationEvent;
import play.Logger;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by dodo on 1/12/16.
 */
public class NotificationUtil {
    public static void addFcmToNotificationQueue(String messageText, String title, String token, int intentType){

        //adding to notificationHandler Queue
        NotificationEvent notificationEvent = new FCMEvent(token, messageText, title, intentType);
        Global.getmNotificationHandler().addToQueue(notificationEvent);
    }

    public static void sendInterviewSelectionNotification(Candidate candidate, JobPostWorkflow jobPostWorkflow){
        String msg = "Hi " + candidate.getCandidateFirstName() + "! You have been selected for the job: " + jobPostWorkflow.getJobPost().getJobPostTitle() + " at " + jobPostWorkflow.getJobPost().getCompany().getCompanyName() +
                ". Congratulations!";
        if(candidate.getCandidateAndroidToken() != null){
            addFcmToNotificationQueue(msg, "Interview Selected", candidate.getCandidateAndroidToken(), ServerConstants.ANDROID_INTENT_ACTIVITY_MY_JOBS_COMPLETED);
        } else{
            Logger.info("Token not available");
        }
    }

    public static void sendInterviewRejectionNotification(Candidate candidate, JobPostWorkflow jobPostWorkflow){
        String msg = "Hi " + candidate.getCandidateFirstName() + "! You were not selected for the job: " + jobPostWorkflow.getJobPost().getJobPostTitle() + " at " + jobPostWorkflow.getJobPost().getCompany().getCompanyName();
        if(candidate.getCandidateAndroidToken() != null){
            addFcmToNotificationQueue(msg, "Interview Rejected", candidate.getCandidateAndroidToken(), ServerConstants.ANDROID_INTENT_ACTIVITY_MY_JOBS_COMPLETED);
        } else{
            Logger.info("Token not available");
        }
    }

    public static void sendInterviewConfirmationNotification(Candidate candidate, JobPostWorkflow jobPostWorkflow){
        Calendar cal = Calendar.getInstance();
        cal.setTime(jobPostWorkflow.getScheduledInterviewDate());
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);

        String interviewDate = day + "-" + (month + 1) + "-" + year;

        String msg = "Your interview for " + jobPostWorkflow.getJobPost().getJobPostTitle() + " at " + jobPostWorkflow.getJobPost().getCompany().getCompanyName() +
        " has been confirmed on " + interviewDate + " between " + jobPostWorkflow.getScheduledInterviewTimeSlot().getInterviewTimeSlotName();

        if(candidate.getCandidateAndroidToken() != null){
            addFcmToNotificationQueue(msg, "Interview Confirmed", candidate.getCandidateAndroidToken(), ServerConstants.ANDROID_INTENT_ACTIVITY_MY_JOBS_CONFIRMED);
        } else{
            Logger.info("Token not available");
        }
    }

    public static void sendInterviewRescheduledNotification(JobPostWorkflow jobPostWorkflow, Candidate candidate, Date interviewDate, InterviewTimeSlot slot){
        Calendar cal = Calendar.getInstance();
        cal.setTime(interviewDate);
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);

        String interviewDateString = day + "-" + (month + 1) + "-" + year;

        String msg = "Your interview for " + jobPostWorkflow.getJobPost().getJobPostTitle() + " at " + jobPostWorkflow.getJobPost().getCompany().getCompanyName() +
                " has been rescheduled on " + interviewDateString + " between " + slot.getInterviewTimeSlotName();

        if(candidate.getCandidateAndroidToken() != null){
            addFcmToNotificationQueue(msg, "Interview Rescheduled", candidate.getCandidateAndroidToken(), ServerConstants.ANDROID_INTENT_ACTIVITY_MY_JOBS_PENDING);
        } else{
            Logger.info("Token not available");
        }
    }

    public static void sendInterviewNotShortlistNotification(Candidate candidate, JobPostWorkflow jobPostWorkflow){
        String msg = "Your application for " + jobPostWorkflow.getJobPost().getJobPostTitle() + " at " + jobPostWorkflow.getJobPost().getCompany().getCompanyName() +
                " was not shortlisted";

        if(candidate.getCandidateAndroidToken() != null){
            addFcmToNotificationQueue(msg, "Application not shortlisted", candidate.getCandidateAndroidToken(), ServerConstants.ANDROID_INTENT_ACTIVITY_MY_JOBS_PENDING);
        } else{
            Logger.info("Token not available");
        }
    }

    public static void sendInterviewShortlistNotification(Candidate candidate, JobPostWorkflow jobPostWorkflow) {
        String msg = "Your job application for " + jobPostWorkflow.getJobPost().getJobPostTitle() + " at " + jobPostWorkflow.getJobPost().getCompany().getCompanyName() +
                " has been shortlisted for the interview. We will get in touch with you shortly to confirm interview date and time!";

        if(candidate.getCandidateAndroidToken() != null){
            addFcmToNotificationQueue(msg, "Application Shortlisted", candidate.getCandidateAndroidToken(), ServerConstants.ANDROID_INTENT_ACTIVITY_MY_JOBS_CONFIRMED);
        } else{
            Logger.info("Token not available");
        }
    }

    public static void sendJobApplicationNotification(Candidate candidate, String jobTitle, String company, String prescreenLocation) {
        String msg = "You have initiated your application for " + jobTitle + " job at " + company + " @" + prescreenLocation + ". Your application is under review " +
                "and you will get a notification once the recruiter shortlists you for interview.";

        if(candidate.getCandidateAndroidToken() != null){
            addFcmToNotificationQueue(msg, "Job Application Initiated!", candidate.getCandidateAndroidToken(), ServerConstants.ANDROID_INTENT_ACTIVITY_MY_JOBS_PENDING);
        } else{
            Logger.info("Token not available");
        }
    }

    public static void sendJobPostNotificationToCandidate(JobPost jobPost, Candidate candidate, Boolean hasCredit) {
        String jobLocalities = "";
        String salary;
        if(jobPost.getJobPostMaxSalary() != null || jobPost.getJobPostMaxSalary() != 0){
            salary = jobPost.getJobPostMinSalary() + " - " + jobPost.getJobPostMaxSalary();
        } else {
            salary = String.valueOf(jobPost.getJobPostMinSalary());
        }

        if(jobPost.getJobPostToLocalityList() != null){
            for(JobPostToLocality jobPostToLocality : jobPost.getJobPostToLocalityList()){
                jobLocalities += jobPostToLocality.getLocality().getLocalityName() + ", ";
            }
        }
        String msg = jobPost.getJobPostTitle() +  " | " + jobPost.getCompany().getCompanyName() + ". Salary: " + salary + " per month, Location: " +
                jobLocalities.substring(0, jobLocalities.length() - 2) + ".";

        String notificationTitle;
        if(hasCredit){
            msg += " Book interview at www.trujobs.in or download app at bit.ly/trujobsapp";
            notificationTitle = "Book interviews Today!";
        } else{
            msg += " Apply now at www.trujobs.in or download app at bit.ly/trujobsapp";
            notificationTitle = "New Job Alert!";
        }

        if(candidate.getCandidateAndroidToken() != null){
            addFcmToNotificationQueue(msg, notificationTitle, candidate.getCandidateAndroidToken(), ServerConstants.ANDROID_INTENT_ACTIVITY_SEARCH_JOBS);
        } else{
            Logger.info("Token not available");
        }
    }

    public static void sendEODNotificationToCandidatePostInterview(JobPost jobPost, Candidate candidate) {
        String msg = "Hi " + candidate.getCandidateFirstName() + ", you had an interview today for " + jobPost.getJobPostTitle() +  " | " + jobPost.getCompany().getCompanyName() + ". " +
                "How would you rate your experience with TruJobs? Please rate us on bit.ly/trujobsapp";

        if(candidate.getCandidateAndroidToken() != null){
            addFcmToNotificationQueue(msg, "We value your feedback!", candidate.getCandidateAndroidToken(), ServerConstants.ANDROID_INTENT_ACTIVITY_SEARCH_JOBS);
        } else{
            Logger.info("Token not available");
        }

    }

    public static void sendWeeklyNotificationToCompleteProfile(Candidate candidate) {
        String msg = "Hi " + candidate.getCandidateFirstName() + ", We noticed that your TruJobs profile is not complete. " +
                "Did you know that a complete profile will get you a job 5 times faster? Complete profile now at www.trujobs.in or" +
                " download app at bit.ly/trujobsapp";

        if(candidate.getCandidateAndroidToken() != null){
            addFcmToNotificationQueue(msg, "Your TruJobs profile is incomplete", candidate.getCandidateAndroidToken(), ServerConstants.ANDROID_INTENT_ACTIVITY_SEARCH_JOBS);
        } else{
            Logger.info("Token not available");
        }
    }

    public static void sendWeeklyMatchingJobsNotification(Candidate candidate, Integer jobCount, String jobRole) {
        String msg = "Hi " + candidate.getCandidateFirstName() + ", You are missing out on new jobs! There are over " + jobCount +
                " new " + jobRole + " jobs on TruJobs platform near your locality! Apply now at www.trujobs.in or download app at bit.ly/trujobsapp.";

        if(candidate.getCandidateAndroidToken() != null){
            addFcmToNotificationQueue(msg, jobCount + " new jobs near you!", candidate.getCandidateAndroidToken(), ServerConstants.ANDROID_INTENT_ACTIVITY_SEARCH_JOBS);
        } else{
            Logger.info("Token not available");
        }
    }


}