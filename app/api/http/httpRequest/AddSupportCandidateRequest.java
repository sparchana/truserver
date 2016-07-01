package api.http.httpRequest;

import java.util.List;

/**
 * Created by batcoder1 on 10/5/16.
 */
public class AddSupportCandidateRequest extends AddCandidateEducationRequest {
    public Integer candidateAge ;
    public String candidatePhoneType;
    public Integer candidateHomeLocality;
    public Integer candidateMaritalStatus ;
    public String candidateEmail;
    public String candidatePastJobCompany;
    public Integer candidatePastJobRole;
    public Long candidatePastJobSalary ;
    public Long candidateLastWithdrawnSalary ;
    public Integer candidateCurrentJobLocation;
    public Integer candidateTransportation ;
    public Integer candidateCurrentWorkShift ;
    public Integer candidateCurrentJobRole;
    public String candidateCurrentJobDesignation;
    public Integer candidateCurrentJobDuration ;
    public List<Integer> candidateIdProof;
    public Integer candidateSalarySlip ;
    public Integer candidateAppointmentLetter ;
    public Integer candidateExperienceLetter ;
    public String supportNote;

    public static class ExpList {
        Integer jobExpQuestionId;
        List<Integer> jobExpResponseIdArray;

        public ExpList(){
        }
        public ExpList(ExpList expList){
            //constructor Code
        }

        public Integer getJobExpQuestionId() {
            return jobExpQuestionId;
        }

        public void setJobExpQuestionId(Integer jobExpQuestionId) {
            this.jobExpQuestionId = jobExpQuestionId;
        }

        public List<Integer> getJobExpResponseIdArray() {
            return jobExpResponseIdArray;
        }

        public void setJobExpResponseIdArray(List<Integer> jobExpResponseIdArray) {
            this.jobExpResponseIdArray = jobExpResponseIdArray;
        }
    }
    public static class PastCompany {
        Integer jobRoleId;
        List<String> companyName;
        Integer currentCompanyEnumVal;

        public PastCompany(){}

        public Integer getJobRoleId() {
            return jobRoleId;
        }

        public void setJobRoleId(Integer jobRoleId) {
            this.jobRoleId = jobRoleId;
        }

        public List<String> getCompanyName() {
            return companyName;
        }

        public void setCompanyName(List<String> companyName) {
            this.companyName = companyName;
        }

        public Integer getCurrentCompanyEnumVal() {
            return currentCompanyEnumVal;
        }

        public void setCurrentCompanyEnumVal(Integer currentCompanyEnumVal) {
            this.currentCompanyEnumVal = currentCompanyEnumVal;
        }
    }
    public List<ExpList> expList;
    public List<PastCompany> pastCompany;

    public String getSupportNote() {
        return supportNote;
    }

    public void setSupportNote(String supportNote) {
        this.supportNote = supportNote;
    }

    public Integer getCandidateAge() {
        return candidateAge;
    }

    public Integer getCandidateHomeLocality() {
        return candidateHomeLocality;
    }

    public String getCandidatePhoneType() {
        return candidatePhoneType;
    }

    public Integer getCandidateMaritalStatus() {
        return candidateMaritalStatus;
    }

    public String getCandidateEmail() {
        return candidateEmail;
    }

    public Integer getCandidateCurrentJobLocation() {
        return candidateCurrentJobLocation;
    }

    public Integer getCandidateTransportation() {
        return candidateTransportation;
    }

    public Integer getCandidateCurrentWorkShift() {
        return candidateCurrentWorkShift;
    }

    public Integer getCandidateCurrentJobRole() {
        return candidateCurrentJobRole;
    }

    public String getCandidateCurrentJobDesignation() {
        return candidateCurrentJobDesignation;
    }


    public Integer getCandidateCurrentJobDuration() {
        return candidateCurrentJobDuration;
    }

    public Integer getCandidatePastJobRole() {
        return candidatePastJobRole;
    }

