package api.http;

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

    public Date candidateDob = new Date();
    public int candidateGender ;
    public String candidateTimeShiftPref;

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

    public int getCandidateGender() {
        return candidateGender;
    }

    public String getCandidateTimeShiftPref() {
        return candidateTimeShiftPref;
    }

}