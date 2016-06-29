package api.http.httpResponse;

import java.sql.Timestamp;

/**
 * Created by batcoder1 on 29/6/16.
 */
public class JobApplicationGoogleSheetResponse {
    public String companyName;
    public String JobRoleName;
    public String candidateName;
    public String candidateMobile;
    public Long candidateLeadId;
    public Integer candidateGender;
    public Integer candidateIsAssessed;
    public Integer candidateIsEmployed;
    public Integer candidateTotalExp;
    public String languageKnown;
    public String candidateMotherTongue;
    public String candidateHomeLocality;
    public Long candidateCurrentSalary;
    public String candidateEducation;
    public String candidateJobPref;
    public String candidateLocalityPref;
    public String candidateSkill;
    public Timestamp candidateCreationTimestamp;

    public String getCompanyName() {
        return companyName;
    }

    public Timestamp getCandidateCreationTimestamp() {
        return candidateCreationTimestamp;
    }

    public Integer getCandidateTotalExp() {
        return candidateTotalExp;
    }

    public void setCandidateTotalExp(Integer candidateTotalExp) {
        this.candidateTotalExp = candidateTotalExp;
    }

    public void setCandidateCreationTimestamp(Timestamp candidateCreationTimestamp) {
        this.candidateCreationTimestamp = candidateCreationTimestamp;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getJobRoleName() {
        return JobRoleName;
    }

    public void setJobRoleName(String jobRoleName) {
        JobRoleName = jobRoleName;
    }

    public String getCandidateName() {
        return candidateName;
    }

    public void setCandidateName(String candidateName) {
        this.candidateName = candidateName;
    }

    public String getCandidateMobile() {
        return candidateMobile;
    }

    public void setCandidateMobile(String candidateMobile) {
        this.candidateMobile = candidateMobile;
    }

    public Long getCandidateLeadId() {
        return candidateLeadId;
    }

    public void setCandidateLeadId(Long candidateLeadId) {
        this.candidateLeadId = candidateLeadId;
    }

    public Integer getCandidateGender() {
        return candidateGender;
    }

    public void setCandidateGender(Integer candidateGender) {
        this.candidateGender = candidateGender;
    }

    public Integer getCandidateIsAssessed() {
        return candidateIsAssessed;
    }

    public void setCandidateIsAssessed(Integer candidateIsAssessed) {
        this.candidateIsAssessed = candidateIsAssessed;
    }

    public Integer getCandidateIsEmployed() {
        return candidateIsEmployed;
    }

    public void setCandidateIsEmployed(Integer candidateIsEmployed) {
        this.candidateIsEmployed = candidateIsEmployed;
    }

    public String getLanguageKnown() {
        return languageKnown;
    }

    public void setLanguageKnown(String languageKnown) {
        this.languageKnown = languageKnown;
    }

    public String getCandidateMotherTongue() {
        return candidateMotherTongue;
    }

    public void setCandidateMotherTongue(String candidateMotherTongue) {
        this.candidateMotherTongue = candidateMotherTongue;
    }

    public String getCandidateHomeLocality() {
        return candidateHomeLocality;
    }

    public void setCandidateHomeLocality(String candidateHomeLocality) {
        this.candidateHomeLocality = candidateHomeLocality;
    }

    public Long getCandidateCurrentSalary() {
        return candidateCurrentSalary;
    }

    public void setCandidateCurrentSalary(Long candidateCurrentSalary) {
        this.candidateCurrentSalary = candidateCurrentSalary;
    }

    public String getCandidateEducation() {
        return candidateEducation;
    }

    public void setCandidateEducation(String candidateEducation) {
        this.candidateEducation = candidateEducation;
    }

    public String getCandidateJobPref() {
        return candidateJobPref;
    }

    public void setCandidateJobPref(String candidateJobPref) {
        this.candidateJobPref = candidateJobPref;
    }

    public String getCandidateLocalityPref() {
        return candidateLocalityPref;
    }

    public void setCandidateLocalityPref(String candidateLocalityPref) {
        this.candidateLocalityPref = candidateLocalityPref;
    }

    public String getCandidateSkill() {
        return candidateSkill;
    }

    public void setCandidateSkill(String candidateSkill) {
        this.candidateSkill = candidateSkill;
    }
}
