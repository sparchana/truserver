package api.http.httpResponse.Recruiter;

import models.entity.JobPost;

/**
 * Created by dodo on 7/12/16.
 */
public class RecruiterJobPostResponse {
    JobPost jobPost;
    Integer applicants;
    Integer pendingCount;

    public JobPost getJobPost() {
        return jobPost;
    }

    public void setJobPost(JobPost jobPost) {
        this.jobPost = jobPost;
    }

    public Integer getApplicants() {
        return applicants;
    }

    public void setApplicants(Integer applicants) {
        this.applicants = applicants;
    }

    public Integer getPendingCount() {
        return pendingCount;
    }

    public void setPendingCount(Integer pendingCount) {
        this.pendingCount = pendingCount;
    }
}
