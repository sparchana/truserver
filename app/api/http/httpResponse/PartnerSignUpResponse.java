package api.http.httpResponse;

/**
 * Created by adarsh on 10/9/16.
 */
public class PartnerSignUpResponse {
    public static final int STATUS_SUCCESS = 1;
    public static final int STATUS_FAILURE = 2;
    public static final int STATUS_EXISTS = 3;

    public int status;
    public String partnerMobile;
    public int otp;

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

    public String getPartnerMobile() {
        return partnerMobile;
    }

    public void setPartnerMobile(String partnerMobile) {
        this.partnerMobile = partnerMobile;
    }

    public int getOtp() {
        return otp;
    }

    public void setOtp(int otp) {
        this.otp = otp;
    }
}
