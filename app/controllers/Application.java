package controllers;

import api.ServerConstants;
import api.http.*;
import models.entity.*;
import models.entity.OM.IDProofreference;
import models.entity.Static.CandidateProfileStatus;
import models.entity.Static.Locality;
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
import java.util.List;
import java.util.UUID;

import static play.libs.Json.toJson;

public class Application extends Controller {

    public static Result index() {
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
        return ok(toJson(Lead.addLead(addLeadRequest)));
    }

    public static Result signUpSubmit() {
        Form<CandidateSignUpRequest> candidateForm = Form.form(CandidateSignUpRequest.class);
        CandidateSignUpRequest candidateSignUpRequest = candidateForm.bindFromRequest().get();

        return ok(toJson(Candidate.candidateSignUp(candidateSignUpRequest)));
    }

    public static Result verifyOtp() {
        Form<CandidateSignUpRequest> candidateForm = Form.form(CandidateSignUpRequest.class);
        CandidateSignUpRequest candidateSignUpRequest = candidateForm.bindFromRequest().get();
        return ok(toJson(Candidate.verifyOtp(candidateSignUpRequest)));
    }

    public static Result addPassword() {
        Form<CandidateSignUpRequest> candidateForm = Form.form(CandidateSignUpRequest.class);
        CandidateSignUpRequest candidateSignUpRequest = candidateForm.bindFromRequest().get();
        return ok(toJson(Auth.addAuth(candidateSignUpRequest)));
    }

    public static Result savePassword() {
        Form<ResetPasswordResquest> resetPassword = Form.form(ResetPasswordResquest.class);
        ResetPasswordResquest resetPasswordResquest = resetPassword.bindFromRequest().get();
        return ok(toJson(Auth.savePassword(resetPasswordResquest)));
    }

    public static Result loginSubmit() {
        Form<LoginRequest> loginForm = Form.form(LoginRequest.class);
        LoginRequest loginRequest = loginForm.bindFromRequest().get();
        return ok(toJson(Candidate.login(loginRequest)));
    }

    @Security.Authenticated(SecuredUser.class)
    public static Result dashboard() {
        return ok(views.html.candidate_home.render());
    }

    public static Result checkCandidate() {
        Form<ResetPasswordResquest> checkCandidate = Form.form(ResetPasswordResquest.class);
        ResetPasswordResquest resetPasswordResquest = checkCandidate.bindFromRequest().get();
        return ok(toJson(Candidate.checkCandidate(resetPasswordResquest)));
    }

    public static Result checkResetOtp() {
        Form<ResetPasswordResquest> checkResetOtp = Form.form(ResetPasswordResquest.class);
        ResetPasswordResquest resetPasswordResquest = checkResetOtp.bindFromRequest().get();
        return ok(toJson(Candidate.checkResetOtp(resetPasswordResquest)));
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
        return redirect(
                routes.Application.index()
        );
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
        List<Job> jobs = Job.find.findList();
        return ok(toJson(jobs));
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
}
