package api.http.httpResponse.interview;

/**
 * Created by zero on 16/1/17.
 */
public class InterviewDateTime {
    private long interviewDateMillis;
    private InterviewTimeSlot interviewTimeSlot;

    public long getInterviewDateMillis() {
        return interviewDateMillis;
    }

    public void setInterviewDateMillis(long interviewDateMillis) {
        this.interviewDateMillis = interviewDateMillis;
    }

    public InterviewTimeSlot getInterviewTimeSlot() {
        return interviewTimeSlot;
    }

    public void setInterviewTimeSlot(InterviewTimeSlot interviewTimeSlot) {
        this.interviewTimeSlot = interviewTimeSlot;
    }
}
