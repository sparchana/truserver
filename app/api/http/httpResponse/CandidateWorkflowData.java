package api.http.httpResponse;

import models.entity.Candidate;
import models.entity.OM.JobApplication;

/**
 * Created by zero on 4/10/16.
 */
public class CandidateWorkflowData {
    public Candidate candidate;
    private CandidateExtraData extraData;

    public Candidate getCandidate() {
        return candidate;
    }

    public void setCandidate(Candidate candidate) {
        this.candidate = candidate;
    }

    public CandidateExtraData getExtraData() {
        return extraData;
    }

    public void setExtraData(CandidateExtraData candidateExtraData) {
        this.extraData = candidateExtraData;
    }
}
