package api.http.httpRequest.Recruiter;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Created by dodo on 4/10/16.
 */

@JsonIgnoreProperties(ignoreUnknown=true)
public class RecruiterSignUpRequest extends AddRecruiterRequest{
    //for setting password
    private String recruiterPassword;
    private String recruiterAuthMobile;

    public String getRecruiterPassword() {
        return recruiterPassword;
    }

    public void setRecruiterPassword(String recruiterPassword) {
        this.recruiterPassword = recruiterPassword;
    }

    public String getRecruiterAuthMobile() {
        return recruiterAuthMobile;
    }

    public void setRecruiterAuthMobile(String recruiterAuthMobile) {
        this.recruiterAuthMobile = recruiterAuthMobile;
    }
}
