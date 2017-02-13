package api.http.httpResponse;

import controllers.businessLogic.JobWorkflow.JobPostWorkflowEngine;
import models.entity.Static.InterviewTimeSlot;
import models.entity.Static.JobPostWorkflowStatus;
import models.entity.Static.RejectReason;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

/**
 * Created by zero on 10/10/16.
 */
public class CandidateExtraData {
    public String appliedOn;
    public JobPostWorkflowEngine.LastActiveValue lastActive;
    public Integer assessmentAttemptId;
    public Integer preScreenCallAttemptCount;
    public Integer allInteractionCount;
    public String jobApplicationMode;
    public Timestamp preScreenSelectionTimeStamp;
    public Timestamp creationTimestamp;
    public String workflowUUId;
    public Long workflowId;
    public JobPostWorkflowStatus workflowStatus;
    public String createdBy;
    public String interviewSchedule;
    public Date interviewDate;
    public InterviewTimeSlot interviewSlot;
    public Double interviewLat;
    public Double interviewLng;
    public JobPostWorkflowStatus candidateInterviewStatus;
    public RejectReason reason;
    public Integer totalSmsSent;
    public List<CompanyIdName> companyList;
    public Integer round;
    public String interviewRecruiterName;
    public Long interviewRecruiterId;

    public static class CompanyIdName {
        public Long companyId;
        public String companyName;

        public CompanyIdName() {
        }

        public CompanyIdName(Long companyId, String companyName) {
            this.companyId = companyId;
            this.companyName = companyName;
        }

        public Long getCompanyId() {
            return companyId;
        }

        public void setCompanyId(Long companyId) {
            this.companyId = companyId;
        }

        public String getCompanyName() {
            return companyName;
        }

        public void setCompanyName(String companyName) {
            this.companyName = companyName;
        }
    }

    public String getAppliedOn() {
        return appliedOn;
    }

    public void setAppliedOn(String appliedOn) {
        this.appliedOn = appliedOn;
    }

    public JobPostWorkflowEngine.LastActiveValue getLastActive() {
        return lastActive;
    }

    public void setLastActive(JobPostWorkflowEngine.LastActiveValue lastActive) {
        this.lastActive = lastActive;
    }

    public Integer getAssessmentAttemptId() {
        return assessmentAttemptId;
    }

    public void setAssessmentAttemptId(Integer assessmentAttemptId) {
        this.assessmentAttemptId = assessmentAttemptId;
    }

    public Integer getPreScreenCallAttemptCount() {
        return preScreenCallAttemptCount;
    }

    public void setPreScreenCallAttemptCount(Integer preScreenCallAttemptCount) {
        this.preScreenCallAttemptCount = preScreenCallAttemptCount;
    }

    public String getJobApplicationMode() {
        return jobApplicationMode;
    }

    public void setJobApplicationMode(String jobApplicationMode) {
        this.jobApplicationMode = jobApplicationMode;
    }

    public Timestamp getPreScreenSelectionTimeStamp() {
        return preScreenSelectionTimeStamp;
    }

    public void setPreScreenSelectionTimeStamp(Timestamp preScreenSelectionTimeStamp) {
        this.preScreenSelectionTimeStamp = preScreenSelectionTimeStamp;
    }

    public String getWorkflowUUId() {
        return workflowUUId;
    }

    public void setWorkflowUUId(String workflowUUId) {
        this.workflowUUId = workflowUUId;
    }

    public Long getWorkflowId() {
        return workflowId;
    }

    public void setWorkflowId(Long workflowId) {
        this.workflowId = workflowId;
    }

    public JobPostWorkflowStatus getWorkflowStatus() {
        return workflowStatus;
    }

    public void setWorkflowStatus(JobPostWorkflowStatus workflowStatus) {
        this.workflowStatus = workflowStatus;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getInterviewSchedule() {
        return interviewSchedule;
    }

    public void setInterviewSchedule(String interviewSchedule) {
        this.interviewSchedule = interviewSchedule;
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

    public Date getInterviewDate() {
        return interviewDate;
    }

    public void setInterviewDate(Date interviewDate) {
        this.interviewDate = interviewDate;
    }

    public InterviewTimeSlot getInterviewSlot() {
        return interviewSlot;
    }

    public void setInterviewSlot(InterviewTimeSlot interviewSlot) {
        this.interviewSlot = interviewSlot;
    }

    public JobPostWorkflowStatus getCandidateInterviewStatus() {
        return candidateInterviewStatus;
    }

    public void setCandidateInterviewStatus(JobPostWorkflowStatus candidateInterviewStatus) {
        this.candidateInterviewStatus = candidateInterviewStatus;
    }

    public Integer getAllInteractionCount() {
        return allInteractionCount;
    }

    public void setAllInteractionCount(Integer allInteractionCount) {
        this.allInteractionCount = allInteractionCount;
    }

    public Timestamp getCreationTimestamp() {
        return creationTimestamp;
    }

    public void setCreationTimestamp(Timestamp creationTimestamp) {
        this.creationTimestamp = creationTimestamp;
    }

    public RejectReason getReason() {
        return reason;
    }

    public void setReason(RejectReason reason) {
        this.reason = reason;
    }

    public Integer getTotalSmsSent() {
        return totalSmsSent;
    }

    public void setTotalSmsSent(Integer totalSmsSent) {
        this.totalSmsSent = totalSmsSent;
    }

    public List<CompanyIdName> getCompanyList() {
        return companyList;
    }

    public void setCompanyList(List<CompanyIdName> companyList) {
        this.companyList = companyList;
    }

    public Integer getRound() {
        return round;
    }

    public void setRound(Integer round) {
        this.round = round;
    }

    public String getInterviewRecruiterName() {
        return interviewRecruiterName;
    }

    public void setInterviewRecruiterName(String interviewRecruiterName) {
        this.interviewRecruiterName = interviewRecruiterName;
    }

    public Long getInterviewRecruiterId() {
        return interviewRecruiterId;
    }

    public void setInterviewRecruiterId(Long interviewRecruiterId) {
        this.interviewRecruiterId = interviewRecruiterId;
    }
}
