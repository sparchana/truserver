package api.http.httpRequest;

import api.http.FormValidator;

import java.util.List;

/**
 * Created by adarsh on 10/9/16.
 */
public class PartnerSignUpRequest {
    protected String partnerName;
    protected String partnerMobile;

    protected String partnerPassword;
    protected String partnerAuthMobile;

    public String getPartnerName() {
        return partnerName;
    }

    public void setPartnerName(String partnerName) {
        this.partnerName = partnerName;
    }

    public String getPartnerMobile() {
        return FormValidator.convertToIndianMobileFormat(partnerMobile);
    }

    public void setPartnerMobile(String partnerMobile) {
        this.partnerMobile = FormValidator.convertToIndianMobileFormat(partnerMobile);
    }

    public String getpartnerPassword() {
        return partnerPassword;
    }

    public void setpartnerPassword(String partnerPassword) {
        this.partnerPassword = partnerPassword;
    }

    public String getpartnerAuthMobile() {
        return FormValidator.convertToIndianMobileFormat(partnerAuthMobile);
    }

    public void setpartnerAuthMobile(String partnerAuthMobile) {
        this.partnerAuthMobile = FormValidator.convertToIndianMobileFormat(partnerAuthMobile);
    }
}
