package api.http;

/**
 * Created by batcoder1 on 13/5/16.
 */
public class SupportInteractionResponse {
    public long userId;
    public String userName;
    public String userInteractionTimestamp;
    public String userInteractionType;
    public String userNote;
    public String userResults;

    public void setUserInteractionType(String userInteractionType) {
        this.userInteractionType = userInteractionType;
    }

    public void setUserResults(String userResults) {
        this.userResults = userResults;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
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


}
