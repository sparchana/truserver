package controllers;

import api.ServerConstants;
import api.http.*;
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

import java.io.File;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

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

    public static Result addLead() {
        Form<AddLeadRequest> userForm = Form.form(AddLeadRequest.class);
        AddLeadRequest addLeadRequest = userForm.bindFromRequest().get();

        AddLeadResponse addLeadResponse = new AddLeadResponse();
        Lead lead = new Lead();
        lead.leadId = Util.randomLong();
        lead.leadUUId = UUID.randomUUID().toString();
        lead.leadName = addLeadRequest.getLeadName();
        lead.leadMobile = "+91" + addLeadRequest.getLeadMobile();
        lead.leadChannel = addLeadRequest.getLeadChannel();
        lead.leadType = ServerConstants.TYPE_LEAD;
        lead.leadStatus = ServerConstants.LEAD_STATUS_NEW;
        lead.leadInterest = addLeadRequest.getLeadInterest();
        Logger.info("going inside");
        LeadService.createLead(lead);
        addLeadResponse.setStatus(AddLeadResponse.STATUS_SUCCESS);
        return ok(toJson(addLeadResponse));
    }

    public static Result signUp() {
        Form<CandidateSignUpRequest> candidateForm = Form.form(CandidateSignUpRequest.class);
        CandidateSignUpRequest candidateSignUpRequest = candidateForm.bindFromRequest().get();

        Candidate candidate = new Candidate();
        candidate.candidateId = Util.randomLong();
        candidate.candidateUUId = UUID.randomUUID().toString();
        candidate.candidateName = candidateSignUpRequest.getCandidateName();
        candidate.candidateMobile = "+91" + candidateSignUpRequest.getCandidateMobile();
        CandidateProfileStatus newcandidateProfileStatus = CandidateProfileStatus.find.where().eq("profileStatusId", 1).findUnique();
        candidate.candidateprofilestatus = newcandidateProfileStatus;

        List<String> localityList = Arrays.asList(candidateSignUpRequest.getCandidateLocality().split("\\s*,\\s*"));
        List<String> jobsList = Arrays.asList(candidateSignUpRequest.getCandidateJobPref().split("\\s*,\\s*"));

        return ok(toJson(CandidateService.createCandidate(candidate,localityList,jobsList)));
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
        return ok(toJson(CandidateService.findUserAndSendOtp(candidateMobile)));
    }

    public static Result processcsv() {
        java.io.File file = (File) request().body().asMultipartFormData().getFile("file").getFile();
        if(file == null) {
            return badRequest("error uploading file. Check file type");
        }
        return ok(toJson(ParseCSV.parseCSV(file)));
    }

    public static Result getAll(){
        List<Lead> allLead = Lead.find.where()
                .ne("leadStatus", ServerConstants.LEAD_STATUS_WON)
                .ne("leadStatus", ServerConstants.LEAD_STATUS_LOST)
                .findList();

//        List<Interaction> allInteractions = Interaction.find.all();
//        List<Lead> allNewLeads = Lead.find.where()
//                .eq("leadType", ServerConstants.TYPE_LEAD)
//                .ne("leadStatus", ServerConstants.LEAD_STATUS_WON)
//                .eq("leadStatus", ServerConstants.LEAD_STATUS_NEW).findList();
        ArrayList<SupportDashboardElementResponse> responses = new ArrayList<>();

        SimpleDateFormat sfd = new SimpleDateFormat(ServerConstants.SDF_FORMAT);

        for(Lead l : allLead){
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
            List<Interaction> interactionsOfLead = Interaction.find.where().eq("objectAUUId", l.leadUUId).findList();
            Timestamp mostRecent = l.leadCreationTimestamp;
            for(Interaction i: interactionsOfLead){
                mTotalInteraction++;
                if(mostRecent.getTime() <= i.creationTimestamp.getTime()){
                    mostRecent = i.creationTimestamp;
                }
            }
            response.setLastIncomingCallTimestamp(sfd.format(mostRecent));
            response.setTotalInBounds(mTotalInteraction);
            responses.add(response);
        }

        return ok(toJson(responses));
    }

    public static Result getUserInfo(long id) {
        return ok(toJson(id));
    }

    public static Result getCandidateInfo(long id) {
            Lead lead = Lead.find.where().eq("leadId", id).findUnique();
            if(lead != null) {
                Interaction currentInteraction = Interaction.find.where().eq("objectAUUId", lead.leadUUId).findUnique();
                if(currentInteraction != null) {
                    return ok(toJson(lead+""+currentInteraction));
                }
                return ok(toJson(lead));
            }
        return badRequest("{ status: 0}");
    }

    public static Result getCandidateLocality(long id) {
        List<LocalityPreference> candidateLocalities = LocalityPreference.find.where().eq("CandidateId", id).findList();
        return ok(toJson(candidateLocalities));
    }

    public static Result getCandidateJob(long id) {
        List<JobPreference> candidateJobs = JobPreference.find.where().eq("CandidateId", id).findList();
        return ok(toJson(candidateJobs));
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

    public static Result updateLeadType(long leadId, long newType) {
        try{
            Lead lead = Lead.find.where().eq("leadId", leadId).findUnique();
            if(lead != null){
                lead.setLeadStatus(ServerConstants.LEAD_STATUS_WON);
                lead.setLeadType((int) newType);
                lead.update();
                return ok(toJson(newType));
            }
        } catch (NullPointerException n) {
            n.printStackTrace();
        }
        return badRequest();
    }

    public static Result updateLeadStatus(long leadId, int leadStatus) {
        try {
            Lead lead = Lead.find.where().eq("leadId", leadId).findUnique();

            if(lead != null){
                Logger.info("updateLeadStatus invoked leadId:"+leadId+" status:" + leadStatus);
                switch (leadStatus) {
                    case 1: lead.setLeadStatus(ServerConstants.LEAD_STATUS_TTC);
                        break;
                    case 2: lead.setLeadStatus(ServerConstants.LEAD_STATUS_WON);
                        break;
                    case 3: lead.setLeadStatus(ServerConstants.LEAD_STATUS_LOST);
                        break;
                }
                lead.update();

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

    public static Result test(int n) {
        long testCandidateId = 68600208;
        switch (n) {
            case 1: // fetch in json
                try{
                    List<IDProofreference>idProofreferenceList = IDProofreference.find.all();
                    List<Candidate> candidates = Candidate.find.all();
                    for(IDProofreference i: idProofreferenceList) {
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
                candidate.leadId = Util.randomLong();
                candidate.candidateMobile = "8984584584";
                candidate.candidateName = "Sandeep";

                // testcase related var set

                // create a sub-obj
                CandidateProfileStatus newcandidateProfileStatus = CandidateProfileStatus.find.where().eq("profileStatusId", 1).findUnique();
                if(newcandidateProfileStatus  == null){
                    newcandidateProfileStatus = new CandidateProfileStatus();
                    newcandidateProfileStatus.setProfileStatusId(10);
                    newcandidateProfileStatus.setProfileStatusName("Test10");
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
                newcandidateProfileStatus = CandidateProfileStatus.find.where().eq("profileStatusId", 11).findUnique();
                if(newcandidateProfileStatus  == null){
                    newcandidateProfileStatus = new CandidateProfileStatus();
                    newcandidateProfileStatus.setProfileStatusId(11);
                    newcandidateProfileStatus.setProfileStatusName("Test11");
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
                    candidateJobHistory.setCandidatepastCompany("AGS");
                    candidateJobHistory2.setCandidatepastCompany("Microtek");
                    candidateJobHistory.setCandidatepastSalary(15000);
                    candidateJobHistory2.setCandidatepastSalary(20500);
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
            case 9: // No candidates required
                titleMsg = "Test 9 :Adding JobToSkill Mapping";
                try {
                    System.out.println(titleMsg);
                    System.out.println("Test 9 Initiated .......\n No candidateObj Req" );

                    JobToSkill jobToSkill = new JobToSkill();

                    // TODO: since requres db query hence find a way
                    JobRole jobRole = JobRole.find.where().eq("jobRoleId", 1).findUnique();
                    Skill skill= Skill.find.where().eq("skillId", 1).findUnique();
                    if(jobRole == null){
                    } else{
                        jobToSkill.setJobRole(jobRole);
                    }
                    if(skill == null){
                    } else{
                        jobToSkill.setSkill(skill);
                    }
                    // get ref to existing skill

                    jobToSkill.setUpdateTimeStamp(new Timestamp(System.currentTimeMillis()));
                    jobToSkill.update();
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
                    LanguagePreference languagePreference = new LanguagePreference();
                    LanguagePreference languagePreference2 = new LanguagePreference();

                    // create sub-obj // this will be in a loop for multiple id
                    Language english = Language.find.where().eq("languageId", 1).findUnique();
                    languagePreference.setLanguage(english);
                    candidate10.languagePreferenceList.add(languagePreference);
                    Language hindi = Language.find.where().eq("languageId", 2).findUnique();
                    languagePreference2.setLanguage(hindi);
                    candidate10.languagePreferenceList.add(languagePreference2);

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
                        candidate11.timeShiftPreference.setTimeShift(timeShift);
                    }

                    candidate11.update();
                    System.out.println("Test 11 Completed ");
                    candidate11 = Candidate.find.where().eq("candidateId", candidate11.candidateId).findUnique();
                    return ok(toJson(candidate11));
                } catch(NullPointerException n11){
                    n11.printStackTrace();
                    System.out.println("Test 11 : candidateNotFound Check TestCase...");
                }
                break;
        }
        return ok("");
    }
}
