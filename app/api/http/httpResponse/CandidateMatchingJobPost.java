package api.http.httpResponse;

import models.entity.Candidate;

/**
 * Created by zero on 4/10/16.
 */
public class CandidateMatchingJobPost {
    public Candidate candidate;
    private CandidateFeature feature;

    public Candidate getCandidate() {
        return candidate;
    }

    public void setCandidate(Candidate candidate) {
        this.candidate = candidate;
    }

    public CandidateFeature getFeature() {
        return feature;
    }

    public void setFeature(CandidateFeature feature) {
        this.feature = feature;
    }
}
