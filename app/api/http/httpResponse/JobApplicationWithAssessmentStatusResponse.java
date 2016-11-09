package api.http.httpResponse;

import models.entity.JobPost;
import models.entity.OM.JobApplication;
import models.entity.Static.InterviewStatus;
import models.entity.Static.InterviewTimeSlot;
import models.entity.Static.Locality;
import models.entity.Static.ScreeningStatus;

import java.sql.Timestamp;
import java.util.Date;

/**
 * Created by zero on 27/9/16.
 */
public class JobApplicationWithAssessmentStatusResponse {
    public Integer jobApplicationId;
    public Timestamp jobApplicationCreateTimeStamp;
    public Timestamp jobApplicationUpdateTimestamp;
    public String screeningComments;
    public Boolean preScreenSalary;
    public Boolean preScreenTimings;
    public JobPost jobPost;
    public ScreeningStatus screeningStatus;
    public Long candidateId;
    public Locality locality;
    public boolean assessmentRequired;
    public InterviewStatus interviewStatus;
    public InterviewTimeSlot interviewTimeSlot;
    public Date interviewDate;

    public JobApplicationWithAssessmentStatusResponse(){

    }

    public JobApplicationWithAssessmentStatusResponse(JobApplication jobApplication){
        this.jobApplicationId = jobApplication.getJobApplicationId();
        this.jobApplicationCreateTimeStamp = jobApplication.getJobApplicationCreateTimeStamp();
        this.jobApplicationUpdateTimestamp = jobApplication.getJobApplicationUpdateTimestamp();
        this.screeningComments = jobApplication.getScreeningComments();
        this.preScreenSalary = jobApplication.getPreScreenSalary();
        this.preScreenTimings = jobApplication.getPreScreenTimings();
        this.jobPost = jobApplication.getJobPost();
        this.screeningStatus = jobApplication.getScreeningStatus();
        // json backed reference
        this.candidateId = jobApplication.getCandidate().getCandidateId();
        this.locality = jobApplication.getLocality();
        if(jobApplication.getInterviewStatus() != null) this.interviewStatus = jobApplication.getInterviewStatus();
        if(jobApplication.getInterviewTimeSlot() != null) this.interviewTimeSlot = jobApplication.getInterviewTimeSlot();
        if(jobApplication.getScheduledInterviewDate() != null) this.interviewDate = jobApplication.getScheduledInterviewDate();
    }

    public Integer getJobApplicationId() {
        return jobApplicationId;
    }

    public void setJobApplicationId(Integer jobApplicationId) {
        this.jobApplicationId = jobApplicationId;
    }

    public Timestamp getJobApplicationCreateTimeStamp() {
        return jobApplicationCreateTimeStamp;
    }

    public void setJobApplicationCreateTimeStamp(Timestamp jobApplicationCreateTimeStamp) {
        this.jobApplicationCreateTimeStamp = jobApplicationCreateTimeStamp;
    }

    public Timestamp getJobApplicationUpdateTimestamp() {
        return jobApplicationUpdateTimestamp;
    }

    public void setJobApplicationUpdateTimestamp(Timestamp jobApplicationUpdateTimestamp) {
        this.jobApplicationUpdateTimestamp = jobApplicationUpdateTimestamp;
    }

    public String getScreeningComments() {
        return screeningComments;
    }

    public void setScreeningComments(String screeningComments) {
        this.screeningComments = screeningComments;
    }

    public Boolean getPreScreenSalary() {
        return preScreenSalary;
    }

    public void setPreScreenSalary(Boolean preScreenSalary) {
        this.preScreenSalary = preScreenSalary;
    }

    public Boolean getPreScreenTimings() {
        return preScreenTimings;
    }

    public void setPreScreenTimings(Boolean preScreenTimings) {
        this.preScreenTimings = preScreenTimings;
    }

    public JobPost getJobPost() {
        return jobPost;
    }

    public void setJobPost(JobPost jobPost) {
        this.jobPost = jobPost;
    }

    public ScreeningStatus getScreeningStatus() {
        return screeningStatus;
    }

    public void setScreeningStatus(ScreeningStatus screeningStatus) {
        this.screeningStatus = screeningStatus;
    }

    public Long getCandidateId() {
        return candidateId;
    }

    public void setCandidateId(Long candidateId) {
        this.candidateId = candidateId;
    }

    public Locality getLocality() {
        return locality;
    }

    public void setLocality(Locality locality) {
        this.locality = locality;
    }

    public boolean isAssessmentRequired() {
        return assessmentRequired;
    }

    public void setAssessmentRequired(boolean assessmentRequired) {
        this.assessmentRequired = assessmentRequired;
    }

    public InterviewStatus getInterviewStatus() {
        return interviewStatus;
    }

    public void setInterviewStatus(InterviewStatus interviewStatus) {
        this.interviewStatus = interviewStatus;
    }

    public InterviewTimeSlot getInterviewTimeSlot() {
        return interviewTimeSlot;
    }

    public void setInterviewTimeSlot(InterviewTimeSlot interviewTimeSlot) {
        this.interviewTimeSlot = interviewTimeSlot;
    }

    public Date getInterviewDate() {
        return interviewDate;
    }

    public void setInterviewDate(Date interviewDate) {
        this.interviewDate = interviewDate;
    }
}
