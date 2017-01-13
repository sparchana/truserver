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
    public static final int STATUS_APPLICATION_LIMIT_REACHED = 6;

    public int status;
    public boolean isPreScreenAvailable;
    public boolean isInterviewAvailable;

    /* req for deactive candidate in app*/
    public boolean isCandidateDeActive;
    public String deActiveHeadMessage;
    public String deActiveTitleMessage;
    public String deActiveBodyMessage;

    /* applied job post details req to show interview*/
    public String companyName;
    public String jobRoleTitle;
    public String jobTitle;
    public Long jobPostId;

    public ApplyJobResponse() {
        this.isCandidateDeActive = false;
    }

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

    public boolean isCandidateDeActive() {
        return isCandidateDeActive;
    }

    public void setCandidateDeActive(boolean candidateDeActive) {
        isCandidateDeActive = candidateDeActive;
    }

    public String getDeActiveHeadMessage() {
        return deActiveHeadMessage;
    }

    public void setDeActiveHeadMessage(String deActiveHeadMessage) {
        this.deActiveHeadMessage = deActiveHeadMessage;
    }

    public String getDeActiveTitleMessage() {
        return deActiveTitleMessage;
    }

    public void setDeActiveTitleMessage(String deActiveTitleMessage) {
        this.deActiveTitleMessage = deActiveTitleMessage;
    }

    public String getDeActiveBodyMessage() {
        return deActiveBodyMessage;
    }

    public void setDeActiveBodyMessage(String deActiveBodyMessage) {
        this.deActiveBodyMessage = deActiveBodyMessage;
    }

}
