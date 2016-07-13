package api.http.httpResponse;

/**
 * Created by zero on 12/7/16.
 */
public class GlobalAnalyticsResponse {
    public Integer totalNumberOfCandidate;
    public Integer totalNumberOfVerifiedCandidate;
    public Integer totalNumberOfSelfSignUp;
    public Integer totalNumberOfSupportSingUp;
    public Integer totaNumberOfLeads;

    public Integer getTotalNumberOfCandidate() {
        return totalNumberOfCandidate;
    }

    public void setTotalNumberOfCandidate(Integer totalNumberOfCandidate) {
        this.totalNumberOfCandidate = totalNumberOfCandidate;
    }

    public Integer getTotalNumberOfVerifiedCandidate() {
        return totalNumberOfVerifiedCandidate;
    }

    public void setTotalNumberOfVerifiedCandidate(Integer totalNumberOfVerifiedCandidate) {
        this.totalNumberOfVerifiedCandidate = totalNumberOfVerifiedCandidate;
    }

    public Integer getTotalNumberOfSelfSignUp() {
        return totalNumberOfSelfSignUp;
    }

    public void setTotalNumberOfSelfSignUp(Integer totalNumberOfSelfSignUp) {
        this.totalNumberOfSelfSignUp = totalNumberOfSelfSignUp;
    }

    public Integer getTotalNumberOfSupportSingUp() {
        return totalNumberOfSupportSingUp;
    }

    public void setTotalNumberOfSupportSingUp(Integer totalNumberOfSupportSingUp) {
        this.totalNumberOfSupportSingUp = totalNumberOfSupportSingUp;
    }

    public Integer getTotaNumberOfLeads() {
        return totaNumberOfLeads;
    }

    public void setTotaNumberOfLeads(Integer totaNumberOfLeads) {
        this.totaNumberOfLeads = totaNumberOfLeads;
    }
}
