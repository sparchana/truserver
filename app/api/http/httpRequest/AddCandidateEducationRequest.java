package api.http.httpRequest;

/**
 * Created by batcoder1 on 31/5/16.
 */
public class AddCandidateEducationRequest extends AddCandidateExperienceRequest {
    public Integer candidateEducationLevel;
    public Integer candidateDegree;
    public String candidateEducationInstitute;
    public Integer candidateEducationCompletionStatus;

    public Integer getCandidateEducationLevel() {
        return candidateEducationLevel;
    }

    public Integer getCandidateDegree() {
        return candidateDegree;
    }

    public String getCandidateEducationInstitute() {
        return candidateEducationInstitute;
    }

    public void setCandidateEducationLevel(Integer candidateEducationLevel) {
        this.candidateEducationLevel = candidateEducationLevel;
    }

    public void setCandidateDegree(Integer candidateDegree) {
        this.candidateDegree = candidateDegree;
    }

    public void setCandidateEducationInstitute(String candidateEducationInstitute) {
        this.candidateEducationInstitute = candidateEducationInstitute;
    }

    public Integer getCandidateEducationCompletionStatus() {
        return candidateEducationCompletionStatus;
    }

    public void setEducationStatus(Integer candidateEducationCompletionStatus) {
        this.candidateEducationCompletionStatus = candidateEducationCompletionStatus;
    }
}
