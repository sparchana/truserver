package controllers.scheduler.task;

import api.ServerConstants;
import com.avaje.ebean.Ebean;
import com.avaje.ebean.RawSql;
import com.avaje.ebean.RawSqlBuilder;
import controllers.SharedSettings;
import controllers.scheduler.SchedulerManager;
import models.entity.JobPost;
import models.entity.OM.JobPostWorkflow;
import models.entity.Recruiter.RecruiterProfile;
import models.entity.scheduler.Static.SchedulerSubType;
import models.entity.scheduler.Static.SchedulerType;
import models.util.EmailUtil;
import notificationService.EmailEvent;
import play.Logger;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.*;

import static controllers.scheduler.SchedulerConstants.*;
import static play.libs.Json.toJson;

/**
 * Created by zero on 14/12/16.
 */
public class EODRecruiterEmailAlertTask extends TimerTask{
    public static String BASE_URL = "http://trujobs.in/recruiter";

    private Calendar mCalendar = Calendar.getInstance();
    private Date mToday = mCalendar.getTime();

    private SimpleDateFormat mSdf = new SimpleDateFormat(ServerConstants.SDF_FORMAT_YYYYMMDD);

    private List<JobPostWorkflow> mJobPostWorkflowList;

    private void sendTodaysInterview(){
        if( !SchedulerManager.checkIfEODTaskShouldRun(SCHEDULER_TYPE_EMAIL,
                SCHEDULER_SUB_TYPE_RECRUITER_EOD_INTERVIEW_LINEUP)){
            return;
        }

        Timestamp startTime = new Timestamp(System.currentTimeMillis());
        SchedulerType type = SchedulerType.find.where()
                .eq("schedulerTypeId", SCHEDULER_TYPE_EMAIL).findUnique();
        SchedulerSubType subType = SchedulerSubType.find.where()
                .eq("schedulerSubTypeId", SCHEDULER_SUB_TYPE_RECRUITER_EOD_INTERVIEW_LINEUP)
                .findUnique();
        String note = "Interview EMAIL alert for confirm/awaiting interview summary.";


        // club all interviews that happened today into their respective
        // recruiters (map<RecruiterId, List<JobPostWorkflow>)

        Map<Long, List<JobPostWorkflow>> interviewsPerRecruiterMap = getInterviewMap(mToday);
        String subject = "Trujobs.in : Your today's Interview Summary!";

        // from map, create email event and append it to the Queue
        // email desc: html table with columns {candidate name: schedule time slot, feedback}
        for(Map.Entry<Long, List<JobPostWorkflow>> entry: interviewsPerRecruiterMap.entrySet()){
            // send email for every recruiter
            EmailEvent interviewEmailEvent = getHTMLMessage(entry.getValue().get(0).getJobPost().getRecruiterProfile()
                    , entry.getValue(), subject, null);
            SharedSettings.getGlobalSettings().getMyNotificationHandler().addToQueue(interviewEmailEvent);
        }

        Timestamp endTime = new Timestamp(System.currentTimeMillis());

        SchedulerManager.saveNewSchedularStats(startTime, type, subType, note, endTime, true);

    }

