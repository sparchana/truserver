package models.entity;

import com.avaje.ebean.Model;
import com.avaje.ebean.annotation.PrivateOwned;
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
import java.util.UUID;

/**
 * Created by batcoder1 on 19/4/16.
 */

@Entity(name = "candidate")
@Table(name = "candidate")
public class Candidate extends Model {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name = "CandidateId", columnDefinition = "bigint signed", unique = true)
    private long candidateId = 0;

    @Column(name = "candidateUUId", columnDefinition = "varchar(255) not null", nullable = false, unique = true)
    private String candidateUUId;

    @Column(name = "CandidateName", columnDefinition = "varchar(50) not null")
    private String candidateFirstName;

    @Column(name = "CandidateLastName", columnDefinition = "varchar(50) null")
    private String candidateLastName;

    @Column(name = "CandidateGender", columnDefinition = "int(1) null")
    private Integer candidateGender;

    @Column(name = "CandidateDOB", columnDefinition = "date null")
    private Date candidateDOB;

    @Column(name = "CandidateMobile", columnDefinition = "varchar(13) not null")
    private String candidateMobile;

    @Column(name = "CandidatePhoneType", columnDefinition = "varchar(100) null")
    private String candidatePhoneType;

    @Column(name = "CandidateMaritalStatus", columnDefinition = "int null")
    private Integer candidateMaritalStatus;

    @Column(name = "CandidateEmail", columnDefinition = "varchar(255) null")
    private String candidateEmail;

    @Column(name = "CandidateIsEmployed", columnDefinition = "int null")
    private Integer candidateIsEmployed;

    @Column(name = "CandidateTotalExperience", columnDefinition = "int signed null")
    private Integer candidateTotalExperience;  // data in months

    @Column(name = "CandidateAge", columnDefinition = "int signed null")
    private Integer candidateAge;

    @Column(name = "CandidateCreateTimestamp", columnDefinition = "timestamp not null")
    private Timestamp candidateCreateTimestamp;

    @Column(name = "CandidateUpdateTimestamp", columnDefinition = "timestamp null")
    private Timestamp candidateUpdateTimestamp;

    @Column(name = "CandidateIsAssessed", columnDefinition = "int signed not null default 0")
    private int candidateIsAssessed;

    @Column(name = "CandidateSalarySlip", columnDefinition = "int signed null")
    private Integer candidateSalarySlip;

    @Column(name = "CandidateAppointmentLetter", columnDefinition = "int signed null")
    private Integer candidateAppointmentLetter;

    @Column(name = "IsMinProfileComplete", columnDefinition = "int signed not null default 0")
    private int IsMinProfileComplete = 0; // 0 - Not Complete

    @JsonManagedReference
    @PrivateOwned
    @OneToMany(mappedBy = "candidate", cascade = CascadeType.ALL)
    private List<IDProofReference> idProofReferenceList;

    @JsonManagedReference
    @PrivateOwned
    @OneToMany(mappedBy = "candidate", cascade = CascadeType.ALL)
    private List<JobHistory> jobHistoryList;

    @JsonManagedReference
    @PrivateOwned
    @OneToMany(mappedBy = "candidate", cascade = CascadeType.ALL)
    private List<JobPreference> jobPreferencesList;

    @JsonManagedReference
    @PrivateOwned
    @OneToMany(mappedBy = "candidate", cascade = CascadeType.ALL)
    private List<LanguageKnown> languageKnownList;

    @JsonManagedReference
    @PrivateOwned
    @OneToMany(mappedBy = "candidate", cascade = CascadeType.ALL)
    private List<LocalityPreference> localityPreferenceList;

    @JsonManagedReference
    @PrivateOwned
    @OneToMany(mappedBy = "candidate", cascade = CascadeType.ALL)
    private List<CandidateSkill> candidateSkillList;

    @JsonManagedReference
    @PrivateOwned
    @OneToMany(mappedBy = "candidate", cascade = CascadeType.ALL)
    private List<CandidateExp> candidateExpList;

    @JsonManagedReference
    @OneToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private Lead lead;

    @JsonManagedReference
    @JoinColumn(name = "candidateCurrentJobId", referencedColumnName = "candidateCurrentJobId")
    @OneToOne(cascade = CascadeType.ALL)
    private CandidateCurrentJobDetail candidateCurrentJobDetail;

    @JsonManagedReference
    @JoinColumn(name = "candidateEducationId", referencedColumnName = "candidateEducationId")
    @OneToOne(cascade = CascadeType.ALL)
    private CandidateEducation candidateEducation;

    @JsonManagedReference
    @JoinColumn(name = "timeShiftPreferenceId", referencedColumnName = "timeShiftPreferenceId")
    @OneToOne(cascade = CascadeType.ALL)
    private TimeShiftPreference timeShiftPreference;

    @JsonManagedReference
    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "CandidateMotherTongue", referencedColumnName = "languageId")
    private Language motherTongue;

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JsonManagedReference
    @JoinColumn(name = "CandidateHomeLocality")
    private Locality locality;

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JsonManagedReference
    @JoinColumn(name = "CandidateStatusId", referencedColumnName = "profileStatusId")
    private CandidateProfileStatus candidateprofilestatus;


    public static Finder<String, Candidate> find = new Finder(Candidate.class);

    public Candidate() {
        this.candidateUUId = UUID.randomUUID().toString();
        this.candidateCreateTimestamp = new Timestamp(System.currentTimeMillis());
        this.candidateUpdateTimestamp = new Timestamp(System.currentTimeMillis());
    }

    public void registerCandidate() {
        Logger.info("inside registerCandidate(), Candidate registered/saved" );
        this.save();
    }

    public void candidateUpdate() {
        Logger.info("inside CandidateUpdate(), Candidate updated" );
        this.candidateUpdateTimestamp = new Timestamp(System.currentTimeMillis());
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

    public void setCandidateFirstName(String candidateFirstName) {
        this.candidateFirstName = candidateFirstName;
    }

    public void setCandidateLastName(String candidateLastName) {
        this.candidateLastName = candidateLastName;
    }

    public void setCandidateGender(Integer candidateGender) {
        this.candidateGender = candidateGender;
    }

    public void setCandidateMobile(String candidateMobile) {
        this.candidateMobile = candidateMobile;
    }

    public void setCandidatePhoneType(String candidatePhoneType) {
        this.candidatePhoneType = candidatePhoneType;
    }

    public void setCandidateMaritalStatus(Integer candidateMaritalStatus) {
        this.candidateMaritalStatus = candidateMaritalStatus;
    }

    public void setCandidateEmail(String candidateEmail) {
        this.candidateEmail = candidateEmail;
    }

    public void setCandidateIsEmployed(Integer candidateIsEmployed) {
        this.candidateIsEmployed = candidateIsEmployed;
    }

    public void setCandidateTotalExperience(Integer candidateTotalExperience) {
        this.candidateTotalExperience = candidateTotalExperience;
    }

    public void setCandidateAge(Integer candidateAge) {
        this.candidateAge = candidateAge;
    }

    public void setCandidateCreateTimestamp(Timestamp candidateCreateTimestamp) {
        this.candidateCreateTimestamp = candidateCreateTimestamp;
    }

    public void setCandidateUpdateTimestamp(Timestamp candidateUpdateTimestamp) {
        this.candidateUpdateTimestamp = candidateUpdateTimestamp;
    }

    public void setCandidateIsAssessed(Integer candidateIsAssessed) {
        this.candidateIsAssessed = candidateIsAssessed;
    }

    public void setCandidateSalarySlip(Integer candidateSalarySlip) {
        this.candidateSalarySlip = candidateSalarySlip;
    }

    public void setCandidateAppointmentLetter(Integer candidateAppointmentLetter) {
        this.candidateAppointmentLetter = candidateAppointmentLetter;
    }

    public void setIsMinProfileComplete(Integer isMinProfileComplete) {
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

    public long getCandidateId() {
        return candidateId;
    }

    public String getCandidateUUId() {
        return candidateUUId;
    }

    public String getCandidateFirstName() {
        return candidateFirstName;
    }

    public String getCandidateLastName() {
        return candidateLastName;
    }

    public Integer getCandidateGender() {
        return candidateGender;
    }

    public Date getCandidateDOB() {
        return candidateDOB;
    }

    public String getCandidateMobile() {
        return candidateMobile;
    }

    public String getCandidatePhoneType() {
        return candidatePhoneType;
    }

    public Integer getCandidateMaritalStatus() {
        return candidateMaritalStatus;
    }

    public String getCandidateEmail() {
        return candidateEmail;
    }

    public Integer getCandidateIsEmployed() {
        return candidateIsEmployed;
    }

    public Integer getCandidateTotalExperience() {
        return candidateTotalExperience;
    }

    public Integer getCandidateAge() {
        return candidateAge;
    }

    public Timestamp getCandidateCreateTimestamp() {
        return candidateCreateTimestamp;
    }

    public Timestamp getCandidateUpdateTimestamp() {
        return candidateUpdateTimestamp;
    }

    public int getCandidateIsAssessed() {
        return candidateIsAssessed;
    }

    public void setCandidateIsAssessed(int candidateIsAssessed) {
        this.candidateIsAssessed = candidateIsAssessed;
    }

    public Integer getCandidateSalarySlip() {
        return candidateSalarySlip;
    }

    public Integer getCandidateAppointmentLetter() {
        return candidateAppointmentLetter;
    }

    public int getIsMinProfileComplete() {
        return IsMinProfileComplete;
    }

    public void setIsMinProfileComplete(int isMinProfileComplete) {
        IsMinProfileComplete = isMinProfileComplete;
    }

    public List<IDProofReference> getIdProofReferenceList() {
        return idProofReferenceList;
    }

    public List<JobHistory> getJobHistoryList() {
        return jobHistoryList;
    }

    public List<JobPreference> getJobPreferencesList() {
        return jobPreferencesList;
    }

    public List<LanguageKnown> getLanguageKnownList() {
        return languageKnownList;
    }

    public List<LocalityPreference> getLocalityPreferenceList() {
        return localityPreferenceList;
    }

    public List<CandidateSkill> getCandidateSkillList() {
        return candidateSkillList;
    }

    public CandidateCurrentJobDetail getCandidateCurrentJobDetail() {
        return candidateCurrentJobDetail;
    }

    public Lead getLead() {
        return lead;
    }

    public TimeShiftPreference getTimeShiftPreference() {
        return timeShiftPreference;
    }

    public Language getMotherTongue() {
        return motherTongue;
    }

    public Locality getLocality() {
        return locality;
    }

    public CandidateProfileStatus getCandidateprofilestatus() {
        return candidateprofilestatus;
    }

    public CandidateEducation getCandidateEducation() {
        return candidateEducation;
    }

}


