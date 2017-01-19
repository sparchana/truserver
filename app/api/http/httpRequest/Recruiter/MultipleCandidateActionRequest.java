package api.http.httpRequest.Recruiter;

import java.util.List;

/**
 * Created by dodo on 16/1/17.
 */
public class MultipleCandidateActionRequest {
    List<Long> candidateIdList;
    String smsMessage;

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
}
