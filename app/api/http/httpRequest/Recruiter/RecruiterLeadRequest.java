package api.http.httpRequest.Recruiter;

import java.util.List;

/**
 * Created by dodo on 5/10/16.
 */
public class RecruiterLeadRequest {
    protected String recruiterMobile;
    protected String recruiterRequirement;
    protected List<Integer> recruiterJobLocality;
    protected List<Integer> recruiterJobRole;

    public String getRecruiterMobile() {
        return recruiterMobile;
    }

    public void setRecruiterMobile(String recruiterMobile) {
        this.recruiterMobile = recruiterMobile;
    }

    public String getRecruiterRequirement() {
        return recruiterRequirement;
    }

    public void setRecruiterRequirement(String recruiterRequirement) {
        this.recruiterRequirement = recruiterRequirement;
    }

    public List<Integer> getRecruiterJobLocality() {
        return recruiterJobLocality;
    }

    public void setRecruiterJobLocality(List<Integer> recruiterJobLocality) {
        this.recruiterJobLocality = recruiterJobLocality;
    }

    public List<Integer> getRecruiterJobRole() {
        return recruiterJobRole;
    }

    public void setRecruiterJobRole(List<Integer> recruiterJobRole) {
        this.recruiterJobRole = recruiterJobRole;
    }
}
