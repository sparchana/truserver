package api.http.httpRequest;

/**
 * Created by zero on 23/4/16.
 */
public class AddLeadRequest {
    protected String leadName = " ";
    protected String leadMobile = " ";
    protected Integer leadType = 0;
    protected Integer leadChannel = 0;
    protected String leadInterest = " ";

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

    public Integer getLeadType() {
        return leadType;
    }

    public void setLeadChannel(int leadChannel) {
        this.leadChannel = leadChannel;
    }

    public Integer getLeadChannel() {
        return leadChannel;
    }

    public void setLeadInterest(String leadInterest) {
        this.leadInterest = leadInterest;
    }

    public String getLeadInterest() {
        return leadInterest;
    }
}
