package dao;

import api.ServerConstants;
import com.avaje.ebean.Ebean;
import com.avaje.ebean.RawSql;
import com.avaje.ebean.RawSqlBuilder;
import models.entity.OM.CandidateInterviewStatusUpdate;
import models.entity.OM.JobPostWorkflow;
import org.apache.commons.lang3.StringUtils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by dodo on 29/11/16.
 */
public class JobPostWorkFlowDAO {
    /**
     *
     * @param candidateId , accepts candidateId
     * @return List<JobPostWorkflow> containing all jobPost to which a candidate with id: 'candidateId', has applied to.
     */
    public static List<JobPostWorkflow> candidateAppliedJobs(Long candidateId) {
        List<JobPostWorkflow> appliedJobsList;

        String candidateAppliedJobsSql = "select creation_timestamp, job_post_workflow_id, job_post_id, status_id, scheduled_interview_time_slot, scheduled_interview_date, interview_location_lat, interview_location_lng " +
                "from job_post_workflow jwf where jwf.job_post_workflow_id = (select max(job_post_workflow_id) from job_post_workflow where jwf.job_post_id = job_post_workflow.job_post_id and job_post_workflow.candidate_id = " + candidateId +
                " ) order by job_post_workflow_id desc";

        RawSql rawSql = RawSqlBuilder.parse(candidateAppliedJobsSql)
                .tableAliasMapping("jwf", "job_post_workflow")
                .columnMapping("job_post_id", "jobPost.jobPostId")
                .columnMapping("status_id", "status.statusId")
                .columnMapping("scheduled_interview_time_slot", "scheduledInterviewTimeSlot.interviewTimeSlotId")
                .columnMapping("scheduled_interview_date", "scheduledInterviewDate")
                .columnMapping("interview_location_lat", "interviewLocationLat")
                .columnMapping("interview_location_lng", "interviewLocationLng")
                .columnMapping("job_post_workflow_id", "jobPostWorkflowId")
                .create();

        appliedJobsList = Ebean.find(JobPostWorkflow.class)
                .setRawSql(rawSql)
                .findList();

        return appliedJobsList;
    }

    public static List<JobPostWorkflow> getPartnerAppliedJobsForCandidate(long candidateId, List<Long> jobPostIdList){
        String candidateAppliedJobsSql = "select job_post_id, status_id, job_post_workflow_id, scheduled_interview_time_slot, scheduled_interview_date, interview_location_lat, interview_location_lng " +
                "from job_post_workflow jwf where jwf.job_post_workflow_id = (select max(job_post_workflow_id)\n" +
                " from job_post_workflow where jwf.job_post_id = job_post_workflow.job_post_id and job_post_workflow.candidate_id = " + candidateId + ") " +
                "and jwf.job_post_id in (" + StringUtils.join(jobPostIdList, ", ") + ")";

        RawSql rawSql = RawSqlBuilder.parse(candidateAppliedJobsSql)
                .tableAliasMapping("jwf", "job_post_workflow")
                .columnMapping("job_post_id", "jobPost.jobPostId")
                .columnMapping("status_id", "status.statusId")
                .columnMapping("scheduled_interview_time_slot", "scheduledInterviewTimeSlot.interviewTimeSlotId")
                .columnMapping("scheduled_interview_date", "scheduledInterviewDate")
                .columnMapping("interview_location_lat", "interviewLocationLat")
                .columnMapping("interview_location_lng", "interviewLocationLng")
                .columnMapping("job_post_workflow_id", "jobPostWorkflowId")
                .create();

        return Ebean.find(JobPostWorkflow.class)
                .setRawSql(rawSql)
                .findList();
    }

