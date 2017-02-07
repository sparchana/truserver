package api.http.httpResponse.Workflow;

import api.http.httpResponse.interview.InterviewDateTime;
import api.http.httpResponse.interview.InterviewResponse;
import models.entity.JobPost;

import java.util.Map;

/**
 * Created by zero on 21/1/17.
 */
public class InterviewSlotPopulateResponse {
    private JobPost jobPost;
    private InterviewResponse interviewResponse;

    private Map<String, InterviewDateTime> interviewSlotMap;

    public InterviewSlotPopulateResponse(Map<String, InterviewDateTime> interviewSlotMap,
                                         InterviewResponse interviewResponse,
                                         JobPost jobPost) {
        this.interviewResponse = interviewResponse;
        this.interviewSlotMap = interviewSlotMap;
        this.jobPost = jobPost;
    }

    public Map<String, InterviewDateTime> getInterviewSlotMap() {
        return interviewSlotMap;
    }

    public void setInterviewSlotMap(Map<String, InterviewDateTime> interviewSlotMap) {
        this.interviewSlotMap = interviewSlotMap;
    }

    public InterviewResponse getInterviewResponse() {
        return interviewResponse;
    }

    public void setInterviewResponse(InterviewResponse interviewResponse) {
        this.interviewResponse = interviewResponse;
    }

    public JobPost getJobPost() {
        return jobPost;
    }

    public void setJobPost(JobPost jobPost) {
        this.jobPost = jobPost;
    }
}
