package api.http.httpResponse;


import models.entity.OM.InterviewDetails;

import java.sql.Timestamp;
import java.util.List;

/**
 * Created by dodo on 8/2/17.
 */
public class RecDashboardResponse {
    Integer jobPostId;
    Timestamp creationTimeStamp;
    Integer companyId;
    String companyName;
    String jobTitle;
    Integer recruiterId;
    String recruiterName;
    Integer totalInterviewCredits;
    Integer totalContactCredits;
    String salary;
    String jobLocation;
    String jobRole;
    String jobStatus;
    Integer jobTypeId;
    String jobPlan;
    Boolean jobIsHot;
    String createdBy;
    Integer awaitingInterviewSchedule;
    Integer awaitingRecruiterConfirmation;
    Integer confirmedInterviews;
    Integer todaysInterviews;
    Integer tomorrowsInterviews;
    Integer completedInterviews;
    String jobExperience;
    List<InterviewDetails> interviewDetailsList;
    String interviewAddress;

    public Integer getJobPostId() {
        return jobPostId;
    }

    public void setJobPostId(Integer jobPostId) {
        this.jobPostId = jobPostId;
    }

    public Timestamp getCreationTimeStamp() {
        return creationTimeStamp;
    }

    public void setCreationTimeStamp(Timestamp creationTimeStamp) {
        this.creationTimeStamp = creationTimeStamp;
    }

    public Integer getCompanyId() {
        return companyId;
    }

    public void setCompanyId(Integer companyId) {
        this.companyId = companyId;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getJobTitle() {
        return jobTitle;
    }

    public void setJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
    }

    public Integer getRecruiterId() {
        return recruiterId;
    }

    public void setRecruiterId(Integer recruiterId) {
        this.recruiterId = recruiterId;
    }

    public String getRecruiterName() {
        return recruiterName;
    }

    public void setRecruiterName(String recruiterName) {
        this.recruiterName = recruiterName;
    }

    public Integer getTotalInterviewCredits() {
        return totalInterviewCredits;
    }

    public void setTotalInterviewCredits(Integer totalInterviewCredits) {
        this.totalInterviewCredits = totalInterviewCredits;
    }

    public Integer getTotalContactCredits() {
        return totalContactCredits;
    }

    public void setTotalContactCredits(Integer totalContactCredits) {
        this.totalContactCredits = totalContactCredits;
    }

    public String getSalary() {
        return salary;
    }

    public void setSalary(String salary) {
        this.salary = salary;
    }

    public String getJobLocation() {
        return jobLocation;
    }

    public void setJobLocation(String jobLocation) {
        this.jobLocation = jobLocation;
    }

    public String getJobRole() {
        return jobRole;
    }

    public void setJobRole(String jobRole) {
        this.jobRole = jobRole;
    }

    public String getJobStatus() {
        return jobStatus;
    }

    public void setJobStatus(String jobStatus) {
        this.jobStatus = jobStatus;
    }

    public Integer getJobTypeId() {
        return jobTypeId;
    }

    public void setJobTypeId(Integer jobTypeId) {
        this.jobTypeId = jobTypeId;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public Integer getAwaitingInterviewSchedule() {
        return awaitingInterviewSchedule;
    }

    public void setAwaitingInterviewSchedule(Integer awaitingInterviewSchedule) {
        this.awaitingInterviewSchedule = awaitingInterviewSchedule;
    }

    public Integer getAwaitingRecruiterConfirmation() {
        return awaitingRecruiterConfirmation;
    }

    public void setAwaitingRecruiterConfirmation(Integer awaitingRecruiterConfirmation) {
        this.awaitingRecruiterConfirmation = awaitingRecruiterConfirmation;
    }

    public Integer getConfirmedInterviews() {
        return confirmedInterviews;
    }

    public void setConfirmedInterviews(Integer confirmedInterviews) {
        this.confirmedInterviews = confirmedInterviews;
    }

    public Integer getTodaysInterviews() {
        return todaysInterviews;
    }

    public void setTodaysInterviews(Integer todaysInterviews) {
        this.todaysInterviews = todaysInterviews;
    }

    public Integer getTomorrowsInterviews() {
        return tomorrowsInterviews;
    }

    public void setTomorrowsInterviews(Integer tomorrowsInterviews) {
        this.tomorrowsInterviews = tomorrowsInterviews;
    }

    public Integer getCompletedInterviews() {
        return completedInterviews;
    }

    public void setCompletedInterviews(Integer completedInterviews) {
        this.completedInterviews = completedInterviews;
    }

    public String getJobExperience() {
        return jobExperience;
    }

    public void setJobExperience(String jobExperience) {
        this.jobExperience = jobExperience;
    }

    public List<InterviewDetails> getInterviewDetailsList() {
        return interviewDetailsList;
    }

    public void setInterviewDetailsList(List<InterviewDetails> interviewDetailsList) {
        this.interviewDetailsList = interviewDetailsList;
    }

    public String getInterviewAddress() {
        return interviewAddress;
    }

    public void setInterviewAddress(String interviewAddress) {
        this.interviewAddress = interviewAddress;
    }

    public Boolean getJobIsHot() {
        return jobIsHot;
    }

    public void setJobIsHot(Boolean jobIsHot) {
        this.jobIsHot = jobIsHot;
    }

    public String getJobPlan() {
        return jobPlan;
    }

    public void setJobPlan(String jobPlan) {
        this.jobPlan = jobPlan;
    }
}
