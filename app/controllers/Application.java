package controllers;

import api.ServerConstants;
import api.http.*;
import controllers.businessLogic.AuthService;
import controllers.businessLogic.CandidateService;
import controllers.businessLogic.LeadService;
import models.entity.*;
import models.entity.OM.IDProofreference;
import models.entity.OM.JobToSkill;
import models.entity.OO.CandidateCurrentJobDetail;
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
import java.sql.Time;
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
        candidate.candidateAge = 0;

        List<String> localityList = Arrays.asList(candidateSignUpRequest.getCandidateLocality().split("\\s*,\\s*"));
        List<String> jobsList = Arrays.asList(candidateSignUpRequest.getCandidateJobPref().split("\\s*,\\s*"));

        return ok(toJson(CandidateService.createCandidate(candidate,localityList,jobsList)));
    }

    public static Result signUpSupport() {
        Form<AddSupportCandidateRequest> candidateForm = Form.form(AddSupportCandidateRequest.class);
        AddSupportCandidateRequest addSupportCandidateRequest = candidateForm.bindFromRequest().get();

        Candidate candidate = new Candidate();
        candidate.candidateId = Util.randomLong();
        candidate.candidateUUId = UUID.randomUUID().toString();
        candidate.candidateName = addSupportCandidateRequest.getCandidateName();
        candidate.candidateMobile = "+91" + addSupportCandidateRequest.getCandidateMobile();

        candidate.candidateDOB = addSupportCandidateRequest.getCandidateDob();
        candidate.candidateAge = addSupportCandidateRequest.getCandidateAge();
        candidate.candidatePhoneType = addSupportCandidateRequest.getCandidatePhoneType();
        candidate.candidateGender = addSupportCandidateRequest.getCandidateGender();
        candidate.candidateMaritalStatus = addSupportCandidateRequest.getCandidateMaritalStatus();
        candidate.candidateEmail = addSupportCandidateRequest.getCandidateEmail();
        candidate.candidateIsEmployed = addSupportCandidateRequest.getCandidateIsEmployed();
        candidate.candidateTotalExperience = addSupportCandidateRequest.getCandidateTotalExperience();

        String candidateHomeLocality = addSupportCandidateRequest.getCandidateHomeLocality();

        String candidateCurrentCompany = addSupportCandidateRequest.getCandidateCurrentCompany();
        String candidateCurrentJobLocation = addSupportCandidateRequest.getCandidateCurrentJobLocation();
        int candidateTransportation = addSupportCandidateRequest.getCandidateTransportation();
        int candidateCurrentWorkShift = addSupportCandidateRequest.getCandidateCurrentWorkShift();
        String candidateCurrentJobRole = addSupportCandidateRequest.getCandidateCurrentJobRole();
        String candidateCurrentJobDesignation = addSupportCandidateRequest.getCandidateCurrentJobDesignation();
        long candidateCurrentSalary = addSupportCandidateRequest.getCandidateCurrentSalary();
        int candidateCurrentJobDuration = addSupportCandidateRequest.getCandidateCurrentJobDuration();

        String candidatePastJobCompany = addSupportCandidateRequest.getCandidatePastJobCompany();
        String candidatePastJobRole = addSupportCandidateRequest.getCandidatePastJobRole();
        long candidatePastJobSalary = addSupportCandidateRequest.getCandidatePastJobSalary();

        int candidateEducationLevel = addSupportCandidateRequest.getCandidateEducationLevel();
        int candidateDegree = addSupportCandidateRequest.getCandidateDegree();
        String candidateEducationInstitute = addSupportCandidateRequest.getCandidateEducationInstitute();

        List<String> shiftTimePref = Arrays.asList(addSupportCandidateRequest.getCandidateTimeShiftPref().split("\\s*,\\s*"));

        int candidateMotherTongue = addSupportCandidateRequest.getCandidateMotherTongue();

        int candidateIdProof = addSupportCandidateRequest.getCandidateIdProof();
        int candidateSalarySlip = addSupportCandidateRequest.getCandidateSalarySlip();
        int candidateAppointmentLetter = addSupportCandidateRequest.getCandidateAppointmentLetter();

        List<String> localityPrefList = Arrays.asList(addSupportCandidateRequest.getCandidateLocality().split("\\s*,\\s*"));
        List<String> jobsPrefList = Arrays.asList(addSupportCandidateRequest.getCandidateJobInterest().split("\\s*,\\s*"));

        return ok("done");
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
        List<CandidateLocality> candidateLocalities = CandidateLocality.find.where().eq("CandidateLocalityCandidateId", id).findList();
        return ok(toJson(candidateLocalities));
    }

    public static Result getCandidateJob(long id) {
        List<CandidateJob> candidateJobs = CandidateJob.find.where().eq("CandidateJobCandidateId", id).findList();
        return ok(toJson(candidateJobs));
    }

    public static Result getAllSkills(long id) {
        List<JobToSkill> jobToSkillList = JobToSkill.find.where().eq("JobRoleId",id).findList();
        return ok(toJson(jobToSkillList));
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

    public static Result getAllShift() {
        List<Timeshift> timeshifts = Timeshift.find.findList();
        return ok(toJson(timeshifts));
    }

    public static Result getAllTransportation() {
        List<TransportationMode> transportationModes = TransportationMode.find.findList();
        return ok(toJson(transportationModes));
    }

    public static Result getAllEducation() {
        List<Education> educations = Education.find.findList();
        return ok(toJson(educations));
    }

    public static Result getAllLanguage() {
        List<Language> languages = Language.find.findList();
        return ok(toJson(languages));
    }

    public static Result test() {
        int n=3;
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

                // create a sub-obj
                CandidateProfileStatus newcandidateProfileStatus = CandidateProfileStatus.find.where().eq("profileStatusId", 1).findUnique();
                if(newcandidateProfileStatus  == null){
                    newcandidateProfileStatus = new CandidateProfileStatus();
                    newcandidateProfileStatus.setProfileStatusId(10);
                    newcandidateProfileStatus.setProfileStatusName("Test10");
                    candidate.candidateprofilestatus = newcandidateProfileStatus ;
                } else {
                    newcandidateProfileStatus.setProfileStatusName("Changed");
                    candidate.candidateprofilestatus = newcandidateProfileStatus ;
                }

                // save the parent obj
                candidate.save();
                break;
            case 3: // retrive a candidate obj and update sub-obj of candidate class
                Candidate retrievedCandidate = Candidate.find.where().eq("candidateId", 2).findUnique();
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
                break;
            case 4: // delete a candidate obj and check if the sub-obj data also gets cleared or not.
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
        }
        return ok("");
    }

    public static Result candidateSignupSupport() {
        return ok(views.html.signup_support.render());
    }

}
