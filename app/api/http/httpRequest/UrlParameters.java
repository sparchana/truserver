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
        TYPE_JOB_ROLE_LOCATION_COMPANY_WITH_JOB_POST_ID,
        TYPE_JOB_ROLE_LOCATION_COMPANY,
        TYPE_JOB_ROLE_COMPANY,
        TYPE_JOB_ROLE_LOCATION,
        TYPE_ALL_JOBS_COMPANY,
        TYPE_ALL_JOBS_LOCATION,
        TYPE_ALL_JOBS_LOCATION_COMPANY,
        TYPE_ALL_JOBS_WITH_JOB_ROLE_ID,
        TYPE_JOB_DETAILS_WITH_JOB_POST_ID_REQUEST,
        TYPE_JOB_POST_WITH_JOB_ROLE_ID_REQUEST,
        INVALID_REQUEST
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
