package api.http;

import models.entity.Candidate;

/**
 * Created by zero on 23/5/16.
 */
public class SearchCandidateResponse {
    public static final int STATUS_SUCCESS = 1;
    public static final int STATUS_FAILURE = 2;

    public int status;
    public Candidate candidate;

    public void setStatus(int status) {
        this.status = status;
    }

    public void setCandidate(Candidate candidate) {
        this.candidate = candidate;
    }
}
