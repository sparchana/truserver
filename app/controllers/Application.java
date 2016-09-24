package controllers;

import api.InteractionConstants;
import api.ServerConstants;
import api.http.FormValidator;
import api.http.httpRequest.*;
import api.http.httpResponse.*;
import com.amazonaws.util.json.JSONException;
import com.avaje.ebean.Ebean;
import com.avaje.ebean.Query;
import com.avaje.ebean.cache.ServerCacheManager;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import controllers.businessLogic.*;
import controllers.businessLogic.Assessment.AssessmentService;
import controllers.security.*;
import models.entity.*;
import models.entity.OM.*;
import models.entity.Static.*;
import models.util.ParseCSV;
import models.util.SmsUtil;
import models.util.Util;
import play.Logger;
import play.api.Play;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.Security;

import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

import static com.avaje.ebean.Expr.eq;
import static play.libs.Json.toJson;

public class Application extends Controller {

    private static boolean isDevMode = Play.isDev(Play.current()) || Play.isTest(Play.current());

    public static Result index() {
        String sessionId = session().get("sessionId");
        if(sessionId != null){
            String partnerId = session().get("partnerId");
            if(partnerId != null){
                return redirect("/partner/home");
            } else {
                return redirect("/dashboard");
            }
        }
        return ok(views.html.index.render());
    }

    @Security.Authenticated(RecSecured.class)
    public static Result showCompanyAndJob() {
        return ok(views.html.Recs.company_and_job.render());
    }

    @Security.Authenticated(PartnerSecured.class)
    public static Result support() {
        return ok(views.html.support.render());
    }

    @Security.Authenticated(PartnerSecured.class)
    public static Result candidateInteraction(long id) {
        return ok(views.html.candidate_interaction.render());
    }

    public static Result privacy() {
        return ok(views.html.privacy.render());
    }

    @Security.Authenticated(Secured.class)
    public static Result getCandidateInteraction(long id){
        Lead lead = Lead.find.where().eq("leadId",id).findUnique();
        if(lead !=null){
            List<Interaction> fullInteractionList = Interaction.find.where().eq("objectAUUId", lead.getLeadUUId()).findList();

            // fetch candidate interaction as well
            Candidate candidate = Candidate.find.where().eq("lead_leadId", id).findUnique();
            if(candidate != null){
                List<Interaction> candidateInteractionList = Interaction.find.where().eq("objectAUUId", candidate.getCandidateUUId()).findList();
                fullInteractionList.addAll(candidateInteractionList);
            }

            List<SupportInteractionResponse> responses = new ArrayList<>();

            SimpleDateFormat sfd = new SimpleDateFormat(ServerConstants.SDF_FORMAT);

            for(Interaction interaction : fullInteractionList){
                SupportInteractionResponse response = new SupportInteractionResponse();
                response.setUserInteractionTimestamp(sfd.format(interaction.getCreationTimestamp()));
                response.setInteractionId(interaction.getId());
                response.setUserNote(interaction.getNote());
                response.setUserResults(interaction.getResult());
                response.setUserCreatedBy(interaction.getCreatedBy());
                switch (interaction.getInteractionType()) {
                    case 0: response.setUserInteractionType("Unknown"); break;
                    case 1: response.setUserInteractionType("Follow Up Call"); break;
                    case 2: response.setUserInteractionType("Job Apply Successful"); break;
                    case 3: response.setUserInteractionType("Tried to Apply a Job"); break;
                    case 4: response.setUserInteractionType("Tried to Reset Password"); break;
                    case 5: response.setUserInteractionType("Reset Password Successful"); break;
                    case 6: response.setUserInteractionType("Clicked Candidate Alert"); break;
                    case 7: response.setUserInteractionType("Job Search"); break;
                    case 8: response.setUserInteractionType("Job Post View"); break;
                    case 9: response.setUserInteractionType("Login"); break;
                    case 10: response.setUserInteractionType("Sign Up"); break;
                    case 11: response.setUserInteractionType("Profile Updated"); break;
                    case 12: response.setUserInteractionType("Profile Created"); break;
                    case 13: response.setUserInteractionType("New Lead"); break;
                    case 14: response.setUserInteractionType("Candidate Verified"); break;
                    case 15: response.setUserInteractionType("Tried to Verify candidate"); break;
                    case 16: response.setUserInteractionType("Password Created"); break;
                    case 17: response.setUserInteractionType("Candidate Activated"); break;
                    case 18: response.setUserInteractionType("Candidate Deactivated"); break;
                    case 19: response.setUserInteractionType("Lead Status Updated"); break;
                    default: response.setUserInteractionType("Interaction Undefined in getCandidateInteraction()"); break;
                }
                Logger.info(interaction.getInteractionChannel() + " ------<<<<");
                switch (interaction.getInteractionChannel()) {
                    case 0: response.setChannel("Unknown"); break;
                    case 1: response.setChannel("Candidate via Website"); break;
                    case 2: response.setChannel("Candidate via Android"); break;
                    case 3: response.setChannel("Partner via website"); break;
                    case 4: response.setChannel("System via website"); break;
                    default: response.setChannel("channel Undefined in getCandidateInteraction()"); break;
                }

                responses.add(response);
            }
            return ok(toJson(responses));
        }
        else
            return ok("no records");
    }

