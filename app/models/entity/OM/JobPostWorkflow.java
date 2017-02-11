package models.entity.OM;

import com.avaje.ebean.Model;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import models.entity.Candidate;
import models.entity.JobPost;
import models.entity.Recruiter.RecruiterProfile;
import models.entity.Static.InterviewTimeSlot;
import models.entity.Static.JobPostWorkflowStatus;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Date;
import java.util.UUID;

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
    private String jobPostWorkflowUUId; // UUID

    @ManyToOne
    @JsonManagedReference
    @JoinColumn(name = "job_post_id", referencedColumnName = "JobPostId")
    private JobPost jobPost;

    @ManyToOne
    @JsonManagedReference
    @JoinColumn(name = "candidate_id", referencedColumnName = "CandidateId")
    private Candidate candidate;

    @Column(name = "creation_timestamp", columnDefinition = "timestamp default current_timestamp not null", nullable = false)
    private Timestamp creationTimestamp;

    @ManyToOne
    @JsonManagedReference
    @JoinColumn(name = "status_id", referencedColumnName = "status_id")
    private JobPostWorkflowStatus status;

    @Column(name = "createdby", columnDefinition = "varchar(255) null", nullable = false)
    private String createdBy;

    @ManyToOne
    @JsonManagedReference
    @JoinColumn(name = "scheduled_interview_time_slot")
    private InterviewTimeSlot scheduledInterviewTimeSlot;

    @Column(name = "scheduled_interview_date", columnDefinition = "date null")
    private Date scheduledInterviewDate;

    @Column(name = "channel", columnDefinition = "int null")
    private Integer channel;

    @Column(name = "interview_location_lat", columnDefinition = "double null")
    private Double interviewLocationLat;

    @Column(name = "interview_location_lng", columnDefinition = "double null")
    private Double interviewLocationLng;

    @ManyToOne
    @JsonManagedReference
    @JoinColumn(name = "interview_recruiter_id", referencedColumnName = "RecruiterProfileId")
    private RecruiterProfile recruiterProfile;

    @Column(name = "interview_round", columnDefinition = "int null")
    private Integer interviewRound;


    @Transient
    private boolean isPreScreenRequired = true;

    public JobPostWorkflow() {
        this.creationTimestamp = new Timestamp(System.currentTimeMillis());
        this.jobPostWorkflowUUId = UUID.randomUUID().toString();
    }

    public static Model.Finder<String, JobPostWorkflow> find = new Model.Finder(JobPostWorkflow.class);

    public void setJobPostWorkflowUUId(String jobPostWorkflowUUId) {
        this.jobPostWorkflowUUId = jobPostWorkflowUUId;
    }

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

    public InterviewTimeSlot getScheduledInterviewTimeSlot() {
        return scheduledInterviewTimeSlot;
    }

    public void setScheduledInterviewTimeSlot(InterviewTimeSlot scheduledInterviewTimeSlot) {
        this.scheduledInterviewTimeSlot = scheduledInterviewTimeSlot;
    }

    public Date getScheduledInterviewDate() {
        return scheduledInterviewDate;
    }

    public void setScheduledInterviewDate(Date scheduledInterviewDate) {
        this.scheduledInterviewDate = scheduledInterviewDate;
    }

    public Integer getChannel() {
        return channel;
    }

    public void setChannel(Integer channel) {
        this.channel = channel;
    }

    public Double getInterviewLocationLat() {
        return interviewLocationLat;
    }

    public void setInterviewLocationLat(Double interviewLocationLat) {
        this.interviewLocationLat = interviewLocationLat;
    }

    public Double getInterviewLocationLng() {
        return interviewLocationLng;
    }

    public void setInterviewLocationLng(Double interviewLocationLng) {
        this.interviewLocationLng = interviewLocationLng;
    }

    public boolean isPreScreenRequired() {
        return isPreScreenRequired;
    }

    public void setPreScreenRequired(boolean preScreenRequired) {
        isPreScreenRequired = preScreenRequired;
    }

    public RecruiterProfile getRecruiterProfile() {
        return recruiterProfile;
    }

    public void setRecruiterProfile(RecruiterProfile recruiterProfile) {
        this.recruiterProfile = recruiterProfile;
    }

    public Integer getInterviewRound() {
        return interviewRound;
    }

    public void setInterviewRound(Integer interviewRound) {
        this.interviewRound = interviewRound;
    }
}
