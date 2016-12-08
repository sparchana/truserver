package NotificationService;

/**
 * Created by dodo on 8/12/16.
 */
public class FCMEvent extends NotificationEvent {

    private String tokenId;

    @Override
    void send() {
        //Send FCM notification method here
    }

    public String getTokenId() {
        return tokenId;
    }

    public void setTokenId(String tokenId) {
        this.tokenId = tokenId;
    }
}
