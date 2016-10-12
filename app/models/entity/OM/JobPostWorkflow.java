package models.entity.OM;

import com.avaje.ebean.Model;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import models.entity.Candidate;
import models.entity.JobPost;
import models.entity.Static.JobPostWorkflowStatus;

import javax.persistence.*;
import java.sql.Timestamp;

/**
 * Created by zero on 10/10/16.
 */

@Entity(name = "job_post_workflow")
@Table(name = "job_post_workflow")
public class JobPostWorkflow extends Model {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name = "job_post_workflow_id", columnDefinition = "bigint unsigned", unique = true)
    private long jobPostWorkflowId;

    @Column(name = "job_post_workflow_uuid", columnDefinition = "varchar(255) not null", nullable = false)
    private String jobPostWorkflowUUId = ""; // UUID

    @ManyToOne
    @JsonManagedReference
    @JoinColumn(name = "job_post_id", referencedColumnName = "JobPostId")
    private JobPost jobPost;

    @ManyToOne
    @JsonBackReference
    @JoinColumn(name = "candidate_id", referencedColumnName = "CandidateId")
    private Candidate candidate;

    @Column(name = "creation_timestamp", columnDefinition = "timestamp default current_timestamp not null", nullable = false)
    private Timestamp creationTimestamp;

    @ManyToOne
    @JsonBackReference
    @JoinColumn(name = "status_id", referencedColumnName = "status_id")
    private JobPostWorkflowStatus status;

    @Column(name = "createdby", columnDefinition = "varchar(255) null", nullable = false)
    private String createdBy;

    public JobPostWorkflow() {
        this.creationTimestamp = new Timestamp(System.currentTimeMillis());
    }

    public static Model.Finder<String, JobPostWorkflow> find = new Model.Finder(JobPostWorkflow.class);

    public long getJobPostWorkflowId() {
        return jobPostWorkflowId;
    }

    public String getJobPostWorkflowUUId() {
        return jobPostWorkflowUUId;
    }

    public JobPost getJobPost() {
        return jobPost;
    }

    public void setJobPost(JobPost jobPost) {
        this.jobPost = jobPost;
    }

    public Candidate getCandidate() {
        return candidate;
    }

    public void setCandidate(Candidate candidate) {
        this.candidate = candidate;
    }

    public Timestamp getCreationTimestamp() {
        return creationTimestamp;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public JobPostWorkflowStatus getStatus() {
        return status;
    }

    public void setStatus(JobPostWorkflowStatus status) {
        this.status = status;
    }

}

