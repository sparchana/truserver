package api.http.httpRequest.Workflow;

import java.util.List;

/**
 * Created by zero on 17/10/16.
 */
public class PreScreenRequest {
    public Long candidateId;
    public Long jobPostId;
    public boolean forceSet;
    public List<Long> preScreenIdList;

    public Long getCandidateId() {
        return candidateId;
    }

    public void setCandidateId(Long candidateId) {
        this.candidateId = candidateId;
    }

    public Long getJobPostId() {
        return jobPostId;
    }

    public void setJobPostId(Long jobPostId) {
        this.jobPostId = jobPostId;
    }

    public List<Long> getPreScreenIdList() {
        return preScreenIdList;
    }

    public void setPreScreenIdList(List<Long> responseList) {
        this.preScreenIdList = responseList;
    }

    public boolean isForceSet() {
        return forceSet;
    }

    public void setForceSet(boolean forceSet) {
        this.forceSet = forceSet;
    }
}
