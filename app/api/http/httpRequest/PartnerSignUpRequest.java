package api.http.httpRequest;

import api.http.FormValidator;
import models.entity.Interaction;

import java.util.List;

/**
 * Created by adarsh on 10/9/16.
 */
public class PartnerSignUpRequest {
    private String partnerName;
    private String partnerMobile;

    private Integer partnerType;
    private Integer partnerLocality;

    //for setting password
    private String partnerPassword;
    private String partnerAuthMobile;

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

    public Integer getPartnerType() {
        return partnerType;
    }

    public void setPartnerType(Integer partnerType) {
        this.partnerType = partnerType;
    }

    public Integer getPartnerLocality() {
        return partnerLocality;
    }

    public void setPartnerLocality(Integer partnerLocality) {
        this.partnerLocality = partnerLocality;
    }
}
