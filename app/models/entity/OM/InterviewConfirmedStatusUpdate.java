package models.entity.OM;

import com.avaje.ebean.Model;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.UUID;

/**
 * Created by dodo on 12/11/16.
 */
@Entity(name = "interview_confirmed_status_update")
@Table(name = "interview_confirmed_status_update")
public class InterviewConfirmedStatusUpdate extends Model {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "interview_confirmed_status_update_id", columnDefinition = "bigint unsigned", unique = true)
    private long interviewConfirmedStatusUpdateId;

    @Column(name = "interview_confirmed_status_update_uuid", columnDefinition = "varchar(255)", nullable = false)
    private String interviewConfirmedStatusUpdateUUId; // UUID

    @Column(name = "create_timestamp", columnDefinition = "timestamp default current_timestamp", nullable = false)
    private Timestamp createTimestamp;

    @Column(name = "update_timestamp", columnDefinition = "timestamp null")
    private Timestamp updateTimestamp;

    @ManyToOne
    @JsonManagedReference
    @JoinColumn(name = "job_post_workflow_id", referencedColumnName = "job_post_workflow_id")
    private JobPostWorkflow jobPostWorkflow;

    @Column(name = "interview_confirmed_status_update_note", columnDefinition = "text null")
    private String interviewConfirmedStatusUpdateNote;

    public InterviewConfirmedStatusUpdate() {
        this.interviewConfirmedStatusUpdateUUId = UUID.randomUUID().toString();
        this.createTimestamp = new Timestamp(System.currentTimeMillis());
    }

    public static Finder<String, InterviewConfirmedStatusUpdate> find = new Finder(InterviewConfirmedStatusUpdate.class);

    public long getInterviewConfirmedStatusUpdateId() {
        return interviewConfirmedStatusUpdateId;
    }

    public void setInterviewConfirmedStatusUpdateId(long interviewConfirmedStatusUpdateId) {
        this.interviewConfirmedStatusUpdateId = interviewConfirmedStatusUpdateId;
    }

    public String getInterviewConfirmedStatusUpdateUUId() {
        return interviewConfirmedStatusUpdateUUId;
    }

    public void setInterviewConfirmedStatusUpdateUUId(String interviewConfirmedStatusUpdateUUId) {
        this.interviewConfirmedStatusUpdateUUId = interviewConfirmedStatusUpdateUUId;
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

    public String getInterviewConfirmedStatusUpdateNote() {
        return interviewConfirmedStatusUpdateNote;
    }

    public void setInterviewConfirmedStatusUpdateNote(String interviewConfirmedStatusUpdateNote) {
        this.interviewConfirmedStatusUpdateNote = interviewConfirmedStatusUpdateNote;
    }
}