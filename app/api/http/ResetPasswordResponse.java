package api.http;

/**
 * Created by batcoder1 on 28/4/16.
 */
public class ResetPasswordResponse {
    public static final int STATUS_SUCCESS = 1;
    public static final int STATUS_FAILURE = 2;
    public static final int STATUS_EXISTS = 3;

    public int status;
    public int otp;
    public long accountStatus;
    public long candidateId;
    public String candidateName;
    public String candidateEmail;
    public String candidateMobile;

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
}
