package api.http.httpResponse.ongrid;

import controllers.businessLogic.ongrid.OnGridAadharResponse;

/**
 * Created by archana on 11/17/16.
 */
public class OngridAadhaarVerificationResponse {

    public static final int STATUS_SUCCESS = 1;
    public static final int STATUS_ERROR = 2;
    public static final int STATUS_TIMEDOUT = 3;

    public int responseStatus = 1;
    public String responseMessage = "";
    public OnGridAadharResponse ongridResponse;

    public String getResponseMessage() {
        return responseMessage;
    }

    public void setResponseMessage(String response) {
        responseMessage = response;
    }

    public int getResponseStatus() {
        return responseStatus;
    }

    public void setResponseStatus(int responseStatus) {
        responseStatus = responseStatus;
    }

    public OnGridAadharResponse getOngridResponse() {
        return ongridResponse;
    }

    public void setOngridResponse(OnGridAadharResponse ongridResponse) {
        this.ongridResponse = ongridResponse;
    }

}

