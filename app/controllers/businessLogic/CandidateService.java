package controllers.businessLogic;

import api.ServerConstants;
import api.http.*;
import models.entity.Auth;
import models.entity.Candidate;
import models.entity.Interaction;
import models.entity.Lead;
import models.entity.OM.JobPreference;
import models.entity.OM.LocalityPreference;
import models.entity.Static.CandidateProfileStatus;
import models.entity.Static.JobRole;
import models.entity.Static.Locality;
import models.util.Util;
import play.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static models.util.Util.generateOtp;
import static play.mvc.Controller.session;

/**
 * Created by batcoder1 on 3/5/16.
 */
public class CandidateService {

    public static CandidateSignUpResponse createCandidate(Candidate candidate){
        CandidateSignUpResponse candidateSignUpResponse = new CandidateSignUpResponse();
        Interaction interaction = new Interaction();
        Logger.info("Checking this mobile : " + candidate.candidateMobile );
        Candidate existingCandidate = Candidate.find.where().eq("candidateMobile",candidate.candidateMobile).findUnique();
        Lead existingLead = Lead.find.where().eq("leadMobile", candidate.candidateMobile).findUnique();
        int randomPIN = generateOtp();

        try {
            if(existingCandidate == null) {
                // if no candidate exists
                if(existingLead == null){
                    LeadService.createLead(getLeadFromCandidate(candidate));
                }
                else{
                    candidate.leadId = existingLead.leadId;
                }

                CandidateProfileStatus candidateProfileStatus = CandidateProfileStatus.find.where().eq("profileStatusId", ServerConstants.CANDIDATE_STATE_NEW).findUnique();
                if(candidateProfileStatus != null){
                    candidate.setCandidateprofilestatus(candidateProfileStatus);
                    candidateSignUpResponse.setStatus(CandidateSignUpResponse.STATUS_SUCCESS);
                    Candidate.registerCandidate(candidate);
                } else {
                    candidateSignUpResponse.setStatus(CandidateSignUpResponse.STATUS_FAILURE);
                }

                String msg = "Welcome to Trujobs.in! Use OTP " + randomPIN + " to register";
                SendOtpService.sendSms(candidate.candidateMobile, msg);

                candidateSignUpResponse.setCandidateId(candidate.candidateId);
                candidateSignUpResponse.setCandidateName(candidate.candidateName);
                candidateSignUpResponse.setOtp(randomPIN);

                Logger.info("Candidate successfully registered " + candidate);
                interaction.objectAUUId = candidate.candidateUUId;
                interaction.result = "New Candidate Added";
                interaction.objectAType = ServerConstants.OBJECT_TYPE_CANDIDATE;
                interaction.interactionType = ServerConstants.INTERACTION_TYPE_WEBSITE;
                InteractionService.createIntraction(interaction);

            } else if(existingCandidate != null && existingCandidate.candidateprofilestatus.profileStatusId == 0){
                List<LocalityPreference> allLocality = models.entity.OM.LocalityPreference.find.where().eq("CandidateId", existingCandidate.candidateId).findList();
                for(LocalityPreference candidateLocality : allLocality){
                    candidateLocality.delete();
                }

                List<JobPreference> allJob = JobPreference.find.where().eq("CandidateId", existingCandidate.candidateId).findList();
                for(JobPreference candidateJobs : allJob){
                    candidateJobs.delete();
                }
                existingCandidate.localityPreferenceList = candidate.localityPreferenceList;
                existingCandidate.jobPreferencesList = candidate.jobPreferencesList;

                Candidate.candidateUpdate(existingCandidate);

                Logger.info("Existing Candidate successfully updated" + candidate);

                String msg = "Welcome to Trujobs.in! Use OTP " + randomPIN + " to register";
                SendOtpService.sendSms(candidate.candidateMobile, msg);

                candidateSignUpResponse.setCandidateId(candidate.candidateId);
                candidateSignUpResponse.setCandidateName(candidate.candidateName);
                candidateSignUpResponse.setOtp(randomPIN);
                candidateSignUpResponse.setStatus(CandidateSignUpResponse.STATUS_SUCCESS);

                interaction.objectAUUId = existingCandidate.candidateUUId;
                interaction.result = "New Candidate Added";
                InteractionService.createIntraction(interaction);
                interaction.objectAType = ServerConstants.OBJECT_TYPE_CANDIDATE;
                interaction.interactionType = ServerConstants.INTERACTION_TYPE_WEBSITE;
                InteractionService.createIntraction(interaction);
            } else{
                candidateSignUpResponse.setStatus(CandidateSignUpResponse.STATUS_EXISTS);
            }
        } catch (NullPointerException n){
            n.printStackTrace();
            candidateSignUpResponse.setStatus(CandidateSignUpResponse.STATUS_FAILURE);
        }

        return candidateSignUpResponse;
    }

