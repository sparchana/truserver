package api.http.httpRequest.Workflow;

import java.util.List;

/**
 * Created by zero on 17/10/16.
 */
public class PreScreenRequest {
    public Long candidateId;
    public Long jobPostId;
    public Boolean pass;
    public List<Long> preScreenIdList;
    public String preScreenNote;

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

    public List<Long> getPreScreenIdList() {
        return preScreenIdList;
    }

    public void setPreScreenIdList(List<Long> responseList) {
        this.preScreenIdList = responseList;
    }

    public Boolean isPass() {
        return pass;
    }

    public void setPass(Boolean pass) {
        this.pass = pass;
    }

    public String getPreScreenNote() {
        return preScreenNote;
    }

    public void setPreScreenNote(String preScreenNote) {
        this.preScreenNote = preScreenNote;
    }
}
