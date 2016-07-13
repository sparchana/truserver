package api.http.httpRequest;

import play.Logger;

import java.util.Date;
import java.util.List;

/**
 * Created by zero on 12/7/16.
 */
public class AnalyticsRequest {
    public Date fromThisDate;
    public Date toThisDate;
    public List<String> Metrics;

    public Date getFromThisDate() {

        if(this.fromThisDate == null) {
            this.fromThisDate = new Date();
            Logger.info("No FromDate Supplied to Analytics hence setting from date to " + this.fromThisDate);
        }

        if(this.fromThisDate.after(this.toThisDate)){
            fromThisDate = toThisDate;
            Logger.info("FromDate is greater than toDate hence from date = toDate");
        }

        return this.fromThisDate;
    }

    public void setFromThisDate(Date fromThisDate) {
        this.fromThisDate = fromThisDate;
    }

    public Date getToThisDate() {
        if(this.toThisDate == null) {
            this.toThisDate = new Date();
            Logger.info("No toDate Supplied to Analytics hence setting from date to " + this.toThisDate);
        }
        Date currentDate = new Date();
        if(toThisDate.after(currentDate)){
            Logger.info("toDate is in future hence converting to currentDate");
            toThisDate = currentDate;
        }
        return this.toThisDate;
    }

    public void setToThisDate(Date toThisDate) {
        this.toThisDate = toThisDate;
    }

    public List<String> getMetrics() {
        return Metrics;
    }

    public void setMetrics(List<String> metrics) {
        Metrics = metrics;
    }
}
