package api.http.httpResponse.Recruiter;

import models.entity.Candidate;
import models.entity.OM.JobPostWorkflow;
import models.entity.Static.JobPostWorkflowStatus;

/**
 * Created by dodo on 22/11/16.
 */
public class InterviewTodayResponse {
    JobPostWorkflow jobPostWorkflow;
    Candidate candidate;
    JobPostWorkflowStatus currentStatus;

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
}
