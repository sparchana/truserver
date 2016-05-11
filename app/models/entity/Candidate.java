package models.entity;

import com.avaje.ebean.Model;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import models.entity.OM.*;
import models.entity.OO.CandidateCurrentJobDetail;
import models.entity.OO.TimeShiftPreference;
import models.entity.Static.CandidateProfileStatus;
import models.entity.Static.Education;
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
    @Column(name = "CandidateId", columnDefinition = "bigint signed not null", unique = true)
    public long candidateId = 0;

    @Column(name = "candidateUUId", columnDefinition = "varchar(255) not null", nullable = false, unique = true)
    public String candidateUUId;

    @Column(name = "LeadId", columnDefinition = "bigint signed not null", unique = true)
    public long leadId;

    @Column(name = "CandidateName", columnDefinition = "varchar(50) not null")
    public String candidateName;

    @Column(name = "CandidateLastName", columnDefinition = "varchar(50) null")
    public String candidateLastName;

    @Column(name = "CandidateGender", columnDefinition = "int(1) null default 0")
    public int candidateGender;

    @Column(name = "CandidateDOB", columnDefinition = "timestamp null")
    public Timestamp candidateDOB;

    @Column(name = "CandidateMobile", columnDefinition = "varchar(13) not null")
    public String candidateMobile;

    @Column(name = "CandidatePhoneType", columnDefinition = "varchar(100) null")
    public String candidatePhoneType;

    @Column(name = "CandidateMaritalStatus", columnDefinition = "int null")
    public int candidateMaritalStatus;

    @Column(name = "CandidateEmail", columnDefinition = "varchar(255) null")
    public String candidateEmail;

    @Column(name = "CandidateIsEmployed", columnDefinition = "int not null")
    public int candidateIsEmployed;

    @Column(name = "CandidateTotalExperience", columnDefinition = "decimal(3,2) signed null default 0.00")
    public float candidateTotalExperience;  // data in years

    @Column(name = "CandidateAge", columnDefinition = "int signed not null default 0")
    public int candidateAge;

    @Column(name = "CandidateCreateTimestamp", columnDefinition = "timestamp default current_timestamp not null")
    public Timestamp candidateCreateTimestamp = new Timestamp(System.currentTimeMillis());

    @Column(name = "CandidateUpdateTimestamp", columnDefinition = "timestamp null")
    public Timestamp candidateUpdateTimestamp;

    @Column(name = "CandidateIsAssessed", columnDefinition = "int signed not null default 0")
    public int candidateIsAssessed;

    @Column(name = "CandidateSalarySlip", columnDefinition = "int signed not null default 0")
    public int candidateSalarySlip;

    @Column(name = "CandidateAppointmentLetter", columnDefinition = "int signed not null default 0")
    public int candidateAppointmentLetter;

    @Column(name = "IsMinProfileComplete", columnDefinition = "int signed not null default 0")
    public int IsMinProfileComplete = 0; // 0 - Not Complete

    @JsonManagedReference
    @OneToMany(mappedBy = "candidate", cascade = CascadeType.ALL)
    public List<IDProofreference> idProofreferenceList;

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
    @OneToOne(mappedBy = "candidate", cascade = CascadeType.ALL)
    public CandidateCurrentJobDetail candidateCurrentJobDetail;

    @JsonManagedReference
    @OneToOne(mappedBy = "candidate", cascade = CascadeType.ALL)
    public TimeShiftPreference timeShiftPreference;

    @JsonManagedReference
    @ManyToOne
    @JoinColumn(name = "CandidateMotherTongue", referencedColumnName = "languageId")
    public Language motherTongue;

    @ManyToOne
    @JsonManagedReference
    @JoinColumn(name = "CandidateHomeLocality")
    public Locality locality;

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JsonManagedReference
    @JoinColumn(name = "CandidateStatusId", referencedColumnName = "profileStatusId")
    public CandidateProfileStatus candidateprofilestatus;

    @ManyToOne
    @JsonManagedReference
    @JoinColumn(name = "EducationId", referencedColumnName = "EducationId")
    public Education education;

    public static Finder<String, Candidate> find = new Finder(Candidate.class);

    public static void registerCandidate(Candidate candidate) {
        Logger.info("inside signup method" );
        candidate.save();
    }

    public static void candidateUpdate(Candidate candidate) {
        Logger.info("inside Candidate Update method" );
        candidate.update();
    }

    public void setCandidateDOB(Timestamp candidateDOB) {
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
}


