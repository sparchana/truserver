package models.entity.OM;

import com.avaje.ebean.Model;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import models.entity.Candidate;
import models.entity.JobPost;

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
    @JoinColumn(name = "JobPostId", referencedColumnName = "JobPostId")
    private JobPost jobPost;

    @ManyToOne
    @JsonBackReference
    @JoinColumn(name = "CandidateId", referencedColumnName = "CandidateId")
    private Candidate candidate;

    @Column(name = "CreationTimestamp", columnDefinition = "timestamp default current_timestamp not null", nullable = false)
    private Timestamp creationTimestamp;


    @Column(name = "CreatedBy", columnDefinition = "varchar(255) null", nullable = false)
    private String createdBy;

    public JobPostWorkflow() {
        this.creationTimestamp = new Timestamp(System.currentTimeMillis());
    }

    public static Model.Finder<String, JobPostWorkflow> find = new Model.Finder(JobPostWorkflow.class);

    public long getJobPostWorkflowId() {
        return jobPostWorkflowId;
    }

    public void setJobPostWorkflowId(long jobPostWorkflowId) {
        this.jobPostWorkflowId = jobPostWorkflowId;
    }

    public String getJobPostWorkflowUUId() {
        return jobPostWorkflowUUId;
    }

    public void setJobPostWorkflowUUId(String jobPostWorkflowUUId) {
        this.jobPostWorkflowUUId = jobPostWorkflowUUId;
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

    public void setCreationTimestamp(Timestamp creationTimestamp) {
        this.creationTimestamp = creationTimestamp;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }
}