    public String getCandidatePastJobCompany() {
        return candidatePastJobCompany;
    }

    public Long getCandidatePastJobSalary() {
        return candidatePastJobSalary;
    }

    public String getCandidateTimeShiftPref() {
        return candidateTimeShiftPref;
    }

    public List<Integer> getCandidateIdProof() {
        return candidateIdProof;
    }

    public Integer getCandidateSalarySlip() {
        return candidateSalarySlip;
    }

    public Integer getCandidateAppointmentLetter() {
        return candidateAppointmentLetter;
    }

    public void setCandidateAge(Integer candidateAge) {
        this.candidateAge = candidateAge;
    }

    public void setCandidatePhoneType(String candidatePhoneType) {
        this.candidatePhoneType = candidatePhoneType;
    }

    public void setCandidateHomeLocality(Integer candidateHomeLocality) {
        this.candidateHomeLocality = candidateHomeLocality;
    }

    public void setCandidateMaritalStatus(Integer candidateMaritalStatus) {
        this.candidateMaritalStatus = candidateMaritalStatus;
    }

    public void setCandidateEmail(String candidateEmail) {
        this.candidateEmail = candidateEmail;
    }

    public void setCandidatePastJobCompany(String candidatePastJobCompany) {
        this.candidatePastJobCompany = candidatePastJobCompany;
    }

    public void setCandidatePastJobRole(Integer candidatePastJobRole) {
        this.candidatePastJobRole = candidatePastJobRole;
    }

    public void setCandidatePastJobSalary(Long candidatePastJobSalary) {
        this.candidatePastJobSalary = candidatePastJobSalary;
    }

    public void setCandidateCurrentJobLocation(Integer candidateCurrentJobLocation) {
        this.candidateCurrentJobLocation = candidateCurrentJobLocation;
    }

    public void setCandidateTransportation(Integer candidateTransportation) {
        this.candidateTransportation = candidateTransportation;
    }

    public void setCandidateCurrentWorkShift(Integer candidateCurrentWorkShift) {
        this.candidateCurrentWorkShift = candidateCurrentWorkShift;
    }

    public void setCandidateCurrentJobRole(Integer candidateCurrentJobRole) {
        this.candidateCurrentJobRole = candidateCurrentJobRole;
    }

    public void setCandidateCurrentJobDesignation(String candidateCurrentJobDesignation) {
        this.candidateCurrentJobDesignation = candidateCurrentJobDesignation;
    }

    public void setCandidateCurrentJobDuration(Integer candidateCurrentJobDuration) {
        this.candidateCurrentJobDuration = candidateCurrentJobDuration;
    }

    public void setCandidateIdProof(List<Integer> candidateIdProof) {
        this.candidateIdProof = candidateIdProof;
    }

    public void setCandidateSalarySlip(Integer candidateSalarySlip) {
        this.candidateSalarySlip = candidateSalarySlip;
    }

    public void setCandidateAppointmentLetter(Integer candidateAppointmentLetter) {
        this.candidateAppointmentLetter = candidateAppointmentLetter;
    }

    public List<ExpList> getExpList() {
        return expList;
    }

    public void setExpList(List<ExpList> expList) {
        this.expList = expList;
    }

    public List<PastCompany> getPastCompany() {
        return pastCompany;
    }

    public void setPastCompany(List<PastCompany> pastCompany) {
        this.pastCompany = pastCompany;
    }

    public Long getCandidateLastWithdrawnSalary() {
        return candidateLastWithdrawnSalary;
    }

    public void setCandidateLastWithdrawnSalary(Long candidateLastWithdrawnSalary) {
        this.candidateLastWithdrawnSalary = candidateLastWithdrawnSalary;
    }

    public Integer getCandidateExperienceLetter() {
        return candidateExperienceLetter;
    }

    public void setCandidateExperienceLetter(Integer candidateExperienceLetter) {
        this.candidateExperienceLetter = candidateExperienceLetter;
    }
}
