package api.http.httpRequest;

import api.http.FormValidator;

/**
 * Created by adarsh on 19/9/16.
 */
public class VerifyCandidateRequest {
    public Integer userOtp;
    public String candidateMobile;

    public Integer getUserOtp() {
        return userOtp;
    }

    public String getCandidateMobile() {
        return FormValidator.convertToIndianMobileFormat(candidateMobile);
    }

    public void setCandidateMobile(String candidateMobile) {
        this.candidateMobile = FormValidator.convertToIndianMobileFormat(candidateMobile);
    }

    public void setUserOtp(Integer userOtp) {
        this.userOtp = userOtp;
    }
}