    public static LoginResponse login(String loginMobile, String loginPassword){
        LoginResponse loginResponse = new LoginResponse();
        Logger.info(" login mobile: " + loginMobile);
        Candidate existingCandidate = Candidate.find.where().eq("candidateMobile", "+91" + loginMobile).findUnique();
        if(existingCandidate == null){
            loginResponse.setStatus(loginResponse.STATUS_NO_USER);
            Logger.info("User Does not Exists");
        }
        else {
            long candidateId = existingCandidate.candidateId;
            Auth existingAuth = Auth.find.where().eq("candidateId",candidateId).findUnique();
            if(existingAuth != null){
                if (((existingAuth.passwordMd5.equals(Util.md5(loginPassword + existingAuth.passwordSalt))) &&
                    (existingCandidate.candidateprofilestatus.profileStatusId != 0))) {
                    Logger.info(existingCandidate.candidateName + " " + existingCandidate.candidateprofilestatus.profileStatusId);
                    loginResponse.setCandidateId(existingCandidate.candidateId);
                    loginResponse.setCandidateName(existingCandidate.candidateName);
                    loginResponse.setAccountStatus(existingCandidate.candidateprofilestatus.profileStatusId);
                    loginResponse.setStatus(loginResponse.STATUS_SUCCESS);

                    existingAuth.authSessionId = UUID.randomUUID().toString();
                    existingAuth.authSessionIdExpiryMillis = System.currentTimeMillis() + 24 * 60 * 60 * 1000;
                    session("sessionId", existingAuth.authSessionId);
                    session("sessionExpiry", String.valueOf(existingAuth.authSessionIdExpiryMillis));
                    existingAuth.update();
                    Logger.info("Login Successful");
                }
                else {
                    loginResponse.setStatus(loginResponse.STATUS_WRONG_PASSWORD);
                    Logger.info("Incorrect Password");
                }
            }
            else {
                loginResponse.setStatus(loginResponse.STATUS_NO_USER);
                Logger.info("No User");
            }
        }
        return loginResponse;
    }

    public static ResetPasswordResponse findUserAndSendOtp(String candidateMobile){
        ResetPasswordResponse resetPasswordResponse = new ResetPasswordResponse();
        Candidate existingCandidate = Candidate.find.where().eq("candidateMobile", "+91" + candidateMobile).findUnique();
        if(existingCandidate != null){
            if(existingCandidate.candidateprofilestatus.profileStatusId == ServerConstants.CANDIDATE_STATUS_VERIFIED){
                int randomPIN = generateOtp();
                existingCandidate.update();
                String msg = "Welcome to Trujobs.in! Use OTP " + randomPIN + " to reset password";
                SendOtpService.sendSms(existingCandidate.candidateMobile, msg);
                resetPasswordResponse.setOtp(randomPIN);
                resetPasswordResponse.setStatus(LoginResponse.STATUS_SUCCESS);
            }
            else{
                Logger.info("Reset otp sent");
                resetPasswordResponse.setStatus(LoginResponse.STATUS_NO_USER);
            }
        }
        else{
            resetPasswordResponse.setStatus(LoginResponse.STATUS_NO_USER);
            Logger.info("Verification failed");
        }
        return resetPasswordResponse;
    }

    public static ArrayList<JobPreference> getCandidateJobPreferenceList(List<String> jobsList, Candidate candidate) {

        ArrayList<JobPreference> candidateJobPreferences = new ArrayList<>();
        for(String  s : jobsList) {
            JobPreference candidateJobPreference = new JobPreference();
            candidateJobPreference.candidate = candidate;
            JobRole jobRole = JobRole.find.where().eq("JobRoleId", s).findUnique();
            candidateJobPreference.jobRole = jobRole;
            candidateJobPreferences.add(candidateJobPreference);
        }
        return candidateJobPreferences;
    }

    public static ArrayList<LocalityPreference> getCandidateLocalityPreferenceList(List<String> localityList, Candidate candidate) {
        ArrayList<LocalityPreference> candidateLocalityPreferences = new ArrayList<>();
        for(String  localityId : localityList) {
            LocalityPreference candidateLocalityPreference = new LocalityPreference();
            candidateLocalityPreference.candidate= candidate;
            Locality locality = Locality.find.where()
                    .eq("localityId", localityId).findUnique();
            candidateLocalityPreference.locality = locality;
            candidateLocalityPreferences.add(candidateLocalityPreference);
        }
        return candidateLocalityPreferences;
    }

    // extract lead features from candidate obj and returns a lead object
    private static Lead getLeadFromCandidate(Candidate candidate) {
        // if no lead is there with the given mobile no.
        Lead lead = new Lead();
        lead.leadId = Util.randomLong();
        lead.leadUUId = UUID.randomUUID().toString();
        lead.leadName = candidate.candidateName;
        lead.leadMobile = candidate.candidateMobile;
        lead.leadChannel = ServerConstants.LEAD_CHANNEL_WEBSITE;
        lead.leadType = ServerConstants.TYPE_LEAD;
        lead.leadStatus = ServerConstants.LEAD_STATUS_WON;
        candidate.leadId = lead.leadId;
        return lead;
    }
}
