package models.entity;

import com.avaje.ebean.Model;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import models.entity.OM.*;
import models.entity.OO.CandidateCurrentJobDetail;
import models.entity.OO.CandidateEducation;
import models.entity.OO.TimeShiftPreference;
import models.entity.Static.CandidateProfileStatus;
import models.entity.Static.Language;
import models.entity.Static.Locality;
import play.Logger;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

/**
 * Created by batcoder1 on 19/4/16.
 */

@Entity(name = "candidate")
@Table(name = "candidate")
public class Candidate extends Model {
    @Id
    @Column(name = "CandidateId", columnDefinition = "bigint signed null", unique = true)
    public long candidateId = 0;

    @Column(name = "candidateUUId", columnDefinition = "varchar(255) not null", nullable = false, unique = true)
    public String candidateUUId;

    @Column(name = "CandidateName", columnDefinition = "varchar(50) not null")
    public String candidateName;

    @Column(name = "CandidateLastName", columnDefinition = "varchar(50) null")
    public String candidateLastName;

    @Column(name = "CandidateGender", columnDefinition = "int(1) null")
    public Integer candidateGender;

    @Column(name = "CandidateDOB", columnDefinition = "date null")
    public Date candidateDOB;

    @Column(name = "CandidateMobile", columnDefinition = "varchar(13) not null")
    public String candidateMobile;

    @Column(name = "CandidatePhoneType", columnDefinition = "varchar(100) null")
    public String candidatePhoneType;

    @Column(name = "CandidateMaritalStatus", columnDefinition = "int null")
    public Integer candidateMaritalStatus;

    @Column(name = "CandidateEmail", columnDefinition = "varchar(255) null")
    public String candidateEmail;

    @Column(name = "CandidateIsEmployed", columnDefinition = "int null")
    public Integer candidateIsEmployed;

    @Column(name = "CandidateTotalExperience", columnDefinition = "int signed null")
    public Integer candidateTotalExperience;  // data in months

    @Column(name = "CandidateAge", columnDefinition = "int signed null")
    public Integer candidateAge;

    @Column(name = "CandidateCreateTimestamp", columnDefinition = "timestamp default current_timestamp not null")
    public Timestamp candidateCreateTimestamp = new Timestamp(System.currentTimeMillis());

    @Column(name = "CandidateUpdateTimestamp", columnDefinition = "timestamp null")
    public Timestamp candidateUpdateTimestamp;

    @Column(name = "CandidateIsAssessed", columnDefinition = "int signed not null default 0")
    public int candidateIsAssessed;

    @Column(name = "CandidateSalarySlip", columnDefinition = "int signed null")
    public Integer candidateSalarySlip;

    @Column(name = "CandidateAppointmentLetter", columnDefinition = "int signed null")
    public Integer candidateAppointmentLetter;

    @Column(name = "IsMinProfileComplete", columnDefinition = "int signed not null default 0")
    public int IsMinProfileComplete = 0; // 0 - Not Complete

    @JsonManagedReference
    @OneToMany(mappedBy = "candidate", cascade = CascadeType.ALL)
    public List<IDProofReference> idProofReferenceList;

    @JsonManagedReference
    @OneToMany(mappedBy = "candidate", cascade = CascadeType.ALL)
    public List<JobHistory> jobHistoryList;

    @JsonManagedReference
    @OneToMany(mappedBy = "candidate", cascade = CascadeType.ALL)
    public List<JobPreference> jobPreferencesList;

    @JsonManagedReference
    @OneToMany(mappedBy = "candidate", cascade = CascadeType.ALL)
    public List<LanguageKnown> languageKnownList;

    @JsonManagedReference
    @OneToMany(mappedBy = "candidate", cascade = CascadeType.ALL)
    public List<LocalityPreference> localityPreferenceList;

    @JsonManagedReference
    @OneToMany(mappedBy = "candidate", cascade = CascadeType.ALL)
    public List<CandidateSkill> candidateSkillList;

    @JsonManagedReference
    @OneToOne(mappedBy = "candidate", cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    public CandidateCurrentJobDetail candidateCurrentJobDetail;

    @JsonManagedReference
    @OneToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    public Lead lead;

    @JsonManagedReference
    @OneToOne(mappedBy = "candidate", cascade = CascadeType.ALL)
    public TimeShiftPreference timeShiftPreference;

    @JsonManagedReference
    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "CandidateMotherTongue", referencedColumnName = "languageId")
    public Language motherTongue;

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JsonManagedReference
    @JoinColumn(name = "CandidateHomeLocality")
    public Locality locality;

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JsonManagedReference
    @JoinColumn(name = "CandidateStatusId", referencedColumnName = "profileStatusId")
    public CandidateProfileStatus candidateprofilestatus;

