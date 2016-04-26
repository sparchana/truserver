package api;

/**
 * Created by batcoder1 on 25/4/16.
 */

public class CandidateSignUpRequest {
    protected String candidateName;
    protected String candidateMobile;
    protected int candidateOtp;
    protected String autoCandidateMobile;
    protected String candidatePassword;
    protected String candidateAuthMobile;

    public void setCandidateName(String candidateName) {
        this.candidateName = candidateName;
    }

    public String getCandidateName() {
        return candidateName;
    }

    public void setCandidateMobile(String candidateMobile) {
        this.candidateMobile = candidateMobile;
    }

    public String getAutoCandidateMobile() { return autoCandidateMobile; }

    public void setAutoCandidateMobile(String autoCandidateMobile) {
        this.autoCandidateMobile = autoCandidateMobile;
    }

    public String getCandidateMobile() {
        return candidateMobile;
    }

    public void setCandidateOtp(int candidateOtp) {
        this.candidateOtp = candidateOtp;
    }

    public int getCandidateOtp() {
        return candidateOtp;
    }

    public void setCandidatePassword(String candidatePassword) {
        this.candidatePassword = candidatePassword;
    }

    public String getCandidatePassword() {
        return candidatePassword;
    }

    public void setCandidateAuthMobile(String candidateAuthMobile) {
        this.candidateAuthMobile = candidateAuthMobile;
    }

    public String getCandidateAuthMobile() { return candidateAuthMobile; }
}