    private EmailEvent getHTMLMessage(RecruiterProfile recruiterProfile,
                                      List<JobPostWorkflow> jobPostWorkflows,
                                      String subject, Date date){
        StringBuilder htmlTable = new StringBuilder();
        htmlTable.append(subject +
                "<table>\n"
                + "\t<thead>\n"
                + "\t\t<tr>\n"
                + "\t\t\t<th>Job Post Title</th>\n"
                + "\t\t\t<th>Candidate Name</th>\n"
                + "\t\t\t<th>Interview slot</th>\n"
                + "\t\t\t<th>Coming from ?</th>\n"
                + "\t\t\t<th>Feedback</th>\n"
                + "\t\t</tr>\n"
                + "\t</thead><tbody>");

        String feedbackLink;

        SimpleDateFormat sdf = new SimpleDateFormat(ServerConstants.SDF_FORMAT_YYYYMMDD);


        for(JobPostWorkflow jobPostWorkflow: jobPostWorkflows) {
            if(date == null) {
                feedbackLink = BASE_URL + "/home";
            } else {
                feedbackLink = BASE_URL + "/job/track/"+jobPostWorkflow.getJobPost().getJobPostId()+"?date="+sdf.format(date);
            }
            htmlTable.append(
                    getHTMLTableRow(
                            jobPostWorkflow.getJobPost().getJobPostTitle(),
                            jobPostWorkflow.getCandidate().getCandidateFullName(),
                            jobPostWorkflow.getScheduledInterviewTimeSlot().getInterviewTimeSlotName(),
                            jobPostWorkflow.getCandidate().getLocality() != null ? jobPostWorkflow.getCandidate().getLocality().getLocalityName() : "NA",
                            feedbackLink));
        }
        htmlTable.append("</tbody></table>");

        return new EmailEvent(recruiterProfile.getRecruiterProfileEmail(), EmailUtil.getEmailHTML(recruiterProfile, htmlTable.toString()), subject);
    }

    private String getHTMLTableRow(String jobTitle, String candidateName, String slotTitle,String locationTitle,
                                   String feedbackLink) {

        return "<tr>"+
                "<th>"+ jobTitle +"</th>"+
                "<th>"+ candidateName +"</th>"+
                "<th>"+ slotTitle +"</th>"+
                "<th>"+ locationTitle +"</th>"+
                "<th> <a href="+feedbackLink+" target=\"_blank\">" +"Feedback</th>"+
                "</tr>";
    }

    private void sendTomorrowsLineUp() {
        if( !SchedulerManager.checkIfEODTaskShouldRun(SCHEDULER_TYPE_EMAIL,
                SCHEDULER_SUB_TYPE_RECRUITER_EOD_NEXT_DAY_INTERVIEW_LINEUP)){
            return;
        }

        Timestamp startTime = new Timestamp(System.currentTimeMillis());
        SchedulerType type = SchedulerType.find.where()
                .eq("schedulerTypeId", SCHEDULER_TYPE_EMAIL).findUnique();
        SchedulerSubType subType = SchedulerSubType.find.where()
                .eq("schedulerSubTypeId", SCHEDULER_SUB_TYPE_RECRUITER_EOD_NEXT_DAY_INTERVIEW_LINEUP)
                .findUnique();
        String note = "Interview EMAIL alert for confirm/awaiting interview summary.";



        mCalendar.set(Calendar.DAY_OF_MONTH, mToday.getDate() + 1);
        Date tomorrow = mCalendar.getTime();

        mCalendar = Calendar.getInstance(); // reset calendar back to current state

        String subject = "Trujobs.in : Your tomorrow's Interview Schedules!";
        // Get all interview that are scheduled for tomorrow, reminder
        // prepare map of recruiters and their pending jobPostWorkflow list
        Map<Long, List<JobPostWorkflow>> tomorrowsInterviewsPerRecruiterMap = getInterviewMap(tomorrow);
        // from map, create email event and append it to the Queue
        // email desc: html table with columns {candidate name: tomorrow date, schedule time slot, feedback}
        for(Map.Entry<Long, List<JobPostWorkflow>> entry: tomorrowsInterviewsPerRecruiterMap.entrySet()) {
            // send email for every recruiter
            EmailEvent tomorrowsInterviewEmailEvent = getHTMLMessage(entry.getValue().get(0).getJobPost().getRecruiterProfile()
                    , entry.getValue(), subject, tomorrow);
            SharedSettings.getGlobalSettings().getMyNotificationHandler().addToQueue(tomorrowsInterviewEmailEvent);
        }

        Timestamp endTime = new Timestamp(System.currentTimeMillis());

        SchedulerManager.saveNewSchedularStats(startTime, type, subType, note, endTime, true);
    }

