package api.http.httpResponse;

/**
 * Created by zero on 10/10/16.
 */
public class CandidateExtraData {
    public String appliedOn;
    public String lastActive;
    public Integer assessmentAttemptId;
    public Integer preScreenCallAttemptCount;

    public String getAppliedOn() {
        return appliedOn;
    }

    public void setAppliedOn(String appliedOn) {
        this.appliedOn = appliedOn;
    }

    public String getLastActive() {
        return lastActive;
    }

    public void setLastActive(String lastActive) {
        this.lastActive = lastActive;
    }

    public Integer getAssessmentAttemptId() {
        return assessmentAttemptId;
    }

    public void setAssessmentAttemptId(Integer assessmentAttemptId) {
        this.assessmentAttemptId = assessmentAttemptId;
    }

    public Integer getPreScreenCallAttemptCount() {
        return preScreenCallAttemptCount;
    }

    public void setPreScreenCallAttemptCount(Integer preScreenCallAttemptCount) {
        this.preScreenCallAttemptCount = preScreenCallAttemptCount;
    }
}
