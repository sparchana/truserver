package api;

/**
 * Created by zero on 23/4/16.
 */
public class AddLeadRequest {
    protected String leadName;
    protected String leadMobile;

    public void setleadName(String leadName) {
        this.leadName = leadName;
    }

    public String getleadName() {
        return leadName;
    }

    public void setleadMobile(String leadMobile) {
        this.leadMobile = leadMobile;
    }

    public String getleadMobile() {
        return leadMobile;
    }

}
