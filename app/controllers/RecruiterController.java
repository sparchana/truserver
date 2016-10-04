package controllers;

import api.ServerConstants;
import api.http.httpRequest.PartnerSignUpRequest;
import api.http.httpRequest.Recruiter.RecruiterSignUpRequest;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import controllers.businessLogic.InteractionService;
import controllers.businessLogic.PartnerService;
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
    public static Result recruiterIndex() {
        String sessionId = session().get("recruiterId");
        if(sessionId != null){
            return redirect("/recruiter/home");
        }
        return ok(views.html.Recruiter.recruiter_index.render());
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

        return ok(toJson("0"));

    }

}
