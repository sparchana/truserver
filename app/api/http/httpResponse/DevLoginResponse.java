package api.http.httpResponse;

/**
 * Created by zero on 30/4/16.
 */
public class DevLoginResponse {
    public static final int UNKNOWN = 0;
    public static final int SUCCESS = 1;
    public static final int FAILURE = 2;
    public static final int PASSWORD_INCORRECT = 3;
    public static final int NO_ACCOUNT = 4;

    public long developerId;
    public String developerSessionId;
    public String developerSessionIdExpiryMillis;
    public int status;

    public void setStatus(int status) {
        this.status = status;
    }

    public long getDeveloperId() {
        return developerId;
    }

    public void setDeveloperId(long developerId) {
        this.developerId = developerId;
    }

    public String getDeveloperSessionId() {
        return developerSessionId;
    }

    public void setDeveloperSessionId(String developerSessionId) {
        this.developerSessionId = developerSessionId;
    }

    public String getDeveloperSessionIdExpiryMillis() {
        return developerSessionIdExpiryMillis;
    }

    public void setDeveloperSessionIdExpiryMillis(String developerSessionIdExpiryMillis) {
        this.developerSessionIdExpiryMillis = developerSessionIdExpiryMillis;
    }

    public int getStatus() {
        return status;
    }
}
