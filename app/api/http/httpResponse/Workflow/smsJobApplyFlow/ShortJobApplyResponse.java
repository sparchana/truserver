package api.http.httpResponse.Workflow.smsJobApplyFlow;

import com.fasterxml.jackson.annotation.JsonValue;
import models.entity.JobPost;

/**
 * Created by zero on 16/1/17.
 */
public class ShortJobApplyResponse {
    public enum Status {
        UNKNOWN,
        FAILURE,
        BAD_REQUEST,
        BAD_PARAMS,
        SUCCESS,
        ALREADY_APPLIED,
        NO_JOB;

        @JsonValue
        public String toJson() {
            return name().toLowerCase();
        }

    }

    private Status status;
    private InterviewSlotPopulateResponse interviewSlotPopulateResponse;
    private LocalityPopulateResponse localityPopulateResponse;
    private ShortPSPopulateResponse shortPSPopulateResponse;
    private JobPost jobPost;
    private boolean alreadyApplied;

    public ShortJobApplyResponse(boolean alreadyApplied) {
        this.alreadyApplied = alreadyApplied;
    }

    public InterviewSlotPopulateResponse getInterviewSlotPopulateResponse() {
        return interviewSlotPopulateResponse;
    }

    public void setInterviewSlotPopulateResponse(InterviewSlotPopulateResponse interviewSlotPopulateResponse) {
        this.interviewSlotPopulateResponse = interviewSlotPopulateResponse;
    }

    public LocalityPopulateResponse getLocalityPopulateResponse() {
        return localityPopulateResponse;
    }

    public void setLocalityPopulateResponse(LocalityPopulateResponse localityPopulateResponse) {
        this.localityPopulateResponse = localityPopulateResponse;
    }

    public ShortPSPopulateResponse getShortPSPopulateResponse() {
        return shortPSPopulateResponse;
    }

    public void setShortPSPopulateResponse(ShortPSPopulateResponse shortPSPopulateResponse) {
        this.shortPSPopulateResponse = shortPSPopulateResponse;
    }

    public JobPost getJobPost() {
        return jobPost;
    }

    public void setJobPost(JobPost jobPost) {
        this.jobPost = jobPost;
    }

    public boolean isAlreadyApplied() {
        return alreadyApplied;
    }

    public void setAlreadyApplied(boolean alreadyApplied) {
        this.alreadyApplied = alreadyApplied;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public int getStatusCode() {
        return this.status.ordinal();
    }
}