    @OneToOne(mappedBy = "candidate", cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    public CandidateEducation candidateEducation;

    public static Finder<String, Candidate> find = new Finder(Candidate.class);

    public void registerCandidate() {
        Logger.info("inside signup method" );
        this.save();
    }

    public void candidateUpdate() {
        Logger.info("inside Candidate Update method" );
        this.update();
    }

    public void setCandidateDOB(Date candidateDOB) {
        // calculate age and save that too
        this.candidateDOB = candidateDOB;
    }

    public void setCandidateprofilestatus(CandidateProfileStatus candidateprofilestatus) {
        this.candidateprofilestatus = candidateprofilestatus;
    }

    public void setCandidateId(long candidateId) {
        this.candidateId = candidateId;
    }

    public void setCandidateName(String candidateName) {
        this.candidateName = candidateName;
    }

    public void setCandidateLastName(String candidateLastName) {
        this.candidateLastName = candidateLastName;
    }

    public void setCandidateGender(int candidateGender) {
        this.candidateGender = candidateGender;
    }

    public void setCandidateMobile(String candidateMobile) {
        this.candidateMobile = candidateMobile;
    }

    public void setCandidatePhoneType(String candidatePhoneType) {
        this.candidatePhoneType = candidatePhoneType;
    }

    public void setCandidateMaritalStatus(int candidateMaritalStatus) {
        this.candidateMaritalStatus = candidateMaritalStatus;
    }

    public void setCandidateEmail(String candidateEmail) {
        this.candidateEmail = candidateEmail;
    }

    public void setCandidateIsEmployed(int candidateIsEmployed) {
        this.candidateIsEmployed = candidateIsEmployed;
    }

    public void setCandidateTotalExperience(int candidateTotalExperience) {
        this.candidateTotalExperience = candidateTotalExperience;
    }

    public void setCandidateAge(int candidateAge) {
        this.candidateAge = candidateAge;
    }

    public void setCandidateCreateTimestamp(Timestamp candidateCreateTimestamp) {
        this.candidateCreateTimestamp = candidateCreateTimestamp;
    }

    public void setCandidateUpdateTimestamp(Timestamp candidateUpdateTimestamp) {
        this.candidateUpdateTimestamp = candidateUpdateTimestamp;
    }

    public void setCandidateIsAssessed(int candidateIsAssessed) {
        this.candidateIsAssessed = candidateIsAssessed;
    }

    public void setCandidateSalarySlip(int candidateSalarySlip) {
        this.candidateSalarySlip = candidateSalarySlip;
    }

    public void setCandidateAppointmentLetter(int candidateAppointmentLetter) {
        this.candidateAppointmentLetter = candidateAppointmentLetter;
    }

    public void setIsMinProfileComplete(int isMinProfileComplete) {
        IsMinProfileComplete = isMinProfileComplete;
    }

    public void setIdProofReferenceList(List<IDProofReference> idProofReferenceList) {
        this.idProofReferenceList = idProofReferenceList;
    }

    public void setJobHistoryList(List<JobHistory> jobHistoryList) {
        this.jobHistoryList = jobHistoryList;
    }

    public void setJobPreferencesList(List<JobPreference> jobPreferencesList) {
        this.jobPreferencesList = jobPreferencesList;
    }

    public void setLanguageKnownList(List<LanguageKnown> languageKnownList) {
        this.languageKnownList = languageKnownList;
    }

    public void setLocalityPreferenceList(List<LocalityPreference> localityPreferenceList) {
        this.localityPreferenceList = localityPreferenceList;
    }

    public void setCandidateSkillList(List<CandidateSkill> candidateSkillList) {
        this.candidateSkillList = candidateSkillList;
    }

    public void setCandidateCurrentJobDetail(CandidateCurrentJobDetail candidateCurrentJobDetail) {
        this.candidateCurrentJobDetail = candidateCurrentJobDetail;
    }

    public void setTimeShiftPreference(TimeShiftPreference timeShiftPreference) {
        this.timeShiftPreference = timeShiftPreference;
    }

    public void setMotherTongue(Language motherTongue) {
        this.motherTongue = motherTongue;
    }

    public void setLocality(Locality locality) {
        this.locality = locality;
    }

    public void setCandidateEducation(CandidateEducation candidateEducation) {
        this.candidateEducation = candidateEducation;
    }

    public void setCandidateUUId(String candidateUUId) {
        this.candidateUUId = candidateUUId;
    }

    public void setLead(Lead lead) {
        this.lead = lead;
    }
}


