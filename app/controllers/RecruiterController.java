package controllers;

import api.ServerConstants;
import api.http.httpRequest.LoginRequest;
import api.http.httpRequest.Recruiter.RecruiterLeadRequest;
import api.http.httpRequest.Recruiter.RecruiterSignUpRequest;
import api.http.httpResponse.Recruiter.JobApplicationResponse;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import controllers.businessLogic.*;
import controllers.security.SecuredUser;
import models.entity.JobPost;
import models.entity.OM.JobApplication;
import models.entity.Recruiter.RecruiterProfile;
import models.entity.Recruiter.Static.RecruiterCreditCategory;
import models.entity.RecruiterCreditHistory;
import models.entity.Static.Degree;
import models.entity.Static.InterviewTimeSlot;
import models.entity.Static.Locality;
import play.Logger;
import play.mvc.Result;
import play.mvc.Security;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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

    @Security.Authenticated(SecuredUser.class)
    public static Result recruiterHome() {
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
        Logger.info("req JSON: save password json. hiding password. rec mobile mobile: " + recruiterSignUpRequest.getRecruiterAuthMobile());

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
        Logger.info("req JSON: login json. hiding password. login mobile: " + loginRequest.getCandidateLoginMobile());
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

    @Security.Authenticated(SecuredUser.class)
    public static Result getRecruiterProfileInfo() {
        RecruiterProfile recruiterProfile = RecruiterProfile.find.where().eq("recruiterProfileId", session().get("recruiterId")).findUnique();
        if(recruiterProfile != null) {
            return ok(toJson(recruiterProfile));
        }
        return ok("0");
    }

    @Security.Authenticated(SecuredUser.class)
    public static Result recruiterEditProfile() {
        return ok(views.html.Recruiter.recruiter_edit_profile.render());
    }

    public static Result getAllCreditCategory() {
        List<RecruiterCreditCategory> recruiterCreditCategoryList = RecruiterCreditCategory.find.all();
        return ok(toJson(recruiterCreditCategoryList));
    }

    public static Result getRecruiterCredits(Long recId) {
        RecruiterProfile recruiterProfile = RecruiterProfile.find.where().eq("RecruiterProfileId", recId).findUnique();
        if(recruiterProfile != null){
            if(recruiterProfile.getRecruiterCandidateUnlockCredits() != null && recruiterProfile.getRecruiterInterviewUnlockCredits() != null){
                if(recruiterProfile.getRecruiterCandidateUnlockCredits() > 0 && recruiterProfile.getRecruiterInterviewUnlockCredits() > 0){
                    return ok("1");
                }
            }
        }
        return ok("0");
    }

    public static Result unlockCandidateContact(Long candidateId) {
        if(session().get("recruiterId") != null){
            RecruiterProfile recruiterProfile = RecruiterProfile.find.where().eq("RecruiterProfileId", session().get("recruiterId")).findUnique();
            if(recruiterProfile != null){
                return RecruiterService.unlockCandidate(recruiterProfile, candidateId);
            }
        }
        // no recruiter session found
        return ok("-1");
    }

    public static Result getAllJobApplicants(long jobPostId) {
        JobPost jobPost = JobPost.find.where().eq("JobPostId", jobPostId).findUnique();
        if(jobPost != null){
            List<JobApplication> jobApplicationList = JobApplication.find.where().eq("JobPostId", jobPostId).findList();
            List<JobApplicationResponse> jobApplicationResponseList = new ArrayList<>();
            for(JobApplication jobApplication: jobApplicationList){
                JobApplicationResponse jobApplicationResponse = new JobApplicationResponse();

                jobApplicationResponse.setCandidate(jobApplication.getCandidate());
                jobApplicationResponse.setJobApplicationId(jobApplication.getJobApplicationId());
                jobApplicationResponse.setJobApplicationCreatingTimeStamp(String.valueOf(jobApplication.getJobApplicationCreateTimeStamp()));
                jobApplicationResponse.setPreScreenLocation(jobApplication.getLocality());
                jobApplicationResponse.setPreScreenLocation(jobApplication.getLocality());
                jobApplicationResponse.setInterviewTimeSlot(jobApplication.getInterviewTimeSlot());
                jobApplicationResponse.setScheduledInterviewDate(jobApplication.getScheduledInterviewDate());

                jobApplicationResponseList.add(jobApplicationResponse);
            }

            return ok(toJson(jobApplicationResponseList));
        }
        return ok("0");
    }

    public static Result getAllRecruiterJobPosts() {
        if(session().get("recruiterId") != null){
            return ok(toJson(JobPost.find.where().eq("JobRecruiterId", session().get("recruiterId")).findList()));
        }
        return ok("0");
    }
}