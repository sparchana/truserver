package api.http.httpResponse.Recruiter;

/**
 * Created by dodo on 4/10/16.
 */
public class RecruiterSignUpResponse extends AddRecruiterResponse{
    public String recruiterMobile;
    public int otp;
    public int firstTime;

    public static int getStatusSuccess() {
        return STATUS_SUCCESS;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public static int getStatusFailure() {
        return STATUS_FAILURE;
    }

    public static int getStatusExists() {
        return STATUS_EXISTS;
    }

    public String getRecruiterMobile() {
        return recruiterMobile;
    }

    public void setRecruiterMobile(String recruiterMobile) {
        this.recruiterMobile = recruiterMobile;
    }

    public int getOtp() {
        return otp;
    }

    public void setOtp(int otp) {
        this.otp = otp;
    }

    public int getFirstTime() {
        return firstTime;
    }

    public void setFirstTime(int firstTime) {
        this.firstTime = firstTime;
    }
}
