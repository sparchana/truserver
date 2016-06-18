package models.entity;

import com.avaje.ebean.Model;
import com.avaje.ebean.annotation.PrivateOwned;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.sun.org.apache.xalan.internal.xsltc.dom.BitArray;
import models.entity.OM.JobApplication;
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

    @Column(name = "JobPostBenefitPF", columnDefinition = "int signed null")
    private Boolean jobPostBenefitPF;

    @Column(name = "JobPostBenefitFuel", columnDefinition = "int signed null")
    private Boolean jobPostBenefitFuel;

    @Column(name = "JobPostBenefitInsurance", columnDefinition = "int signed null")
    private Boolean jobPostBenefitInsurance;

    @Column(name = "JobPostDescription", columnDefinition = "varchar(1000) null")
    private String jobDescription;

    @Column(name = "JobPostTitle", columnDefinition = "varchar(100) null")
    private String jobPostTitle;

    @Column(name = "JobPostVacancy", columnDefinition = "bigint signed null")
    private Integer jobPostVacancy;

    @Column(name = "JobPostDescriptionAudio", columnDefinition = "varchar(100) null")
    private String jobDescriptionAudio;

    @Column(name = "JobPostWorkFromHome", columnDefinition = "int signed null")
    private Boolean jobWorkFromHome;

    @Column(name = "JobPostWorkingDays", columnDefinition = "binary(7) null")
    private BitArray jobPostWorkingDays;

    @ManyToOne
    @JsonManagedReference
    @JoinColumn(name = "JobStatus")
    private JobStatus jobPostStatus;

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JsonManagedReference
    @JoinColumn(name = "JobPostJobRole")
    private JobRole jobRole;

    @JsonManagedReference
    @PrivateOwned
    @OneToMany(mappedBy = "jobPost", cascade = CascadeType.ALL)
    private List<JobPostToLocality> jobPostToLocalityList;

    @JsonBackReference
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
        this.jobPostUpdateTimestamp = new Timestamp(System.currentTimeMillis());
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

    public Boolean getJobPostBenefitPF() {
        return jobPostBenefitPF;
    }

    public void setJobPostBenefitPF(Boolean jobPostBenefitPF) {
        this.jobPostBenefitPF = jobPostBenefitPF;
    }

    public Boolean getJobPostBenefitFuel() {
        return jobPostBenefitFuel;
    }

    public void setJobPostBenefitFuel(Boolean jobPostBenefitFuel) {
        this.jobPostBenefitFuel = jobPostBenefitFuel;
    }

    public Boolean getJobPostBenefitInsurance() {
        return jobPostBenefitInsurance;
    }

    public void setJobPostBenefitInsurance(Boolean jobPostBenefitInsurance) {
        this.jobPostBenefitInsurance = jobPostBenefitInsurance;
    }

    public String getJobDescription() {
        return jobDescription;
    }

    public void setJobDescription(String jobDescription) {
        this.jobDescription = jobDescription;
    }

    public String getJobPostTitle() {
        return jobPostTitle;
    }

    public void setJobPostTitle(String jobPostTitle) {
        this.jobPostTitle = jobPostTitle;
    }

    public Integer getJobPostVacancy() {
        return jobPostVacancy;
    }

    public void setJobPostVacancy(Integer jobPostVacancy) {
        this.jobPostVacancy = jobPostVacancy;
    }

    public String getJobDescriptionAudio() {
        return jobDescriptionAudio;
    }

    public void setJobDescriptionAudio(String jobDescriptionAudio) {
        this.jobDescriptionAudio = jobDescriptionAudio;
    }

    public Boolean getJobWorkFromHome() {
        return jobWorkFromHome;
    }

    public void setJobWorkFromHome(Boolean jobWorkFromHome) {
        this.jobWorkFromHome = jobWorkFromHome;
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
