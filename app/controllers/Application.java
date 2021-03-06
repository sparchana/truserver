package controllers;

import api.InteractionConstants;
import api.ServerConstants;
import api.http.FormValidator;
import api.http.httpRequest.*;
import api.http.httpRequest.Recruiter.AddRecruiterRequest;
import api.http.httpRequest.Recruiter.RecruiterSignUpRequest;
import api.http.httpRequest.Workflow.InterviewDateTime.AddCandidateInterviewSlotDetail;
import api.http.httpRequest.Workflow.MatchingCandidateRequest;
import api.http.httpRequest.Workflow.PreScreenRequest;
import api.http.httpRequest.Workflow.SelectedCandidateRequest;
import api.http.httpRequest.Workflow.applyInshort.ApplyInShortRequest;
import api.http.httpRequest.Workflow.preScreenEdit.*;
import api.http.httpResponse.*;
import api.http.httpResponse.hirewand.HireWandResponse;
import api.http.httpResponse.hirewand.UploadResumeResponse;
import api.http.httpResponse.Workflow.smsJobApplyFlow.PostApplyInShortResponse;
import api.http.httpResponse.Workflow.InterviewSlotPopulateResponse;
import api.http.httpResponse.interview.InterviewResponse;
import api.http.httpResponse.Recruiter.JobPostFilterResponse;
import com.amazonaws.util.json.JSONException;
import com.avaje.ebean.Ebean;
import com.avaje.ebean.Query;
import com.avaje.ebean.cache.ServerCacheManager;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import controllers.AnalyticsLogic.GlobalAnalyticsService;
import controllers.AnalyticsLogic.JobRelevancyEngine;
import controllers.businessLogic.Assessment.AssessmentService;
import controllers.businessLogic.*;
import controllers.businessLogic.JobWorkflow.JobPostWorkflowEngine;
import controllers.businessLogic.hirewand.HireWandService;
import controllers.security.*;
import dao.CandidateDAO;
import dao.CompanyDAO;
import dao.JobPostDAO;
import dao.JobPostWorkFlowDAO;
import dao.staticdao.RejectReasonDAO;
import models.entity.*;
import models.entity.Intelligence.RelatedJobRole;
import models.entity.OM.CandidateResume;
import models.entity.OM.JobApplication;
import models.entity.OM.JobPreference;
import models.entity.OM.JobToSkill;
import models.entity.OM.*;
import models.entity.Recruiter.RecruiterProfile;
import models.entity.Static.*;
import models.util.ParseCSV;
import models.util.SmsUtil;
import models.util.UrlValidatorUtil;
import models.util.Util;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;
import org.json.simple.JSONArray;
import play.Logger;
import play.api.Play;
import play.data.Form;
import play.mvc.*;

import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

import static api.InteractionConstants.INTERACTION_CHANNEL_CANDIDATE_WEBSITE;
import static api.InteractionConstants.INTERACTION_CHANNEL_SUPPORT_WEBSITE;
import static api.InteractionConstants.INTERACTION_TYPE_MAP;
import static com.avaje.ebean.Expr.eq;
import static controllers.PartnerController.checkCandidateExistence;
import static play.libs.Json.toJson;

@With(ForceHttps.class)
public class Application extends Controller {

    private static boolean isDevMode = Play.isDev(Play.current()) || Play.isTest(Play.current());

