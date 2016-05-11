package api.http;

import models.entity.OM.*;
import models.entity.OO.*;
import models.entity.Static.*;

import java.sql.Timestamp;
import java.util.List;

/**
 * Created by zero on 11/5/16.
 */
public class CandidateProfileCreateRequest {
    public long candidateId;

    public String candidateUUId;

    public long leadId;

    public String candidateName;

    public String candidateLastName;

    public int candidateGender;

    public Timestamp candidateDOB;

    public String candidateMobile;

    public String candidatePhoneType;

    public int candidateMaritalStatus;

    public String candidateEmail;

    public int candidateIsEmployed;

    public float candidateTotalExperience;  // data in years

    public int candidateAge;

    public Timestamp candidateCreateTimestamp = new Timestamp(System.currentTimeMillis());

    public Timestamp candidateUpdateTimestamp;

    public int candidateIsAssessed;

    public int candidateSalarySlip;

    public int candidateAppointmentLetter;

    public int IsMinProfileComplete = 0; // 0 - Not Complete

    public List<IDProofreference> idProofreferenceList;

    public List<JobHistory> jobHistoryList;

    public List<JobPreference> jobPreferencesList;

    public List<LanguageKnown> languageKnownList;

    public List<LocalityPreference> localityPreferenceList;

    public List<CandidateSkill> candidateSkillList;

    public CandidateCurrentJobDetail candidateCurrentJobDetail;

    public TimeShiftPreference timeShiftPreference;

    public Language motherTongue;

    public Locality locality;

    public CandidateProfileStatus candidateprofilestatus;

    public Education education;

    public long getCandidateId() {
        return candidateId;
    }

    public void setCandidateId(long candidateId) {
        this.candidateId = candidateId;
    }

    public String getCandidateUUId() {
        return candidateUUId;
    }

    public void setCandidateUUId(String candidateUUId) {
        this.candidateUUId = candidateUUId;
    }

    public long getLeadId() {
        return leadId;
    }

    public void setLeadId(long leadId) {
        this.leadId = leadId;
    }

    public String getCandidateName() {
        return candidateName;
    }

    public void setCandidateName(String candidateName) {
        this.candidateName = candidateName;
    }

    public String getCandidateLastName() {
        return candidateLastName;
    }

    public void setCandidateLastName(String candidateLastName) {
        this.candidateLastName = candidateLastName;
    }

    public int getCandidateGender() {
        return candidateGender;
    }

    public void setCandidateGender(int candidateGender) {
        this.candidateGender = candidateGender;
    }

    public Timestamp getCandidateDOB() {
        return candidateDOB;
    }

    public void setCandidateDOB(Timestamp candidateDOB) {
        this.candidateDOB = candidateDOB;
    }

    public String getCandidateMobile() {
        return candidateMobile;
    }

    public void setCandidateMobile(String candidateMobile) {
        this.candidateMobile = candidateMobile;
    }

    public String getCandidatePhoneType() {
        return candidatePhoneType;
    }

    public void setCandidatePhoneType(String candidatePhoneType) {
        this.candidatePhoneType = candidatePhoneType;
    }

    public int getCandidateMaritalStatus() {
        return candidateMaritalStatus;
    }

    public void setCandidateMaritalStatus(int candidateMaritalStatus) {
        this.candidateMaritalStatus = candidateMaritalStatus;
    }

    public String getCandidateEmail() {
        return candidateEmail;
    }

    public void setCandidateEmail(String candidateEmail) {
        this.candidateEmail = candidateEmail;
    }

    public int getCandidateIsEmployed() {
        return candidateIsEmployed;
    }

    public void setCandidateIsEmployed(int candidateIsEmployed) {
        this.candidateIsEmployed = candidateIsEmployed;
    }

    public float getCandidateTotalExperience() {
        return candidateTotalExperience;
    }

    public void setCandidateTotalExperience(float candidateTotalExperience) {
        this.candidateTotalExperience = candidateTotalExperience;
    }

    public int getCandidateAge() {
        return candidateAge;
    }

    public void setCandidateAge(int candidateAge) {
        this.candidateAge = candidateAge;
    }

