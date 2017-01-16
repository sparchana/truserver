package api.http.httpResponse.interview;

/**
 * Created by zero on 16/1/17.
 */
public class InterviewTimeSlot {
    private int slotId;
    private String slotTitle;

    public int getSlotId() {
        return slotId;
    }

    public void setSlotId(int slotId) {
        this.slotId = slotId;
    }

    public String getSlotTitle() {
        return slotTitle;
    }

    public void setSlotTitle(String slotTitle) {
        this.slotTitle = slotTitle;
    }
}
