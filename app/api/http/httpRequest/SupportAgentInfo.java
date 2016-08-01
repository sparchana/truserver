package api.http.httpRequest;

/**
 * Created by batcoder1 on 31/5/16.
 */
public class SupportAgentInfo {

    public String agentMobileNumber;
    public String agentAccessLevel;

    public String getAgentMobileNumber() {
        return agentMobileNumber;
    }

    public String getAgentAccessLevel() {
        return agentAccessLevel;
    }

    public void setAgentMobileNumber(String mobileNumber) {
        this.agentMobileNumber = mobileNumber;
    }

    public void setAgentAccessLevel(String accessLevel) {
        this.agentAccessLevel = accessLevel;
    }
}
