package controllers;

import api.ServerConstants;
import api.http.httpRequest.*;
import api.http.httpResponse.AddLeadResponse;
import api.http.httpResponse.SupportDashboardElementResponse;
import api.http.httpResponse.SupportInteractionResponse;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import controllers.businessLogic.AuthService;
import controllers.businessLogic.CandidateService;
import controllers.businessLogic.JobService;
import controllers.businessLogic.LeadService;
import models.entity.*;
import models.entity.OM.JobToSkill;
import models.entity.OM.LocalityPreference;
import models.entity.Static.*;
import models.util.ParseCSV;
import models.util.SmsUtil;
import models.util.Util;
import play.Logger;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;

import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static play.libs.Json.toJson;

public class Application extends Controller {

    public static Result index() {
        String sessionId = session().get("sessionId");
        if(sessionId != null){
            return redirect("/dashboard");
        }
        return ok(views.html.index.render());
    }

    @Security.Authenticated(Secured.class)
    public static Result support() {
        String sessionId = session().get("sessionId");
        Developer developer = Developer.find.where().eq("developerSessionId", sessionId ).findUnique();
        if(developer != null && developer.getDeveloperAccessLevel() == ServerConstants.DEV_ACCESS_LEVEL_SUPPORT_ROLE) {
            return ok(views.html.support.render());
        }
        return redirect("/street");
    }
    @Security.Authenticated(Secured.class)
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
                    case 1: response.setUserInteractionType("Incoming Call"); break;
                    case 2: response.setUserInteractionType("Out Going Call"); break;
                    case 3: response.setUserInteractionType("Incoming SMS"); break;
                    case 4: response.setUserInteractionType("Out Going SMS"); break;
                    case 5: response.setUserInteractionType("Website Interaction"); break;
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
        boolean isSupport = false;
        LeadService.createLead(lead, isSupport);
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

