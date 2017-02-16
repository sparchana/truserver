package api.http.httpRequest.Recruiter;

import java.util.Date;

/**
 * Created by batcoder1 on 18/7/16.
 */
public class AddRecruiterRequest {
    public String recruiterName;
    public String recruiterMobile;
    public String recruiterLandline;
    public String recruiterEmail;
    private String recruiterLinkedinProfile;
    private String recruiterAlternateMobile;
    public Long recruiterCompany;
    private String recruiterCompanyName;
    private Integer recruiterTotalAmount;
    private Integer recruiterInterviewCreditAmount;
    private Integer recruiterContactCreditAmount;
    private Integer recruiterInterviewCreditUnitPrice;
    private Integer recruiterContactCreditUnitPrice;
    private Integer recruiterCreditMode;
    private Integer contactCredits;
    private Integer ctaCredits;
    private Integer interviewCredits;

    private String companyCode;

    private Integer packId;
    private Integer creditCount;
    private Date expiryDate;

    public Integer getCtaCredits() {
        return ctaCredits;
    }

    public void setCtaCredits(Integer ctaCredits) {
        this.ctaCredits = ctaCredits;
    }
    private Integer recruiterType;

    public String getRecruiterName() {
        return recruiterName;
    }

    public void setRecruiterName(String recruiterName) {
        this.recruiterName = recruiterName;
    }

    public String getRecruiterMobile() {
        return recruiterMobile;
    }

    public void setRecruiterMobile(String recruiterMobile) {
        this.recruiterMobile = recruiterMobile;
    }

    public String getRecruiterLandline() {
        return recruiterLandline;
    }

    public void setRecruiterLandline(String recruiterLandline) {
        this.recruiterLandline = recruiterLandline;
    }

    public String getRecruiterEmail() {
        return recruiterEmail;
    }

    public void setRecruiterEmail(String recruiterEmail) {
        this.recruiterEmail = recruiterEmail;
    }

    public Long getRecruiterCompany() {
        return recruiterCompany;
    }

    public void setRecruiterCompany(Long recruiterCompany) {
        this.recruiterCompany = recruiterCompany;
    }

    public String getRecruiterCompanyName() {
        return recruiterCompanyName;
    }

    public void setRecruiterCompanyName(String recruiterCompanyName) {
        this.recruiterCompanyName = recruiterCompanyName;
    }

    public Integer getRecruiterTotalAmount() {
        return recruiterTotalAmount;
    }

    public void setRecruiterTotalAmount(Integer recruiterTotalAmount) {
        this.recruiterTotalAmount = recruiterTotalAmount;
    }

    public Integer getRecruiterInterviewCreditAmount() {
        return recruiterInterviewCreditAmount;
    }

    public void setRecruiterInterviewCreditAmount(Integer recruiterInterviewCreditAmount) {
        this.recruiterInterviewCreditAmount = recruiterInterviewCreditAmount;
    }

    public Integer getRecruiterContactCreditAmount() {
        return recruiterContactCreditAmount;
    }

    public void setRecruiterContactCreditAmount(Integer recruiterContactCreditAmount) {
        this.recruiterContactCreditAmount = recruiterContactCreditAmount;
    }

    public Integer getRecruiterInterviewCreditUnitPrice() {
        return recruiterInterviewCreditUnitPrice;
    }

    public void setRecruiterInterviewCreditUnitPrice(Integer recruiterInterviewCreditUnitPrice) {
        this.recruiterInterviewCreditUnitPrice = recruiterInterviewCreditUnitPrice;
    }

    public Integer getRecruiterContactCreditUnitPrice() {
        return recruiterContactCreditUnitPrice;
    }

    public void setRecruiterContactCreditUnitPrice(Integer recruiterContactCreditUnitPrice) {
        this.recruiterContactCreditUnitPrice = recruiterContactCreditUnitPrice;
    }

    public Integer getRecruiterCreditMode() {
        return recruiterCreditMode;
    }

    public void setRecruiterCreditMode(Integer recruiterCreditMode) {
        this.recruiterCreditMode = recruiterCreditMode;
    }

    public String getRecruiterLinkedinProfile() {
        return recruiterLinkedinProfile;
    }

    public void setRecruiterLinkedinProfile(String recruiterLinkedinProfile) {
        this.recruiterLinkedinProfile = recruiterLinkedinProfile;
    }

    public String getRecruiterAlternateMobile() {
        return recruiterAlternateMobile;
    }

    public void setRecruiterAlternateMobile(String recruiterAlternateMobile) {
        this.recruiterAlternateMobile = recruiterAlternateMobile;
    }

    public Integer getContactCredits() {
        return contactCredits;
    }

    public void setContactCredits(Integer contactCredits) {
        this.contactCredits = contactCredits;
    }

    public Integer getInterviewCredits() {
        return interviewCredits;
    }

    public void setInterviewCredits(Integer interviewCredits) {
        this.interviewCredits = interviewCredits;
    }

    public Integer getPackId() {
        return packId;
    }

    public void setPackId(Integer packId) {
        this.packId = packId;
    }

    public Integer getCreditCount() {
        return creditCount;
    }

    public Date getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(Date expiryDate) {
        this.expiryDate = expiryDate;
    }

    public void setCreditCount(Integer creditCount) {
        this.creditCount = creditCount;
    }

    public String getCompanyCode() {
        return companyCode;
    }

    public void setCompanyCode(String companyCode) {
        this.companyCode = companyCode;
    }

    public Integer getRecruiterType() {
        return recruiterType;
    }

    public void setRecruiterType(Integer recruiterType) {
        this.recruiterType = recruiterType;
    }
}
