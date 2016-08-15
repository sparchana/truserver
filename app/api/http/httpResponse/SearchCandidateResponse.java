package api.http.httpResponse;

import models.entity.Candidate;

import java.util.List;

/**
 * Created by zero on 23/5/16.
 */
public class SearchCandidateResponse {
    public static final int STATUS_SUCCESS = 1;
    public static final int STATUS_FAILURE = 2;
    public static final int STATUS_LIMIT_EXHAUSTED = 3;

    public int status;
    public List<Candidate> candidateList;

    public List<Candidate> getCandidateList() {
        return candidateList;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public void setCandidateList(List<Candidate> responseList) {
        this.candidateList = responseList;
    }
}
