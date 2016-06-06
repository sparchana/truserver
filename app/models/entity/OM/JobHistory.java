package models.entity.OM;

import com.avaje.ebean.Model;
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
    @Column(name = "JobHistoryId", columnDefinition = "bigint signed not null", unique = true)
    public long jobHistoryId = 0;

    @Column(name = "CandidatePastCompany", columnDefinition = "varchar(255) null")
    public String candidatePastCompany = "";

    @Column(name = "CandidatePastSalary", columnDefinition = "bigint signed null")
    public Long candidatePastSalary;

    @Column(name = "UpdateTimeStamp", columnDefinition = "timestamp default current_timestamp null")
    public Timestamp updateTimeStamp;

    @ManyToOne
    @JsonManagedReference
    @JoinColumn(name = "JobRoleId", referencedColumnName = "JobRoleId")
    public JobRole jobRole;

    @ManyToOne
    @JsonBackReference
    @JoinColumn(name = "CandidateId", referencedColumnName = "CandidateId")
    public Candidate candidate;

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

    public static Finder<String, JobHistory> find = new Finder(JobHistory.class);

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
}
