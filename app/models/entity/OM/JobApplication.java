package models.entity.OM;

import com.avaje.ebean.Model;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import models.entity.Candidate;
import models.entity.JobPost;

import javax.persistence.*;
import java.sql.Timestamp;

/**
 * Created by batcoder1 on 16/6/16.
 */
@Entity(name = "jobapplication")
@Table(name = "jobapplication")
public class JobApplication extends Model {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name = "JobApplicationId", columnDefinition = "int signed", unique = true)
    private Integer jobApplicationId;

    @JsonBackReference
    @Column(name = "JobApplicationCreateTimeStamp", columnDefinition = "timestamp null")
    private Timestamp jobApplicationCreateTimeStamp = new Timestamp(System.currentTimeMillis());

    @ManyToOne
    @JsonManagedReference
    @JoinColumn(name = "JobPostId", referencedColumnName = "JobPostId")
    private JobPost jobPost;

    @ManyToOne
    @JsonBackReference
    @JoinColumn(name = "CandidateId", referencedColumnName = "CandidateId")
    private Candidate candidate;

    public static Model.Finder<String, JobApplication> find = new Model.Finder(JobApplication.class);

    public Integer getJobApplicationId() {
        return jobApplicationId;
    }

    public void setJobApplicationId(Integer jobApplicationId) {
        this.jobApplicationId = jobApplicationId;
    }

    public JobPost getJobPost() {
        return jobPost;
    }

    public void setJobPost(JobPost jobPost) {
        this.jobPost = jobPost;
    }

    public Timestamp getJobApplicationCreateTimeStamp() {
        return jobApplicationCreateTimeStamp;
    }

    public void setJobApplicationCreateTimeStamp(Timestamp jobApplicationCreateTimeStamp) {
        this.jobApplicationCreateTimeStamp = jobApplicationCreateTimeStamp;
    }

    public Candidate getCandidate() {
        return candidate;
    }

    public void setCandidate(Candidate candidate) {
        this.candidate = candidate;
    }
}
