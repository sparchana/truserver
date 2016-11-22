package api.http.httpRequest;

/**
 * Created by dodo on 22/11/16.
 */
public class AddFeedbackRequest {
    Long candidateId;
    Long jobPostId;
    Long feedbackStatus;
    String feedbackComment;

    public Long getCandidateId() {
        return candidateId;
    }

    public void setCandidateId(Long candidateId) {
        this.candidateId = candidateId;
    }

    public Long getJobPostId() {
        return jobPostId;
    }

    public void setJobPostId(Long jobPostId) {
        this.jobPostId = jobPostId;
    }

    public Long getFeedbackStatus() {
        return feedbackStatus;
    }

    public void setFeedbackStatus(Long feedbackStatus) {
        this.feedbackStatus = feedbackStatus;
    }

    public String getFeedbackComment() {
        return feedbackComment;
    }

    public void setFeedbackComment(String feedbackComment) {
        this.feedbackComment = feedbackComment;
    }
}
