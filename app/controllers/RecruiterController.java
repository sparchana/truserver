package controllers;

import api.InteractionConstants;
import api.ServerConstants;
import api.http.FormValidator;
import api.http.httpRequest.AddJobPostRequest;
import api.http.httpRequest.LoginRequest;
import api.http.httpRequest.Recruiter.*;
import api.http.httpRequest.ResetPasswordResquest;
import api.http.httpRequest.Workflow.MatchingCandidateRequest;
import api.http.httpResponse.CandidateWorkflowData;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import controllers.businessLogic.JobService;
import controllers.businessLogic.JobWorkflow.JobPostWorkflowEngine;
import controllers.businessLogic.Recruiter.RecruiterAuthService;
import controllers.businessLogic.Recruiter.RecruiterLeadService;
import controllers.businessLogic.RecruiterService;
import controllers.security.FlashSessionController;
import controllers.security.RecruiterSecured;
import dao.JobPostDAO;
import dao.JobPostWorkFlowDAO;
import dao.RecruiterDAO;
import models.entity.Candidate;
import models.entity.JobPost;
import models.entity.OM.JobPostWorkflow;
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

import static api.InteractionConstants.INTERACTION_CHANNEL_CANDIDATE_WEBSITE;
import static controllers.businessLogic.Recruiter.RecruiterInteractionService.createInteractionForRecruiterSearchCandidate;
import static play.libs.Json.toJson;
import static play.mvc.Controller.request;
import static play.mvc.Controller.session;
import static play.mvc.Results.*;
/**
 * Created by dodo on 4/10/16.
 */
public class RecruiterController {
    public static Result recruiterIndex() {
        String sessionId = session().get("recruiterId");
        if(sessionId != null){
            if(!FlashSessionController.isEmpty()){
                return redirect(FlashSessionController.getFlashFromSession());
            }
                return redirect("/recruiter/home");
        }
        return ok(views.html.Recruiter.recruiter_index.render());
    }

    @Security.Authenticated(RecruiterSecured.class)
    public static Result recruiterHome() {
        /* Adding it here, assuming , login always redirect to home first */
        if(!FlashSessionController.isEmpty()){
            return redirect(FlashSessionController.getFlashFromSession());
        }
        return ok(views.html.Recruiter.recruiter_home.render());
    }

