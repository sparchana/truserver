package api.http;

/**
 * Created by zero on 23/4/16.
 */
public class AddLeadRequest {
    protected String leadName;
    protected String leadMobile;
    protected int leadType;
    protected int leadChannel;
    protected String leadInterest;

    public void setLeadName(String leadName) {
        this.leadName = leadName;
    }

    public String getLeadName() {
        return leadName;
    }

    public void setLeadMobile(String leadMobile) {
        this.leadMobile = leadMobile;
    }

    public String getLeadMobile() {
        return leadMobile;
    }

    public void setLeadType(int leadType) {
        this.leadType = leadType;
    }

    public int getLeadType() {
        return leadType;
    }

    public void setLeadChannel(int leadChannel) {
        this.leadChannel = leadChannel;
    }

    public int getLeadChannel() {
        return leadChannel;
    }

    public void setLeadInterest(String leadInterest) {
        this.leadInterest = leadInterest;
    }

    public String getLeadInterest() {
        return leadInterest;
    }
}
