package controllers.businessLogic;

import api.ServerConstants;
import api.http.CandidateSignUpResponse;
import api.http.LoginResponse;
import api.http.ResetPasswordResponse;
import models.entity.Auth;
import models.entity.Candidate;
import models.entity.Interaction;
import models.entity.Lead;
import models.entity.OM.JobPreference;
import models.entity.OM.LocalityPreference;
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

    public static CandidateSignUpResponse createCandidate(Candidate candidate, List<String> localityList, List<String> jobsList){
        CandidateSignUpResponse candidateSignUpResponse = new CandidateSignUpResponse();
        Logger.info("Checking this mobile : " + candidate.candidateMobile );
        Candidate existingCandidate = Candidate.find.where().eq("candidateMobile",candidate.candidateMobile).findUnique();
        Lead existingLead = Lead.find.where().eq("leadMobile", candidate.candidateMobile).findUnique();
        int randomPIN = generateOtp();

        Interaction interaction = new Interaction();
        if(existingCandidate == null) {

            // if no candidate exists
            if(existingLead == null){
                LeadService.createLead(getLeadFromCandidate(candidate));
            }
            else{
                candidate.leadId = existingLead.leadId;
            }

            candidate.localityPreferenceList  = getCandidateLocalityPreferenceList(localityList, candidate);
            candidate.jobPreferencesList = getCandidateJobPreferenceList(jobsList, candidate);

            Candidate.registerCandidate(candidate);
            candidateSignUpResponse.setStatus(CandidateSignUpResponse.STATUS_SUCCESS);
            candidateSignUpResponse.setCandidateId(candidate.candidateId);
            candidateSignUpResponse.setCandidateName(candidate.candidateName);

            interaction.objectAUUId = candidate.candidateUUId;
            interaction.result = "New Candidate Added";

            String msg = "Welcome to Trujobs.in! Use OTP " + randomPIN + " to register";
            SendOtpService.sendSms(candidate.candidateMobile, msg);

            Logger.info("Candidate successfully registered " + candidate);
            interaction.objectAType = ServerConstants.OBJECT_TYPE_CANDIDATE;
            interaction.interactionType = ServerConstants.INTERACTION_TYPE_WEBSITE;
            InteractionService.createIntraction(interaction);

            candidateSignUpResponse.setOtp(randomPIN);
        }

        else if(existingCandidate != null && existingCandidate.candidateprofilestatus.profileStatusId == 0){

            candidateSignUpResponse.setStatus(CandidateSignUpResponse.STATUS_SUCCESS);
            candidateSignUpResponse.setCandidateId(candidate.candidateId);
            candidateSignUpResponse.setCandidateName(candidate.candidateName);
            candidateSignUpResponse.setStatus(CandidateSignUpResponse.STATUS_SUCCESS);

            List<LocalityPreference> allLocality = models.entity.OM.LocalityPreference.find.where().eq("CandidateId", existingCandidate.candidateId).findList();
            for(LocalityPreference candidateLocality : allLocality){
                candidateLocality.delete();
            }

            List<JobPreference> allJob = JobPreference.find.where().eq("CandidateId", existingCandidate.candidateId).findList();
            for(JobPreference candidateJobs : allJob){
                candidateJobs.delete();
            }

            existingCandidate.localityPreferenceList = getCandidateLocalityPreferenceList(localityList, candidate);
            existingCandidate.jobPreferencesList = getCandidateJobPreferenceList(jobsList, candidate);

            Candidate.candidateUpdate(existingCandidate);

            Logger.info("Existing Candidate successfully updated" + candidate);

            interaction.objectAUUId = existingCandidate.candidateUUId;
            interaction.result = "New Candidate Added";
            InteractionService.createIntraction(interaction);

            String msg = "Welcome to Trujobs.in! Use OTP " + randomPIN + " to register";
            SendOtpService.sendSms(candidate.candidateMobile, msg);
            candidateSignUpResponse.setOtp(randomPIN);

            interaction.objectAType = ServerConstants.OBJECT_TYPE_CANDIDATE;
            interaction.interactionType = ServerConstants.INTERACTION_TYPE_WEBSITE;
            InteractionService.createIntraction(interaction);
        }

        else{
            candidateSignUpResponse.setStatus(CandidateSignUpResponse.STATUS_EXISTS);
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

    private static ArrayList<JobPreference> getCandidateJobPreferenceList(List<String> jobsList, Candidate candidate) {

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

    private static ArrayList<LocalityPreference> getCandidateLocalityPreferenceList(List<String> localityList, Candidate candidate) {
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
