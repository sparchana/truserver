package api.http.httpResponse;

import models.entity.Candidate;

/**
 * Created by zero on 4/10/16.
 */
public class CandidateMatchingJobPost {
    public enum FEATURE {
        APPLIED_ON,
        LAST_ACTIVE,
        ASSESSMENT_ATTEMPT_ID,
        IS_LOCALITY_REQ_SATISFIED,
        IS_LANGUAGE_REQ_SATISFIED,
        IS_AGE_REQ_SATISFIED,
        JOB_ROLE_PREFERENCE,
        IS_EXPERIENCE_REQ_SATISFIED,
        IS_GENDER_REQ_SATISFIED
    }
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
