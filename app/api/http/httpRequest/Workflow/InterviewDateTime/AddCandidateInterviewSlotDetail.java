package api.http.httpRequest.Workflow.InterviewDateTime;

import java.util.Date;

/**
 * Created by zero on 14/11/16.
 */
public class AddCandidateInterviewSlotDetail {
    public Integer timeSlot;
    public Date scheduledInterviewDate;

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
}
