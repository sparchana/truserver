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
    public String authSessionId;
    public Long sessionExpiryInMilliSecond;

    public Integer isCandidateVerified;

    /*
    * To cater mobile needs
    */
    public Double candidateHomeLat;
    public Double candidateHomeLng;
    public Long candidatePrefJobRoleIdOne;
    public Long candidatePrefJobRoleIdTwo;
    public Long candidatePrefJobRoleIdThree;
    public String candidateHomeLocalityName;

    public Integer getIsCandidateVerified() {
        return isCandidateVerified;
    }

    public void setIsCandidateVerified(Integer isCandidateVerified) {
        this.isCandidateVerified = isCandidateVerified;
    }

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
    public void setAuthSessionId(String authSessionId) {
        this.authSessionId = authSessionId;
    }
    public void setSessionExpiryInMilliSecond(Long sessionExpiryInMilliSecond) {
        this.sessionExpiryInMilliSecond = sessionExpiryInMilliSecond;
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
    public String getAuthSessionId() {
        return authSessionId;
    }

    public Long getSessionExpiryInMilliSecond() {
        return sessionExpiryInMilliSecond;
    }

    public Double getCandidateHomeLng() {
        return candidateHomeLng;
    }

    public void setCandidateHomeLng(Double candidateHomeLng) {
        this.candidateHomeLng = candidateHomeLng;
    }

    public Double getCandidateHomeLat() {
        return candidateHomeLat;
    }

    public void setCandidateHomeLat(Double candidateHomeLat) {
        this.candidateHomeLat = candidateHomeLat;
    }

    public Long getCandidatePrefJobRoleIdOne() {
        return candidatePrefJobRoleIdOne;
    }

    public void setCandidatePrefJobRoleIdOne(Long candidatePrefJobRoleIdOne) {
        this.candidatePrefJobRoleIdOne = candidatePrefJobRoleIdOne;
    }

    public Long getCandidatePrefJobRoleIdThree() {
        return candidatePrefJobRoleIdThree;
    }

    public void setCandidatePrefJobRoleIdThree(Long candidatePrefJobRoleIdThree) {
        this.candidatePrefJobRoleIdThree = candidatePrefJobRoleIdThree;
    }

    public Long getCandidatePrefJobRoleIdTwo() {
        return candidatePrefJobRoleIdTwo;
    }

    public void setCandidatePrefJobRoleIdTwo(Long candidatePrefJobRoleIdTwo) {
        this.candidatePrefJobRoleIdTwo = candidatePrefJobRoleIdTwo;
    }

    public String getCandidateHomeLocalityName() {
        return candidateHomeLocalityName;
    }

    public void setCandidateHomeLocalityName(String candidateHomeLocalityName) {
        this.candidateHomeLocalityName = candidateHomeLocalityName;
    }
}
