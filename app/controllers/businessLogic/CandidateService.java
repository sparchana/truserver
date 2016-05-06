package controllers.businessLogic;

import api.ServerConstants;
import api.http.AddLeadResponse;
import api.http.CandidateSignUpResponse;
import api.http.LoginResponse;
import api.http.ResetPasswordResponse;
import models.entity.*;
import models.util.SmsUtil;
import models.util.Util;
import play.Logger;

import java.util.List;
import java.util.UUID;

import static play.mvc.Controller.session;

/**
 * Created by batcoder1 on 3/5/16.
 */
public class CandidateService {
    public static CandidateSignUpResponse createCandidate(Candidate candidate, List<String> locality, List<String> jobs){
        CandidateSignUpResponse candidateSignUpResponse = new CandidateSignUpResponse();
        Logger.info("Checking this mobile : " + candidate.candidateMobile );
        Candidate existingCandidate = Candidate.find.where().eq("candidateMobile",candidate.candidateMobile).findUnique();
        Lead existingLead = Lead.find.where().eq("leadMobile", candidate.candidateMobile).findUnique();
        int randomPIN = 0;
        String otpCode;

        Interaction interaction = new Interaction();
        if(existingCandidate == null) {
            if(existingLead == null){
                AddLeadResponse addLeadResponse = new AddLeadResponse();
                Lead lead = new Lead();
                lead.leadId = Util.randomLong();
                lead.leadUUId = UUID.randomUUID().toString();
                lead.leadName = candidate.candidateName;
                lead.leadMobile = candidate.candidateMobile;
                lead.leadChannel = ServerConstants.LEAD_CHANNEL_WEBSITE;
                lead.leadType = ServerConstants.TYPE_LEAD;
                lead.leadStatus = ServerConstants.LEAD_STATUS_WON;
                candidate.leadId = lead.leadId;
                interaction.objectAUUId = candidate.candidateUUId;

                addLeadResponse = LeadService.createLead(lead);
            }
            else{
                candidate.leadId = existingLead.leadId;
                interaction.objectAUUId = candidate.candidateUUId;
            }

            for(String  s : locality) {
                CandidateLocality candidateLocality = new CandidateLocality();
                candidateLocality.candidateLocalityId = Util.randomLong();
                candidateLocality.candidateLocalityCandidateId = candidate.candidateId;
                candidateLocality.candidateLocalityLocalityId = s;
                candidateLocality.save();
            }

            for(String  s : jobs) {
                CandidateJob candidateJob = new CandidateJob();
                candidateJob.candidateJobId = Util.randomLong();
                candidateJob.candidateJobCandidateId = candidate.candidateId;
                candidateJob.candidateJobJobId = s;
                candidateJob.save();
            }

            candidateSignUpResponse = Candidate.candidateSignUp(candidate);
            interaction.objectAType = ServerConstants.OBJECT_TYPE_CANDIDATE;
            interaction.interactionType = ServerConstants.INTERACTION_TYPE_WEBSITE;
            interaction.result = "New Candidate Added";
            InteractionService.createIntraction(interaction);

            randomPIN = (int)(Math.random()*9000)+1000;
            otpCode = String.valueOf(randomPIN);
            String msg = "Welcome to Trujobs.in! Use OTP " + otpCode + " to register";
            SmsUtil.sendSms(candidate.candidateMobile, msg);

            Logger.info("Candidate successfully registered " + candidate);

            candidateSignUpResponse.setOtp(randomPIN);
            Logger.info(" -- " + candidateSignUpResponse.otp);
        }

        else if(existingCandidate != null && existingCandidate.candidateStatusId == 0){
            candidateSignUpResponse = Candidate.candidateUpdate(existingCandidate,candidate);


            List<CandidateLocality> allLocality = CandidateLocality.find.where().eq("candidateLocalityCandidateId", existingCandidate.candidateId).findList();
            for(CandidateLocality candidateLocality : allLocality){
                candidateLocality.delete();
            }

            for(String  s : locality) {
                CandidateLocality candidateLocality = new CandidateLocality();
                candidateLocality.candidateLocalityId = Util.randomLong();
                candidateLocality.candidateLocalityCandidateId = existingCandidate.candidateId;
                candidateLocality.candidateLocalityLocalityId = s;
                candidateLocality.save();
            }

            List<CandidateJob> allJob = CandidateJob.find.where().eq("candidateJobCandidateId", existingCandidate.candidateId).findList();
            for(CandidateJob candidateJobs : allJob){
                candidateJobs.delete();
            }

            for(String  s : jobs) {

                CandidateJob candidateJob = new CandidateJob();
                candidateJob.candidateJobId = Util.randomLong();
                candidateJob.candidateJobCandidateId = existingCandidate.candidateId;
                candidateJob.candidateJobJobId = s;
                candidateJob.save();
            }

            randomPIN = (int)(Math.random()*9000)+1000;
            otpCode = String.valueOf(randomPIN);
            Logger.info("Existing Candidate successfully updated" + candidate);
            interaction.objectAUUId = existingCandidate.candidateUUId;
            interaction.objectAType = ServerConstants.OBJECT_TYPE_CANDIDATE;
            interaction.interactionType = ServerConstants.INTERACTION_TYPE_WEBSITE;
            interaction.result = "New Candidate Added";
            InteractionService.createIntraction(interaction);
            String msg = "Welcome to Trujobs.in! Use OTP " + otpCode + " to register";
            SmsUtil.sendSms(candidate.candidateMobile, msg);
            candidateSignUpResponse.setOtp(randomPIN);

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
                    (existingCandidate.candidateStatusId != 0))) {
                    Logger.info(existingCandidate.candidateName + " " + existingCandidate.candidateStatusId);
                    loginResponse.setCandidateId(existingCandidate.candidateId);
                    loginResponse.setCandidateName(existingCandidate.candidateName);
                    loginResponse.setAccountStatus(existingCandidate.candidateStatusId);
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

    public static ResetPasswordResponse checkCandidate(String candidateMobile){
        ResetPasswordResponse resetPasswordResponse = new ResetPasswordResponse();
        Candidate existingCandidate = Candidate.find.where().eq("candidateMobile", "+91" + candidateMobile).findUnique();
        if(existingCandidate != null){
            if(existingCandidate.candidateStatusId == ServerConstants.CANDIDATE_STATUS_VERIFIED){
                int randomPIN = (int)(Math.random()*9000)+1000;
                String otpCode = String.valueOf(randomPIN);
                existingCandidate.update();
                String msg = "Welcome to Trujobs.in! Use OTP " + otpCode + " to reset password";
                String getResponse = SmsUtil.sendSms(existingCandidate.candidateMobile, msg);
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

}
