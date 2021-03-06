package api.http.httpRequest;

import api.http.FormValidator;

/**
 * Created by zero on 23/4/16.
 */
public class AddLeadRequest {
    protected String leadName;
    protected String leadMobile;
    protected Integer leadType;
    protected Integer leadChannel;
    protected String leadInterest;

    public void setLeadName(String leadName) {
        this.leadName = leadName;
    }

    public String getLeadName() {
        return leadName;
    }

    public void setLeadMobile(String leadMobile) {
        this.leadMobile = FormValidator.convertToIndianMobileFormat(leadMobile);
    }

    public String getLeadMobile() {
        return FormValidator.convertToIndianMobileFormat(leadMobile);
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