    private void sendEODSummary() {
        if( !SchedulerManager.checkIfEODTaskShouldRun(SCHEDULER_TYPE_EMAIL,
                SCHEDULER_SUB_TYPE_RECRUITER_EOD_SUMMARY)){
            return;
        }

        Timestamp startTime = new Timestamp(System.currentTimeMillis());
        SchedulerType type = SchedulerType.find.where()
                .eq("schedulerTypeId", SCHEDULER_TYPE_EMAIL).findUnique();
        SchedulerSubType subType = SchedulerSubType.find.where()
                .eq("schedulerSubTypeId", SCHEDULER_SUB_TYPE_RECRUITER_EOD_SUMMARY)
                .findUnique();
        String note = "Interview EMAIL alert for confirm/awaiting interview summary.";

        // for all recruiter
        // total confirmed , both auto and byRecruiter, in a day
        // total awaiting to be confirmed..

        // Map< RecruiterId, Map< JobPostId, SummaryCount>>
        Map<RecruiterProfile, Map<JobPost, SummaryCount>> summaryMap = new LinkedHashMap<>();

        // total applicants applied for jobs posted by
        // a recruiter map<recruiterId, map<jobPostId, total_application_received_today>>

        String statusSql;
        statusSql = " (status_id in (" + ServerConstants.JWF_STATUS_PRESCREEN_COMPLETED + ")) ";

        String workFlowQueryBuilder = "select job_post_id, createdby, candidate_id, creation_timestamp, status_id from job_post_workflow i " +
                " where " +
                statusSql +
                " and creation_timestamp = " +
                " (select max(creation_timestamp) from job_post_workflow " +
                " where i.candidate_id = job_post_workflow.candidate_id )" +
                " order by creation_timestamp desc ";

        RawSql rawSql = RawSqlBuilder.parse(workFlowQueryBuilder)
                .columnMapping("creation_timestamp", "creationTimestamp")
                .columnMapping("status_id", "status.statusId")
                .columnMapping("job_post_id", "jobPost.jobPostId")
                .columnMapping("candidate_id", "candidate.candidateId")
                .columnMapping("createdby", "createdBy")
                .create();

        List<JobPostWorkflow> jobPostWorkflowList = Ebean.find(JobPostWorkflow.class)
                .setRawSql(rawSql)
                .findList();
        // 24 hr summary

        Logger.info("list: " + toJson(jobPostWorkflowList.get(0)));

        for(JobPostWorkflow jobPostWorkflow: jobPostWorkflowList) {

            summaryMap.putIfAbsent(jobPostWorkflow.getJobPost().getRecruiterProfile(), new HashMap<>());

            Map<JobPost, SummaryCount> subMap =
                    summaryMap.get(jobPostWorkflow.getJobPost().getRecruiterProfile());

            subMap.putIfAbsent(jobPostWorkflow.getJobPost(), new SummaryCount());
            SummaryCount summaryCount = subMap.get(jobPostWorkflow.getJobPost());
            if(jobPostWorkflow.getStatus().getStatusId() == ServerConstants.JWF_STATUS_INTERVIEW_CONFIRMED){
                summaryCount.setTotalConfirmed(summaryCount.getTotalConfirmed() + 1);
            } else if(jobPostWorkflow.getStatus().getStatusId() == ServerConstants.JWF_STATUS_INTERVIEW_SCHEDULED) {
                summaryCount.setTotalAwaitingConformation(summaryCount.getTotalAwaitingConformation() + 1);
            }
        }

        String subject = "Your Interview Summary !";
        for(Map.Entry<RecruiterProfile, Map<JobPost, SummaryCount>> entry: summaryMap.entrySet()) {
            // send email for every recruiter
            EmailEvent eodSummaryEmailEvent = getHTMLMessageForEODSummary(entry.getKey()
                    , entry.getValue(), subject);
            SharedSettings.getGlobalSettings().getMyNotificationHandler().addToQueue(eodSummaryEmailEvent);
        }

        Timestamp endTime = new Timestamp(System.currentTimeMillis());

        SchedulerManager.saveNewSchedularStats(startTime, type, subType, note, endTime, true);
    }

