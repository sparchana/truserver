package api.http.httpRequest;

import models.util.Validator;

/**
 * Created by batcoder1 on 28/4/16.
 */
public class ResetPasswordResquest {
    protected String resetPasswordMobile;
    protected String candidateForgotMobile;

    protected String candidateNewPassword;
    protected String forgotPasswordNewMobile;

    public void setResetPasswordMobile(String resetPasswordMobile) {
        this.resetPasswordMobile = Validator.indianMobilePattern(resetPasswordMobile);
    }

    public String getResetPasswordMobile() {
        return Validator.indianMobilePattern(resetPasswordMobile);
    }

    public void setCandidateForgotMobile(String candidateForgotMobile) {
        this.candidateForgotMobile = Validator.indianMobilePattern(candidateForgotMobile);
    }

    public String getCandidateForgotMobile() {
        return Validator.indianMobilePattern(candidateForgotMobile);
    }

    public void setCandidateNewPassword(String candidateNewPassword) {
        this.candidateNewPassword = candidateNewPassword;
    }

    public String getCandidateNewPassword() {
        return candidateNewPassword;
    }

    public void setForgotPasswordNewMobile(String forgotPasswordNewMobile) {
        this.forgotPasswordNewMobile = Validator.indianMobilePattern(forgotPasswordNewMobile);
    }

    public String getForgotPasswordNewMobile() {
        return Validator.indianMobilePattern(forgotPasswordNewMobile);
    }
}
