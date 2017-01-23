package models.entity.OM;

import com.avaje.ebean.Model;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import models.entity.Candidate;
import models.entity.Company;
import models.entity.JobPost;
import models.entity.Recruiter.RecruiterProfile;
import models.entity.Static.SmsDeliveryStatus;

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
    @Column(name = "sms_report_id", columnDefinition = "int signed", unique = true)
    private Integer smsReportId;

    @Column(name = "creation_timestamp", columnDefinition = "timestamp not null default current_timestamp")
    private Timestamp creationTimeStamp = new Timestamp(System.currentTimeMillis());

    @Column(name = "sms_text", columnDefinition = "text null")
    private String smsText;

    @Column(name = "sms_scheduler_id", columnDefinition = "text null")
    private String smsSchedulerId;

    @ManyToOne
    @JsonManagedReference
    @JoinColumn(name = "CompanyId", referencedColumnName = "CompanyId")
    private Company company;

    @ManyToOne
    @JsonManagedReference
    @JoinColumn(name = "recruiterProfileId", referencedColumnName = "recruiterProfileId")
    private RecruiterProfile recruiterProfile;

    @ManyToOne
    @JsonBackReference
    @JoinColumn(name = "CandidateId", referencedColumnName = "CandidateId")
    private Candidate candidate;

    @ManyToOne
    @JsonManagedReference
    @JoinColumn(name = "JobPostId", referencedColumnName = "JobPostId")
    private JobPost jobPost;

    @ManyToOne
    @JsonManagedReference
    @JoinColumn(name = "SmsDeliveryStatus")
    private SmsDeliveryStatus smsDeliveryStatus;

    public static Model.Finder<String, SmsReport> find = new Model.Finder(SmsReport.class);

    public SmsReport() {
        this.creationTimeStamp = new Timestamp(System.currentTimeMillis());
    }

    public Integer getSmsReportId() {
        return smsReportId;
    }

    public void setSmsReportId(Integer smsReportId) {
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
}