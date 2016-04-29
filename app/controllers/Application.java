package controllers;

import api.AddLeadRequest;
import api.CandidateSignUpRequest;
import api.LoginRequest;
import api.ResetPasswordResquest;
import models.entity.Auth;
import models.entity.Candidate;
import models.entity.Lead;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;

import static play.libs.Json.toJson;

public class Application extends Controller {

    public static Result index() {
        return ok(views.html.index.render());
    }

    public static Result support() {
        String test = request().body().asText();
        return ok(views.html.support.render());
    }

    public static Result addLead() {
        Form<AddLeadRequest> userForm = Form.form(AddLeadRequest.class);
        AddLeadRequest addLeadRequest = userForm.bindFromRequest().get();

        return ok(toJson(Lead.addLead(addLeadRequest)));
    }

    public static Result signUpSubmit() {
        Form<CandidateSignUpRequest> candidateForm = Form.form(CandidateSignUpRequest.class);
        CandidateSignUpRequest candidateSignUpRequest = candidateForm.bindFromRequest().get();

        return ok(toJson(Candidate.candidateSignUp(candidateSignUpRequest)));
    }

    public static Result signUp() {
        return ok(views.html.signup.render());
    }

    public static Result verifyOtp() {
        Form<CandidateSignUpRequest> candidateForm = Form.form(CandidateSignUpRequest.class);
        CandidateSignUpRequest candidateSignUpRequest = candidateForm.bindFromRequest().get();
        return ok(toJson(Candidate.verifyOtp(candidateSignUpRequest)));
    }

    public static Result addPassword() {
        Form<CandidateSignUpRequest> candidateForm = Form.form(CandidateSignUpRequest.class);
        CandidateSignUpRequest candidateSignUpRequest = candidateForm.bindFromRequest().get();
        return ok(toJson(Auth.addAuth(candidateSignUpRequest)));
    }

    public static Result savePassword() {
        Form<ResetPasswordResquest> resetPassword = Form.form(ResetPasswordResquest.class);
        ResetPasswordResquest resetPasswordResquest = resetPassword.bindFromRequest().get();
        return ok(toJson(Auth.savePassword(resetPasswordResquest)));
    }

    public static Result assessment() {
        return ok(views.html.assessment.render());
    }

    public static Result logIn() {
        return ok(views.html.login.render());
    }

    public static Result loginSubmit() {
        Form<LoginRequest> loginForm = Form.form(LoginRequest.class);
        LoginRequest loginRequest = loginForm.bindFromRequest().get();
        return ok(toJson(Candidate.login(loginRequest)));
    }

    public static Result dashboard() {
        return ok(views.html.dashboard.render());
    }

    public static Result checkCandidate() {
        Form<ResetPasswordResquest> checkCandidate = Form.form(ResetPasswordResquest.class);
        ResetPasswordResquest resetPasswordResquest = checkCandidate.bindFromRequest().get();
        return ok(toJson(Candidate.checkCandidate(resetPasswordResquest)));
    }

    public static Result checkResetOtp() {
        Form<ResetPasswordResquest> checkResetOtp = Form.form(ResetPasswordResquest.class);
        ResetPasswordResquest resetPasswordResquest = checkResetOtp.bindFromRequest().get();
        return ok(toJson(Candidate.checkResetOtp(resetPasswordResquest)));
    }
}
