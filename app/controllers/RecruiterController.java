package controllers;

import api.ServerConstants;
import api.http.httpRequest.LoginRequest;
import api.http.httpRequest.PartnerSignUpRequest;
import api.http.httpRequest.Recruiter.RecruiterLeadRequest;
import api.http.httpRequest.Recruiter.RecruiterSignUpRequest;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import controllers.businessLogic.*;
import play.Logger;
import play.mvc.Result;

import java.io.IOException;

import static play.libs.Json.toJson;
import static play.mvc.Controller.request;
import static play.mvc.Controller.session;
import static play.mvc.Results.ok;
import static play.mvc.Results.redirect;

/**
 * Created by dodo on 4/10/16.
 */
public class RecruiterController {
    public static Result recruiterHome() {
        String sessionId = session().get("recruiterId");
        if(sessionId != null){
            return ok(views.html.Recruiter.recruiter_home.render());
/*            return redirect("/recruiter/home");*/
        }
        return ok(views.html.Recruiter.recruiter_home.render());
    }

    public static Result logoutRecruiter() {
        session().clear();
        Logger.info("Recruiter Logged Out");
        return ok(views.html.Recruiter.recruiter_index.render());
    }

    public static Result checkRecruiterSession() {
        String sessionRecruiterId = session().get("recruiterId");
        if(sessionRecruiterId != null){
            return ok("1");
        } else{
            return ok("0");
        }
    }

    public static Result recruiterSignUp() {
        JsonNode req = request().body().asJson();
        RecruiterSignUpRequest recruiterSignUpRequest = new RecruiterSignUpRequest();
        ObjectMapper newMapper = new ObjectMapper();
        try {
            recruiterSignUpRequest = newMapper.readValue(req.toString(), RecruiterSignUpRequest.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Logger.info("JSON req: " + req);

        return ok(toJson(RecruiterService.recruiterSignUp(recruiterSignUpRequest)));
    }

    public static Result addPassword() {
        JsonNode req = request().body().asJson();
        RecruiterSignUpRequest recruiterSignUpRequest = new RecruiterSignUpRequest();
        ObjectMapper newMapper = new ObjectMapper();
        try {
            recruiterSignUpRequest = newMapper.readValue(req.toString(), RecruiterSignUpRequest.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Logger.info("JSON req: " + req);

        String recruiterMobile = recruiterSignUpRequest.getRecruiterAuthMobile();
        String recruiterPassword = recruiterSignUpRequest.getRecruiterPassword();

        return ok(toJson(RecruiterAuthService.savePassword(recruiterMobile, recruiterPassword)));
    }

    public static Result loginSubmit() {
        JsonNode req = request().body().asJson();
        LoginRequest loginRequest = new LoginRequest();
        ObjectMapper newMapper = new ObjectMapper();
        try {
            loginRequest = newMapper.readValue(req.toString(), LoginRequest.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Logger.info("req JSON: " + req );
        String loginMobile = loginRequest.getCandidateLoginMobile();
        String loginPassword = loginRequest.getCandidateLoginPassword();
        return ok(toJson(RecruiterService.login(loginMobile, loginPassword)));
    }

    public static Result addWebsiteLead() {
        JsonNode req = request().body().asJson();
        RecruiterLeadRequest recruiterLeadRequest = new RecruiterLeadRequest();
        ObjectMapper newMapper = new ObjectMapper();
        try {
            recruiterLeadRequest = newMapper.readValue(req.toString(), RecruiterLeadRequest.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Logger.info("req JSON: " + req );
        return ok(toJson(RecruiterLeadService.createLeadWithOtherDetails(recruiterLeadRequest)));
    }
}
