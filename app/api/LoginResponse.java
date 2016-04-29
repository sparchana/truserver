package api;

/**
 * Created by batcoder1 on 26/4/16.
 */
public class LoginResponse {
    public static final int STATUS_SUCCESS = 1;
    public static final int STATUS_FAILURE = 2;
    public static final int STATUS_NO_USER = 3;

    public int status;
    public long accountStatus;
    public String candidateName;
    public String candidateEmail;

    public void setStatus(int status) {
        this.status = status;
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
