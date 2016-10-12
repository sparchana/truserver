package api.http.httpResponse.Workflow;

/**
 * Created by zero on 8/10/16.
 */
public class WorkflowResponse {
    public enum STATUS {
        UNKNOWN,
        FAILED,
        SUCCESS
    }
    public STATUS status;
    public boolean shouldProceed;
    public Object redirectUrl;
    public Object message;
    public Object nextView;

    public STATUS getStatus() {
        return status;
    }

    public void setStatus(STATUS status) {
        this.status = status;
    }

    public boolean isShouldProceed() {
        return shouldProceed;
    }

    public void setShouldProceed(boolean shouldProceed) {
        this.shouldProceed = shouldProceed;
    }

    public Object getRedirectUrl() {
        return redirectUrl;
    }

    public void setRedirectUrl(Object redirectUrl) {
        this.redirectUrl = redirectUrl;
    }

    public Object getMessage() {
        return message;
    }

    public void setMessage(Object message) {
        this.message = message;
    }

    public Object getNextView() {
        return nextView;
    }

    public void setNextView(Object nextView) {
        this.nextView = nextView;
    }
}