    public static Result logoutRecruiter() {
        FlashSessionController.clearSessionExceptFlash();
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

    @Security.Authenticated(RecruiterSecured.class)
    public static Result getRecruiterProfileInfo() {
        RecruiterProfile recruiterProfile = RecruiterProfile.find.where().eq("recruiterProfileId", session().get("recruiterId")).findUnique();
        if(recruiterProfile != null) {
            return ok(toJson(recruiterProfile));
        }
        return ok("0");
    }

    @Security.Authenticated(RecruiterSecured.class)
    public static Result recruiterCandidateSearch(Long jobPostId){
        /* job post id is being used from url in js */
        Long recruiterId = Long.valueOf(session().get("recruiterId"));
        RecruiterProfile recruiterProfile = RecruiterDAO.findById(recruiterId);
        if(recruiterProfile == null) {
            return badRequest();
        }

        if(recruiterProfile.getRecruiterAccessLevel() == ServerConstants.RECRUITER_ACCESS_LEVEL_PRIVATE) {
            return ok(views.html.Recruiter.rmp.recruiter_candidate_search.render());
        }
        return ok(views.html.Recruiter.recruiter_candidate_search.render());
    }

    @Security.Authenticated(RecruiterSecured.class)
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

    @Security.Authenticated(RecruiterSecured.class)
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
        return ok(toJson(RecruiterService.createRecruiterProfile(recruiterSignUpRequest, INTERACTION_CHANNEL_CANDIDATE_WEBSITE)));
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

    @Security.Authenticated(RecruiterSecured.class)
    public static Result getAllJobApplicants(long jobPostId) {
        JobPost jobPost = JobPostDAO.findById(jobPostId);
        if(jobPost != null){
            if(session().get("recruiterId") != null){
                RecruiterProfile recruiterProfile = RecruiterProfile.find.where().eq("RecruiterProfileId", session().get("recruiterId")).findUnique();
                if(recruiterProfile != null){
                    if(jobPost.getRecruiterProfile() != null){
                        if(Objects.equals(jobPost.getRecruiterProfile().getRecruiterProfileId(), recruiterProfile.getRecruiterProfileId())){

                            //initially we were returning the returned map directly. Since we need the list of candidate in ascending order of the interview date,
                            //  we are adding the values of the map in a list. This is being done because the order of map vales was getting sorted in ascending value
                            // with respect to the key valus. Hence using a list here

                            Map<Long, CandidateWorkflowData> selectedCandidateMap =
                                                             JobPostWorkflowEngine.getRecruiterJobLinedUpCandidates(jobPostId);

                            List<CandidateWorkflowData> jobApplicantList = new LinkedList<>();
                            for (Map.Entry<Long, CandidateWorkflowData> entry : selectedCandidateMap.entrySet()) {
                                sanitizeCandidateDate(entry.getValue().getCandidate());
                                jobApplicantList.add(entry.getValue());
                            }

                            return ok(toJson(jobApplicantList));
                        }
                    }
                }
            }
        }
        return ok("0");
    }

    private static void sanitizeCandidateDate(Candidate candidate) {
        candidate.setJobApplicationList(null);
        candidate.setLead(null);
        candidate.setCandidateUUId(null);
        candidate.setLocalityPreferenceList(null);
        candidate.setCandidateprofilestatus(null);
    }

    @Security.Authenticated(RecruiterSecured.class)
    public static Result getAllRecruiterJobPosts() {
        if(session().get("recruiterId") != null){

            Map<?, JobPost> recruiterJobPostMap = JobPost.find.where().eq("JobRecruiterId", session().get("recruiterId")).setMapKey("jobPostId").findMap();

            String jpIdList = "";

            RecruiterProfile recruiterProfile = RecruiterProfile.find.where().eq("RecruiterProfileId", session().get("recruiterId")).findUnique();
            for(Map.Entry<?, JobPost> entity: recruiterJobPostMap.entrySet()) {
                JobPost jobPost = entity.getValue();

                //checking recruiter and job post company
                if(Objects.equals(jobPost.getCompany().getCompanyId(), recruiterProfile.getCompany().getCompanyId())){
                    jpIdList += "'" + jobPost.getJobPostId() + "', ";
                }
            }

            List<JobPostWorkflow> jobPostWorkflowList = new ArrayList<>();

            if(Objects.equals(jpIdList, "")){
                return ok(toJson(jobPostWorkflowList));
            }
            // if candidate who have applied to the jobpost, only those jobpostworkflow obj will be returned
            jobPostWorkflowList = JobPostWorkFlowDAO.getJobApplications(jpIdList.substring(0, jpIdList.length()-2));

            Map<Long, RecruiterJobPostObject> recruiterJobPostResponseMap = new LinkedHashMap<>();
            for(JobPostWorkflow jpwf : jobPostWorkflowList){
                RecruiterJobPostObject singleObject = recruiterJobPostResponseMap.get(jpwf.getJobPost().getJobPostId());

                if(singleObject == null ){
                    singleObject = new RecruiterJobPostObject();
                    recruiterJobPostResponseMap.put(jpwf.getJobPost().getJobPostId(), singleObject);
                    singleObject.setJobPost(jpwf.getJobPost());
                }

                // jpwf of one candidate
                Map<Long, JobPostWorkflow> response = singleObject.getJobPostWorkflowMap();
                if(response == null){
                    response = new HashMap<>();
                }
                if(response.get(jpwf.getCandidate().getCandidateId()) == null){
                    response.put(jpwf.getCandidate().getCandidateId(), jpwf);

                    //here we are enhancing the 'new application' interview count. We have two variables now.
                    // pendingCount: contains all the application whose status is 'Scheduled'
                    // upcoming count: contains all the application whose status is confirmed and feedback is not set

                    Date today = new Date();
                    Calendar now = Calendar.getInstance();
                    Calendar cal = Calendar.getInstance();

                    //checking all the pendingConfirmation applications
                    if(jpwf.getStatus().getStatusId() == ServerConstants.JWF_STATUS_INTERVIEW_SCHEDULED){
                        singleObject.setPendingCount(singleObject.getPendingCount() + 1);
                    } else if(jpwf.getStatus().getStatusId() >= ServerConstants.JWF_STATUS_INTERVIEW_CONFIRMED
                            && jpwf.getStatus().getStatusId() < ServerConstants.JWF_STATUS_CANDIDATE_FEEDBACK_STATUS_COMPLETE_SELECTED){

                        //checking all the todays and upcoming interview applications


                        Date interviewDate = jpwf.getScheduledInterviewDate();
                        cal.setTime(interviewDate);

                        //today's interviews
                        if(now.get(Calendar.YEAR) == cal.get(Calendar.YEAR) && (now.get(Calendar.MONTH) + 1) == (cal.get(Calendar.MONTH) + 1)
                                && now.get(Calendar.DATE) == cal.get(Calendar.DATE)){

                            singleObject.setUpcomingCount(singleObject.getUpcomingCount() + 1);
                        } else if(interviewDate.after(today)){
                            //future interviews
                            singleObject.setUpcomingCount(singleObject.getUpcomingCount() + 1);
                        }

                        //rest all the applications are past interviews, hence we are not counting
                    }

                    singleObject.setTotalCount(singleObject.getTotalCount()+1);
                    singleObject.setJobPostWorkflowMap(response);
                }
            }


            for(Map.Entry<?, JobPost> entity: recruiterJobPostMap.entrySet()) {
                // jobpost to which no candidate has applied
                RecruiterJobPostObject singleObject = recruiterJobPostResponseMap.get(entity.getKey());
                if(singleObject == null) {
                    singleObject = new RecruiterJobPostObject();
                    singleObject.setJobPost(entity.getValue());
                    singleObject.setTotalCount(0);
                    singleObject.setPendingCount(0);

                    recruiterJobPostResponseMap.put((Long) entity.getKey(), singleObject);
                }
            }

            List<RecruiterJobPostObject> listToBeReturned = new ArrayList<>();

            for(Map.Entry<?, RecruiterJobPostObject> map : recruiterJobPostResponseMap.entrySet()) {
                RecruiterJobPostObject object = new RecruiterJobPostObject();
                if(Objects.equals(map.getValue().getJobPost().getCompany().getCompanyId(), map.getValue().getJobPost().getRecruiterProfile().getCompany().getCompanyId())){
                    sanitizeJobPostData(map.getValue().getJobPost());
                    object.setJobPost(map.getValue().getJobPost());
                    object.setTotalCount(map.getValue().getTotalCount());
                    object.setPendingCount(map.getValue().getPendingCount());
                    object.setUpcomingCount(map.getValue().getUpcomingCount());

                    listToBeReturned.add(object);
                }
            }

            return ok(toJson(listToBeReturned));
        }
        return ok("0");
    }

    public static void sanitizeJobPostData(JobPost jobPost){
        jobPost.setJobPostDescription(null);
        jobPost.setJobPostAddress(null);
        jobPost.setPricingPlanType(null);
        jobPost.setJobRole(null);
        jobPost.setCompany(null);
        jobPost.setRecruiterProfile(null);
        jobPost.setJobPostLanguageRequirements(null);
        jobPost.setJobPostDocumentRequirements(null);
    }

    public static class RecruiterJobPostObject{
        Map<Long, JobPostWorkflow> jobPostWorkflowMap;
        int pendingCount;
        int upcomingCount;
        int totalCount;
        JobPost jobPost;

        public RecruiterJobPostObject() {
            pendingCount = 0;
            totalCount = 0;
            upcomingCount = 0;
        }

        public RecruiterJobPostObject(Map<Long, JobPostWorkflow> jobPostWorkflowMap, int pendingCount, int totalCount, int upcomingCount) {
            this.jobPostWorkflowMap = jobPostWorkflowMap;
            this.pendingCount = pendingCount;
            this.upcomingCount = upcomingCount;
            this.totalCount = totalCount;
        }

        public Map<Long, JobPostWorkflow> getJobPostWorkflowMap() {
            return jobPostWorkflowMap;
        }

        public void setJobPostWorkflowMap(Map<Long, JobPostWorkflow> jobPostWorkflowMap) {
            this.jobPostWorkflowMap = jobPostWorkflowMap;
        }

        public int getPendingCount() {
            return pendingCount;
        }

        public void setPendingCount(int pendingCount) {
            this.pendingCount = pendingCount;
        }

        public int getTotalCount() {
            return totalCount;
        }

        public void setTotalCount(int totalCount) {
            this.totalCount = totalCount;
        }

        public JobPost getJobPost() {
            return jobPost;
        }

        public void setJobPost(JobPost jobPost) {
            this.jobPost = jobPost;
        }

        public int getUpcomingCount() {
            return upcomingCount;
        }

        public void setUpcomingCount(int upcomingCount) {
            this.upcomingCount = upcomingCount;
        }
    }

    @Security.Authenticated(RecruiterSecured.class)
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
                boolean isPrivate = recruiterProfile.getRecruiterAccessLevel() == ServerConstants.RECRUITER_ACCESS_LEVEL_PRIVATE;
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
                            matchingCandidateRequest.getJobPostDocumentIdList(),
                            matchingCandidateRequest.getJobPostAssetIdList(),
                            matchingCandidateRequest.getDistanceRadius(),
                            isPrivate);

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

