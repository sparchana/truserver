package api.http.httpRequest;

/**
 * Created by dodo on 22/11/16.
 */
public class AddFeedbackRequest {
    private Long candidateId;
    private Long jobPostId;
    private Long feedbackStatus;
    private String feedbackComment;
    private Long rejectReason;

    // requred for next interview round
    private Double interviewLat;
    private Double interviewLng;
    private Long interviewRecruiterId;
    private Long interviewDatetimeInMills;
    private Integer interviewSlotId;
    private String interviewAddress;

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


    public Long getRejectReason() {
        return rejectReason;
    }

    public void setRejectReason(Long rejectReason) {
        this.rejectReason = rejectReason;
    }

    public Double getInterviewLat() {
        return interviewLat;
    }

    public void setInterviewLat(Double interviewLat) {
        this.interviewLat = interviewLat;
    }

    public Double getInterviewLng() {
        return interviewLng;
    }

    public void setInterviewLng(Double interviewLng) {
        this.interviewLng = interviewLng;
    }

    public Long getInterviewRecruiterId() {
        return interviewRecruiterId;
    }

    public void setInterviewRecruiterId(Long interviewRecruiterId) {
        this.interviewRecruiterId = interviewRecruiterId;
    }

    public Long getInterviewDatetimeInMills() {
        return interviewDatetimeInMills;
    }

    public void setInterviewDatetimeInMills(Long interviewDatetimeInMills) {
        this.interviewDatetimeInMills = interviewDatetimeInMills;
    }

    public Integer getInterviewSlotId() {
        return interviewSlotId;
    }

    public void setInterviewSlotId(Integer interviewSlotId) {
        this.interviewSlotId = interviewSlotId;
    }

    public String getInterviewAddress() {
        return interviewAddress;
    }

    public void setInterviewAddress(String interviewAddress) {
        this.interviewAddress = interviewAddress;
    }
}
