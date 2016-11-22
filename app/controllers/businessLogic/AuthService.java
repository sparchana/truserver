package controllers.businessLogic;

import api.InteractionConstants;
import api.ServerConstants;
import api.http.httpResponse.CandidateSignUpResponse;
import models.entity.Auth;
import models.entity.Candidate;
import models.entity.Interaction;
import models.entity.Lead;
import models.entity.Static.CandidateProfileStatus;
import models.util.SmsUtil;
import models.util.Util;
import play.Logger;

import java.util.UUID;

import static play.mvc.Controller.session;
/**
 * Created by batcoder1 on 5/5/16.
 */
public class AuthService {
    public static Auth  isAuthExists(Long candidateId){
       return Auth.find.where().eq("CandidateId", candidateId).findUnique();
    }

    public static void setNewPassword(Auth auth, String password){
        auth.setPasswordMd5(Util.md5(password + auth.getPasswordSalt()));
        auth.setAuthSessionIdExpiryMillis(System.currentTimeMillis() + 24 * 60 * 60 * 1000);
        session("sessionId", auth.getAuthSessionId());
        session("sessionExpiry", String.valueOf(auth.getAuthSessionIdExpiryMillis()));
    }

    public static CandidateSignUpResponse savePassword(String mobile, String password, InteractionService.InteractionChannelType channelType){
        CandidateSignUpResponse candidateSignUpResponse = new CandidateSignUpResponse();

        Logger.info("to check: " + mobile);
        Candidate existingCandidate = Candidate.find.where().eq("candidateMobile", mobile).findUnique();

        if(existingCandidate != null) {
            // If candidate exists
            Auth existingAuth = Auth.find.where().eq("candidateId", existingCandidate.getCandidateId()).findUnique();
            if(existingAuth != null){
                // If candidate exists and has a password, reset the old password
                Logger.info("Resetting password");
                existingAuth.setAuthStatus(ServerConstants.CANDIDATE_STATUS_VERIFIED);
                setNewPassword(existingAuth, password);
                Auth.savePassword(existingAuth);
                String interactionResult = InteractionConstants.INTERACTION_RESULT_CANDIDATE_RESET_PASSWORD_SUCCESS;
                String objAUUID = "";
                Candidate candidate = Candidate.find.where().eq("candidateId", existingCandidate.getCandidateId()).findUnique();
                if(candidate != null){
                    objAUUID = candidate.getCandidateUUId();
                }
                if(channelType == InteractionService.InteractionChannelType.SELF){
                    InteractionService.createInteractionForCandidateResetPasswordViaWebsite(objAUUID, interactionResult);
                } else{
                    InteractionService.createInteractionForCandidateResetPasswordViaAndroid(objAUUID, interactionResult);
                }

                existingAuth.setAuthSessionId(UUID.randomUUID().toString());
                existingAuth.setAuthSessionIdExpiryMillis(System.currentTimeMillis() + 24 * 60 * 60 * 1000);
                /* adding session details */
                addSession(existingAuth, existingCandidate);

                candidateSignUpResponse.setStatus(CandidateSignUpResponse.STATUS_SUCCESS);

                candidateSignUpResponse.setCandidateId(existingCandidate.getCandidateId());
                candidateSignUpResponse.setCandidateFirstName(existingCandidate.getCandidateFirstName());
                candidateSignUpResponse.setCandidateLastName(existingCandidate.getCandidateLastName());
                candidateSignUpResponse.setIsAssessed(existingCandidate.getCandidateIsAssessed());
                candidateSignUpResponse.setMinProfile(existingCandidate.getIsMinProfileComplete());
                candidateSignUpResponse.setLeadId(existingCandidate.getLead().getLeadId());
                candidateSignUpResponse.setCandidateJobPrefStatus(0);
                candidateSignUpResponse.setCandidateHomeLocalityStatus(0);
                candidateSignUpResponse.setGender(0);
                if(existingCandidate.getCandidateGender() != null){
                    if(existingCandidate.getCandidateGender() == 1){
                        candidateSignUpResponse.setGender(1);
                    }
                }

                /* START : to cater specifically the app need */
                if(existingCandidate.getCandidateLocalityLat() != null
                        || existingCandidate.getCandidateLocalityLng() != null ){
                    candidateSignUpResponse.setCandidateHomeLat(existingCandidate.getCandidateLocalityLat());
                    candidateSignUpResponse.setCandidateHomeLng(existingCandidate.getCandidateLocalityLng());
                }
                if(!existingCandidate.getJobPreferencesList().isEmpty()){
                    if(existingCandidate.getJobPreferencesList().size()>0 && existingCandidate.getJobPreferencesList().get(0)!= null)
                        candidateSignUpResponse.setCandidatePrefJobRoleIdOne(existingCandidate.getJobPreferencesList().get(0).getJobRole().getJobRoleId());
                    if(existingCandidate.getJobPreferencesList().size()>1 &&existingCandidate.getJobPreferencesList().get(1)!= null)
                        candidateSignUpResponse.setCandidatePrefJobRoleIdTwo(existingCandidate.getJobPreferencesList().get(1).getJobRole().getJobRoleId());
                    if(existingCandidate.getJobPreferencesList().size()>2 &&existingCandidate.getJobPreferencesList().get(2)!= null)
                        candidateSignUpResponse.setCandidatePrefJobRoleIdThree(existingCandidate.getJobPreferencesList().get(2).getJobRole().getJobRoleId());
                }
                    /* END */
                if(existingCandidate.getJobPreferencesList().size() > 0){
                    candidateSignUpResponse.setCandidateJobPrefStatus(1);
                }
                if(existingCandidate.getCandidateLocalityLat() != null && existingCandidate.getCandidateLocalityLng() != null){
                    candidateSignUpResponse.setCandidateHomeLocalityStatus(1);
                }
                if(existingCandidate.getLocality()!= null && existingCandidate.getLocality().getLocalityName()!=null){
                    candidateSignUpResponse.setCandidateHomeLocalityName(existingCandidate.getLocality().getLocalityName());
                }

            } else{
                Auth auth = new Auth();
                auth.setCandidateId(existingCandidate.getCandidateId());
                setNewPassword(auth,password);
                auth.setAuthStatus(ServerConstants.CANDIDATE_STATUS_VERIFIED);
                Auth.savePassword(auth);
                auth.setAuthSessionId(UUID.randomUUID().toString());
                auth.setAuthSessionIdExpiryMillis(System.currentTimeMillis() + 24 * 60 * 60 * 1000);
                /* adding session details */
                addSession(auth, existingCandidate);

                candidateSignUpResponse.setStatus(CandidateSignUpResponse.STATUS_SUCCESS);

                String objAUUID = existingCandidate.getCandidateUUId();
                if (channelType == InteractionService.InteractionChannelType.SELF) {
                    InteractionService.createInteractionForCandidateAddPasswordViaWebsite(objAUUID);
                } else{
                    InteractionService.createInteractionForCandidateAddPasswordViaAndroid(objAUUID);
                }
                try {
                    existingCandidate.setCandidateprofilestatus(CandidateProfileStatus.find.where().eq("profileStatusId", ServerConstants.CANDIDATE_STATE_ACTIVE).findUnique());
                    candidateSignUpResponse.setStatus(CandidateSignUpResponse.STATUS_SUCCESS);
                }catch (NullPointerException n) {
                    Logger.info("Oops ProfileStatusId"+ " doesnot exists");
                    candidateSignUpResponse.setStatus(CandidateSignUpResponse.STATUS_FAILURE);

                }
                existingCandidate.update();
                Logger.info("candidate status confirmed");

                Lead existingLead = Lead.find.where().eq("leadId", existingCandidate.getLead().getLeadId()).findUnique();
                existingLead.setLeadStatus(ServerConstants.LEAD_STATUS_WON);
                existingLead.update();
                Logger.info("Lead converted in candidate");

                SmsUtil.sendWelcomeSmsFromWebsite(existingCandidate.getCandidateFirstName(), existingCandidate.getCandidateMobile());

                candidateSignUpResponse.setCandidateFirstName(existingCandidate.getCandidateFirstName());
                candidateSignUpResponse.setCandidateLastName(existingCandidate.getCandidateLastName());
                candidateSignUpResponse.setCandidateId(existingCandidate.getCandidateId());
                candidateSignUpResponse.setStatus(CandidateSignUpResponse.STATUS_SUCCESS);
                candidateSignUpResponse.setMinProfile(existingCandidate.getIsMinProfileComplete());
                candidateSignUpResponse.setIsAssessed(existingCandidate.getCandidateIsAssessed());
                candidateSignUpResponse.setLeadId(existingCandidate.getLead().getLeadId());
                candidateSignUpResponse.setGender(0);
                if(existingCandidate.getCandidateGender() != null){
                    if(existingCandidate.getCandidateGender() == 1){
                        candidateSignUpResponse.setGender(1);
                    }
                }
            }
            Logger.info("Auth Save Successful");
        }
        else {
            Logger.info("User Does not Exist!");
            candidateSignUpResponse.setStatus(CandidateSignUpResponse.STATUS_FAILURE);
        }

        return candidateSignUpResponse;
    }
    public static void addSession(Auth existingAuth, Candidate existingCandidate){
        session().put("sessionId", existingAuth.getAuthSessionId());
        session().put("sessionUsername", "CID-"+String.valueOf(existingCandidate.getCandidateId()));
        session().put("candidateId", String.valueOf(existingCandidate.getCandidateId()));
        session().put("candidateMobile", String.valueOf(existingCandidate.getCandidateMobile()));
        session().put("leadId", String.valueOf(existingCandidate.getLead().getLeadId()));
        session().put("sessionExpiry", String.valueOf(existingAuth.getAuthSessionIdExpiryMillis()));
        session().put("sessionUsername", String.valueOf(existingCandidate.getCandidateFullName() + "_candidate"));
        session().put("sessionChannel", String.valueOf(ServerConstants.SESSION_CHANNEL_CANDIDATE_WEBSITE));
        Logger.info("set-sessionId"+ session().get("candidateMobile"));
    }
}
