package controllers;

import api.InteractionConstants;
import api.ServerConstants;
import api.http.FormValidator;
import api.http.httpRequest.*;
import api.http.httpResponse.CandidateSignUpResponse;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import controllers.businessLogic.*;
import controllers.businessLogic.JobWorkflow.JobPostWorkflowEngine;
import controllers.security.FlashSessionController;
import controllers.security.ForceHttps;
import controllers.security.PartnerSecured;
import dao.JobPostDAO;
import dao.PartnerToCandidateToCompanyDAO;
import models.entity.*;
import models.entity.OM.PartnerToCandidate;
import models.entity.OM.PartnerToCandidateToCompany;
import models.entity.OM.PartnerToCompany;
import models.entity.Recruiter.RecruiterAuth;
import models.entity.Recruiter.RecruiterProfile;
import models.entity.Static.LeadSource;
import models.entity.Static.PartnerType;
import play.Logger;
import play.mvc.Result;
import play.mvc.Security;
import play.mvc.With;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

import static controllers.businessLogic.Recruiter.RecruiterAuthService.addSession;
import static play.libs.Json.toJson;
import static play.mvc.Controller.request;
import static play.mvc.Controller.session;
import static play.mvc.Results.*;

/**
 * Created by adarsh on 9/9/16.
 */
