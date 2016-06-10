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
    public Integer candidateCurrentJobLocation;
    public Integer candidateTransportation ;
    public Integer candidateCurrentWorkShift ;
    public Integer candidateCurrentJobRole;
    public String candidateCurrentJobDesignation;
    public Integer candidateCurrentJobDuration ;
    public List<Integer> candidateIdProof;
    public Integer candidateSalarySlip ;
    public Integer candidateAppointmentLetter ;


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

}
