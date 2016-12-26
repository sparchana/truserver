package controllers;

import api.InteractionConstants;
import api.ServerConstants;
import api.http.FormValidator;
import api.http.httpRequest.*;
import api.http.httpResponse.CandidateSignUpResponse;
import api.http.httpResponse.PartnerSignUpResponse;
import api.http.httpResponse.SupportDashboardElementResponse;
import com.avaje.ebean.Ebean;
import com.avaje.ebean.RawSql;
import com.avaje.ebean.RawSqlBuilder;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.org.apache.xpath.internal.operations.Bool;
import controllers.businessLogic.*;
import controllers.businessLogic.JobWorkflow.JobPostWorkflowEngine;
import controllers.security.SecuredUser;
import dao.JobPostDAO;
import dao.JobPostWorkFlowDAO;
import models.entity.*;
import models.entity.OM.JobApplication;
import models.entity.OM.JobPostWorkflow;
import models.entity.OM.PartnerToCandidate;
import models.entity.OM.PreScreenRequirement;
import models.entity.Static.LeadSource;
import models.entity.Static.PartnerType;
import models.util.SmsUtil;
import play.Logger;
import play.mvc.Result;
import play.mvc.Security;

import java.io.IOException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static models.util.Util.generateOtp;
import static play.libs.Json.toJson;

import static play.mvc.Controller.request;
import static play.mvc.Controller.session;
import static play.mvc.Results.badRequest;
import static play.mvc.Results.ok;
import static play.mvc.Results.redirect;

/**
 * Created by adarsh on 9/9/16.
 */
public class PartnerController {
    public static Result partnerIndex() {
        String sessionId = session().get("partnerId");
        if(sessionId != null){
            return redirect("/partner/home");
        }
        return ok(views.html.Partner.partner_index.render());
    }

    public static Result renderPagePartnerNavBar() {
        return ok(views.html.Partner.partner_nav_bar.render());
    }

