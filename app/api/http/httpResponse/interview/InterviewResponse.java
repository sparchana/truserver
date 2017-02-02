package api.http.httpResponse.interview;

/**
 * Created by zero on 2/12/16.
 */
public class InterviewResponse {
    public int status;
    public String statusTitle;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getStatusTitle() {
        return statusTitle;
    }

    public void setStatusTitle(String statusTitle) {
        this.statusTitle = statusTitle;
    }
}
