package api.http.httpRequest;

import models.entity.OM.JobPostWorkflow;

import java.util.List;

/**
 * Created by adarsh on 15/9/16.
 */
public class PartnerCandidatesResponse {
    public long leadId;
    public long candidateId;
    public String candidateName;
    public String candidateMobile;
    public String creationTimestamp;
    public String candidateResumeLink;
    public int candidateStatus;
    public int candidateActiveDeactive;
    public int candidateAppliedJobs;
    public List<JobPostWorkflow> appliedJobList;

    public String getCandidateResumeLink() {
        return candidateResumeLink;
    }

    public void setCandidateResumeLink(String candidateResumeLink) {
        this.candidateResumeLink = candidateResumeLink;
    }
    public long getLeadId() {
        return leadId;
    }

    public void setLeadId(long leadId) {
        this.leadId = leadId;
    }

    public long getCandidateId() {
        return candidateId;
    }

    public void setCandidateId(long candidateId) {
        this.candidateId = candidateId;
    }

    public String getCandidateName() {
        return candidateName;
    }

    public void setCandidateName(String candidateName) {
        this.candidateName = candidateName;
    }

    public String getCandidateMobile() {
        return candidateMobile;
    }

    public void setCandidateMobile(String candidateMobile) {
        this.candidateMobile = candidateMobile;
    }

    public String getCreationTimestamp() {
        return creationTimestamp;
    }

    public void setCreationTimestamp(String creationTimestamp) {
        this.creationTimestamp = creationTimestamp;
    }

    public int getCandidateStatus() {
        return candidateStatus;
    }

    public void setCandidateStatus(int candidateStatus) {
        this.candidateStatus = candidateStatus;
    }

    public int getCandidateActiveDeactive() {
        return candidateActiveDeactive;
    }

    public void setCandidateActiveDeactive(int candidateActiveDeactive) {
        this.candidateActiveDeactive = candidateActiveDeactive;
    }

    public int getCandidateAppliedJobs() {
        return candidateAppliedJobs;
    }

    public void setCandidateAppliedJobs(int candidateAppliedJobs) {
        this.candidateAppliedJobs = candidateAppliedJobs;
    }

    public List<JobPostWorkflow> getAppliedJobList() {
        return appliedJobList;
    }

    public void setAppliedJobList(List<JobPostWorkflow> appliedJobList) {
        this.appliedJobList = appliedJobList;
    }
}
