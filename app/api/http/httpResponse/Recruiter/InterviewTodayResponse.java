package api.http.httpResponse.Recruiter;

import models.entity.Candidate;
import models.entity.OM.JobPostWorkflow;
import models.entity.Static.JobPostWorkflowStatus;
import models.entity.Static.RejectReason;

import java.sql.Timestamp;

/**
 * Created by dodo on 22/11/16.
 */
public class InterviewTodayResponse {
    private JobPostWorkflow jobPostWorkflow;
    private Candidate candidate;
    private JobPostWorkflowStatus currentStatus;
    private Timestamp lastUpdate;
    private RejectReason reason;

    public JobPostWorkflow getJobPostWorkflow() {
        return jobPostWorkflow;
    }

    public void setJobPostWorkflow(JobPostWorkflow jobPostWorkflow) {
        this.jobPostWorkflow = jobPostWorkflow;
    }

    public Candidate getCandidate() {
        return candidate;
    }

    public void setCandidate(Candidate candidate) {
        this.candidate = candidate;
    }

    public JobPostWorkflowStatus getCurrentStatus() {
        return currentStatus;
    }

    public void setCurrentStatus(JobPostWorkflowStatus currentStatus) {
        this.currentStatus = currentStatus;
    }

    public Timestamp getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(Timestamp lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public RejectReason getReason() {
        return reason;
    }

    public void setReason(RejectReason reason) {
        this.reason = reason;
    }
}
