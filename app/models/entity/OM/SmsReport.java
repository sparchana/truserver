package models.entity.OM;

import com.avaje.ebean.Model;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import models.entity.Candidate;
import models.entity.Company;
import models.entity.JobPost;
import models.entity.Recruiter.RecruiterProfile;
import models.entity.Static.SmsDeliveryStatus;
import models.entity.Static.SmsType;

import javax.persistence.*;
import java.sql.Timestamp;

/**
 * Created by dodo on 19/1/17.
 */

@Entity(name = "sms_report")
@Table(name = "sms_report")
public class SmsReport extends Model {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "sms_report_id", columnDefinition = "bigint unsigned", unique = true)
    private long smsReportId;

    @Column(name = "creation_timestamp", columnDefinition = "timestamp not null default current_timestamp")
    private Timestamp creationTimeStamp = new Timestamp(System.currentTimeMillis());

    @Column(name = "sms_text", columnDefinition = "text null")
    private String smsText;

    @Column(name = "sms_scheduler_id", columnDefinition = "text null")
    private String smsSchedulerId;

    @ManyToOne
    @JsonBackReference
    @JoinColumn(name = "CompanyId", referencedColumnName = "CompanyId")
    private Company company;

    @ManyToOne
    @JsonBackReference
    @JoinColumn(name = "recruiterProfileId", referencedColumnName = "recruiterProfileId")
    private RecruiterProfile recruiterProfile;

    @ManyToOne
    @JsonManagedReference
    @JoinColumn(name = "CandidateId", referencedColumnName = "CandidateId")
    private Candidate candidate;

    @ManyToOne
    @JsonBackReference
    @JoinColumn(name = "JobPostId", referencedColumnName = "JobPostId")
    private JobPost jobPost;

    @ManyToOne
    @JsonManagedReference
    @JoinColumn(name = "SmsDeliveryStatus")
    private SmsDeliveryStatus smsDeliveryStatus;

    @ManyToOne
    @JsonManagedReference
    @JoinColumn(name = "SmsType")
    private SmsType smsType;

    @Transient
    private Integer hasApplied = 0;

    public static Model.Finder<String, SmsReport> find = new Model.Finder(SmsReport.class);

    public SmsReport() {
        this.creationTimeStamp = new Timestamp(System.currentTimeMillis());
    }

    public long getSmsReportId() {
        return smsReportId;
    }

    public void setSmsReportId(long smsReportId) {
        this.smsReportId = smsReportId;
    }

    public Timestamp getCreationTimeStamp() {
        return creationTimeStamp;
    }

    public void setCreationTimeStamp(Timestamp creationTimeStamp) {
        this.creationTimeStamp = creationTimeStamp;
    }

    public String getSmsText() {
        return smsText;
    }

    public void setSmsText(String smsText) {
        this.smsText = smsText;
    }

    public Candidate getCandidate() {
        return candidate;
    }

    public void setCandidate(Candidate candidate) {
        this.candidate = candidate;
    }

    public JobPost getJobPost() {
        return jobPost;
    }

    public void setJobPost(JobPost jobPost) {
        this.jobPost = jobPost;
    }

    public SmsDeliveryStatus getSmsDeliveryStatus() {
        return smsDeliveryStatus;
    }

    public void setSmsDeliveryStatus(SmsDeliveryStatus smsDeliveryStatus) {
        this.smsDeliveryStatus = smsDeliveryStatus;
    }

    public String getSmsSchedulerId() {
        return smsSchedulerId;
    }

    public void setSmsSchedulerId(String smsSchedulerId) {
        this.smsSchedulerId = smsSchedulerId;
    }

    public Company getCompany() {
        return company;
    }

    public void setCompany(Company company) {
        this.company = company;
    }

    public RecruiterProfile getRecruiterProfile() {
        return recruiterProfile;
    }

    public void setRecruiterProfile(RecruiterProfile recruiterProfile) {
        this.recruiterProfile = recruiterProfile;
    }

    public Integer getHasApplied() {
        return checkIfApplied();
    }

    public void setHasApplied(Integer hasApplied) {
        this.hasApplied = hasApplied;
    }

    private Integer checkIfApplied() {
        SmsReport smsReport = SmsReport.find.where().eq("sms_report_id", this.getSmsReportId()).findUnique();
        JobApplication jobApplication = JobApplication.find.where()
                .eq("CandidateId", smsReport.getCandidate().getCandidateId())
                .eq("JobPostId", smsReport.getJobPost().getJobPostId())
                .findUnique();

        if(jobApplication != null){
            return 1;
        } else{
            return 0;
        }
    }

    public SmsType getSmsType() {
        return smsType;
    }

    public void setSmsType(SmsType smsType) {
        this.smsType = smsType;
    }
}