    private EmailEvent getHTMLMessageForEODSummary(RecruiterProfile recruiterProfile, Map<JobPost, SummaryCount> map, String subject) {
        StringBuilder htmlTable = new StringBuilder();
        htmlTable.append(subject +
                "<table>\n"
                + "\t<thead>\n"
                + "\t\t<tr>\n"
                + "\t\t\t<th>Job Post Title</th>\n"
                + "\t\t\t<th>Total Confirmed</th>\n"
                + "\t\t\t<th>Total Awaiting</th>\n"
                + "\t\t</tr>\n"
                + "\t</thead><tbody>");


        for(Map.Entry<JobPost, SummaryCount> entry: map.entrySet()) {
            // create table entry for every jobpost
            htmlTable.append(
                    getHTMLTableRowForEODSummary(
                            entry.getKey().getJobPostTitle(),
                            entry.getKey().getJobPostId(),
                            entry.getValue()
                            ));

        }
        htmlTable.append("</tbody></table>");

        return new EmailEvent(recruiterProfile.getRecruiterProfileEmail(), EmailUtil.getEmailHTML(recruiterProfile, htmlTable.toString()), subject);
    }

    private String getHTMLTableRowForEODSummary(String jobPostTitle, Long jobPostId, SummaryCount summaryCount) {

        String link = BASE_URL + "/jobApplicants/"+jobPostId;
        return "<tr>"+
                "<th>"+ jobPostTitle +"</th>"+
                "<th><a href="+link+"#confirmed"+" target=\"_blank\">"+ summaryCount.getTotalConfirmed() +"</th>"+
                "<th><a href="+link+"#pendingConfirmation"+" target=\"_blank\">"+ summaryCount.getTotalAwaitingConformation() +"</th>"+
                "</tr>";
    }

    private Map<Long, List<JobPostWorkflow>> getInterviewMap(Date date) {
        // Get all interview that were scheduled for today, inorder to get feedback on each on of it.
        if(mJobPostWorkflowList == null) {

            mJobPostWorkflowList = JobPostWorkflow.find.where()
                    .eq("scheduled_interview_date", mSdf.format(date))
                    .eq("status_id", ServerConstants.JWF_STATUS_INTERVIEW_CONFIRMED)
                    .orderBy().asc("job_post_id")
                    .findList();
        }

        // prepare map of recruiters and their pending jobPostWorkflow list
        Map<Long, List<JobPostWorkflow>> interviewsPerRecruiterMap = new HashMap<>();

        for(JobPostWorkflow jobPostWorkflow: mJobPostWorkflowList) {
            interviewsPerRecruiterMap
                    .putIfAbsent(jobPostWorkflow.getJobPost().getRecruiterProfile().getRecruiterProfileId(), new ArrayList<>());

            List<JobPostWorkflow> jobPostWorkflows =
                    interviewsPerRecruiterMap.get(jobPostWorkflow.getJobPost().getRecruiterProfile().getRecruiterProfileId());

            jobPostWorkflows.add(jobPostWorkflow);
        }
        return interviewsPerRecruiterMap;
    }

    private class SummaryCount {
        private int totalConfirmed;
        private int totalAwaitingConformation;

        int getTotalConfirmed() {
            return totalConfirmed;
        }

        void setTotalConfirmed(int totalConfirmed) {
            this.totalConfirmed = totalConfirmed;
        }

        int getTotalAwaitingConformation() {
            return totalAwaitingConformation;
        }

        void setTotalAwaitingConformation(int totalAwaitingConformation) {
            this.totalAwaitingConformation = totalAwaitingConformation;
        }
    }

    @Override
    public void run() {

        Logger.info("Recruiter Summary Alert started...");
        // This task runs only once, at the end of the day
        //
        // club all interviews that happened today into their respective
        // recruiters (map<RecruiterId, List<JobPostWorkflow>)
        sendTodaysInterview();


        // also prep a map<RecruiterId, List<JobPostWorkflow>) representing
        // interview lined up for next day.
        sendTomorrowsLineUp();

        // also a map<RecruiterId, Class> class = {total confirm / reject/ new application} till now
        // summary mail at the end of day like 5PM
        sendEODSummary();
    }
}
