package api.http.httpResponse.Recruiter.recruiterAdmin;

/**
 * Created by zero on 24/1/17.
 */
public class JobPostSummaryResponse {
    private Long jobPostId;
    private String jobTitle;
    private String jobPostedOn;
    private String percentageFulfillment;
    private int totalSmsSent;
    private int totalApplicants;
    private int totalInterviewConducted;
    private String cycleTime;

    public String getJobTitle() {
        return jobTitle;
    }

    public void setJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
    }

    public String getJobPostedOn() {
        return jobPostedOn;
    }

    public void setJobPostedOn(String jobPostedOn) {
        this.jobPostedOn = jobPostedOn;
    }

    public String getPercentageFulfillment() {
        return percentageFulfillment;
    }

    public void setPercentageFulfillment(String percentageFulfillment) {
        this.percentageFulfillment = percentageFulfillment;
    }

    public int getTotalSmsSent() {
        return totalSmsSent;
    }

    public void setTotalSmsSent(int totalSmsSent) {
        this.totalSmsSent = totalSmsSent;
    }

    public int getTotalApplicants() {
        return totalApplicants;
    }

    public void setTotalApplicants(int totalApplicants) {
        this.totalApplicants = totalApplicants;
    }

    public int getTotalInterviewConducted() {
        return totalInterviewConducted;
    }

    public void setTotalInterviewConducted(int totalInterviewConducted) {
        this.totalInterviewConducted = totalInterviewConducted;
    }

    public String getCycleTime() {
        return cycleTime;
    }

    public void setCycleTime(String cycleTime) {
        this.cycleTime = cycleTime;
    }

    public Long getJobPostId() {
        return jobPostId;
    }

    public void setJobPostId(Long jobPostId) {
        this.jobPostId = jobPostId;
    }
}
