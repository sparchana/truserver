package api.http;

import java.util.Date;

/**
 * Created by batcoder1 on 10/5/16.
 */
public class AddSupportCandidateRequest {
    public String candidateName = " ";
    public String candidateMobile = " ";
    public String candidateJobInterest = " ";
    public String candidateLocality = " ";

    public Date candidateDob = new Date();
    public Integer candidateAge = 0;
    public String candidatePhoneType = " ";
    public Integer candidateGender = 0;
    public String candidateHomeLocality = " ";
    public Integer candidateMaritalStatus = 0;
    public String candidateEmail = " ";
    public Integer candidateIsEmployed = 0;
    public Integer candidateTotalExperience = 0;

    public String candidateCurrentCompany = " ";
    public String candidateCurrentJobLocation = " ";
    public Integer candidateTransportation = 0;
    public Integer candidateCurrentWorkShift = 0;
    public String candidateCurrentJobRole = " ";
    public String candidateCurrentJobDesignation = " ";
    public Integer candidateCurrentSalary = 0;
    public Integer candidateCurrentJobDuration = 0;
    public String candidatePastJobCompany = " ";
    public String candidatePastJobRole = " ";
    public Integer candidatePastJobSalary = 0;

    public Integer candidateEducationLevel= 0;
    public Integer candidateDegree = 0;
    public String candidateEducationInstitute = " ";

    public String candidateTimeShiftPref = " ";

    public Integer candidateMotherTongue = 0;

    public String candidateSkills = " ";

    public String candidateIdProof = " ";
    public Integer candidateSalarySlip = 0;
    public Integer candidateAppointmentLetter = 0;

    /* Mandatory */
    public void setCandidateName(String candidateName) {
        this.candidateName = candidateName;
    }
    public String getCandidateName() {
        return candidateName;
    }

    public void setCandidateMobile(String candidateMobile) { this.candidateMobile = candidateMobile; }
    public String getCandidateMobile() {
        return candidateMobile;
    }

    public void setCandidateJobInterest(String candidateJobInterest) { this.candidateJobInterest = candidateJobInterest; }
    public String getCandidateJobInterest() {
        return candidateJobInterest;
    }

    public void setCandidateLocality(String candidateLocality) { this.candidateLocality = candidateLocality; }
    public String getCandidateLocality() {
        return candidateLocality;
    }

    /* others */
    public void setCandidateDob(Date candidateDob) {
        this.candidateDob = candidateDob;
    }
    public Date getCandidateDob() {
        return candidateDob;
    }

    public void setCandidateGender(Integer candidateGender) {
        this.candidateGender = candidateGender;
    }
    public Integer getCandidateGender() {
        return candidateGender;
    }

    public void setCandidateAge(Integer candidateAge) {
        this.candidateAge = candidateAge;
    }
    public Integer getCandidateAge() {
        return candidateAge;
    }

    public void setCandidateHomeLocality(String candidateHomeLocality) { this.candidateHomeLocality = candidateHomeLocality; }
    public String getCandidateHomeLocality() {
        return candidateHomeLocality;
    }

    public void setCandidatePhoneType(String candidatePhoneType) {
        this.candidatePhoneType = candidatePhoneType;
    }
    public String getCandidatePhoneType() {
        return candidatePhoneType;
    }

    public void setCandidateMaritalStatus(Integer candidateMaritalStatus) { this.candidateMaritalStatus = candidateMaritalStatus; }
    public Integer getCandidateMaritalStatus() {
        return candidateMaritalStatus;
    }

    public void setCandidateIsEmployed(Integer candidateIsEmployed) { this.candidateIsEmployed = candidateIsEmployed; }
    public Integer getCandidateIsEmployed() {
        return candidateIsEmployed;
    }

    public void setCandidateEmail(String candidateEmail) {
        this.candidateEmail = candidateEmail;
    }
    public String getCandidateEmail() {
        return candidateEmail;
    }

    public void setCandidateTotalExperience(Integer candidateTotalExperience) { this.candidateTotalExperience = candidateTotalExperience; }
    public Integer getCandidateTotalExperience() {
        return candidateTotalExperience;
    }

    public void setCandidateCurrentCompany(String candidateCurrentCompany) {
        this.candidateCurrentCompany = candidateCurrentCompany;
    }
    public String getCandidateCurrentCompany() {
        return candidateCurrentCompany;
    }

