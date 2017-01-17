package api.http.httpRequest.Workflow.applyInshort;

import api.http.httpRequest.UpdateCandidateDetail;

/**
 * Created by zero on 17/1/17.
 */
public class ApplyInShortRequest {
    /* locality req*/
    public Long candidateId;
    public Integer jobId;
    public Integer localityId;

    // pre screen request
    public UpdateCandidateDetail updateCandidateDetail;

    // selected interview slot req
    public Integer timeSlot;
    public Long dateInMillis;


    public Long getCandidateId() {
        return candidateId;
    }

    public void setCandidateId(Long candidateId) {
        this.candidateId = candidateId;
    }

    public Integer getJobId() {
        return jobId;
    }

    public void setJobId(Integer jobId) {
        this.jobId = jobId;
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

    public Integer getTimeSlot() {
        return timeSlot;
    }

    public void setTimeSlot(Integer timeSlot) {
        this.timeSlot = timeSlot;
    }

    public Long getDateInMillis() {
        return dateInMillis;
    }

    public void setDateInMillis(Long dateInMillis) {
        this.dateInMillis = dateInMillis;
    }
}
