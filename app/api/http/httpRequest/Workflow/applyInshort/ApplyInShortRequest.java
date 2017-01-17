package api.http.httpRequest.Workflow.applyInshort;

import api.http.httpRequest.UpdateCandidateDetail;

import java.util.List;

/**
 * Created by zero on 17/1/17.
 */
public class ApplyInShortRequest {
    /* locality req*/
    public Long candidateId;
    public Long jobPostId;
    public Integer localityId;
    public List<Integer> propertyIdList;

    // pre screen request
    public UpdateCandidateDetail updateCandidateDetail;

    // selected interview slot req
    public Integer timeSlotId;
    public Long dateInMillis;


    public Long getCandidateId() {
        return candidateId;
    }

    public void setCandidateId(Long candidateId) {
        this.candidateId = candidateId;
    }

    public Long getJobPostId() {
        return jobPostId;
    }

    public void setJobPostId(Long jobPostId) {
        this.jobPostId = jobPostId;
    }

    public Integer getLocalityId() {
        return localityId;
    }

    public void setLocalityId(Integer localityId) {
        this.localityId = localityId;
    }

    public UpdateCandidateDetail getUpdateCandidateDetail() {
        return updateCandidateDetail;
    }

    public void setUpdateCandidateDetail(UpdateCandidateDetail updateCandidateDetail) {
        this.updateCandidateDetail = updateCandidateDetail;
    }

    public Integer getTimeSlotId() {
        return timeSlotId;
    }

    public void setTimeSlotId(Integer timeSlotId) {
        this.timeSlotId = timeSlotId;
    }

    public Long getDateInMillis() {
        return dateInMillis;
    }

    public void setDateInMillis(Long dateInMillis) {
        this.dateInMillis = dateInMillis;
    }

    public List<Integer> getPropertyIdList() {
        return propertyIdList;
    }

    public void setPropertyIdList(List<Integer> propertyIdList) {
        this.propertyIdList = propertyIdList;
    }
}