    /**
     * @param jobPostId, accepts a 'JobPostId'
     * @return List<JobPostWorkflow>, workflow equivalent of all the candidate who has applied to a Job Post with id: 'jobPostId'
     */
    public static List<JobPostWorkflow> getJobApplications(String jobPostId) {
        String statusSql = " and (status_id NOT IN ( '" + ServerConstants.JWF_STATUS_PRESCREEN_FAILED + "')) ";
        String workFlowQueryBuilder = "select createdby, candidate_id, job_post_workflow_id, creation_timestamp, job_post_id, status_id from job_post_workflow i " +
                " where i.job_post_id " +
                " in (" + jobPostId + ") " +
                statusSql +
                " and job_post_workflow_id = " +
                " (select max(job_post_workflow_id) from job_post_workflow " +
                "       where i.candidate_id = job_post_workflow.candidate_id " +
                "       and i.job_post_id = job_post_workflow.job_post_id) " +
                " order by job_post_workflow_id desc ";

        RawSql rawSql = RawSqlBuilder.parse(workFlowQueryBuilder)
                .columnMapping("creation_timestamp", "creationTimestamp")
                .columnMapping("job_post_id", "jobPost.jobPostId")
                .columnMapping("status_id", "status.statusId")
                .columnMapping("candidate_id", "candidate.candidateId")
                .columnMapping("createdby", "createdBy")
                .columnMapping("job_post_workflow_id", "jobPostWorkflowId")
                .create();

        return Ebean.find(JobPostWorkflow.class)
                .setRawSql(rawSql)
                .findList();
    }

    /**
     * @param jobPostId
     * @param statusList
     * @param statusIN true : status list is a IN query, false: status list is NOT IN query
     * @return
     */
    public static List<JobPostWorkflow> getLatestRecords(long jobPostId, List<Integer> statusList, boolean statusIN){


        StringBuilder workFlowQueryBuilder = new StringBuilder(
                " select createdby, candidate_id, job_post_workflow_id, creation_timestamp," +
                " job_post_id, status_id from job_post_workflow i " +
                " where i.job_post_id " +
                " = " + jobPostId + " " +
                " and job_post_workflow_id = " +
                " (select max(job_post_workflow_id) from job_post_workflow " +
                "       where i.candidate_id = job_post_workflow.candidate_id " +
                "       and i.job_post_id = job_post_workflow.job_post_id) ");

        if(statusList != null && statusList.size() > 0){
            if(statusIN){
                workFlowQueryBuilder.append(" and (status_id IN ("+StringUtils.join(statusList, ", ")+")) ");
            } else {
                workFlowQueryBuilder.append(" and (status_id NOT IN ("+StringUtils.join(statusList, ", ")+")) ");
            }
        }

        workFlowQueryBuilder.append(
                " order by scheduled_interview_date ");

        RawSql rawSql = RawSqlBuilder.parse(workFlowQueryBuilder.toString())
                .columnMapping("creation_timestamp", "creationTimestamp")
                .columnMapping("job_post_id", "jobPost.jobPostId")
                .columnMapping("status_id", "status.statusId")
                .columnMapping("candidate_id", "candidate.candidateId")
                .columnMapping("createdby", "createdBy")
                .columnMapping("job_post_workflow_id", "jobPostWorkflowId")
                .create();

        return Ebean.find(JobPostWorkflow.class)
                .setRawSql(rawSql)
                .findList();
    }

