package models.entity.Recruiter;

import com.avaje.ebean.Model;
import com.avaje.ebean.annotation.PrivateOwned;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import models.entity.Company;
import models.entity.RecruiterCreditHistory;
import models.entity.JobPost;
import models.entity.Recruiter.Static.RecruiterProfileStatus;
import models.entity.Recruiter.Static.RecruiterStatus;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;

/**
 * Created by batcoder1 on 21/6/16.
 */
@Entity(name = "recruiterprofile")
@Table(name = "recruiterprofile")
public class RecruiterProfile extends Model {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name = "RecruiterProfileId", columnDefinition = "bigint signed", unique = true)
    private Long recruiterProfileId;

    @Column(name = "RecruiterProfileUUId", columnDefinition = "varchar(255) not null")
    private String recruiterProfileUUId;

    @Column(name = "RecruiterProfileName", columnDefinition = "varchar(50) not null")
    private String recruiterProfileName;

    @Column(name = "RecruiterProfileMobile", columnDefinition = "varchar(13) not null")
    private String recruiterProfileMobile;

    @Column(name = "RecruiterProfileLandline", columnDefinition = "varchar(13) not null")
    private String recruiterProfileLandline;

    @Column(name = "RecruiterProfilePin", columnDefinition = "int signed null")
    private Long recruiterProfilePin;

    @Column(name = "RecruiterProfileEmail", columnDefinition = "varchar(255) null")
    private String recruiterProfileEmail;

    @Column(name = "RecruiterProfileCreateTimestamp", columnDefinition = "timestamp not null default current_timestamp")
    private Timestamp recruiterProfileCreateTimestamp;

    @Column(name = "RecruiterAlternateMobile", columnDefinition = "varchar(13) null")
    private String recruiterAlternateMobile;

    @Column(name = "RecruiterDesignation", columnDefinition = "varchar(50) null")
    private String recruiterDesignation;

    @Column(name = "RecruiterLinkedinProfile", columnDefinition = "varchar(60) null")
    private String recruiterLinkedinProfile;

    @Column(name = "RecruiterOfficeAddress", columnDefinition = "varchar(500) null")
    private String recruiterOfficeAddress;

    @Column(name = "RecruiterEmailStatus", columnDefinition = "int signed not null default 0")
    private int recruiterEmailStatus; // verified, Not-Yet-Verified

    @Column(name = "RecruiterInterviewUnlockCredits", columnDefinition = "int signed null")
    private Integer recruiterInterviewUnlockCredits;

    @Column(name = "RecruiterCandidateUnlockCredits", columnDefinition = "int signed null")
    private Integer recruiterCandidateUnlockCredits;

    @ManyToOne
    @JsonManagedReference
    @JoinColumn(name = "RecStatus")
    private RecruiterStatus recStatus;

    @JsonBackReference
    @PrivateOwned
    @OneToMany(mappedBy = "recruiterJobPost", cascade = CascadeType.ALL)
    private JobPost jobPost;

    @ManyToOne
    @JsonManagedReference
    @JoinColumn(name = "RecCompany")
    private Company company;

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JsonManagedReference
    @JoinColumn(name = "profile_status_id", referencedColumnName = "profile_status_id")
    private RecruiterProfileStatus recruiterprofilestatus;

    @JsonManagedReference
    @OneToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private RecruiterLead recruiterLead;

    @JsonManagedReference
    @PrivateOwned
    @OneToMany(mappedBy = "recruiterProfile", cascade = CascadeType.REMOVE)
    private List<RecruiterCreditHistory> recruiterCreditHistoryList;

    @JsonManagedReference
    @PrivateOwned
    @OneToMany(mappedBy = "recruiterProfile", cascade = CascadeType.REMOVE)
    private List<RecruiterPayment> recruiterPaymentList;

    public static Finder<String, RecruiterProfile> find = new Finder(RecruiterProfile.class);

    public RecruiterProfile() {
        this.recruiterProfileUUId = UUID.randomUUID().toString();
        this.recruiterProfileCreateTimestamp = new Timestamp(System.currentTimeMillis());
    }

    public Company getRecCompany() {
        return company;
    }

    public void setRecCompany(Company company) {
        this.company = company;
    }

    public Long getRecruiterProfileId() {
        return recruiterProfileId;
    }

    public void setRecruiterProfileId(Long recruiterProfileId) {
        this.recruiterProfileId = recruiterProfileId;
    }

    public String getRecruiterProfileUUId() {
        return recruiterProfileUUId;
    }

    public void setRecruiterProfileUUId(String recruiterProfileUUId) {
        this.recruiterProfileUUId = recruiterProfileUUId;
    }

    public String getRecruiterProfileName() {
        return recruiterProfileName;
    }

    public void setRecruiterProfileName(String recruiterProfileName) {
        this.recruiterProfileName = recruiterProfileName;
    }

    public String getRecruiterProfileMobile() {
        return recruiterProfileMobile;
    }

    public void setRecruiterProfileMobile(String recruiterProfileMobile) {
        this.recruiterProfileMobile = recruiterProfileMobile;
    }

    public String getRecruiterProfileLandline() {
        return recruiterProfileLandline;
    }

    public void setRecruiterProfileLandline(String recruiterProfileLandline) {
        this.recruiterProfileLandline = recruiterProfileLandline;
    }

    public Long getRecruiterProfilePin() {
        return recruiterProfilePin;
    }

    public void setRecruiterProfilePin(Long recruiterProfilePin) {
        this.recruiterProfilePin = recruiterProfilePin;
    }

    public String getRecruiterProfileEmail() {
        return recruiterProfileEmail;
    }

    public void setRecruiterProfileEmail(String recruiterProfileEmail) {
        this.recruiterProfileEmail = recruiterProfileEmail;
    }

    public Timestamp getRecruiterProfileCreateTimestamp() {
        return recruiterProfileCreateTimestamp;
    }

    public void setRecruiterProfileCreateTimestamp(Timestamp recruiterProfileCreateTimestamp) {
        this.recruiterProfileCreateTimestamp = recruiterProfileCreateTimestamp;
    }

    public RecruiterStatus getRecStatus() {
        return recStatus;
    }

    public void setRecStatus(RecruiterStatus recStatus) {
        this.recStatus = recStatus;
    }

    public String getRecruiterAlternateMobile() {
        return recruiterAlternateMobile;
    }

    public void setRecruiterAlternateMobile(String recruiterAlternateMobile) {
        this.recruiterAlternateMobile = recruiterAlternateMobile;
    }

    public String getRecruiterDesignation() {
        return recruiterDesignation;
    }

    public void setRecruiterDesignation(String recruiterDesignation) {
        this.recruiterDesignation = recruiterDesignation;
    }

    public String getRecruiterLinkedinProfile() {
        return recruiterLinkedinProfile;
    }

    public void setRecruiterLinkedinProfile(String recruiterLinkedinProfile) {
        this.recruiterLinkedinProfile = recruiterLinkedinProfile;
    }

    public JobPost getJobPost() {
        return jobPost;
    }

    public void setJobPost(JobPost jobPost) {
        this.jobPost = jobPost;
    }

    public Company getCompany() {
        return company;
    }

    public void setCompany(Company company) {
        this.company = company;
    }

    public String getRecruiterOfficeAddress() {
        return recruiterOfficeAddress;
    }

    public void setRecruiterOfficeAddress(String recruiterOfficeAddress) {
        this.recruiterOfficeAddress = recruiterOfficeAddress;
    }

    public int getRecruiterEmailStatus() {
        return recruiterEmailStatus;
    }

    public void setRecruiterEmailStatus(int recruiterEmailStatus) {
        this.recruiterEmailStatus = recruiterEmailStatus;
    }

    public RecruiterProfileStatus getRecruiterprofilestatus() {
        return recruiterprofilestatus;
    }

    public void setRecruiterprofilestatus(RecruiterProfileStatus recruiterprofilestatus) {
        this.recruiterprofilestatus = recruiterprofilestatus;
    }

    public RecruiterLead getRecruiterLead() {
        return recruiterLead;
    }

    public void setRecruiterLead(RecruiterLead recruiterLead) {
        this.recruiterLead = recruiterLead;
    }

    public Integer getRecruiterInterviewUnlockCredits() {
        return recruiterInterviewUnlockCredits;
    }

    public void setRecruiterInterviewUnlockCredits(Integer recruiterInterviewUnlockCredits) {
        this.recruiterInterviewUnlockCredits = recruiterInterviewUnlockCredits;
    }

    public Integer getRecruiterCandidateUnlockCredits() {
        return recruiterCandidateUnlockCredits;
    }

    public void setRecruiterCandidateUnlockCredits(Integer recruiterCandidateUnlockCredits) {
        this.recruiterCandidateUnlockCredits = recruiterCandidateUnlockCredits;
    }

    public List<RecruiterCreditHistory> getRecruiterCreditHistoryList() {
        return recruiterCreditHistoryList;
    }

    public void setRecruiterCreditHistoryList(List<RecruiterCreditHistory> recruiterCreditHistoryList) {
        this.recruiterCreditHistoryList = recruiterCreditHistoryList;
    }

    public List<RecruiterPayment> getRecruiterPaymentList() {
        return recruiterPaymentList;
    }

    public void setRecruiterPaymentList(List<RecruiterPayment> recruiterPaymentList) {
        this.recruiterPaymentList = recruiterPaymentList;
    }
}
