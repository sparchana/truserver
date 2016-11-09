package api.http.httpRequest.Recruiter;

/**
 * Created by dodo on 9/11/16.
 */
public class InterviewStatusRequest {
    private Integer candidateId;
    private Integer jobPostId;
    private Integer interviewStatus; // 1-> Accept, 2 -> Reject, 3 -> Reschedule
    private String rescheduledDate;
    private Integer rescheduledSlot;
    private String reason;

    public Integer getCandidateId() {
        return candidateId;
    }

    public void setCandidateId(Integer candidateId) {
        this.candidateId = candidateId;
    }

    public Integer getJobPostId() {
        return jobPostId;
    }

    public void setJobPostId(Integer jobPostId) {
        this.jobPostId = jobPostId;
    }

    public Integer getInterviewStatus() {
        return interviewStatus;
    }

    public void setInterviewStatus(Integer interviewStatus) {
        this.interviewStatus = interviewStatus;
    }

    public String getRescheduledDate() {
        return rescheduledDate;
    }

    public void setRescheduledDate(String rescheduledDate) {
        this.rescheduledDate = rescheduledDate;
    }

    public Integer getRescheduledSlot() {
        return rescheduledSlot;
    }

    public void setRescheduledSlot(Integer rescheduledSlot) {
        this.rescheduledSlot = rescheduledSlot;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
