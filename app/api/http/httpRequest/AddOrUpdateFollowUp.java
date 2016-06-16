package api.http.httpRequest;

import api.http.FormValidator;

import java.sql.Timestamp;

/**
 * Created by zero on 15/6/16.
 */
public class AddOrUpdateFollowUp {
    private String leadMobile;

    private Timestamp followUpDateTime;

    public String getLeadMobile() {
        return FormValidator.convertToIndianMobileFormat(leadMobile);
    }

    public void setLeadMobile(String leadMobile) {
        this.leadMobile = FormValidator.convertToIndianMobileFormat(leadMobile);
    }

    public Timestamp getFollowUpDateTime() {
        return followUpDateTime;
    }

    public void setFollowUpDateTime(Timestamp followUpDateTime) {
        this.followUpDateTime = followUpDateTime;
    }
}
