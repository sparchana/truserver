package controllers;

import api.InteractionConstants;
import api.ServerConstants;
import api.http.FormValidator;
import api.http.httpRequest.AddJobPostRequest;
import api.http.httpRequest.LoginRequest;
import api.http.httpRequest.Recruiter.*;
import api.http.httpRequest.Recruiter.rmp.EmployeeBulkSmsRequest;
import api.http.httpRequest.ResetPasswordResquest;
import api.http.httpRequest.Workflow.MatchingCandidateRequest;
import api.http.httpResponse.BulkUploadResponse;
import api.http.httpResponse.CandidateWorkflowData;
import api.http.httpResponse.Recruiter.MultipleCandidateContactUnlockResponse;
import api.http.httpResponse.Recruiter.RMP.ApplicationResponse;
import api.http.httpResponse.Recruiter.RMP.EmployeeResponse;
import api.http.httpResponse.Recruiter.RMP.NextRoundComponents;
import api.http.httpResponse.Recruiter.RMP.SmsReportResponse;
import api.http.httpResponse.Recruiter.UnlockContactResponse;
import api.http.httpResponse.Workflow.InterviewSlotPopulateResponse;
import api.http.httpResponse.interview.InterviewResponse;
import com.avaje.ebean.Ebean;
import com.avaje.ebean.RawSql;
import com.avaje.ebean.RawSqlBuilder;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import controllers.businessLogic.EmployeeService;
import controllers.businessLogic.JobService;
import controllers.businessLogic.JobWorkflow.JobPostWorkflowEngine;
import controllers.businessLogic.PartnerAuthService;
import controllers.businessLogic.Recruiter.RecruiterAuthService;
import controllers.businessLogic.Recruiter.RecruiterLeadService;
import controllers.businessLogic.Recruiter.RecruiterLeadStatusService;
import controllers.security.ForceHttps;
import controllers.security.RecruiterSecured;
import controllers.businessLogic.RecruiterService;
import controllers.security.FlashSessionController;
import controllers.security.RecruiterAdminSecured;
import dao.JobPostDAO;
import dao.JobPostWorkFlowDAO;
import dao.RecruiterDAO;
import dao.SmsReportDAO;
import models.entity.*;
import models.entity.OM.*;
import models.entity.Recruiter.OM.RecruiterToCandidateUnlocked;
import models.entity.Recruiter.RecruiterAuth;
import models.entity.Recruiter.RecruiterProfile;
import models.entity.Recruiter.Static.RecruiterCreditCategory;
import models.entity.Static.SmsDeliveryStatus;
import models.entity.Static.SmsType;
import models.util.SmsUtil;
import notificationService.NotificationEvent;
import notificationService.SMSEvent;
import play.Logger;
import play.api.Play;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.Security;
import play.mvc.With;

import java.io.File;
import java.io.IOException;
import java.util.*;

import static api.InteractionConstants.INTERACTION_CHANNEL_CANDIDATE_WEBSITE;
import static api.ServerConstants.*;
import static controllers.businessLogic.Recruiter.RecruiterInteractionService.createInteractionForRecruiterSearchCandidate;
import static play.libs.Json.toJson;
import static play.mvc.Controller.request;
import static play.mvc.Controller.session;
import static play.mvc.Results.*;

/**
 * Created by dodo on 4/10/16.
 */
@With(ForceHttps.class)
public class RecruiterController {
    private static boolean isDevMode = Play.isDev(Play.current()) || Play.isTest(Play.current());

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
        Logger.info("req JSON: " + req);
        //Logger.info("recruiterLeadRequest object: " + recruiterLeadRequest.toString(recruiterLeadRequest));
        //Logger.info("CompanyLeadRequest object: " + recruiterLeadRequest.getCompanyLeadRequest().toString(recruiterLeadRequest.getCompanyLeadRequest()));
        //Logger.info("RecruiterLeadToJobRoleRequest object: " + recruiterLeadRequest.getRecruiterLeadToJobRoleRequestList().get(0).toString(recruiterLeadRequest.getRecruiterLeadToJobRoleRequestList().get(0)));

        /*return ok(toJson(RecruiterLeadService.createLeadWithOtherDetails(recruiterLeadRequest,
                ServerConstants.LEAD_CHANNEL_RECRUITER)));*/
        RecruiterLeadService recruiterLeadService = new RecruiterLeadService();
        JsonNode res = toJson(recruiterLeadService.create(recruiterLeadRequest));
        Logger.info("res JSON: " + res);
        //Logger.info("res.get(\"entity\").get(\"recruiterLeadId\").asLong(): " + res.get("entity").get("recruiterLeadId").asLong());
        //return redirect("/showRecruiterLead/"+res.get("entity").get("recruiterLeadId").asLong());
        return ok(res);
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

