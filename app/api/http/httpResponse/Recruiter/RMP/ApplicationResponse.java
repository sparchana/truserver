package api.http.httpResponse.Recruiter.RMP;

import api.http.httpResponse.CandidateWorkflowData;

import java.util.List;

/**
 * Created by dodo on 25/1/17.
 */
public class ApplicationResponse {
    List<CandidateWorkflowData> applicationList;
    Integer totalCount;

    public List<CandidateWorkflowData> getApplicationList() {
        return applicationList;
    }

    public void setApplicationList(List<CandidateWorkflowData> applicationList) {
        this.applicationList = applicationList;
    }

    public Integer getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(Integer totalCount) {
        this.totalCount = totalCount;
    }
}
