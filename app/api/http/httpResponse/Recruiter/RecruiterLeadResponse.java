package api.http.httpResponse.Recruiter;

/**
 * Created by dodo on 5/10/16.
 */
public class RecruiterLeadResponse {
    public static final int STATUS_SUCCESS = 1;
    public static final int STATUS_FAILURE = 2;

    public int status;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
