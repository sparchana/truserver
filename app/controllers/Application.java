package controllers;

import api.CandidateSignUpRequest;
import api.LoginRequest;
import api.ResetPasswordResquest;
import models.entity.*;
import api.ServerConstants;
import api.http.AddCandidateRequest;
import api.http.AddCandidateResponse;
import api.http.AddLeadRequest;
import api.http.SupportDashboardElementResponse;
import models.util.Util;
import play.Logger;
import play.data.Form;
import play.mvc.Controller;
import au.com.bytecode.opencsv.CSVReader;
import play.mvc.Result;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Timestamp;
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

    public static Result support() {
        return ok(views.html.support.render());
    }

    public static Result addLead() {
        Form<AddLeadRequest> userForm = Form.form(AddLeadRequest.class);
        Logger.info(String.valueOf(request().body().asFormUrlEncoded()));
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

    public static Result assessment() {
        return ok(views.html.assessment.render());
    }


    public static Result loginSubmit() {
        Form<LoginRequest> loginForm = Form.form(LoginRequest.class);
        LoginRequest loginRequest = loginForm.bindFromRequest().get();
        return ok(toJson(Candidate.login(loginRequest)));
    }

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
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ssXXX");
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
                    lead.leadMobile = nextLine[1];
                    Date parsedDate = sdf.parse(nextLine[4]);
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
                        kwObject.objectAType = existingLead.leadType;
                        kwObject.objectAUUId= existingLead.leadUUId;
                        Logger.info("compared DateTime: KwVer." + lead.leadCreationTimestamp.getTime() + "  OurDbVer. " + existingLead.leadCreationTimestamp.getTime());
                        Logger.info("Old Record Encountered !!");
                    }

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
       /* Developer developer = Developer.find.where().eq("developerApikey", api_key).findUnique();
        if(developer == null) {
            return badRequest("Permission Denied! Contact: tech@trujobs.in");
        }*/

        //
        // Picks all kw call inbounds
        List<Lead> allLead = Lead.find.all();
        List<Lead> allNewLeads = Lead.find.where()
                .eq("leadType",ServerConstants.TYPE_LEAD)
                .eq("leadStatus",ServerConstants.LEAD_STATUS_NEW).findList();
        ArrayList<SupportDashboardElementResponse> responses = new ArrayList<>();
        for(Lead l : allNewLeads){
            SupportDashboardElementResponse response = new SupportDashboardElementResponse();

            response.setLeadCreationTimestamp(new SimpleDateFormat("yyyy-MM-dd hh:mm:ssXXX").format(l.getLeadCreationTimestamp()));
            response.setLeadId(l.leadId);
            response.setLeadName(l.leadName);
            response.setLeadMobile(l.leadMobile);
            switch (l.leadStatus) {
                case 0: response.setLeadStatus("New"); break;
                case 1: response.setLeadStatus("T.T.C"); break;
            }
            switch (l.leadType) {
                case 0: response.setLeadType("Fresh"); break;
                case 1: response.setLeadType("Lead"); break;
                case 2: response.setLeadType("Candidate"); break;
                case 3: response.setLeadType("Recruiter"); break;
            }
            switch (l.leadChannel) {
                case 0: response.setLeadChannel("Website"); break;
                case 1: response.setLeadChannel("Knowlarity"); break;
            }
            responses.add(response);
        }
        /*List<Interaction> interactions = Interaction.find.where().eq("InteractionType", ServerConstants.INTERACTION_TYPE_CALL_IN).findList();

        ArrayList<Lead> listUnique = new ArrayList<>();
        for(Lead l: allLead) {
            // find all new leads
        }*/

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

            Candidate candidate = new Candidate();
            candidate.candidateChannel = ServerConstants.LEAD_CHANNEL_WEBSITE;
            candidate.candidateCreateTimestamp = new Timestamp(System.currentTimeMillis());
            candidate.candidateUUId = UUID.randomUUID().toString();
            candidate.candidateId = Util.randomLong();
            candidate.candidateName = request.getCandidateName();
            candidate.candidateMobile = request.getCandidateMobile();
            candidate.candidateUpdateTimestamp =  new Timestamp(System.currentTimeMillis());
            candidate.candidateState = ServerConstants.CANDIDATE_STATE_NEW;
            candidate.leadId = request.getLeadId();
            candidate.save();

            // change lead type from lead to candidate if min-req info is available
            if(candidate.candidateName != null ||candidate.candidateMobile != null ){
                Lead updateLead = Lead.find.where().eq("leadId", request.getLeadId()).findUnique();
                updateLead.setLeadType(ServerConstants.TYPE_CANDIDATE);
                updateLead.update();
            }

            interaction.setObjectAUUId(candidate.candidateUUId);
            interaction.setObjectAType(ServerConstants.TYPE_CANDIDATE);

            response.setStatus(AddCandidateResponse.STATUS_SUCCESS);
        }
        interaction.save();
        return ok(toJson(response));
    }

    public static Result getCandidateInfo(long id) {
            Candidate candidate = Candidate.find.where().eq("leadId", id).findUnique();
            if(candidate != null) {
                Interaction currentInteraction = Interaction.find.where().eq("objectAUUId", candidate.candidateUUId).findUnique();
                if(currentInteraction != null) {
                    return ok(toJson(candidate+""+currentInteraction));
                }
                return ok(toJson(candidate));
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
}
