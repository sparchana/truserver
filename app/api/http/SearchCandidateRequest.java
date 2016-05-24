package api.http;

/**
 * Created by zero on 23/5/16.
 */
public class SearchCandidateRequest {
    public String candidateName;
    public String candidateMobile;
    public String candidateJobInterest;
    public String candidateLocality;

    public String getCandidateName() {
        return candidateName;
    }

    public void setCandidateName(String candidateName) {
        this.candidateName = candidateName;
    }

    public String getCandidateMobile() {
        return candidateMobile;
    }

    public void setCandidateMobile(String candidateMobile) {
        this.candidateMobile = candidateMobile;
    }

    public void setCandidateJobInterest(String candidateJobInterest) {
        this.candidateJobInterest = candidateJobInterest;
    }

    public void setCandidateLocality(String candidateLocality) {
        this.candidateLocality = candidateLocality;
    }
}