    public static List<JobPostWorkflow> getTodayInterview(List<Long> jobPostIdList, int status, Date today){

        SimpleDateFormat sdf = new SimpleDateFormat(ServerConstants.SDF_FORMAT_YYYYMMDD);

        StringBuilder workFlowQueryBuilder = new StringBuilder(
                " select createdby, candidate_id, job_post_workflow_id, scheduled_interview_date, creation_timestamp," +
                        " job_post_id, status_id from job_post_workflow i " +
                        " where i.job_post_id " +
                        " in (" + StringUtils.join(jobPostIdList, ", ")+ ") " +
                        " and scheduled_interview_date = '"+sdf.format(today) + "' "+
                        " and status_id >= "+status +
                        " and job_post_workflow_id = " +
                        " (select max(job_post_workflow_id) from job_post_workflow " +
                        "       where i.candidate_id = job_post_workflow.candidate_id " +
                        "       and i.scheduled_interview_date = job_post_workflow.scheduled_interview_date " +
                        "       and i.job_post_id = job_post_workflow.job_post_id) ");

        workFlowQueryBuilder.append(
                " order by job_post_workflow_id desc ");

        RawSql rawSql = RawSqlBuilder.parse(workFlowQueryBuilder.toString())
                .columnMapping("creation_timestamp", "creationTimestamp")
                .columnMapping("job_post_id", "jobPost.jobPostId")
                .columnMapping("status_id", "status.statusId")
                .columnMapping("candidate_id", "candidate.candidateId")
                .columnMapping("createdby", "createdBy")
                .columnMapping("job_post_workflow_id", "jobPostWorkflowId")
                .columnMapping("scheduled_interview_date", "scheduledInterviewDate")
                .create();

        return Ebean.find(JobPostWorkflow.class)
                .setRawSql(rawSql)
                .findList();
    }

    public static List<JobPostWorkflow> getTomorrowsInterview(List<Long> jobPostIdList, int status, Date today){

        StringBuilder workFlowQueryBuilder = new StringBuilder(
                " select createdby, candidate_id, job_post_workflow_id, scheduled_interview_date, creation_timestamp," +
                        " job_post_id, status_id from job_post_workflow i " +
                        " where i.job_post_id " +
                        " in (" + StringUtils.join(jobPostIdList, ", ")+ ") " +
                        " and DATE(scheduled_interview_date) = curdate()+1"+
                        " and status_id >= "+status +
                        " and job_post_workflow_id = " +
                        " (select max(job_post_workflow_id) from job_post_workflow " +
                        "       where i.candidate_id = job_post_workflow.candidate_id " +
                        "       and i.scheduled_interview_date = job_post_workflow.scheduled_interview_date " +
                        "       and i.job_post_id = job_post_workflow.job_post_id) ");

        workFlowQueryBuilder.append(
                " order by job_post_workflow_id desc ");

        RawSql rawSql = RawSqlBuilder.parse(workFlowQueryBuilder.toString())
                .columnMapping("creation_timestamp", "creationTimestamp")
                .columnMapping("job_post_id", "jobPost.jobPostId")
                .columnMapping("status_id", "status.statusId")
                .columnMapping("candidate_id", "candidate.candidateId")
                .columnMapping("createdby", "createdBy")
                .columnMapping("job_post_workflow_id", "jobPostWorkflowId")
                .columnMapping("scheduled_interview_date", "scheduledInterviewDate")
                .create();

        return Ebean.find(JobPostWorkflow.class)
                .setRawSql(rawSql)
                .findList();
    }

    public static List<JobPostWorkflow> getInterviewsPendingApproval(List<Long> jobPostIdList){

        StringBuilder workFlowQueryBuilder = new StringBuilder(
                " select createdby, candidate_id, job_post_workflow_id, scheduled_interview_date, creation_timestamp," +
                        " job_post_id, status_id from job_post_workflow i " +
                        " where i.job_post_id " +
                        " in (" + StringUtils.join(jobPostIdList, ", ")+ ") " +
                        " and status_id = "+ServerConstants.JWF_STATUS_INTERVIEW_SCHEDULED +
                        " and job_post_workflow_id = " +
                        " (select max(job_post_workflow_id) from job_post_workflow " +
                        "       where i.candidate_id = job_post_workflow.candidate_id " +
                        "       and i.scheduled_interview_date = job_post_workflow.scheduled_interview_date " +
                        "       and i.job_post_id = job_post_workflow.job_post_id) ");

        workFlowQueryBuilder.append(
                " order by job_post_workflow_id desc ");

        RawSql rawSql = RawSqlBuilder.parse(workFlowQueryBuilder.toString())
                .columnMapping("creation_timestamp", "creationTimestamp")
                .columnMapping("job_post_id", "jobPost.jobPostId")
                .columnMapping("status_id", "status.statusId")
                .columnMapping("candidate_id", "candidate.candidateId")
                .columnMapping("createdby", "createdBy")
                .columnMapping("job_post_workflow_id", "jobPostWorkflowId")
                .columnMapping("scheduled_interview_date", "scheduledInterviewDate")
                .create();

        return Ebean.find(JobPostWorkflow.class)
                .setRawSql(rawSql)
                .findList();
    }

