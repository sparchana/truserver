package models.util;

import api.ServerConstants;
import com.google.android.gcm.server.Message;
import com.google.android.gcm.server.Sender;
import models.entity.Candidate;
import models.entity.JobPost;
import models.entity.OM.JobPostWorkflow;
import models.entity.Static.InterviewTimeSlot;
import play.Logger;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;

import static api.InteractionConstants.INTERACTION_CHANNEL_CANDIDATE_WEBSITE;

/**
 * Created by dodo on 1/12/16.
 */
public class NotificationUtil {
    public static void sendNotification(String messageText, String title, String token, int intentType){
        final Sender sender = new Sender("AAAAYK9P22w:APA91bHF7nJZ7BPFYTAnNEYtnnjqRxJA11vzli3cVdmLwu5OeHadupdrX5zyDT4W1hFT-DtQRCemQfSR9lVmfcEfPk3uUGVyEAvxaIew1cBqtF1SANUFzjWp9j8aAyLJ0B7N3nZVr3rYkiLifQulkClwhwUi3cHJcQ");
        com.google.android.gcm.server.Result result = null;

        final Message message = new Message.Builder().timeToLive(30)
                .delayWhileIdle(true)
                .addData("title", title)
                .addData("message", messageText)
                .addData("type", String.valueOf(intentType))
                .build();

        try {
            result = sender.send(message, token, 1);
        } catch (final IOException e) {
            e.printStackTrace();
        }
    }

    public static void sendInterviewSelectionNotification(Candidate candidate, JobPostWorkflow jobPostWorkflow){
        String msg = "Hi " + candidate.getCandidateFirstName() + "! You have been selected got the job: " + jobPostWorkflow.getJobPost().getJobPostTitle() + " at " + jobPostWorkflow.getJobPost().getCompany().getCompanyName() +
                ". Congratulations!";
        if(candidate.getCandidateAndroidToken() != null){
            sendNotification(msg, "Interview Selected", candidate.getCandidateAndroidToken(), ServerConstants.ANDROID_INTENT_ACTIVITY_MY_JOBS_COMPLETED);
        } else{
            Logger.info("Token not available");
        }
    }

    public static void sendInterviewRejectionNotification(Candidate candidate, JobPostWorkflow jobPostWorkflow){
        String msg = "Hi " + candidate.getCandidateFirstName() + "! You were not selected for the job: " + jobPostWorkflow.getJobPost().getJobPostTitle() + " at " + jobPostWorkflow.getJobPost().getCompany().getCompanyName();
        if(candidate.getCandidateAndroidToken() != null){
            sendNotification(msg, "Interview Rejected", candidate.getCandidateAndroidToken(), ServerConstants.ANDROID_INTENT_ACTIVITY_MY_JOBS_COMPLETED);
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
            sendNotification(msg, "Interview Confirmed", candidate.getCandidateAndroidToken(), ServerConstants.ANDROID_INTENT_ACTIVITY_MY_JOBS_CONFIRMED);
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
            sendNotification(msg, "Interview Confirmed", candidate.getCandidateAndroidToken(), ServerConstants.ANDROID_INTENT_ACTIVITY_MY_JOBS_PENDING);
        } else{
            Logger.info("Token not available");
        }
    }

    public static void sendInterviewNotShortlistNotification(Candidate candidate, JobPostWorkflow jobPostWorkflow){
        String msg = "Your application for " + jobPostWorkflow.getJobPost().getJobPostTitle() + " at " + jobPostWorkflow.getJobPost().getCompany().getCompanyName() +
                " was not shortlisted";

        if(candidate.getCandidateAndroidToken() != null){
            sendNotification(msg, "Application not shortlisted", candidate.getCandidateAndroidToken(), ServerConstants.ANDROID_INTENT_ACTIVITY_MY_JOBS_PENDING);
        } else{
            Logger.info("Token not available");
        }
    }

    public static void sendInterviewShortlistNotification(Candidate candidate, JobPostWorkflow jobPostWorkflow) {
        String msg = "Your job application for " + jobPostWorkflow.getJobPost().getJobPostTitle() + " at " + jobPostWorkflow.getJobPost().getCompany().getCompanyName() +
                " has been shortlisted for the interview. We will get in touch with you shortly to confirm interview date and time!";

        if(candidate.getCandidateAndroidToken() != null){
            sendNotification(msg, "Application Shortlisted", candidate.getCandidateAndroidToken(), ServerConstants.ANDROID_INTENT_ACTIVITY_MY_JOBS_CONFIRMED);
        } else{
            Logger.info("Token not available");
        }
    }

    public static void sendJobApplicationNotification(Candidate candidate, String jobTitle, String company, String prescreenLocation) {
        String msg = "You have applied to " + jobTitle + " job at " + company + " @" + prescreenLocation + ". Your application is under review " +
                "and you will get a notification once the recruiter shortlists you for interview.";

        if(candidate.getCandidateAndroidToken() != null){
            sendNotification(msg, "Job Application Successful!", candidate.getCandidateAndroidToken(), ServerConstants.ANDROID_INTENT_ACTIVITY_MY_JOBS_PENDING);
        } else{
            Logger.info("Token not available");
        }
    }
}