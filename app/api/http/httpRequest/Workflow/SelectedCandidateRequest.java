package api.http.httpRequest.Workflow;

import java.util.List;

/**
 * Created by zero on 8/10/16.
 */
public class SelectedCandidateRequest extends WorkflowRequest{
    public List<Long> selectedCandidateIdList;

    public List<Long> getSelectedCandidateIdList() {
        return selectedCandidateIdList;
    }

    public void setSelectedCandidateIdList(List<Long> selectedCandidateIdList) {
        this.selectedCandidateIdList = selectedCandidateIdList;
    }

    public Long getJobPostId() {
        return jobPostId;
    }

    public void setJobPostId(Long jobPostId) {
        this.jobPostId = jobPostId;
    }
}
