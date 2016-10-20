package api.http.httpRequest.Recruiter;

/**
 * Created by dodo on 17/10/16.
 */
public class AddCreditRequest {
    private Integer noOfContactCredits;
    private Integer noOfInterviewCredits;

    public Integer getNoOfContactCredits() {
        return noOfContactCredits;
    }

    public void setNoOfContactCredits(Integer noOfContactCredits) {
        this.noOfContactCredits = noOfContactCredits;
    }

    public Integer getNoOfInterviewCredits() {
        return noOfInterviewCredits;
    }

    public void setNoOfInterviewCredits(Integer noOfInterviewCredits) {
        this.noOfInterviewCredits = noOfInterviewCredits;
    }
}
