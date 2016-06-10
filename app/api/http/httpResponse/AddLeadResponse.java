package api.http.httpResponse;

/**
 * Created by zero on 23/4/16.
 */
public class AddLeadResponse {
    public static final int STATUS_SUCCESS = 1;
    public static final int STATUS_FAILURE = 2;
    public static final int STATUS_EXISTS = 3;

    public int status;

    public void setStatus(int status) {
        this.status = status;
    }
}
