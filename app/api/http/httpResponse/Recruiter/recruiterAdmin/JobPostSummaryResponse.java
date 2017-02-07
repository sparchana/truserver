package api.http.httpResponse.Recruiter.recruiterAdmin;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zero on 24/1/17.
 */
public class JobPostSummaryResponse {
    public String recruiterName;
    public List<JobPostSummary> jobPostSummaryList;

    public JobPostSummaryResponse() {
        this.recruiterName = "";
        this.jobPostSummaryList = new ArrayList<>();
    }

    public String getRecruiterName() {
        return recruiterName;
    }

    public void setRecruiterName(String recruiterName) {
        this.recruiterName = recruiterName;
    }

    public List<JobPostSummary> getJobPostSummaryList() {
        return jobPostSummaryList;
    }

    public void setJobPostSummaryList(List<JobPostSummary> jobPostSummaryList) {
        this.jobPostSummaryList = jobPostSummaryList;
    }
}
