package models.entity.OM;

import com.avaje.ebean.Model;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import models.entity.Static.JobPostWorkflowStatus;
import models.entity.Static.RejectReason;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.UUID;

/**
 * Created by dodo on 21/11/16.
 */

@Entity(name = "interview_scheduled_status_update")
@Table(name = "interview_scheduled_status_update")
public class InterviewScheduleStatusUpdate extends Model {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "interview_scheduled_status_update_id", columnDefinition = "bigint unsigned", unique = true)
    private long interviewScheduleStatusUpdateId;

    @Column(name = "interview_scheduled_status_update_uuid", columnDefinition = "varchar(255)", nullable = false)
    private String interviewScheduleStatusUpdateUUId; // UUID

    @Column(name = "create_timestamp", columnDefinition = "timestamp default current_timestamp", nullable = false)
    private Timestamp createTimestamp;

    @Column(name = "update_timestamp", columnDefinition = "timestamp null")
    private Timestamp updateTimestamp;

    @ManyToOne
    @JsonManagedReference
    @JoinColumn(name = "job_post_workflow_id", referencedColumnName = "job_post_workflow_id")
    private JobPostWorkflow jobPostWorkflow;

    @ManyToOne
    @JsonManagedReference
    @JoinColumn(name = "ReasonId", referencedColumnName = "ReasonId")
    private RejectReason rejectReason;

    @ManyToOne
    @JsonManagedReference
    @JoinColumn(name = "status_id", referencedColumnName = "status_id")
    private JobPostWorkflowStatus status;

    @Column(name = "interview_confirmed_status_update_note", columnDefinition = "text null")
    private String interviewScheduleStatusUpdateNote;

    public InterviewScheduleStatusUpdate() {
        this.interviewScheduleStatusUpdateUUId = UUID.randomUUID().toString();
        this.createTimestamp = new Timestamp(System.currentTimeMillis());
    }

    public static Finder<String, InterviewScheduleStatusUpdate> find = new Finder(InterviewScheduleStatusUpdate.class);

    public long getInterviewScheduleStatusUpdateId() {
        return interviewScheduleStatusUpdateId;
    }

    public void setInterviewScheduleStatusUpdateId(long interviewScheduleStatusUpdateId) {
        this.interviewScheduleStatusUpdateId = interviewScheduleStatusUpdateId;
    }

    public String getInterviewScheduleStatusUpdateUUId() {
        return interviewScheduleStatusUpdateUUId;
    }

    public void setInterviewScheduleStatusUpdateUUId(String interviewScheduleStatusUpdateUUId) {
        this.interviewScheduleStatusUpdateUUId = interviewScheduleStatusUpdateUUId;
    }

    public Timestamp getCreateTimestamp() {
        return createTimestamp;
    }

    public void setCreateTimestamp(Timestamp createTimestamp) {
        this.createTimestamp = createTimestamp;
    }

    public Timestamp getUpdateTimestamp() {
        return updateTimestamp;
    }

    public void setUpdateTimestamp(Timestamp updateTimestamp) {
        this.updateTimestamp = updateTimestamp;
    }

    public JobPostWorkflow getJobPostWorkflow() {
        return jobPostWorkflow;
    }

    public void setJobPostWorkflow(JobPostWorkflow jobPostWorkflow) {
        this.jobPostWorkflow = jobPostWorkflow;
    }

    public String getInterviewScheduleStatusUpdateNote() {
        return interviewScheduleStatusUpdateNote;
    }

    public void setInterviewScheduleStatusUpdateNote(String interviewScheduleStatusUpdateNote) {
        this.interviewScheduleStatusUpdateNote = interviewScheduleStatusUpdateNote;
    }

    public RejectReason getRejectReason() {
        return rejectReason;
    }

    public void setRejectReason(RejectReason rejectReason) {
        this.rejectReason = rejectReason;
    }

    public JobPostWorkflowStatus getStatus() {
        return status;
    }

    public void setStatus(JobPostWorkflowStatus status) {
        this.status = status;
    }
}
