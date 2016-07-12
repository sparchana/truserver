package api.http.httpResponse;

import models.entity.JobPost;

/**
 * Created by batcoder1 on 12/7/16.
 */
public class AddJobPostResponse {
    public static final int STATUS_SUCCESS = 1;
    public static final int STATUS_UPDATE_SUCCESS = 2;
    public static final int STATUS_FAILURE = 3;
    public static final int STATUS_EXISTS = 4;

    public int status;
    public JobPost jobPost;
    public String formUrl;

    public String getFormUrl() {
        return formUrl;
    }

    public void setFormUrl(String formUrl) {
        this.formUrl = formUrl;
    }

    public int getStatus() {
        return status;
    }

    public JobPost getJobPost() {
        return jobPost;
    }

    public void setJobPost(JobPost jobPost) {
        this.jobPost = jobPost;
    }

    public void setStatus(int status) {
        this.status = status;
    }

}
