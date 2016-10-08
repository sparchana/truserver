package api.http.httpResponse;

import models.entity.Candidate;

import java.util.Map;

/**
 * Created by zero on 4/10/16.
 */
public class CandidateMatchingJobPost {
    public enum FEATURE {
        APPLIED_ON,
        IS_LOCALITY_REQ_SATISFIED,
        IS_LANGUAGE_REQ_SATISFIED,
        IS_AGE_REQ_SATISFIED,
        JOB_ROLE_PREFERENCE,
        IS_EXPERIENCE_REQ_SATISFIED,
        IS_GENDER_REQ_SATISFIED,
        IS_ASSESSED
    }
    public Candidate candidate;
    private Map<FEATURE, String> featureMap;

    public Candidate getCandidate() {
        return candidate;
    }

    public void setCandidate(Candidate candidate) {
        this.candidate = candidate;
    }

    public Map<FEATURE, String> getFeatureMap() {
        return featureMap;
    }

    public void setFeatureMap(Map<FEATURE, String> featureMap) {
        this.featureMap = featureMap;
    }
}
