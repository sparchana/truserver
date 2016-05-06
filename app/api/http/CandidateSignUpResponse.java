package api.http;

/**
 * Created by batcoder1 on 25/4/16.
 */
public class CandidateSignUpResponse {
    public static final int STATUS_SUCCESS = 1;
    public static final int STATUS_FAILURE = 2;
    public static final int STATUS_EXISTS = 3;
    public static final int STATUS_INCORRECT_OTP = 4;

    public int status;
    public int selfRegister;
    public int otp;
    public long accountStatus;
    public long candidateId;
    public String candidateName;
    public String candidateEmail;

    public void setStatus(int status) {
        this.status = status;
    }
    public void setSelfRegister(int selfRegister) {
        this.selfRegister = selfRegister;
    }
    public void setOtp(int otp) {
        this.otp = otp;
    }
    public void setCandidateId(long candidateId) {
        this.candidateId = candidateId;
    }
    public void setAccountStatus(long accountStatus) {
        this.status = status;
    }
    public void setCandidateName(String candidateName) {
        this.candidateName = candidateName;
    }
    public void setCandidateEmail(String candidateEmail) {
        this.candidateEmail = candidateEmail;
    }
}
