package api.http;

/**
 * Created by batcoder1 on 13/5/16.
 */
public class SupportInteractionResponse {
    public long user_id;
    public String user_name;
    public String user_interaction_timestamp;

    public long getUser_id() {
        return user_id;
    }

    public void setUser_id(long user_id) {
        this.user_id = user_id;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getUser_interaction_timestamp() {
        return user_interaction_timestamp;
    }

    public void setUser_interaction_timestamp(String user_interaction_timestamp) {
        this.user_interaction_timestamp = user_interaction_timestamp;
    }

    public String getUser_note() {
        return user_note;
    }

    public void setUser_note(String user_note) {
        this.user_note = user_note;
    }

    public String user_note;
}
