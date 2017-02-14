package controllers.scheduler.task;

import api.ServerConstants;
import com.avaje.ebean.Ebean;
import com.avaje.ebean.RawSql;
import com.avaje.ebean.RawSqlBuilder;
import controllers.Global;
import controllers.scheduler.SchedulerManager;
import dao.JobPostWorkFlowDAO;
import models.entity.Candidate;
import models.entity.JobPost;
import models.entity.OM.JobPostWorkflow;
import models.entity.Recruiter.RecruiterProfile;
import models.entity.RecruiterCreditHistory;
import models.entity.scheduler.Static.SchedulerSubType;
import models.entity.scheduler.Static.SchedulerType;
import models.util.EmailUtil;
import notificationService.EmailEvent;
import play.Logger;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.*;

import static controllers.scheduler.SchedulerConstants.*;

/**
 * Created by zero on 14/12/16.
 */
public class EODRecruiterEmailAlertTask extends TimerTask{
    private static final String BASE_URL = "http://trujobs.in/recruiter";

    private Calendar mCalendar = Calendar.getInstance();
    private Date mToday = mCalendar.getTime();

    private final SimpleDateFormat mSdf = new SimpleDateFormat(ServerConstants.SDF_FORMAT_YYYYMMDD);

    private void sendTodaysInterview(){
        if( !SchedulerManager.checkIfEODTaskShouldRun(SCHEDULER_TYPE_EMAIL,
                SCHEDULER_SUB_TYPE_RECRUITER_EOD_INTERVIEW_LINEUP)){
            Logger.info("EOD_Today's Interview should not run. i.e this task has already been triggered today");
            return;
        }

        Timestamp startTime = new Timestamp(System.currentTimeMillis());
        SchedulerType type = SchedulerType.find.where()
                .eq("schedulerTypeId", SCHEDULER_TYPE_EMAIL).findUnique();
        SchedulerSubType subType = SchedulerSubType.find.where()
                .eq("schedulerSubTypeId", SCHEDULER_SUB_TYPE_RECRUITER_EOD_INTERVIEW_LINEUP)
                .findUnique();
        String note = "Interview EMAIL alert for today's interview details.";


        // club all interviews that happened today into their respective
        // recruiters (map<RecruiterId, List<JobPostWorkflow>)

        Map<Long, List<JobPostWorkflow>> interviewsPerRecruiterMap = getInterviewMap(mToday);
        String subject = " TruJobs.in : Provide feedback for today's interviews and earn credits!! ";

        // from map, create email event and append it to the Queue
        // email desc: html table with columns {candidate name: schedule time slot, feedback}
        for(Map.Entry<Long, List<JobPostWorkflow>> entry: interviewsPerRecruiterMap.entrySet()){
            // send email for every recruiter
            EmailEvent interviewEmailEvent = getHTMLMessage(entry.getValue().get(0).getJobPost().getRecruiterProfile()
                    , entry.getValue(), subject, null);
            Global.getmNotificationHandler().addToQueue(interviewEmailEvent);
        }

        Timestamp endTime = new Timestamp(System.currentTimeMillis());

        SchedulerManager.saveNewSchedulerStats(startTime, type, subType, note, endTime, true);

    }

    private void sendTomorrowsLineUp() {
        if( !SchedulerManager.checkIfEODTaskShouldRun(SCHEDULER_TYPE_EMAIL,
                SCHEDULER_SUB_TYPE_RECRUITER_EOD_NEXT_DAY_INTERVIEW_LINEUP)){
            Logger.info("EOD Tomorrow's LineUp should not run. i.e this task has already been triggered today");

            return;
        }

        Timestamp startTime = new Timestamp(System.currentTimeMillis());
        SchedulerType type = SchedulerType.find.where()
                .eq("schedulerTypeId", SCHEDULER_TYPE_EMAIL).findUnique();
        SchedulerSubType subType = SchedulerSubType.find.where()
                .eq("schedulerSubTypeId", SCHEDULER_SUB_TYPE_RECRUITER_EOD_NEXT_DAY_INTERVIEW_LINEUP)
                .findUnique();
        String note = "Interview Email alert for tomorrow's interview line up.";

        mCalendar.set(Calendar.DAY_OF_MONTH, mToday.getDate() + 1);
        Date tomorrow = mCalendar.getTime();

        String subject = "TruJobs.in : Tomorrow's line up details inside! ";
        // Get all interview that are scheduled for tomorrow, reminder
        // prepare map of recruiters and their pending jobPostWorkflow list
        Map<Long, List<JobPostWorkflow>> tomorrowsInterviewsPerRecruiterMap = getInterviewMap(tomorrow);
        // from map, create email event and append it to the Queue
        // email desc: html table with columns {candidate name: tomorrow date, schedule time slot, feedback}
        for(Map.Entry<Long, List<JobPostWorkflow>> entry: tomorrowsInterviewsPerRecruiterMap.entrySet()) {
            // send email for every recruiter
            EmailEvent tomorrowsInterviewEmailEvent = getHTMLMessage(entry.getValue().get(0).getJobPost().getRecruiterProfile()
                    , entry.getValue(), subject, tomorrow);
            Global.getmNotificationHandler().addToQueue(tomorrowsInterviewEmailEvent);
        }

        Timestamp endTime = new Timestamp(System.currentTimeMillis());

        SchedulerManager.saveNewSchedulerStats(startTime, type, subType, note, endTime, true);

        mCalendar = Calendar.getInstance(); // reset calendar back to current state
        mToday = mCalendar.getTime(); // reset today back to current state
    }

