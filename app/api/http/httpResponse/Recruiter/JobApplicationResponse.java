package api.http.httpResponse.Recruiter;

import models.entity.Candidate;
import models.entity.JobPost;
import models.entity.Static.InterviewTimeSlot;
import models.entity.Static.Locality;

import java.util.Date;

/**
 * Created by dodo on 12/10/16.
 */
public class JobApplicationResponse {
    private Integer jobApplicationId;
    private String jobApplicationCreatingTimeStamp;
    private Locality preScreenLocation;
    private InterviewTimeSlot interviewTimeSlot;
    private Date scheduledInterviewDate;
    private Candidate candidate;

    public Integer getJobApplicationId() {
        return jobApplicationId;
    }

    public void setJobApplicationId(Integer jobApplicationId) {
        this.jobApplicationId = jobApplicationId;
    }

    public String getJobApplicationCreatingTimeStamp() {
        return jobApplicationCreatingTimeStamp;
    }

    public void setJobApplicationCreatingTimeStamp(String jobApplicationCreatingTimeStamp) {
        this.jobApplicationCreatingTimeStamp = jobApplicationCreatingTimeStamp;
    }

    public Locality getPreScreenLocation() {
        return preScreenLocation;
    }

    public void setPreScreenLocation(Locality preScreenLocation) {
        this.preScreenLocation = preScreenLocation;
    }

    public InterviewTimeSlot getInterviewTimeSlot() {
        return interviewTimeSlot;
    }

    public void setInterviewTimeSlot(InterviewTimeSlot interviewTimeSlot) {
        this.interviewTimeSlot = interviewTimeSlot;
    }

    public Date getScheduledInterviewDate() {
        return scheduledInterviewDate;
    }

    public void setScheduledInterviewDate(Date scheduledInterviewDate) {
        this.scheduledInterviewDate = scheduledInterviewDate;
    }

    public Candidate getCandidate() {
        return candidate;
    }

    public void setCandidate(Candidate candidate) {
        this.candidate = candidate;
    }
}
