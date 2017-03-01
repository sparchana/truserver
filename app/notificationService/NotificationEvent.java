package notificationService;

import models.entity.Candidate;
import models.entity.Company;
import models.entity.JobPost;
import models.entity.Partner;
import models.entity.Recruiter.RecruiterProfile;
import models.entity.Static.SmsType;

/**
 * Created by dodo on 8/12/16.
 */

public abstract class NotificationEvent {
    private String message;
    private String recipient;

    //RMP
    private RecruiterProfile recruiterProfile;
    private Company company;
    private Candidate candidate;
    private JobPost jobPost;
    private SmsType smsType;

    // rmp employee
    private Partner partner;

    abstract String send();

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getRecipient() {
        return recipient;
    }

    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }

    public RecruiterProfile getRecruiterProfile() {
        return recruiterProfile;
    }

    public void setRecruiterProfile(RecruiterProfile recruiterProfile) {
        this.recruiterProfile = recruiterProfile;
    }

    public Company getCompany() {
        return company;
    }

    public void setCompany(Company company) {
        this.company = company;
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

    public SmsType getSmsType() {
        return smsType;
    }

    public void setSmsType(SmsType smsType) {
        this.smsType = smsType;
    }

    public Partner getPartner() {
        return partner;
    }

    public void setPartner(Partner partner) {
        this.partner = partner;
    }
}
