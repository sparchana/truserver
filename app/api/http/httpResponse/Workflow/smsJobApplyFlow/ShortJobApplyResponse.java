package api.http.httpResponse.Workflow.smsJobApplyFlow;

import models.entity.JobPost;

/**
 * Created by zero on 16/1/17.
 */
public class ShortJobApplyResponse {
    private InterviewSlotPopulateResponse interviewSlotPopulateResponse;
    private LocalityPopulateResponse localityPopulateResponse;
    private ShortPSPopulateResponse shortPSPopulateResponse;
    private JobPost jobPost;

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
}
