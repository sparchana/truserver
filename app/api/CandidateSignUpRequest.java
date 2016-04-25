package api;

/**
 * Created by batcoder1 on 25/4/16.
 */
public class CandidateSignUpRequest {
    protected String candidateName;
    protected String candidateMobile;
    protected int candidateAge;

    public void setCandidateName(String candidateName) {
        this.candidateName = candidateName;
    }

    public String getCandidateName() {
        return candidateName;
    }

    public void setCandidateMobile(String candidateMobile) {
        this.candidateMobile = candidateMobile;
    }

    public String getCandidateMobile() {
        return candidateMobile;
    }

    public void setCandidateAge(int candidateAge) {
        this.candidateAge = candidateAge;
    }

    public int getCandidateAge() {
        return candidateAge;
    }
}
