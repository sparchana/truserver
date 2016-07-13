package api.http.httpRequest;

import java.util.Date;

/**
 * Created by zero on 12/7/16.
 */
public class AnalyticsRequest {
    private Date fromThisDate;
    private Date toThisDate;
    private String Metrics;

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

    public String getMetrics() {
        return Metrics;
    }

    public void setMetrics(String metrics) {
        Metrics = metrics;
    }
}
