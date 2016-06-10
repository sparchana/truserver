package api.http.httpRequest;

import models.util.Validator;

import java.util.List;

/**
 * Created by batcoder1 on 25/4/16.
 */

public class CandidateSignUpRequest {

    protected String candidateName;
    protected String candidateSecondName;
    protected String candidateMobile;
    protected List<Integer> candidateLocality;
    protected List<Integer> candidateJobPref;

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

    public void setCandidateLocality(List<Integer> candidateLocality) {
        this.candidateLocality = candidateLocality;
    }

    public List<Integer> getCandidateLocality() {
        return candidateLocality;
    }

    public void setCandidateJobPref(List<Integer> candidateJobPref) {
        this.candidateJobPref = candidateJobPref;
    }

    public List<Integer> getCandidateJobPref() {
        return candidateJobPref;
    }

    public void setCandidateMobile(String candidateMobile) {
        this.candidateMobile = Validator.indianMobilePattern(candidateMobile);
    }
    public String getCandidateMobile() {
        return Validator.indianMobilePattern(candidateMobile);
    }

    public void setCandidatePassword(String candidatePassword) {
        this.candidatePassword = candidatePassword;
    }

    public String getCandidatePassword() {
        return candidatePassword;
    }

    public void setCandidateAuthMobile(String candidateAuthMobile) {
        this.candidateAuthMobile = Validator.indianMobilePattern(candidateAuthMobile);
    }

    public String getCandidateAuthMobile() { return Validator.indianMobilePattern(candidateAuthMobile); }
}
