package controllers.businessLogic;

import api.ServerConstants;
import api.http.CandidateSignUpResponse;
import models.entity.Auth;
import models.entity.Candidate;
import models.entity.Interaction;
import models.entity.Lead;
import models.entity.Static.CandidateProfileStatus;
import models.util.SmsUtil;
import models.util.Util;
import play.Logger;

import java.util.Random;
import java.util.UUID;

import static play.mvc.Controller.session;

/**
 * Created by batcoder1 on 5/5/16.
 */
public class AuthService {
    public static void setNewPassword(Auth auth, String password){
        int passwordSalt = (new Random()).nextInt();
        auth.passwordMd5 = Util.md5(password + passwordSalt);
        auth.passwordSalt = passwordSalt;
        auth.authSessionId = UUID.randomUUID().toString();
        auth.authSessionIdExpiryMillis = System.currentTimeMillis() + 24 * 60 * 60 * 1000;
        session("sessionId", auth.authSessionId);
        session("sessionExpiry", String.valueOf(auth.authSessionIdExpiryMillis));

    }
    public static CandidateSignUpResponse savePassword(String mobile, String password){
        CandidateSignUpResponse candidateSignUpResponse = new CandidateSignUpResponse();

        Logger.info("to check: " + mobile);
        Candidate existingCandidate = Candidate.find.where().eq("candidateMobile", "+91" + mobile).findUnique();

        if(existingCandidate != null) {
            // If candidate exists
            Auth existingAuth = Auth.find.where().eq("candidateId", existingCandidate.candidateId).findUnique();
            if(existingAuth != null){
                // If candidate exists and has a password, reset the old password
                Logger.info("Resetting password");
                setNewPassword(existingAuth, password);
                Auth.savePassword(existingAuth);

                candidateSignUpResponse.setCandidateName(existingCandidate.candidateName);
                candidateSignUpResponse.setCandidateId(existingCandidate.candidateId);
                candidateSignUpResponse.setStatus(CandidateSignUpResponse.STATUS_SUCCESS);
            }

            else{
                Auth auth = new Auth();
                auth.authId =  (int)(Math.random()*9000)+100000;
                auth.candidateId = existingCandidate.candidateId;
                setNewPassword(auth,password);
                Auth.savePassword(auth);

                candidateSignUpResponse.setStatus(CandidateSignUpResponse.STATUS_SUCCESS);

                Logger.info("Password saved");

                Interaction interaction = new Interaction();
                interaction.objectAUUId = existingCandidate.candidateUUId;
                interaction.objectAType = ServerConstants.OBJECT_TYPE_CANDIDATE;
                interaction.interactionType = ServerConstants.INTERACTION_TYPE_WEBSITE;
                interaction.result = "New Candidate Added";
                InteractionService.createInteraction(interaction);
                try {
                    existingCandidate.candidateprofilestatus = CandidateProfileStatus.find.where().eq("profileStatusId", ServerConstants.CANDIDATE_STATE_NEW).findUnique();
                    candidateSignUpResponse.setStatus(CandidateSignUpResponse.STATUS_SUCCESS);
                }catch (NullPointerException n) {
                    Logger.info("Oops ProfileStatusId"+ " doesnot exists");
                    candidateSignUpResponse.setStatus(CandidateSignUpResponse.STATUS_FAILURE);

                }
                existingCandidate.update();
                Logger.info("candidate status confirmed");

                Lead existingLead = Lead.find.where().eq("leadId", existingCandidate.lead.leadId).findUnique();
                existingLead.setLeadStatus(ServerConstants.LEAD_STATUS_WON);
                existingLead.update();
                Logger.info("Lead converted in candidate");

                String msg = "Hey " + existingCandidate.candidateName +
                        "! Welcome to Trujobs.in. Login and complete our skill assessment today and find your right job.";
                SmsUtil.sendSms(existingCandidate.candidateMobile,msg);

                candidateSignUpResponse.setCandidateId(existingCandidate.candidateId);
                candidateSignUpResponse.setCandidateName(existingCandidate.candidateName);
            }
            Logger.info("Auth Save Successful");
        }
        else {
            Logger.info("User Does not Exist!");
            candidateSignUpResponse.setStatus(CandidateSignUpResponse.STATUS_FAILURE);
        }

        return candidateSignUpResponse;
    }
}
