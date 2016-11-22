package controllers;

import api.http.httpResponse.ongrid.OngridAadhaarVerificationResponse;
import controllers.businessLogic.CandidateService;
import controllers.businessLogic.ongrid.AadhaarService;
import models.entity.Candidate;
import okhttp3.*;
import play.Logger;
import play.mvc.Result;

import java.io.IOException;

import static play.mvc.Results.ok;

/**
 * Created by archana on 11/16/16.
 */
public class OngridController {
    public static Result sendOnGridAadharRequest() {

        Candidate candidate = CandidateService.isCandidateExists("+918197222248");

        OngridAadhaarVerificationResponse response = AadhaarService.sendAadharSyncVerificationRequest(candidate);

        Logger.info("STATUS: " + response.getResponseStatus() + " MESSAGE: " + response.getResponseMessage());

        return ok("1");

    }

    public static Result sendOnGridOnboardingRequest() {

        OkHttpClient client = new OkHttpClient();

        MediaType mediaType = MediaType.parse("application/json");
        String req = "{\n  \"uid\" : \"855022384898\" , \n \"name\": \"Archana\",\n  \"gender\": \"F\",\n  \"city\": \"Coimbatore\",\n  \"professionId\": \"69\",\n  \"phone\": \"8197222248\",\n \"email\": \"sp.archana@gmail.com\",\n  \"dob\": \"1985-01-16\",\n \"employeeId\":\"\",\n  \"permanentAddress\": {\n }, \n \"currentAddress\": \"\",\n \"otherIdentifiers\": {\n \"pan\": \"AIFPA0173R\" \n } \n}";
        Logger.info("req, " + req);
        RequestBody body =
                RequestBody.create(mediaType, "{\n  \"uid\" : \"855022384898\" , \n \"name\": \"Archana\",\n  \"gender\": \"F\",\n  \"city\": \"Coimbatore\",\n  \"professionId\": \"69\",\n  \"phone\": \"8197222248\",\n \"email\": \"sp.archana@gmail.com\",\n  \"dob\": \"1985-01-16\",\n \"employeeId\":\"\",\n  \"permanentAddress\": {\n }, \n \"currentAddress\": \"\",\n \"otherIdentifiers\": {\n  } \n}");
        Request request = new Request.Builder()
                .url("https://api-staging.ongrid.in/app/v1/community/66095/individuals/\n")
                .post(body)
                .addHeader("content-type", "application/json")
                .addHeader("authorization", "Basic dHJ1am9iczo4RFI4TkdoTHh3cjBBWmZBd3BHaU0rTGwwM3JqRkhZVHQ2NU04aWdXVm1yM09PVyttamJLVFpmYUpXeHI1RnNW")
                .build();

        Logger.info("Request: " + request.toString() + " \n Response Body: " + request.body().toString() + " \n Response Header: " + request.headers().toString());
        Response response = null;

        try {
            response = client.newCall(request).execute();
            Logger.info("Response: " + response.toString() + " \n Response Headers: "
                    + response.headers().toString() + "\n Response Body: " + response.body().string()
                    + " \n Response Message: " + response.message().toString());
        }
        catch (IOException ioex) {
            Logger.error("IOException " + ioex.getMessage());
            ioex.printStackTrace();
        }
        return ok("1");
    }

    public static Result sendOnGridDocumentVerificationRequest() {

        OkHttpClient client = new OkHttpClient();

        MediaType mediaType = MediaType.parse("application/json");
        String req = "{\n  \"individualId\" : \"10541\" , \n \"type\": \"PANCard\" \n}";
        Logger.info("req, " + req);
        RequestBody body =
                RequestBody.create(mediaType, "{\n  \"individualId\" : \"10541\" , \n \"type\": \"PANCard\" \n}");
        Request request = new Request.Builder()
                .url("https://api-staging.ongrid.in/app/v1/individual/10541/docv")
                .post(body)
                .addHeader("content-type", "application/json")
                .addHeader("authorization", "Basic dHJ1am9iczo4RFI4TkdoTHh3cjBBWmZBd3BHaU0rTGwwM3JqRkhZVHQ2NU04aWdXVm1yM09PVyttamJLVFpmYUpXeHI1RnNW")
                .build();

        Logger.info("Request: " + request.toString() + " \n Response Body: " + request.body().toString() + " \n Response Header: " + request.headers().toString());
        Response response = null;

        try {
            response = client.newCall(request).execute();
            Logger.info("Response: " + response.toString() + " \n Response Headers: "
                    + response.headers().toString() + "\n Response Body: " + response.body().string()
                    + " \n Response Message: " + response.message().toString());
        }
        catch (IOException ioex) {
            Logger.error("IOException " + ioex.getMessage());
            ioex.printStackTrace();
        }
        return ok("1");
    }
}
