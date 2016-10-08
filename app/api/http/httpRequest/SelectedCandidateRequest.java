package api.http.httpRequest;

import java.util.List;

/**
 * Created by zero on 8/10/16.
 */
public class SelectedCandidateRequest {
    public List<Long> selectedCandidateIdList;

    public List<Long> getSelectedCandidateIdList() {
        return selectedCandidateIdList;
    }

    public void setSelectedCandidateIdList(List<Long> selectedCandidateIdList) {
        this.selectedCandidateIdList = selectedCandidateIdList;
    }
}
