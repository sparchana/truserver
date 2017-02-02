package api.http.httpRequest.Recruiter;

import java.util.List;

/**
 * Created by dodo on 16/1/17.
 */
public class MultipleCandidateActionRequest {
    List<Long> candidateIdList;
    String smsMessage;
    Long jobPostId;
    Long smsType;

    public List<Long> getCandidateIdList() {
        return candidateIdList;
    }

    public void setCandidateIdList(List<Long> candidateIdList) {
        this.candidateIdList = candidateIdList;
    }

    public String getSmsMessage() {
        return smsMessage;
    }

    public void setSmsMessage(String smsMessage) {
        this.smsMessage = smsMessage;
    }

    public Long getJobPostId() {
        return jobPostId;
    }

    public void setJobPostId(Long jobPostId) {
        this.jobPostId = jobPostId;
    }

    public Long getSmsType() {
        return smsType;
    }

    public void setSmsType(Long smsType) {
        this.smsType = smsType;
    }
}