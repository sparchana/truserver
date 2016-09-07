package api.http.httpResponse;

/**
 * Created by batcoder1 on 28/4/16.
 */
public class ResetPasswordResponse {
    public static final int STATUS_SUCCESS = 1;
    public static final int STATUS_FAILURE = 2;
    public static final int STATUS_EXISTS = 3;

    public int status;
    public int otp;
    public int minProfile;
    public long accountStatus;
    public long candidateId;
    public String candidateName;
    public String candidateEmail;
    public String candidateMobile;

    public int getMinProfile() {
        return minProfile;
    }

    public void setMinProfile(int minProfile) {
        this.minProfile = minProfile;
    }

    public void setStatus(int status) {
        this.status = status;
    }
    public void setOtp(int otp) {
        this.otp = otp;
    }
    public void setAccountStatus(long accountStatus) {
        this.status = status;
    }
    public void setCandidateId(long candidateId) {
        this.candidateId = candidateId;
    }
    public void setCandidateName(String candidateName) {
        this.candidateName = candidateName;
    }
    public void setCandidateEmail(String candidateEmail) {
        this.candidateEmail = candidateEmail;
    }
    public void setCandidateMobile(String candidateMobile) {
        this.candidateMobile = candidateMobile;
    }

    public int getStatus() {
        return status;
    }

    public int getOtp() {
        return otp;
    }

    public long getAccountStatus() {
        return accountStatus;
    }

    public long getCandidateId() {
        return candidateId;
    }

    public String getCandidateName() {
        return candidateName;
    }

    public String getCandidateEmail() {
        return candidateEmail;
    }

    public String getCandidateMobile() {
        return candidateMobile;
    }
}
