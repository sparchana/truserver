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
@Entity(name = "jobpreference")
@Table(name = "jobpreference")
public class JobPreference extends Model {
    @Id
    @JsonBackReference
    @Column(name = "JobPreferenceId", columnDefinition = "int signed", nullable = false, unique = true)
    private int jobPreferenceId;

    @JsonBackReference
    @Column(name = "UpdateTimeStamp", columnDefinition = "timestamp null")
    private Timestamp updateTimeStamp = new Timestamp(System.currentTimeMillis());

    @ManyToOne
    @JsonManagedReference
    @JoinColumn(name = "JobRoleId", referencedColumnName = "JobRoleId")
    private JobRole jobRole;

    @ManyToOne
    @JsonBackReference
    @JoinColumn(name = "CandidateId", referencedColumnName= "CandidateId")
    private Candidate candidate;

    public static Finder<String, JobPreference> find = new Finder(JobPreference.class);

    public void setUpdateTimeStamp(Timestamp updateTimeStamp) {
        this.updateTimeStamp = updateTimeStamp;
    }

    public int getJobPreferenceId() {
        return jobPreferenceId;
    }

    public void setJobPreferenceId(int jobPreferenceId) {
        this.jobPreferenceId = jobPreferenceId;
    }

    public Timestamp getUpdateTimeStamp() {
        return updateTimeStamp;
    }

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
}
