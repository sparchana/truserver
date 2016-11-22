package api.http.httpRequest.Recruiter;

import java.util.List;

/**
 * Created by dodo on 22/11/16.
 */
public class InterviewTodayRequest {
    List<Integer> jpId;

    public List<Integer> getJpId() {
        return jpId;
    }

    public void setJpId(List<Integer> jpId) {
        this.jpId = jpId;
    }
}
