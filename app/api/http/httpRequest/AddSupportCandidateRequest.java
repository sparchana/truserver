package api.http.httpRequest;

import java.sql.Date;
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
    public List<Integer> candidateIdProof;
    public Integer candidateSalarySlip ;
    public Integer candidateAppointmentLetter ;
    public Integer candidateExperienceLetter ;
    public String supportNote;
    public Boolean deactivationStatus;
    public Integer deactivationReason;
    public Date deactivationExpiryDate;

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
        String companyName;
        Boolean current;

        public PastCompany(){}

        public Integer getJobRoleId() {
            return jobRoleId;
        }

        public void setJobRoleId(Integer jobRoleId) {
            this.jobRoleId = jobRoleId;
        }

        public String getCompanyName() {
            return companyName;
        }

        public void setCompanyName(String companyName) {
            this.companyName = companyName;
        }

        public Boolean getCurrent() {
            return current;
        }

        public void setCurrent(Boolean current) {
            this.current = current;
        }
    }
    public List<ExpList> expList;
    public List<PastCompany> pastCompanyList;

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

    public List<PastCompany> getPastCompanyList() {
        return pastCompanyList;
    }

    public void setPastCompanyList(List<PastCompany> pastCompany) {
        this.pastCompanyList = pastCompany;
    }

    public Integer getCandidateExperienceLetter() {
        return candidateExperienceLetter;
    }

    public void setCandidateExperienceLetter(Integer candidateExperienceLetter) {
        this.candidateExperienceLetter = candidateExperienceLetter;
    }

    public Boolean getDeactivationStatus() {
        return deactivationStatus;
    }

    public void setDeactivationStatus(Boolean deactivationStatus) {
        this.deactivationStatus = deactivationStatus;
    }

    public Integer getDeactivationReason() {
        return deactivationReason;
    }

    public void setDeactivationReason(Integer deactivationReason) {
        this.deactivationReason = deactivationReason;
    }

    public Date getDeactivationExpiryDate() {
        return deactivationExpiryDate;
    }

    public void setDeactivationExpiryDate(Date deactivationExpiryDate) {
        this.deactivationExpiryDate = deactivationExpiryDate;
    }
}
