package api.http.httpResponse;

/**
 * Created by zero on 16/6/16.
 */
public class AddOrUpdateFollowUpResponse {
    public static final int STATUS_FOLLOWUP_CREATE_SUCCESS = 1;
    public static final int STATUS_FOLLOWUP_UPDATE_SUCCESS = 2;
    public static final int STATUS_FOLLOWUP_REMOVED_SUCCESS = 3;
    public static final int STATUS_FOLLOWUP_FAILURE = 4;

    public int status;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
