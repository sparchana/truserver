package api.http.httpRequest;

/**
 * Created by adarsh on 12/9/16.
 */
public class AddPartnerRequest extends PartnerSignUpRequest{
    public String partnerEmail;
    public String partnerOrganizationName;

    public String getPartnerEmail() {
        return partnerEmail;
    }

    public void setPartnerEmail(String partnerEmail) {
        this.partnerEmail = partnerEmail;
    }

    public String getPartnerOrganizationName() {
        return partnerOrganizationName;
    }

    public void setPartnerOrganizationName(String partnerOrganizationName) {
        this.partnerOrganizationName = partnerOrganizationName;
    }
}
