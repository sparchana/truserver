package api.http.httpResponse;

/**
 * Created by zero on 17/6/16.
 */
public class SupportInteractionNoteResponse {
    public long interactionId;
    public String userInteractionTimestamp;
    public String userNote;

    public long getInteractionId() {
        return interactionId;
    }

    public void setInteractionId(long interactionId) {
        this.interactionId = interactionId;
    }

    public String getUserInteractionTimestamp() {
        return userInteractionTimestamp;
    }

    public void setUserInteractionTimestamp(String userInteractionTimestamp) {
        this.userInteractionTimestamp = userInteractionTimestamp;
    }

    public String getUserNote() {
        return userNote;
    }

    public void setUserNote(String userNote) {
        this.userNote = userNote;
    }
}
