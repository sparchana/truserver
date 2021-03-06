package api.http.httpResponse;

/**
 * Created by zero on 29/4/16.
 */
public class SupportDashboardElementResponse {
    public long leadId;
    public String leadCreationTimestamp;
    public String leadStatus;
    public String leadChannel;
    public String leadName;
    public String leadType;
    public String leadMobile;
    public String lastIncomingCallTimestamp;
    public int totalInBounds;
    public boolean followUpStatus;
    public String followUpTimeStamp;

    public int getTotalInBounds() {
        return totalInBounds;
    }

    public void setTotalInBounds(int totalInBounds) {
        this.totalInBounds = totalInBounds;
    }

    public long getLeadId() {
        return leadId;
    }

    public void setLeadId(long leadId) {
        this.leadId = leadId;
    }

    public String getLeadCreationTimestamp() {
        return leadCreationTimestamp;
    }

    public void setLeadCreationTimestamp(String leadCreationTimestamp) {
        this.leadCreationTimestamp = leadCreationTimestamp;
    }

    public String getLeadStatus() {
        return leadStatus;
    }

    public void setLeadStatus(String leadStatus) {
        this.leadStatus = leadStatus;
    }

    public String getLeadChannel() {
        return leadChannel;
    }

    public void setLeadChannel(String leadChannel) {
        this.leadChannel = leadChannel;
    }

    public String getLeadName() {
        return leadName;
    }

    public void setLeadName(String leadName) {
        this.leadName = leadName;
    }

    public String getLeadType() {
        return leadType;
    }

    public void setLeadType(String leadType) {
        this.leadType = leadType;
    }

    public String getLeadMobile() {
        return leadMobile;
    }

    public void setLeadMobile(String leadMobile) {
        this.leadMobile = leadMobile;
    }

    public String getLastIncomingCallTimestamp() {
        return lastIncomingCallTimestamp;
    }

    public void setLastIncomingCallTimestamp(String lastIncomingCallTimestamp) {
        this.lastIncomingCallTimestamp = lastIncomingCallTimestamp;
    }

    public boolean isFollowUpStatus() {
        return followUpStatus;
    }

    public void setFollowUpStatus(boolean followUpStatus) {
        this.followUpStatus = followUpStatus;
    }

    public String getFollowUpTimeStamp() {
        return followUpTimeStamp;
    }

    public void setFollowUpTimeStamp(String followUpTimeStamp) {
        this.followUpTimeStamp = followUpTimeStamp;
    }
}
