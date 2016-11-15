package api.http.httpRequest.Workflow.preScreenEdit;

import api.http.httpRequest.AddCandidateEducationRequest;

/**
 * Created by zero on 10/11/16.
 */
public class UpdateCandidateEducation{
    public Integer candidateEducationLevel;
    public Integer candidateDegree;
    public String candidateEducationInstitute;
    public Integer candidateEducationCompletionStatus;

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
}
