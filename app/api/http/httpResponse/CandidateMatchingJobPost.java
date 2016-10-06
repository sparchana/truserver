package api.http.httpResponse;

import models.entity.Candidate;

import java.util.Map;

/**
 * Created by zero on 4/10/16.
 */
public class CandidateMatchingJobPost {
    public enum FEATURE {
        LOCALITY_REQ,
        LANGUAGE_REQ,
        AGE_REQ,
        JOB_ROLE_PREFERENCE,
        EXPERIENCE_REQ,
        GENDER_REQ,
        IS_APPLIED,
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
