package api.http.httpRequest;

/**
 * Created by hawk on 24/12/16.
 */
public class UrlParameters {

    public String jobPostTitle;
    public String jobCompany;
    public String jobLocation;
    public Long jobPostId;
    public String jobRoleName;
    public Long jobRoleId;
    public TYPE urlType;

    public  enum TYPE{
        jobsRoleInAtWithJobPostId,
        jobRoleInAt,
        jobRoleAt,
        jobRoleIn,
        allJobsAt,
        allJobsIn,
        allJobsInAt,
        allJobsWithJobRoleId,
        getJobDetailsWithJobPostId,
        getJobPostWithJobRoleId,
        InvalidRequest
    }

    public String getJobPostTitle() {
        return jobPostTitle;
    }

    public void setJobPostTitle(String jobPostTitle) {
        this.jobPostTitle = jobPostTitle;
    }

    public String getJobCompany() {
        return jobCompany;
    }

    public void setJobCompany(String jobCompany) {
        this.jobCompany = jobCompany;
    }

    public String getJobLocation() {
        return this.jobLocation;
    }

    public void setJobLocation(String jobLocation) {
        this.jobLocation = jobLocation;
    }

    public Long getJobPostId() {
        return jobPostId;
    }

    public void setJobPostId(Long jobPostId) {
        this.jobPostId = jobPostId;
    }

    public String getJobRoleName() {
        return jobRoleName;
    }

    public void setJobRoleName(String jobRoleName) {
        this.jobRoleName = jobRoleName;
    }

    public Long getJobRoleId() {
        return jobRoleId;
    }

    public void setJobRoleId(Long jobRoleId) {
        this.jobRoleId = jobRoleId;
    }

    public TYPE getUrlType() {
            return urlType;
        }

    public void setUrlType(TYPE urlType) { this.urlType = urlType; }
}
