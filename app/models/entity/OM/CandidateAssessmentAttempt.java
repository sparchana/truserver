package models.entity.OM;

import com.avaje.ebean.Model;
import com.avaje.ebean.annotation.PrivateOwned;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import models.entity.Candidate;
import models.entity.JobPost;
import models.entity.Static.JobRole;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;

/**
 * Created by zero on 19/9/16.
 */

@Entity(name = "candidate_assessment_attempt")
@Table(name = "candidate_assessment_attempt")
public class CandidateAssessmentAttempt extends Model {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name = "ca_attempt_id", columnDefinition = "bigint signed", unique = true)
    private int attemptId;

    @ManyToOne
    @JsonBackReference
    @JoinColumn(name = "CandidateId", referencedColumnName = "CandidateId")
    private Candidate candidate;

    @ManyToOne
    @JsonBackReference
    @JoinColumn(name = "JobPostId", referencedColumnName = "JobPostId")
    private JobPost jobPost;

    @ManyToOne
    @JsonBackReference
    @JoinColumn(name = "JobRoleId", referencedColumnName = "JobRoleId")
    private JobRole jobRole;

    @Column(name = "result", columnDefinition = "double(1, 1) null")
    private Double result;

    @JsonManagedReference
    @PrivateOwned
    @OneToMany(mappedBy = "candidateAssessmentAttempt", cascade = CascadeType.ALL)
    private List<CandidateAssessmentResponse> candidateAssessmentResponseList;

    @Column(name = "create_timeStamp", columnDefinition = "timestamp not null default current_timestamp")
    private Timestamp createTimeStamp;

    @Column(name = "ca_attempt_uuid", columnDefinition = "varchar(255) not null default uuid()", nullable = false, unique = true)
    private String attemptUUID;

    public CandidateAssessmentAttempt(){
        this.attemptUUID = UUID.randomUUID().toString();
        this.createTimeStamp = new Timestamp(System.currentTimeMillis());
    }

    public static Model.Finder<String, CandidateAssessmentAttempt> find = new Model.Finder(CandidateAssessmentAttempt.class);

    public int getAttemptId() {
        return attemptId;
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

    public JobRole getJobRole() {
        return jobRole;
    }

    public void setJobRole(JobRole jobRole) {
        this.jobRole = jobRole;
    }

    public Double getResult() {
        return result;
    }

    public void setResult(Double result) {
        this.result = result;
    }

    public List<CandidateAssessmentResponse> getCandidateAssessmentResponseList() {
        return candidateAssessmentResponseList;
    }

    public void setCandidateAssessmentResponseList(List<CandidateAssessmentResponse> candidateAssessmentResponseList) {
        this.candidateAssessmentResponseList = candidateAssessmentResponseList;
    }

    public Timestamp getCreateTimeStamp() {
        return createTimeStamp;
    }

    public void setCreateTimeStamp(Timestamp createTimeStamp) {
        this.createTimeStamp = createTimeStamp;
    }

    public String getAttemptUUID() {
        return attemptUUID;
    }

    public void setAttemptUUID(String attemptUUID) {
        this.attemptUUID = attemptUUID;
    }
}
