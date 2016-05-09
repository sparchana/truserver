package api.http;

/**
 * Created by zero on 28/4/16.
 */
public class AddCandidateRequest {
    public String candidateUUID;
    public long leadId;
    public String candidateName = " ";
    public String candidateMobile = " ";
    public String candidateJobInterest = " ";
    public String candidateLocality = " ";
    public String candidateNote = " ";


    public void setCandidateName(String candidateName) {
        this.candidateName = candidateName;
    }

    public String getCandidateUUID() {
        return candidateUUID;
    }

    public void setCandidateUUID(String candidateUUID) {
        this.candidateUUID = candidateUUID;
    }

    public String getCandidateName() {
        return candidateName;
    }
    public String getCandidateLocality() {
        return candidateLocality;
    }
    public String getCandidateJobInterest() {
        return candidateJobInterest;
    }
    public long getLeadId() {
        return leadId;
    }

    public void setCandidateMobile(String candidateMobile) {
        this.candidateMobile = candidateMobile;
    }

    public String getCandidateMobile() {
        return candidateMobile;
    }

}
