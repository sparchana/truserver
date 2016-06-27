package api.http.httpRequest;

import api.http.FormValidator;

/**
 * Created by batcoder1 on 17/6/16.
 */
public class ApplyJobRequest {
    public String candidateMobile ;
    public Integer jobId;

    public String getCandidateMobile() {
        return candidateMobile;
    }

    public void setCandidateMobile(String candidateMobile) {
        this.candidateMobile = FormValidator.convertToIndianMobileFormat(candidateMobile);
    }

    public Integer getJobId() {
        return jobId;
    }

    public void setJobId(Integer jobId) {
        this.jobId = jobId;
    }
}