                    Logger.info("total list size: " + listToBeReturned.size());

                    //getting limited results
                    for (CandidateWorkflowData val : listToBeReturned) {
                        if(count >= matchingCandidateRequest.getInitialValue()){
                            if(count < (matchingCandidateRequest.getInitialValue() + 10) ){
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

    @Security.Authenticated(RecruiterSecured.class)
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

    @Security.Authenticated(RecruiterSecured.class)
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

    @Security.Authenticated(RecruiterSecured.class)
    public static Result recruiterJobPost(Long id) {
        return ok(views.html.Recruiter.recruiter_post_free_job.render());
    }

    public static Result getRecruiterJobPostInfo(long jpId) {
        JobPost jobPost = JobPostDAO.findById(jpId);
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

    @Security.Authenticated(RecruiterSecured.class)
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
        return ok(toJson(JobService.addJobPost(addJobPostRequest, INTERACTION_CHANNEL_CANDIDATE_WEBSITE)));
    }

    @Security.Authenticated(RecruiterSecured.class)
    public static Result renderAllRecruiterJobPosts() {
        Long recruiterId = Long.valueOf(session().get("recruiterId"));
        RecruiterProfile recruiterProfile = RecruiterDAO.findById(recruiterId);
        if(recruiterProfile == null) {
            return badRequest();
        }

        if(recruiterProfile.getRecruiterAccessLevel() == ServerConstants.RECRUITER_ACCESS_LEVEL_PRIVATE) {
            return ok(views.html.Recruiter.rmp.recruiter_my_jobs.render());
        }

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

    public static Result updateInterviewStatus() {
        JsonNode req = request().body().asJson();

        InterviewStatusRequest interviewStatusRequest = new InterviewStatusRequest();
        ObjectMapper newMapper = new ObjectMapper();
        try {
            interviewStatusRequest = newMapper.readValue(req.toString(), InterviewStatusRequest.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(session().get("sessionChannel") == null){
            Logger.warn("SessionChannel null, hence recruiter logged out");
            logoutRecruiter();
            return badRequest();
        }

        return JobPostWorkflowEngine.updateInterviewStatus(interviewStatusRequest, InteractionConstants.INTERACTION_CHANNEL_RECRUITER_WEBSITE);
    }

    @Security.Authenticated(RecruiterSecured.class)
    public static Result getTodayInterviewDetails() {
        JsonNode req = request().body().asJson();

        InterviewTodayRequest interviewTodayRequest = new InterviewTodayRequest();
        ObjectMapper newMapper = new ObjectMapper();
        try {
            interviewTodayRequest = newMapper.readValue(req.toString(), InterviewTodayRequest.class);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return ok(toJson(JobPostWorkflowEngine.getTodaysInterviewDetails(interviewTodayRequest)));
    }

    @Security.Authenticated(RecruiterSecured.class)
    public static Result getPendingCandidateApproval() {
        JsonNode req = request().body().asJson();

        InterviewTodayRequest interviewTodayRequest = new InterviewTodayRequest();
        ObjectMapper newMapper = new ObjectMapper();
        try {
            interviewTodayRequest = newMapper.readValue(req.toString(), InterviewTodayRequest.class);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return ok(toJson(JobPostWorkflowEngine.processDataPendingApproval(interviewTodayRequest)));
    }

    // sorting helper methods
    private static class LastActiveComparator implements Comparator<CandidateWorkflowData> {

        @Override
        public int compare(CandidateWorkflowData o1, CandidateWorkflowData o2) {
            if (o1.getExtraData().getLastActive() == null) {
                return (o2.getExtraData().getLastActive() == null) ? 0 : 1;
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

    @Security.Authenticated(RecruiterSecured.class)
    public static Result renderAllApplications(long id) {
        return ok(views.html.Recruiter.recruiter_applied_candidates.render());
    }

    @Security.Authenticated(RecruiterSecured.class)
    public static Result renderAllUnlockedCandidates() {
        return ok(views.html.Recruiter.recruiter_unlocked_candidate.render());
    }

    public static Result trackApplication(long id) {
        return ok(views.html.Recruiter.recruiter_interviews.render());
    }

}