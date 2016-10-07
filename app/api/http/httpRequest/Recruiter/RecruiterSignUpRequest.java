package api.http.httpRequest.Recruiter;

/**
 * Created by dodo on 4/10/16.
 */
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
