package api.http;

import play.Logger;

import java.util.Date;
import java.util.List;

/**
 * Created by batcoder1 on 10/5/16.
 */
public class AddSupportCandidateRequest {
    public String candidateName;
    public String candidateMobile;
    public String candidateJobInterest;
    public String candidateLocality;

    public Date candidateDob = new Date();
    public int candidateAge ;
    public String candidatePhoneType;
    public int candidateGender ;
    public String candidateHomeLocality;
    public int candidateMaritalStatus ;
    public String candidateEmail;
    public int candidateIsEmployed ;
    public int candidateTotalExperience ;

    public String candidateCurrentCompany;
    public String candidateCurrentJobLocation;
    public int candidateTransportation ;
    public int candidateCurrentWorkShift ;
    public String candidateCurrentJobRole;
    public String candidateCurrentJobDesignation;
    public int candidateCurrentSalary ;
    public int candidateCurrentJobDuration ;
    public String candidatePastJobCompany;
    public String candidatePastJobRole;
    public int candidatePastJobSalary ;

    public int candidateEducationLevel;
    public int candidateDegree ;
    public String candidateEducationInstitute;

    public String candidateTimeShiftPref;

    public int candidateMotherTongue ;

    public String candidateIdProof;

    public int candidateSalarySlip ;
    public int candidateAppointmentLetter ;

    public List<SkillMapClass> candidateSkills;

    public void setCandidateLanguageKnown(List<LanguageClass> candidateLanguageKnown) {
        this.candidateLanguageKnown = candidateLanguageKnown;
    }

    public List<LanguageClass> candidateLanguageKnown;
    public void setCandidateSkills(List<SkillMapClass> candidateSkills) {
        this.candidateSkills = candidateSkills;
    }

    /* Mandatory */
    public void setCandidateName(String candidateName) {
            this.candidateName = candidateName;
    }
    public String getCandidateName() {
        return candidateName;
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
    public int getCandidateGender() {
        return candidateGender;
    }

    public void setCandidateAge(int candidateAge) {
        this.candidateAge = candidateAge;
    }
    public int getCandidateAge() {
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
    public int getCandidateMaritalStatus() {
        return candidateMaritalStatus;
    }

    public void setCandidateIsEmployed(int candidateIsEmployed) { this.candidateIsEmployed = candidateIsEmployed; }
    public int getCandidateIsEmployed() {
        return candidateIsEmployed;
    }

    public void setCandidateEmail(String candidateEmail) {
        this.candidateEmail = candidateEmail;
    }
    public String getCandidateEmail() {
        return candidateEmail;
    }

    public void setCandidateTotalExperience(int candidateTotalExperience) { this.candidateTotalExperience = candidateTotalExperience; }
    public int getCandidateTotalExperience() {
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
    public int getCandidateTransportation() {
        return candidateTransportation;
    }

    public void setCandidateCurrentWorkShift(int candidateCurrentWorkShift) {
        this.candidateCurrentWorkShift = candidateCurrentWorkShift;
    }
    public int getCandidateCurrentWorkShift() {
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

    public void setCandidateCurrentSalary(int candidateCurrentSalary) { this.candidateCurrentSalary = candidateCurrentSalary; }
    public int getCandidateCurrentSalary() {
        return candidateCurrentSalary;
    }

    public void setCandidateCurrentJobDuration(int candidateCurrentJobDuration) {
        this.candidateCurrentJobDuration = candidateCurrentJobDuration;
    }
    public int getCandidateCurrentJobDuration() {
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

    public void setCandidatePastJobSalary(int candidatePastJobSalary) { this.candidatePastJobSalary = candidatePastJobSalary; }
    public int getCandidatePastJobSalary() {
        return candidatePastJobSalary;
    }

    public void setCandidateEducationLevel(int candidateEducationLevel) {
        this.candidateEducationLevel = candidateEducationLevel;
    }
    public int getCandidateEducationLevel() {
        return candidateEducationLevel;
    }

    public void setCandidateDegree(int candidateDegree) {
        this.candidateDegree = candidateDegree;
    }
    public int getCandidateDegree() {
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
    public int getCandidateMotherTongue() {
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
    public int getCandidateSalarySlip() {
        return candidateSalarySlip;
    }

    public void setCandidateAppointmentLetter(int candidateAppointmentLetter) {
        this.candidateAppointmentLetter = candidateAppointmentLetter;
    }
    public int getCandidateAppointmentLetter() {
        return candidateAppointmentLetter;
    }

}
