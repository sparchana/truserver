package controllers;

import api.ServerConstants;
import api.http.*;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import controllers.businessLogic.AuthService;
import controllers.businessLogic.CandidateService;
import controllers.businessLogic.LeadService;
import models.entity.Candidate;
import models.entity.Developer;
import models.entity.Interaction;
import models.entity.Lead;
import models.entity.OM.*;
import models.entity.OO.TimeShiftPreference;
import models.entity.Static.*;
import models.util.ParseCSV;
import models.util.Util;
import models.util.Validator;
import play.Logger;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;

import javax.persistence.PersistenceException;
import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static controllers.businessLogic.CandidateService.getCandidateJobPreferenceList;
import static controllers.businessLogic.CandidateService.getCandidateLocalityPreferenceList;
import static play.libs.Json.toJson;

public class Application extends Controller {

    public static Result index() {
        String sessionId = session().get("sessionId");
        if(sessionId != null){
            return ok(views.html.candidate_home.render());
        }
        return ok(views.html.index.render());
    }

    @Security.Authenticated(Secured.class)
    public static Result support() {
        String sessionId = session().get("sessionId");
        Developer developer = Developer.find.where().eq("developerSessionId", sessionId ).findUnique();
        if(developer != null && developer.developerAccessLevel == ServerConstants.DEV_ACCESS_LEVEL_SUPPORT_ROLE) {
            return ok(views.html.support.render());
        }
        return redirect("/street");
    }
    @Security.Authenticated(Secured.class)
    public static Result candidateInteraction(long id) {
        return ok(views.html.candidate_interaction.render());
    }
    @Security.Authenticated(Secured.class)
    public static Result getCandidateInteraction(long id){
        Lead lead = Lead.find.where().eq("leadId",id).findUnique();
        if(lead !=null){
            List<Interaction> fullInteractionList = Interaction.find.where().eq("objectAUUId", lead.getLeadUUId()).findList();

            // fetch candidate interaction as well
            Candidate candidate = Candidate.find.where().eq("lead_leadId", id).findUnique();
            if(candidate != null){
                List<Interaction> candidateInteractionList = Interaction.find.where().eq("objectAUUId", candidate.candidateUUId).findList();
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
        Form<AddLeadRequest> userForm = Form.form(AddLeadRequest.class);
        AddLeadRequest addLeadRequest = userForm.bindFromRequest().get();

        AddLeadResponse addLeadResponse = new AddLeadResponse();
        Lead lead = new Lead(addLeadRequest.getLeadName(),
                "+91" + addLeadRequest.getLeadMobile(),
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
        Form<CandidateSignUpRequest> candidateForm = Form.form(CandidateSignUpRequest.class);
        CandidateSignUpRequest candidateSignUpRequest = candidateForm.bindFromRequest().get();
        List<String> localityList = Arrays.asList(candidateSignUpRequest.getCandidateLocality().split("\\s*,\\s*"));
        List<String> jobsList = Arrays.asList(candidateSignUpRequest.getCandidateJobPref().split("\\s*,\\s*"));
        boolean isSupport = false;
        Candidate candidate = new Candidate();
        candidate.candidateId = Util.randomLong();
        candidate.candidateUUId = UUID.randomUUID().toString();
        candidate.candidateName = candidateSignUpRequest.getCandidateName();
        candidate.candidateLastName = candidateSignUpRequest.getCandidateSecondName();
        candidate.candidateMobile = "+91" + candidateSignUpRequest.getCandidateMobile();

        CandidateProfileStatus newcandidateProfileStatus = CandidateProfileStatus.find.where().eq("profileStatusId", 1).findUnique();
        candidate.candidateprofilestatus = newcandidateProfileStatus;
        candidate.localityPreferenceList  = getCandidateLocalityPreferenceList(localityList, candidate);
        candidate.jobPreferencesList = getCandidateJobPreferenceList(jobsList, candidate);

        return ok(toJson(CandidateService.createCandidate(candidate, isSupport, ServerConstants.LEAD_SOURCE_UNKNOWN)));
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
        return ok(toJson(CandidateService.createCandidateBySupport(addSupportCandidateRequest)));
    }

    public static Result addPassword() {
        Form<CandidateSignUpRequest> candidateForm = Form.form(CandidateSignUpRequest.class);
        CandidateSignUpRequest candidateSignUpRequest = candidateForm.bindFromRequest().get();

        String userMobile = candidateSignUpRequest.getCandidateAuthMobile();
        String userPassword = candidateSignUpRequest.getCandidatePassword();

        return ok(toJson(AuthService.savePassword(userMobile, userPassword)));
    }

    public static Result loginSubmit() {
        Form<LoginRequest> loginForm = Form.form(LoginRequest.class);
        LoginRequest loginRequest = loginForm.bindFromRequest().get();
        String loginMobile = loginRequest.getCandidateLoginMobile();
        String loginPassword = loginRequest.getCandidateLoginPassword();

        return ok(toJson(CandidateService.login(loginMobile, loginPassword)));
    }

    @Security.Authenticated(SecuredUser.class)
    public static Result dashboard() {
        return ok(views.html.candidate_home.render());
    }

    public static Result findUserAndSendOtp() {
        Form<ResetPasswordResquest> checkCandidate = Form.form(ResetPasswordResquest.class);
        ResetPasswordResquest resetPasswordResquest = checkCandidate.bindFromRequest().get();

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

        for(Lead l : allLead) {
            SupportDashboardElementResponse response = new SupportDashboardElementResponse();

            response.setLeadCreationTimestamp(sfd.format(l.getLeadCreationTimestamp()));
            response.setLeadId(l.leadId);
            response.setLeadName(l.leadName);
            response.setLeadMobile(l.leadMobile);
            switch (l.leadStatus) {
                case 0: response.setLeadStatus("New"); break;
                case 1: response.setLeadStatus("T.T.C"); break;
                case 2: response.setLeadStatus("Won"); break;
                case 3: response.setLeadStatus("Lost"); break;
            }
            switch (l.leadType) {
                case 0: response.setLeadType("Fresh"); break;
                case 1: response.setLeadType("Lead"); break;
                case 2: response.setLeadType("Potential Candidate"); break;
                case 3: response.setLeadType("Potential Recruiter"); break;
                case 4: response.setLeadType("Candidate"); break;
                case 5: response.setLeadType("Recruiter"); break;
            }
            switch (l.leadChannel) {
                case 0: response.setLeadChannel("Website"); break;
                case 1: response.setLeadChannel("Knowlarity"); break;
            }
            int mTotalInteraction=0;
            Timestamp mostRecent = l.leadCreationTimestamp;
            for(Interaction i: interactionsOfLead) {
                if(i.objectAUUId.equals(l.getLeadUUId())){
                    if(i.getInteractionType() == 1 || i.getInteractionType() == 5) {
                        mTotalInteraction++;
                        if(mostRecent.getTime() <= i.creationTimestamp.getTime()){
                            mostRecent = i.creationTimestamp;
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
    @Security.Authenticated(SecuredUser.class)
    public static Result getCandidateLocality(long candidateId) {
        List<LocalityPreference> candidateLocalities = LocalityPreference.find.where().eq("candidateId", candidateId).findList();
        if(candidateLocalities == null)
            return ok("0");
        return ok(toJson(candidateLocalities));
    }
    @Security.Authenticated(SecuredUser.class)
    public static Result getCandidateJob(long id) {
        List<JobPreference> candidateJobs = JobPreference.find.where().eq("CandidateId", id).findList();
        return ok(toJson(candidateJobs));
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
        Logger.info("inside support" + request.toString());
        Developer developer = Developer.find.where().eq("developerId", request.getAdminid()).findUnique();
        if(developer!=null){
            Logger.info(Util.md5(request.getAdminpass() + developer.developerPasswordSalt));
            if(developer.developerPasswordMd5.equals(Util.md5(request.getAdminpass() + developer.developerPasswordSalt))) {
                developer.setDeveloperSessionId(UUID.randomUUID().toString());
                developer.setDeveloperSessionIdExpiryMillis(System.currentTimeMillis() + 24 * 60 * 60 * 1000);
                developer.update();
                session("sessionId", developer.developerSessionId);
                session("sessionUsername", developer.developerName);
                session("sessionUserId", "" + developer.developerId);
                session("sessionExpiry", String.valueOf(developer.developerSessionIdExpiryMillis));
                if(developer.developerAccessLevel == ServerConstants.DEV_ACCESS_LEVEL_SUPPORT_ROLE){
                    return redirect(routes.Application.support());
                }
                if(developer.developerAccessLevel == ServerConstants.DEV_ACCESS_LEVEL_UPLOADER) {
                    return ok(views.html.uploadcsv.render());
                }
            }
        } else {
            return badRequest("Account Doesn't exists!!");
        }
        return redirect(routes.Application.supportAuth());
    }
    @Security.Authenticated(Secured.class)
    public static Result updateLeadType(long leadId, long newType) {
        try{
            Lead lead = Lead.find.where().eq("leadId", leadId).findUnique();
            if(lead != null){
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

                return ok(toJson(newType));
            }
        } catch (NullPointerException n) {
            n.printStackTrace();
        }
        return badRequest();
    }
    @Security.Authenticated(Secured.class)
    public static Result updateLeadStatus(long leadId, int leadStatus, String interactionResult) {
        try {
            Lead lead = Lead.find.where().eq("leadId", leadId).findUnique();
            // A value is for overriding leadStatus is also there in Lead Model setLeadStatus
            if(lead != null){
                if(lead.leadStatus <= leadStatus){
                    switch (leadStatus) {
                        case 1: lead.setLeadStatus(ServerConstants.LEAD_STATUS_TTC);
                            break;
                        case 2: lead.setLeadStatus(ServerConstants.LEAD_STATUS_WON);
                            break;
                        case 3: lead.setLeadStatus(ServerConstants.LEAD_STATUS_LOST);
                            break;
                    }
                    Logger.info("updateLeadStatus invoked leadId:"+leadId+" status:" + leadStatus);
                    lead.update();
                }

                /* TODO: SEPERATE THIS INOT A METHOD */
                Interaction interaction = new Interaction(
                        lead.getLeadUUId(),
                        lead.getLeadType(),
                        ServerConstants.INTERACTION_TYPE_CALL_OUT,
                        ServerConstants.INTERACTION_NOTE_LEAD_STATUS_CHANGED,
                        ServerConstants.INTERACTION_RESULT_SYSTEM_UPDATED_LEADSTATUS + leadStatus,
                        session().get("sessionUsername")
                );
                interaction.save();
                return ok(toJson(lead.leadStatus));
            }
        } catch (NullPointerException n) {
            n.printStackTrace();
        }
        return badRequest();
    }

    public static Result kwCdrInput() {
        return ok("TODO");
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

    public static Result test(int n) {
        long testCandidateId = 73064660;
        switch (n) {
            case 1: // fetch in json
                try{
                    List<IDProofReference> idProofReferenceList = IDProofReference.find.all();
                    List<Candidate> candidates = Candidate.find.all();
                    for(IDProofReference i: idProofReferenceList) {
                        if(i != null){
                            Logger.info("idProofreference " + i.candidate);
                        }
                    }
                    return ok(toJson(candidates));
                } catch (NullPointerException e){
                    e.printStackTrace();
                }
                break;
            case 2: // insert new candidate with every sub-object filled and just save candidate obj
                Candidate candidate = new Candidate();
                candidate.candidateId = Util.randomLong();
                candidate.candidateUUId = UUID.randomUUID().toString();
                candidate.candidateMobile = "8111110011";
                candidate.candidateName = "frog";
                Lead lead = new Lead();
                lead.leadId = Util.randomLong();
                lead.leadMobile = candidate.candidateMobile;
                candidate.lead = lead;
                Logger.info("logged " + candidate.candidateId);

                // testcase related var set

                // create a sub-obj
                CandidateProfileStatus newcandidateProfileStatus = CandidateProfileStatus.find.where().eq("profileStatusId", 1).findUnique();
                if(newcandidateProfileStatus  == null){
                    newcandidateProfileStatus = new CandidateProfileStatus();
                    newcandidateProfileStatus.setProfileStatusId(1);
                    newcandidateProfileStatus.setProfileStatusName("New");
                    candidate.candidateprofilestatus = newcandidateProfileStatus ;
                } else {
                    candidate.candidateprofilestatus = newcandidateProfileStatus ;
                }
                // save the parent obj
                candidate.save();
                candidate = Candidate.find.where().eq("candidateId", candidate.candidateId).findUnique();
                return ok(toJson(candidate));
            case 3: // retrive a candidate obj and update sub-obj of candidate class
                Candidate retrievedCandidate = Candidate.find.where().eq("candidateId", testCandidateId).findUnique();
                System.out.println(retrievedCandidate.candidateName);

                // create a sub-obj
                newcandidateProfileStatus = CandidateProfileStatus.find.where().eq("profileStatusId", 1).findUnique();
                if(newcandidateProfileStatus  == null){
                    newcandidateProfileStatus = new CandidateProfileStatus();
                    newcandidateProfileStatus.setProfileStatusId(1);
                    newcandidateProfileStatus.setProfileStatusName("New");
                    retrievedCandidate.candidateprofilestatus = newcandidateProfileStatus;
                } else {
                    Logger.info("New Status:"+retrievedCandidate.candidateprofilestatus.profileStatusName);
                    retrievedCandidate.candidateprofilestatus = newcandidateProfileStatus;
                }
                retrievedCandidate.save();
                return ok(toJson(retrievedCandidate));
            case 4: // delete a candidate obj and check if the sub-obj data also gets cleared or not.
                try{
                    Candidate toDelete = Candidate.find.where().eq("candidateId", testCandidateId).findUnique();
                    Logger.info("Candidate Delete inititalted: " + toDelete.candidateName);
                    toDelete.delete();
                    Logger.info("Candidate Deleted check Jobpref and locPref ");
                } catch (NullPointerException e){
                    Logger.info("Candidate Doesnot exists");
                }
                break;
            case 5: // Testing validator
                String phoneNo = "7666666666";
                String result;
                if(Validator.isPhoneNumberValid(phoneNo)){
                    result = phoneNo + " is valid";
                } else {
                    result = phoneNo + " is Invalid";
                }
                String testName= "aaa+_as";
                if(Validator.isNameValid(testName)){
                    result = testName + " is valid";
                } else {
                    result = phoneNo + " is Invalid";
                }
                String testEmail= "test_a@yahoo.com";
                if(Validator.isEmailVaild(testName)){
                    result = testEmail + " is valid";
                } else {
                    result = testEmail + " is Invalid";
                }
                return  ok(result);
            case 6: // For existing Candidates
                // CandidateSkill Table Test
                String titleMsg = "Test 6 :Adding candidate Skill";
                try{
                    System.out.println(titleMsg);
                    Candidate candidate6 = Candidate.find.where().eq("candidateId", testCandidateId).findUnique();
                    System.out.println("Test 6 Initiated .......\n On CandidateId " + candidate6.candidateId);
                    CandidateSkill candidateSkill = new CandidateSkill();
                    candidateSkill.setUpdateTimeStamp(new Timestamp(System.currentTimeMillis()));

                    // TODO: since requres db query hence find a way
                    Skill skill = Skill.find.where().eq("skillId", 1).findUnique();
                    // binding both skill and candidate to candidateskill
                    if(skill == null){
                        System.out.println("Test 6: NPE, static table skill is not set");
                    } else{
                        candidateSkill.setSkill(skill);
                        System.out.println("Test 6: associating skill obj to candidateskill obj.............");
                    }
                    // get ref to existing skill
                    candidateSkill.setCandidate(candidate6);
                    System.out.println("Test 6: associating candidate obj to candidateskill obj.............");
                    candidate6.candidateSkillList.add(candidateSkill);
                    candidate6.save();
                    System.out.println("Test 6 Completed ");
                    candidate6 = Candidate.find.where().eq("candidateId", candidate6.candidateId).findUnique();
                    return ok(toJson(candidate6));
                } catch(NullPointerException n6){
                    System.out.println("Test 6 : candidateNotFound Check TestCase...");
                }
                break;
            case 7: // For existing Candidates
                // JobRole Table Test
                titleMsg = "Test 7 :Adding JobPref for a candidate";
                try{
                    System.out.println(titleMsg);
                    Candidate candidate7 = Candidate.find.where().eq("candidateId", testCandidateId).findUnique();
                    System.out.println("Test 7 Initiated .......\n On CandidateId " + candidate7.candidateId);

                    JobPreference jobPreference = new JobPreference();
                    JobRole jobRole = new JobRole();
                    jobRole.setJobRoleId(8);
                    jobPreference.jobRole=jobRole;
                    jobPreference.candidate = candidate7;
                    jobPreference.updateTimeStamp = new Timestamp(System.currentTimeMillis());
                    candidate7.jobPreferencesList.add(jobPreference);
                    candidate7.update();
                    System.out.println("Test 7 Completed ");
                    candidate7 = Candidate.find.where().eq("candidateId", candidate7.candidateId).findUnique();
                    return ok(toJson(candidate7));
                } catch(NullPointerException n6){
                    System.out.println("Test 7 : candidateNotFound Check TestCase...");
                }
                break;
            case 8: // For existing Candidates
                // JobHistory Table Test
                titleMsg = "Test 8 :Adding Job History";
                try{
                    System.out.println(titleMsg);
                    Candidate candidate8 = Candidate.find.where().eq("candidateId", testCandidateId).findUnique();
                    System.out.println("Test 8 Initiated .......\n On CandidateId " + candidate8.candidateId);

                    JobHistory candidateJobHistory = new JobHistory();
                    JobHistory candidateJobHistory2 = new JobHistory();
                    candidateJobHistory.setUpdateTimeStamp(new Timestamp(System.currentTimeMillis()));
                    candidateJobHistory2.setUpdateTimeStamp(new Timestamp(System.currentTimeMillis()));
                    candidateJobHistory.setCandidate(candidate8);
                    candidateJobHistory2.setCandidate(candidate8);
                    candidateJobHistory.setCandidatePastCompany("AGS");
                    candidateJobHistory2.setCandidatePastCompany("Microtek");
                    candidateJobHistory.setCandidatePastSalary(15000);
                    candidateJobHistory2.setCandidatePastSalary(20500);
                    // TODO: since requres db query hence find a way
                    JobRole jobRole = JobRole.find.where().eq("jobRoleId", 1).findUnique();
                    JobRole jobRole2 = JobRole.find.where().eq("jobRoleId", 2).findUnique();
                    // binding both JobRole and candidate to JobHistory
                    if(jobRole == null){
                        System.out.println("Test 8: NPE, static table skill is not set");
                    } else{
                        candidateJobHistory.setJobRole(jobRole);
                        candidateJobHistory2.setJobRole(jobRole2);
                        System.out.println("Test 8: associating skill obj to candidateJobHistory obj.............");
                    }
                    // get ref to existing skill
                    System.out.println("Test 8: associating candidate obj to candidateJobHistory obj.............");
                    candidate8.jobHistoryList.add(candidateJobHistory);
                    candidate8.jobHistoryList.add(candidateJobHistory2);
                    candidate8.update();
                    System.out.println("Test 8 Completed ");
                    candidate8 = Candidate.find.where().eq("candidateId", candidate8.candidateId).findUnique();
                    return ok(toJson(candidate8));
                } catch(NullPointerException n8){
                    System.out.println("Test 8 : candidateNotFound Check TestCase...");
                }
                break;
            case 9: // No candidates required/ always use save since this obj is getting created all the time
                titleMsg = "Test 9 :Add JobToSkill Mapping";
                try {
                    System.out.println(titleMsg);
                    System.out.println("Test 9 Initiated .......\n No candidateObj Req" );

                    JobToSkill jobToSkill = new JobToSkill();

                    // TODO: since requres db query hence find a way
                    JobRole jobRole = JobRole.find.where().eq("jobRoleId", 1).findUnique();
                    Skill skill= Skill.find.where().eq("skillId", 1).findUnique();
                    if(jobRole == null){
                        System.out.println("null jobRole" );
                    } else{
                        jobToSkill.setJobRole(jobRole);
                    }
                    if(skill == null){
                        System.out.println("null skill" );
                    } else{
                        jobToSkill.setSkill(skill);
                    }
                    // get ref to existing skill

                    jobToSkill.setUpdateTimeStamp(new Timestamp(System.currentTimeMillis()));
                    jobToSkill.save();
                    System.out.println("Test 9 Completed ");
                    List<JobToSkill> jobToSkillList = JobToSkill.find.all();
                    return ok(toJson(jobToSkillList));
                } catch(NullPointerException n9){
                    System.out.println("Test 9 : candidateNotFound Check TestCase...");
                }
                break;

            case 10: // For existing Candidates
                // LanguagePref Table Test
                titleMsg = "Test 10 :Adding LanguagePref and Mapping it to language table";
                try{
                    System.out.println(titleMsg);
                    Candidate candidate10 = Candidate.find.where().eq("candidateId", testCandidateId).findUnique();
                    System.out.println("Test 10 Initiated .......\n On CandidateId " + candidate10.candidateId);

                    // create one obj to add
                    LanguageKnown languageKnown = new LanguageKnown();
                    LanguageKnown languageKnown2 = new LanguageKnown();

                    // create sub-obj // this will be in a loop for multiple id
                    Language english = Language.find.where().eq("languageId", 1).findUnique();
                    languageKnown.setLanguage(english);
                    languageKnown.setUpdateTimeStamp(new Timestamp(System.currentTimeMillis()));
                    candidate10.languageKnownList.add(languageKnown);
                    Language hindi = Language.find.where().eq("languageId", 2).findUnique();
                    languageKnown2.setLanguage(hindi);
                    languageKnown2.setUpdateTimeStamp(new Timestamp(System.currentTimeMillis()));
                    candidate10.languageKnownList.add(languageKnown2);

                    candidate10.update();
                    System.out.println("Test 10 Completed ");
                    candidate10 = Candidate.find.where().eq("candidateId", candidate10.candidateId).findUnique();
                    return ok(toJson(candidate10));
                } catch(NullPointerException n8){
                    System.out.println("Test 10 : candidateNotFound Check TestCase...");
                }
                break;
            case 11: // For existing Candidates
                // TimeShift Table Test
                // one to one table
                // should allow update of existing data
                titleMsg = "Test 11 :Adding TimeShiftPref and Mapping it to TimeShift table";
                try{
                    System.out.println(titleMsg);
                    Candidate candidate11 = Candidate.find.where().eq("candidateId", testCandidateId).findUnique();
                    System.out.println("Test 11 Initiated .......\n On CandidateId " + candidate11.candidateId);

                    TimeShiftPreference timeShiftPreference = new TimeShiftPreference();
                    timeShiftPreference.setUpdateTimeStamp(new Timestamp(System.currentTimeMillis()));

                    TimeShift timeShift = TimeShift.find.where().eq("timeShiftId", 1).findUnique();
                    if(timeShift == null){
                        Logger.info("static table empty");
                    } else{
                        Logger.info("static table associating");
                        timeShiftPreference.setTimeShift(timeShift);
                        candidate11.timeShiftPreference = timeShiftPreference;
                    }
                    try {
                        candidate11.save();
                    } catch (PersistenceException p){

                    }
                    System.out.println("Test 11 Completed ");
                    candidate11 = Candidate.find.where().eq("candidateId", candidate11.candidateId).findUnique();
                    return ok(toJson(candidate11));
                } catch(NullPointerException n11){
                    n11.printStackTrace();
                    System.out.println("Test 11 : candidateNotFound Check TestCase...");
                }
                break;
            case 12:
                List<CandidateSkill> candidateSkillList = CandidateSkill.find.where().eq("CandidateId", 1).findList(); // working
                List<Skill> skillList = Skill.find.where().eq("SkillId", 1).findList(); // works
                List<JobToSkill> jobToSkillList = JobToSkill.find.where().eq("JobRoleId", 1).findList();
                return ok(toJson(jobToSkillList));
            case 13:
                Lead leadobj = Lead.find.where().eq("leadId", "10398425").findUnique();
                return ok(toJson(leadobj.leadSource));
            case 14:
                List<LeadSource> leadSourceList = LeadSource.find.all();
                return  ok(toJson(leadSourceList));
            case 15:
                String password = "Enter Password";
                int salt =  Util.randomInt();
                String md5Password = Util.md5(salt + password);
                return ok("Password: " + password + " salt: " + salt + " : md5 = " + md5Password);
        }
        return ok("");
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
        List<LeadSource> leadSources = LeadSource.find.findList();
        return ok(toJson(leadSources));
    }
}
