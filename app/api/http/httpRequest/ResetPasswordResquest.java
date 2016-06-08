package api.http.httpRequest;

/**
 * Created by batcoder1 on 28/4/16.
 */
public class ResetPasswordResquest {
    protected String resetPasswordMobile;
    protected String candidateForgotMobile;
    protected int candidateForgotOtp;

    protected String candidateNewPassword;
    protected String forgotPasswordNewMobile;

    public void setResetPasswordMobile(String resetPasswordMobile) {
        this.resetPasswordMobile = resetPasswordMobile;
    }

    public String getResetPasswordMobile() {
        return resetPasswordMobile;
    }

    public void setCandidateForgotMobile(String candidateForgotMobile) {
        this.candidateForgotMobile = candidateForgotMobile;
    }

    public String getCandidateForgotMobile() {
        return candidateForgotMobile;
    }

    public void setCandidateForgotOtp(int candidateForgotOtp) {
        this.candidateForgotOtp = candidateForgotOtp;
    }

    public int getCandidateForgotOtp() {
        return candidateForgotOtp;
    }

    public void setCandidateNewPassword(String candidateNewPassword) {
        this.candidateNewPassword = candidateNewPassword;
    }

    public String getCandidateNewPassword() {
        return candidateNewPassword;
    }

    public void setForgotPasswordNewMobile(String forgotPasswordNewMobile) {
        this.forgotPasswordNewMobile = forgotPasswordNewMobile;
    }

    public String getForgotPasswordNewMobile() {
        return forgotPasswordNewMobile;
    }
}
