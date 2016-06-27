package api.http.httpRequest;

/**
 * Created by batcoder1 on 22/6/16.
 */
public class AddCompanyRequest {
    public String companyName;
    public String companyEmployeeCount;
    public String companyWebsite;
    public String companyDescription;
    public String companyAddress;
    public Long companyPinCode;
    public String companyLogo;
    public Integer companyLocality;
    public Integer companyType;
    public Integer companyStatus;

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getCompanyEmployeeCount() {
        return companyEmployeeCount;
    }

    public void setCompanyEmployeeCount(String companyEmployeeCount) {
        this.companyEmployeeCount = companyEmployeeCount;
    }

    public String getCompanyWebsite() {
        return companyWebsite;
    }

    public void setCompanyWebsite(String companyWebsite) {
        this.companyWebsite = companyWebsite;
    }

    public String getCompanyDescription() {
        return companyDescription;
    }

    public void setCompanyDescription(String companyDescription) {
        this.companyDescription = companyDescription;
    }

    public String getCompanyAddress() {
        return companyAddress;
    }

    public void setCompanyAddress(String companyAddress) {
        this.companyAddress = companyAddress;
    }

    public Long getCompanyPinCode() {
        return companyPinCode;
    }

    public void setCompanyPinCode(Long companyPinCode) {
        this.companyPinCode = companyPinCode;
    }

    public String getCompanyLogo() {
        return companyLogo;
    }

    public void setCompanyLogo(String companyLogo) {
        this.companyLogo = companyLogo;
    }

    public Integer getCompanyLocality() {
        return companyLocality;
    }

    public void setCompanyLocality(Integer companyLocality) {
        this.companyLocality = companyLocality;
    }

    public Integer getCompanyType() {
        return companyType;
    }

    public void setCompanyType(Integer companyType) {
        this.companyType = companyType;
    }

    public Integer getCompanyStatus() {
        return companyStatus;
    }

    public void setCompanyStatus(Integer companyStatus) {
        this.companyStatus = companyStatus;
    }
}
