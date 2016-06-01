package api.http;

/**
 * Created by batcoder1 on 31/5/16.
 */
public class AddCandidateEducationRequest extends AddCandidateExperienceRequest {
    public int candidateEducationLevel;
    public int candidateDegree ;
    public String candidateEducationInstitute;

    public int getCandidateEducationLevel() {
        return candidateEducationLevel;
    }

    public int getCandidateDegree() {
        return candidateDegree;
    }

    public String getCandidateEducationInstitute() {
        return candidateEducationInstitute;
    }
}
