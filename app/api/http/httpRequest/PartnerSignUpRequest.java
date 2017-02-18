package api.http.httpRequest;

import api.http.FormValidator;

/**
 * Created by adarsh on 10/9/16.
 */
public class PartnerSignUpRequest {
    private String partnerName;
    private String partnerLastName;
    private String partnerMobile;

    private Integer partnerType;
    private Long partnerLocality;

    private String partnerCompanyCode;

    //for setting password
    private String partnerPassword;
    private String partnerAuthMobile;

    private String partnerEmail;


    // used in employee bulk upload via recruiter
    private String createdByRecuiterUUId;
    private String foreginEmployeeId;


    public String getPartnerName() {
        return partnerName;
    }

    public void setPartnerName(String partnerName) {
        this.partnerName = partnerName;
    }

    public String getPartnerLastName() {
        return partnerLastName;
    }

    public void setPartnerLastName(String partnerLastName) {
        this.partnerLastName = partnerLastName;
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

    public Long getPartnerLocality() {
        return partnerLocality;
    }

    public void setPartnerLocality(Long partnerLocality) {
        this.partnerLocality = partnerLocality;
    }

    public String getPartnerCompanyCode() {
        return partnerCompanyCode;
    }

    public void setPartnerCompanyCode(String partnerCompanyId) {
        this.partnerCompanyCode = partnerCompanyId;
    }

    public String getPartnerEmail() {
        return partnerEmail;
    }

    public void setPartnerEmail(String partnerEmail) {
        this.partnerEmail = partnerEmail;
    }

    public String getCreatedByRecuiterUUId() {
        return createdByRecuiterUUId;
    }

    public void setCreatedByRecuiterUUId(String createdByRecuiterUUId) {
        this.createdByRecuiterUUId = createdByRecuiterUUId;
    }

    public String getForeginEmployeeId() {
        return foreginEmployeeId;
    }

    public void setForeginEmployeeId(String foreginEmployeeId) {
        this.foreginEmployeeId = foreginEmployeeId;
    }
}
