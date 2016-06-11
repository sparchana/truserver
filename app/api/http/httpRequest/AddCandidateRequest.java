package api.http.httpRequest;

import api.ServerConstants;
import api.http.FormValidator;

import java.util.Date;
import java.util.List;

/**
 * Created by batcoder1 on 25/5/16.
 */
public class AddCandidateRequest {
    public String candidateFirstName;
    public String candidateSecondName;
    public String candidateMobile;
    public List<Integer> candidateJobInterest;
    public List<Integer> candidateLocality;

    public Date candidateDob;
    public Integer candidateGender ;
    public String candidateTimeShiftPref;

    public Integer leadSource = ServerConstants.LEAD_SOURCE_UNKNOWN;

    public Integer getLeadSource() {
        return leadSource;
    }

    /* Mandatory */
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

    public void setCandidateMobile(String candidateMobile) {
            this.candidateMobile = FormValidator.convertToIndianMobileFormat(candidateMobile);
    }
    public String getCandidateMobile() {
        return FormValidator.convertToIndianMobileFormat(candidateMobile);
    }

    public List<Integer> getCandidateJobInterest() {
        return candidateJobInterest;
    }

    public List<Integer> getCandidateLocality() {
        return candidateLocality;
    }

    /* others */
    public Date getCandidateDob() {
        return candidateDob;
    }

    public Integer getCandidateGender() {
        return candidateGender;
    }

    public String getCandidateTimeShiftPref() {
        return candidateTimeShiftPref;
    }


    public void setCandidateDob(Date candidateDob) {
        this.candidateDob = candidateDob;
    }

    public void setCandidateGender(Integer candidateGender) {
        this.candidateGender = candidateGender;
    }

    public void setCandidateTimeShiftPref(String candidateTimeShiftPref) {
        this.candidateTimeShiftPref = candidateTimeShiftPref;
    }

    public void setLeadSource(Integer leadSource) {
        this.leadSource = leadSource;
    }

    public void setCandidateJobInterest(List<Integer> candidateJobInterest) {
        this.candidateJobInterest = candidateJobInterest;
    }

    public void setCandidateLocality(List<Integer> candidateLocality) {
        this.candidateLocality = candidateLocality;
    }
}