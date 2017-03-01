package api.http.httpRequest;

/**
 * Created by adarsh on 12/9/16.
 */
public class PartnerProfileRequest extends PartnerSignUpRequest{
    private String partnerOrganizationName;

    public String getPartnerOrganizationName() {
        return partnerOrganizationName;
    }

    public void setPartnerOrganizationName(String partnerOrganizationName) {
        this.partnerOrganizationName = partnerOrganizationName;
    }
}