    public void setCandidateCurrentJobLocation(String candidateCurrentJobLocation) {
        this.candidateCurrentJobLocation = candidateCurrentJobLocation;
    }
    public String getCandidateCurrentJobLocation() {
        return candidateCurrentJobLocation;
    }

    public void setCandidateTransportation(Integer candidateTransportation) {
        this.candidateTransportation = candidateTransportation;
    }
    public Integer getCandidateTransportation() {
        return candidateTransportation;
    }

    public void setCandidateCurrentWorkShift(Integer candidateCurrentWorkShift) {
        this.candidateCurrentWorkShift = candidateCurrentWorkShift;
    }
    public Integer getCandidateCurrentWorkShift() {
        return candidateCurrentWorkShift;
    }

    public void setCandidateCurrentJobRole(String candidateCurrentJobRole) {
        this.candidateCurrentJobRole = candidateCurrentJobRole;
    }
    public String getCandidateCurrentJobRole() {
        return candidateCurrentJobRole;
    }

    public void setCandidateCurrentJobDesignation(String candidateCurrentJobDesignation) {
        this.candidateCurrentJobDesignation = candidateCurrentJobDesignation;
    }
    public String getCandidateCurrentJobDesignation() {
        return candidateCurrentJobDesignation;
    }

    public void setCandidateCurrentSalary(Integer candidateCurrentSalary) { this.candidateCurrentSalary = candidateCurrentSalary; }
    public Integer getCandidateCurrentSalary() {
        return candidateCurrentSalary;
    }

    public void setCandidateCurrentJobDuration(Integer candidateCurrentJobDuration) {
        this.candidateCurrentJobDuration = candidateCurrentJobDuration;
    }
    public Integer getCandidateCurrentJobDuration() {
        return candidateCurrentJobDuration;
    }

    public void setCandidatePastJobRole(String candidatePastJobRole) {
        this.candidatePastJobRole = candidatePastJobRole;
    }
    public String getCandidatePastJobRole() {
        return candidatePastJobRole;
    }

    public void setCandidatePastJobCompany(String candidatePastJobCampany) {
        this.candidatePastJobCompany = candidatePastJobCampany;
    }
    public String getCandidatePastJobCompany() {
        return candidatePastJobCompany;
    }

    public void setCandidatePastJobSalary(Integer candidatePastJobSalary) { this.candidatePastJobSalary = candidatePastJobSalary; }
    public Integer getCandidatePastJobSalary() {
        return candidatePastJobSalary;
    }

    public void setCandidateEducationLevel(Integer candidateEducationLevel) {
        this.candidateEducationLevel = candidateEducationLevel;
    }
    public Integer getCandidateEducationLevel() {
        return candidateEducationLevel;
    }

    public void setCandidateDegree(Integer candidateDegree) {
        this.candidateDegree = candidateDegree;
    }
    public Integer getCandidateDegree() {
        return candidateDegree;
    }

    public void setCandidateEducationInstitute(String candidateEducationInstitute) {
        this.candidateEducationInstitute= candidateEducationInstitute;
    }
    public String getCandidateEducationInstitute() {
        return candidateEducationInstitute;
    }

    public void setCandidateTimeShiftPref(String candidateTimeShiftPref) {
        this.candidateTimeShiftPref = candidateTimeShiftPref;
    }
    public String getCandidateTimeShiftPref() {
        return candidateTimeShiftPref;
    }

    public void setCandidateMotherTongue(Integer candidateMotherTongue) {
        this.candidateMotherTongue = candidateMotherTongue;
    }
    public Integer getCandidateMotherTongue() {
        return candidateMotherTongue;
    }

    public void setCandidateSkills(String candidateSkills) {
        this.candidateSkills = candidateSkills;
    }
    public String getCandidateSkills() {
        return candidateSkills;
    }

    public void setCandidateIdProof(String candidateIdProof) {
        this.candidateIdProof = candidateIdProof;
    }
    public String getCandidateIdProof() {
        return candidateIdProof;
    }

    public void setCandidateSalarySlip(Integer candidateSalarySlip) {
        this.candidateSalarySlip = candidateSalarySlip;
    }
    public Integer getCandidateSalarySlip() {
        return candidateSalarySlip;
    }

    public void setCandidateAppointmentLetter(Integer candidateAppointmentLetter) {
        this.candidateAppointmentLetter = candidateAppointmentLetter;
    }
    public Integer getCandidateAppointmentLetter() {
        return candidateAppointmentLetter;
    }

}