    public Timestamp getCandidateCreateTimestamp() {
        return candidateCreateTimestamp;
    }

    public void setCandidateCreateTimestamp(Timestamp candidateCreateTimestamp) {
        this.candidateCreateTimestamp = candidateCreateTimestamp;
    }

    public Timestamp getCandidateUpdateTimestamp() {
        return candidateUpdateTimestamp;
    }

    public void setCandidateUpdateTimestamp(Timestamp candidateUpdateTimestamp) {
        this.candidateUpdateTimestamp = candidateUpdateTimestamp;
    }

    public int getCandidateIsAssessed() {
        return candidateIsAssessed;
    }

    public void setCandidateIsAssessed(int candidateIsAssessed) {
        this.candidateIsAssessed = candidateIsAssessed;
    }

    public int getCandidateSalarySlip() {
        return candidateSalarySlip;
    }

    public void setCandidateSalarySlip(int candidateSalarySlip) {
        this.candidateSalarySlip = candidateSalarySlip;
    }

    public int getCandidateAppointmentLetter() {
        return candidateAppointmentLetter;
    }

    public void setCandidateAppointmentLetter(int candidateAppointmentLetter) {
        this.candidateAppointmentLetter = candidateAppointmentLetter;
    }

    public int getIsMinProfileComplete() {
        return IsMinProfileComplete;
    }

    public void setIsMinProfileComplete(int isMinProfileComplete) {
        IsMinProfileComplete = isMinProfileComplete;
    }

    public List<IDProofreference> getIdProofreferenceList() {
        return idProofreferenceList;
    }

    public void setIdProofreferenceList(List<IDProofreference> idProofreferenceList) {
        this.idProofreferenceList = idProofreferenceList;
    }

    public List<JobHistory> getJobHistoryList() {
        return jobHistoryList;
    }

    public void setJobHistoryList(List<JobHistory> jobHistoryList) {
        this.jobHistoryList = jobHistoryList;
    }

    public List<JobPreference> getJobPreferencesList() {
        return jobPreferencesList;
    }

    public void setJobPreferencesList(List<JobPreference> jobPreferencesList) {
        this.jobPreferencesList = jobPreferencesList;
    }

    public List<LanguageKnown> getLanguageKnownList() {
        return languageKnownList;
    }

    public void setLanguageKnownList(List<LanguageKnown> languageKnownList) {
        this.languageKnownList = languageKnownList;
    }

    public List<LocalityPreference> getLocalityPreferenceList() {
        return localityPreferenceList;
    }

    public void setLocalityPreferenceList(List<LocalityPreference> localityPreferenceList) {
        this.localityPreferenceList = localityPreferenceList;
    }

    public List<CandidateSkill> getCandidateSkillList() {
        return candidateSkillList;
    }

    public void setCandidateSkillList(List<CandidateSkill> candidateSkillList) {
        this.candidateSkillList = candidateSkillList;
    }

    public CandidateCurrentJobDetail getCandidateCurrentJobDetail() {
        return candidateCurrentJobDetail;
    }

    public void setCandidateCurrentJobDetail(CandidateCurrentJobDetail candidateCurrentJobDetail) {
        this.candidateCurrentJobDetail = candidateCurrentJobDetail;
    }

    public TimeShiftPreference getTimeShiftPreference() {
        return timeShiftPreference;
    }

    public void setTimeShiftPreference(TimeShiftPreference timeShiftPreference) {
        this.timeShiftPreference = timeShiftPreference;
    }

    public Language getMotherTongue() {
        return motherTongue;
    }

    public void setMotherTongue(Language motherTongue) {
        this.motherTongue = motherTongue;
    }

    public Locality getLocality() {
        return locality;
    }

    public void setLocality(Locality locality) {
        this.locality = locality;
    }

    public CandidateProfileStatus getCandidateprofilestatus() {
        return candidateprofilestatus;
    }

    public void setCandidateprofilestatus(CandidateProfileStatus candidateprofilestatus) {
        this.candidateprofilestatus = candidateprofilestatus;
    }

    public Education getEducation() {
        return education;
    }

    public void setEducation(Education education) {
        this.education = education;
    }
}
