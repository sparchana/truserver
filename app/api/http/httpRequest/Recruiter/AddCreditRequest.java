package api.http.httpRequest.Recruiter;

/**
 * Created by dodo on 17/10/16.
 */
public class AddCreditRequest {
    private Integer creditAmount;
    private Integer noOfCredits;
    private Integer creditCategory;

    public Integer getCreditAmount() {
        return creditAmount;
    }

    public void setCreditAmount(Integer creditAmount) {
        this.creditAmount = creditAmount;
    }

    public Integer getNoOfCredits() {
        return noOfCredits;
    }

    public void setNoOfCredits(Integer noOfCredits) {
        this.noOfCredits = noOfCredits;
    }

    public Integer getCreditCategory() {
        return creditCategory;
    }

    public void setCreditCategory(Integer creditCategory) {
        this.creditCategory = creditCategory;
    }
}