@With(ForceHttps.class)
public class PartnerController {
    public static Result partnerIndex() {
        String sessionId = session().get("partnerId");
        if(sessionId != null){

            // if flash is available, redirect there
            if(!FlashSessionController.isEmpty()){
                return redirect(FlashSessionController.getFlashFromSession());
            }

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
        Logger.info("partner login mobile: : " + loginRequest.getCandidateLoginMobile());
        String loginMobile = loginRequest.getCandidateLoginMobile();
        String loginPassword = loginRequest.getCandidateLoginPassword();
        return ok(toJson(PartnerService.login(loginMobile, loginPassword, InteractionConstants.INTERACTION_CHANNEL_CANDIDATE_WEBSITE)));
    }

    @Security.Authenticated(PartnerSecured.class)
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

    @Security.Authenticated(PartnerSecured.class)
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
            Partner partner = Partner.find.where().eq("partner_id", sessionPartnerId).findUnique();
            return ok(toJson(partner));
        } else{
            return ok("0");
        }
    }

    @Security.Authenticated(PartnerSecured.class)
    public static Result partnerEditProfile() {
        return ok(views.html.Partner.partner_edit_profile.render());
    }

    @Security.Authenticated(PartnerSecured.class)
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

    public static Integer checkCandidateExistence(Partner partner, String mobile){
        Integer response = ServerConstants.STATUS_NO_CANDIDATE;
        Candidate candidate = Candidate.find.where().eq("CandidateMobile", mobile).findUnique();
        if(candidate != null){

            //candidate exists! getting partnerToCompany list
            List<PartnerToCompany> companyList = PartnerToCompany.find.where()
                    .eq("partner_id", partner.getPartnerId())
                    .findList();

            List<Long> companyIdList = new ArrayList<>();
            for(PartnerToCompany company : companyList){
                companyIdList.add(company.getCompany().getCompanyId());
            }

            //to check partner to candidate pool
            PartnerToCandidateToCompany partnerToCandidateToCompany = PartnerToCandidateToCompanyDAO.getPartnerCreatedCandidateById(candidate, companyIdList);

            if(partnerToCandidateToCompany == null){
                response = ServerConstants.STATUS_CANDIDATE_EXISTS_DIFFERENT_COMPANY;
            } else{
                response = ServerConstants.STATUS_CANDIDATE_EXISTS_SAME_COMPANY;
            }
        }

        return response;
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

        String partnerId = session().get("partnerId");
        Partner partner = Partner.find.where().eq("partner_id", partnerId).findUnique();

        //checking if a candidate can be associate with a partner or not
        Integer associationStatus = checkCandidateExistence(partner, FormValidator.convertToIndianMobileFormat(addSupportCandidateRequest.getCandidateMobile()));
        Candidate candidate = CandidateService.isCandidateExists(FormValidator.convertToIndianMobileFormat(addSupportCandidateRequest.getCandidateMobile()));
        if(candidate == null){
            isNewCandidate = true; //checking if the candidate exists
        }
        if(partner != null){
            LeadSource leadSource = LeadSource.find.where().eq("leadSourceId", addSupportCandidateRequest.getLeadSource()).findUnique();
            if(leadSource != null){
                addSupportCandidateRequest.setLeadSource(leadSource.getLeadSourceId());
            }

            return ok(toJson(createCandidateViaPartner(addSupportCandidateRequest, partner, isNewCandidate, associationStatus)));
        } else{
            return ok("-1");
        }
    }

    public static CandidateSignUpResponse createCandidateViaPartner(AddSupportCandidateRequest addSupportCandidateRequest,
                                                                    Partner partner,
                                                                    Boolean isNewCandidate,
                                                                    Integer associationStatus,
                                                                    Boolean autoVerify) {

        Boolean isPrivatePartner = false;
        if(partner.getPartnerType().getPartnerTypeId() == ServerConstants.PARTNER_TYPE_PRIVATE){
            isPrivatePartner = true;
        }

        CandidateSignUpResponse candidateSignUpResponse = CandidateService.createCandidateProfile(addSupportCandidateRequest,
                InteractionConstants.INTERACTION_CHANNEL_PARTNER_WEBSITE,
                ServerConstants.UPDATE_ALL_BY_SUPPORT);

        if(candidateSignUpResponse.getStatus() == CandidateSignUpResponse.STATUS_SUCCESS){

            candidateSignUpResponse.setOtp(0);
            Candidate existingCandidate = CandidateService.isCandidateExists(FormValidator.convertToIndianMobileFormat(addSupportCandidateRequest.getCandidateMobile()));

            candidateSignUpResponse = partnerToCandidateAssociation(existingCandidate, partner, isPrivatePartner, isNewCandidate, associationStatus, autoVerify);
        }

        return candidateSignUpResponse;
    }

    public static CandidateSignUpResponse createCandidateViaPartner(AddSupportCandidateRequest addSupportCandidateRequest,
                                                                    Partner partner,
                                                                    Boolean isNewCandidate,
                                                                    Integer associationStatus){

        return createCandidateViaPartner(addSupportCandidateRequest, partner, isNewCandidate, associationStatus, false);
    }

    public static CandidateSignUpResponse partnerToCandidateAssociation(Candidate existingCandidate,
                                                                        Partner partner,
                                                                        Boolean isPrivatePartner,
                                                                        Boolean isNewCandidate,
                                                                        Integer associationStatus,
                                                                        Boolean autoVerify)
    {
        CandidateSignUpResponse candidateSignUpResponse =
                PartnerService.createPartnerToCandidateMapping(partner, existingCandidate.getCandidateMobile());

        //if the partner is a private partner
        if(isPrivatePartner || autoVerify){

            if(isNewCandidate){
                //auto verifying candidate profile as it is created via private partner
                Auth existingAuth = Auth.find.where().eq("candidateId", existingCandidate.getCandidateId()).findUnique();
                if(existingAuth != null){
                    existingAuth.setAuthStatus(ServerConstants.CANDIDATE_STATUS_VERIFIED);
                    existingAuth.update();
                    CandidateService.sendDummyAuthForCandidateByPartner(existingCandidate);
                    String objAUUID = existingCandidate.getCandidateUUId();
                    String objBUUID = partner.getPartnerUUId();

                    //creating interaction
                    PartnerInteractionService.createInteractionForPartnerVerifyingCandidate(objAUUID, objBUUID, partner.getPartnerFirstName());
                }
            }

            if(isPrivatePartner){
                existingCandidate.setCandidateAccessLevel(ServerConstants.CANDIDATE_ACCESS_LEVEL_PRIVATE);
                existingCandidate.update();
            }

            //don't send otp
            candidateSignUpResponse.setOtp(0);
        } else{
            candidateSignUpResponse.setOtp(PartnerService.sendCandidateVerificationSms(existingCandidate));
        }


        //STATUS NO CANDIDATE means its a new candidate, STATUS_CANDIDATE_EXISTS_DIFFERENT_COMPANY means this candidate exists
        // and is associated with other company, hence create an entry in partner to candidate followed by PartnerToCandidateToCompany

        if(associationStatus == ServerConstants.STATUS_NO_CANDIDATE
                || associationStatus == ServerConstants.STATUS_CANDIDATE_EXISTS_DIFFERENT_COMPANY)
        {

            PartnerService.createPartnerToCandidateMapping(partner, existingCandidate.getCandidateMobile());

            if(isPrivatePartner){
                //making entry in partnerToCandidateToCompany table
                partnerToCandidateToCompanyMapping(partner, existingCandidate);
            }
        }

        return candidateSignUpResponse;

    }
    public static CandidateSignUpResponse partnerToCandidateAssociation(Candidate existingCandidate, Partner partner, Boolean isPrivatePartner, Boolean isNewCandidate, Integer associationStatus){
        return partnerToCandidateAssociation(existingCandidate, partner, isPrivatePartner, isNewCandidate, associationStatus, false);
    }

    public static void partnerToCandidateToCompanyMapping(Partner partner, Candidate candidate){
        PartnerToCandidate partnerToCandidate = PartnerToCandidate.find.where()
                .eq("partner_id", partner.getPartnerId())
                .eq("candidate_candidateId", candidate.getCandidateId())
                .findUnique();

        List<PartnerToCompany> partnerToCompanyList = PartnerToCompany.find.where()
                .eq("partner_id", partner.getPartnerId())
                .eq("verification_status", ServerConstants.PARTNER_TO_COMPANY_VERIFIED)
                .findList();

        if(partnerToCandidate != null){
            Logger.info("Making entry in partnerToCandidateToCompany table");
            for(PartnerToCompany partnerToCompany : partnerToCompanyList){
                PartnerToCandidateToCompany partnerToCandidateToCompany = new PartnerToCandidateToCompany();
                partnerToCandidateToCompany.setPartner(partner);
                partnerToCandidateToCompany.setPartnerToCandidate(partnerToCandidate);
                partnerToCandidateToCompany.setPartnerToCompany(partnerToCompany);
                partnerToCandidateToCompany.save();
            }
        } else{
            Logger.info("Error in making entry in partnerToCandidateToCompany as the candidate is not associated with the partner");
        }
    }

    @Security.Authenticated(PartnerSecured.class)
    public static Result partnerCandidates() {
        return ok(views.html.Partner.partner_candidates.render());
    }

    @Security.Authenticated(PartnerSecured.class)
    public static Result getMyCandidates(){
        Partner partner = Partner.find.where().eq("partner_id", session().get("partnerId")).findUnique();
        if(partner != null){

            List<PartnerToCandidate> partnerToCandidateList = new ArrayList<>();
            List<PartnerToCandidateToCompany> partnerToCandidateToCompanyList = new ArrayList<>();
            List<Candidate> candidateList = new ArrayList<>();
            List<Long> candidateIdList = new ArrayList<>();

            if(partner.getPartnerType().getPartnerTypeId() == ServerConstants.PARTNER_TYPE_PRIVATE){
                partnerToCandidateToCompanyList = PartnerToCandidateToCompanyDAO.getPartnerCreatedCandidateList(partner);

                for(PartnerToCandidateToCompany partnerToCandidateToCompany : partnerToCandidateToCompanyList) {
                    candidateList.add(partnerToCandidateToCompany.getPartnerToCandidate().getCandidate());
                }

                //removing duplicate data from list
                Set<Candidate> candidateSet = new HashSet<>();
                candidateSet.addAll(candidateList);
                candidateList.clear();
                candidateList.addAll(candidateSet);

            } else{
                partnerToCandidateList = PartnerToCandidate.find.where()
                        .eq("partner_id", partner.getPartnerId())
                        .orderBy("partner_to_candidate_create_timestamp desc")
                        .findList();

                for(PartnerToCandidate partnerToCandidate : partnerToCandidateList) {
                    candidateList.add(partnerToCandidate.getCandidate());
                }
            }

            ArrayList<PartnerCandidatesResponse> responses = new ArrayList<>();

            SimpleDateFormat sfd = new SimpleDateFormat(ServerConstants.SDF_FORMAT_YYYYMMDD);
            for(Candidate candidate : candidateList) {
                candidateIdList.add(candidate.getCandidateId());
            }

            Map<?, Auth> authMap = Auth.find.where().in("candidateId", candidateIdList).setMapKey("candidateId").findMap();

            for(Candidate candidate : candidateList) {
                PartnerCandidatesResponse response = new PartnerCandidatesResponse();

                response.setCandidateId(candidate.getCandidateId());
                response.setCreationTimestamp(sfd.format(candidate.getCandidateCreateTimestamp()));
                response.setLeadId(candidate.getLead().getLeadId());
                if(candidate.getCandidateFirstName() != null){
                    response.setCandidateName(candidate.getCandidateFirstName());
                    if(candidate.getCandidateLastName() != null){
                        response.setCandidateName(candidate.getCandidateFirstName() + " " + candidate.getCandidateLastName());
                    }
                }
                Auth auth = authMap.get(candidate.getCandidateId());

                if(auth != null){
                    response.setCandidateStatus(auth.getAuthStatus());
                    if(auth.getAuthStatus() == ServerConstants.CANDIDATE_STATUS_VERIFIED){
                        response.setCandidateActiveDeactive(candidate.getCandidateprofilestatus().getProfileStatusId());
                    }
                }
                response.setCandidateAppliedJobs(JobPostWorkflowEngine.getPartnerAppliedJobsForCandidate(
                        candidate, partner).size());
                response.setCandidateMobile(candidate.getCandidateMobile());
                response.setCandidateResumeLink(candidate.getCandidateResumeLink());
                responses.add(response);
            }
            return ok(toJson(responses));
        } else{
            //partner does not exists
            Logger.info("Partner not available");

        }
        return ok("0");
    }

    @Security.Authenticated(PartnerSecured.class)
    public static Result getPartnerCandidate(long leadId) {
        Partner partner = Partner.find.where().eq("partner_id", session().get("partnerId")).findUnique();
        if(partner != null){ //checking if partner is logged in or not
            Lead lead = Lead.find.where().eq("leadId", leadId).findUnique(); //getting candidate profile from db
            if(lead != null) {
                Candidate candidate = CandidateService.isCandidateExists(lead.getLeadMobile());
                if(candidate != null){ //checking if the candidate was created by the requested partner
                    List<PartnerToCandidate> partnerToCandidateList = PartnerToCandidate.find
                            .where()
                            .eq("candidate_candidateid", candidate.getCandidateId())
                            .findList();

                    Boolean allow = false;
                    for(PartnerToCandidate partnerToCandidate : partnerToCandidateList){
                        if(partnerToCandidate.getPartner().getPartnerId() == partner.getPartnerId()){
                            allow = true;
                        }
                    }
                    if(allow){
                        return ok(toJson(candidate));
                    } else{
                        return ok("-1");
                    }

                }
            }
        }
        return ok("0");
    }

    public static Result logoutPartner() {
        FlashSessionController.clearSessionExceptFlash();

        Logger.info("Partner Logged Out");
        return ok(views.html.Partner.partner_index.render());
    }

    @Security.Authenticated(PartnerSecured.class)
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
                        .eq("partner_id", partner.getPartnerId())
                        .eq("candidate_candidateid", candidate.getCandidateId())
                        .setMaxRows(1)
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

