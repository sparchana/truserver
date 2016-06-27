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
}
