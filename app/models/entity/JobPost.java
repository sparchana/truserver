package models.entity;

import api.ServerConstants;
import com.avaje.ebean.Model;
import com.avaje.ebean.annotation.PrivateOwned;
import com.avaje.ebean.annotation.UpdatedTimestamp;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import dao.JobPostWorkFlowDAO;
import models.entity.OM.*;
import models.entity.Recruiter.RecruiterProfile;
import models.entity.Static.*;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.*;

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

    @Column(name = "JobPostCreateTimestamp", columnDefinition = "timestamp not null default current_timestamp")
    private Timestamp jobPostCreateTimestamp;

    @UpdatedTimestamp
    @Column(name = "JobPostUpdateTimestamp", columnDefinition = "timestamp null")
    private Timestamp jobPostUpdateTimestamp;

    @Column(name = "JobPostMinSalary", columnDefinition = "bigint signed null")
    private Long jobPostMinSalary;

    @Column(name = "JobPostMaxSalary", columnDefinition = "bigint signed null")
    private Long jobPostMaxSalary;

    @Column(name = "JobPostStartTime", columnDefinition = "int null")
    private Integer jobPostStartTime;

    @Column(name = "JobPostEndTime", columnDefinition = "int null")
    private Integer jobPostEndTime;

    @Column(name = "JobPostIsHot", columnDefinition = "int signed null")
    private Boolean jobPostIsHot;

    @Column(name = "JobPostDescription", columnDefinition = "varchar(5000) null")
    private String jobPostDescription;

    @Column(name = "JobPostTitle", columnDefinition = "varchar(100) null")
    private String jobPostTitle;

    @Column(name = "JobPostIncentives", columnDefinition = "varchar(5000) null")
    private String jobPostIncentives;

    @Column(name = "JobPostMinRequirement", columnDefinition = "varchar(5000) null")
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
    private Byte jobPostWorkingDays;

    @Column(name = "PlaceId", columnDefinition = "text null")
    private String placeId;

    @Column(name = "interview_building_no", columnDefinition = "text null")
    private String interviewBuildingNo;

    @Column(name = "interview_landmark", columnDefinition = "text null")
    private String interviewLandmark;

    @Column(name = "ReviewApplication", columnDefinition = "int(1) null")
    private Integer reviewApplication;

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

    @ManyToOne
    @JsonManagedReference
    @JoinColumn(name = "JobRecruiterId")
    private RecruiterProfile recruiterProfile;

    @JsonBackReference
    @OneToMany(mappedBy = "jobPost", cascade = CascadeType.ALL)
    private List<JobApplication> jobPostApplicationList;

    @Column(name = "Gender", columnDefinition = "int(1) null")
    private Integer gender;

    @Column(name = "Source", columnDefinition = "int null")
    private Integer source; // internal data

    // partner side requirement
    @Column(name = "JobPostPartnerInterviewIncentive", columnDefinition = "bigint signed null")
    private Long jobPostPartnerInterviewIncentive;

    @Column(name = "JobPostPartnerJoiningIncentive", columnDefinition = "bigint signed null")
    private Long jobPostPartnerJoiningIncentive;

    @JsonManagedReference
    @PrivateOwned
    @OneToMany(mappedBy = "jobPost", cascade = CascadeType.ALL)
    private List<InterviewDetails> interviewDetailsList;

    @Column(name = "JobPostMaxAge", columnDefinition = "int unsigned null")
    private Integer jobPostMaxAge;

    @JsonManagedReference
    @PrivateOwned
    @OneToMany(mappedBy = "jobPost", cascade = CascadeType.ALL)
    private List<JobPostLanguageRequirement> jobPostLanguageRequirements;

    @JsonManagedReference
    @PrivateOwned
    @OneToMany(mappedBy = "jobPost", cascade = CascadeType.ALL)
    private List<JobPostAssetRequirement> jobPostAssetRequirements;

    @JsonManagedReference
    @PrivateOwned
    @OneToMany(mappedBy = "jobPost", cascade = CascadeType.ALL)
    private List<JobPostDocumentRequirement> jobPostDocumentRequirements;

    @Transient
    private String createdBy = null;

    @Transient
    private int applyBtnStatus = 0; //  2: book Interview, 3: already applied, 4 : apply

    public static Finder<String, JobPost> find = new Finder(JobPost.class);

    public JobPost() {
        this.jobPostUUId = UUID.randomUUID().toString();
        this.jobPostCreateTimestamp = new Timestamp(System.currentTimeMillis());
        this.source = ServerConstants.SOURCE_INTERNAL;
    }

    public JobPost(JobPost jobPost) {
        this.jobPostId = jobPost.jobPostId;
        this.jobPostUUId  = jobPost.jobPostUUId;
        this.jobPostCreateTimestamp = jobPost.jobPostCreateTimestamp;
        this.jobPostUpdateTimestamp  = jobPost.jobPostUpdateTimestamp;
        this.jobPostMinSalary = jobPost.jobPostMinSalary;
        this.jobPostMaxSalary  = jobPost.jobPostMaxSalary;
        this.jobPostStartTime = jobPost.jobPostStartTime;
        this.jobPostEndTime  = jobPost.jobPostEndTime;
        this.jobPostIsHot = jobPost.jobPostIsHot;
        this.jobPostDescription  = jobPost.jobPostDescription;
        this.jobPostTitle = jobPost.jobPostTitle;
        this.jobPostIncentives  = jobPost.jobPostIncentives;
        this.jobPostMinRequirement = jobPost.jobPostMinRequirement;
        this.jobPostAddress  = jobPost.jobPostAddress;
        this.latitude = jobPost.latitude;
        this.longitude  = jobPost.longitude;
        this.jobPostPinCode = jobPost.jobPostPinCode;
        this.jobPostVacancies  = jobPost.jobPostVacancies;
        this.jobPostDescriptionAudio = jobPost.jobPostDescriptionAudio;
        this.jobPostWorkFromHome  = jobPost.jobPostWorkFromHome;
        this.jobPostWorkingDays = jobPost.jobPostWorkingDays;
        this.jobPostStatus  = jobPost.jobPostStatus;
        this.pricingPlanType = jobPost.pricingPlanType;
        this.jobRole  = jobPost.jobRole;
        this.jobPostToLocalityList = jobPost.jobPostToLocalityList;
        this.jobPostToBenefitsList  = jobPost.jobPostToBenefitsList;
        this.company = jobPost.company;
        this.jobPostShift  = jobPost.jobPostShift;
        this.jobPostExperience = jobPost.jobPostExperience;
        this.jobPostEducation  = jobPost.jobPostEducation;
        this.recruiterProfile = jobPost.recruiterProfile;
        this.jobPostApplicationList = jobPost.jobPostApplicationList;
        this.gender = jobPost.gender;
        this.source = jobPost.source;
        this.jobPostMaxAge = jobPost.jobPostMaxAge;
        this.jobPostLanguageRequirements = jobPost.jobPostLanguageRequirements;
        this.jobPostDocumentRequirements = jobPost.jobPostDocumentRequirements;
        this.jobPostAssetRequirements = jobPost.jobPostAssetRequirements;
    }

    public RecruiterProfile getRecruiterProfile() {
        return recruiterProfile;
    }

    public void setRecruiterProfile(RecruiterProfile recruiterProfile) {
        this.recruiterProfile = recruiterProfile;
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

    public Integer getJobPostStartTime() {
        return jobPostStartTime;
    }

    public void setJobPostStartTime(Integer jobPostStartTime) {
        this.jobPostStartTime = jobPostStartTime;
    }

    public Integer getJobPostEndTime() {
        return jobPostEndTime;
    }

    public void setJobPostEndTime(Integer jobPostEndTime) {
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

    public Byte getJobPostWorkingDays() {
        return jobPostWorkingDays;
    }

    public void setJobPostWorkingDays(Byte jobPostWorkingDays) {
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

    public Integer getGender() {
        return gender;
    }

    public void setGender(Integer gender) {
        this.gender = gender;
    }

    public Integer getSource() {
        return source;
    }

    public void setSource(Integer source) {
        this.source = source;
    }

    public Long getJobPostPartnerInterviewIncentive() {
        return jobPostPartnerInterviewIncentive;
    }

    public void setJobPostPartnerInterviewIncentive(Long jobPostPartnerInterviewIncentive) {
        this.jobPostPartnerInterviewIncentive = jobPostPartnerInterviewIncentive;
    }

    public Long getJobPostPartnerJoiningIncentive() {
        return jobPostPartnerJoiningIncentive;
    }

    public void setJobPostPartnerJoiningIncentive(Long jobPostPartnerJoiningIncentive) {
        this.jobPostPartnerJoiningIncentive = jobPostPartnerJoiningIncentive;
    }

    public List<InterviewDetails> getInterviewDetailsList() {
        return interviewDetailsList;
    }

    public void setInterviewDetailsList(List<InterviewDetails> interviewDetailsList) {
        this.interviewDetailsList = interviewDetailsList;
    }

    @Override
    public String toString() {

        String exp = getJobPostExperience() == null ? "N/A" : getJobPostExperience().getExperienceType();
        String edu = getJobPostEducation() == null ? "N/A" : getJobPostEducation().getEducationName();
        String gen = getGender() == null? "N/A" : getGender() == 0 ? "M" : "F";
        StringBuilder locs = new StringBuilder();

        for (JobPostToLocality loc :jobPostToLocalityList) {
            locs.append(loc.toString());
            locs.append(",");
        }

        String toS = "JOBPOST: " + getJobPostId() + "|" + getJobPostTitle() + "|" + getCompany().getCompanyName()
                + "|" + locs.toString()
                + "|" + getJobRole().getJobName() + "|" + getJobPostMinSalary() + "-" + getJobPostMaxSalary()
                + "|" + exp + "|" + edu + "|" + getGender()
                + "|" + getJobPostCreateTimestamp() + "|" + getSource();
        return toS;
    }

    public Integer getJobPostMaxAge() {
        return jobPostMaxAge;
    }

    public void setJobPostMaxAge(Integer jobPostMaxAge) {
        this.jobPostMaxAge = jobPostMaxAge;
    }

    public List<JobPostLanguageRequirement> getJobPostLanguageRequirements() {
        return jobPostLanguageRequirements;
    }

    public void setJobPostLanguageRequirements(List<JobPostLanguageRequirement> jobPostLanguageRequirements) {
        this.jobPostLanguageRequirements = jobPostLanguageRequirements;
    }

    public List<JobPostAssetRequirement> getJobPostAssetRequirements() {
        return jobPostAssetRequirements;
    }

    public void setJobPostAssetRequirements(List<JobPostAssetRequirement> jobPostAssetRequirements) {
        this.jobPostAssetRequirements = jobPostAssetRequirements;
    }

    public List<JobPostDocumentRequirement> getJobPostDocumentRequirements() {
        return jobPostDocumentRequirements;
    }

    public void setJobPostDocumentRequirements(List<JobPostDocumentRequirement> jobPostDocumentRequirements) {
        this.jobPostDocumentRequirements = jobPostDocumentRequirements;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getPlaceId() {
        return placeId;
    }

    public void setPlaceId(String placeId) {
        this.placeId = placeId;
    }

    public String getInterviewBuildingNo() {
        return interviewBuildingNo;
    }

    public void setInterviewBuildingNo(String interviewBuildingNo) {
        this.interviewBuildingNo = interviewBuildingNo;
    }

    public String getInterviewLandmark() {
        return interviewLandmark;
    }

    public void setInterviewLandmark(String interviewLandmark) {
        this.interviewLandmark = interviewLandmark;
    }

    public Integer getReviewApplication() {
        return reviewApplication;
    }

    public void setReviewApplication(Integer reviewApplication) {
        this.reviewApplication = reviewApplication;
    }

    public String getInterviewFullAddress() {
        String address = "";

        if(this.getJobPostAddress() != null){
            address = this.getJobPostAddress();

            //if building No/ office no/ office no is there, prefix it
            if(!Objects.equals(this.getInterviewBuildingNo(), "") && this.getInterviewBuildingNo() != null){
                address = this.getInterviewBuildingNo() + ", " + address;

                //if landmark is available is there, add it after full address
                if(!Objects.equals(this.getInterviewLandmark(), "") && this.getInterviewLandmark() != null){
                    address += ", Landmark: " + this.getInterviewLandmark();
                }
            } else if(!Objects.equals(this.getInterviewLandmark(), "") && this.getInterviewLandmark() != null){
                //if landmark is available is there, add it after full address
                address += ", Landmark: " + this.getInterviewLandmark();
            }
        }

        return address;
    }

    public Integer getAwaitingInterviewScheduleCount() {
        List<Long> jobIdList = new ArrayList<>();
        jobIdList.add(this.getJobPostId());

        // get records for specific jobPostid with status and exact scheduleDate
        List<JobPostWorkflow> jobPostWorkflowList =
                JobPostWorkFlowDAO.getApplicationCountAccordingToStatus(
                        jobIdList,
                        ServerConstants.JWF_STATUS_PRESCREEN_COMPLETED
                );

        return jobPostWorkflowList.size();
    }

    public Integer getAwaitingRecruiterConfirmationCount() {
        List<Long> jobIdList = new ArrayList<>();
        jobIdList.add(this.getJobPostId());

        // get records for specific jobPostid with status and exact scheduleDate
        List<JobPostWorkflow> jobPostWorkflowList =
                JobPostWorkFlowDAO.getApplicationCountAccordingToStatus(
                        jobIdList,
                        ServerConstants.JWF_STATUS_INTERVIEW_SCHEDULED
                );

        return jobPostWorkflowList.size();
    }

    public Integer getConfirmedInterviewsCount() {
        List<Long> jobIdList = new ArrayList<>();
        jobIdList.add(this.getJobPostId());

        // get records for specific jobPostid with status and exact scheduleDate
        List<JobPostWorkflow> jobPostWorkflowList =
                JobPostWorkFlowDAO.getConfirmedInterviewApplications(
                        jobIdList,
                        ServerConstants.JWF_STATUS_INTERVIEW_CONFIRMED
                );

        return jobPostWorkflowList.size();
    }

    public Integer getTodaysInterviewCount() {
        Calendar now = Calendar.getInstance();
        Date today = now.getTime();

        List<Long> jobIdList = new ArrayList<>();
        jobIdList.add(this.getJobPostId());

        // get records for specific jobPostid with status and exact scheduleDate
        List<JobPostWorkflow> jobPostWorkflowList =
                JobPostWorkFlowDAO.getTodayInterview(
                        jobIdList,
                        ServerConstants.JWF_STATUS_INTERVIEW_CONFIRMED,
                        today);

        return jobPostWorkflowList.size();
    }

    public Integer getTomorrowsInterviewCount() {
        Calendar now = Calendar.getInstance();
        Date today = now.getTime();

        List<Long> jobIdList = new ArrayList<>();
        jobIdList.add(this.getJobPostId());

        // get records for specific jobPostid with status and exact scheduleDate
        List<JobPostWorkflow> jobPostWorkflowList =
                JobPostWorkFlowDAO.getTomorrowsInterview(
                        jobIdList,
                        ServerConstants.JWF_STATUS_INTERVIEW_CONFIRMED,
                        today);

        return jobPostWorkflowList.size();
    }

    public Integer getCompletedInterviewCount() {
        List<Long> jobIdList = new ArrayList<>();
        jobIdList.add(this.getJobPostId());

        // get records for specific jobPostid with status and exact scheduleDate
        List<JobPostWorkflow> jobPostWorkflowList =
                JobPostWorkFlowDAO.getConfirmedInterviewApplications(
                        jobIdList,
                        ServerConstants.JWF_STATUS_CANDIDATE_FEEDBACK_STATUS_COMPLETE_SELECTED
                );

        return jobPostWorkflowList.size();

    }

    public int getApplyBtnStatus() {
        return applyBtnStatus;
    }

    public void setApplyBtnStatus(int applyBtnStatus) {
        this.applyBtnStatus = applyBtnStatus;
    }
}