public static Result checkExistingCompany(String CompanyCode) {
    Integer companyCount = Company.find.where().eq("CompanyCode", CompanyCode).findRowCount();
    if(companyCount > 0){
        return ok("1");
    } else{
        return ok("0");
    }
}

    @Security.Authenticated(PartnerSecured.class)
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

    @Security.Authenticated(PartnerSecured.class)
    public static Result checkPrivatePartnerRecruiterAccount() {
        if(session().get("partnerId") != null){
            Partner partner = Partner.find.where().eq("partner_id", session().get("partnerId")).findUnique();
            if(partner != null && partner.getPartnerType().getPartnerTypeId() == ServerConstants.PARTNER_TYPE_PRIVATE){
                List<Integer> recruiterTypeList = new ArrayList<>();
                recruiterTypeList.add(ServerConstants.RECRUITER_ACCESS_LEVEL_PRIVATE);
                recruiterTypeList.add(ServerConstants.RECRUITER_ACCESS_LEVEL_PRIVATE_ADMIN);

                RecruiterProfile recruiterProfile = RecruiterProfile.find.where()
                        .eq("RecruiterProfileMobile", partner.getPartnerMobile())
                        .in("recruiter_access_level", recruiterTypeList)
                        .findUnique();

                if(recruiterProfile != null){
                    RecruiterAuth recruiterAuth = RecruiterAuth.find.where().eq("recruiter_id",
                            recruiterProfile.getRecruiterProfileId()).findUnique();

                    if(recruiterAuth != null){
                        return ok("1");
                    }
                }
            }
        }
        return ok("0");
    }

    @Security.Authenticated(PartnerSecured.class)
    public static Result switchToRecruiter() {
        if(session().get("partnerId") != null){
            Partner partner = Partner.find.where().eq("partner_id", session().get("partnerId")).findUnique();
            if(partner != null && partner.getPartnerType().getPartnerTypeId() == ServerConstants.PARTNER_TYPE_PRIVATE){
                List<Integer> recruiterTypeList = new ArrayList<>();
                recruiterTypeList.add(ServerConstants.RECRUITER_ACCESS_LEVEL_PRIVATE);
                recruiterTypeList.add(ServerConstants.RECRUITER_ACCESS_LEVEL_PRIVATE_ADMIN);

                RecruiterProfile existingRecruiter = RecruiterProfile.find.where()
                    .eq("RecruiterProfileMobile", partner.getPartnerMobile())
                    .in("recruiter_access_level", recruiterTypeList)
                    .findUnique();

                if(existingRecruiter != null){

                    RecruiterAuth recruiterAuth = RecruiterAuth.find.where().eq("recruiter_id",
                            existingRecruiter.getRecruiterProfileId()).findUnique();

                    if(recruiterAuth != null){
                        //clearing session for partner
                        FlashSessionController.clearSessionExceptFlash();
                        addSession(recruiterAuth, existingRecruiter);
                        return ok("1");
                    }
                }
            }
        }
        return ok("0");
    }

    public static Result getCandidateMatchingJobs(long id) {
        Candidate existingCandidate = Candidate.find.where().eq("candidateId", id).findUnique();
        if(existingCandidate != null){
            List<JobPost> matchingJobList = new ArrayList<>();
            if (session().get("partnerId") != null) {
                Partner partner = Partner.find.where().eq("partner_id", session().get("partnerId")).findUnique();
                if(partner != null){
                    if(partner.getPartnerType().getPartnerTypeId() == ServerConstants.PARTNER_TYPE_PRIVATE){

                        //fetching all the private jobs posted by the recruiter of the given company
                        List<PartnerToCompany> partnerToCompanyList = PartnerToCompany.find.where()
                                .eq("partner_id", partner.getPartnerId())
                                .findList();

                        List<Long> companyIdList = new ArrayList<>();
                        for(PartnerToCompany partnerToCompany : partnerToCompanyList){
                            companyIdList.add(partnerToCompany.getCompany().getCompanyId());
                        }
                        matchingJobList = JobPostDAO.getAllActiveHotNonPrivateJobsPostOfCompany(companyIdList);
                    } else{
                        matchingJobList = JobSearchService
                                .getAllJobsForCandidate(FormValidator.convertToIndianMobileFormat(
                                        existingCandidate.getCandidateMobile()), ServerConstants.JOB_POST_TYPE_OPEN);
                    }
                }
            }

            SearchJobService.computeCTA(matchingJobList, id);
            return ok(toJson(matchingJobList));

        }
        return ok("0");
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

    @Security.Authenticated(PartnerSecured.class)
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
