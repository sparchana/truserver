package models.entity;

import com.avaje.ebean.Model;
import com.avaje.ebean.annotation.PrivateOwned;
import com.avaje.ebean.annotation.UpdatedTimestamp;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.sun.org.apache.xalan.internal.xsltc.dom.BitArray;
import models.entity.OM.JobApplication;
import models.entity.OM.JobPostToBenefits;
import models.entity.OM.JobPostToLocality;
import models.entity.Static.*;

import javax.persistence.*;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;

/**
 * Created by batcoder1 on 15/6/16.
 */

@Entity(name = "jobpost")
@Table(name = "jobpost")
public class JobPost extends Model {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name = "JobPostId", columnDefinition = "bigint signed", unique = true)
    private Long jobPostId;

    @Column(name = "JobPostUUId", columnDefinition = "varchar(255) not null")
    private String jobPostUUId;

    @Column(name = "JobPostCreateTimestamp", columnDefinition = "timestamp not null")
    private Timestamp jobPostCreateTimestamp;

    @UpdatedTimestamp
    @Column(name = "JobPostUpdateTimestamp", columnDefinition = "timestamp null")
    private Timestamp jobPostUpdateTimestamp;

    @Column(name = "JobPostMinSalary", columnDefinition = "bigint signed null")
    private Long jobPostMinSalary;

    @Column(name = "JobPostMaxSalary", columnDefinition = "bigint signed null")
    private Long jobPostMaxSalary;

    @Column(name = "JobPostStartTime", columnDefinition = "time null")
    private Time jobPostStartTime;

    @Column(name = "JobPostEndTime", columnDefinition = "time null")
    private Time jobPostEndTime;

    @Column(name = "JobPostIsHot", columnDefinition = "int signed null")
    private Boolean jobPostIsHot;

    @Column(name = "JobPostDescription", columnDefinition = "varchar(1000) null")
    private String jobPostDescription;

    @Column(name = "JobPostTitle", columnDefinition = "varchar(100) null")
    private String jobPostTitle;

    @Column(name = "JobPostIncentives", columnDefinition = "varchar(1000) null")
    private String jobPostIncentives;

    @Column(name = "JobPostMinRequirement", columnDefinition = "varchar(1000) null")
    private String jobPostMinRequirement;

    @Column(name = "JobPostAddress", columnDefinition = "varchar(1000) null")
    private String jobPostAddress;

    @Column(name = "Latitude", columnDefinition = "double(10,6) null")
    private Double latitude;

    @Column(name = "Longitude", columnDefinition = "double(10,6) null")
    private Double longitude;

    @Column(name = "JobPostPinCode", columnDefinition = "bigint signed null")
    private Long jobPostPinCode;

    @Column(name = "JobPostVacancies", columnDefinition = "bigint signed null")
    private Integer jobPostVacancies;

    @Column(name = "JobPostDescriptionAudio", columnDefinition = "varchar(100) null")
    private String jobPostDescriptionAudio;

    @Column(name = "JobPostWorkFromHome", columnDefinition = "int signed null")
    private Boolean jobPostWorkFromHome;

    @Column(name = "JobPostWorkingDays", columnDefinition = "binary(7) null")
    private BitArray jobPostWorkingDays;

    @ManyToOne
    @JsonManagedReference
    @JoinColumn(name = "JobStatus")
    private JobStatus jobPostStatus;

    @ManyToOne
    @JsonManagedReference
    @JoinColumn(name = "PricingPlanType")
    private PricingPlanType pricingPlanType;

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JsonManagedReference
    @JoinColumn(name = "JobPostJobRole")
    private JobRole jobRole;

    @JsonManagedReference
    @PrivateOwned
    @OneToMany(mappedBy = "jobPost", cascade = CascadeType.ALL)
    private List<JobPostToLocality> jobPostToLocalityList;

    @JsonManagedReference
    @PrivateOwned
    @OneToMany(mappedBy = "jobPost", cascade = CascadeType.ALL)
    private List<JobPostToBenefits> jobPostToBenefitsList;

    @JsonManagedReference
    @ManyToOne
    @JoinColumn(name = "CompanyId", referencedColumnName = "CompanyId")
    private Company company;

    @ManyToOne
    @JsonManagedReference
    @JoinColumn(name = "JobShiftId")
    private TimeShift jobPostShift;

    @ManyToOne
    @JsonManagedReference
    @JoinColumn(name = "JobExperienceId")
    private Experience jobPostExperience;

    @ManyToOne
    @JsonManagedReference
    @JoinColumn(name = "JobEducationId")
    private Education jobPostEducation;

    @JsonBackReference
    @OneToMany(mappedBy = "jobPost", cascade = CascadeType.ALL)
    private List<JobApplication> jobPostApplicationList;

    public static Finder<String, JobPost> find = new Finder(JobPost.class);

    public JobPost() {
        this.jobPostUUId = UUID.randomUUID().toString();
        this.jobPostCreateTimestamp = new Timestamp(System.currentTimeMillis());
    }

    public Boolean getJobPostIsHot() {
        return jobPostIsHot;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Boolean getJobPostIsHot(Boolean isJobHot) {
        return jobPostIsHot;
    }

    public void setJobPostIsHot(Boolean jobPostIsHot) {
        this.jobPostIsHot = jobPostIsHot;
    }

    public String getJobPostDescription() {
        return jobPostDescription;
    }

    public void setJobPostDescription(String jobPostDescription) {
        this.jobPostDescription = jobPostDescription;
    }

    public String getJobPostIncentives() {
        return jobPostIncentives;
    }

    public void setJobPostIncentives(String jobPostIncentives) {
        this.jobPostIncentives = jobPostIncentives;
    }

    public String getJobPostMinRequirement() {
        return jobPostMinRequirement;
    }

    public void setJobPostMinRequirement(String jobPostMinRequirement) {
        this.jobPostMinRequirement = jobPostMinRequirement;
    }

    public String getJobPostAddress() {
        return jobPostAddress;
    }

    public void setJobPostAddress(String jobPostAddress) {
        this.jobPostAddress = jobPostAddress;
    }

    public Long getJobPostPinCode() {
        return jobPostPinCode;
    }

    public void setJobPostPinCode(Long jobPostPinCode) {
        this.jobPostPinCode = jobPostPinCode;
    }

    public String getJobPostDescriptionAudio() {
        return jobPostDescriptionAudio;
    }

    public void setJobPostDescriptionAudio(String jobPostDescriptionAudio) {
        this.jobPostDescriptionAudio = jobPostDescriptionAudio;
    }

    public Boolean getJobPostWorkFromHome() {
        return jobPostWorkFromHome;
    }

    public void setJobPostWorkFromHome(Boolean jobPostWorkFromHome) {
        this.jobPostWorkFromHome = jobPostWorkFromHome;
    }

    public PricingPlanType getPricingPlanType() {
        return pricingPlanType;
    }

    public void setPricingPlanType(PricingPlanType pricingPlanType) {
        this.pricingPlanType = pricingPlanType;
    }

    public List<JobPostToBenefits> getJobPostToBenefitsList() {
        return jobPostToBenefitsList;
    }

    public void setJobPostToBenefitsList(List<JobPostToBenefits> jobPostToBenefitsList) {
        this.jobPostToBenefitsList = jobPostToBenefitsList;
    }

    public Long getJobPostId() {
        return jobPostId;
    }

    public void setJobPostId(Long jobPostId) {
        this.jobPostId = jobPostId;
    }

    public String getJobPostUUId() {
        return jobPostUUId;
    }

    public void setJobPostUUId(String jobPostUUId) {
        this.jobPostUUId = jobPostUUId;
    }

    public Timestamp getJobPostCreateTimestamp() {
        return jobPostCreateTimestamp;
    }

    public void setJobPostCreateTimestamp(Timestamp jobPostCreateTimestamp) {
        this.jobPostCreateTimestamp = jobPostCreateTimestamp;
    }

    public Timestamp getJobPostUpdateTimestamp() {
        return jobPostUpdateTimestamp;
    }

    public void setJobPostUpdateTimestamp(Timestamp jobPostUpdateTimestamp) {
        this.jobPostUpdateTimestamp = jobPostUpdateTimestamp;
    }

    public Long getJobPostMinSalary() {
        return jobPostMinSalary;
    }

    public void setJobPostMinSalary(Long jobPostMinSalary) {
        this.jobPostMinSalary = jobPostMinSalary;
    }

    public Long getJobPostMaxSalary() {
        return jobPostMaxSalary;
    }

    public void setJobPostMaxSalary(Long jobPostmaxSalary) {
        this.jobPostMaxSalary = jobPostmaxSalary;
    }

    public Time getJobPostStartTime() {
        return jobPostStartTime;
    }

    public void setJobPostStartTime(Time jobPostStartTime) {
        this.jobPostStartTime = jobPostStartTime;
    }

    public Time getJobPostEndTime() {
        return jobPostEndTime;
    }

    public void setJobPostEndTime(Time jobPostEndTime) {
        this.jobPostEndTime = jobPostEndTime;
    }

    public String getJobPostTitle() {
        return jobPostTitle;
    }

    public void setJobPostTitle(String jobPostTitle) {
        this.jobPostTitle = jobPostTitle;
    }

    public Integer getJobPostVacancies() {
        return jobPostVacancies;
    }

    public void setJobPostVacancies(Integer jobPostVacancies) {
        this.jobPostVacancies = jobPostVacancies;
    }

    public BitArray getJobPostWorkingDays() {
        return jobPostWorkingDays;
    }

    public void setJobPostWorkingDays(BitArray jobPostWorkingDays) {
        this.jobPostWorkingDays = jobPostWorkingDays;
    }

    public JobStatus getJobPostStatus() {
        return jobPostStatus;
    }

    public void setJobPostStatus(JobStatus jobPostStatus) {
        this.jobPostStatus = jobPostStatus;
    }

    public List<JobApplication> getJobPostApplicationList() {
        return jobPostApplicationList;
    }

    public void setJobPostApplicationList(List<JobApplication> jobPostApplicationList) {
        this.jobPostApplicationList = jobPostApplicationList;
    }

    public JobRole getJobRole() {
        return jobRole;
    }

    public void setJobRole(JobRole jobRole) {
        this.jobRole = jobRole;
    }

    public List<JobPostToLocality> getJobPostToLocalityList() {
        return jobPostToLocalityList;
    }

    public void setJobPostToLocalityList(List<JobPostToLocality> jobPostToLocalityList) {
        this.jobPostToLocalityList = jobPostToLocalityList;
    }

    public Company getCompany() {
        return company;
    }

    public void setCompany(Company company) {
        this.company = company;
    }

    public TimeShift getJobPostShift() {
        return jobPostShift;
    }

    public void setJobPostShift(TimeShift jobPostShift) {
        this.jobPostShift = jobPostShift;
    }

    public Experience getJobPostExperience() {
        return jobPostExperience;
    }

    public void setJobPostExperience(Experience jobPostExperience) {
        this.jobPostExperience = jobPostExperience;
    }

    public Education getJobPostEducation() {
        return jobPostEducation;
    }

    public void setJobPostEducation(Education jobPostEducation) {
        this.jobPostEducation = jobPostEducation;
    }
}