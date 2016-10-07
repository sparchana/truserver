package controllers.businessLogic;

import api.InteractionConstants;
import api.ServerConstants;
import api.http.FormValidator;
import api.http.httpResponse.CandidateSignUpResponse;
import api.http.httpResponse.PartnerSignUpResponse;
import api.http.httpResponse.Recruiter.RecruiterSignUpResponse;
import models.entity.*;
import models.entity.Static.PartnerProfileStatus;
import models.entity.Static.RecruiterProfileStatus;
import models.util.SmsUtil;
import models.util.Util;
import play.Logger;

import java.util.UUID;

import static controllers.businessLogic.PartnerInteractionService.createInteractionForPartnerAddPasswordViaWebsite;
import static controllers.businessLogic.PartnerInteractionService.createInteractionForPartnerResetPasswordViaWebsite;
import static play.mvc.Controller.session;

/**
 * Created by dodo on 4/10/16.
 */
public class RecruiterAuthService {
    public static RecruiterAuth isAuthExists(Long recruiterId){
        return RecruiterAuth.find.where().eq("recruiterId", recruiterId).findUnique();
    }

    public static void setNewPassword(RecruiterAuth recruiterAuth, String password){
        recruiterAuth.setPasswordMd5(Util.md5(password + recruiterAuth.getPasswordSalt()));
        recruiterAuth.setAuthSessionIdExpiryMillis(System.currentTimeMillis() + 24 * 60 * 60 * 1000);
        session("sessionId", recruiterAuth.getAuthSessionId());
        session("sessionExpiry", String.valueOf(recruiterAuth.getAuthSessionIdExpiryMillis()));
    }

    public static RecruiterSignUpResponse savePassword(String recruiterMobile, String recruiterPassword) {
        RecruiterSignUpResponse recruiterSignUpResponse = new RecruiterSignUpResponse();

        Logger.info("to check: " + recruiterMobile);
        RecruiterProfile existingRecruiter = RecruiterProfile.find.where().eq("RecruiterProfileMobile", FormValidator.convertToIndianMobileFormat(recruiterMobile)).findUnique();
        if(existingRecruiter != null) {
            // If recruiter exists
            Logger.info(existingRecruiter.getRecruiterProfileId() + " : recruiter ID");
            RecruiterAuth recruiterAuth = RecruiterAuth.find.where().eq("recruiter_id", existingRecruiter.getRecruiterProfileId()).findUnique();
            if(recruiterAuth != null){
                // If recruiter exists and has a password, reset the old password
                Logger.info("Resetting password");
                setNewPassword(recruiterAuth, recruiterPassword);
                RecruiterAuth.savePassword(recruiterAuth);
                recruiterAuth.setAuthSessionId(UUID.randomUUID().toString());
                recruiterAuth.setAuthSessionIdExpiryMillis(System.currentTimeMillis() + 24 * 60 * 60 * 1000);

                /* adding session details */
                addSession(recruiterAuth, existingRecruiter);
                recruiterSignUpResponse.setStatus(PartnerSignUpResponse.STATUS_SUCCESS);
                recruiterSignUpResponse.setRecruiterMobile(existingRecruiter.getRecruiterProfileMobile());
            } else {
                RecruiterAuth auth = new RecruiterAuth();
                auth.setRecruiterId(existingRecruiter.getRecruiterProfileId());
                setNewPassword(auth, recruiterPassword);
                auth.setRecruiterAuthStatus(ServerConstants.RECRUITER_STATUS_VERIFIED);
                RecruiterAuth.savePassword(auth);
                auth.setAuthSessionId(UUID.randomUUID().toString());
                auth.setAuthSessionIdExpiryMillis(System.currentTimeMillis() + 24 * 60 * 60 * 1000);

                /* adding session details */
                addSession(auth, existingRecruiter);

                recruiterSignUpResponse.setStatus(CandidateSignUpResponse.STATUS_SUCCESS);

                try {
                    existingRecruiter.setRecruiterprofilestatus(RecruiterProfileStatus.find.where().eq("profile_status_id", ServerConstants.RECRUITER_STATE_ACTIVE).findUnique());
                    recruiterSignUpResponse.setStatus(RecruiterSignUpResponse.STATUS_SUCCESS);
                } catch (NullPointerException n) {
                    Logger.info("Oops recruiterStatusId"+ " doesnot exists");
                    recruiterSignUpResponse.setStatus(RecruiterSignUpResponse.STATUS_FAILURE);
                }

                existingRecruiter.update();
                Logger.info("recruiter status confirmed");

                recruiterSignUpResponse.setRecruiterMobile(existingRecruiter.getRecruiterProfileMobile());
            }
            Logger.info("Auth Save Successful");
        }
        else {
            Logger.info("Recruiter Does not Exist!");
            recruiterSignUpResponse.setStatus(RecruiterSignUpResponse.STATUS_FAILURE);
        }
        return recruiterSignUpResponse;
    }

    public static void addSession(RecruiterAuth existingAuth, RecruiterProfile recruiterProfile){
        session().put("sessionId", existingAuth.getAuthSessionId());
        session().put("recruiterId", String.valueOf(recruiterProfile.getRecruiterProfileId()));
        session().put("recruiterName", String.valueOf(recruiterProfile.getRecruiterProfileName()));
        session().put("recruiterMobile", String.valueOf(recruiterProfile.getRecruiterProfileMobile()));
        session().put("sessionExpiry", String.valueOf(existingAuth.getAuthSessionIdExpiryMillis()));
        Logger.info("set-sessionId"+ session().get("sessionId"));
    }
}