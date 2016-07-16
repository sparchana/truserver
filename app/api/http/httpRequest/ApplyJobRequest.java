package api.http.httpRequest;

import api.http.FormValidator;

/**
 * Created by batcoder1 on 17/6/16.
 */
public class ApplyJobRequest {
    public String candidateMobile ;
    public Integer jobId;
    public Integer localityId;

    public String getCandidateMobile() {
        return candidateMobile;
    }

    public void setCandidateMobile(String candidateMobile) {
        this.candidateMobile = FormValidator.convertToIndianMobileFormat(candidateMobile);
    }

    public Integer getLocalityId() {
        return localityId;
    }

    public void setLocalityId(Integer localityId) {
        this.localityId = localityId;
    }

    public Integer getJobId() {
        return jobId;
    }

    public void setJobId(Integer jobId) {
        this.jobId = jobId;
    }
}
