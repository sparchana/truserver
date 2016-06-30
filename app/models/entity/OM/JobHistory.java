package models.entity.OM;

import com.avaje.ebean.Model;
import com.avaje.ebean.annotation.UpdatedTimestamp;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import models.entity.Candidate;
import models.entity.Static.JobRole;

import javax.persistence.*;
import java.sql.Timestamp;

/**
 * Created by zero on 4/5/16.
 */
@Entity(name = "jobhistory")
@Table(name = "jobhistory")
public class JobHistory extends Model {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name = "JobHistoryId", columnDefinition = "bigint signed", unique = true)
    private long jobHistoryId = 0;

    @Column(name = "CandidatePastCompany", columnDefinition = "varchar(255) null")
    private String candidatePastCompany = "";

    @Column(name = "CandidatePastSalary", columnDefinition = "bigint signed null")
    private Long candidatePastSalary;

    @UpdatedTimestamp
    @Column(name = "UpdateTimeStamp", columnDefinition = "timestamp null")
    private Timestamp updateTimeStamp;

    @ManyToOne
    @JsonManagedReference
    @JoinColumn(name = "JobRoleId", referencedColumnName = "JobRoleId")
    private JobRole jobRole;

    @ManyToOne
    @JsonBackReference
    @JoinColumn(name = "CandidateId", referencedColumnName = "CandidateId")
    private Candidate candidate;

    @Column(name = "CurrentJob", columnDefinition = "bit null")
    private Boolean currentJob;

    public static Finder<String, JobHistory> find = new Finder(JobHistory.class);

    public JobRole getJobRole() {
        return jobRole;
    }

    public void setJobRole(JobRole jobRole) {
        this.jobRole = jobRole;
    }

    public Candidate getCandidate() {
        return candidate;
    }

    public void setCandidate(Candidate candidate) {
        this.candidate = candidate;
    }

    public void setUpdateTimeStamp(Timestamp updateTimeStamp) {
        this.updateTimeStamp = updateTimeStamp;
    }

    public String getCandidatePastCompany() {
        return candidatePastCompany;
    }

    public void setCandidatePastCompany(String candidatePastCompany) {
        this.candidatePastCompany = candidatePastCompany;
    }

    public Long getCandidatePastSalary() {
        return candidatePastSalary;
    }

    public void setCandidatePastSalary(Long candidatePastSalary) {
        this.candidatePastSalary = candidatePastSalary;
    }

    public Boolean getCurrentJob() {
        return currentJob;
    }

    public void setCurrentJob(Boolean currentJob) {
        this.currentJob = currentJob;
    }
}