    public static Result index() {
        String sessionId = session().get("sessionId");
        /**
        * TODO need to change this, modify old partnerSecured to take new partnerFlow into consideration and properly annotate rest of the api end-points
        * */
        if(sessionId != null){
            String partnerId = session().get("partnerId");
            String recruiterId = session().get("recruiterId");

            if (!FlashSessionController.isEmpty()) {
                return redirect(FlashSessionController.getFlashFromSession());
            }

            if(partnerId != null){
                return redirect("/partner/home");
            } else if(recruiterId != null){
                return redirect("/recruiter/home");
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

    @Security.Authenticated(PartnerInternalSecured.class)
    public static Result support() {
        return ok(views.html.support.render());
    }

    @Security.Authenticated(PartnerInternalSecured.class)
    public static Result candidateInteraction(long id) {
        return ok(views.html.candidate_interaction.render());
    }

    public static Result privacy() {
        return ok(views.html.privacy.render());
    }

    @Security.Authenticated(Secured.class)
    public static Result getCandidateInteraction(long id) {
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
                response.setUserInteractionType(InteractionConstants.INTERACTION_TYPE_MAP.get(interaction.getInteractionType()));
                response.setChannel(InteractionConstants.INTERACTION_CHANNEL_MAP.get(interaction.getInteractionChannel()));

                responses.add(response);
            }
            return ok(toJson(responses));
        }
        else
            return ok("no records");
    }

    public static Result addLead() {
        JsonNode req = request().body().asJson();
        Logger.info("Browser: " +  request().getHeader("User-Agent") + "; Req JSON : " + req );
        AddLeadRequest addLeadRequest = new AddLeadRequest();
        ObjectMapper newMapper = new ObjectMapper();
        try {
            addLeadRequest = newMapper.readValue(req.toString(), AddLeadRequest.class);
        } catch (IOException e) {
            e.printStackTrace();
        }

        AddLeadResponse addLeadResponse = new AddLeadResponse();
        Lead lead = new Lead(addLeadRequest.getLeadName(),
                addLeadRequest.getLeadMobile(),
                addLeadRequest.getLeadChannel(),
                ServerConstants.TYPE_LEAD,
                ServerConstants.LEAD_SOURCE_UNKNOWN
        );
        lead.setLeadType(addLeadRequest.getLeadType());
        LeadService.createLead(lead, InteractionConstants.INTERACTION_CHANNEL_CANDIDATE_WEBSITE);
        addLeadResponse.setStatus(AddLeadResponse.STATUS_SUCCESS);
        return ok(toJson(addLeadResponse));
    }

    public static Result signUp() {
        JsonNode req = request().body().asJson();
        Logger.info("Browser: " +  request().getHeader("User-Agent") + "; Req JSON : " + req );
        CandidateSignUpRequest candidateSignUpRequest = new CandidateSignUpRequest();
        ObjectMapper newMapper = new ObjectMapper();
        try {
            candidateSignUpRequest = newMapper.readValue(req.toString(), CandidateSignUpRequest.class);
        } catch (IOException e) {
            e.printStackTrace();
        }

        int channelType = INTERACTION_CHANNEL_CANDIDATE_WEBSITE;
        return ok(toJson(CandidateService.signUpCandidate(candidateSignUpRequest, channelType, ServerConstants.LEAD_SOURCE_UNKNOWN)));
    }
    @Security.Authenticated(PartnerInternalSecured.class)
    public static Result signUpSupport() {
        JsonNode req = request().body().asJson();
        Logger.info("Browser: " +  request().getHeader("User-Agent") + "; Req JSON : " + req );
        AddSupportCandidateRequest addSupportCandidateRequest = new AddSupportCandidateRequest();
        ObjectMapper newMapper = new ObjectMapper();

        // since jsonReq has single/multiple values in array
        newMapper.enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY);
        try {
            addSupportCandidateRequest = newMapper.readValue(req.toString(), AddSupportCandidateRequest.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ok(toJson(CandidateService.createCandidateProfile(addSupportCandidateRequest,
                INTERACTION_CHANNEL_SUPPORT_WEBSITE,
                ServerConstants.UPDATE_ALL_BY_SUPPORT)));
    }

    public static Result candidateUpdateBasicProfile() {
        JsonNode req = request().body().asJson();
        Logger.info("Browser: " +  request().getHeader("User-Agent") + "; Req JSON : " + req );

        AddCandidateRequest addCandidateRequest = new AddCandidateRequest();
        ObjectMapper newMapper = new ObjectMapper();
        try {
            addCandidateRequest = newMapper.readValue(req.toString(), AddCandidateRequest.class);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return ok(toJson(CandidateService.createCandidateProfile(addCandidateRequest, INTERACTION_CHANNEL_CANDIDATE_WEBSITE, ServerConstants.UPDATE_BASIC_PROFILE)));
    }

    public static Result candidateUpdateExperienceDetails() {
        JsonNode req = request().body().asJson();
        Logger.info("Browser: " +  request().getHeader("User-Agent") + "; Req JSON : " + req );
        AddCandidateExperienceRequest addCandidateExperienceRequest = new AddCandidateExperienceRequest();
        ObjectMapper newMapper = new ObjectMapper();
        try {
            addCandidateExperienceRequest = newMapper.readValue(req.toString(), AddCandidateExperienceRequest.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ok(toJson(CandidateService.createCandidateProfile(addCandidateExperienceRequest, INTERACTION_CHANNEL_CANDIDATE_WEBSITE, ServerConstants.UPDATE_SKILLS_PROFILE)));
    }

    public static Result candidateUpdateEducationDetails() {
        JsonNode req = request().body().asJson();
        Logger.info("Browser: " +  request().getHeader("User-Agent") + "; Req JSON : " + req );
        AddCandidateEducationRequest addCandidateEducationRequest = new AddCandidateEducationRequest();
        ObjectMapper newMapper = new ObjectMapper();
        try {
            addCandidateEducationRequest = newMapper.readValue(req.toString(), AddCandidateEducationRequest.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ok(toJson(CandidateService.createCandidateProfile(addCandidateEducationRequest, INTERACTION_CHANNEL_CANDIDATE_WEBSITE, ServerConstants.UPDATE_EDUCATION_PROFILE)));
    }

    public static Result addPassword() {
        JsonNode req = request().body().asJson();
        Logger.info("Browser: " +  request().getHeader("User-Agent") + "; save password api json");
        CandidateSignUpRequest candidateSignUpRequest = new CandidateSignUpRequest();
        ObjectMapper newMapper = new ObjectMapper();
        try {
            candidateSignUpRequest = newMapper.readValue(req.toString(), CandidateSignUpRequest.class);
        } catch (IOException e) {
            e.printStackTrace();
        }

        String userMobile = candidateSignUpRequest.getCandidateAuthMobile();
        String userPassword = candidateSignUpRequest.getCandidatePassword();

        return ok(toJson(AuthService.savePassword(userMobile, userPassword, INTERACTION_CHANNEL_CANDIDATE_WEBSITE)));
    }

    @Security.Authenticated(SecuredUser.class)
    public static Result applyJob() throws IOException, JSONException {
        JsonNode req = request().body().asJson();
        Logger.info("Browser: " +  request().getHeader("User-Agent") + "; Req JSON : " + req );
        ApplyJobRequest applyJobRequest = new ApplyJobRequest();
        ObjectMapper newMapper = new ObjectMapper();
        try {
            applyJobRequest = newMapper.readValue(req.toString(), ApplyJobRequest.class);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if(session().get("sessionChannel") != null || !session().get("sessionChannel").isEmpty()){
            Integer channelId = Integer.parseInt(session().get("sessionChannel"));
            int channelType = channelId == null ? InteractionConstants.INTERACTION_CHANNEL_UNKNOWN : channelId;
            return ok(toJson(JobService.applyJob(applyJobRequest, channelType, InteractionConstants.INTERACTION_TYPE_APPLIED_JOB, true)));
        } else {
            return badRequest();
        }
    }

    @Security.Authenticated(SecuredUser.class)
    public static Result addJobPost() {
        JsonNode req = request().body().asJson();
        Logger.info("Browser: " +  request().getHeader("User-Agent") + "; Req JSON : " + req );
        AddJobPostRequest addJobPostRequest = new AddJobPostRequest();
        ObjectMapper newMapper = new ObjectMapper();
        newMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        try {
            addJobPostRequest = newMapper.readValue(req.toString(), AddJobPostRequest.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ok(toJson(JobService.addJobPost(addJobPostRequest, InteractionConstants.INTERACTION_CHANNEL_SUPPORT_WEBSITE)));
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
        return ok(toJson(RecruiterService.createRecruiterProfile(recruiterSignUpRequest, InteractionConstants.INTERACTION_CHANNEL_SUPPORT_WEBSITE)));
    }

    @Security.Authenticated(SecuredUser.class)
    public static Result addCompany() {
        JsonNode req = request().body().asJson();
        Logger.info("Browser: " +  request().getHeader("User-Agent") + "; Req JSON : " + req );
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
        Logger.info("Browser: " +  request().getHeader("User-Agent") + "; Login api json ");
        LoginRequest loginRequest = new LoginRequest();
        ObjectMapper newMapper = new ObjectMapper();
        try {
            loginRequest = newMapper.readValue(req.toString(), LoginRequest.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        String loginMobile = loginRequest.getCandidateLoginMobile();
        String loginPassword = loginRequest.getCandidateLoginPassword();
        return ok(toJson(CandidateService.login(loginMobile, loginPassword, InteractionConstants.INTERACTION_CHANNEL_CANDIDATE_WEBSITE)));
    }

    @Security.Authenticated(SecuredUser.class)
    public static Result dashboard() {
        if (!FlashSessionController.isEmpty()) {
            return redirect(FlashSessionController.getFlashFromSession());
        }
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
        Logger.info("Browser: " +  request().getHeader("User-Agent") + "; Req JSON : " + req );
        ResetPasswordResquest resetPasswordResquest = new ResetPasswordResquest();
        ObjectMapper newMapper = new ObjectMapper();
        try {
            resetPasswordResquest = newMapper.readValue(req.toString(), ResetPasswordResquest.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        String candidateMobile = resetPasswordResquest.getResetPasswordMobile();

        return ok(toJson(CandidateService.findUserAndSendOtp(candidateMobile, InteractionConstants.INTERACTION_CHANNEL_CANDIDATE_WEBSITE)));
    }

    public static Result processcsv() {
        java.io.File file = (File) request().body().asMultipartFormData().getFile("file").getFile();
        if(file == null) {
            return badRequest("error uploading file. Check file type");
        }
        return ok(toJson(ParseCSV.parseCSV(file)));
    }

    public static Result uploadLogo() {
        Http.MultipartFormData body = request().body().asMultipartFormData();
        Http.MultipartFormData.FilePart companyLogo = body.getFile("file");

        if (companyLogo != null) {
            String fileName = companyLogo.getFilename();

            File file = (File) companyLogo.getFile();
            Logger.info("uploaded! " + fileName);
            CompanyService.uploadCompanyLogo(file, fileName);

            List<String> companyId = Arrays.asList(fileName.split("\\s*_\\s*"));
            if(companyId.get(1) != null){
                Company company = Company.find.where().eq("CompanyId", companyId.get(1)).findUnique();
                if(company != null){
                    company.setCompanyLogo("https://s3.amazonaws.com/trujobs.in/companyLogos/" + fileName);
                    company.update();
                }
            }
            return ok("File uploaded");


        } else {
            flash("error", "Missing file");
            return redirect(routes.Application.index());
        }
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

        // query all interactions of all leads which are of type call (channel/createdby = knowlarity) and order
        // the list by most recent interaction on top
        List<Interaction> allInteractions = Interaction.find.where()
                .in("objectAUUId", leadUUIDList)
                .or(eq("createdBy", InteractionConstants.INTERACTION_CREATED_SYSTEM_KNOWLARITY),
                        eq("interactionChannel", InteractionConstants.INTERACTION_CHANNEL_KNOWLARITY))
                .orderBy().desc("creationTimestamp")
                .findList();

        Map<String, ArrayList<Interaction>> objAUUIDToInteractions = new HashMap<String, ArrayList<Interaction>>();

        // iterate on the entire interactions list and create a mapping between objectauuid (leadid) and
        // its corressponding interactions
        for (Interaction i : allInteractions) {

            String objectAUUID = i.getObjectAUUId();
            ArrayList<Interaction> interactionsOfLead = objAUUIDToInteractions.get(objectAUUID);

            if (interactionsOfLead == null) {
                interactionsOfLead = new ArrayList<Interaction>();
                objAUUIDToInteractions.put(objectAUUID, interactionsOfLead);
            }
            interactionsOfLead.add(i);
        }

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

            int mTotalInteraction = 0;
            Timestamp mostRecent = lead.getLeadCreationTimestamp();

            ArrayList<Interaction> iList = objAUUIDToInteractions.get(lead.getLeadUUId());
            if (iList != null && !iList.isEmpty()) {
                mTotalInteraction = iList.size();
                // we have ordered the list by most recent on top. hence check only the top element
                Timestamp recentInteraction = iList.get(0).getCreationTimestamp();
                mostRecent =  recentInteraction.getTime() >= mostRecent.getTime() ? recentInteraction : mostRecent;
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
    @Security.Authenticated(PartnerInternalSecured.class)
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
            candidate.getCandidateResumeLink();
            if(candidate!=null){
                return ok(toJson(candidate));
            }
        }
        return ok("0");
    }

    /* this method is used by support */
    @Security.Authenticated(PartnerInternalSecured.class)
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
        JobPost jobPost = JobPostDAO.findById(jobPostId);
        SearchJobService searchJobService = new SearchJobService();
        Long candidateId = null;

        if(jobPost!=null){
            if(isSupport == 0){
                String interactionResult = InteractionConstants.INTERACTION_RESULT_CANDIDATE_TRIED_TO_APPLY_JOB;
                String objAUUID = "";
                if(session().get("candidateId") != null){
                    Candidate candidate = Candidate.find.where().eq("candidateId", session().get("candidateId")). findUnique();
                    if(candidate == null) {
                        return badRequest();
                    }
                    candidateId = candidate.getCandidateId();
                    objAUUID = candidate.getCandidateUUId();
                }
                InteractionService.createInteractionForJobApplicationAttemptViaWebsite(
                        objAUUID,
                        jobPost.getJobPostUUId(),
                        interactionResult + jobPost.getJobPostTitle() + " at " + jobPost.getCompany().getCompanyName()
                );
            }

            searchJobService.computeCTA(jobPost, candidateId);
            return ok(toJson(jobPost));
        }
        return ok("0");
    }

    @Security.Authenticated(SecuredUser.class)
    public static Result getCandidateJobApplication() {
        if(session().get("candidateId") != null) {
            Long candidateId = Long.parseLong(session().get("candidateId"));
            return ok(toJson(new JobPostWorkFlowDAO().candidateAppliedJobs(candidateId)));
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
        FlashSessionController.clearSessionExceptFlash();

        flash("success", "You've been logged out");
        return redirect(
                routes.Application.supportAuth()
        );
    }

    public static Result logoutUser() {
        FlashSessionController.clearSessionExceptFlash();

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
                session("sessionChannel", String.valueOf(InteractionConstants.INTERACTION_CHANNEL_SUPPORT_WEBSITE));

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

    public static Result getAllNormalJobPosts(Long index) {
        return ok(toJson(JobSearchService.getAllActiveJobsPaginated(index)));
    }

    public static Result getPartnerViewJobs(Long index) {
        String sessionPartnerId = session().get("partnerId");
        if(sessionPartnerId != null){
            Partner partner = Partner.find.where().eq("partner_id", session().get("partnerId")).findUnique();
            if(partner != null) {
                if(partner.getPartnerType().getPartnerTypeId() == ServerConstants.PARTNER_TYPE_PRIVATE){
//                    return ok(toJson(JobSearchService.getAllPrivateJobsOfCompany(index, partner.getCompany())));
                }
            }
        }

        return ok(toJson(JobSearchService.getAllActiveJobsPaginated(index)));
    }

    public static Result getAllHotJobPosts(Integer index, Integer sessionSalt) {
        return ok(toJson(JobSearchService.getAllHotJobsPaginated(index, sessionSalt)));
    }

    @Security.Authenticated(Secured.class)
    public static Result getAllJobPosts() {
        List<JobPost> jobPosts = JobPost.find.where()
                .or(eq("source", null), eq("source", ServerConstants.SOURCE_INTERNAL))
                .orderBy().desc("jobPostUpdateTimestamp")
                .findList();

        // get all jobpost uuids
        List<String> jobpostUUIDs = new ArrayList<>();
        for (JobPost jobPost : jobPosts) {
            jobpostUUIDs.add(jobPost.getJobPostUUId());
        }

        // Query interactions table to get who created this job post
        Map <?, Interaction> jobPostCreatedInteractionMap =
                Interaction.find.where().eq("interactionType", InteractionConstants.INTERACTION_TYPE_NEW_JOB_CREATED)
                        .in("objectBUUId", jobpostUUIDs).setMapKey("objectBUUId").findMap();

        List<RecDashboardResponse> jobList = new ArrayList<>();

        //setting list objects
        for (JobPost jobPost : jobPosts) {
            RecDashboardResponse response = new RecDashboardResponse();

            response.setJobPostId(Math.toIntExact(jobPost.getJobPostId()));
            response.setCreationTimeStamp(jobPost.getJobPostCreateTimestamp());

            //company
            if(jobPost.getCompany() != null){
                response.setCompanyId(Math.toIntExact(jobPost.getCompany().getCompanyId()));
                response.setCompanyName(jobPost.getCompany().getCompanyName());
            }
            response.setJobTitle(jobPost.getJobPostTitle());

            //if its a private job
            if(jobPost.getJobPostAccessLevel() == ServerConstants.JOB_POST_TYPE_PRIVATE){
                response.setJobTitle("[Private Job] " + jobPost.getJobPostTitle());
            }

            //salary
            response.setSalary(String.valueOf(jobPost.getJobPostMinSalary()));
            if(jobPost.getJobPostMaxSalary() != null && jobPost.getJobPostMaxSalary() > 0){
                response.setSalary(jobPost.getJobPostMinSalary() + " - " + jobPost.getJobPostMaxSalary());
            }

            //localities
            String localities = "";
            for(JobPostToLocality jobLocality : jobPost.getJobPostToLocalityList()){
                localities += jobLocality.getLocality().getLocalityName() + ", ";
            }
            if(!Objects.equals(localities, "")){
                response.setJobLocation(localities.substring(0, (localities.length() - 2)));
            } else{
                response.setJobLocation(localities);
            }

            //is hot
            if(jobPost.getJobPostIsHot() != null){
                response.setJobIsHot(jobPost.getJobPostIsHot());
            }

            if(jobPost.getPricingPlanType() != null){
                response.setJobPlan(jobPost.getPricingPlanType().getPricingPlanTypeName());
            }

            //job role
            if(jobPost.getJobRole() != null){
                response.setJobRole(jobPost.getJobRole().getJobName());
            }

            //job status
            if(jobPost.getJobPostStatus() != null){
                response.setJobStatus(jobPost.getJobPostStatus().getJobStatusName());
            }

            //job type id
            response.setJobTypeId(ServerConstants.JOB_POST_TYPE_OPEN);

            //created by
            Interaction createdInteraction = jobPostCreatedInteractionMap.get(jobPost.getJobPostUUId());
            if (createdInteraction != null) {
                response.setCreatedBy(createdInteraction.getCreatedBy());
            }

            //awaiting interview schedule
            response.setAwaitingInterviewSchedule(jobPost.getAwaitingInterviewScheduleCount());

            //awaiting recruiter confirmation
            response.setAwaitingRecruiterConfirmation(jobPost.getAwaitingRecruiterConfirmationCount());

            //confirmed interviews
            response.setConfirmedInterviews(jobPost.getConfirmedInterviewsCount());

            //today's interviews
            response.setTodaysInterviews(jobPost.getTodaysInterviewCount());

            //tomorrow's interviews
            response.setTomorrowsInterviews(jobPost.getTomorrowsInterviewCount());

            //completed interviews
            response.setCompletedInterviews(jobPost.getCompletedInterviewCount());

            //job experience
            if(jobPost.getJobPostExperience() != null){
                response.setJobExperience(jobPost.getJobPostExperience().getExperienceType());
            }

            //job experience
            if(jobPost.getJobPostExperience() != null){
                response.setJobExperience(jobPost.getJobPostExperience().getExperienceType());
            }

            //interview address
            if(jobPost.getInterviewFullAddress() != null){
                response.setInterviewAddress(jobPost.getInterviewFullAddress());
            }

            //interview days
            response.setInterviewDetailsList(jobPost.getInterviewDetailsList());

            //recruiter info
            if(jobPost.getRecruiterProfile() != null){
                String extraMsg = "";
                if(!Objects.equals(jobPost.getCompany().getCompanyId(), jobPost.getRecruiterProfile().getCompany().getCompanyId())){
                    extraMsg = "(Recruiter changed company to : " + jobPost.getRecruiterProfile().getCompany().getCompanyName() + ") ";
                }
                response.setRecruiterName(jobPost.getRecruiterProfile().getRecruiterProfileName() + " " + extraMsg);
                response.setRecruiterId(Math.toIntExact(jobPost.getRecruiterProfile().getRecruiterProfileId()));
                response.setTotalInterviewCredits(jobPost.getRecruiterProfile().getInterviewCreditCount());
                response.setTotalContactCredits(jobPost.getRecruiterProfile().getContactCreditCount());
            }

            //adding in list
            jobList.add(response);
        }

/*
        return ok(toJson(jobPosts));
*/
        return ok(toJson(jobList));
    }

    public static Result getAllLocality() {

        List<String> cityNames = Arrays.asList("Bangalore", "Bengaluru", "Bangalore Rural", "Bangalore Urban", "Bommasandra");

        List<Locality> localities = Locality.find.setUseQueryCache(!isDevMode)
                .where().in("city", cityNames)
                .orderBy("localityName").findList();

        return ok(toJson(localities));
    }

    public static Result getAllJobsRolesWithJobs() {
        List<JobRole> jobs = JobRole.find.setUseQueryCache(!isDevMode).orderBy("jobName").findList();
        List<JobRole> jobRolesToReturn = new ArrayList<JobRole>();
        for(JobRole jobRole : jobs){
            List<JobPost> jobPostList = JobPost.find.where()
                    .eq("jobRole.jobRoleId",jobRole.getJobRoleId())
                    .eq("JobStatus", ServerConstants.JOB_STATUS_ACTIVE)
                    .eq("Source", ServerConstants.SOURCE_INTERNAL)
                    .findList();

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

    public static Result getAllAsset() {
        List<Asset> assets = Asset.find.setUseQueryCache(!isDevMode).orderBy("asset_title").findList();
        return ok(toJson(assets));
    }

    public static Result getAllInterviewRejectReasons() {
        return ok(toJson(new RejectReasonDAO().getByType(ServerConstants.INTERVIEW_REJECT_TYPE_REASON)));
    }

    public static Result getAllInterviewNotGoingReasons() {
        return ok(toJson(new RejectReasonDAO().getByType(ServerConstants.INTERVIEW_NOT_GOING_TYPE_REASON)));
    }

    public static Result getAllCandidateETA() {
        return ok(toJson(new RejectReasonDAO().getByType(ServerConstants.CANDIDATE_ETA)));
    }

    public static Result getAllNotSelectedReasons() {
        return ok(toJson(new RejectReasonDAO().getByType(ServerConstants.INTERVIEW_NOT_SELECED_TYPE_REASON)));
    }

    public static Result getAllCompany() {
        List<Company> companyList = Company.find.where().orderBy("companyName").findList();
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

    public static Result getAllExperience() {
        List<Experience> experienceList = Experience.find.setUseQueryCache(!isDevMode).findList();
        return ok(toJson(experienceList));
    }

    @Security.Authenticated(RecSecured.class)
    public static Result getAllCompanyStatus() {
        List<CompanyStatus> companyStatusList = CompanyStatus.find.findList();
        return ok(toJson(companyStatusList));
    }

    @Security.Authenticated(SecuredUser.class)
    public static Result getAllCompanyType() {
        List<CompanyType> companyTypeList = CompanyType.find.findList();
        return ok(toJson(companyTypeList));
    }

    @Security.Authenticated(RecSecured.class)
    public static Result getAllJobStatus() {
        List<JobStatus> jobStatusList = JobStatus.find.findList();
        return ok(toJson(jobStatusList));
    }

    @Security.Authenticated(PartnerInternalSecured.class)
    public static Result candidateSignupSupport(Long candidateId, String isCallTrigger) {
        return ok(views.html.signup_support.render(candidateId, isCallTrigger));
    }

    @Security.Authenticated(RecSecured.class)
    public static Result createCompany() {
        return ok(views.html.Recs.create_company.render());
    }

    @Security.Authenticated(PartnerInternalSecured.class)
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
        Logger.info("Browser: " +  request().getHeader("User-Agent") + "; Req JSON : " + searchReq );
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

    @Security.Authenticated(PartnerInternalSecured.class)
    public static Result getAllLeadSource() {
        List<LeadSource> leadSources = LeadSource.find.orderBy("leadSourceName").findList();
        return ok(toJson(leadSources));
    }
    @Security.Authenticated(PartnerInternalSecured.class)
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
        Logger.info("Browser: " +  request().getHeader("User-Agent") + "; Req JSON : " + followUp );
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

    @Security.Authenticated(PartnerInternalSecured.class)
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

    @Security.Authenticated(PartnerInternalSecured.class)
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

    @Security.Authenticated(RecSecured.class)
    public static Result isRecruiterExists(String mobile) {
        if(mobile != null){
            mobile = FormValidator.convertToIndianMobileFormat(mobile);
            RecruiterProfile existingRecruiter = RecruiterService.isRecruiterExists(mobile);
            if(existingRecruiter != null) {
                return ok(toJson(existingRecruiter.getRecruiterProfileId()));
            }
        }
        return ok("0");
    }


    @Security.Authenticated(SecuredUser.class)
    public static Result ifCandidateExists(String mobile) {
        if(session().get("partnerId") != null){
            Partner partner = Partner.find.where().eq("partner_id", session().get("partnerId")).findUnique();
            if(partner != null && partner.getPartnerType().getPartnerTypeId() == ServerConstants.PARTNER_TYPE_PRIVATE){
                //its a private partner
                Integer associationStatus = checkCandidateExistence(partner, FormValidator.convertToIndianMobileFormat(mobile));
                if(associationStatus == ServerConstants.STATUS_NO_CANDIDATE ||
                        associationStatus == ServerConstants.STATUS_CANDIDATE_EXISTS_DIFFERENT_COMPANY){
                    return ok("0");
                } else{
                    return ok("1");
                }
            }
        }
        if(mobile != null){
            mobile = FormValidator.convertToIndianMobileFormat(mobile);
            Candidate existingCandidate = CandidateService.isCandidateExists(mobile);
            if(existingCandidate != null) {
                return ok("1");
            }
        }
        return ok("0");
    }

    @Security.Authenticated(PartnerInternalSecured.class)
    public static Result getAllDeactivationReason() {
        List<Reason> deactivationReasons = Reason.find.all();
        return ok(toJson(deactivationReasons));
    }

    @Security.Authenticated(SuperSecured.class)
    public static Result getDeactivatedCandidateList() {
        JsonNode deactivatedCandidateJsonNode = request().body().asJson();
        Logger.info("Browser: " +  request().getHeader("User-Agent") + "; Req JSON : " + deactivatedCandidateJsonNode );
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

        return ok(toJson(DeactivationService.getDeActivatedCandidates(deactivatedCandidateRequest)));
    }

    public static Result deactiveToActive() {
        JsonNode deactiveToActiveJson = request().body().asJson();
        Logger.info("Browser: " +  request().getHeader("User-Agent") + "; Req JSON : " + deactiveToActiveJson );
        if(deactiveToActiveJson == null){
            return badRequest();
        }

        DeActiveToActiveRequest deActiveToActiveRequest = new DeActiveToActiveRequest();
        ObjectMapper newMapper = new ObjectMapper();
        Logger.info("deactivatedCandidateJsonNode: "+deactiveToActiveJson);
        try {
            deActiveToActiveRequest = newMapper.readValue(deactiveToActiveJson.toString(), DeActiveToActiveRequest.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ok(toJson(DeactivationService.deactivateToActive(deActiveToActiveRequest)));
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
    public static Result renderPageFooter() {
        return ok(views.html.Fragment.footer.render());}

    public static Result renderJobPostCards() { return ok(views.html.Fragment.hot_jobs_card_view.render());}
    public static Result pageNotFound() { return ok(views.html.page_not_found.render());}

    public static Result renderJobRelatedPages(String urlString, Long candidateId, String key){

        UrlValidatorUtil urlValidatorUtil = new UrlValidatorUtil();
        UrlParameters urlParameters = urlValidatorUtil.parseURL(urlString);


        boolean redirectToApplyInShort = false;
        if(candidateId != null && key != null) {
            boolean invalidParams = false;
            Candidate existingCandidate = CandidateDAO.getById(candidateId);
            if(existingCandidate != null) {

                // adding session details
                Auth existingAuth = Auth.find.where().eq("candidateId", candidateId).findUnique();
                if(existingAuth != null) {

                    boolean isKeyValid = key.equals(Util.md5(existingAuth.getOtp() + ""));
                    if (isKeyValid ) {
                        Logger.info("Added session for Sms link based login ");
                        AuthService.addSession(existingAuth, existingCandidate);

                        // otp gets updated on post apply

                        String jobPostUUId = null;
                        if(urlParameters!= null && urlParameters.getJobPostId() != null) {
                            JobPost jobPost = JobPostDAO.findById(urlParameters.getJobPostId());
                            if(jobPost != null){
                                jobPostUUId = jobPost.getJobPostUUId();
                            }
                        }

                        // create interaction for this event of candidate applying through this channel
                        InteractionService.createInteractionForApplyInShort(
                                existingCandidate.getCandidateUUId(),
                                jobPostUUId);

                        redirectToApplyInShort = true;
                    } else {
                        invalidParams = true;
                    }
                } else {
                    invalidParams = true;
                }
            } else {
                invalidParams = true;
            }

            if(invalidParams) {
                session().clear();
                return redirect("/pageNotFound");
            }
        }

        if (urlParameters.getUrlType() == UrlParameters.TYPE.TYPE_JOB_ROLE_LOCATION_COMPANY_WITH_JOB_POST_ID) {
            String jobLocation = urlParameters.getJobLocation();
            String jobCompany = urlParameters.getJobCompany();
            String jobPostTile = urlParameters.getJobPostTitle();
            Long jobPostId = urlParameters.getJobPostId();

            if(redirectToApplyInShort) {
                return ok(views.html.Fragment.apply_job_in_short.render(jobLocation, jobCompany, jobPostTile, jobPostId));
            } else {
                return ok(views.html.Fragment.posted_job_details.render(jobLocation, jobCompany, jobPostTile, jobPostId));
            }
        } else if (urlParameters.getUrlType() == UrlParameters.TYPE.TYPE_JOB_ROLE_LOCATION_COMPANY) {
            //return ok("All Post");
            return ok(views.html.page_not_found.render());
        } else if (urlParameters.getUrlType() == UrlParameters.TYPE.TYPE_JOB_ROLE_COMPANY) {
            //return ok("Job Post at Company");
            return ok(views.html.page_not_found.render());
        } else if (urlParameters.getUrlType() == UrlParameters.TYPE.TYPE_JOB_ROLE_LOCATION) {
            return ok(views.html.Fragment.job_role_page.render(urlParameters.getJobRoleName(),
                    urlParameters.getJobRoleId()));
        } else if (urlParameters.getUrlType() == UrlParameters.TYPE.TYPE_ALL_JOBS_LOCATION_COMPANY) {
            //return ok("All Jobs in Location at Company");
            return ok(views.html.page_not_found.render());
        } else if (urlParameters.getUrlType() == UrlParameters.TYPE.TYPE_ALL_JOBS_COMPANY) {
            return ok(views.html.page_not_found.render());
            //return ok("All Jobs at Company");
        } else if (urlParameters.getUrlType() == UrlParameters.TYPE.TYPE_ALL_JOBS_LOCATION) {
            return ok(views.html.Fragment.show_all_jobs_page.render());
        } else if (urlParameters.getUrlType() == UrlParameters.TYPE.TYPE_ALL_JOBS_WITH_JOB_ROLE_ID) {
            String jobRoleName = urlParameters.getJobRoleName();
            Long jobRoleId = urlParameters.getJobRoleId();
            return ok(views.html.Fragment.job_role_page.render(jobRoleName, jobRoleId));
        } else if (urlParameters.getUrlType() == UrlParameters.TYPE.INVALID_REQUEST) {
            return ok(views.html.page_not_found.render());
        }

        return ok(views.html.page_not_found.render());
    }


    public static Result getJobsPageContent(String urlString,Long index) {

        UrlValidatorUtil urlValidatorUtil = new UrlValidatorUtil();
        UrlParameters urlParameters = urlValidatorUtil.parseJobsContentPageUrl(urlString);
        SearchJobService searchJobService = new SearchJobService();
        Long candidateId = session().get("candidateId") == null? null : Long.parseLong(session().get("candidateId"));
        if(urlParameters.getUrlType() == UrlParameters.TYPE.TYPE_JOB_DETAILS_WITH_JOB_POST_ID_REQUEST){
            JobPost jobPost = JobPost.find.where().eq("JobPostId", urlParameters.getJobPostId()).findUnique();
            if (jobPost != null) {
                searchJobService.computeCTA(jobPost, candidateId);
                return ok(toJson(jobPost));
            }
            else {
                Logger.error(" Job post with id " + urlParameters.getJobPostId() + " not found!. Forwarding user to page not found");
                return badRequest();
            }
        }
        else if(urlParameters.getUrlType() == UrlParameters.TYPE.TYPE_JOB_POST_WITH_JOB_ROLE_ID_REQUEST) {
            // query jobrole table for the given id. if it doesnt exist, fwd to page not found
            JobRole jobRole = JobRole.find.where().eq("JobRoleId",urlParameters.getJobRoleId()).findUnique();
            if(jobRole != null){
                JobSearchService jobSearchService = new JobSearchService();
                JobPostResponse jobPostResponse = jobSearchService.getActiveJobsForJobRolePaginated(urlParameters.getJobRoleId(),index);
                if(jobPostResponse != null && jobPostResponse.getTotalJobs() > 0){
                    return ok(toJson(jobPostResponse));
                }else{
                    return ok("Error");
                }
            }
            else{
                Logger.error(" Job post with id " + urlParameters.getJobPostId() + " not found!. Forwarding user to page not found");
                return badRequest();
            }
        }
        Logger.error("Unrecognized URL pattern detected " + urlString + ". Forwarding user to page not found");
        return badRequest();
    }

    public static Result getAllCompanyLogos() {
        List<Company> companyList = new CompanyDAO().getHiringCompanyLogos();

        List<String> logoList = new ArrayList<>();
        for(Company company: companyList){
            logoList.add(company.getCompanyLogo());
        }
        return ok(toJson(logoList));
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
            if (jobRoleIds != null && !jobRoleIds.equalsIgnoreCase("null")) {
                jobRoleIds = jobRoleIds.replaceAll("[^0-9,]", "");
                if (jobRoleIds.isEmpty()) {
                    return ok("Error ! wrong param value");
                }
                List<String> jobRoleIdStrList = Arrays.asList(jobRoleIds.split("\\s*,\\s*"));
                if (jobRoleIdStrList.size() > 0) {
                    for (String roleId: jobRoleIdStrList) {
                        if(roleId.isEmpty() || roleId.length() > 3) continue;
                        jobRoleIdList.add(Long.parseLong(roleId));
                    }
                }
            } else {
                if (jobPostIds != null && !jobPostIds.equalsIgnoreCase("null")) {
                    jobPostIds = jobPostIds.replaceAll("[^0-9,]", "");
                    if (jobPostIds.isEmpty()) {
                        return ok("Error ! wrong param value");
                    }
                    List<String> jobPostIdStrList = Arrays.asList(jobPostIds.split("\\s*,\\s*"));
                    List<Long> jobPostIdList = new ArrayList<>();
                    if (jobPostIdStrList.size() > 0) {
                        for (String jobPostId: jobPostIdStrList) {
                            if(jobPostId.isEmpty() || jobPostId.length() > 3) continue;
                            jobPostIdList.add(Long.parseLong(jobPostId));
                        }
                    }
                    List<JobPost> jobPostList = JobPostDAO.findByIdList(jobPostIdList);
                    for (JobPost jobPost : jobPostList) {
                        if (!jobRoleIdList.contains(jobPost.getJobRole().getJobRoleId())){
                            jobRoleIdList.add(jobPost.getJobRole().getJobRoleId());
                        }
                    }
                } else {
                    Candidate candidate = Candidate.find.where().eq("candidateId", candidateId).findUnique();
                    for (JobPreference jobPreference : candidate.getJobPreferencesList()) {
                        jobRoleIdList.add(jobPreference.getJobRole().getJobRoleId());
                    }
                }
            }
            if (jobRoleIdList.size() == 0) {
                return ok("NA");
            }
            List<AssessmentService.JobRoleWithAssessmentBundle> assessmentBundleList = AssessmentService.getJobRoleIdsVsIsAssessedList(candidateId, jobRoleIdList);

            if (assessmentBundleList != null && assessmentBundleList.size() > 0) {
                jobRoleIdList = new ArrayList<>();
                for(AssessmentService.JobRoleWithAssessmentBundle bundle : assessmentBundleList){
                    if (!bundle.isAssessed()) {
                        jobRoleIdList.add(bundle.getJobRoleId());
                    }
                }
            }
            if (jobRoleIdList.size() == 0) {
                return ok("OK");
            }
            List<AssessmentQuestion> assessmentQuestionList = AssessmentService.getQuestions(jobRoleIdList);

            if (assessmentQuestionList.size() > 0) {
                return ok(toJson(assessmentQuestionList));
            }
        }
        return ok("NA");
    }

    @Security.Authenticated(SecuredUser.class)
    public static Result submitAssessment() {
        JsonNode assessmentRequestJson = request().body().asJson();
        Logger.info("Browser: " +  request().getHeader("User-Agent") + "; Req JSON : " + assessmentRequestJson );
        if(assessmentRequestJson == null) {
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

    @Security.Authenticated(SecuredUser.class)
    public static Result getCandidateJobPrefWithAssessmentStatus(Integer limit) {
        String candidateId = session().get("candidateId");
        if(candidateId != null) {
            List<JobPreference> jobPreferenceList;
            if (limit!=null){
                jobPreferenceList = JobPreference.find.where().eq("candidateId", candidateId).setMaxRows(limit).findList();
            } else {
                jobPreferenceList = JobPreference.find.where().eq("candidateId", candidateId).findList();
            }
            List<CandidateJobPrefs.JobPrefWithAssessmentBundle> jobPrefWithAssessmentBundleList = AssessmentService.getJobPrefVsIsAssessedList(Long.parseLong(candidateId), jobPreferenceList);

            if (jobPrefWithAssessmentBundleList != null) {
                return ok(toJson(jobPrefWithAssessmentBundleList));
            }
        }
        return ok("0");
    }

    @Security.Authenticated(SecuredUser.class)
    public static Result getJobPostAppliedStatus(Long jobPostId) {
        String candidateId = session().get("candidateId");
        if(jobPostId != null && candidateId != null){
            JobApplication jobApplication = JobApplication.find.where()
                    .eq("candidateId", candidateId)
                    .eq("jobPostId", jobPostId).findUnique();
            if(jobApplication != null){
                return ok("true");
            } else {
                return ok("false");
            }
        }
        return ok("NA");
    }

    public static Result getRelevantJobsPostsForCandidate(long id) {
        Candidate existingCandidate = Candidate.find.where().eq("candidateId", id).findUnique();
        if (existingCandidate != null) {
            List<JobPost> matchingJobList = JobSearchService.getRelevantJobsPostsForCandidate(
                    FormValidator.convertToIndianMobileFormat(existingCandidate.getCandidateMobile()));

            SearchJobService.computeCTA(matchingJobList, id);
            SearchJobService.removeSensitiveDetail(matchingJobList);

            return ok(toJson(matchingJobList));
        }
        return ok("ok");
    }

    @Security.Authenticated(SecuredUser.class)
    public static Result getAllTimeSlots(){
        List<InterviewTimeSlot> interviewTimeSlotList = InterviewTimeSlot.find.findList();
        return ok(toJson(interviewTimeSlotList));
    }

    @Security.Authenticated(SuperAdminSecured.class)
    public static Result updateAllRelevantJobCategories() {
        return ok(toJson(JobRelevancyEngine.updateAllRelevantJobCategories()));
    }

    @Security.Authenticated(SuperAdminSecured.class)
    public static Result updateAllActivityScores() {
        return ok(toJson(GlobalAnalyticsService.computeActivityScore()));
    }

    @Security.Authenticated(SuperAdminSecured.class)
    public static Result getRelatedJobRole(String format, String jobRoleIds) {
        List<RelatedJobRole> relatedJobRoleList;
        if(jobRoleIds != null) {
            relatedJobRoleList = RelatedJobRole.find.where().in("jobRoleId", jobRoleIds).findList();
        } else {
            relatedJobRoleList = RelatedJobRole.find.all();
        }

        Map<Object, List<Object>> relevantJobs = new LinkedHashMap<>();
        if (format != null) {
            int mode = format.equalsIgnoreCase("only_id") ? 1 : format.equalsIgnoreCase("only_name") ? 2 : format.equalsIgnoreCase("object") ? 3 : 4;
            Long prevJobRoleId = null;
            for (RelatedJobRole relatedJobRole : relatedJobRoleList) {
                List relatedJobList;
                if (mode == 1) {
                    relatedJobList = relevantJobs.get(relatedJobRole.getJobRole().getJobRoleId());
                } else if (mode == 2) {
                    relatedJobList = relevantJobs.get(relatedJobRole.getJobRole().getJobName());
                } else if (mode == 3) {
                    relatedJobList = relevantJobs.get(relatedJobRole.getJobRole().getJobRoleId());
                } else {
                    return badRequest();
                }
                if (relatedJobList == null){
                    relatedJobList = new ArrayList<>();
                    if (mode == 1) {
                        relevantJobs.put( relatedJobRole.getJobRole().getJobRoleId(), relatedJobList);
                    } else if (mode == 2) {
                        relevantJobs.put( relatedJobRole.getJobRole().getJobName(), relatedJobList);
                    } else if (mode == 3) {
                        relevantJobs.put( relatedJobRole.getJobRole().getJobRoleId(), relatedJobList);
                    } else {
                        return badRequest();
                    }
                }
                if (prevJobRoleId == null || prevJobRoleId != relatedJobRole.getJobRole().getJobRoleId()) {
                    prevJobRoleId = relatedJobRole.getJobRole().getJobRoleId();
                } else {
                    if (mode == 1) {
                        relatedJobList.add(relatedJobRole.getRelatedJobRole().getJobRoleId());
                    } else if (mode == 2) {
                        relatedJobList.add(relatedJobRole.getRelatedJobRole().getJobName());
                    } else if (mode == 3) {
                        relatedJobList.add(relatedJobRole.getRelatedJobRole());
                    } else {
                        return badRequest();
                    }
                }
            }
        }
        return ok(toJson(relevantJobs));
    }

    @Security.Authenticated(RecSecured.class)
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

        if (matchingCandidateRequest != null) {
            return ok(toJson(JobPostWorkflowEngine.getMatchingCandidate(
                    matchingCandidateRequest.getJobPostId(),
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
                    matchingCandidateRequest.getDistanceRadius())));
        }
        return badRequest();
    }

    @Security.Authenticated(RecSecured.class)
    public static Result renderWorkflow(Long jobPostId, String view) {
        if(view == null) {
            return badRequest();
        }

        switch (view.trim()) {
            case "match_view":
                return ok(views.html.match_candidate.render());
            case "pre_screen_view":
                return ok(views.html.pre_screen.render());
            case "pending_interview_schedule":
                return ok(views.html.pending_interview_schedule.render());
            case "pre_screen_completed_view":
                return ok(views.html.pre_screen_completed.render());
            case "confirmed_interview_view":
                return ok(views.html.confirmed_interview.render());
            case "completed_interview_view":
                return ok(views.html.interview_complete_view.render());
        }
        return badRequest();
    }

    @Security.Authenticated(RecSecured.class)
    public static Result getJobPostMatchingParams(long jobPostId) {
        return ok(toJson(JobPostDAO.findById(jobPostId)));
    }

    @Security.Authenticated(RecSecured.class)
    public static Result saveSelectedCandidate() {
        JsonNode selectedCandidateIdsJson = request().body().asJson();
        Logger.info("Browser: " +  request().getHeader("User-Agent") + "; Req JSON : " + selectedCandidateIdsJson);
        if(selectedCandidateIdsJson == null){
            return badRequest();
        }
        SelectedCandidateRequest selectedCandidateRequest= new SelectedCandidateRequest();
        ObjectMapper newMapper = new ObjectMapper();

        // since jsonReq has single/multiple values in array
        newMapper.enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY);

        try {
            selectedCandidateRequest = newMapper.readValue(selectedCandidateIdsJson.toString(), SelectedCandidateRequest.class);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return ok(toJson(JobPostWorkflowEngine.saveSelectedCandidates(selectedCandidateRequest)));
    }

    @Security.Authenticated(SecuredUser.class)
    public static Result getSelectedCandidate(Long jobPostId) {

        return ok(toJson(JobPostWorkflowEngine.getSelectedCandidates(jobPostId)));
    }

    public static Result getJobPostVsCandidate(Long candidateId, Long jobPostId, Boolean rePreScreen, String candidateMobile) {
        if(candidateId == null && jobPostId == null) {
            return badRequest();
        } else if (candidateId != null && candidateId == 0L && jobPostId != null && jobPostId == 0L) {
            return badRequest();
        }
        if(candidateId == null && candidateMobile !=null){
            candidateMobile = FormValidator.convertToIndianMobileFormat(candidateMobile);

            Candidate candidate = Candidate.find.where().eq("candidateMobile", candidateMobile).findUnique();
            if(candidate == null) {
                return badRequest();
            } else {
                candidateId = candidate.getCandidateId();
            }
        }

        return ok(toJson(JobPostWorkflowEngine.getJobPostVsCandidate(jobPostId, candidateId, rePreScreen)));
    }

    @Security.Authenticated(SecuredUser.class)
    public static Result updatePreScreenAttempt(Long candidateId, Long jobPostId, String callStatus) {
        if (candidateId == 0L || jobPostId == 0L) {
            return badRequest();
        }

        return ok(toJson(JobPostWorkflowEngine.updatePreScreenAttempt(jobPostId, candidateId, callStatus, Integer.valueOf(session().get("sessionChannel")))));
    }

    @Security.Authenticated(SecuredUser.class)
    public static Result submitPreScreen() {
        JsonNode preScreenRequestJson = request().body().asJson();
        Logger.info("Browser: " +  request().getHeader("User-Agent") + "; Req JSON : " + preScreenRequestJson);
        if(preScreenRequestJson == null){
            return badRequest();
        }
        PreScreenRequest preScreenRequest= new PreScreenRequest();
        ObjectMapper newMapper = new ObjectMapper();
        try {
            preScreenRequest = newMapper.readValue(preScreenRequestJson.toString(), PreScreenRequest.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ok(toJson(JobPostWorkflowEngine.savePreScreenResult(preScreenRequest, Integer.valueOf(session().get("sessionChannel")), ServerConstants.JWF_STATUS_PRESCREEN_ATTEMPTED)));
    }

    @Security.Authenticated(SecuredUser.class)
    public static Result getDocumentReqForJobRole(Long jobPostId, Long jobRoleId) {
        if(jobPostId == null && jobRoleId == null) {
            return badRequest();
        }
        if(jobRoleId == null && jobPostId !=null && jobPostId != 0) {
            JobPost jobPost = JobPostDAO.findById(jobPostId);
            jobRoleId = jobPost.getJobRole().getJobRoleId();
        }

        if ((jobPostId != null && jobPostId == 0 )|| jobRoleId == 0){
            return badRequest();
        }
        List<IdProof> idProofList = new ArrayList<>();
        List<IdProof> commonIdProofList = IdProof.find.setUseQueryCache(!isDevMode)
                .where()
                .eq("isCommon",ServerConstants.IS_COMMON)
                .orderBy("idProofName")
                .findList();

        List<JobRoleToDocument> jobRoleToDocumentList= JobRoleToDocument.find.setUseQueryCache(!isDevMode)
                .where().eq("jobRole.jobRoleId", jobRoleId).findList();

        for(JobRoleToDocument jobRoleToDocument: jobRoleToDocumentList) {
            idProofList.add(jobRoleToDocument.getIdProof());

            // remove duplicates from the common list
            if (commonIdProofList.contains(jobRoleToDocument.getIdProof())) {
                commonIdProofList.remove(jobRoleToDocument.getIdProof());
            }
        }

        idProofList.addAll(commonIdProofList);

        Collections.sort(idProofList,  (o1, o2) -> o1.getIdProofName().compareTo(o2.getIdProofName()));
        return ok(toJson(idProofList));
    }

    @Security.Authenticated(SecuredUser.class)
    public static Result getAssetReqForJobRole(Long jobPostId, Long jobRoleId, String jobRoleIds) {

        if(jobPostId == null && jobRoleId == null && jobRoleIds == null) {
            return badRequest();
        }

        List<String> jobRoleIdList = new ArrayList<>();

        if(jobRoleIds == null && jobRoleId == null && jobPostId !=null && jobPostId != 0) {
            JobPost jobPost = JobPostDAO.findById(jobPostId);
            if(jobPost == null) {
                return badRequest();
            }
            jobRoleId = jobPost.getJobRole().getJobRoleId();

            jobRoleIdList.add(String.valueOf(jobRoleId));
        } else if(jobRoleIds != null) {
            jobRoleIdList = Arrays.asList(jobRoleIds.split("\\s*,\\s*"));
        } else if(jobRoleId != null &&  jobRoleId  != 0){
            jobRoleIdList = new ArrayList<>();
            jobRoleIdList.add(String.valueOf(jobRoleId));
        }
        if ((jobPostId != null && jobPostId == 0 )|| (jobRoleId != null && jobRoleId == 0)){
            return badRequest();
        }
        List<Asset> assetList = new ArrayList<>();
        List<Asset> commonAssetList = Asset.find.setUseQueryCache(!isDevMode)
                .where()
                .eq("isCommon", ServerConstants.IS_COMMON)
                .orderBy("assetTitle")
                .findList();

        List<JobRoleToAsset> jobRoleToAssetList= JobRoleToAsset.find.setUseQueryCache(!isDevMode)
                .where().in("jobRole.jobRoleId", jobRoleIdList).findList();

        for(JobRoleToAsset jobRoleToAsset: jobRoleToAssetList) {
            if(!assetList.contains(jobRoleToAsset.getAsset())){
                assetList.add(jobRoleToAsset.getAsset());
            }

            // remove duplicates from common list
            if (commonAssetList.contains(jobRoleToAsset.getAsset())) {
                commonAssetList.remove(jobRoleToAsset.getAsset());
            }
        }

        assetList.addAll(commonAssetList);

        Collections.sort(assetList,  (o1, o2) -> o1.getAssetTitle().compareTo(o2.getAssetTitle()));
        return ok(toJson(assetList));
    }

    @Security.Authenticated(SecuredUser.class)
    public static Result renderWorkflowInteraction(String uuid) {
        return ok(views.html.workflow_interaction.render());
    }

    @Security.Authenticated(SecuredUser.class)
    public static Result getWorkflowInteraction(String job_post_workflow_uuid) {

        if(job_post_workflow_uuid == null) {
            return badRequest();
        }

        List<Interaction> interactionList = Interaction.find
                .where().eq("objectAUUId", job_post_workflow_uuid.trim())
                .findList();

        List<SupportInteractionResponse> responses = new ArrayList<>();

        SimpleDateFormat sfd = new SimpleDateFormat(ServerConstants.SDF_FORMAT);

        for(Interaction interaction : interactionList){
            SupportInteractionResponse response = new SupportInteractionResponse();
            response.setUserInteractionTimestamp(sfd.format(interaction.getCreationTimestamp()));
            response.setInteractionId(interaction.getId());
            response.setUserNote(interaction.getNote());
            response.setUserResults(interaction.getResult());
            response.setUserCreatedBy(interaction.getCreatedBy());
            response.setUserInteractionType(InteractionConstants.INTERACTION_TYPE_MAP.get(interaction.getInteractionType()));
            response.setChannel(InteractionConstants.INTERACTION_CHANNEL_MAP.get(interaction.getInteractionChannel()));

            responses.add(response);
        }
        return ok(toJson(responses));
    }

    @Security.Authenticated(SecuredUser.class)
    public static Result getPendingInterviewScheduleCandidates(Long jobPostId) {
        return ok(toJson(JobPostWorkflowEngine.getPendingInterviewScheduleCandidates(jobPostId)));
    }


    @Security.Authenticated(SecuredUser.class)
    public static Result getPreScreenedCandidate(Long jobPostId, Long status) {
        return ok(toJson(JobPostWorkflowEngine.getAllPendingInterviewAndRescheduleConfirmation(jobPostId, status)));
    }

    public static Result getConfirmedInterviewCandidates(Long jobPostId, String start, String end) {
        return ok(toJson(JobPostWorkflowEngine.getConfirmedInterviewCandidates(jobPostId, start, end)));
    }

    public static Result getAllCompletedInterviews(Long jpId) {
        return ok(toJson(JobPostWorkflowEngine.getAllCompletedInterviews(jpId)));
    }


    @Security.Authenticated(SecuredUser.class)
    public static Result confirmInterview(long jpId, long value) {
        if (session().get("candidateId") != null) {
            if(session().get("sessionChannel") == null) {
                Logger.warn("Session channel not set, logged out user");
                logout();
                return badRequest();
            }
            Candidate candidate = Candidate.find.where().eq("candidateId", session().get("candidateId")).findUnique();
            if(candidate != null){
                return ok(toJson(JobPostWorkflowEngine.confirmCandidateInterview(jpId, value, candidate, Integer.valueOf(session().get("sessionChannel")))));
            }
        }
        return ok("0");
    }

    @Security.Authenticated(SecuredUser.class)
    public static Result getCandidateDetails(Long candidateId, Integer propertyId) {
        if(candidateId == null || propertyId == null) {
            return badRequest();
        }
        Candidate candidate = Candidate.find.where().eq("candidateId", candidateId).findUnique();
        if(candidate == null) {
            return badRequest("Candidate Not Found!");
        }

        // unable to use switch-case, issue with ordinal value
        // return candidate Detail + container element
        if (ServerConstants.PROPERTY_TYPE_DOCUMENT == propertyId) {
            return  ok(toJson(candidate.getIdProofReferenceList() != null ? candidate.getIdProofReferenceList(): new ArrayList<>()));
        } else if (ServerConstants.PROPERTY_TYPE_LANGUAGE == propertyId) {
            return  ok(toJson(candidate.getLanguageKnownList() != null ? candidate.getLanguageKnownList(): new ArrayList<>()));
        } else if (ServerConstants.PROPERTY_TYPE_ASSET_OWNED == propertyId) {
            return  ok(toJson(candidate.getCandidateAssetList() != null ? candidate.getCandidateAssetList(): new ArrayList<>()));
        } else if (ServerConstants.PROPERTY_TYPE_MAX_AGE == propertyId) {
            return  ok(toJson(candidate.getCandidateDOB() != null ? candidate.getCandidateDOB(): ""));
        } else if (ServerConstants.PROPERTY_TYPE_EXPERIENCE == propertyId) {
            return  ok(toJson(candidate.getCandidateTotalExperience() != null ? candidate.getCandidateTotalExperience(): ""));
        } else if (ServerConstants.PROPERTY_TYPE_EDUCATION == propertyId) {
            return  ok(toJson(candidate.getCandidateEducation() != null ? candidate.getCandidateEducation(): ""));
        } else if (ServerConstants.PROPERTY_TYPE_GENDER == propertyId) {
            return  ok(toJson(candidate.getCandidateGender() != null ? candidate.getCandidateGender(): ""));
        } else if (ServerConstants.PROPERTY_TYPE_SALARY == propertyId) {
            if(candidate.getCandidateLastWithdrawnSalary() != null) {
                return  ok(toJson(candidate.getCandidateLastWithdrawnSalary()));
            }
            return ok();
        } else if (ServerConstants.PROPERTY_TYPE_LOCALITY == propertyId) {
            if(candidate.getLocality() != null){
                return  ok(toJson(candidate.getLocality()));
            }
            return ok();
        } else if (ServerConstants.PROPERTY_TYPE_WORK_SHIFT == propertyId) {
            if(candidate.getTimeShiftPreference() != null){
                return  ok(toJson(candidate.getTimeShiftPreference()));
            }
            return ok();
        }

        return badRequest("Error");
    }

    @Security.Authenticated(SecuredUser.class)
    public static Result updateCandidateDetailsAtPreScreen(Integer propertyId, Long candidateId) throws IOException {
        if(candidateId == null && propertyId == null) {
            return badRequest();
        }

        JsonNode updateCandidateDetailJSON = request().body().asJson();
        Logger.info("Browser: " +  request().getHeader("User-Agent") + "; Req JSON : " + updateCandidateDetailJSON);
        if(updateCandidateDetailJSON == null){
            return badRequest();
        }
        ObjectMapper newMapper = new ObjectMapper();

        // since jsonReq has single/multiple values in array
        newMapper.enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY);

        Candidate candidate = Candidate.find.where().eq("candidateId", candidateId).findUnique();
        if(candidate == null) {
            return badRequest("Candidate Not Found!");
        }

        if (ServerConstants.PROPERTY_TYPE_DOCUMENT == propertyId) {
            UpdateCandidateDocument updateCandidateDocument = newMapper.readValue(updateCandidateDetailJSON.toString(), UpdateCandidateDocument.class);
            boolean isVerifyAadhaar = CandidateService.updateCandidateDocument(candidate, updateCandidateDocument);

            if (isVerifyAadhaar) {
                CandidateService.verifyAadhaar(candidate.getCandidateMobile());
            }

            return ok("ok");
        } else if (ServerConstants.PROPERTY_TYPE_LANGUAGE == propertyId) {
            UpdateCandidateLanguageKnown updateCandidateLanguageKnown = newMapper.readValue(updateCandidateDetailJSON.toString(), UpdateCandidateLanguageKnown.class);

            CandidateService.updateCandidateLanguageKnown(candidate, updateCandidateLanguageKnown);
            return ok("ok");
        } else if (ServerConstants.PROPERTY_TYPE_ASSET_OWNED == propertyId) {
            UpdateCandidateAsset updateCandidateAsset = newMapper.readValue(updateCandidateDetailJSON.toString(), UpdateCandidateAsset.class);

            CandidateService.updateCandidateAssetOwned(candidate, updateCandidateAsset);
            return ok("ok");
        } else if (ServerConstants.PROPERTY_TYPE_MAX_AGE == propertyId) {
            UpdateCandidateDob updateCandidateDob = newMapper.readValue(updateCandidateDetailJSON.toString(), UpdateCandidateDob.class);

            CandidateService.updateCandidateDOB(candidate, updateCandidateDob);
            return ok("ok");
        } else if (ServerConstants.PROPERTY_TYPE_EXPERIENCE == propertyId) {
            UpdateCandidateWorkExperience updateCandidateWorkExperience = newMapper.readValue(updateCandidateDetailJSON.toString(), UpdateCandidateWorkExperience.class);

            Logger.info(toJson(updateCandidateWorkExperience) + " ");
            CandidateService.updateCandidateWorkExperience(candidate, updateCandidateWorkExperience);
            return ok("ok");
        } else if (ServerConstants.PROPERTY_TYPE_EDUCATION == propertyId) {
            UpdateCandidateEducation updateCandidateEducation= newMapper.readValue(updateCandidateDetailJSON.toString(), UpdateCandidateEducation.class);

            CandidateService.updateCandidateEducation(candidate, updateCandidateEducation);
            return ok("ok");
        } else if (ServerConstants.PROPERTY_TYPE_GENDER == propertyId) {
            UpdateCandidateGender updateCandidateGender= newMapper.readValue(updateCandidateDetailJSON.toString(), UpdateCandidateGender.class);

            CandidateService.updateCandidateGender(candidate, updateCandidateGender);
            return ok("ok");
        } else if (ServerConstants.PROPERTY_TYPE_SALARY == propertyId) {
            UpdateCandidateLastWithdrawnSalary lastWithdrawnSalary = newMapper.readValue(updateCandidateDetailJSON.toString(), UpdateCandidateLastWithdrawnSalary.class);

            CandidateService.updateCandidateLastWithdrawnSalary(candidate, lastWithdrawnSalary);
            return ok("ok");
        } else if (ServerConstants.PROPERTY_TYPE_LOCALITY == propertyId) {
            UpdateCandidateHomeLocality updateCandidateHomeLocality = newMapper.readValue(updateCandidateDetailJSON.toString(), UpdateCandidateHomeLocality.class);

            CandidateService.updateCandidateHomeLocality(candidate, updateCandidateHomeLocality);
            return ok("ok");
        } else if (ServerConstants.PROPERTY_TYPE_WORK_SHIFT == propertyId) {
            UpdateCandidateTimeShiftPreference timeShiftPreference= newMapper.readValue(updateCandidateDetailJSON.toString(), UpdateCandidateTimeShiftPreference.class);

            CandidateService.updateCandidateWorkshift(candidate, timeShiftPreference);
            return ok("ok");
        }

        return badRequest();
    }

    @Security.Authenticated(SecuredUser.class)
    public static Result updateCandidateInterviewDetail(Long candidateId, Long jobPostId) throws IOException {
        if(candidateId == null || jobPostId == null) {
            return badRequest();
        }

        // TODO: if channel = partner -> put a check to find if the candidate belongs to the partner or not
        JsonNode updateCandidateDetailJSON = request().body().asJson();
        Logger.info("Browser: " +  request().getHeader("User-Agent") + "; Req JSON : " + updateCandidateDetailJSON);
        if(updateCandidateDetailJSON == null) {
            return badRequest();
        }
        ObjectMapper newMapper = new ObjectMapper();

        // since jsonReq has single/multiple values in array
        newMapper.enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY);

        AddCandidateInterviewSlotDetail interviewSlotDetail = newMapper.readValue(updateCandidateDetailJSON.toString(), AddCandidateInterviewSlotDetail.class);

        return ok(toJson(JobPostWorkflowEngine.updateCandidateInterviewDetail(candidateId, jobPostId, interviewSlotDetail, Integer.valueOf(session().get("sessionChannel")))));
    }

    public static Result renderStatusUpdate(long jpId, long cId) {
        return ok(views.html.CandidateDashboard.update_status_view.render());
    }

    public static Result updateInterviewStatus(long cId, long jpId, long val, long reason) {
        Candidate candidate = Candidate.find.where().eq("candidateId", cId).findUnique();
        Integer channel;
        if(session().get("sessionChannel") != null){
            channel = Integer.valueOf(session().get("sessionChannel"));
        } else{
            channel = InteractionConstants.INTERACTION_CHANNEL_CANDIDATE_WEBSITE;
        }
        if(candidate != null){
            JobPost jobPost = JobPostDAO.findById(jpId);
            if(jobPost != null){
                return ok(toJson(JobPostWorkflowEngine.updateCandidateInterviewStatus(candidate, jobPost, val, reason, channel)));
            }
        }
        return ok("0");
    }

    @Security.Authenticated(SecuredUser.class)
    public static Result updateInterviewStatusViaCandidate(long jpId, long val, long reason) {
        if(session().get("candidateId") != null){
            Candidate candidate = Candidate.find.where().eq("candidateId", session().get("candidateId")).findUnique();
            if(candidate != null){
                JobPost jobPost = JobPostDAO.findById(jpId);
                if(jobPost != null){
                    return ok(toJson(JobPostWorkflowEngine.updateCandidateInterviewStatus(candidate, jobPost, val, reason, Integer.valueOf(session().get("sessionChannel")))));
                }
            }
        }
        return ok("0");
    }

    public static Result getJpWfStatus(long cId, long jpId) {
        Candidate candidate = Candidate.find.where().eq("candidateId", cId).findUnique();
        if(candidate != null){
            JobPost jobPost = JobPostDAO.findById(jpId);
            if(jobPost != null){
                if(JobPostWorkflowEngine.getCandidateLatestStatus(candidate, jobPost) != null){
                    return ok(toJson(JobPostWorkflowEngine.getCandidateLatestStatus(candidate, jobPost)));
                }
            }
        }
        return ok("0");
    }

    @Security.Authenticated(SecuredUser.class)
    public static Result confirmInterviewSupport(long cid, long jpId, long status) {
        Candidate candidate = Candidate.find.where().eq("candidateId", cid).findUnique();
        if(session().get("sessionChannel") == null) {
            Logger.warn("Session channel not set, logged out candidate");
            logout();
            return badRequest();
        }
        if (candidate != null) {
            return ok(toJson(JobPostWorkflowEngine.confirmCandidateInterview(jpId, status, candidate, Integer.valueOf(session().get("sessionChannel")))));
        }
        return ok("0");
    }

    public static Result getAllIdProofs(String ids) {
        List<String> idProofIdList = Arrays.asList(ids.split("\\s*,\\s*"));
        if(ids == null) {
            return ok(toJson(IdProof.find.all()));
        } else {
            return ok(toJson(IdProof.find.where().in("idProofId", idProofIdList).findList()));
        }
    }

    public static Result shouldShowInterview(Long jobPostId) {
        if (jobPostId == null) {
            Logger.info("null jobPostId received in GET");
            return badRequest();
        }
        JobPost jobPost = JobPostDAO.findById(jobPostId);
        if(jobPost == null) {
            Logger.info("No JobPost Found for jobPostId: " + jobPostId);
            return badRequest();
        }

        return ok(toJson(RecruiterService.isInterviewRequired(jobPost)));
    }

    @Security.Authenticated(SecuredUser.class)
    public static Result updateCandidateDetailsViaPreScreen(String propertyIdList, String candidateMobile, Long jobPostId) throws IOException {
        List<String> propertyIds = Arrays.asList(propertyIdList.split("\\s*,\\s*"));
        List<Integer> propIdList = new ArrayList<>();
        if(propertyIdList == null || candidateMobile == null) {
            badRequest("Empty Values!");
        }
        for(String propId: propertyIds) {
            propIdList.add(Integer.parseInt(propId));
        }
        if(candidateMobile !=null){
            Candidate candidate = CandidateService.isCandidateExists(candidateMobile);
            if(candidate == null) {
                Logger.info("Candidate not found");
                return badRequest();
            }
            Logger.info("Candidate found");

            JsonNode updateCandidateDetailJSON = request().body().asJson();
            Logger.info("Browser: " +  request().getHeader("User-Agent") + "; Req JSON : " + updateCandidateDetailJSON);
            if(updateCandidateDetailJSON == null){
                return badRequest();
            }
            ObjectMapper newMapper = new ObjectMapper();
            // since jsonReq has single/multiple values in array
            newMapper.enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY);

            UpdateCandidateDetail updateCandidateDetail = newMapper.readValue(updateCandidateDetailJSON.toString(), UpdateCandidateDetail.class);
            boolean isVerifyAadhaar = false;

            isVerifyAadhaar = CandidateService.updateCandidateDetail(propIdList, candidate, updateCandidateDetail);


            if (isVerifyAadhaar) {
                CandidateService.verifyAadhaar(candidateMobile);
            }

            // make entry into prescreen result/response table
            JobPostWorkflowEngine.savePreScreenResultForCandidateUpdate(candidate.getCandidateId(), jobPostId, Integer.valueOf(session().get("sessionChannel")));
            JobPost jobPost = JobPostDAO.findById(jobPostId);

            return ok(toJson(RecruiterService.isInterviewRequired(jobPost)));
        }
        return badRequest();
    }

    @Security.Authenticated(SecuredUser.class)
    public static Result updateFeedback() {
        JsonNode req = request().body().asJson();
        Logger.info("Browser: " +  request().getHeader("User-Agent") + "; Req JSON : " + req);
        AddFeedbackRequest addFeedbackRequest = new AddFeedbackRequest();
        ObjectMapper newMapper = new ObjectMapper();
        try {
            addFeedbackRequest = newMapper.readValue(req.toString(), AddFeedbackRequest.class);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return ok(toJson(JobPostWorkflowEngine.updateFeedback(addFeedbackRequest, Integer.valueOf(session().get("sessionChannel")))));
    }

    public static Result showResumeUpload() {
        return ok(views.html.resumeUpload.render());
    }


    public static Result doResumeUpload(String candidateId) throws org.json.JSONException {

        Http.MultipartFormData body = request().body().asMultipartFormData();

        UploadResumeResponse resumeResponse = new UploadResumeResponse();
        int i = 0;
        JSONArray list = new JSONArray();

        while(Boolean.TRUE){
            i++;
            Http.MultipartFormData.FilePart resume = body.getFile("resume"+i);
            Logger.info("Fetching file against key --> resume"+i);

            if(resume == null) {
                Logger.info("Could not fetch file against key --> resume"+i);
                break;
            }

            Long cId = 0L;
            if(candidateId != null) {
                Logger.info("candidateId = "+ candidateId);
                try{
                    cId = Long.parseLong(candidateId, 10);
                } catch (NumberFormatException e){
                    Logger.info("Could not convert "+candidateId+" to Long. Throws "+ e.getMessage());
                }
                Logger.info("cId = "+ cId);
            }
            else Logger.info("candidateId is null");

            if (resume != null) {
                String fileName = resume.getFilename();
                Logger.info("fileName="+fileName);
                File file = (File) resume.getFile();
                Logger.info("Uploading! " + file);
                JSONObject obj = CandidateService.uploadResume(file, fileName, cId);

                resumeResponse.setResume((String) obj.get("resume"));
                resumeResponse.setMsg((String) obj.get("msg"));
                resumeResponse.setStatus((Integer) obj.get("status"));

                if(cId != null){

                    CandidateResumeService resumeService = new CandidateResumeService();
                        // Found candidate
                        CandidateResume candidateResume = (CandidateResume) resumeService.fetchLatestResumeForCandidate(String.valueOf(cId)).getEntity();
                        if(candidateResume !=null){
                            resumeResponse.setCandidateResumeLink(candidateResume.getFilePath());
                            resumeResponse.setCandidateId(candidateResume.getCandidate().getCandidateId());
                        }
                }

                try {
                    resumeResponse.setKey("resume"+i);
                    obj.put("key","Resume_"+i);
                } catch (org.json.JSONException e) {
                    e.printStackTrace();
                }

                Logger.info("obj.toString() ="+obj.toString());
                list.add(obj);
            }
        }
        Logger.info("list.toString() = "+list.toString());

        return ok(toJson(resumeResponse));

    }

    public static Result receiveParsedResume() {
        //Logger.info("Entered receiveParsedResume");
        //Logger.info("request.asFormUrlEncoded().keySet().size()="+request().body().asFormUrlEncoded().keySet().size());

        // build json string
        String json = "{";
        for(String key:request().body().asFormUrlEncoded().keySet()){
            json += "\""+key+"\":";
            switch(key){
                case "profilemergedto":
                case "personid":
                case "status":
                case "message":
                case "type":
                    json += "\""+StringUtils.join(request().body().asFormUrlEncoded().get(key),"")+"\"";
                    break;
                default:
                    json += StringUtils.join(request().body().asFormUrlEncoded().get(key),"");
            }
            json += ",";
        }
        json = StringUtils.removeEnd(json,",");
        json += "}";

        Logger.info("json = "+json);

        HireWandResponse hireWandResponse = null;
        // try to map it to HireWandResponse
        ObjectMapper newMapper = new ObjectMapper();
        try {
            hireWandResponse = newMapper.readValue(json, HireWandResponse.class);
        } catch (IOException e) {
            e.printStackTrace();
            Logger.info("Error while mapping from request to HireWandResponse"+e.getMessage());
            return internalServerError();
        }
        if(hireWandResponse != null &&
                hireWandResponse.getStatus()!=null &&
                hireWandResponse.getStatus().toLowerCase().contains("success")){

            // mapped successfully
            Logger.info("Successfully mapped json to hireWandResponse");

            // keep a copy of the raw profile string
            hireWandResponse.getProfile().setProfileJSON(StringUtils.join(request().body().asFormUrlEncoded().get("profile"),null));
            Logger.info("hireWandResponse.Profile.ProfileJSON="+hireWandResponse.getProfile().getProfileJSON());

            // is this a duplicate?
            if(hireWandResponse.getProfilemergedto() != null) {
                // if yes, set unique profile id
                hireWandResponse.getProfile().setProfilemergedto(hireWandResponse.getProfilemergedto());
                Logger.info("hireWandResponse.getProfile().getProfilemergedto="+hireWandResponse.getProfile().getProfilemergedto());
            }

            // ensure external key (PersonId) is filled before calling business logic
            if(!hireWandResponse.getPersonid().isEmpty()){
                // send for processing
                Logger.info("Updating resume for HireWand personid = "+hireWandResponse.getPersonid());
                JSONObject resp = CandidateService.updateResume(hireWandResponse.getPersonid(), hireWandResponse.getProfile(),hireWandResponse.getDuplicate());
                Logger.info("resp="+resp.toString());
                return ok(resp+"");
            }
            else {
                Logger.info("Hirewand callback invoked with empty PersonID");
                return badRequest();
            }
        }
        else{
            if(hireWandResponse == null){
                Logger.info("Failed to map json to hireWandResponse");
                return internalServerError();
            }
            else if (hireWandResponse.getStatus()!=null &&
                    !hireWandResponse.getStatus().toLowerCase().contains("success")){
                Logger.info("hireWandResponse returned status = "+hireWandResponse.getStatus());
                return ok();
            }
        }
        return ok();
    }

    @Security.Authenticated(PartnerInternalSecured.class)
    public static Result uploadCandidates() {
        return ok(views.html.upload_candidate_excel.render());
    }

    public static Result fetchResume(String candidateId) {

/*
        CandidateResumeService resumeService = new CandidateResumeService();
        List<Map<String,String>> params = new ArrayList<>();
        Map<String,String> param = new HashMap<>();
        param.put("candidateid",candidateId);
        params.add(param);
        List<TruResponse> truResponses = resumeService.readByAttribute(params);
*/
        //if(truResponses !=null && truResponses.size() > 0){
           // Found candidate
            //CandidateResume candidateResume = (CandidateResume) truResponses.get(0).getEntity();
        CandidateResumeService resumeService = new CandidateResumeService();
        CandidateResume candidateResume = (CandidateResume) resumeService.fetchLatestResumeForCandidate(candidateId).getEntity();
        if(candidateResume != null) return ok(toJson(candidateResume));
        else{ return ok(); }
    
    }
    @Security.Authenticated(PartnerSecured.class)
    public static Result processCandidates() {
        Http.MultipartFormData body = request().body().asMultipartFormData();
        Http.MultipartFormData.FilePart excel = body.getFile("file");
        if (excel != null) {
            String fileName = excel.getFilename();
            Logger.info("fileName=" + fileName);
            File file = (File) excel.getFile();
            Logger.info("Uploading " + file);
            BulkUploadResponse bulkUploadResponse = CandidateService.bulkUploadCandidates(file,fileName);
            return ok(toJson(bulkUploadResponse));
        }
        else{
            return internalServerError("Bulk upload failed due to an internal error");
        }
    }

    public static Result getDeactivationMessage(Long candidateId) {
        DeActivationStatusResponse response = new DeActivationStatusResponse();

        if(candidateId == null && session().get("candidateId")!= null) {
            candidateId = Long.valueOf(session().get("candidateId"));
        }
        response.setDeActivationMessage(CandidateService.getDeActivationMessage(candidateId));

        return ok(toJson(response));
    }

    @Security.Authenticated(SecuredUser.class)
    public static Result updateRecruiterCreditPack() {
        JsonNode req = request().body().asJson();
        AddRecruiterRequest addRecruiterRequest = new AddRecruiterRequest();
        ObjectMapper newMapper = new ObjectMapper();
        try {
            addRecruiterRequest = newMapper.readValue(req.toString(), AddRecruiterRequest.class);
        } catch (IOException e) {
            e.printStackTrace();
        }

        RecruiterProfile recruiterProfile = RecruiterProfile.find.where()
                .eq("RecruiterProfileMobile", FormValidator.convertToIndianMobileFormat(addRecruiterRequest.getRecruiterMobile()))
                .findUnique();

        if(recruiterProfile != null){

            String createdBy = "Not specified";

            if(session().get("sessionUsername") != null){
                createdBy = "Support: " + session().get("sessionUsername");
            }

            return ok(toJson(RecruiterService.updateExistingRecruiterPack(recruiterProfile, addRecruiterRequest.getPackId(),
                    addRecruiterRequest.getCreditCount(), createdBy, addRecruiterRequest.getExpiryDate())));
        }

        return ok("0");

    }

    @Security.Authenticated(SecuredUser.class)
    public static Result expireCreditPack() {
        JsonNode req = request().body().asJson();
        AddRecruiterRequest addRecruiterRequest = new AddRecruiterRequest();
        ObjectMapper newMapper = new ObjectMapper();
        try {
            addRecruiterRequest = newMapper.readValue(req.toString(), AddRecruiterRequest.class);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return ok(toJson(RecruiterService.expireCreditPack(addRecruiterRequest)));

    }

    public static Result getMissingData(Long jobPostId, Long candidateId) {

        return ok(toJson(JobPostWorkflowEngine.getShortJobApplyResponse(jobPostId, candidateId)));
    }

    public static Result updateCandidateDetailsViaShortJobApply() throws IOException, JSONException {
        JsonNode updateCandidateDetailJSON = request().body().asJson();

        PostApplyInShortResponse response = new PostApplyInShortResponse();

        Logger.info("Apply In Short | Browser: " +  request().getHeader("User-Agent") + "; Req JSON : " + updateCandidateDetailJSON);

        if(updateCandidateDetailJSON == null){
            return badRequest();
        }

        ObjectMapper newMapper = new ObjectMapper();
        // since jsonReq has single/multiple values in array
        newMapper.enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY);

        ApplyInShortRequest request = newMapper.readValue(updateCandidateDetailJSON.toString(), ApplyInShortRequest.class);

        boolean isVerifyAadhaar;

        if(request == null) {
            response.setStatus(PostApplyInShortResponse.Status.BAD_REQUEST);
            return badRequest(toJson(response));
        }

        if(request.getCandidateId() == null || request.getCandidateId() < 1 || request.getJobPostId() == null
                || request.getJobPostId() < 1) {

            response.setStatus(PostApplyInShortResponse.Status.BAD_PARAMS);
            return badRequest(toJson(response));
        }

        Candidate candidate = CandidateDAO.getById(request.getCandidateId());

        if(candidate == null) {
            response.setStatus(PostApplyInShortResponse.Status.BAD_REQUEST);
            return badRequest(toJson(response));
        }

        Auth existingAuth = Auth.find.where().eq("candidateId", request.getCandidateId()).findUnique();

        // update auth otp after login
        existingAuth.setOtp(Util.generateOtp());
        existingAuth.update();

        String deActivationMessage = CandidateService.getDeActivationMessage(candidate);
        if(deActivationMessage != null) {

            response.setStatus(PostApplyInShortResponse.Status.CANDIDATE_DEACTIVE);
            response.setMessage(deActivationMessage);
            return badRequest(toJson(response));
        }

        // #1. update locality
        ApplyJobRequest applyJobRequest = new ApplyJobRequest();
        applyJobRequest.setJobId(request.getJobPostId());
        applyJobRequest.setLocalityId(request.getLocalityId());
        applyJobRequest.setCandidateMobile(candidate.getCandidateMobile());
        applyJobRequest.setPartner(false);
        applyJobRequest.setAppVersionCode(0);

        Integer channelId = Integer.parseInt(session().get("sessionChannel"));
        int channelType = channelId == null ? InteractionConstants.INTERACTION_CHANNEL_UNKNOWN : channelId;
        ApplyJobResponse applyJobResponse = JobService.applyJob(applyJobRequest, channelType, InteractionConstants.INTERACTION_TYPE_APPLIED_JOB_IN_SHORT, true);

        if(applyJobResponse.getStatus() == ApplyJobResponse.STATUS_EXISTS){
            response.setStatus(PostApplyInShortResponse.Status.ALREADY_APPLIED);
        }

        // #2. update candidate interview detail
        if(request.getTimeSlotId() != null && request.getDateInMillis()!= null){
            AddCandidateInterviewSlotDetail interviewSlotDetail = new AddCandidateInterviewSlotDetail();

            interviewSlotDetail.setScheduledInterviewDate(new Date(request.getDateInMillis()));
            interviewSlotDetail.setTimeSlot(request.getTimeSlotId());

            JobPostWorkflowEngine.updateCandidateInterviewDetail(candidate.getCandidateId(), request.getJobPostId(), interviewSlotDetail,  channelType);
        }

        // #3. update candidate info
        isVerifyAadhaar = CandidateService.updateCandidateDetail(request.getPropertyIdList(), candidate, request.getUpdateCandidateDetail());

        if (isVerifyAadhaar) {
            CandidateService.verifyAadhaar(candidate.getCandidateMobile());
        }


        response.setStatus(PostApplyInShortResponse.Status.SUCCESS);
        return  ok(toJson(response));
    }
    public static Result getInterviewSlots(Long jobPostId) {
        if (jobPostId == null) {
            return badRequest();
        }

        JobPost jobPost = JobPostDAO.findById(jobPostId);

        if (jobPost == null) {
            return badRequest();
        }

        InterviewResponse interviewResponse = RecruiterService.isInterviewRequired(jobPost);

        InterviewSlotPopulateResponse response =
                new InterviewSlotPopulateResponse(
                        JobService.getInterviewSlot(jobPost), interviewResponse, jobPost);

        return ok(toJson(response));
    }

    /**
     *
     * @param jobPostId
     * @return
     *
     *  This class provides minimal jobp post data required to fill filters of
     * private recruiter 'search candidate page'
     *
     */
    public static Result getJobPostFilterData(Long jobPostId) {
        if(jobPostId == null)
            return badRequest();

        JobPost jobPost = JobPostDAO.findById(jobPostId);

        if(jobPost == null)
            return badRequest();

        JobPostFilterResponse response = new JobPostFilterResponse();

        response.setJobPostId(jobPost.getJobPostId());
        response.setJobPostTitle(jobPost.getJobPostTitle());
        response.setGender(jobPost.getGender());
        response.setMaxSalary(jobPost.getJobPostMaxSalary());
        response.setJobPostJobRoleId(jobPost.getJobRole().getJobRoleId());
        response.setJobPostJobRoleTitle(jobPost.getJobRole().getJobName());
        // add document
        if(jobPost.getJobPostDocumentRequirements() != null
                && jobPost.getJobPostDocumentRequirements().size() > 0) {
            response.setJobPostDocumentIdList(new ArrayList<>());
            for(JobPostDocumentRequirement documentRequirement : jobPost.getJobPostDocumentRequirements()){
                response.getJobPostDocumentIdList().add( documentRequirement.getIdProof().getIdProofId());
            }
        }

        // add asset
        if(jobPost.getJobPostAssetRequirements() != null
                && jobPost.getJobPostAssetRequirements().size() > 0) {
            response.setJobPostAssetIdList(new ArrayList<>());
            for(JobPostAssetRequirement assetRequirement : jobPost.getJobPostAssetRequirements()){
                response.getJobPostAssetIdList().add(assetRequirement.getAsset().getAssetId());
            }
        }


        // add language
        if(jobPost.getJobPostLanguageRequirements() != null
                && jobPost.getJobPostLanguageRequirements().size() > 0) {
            response.setJobPostLanguageIdList(new ArrayList<>());
            for(JobPostLanguageRequirement languageRequirement : jobPost.getJobPostLanguageRequirements()){
                response.getJobPostLanguageIdList().add( languageRequirement.getLanguage().getLanguageId());
            }
        }

        // add locality
        if(jobPost.getJobPostToLocalityList() != null
                && jobPost.getJobPostToLocalityList().size() > 0) {
            response.setJobPostLocalityIdList(new ArrayList<>());
            response.setJobPostLocalityList(new ArrayList<>());
            for(JobPostToLocality jobPostToLocality : jobPost.getJobPostToLocalityList()){
                response.getJobPostLocalityIdList().add( jobPostToLocality.getLocality().getLocalityId());
                response.getJobPostLocalityList().add(
                        new JobPostFilterResponse.LocalityIdName(
                            jobPostToLocality.getLocality().getLocalityId(),
                            jobPostToLocality.getLocality().getLocalityName())
                        );
            }
        }

        // add experience
        if(jobPost.getJobPostExperience() != null) {
            response.setJobPostExperienceId(jobPost.getJobPostExperience().getExperienceId());
        }

        // add education
        if(jobPost.getJobPostEducation() != null) {
            response.setJobPostEducationId(jobPost.getJobPostEducation().getEducationId());
        }


        return ok(toJson(response));
    }

    public static Result generateCompanyCode() {
        List<Company> companyList = CompanyDAO.getCompaniesWithoutCompanyCode();
        for (Company company : companyList) {
            company.setCompanyCode(Util.generateCompanyCode(company));
            company.update();
        }
        return ok("Done");
    }

    public static Result doQuickApply() {

        JsonNode req = request().body().asJson();
        Logger.info("Browser: " +  request().getHeader("User-Agent") + "; Req JSON : " + req );
        ApplyJobRequest applyJobRequest = new ApplyJobRequest();
        ObjectMapper newMapper = new ObjectMapper();
        try {
            applyJobRequest = newMapper.readValue(req.toString(), ApplyJobRequest.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ok(toJson(JobService.callToApply(applyJobRequest)));
    }

}
