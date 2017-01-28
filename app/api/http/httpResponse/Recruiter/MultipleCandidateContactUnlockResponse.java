package api.http.httpResponse.Recruiter;

import java.util.List;

/**
 * Created by dodo on 16/1/17.
 */
public class MultipleCandidateContactUnlockResponse {
    List<UnlockContactResponse> unlockContactResponseList;
    Integer recruiterContactCreditsLeft;
    Integer recruiterInterviewCreditsLeft;

    public List<UnlockContactResponse> getUnlockContactResponseList() {
        return unlockContactResponseList;
    }

    public void setUnlockContactResponseList(List<UnlockContactResponse> unlockContactResponseList) {
        this.unlockContactResponseList = unlockContactResponseList;
    }

    public Integer getRecruiterContactCreditsLeft() {
        return recruiterContactCreditsLeft;
    }

    public void setRecruiterContactCreditsLeft(Integer recruiterContactCreditsLeft) {
        this.recruiterContactCreditsLeft = recruiterContactCreditsLeft;
    }

    public Integer getRecruiterInterviewCreditsLeft() {
        return recruiterInterviewCreditsLeft;
    }

    public void setRecruiterInterviewCreditsLeft(Integer recruiterInterviewCreditsLeft) {
        this.recruiterInterviewCreditsLeft = recruiterInterviewCreditsLeft;
    }
}
