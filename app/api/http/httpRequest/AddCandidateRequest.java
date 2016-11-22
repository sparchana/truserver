package api.http.httpRequest;

import api.ServerConstants;

import java.util.Date;
import java.util.List;

/**
 * Created by batcoder1 on 25/5/16.
 */
public class AddCandidateRequest extends CandidateSignUpRequest{
    public Date candidateDob;
    public Integer candidateGender ;
    public String candidateTimeShiftPref;
    public List<IdProofWithValue> candidateIdProofList;

    public Integer leadSource = ServerConstants.LEAD_SOURCE_UNKNOWN;


    public static class IdProofWithValue {
        Integer idProofId;
        String idProofValue;

        public IdProofWithValue(){
        }
        public IdProofWithValue(IdProofWithValue idProofWithValue){
            //constructor Code
        }

        public Integer getIdProofId() {
            return idProofId;
        }

        public void setIdProofId(Integer idProofId) {
            this.idProofId = idProofId;
        }

        public String getIdProofValue() {
            return idProofValue;
        }

        public void setCandidateIdProofValues(String candidateIdProofValue) {
            this.idProofValue = candidateIdProofValue;
        }
    }

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

    public List<IdProofWithValue> getCandidateIdProofList() {
        return candidateIdProofList;
    }

    public void setCandidateIdProofList(List<IdProofWithValue> candidateIdProofList) {
        this.candidateIdProofList = candidateIdProofList;
    }
}