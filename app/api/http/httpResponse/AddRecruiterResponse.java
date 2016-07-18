package api.http.httpResponse;

/**
 * Created by batcoder1 on 18/7/16.
 */
public class AddRecruiterResponse {
    public static final int STATUS_SUCCESS = 1;
    public static final int STATUS_FAILURE = 2;
    public static final int STATUS_EXISTS = 3;
    public static final int STATUS_UPDATE = 4;

    public int status;
    public Long recruiterId;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public Long getRecruiterId() {
        return recruiterId;
    }

    public void setRecruiterId(Long recruiterId) {
        this.recruiterId = recruiterId;
    }
}
