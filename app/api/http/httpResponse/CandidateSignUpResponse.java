package api.http.httpResponse;

/**
 * Created by batcoder1 on 25/4/16.
 */
public class CandidateSignUpResponse {
    public static final int STATUS_SUCCESS = 1;
    public static final int STATUS_FAILURE = 2;
    public static final int STATUS_EXISTS = 3;
    public static final int STATUS_INCORRECT_OTP = 4;

    public int status;
    public int isAssessed;
    public int minProfile;
    public int otp;
    public long leadId;
    public long accountStatus;
    public long candidateId;
    public String candidateFirstName;
    public String candidateLastName;
    public String candidateEmail;
    public int candidateJobPrefStatus;
    public int candidateHomeLocalityStatus;
    public int gender;

    /*
     * To cater mobile needs
    */
    public Double candidateHomeLat;
    public Double candidateHomeLng;
    public Long candidatePrefJobRoleIdOne;
    public Long candidatePrefJobRoleIdTwo;
    public Long candidatePrefJobRoleIdThree;
    public String candidateHomeLocalityName;

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

    public void setStatus(int status) {
        this.status = status;
    }
    public void setIsAssessed(int isAssessed) {
        this.isAssessed = isAssessed;
    }
    public void setOtp(int otp) {
        this.otp = otp;
    }
    public void setCandidateId(long candidateId) {
        this.candidateId = candidateId;
    }
    public void setLeadId(long leadId) {
        this.leadId = leadId;
    }
    public void setAccountStatus(long accountStatus) {
        this.status = status;
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

    public int getMinProfile() {
        return minProfile;
    }

    public void setMinProfile(int minProfile) {
        this.minProfile = minProfile;
    }

    public int getStatus() {
        return status;
    }

    public int getOtp() {
        return otp;
    }

    public int getIsAssessed() {
        return isAssessed;
    }

    public long getLeadId() {
        return leadId;
    }

    public long getAccountStatus() {
        return accountStatus;
    }

    public long getCandidateId() {
        return candidateId;
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

    public Double getCandidateHomeLat() {
        return candidateHomeLat;
    }

    public void setCandidateHomeLat(Double candidateHomeLat) {
        this.candidateHomeLat = candidateHomeLat;
    }

    public Double getCandidateHomeLng() {
        return candidateHomeLng;
    }

    public void setCandidateHomeLng(Double candidateHomeLng) {
        this.candidateHomeLng = candidateHomeLng;
    }

    public Long getCandidatePrefJobRoleIdOne() {
        return candidatePrefJobRoleIdOne;
    }

    public void setCandidatePrefJobRoleIdOne(Long candidatePrefJobRoleIdOne) {
        this.candidatePrefJobRoleIdOne = candidatePrefJobRoleIdOne;
    }

    public Long getCandidatePrefJobRoleIdTwo() {
        return candidatePrefJobRoleIdTwo;
    }

    public void setCandidatePrefJobRoleIdTwo(Long candidatePrefJobRoleIdTwo) {
        this.candidatePrefJobRoleIdTwo = candidatePrefJobRoleIdTwo;
    }

    public Long getCandidatePrefJobRoleIdThree() {
        return candidatePrefJobRoleIdThree;
    }

    public void setCandidatePrefJobRoleIdThree(Long candidatePrefJobRoleIdThree) {
        this.candidatePrefJobRoleIdThree = candidatePrefJobRoleIdThree;
    }

    public String getCandidateHomeLocalityName() {
        return candidateHomeLocalityName;
    }

    public void setCandidateHomeLocalityName(String candidateHomeLocalityName) {
        this.candidateHomeLocalityName = candidateHomeLocalityName;
    }

    public int getGender() {
        return gender;
    }

    public void setGender(int gender) {
        this.gender = gender;
    }
}
