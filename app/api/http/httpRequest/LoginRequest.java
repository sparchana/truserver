package api.http.httpRequest;

import api.http.FormValidator;

/**
 * Created by batcoder1 on 26/4/16.
 */
public class LoginRequest {
    protected String candidateLoginMobile;
    protected String candidateLoginPassword;

    public void setCandidateLoginMobile(String candidateLoginMobile) { this.candidateLoginMobile = FormValidator.indianMobilePattern(candidateLoginMobile); }

    public String getCandidateLoginMobile() {
        return FormValidator.indianMobilePattern(candidateLoginMobile);
    }

    public void setCandidateLoginPassword(String candidateLoginPassword) {
        this.candidateLoginPassword = candidateLoginPassword;
    }

    public String getCandidateLoginPassword() {
        return candidateLoginPassword;
    }

}
