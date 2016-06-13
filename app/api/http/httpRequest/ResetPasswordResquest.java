package api.http.httpRequest;

import api.http.FormValidator;

/**
 * Created by batcoder1 on 28/4/16.
 */
public class ResetPasswordResquest {
    protected String resetPasswordMobile;
    protected String candidateForgotMobile;

    protected String candidateNewPassword;
    protected String forgotPasswordNewMobile;

    public void setResetPasswordMobile(String resetPasswordMobile) {
        this.resetPasswordMobile = FormValidator.convertToIndianMobileFormat(resetPasswordMobile);
    }

    public String getResetPasswordMobile() {
        return FormValidator.convertToIndianMobileFormat(resetPasswordMobile);
    }

    public void setCandidateForgotMobile(String candidateForgotMobile) {
        this.candidateForgotMobile = FormValidator.convertToIndianMobileFormat(candidateForgotMobile);
    }

    public String getCandidateForgotMobile() {
        return FormValidator.convertToIndianMobileFormat(candidateForgotMobile);
    }

    public void setCandidateNewPassword(String candidateNewPassword) {
        this.candidateNewPassword = candidateNewPassword;
    }

    public String getCandidateNewPassword() {
        return candidateNewPassword;
    }

    public void setForgotPasswordNewMobile(String forgotPasswordNewMobile) {
        this.forgotPasswordNewMobile = FormValidator.convertToIndianMobileFormat(forgotPasswordNewMobile);
    }

    public String getForgotPasswordNewMobile() {
        return FormValidator.convertToIndianMobileFormat(forgotPasswordNewMobile);
    }
}
