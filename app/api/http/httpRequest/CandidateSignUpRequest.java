package api.http.httpRequest;

import api.http.FormValidator;

import java.util.List;

/**
 * Created by batcoder1 on 25/4/16.
 */

public class CandidateSignUpRequest{

    protected String candidateFirstName;
    protected String candidateSecondName;
    protected String candidateMobile;
    protected String candidateSecondMobile;
    protected String candidateThirdMobile;
    protected List<Integer> candidateLocality;
    protected List<Integer> candidateJobPref;

    protected String candidatePassword;
    protected String candidateAuthMobile;

    public void setCandidateFirstName(String candidateFirstName) {
        this.candidateFirstName = candidateFirstName;
    }

    public String getCandidateFirstName() {
        return candidateFirstName;
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
        this.candidateMobile = FormValidator.convertToIndianMobileFormat(candidateMobile);
    }
    public String getCandidateMobile() {
        return FormValidator.convertToIndianMobileFormat(candidateMobile);
    }

    public void setCandidatePassword(String candidatePassword) {
        this.candidatePassword = candidatePassword;
    }

    public String getCandidatePassword() {
        return candidatePassword;
    }

    public void setCandidateAuthMobile(String candidateAuthMobile) {
        this.candidateAuthMobile = FormValidator.convertToIndianMobileFormat(candidateAuthMobile);
    }

    public String getCandidateAuthMobile() { return FormValidator.convertToIndianMobileFormat(candidateAuthMobile); }

    public String getCandidateSecondMobile() {
        return FormValidator.convertToIndianMobileFormat(candidateSecondMobile);
    }

    public void setCandidateSecondMobile(String candidateSecondMobile) {
        this.candidateSecondMobile = FormValidator.convertToIndianMobileFormat(candidateSecondMobile);
    }

    public String getCandidateThirdMobile() {
        return FormValidator.convertToIndianMobileFormat(candidateThirdMobile);
    }

    public void setCandidateThirdMobile(String candidateThirdMobile) {
        this.candidateThirdMobile = FormValidator.convertToIndianMobileFormat(candidateThirdMobile);
    }
}
