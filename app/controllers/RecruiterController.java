package controllers;

import api.ServerConstants;
import api.http.FormValidator;
import api.http.httpRequest.AddJobPostRequest;
import api.http.httpRequest.LoginRequest;
import api.http.httpRequest.Recruiter.AddCreditRequest;
import api.http.httpRequest.Recruiter.RecruiterLeadRequest;
import api.http.httpRequest.Recruiter.RecruiterSignUpRequest;
import api.http.httpRequest.ResetPasswordResquest;
import api.http.httpRequest.Workflow.MatchingCandidateRequest;
import api.http.httpResponse.CandidateWorkflowData;
import api.http.httpResponse.Recruiter.JobApplicationResponse;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import controllers.businessLogic.*;
import controllers.businessLogic.JobWorkflow.JobPostWorkflowEngine;
import controllers.businessLogic.Recruiter.RecruiterAuthService;
import controllers.businessLogic.Recruiter.RecruiterLeadService;
import controllers.security.SecuredUser;
import models.entity.JobPost;
import models.entity.OM.JobApplication;
import models.entity.Recruiter.OM.RecruiterToCandidateUnlocked;
import models.entity.Recruiter.RecruiterAuth;
import models.entity.Recruiter.RecruiterProfile;
import models.entity.Recruiter.Static.RecruiterCreditCategory;
import models.entity.RecruiterCreditHistory;
import play.Logger;
import play.mvc.Result;
import play.mvc.Security;

import java.io.IOException;
import java.util.*;

