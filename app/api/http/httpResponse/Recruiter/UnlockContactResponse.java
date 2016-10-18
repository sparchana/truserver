package api.http.httpResponse.Recruiter;

import models.entity.JobPost;

/**
 * Created by dodo on 14/10/16.
 */
public class UnlockContactResponse {
    public static final int STATUS_FAILURE = 0;
    public static final int STATUS_SUCCESS = 1;
    public static final int STATUS_ALREADY_UNLOCKED = 2;
    public static final int STATUS_NO_CREDITS = 3;

    public int status;
    public String candidateMobile;
    public Long candidateId;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getCandidateMobile() {
        return candidateMobile;
    }

    public void setCandidateMobile(String candidateMobile) {
        this.candidateMobile = candidateMobile;
    }

    public Long getCandidateId() {
        return candidateId;
    }

    public void setCandidateId(Long candidateId) {
        this.candidateId = candidateId;
    }
}
