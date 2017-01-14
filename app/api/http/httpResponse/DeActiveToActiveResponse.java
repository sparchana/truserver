package api.http.httpResponse;

import models.entity.Candidate;

import java.util.List;

/**
 * Created by zero on 19/7/16.
 */
public class DeActiveToActiveResponse {
    public static final int STATUS_SUCCESS = 1;
    public static final int STATUS_FAILURE = 2;

    public int status;
    public List<Candidate> candidateList;

    public List<Candidate> getCandidateList() {
        return candidateList;
    }

    public void setCandidateList(List<Candidate> candidateList) {
        this.candidateList = candidateList;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
