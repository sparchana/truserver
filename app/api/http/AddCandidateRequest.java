package api.http;

import api.ServerConstants;
import play.Logger;

import java.util.Date;
/**
 * Created by batcoder1 on 25/5/16.
 */
public class AddCandidateRequest {
    public String candidateFirstName;
    public String candidateSecondName;
    public String candidateMobile;
    public String candidateJobInterest;
    public String candidateLocality;
    public Date candidateDob;
    public Integer candidateGender ;
    public String candidateTimeShiftPref;
    public Integer leadSource= ServerConstants.LEAD_SOURCE_UNKNOWN;

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

    public String getCandidateJobInterest() {
        return candidateJobInterest;
    }

    public String getCandidateLocality() {
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

    public void setCandidateJobInterest(String candidateJobInterest) {
        this.candidateJobInterest = candidateJobInterest;
    }

    public void setCandidateLocality(String candidateLocality) {
        this.candidateLocality = candidateLocality;
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
}