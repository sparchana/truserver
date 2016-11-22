package models.entity.OM;

import com.avaje.ebean.Model;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import models.entity.Candidate;
import models.entity.JobPost;
import models.entity.Static.JobPostWorkflowStatus;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.UUID;

/**
 * Created by dodo on 22/11/16.
 */

@Entity(name = "interview_feedback_update")
@Table(name = "interview_feedback_update")
public class InterviewFeedbackUpdate extends Model {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "interview_feedback_update_id", columnDefinition = "bigint unsigned", unique = true)
    private long interviewFeedbackUpdateId;

    @Column(name = "interview_feedback_update_uuid", columnDefinition = "varchar(255)", nullable = false)
    private String interviewFeedbackUpdateUUId; // UUID

    @Column(name = "create_timestamp", columnDefinition = "timestamp default current_timestamp", nullable = false)
    private Timestamp createTimestamp;

    @ManyToOne
    @JsonManagedReference
    @JoinColumn(name = "job_post_workflow_id", referencedColumnName = "job_post_workflow_id")
    private JobPostWorkflow jobPostWorkflow;

    @ManyToOne
    @JsonBackReference
    @JoinColumn(name = "CandidateId", referencedColumnName = "CandidateId")
    private Candidate candidate;

    @ManyToOne
    @JsonManagedReference
    @JoinColumn(name = "JobPostId", referencedColumnName = "JobPostId")
    private JobPost jobPost;

    @ManyToOne
    @JsonManagedReference
    @JoinColumn(name = "status_id", referencedColumnName = "status_id")
    private JobPostWorkflowStatus status;

    @Column(name = "interview_feedback_update_note", columnDefinition = "text null")
    private String candidateInterviewStatusUpdateNote;

    public InterviewFeedbackUpdate() {
        this.interviewFeedbackUpdateUUId = UUID.randomUUID().toString();
        this.createTimestamp = new Timestamp(System.currentTimeMillis());
    }

    public static Finder<String, InterviewFeedbackUpdate> find = new Finder(InterviewFeedbackUpdate.class);

    public long getInterviewFeedbackUpdateId() {
        return interviewFeedbackUpdateId;
    }

    public void setInterviewFeedbackUpdateId(long interviewFeedbackUpdateId) {
        this.interviewFeedbackUpdateId = interviewFeedbackUpdateId;
    }

    public String getInterviewFeedbackUpdateUUId() {
        return interviewFeedbackUpdateUUId;
    }

    public void setInterviewFeedbackUpdateUUId(String interviewFeedbackUpdateUUId) {
        this.interviewFeedbackUpdateUUId = interviewFeedbackUpdateUUId;
    }

    public Timestamp getCreateTimestamp() {
        return createTimestamp;
    }

    public void setCreateTimestamp(Timestamp createTimestamp) {
        this.createTimestamp = createTimestamp;
    }

    public JobPostWorkflow getJobPostWorkflow() {
        return jobPostWorkflow;
    }

    public void setJobPostWorkflow(JobPostWorkflow jobPostWorkflow) {
        this.jobPostWorkflow = jobPostWorkflow;
    }

    public Candidate getCandidate() {
        return candidate;
    }

    public void setCandidate(Candidate candidate) {
        this.candidate = candidate;
    }

    public JobPost getJobPost() {
        return jobPost;
    }

    public void setJobPost(JobPost jobPost) {
        this.jobPost = jobPost;
    }

    public JobPostWorkflowStatus getStatus() {
        return status;
    }

    public void setStatus(JobPostWorkflowStatus status) {
        this.status = status;
    }

    public String getCandidateInterviewStatusUpdateNote() {
        return candidateInterviewStatusUpdateNote;
    }

    public void setCandidateInterviewStatusUpdateNote(String candidateInterviewStatusUpdateNote) {
        this.candidateInterviewStatusUpdateNote = candidateInterviewStatusUpdateNote;
    }
}