package api.http.httpResponse;

/**
 * Created by batcoder1 on 17/6/16.
 */
public class AddCompanyResponse {
    public static final int STATUS_SUCCESS = 1;
    public static final int STATUS_UPDATE_SUCCESS = 2;
    public static final int STATUS_FAILURE = 3;
    public static final int STATUS_EXISTS = 4;

    public int status;
    public Long companyId;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public Long getCompanyId() {
        return companyId;
    }

    public void setCompanyId(Long companyId) {
        this.companyId = companyId;
    }
}
