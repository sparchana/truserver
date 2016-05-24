package api.http;

import play.Logger;

import java.util.Date;
import java.util.List;

/**
 * Created by batcoder1 on 10/5/16.
 */
public class AddSupportCandidateRequest {
    public String candidateFirstName;
    public String candidateSecondName;
    public String candidateMobile;
    public String candidateJobInterest;
    public String candidateLocality;

    public Date candidateDob = new Date();
    public Integer candidateAge ;
    public String candidatePhoneType;
    public Integer candidateGender ;
    public String candidateHomeLocality;
    public Integer candidateMaritalStatus ;
    public String candidateEmail;
    public Integer candidateIsEmployed ;
    public Integer candidateTotalExperience ;

    public String candidateCurrentCompany;
    public String candidateCurrentJobLocation;
    public Integer candidateTransportation ;
    public Integer candidateCurrentWorkShift ;
    public String candidateCurrentJobRole;
    public String candidateCurrentJobDesignation;
    public long candidateCurrentSalary ;
    public Integer candidateCurrentJobDuration ;
    public String candidatePastJobCompany;
    public String candidatePastJobRole;
    public long candidatePastJobSalary ;

    public Integer candidateEducationLevel;
    public Integer candidateDegree ;
    public String candidateEducationInstitute;

    public String candidateTimeShiftPref;

    public Integer candidateMotherTongue ;

    public String candidateIdProof;

    public Integer candidateSalarySlip ;
    public Integer candidateAppointmentLetter ;

    public List<SkillMapClass> candidateSkills;

    public void setCandidateLanguageKnown(List<LanguageClass> candidateLanguageKnown) {
        this.candidateLanguageKnown = candidateLanguageKnown;
    }

    public List<LanguageClass> candidateLanguageKnown;
    public void setCandidateSkills(List<SkillMapClass> candidateSkills) {
        this.candidateSkills = candidateSkills;
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

    public void setCandidateGender(int candidateGender) {
        this.candidateGender = candidateGender;
    }
    public Integer getCandidateGender() {
        return candidateGender;
    }

    public void setCandidateAge(int candidateAge) {
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

    public void setCandidateMaritalStatus(int candidateMaritalStatus) { this.candidateMaritalStatus = candidateMaritalStatus; }
    public Integer getCandidateMaritalStatus() {
        return candidateMaritalStatus;
    }

    public void setCandidateIsEmployed(int candidateIsEmployed) { this.candidateIsEmployed = candidateIsEmployed; }
    public Integer getCandidateIsEmployed() {
        return candidateIsEmployed;
    }

    public void setCandidateEmail(String candidateEmail) {
        this.candidateEmail = candidateEmail;
    }
    public String getCandidateEmail() {
        return candidateEmail;
    }

    public void setCandidateTotalExperience(int candidateTotalExperience) { this.candidateTotalExperience = candidateTotalExperience; }
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

    public void setCandidateTransportation(int candidateTransportation) {
        this.candidateTransportation = candidateTransportation;
    }
    public Integer getCandidateTransportation() {
        return candidateTransportation;
    }

    public void setCandidateCurrentWorkShift(int candidateCurrentWorkShift) {
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

    public void setCandidateCurrentSalary(long candidateCurrentSalary) { this.candidateCurrentSalary = candidateCurrentSalary; }
    public long getCandidateCurrentSalary() {
        return candidateCurrentSalary;
    }

    public void setCandidateCurrentJobDuration(int candidateCurrentJobDuration) {
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

    public void setCandidatePastJobSalary(long candidatePastJobSalary) { this.candidatePastJobSalary = candidatePastJobSalary; }
    public long getCandidatePastJobSalary() {
        return candidatePastJobSalary;
    }

    public void setCandidateEducationLevel(int candidateEducationLevel) {
        this.candidateEducationLevel = candidateEducationLevel;
    }
    public Integer getCandidateEducationLevel() {
        return candidateEducationLevel;
    }

    public void setCandidateDegree(int candidateDegree) {
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

    public void setCandidateMotherTongue(int candidateMotherTongue) {
        this.candidateMotherTongue = candidateMotherTongue;
    }
    public Integer getCandidateMotherTongue() {
        return candidateMotherTongue;
    }

    public void setCandidateIdProof(String candidateIdProof) {
        this.candidateIdProof = candidateIdProof;
    }
    public String getCandidateIdProof() {
        return candidateIdProof;
    }

    public void setCandidateSalarySlip(int candidateSalarySlip) {
        this.candidateSalarySlip = candidateSalarySlip;
    }
    public Integer getCandidateSalarySlip() {
        return candidateSalarySlip;
    }

    public void setCandidateAppointmentLetter(int candidateAppointmentLetter) {
        this.candidateAppointmentLetter = candidateAppointmentLetter;
    }
    public Integer getCandidateAppointmentLetter() {
        return candidateAppointmentLetter;
    }

}
