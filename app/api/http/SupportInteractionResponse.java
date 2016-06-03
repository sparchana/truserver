package api.http;

/**
 * Created by batcoder1 on 13/5/16.
 */
public class SupportInteractionResponse {
    public long interactionId;
    public String userName;
    public String userInteractionTimestamp;
    public String userInteractionType;
    public String userNote;
    public String userResults;
    public String userCreatedBy;

    public void setUserInteractionType(String userInteractionType) {
        this.userInteractionType = userInteractionType;
    }

    public void setUserResults(String userResults) {
        this.userResults = userResults;
    }

    public long getInteractionId() {
        return interactionId;
    }

    public void setInteractionId(long interactionId) {
        this.interactionId = interactionId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
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


    public String getUserCreatedBy() {
        return userCreatedBy;
    }

    public void setUserCreatedBy(String userCreatedBy) {
        this.userCreatedBy = userCreatedBy;
    }



}