    public static List<JobPostWorkflow> getRecords(long jobPostId, List<Integer> statusList){
        return JobPostWorkflow.find.where()
                .eq("jobPost.jobPostId", jobPostId)
                .in("status_id", statusList)
                .findList();
    }

    public static List<JobPostWorkflow> getRecords(long jobPostId, int status, String startDate, String endDate){
        return JobPostWorkflow.find.where()
                .eq("jobPost.jobPostId", jobPostId)
                .eq("status_id", status)
                .ge("scheduled_interview_date", startDate)
                .le("scheduled_interview_date", endDate)
                .findList();
    }

    public static JobPostWorkflow getJobPostWorkflowCurrent(long jobPostId, long candidateId){
        // fetch existing workflow old
        return  JobPostWorkflow.find.where()
                .eq("jobPost.jobPostId",  jobPostId)
                .eq("candidate.candidateId", candidateId)
                .orderBy().desc("job_post_workflow_id").setMaxRows(1).findUnique();
    }

    public static Map<?, JobPostWorkflow> getJobPostWorkflowMapByCandidateId(long jobPostId, int statusId, List<Long> selectedCandidateIdList){
        return JobPostWorkflow.find
                .where()
                .eq("job_post_id", jobPostId)
                .eq("status_id", statusId)
                .in("candidate_id", selectedCandidateIdList)
                .setMapKey("candidate_id")
                .findMap();
    }

    public static List<JobPostWorkflow> getAllInterviewScheduledFor(Date date){


        SimpleDateFormat sdf = new SimpleDateFormat(ServerConstants.SDF_FORMAT_YYYYMMDD);

        StringBuilder workFlowQueryBuilder = new StringBuilder(
                " select createdby, candidate_id, job_post_workflow_id, scheduled_interview_date, creation_timestamp," +
                        " job_post_id, status_id from job_post_workflow i " +
                        " where " +
                        " scheduled_interview_date = '"+sdf.format(date) + "' "+
                        " and status_id = "+ServerConstants.JWF_STATUS_INTERVIEW_CONFIRMED +
                        " and job_post_workflow_id = " +
                        " (select max(job_post_workflow_id) from job_post_workflow " +
                        "       where i.candidate_id = job_post_workflow.candidate_id " +
                        "       and i.scheduled_interview_date = job_post_workflow.scheduled_interview_date " +
                        "       and i.job_post_id = job_post_workflow.job_post_id) ");

        workFlowQueryBuilder.append(
                " order by job_post_id desc ");

        RawSql rawSql = RawSqlBuilder.parse(workFlowQueryBuilder.toString())
                .columnMapping("creation_timestamp", "creationTimestamp")
                .columnMapping("job_post_id", "jobPost.jobPostId")
                .columnMapping("status_id", "status.statusId")
                .columnMapping("candidate_id", "candidate.candidateId")
                .columnMapping("createdby", "createdBy")
                .columnMapping("job_post_workflow_id", "jobPostWorkflowId")
                .columnMapping("scheduled_interview_date", "scheduledInterviewDate")
                .create();

        return Ebean.find(JobPostWorkflow.class)
                .setRawSql(rawSql)
                .findList();
    }

