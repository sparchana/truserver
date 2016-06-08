package api.http.httpRequest;

/**
 * Created by batcoder1 on 25/4/16.
 */

public class CandidateSignUpRequest {

    protected String candidateName;
    protected String candidateSecondName;
    protected String candidateMobile;
    protected String candidateLocality;
    protected String candidateJobPref;

    protected String candidatePassword;
    protected String candidateAuthMobile;

    public void setCandidateName(String candidateName) {
        this.candidateName = candidateName;
    }

    public String getCandidateName() {
        return candidateName;
    }

    public void setCandidateSecondName(String candidateSecondName) {
        this.candidateSecondName = candidateSecondName;
    }

    public String getCandidateSecondName() {
        return candidateSecondName;
    }

    public void setCandidateLocality(String candidateLocality) {
        this.candidateLocality = candidateLocality;
    }

    public String getCandidateLocality() {
        return candidateLocality;
    }

    public void setCandidateJobPref(String candidateJobPref) {
        this.candidateJobPref = candidateJobPref;
    }

    public String getCandidateJobPref() {
        return candidateJobPref;
    }

    public void setCandidateMobile(String candidateMobile) {
        this.candidateMobile = candidateMobile;
    }
    public String getCandidateMobile() {
        return candidateMobile;
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