    private void sendEODSummary() {
        if( !SchedulerManager.checkIfEODTaskShouldRun(SCHEDULER_TYPE_EMAIL,
                SCHEDULER_SUB_TYPE_RECRUITER_EOD_SUMMARY)){
            Logger.info("EOD summary should not run. i.e this task has already been triggered today");

            return;
        }

        Timestamp startTime = new Timestamp(System.currentTimeMillis());
        SchedulerType type = SchedulerType.find.where()
                .eq("schedulerTypeId", SCHEDULER_TYPE_EMAIL).findUnique();
        SchedulerSubType subType = SchedulerSubType.find.where()
                .eq("schedulerSubTypeId", SCHEDULER_SUB_TYPE_RECRUITER_EOD_SUMMARY)
                .findUnique();
        String note = "Interview Email alert for confirm/awaiting interview summary.";

        // for all recruiter
        // total confirmed , both auto and byRecruiter, in a day
        // total awaiting to be confirmed..

        // Map< RecruiterId, Map< JobPostId, SummaryCount>>
        Map<RecruiterProfile, Map<JobPost, SummaryCount>> summaryMap = new LinkedHashMap<>();

        // total applicants applied for jobs posted by
        // a recruiter map<recruiterId, map<jobPostId, total_application_received_today>>
        mCalendar.set(Calendar.DAY_OF_MONTH, mToday.getDate() - 1);
        Date yesterday = mCalendar.getTime();
        mCalendar = Calendar.getInstance(); // reset calendar back to current state
        mToday = mCalendar.getTime();


        String statusSql;
        statusSql = " (status_id in (" + ServerConstants.JWF_STATUS_INTERVIEW_SCHEDULED+ " , "+ServerConstants.JWF_STATUS_INTERVIEW_CONFIRMED+")) ";

        String workFlowQuery = "select job_post_id, createdby, candidate_id, job_post_workflow_id, creation_timestamp, status_id from job_post_workflow i " +
                " where " +
                statusSql +
                " and job_post_workflow_id = " +
                " (select max(job_post_workflow_id) from job_post_workflow " +
                " where i.candidate_id = job_post_workflow.candidate_id )" +
                " and creation_timestamp >= '" +mSdf.format(yesterday) +"'"+
                " order by job_post_workflow_id desc ";

        RawSql rawSql = RawSqlBuilder.parse(workFlowQuery)
                .columnMapping("creation_timestamp", "creationTimestamp")
                .columnMapping("status_id", "status.statusId")
                .columnMapping("job_post_id", "jobPost.jobPostId")
                .columnMapping("candidate_id", "candidate.candidateId")
                .columnMapping("createdby", "createdBy")
                .columnMapping("job_post_workflow_id", "jobPostWorkflowId")
                .create();

        List<JobPostWorkflow> jobPostWorkflowList = Ebean.find(JobPostWorkflow.class)
                .setRawSql(rawSql)
                .findList();

        // prep map
        for(JobPostWorkflow jobPostWorkflow: jobPostWorkflowList) {
            if(jobPostWorkflow.getJobPost() == null
                    || jobPostWorkflow.getJobPost().getRecruiterProfile() == null
                    || jobPostWorkflow.getJobPost().getRecruiterProfile().getRecruiterProfileEmail() == null
                    || jobPostWorkflow.getJobPost().getRecruiterProfile().getRecruiterProfileEmail().trim().isEmpty()) {
                continue;
            }

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

        // fetch, create and append event from map
        String subject = "Your posted job is receiving applications!! ACTION NEEDED!";
        for(Map.Entry<RecruiterProfile, Map<JobPost, SummaryCount>> entry: summaryMap.entrySet()) {
            // send email for every recruiter
            EmailEvent eodSummaryEmailEvent = getHTMLMessageForEODSummary(entry.getKey()
                    , entry.getValue(), subject);

            if(eodSummaryEmailEvent != null){
               Global.getmNotificationHandler().addToQueue(eodSummaryEmailEvent);
            }
        }

        Timestamp endTime = new Timestamp(System.currentTimeMillis());

        SchedulerManager.saveNewSchedulerStats(startTime, type, subType, note, endTime, true);
    }

    /* Helper methods */
    private EmailEvent getHTMLMessage(RecruiterProfile recruiterProfile,
                                      List<JobPostWorkflow> jobPostWorkflows,
                                      String subject, Date date){

        boolean isReqForToday = false;
        SimpleDateFormat sdf = new SimpleDateFormat(ServerConstants.SDF_FORMAT_YYYYMMDD);

        StringBuilder htmlTable = new StringBuilder();

        if(date == null){
            isReqForToday = true;
            date = Calendar.getInstance().getTime();
            htmlTable.append(
                      "<div>We value your feedback! \n Please login to www.trujobs.in/recruiter and provide feedback for the candidates that you interviewed today!</div><br> \n\n\n"
                    + "<div><font color=\"#0000ff\"><b>For every feedback you provide we would add an interview credit back to your account!! </b></font></div><br>\n\n\n"
                    + "<div><b>The following candidates had booked for interviews today ("+sdf.format(date)+"): </b></div><br>\n\n\n");
        } else {
            htmlTable.append("" +
                     "<div><b>The following interviews are booked for tomorrow  ("+sdf.format(date)+"): </b></div><br>\n\n\n"
                    +"<div> You can use http://trujobs.in/recruiter/home to track tomorrow's interviews. </div><br>\n\n\n"
            );
        }

        htmlTable.append(
                  " <table border=\"1\" cellpadding=\"5\" style=\"font-size:12.8px\">\n"
                + " <thead>"
                + " <tr><b>"
                + " <th>Job Post Title</th>"
                + " <th>Name</th>"
                + " <th>Time Slot</th>"
        );
        if(isReqForToday){
            htmlTable.append("<th>Add feedback </th>\n");
        } else {
            htmlTable.append("<th>Mobile </th>\n");
        }
        htmlTable.append(
                  "</b></tr>\n"
                + "</thead><tbody>");

        String feedbackLink;

        int i = 0;
        for(JobPostWorkflow jobPostWorkflow: jobPostWorkflows) {
            if(jobPostWorkflow.getJobPost() == null
                    || jobPostWorkflow.getJobPost().getRecruiterProfile() == null
                    || jobPostWorkflow.getJobPost().getRecruiterProfile().getRecruiterProfileEmail() == null
                    || jobPostWorkflow.getJobPost().getRecruiterProfile().getRecruiterProfileEmail().trim().isEmpty()) {
                continue;
            }

            feedbackLink = BASE_URL + "/jobApplicants/"+jobPostWorkflow.getJobPost().getJobPostId();

            htmlTable.append(
                    getHTMLTableRow(++i,
                            jobPostWorkflow.getJobPost().getJobPostTitle(),
                            jobPostWorkflow.getCandidate(),
                            jobPostWorkflow.getScheduledInterviewTimeSlot().getInterviewTimeSlotName(),
                            feedbackLink,
                            isReqForToday));
        }
        htmlTable.append("</tbody></table><br>");

        return new EmailEvent(recruiterProfile.getRecruiterProfileEmail(),
                EmailUtil.getEmailHTML(recruiterProfile, htmlTable.toString()), subject);
    }

    private String getHTMLTableRow(int slno, String jobTitle, Candidate candidate, String slotTitle,
                                   String feedbackLink, boolean isReqForToday) {
        StringBuilder row = new StringBuilder();
        row.append(
                "<tr>"
                + "<td>"+ slno +"</td>"
                + "<td><a href="+feedbackLink+" target=\"_blank\">"+ jobTitle +"</td>"
                + "<td>"+ candidate.getCandidateFullName() +"</td>");

        if(isReqForToday){
            row.append(
                     "<td>"+ slotTitle +"</td>"
                    +"<td><a href="+feedbackLink+" target=\"_blank\">" +"Feedback</td>"
            );
        } else {
            row.append(
                     "<td>"+ slotTitle +"</td>"
                    +"<td>" +candidate.getCandidateMobile() + "</td>"
            );
        }
        row.append("</tr>");

        return row.toString();
    }

    private EmailEvent getHTMLMessageForEODSummary(RecruiterProfile recruiterProfile, Map<JobPost, SummaryCount> map, String subject) {
        StringBuilder htmlTable = new StringBuilder();
        StringBuilder tableContent = new StringBuilder();
        int totalApplications = 0;

        tableContent.append("\n<table border=\"1\" cellpadding=\"5\" style=\"font-size:12.8px\">\n"
                + "\t<thead>\n"
                + "\t\t<tr>\n"
                + "\t\t\t<th>Job Post Title</th>\n"
                + "\t\t\t<th>Confirmed Interviews</th>\n"
                + "\t\t\t<th>Awaiting Confirmation</th>\n"
                + "\t\t</tr>\n"
                + "\t</thead><tbody>");

        for(Map.Entry<JobPost, SummaryCount> entry: map.entrySet()) {
            totalApplications += entry.getValue().getTotalAwaitingConformation() + entry.getValue().getTotalConfirmed();

            // create table entry for every jobpost
            tableContent.append(
                    getHTMLTableRowForEODSummary(
                            entry.getKey().getJobPostTitle(),
                            entry.getKey().getJobPostId(),
                            entry.getValue()
                            ));

        }

        if(totalApplications<1) return null;

        htmlTable.append(
                  " <div> Your Job has received "+totalApplications+" applications in the last 24 hrs. Below is a summary of new applications received in the last 24 hrs.</div> "
                + " <div>Login now at www.trujobs.in/recruiter to view more details and contact the applicants!</div><br>\n\n");

        htmlTable.append(tableContent);
        htmlTable.append("</tbody></table>");

        htmlTable.append("<br><div>You can track all applications from your <a href=\"www.trujobs.in/recruiter\" target=\"_blank\">TruJobs dashboard!</a></div><br>");

        // figure out if recruiter has credit or not, order by history id desc
        List<RecruiterCreditHistory> creditHistoryList = recruiterProfile.getRecruiterCreditHistoryList();
        if(creditHistoryList != null && creditHistoryList.size()>0){
            Collections.sort(creditHistoryList, (o1, o2) -> ((Long) o2.getRecruiterCreditHistoryId())
                    .compareTo(o1.getRecruiterCreditHistoryId()));

            if(creditHistoryList.get(0).getRecruiterCreditsAvailable() < 1 ){
                htmlTable.append("<div style=\"color: #4CAF50; font-size: 15px; font-weight: bold;\">Did you know? - Recharging your TruJobs account with interview credits" +
                        " will let interested candidates directly schedule interviews. Login at www.trujobs.in/recruiter to request interview credits now!!\n</div><br>");
            }
        }



        return new EmailEvent(recruiterProfile.getRecruiterProfileEmail(), EmailUtil.getEmailHTML(recruiterProfile, htmlTable.toString()), subject);
    }

    private String getHTMLTableRowForEODSummary(String jobPostTitle, Long jobPostId, SummaryCount summaryCount) {

        String link = BASE_URL + "/jobApplicants/"+jobPostId;
        StringBuilder row = new StringBuilder();
        row.append("<tr>"+
                "<td><a href="+link+" target=\"_blank\">"+ jobPostTitle +"</td>");

        // manipulate confirm
        if(summaryCount.getTotalConfirmed() == 0){
            row.append("<td>"+ summaryCount.getTotalConfirmed() +"</td>");
        } else {
            row.append("<td><a href="+link+"#confirmed"+" target=\"_blank\">"+ summaryCount.getTotalConfirmed() +" (Click to view)</td>");
        }


        // manipulate awaiting
        if(summaryCount.getTotalAwaitingConformation() == 0){
            row.append("<td>"+ summaryCount.getTotalAwaitingConformation() +"</td>");
        } else {
            row.append("<td><a href="+link+"#pendingConfirmation"+" target=\"_blank\">"+ summaryCount.getTotalAwaitingConformation() +" (Click to view)</td>");
        }

        row.append("</tr>");
        return row.toString();
    }

    private Map<Long, List<JobPostWorkflow>> getInterviewMap(Date date) {
        // Get all interview that were scheduled for today, inorder to get feedback on each on of it.

        List<JobPostWorkflow> mJobPostWorkflowList = JobPostWorkFlowDAO.getAllInterviewScheduledFor(date);

        // prepare map of recruiters and their pending jobPostWorkflow list
        Map<Long, List<JobPostWorkflow>> interviewsPerRecruiterMap = new HashMap<>();

        for(JobPostWorkflow jobPostWorkflow: mJobPostWorkflowList) {
            if(jobPostWorkflow.getJobPost() == null
                    || jobPostWorkflow.getJobPost().getRecruiterProfile() == null
                    || jobPostWorkflow.getJobPost().getRecruiterProfile().getRecruiterProfileEmail() == null
                    || jobPostWorkflow.getJobPost().getRecruiterProfile().getRecruiterProfileEmail().trim().isEmpty()) {
                continue;
            }
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

        SummaryCount() {
            // init
            this.setTotalConfirmed(0);
            this.setTotalAwaitingConformation(0);
        }

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
