package api.http.httpRequest;


import api.http.CandidateKnownLanguage;
import api.http.httpRequest.Workflow.preScreenEdit.UpdateCandidateDocument;

import java.util.Date;
import java.util.List;

/**
 * Created by zero on 20/11/16.
 */
public class UpdateCandidateDetail  {
    public List<Integer> assetIdList;
    public Date candidateDob;
    public List<UpdateCandidateDocument.IdProofWithIdNumber> idProofWithIdNumberList;
    public Integer candidateEducationLevel;
    public Integer candidateDegree;
    public String candidateEducationInstitute;
    public Integer candidateEducationCompletionStatus;
    public Integer candidateGender ;
    public Integer candidateHomeLocality;
    public List<CandidateKnownLanguage> candidateKnownLanguageList;
    public Long candidateLastWithdrawnSalary;
    public String candidateTimeShiftPref;
    public Integer candidateTotalExperience;

    public List<Integer> getAssetIdList() {
        return assetIdList;
    }

    public void setAssetIdList(List<Integer> assetIdList) {
        this.assetIdList = assetIdList;
    }

    public Date getCandidateDob() {
        return candidateDob;
    }

    public void setCandidateDob(Date candidateDob) {
        this.candidateDob = candidateDob;
    }

    public List<UpdateCandidateDocument.IdProofWithIdNumber> getIdProofWithIdNumberList() {
        return idProofWithIdNumberList;
    }

    public void setIdProofWithIdNumberList(List<UpdateCandidateDocument.IdProofWithIdNumber> idProofWithIdNumberList) {
        this.idProofWithIdNumberList = idProofWithIdNumberList;
    }

    public Integer getCandidateEducationLevel() {
        return candidateEducationLevel;
    }

    public void setCandidateEducationLevel(Integer candidateEducationLevel) {
        this.candidateEducationLevel = candidateEducationLevel;
    }

    public Integer getCandidateDegree() {
        return candidateDegree;
    }

    public void setCandidateDegree(Integer candidateDegree) {
        this.candidateDegree = candidateDegree;
    }

    public String getCandidateEducationInstitute() {
        return candidateEducationInstitute;
    }

    public void setCandidateEducationInstitute(String candidateEducationInstitute) {
        this.candidateEducationInstitute = candidateEducationInstitute;
    }

    public Integer getCandidateEducationCompletionStatus() {
        return candidateEducationCompletionStatus;
    }

    public void setCandidateEducationCompletionStatus(Integer candidateEducationCompletionStatus) {
        this.candidateEducationCompletionStatus = candidateEducationCompletionStatus;
    }

    public Integer getCandidateGender() {
        return candidateGender;
    }

    public void setCandidateGender(Integer candidateGender) {
        this.candidateGender = candidateGender;
    }

    public Integer getCandidateHomeLocality() {
        return candidateHomeLocality;
    }

    public void setCandidateHomeLocality(Integer candidateHomeLocality) {
        this.candidateHomeLocality = candidateHomeLocality;
    }

    public List<CandidateKnownLanguage> getCandidateKnownLanguageList() {
        return candidateKnownLanguageList;
    }

    public void setCandidateKnownLanguageList(List<CandidateKnownLanguage> candidateKnownLanguageList) {
        this.candidateKnownLanguageList = candidateKnownLanguageList;
    }

    public Long getCandidateLastWithdrawnSalary() {
        return candidateLastWithdrawnSalary;
    }

    public void setCandidateLastWithdrawnSalary(Long candidateLastWithdrawnSalary) {
        this.candidateLastWithdrawnSalary = candidateLastWithdrawnSalary;
    }

    public String getCandidateTimeShiftPref() {
        return candidateTimeShiftPref;
    }

    public void setCandidateTimeShiftPref(String candidateTimeShiftPref) {
        this.candidateTimeShiftPref = candidateTimeShiftPref;
    }

    public Integer getCandidateTotalExperience() {
        return candidateTotalExperience;
    }

    public void setCandidateTotalExperience(Integer candidateTotalExperience) {
        this.candidateTotalExperience = candidateTotalExperience;
    }
}