import static controllers.businessLogic.Recruiter.RecruiterInteractionService.createInteractionForRecruiterSearchCandidate;
import static play.libs.Json.toJson;
import static play.mvc.Controller.request;
import static play.mvc.Controller.session;
import static play.mvc.Results.badRequest;
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
            RecruiterProfile recruiterProfile = RecruiterProfile.find.where().eq("RecruiterProfileId", sessionRecruiterId).findUnique();
            if(recruiterProfile != null){
                RecruiterAuth recruiterAuth = RecruiterAuth.find.where().eq("recruiter_id", recruiterProfile.getRecruiterProfileId()).findUnique();
                if(recruiterAuth != null){
                    if(recruiterAuth.getRecruiterAuthStatus() == 1){
                        return ok(toJson(recruiterProfile));
                    }
                }
            }
        }
        return ok("0");
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
        return ok(toJson(RecruiterLeadService.createLeadWithOtherDetails(recruiterLeadRequest,
                ServerConstants.LEAD_CHANNEL_RECRUITER)));
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
    public static Result recruiterCandidateSearch(){
        return ok(views.html.Recruiter.recruiter_candidate_search.render());
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
            RecruiterCreditHistory recruiterCreditHistoryLatest = RecruiterCreditHistory.find.where()
                    .eq("RecruiterProfileId", recruiterProfile.getRecruiterProfileId())
                    .eq("RecruiterCreditCategory", ServerConstants.RECRUITER_CATEGORY_CONTACT_UNLOCK)
                    .setMaxRows(1)
                    .orderBy("create_timestamp desc")
                    .findUnique();

            if(recruiterCreditHistoryLatest != null){
                if(recruiterCreditHistoryLatest.getRecruiterCreditsAvailable() > 0){
                    return ok("1");
                }
            }
        }
        return ok("0");
    }

    @Security.Authenticated(SecuredUser.class)
    public static Result addRecruiter() {
        JsonNode req = request().body().asJson();
        Logger.info("Browser: " +  request().getHeader("User-Agent") + "; Req JSON : " + req );
        RecruiterSignUpRequest recruiterSignUpRequest = new RecruiterSignUpRequest();
        ObjectMapper newMapper = new ObjectMapper();
        try {
            recruiterSignUpRequest = newMapper.readValue(req.toString(), RecruiterSignUpRequest.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ok(toJson(RecruiterService.createRecruiterProfile(recruiterSignUpRequest, InteractionService.InteractionChannelType.SELF)));
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
        JobPost jobPost = JobPost.find.where().eq("jobPostId", jobPostId).findUnique();
        if(jobPost != null){
            if(session().get("recruiterId") != null){
                RecruiterProfile recruiterProfile = RecruiterProfile.find.where().eq("RecruiterProfileId", session().get("recruiterId")).findUnique();
                if(recruiterProfile != null){
                    if(jobPost.getRecruiterProfile() != null){
                        if(Objects.equals(jobPost.getRecruiterProfile().getRecruiterProfileId(), recruiterProfile.getRecruiterProfileId())){
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
                    }
                }
            }
        }

        return ok("0");
    }

    public static Result getAllRecruiterJobPosts() {
        if(session().get("recruiterId") != null){
            return ok(toJson(JobPost.find.where().eq("JobRecruiterId", session().get("recruiterId")).findList()));
        }
        return ok("0");
    }

    @Security.Authenticated(SecuredUser.class)
    public static Result getMatchingCandidate() {
        JsonNode matchingCandidateRequestJson = request().body().asJson();
        Logger.info("Browser: " +  request().getHeader("User-Agent") + "; Req JSON : " + matchingCandidateRequestJson);
        if(matchingCandidateRequestJson == null){
            return badRequest();
        }
        MatchingCandidateRequest matchingCandidateRequest= new MatchingCandidateRequest();
        ObjectMapper newMapper = new ObjectMapper();

        // since jsonReq has single/multiple values in array
        newMapper.enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY);

        try {
            matchingCandidateRequest = newMapper.readValue(matchingCandidateRequestJson.toString(), MatchingCandidateRequest.class);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if(session().get("recruiterId") != null) {
            RecruiterProfile recruiterProfile = RecruiterProfile.find.where().eq("RecruiterProfileId", session().get("recruiterId")).findUnique();
            if(recruiterProfile != null){
                if (matchingCandidateRequest != null) {
                    Map<Long, CandidateWorkflowData> candidateSearchMap = JobPostWorkflowEngine.getCandidateForRecruiterSearch(
                            matchingCandidateRequest.getMaxAge(),
                            matchingCandidateRequest.getMinSalary(),
                            matchingCandidateRequest.getMaxSalary(),
                            matchingCandidateRequest.getGender(),
                            matchingCandidateRequest.getExperienceIdList(),
                            matchingCandidateRequest.getJobPostJobRoleId(),
                            matchingCandidateRequest.getJobPostEducationIdList(),
                            matchingCandidateRequest.getJobPostLocalityIdList(),
                            matchingCandidateRequest.getJobPostLanguageIdList(),
                            matchingCandidateRequest.getDistanceRadius());

                    //computing interactionResult values
                    String result = "Search Candidate. Total Candidates found: " + candidateSearchMap.size() +
                            ". Search parameters: Max age: " + matchingCandidateRequest.getMaxAge()+
                            ", Min salary: " + matchingCandidateRequest.getMinSalary()+
                            ", Max salary: " + matchingCandidateRequest.getMaxSalary()+
                            ", Gender: " + matchingCandidateRequest.getGender()+
                            ", Experience: " + matchingCandidateRequest.getExperienceId()+
                            ", Job role: " + matchingCandidateRequest.getJobPostJobRoleId()+
                            ", Education: " + matchingCandidateRequest.getJobPostEducationId()+
                            ", Localities: " + matchingCandidateRequest.getJobPostLocalityIdList()+
                            ", Languages: " + matchingCandidateRequest.getJobPostLanguageIdList()+
                            ", Distance radius: " + matchingCandidateRequest.getDistanceRadius();

                    if(matchingCandidateRequest.getInitialValue() == 0){
                        //creating search candidate parameters
                        createInteractionForRecruiterSearchCandidate(recruiterProfile.getRecruiterProfileUUId(), result);
                    }

                    List<CandidateWorkflowData> listToBeReturned = new ArrayList<>();
                    List<CandidateWorkflowData> finalListToBeReturned = new ArrayList<>();

                    Integer count = 0;
                    for (Map.Entry<Long, CandidateWorkflowData> val : candidateSearchMap.entrySet()) {
                        CandidateWorkflowData candidateWorkflowData = new CandidateWorkflowData();
                        candidateWorkflowData.setCandidate(val.getValue().getCandidate());
                        candidateWorkflowData.setExtraData(val.getValue().getExtraData());
                        listToBeReturned.add(candidateWorkflowData);
                    }

                    // sort by last active
                    // TODO move 1,2,3 to server constant
                    if (Objects.equals(matchingCandidateRequest.getSortBy(), ServerConstants.REC_SORT_LASTEST_ACTIVE)) {
                        // last active, latest on top
                        Collections.sort(listToBeReturned,  new LastActiveComparator());
                    } else if (Objects.equals(matchingCandidateRequest.getSortBy(), ServerConstants.REC_SORT_SALARY_H_TO_L)) {
                        // candidate lw salary H->L
                        Collections.sort(listToBeReturned,  new SalaryComparatorHtoL());
                    } else if (Objects.equals(matchingCandidateRequest.getSortBy(), ServerConstants.REC_SORT_SALARY_L_TO_H)) {
                        // candidate lw salary L->H
                        Collections.sort(listToBeReturned,  new SalaryComparatorLtoH());
                    }

                    //getting limited results
                    for (CandidateWorkflowData val : listToBeReturned) {
                        if(count >= matchingCandidateRequest.getInitialValue()){
                            if(count < (matchingCandidateRequest.getInitialValue()+10) ){
                                val.getCandidate().setCandidateMobile("");
                                val.getCandidate().setCandidateEmail("");
                                finalListToBeReturned.add(val);
                            }
                        }
                        count ++;
                        if(count == (matchingCandidateRequest.getInitialValue() + 10)){
                            break;
                        }
                    }

                    return ok(toJson(finalListToBeReturned));
                }
            }
        }
        return ok("0");
    }

    @Security.Authenticated(SecuredUser.class)
    public static Result getUnlockedCandidates() {
        if(session().get("recruiterId") != null){
            RecruiterProfile recruiterProfile = RecruiterProfile.find.where().eq("recruiterProfileId", session().get("recruiterId")).findUnique();
            if(recruiterProfile != null){
                return ok(toJson(RecruiterToCandidateUnlocked.find.where()
                        .eq("recruiterProfileId", recruiterProfile.getRecruiterProfileId())
                        .findList()));
            }
        }
        return ok("0");
    }

    @Security.Authenticated(SecuredUser.class)
    public static Result requestCredits() {
        JsonNode req = request().body().asJson();
        Logger.info("req JSON: " + req );
        AddCreditRequest addCreditRequest = new AddCreditRequest();
        ObjectMapper newMapper = new ObjectMapper();
        try {
            addCreditRequest = newMapper.readValue(req.toString(), AddCreditRequest.class);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return ok(toJson(RecruiterService.requestCreditForRecruiter(addCreditRequest)));
    }

    @Security.Authenticated(SecuredUser.class)
    public static Result recruiterJobPost(Long id) {
        return ok(views.html.Recruiter.recruiter_post_free_job.render());
    }

    public static Result getRecruiterJobPostInfo(long jpId) {
        JobPost jobPost = JobPost.find.where().eq("jobPostId", jpId).findUnique();
        if(jobPost != null){
            if(session().get("recruiterId") != null){
                RecruiterProfile recruiterProfile = RecruiterProfile.find.where().eq("RecruiterProfileId", session().get("recruiterId")).findUnique();
                if(recruiterProfile != null){
                    if(jobPost.getRecruiterProfile() != null){
                        if(Objects.equals(jobPost.getRecruiterProfile().getRecruiterProfileId(), recruiterProfile.getRecruiterProfileId())){
                            return ok(toJson(jobPost));
                        }
                    }
                }
            }
        }
        return ok("0");
    }

    @Security.Authenticated(SecuredUser.class)
    public static Result addJobPost() {
        JsonNode req = request().body().asJson();
        Logger.info("Browser: " + request().getHeader("User-Agent") + "; Req JSON : " + req);
        AddJobPostRequest addJobPostRequest = new AddJobPostRequest();
        ObjectMapper newMapper = new ObjectMapper();
        newMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        try {
            addJobPostRequest = newMapper.readValue(req.toString(), AddJobPostRequest.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ok(toJson(JobService.addJobPost(addJobPostRequest, InteractionService.InteractionChannelType.SELF)));
    }

    public static Result renderAllRecruiterJobPosts() {
        return ok(views.html.Recruiter.recruiter_my_jobs.render());
    }
    public static Result recruiterNavbar() {
        return ok(views.html.Recruiter.recruiter_navbar.render());
    }

    public static Result findRecruiterAndSendOtp() {
        JsonNode req = request().body().asJson();
        ResetPasswordResquest resetPasswordResquest = new ResetPasswordResquest();
        ObjectMapper newMapper = new ObjectMapper();
        try {
            resetPasswordResquest = newMapper.readValue(req.toString(), ResetPasswordResquest.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        String recruiterMobile = resetPasswordResquest.getResetPasswordMobile();

        return ok(toJson(RecruiterService.findRecruiterAndSendOtp(FormValidator.convertToIndianMobileFormat(recruiterMobile))));

    }

    // sorting helper methods
    private static class LastActiveComparator implements Comparator<CandidateWorkflowData> {

        @Override
        public int compare(CandidateWorkflowData o1, CandidateWorkflowData o2) {
            if (o1.getExtraData().getLastActive() == null) {
                return (o2.getCandidate().getCandidateLastWithdrawnSalary() == null) ? 0 : 1;
            }
            if (o2.getExtraData().getLastActive() == null) {
                return -1;
            }
            return o1.getExtraData().getLastActive().lastActiveValueId.compareTo(o2.getExtraData().getLastActive().lastActiveValueId);

        }
    }
    private static class SalaryComparatorHtoL implements Comparator<CandidateWorkflowData> {

        @Override
        public int compare(CandidateWorkflowData o1, CandidateWorkflowData o2) {
            if (o1.getCandidate().getCandidateLastWithdrawnSalary() == null) {
                return (o2.getCandidate().getCandidateLastWithdrawnSalary() == null) ? 0 : 1;
            }
            if (o2.getCandidate().getCandidateLastWithdrawnSalary() == null) {
                return -1;
            }
            return o2.getCandidate().getCandidateLastWithdrawnSalary().compareTo(o1.getCandidate().getCandidateLastWithdrawnSalary());
        }
    }
    private static class SalaryComparatorLtoH implements Comparator<CandidateWorkflowData> {

        @Override
        public int compare(CandidateWorkflowData o1, CandidateWorkflowData o2) {
            if (o1.getCandidate().getCandidateLastWithdrawnSalary() == null) {
                return (o2.getCandidate().getCandidateLastWithdrawnSalary() == null) ? 0 : 1;
            }
            if (o2.getCandidate().getCandidateLastWithdrawnSalary() == null) {
                return -1;
            }
            return o1.getCandidate().getCandidateLastWithdrawnSalary().compareTo(o2.getCandidate().getCandidateLastWithdrawnSalary());
        }
    }

    public static Result renderAllApplications(long id) {
        return ok(views.html.Recruiter.recruiter_applied_candidates.render());
    }

    public static Result renderAllUnlockedCandidates() {
        return ok(views.html.Recruiter.recruiter_unlocked_candidate.render());
    }

}