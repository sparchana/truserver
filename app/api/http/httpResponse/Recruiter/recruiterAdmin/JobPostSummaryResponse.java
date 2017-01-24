package api.http.httpResponse.Recruiter.recruiterAdmin;

/**
 * Created by zero on 24/1/17.
 */
public class JobPostSummaryResponse {
    private String jobTitle;
    private String jobPostedOn;
    private float fulfilmentStatus;
    private int totalSmsSent;
    private int totalApplicants;
    private int totalInterviewConducted;
    private int cycleTime;

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

    public float getFulfilmentStatus() {
        return fulfilmentStatus;
    }

    public void setFulfilmentStatus(float fulfilmentStatus) {
        this.fulfilmentStatus = fulfilmentStatus;
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

    public int getCycleTime() {
        return cycleTime;
    }

    public void setCycleTime(int cycleTime) {
        this.cycleTime = cycleTime;
    }
}
