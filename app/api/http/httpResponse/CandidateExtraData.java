package api.http.httpResponse;

import controllers.businessLogic.JobWorkflow.JobPostWorkflowEngine;

import java.sql.Timestamp;

/**
 * Created by zero on 10/10/16.
 */
public class CandidateExtraData {
    public String appliedOn;
    public JobPostWorkflowEngine.LastActiveValue lastActive;
    public Integer assessmentAttemptId;
    public Integer preScreenCallAttemptCount;
    public String jobApplicationMode;
    public Timestamp preScreenSelectionTimeStamp;
    public String workflowUUId;
    public Long workflowId;
    public String workflowStatus;

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

    public Integer getPreScreenCallAttemptCount() {
        return preScreenCallAttemptCount;
    }

    public void setPreScreenCallAttemptCount(Integer preScreenCallAttemptCount) {
        this.preScreenCallAttemptCount = preScreenCallAttemptCount;
    }

    public String getJobApplicationMode() {
        return jobApplicationMode;
    }

    public void setJobApplicationMode(String jobApplicationMode) {
        this.jobApplicationMode = jobApplicationMode;
    }

    public Timestamp getPreScreenSelectionTimeStamp() {
        return preScreenSelectionTimeStamp;
    }

    public void setPreScreenSelectionTimeStamp(Timestamp preScreenSelectionTimeStamp) {
        this.preScreenSelectionTimeStamp = preScreenSelectionTimeStamp;
    }

    public String getWorkflowUUId() {
        return workflowUUId;
    }

    public void setWorkflowUUId(String workflowUUId) {
        this.workflowUUId = workflowUUId;
    }

    public Long getWorkflowId() {
        return workflowId;
    }

    public void setWorkflowId(Long workflowId) {
        this.workflowId = workflowId;
    }

    public String getWorkflowStatus() {
        return workflowStatus;
    }

    public void setWorkflowStatus(String workflowStatus) {
        this.workflowStatus = workflowStatus;
    }
}
