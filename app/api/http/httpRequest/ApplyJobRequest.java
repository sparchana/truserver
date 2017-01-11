package api.http.httpRequest;

import api.http.FormValidator;

import java.util.Date;

/**
 * Created by batcoder1 on 17/6/16.
 */
public class ApplyJobRequest {
    public String candidateMobile ;
    public Integer jobId;
    public Integer localityId;
    public Boolean isPartner;

    public Integer timeSlot;
    public Date scheduledInterviewDate;

    /* used in app to determine its version*/
    public double appVersion;

    public String getCandidateMobile() {
        return candidateMobile;
    }

    public void setCandidateMobile(String candidateMobile) {
        this.candidateMobile = FormValidator.convertToIndianMobileFormat(candidateMobile);
    }

    public Integer getLocalityId() {
        return localityId;
    }

    public void setLocalityId(Integer localityId) {
        this.localityId = localityId;
    }

    public Integer getJobId() {
        return jobId;
    }

    public void setJobId(Integer jobId) {
        this.jobId = jobId;
    }

    public Boolean getPartner() {
        return isPartner;
    }

    public void setPartner(Boolean partner) {
        isPartner = partner;
    }

    public Integer getTimeSlot() {
        return timeSlot;
    }

    public void setTimeSlot(Integer timeSlot) {
        this.timeSlot = timeSlot;
    }

    public Date getScheduledInterviewDate() {
        return scheduledInterviewDate;
    }

    public void setScheduledInterviewDate(Date scheduledInterviewDate) {
        this.scheduledInterviewDate = scheduledInterviewDate;
    }

    public double getAppVersion() {
        return appVersion;
    }

    public void setAppVersion(double appVersion) {
        this.appVersion = appVersion;
    }
}
