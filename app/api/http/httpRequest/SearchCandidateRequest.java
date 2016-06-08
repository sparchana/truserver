package api.http.httpRequest;

import java.util.Date;

/**
 * Created by zero on 23/5/16.
 */
public class SearchCandidateRequest {
    public String candidateName;
    public String candidateMobile;
    public String candidateJobInterest; // ',' separated jobRoleId values
    public String candidateLocality; // ',' separated candidateLocalityId values
    public Date fromThisDate;
    public Date toThisDate;

    public Date getFromThisDate() {
        return fromThisDate;
    }

    public void setFromThisDate(Date fromThisDate) {
        this.fromThisDate = fromThisDate;
    }

    public Date getToThisDate() {
        return toThisDate;
    }

    public void setToThisDate(Date toThisDate) {
        this.toThisDate = toThisDate;
    }

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
