package api.http.httpResponse;

/**
 * Created by adarsh on 19/9/16.
 */
public class VerifyCandidateResponse {
    public static final int STATUS_SUCCESS = 1;
    public static final int STATUS_WRONG_OTP = 2;
    public static final int STATUS_FAILURE = 3;

    public int status;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
