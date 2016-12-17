package dao;

import api.ServerConstants;
import com.avaje.ebean.Ebean;
import com.avaje.ebean.RawSql;
import com.avaje.ebean.RawSqlBuilder;
import models.entity.Company;
import models.entity.OM.JobPostWorkflow;
import play.Logger;

import java.util.List;

/**
 * Created by dodo on 29/11/16.
 */
public class JobPostWorkFlowDAO {
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
}
