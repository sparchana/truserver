package api.http.httpRequest;

/**
 * Created by batcoder1 on 10/5/16.
 */
public class AddSupportCandidateRequest extends AddCandidateEducationRequest {
    public Integer candidateAge ;
    public String candidatePhoneType;
    public String candidateHomeLocality;
    public Integer candidateMaritalStatus ;
    public String candidateEmail;
    public String candidatePastJobCompany;
    public String candidatePastJobRole;
    public Long candidatePastJobSalary ;
    public String candidateCurrentJobLocation;
    public Integer candidateTransportation ;
    public Integer candidateCurrentWorkShift ;
    public String candidateCurrentJobRole;
    public String candidateCurrentJobDesignation;
    public Integer candidateCurrentJobDuration ;
    public String candidateIdProof;
    public Integer candidateSalarySlip ;
    public Integer candidateAppointmentLetter ;


    public Integer getCandidateAge() {
        return candidateAge;
    }

    public String getCandidateHomeLocality() {
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

    public String getCandidateCurrentJobLocation() {
        return candidateCurrentJobLocation;
    }

    public Integer getCandidateTransportation() {
        return candidateTransportation;
    }

    public Integer getCandidateCurrentWorkShift() {
        return candidateCurrentWorkShift;
    }

    public String getCandidateCurrentJobRole() {
        return candidateCurrentJobRole;
    }

    public String getCandidateCurrentJobDesignation() {
        return candidateCurrentJobDesignation;
    }


    public Integer getCandidateCurrentJobDuration() {
        return candidateCurrentJobDuration;
    }

    public String getCandidatePastJobRole() {
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

    public String getCandidateIdProof() {
        return candidateIdProof;
    }

    public Integer getCandidateSalarySlip() {
        return candidateSalarySlip;
    }

    public Integer getCandidateAppointmentLetter() {
        return candidateAppointmentLetter;
    }

}
