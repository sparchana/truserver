package models.entity.OO;

import com.avaje.ebean.Model;
import com.avaje.ebean.annotation.UpdatedTimestamp;
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
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name = "CandidateCurrentJobId", columnDefinition = "bigint signed")
    private long candidateCurrentJobId;

    @Column(name = "CandidateCurrentCompany", columnDefinition = "varchar(100) null")
    private String candidateCurrentCompany;

    @Column(name = "CandidateCurrentDesignation", columnDefinition = "varchar(255) null")
    private String candidateCurrentDesignation;

    @Column(name = "CandidateCurrentSalary", columnDefinition = "bigint signed null")
    private Long candidateCurrentSalary;

    @Column(name = "CandidateCurrentJobDuration", columnDefinition = "int signed null")
    private Integer candidateCurrentJobDuration;

    @Column(name = "CandidateCurrentEmployerRefName", columnDefinition = "varchar(100) null")
    private String candidateCurrentEmployerRefName;

    @Column(name = "CandidateCurrentEmployerRefMobile", columnDefinition = "varchar(13) null")
    private String candidateCurrentEmployerRefMobile;

    @UpdatedTimestamp
    @Column(name = "UpdateTimeStamp", columnDefinition = "timestamp null")
    private Timestamp updateTimeStamp;

    @JsonBackReference
    @OneToOne(mappedBy = "candidateCurrentJobDetail")
    private Candidate candidate;

    @ManyToOne
    @JsonManagedReference
    @JoinColumn(name = "JobRoleId", referencedColumnName = "JobRoleId")
    private JobRole jobRole;

    @ManyToOne
    @JsonManagedReference
    @JoinColumn(name = "LocalityId", referencedColumnName = "LocalityId")
    private Locality candidateCurrentJobLocation;

    @ManyToOne
    @JsonManagedReference
    @JoinColumn(name = "TransportationModeId", referencedColumnName = "TransportationModeId")
    private TransportationMode candidateTransportationMode;

    @ManyToOne
    @JsonManagedReference
    @JoinColumn(name = "TimeShiftId", referencedColumnName = "TimeShiftId")
    private TimeShift candidateCurrentWorkShift;

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

    public long getCandidateCurrentJobId() {
        return candidateCurrentJobId;
    }

    public void setCandidateCurrentJobId(long candidateCurrentJobId) {
        this.candidateCurrentJobId = candidateCurrentJobId;
    }

    public String getCandidateCurrentCompany() {
        return candidateCurrentCompany;
    }

    public String getCandidateCurrentDesignation() {
        return candidateCurrentDesignation;
    }

    public Long getCandidateCurrentSalary() {
        return candidateCurrentSalary;
    }

    public Integer getCandidateCurrentJobDuration() {
        return candidateCurrentJobDuration;
    }

    public String getCandidateCurrentEmployerRefName() {
        return candidateCurrentEmployerRefName;
    }

    public String getCandidateCurrentEmployerRefMobile() {
        return candidateCurrentEmployerRefMobile;
    }

    public Timestamp getUpdateTimeStamp() {
        return updateTimeStamp;
    }

    public Candidate getCandidate() {
        return candidate;
    }

    public JobRole getJobRole() {
        return jobRole;
    }

    public Locality getCandidateCurrentJobLocation() {
        return candidateCurrentJobLocation;
    }

    public TransportationMode getCandidateTransportationMode() {
        return candidateTransportationMode;
    }

    public TimeShift getCandidateCurrentWorkShift() {
        return candidateCurrentWorkShift;
    }
}