    public static Result partnerSignUp() {
        JsonNode req = request().body().asJson();
        PartnerSignUpRequest partnerSignUpRequest = new PartnerSignUpRequest();
        ObjectMapper newMapper = new ObjectMapper();
        try {
            partnerSignUpRequest = newMapper.readValue(req.toString(), PartnerSignUpRequest.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Logger.info("JSON req: " + req);

        int channelType = InteractionConstants.INTERACTION_CHANNEL_PARTNER_WEBSITE;
        return ok(toJson(PartnerService.signUpPartner(partnerSignUpRequest, channelType, ServerConstants.LEAD_SOURCE_UNKNOWN)));

    }

    public static Result addPassword() {
        JsonNode req = request().body().asJson();
        PartnerSignUpRequest partnerSignUpRequest = new PartnerSignUpRequest();
        ObjectMapper newMapper = new ObjectMapper();
        try {
            partnerSignUpRequest = newMapper.readValue(req.toString(), PartnerSignUpRequest.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Logger.info("JSON req: " + req);

        String partnerMobile = partnerSignUpRequest.getpartnerAuthMobile();
        String partnerPassword = partnerSignUpRequest.getpartnerPassword();

        return ok(toJson(PartnerAuthService.savePassword(partnerMobile, partnerPassword, InteractionConstants.INTERACTION_CHANNEL_CANDIDATE_WEBSITE)));
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
        return ok(toJson(PartnerService.login(loginMobile, loginPassword, InteractionConstants.INTERACTION_CHANNEL_CANDIDATE_WEBSITE)));
    }

    @Security.Authenticated(SecuredUser.class)
    public static Result partnerHome() {
        return ok(views.html.Partner.partner_home.render());
    }

    public static Result renderPagePartnerLoggedInNavbar() {
        return ok(views.html.Partner.partner_logged_in_nav_bar.render());
    }

    public static Result findPartnerAndSendOtp() {
        JsonNode req = request().body().asJson();
        ResetPasswordResquest resetPasswordResquest = new ResetPasswordResquest();
        ObjectMapper newMapper = new ObjectMapper();
        try {
            resetPasswordResquest = newMapper.readValue(req.toString(), ResetPasswordResquest.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        String partnerMobile = resetPasswordResquest.getResetPasswordMobile();
        Logger.info("==> " + partnerMobile);

        return ok(toJson(PartnerService.findPartnerAndSendOtp(partnerMobile, InteractionConstants.INTERACTION_CHANNEL_CANDIDATE_WEBSITE)));
    }

    public static Result getAllPartnerType() {
        List<PartnerType> partnerTypeList = PartnerType.find.all();
        return ok(toJson(partnerTypeList));
    }

    @Security.Authenticated(SecuredUser.class)
    public static Result getPartnerProfileInfo() {
        Partner partner = Partner.find.where().eq("partner_id", session().get("partnerId")).findUnique();
        if(partner != null) {
            return ok(toJson(partner));
        }
        return ok("0");
    }

    public static Result checkPartnerSession() {
        String sessionPartnerId = session().get("partnerId");
        if(sessionPartnerId != null){
            return ok("1");
        } else{
            return ok("0");
        }
    }

    @Security.Authenticated(SecuredUser.class)
    public static Result partnerEditProfile() {
        return ok(views.html.Partner.partner_edit_profile.render());
    }

    @Security.Authenticated(SecuredUser.class)
    public static Result partnerCreateCandidate(long candidateId) {
        return ok(views.html.Partner.partner_create_candidate.render(candidateId));
    }

    public static Result partnerUpdateBasicProfile() {
        JsonNode req = request().body().asJson();
        PartnerProfileRequest partnerProfileRequest = new PartnerProfileRequest();
        ObjectMapper newMapper = new ObjectMapper();
        try {
            partnerProfileRequest = newMapper.readValue(req.toString(), PartnerProfileRequest.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Logger.info("Req JSON : " + req);
        String partnerId = session().get("partnerId");
        Partner partner = Partner.find.where().eq("partner_id", partnerId).findUnique();
        if(partner != null){
            partnerProfileRequest.setPartnerMobile(partner.getPartnerMobile());
            return ok(toJson(PartnerService.createPartnerProfile(partnerProfileRequest, InteractionConstants.INTERACTION_CHANNEL_CANDIDATE_WEBSITE, ServerConstants.UPDATE_BASIC_PROFILE)));
        } else{
            return ok("0");
        }
    }

    public static Result partnerCreateCandidateSubmit() {
        JsonNode req = request().body().asJson();
        AddSupportCandidateRequest addSupportCandidateRequest = new AddSupportCandidateRequest();
        ObjectMapper newMapper = new ObjectMapper();
        try {
            addSupportCandidateRequest = newMapper.readValue(req.toString(), AddSupportCandidateRequest.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Logger.info("Req JSON : " + req);
        Boolean isNewCandidate = false;
        Candidate candidate = CandidateService.isCandidateExists(FormValidator.convertToIndianMobileFormat(addSupportCandidateRequest.getCandidateMobile()));
        if(candidate == null){
            isNewCandidate = true; //checking if the candidate exists
        }
        String partnerId = session().get("partnerId");
        Partner partner = Partner.find.where().eq("partner_id", partnerId).findUnique();
        if(partner != null){
            LeadSource leadSource = LeadSource.find.where().eq("leadSourceId", addSupportCandidateRequest.getLeadSource()).findUnique();
            if(leadSource != null){
                addSupportCandidateRequest.setLeadSource(leadSource.getLeadSourceId());
            }
            CandidateSignUpResponse candidateSignUpResponse = CandidateService.createCandidateProfile(addSupportCandidateRequest,
                    InteractionConstants.INTERACTION_CHANNEL_PARTNER_WEBSITE,
                    ServerConstants.UPDATE_ALL_BY_SUPPORT);
            if(candidateSignUpResponse.getStatus() == CandidateSignUpResponse.STATUS_SUCCESS){
                if(isNewCandidate){ //save a record in partnerToCandidate
                    candidateSignUpResponse =
                            PartnerService.createPartnerToCandidateMapping(partner, FormValidator.convertToIndianMobileFormat(addSupportCandidateRequest.getCandidateMobile()));
                    Candidate existingCandidate = CandidateService.isCandidateExists(FormValidator.convertToIndianMobileFormat(addSupportCandidateRequest.getCandidateMobile()));
                    candidateSignUpResponse.setOtp(PartnerService.sendCandidateVerificationSms(existingCandidate));
                } else{
                    candidateSignUpResponse.setOtp(0);
                }
            }
            return ok(toJson(candidateSignUpResponse));
        } else{
            return ok("-1");
        }
    }

    @Security.Authenticated(SecuredUser.class)
    public static Result partnerCandidates() {
        return ok(views.html.Partner.partner_candidates.render());
    }

    @Security.Authenticated(SecuredUser.class)
    public static Result getMyCandidates(){
        Partner partner = Partner.find.where().eq("partner_id", session().get("partnerId")).findUnique();
        if(partner != null){
            List<PartnerToCandidate> partnerToCandidateList = PartnerToCandidate.find.where()
                    .eq("partner_id", partner.getPartnerId())
                    .orderBy("partner_to_candidate_create_timestamp desc")
                    .findList();
            ArrayList<PartnerCandidatesResponse> responses = new ArrayList<>();

            SimpleDateFormat sfd = new SimpleDateFormat(ServerConstants.SDF_FORMAT_YYYYMMDD);

            for(PartnerToCandidate partnerToCandidate : partnerToCandidateList) {
                PartnerCandidatesResponse response = new PartnerCandidatesResponse();

                response.setCandidateId(partnerToCandidate.getCandidate().getCandidateId());
                response.setCreationTimestamp(sfd.format(partnerToCandidate.getCandidate().getCandidateCreateTimestamp()));
                response.setLeadId(partnerToCandidate.getCandidate().getLead().getLeadId());
                if(partnerToCandidate.getCandidate().getCandidateFirstName() != null){
                    response.setCandidateName(partnerToCandidate.getCandidate().getCandidateFirstName());
                    if(partnerToCandidate.getCandidate().getCandidateLastName() != null){
                        response.setCandidateName(partnerToCandidate.getCandidate().getCandidateFirstName() + " " + partnerToCandidate.getCandidate().getCandidateLastName());
                    }
                }
                Auth auth = Auth.find.where().eq("candidateId", partnerToCandidate.getCandidate().getCandidateId()).findUnique();
                if(auth != null){
                    response.setCandidateStatus(auth.getAuthStatus());
                    if(auth.getAuthStatus() == ServerConstants.CANDIDATE_STATUS_VERIFIED){
                        response.setCandidateActiveDeactive(partnerToCandidate.getCandidate().getCandidateprofilestatus().getProfileStatusId());
                    }
                }
                response.setCandidateAppliedJobs(JobPostWorkFlowDAO.candidateAppliedJobs(partnerToCandidate.getCandidate().getCandidateId()).size());
                response.setCandidateMobile(partnerToCandidate.getCandidate().getCandidateMobile());
                responses.add(response);
            }
            return ok(toJson(responses));
        } else{
            //partner does not exists
            Logger.info("Partner not available");

        }
        return ok("0");
    }

    @Security.Authenticated(SecuredUser.class)
    public static Result getPartnerCandidate(long leadId) {
        Partner partner = Partner.find.where().eq("partner_id", session().get("partnerId")).findUnique();
        if(partner != null){ //checking if partner is logged in or not
            Lead lead = Lead.find.where().eq("leadId", leadId).findUnique(); //getting candidate profile from db
            if(lead != null) {
                Candidate candidate = CandidateService.isCandidateExists(lead.getLeadMobile());
                if(candidate != null){ //checking if the candidate was created by the requested partner
                    PartnerToCandidate partnerToCandidate = PartnerToCandidate.find
                            .where()
                            .eq("candidate_candidateid", candidate.getCandidateId())
                            .findUnique();
                    if(partnerToCandidate != null){
                        if(partnerToCandidate.getPartner().getPartnerId() == partner.getPartnerId()){
                            return ok(toJson(candidate));
                        } else{
                            return ok("-1");
                        }
                    }
                }
            }
        }
        return ok("0");
    }

    public static Result logoutPartner() {
        session().clear();
        Logger.info("Partner Logged Out");
        return ok(views.html.Partner.partner_index.render());
    }

    @Security.Authenticated(SecuredUser.class)
    public static Result sendCandidateVerificationSMS(String mobile) {
        Logger.info("trying to send verification SMS to mobile no: " + FormValidator.convertToIndianMobileFormat(mobile));
        Candidate existingCandidate = CandidateService.isCandidateExists(FormValidator.convertToIndianMobileFormat(mobile));
        if(existingCandidate != null){
            PartnerService.sendCandidateVerificationSms(existingCandidate);
            return ok("1");
        }else{
            return ok("0");
        }
    }

    public static Result verifyCandidateUsingOtp() {
        JsonNode req = request().body().asJson();
        VerifyCandidateRequest verifyCandidateRequest = new VerifyCandidateRequest();
        ObjectMapper newMapper = new ObjectMapper();
        try {
            verifyCandidateRequest = newMapper.readValue(req.toString(), VerifyCandidateRequest.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Logger.info("Req JSON : " + req);
        return ok(toJson(PartnerService.verifyCandidateByPartner(verifyCandidateRequest)));
    }

    public static Result renderCandidateJobPage(long candidateId) { return ok(views.html.Partner.candidate_jobs.render()); }

    public static Result checkPartnerCandidate(long id) {
        Partner partner = Partner.find.where().eq("partner_id", session().get("partnerId")).findUnique();
        if(partner != null){ //checking if partner is logged in or not
            Candidate candidate = Candidate.find.where().eq("candidateId", id).findUnique(); //getting candidate profile from db
            if(candidate != null){ //checking if the candidate was created by the requested partner
                PartnerToCandidate partnerToCandidate = PartnerToCandidate.find
                        .where()
                        .eq("candidate_candidateid", candidate.getCandidateId())
                        .findUnique();
                if(partnerToCandidate != null){
                    if(partnerToCandidate.getPartner().getPartnerId() == partner.getPartnerId()){
                        return ok(toJson(candidate));
                    } else{
                        return ok("-1");
                    }
                }
            }
        }
        return ok("0");
    }

    @Security.Authenticated(SecuredUser.class)
    public static Result getAppliedJobsByPartnerForCandidate(long id) {
        Logger.info(id + " candidateId");
        Candidate candidate = Candidate.find.where().eq("candidateId", id).findUnique();
        if(candidate != null){
            Partner partner = Partner.find.where().eq("partner_id", session().get("partnerId")).findUnique();
            if(partner != null){
                return ok(toJson(JobPostWorkflowEngine.getPartnerAppliedJobsForCandidate(candidate, partner)));
            }
        }
        return ok("0");

    }

    public static Result getCandidateMatchingJobs(long id) {
        Candidate existingCandidate = Candidate.find.where().eq("candidateId", id).findUnique();
        if(existingCandidate != null){
            return ok(toJson(JobSearchService.getAllJobsForCandidate(FormValidator.convertToIndianMobileFormat(existingCandidate.getCandidateMobile()))));
        }
        return ok("ok");
    }

    public static Result getJobPostInfoViaPartner(long jobPostId, long candidateId) {
        JobPost jobPost = JobPostDAO.findById(jobPostId);
        if(jobPost !=null){
            String interactionResult = InteractionConstants.INTERACTION_RESULT_CANDIDATE_TRIED_TO_APPLY_JOB;
            String objAUUID = "";
            Candidate candidate = Candidate.find.where().eq("candidateId", candidateId). findUnique();
            if(candidate != null){
                objAUUID = candidate.getCandidateUUId();
                InteractionService.createInteractionForJobApplicationAttemptViaWebsite(
                        objAUUID,
                        jobPost.getJobPostUUId(),
                        interactionResult + jobPost.getJobPostTitle() + " at " + jobPost.getCompany().getCompanyName()
                );
                return ok(toJson(jobPost));
            }
        }
        return ok("0");
    }

    @Security.Authenticated(SecuredUser.class)
    public static Result confirmInterview(long cId, long jpId, long value){
        if(session().get("sessionChannel") == null){
            Logger.warn("Partner session channel not set, logged out partner");
            logoutPartner();
            return badRequest();
        }
        if (session().get("partnerId") != null) {
            Partner partner = Partner.find.where().eq("partner_id", session().get("partnerId")).findUnique();
            if(partner != null){
                Candidate candidate = Candidate.find.where().eq("candidateId", cId).findUnique();
                if(candidate != null){
                    PartnerToCandidate partnerToCandidate = PartnerToCandidate.find.where()
                            .eq("partner_id", partner.getPartnerId())
                            .eq("t0.candidate_CandidateId", candidate.getCandidateId())
                            .findUnique();

                    if(partnerToCandidate != null){
                        return ok(toJson(JobPostWorkflowEngine.confirmCandidateInterview(jpId, value, candidate, Integer.valueOf(session().get("sessionChannel")))));
                    }
                }
            }
        }
        return ok("0");
    }
}
