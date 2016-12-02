package api.http.httpResponse;

/**
 * Created by batcoder1 on 17/6/16.
 */
public class ApplyJobResponse {
    public static final int STATUS_SUCCESS = 1;
    public static final int STATUS_FAILURE = 2;
    public static final int STATUS_EXISTS = 3;
    public static final int STATUS_NO_CANDIDATE = 4;
    public static final int STATUS_NO_JOB = 5;

    public int status;
    public boolean isPreScreenAvailable;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public boolean isPreScreenAvailable() {
        return isPreScreenAvailable;
    }

    public void setPreScreenAvailable(boolean preScreenAvailable) {
        isPreScreenAvailable = preScreenAvailable;
    }
}
