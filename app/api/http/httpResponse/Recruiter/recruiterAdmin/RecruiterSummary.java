package api.http.httpResponse.Recruiter.recruiterAdmin;

/**
 * Created by zero on 4/2/17.
 */
public class RecruiterSummary {
    private Long recruiterId;
    private String recruiterName;
    private String recruiterMobile;
    private Integer noOfJobPosted;
    private Integer totalCandidatesApplied;
    private Integer totalInterviewConducted;
    private Integer totalSelected;
    private String percentageFulfillment;


    public String getRecruiterName() {
        return recruiterName;
    }

    public void setRecruiterName(String recruiterName) {
        this.recruiterName = recruiterName;
    }

    public String getRecruiterMobile() {
        return recruiterMobile;
    }

    public void setRecruiterMobile(String recruiterMobile) {
        this.recruiterMobile = recruiterMobile;
    }

    public Integer getNoOfJobPosted() {
        return noOfJobPosted;
    }

    public void setNoOfJobPosted(Integer noOfJobPosted) {
        this.noOfJobPosted = noOfJobPosted;
    }

    public Integer getTotalCandidatesApplied() {
        return totalCandidatesApplied;
    }

    public void setTotalCandidatesApplied(Integer totalCandidatesApplied) {
        this.totalCandidatesApplied = totalCandidatesApplied;
    }

    public Integer getTotalInterviewConducted() {
        return totalInterviewConducted;
    }

    public void setTotalInterviewConducted(Integer totalInterviewConducted) {
        this.totalInterviewConducted = totalInterviewConducted;
    }

    public Integer getTotalSelected() {
        return totalSelected;
    }

    public void setTotalSelected(Integer totalSelected) {
        this.totalSelected = totalSelected;
    }

    public Long getRecruiterId() {
        return recruiterId;
    }

    public void setRecruiterId(Long recruiterId) {
        this.recruiterId = recruiterId;
    }

    public String getPercentageFulfillment() {
        return percentageFulfillment;
    }

    public void setPercentageFulfillment(String percentageFulfillment) {
        this.percentageFulfillment = percentageFulfillment;
    }
}
