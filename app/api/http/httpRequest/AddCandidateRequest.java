package api.http.httpRequest;

import api.ServerConstants;
import play.Logger;

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
        if(candidateMobile.length() == 10){
            Logger.info("adding +91 to " + candidateMobile);
            this.candidateMobile = "+91" + candidateMobile;
        } else{
            this.candidateMobile = candidateMobile;
        }
    }
    public String getCandidateMobile() {
        return candidateMobile;
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

}