package api;

/**
 * Created by batcoder1 on 25/4/16.
 */
public class CandidateSignUpResponse {
    public static final int STATUS_SUCCESS = 1;
    public static final int STATUS_FAILURE = 2;
    public static final int STATUS_EXISTS = 3;

    public int status;

    public void setStatus(int status) {
        this.status = status;
    }
}