    public static Result addLead() {
        JsonNode req = request().body().asJson();
        AddLeadRequest addLeadRequest = new AddLeadRequest();
        ObjectMapper newMapper = new ObjectMapper();
        try {
            addLeadRequest = newMapper.readValue(req.toString(), AddLeadRequest.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Logger.info(addLeadRequest.getLeadMobile() + " JSON req: " + req);

        AddLeadResponse addLeadResponse = new AddLeadResponse();
        Lead lead = new Lead(addLeadRequest.getLeadName(),
                addLeadRequest.getLeadMobile(),
                addLeadRequest.getLeadChannel(),
                ServerConstants.TYPE_LEAD,
                ServerConstants.LEAD_SOURCE_UNKNOWN
        );
        lead.setLeadType(addLeadRequest.getLeadType());
        LeadService.createLead(lead, InteractionService.InteractionChannelType.SELF);
        addLeadResponse.setStatus(AddLeadResponse.STATUS_SUCCESS);
        return ok(toJson(addLeadResponse));
    }

    public static Result signUp() {
        JsonNode req = request().body().asJson();
        CandidateSignUpRequest candidateSignUpRequest = new CandidateSignUpRequest();
        ObjectMapper newMapper = new ObjectMapper();
        try {
            candidateSignUpRequest = newMapper.readValue(req.toString(), CandidateSignUpRequest.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Logger.info("JSON req: " + req);

        InteractionService.InteractionChannelType channelType = InteractionService.InteractionChannelType.SELF;
        return ok(toJson(CandidateService.signUpCandidate(candidateSignUpRequest, channelType, ServerConstants.LEAD_SOURCE_UNKNOWN)));
    }
    @Security.Authenticated(PartnerSecured.class)
    public static Result signUpSupport() {
        JsonNode req = request().body().asJson();
        AddSupportCandidateRequest addSupportCandidateRequest = new AddSupportCandidateRequest();
        ObjectMapper newMapper = new ObjectMapper();
        // since jsonReq has single/multiple values in array
        newMapper.enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY);
        try {
            addSupportCandidateRequest = newMapper.readValue(req.toString(), AddSupportCandidateRequest.class);
            Logger.info("json" + req.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ok(toJson(CandidateService.createCandidateProfile(addSupportCandidateRequest,
                InteractionService.InteractionChannelType.SUPPORT,
                ServerConstants.UPDATE_ALL_BY_SUPPORT)));
    }

    public static Result candidateUpdateBasicProfile() {
        JsonNode req = request().body().asJson();
        AddCandidateRequest addCandidateRequest = new AddCandidateRequest();
        ObjectMapper newMapper = new ObjectMapper();
        try {
            addCandidateRequest = newMapper.readValue(req.toString(), AddCandidateRequest.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Logger.info("Req JSON : " + req);
        return ok(toJson(CandidateService.createCandidateProfile(addCandidateRequest, InteractionService.InteractionChannelType.SELF, ServerConstants.UPDATE_BASIC_PROFILE)));
    }

    public static Result candidateUpdateExperienceDetails() {
        JsonNode req = request().body().asJson();
        Logger.info(" == " + req);
        AddCandidateExperienceRequest addCandidateExperienceRequest = new AddCandidateExperienceRequest();
        ObjectMapper newMapper = new ObjectMapper();
        try {
            addCandidateExperienceRequest = newMapper.readValue(req.toString(), AddCandidateExperienceRequest.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ok(toJson(CandidateService.createCandidateProfile(addCandidateExperienceRequest, InteractionService.InteractionChannelType.SELF, ServerConstants.UPDATE_SKILLS_PROFILE)));
    }

    public static Result candidateUpdateEducationDetails() {
        JsonNode req = request().body().asJson();
        AddCandidateEducationRequest addCandidateEducationRequest = new AddCandidateEducationRequest();
        ObjectMapper newMapper = new ObjectMapper();
        try {
            addCandidateEducationRequest = newMapper.readValue(req.toString(), AddCandidateEducationRequest.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ok(toJson(CandidateService.createCandidateProfile(addCandidateEducationRequest, InteractionService.InteractionChannelType.SELF, ServerConstants.UPDATE_EDUCATION_PROFILE)));
    }

    public static Result addPassword() {
        JsonNode req = request().body().asJson();
        CandidateSignUpRequest candidateSignUpRequest = new CandidateSignUpRequest();
        ObjectMapper newMapper = new ObjectMapper();
        try {
            candidateSignUpRequest = newMapper.readValue(req.toString(), CandidateSignUpRequest.class);
        } catch (IOException e) {
            e.printStackTrace();
        }

        String userMobile = candidateSignUpRequest.getCandidateAuthMobile();
        String userPassword = candidateSignUpRequest.getCandidatePassword();

        return ok(toJson(AuthService.savePassword(userMobile, userPassword, InteractionService.InteractionChannelType.SELF)));
    }

    public static Result applyJob() throws IOException, JSONException {
        JsonNode req = request().body().asJson();
        ApplyJobRequest applyJobRequest = new ApplyJobRequest();
        ObjectMapper newMapper = new ObjectMapper();
        try {
            applyJobRequest = newMapper.readValue(req.toString(), ApplyJobRequest.class);
        } catch (IOException e) {
            e.printStackTrace();
        }

        String userMobile = applyJobRequest.getCandidateMobile();
        Integer jobId = applyJobRequest.getJobId();

        return ok(toJson(JobService.applyJob(applyJobRequest, InteractionService.InteractionChannelType.SELF)));
    }

    @Security.Authenticated(RecSecured.class)
    public static Result addJobPost() {
        JsonNode req = request().body().asJson();
        Logger.info(req + " == ");
        AddJobPostRequest addJobPostRequest = new AddJobPostRequest();
        ObjectMapper newMapper = new ObjectMapper();
        newMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        try {
            addJobPostRequest = newMapper.readValue(req.toString(), AddJobPostRequest.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ok(toJson(JobService.addJobPost(addJobPostRequest)));
    }

    public static Result addCompanyLogo() {
        Http.MultipartFormData body = request().body().asMultipartFormData();
        Http.MultipartFormData.FilePart picture = body.getFile("picture");
        if (picture != null) {
            String fileName = picture.getFilename();
            String contentType = picture.getContentType();
            File file = (File) picture.getFile();
            Logger.info("uploaded! " + file);
            CompanyService.uploadCompanyLogo(file, fileName);
            return ok("File uploaded");
        } else {
            flash("error", "Missing file");
            return redirect(routes.Application.index());
        }
    }

    @Security.Authenticated(RecSecured.class)
    public static Result addRecruiter() {
        JsonNode req = request().body().asJson();
        Logger.info(req + " == ");
        AddRecruiterRequest addRecruiterRequest = new AddRecruiterRequest();
        ObjectMapper newMapper = new ObjectMapper();
        try {
            addRecruiterRequest = newMapper.readValue(req.toString(), AddRecruiterRequest.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ok(toJson(RecruiterService.addRecruiter(addRecruiterRequest)));
    }

    @Security.Authenticated(RecSecured.class)
    public static Result addCompany() {
        JsonNode req = request().body().asJson();
        Logger.info(req + " == ");
        AddCompanyRequest addCompanyRequest = new AddCompanyRequest();
        ObjectMapper newMapper = new ObjectMapper();
        try {
            addCompanyRequest = newMapper.readValue(req.toString(), AddCompanyRequest.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ok(toJson(CompanyService.addCompany(addCompanyRequest)));
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
        return ok(toJson(CandidateService.login(loginMobile, loginPassword, InteractionService.InteractionChannelType.SELF)));
    }

    @Security.Authenticated(SecuredUser.class)
    public static Result dashboard() {
        return ok(views.html.CandidateDashboard.candidate_home.render());
    }

    @Security.Authenticated(SecuredUser.class)
    public static Result editProfile() {
        return ok(views.html.CandidateDashboard.edit_profile.render());
    }

    @Security.Authenticated(SecuredUser.class)
    public static Result appliedJobs() {
        return ok(views.html.CandidateDashboard.candidate_applied_job.render());
    }

    public static Result findUserAndSendOtp() {
        JsonNode req = request().body().asJson();
        ResetPasswordResquest resetPasswordResquest = new ResetPasswordResquest();
        ObjectMapper newMapper = new ObjectMapper();
        try {
            resetPasswordResquest = newMapper.readValue(req.toString(), ResetPasswordResquest.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        String candidateMobile = resetPasswordResquest.getResetPasswordMobile();
        Logger.info("==> " + candidateMobile);

        return ok(toJson(CandidateService.findUserAndSendOtp(candidateMobile, InteractionService.InteractionChannelType.SELF)));
    }

    public static Result processcsv() {
        java.io.File file = (File) request().body().asMultipartFormData().getFile("file").getFile();
        if(file == null) {
            return badRequest("error uploading file. Check file type");
        }
        return ok(toJson(ParseCSV.parseCSV(file)));
    }

    @Security.Authenticated(SuperAdminSecured.class)
    public static Result processBabaJCSV() {
        java.io.File file = (File) request().body().asMultipartFormData().getFile("file").getFile();
        if(file == null) {
            return badRequest("error uploading file. Check file type");
        }
        return ok(toJson(ParseCSV.parseBabaJobsCSV(file)));
    }

    @Security.Authenticated(Secured.class)
    public static Result getAll(int id){
        List<Lead> allLead = new ArrayList<>();
        switch (id){
            case 1: // get all leads only
                allLead = Lead.find.where()
                        .ne("leadStatus", ServerConstants.LEAD_STATUS_WON)
                        .ne("leadStatus", ServerConstants.LEAD_STATUS_LOST)
                        .findList();
                break;
            case 2: // get all candidates only
                allLead = Lead.find.where()
                        .eq("leadType", ServerConstants.TYPE_CANDIDATE)
                        .eq("leadStatus", ServerConstants.LEAD_STATUS_WON)
                        .findList();
                break;
            case 3: // get all except all the partners
                allLead = Lead.find.where()
                        .ne("leadType", ServerConstants.TYPE_PARTNER)
                        .findList();
                break;
        }

        ArrayList<SupportDashboardElementResponse> responses = new ArrayList<>();

        SimpleDateFormat sfd = new SimpleDateFormat(ServerConstants.SDF_FORMAT);
        SimpleDateFormat sfdFollowUp = new SimpleDateFormat(ServerConstants.SDF_FORMAT_FOLLOWUP);

        //getting leadUUID from allLead
        List<String> leadUUIDList = allLead.stream().map(Lead::getLeadUUId).collect(Collectors.toList());

        List<Interaction> interactionsOfLead = Interaction.find.where().in("objectAUUId", leadUUIDList).findList();

        Logger.info("Entering Loop at " + new Timestamp(System.currentTimeMillis()));
        for(Lead lead : allLead) {
            SupportDashboardElementResponse response = new SupportDashboardElementResponse();

            response.setLeadCreationTimestamp(sfd.format(lead.getLeadCreationTimestamp()));
            response.setLeadId(lead.getLeadId());
            response.setLeadName(lead.getLeadName());
            response.setLeadMobile(lead.getLeadMobile());
            switch (lead.getLeadStatus()) {
                case 0: response.setLeadStatus("New"); break;
                case 1: response.setLeadStatus("T.T.C"); break;
                case 2: response.setLeadStatus("Won"); break;
                case 3: response.setLeadStatus("Lost"); break;
            }
            switch (lead.getLeadType()) {
                case 0: response.setLeadType("Fresh"); break;
                case 1: response.setLeadType("Lead"); break;
                case 2: response.setLeadType("Potential Candidate"); break;
                case 3: response.setLeadType("Potential Recruiter"); break;
                case 4: response.setLeadType("Candidate"); break;
                case 5: response.setLeadType("Recruiter"); break;
            }
            switch (lead.getLeadChannel()) {
                case 0: response.setLeadChannel("Website"); break;
                case 1: response.setLeadChannel("Knowlarity"); break;
            }
            int mTotalInteraction=0;
            Timestamp mostRecent = lead.getLeadCreationTimestamp();
            for(Interaction i: interactionsOfLead) {
                if(i.getObjectAUUId().equals(lead.getLeadUUId())){
                    if(i.getInteractionType() == 1 || i.getInteractionType() == 5) {
                        mTotalInteraction++;
                        if(mostRecent.getTime() <= i.getCreationTimestamp().getTime()){
                            mostRecent = i.getCreationTimestamp();
                        }
                    }
                }
            }
            response.setLastIncomingCallTimestamp(sfd.format(mostRecent));
            response.setTotalInBounds(mTotalInteraction);
            if(lead.getFollowUp() != null && lead.getFollowUp().getFollowUpTimeStamp()!= null){
                response.setFollowUpStatus(lead.getFollowUp().isFollowUpStatusRequired());
                response.setFollowUpTimeStamp(sfdFollowUp.format(lead.getFollowUp().getFollowUpTimeStamp()));
            }
            responses.add(response);
        }
        Logger.info("Exit Loop at " + new Timestamp(System.currentTimeMillis()));

        return ok(toJson(responses));
    }
    @Security.Authenticated(PartnerSecured.class)
    public static Result getLeadMobile(long id) {
        if (id != 0) {
            try {
                Lead lead = Lead.find.where().eq("leadId", id).findUnique();
                String leadMobile = lead.getLeadMobile();
                return ok(leadMobile);
            }
            catch(NullPointerException n) {
                Logger.error("Could not find lead with id :" + id );
                n.printStackTrace();
            }
        }
        return ok();
    }

    /* this method is used by candidate dashboard */
    @Security.Authenticated(SecuredUser.class)
    public static Result getCandidateInfoDashboard() {
        Lead lead = Lead.find.where().eq("leadId", session().get("leadId")).findUnique();
        if(lead != null) {
            Candidate candidate = CandidateService.isCandidateExists(lead.getLeadMobile());
            if(candidate!=null){
                return ok(toJson(candidate));
            }
        }
        return ok("0");
    }

    /* this method is used by support */
    @Security.Authenticated(PartnerSecured.class)
    public static Result getCandidateInfo(long leadId) {
            Lead lead = Lead.find.where().eq("leadId", leadId).findUnique();
            if(lead != null) {
                Candidate candidate = CandidateService.isCandidateExists(lead.getLeadMobile());
                if(candidate!=null){
                    return ok(toJson(candidate));
                }
            }
        return ok("0");
    }

    @Security.Authenticated(RecSecured.class)
    public static Result getCompanyInfo(long companyId) {
        Company company = Company.find.where().eq("companyId", companyId).findUnique();
        if(company!=null){
            return ok(toJson(company));
        }
        return ok("0");
    }

    @Security.Authenticated(RecSecured.class)
    public static Result getCompanyRecruiters(long companyId) {
        List<RecruiterProfile> recruiterProfileList = RecruiterProfile.find.where().eq("company.companyId", companyId).findList();
        if(recruiterProfileList != null){
            return ok(toJson(recruiterProfileList));
        }
        return ok("0");
    }

    @Security.Authenticated(RecSecured.class)
    public static Result getRecruiterInfo(long recId) {
        RecruiterProfile recruiterProfile = RecruiterProfile.find.where().eq("recruiterProfileId", recId).findUnique();
        if(recruiterProfile != null){
            return ok(toJson(recruiterProfile));
        }
        return ok("0");
    }

    public static Result getJobPostInfo(long jobPostId, Integer isSupport) {
        JobPost jobPost = JobPost.find.where().eq("jobPostId", jobPostId).findUnique();
        if(jobPost!=null){
            if(isSupport == 0){
                String interactionResult = InteractionConstants.INTERACTION_RESULT_CANDIDATE_TRIED_TO_APPLY_JOB;
                String objAUUID = "";
                if(session().get("candidateId") != null){
                    Candidate candidate = Candidate.find.where().eq("candidateId", session().get("candidateId")). findUnique();
                    objAUUID = candidate.getCandidateUUId();
                }
                InteractionService.createInteractionForJobApplicationAttemptViaWebsite(
                        objAUUID,
                        jobPost.getJobPostUUId(),
                        interactionResult + jobPost.getJobPostTitle() + " at " + jobPost.getCompany().getCompanyName()
                );
            }

            return ok(toJson(jobPost));
        }
        return ok("0");
    }

    @Security.Authenticated(SecuredUser.class)
    public static Result getCandidateJobApplication() {
        if(session().get("candidateId") != null){
            List<JobApplication> jobApplicationList = JobApplication.find.where().eq("candidateId", session().get("candidateId")).findList();
            if(jobApplicationList == null)
                return ok("0");
            return ok(toJson(jobApplicationList));
        } else{
            return ok("0");
        }
    }

    public static Result getAllSkills(String ids) {
        List<String> jobPrefIdList = Arrays.asList(ids.split("\\s*,\\s*"));
        List<JobToSkill> response = new ArrayList<>();
        int flag = 0;
        for(String jobId: jobPrefIdList) {
            List<JobToSkill> jobToSkillList = JobToSkill.find.where().eq("JobRoleId", jobId).findList();
            if(response.isEmpty()){
                response.addAll(jobToSkillList);
            } else {
                for (JobToSkill dbItem: jobToSkillList){
                    flag = 0;
                    for(JobToSkill item: response){
                            if(item.getSkill().getSkillId() == dbItem.getSkill().getSkillId()){
                                flag = 1;
                                break;
                            }
                        }
                    if(flag == 0){
                        response.add(dbItem);
                    }
                }
            }
        }
        return ok(toJson(response));
    }

    @Security.Authenticated(RecSecured.class)
    public static Result companyInfoHome(Long id) {
        return ok(views.html.Recs.company_details.render());
    }

    @Security.Authenticated(RecSecured.class)
    public static Result recruiterInfoHome(Long id) {
        return ok(views.html.Recs.recruiter_details.render());
    }

    @Security.Authenticated(RecSecured.class)
    public static Result jobPostInfoHome(Long id) {
        return ok(views.html.Recs.job_post_details.render());
    }

    public static Result supportAuth() {
        return ok(views.html.supportAuth.render());
    }

    public static Result logout() {
        session().clear();
        flash("success", "You've been logged out");
        return redirect(
                routes.Application.supportAuth()
        );
    }

    public static Result logoutUser() {
        session().clear();
        Logger.info("Candidate Logged Out");
        return ok(views.html.main.render());
    }

    public static Result auth() {
        Form<DevLoginRequest> userForm = Form.form(DevLoginRequest.class);
        DevLoginRequest request = userForm.bindFromRequest().get();
        Logger.info("Verifying credentials for support UserName : " + request.getAdminid() + " Password: " + request.getAdminpass());
        Developer developer = Developer.find.where().eq("developerId", request.getAdminid()).findUnique();
        if(developer!=null){
            Logger.info(Util.md5(request.getAdminpass() + developer.getDeveloperPasswordSalt()));
            if(developer.getDeveloperPasswordMd5().equals(Util.md5(request.getAdminpass() + developer.getDeveloperPasswordSalt()))) {
                developer.setDeveloperSessionId(UUID.randomUUID().toString());
                developer.setDeveloperSessionIdExpiryMillis(System.currentTimeMillis() + 24 * 60 * 60 * 1000);
                developer.update();
                session("sessionId", developer.getDeveloperSessionId());
                session("sessionUsername", developer.getDeveloperName());
                session("sessionUserId", "" + developer.getDeveloperId());
                session("sessionExpiry", String.valueOf(developer.getDeveloperSessionIdExpiryMillis()));
                session("sessionRDPK", String.valueOf(developer.getDeveloperAccessLevel()));

                if(developer.getDeveloperAccessLevel() == ServerConstants.DEV_ACCESS_LEVEL_SUPER_ADMIN) {
                    return redirect("/support/administrator");
                }
                if(developer.getDeveloperAccessLevel() == ServerConstants.DEV_ACCESS_LEVEL_SUPPORT_ROLE ||
                        developer.getDeveloperAccessLevel() == ServerConstants.DEV_ACCESS_LEVEL_SUPER_ADMIN ||
                        developer.getDeveloperAccessLevel() == ServerConstants.DEV_ACCESS_LEVEL_REC ||
                        developer.getDeveloperAccessLevel() == ServerConstants.DEV_ACCESS_LEVEL_ADMIN)
                {
                    return redirect(routes.Application.support());
                }

                if (developer.getDeveloperAccessLevel() == ServerConstants.DEV_ACCESS_LEVEL_PARTNER_ROLE) {
                    return redirect(routes.Application.createCandidateForm());
                }
            }
        } else {
            return badRequest("Account Doesn't exist!!");
        }
        return redirect(routes.Application.supportAuth());
    }

    @Security.Authenticated(SecuredUser.class)
    public static Result updateIsAssessedToAssessed() {
        if(session().get("candidateId") != null){
            Candidate existingCandidate = Candidate.find.where().eq("candidateId", session().get("candidateId")).findUnique();
            if(existingCandidate != null){
                existingCandidate.setCandidateIsAssessed(ServerConstants.CANDIDATE_ASSESSED);
                existingCandidate.update();
                return ok(toJson(ServerConstants.CANDIDATE_ASSESSED));
            }
        }
        return badRequest();
    }

    @Security.Authenticated(Secured.class)
    public static Result updateLeadType(long leadId, long newType) {
        //TODO: Not using this api anymore
        try{
            Lead lead = Lead.find.where().eq("leadId", leadId).findUnique();
            if(lead != null){
                if(lead.getLeadStatus() < ServerConstants.LEAD_STATUS_WON) {
                    lead.setLeadStatus(ServerConstants.LEAD_STATUS_WON);
                    lead.setLeadType((int) newType);
                    lead.update();

                    // TODO: interaction type to be defined
                    Interaction interaction = new Interaction(
                            lead.getLeadUUId(),
                            lead.getLeadType(),
                            InteractionConstants.INTERACTION_TYPE_LEAD_STATUS_UPDATE,
                            InteractionConstants.INTERACTION_NOTE_BLANK,
                            InteractionConstants.INTERACTION_RESULT_SYSTEM_UPDATED_LEADTYPE + newType,
                            session().get("sessionUsername"),
                            InteractionConstants.INTERACTION_CHANNEL_SUPPORT_WEBSITE
                    );
                    interaction.save();
                } else {
                    // TODO: interaction type to be defined
                    Interaction interaction = new Interaction(
                            lead.getLeadUUId(),
                            lead.getLeadType(),
                            InteractionConstants.INTERACTION_TYPE_LEAD_STATUS_UPDATE,
                            InteractionConstants.INTERACTION_NOTE_BLANK,
                            InteractionConstants.INTERACTION_RESULT_SYSTEM_UPDATED_LEADTYPE + newType,
                            session().get("sessionUsername"),
                            InteractionConstants.INTERACTION_CHANNEL_SUPPORT_WEBSITE
                    );
                    interaction.save();
                }
                return ok(toJson(newType));
            }
        } catch (NullPointerException n) {
            n.printStackTrace();
        }
        return badRequest();
    }

    /**
     * This API is invoked from the support dashboard when the support executive marks the call status.
     * This results in changing the lead status and creating a new interaction.
     *
     * @param leadId The id of the lead that we tried calling
     * @param leadStatus The current status of this lead
     * @param callStatus Whether call was connected or not. If not, what was the failure reason.
     * @return the Json view of target view (in this case lead status)
     */
    @Security.Authenticated(Secured.class)
    public static Result updateLeadStatus(long leadId, int leadStatus, String callStatus) {

        String interactionResult;
        String interactionNote;

        try {
            Lead lead = Lead.find.where().eq("leadId", leadId).findUnique();
            // A value is for overriding leadStatus is also there in Lead Model setLeadStatus
            if(lead != null){
                if(lead.getLeadStatus() < leadStatus){
                    switch (leadStatus) {
                        case 1: lead.setLeadStatus(ServerConstants.LEAD_STATUS_TTC);
                            break;
                        case 2: lead.setLeadStatus(ServerConstants.LEAD_STATUS_WON);
                            lead.setLeadType(ServerConstants.TYPE_CANDIDATE);
                            break;
                        case 3: lead.setLeadStatus(ServerConstants.LEAD_STATUS_LOST);
                            break;
                    }
                    Logger.info("updateLeadStatus invoked leadId:"+leadId+" status:" + leadStatus);
                    lead.update();
                    interactionNote = InteractionConstants.INTERACTION_NOTE_BLANK;

                } else {
                    interactionNote = InteractionConstants.INTERACTION_NOTE_BLANK;
                }

                // If call was connected just set the right interaction result
                if (callStatus.equals("CONNECTED")) {
                    interactionResult = "Out Bound Call Successfully got connected";
                }
                else {
                    // if call was not connected, set the interaction result and send an sms
                    // to lead/candidate saying we tried reaching
                    interactionResult = "Out Bound Call UnSuccessful : Callee is " + callStatus;

                    if (callStatus.equals(ServerConstants.CALL_STATUS_BUSY)
                            || callStatus.equals(ServerConstants.CALL_STATUS_DND)
                            || callStatus.equals(ServerConstants.CALL_STATUS_NA)
                            || callStatus.equals(ServerConstants.CALL_STATUS_NR)
                            || callStatus.equals(ServerConstants.CALL_STATUS_SWITCHED_OFF)) {

                        SmsUtil.sendTryingToCallSms(lead.getLeadMobile());
                    }
                }

                // save the interaction
                Interaction interaction = new Interaction(
                        lead.getLeadUUId(),
                        lead.getLeadType(),
                        InteractionConstants.INTERACTION_TYPE_LEAD_STATUS_UPDATE,
                        interactionNote,
                        interactionResult,
                        session().get("sessionUsername"),
                        InteractionConstants.INTERACTION_CHANNEL_SUPPORT_WEBSITE
                );
                interaction.save();

                return ok(toJson(lead.getLeadStatus()));
            }
        } catch (NullPointerException n) {
            n.printStackTrace();
        }
        return badRequest();
    }

    public static Result kwCdrInput() {
        return ok("0");
    }

    public static Result getAllNormalJobPosts() {
        List<JobPost> jobPosts = JobPost.find.where().orderBy().asc("source").orderBy().desc("jobPostUpdateTimestamp").findList();
        return ok(toJson(jobPosts));
    }
    public static Result getAllHotJobPosts() {
        List<JobPost> jobPosts = JobPost.find.where().eq("jobPostIsHot", "1").orderBy().asc("source").orderBy().desc("jobPostUpdateTimestamp").findList();
        return ok(toJson(jobPosts));
    }

    @Security.Authenticated(Secured.class)
    public static Result getAllJobPosts() {
        List<JobPost> jobPosts = JobPost.find.where()
                                             .or(eq("source", null), eq("source", ServerConstants.SOURCE_INTERNAL))
                                             .orderBy().desc("jobPostUpdateTimestamp")
                                             .findList();
        return ok(toJson(jobPosts));
    }

    public static Result getAllLocality() {
        List<Locality> localities = Locality.find.setUseQueryCache(!isDevMode).orderBy("localityName").findList();
        return ok(toJson(localities));
    }

    public static Result getAllJobsRolesWithJobs() {
        List<JobRole> jobs = JobRole.find.setUseQueryCache(!isDevMode).orderBy("jobName").findList();
        List<JobRole> jobRolesToReturn = new ArrayList<JobRole>();
        for(JobRole jobRole : jobs){
            List<JobPost> jobPostList = JobPost.find.where().eq("jobRole.jobRoleId",jobRole.getJobRoleId()).findList();
            if(jobPostList.size() > 0){
                jobRolesToReturn.add(jobRole);
            }
        }
        return ok(toJson(jobRolesToReturn));
    }

    public static Result getAllJobs() {
        List<JobRole> jobs = JobRole.find.setUseQueryCache(!isDevMode).orderBy("jobName").findList();
        return ok(toJson(jobs));
    }

    public static Result getAllShift() {
        List<TimeShift> timeShifts = TimeShift.find.setUseQueryCache(!isDevMode).findList();
        return ok(toJson(timeShifts));
    }

    public static Result getAllTransportation() {
        List<TransportationMode> transportationModes = TransportationMode.find.setUseQueryCache(!isDevMode).findList();
        return ok(toJson(transportationModes));
    }

    public static Result getAllEducation() {
        List<Education> educations = Education.find.setUseQueryCache(!isDevMode).findList();
        return ok(toJson(educations));
    }

    public static Result getAllLanguage() {
        List<Language> languages = Language.find.setUseQueryCache(!isDevMode).orderBy("languageName").findList();
        return ok(toJson(languages));
    }

    public static Result getAllIdProof() {
        List<IdProof> idProofs = IdProof.find.setUseQueryCache(!isDevMode).orderBy("idProofName").findList();
        return ok(toJson(idProofs));
    }

    public static Result getAllDegree() {
        List<Degree> degreeList = Degree.find.setUseQueryCache(!isDevMode).orderBy("degreeName").findList();
        return ok(toJson(degreeList));
    }

    @Security.Authenticated(RecSecured.class)
    public static Result getAllCompany() {
        List<Company> companyList = Company.find.where()
                .or(eq("source", null), eq("source", ServerConstants.SOURCE_INTERNAL))
                .orderBy("companyName").findList();
        return ok(toJson(companyList));
    }

    @Security.Authenticated(RecSecured.class)
    public static Result getAllRecruiters() {
        List<RecruiterProfile> recruiterProfileList = RecruiterProfile.find.findList();
        return ok(toJson(recruiterProfileList));
    }

    @Security.Authenticated(RecSecured.class)
    public static Result getAllPricingPlans() {
        List<PricingPlanType> pricingPlanTypeList = PricingPlanType.find.findList();
        return ok(toJson(pricingPlanTypeList));
    }

    @Security.Authenticated(RecSecured.class)
    public static Result getAllExperience() {
        List<Experience> experienceList = Experience.find.setUseQueryCache(!isDevMode).findList();
        return ok(toJson(experienceList));
    }

    @Security.Authenticated(RecSecured.class)
    public static Result getAllCompanyStatus() {
        List<CompanyStatus> companyStatusList = CompanyStatus.find.findList();
        return ok(toJson(companyStatusList));
    }

    @Security.Authenticated(RecSecured.class)
    public static Result getAllCompanyType() {
        List<CompanyType> companyTypeList = CompanyType.find.findList();
        return ok(toJson(companyTypeList));
    }

    @Security.Authenticated(RecSecured.class)
    public static Result getAllJobStatus() {
        List<JobStatus> jobStatusList = JobStatus.find.findList();
        return ok(toJson(jobStatusList));
    }

    @Security.Authenticated(PartnerSecured.class)
    public static Result candidateSignupSupport(Long candidateId, String isCallTrigger) {
        return ok(views.html.signup_support.render(candidateId, isCallTrigger));
    }

    @Security.Authenticated(RecSecured.class)
    public static Result createCompany() {
        return ok(views.html.Recs.create_company.render());
    }

    @Security.Authenticated(PartnerSecured.class)
    public static Result createCandidateForm() {
        return redirect("/candidateSignupSupport/0/false");
    }

    @Security.Authenticated(RecSecured.class)
    public static Result searchCandidate() {
        return ok(views.html.search.render());
    }

    @Security.Authenticated(Secured.class)
    public static Result getSearchCandidateResult() {
        JsonNode searchReq = request().body().asJson();
        if(searchReq == null) {
            return badRequest();
        }

        SearchCandidateRequest searchCandidateRequest = new SearchCandidateRequest();
        ObjectMapper newMapper = new ObjectMapper();

        try {
            searchCandidateRequest = newMapper.readValue(searchReq.toString(), SearchCandidateRequest.class);
        } catch (IOException e) {
            e.printStackTrace();
        }

        String sessionId = session().get("sessionId");
        Developer developer = null;

        if(sessionId != null) {
            developer = Developer.find.where().eq("developerSessionId", sessionId).findUnique();
        }

        JsonNode resp = toJson(SupportSearchService.searchCandidateBySupport(searchCandidateRequest, developer));

        return ok(resp);
    }

    @Security.Authenticated(PartnerSecured.class)
    public static Result getAllLeadSource() {
        List<LeadSource> leadSources = LeadSource.find.orderBy("leadSourceName").findList();
        return ok(toJson(leadSources));
    }
    @Security.Authenticated(PartnerSecured.class)
    public static Result getSupportAgent() {

        String agentMobile = "+91" + session().get("sessionUserId");
        String accessLevel = session().get("sessionRDPK");

        SupportAgentInfo agentInfo = new SupportAgentInfo();
        agentInfo.setAgentAccessLevel(accessLevel);

        if(agentMobile.length() == 13){
            agentInfo.setAgentMobileNumber(agentMobile);
        }

        return ok(toJson(agentInfo));
    }

    @Security.Authenticated(Secured.class)
    public static Result addOrUpdateFollowUp() {
        JsonNode followUp = request().body().asJson();
        if(followUp == null){
            return badRequest();
        }
        AddOrUpdateFollowUpRequest addOrUpdateFollowUpRequest = new AddOrUpdateFollowUpRequest();
        ObjectMapper newMapper = new ObjectMapper();
        try {
            addOrUpdateFollowUpRequest = newMapper.readValue(followUp.toString(), AddOrUpdateFollowUpRequest.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Logger.info("addOrUpdateFollowUp: " + addOrUpdateFollowUpRequest.getLeadMobile() + " createTimeStamp: " + addOrUpdateFollowUpRequest.getFollowUpDateTime());
        return ok(toJson(FollowUpService.CreateOrUpdateFollowUp(addOrUpdateFollowUpRequest)));
    }

    @Security.Authenticated(PartnerSecured.class)
    public static Result getInteractionNote(Long leadId, Long limit) {
        Lead lead = Lead.find.where().eq("leadId",leadId).findUnique();
        if(lead !=null){
            List<Interaction> fullInteractionList = Interaction.find.where()
                    .eq("objectAUUId", lead.getLeadUUId())
                    .ne("note", "")
                    .findList();

            // fetch candidate interaction as well
            Candidate candidate = Candidate.find.where().eq("lead_leadId", leadId).findUnique();
            if(candidate != null){
                List<Interaction> candidateInteractionList = Interaction.find.where()
                        .eq("objectAUUId", candidate.getCandidateUUId())
                        .ne("note", "")
                        .findList();
                fullInteractionList.addAll(candidateInteractionList);
            }

            List<SupportInteractionNoteResponse> responses = new ArrayList<>();

            SimpleDateFormat sfd = new SimpleDateFormat(ServerConstants.SDF_FORMAT_FOLLOWUP);
            //latest timestamp on top
            Collections.sort(fullInteractionList,  (o1, o2) -> o2.getCreationTimestamp().compareTo(o1.getCreationTimestamp()));
            int lastTenRecords = 0;
            for(Interaction interaction : fullInteractionList){
                if(interaction.getNote() != null){
                    lastTenRecords++;
                    SupportInteractionNoteResponse response = new SupportInteractionNoteResponse();
                    response.setInteractionId(interaction.getId());
                    response.setUserInteractionTimestamp(sfd.format(interaction.getCreationTimestamp()));
                    response.setUserNote(interaction.getNote());
                    responses.add(response);
                }
                if(lastTenRecords >= limit){
                    break;
                }
            }
            return ok(toJson(responses));
        }
        else
            return ok("no records");
    }

    public static Result getAllJobExpQuestion() {
        List<JobExpQuestion> jobExpQuestionList = JobExpQuestion.find.all();
        return ok(toJson(jobExpQuestionList));
    }

    public static Result getJobExpQuestion(String jobRoleIds) {
        List<String> jobRoleIdList = Arrays.asList(jobRoleIds.split("\\s*,\\s*"));
        Query<JobExpQuestion> query = JobExpQuestion.find.query();
        query = query.select("*").fetch("jobRole")
                .where()
                .in("jobRole.jobRoleId", jobRoleIdList)
                .query();
        List<JobExpQuestion> response = query.findList();
        if(response != null){
            return ok(toJson(response));
        }
        return ok();
    }


    @Security.Authenticated(AdminSecured.class)
    public static Result invalidateDbCache() {
        ServerCacheManager serverCacheManager = Ebean.getServerCacheManager();
        serverCacheManager.clearAll();
        return ok("Cleared Static Cache");
    }

    @Security.Authenticated(SuperAdminSecured.class)
    public static Result removeDuplicateLeadOrCandidate(String mobile) {
        mobile = FormValidator.convertToIndianMobileFormat(mobile);
        if(mobile != null  && mobile.length() == 13 ){
            if(DeleteService.DeleteLeadServiceButPreserveOne(mobile) == null){
                return ok("Given Mobile number "+mobile+" was not found in DB");
            }
            String sessionUser = session().get("sessionUsername");
            Logger.info("A duplicated data delete action has been executed by " + sessionUser + " for mobile number" + mobile);
            SmsUtil.sendDuplicateLeadOrCandidateDeleteActionSmsToDevTeam(mobile);
            return ok("Duplicate removal operation for Mobile number:"+mobile+" has been successfully completed");
        }
        return ok("Invalid Mobile number !!");
    }

    @Security.Authenticated(SuperAdminSecured.class)
    public static Result administrator() {
        return ok(views.html.admin.render());
    }

    @Security.Authenticated(RecSecured.class)
    public static Result uploadCSV() {
        return ok(views.html.uploadcsv.render());
    }

    @Security.Authenticated(PartnerSecured.class)
    public static Result ifExists(String mobile) {
        if(mobile != null){
            mobile = FormValidator.convertToIndianMobileFormat(mobile);
            Candidate existingCandidate = CandidateService.isCandidateExists(mobile);
            if(existingCandidate != null) {
                return ok(toJson(existingCandidate.getLead().getLeadId()));
            }
        }
        return ok("0");
    }

    @Security.Authenticated(SecuredUser.class)
    public static Result ifCandidateExists(String mobile) {
        if(mobile != null){
            mobile = FormValidator.convertToIndianMobileFormat(mobile);
            Candidate existingCandidate = CandidateService.isCandidateExists(mobile);
            if(existingCandidate != null) {
                return ok("1");
            }
        }
        return ok("0");
    }

    @Security.Authenticated(PartnerSecured.class)
    public static Result getAllDeactivationReason() {
        List<Reason> deactivationReasons = Reason.find.all();
        return ok(toJson(deactivationReasons));
    }

    @Security.Authenticated(SuperSecured.class)
    public static Result getDeactivatedCandidateList() {
        JsonNode deactivatedCandidateJsonNode = request().body().asJson();
        if(deactivatedCandidateJsonNode == null){
            return badRequest();
        }

        DeactivatedCandidateRequest deactivatedCandidateRequest = new DeactivatedCandidateRequest();
        ObjectMapper newMapper = new ObjectMapper();
        Logger.info("deactivatedCandidateJsonNode: "+deactivatedCandidateJsonNode);
        try {
            deactivatedCandidateRequest = newMapper.readValue(deactivatedCandidateJsonNode.toString(), DeactivatedCandidateRequest.class);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return ok(toJson(DeactivationService.getDeactivatedCandidates(deactivatedCandidateRequest)));
    }

    public static Result deactiveToActive() {
        JsonNode deactiveToActiveJson = request().body().asJson();
        if(deactiveToActiveJson == null){
            return badRequest();
        }

        DeactiveToActiveRequest deactiveToActiveRequest= new DeactiveToActiveRequest();
        ObjectMapper newMapper = new ObjectMapper();
        Logger.info("deactivatedCandidateJsonNode: "+deactiveToActiveJson);
        try {
            deactiveToActiveRequest = newMapper.readValue(deactiveToActiveJson.toString(), DeactiveToActiveRequest.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ok(toJson(DeactivationService.deactivateToActive(deactiveToActiveRequest)));
    }

    public static Result postJob() {
        return redirect("http://goo.gl/Dpsvcn");
    }
    public static Result renderPageNavBar() {
        return ok(views.html.Fragment.nav_bar.render());
    }
    public static Result renderPageNavBarLoggedIn() {
        return ok(views.html.Fragment.nav_bar_logged_in.render());
    }
    public static Result renderGAScript() { return ok(views.html.Fragment.script.render()); }
    public static Result renderPageFooter() {
        return ok(views.html.Fragment.footer.render());
    }
    public static Result renderJobPostCards() { return ok(views.html.Fragment.hot_jobs_card_view.render());}
    public static Result renderShowAllJobs() { return ok(views.html.Fragment.show_all_jobs_page.render());}
    public static Result renderJobPostDetails(String jobTitle, String jobLocation, String jobCompany, long jobId) {
        return ok(views.html.Fragment.posted_job_details.render(jobCompany,jobTitle));
    }

    public static Result getJobPostDetails(String jobTitle, String jobLocation, String jobCompany, long jobId) {
        JobPost jobPost = JobPost.find.where().eq("JobPostId",jobId).findUnique();
        if (jobPost != null) {
            return ok(toJson(jobPost));
        }
        return ok("Error");
    }
    public static Result renderJobRoleJobPage(String rolePara, Long idPara) {
        return ok(views.html.Fragment.job_role_page.render(rolePara));
    }

    public static Result getJobRoleWiseJobPosts(String rolePara, Long idPara) {
        List<JobPost> jobPostList = JobPost.find.where().eq("jobRole.jobRoleId",idPara).orderBy().asc("source").orderBy().desc("jobPostUpdateTimestamp").findList();
        return ok(toJson(jobPostList));
    }

    public static Result getAllCompanyLogos() {
        List<Company> companyList = Company.find.where()
                .or(eq("source", null), eq("source", ServerConstants.SOURCE_INTERNAL))
                .orderBy("companyName").findList();
        return ok(toJson(companyList));
    }

    @Security.Authenticated(SuperAdminSecured.class)
    public static Result scrapArena() {
        return ok(views.html.ScrapArena.render());
    }

    public static Result checkCandidateSession() {
        String sessionCandidateId = session().get("candidateId");
        if(sessionCandidateId != null) {
            Auth existingAuth = Auth.find.where().eq("candidateId", sessionCandidateId).findUnique();
            if(existingAuth != null){
                if(existingAuth.getAuthStatus() == 1){
                    return ok("1");
                } else{
                    return ok("0"); //auth is not verified
                }
            }
            return ok("1");
        } else{
            return ok("0");
        }
    }

    @Security.Authenticated(SecuredUser.class)
    public static Result getAssessmentQuestion(String jobRoleIds, String jobPostIds, Integer limit) {

        /*
        *  Since the flow is such that assessment is triggered only if user is logged in
        *  and if jobroleid is null then jobpostid is used to resolve jobroleid and then passed to getQuestion
        *
        */
        if(session().get("candidateId") != null){
            Long candidateId = Long.parseLong(session().get("candidateId"));
            List<Long> jobRoleIdList = new ArrayList<>();
            if(jobRoleIds != null){
                List<String> jobRoleIdStrList = Arrays.asList(jobRoleIds.split("\\s*,\\s*"));
                if (jobRoleIdStrList.size() > 0){
                    for (String roleId: jobRoleIdStrList) {
                        jobRoleIdList.add(Long.parseLong(roleId));
                    }
                }
            } else {
                if(jobPostIds != null) {
                    List<String> jobPostIdStrList = Arrays.asList(jobPostIds.split("\\s*,\\s*"));
                    List<JobPost> jobPostList = JobPost.find.where().in("jobPostId", jobPostIdStrList).findList();
                    for(JobPost jobPost : jobPostList) {
                        jobRoleIdList.add(jobPost.getJobRole().getJobRoleId());
                    }
                } else {
                    Candidate candidate = Candidate.find.where().eq("candidateId", candidateId).findUnique();
                    for(JobPreference jobPreference : candidate.getJobPreferencesList()){
                        jobRoleIdList.add(jobPreference.getJobRole().getJobRoleId());
                    }
                    List<AssessmentQuestion> assessmentQuestionList = AssessmentQuestion.find.where().in("jobRoleId", jobRoleIdList).findList();
                    if (assessmentQuestionList.size() > 0) {
                        jobRoleIdList = new ArrayList<>();
                        for(AssessmentQuestion assessmentQuestion: assessmentQuestionList) {
                            if(!jobRoleIdList.contains(assessmentQuestion.getJobRole().getJobRoleId())){
                                jobRoleIdList.add(assessmentQuestion.getJobRole().getJobRoleId());
                            }
                        }
                    } else {
                        return ok("assessed");
                    }

                }
            }

            List<CandidateAssessmentAttempt> candidateAssessmentAttemptList = CandidateAssessmentAttempt.find.where()
                    .eq("candidate.candidateId", candidateId)
                    .in("jobRole.jobRoleId", jobRoleIdList)
                    .findList();
            if (candidateAssessmentAttemptList != null && jobRoleIdList.size() > 0 && candidateAssessmentAttemptList.size() == jobRoleIdList.size()) {
                Logger.info("already assessed");
                return ok("assessed");
            } else {
                // filter out all jobroles out of job prefs which are not attempted
                List<Long> assessedJobRoleIdList = new ArrayList<>();
                for (CandidateAssessmentAttempt caRes : candidateAssessmentAttemptList){
                    if(jobRoleIdList.contains(caRes.getJobRole().getJobRoleId())){
                        assessedJobRoleIdList.add(caRes.getJobRole().getJobRoleId());
                    }
                }
                jobRoleIdList.removeAll(assessedJobRoleIdList);
            }

            List<AssessmentQuestion> assessmentQuestionList = AssessmentService.getQuestions(jobRoleIdList);

            if(assessmentQuestionList.size() > 0){
                return ok(toJson(assessmentQuestionList));
            }
        }
        return ok("NA");
    }

    @Security.Authenticated(SecuredUser.class)
    public static Result submitAssessment() {
        JsonNode assessmentRequestJson = request().body().asJson();
        if(assessmentRequestJson == null){
            return badRequest();
        }
        AssessmentRequest assessmentRequest= new AssessmentRequest();
        ObjectMapper newMapper = new ObjectMapper();
        try {
            assessmentRequest = newMapper.readValue(assessmentRequestJson.toString(), AssessmentRequest.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ok(toJson(AssessmentService.addAssessedInfoToGS(assessmentRequest, Long.parseLong(session().get("candidateId")))));
    }

    public static Result checkNavBar() {
        if (session().get("partnerId") != null) {
            // partner logged in
            return ok("2");
        } else if (session().get("sessionId") != null) {
            // candidate logged in
            return ok("1");
        } else {
            return ok("0");
        }
    }

    public static Result getJobPostInfoViaPartner(long jobPostId, long candidateId) {
        JobPost jobPost = JobPost.find.where().eq("jobPostId", jobPostId).findUnique();
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
}
