package api.http.httpRequest.Recruiter;

import java.util.List;

/**
 * Created by dodo on 22/11/16.
 */
public class InterviewTodayRequest {
    List<Long> jpId;

    public List<Long> getJpId() {
        return jpId;
    }

    public void setJpId(List<Long> jpId) {
        this.jpId = jpId;
    }
}
