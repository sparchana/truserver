package api.http.httpResponse;

import models.entity.JobPost;

import java.util.List;

/**
 * Created by dodo on 13/12/16.
 */
public class JobPostResponse {
    List<JobPost> allJobPost;
    Integer totalJobs;
    Integer jobsPerPage;

    public List<JobPost> getAllJobPost() {
        return allJobPost;
    }

    public void setAllJobPost(List<JobPost> allJobPost) {
        this.allJobPost = allJobPost;
    }

    public Integer getTotalJobs() {
        return totalJobs;
    }

    public void setTotalJobs(Integer totalJobs) {
        this.totalJobs = totalJobs;
    }

    public Integer getJobsPerPage() {
        return jobsPerPage;
    }

    public void setJobsPerPage(Integer jobsPerPage) {
        this.jobsPerPage = jobsPerPage;
    }
}

