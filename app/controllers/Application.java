package controllers;

import api.CandidateSignUpRequest;
import api.LoginRequest;
import api.ResetPasswordResquest;
import api.ServerConstants;
import api.http.*;
import au.com.bytecode.opencsv.CSVReader;
import models.entity.*;
import models.util.Util;
import play.Logger;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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
        return badRequest("Your Access Level : Only Upload");
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
        return ok(views.html.dashboard.render());
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
        Logger.info("File Uploaded "  + file.toString());
        String[] nextLine;
        ArrayList<Lead> leads = new ArrayList<>();
        int count = 0;
        int overLappingRecordCount = 0;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ssXXX");
        SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        try {
            CSVReader reader = new CSVReader(new FileReader(file));
            reader.readNext();// skip title row
            while ((nextLine = reader.readNext()) != null) {
                Lead lead = new Lead();
                Interaction kwObject = new Interaction();
                // Read all InBound Calls
                if (nextLine != null && nextLine[0].equals("0")) {
                    lead.leadUUId = UUID.randomUUID().toString();
                    lead.leadId = Util.randomLong();
                    if(!nextLine[1].contains("+")) {
                        lead.leadMobile = "+"+nextLine[1];
                    } else {
                        lead.leadMobile = nextLine[1];
                    }
                    Date parsedDate;
                    try {
                        parsedDate = sdf.parse(nextLine[4]);
                    } catch (ParseException e) {
                        parsedDate = sdf2.parse(nextLine[4]);
                    }
                    lead.leadCreationTimestamp = new Timestamp(parsedDate.getTime());
                    Lead existingLead = Lead.find.where()
                            .eq("leadMobile", lead.leadMobile)
                            .findUnique();
                    lead.leadType = ServerConstants.TYPE_LEAD;
                    lead.leadChannel = ServerConstants.LEAD_CHANNEL_KNOWLARITY;
                    lead.leadStatus = ServerConstants.LEAD_STATUS_NEW;

                    kwObject.objectAType = lead.leadType;
                    kwObject.objectAUUId= lead.leadUUId;
                    if (existingLead == null) {
                        lead.save();
                        count++;
                        leads.add(lead);
                    } else {
                        if(existingLead.getLeadCreationTimestamp().getTime() > lead.leadCreationTimestamp.getTime()) {
                            // recording the first inbound of a lead
                            existingLead.setLeadCreationTimestamp(lead.leadCreationTimestamp);
                            existingLead.update();
                        }
                        overLappingRecordCount++;
                        kwObject.objectAType = existingLead.leadType;
                        kwObject.objectAUUId= existingLead.leadUUId;
                        Logger.info("compared DateTime: KwVer." + lead.leadCreationTimestamp.getTime() + "  OurDbVer. " + existingLead.leadCreationTimestamp.getTime());
                    }
                    // gives total no of old leads
                    Logger.info("Total OverLapping records : " + overLappingRecordCount);

                    // save all inbound calls to interaction
                    kwObject.createdBy = "System";
                    kwObject.creationTimestamp = new Timestamp(parsedDate.getTime());
                    kwObject.interactionType = ServerConstants.INTERACTION_TYPE_CALL_IN;
                    kwObject.save();
                }
            }
            Logger.info("Csv File Parsed and stored in db!");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ok(toJson(count));
    }

    public static Result getAll(){
        List<Lead> allLead = Lead.find.where().ne("leadStatus", ServerConstants.LEAD_STATUS_WON).findList();
        List<Interaction> allInteractions = Interaction.find.all();
        List<Lead> allNewLeads = Lead.find.where()
                .eq("leadType", ServerConstants.TYPE_LEAD)
                .ne("leadStatus", ServerConstants.LEAD_STATUS_WON)
                .eq("leadStatus", ServerConstants.LEAD_STATUS_NEW).findList();
        ArrayList<SupportDashboardElementResponse> responses = new ArrayList<>();

        SimpleDateFormat sfd = new SimpleDateFormat("yyyy-MM-dd hh:mm:ssXXX");

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

    public static Result addCandidate() {
        Form<AddCandidateRequest> userForm = Form.form(AddCandidateRequest.class);
        AddCandidateRequest request = userForm.bindFromRequest().get();
        AddCandidateResponse response = new AddCandidateResponse();

        // create interaction for AddCandidateAction
        Interaction interaction = new Interaction();
        interaction.setInteractionType(ServerConstants.INTERACTION_TYPE_CALL_OUT);
        interaction.setCreatedBy(ServerConstants.INTERACTION_CREATED_BY_AGENT);
        interaction.setNote(request.candidateNote);
        interaction.setResult("Candidate Info Saved");
        // checks if lead AlreadyExists
        Candidate existingCandidate = Candidate.find.where().eq("leadId", request.getLeadId()).findUnique();
        if(existingCandidate != null) {
            interaction.setObjectAType(ServerConstants.TYPE_LEAD);
            existingCandidate.candidateUpdateTimestamp =  new Timestamp(System.currentTimeMillis());
            existingCandidate.update();
            response.setStatus(AddCandidateResponse.STATUS_EXISTS);

            interaction.setObjectAUUId(existingCandidate.candidateUUId);
            Logger.info("Candidate Info Updated !!");
        } else {

            Lead updateLead = Lead.find.where().eq("leadId", request.getLeadId()).findUnique();

            interaction.setObjectAUUId(updateLead.getLeadUUId());
            interaction.setObjectAType(ServerConstants.TYPE_LEAD);

            response.setStatus(AddCandidateResponse.STATUS_SUCCESS);
        }
        interaction.save();
        return ok(toJson(response));
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

    public static Result updateLeadStatus(long leadId) {
        try {
            Lead lead = Lead.find.where().eq("leadId", leadId).findUnique();

            if(lead != null){
                if(lead.leadStatus == ServerConstants.LEAD_STATUS_NEW) {
                    lead.setLeadStatus(ServerConstants.LEAD_STATUS_TTC);
                    lead.update();
                }
                return ok(toJson(lead.leadStatus));
            }

        } catch (NullPointerException n) {
            n.printStackTrace();
        }
        return badRequest();
    }
}
