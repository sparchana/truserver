package api.http;

/**
 * Created by batcoder1 on 31/5/16.
 */
public class AddCandidateEducationRequest extends AddCandidateExperienceRequest {
    public Integer candidateEducationLevel;
    public Integer candidateDegree ;
    public String candidateEducationInstitute;

    public Integer getCandidateEducationLevel() {
        return candidateEducationLevel;
    }

    public Integer getCandidateDegree() {
        return candidateDegree;
    }

    public String getCandidateEducationInstitute() {
        return candidateEducationInstitute;
    }
}
