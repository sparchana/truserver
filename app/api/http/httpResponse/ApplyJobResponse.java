package api.http.httpResponse;

/**
 * Created by batcoder1 on 17/6/16.
 */
public class ApplyJobResponse {
    public static final int STATUS_SUCCESS = 1;
    public static final int STATUS_FAILURE = 2;
    public static final int STATUS_EXISTS = 3;
    public static final int STATUS_NO_CANDIDATE = 4;
    public static final int STATUS_NO_JOB = 5;

    public int status;
    public boolean isPreScreenAvailable;
    public boolean isInterviewAvailable;

    /* applied job post details req to show interview*/
    public String companyName;
    public String jobRoleTitle;
    public String jobTitle;
    public Long jobPostId;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public boolean isPreScreenAvailable() {
        return isPreScreenAvailable;
    }

    public void setPreScreenAvailable(boolean preScreenAvailable) {
        isPreScreenAvailable = preScreenAvailable;
    }

    public boolean isInterviewAvailable() {
        return isInterviewAvailable;
    }

    public void setInterviewAvailable(boolean interviewAvailable) {
        isInterviewAvailable = interviewAvailable;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getJobRoleTitle() {
        return jobRoleTitle;
    }

    public void setJobRoleTitle(String jobRoleTitle) {
        this.jobRoleTitle = jobRoleTitle;
    }

    public String getJobTitle() {
        return jobTitle;
    }

    public void setJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
    }

    public Long getJobPostId() {
        return jobPostId;
    }

    public void setJobPostId(Long jobPostId) {
        this.jobPostId = jobPostId;
    }
}