        boolean isSupport = false;
        return ok(toJson(CandidateService.signUpCandidate(candidateSignUpRequest, isSupport, ServerConstants.LEAD_SOURCE_UNKNOWN)));
    }
    @Security.Authenticated(Secured.class)
    public static Result signUpSupport() {
        JsonNode req = request().body().asJson();
        AddSupportCandidateRequest addSupportCandidateRequest = new AddSupportCandidateRequest();
        ObjectMapper newMapper = new ObjectMapper();
        try {
            addSupportCandidateRequest = newMapper.readValue(req.toString(), AddSupportCandidateRequest.class);
            Logger.info("json" + req.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
        boolean isSupport = true;
        return ok(toJson(CandidateService.createCandidateProfile(addSupportCandidateRequest, isSupport, ServerConstants.UPDATE_ALL_BY_SUPPORT)));
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
        boolean isSupport = false;
        return ok(toJson(CandidateService.createCandidateProfile(addCandidateRequest, isSupport, ServerConstants.UPDATE_BASIC_PROFILE)));
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
        boolean isSupport = false;
        return ok(toJson(CandidateService.createCandidateProfile(addCandidateExperienceRequest, isSupport, ServerConstants.UPDATE_SKILLS_PROFILE)));
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
        boolean isSupport = false;
        return ok(toJson(CandidateService.createCandidateProfile(addCandidateEducationRequest, isSupport, ServerConstants.UPDATE_EDUCATION_PROFILE)));
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

        return ok(toJson(AuthService.savePassword(userMobile, userPassword)));
    }

    public static Result applyJob() {
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

        return ok(toJson(JobService.applyJob(userMobile, jobId)));
    }

    public static Result addJobPost() {
        JsonNode req = request().body().asJson();
        AddJobPostRequest addJobPostRequest = new AddJobPostRequest();
        ObjectMapper newMapper = new ObjectMapper();
        try {
            addJobPostRequest = newMapper.readValue(req.toString(), AddJobPostRequest.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ok(toJson(JobService.addJobPost(addJobPostRequest)));
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

        return ok(toJson(CandidateService.login(loginMobile, loginPassword)));
    }

    @Security.Authenticated(SecuredUser.class)
    public static Result dashboard() {
        return ok(views.html.candidate_home.render());
    }

    @Security.Authenticated(SecuredUser.class)
    public static Result editProfile() {
        return ok(views.html.edit_profile.render());
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
        return ok(toJson(CandidateService.findUserAndSendOtp(candidateMobile)));
    }

    public static Result processcsv() {
        java.io.File file = (File) request().body().asMultipartFormData().getFile("file").getFile();
        if(file == null) {
            return badRequest("error uploading file. Check file type");
        }
        return ok(toJson(ParseCSV.parseCSV(file)));
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
            case 3: // get all
                allLead = Lead.find.where()
                        .ne("leadStatus", ServerConstants.LEAD_STATUS_LOST)
                        .findList();
                break;
        }

        ArrayList<SupportDashboardElementResponse> responses = new ArrayList<>();

        SimpleDateFormat sfd = new SimpleDateFormat(ServerConstants.SDF_FORMAT);

        //getting leadUUID from allLead
        List<String> leadUUIDList = allLead.stream().map(Lead::getLeadUUId).collect(Collectors.toList());

        List<Interaction> interactionsOfLead = Interaction.find.where().in("objectAUUId", leadUUIDList).findList();

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
            responses.add(response);
        }

        return ok(toJson(responses));
    }
    @Security.Authenticated(Secured.class)
    public static Result getUserInfo(long id) {
        try{
            Lead lead = Lead.find.where().eq("leadId",id).findUnique();
            String leadMobile = lead.getLeadMobile();
            return ok(leadMobile);
        } catch (NullPointerException n){
            Logger.info("Create new candidate initiated");
        }
        return ok();
    }
    @Security.Authenticated(Secured.class)
    public static Result getCandidateInfo(long leadId) {
            Lead lead = Lead.find.where().eq("leadId", leadId).findUnique();
            if(lead != null) {
                Candidate candidate = Candidate.find.where().eq("lead_leadId", lead.getLeadId()).findUnique();
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

    public static Result getJobPostInfo(long jobPostId) {
        JobPost jobPost = JobPost.find.where().eq("jobPostId", jobPostId).findUnique();
        if(jobPost!=null){
            return ok(toJson(jobPost));
        }
        return ok("0");
    }

    @Security.Authenticated(SecuredUser.class)
    public static Result getCandidateLocality(long candidateId) {
        List<LocalityPreference> candidateLocalities = LocalityPreference.find.where().eq("candidateId", candidateId).findList();
        if(candidateLocalities == null)
            return ok("0");
        return ok(toJson(candidateLocalities));
    }

    public static Result checkMinProfile(long id) {
        Candidate existingCandidate = Candidate.find.where().eq("candidateId", id).findUnique();
        return ok(toJson(existingCandidate.getIsMinProfileComplete()));
    }

    @Security.Authenticated(Secured.class)
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
        String sessionId = session().get("sessionId");
        if(sessionId != null){
            return ok(views.html.candidate_home.render());
        }
        else{
            return ok(views.html.index.render());
        }
    }
    public static Result auth() {
        Form<DevLoginRequest> userForm = Form.form(DevLoginRequest.class);
        DevLoginRequest request = userForm.bindFromRequest().get();
        Logger.info("inside support AdminId: " + request.getAdminid() + " AdminPass: " + request.getAdminpass());
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
                if(developer.getDeveloperAccessLevel() == ServerConstants.DEV_ACCESS_LEVEL_SUPPORT_ROLE){
                    return redirect(routes.Application.support());
                }
                if(developer.getDeveloperAccessLevel() == ServerConstants.DEV_ACCESS_LEVEL_UPLOADER) {
                    return ok(views.html.uploadcsv.render());
                }
            }
        } else {
            return badRequest("Account Doesn't exists!!");
        }
        return redirect(routes.Application.supportAuth());
    }

    public static Result updateIsAssessedToAssessed(long candidateId) {
        try{
            Candidate existingCandidate = Candidate.find.where().eq("candidateId", candidateId).findUnique();
            try{
                existingCandidate.setCandidateIsAssessed(ServerConstants.CANDIDATE_ASSESSED);
                existingCandidate.update();
                return ok(toJson(ServerConstants.CANDIDATE_ASSESSED));
            } catch (NullPointerException n) {
                n.printStackTrace();
            }
        } catch (NullPointerException n) {
            n.printStackTrace();
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
                            ServerConstants.INTERACTION_TYPE_CALL_OUT,
                            ServerConstants.INTERACTION_NOTE_LEAD_TYPE_CHANGED,
                            ServerConstants.INTERACTION_RESULT_SYSTEM_UPDATED_LEADTYPE + newType,
                            session().get("sessionUsername")
                    );
                    interaction.save();
                } else {
                    // TODO: interaction type to be defined
                    Interaction interaction = new Interaction(
                            lead.getLeadUUId(),
                            lead.getLeadType(),
                            ServerConstants.INTERACTION_TYPE_CALL_OUT,
                            ServerConstants.INTERACTION_NOTE_LEAD_TYPE_CHANGED,
                            ServerConstants.INTERACTION_RESULT_SYSTEM_UPDATED_LEADTYPE + newType,
                            session().get("sessionUsername")
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
                    interactionNote = ServerConstants.INTERACTION_NOTE_LEAD_STATUS_CHANGED;

                } else {
                    interactionNote = ServerConstants.INTERACTION_NOTE_CALL_OUTBOUNDS;
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
                        ServerConstants.INTERACTION_TYPE_CALL_OUT,
                        interactionNote,
                        interactionResult,
                        session().get("sessionUsername")
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
        return ok("TODO");
    }

    public static Result getAllJobPosts() {
        List<JobPost> jobPosts = JobPost.find.findList();
        return ok(toJson(jobPosts));
    }
    public static Result getAllLocality() {
        List<Locality> localities = Locality.find.findList();
        return ok(toJson(localities));
    }
    public static Result getAllJobs() {
        List<JobRole> jobs = JobRole.find.findList();
        return ok(toJson(jobs));
    }
    @Security.Authenticated(Secured.class)
    public static Result getAllShift() {
        List<TimeShift> timeShifts = TimeShift.find.findList();
        return ok(toJson(timeShifts));
    }
    @Security.Authenticated(Secured.class)
    public static Result getAllTransportation() {
        List<TransportationMode> transportationModes = TransportationMode.find.findList();
        return ok(toJson(transportationModes));
    }
    @Security.Authenticated(Secured.class)
    public static Result getAllEducation() {
        List<Education> educations = Education.find.findList();
        return ok(toJson(educations));
    }
    @Security.Authenticated(Secured.class)
    public static Result getAllLanguage() {
        List<Language> languages = Language.find.findList();
        return ok(toJson(languages));
    }
    @Security.Authenticated(Secured.class)
    public static Result getAllIdProof() {
        List<IdProof> idProofs = IdProof.find.findList();
        return ok(toJson(idProofs));
    }
    @Security.Authenticated(Secured.class)
    public static Result getAllDegree() {
        List<Degree> degreeList = Degree.find.findList();
        return ok(toJson(degreeList));
    }

    @Security.Authenticated(Secured.class)
    public static Result candidateSignupSupport(Long candidateId) {
        return ok(views.html.signup_support.render(candidateId));
    }

    public static Result createCandidateForm() {
        return redirect("/candidateSignupSupport/"+"0");
    }
    @Security.Authenticated(Secured.class)
    public static Result searchCandidate() {
        return ok(views.html.search.render());
    }
    @Security.Authenticated(Secured.class)
    public static Result getSearchCandidateResult() {
        JsonNode searchReq = request().body().asJson();
        if(searchReq == null){
            return badRequest();
        }

        SearchCandidateRequest searchCandidateRequest = new SearchCandidateRequest();
        ObjectMapper newMapper = new ObjectMapper();
        try {
            searchCandidateRequest = newMapper.readValue(searchReq.toString(), SearchCandidateRequest.class);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return ok(toJson(CandidateService.searchCandidateBySupport(searchCandidateRequest)));
    }
    @Security.Authenticated(Secured.class)
    public static Result getAllLeadSource() {
        List<LeadSource> leadSources = LeadSource.find.all();
        return ok(toJson(leadSources));
    }
    @Security.Authenticated(Secured.class)
    public static Result getSupportAgent() {
        String agentMobile = "+91" + session().get("sessionUserId");
        if(agentMobile.length() == 13){
            return ok(toJson(agentMobile));
        }
        return ok("0");
    }
}
