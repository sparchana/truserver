package api.http.httpResponse;

import controllers.businessLogic.JobWorkflow.JobPostWorkflowEngine;

/**
 * Created by zero on 10/10/16.
 */
public class CandidateExtraData {
    public String appliedOn;
    public JobPostWorkflowEngine.LastActiveValue lastActive;
    public Integer assessmentAttemptId;

    public String getAppliedOn() {
        return appliedOn;
    }

    public void setAppliedOn(String appliedOn) {
        this.appliedOn = appliedOn;
    }

    public JobPostWorkflowEngine.LastActiveValue getLastActive() {
        return lastActive;
    }

    public void setLastActive(JobPostWorkflowEngine.LastActiveValue lastActive) {
        this.lastActive = lastActive;
    }

    public Integer getAssessmentAttemptId() {
        return assessmentAttemptId;
    }

    public void setAssessmentAttemptId(Integer assessmentAttemptId) {
        this.assessmentAttemptId = assessmentAttemptId;
    }

}
