package models.entity.Recruiter;

import api.ServerConstants;
import com.avaje.ebean.Model;
import com.avaje.ebean.annotation.PrivateOwned;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import models.entity.Company;
import models.entity.JobPost;
import models.entity.Recruiter.OM.RecruiterToCandidateUnlocked;
import models.entity.Recruiter.Static.RecruiterProfileStatus;
import models.entity.Recruiter.Static.RecruiterStatus;
import models.entity.RecruiterCreditHistory;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.List;
import java.util.Objects;
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

    @Column(name = "RecruiterDesignation", columnDefinition = "varchar(150) null")
    private String recruiterDesignation;

    @Column(name = "RecruiterLinkedinProfile", columnDefinition = "text null")
    private String recruiterLinkedinProfile;

    @Column(name = "RecruiterOfficeAddress", columnDefinition = "text null")
    private String recruiterOfficeAddress;

    @Column(name = "RecruiterEmailStatus", columnDefinition = "int signed not null default 0")
    private int recruiterEmailStatus; // verified, Not-Yet-Verified

    @ManyToOne
    @JsonManagedReference
    @JoinColumn(name = "RecStatus")
    private RecruiterStatus recStatus;

    @JsonBackReference
    @PrivateOwned
    @OneToMany(mappedBy = "recruiterProfile", cascade = CascadeType.ALL)
    private List<JobPost> jobPosts;

    @JsonManagedReference
    @PrivateOwned
    @OneToOne(mappedBy = "recruiterId", cascade = CascadeType.ALL)
    private RecruiterAuth recruiterAuth;

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

    @JsonBackReference
    @OneToMany(mappedBy = "recruiterProfile", cascade = CascadeType.ALL)
    private List<RecruiterToCandidateUnlocked> recruiterToCandidateUnlockedList;

    @Transient
    private Integer contactCreditCount = 0;

    @Transient
    private Integer interviewCreditCount = 0;

    @Transient
    private Integer ctaCreditCount = 0;

    @Column(name = "recruiter_access_level", columnDefinition = "int(2) signed not null default 0")
    private int recruiterAccessLevel;

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

    public List<JobPost> getJobPosts() {
        return jobPosts;
    }

    public void setJobPost(List<JobPost> jobPost) {
        this.jobPosts = jobPost;
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

    public List<RecruiterToCandidateUnlocked> getRecruiterToCandidateUnlockedList() {
        return recruiterToCandidateUnlockedList;
    }

    public void setRecruiterToCandidateUnlockedList(List<RecruiterToCandidateUnlocked> recruiterToCandidateUnlockedList) {
        this.recruiterToCandidateUnlockedList = recruiterToCandidateUnlockedList;
    }

    public RecruiterAuth getRecruiterAuth() {
        return recruiterAuth;
    }

    public void setRecruiterAuth(RecruiterAuth recruiterAuth) {
        this.recruiterAuth = recruiterAuth;
    }


    public Integer getContactCreditCount() {
        return creditCount(ServerConstants.RECRUITER_CATEGORY_CONTACT_UNLOCK);
    }

    public void setContactCreditCount(Integer contactCreditCount) {
        this.contactCreditCount = contactCreditCount;
    }

    public Integer getInterviewCreditCount() {
        return creditCount(ServerConstants.RECRUITER_CATEGORY_INTERVIEW_UNLOCK);
    }

    public void setInterviewCreditCount(Integer interviewCreditCount) {
        this.interviewCreditCount = interviewCreditCount;
    }

    private Integer creditCount(Integer categoryId) {
        List<RecruiterCreditHistory> creditHistoryList = RecruiterCreditHistory.find.where().eq("RecruiterProfileId", this.getRecruiterProfileId()).findList();
        Integer count = 0;
        for(RecruiterCreditHistory history : creditHistoryList){
            if(Objects.equals(history.getRecruiterCreditCategory().getRecruiterCreditCategoryId(), categoryId)){
                if(history.getCreditIsExpired() != null && !history.getCreditIsExpired()){
                    if(history.getLatest() != null && history.getLatest()){
                        if(history.getRecruiterCreditsAvailable() != null){
                            count = count + history.getRecruiterCreditsAvailable();
                        }
                    }
                }
            }
        }

        return count;
    }

    public int getRecruiterAccessLevel() {
        return recruiterAccessLevel;
    }

    public void setRecruiterAccessLevel(int recruiterAccessLevel) {
        this.recruiterAccessLevel = recruiterAccessLevel;
    }

    public Integer getCtaCreditCount() {
        return creditCount(ServerConstants.RECRUITER_CATEGORY_CTA_CREDIT);
    }

    public void setCtaCreditCount(Integer ctaCreditCount) {
        this.ctaCreditCount = ctaCreditCount;
    }
}
