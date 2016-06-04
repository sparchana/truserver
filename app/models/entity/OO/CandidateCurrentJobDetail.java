package models.entity.OO;

import com.avaje.ebean.Model;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import models.entity.Candidate;
import models.entity.Static.JobRole;
import models.entity.Static.Locality;
import models.entity.Static.TimeShift;
import models.entity.Static.TransportationMode;

import javax.persistence.*;
import java.sql.Timestamp;

/**
 * Created by zero on 4/5/16.
 */
@Entity(name = "candidatecurrentjobdetail")
@Table(name = "candidatecurrentjobdetail")
public class CandidateCurrentJobDetail extends Model{
    @Id
    @Column(name = "CandidateCurrentJobId", columnDefinition = "bigint signed not null", unique = true)
    public long candidateCurrentJobId = 0;

    @Column(name = "CandidateCurrentCompany", columnDefinition = "varchar(100) null")
    public String candidateCurrentCompany;

    @Column(name = "CandidateCurrentDesignation", columnDefinition = "varchar(255) null")
    public String candidateCurrentDesignation;

    @Column(name = "CandidateCurrentSalary", columnDefinition = "bigint signed null")
    public Long candidateCurrentSalary;

    @Column(name = "CandidateCurrentJobDuration", columnDefinition = "int signed null")
    public Integer candidateCurrentJobDuration;

    @Column(name = "CandidateCurrentEmployerRefName", columnDefinition = "varchar(100) null")
    public String candidateCurrentEmployerRefName;

    @Column(name = "CandidateCurrentEmployerRefMobile", columnDefinition = "varchar(13) null")
    public String candidateCurrentEmployerRefMobile;

    @Column(name = "UpdateTimeStamp", columnDefinition = "timestamp default current_timestamp null")
    public Timestamp updateTimeStamp;

    @OneToOne
    @JsonBackReference
    @JoinColumn(name = "CandidateId", referencedColumnName = "CandidateId")
    public Candidate candidate;

    @ManyToOne
    @JsonManagedReference
    @JoinColumn(name = "JobRoleId", referencedColumnName = "JobRoleId")
    public JobRole jobRole;

    @ManyToOne
    @JsonManagedReference
    @JoinColumn(name = "LocalityId", referencedColumnName = "LocalityId")
    public Locality candidateCurrentJobLocation;

    @ManyToOne
    @JsonManagedReference
    @JoinColumn(name = "TransportationModeId", referencedColumnName = "TransportationModeId")
    public TransportationMode candidateTransportationMode;

    @ManyToOne
    @JsonManagedReference
    @JoinColumn(name = "TimeShiftId", referencedColumnName = "TimeShiftId")
    public TimeShift candidateCurrentWorkShift;

    public static Finder<String, CandidateCurrentJobDetail> find = new Finder(CandidateCurrentJobDetail.class);

    public void setCandidateCurrentCompany(String candidateCurrentCompany) {
        this.candidateCurrentCompany = candidateCurrentCompany;
    }

    public void setCandidateCurrentJobLocation(Locality candidateCurrentJobLocation) {
        this.candidateCurrentJobLocation = candidateCurrentJobLocation;
    }

    public void setCandidateTransportationMode(TransportationMode candidateTransportationMode) {
        this.candidateTransportationMode = candidateTransportationMode;
    }

    public void setCandidateCurrentWorkShift(TimeShift candidateCurrentWorkShift) {
        this.candidateCurrentWorkShift = candidateCurrentWorkShift;
    }

    public void setCandidateCurrentDesignation(String candidateCurrentDesignation) {
        this.candidateCurrentDesignation = candidateCurrentDesignation;
    }

    public void setCandidateCurrentSalary(Long candidateCurrentSalary) {
        this.candidateCurrentSalary = candidateCurrentSalary;
    }

    public void setCandidateCurrentJobDuration(Integer candidateCurrentJobDuration) {
        this.candidateCurrentJobDuration = candidateCurrentJobDuration;
    }

    public void setCandidateCurrentEmployerRefName(String candidateCurrentEmployerRefName) {
        this.candidateCurrentEmployerRefName = candidateCurrentEmployerRefName;
    }

    public void setCandidateCurrentEmployerRefMobile(String candidateCurrentEmployerRefMobile) {
        this.candidateCurrentEmployerRefMobile = candidateCurrentEmployerRefMobile;
    }

    public void setUpdateTimeStamp(Timestamp updateTimeStamp) {
        this.updateTimeStamp = updateTimeStamp;
    }

    public void setCandidate(Candidate candidate) {
        this.candidate = candidate;
    }

    public void setJobRole(JobRole jobRole) {
        this.jobRole = jobRole;
    }
}