    public static List<JobPostWorkflow> getApplicationCountAccordingToStatus(List<Long> jobPostIdList, int status){
        StringBuilder workFlowQueryBuilder = new StringBuilder(
                " select createdby, candidate_id, job_post_workflow_id, scheduled_interview_date, creation_timestamp," +
                        " job_post_id, status_id from job_post_workflow i " +
                        " where i.job_post_id " +
                        " in (" + StringUtils.join(jobPostIdList, ", ")+ ") " +
                        " and status_id = "+status +
                        " and job_post_workflow_id = " +
                        " (select max(job_post_workflow_id) from job_post_workflow " +
                        "       where i.candidate_id = job_post_workflow.candidate_id " +
                        "       and i.job_post_id = job_post_workflow.job_post_id) ");

        workFlowQueryBuilder.append(
                " order by job_post_workflow_id desc ");

        RawSql rawSql = RawSqlBuilder.parse(workFlowQueryBuilder.toString())
                .columnMapping("creation_timestamp", "creationTimestamp")
                .columnMapping("job_post_id", "jobPost.jobPostId")
                .columnMapping("status_id", "status.statusId")
                .columnMapping("candidate_id", "candidate.candidateId")
                .columnMapping("createdby", "createdBy")
                .columnMapping("job_post_workflow_id", "jobPostWorkflowId")
                .create();

        return Ebean.find(JobPostWorkflow.class)
                .setRawSql(rawSql)
                .findList();
    }

    /**
     * @param jobPostIdList, accepts a list of jobPostId
     * @param status, accepts a jobPostWorkFlowStatus
     * @return List<JobPostWorkflow>, all the confirmed interview applications
     */

    public static List<JobPostWorkflow> getConfirmedInterviewApplications(List<Long> jobPostIdList, int status){
        StringBuilder workFlowQueryBuilder = new StringBuilder(
                " select createdby, candidate_id, job_post_workflow_id, scheduled_interview_date, creation_timestamp," +
                        " job_post_id, status_id from job_post_workflow i " +
                        " where i.job_post_id " +
                        " in (" + StringUtils.join(jobPostIdList, ", ")+ ") " +
                        " and status_id >= "+status +
                        " and job_post_workflow_id = " +
                        " (select max(job_post_workflow_id) from job_post_workflow " +
                        "       where i.candidate_id = job_post_workflow.candidate_id " +
                        "       and i.job_post_id = job_post_workflow.job_post_id) ");

        workFlowQueryBuilder.append(
                " order by job_post_workflow_id desc ");

        RawSql rawSql = RawSqlBuilder.parse(workFlowQueryBuilder.toString())
                .columnMapping("creation_timestamp", "creationTimestamp")
                .columnMapping("job_post_id", "jobPost.jobPostId")
                .columnMapping("status_id", "status.statusId")
                .columnMapping("candidate_id", "candidate.candidateId")
                .columnMapping("createdby", "createdBy")
                .columnMapping("job_post_workflow_id", "jobPostWorkflowId")
                .columnMapping("scheduled_interview_date", "scheduledInterviewDate")
                .create();

        return Ebean.find(JobPostWorkflow.class)
                .setRawSql(rawSql)
                .findList();
    }

    /**
     *
     * @return List<JobPostWorkflow>, all the interviews which are scheduled today where the status is no 'not going'
     */

    public static List<JobPostWorkflow> getTodaysConfirmedInterviews(){
        String workFlowQueryBuilder = " select createdby, candidate_id, job_post_workflow_id, scheduled_interview_date, creation_timestamp," +
                " job_post_id, status_id from job_post_workflow i " +
                " where status_id >= '" + ServerConstants.JWF_STATUS_INTERVIEW_CONFIRMED + "'" +
                " and status_id not in (" + ServerConstants.JWF_STATUS_CANDIDATE_INTERVIEW_STATUS_NOT_GOING + ")" + //not '10' because candidate reported as not going
                " and DATE(scheduled_interview_date) = curdate()" +
                " and job_post_workflow_id = " +
                " (select max(job_post_workflow_id) from job_post_workflow " +
                "       where i.candidate_id = job_post_workflow.candidate_id " +
                "       and i.job_post_id = job_post_workflow.job_post_id)" +
                " order by job_post_workflow_id desc ";

                RawSql rawSql = RawSqlBuilder.parse(workFlowQueryBuilder)
                    .columnMapping("creation_timestamp", "creationTimestamp")
                    .columnMapping("job_post_id", "jobPost.jobPostId")
                    .columnMapping("status_id", "status.statusId")
                    .columnMapping("candidate_id", "candidate.candidateId")
                    .columnMapping("createdby", "createdBy")
                    .columnMapping("job_post_workflow_id", "jobPostWorkflowId")
                    .columnMapping("scheduled_interview_date", "scheduledInterviewDate")
                    .create();

                return Ebean.find(JobPostWorkflow.class)
                    .setRawSql(rawSql)
                    .findList();
    }

