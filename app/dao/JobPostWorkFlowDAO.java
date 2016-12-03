package dao;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.RawSql;
import com.avaje.ebean.RawSqlBuilder;
import models.entity.OM.JobPostWorkflow;
import play.Logger;

import java.util.List;

/**
 * Created by dodo on 29/11/16.
 */
public class JobPostWorkFlowDAO {
    public static List<JobPostWorkflow> candidateAppliedJobs(Long candidateId) {
        List<JobPostWorkflow> appliedJobsList;

        String candidateAppliedJobsSql = "select creation_timestamp, job_post_id, status_id, scheduled_interview_time_slot, scheduled_interview_date, interview_location_lat, interview_location_lng " +
                "from job_post_workflow jwf where jwf.creation_timestamp = (select max(creation_timestamp)\n" +
                " from job_post_workflow where jwf.job_post_id = job_post_workflow.job_post_id and job_post_workflow.candidate_id = " + candidateId + ") order by creation_timestamp";

        RawSql rawSql = RawSqlBuilder.parse(candidateAppliedJobsSql)
                .tableAliasMapping("jwf", "job_post_workflow")
                .columnMapping("job_post_id", "jobPost.jobPostId")
                .columnMapping("status_id", "status.statusId")
                .columnMapping("scheduled_interview_time_slot", "scheduledInterviewTimeSlot.interviewTimeSlotId")
                .columnMapping("scheduled_interview_date", "scheduledInterviewDate")
                .columnMapping("interview_location_lat", "interviewLocationLat")
                .columnMapping("interview_location_lng", "interviewLocationLng")
                .columnMapping("creation_timestamp", "creationTimestamp")
                .create();

        appliedJobsList = Ebean.find(JobPostWorkflow.class)
                .setRawSql(rawSql)
                .findList();

        return appliedJobsList;
    }

}
