package api.http.httpRequest;

import api.ServerConstants;

import java.util.Date;

/**
 * Created by batcoder1 on 25/5/16.
 */
public class AddCandidateRequest extends CandidateSignUpRequest{
    public Date candidateDob;
    public Integer candidateGender ;
    public String candidateTimeShiftPref;

    public Integer leadSource = ServerConstants.LEAD_SOURCE_UNKNOWN;

    public Integer getLeadSource() {
        return leadSource;
    }

    /* Mandatory */

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
}