package api.http.httpResponse;

/**
 * Created by zero on 23/9/16.
 */
public class AssessmentSubmissionResponse {
    public enum Status {
        UNKNOW,
        FAILED,
        SUCCESS,
        ALREADY_ASSESSED,
        ALL_ASSESSED
    }
    Long jobRoleId;
    Status status;

    public Long getJobRoleId() {
        return jobRoleId;
    }

    public void setJobRoleId(Long jobRoleId) {
        this.jobRoleId = jobRoleId;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }
}
