package api.http.httpResponse.Workflow.smsJobApplyFlow;


import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Created by zero on 18/1/17.
 */
public class PostApplyInShortResponse {

    public enum Status {
        UNKNOWN,
        FAILURE,
        BAD_REQUEST,
        SUCCESS,
        BAD_PARAMS, ALREADY_APPLIED;

        @JsonValue
        public String toJson() {
            return name().toLowerCase();
        }
    }

    private Status status;

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }
    public int getStatusCode() {
        return this.status.ordinal();
    }
}
