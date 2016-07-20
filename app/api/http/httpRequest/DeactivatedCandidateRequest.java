package api.http.httpRequest;

import java.sql.Date;

/**
 * Created by zero on 19/7/16.
 */
public class DeactivatedCandidateRequest {
    public Date fromThisDate;
    public Date toThisDate;

    public Date getFromThisDate() {
        return fromThisDate;
    }

    public void setFromThisDate(Date fromThisDate) {
        this.fromThisDate = fromThisDate;
    }

    public Date getToThisDate() {
        return toThisDate;
    }

    public void setToThisDate(Date toThisDate) {
        this.toThisDate = toThisDate;
    }
}