        if(recruiterProfile.getRecruiterAccessLevel() >= ServerConstants.RECRUITER_ACCESS_LEVEL_PRIVATE) {
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
                return ok(toJson(RecruiterService.unlockCandidate(recruiterProfile, candidateId)));
            }
        }
        // no recruiter session found
        return ok("-1");
    }

    public static Result bulkUnlockCandidates() {
        if(session().get("recruiterId") != null){
            RecruiterProfile recruiterProfile = RecruiterProfile.find.where().eq("RecruiterProfileId", session().get("recruiterId")).findUnique();
            if(recruiterProfile != null){
                JsonNode req = request().body().asJson();
                Logger.info("Browser: " +  request().getHeader("User-Agent") + "; Req JSON : " + req );
                MultipleCandidateActionRequest multipleCandidateActionRequest = new MultipleCandidateActionRequest();
                ObjectMapper newMapper = new ObjectMapper();
                try {
                    multipleCandidateActionRequest = newMapper.readValue(req.toString(), MultipleCandidateActionRequest.class);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                Integer count = 0;
                Logger.info("Recruiter: " + recruiterProfile.getRecruiterProfileName() + " trying to unlock "
                        + multipleCandidateActionRequest.getCandidateIdList().size() + " candidate contacts");

                MultipleCandidateContactUnlockResponse response = new MultipleCandidateContactUnlockResponse();
                List<UnlockContactResponse> unlockContactResponses = new ArrayList<>();

                for(Long candidateId : multipleCandidateActionRequest.getCandidateIdList()){
                    UnlockContactResponse unlockContactResponse = RecruiterService.unlockCandidate(recruiterProfile, candidateId);
                    if(unlockContactResponse.getStatus() == UnlockContactResponse.STATUS_SUCCESS){
                        count++;
                    }
                    unlockContactResponses.add(unlockContactResponse);
                }

                Logger.info("Recruiter: " + recruiterProfile.getRecruiterProfileName() + " unlocked total " + count + " candidates contact");
                response.setUnlockContactResponseList(unlockContactResponses);
                response.setRecruiterContactCreditsLeft(recruiterProfile.getContactCreditCount());
                response.setRecruiterInterviewCreditsLeft(recruiterProfile.getInterviewCreditCount());

                return ok(toJson(response));
            }
        }
        // no recruiter session found
        return ok("-1");
    }

    public static Result bulkSendSms() {
        if(session().get("recruiterId") != null){
            RecruiterProfile recruiterProfile = RecruiterProfile.find.where().eq("RecruiterProfileId", session().get("recruiterId")).findUnique();
            if(recruiterProfile != null){
                JsonNode req = request().body().asJson();
                Logger.info("Browser: " +  request().getHeader("User-Agent") + "; Req JSON : " + req );
                MultipleCandidateActionRequest multipleCandidateActionRequest = new MultipleCandidateActionRequest();
                ObjectMapper newMapper = new ObjectMapper();
                try {
                    multipleCandidateActionRequest = newMapper.readValue(req.toString(), MultipleCandidateActionRequest.class);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                JobPost jobPost = null;
                Logger.info("Sending recruiter sms to " + multipleCandidateActionRequest.getCandidateIdList().size() + " candidates");
                if(multipleCandidateActionRequest.getJobPostId() != null) {
                    jobPost = JobPost.find.where().eq("JobPostId", multipleCandidateActionRequest.getJobPostId()).findUnique();
                }

                SmsType smsType = null;
                if(multipleCandidateActionRequest.getSmsType() != null){
                    smsType = SmsType.find.where().eq("sms_type_id", multipleCandidateActionRequest.getSmsType()).findUnique();
                    if(smsType == null){
                        Logger.info("Sms type static table is empty!");
                    }
                }
                // map of all candidate to be used in below loop
                Map<?, Candidate> existingCandidateMap = Candidate.find
                      .where()
                      .in("candidateId", multipleCandidateActionRequest.getCandidateIdList())
                      .setMapKey("candidateId")
                      .findMap();

                String commonSMSMessage = multipleCandidateActionRequest.getSmsMessage();
                for(Long candidateId : multipleCandidateActionRequest.getCandidateIdList()){
                    // remove this from loop and put it in map
                    Candidate candidate = existingCandidateMap.get(candidateId);
                    if(candidate != null){

                        //sending sms
                        //RMP product sms blast
                        if(multipleCandidateActionRequest.getJobPostId() != null){
                            if(jobPost != null){
                                if(multipleCandidateActionRequest.getSmsType() == 1){
                                    multipleCandidateActionRequest.setSmsMessage(
                                            RecruiterService.modifySMS(commonSMSMessage,
                                                    candidate,
                                                    jobPost)
                                    );
                                }
                                NotificationEvent notificationEvent =
                                        new SMSEvent(candidate.getCandidateMobile(),
                                                multipleCandidateActionRequest.getSmsMessage(),
                                                recruiterProfile.getCompany(),
                                                recruiterProfile,
                                                jobPost,
                                                candidate,
                                                smsType);

                                Global.getmNotificationHandler().addToQueue(notificationEvent);

                            } else{
                                return ok(toJson('0'));
                            }
                        } else{
                            // ordinary msg
                            SmsUtil.addSmsToNotificationQueue(candidate.getCandidateMobile(), multipleCandidateActionRequest.getSmsMessage());
                        }
                    }
                }

                if(multipleCandidateActionRequest.getJobPostId() != null){
                    //start checking sent sms delivery status
                    checkDeliveryStatus();
                }

                return ok(toJson('1'));
            }
        }
        // no recruiter session found
        return ok("-1");
    }

    public static Result getFetchedCandidateData() {
        if(session().get("recruiterId") != null){
            RecruiterProfile recruiterProfile = RecruiterProfile.find.where().eq("RecruiterProfileId", session().get("recruiterId")).findUnique();
            if(recruiterProfile != null && recruiterProfile.getRecruiterAccessLevel() >= RECRUITER_ACCESS_LEVEL_PRIVATE){
                JsonNode req = request().body().asJson();
                Logger.info("Browser: " +  request().getHeader("User-Agent") + "; Req JSON : " + req );
                MultipleCandidateActionRequest multipleCandidateActionRequest = new MultipleCandidateActionRequest();
                ObjectMapper newMapper = new ObjectMapper();
                try {
                    multipleCandidateActionRequest = newMapper.readValue(req.toString(), MultipleCandidateActionRequest.class);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                List<Candidate> candidateList = Candidate.find.where()
                        .in("CandidateId", multipleCandidateActionRequest.getCandidateIdList())
                        .findList();

                List<UnlockContactResponse> responseList = new ArrayList<>();
                for(Candidate candidate : candidateList){
                    UnlockContactResponse unlockContactResponse = new UnlockContactResponse();
                    unlockContactResponse.setCandidateMobile(candidate.getCandidateMobile());
                    unlockContactResponse.setCandidateId(candidate.getCandidateId());
                    unlockContactResponse.setResumeLink(candidate.getCandidateResumeLink());
                    responseList.add(unlockContactResponse);
                }
                MultipleCandidateContactUnlockResponse response = new MultipleCandidateContactUnlockResponse();
                response.setUnlockContactResponseList(responseList);

                return ok(toJson(response));
            }
        }
        // no recruiter session found
        return ok("-1");
    }

    public static void checkDeliveryStatus(){
        Logger.info("Will check sms status after 10 seconds");
        new Thread(() -> {
            try{
                Thread.sleep(10000); //check after 10 seconds
                Logger.info("Starting sms delivery status check");
                SmsDeliveryStatus status = SmsDeliveryStatus.find.where().eq("status_id", SMS_STATUS_PENDING).findUnique();
                Boolean reRun = false;
                if(status != null){
                    List<SmsReport> smsReportList = SmsReportDAO.getAllSMSByStatusSinceLastOneDay(status);
                    Logger.info("Checking " + smsReportList.size() + " sms's report");
                    for(SmsReport report : smsReportList){
                        String response = SmsUtil.checkDeliveryReport(report.getSmsSchedulerId());
                        if(response != null){
                            response = response.substring(13, response.length() -4);
                            Logger.info("Pinnacle Response :" + response);

                            Integer statusId;
                            if(Objects.equals(response, SMS_DELIVERY_RESPONSE.get(SMS_STATUS_DELIVERED))){
                                statusId = SMS_STATUS_DELIVERED;
                            } else if(Objects.equals(response, SMS_DELIVERY_RESPONSE.get(SMS_STATUS_UNDELIVERED))){
                                statusId = SMS_STATUS_UNDELIVERED;
                            } else if(Objects.equals(response, SMS_DELIVERY_RESPONSE.get(SMS_STATUS_EXPIRED))){
                                statusId = SMS_STATUS_EXPIRED;
                            } else if(Objects.equals(response, SMS_DELIVERY_RESPONSE.get(SMS_STATUS_DND))){
                                statusId = SMS_STATUS_DND;
                            } else if(Objects.equals(response, SMS_DELIVERY_RESPONSE.get(SMS_STATUS_PENDING))){
                                statusId = SMS_STATUS_PENDING;
                                reRun = true;
                            } else{
                                statusId = SMS_STATUS_FAILED;
                            }

                            report.setSmsDeliveryStatus(SmsDeliveryStatus.find.where().eq("status_id", statusId).setUseQueryCache(true).findUnique());
                            report.update();
                        }
                    }

                    if(reRun){
                        Logger.info("Re running the method since some");
                        checkDeliveryStatus();
                    }

                } else{
                    Logger.info("Sms delivery status static table empty");
                }
            } catch(InterruptedException e){
                Logger.info("exception: " + e);
            }


        }).start();
    }

    @Security.Authenticated(RecruiterSecured.class)
    public static Result getAllJobApplicants(long jobPostId) {
        JobPost jobPost = JobPostDAO.findById(jobPostId);
        Boolean toReturnJobPostObject = false;
        if(jobPost != null){
            if(session().get("recruiterId") != null){
                RecruiterProfile recruiterProfile = RecruiterProfile.find.where().eq("RecruiterProfileId", session().get("recruiterId")).findUnique();
                if(recruiterProfile != null){
                    if(jobPost.getRecruiterProfile() != null){
                        if(Objects.equals(jobPost.getRecruiterProfile().getRecruiterProfileId(), recruiterProfile.getRecruiterProfileId())){
                            toReturnJobPostObject = true;
                        } else{
                            if(Objects.equals(recruiterProfile.getCompany().getCompanyId(), jobPost.getCompany().getCompanyId())){
                                toReturnJobPostObject = true;
                            }
                        }
                    }

                    if(toReturnJobPostObject){
                        //initially we were returning the returned map directly. Since we need the list of candidate in ascending order of the interview date,
                        //  we are adding the values of the map in a list. This is being done because the order of map vales was getting sorted in ascending value
                        // with respect to the key valus. Hence using a list here

                        Map<Long, CandidateWorkflowData> selectedCandidateMap =
                                JobPostWorkflowEngine.getRecruiterJobLinedUpCandidates(jobPostId);

                        List<CandidateWorkflowData> jobApplicantList = new LinkedList<>();
                        for (Map.Entry<Long, CandidateWorkflowData> entry : selectedCandidateMap.entrySet()) {
                            sanitizeCandidateData(entry.getValue().getCandidate());
                            jobApplicantList.add(entry.getValue());
                        }

                        return ok(toJson(jobApplicantList));
                    }
                }
            }
        }
        return ok("0");
    }
    private static void sanitizeCandidateData(Candidate candidate) {
        candidate.setJobApplicationList(null);
        candidate.setLead(null);
        candidate.setCandidateUUId(null);
        candidate.setLocalityPreferenceList(null);
        candidate.setCandidateprofilestatus(null);
    }

    @Security.Authenticated(RecruiterSecured.class)
    public static Result getAllRecruiterJobPosts(Integer viewType) {
        if(session().get("recruiterId") != null){

            RecruiterProfile recruiterProfile = RecruiterProfile.find.where().eq("RecruiterProfileId", session().get("recruiterId")).findUnique();
            if(recruiterProfile != null){
                Map<Long, JobPost> recJobPostMap = new HashMap<>();
                Map<?, JobPost> recruiterJobPostMap;
                Map<Long, JobPost> otherJobPostMap = new HashMap<>();

                if(recruiterProfile.getRecruiterAccessLevel() >= ServerConstants.RECRUITER_ACCESS_LEVEL_PRIVATE){
                    recruiterJobPostMap = JobPost.find.where()
                            .eq("job_post_access_level", ServerConstants.JOB_POST_TYPE_PRIVATE)
                            .eq("CompanyId", recruiterProfile.getCompany().getCompanyId())
                            .setMapKey("jobPostId")
                            .findMap();

                    //get recruiter's owned jobs
                    if(viewType != null && Objects.equals(viewType, ServerConstants.VIEW_TYPE_MY_JOBS)){
                        // finding all the jobs created by this recruiter
                        recruiterJobPostMap = JobPost.find.where()
                                .eq("job_post_access_level", ServerConstants.JOB_POST_TYPE_PRIVATE)
                                .eq("JobRecruiterId", recruiterProfile.getRecruiterProfileId())
                                .setMapKey("jobPostId")
                                .findMap();

                        // finding all the jobs in which the recruiter \has been associated by other recruiters
                        List<Integer> jpwfStatusList = new ArrayList<>();
                        jpwfStatusList.add(ServerConstants.JWF_STATUS_INTERVIEW_SCHEDULED);
                        jpwfStatusList.add(ServerConstants.JWF_STATUS_INTERVIEW_RESCHEDULE);
                        jpwfStatusList.add(ServerConstants.JWF_STATUS_INTERVIEW_CONFIRMED);
                        jpwfStatusList.add(ServerConstants.JWF_STATUS_CANDIDATE_INTERVIEW_STATUS_NOT_GOING);
                        jpwfStatusList.add(ServerConstants.JWF_STATUS_CANDIDATE_INTERVIEW_STATUS_DELAYED);
                        jpwfStatusList.add(ServerConstants.JWF_STATUS_CANDIDATE_INTERVIEW_STATUS_ON_THE_WAY);
                        jpwfStatusList.add(ServerConstants.JWF_STATUS_CANDIDATE_INTERVIEW_STATUS_REACHED);
                        jpwfStatusList.add(ServerConstants.JWF_STATUS_CANDIDATE_FEEDBACK_STATUS_COMPLETE_SELECTED);
                        jpwfStatusList.add(ServerConstants.JWF_STATUS_CANDIDATE_FEEDBACK_STATUS_COMPLETE_REJECTED);
                        jpwfStatusList.add(ServerConstants.JWF_STATUS_CANDIDATE_FEEDBACK_STATUS_NO_SHOW);
                        jpwfStatusList.add(ServerConstants.JWF_STATUS_CANDIDATE_FEEDBACK_STATUS_NOT_QUALIFIED);

                        String statusList = "";
                        for(Integer status : jpwfStatusList){
                            statusList += "'" + status + "', ";
                        }

                        //Logger.info("Calling getAssociatedRecruiterJobsApplications for recruiter "+Math.toIntExact(recruiterProfile.getRecruiterProfileId()));
                        List<JobPostWorkflow> otherApplicationList =
                                JobPostWorkFlowDAO.getAssociatedRecruiterJobsApplications(statusList, Math.toIntExact(recruiterProfile.getRecruiterProfileId()));
                        //Logger.info("otherApplicationList size "+otherApplicationList.size());
                        //removing duplicate data from list
                        Set<JobPostWorkflow> jobSet = new HashSet<>();
                        jobSet.addAll(otherApplicationList);
                        otherApplicationList.clear();
                        otherApplicationList.addAll(jobSet);

                        //Logger.info("otherApplicationList size "+otherApplicationList.size());
                        for(JobPostWorkflow jobPostWorkflow : otherApplicationList){
                            otherJobPostMap.put(jobPostWorkflow.getJobPost().getJobPostId(), jobPostWorkflow.getJobPost());
                        }
                    }

                } else{
                    recruiterJobPostMap = JobPost.find.where()
                            .eq("JobRecruiterId", recruiterProfile.getRecruiterProfileId())
                            .eq("job_post_access_level", ServerConstants.JOB_POST_TYPE_OPEN)
                            .setMapKey("jobPostId")
                            .findMap();
                }

                String jpIdList = "";


                for(Map.Entry<?, JobPost> entity: recruiterJobPostMap.entrySet()) {
                    JobPost jobPost = entity.getValue();
                    recJobPostMap.put(jobPost.getJobPostId(), jobPost);

                    //checking recruiter and job post company
                    if(Objects.equals(jobPost.getCompany().getCompanyId(), recruiterProfile.getCompany().getCompanyId())){
                        jpIdList += "'" + jobPost.getJobPostId() + "', ";
                    }
                }

                //association other related jobs for this recruiter
                if(recruiterProfile.getRecruiterAccessLevel() >= ServerConstants.RECRUITER_ACCESS_LEVEL_PRIVATE){

                    //Logger.info("recruiterProfile.getRecruiterAccessLevel() "+recruiterProfile.getRecruiterAccessLevel());
                    //Logger.info("viewType "+viewType);

                    //get recruiter's owned jobs
                    if(viewType != null &&
                            Objects.equals(viewType, ServerConstants.VIEW_TYPE_MY_JOBS)
                            && recruiterProfile.getRecruiterAccessLevel() >= ServerConstants.RECRUITER_ACCESS_LEVEL_PRIVATE)
                    {
                        //Logger.info("otherJobPostMap size "+otherJobPostMap.size());
                        for(Map.Entry<?, JobPost> entity: otherJobPostMap.entrySet()) {
                            JobPost jobPost = entity.getValue();
                            recJobPostMap.put(jobPost.getJobPostId(), jobPost);

                            //checking recruiter and job post company
                            if(Objects.equals(jobPost.getCompany().getCompanyId(), recruiterProfile.getCompany().getCompanyId())){
                                jpIdList += "'" + jobPost.getJobPostId() + "', ";
                                //Logger.info("Adding associated job id to jpIdList "+jobPost.getJobPostId());
                            }
                        }
                    }

                }

                List<JobPostWorkflow> jobPostWorkflowList = new ArrayList<>();

                if(Objects.equals(jpIdList, "")){
                    return ok(toJson(jobPostWorkflowList));
                }

                // if candidate who have applied to the jobpost, only those jobpostworkflow obj will be returned
                jobPostWorkflowList = JobPostWorkFlowDAO.getJobApplications(jpIdList.substring(0, jpIdList.length()-2),
                        Math.toIntExact(recruiterProfile.getRecruiterProfileId()));

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

                        } else if(jpwf.getStatus().getStatusId() == ServerConstants.JWF_STATUS_PRESCREEN_COMPLETED
                                && recruiterProfile.getRecruiterAccessLevel() == ServerConstants.RECRUITER_ACCESS_LEVEL_PRIVATE){

                            singleObject.setPendingCount(singleObject.getPendingCount() + 1);
                        }
                        //rest all the applications are past interviews, hence we are not counting

                        singleObject.setTotalCount(singleObject.getTotalCount()+1);
                        singleObject.setJobPostWorkflowMap(response);
                    }
                }


                for(Map.Entry<?, JobPost> entity: recJobPostMap.entrySet()) {
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
        }
        return ok("0");
    }

    public static void sanitizeJobPostData(JobPost jobPost){
        /*jobPost.setJobPostDescription(null);*/
        jobPost.setJobPostAddress(null);
        jobPost.setPricingPlanType(null);
        jobPost.setJobRole(null);
        jobPost.setCompany(null);

        RecruiterProfile oldRec = jobPost.getRecruiterProfile();
        oldRec.setJobPost(null);
        oldRec.setRecruiterCreditHistoryList(null);
        oldRec.setRecruiterLead(null);
        oldRec.setRecruiterAuth(null);
        oldRec.setRecruiterToCandidateUnlockedList(null);

        jobPost.setRecruiterProfile(oldRec);
        jobPost.setJobPostLanguageRequirements(null);
        jobPost.setJobPostDocumentRequirements(null);
    }

    @Security.Authenticated(RecruiterSecured.class)
    public static Result getNextRoundComponents(Long jobPostId) {
        // get details from session
        //
        if(jobPostId == null) {
            return badRequest();
        }
        int minAccessLevel = 0;
        RecruiterProfile recruiterProfile = RecruiterProfile.find.where().eq("RecruiterProfileId", session().get("recruiterId")).findUnique();

        if(recruiterProfile.getRecruiterAccessLevel()>0){
            minAccessLevel = 1;
        }
        NextRoundComponents nextRoundComponents = new NextRoundComponents();
        List<NextRoundComponents.Recruiter> recruiterList = new ArrayList<>();
        for(RecruiterProfile profile: RecruiterDAO.findListByCompanyId(recruiterProfile.getCompany().getCompanyId(), minAccessLevel)) {
            NextRoundComponents.Recruiter recruiter = new NextRoundComponents.Recruiter();

            recruiter.setRecruiterProfileId(profile.getRecruiterProfileId());
            recruiter.setRecruiterProfileMobile(profile.getRecruiterProfileMobile());
            recruiter.setRecruiterProfileName(profile.getRecruiterProfileName());

            recruiterList.add(recruiter);
        }
        nextRoundComponents.setRecruiterList(recruiterList);
        JobPost jobPost = JobPostDAO.findById(jobPostId);
        InterviewResponse interviewResponse = RecruiterService.isInterviewRequired(jobPost);

        InterviewSlotPopulateResponse response =
                new InterviewSlotPopulateResponse(
                        JobService.getInterviewSlot(jobPost), interviewResponse, jobPost);

        // removing jobpost object from response
        response.setJobPost(null);
        nextRoundComponents.setInterviewSlotPopulateResponse(response);

        NextRoundComponents.Location location = new NextRoundComponents.Location();
        location.setJobPostAddress(jobPost.getJobPostAddress());
        location.setLatitude(jobPost.getLatitude());
        location.setLongitude(jobPost.getLongitude());
        location.setJobPostPinCode(jobPost.getJobPostPinCode());

        nextRoundComponents.setLocation(location);
        nextRoundComponents.setJobPostId(jobPostId);

        return ok(toJson(nextRoundComponents));
    }

    public static Result getPreviousRounds(Long jpId, Long cId) {
        return ok(toJson(JobPostWorkflowEngine.getPreviousRounds(jpId, cId)));
    }

    public static Result processJobCSV() throws IOException {
        Http.MultipartFormData body = request().body().asMultipartFormData();
        Http.MultipartFormData.FilePart excel = body.getFile("file");
        if (excel != null) {
            String fileName = excel.getFilename();
            Logger.info("fileName=" + fileName);
            File file = (File) excel.getFile();
            Logger.info("Uploading " + file);
            BulkUploadResponse bulkUploadResponse = RecruiterService.bulkUploadJob(file,fileName);
            return ok(toJson(bulkUploadResponse));
        } else{
            return ok("0");
        }
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
                boolean isPrivate = recruiterProfile.getRecruiterAccessLevel() >= ServerConstants.RECRUITER_ACCESS_LEVEL_PRIVATE;
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
                            matchingCandidateRequest.getJobPostId(),
                            matchingCandidateRequest.getShowOnlyFreshCandidate() ==  null? false: matchingCandidateRequest.getShowOnlyFreshCandidate(),
                            isPrivate,
                            recruiterProfile);

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
        Boolean toReturnJobPostObject = false;

        if(jobPost != null){
            if(session().get("recruiterId") != null){
                RecruiterProfile recruiterProfile = RecruiterProfile.find.where().eq("RecruiterProfileId", session().get("recruiterId")).findUnique();
                if(recruiterProfile != null){

                    if(jobPost.getRecruiterProfile() != null){
                        if(Objects.equals(jobPost.getRecruiterProfile().getRecruiterProfileId(), recruiterProfile.getRecruiterProfileId())){
                            toReturnJobPostObject = true;
                        } else{
                            if(Objects.equals(recruiterProfile.getCompany().getCompanyId(), jobPost.getCompany().getCompanyId())){
                                toReturnJobPostObject = true;
                            }
                        }
                    }
                }
            }
        }

        if(toReturnJobPostObject){
            return ok(toJson(jobPost));
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

        if(recruiterProfile.getRecruiterAccessLevel() >= ServerConstants.RECRUITER_ACCESS_LEVEL_PRIVATE) {
            return ok(views.html.Recruiter.rmp.recruiter_my_jobs.render());
        }

        return ok(views.html.Recruiter.recruiter_my_jobs.render());
    }

    @Security.Authenticated(RecruiterSecured.class)
    public static Result renderJobPostTrack(Long id, String tabView) {
        Long recruiterId = Long.valueOf(session().get("recruiterId"));
        RecruiterProfile recruiterProfile = RecruiterDAO.findById(recruiterId);
        if(recruiterProfile == null) {
            return badRequest();
        }

        if(recruiterProfile.getRecruiterAccessLevel() >= ServerConstants.RECRUITER_ACCESS_LEVEL_PRIVATE) {
            return ok(views.html.Recruiter.rmp.job_post_sms_track.render());
        }

        return ok(views.html.Recruiter.recruiter_home.render());
    }

    public static Result recruiterNavbar() {
        if(session().get("recruiterId") != null) {
            RecruiterProfile recruiterProfile = RecruiterProfile.find.where().eq("recruiterProfileId", session().get("recruiterId")).findUnique();
            if (recruiterProfile != null && recruiterProfile.getRecruiterAccessLevel() == ServerConstants.RECRUITER_ACCESS_LEVEL_PRIVATE) {
                return ok(views.html.Recruiter.rmp.private_recruiter_nav.render());
            } else if (recruiterProfile != null && recruiterProfile.getRecruiterAccessLevel() == ServerConstants.RECRUITER_ACCESS_LEVEL_PRIVATE_ADMIN) {
                return ok(views.html.Recruiter.rmp.private_recruiter_admin_nav.render());
            }
        }

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

    //@Security.Authenticated(SecuredUser.class)
    public static Result showRecruiterLead(Long id) {
        return ok(views.html.Recruiter.recruiter_lead_details.render());
    }

    public static Result readRecruiterLead(Long id) {
        RecruiterLeadService recruiterLeadService = new RecruiterLeadService();
        List<Long> ids = new ArrayList<Long>();
        ids.add(id);
        JsonNode res = toJson(recruiterLeadService.readById(ids));
        Logger.info("res JSON: " + res);
        return ok(res);
    }

    public static Result showRecruiterLeadStatus() {
        RecruiterLeadStatusService recruiterLeadStatusService = new RecruiterLeadStatusService();
        JsonNode res = toJson(recruiterLeadStatusService.readAll());
        Logger.info("res JSON: " + res);
        return ok(res);
    }

    public static Result updateWebsiteLead() {
        JsonNode req = request().body().asJson();
        RecruiterLeadRequest recruiterLeadRequest = new RecruiterLeadRequest();
        ObjectMapper newMapper = new ObjectMapper();
        try {
            recruiterLeadRequest = newMapper.readValue(req.toString(), RecruiterLeadRequest.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Logger.info("req JSON: " + req);
        //Logger.info("recruiterLeadRequest object: " + recruiterLeadRequest.toString(recruiterLeadRequest));
        //Logger.info("CompanyLeadRequest object: " + recruiterLeadRequest.getCompanyLeadRequest().toString(recruiterLeadRequest.getCompanyLeadRequest()));
        //Logger.info("RecruiterLeadToJobRoleRequest object: " + recruiterLeadRequest.getRecruiterLeadToJobRoleRequestList().get(0).toString(recruiterLeadRequest.getRecruiterLeadToJobRoleRequestList().get(0)));

        RecruiterLeadService recruiterLeadService = new RecruiterLeadService();
        JsonNode res = toJson(recruiterLeadService.update(recruiterLeadRequest));
        Logger.info("res JSON: " + res);
        return ok(res);
    }

    public static Result deleteWebsiteLead() {
        JsonNode req = request().body().asJson();
        RecruiterLeadRequest recruiterLeadRequest = new RecruiterLeadRequest();
        ObjectMapper newMapper = new ObjectMapper();
        try {
            recruiterLeadRequest = newMapper.readValue(req.toString(), RecruiterLeadRequest.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Logger.info("req JSON: " + req);
        //Logger.info("recruiterLeadRequest object: " + recruiterLeadRequest.toString(recruiterLeadRequest));
        //Logger.info("CompanyLeadRequest object: " + recruiterLeadRequest.getCompanyLeadRequest().toString(recruiterLeadRequest.getCompanyLeadRequest()));
        //Logger.info("RecruiterLeadToJobRoleRequest object: " + recruiterLeadRequest.getRecruiterLeadToJobRoleRequestList().get(0).toString(recruiterLeadRequest.getRecruiterLeadToJobRoleRequestList().get(0)));

        RecruiterLeadService recruiterLeadService = new RecruiterLeadService();
        JsonNode res = toJson(recruiterLeadService.delete(recruiterLeadRequest));
        Logger.info("res JSON: " + res);
        return ok(res);

    }

    public static Result trackApplication(long id) {
        return ok(views.html.Recruiter.recruiter_interviews.render());
    }

    public static Boolean checkCompanyJob(JobPost jobPost){
        RecruiterProfile recruiterProfile = RecruiterProfile.find.where().eq("RecruiterProfileId", session().get("recruiterId")).findUnique();
        if(recruiterProfile != null){
            if(Objects.equals(recruiterProfile.getCompany().getCompanyId(), jobPost.getCompany().getCompanyId())){
                return true;
            }
        }
        return false;
    }

    @Security.Authenticated(RecruiterSecured.class)
    public static Result getSentSms(Long jpId) {
        JobPost jobPost = JobPostDAO.findById(jpId);
        if(jobPost != null){
            if(checkCompanyJob(jobPost)){
                SmsReportResponse smsReportResponse = new SmsReportResponse();

                // only get sms of type one and two
                List<SmsReport> smsReportList = SmsReport.find
                        .where()
                        .eq("JobPostId", jpId)
                        .le("SmsType", ServerConstants.SMS_TYPE_APPLY_INTERVIEW_SMS)
                        .orderBy().desc("sms_report_id")
                        .findList();

                for(SmsReport reports : smsReportList){
                    reports.setCompany(null);
                    reports.setJobPost(null);
                    reports.setRecruiterProfile(null);

                    Candidate candidate = reports.getCandidate();
                    candidate.setLocalityPreferenceList(null);
                    candidate.setJobApplicationList(null);
                    candidate.setJobHistoryList(null);
                    candidate.setJobPostWorkflowList(null);
                    candidate.setJobPreferencesList(null);
                    candidate.setLanguageKnownList(null);
                    candidate.setCandidateAssetList(null);
                    candidate.setCandidateEducation(null);
                    candidate.setCandidateExpList(null);
                    reports.setCandidate(candidate);
                }

                smsReportResponse.setSmsReportList(smsReportList);
                return ok(toJson(smsReportResponse));
            }
        }
        return ok("0");
    }

    @Security.Authenticated(RecruiterSecured.class)
    public static Result getAppliedCandidates(Long jpId) {
        JobPost jobPost = JobPostDAO.findById(jpId);
        if(jobPost != null){
            if(checkCompanyJob(jobPost)){

                List<JobPostWorkflow> applicationList = JobPostWorkFlowDAO.getAllJobApplicationWithinStatusId(jpId,
                        ServerConstants.JWF_STATUS_SELECTED, ServerConstants.JWF_STATUS_INTERVIEW_RESCHEDULE,
                        Long.valueOf(session().get("recruiterId")));

                for(JobPostWorkflow workflow : applicationList){
                    sanitizeCandidateData(workflow.getCandidate());
                }

                List<Candidate> candidateList = new ArrayList<>();
                List<Long> candidateIdList = new ArrayList<>();
                for (JobPostWorkflow jpwf : applicationList) {
                    candidateList.add(jpwf.getCandidate());
                    candidateIdList.add(jpwf.getCandidate().getCandidateId());
                }

                List<Integer> statusList = new ArrayList<>();
                statusList.add(ServerConstants.JWF_STATUS_SELECTED);
                statusList.add(ServerConstants.JWF_STATUS_PRESCREEN_ATTEMPTED);
                statusList.add(ServerConstants.JWF_STATUS_PRESCREEN_FAILED);
                statusList.add(ServerConstants.JWF_STATUS_PRESCREEN_COMPLETED);
                statusList.add(ServerConstants.JWF_STATUS_INTERVIEW_SCHEDULED);
                statusList.add(ServerConstants.JWF_STATUS_INTERVIEW_REJECTED_BY_RECRUITER_SUPPORT);
                statusList.add(ServerConstants.JWF_STATUS_INTERVIEW_REJECTED_BY_CANDIDATE);
                statusList.add(ServerConstants.JWF_STATUS_INTERVIEW_RESCHEDULE);

                Map<Long, CandidateWorkflowData> mapToBeReturned =
                        JobPostWorkflowEngine.getCandidateMap(candidateList, jpId, statusList, false);

                List<CandidateWorkflowData> jobApplicantList = new LinkedList<>();
                for (Map.Entry<Long, CandidateWorkflowData> entry : mapToBeReturned.entrySet()) {
                    sanitizeCandidateData(entry.getValue().getCandidate());
                    jobApplicantList.add(entry.getValue());
                }

                Map<Long, JobApplication> jobApplicationMap = candidateToJobApplicationMapper(jpId, candidateIdList);

                for (CandidateWorkflowData data: jobApplicantList) {
                    data.setApplicationChannel(ServerConstants.APPLICATION_CHANNEL_SUPPORT);
                    if(jobApplicationMap.get(data.getCandidate().getCandidateId()) != null){
                        JobApplication application = jobApplicationMap.get(data.getCandidate().getCandidateId());
                        data.setAppliedOn(application.getJobApplicationCreateTimeStamp());

                        if(application.getPartner() != null){
                            // TODO check partner access level first, ==1 then normal , if ==2 then employee
                            // set server constant as channel_EMPLOYEE
                            // FE recruiter_job_post_track.js
                            data.setApplicationChannel(ServerConstants.APPLICATION_CHANNEL_PARTNER);
                            data.setPartner(application.getPartner());
                        } else{
                            data.setApplicationChannel(ServerConstants.APPLICATION_CHANNEL_SELF);
                        }
                    }
                }

                ApplicationResponse applicationResponse = new ApplicationResponse();
                applicationResponse.setApplicationList(jobApplicantList);

                return ok(toJson(applicationResponse));
            }
        }
        return ok("0");
    }

    @Security.Authenticated(RecruiterSecured.class)
    public static Result getConfirmedApplication(Long jpId) {
        JobPost jobPost = JobPostDAO.findById(jpId);
        if(jobPost != null){
            if(checkCompanyJob(jobPost)){
                List<JobPostWorkflow> applicationList = JobPostWorkFlowDAO.getAllConfirmedApplicationsJobPost(jpId,
                        ServerConstants.JWF_STATUS_INTERVIEW_CONFIRMED, ServerConstants.JWF_STATUS_CANDIDATE_FEEDBACK_STATUS_NOT_QUALIFIED,
                        Long.valueOf(session().get("recruiterId")));

                for(JobPostWorkflow workflow : applicationList){
                    sanitizeCandidateData(workflow.getCandidate());
                }

                List<Candidate> candidateList = new ArrayList<>();
                List<Long> candidateIdList = new ArrayList<>();
                for (JobPostWorkflow jpwf : applicationList) {
                    candidateList.add(jpwf.getCandidate());
                    candidateIdList.add(jpwf.getCandidate().getCandidateId());
                }

                Integer status = ServerConstants.JWF_STATUS_INTERVIEW_CONFIRMED;

                Map<Long, CandidateWorkflowData> mapToBeReturned = JobPostWorkflowEngine.getCandidateMap(candidateList, jpId, new ArrayList<>(Collections.singletonList(status)), false);

                List<CandidateWorkflowData> jobApplicantList = new LinkedList<>();
                for (Map.Entry<Long, CandidateWorkflowData> entry : mapToBeReturned.entrySet()) {
                    sanitizeCandidateData(entry.getValue().getCandidate());
                    jobApplicantList.add(entry.getValue());
                }

                Map<Long, JobApplication> jobApplicationMap = candidateToJobApplicationMapper(jpId, candidateIdList);

                for (CandidateWorkflowData data: jobApplicantList) {
                    data.setApplicationChannel(ServerConstants.APPLICATION_CHANNEL_SUPPORT);
                    if(jobApplicationMap.get(data.getCandidate().getCandidateId()) != null){
                        JobApplication application = jobApplicationMap.get(data.getCandidate().getCandidateId());
                        data.setAppliedOn(application.getJobApplicationCreateTimeStamp());

                        if(application.getPartner() != null){
                            data.setApplicationChannel(ServerConstants.APPLICATION_CHANNEL_PARTNER);
                            data.setPartner(application.getPartner());
                        } else{
                            data.setApplicationChannel(ServerConstants.APPLICATION_CHANNEL_SELF);
                        }
                    }
                }

                ApplicationResponse applicationResponse = new ApplicationResponse();
                applicationResponse.setApplicationList(jobApplicantList);

                return ok(toJson(applicationResponse));
            }
        }
        return ok("0");
    }

    public static Map<Long, JobApplication> candidateToJobApplicationMapper(Long jpId, List<Long> candidateIdList){
        List<JobApplication> jobApplicationList = JobApplication.find
                .where()
                .eq("JobPostId", jpId)
                .in("CandidateId", candidateIdList)
                .findList();

        Map<Long, JobApplication> jobApplicationMap = new HashMap<>();

        for(JobApplication jobApplication : jobApplicationList) {
            if(jobApplicationMap.get(jobApplication.getCandidate().getCandidateId()) == null) {
                jobApplicationMap.put(jobApplication.getCandidate().getCandidateId(), jobApplication);
            } else {
                Logger.info("found multiple job application against one jobpost and one candidate");
            }
        }

        return jobApplicationMap;
    }

    @Security.Authenticated(RecruiterAdminSecured.class)
    public static Result recruiterSummary(Long recruiterId, String from , String to) {

        RecruiterService recruiterService = new RecruiterService();
        if(recruiterId == null) {
            // return summary for all recruiter
            return ok(toJson(recruiterService.getRecruiterSummary(null, Long.valueOf(session().get("recruiterId")), from, to)));
        }
        return ok();
    }

    @Security.Authenticated(RecruiterAdminSecured.class)
    public static Result jobPostSummary(Long recruiterId, Long jpId) {
        if(recruiterId == null) {
            return badRequest();
        }

        RecruiterService recruiterService = new RecruiterService();
        if(jpId == null) {
            // return summary for all jobPost per recruiter
            return ok(toJson(recruiterService.getAllJobPostPerRecruiterSummary(recruiterId, Long.valueOf(session().get("recruiterId")))));
        }
        return ok();
    }


    @Security.Authenticated(RecruiterAdminSecured.class)
    public static Result renderReportPage(String summary, Long recruiterId) {
        if(summary!= null && summary.equalsIgnoreCase("job_post")) {
            return ok(views.html.Recruiter.rmp.private_recruiter_admin_job_post_report_view.render());
        }
        return ok(views.html.Recruiter.rmp.private_recruiter_admin_report_view.render());
    }

    @Security.Authenticated(RecruiterSecured.class)
    public static Result recruiterGetCandidateInfo(long candidateId) {
        Logger.info("Candidate Id : "+ candidateId);
        Candidate candidate = Candidate.find.where().eq("CandidateId", candidateId).findUnique();
        if(candidate != null) {
                return ok(toJson(candidate));
        }
        return ok("0");
    }
    @Security.Authenticated(RecruiterSecured.class)
    public static Result recruiterCandidateModal() {
        return ok(views.html.Recruiter.recruiter_candidate_modal.render());
    }

    @Security.Authenticated(RecruiterSecured.class)
    public static Result checkPrivateRecruiterPartnerAccount() {
        if(session().get("recruiterId") != null){
            RecruiterProfile recruiterProfile = RecruiterProfile.find.where().eq("RecruiterProfileId", session().get("recruiterId")).findUnique();
            if(recruiterProfile != null && recruiterProfile.getRecruiterAccessLevel() >= ServerConstants.RECRUITER_ACCESS_LEVEL_PRIVATE){
                Partner partner = Partner.find.where()
                        .eq("partner_mobile", recruiterProfile.getRecruiterProfileMobile())
                        .eq("partner_type", ServerConstants.PARTNER_TYPE_PRIVATE)
                        .findUnique();

                if(partner != null){
                    PartnerAuth existingAuth = PartnerAuth.find.where().eq("partner_id", partner.getPartnerId()).findUnique();

                    if(existingAuth != null){
                        return ok("1");
                    }
                }

            }
        }
        return ok("0");
    }

    @Security.Authenticated(RecruiterSecured.class)
    public static Result switchToPartner() {
        if(session().get("recruiterId") != null){
            RecruiterProfile recruiterProfile = RecruiterProfile.find.where().eq("RecruiterProfileId", session().get("recruiterId")).findUnique();
            if(recruiterProfile != null && recruiterProfile.getRecruiterAccessLevel() >= ServerConstants.RECRUITER_ACCESS_LEVEL_PRIVATE){
                Partner existingPartner = Partner.find.where()
                        .eq("partner_mobile", recruiterProfile.getRecruiterProfileMobile())
                        .eq("partner_type", ServerConstants.PARTNER_TYPE_PRIVATE)
                        .findUnique();

                if(existingPartner != null){
                    PartnerAuth existingAuth = PartnerAuth.find.where().eq("partner_id", existingPartner.getPartnerId()).findUnique();

                    if(existingAuth != null){
                        //clearing session for recruiter
                        FlashSessionController.clearSessionExceptFlash();

                        PartnerAuthService.addSession(existingAuth, existingPartner);
                        return ok("1");
                    }
                }
            }
        }
        return ok("0");
    }

    @Security.Authenticated(RecruiterSecured.class)
    public static Result uploadEmployee() throws Exception {
        java.io.File file = (java.io.File) request().body().asMultipartFormData().getFile("file").getFile();

        EmployeeService employeeService = new EmployeeService(InteractionConstants.INTERACTION_CHANNEL_RECRUITER_WEBSITE);

        return ok(toJson(employeeService.parseEmployeeCsv(file, RecruiterDAO.findById(Long.valueOf(session().get("recruiterId"))))));
    }

    @Security.Authenticated(RecruiterSecured.class)
    public static Result getAllEmployee() {

        RecruiterProfile recruiterProfile = RecruiterDAO.findById(Long.valueOf(session().get("recruiterId")));

            // allow employee lookup only to private recruiters
        if(recruiterProfile.getRecruiterAccessLevel() < ServerConstants.RECRUITER_ACCESS_LEVEL_PRIVATE){
            return badRequest();
        }


        List<PartnerToCompany> pToCList =
                               PartnerToCompany.find.where()
                                               .eq("CompanyId", recruiterProfile.getCompany().getCompanyId())
                                               .eq("partner.partnerType.partnerTypeId", ServerConstants.PARTNER_TYPE_PRIVATE_EMPLOYEE)
                                               .findList();

        List<EmployeeResponse> employeeList = new ArrayList<>();

        for(PartnerToCompany partnerToCompany: pToCList) {
            EmployeeResponse response = new EmployeeResponse();

            response.setPartnerId(partnerToCompany.getPartner().getPartnerId());
            response.setEmailId(partnerToCompany.getPartner().getPartnerEmail());
            response.setFirstName(partnerToCompany.getPartner().getPartnerFirstName());
            response.setLastName(partnerToCompany.getPartner().getPartnerLastName());
            response.setMobile(partnerToCompany.getPartner().getPartnerMobile());
            response.setEmployeeId(partnerToCompany.getForeignEmployeeId());
            response.setLocality(partnerToCompany.getPartner().getLocality().getLocalityName());

            employeeList.add(response);
        }

        return ok(toJson(employeeList));
    }

    /** jpId in url is used in front-end for sms-module */
    @Security.Authenticated(RecruiterSecured.class)
    public static Result employeeView(String jpId) {
        if(jpId == null || jpId.isEmpty()){
            return badRequest();
        }

        return ok(views.html.Recruiter.rmp.private_recruiter_employee_view.render());
    }

    @Security.Authenticated(RecruiterSecured.class)
    public static Result bulkSendSmsEmployee() {
        JsonNode req = request().body().asJson();
        EmployeeBulkSmsRequest employeeBulkSmsRequest = new EmployeeBulkSmsRequest();
        ObjectMapper newMapper = new ObjectMapper();
        try {
            employeeBulkSmsRequest = newMapper.readValue(req.toString(), EmployeeBulkSmsRequest.class);

            RecruiterProfile recruiterProfile = RecruiterDAO.findById(Long.valueOf(session().get("recruiterId")));

            if(recruiterProfile == null) {
                return badRequest(" No Recruiter found with recId: " + session().get("recruiterId"));
            }

            return ok(toJson(RecruiterService.sendBulkSmsEmployee(employeeBulkSmsRequest, recruiterProfile)));
        } catch (IOException e) {
            e.printStackTrace();
            return badRequest();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return badRequest();
    }
}