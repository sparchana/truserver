package api.http.httpResponse;

/**
 * Created by batcoder1 on 26/4/16.
 */
public class LoginResponse {
    public static final int STATUS_SUCCESS = 1;
    public static final int STATUS_FAILURE = 2;
    public static final int STATUS_NO_USER = 3;
    public static final int STATUS_WRONG_PASSWORD = 4;

    public int status;
    public int isAssessed;
    public int minProfile;
    public long candidateId;
    public long leadId;
    public String candidateFirstName;
    public String candidateLastName;
    public String candidateEmail;
    public int candidateJobPrefStatus;
    public int candidateHomeLocalityStatus;

    public int getMinProfile() {
        return minProfile;
    }

    public void setMinProfile(int minProfile) {
        this.minProfile = minProfile;
    }

    public void setStatus(int status) {
        this.status = status;
    }
    public void setIsAssessed(int isAssessed) {
        this.isAssessed = isAssessed;
    }
    public void setCandidateId(long candidateId) {
        this.candidateId = candidateId;
    }
    public void setLeadId(long leadId) {
        this.leadId = leadId;
    }
    public void setCandidateFirstName(String candidateFirstName) {
        this.candidateFirstName = candidateFirstName;
    }
    public void setCandidateLastName(String candidateLastName) {
        this.candidateLastName = candidateLastName;
    }
    public void setCandidateEmail(String candidateEmail) {
        this.candidateEmail = candidateEmail;
    }

    public int getStatus() {
        return status;
    }

    public int getIsAssessed() {
        return isAssessed;
    }

    public long getCandidateId() {
        return candidateId;
    }

    public long getLeadId() {
        return leadId;
    }

    public String getCandidateFirstName() {
        return candidateFirstName;
    }

    public String getCandidateLastName() {
        return candidateLastName;
    }

    public String getCandidateEmail() {
        return candidateEmail;
    }

    public int getCandidateJobPrefStatus() {
        return candidateJobPrefStatus;
    }

    public void setCandidateJobPrefStatus(int candidateJobPrefStatus) {
        this.candidateJobPrefStatus = candidateJobPrefStatus;
    }

    public int getCandidateHomeLocalityStatus() {
        return candidateHomeLocalityStatus;
    }

    public void setCandidateHomeLocalityStatus(int candidateHomeLocalityStatus) {
        this.candidateHomeLocalityStatus = candidateHomeLocalityStatus;
    }
}