    public static List<JobPostWorkflow> getConfirmedInterviewsBetweenDate(Long jobPostId, Date startDate, Date endDate){

        SimpleDateFormat sdf = new SimpleDateFormat(ServerConstants.SDF_FORMAT_YYYYMMDD);

        String workFlowQueryBuilder = " select candidate_id, job_post_workflow_id, scheduled_interview_date, creation_timestamp," +
                " job_post_id, status_id from job_post_workflow i " +
                " where i.job_post_id = " + jobPostId +
                " and scheduled_interview_date > '" + sdf.format(startDate) + "' " +
                " and scheduled_interview_date < '" + sdf.format(endDate) + "' " +
                " and status_id >= " + ServerConstants.JWF_STATUS_INTERVIEW_CONFIRMED +
                " and job_post_workflow_id = " +
                " (select max(job_post_workflow_id) from job_post_workflow " +
                "       where i.candidate_id = job_post_workflow.candidate_id " +
                "       and i.scheduled_interview_date = job_post_workflow.scheduled_interview_date " +
                "       and i.job_post_id = job_post_workflow.job_post_id) " +
                " order by job_post_workflow_id desc ";

        RawSql rawSql = RawSqlBuilder.parse(workFlowQueryBuilder)
                .columnMapping("creation_timestamp", "creationTimestamp")
                .columnMapping("job_post_id", "jobPost.jobPostId")
                .columnMapping("status_id", "status.statusId")
                .columnMapping("candidate_id", "candidate.candidateId")
                .columnMapping("job_post_workflow_id", "jobPostWorkflowId")
                .columnMapping("scheduled_interview_date", "scheduledInterviewDate")
                .create();

        return Ebean.find(JobPostWorkflow.class)
                .setRawSql(rawSql)
                .findList();
    }

    public static List<JobPostWorkflow> getAllConfirmedInterviewsFromToday(Long jobPostId, Date startDate){

        SimpleDateFormat sdf = new SimpleDateFormat(ServerConstants.SDF_FORMAT_YYYYMMDD);

        String workFlowQueryBuilder = " select candidate_id, job_post_workflow_id, scheduled_interview_date, creation_timestamp," +
                " job_post_id, status_id from job_post_workflow i " +
                " where i.job_post_id = " + jobPostId +
                " and scheduled_interview_date > '" + sdf.format(startDate) + "' " +
                " and status_id >= " + ServerConstants.JWF_STATUS_INTERVIEW_CONFIRMED +
                " and job_post_workflow_id = " +
                " (select max(job_post_workflow_id) from job_post_workflow " +
                "       where i.candidate_id = job_post_workflow.candidate_id " +
                "       and i.scheduled_interview_date = job_post_workflow.scheduled_interview_date " +
                "       and i.job_post_id = job_post_workflow.job_post_id) " +
                " order by job_post_workflow_id desc ";

        RawSql rawSql = RawSqlBuilder.parse(workFlowQueryBuilder)
                .columnMapping("creation_timestamp", "creationTimestamp")
                .columnMapping("job_post_id", "jobPost.jobPostId")
                .columnMapping("status_id", "status.statusId")
                .columnMapping("candidate_id", "candidate.candidateId")
                .columnMapping("job_post_workflow_id", "jobPostWorkflowId")
                .columnMapping("scheduled_interview_date", "scheduledInterviewDate")
                .create();

        return Ebean.find(JobPostWorkflow.class)
                .setRawSql(rawSql)
                .findList();
    }

}
