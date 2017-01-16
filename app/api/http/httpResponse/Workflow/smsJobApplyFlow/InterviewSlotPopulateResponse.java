package api.http.httpResponse.Workflow.smsJobApplyFlow;


import api.http.httpResponse.interview.InterviewDateTime;
import api.http.httpResponse.interview.InterviewResponse;

import java.util.Map;

/**
 * Created by zero on 16/1/17.
 */
public class InterviewSlotPopulateResponse {
    private InterviewResponse interviewResponse;

    private Map<String, InterviewDateTime> interviewSlotMap;

    public InterviewSlotPopulateResponse(Map<String, InterviewDateTime> interviewSlotMap, InterviewResponse interviewResponse) {
        this.interviewResponse = interviewResponse;
        this.interviewSlotMap = interviewSlotMap;
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
}
