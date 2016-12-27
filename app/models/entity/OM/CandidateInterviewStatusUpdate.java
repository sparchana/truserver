package models.entity.OM;

import com.avaje.ebean.Model;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import models.entity.Candidate;
import models.entity.JobPost;
import models.entity.Static.RejectReason;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.UUID;

/**
 * Created by dodo on 18/11/16.
 */

@Entity(name = "candidate_interview_status_update")
@Table(name = "candidate_interview_status_update")
public class CandidateInterviewStatusUpdate extends Model {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "candidate_interview_status_update_id", columnDefinition = "bigint unsigned", unique = true)
    private long candidateInterviewStatusUpdateId;

    @Column(name = "candidate_interview_status_update_uuid", columnDefinition = "varchar(255)", nullable = false)
    private String candidateInterviewStatusUpdateUUId; // UUID

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
    @JoinColumn(name = "reason_id", referencedColumnName = "reason_id")
    private RejectReason rejectReason;

    @Column(name = "candidate_interview_status_update_note", columnDefinition = "text null")
    private String candidateInterviewStatusUpdateNote;

    public CandidateInterviewStatusUpdate() {
        this.candidateInterviewStatusUpdateUUId = UUID.randomUUID().toString();
        this.createTimestamp = new Timestamp(System.currentTimeMillis());
    }

    public static Finder<String, CandidateInterviewStatusUpdate> find = new Finder(CandidateInterviewStatusUpdate.class);

    public long getCandidateInterviewStatusUpdateId() {
        return candidateInterviewStatusUpdateId;
    }

    public void setCandidateInterviewStatusUpdateId(long candidateInterviewStatusUpdateId) {
        this.candidateInterviewStatusUpdateId = candidateInterviewStatusUpdateId;
    }

    public String getCandidateInterviewStatusUpdateUUId() {
        return candidateInterviewStatusUpdateUUId;
    }

    public void setCandidateInterviewStatusUpdateUUId(String candidateInterviewStatusUpdateUUId) {
        this.candidateInterviewStatusUpdateUUId = candidateInterviewStatusUpdateUUId;
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

    public String getCandidateInterviewStatusUpdateNote() {
        return candidateInterviewStatusUpdateNote;
    }

    public void setCandidateInterviewStatusUpdateNote(String candidateInterviewStatusUpdateNote) {
        this.candidateInterviewStatusUpdateNote = candidateInterviewStatusUpdateNote;
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

    public RejectReason getRejectReason() {
        return rejectReason;
    }

    public void setRejectReason(RejectReason rejectReason) {
        this.rejectReason = rejectReason;
    }
}