package controllers.businessLogic.Recruiter;

import api.InteractionConstants;
import api.ServerConstants;
import api.http.FormValidator;
import api.http.httpResponse.CandidateSignUpResponse;
import api.http.httpResponse.PartnerSignUpResponse;
import api.http.httpResponse.Recruiter.RecruiterSignUpResponse;
import models.entity.Recruiter.RecruiterAuth;
import models.entity.Recruiter.RecruiterProfile;
import models.entity.Recruiter.Static.RecruiterProfileStatus;
import models.util.EmailUtil;
import models.util.SmsUtil;
import models.util.Util;
import play.Logger;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

import static controllers.businessLogic.Recruiter.RecruiterInteractionService.createInteractionForRecruiterAddPasswordViaWebsite;
import static controllers.businessLogic.RecruiterService.addCredits;
import static play.mvc.Controller.session;

/**
 * Created by dodo on 4/10/16.
 */
public class RecruiterAuthService {
    public static RecruiterAuth isAuthExists(RecruiterProfile recruiterId){
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

        RecruiterProfile existingRecruiter =
                RecruiterProfile.find.where().eq("RecruiterProfileMobile",
                        FormValidator.convertToIndianMobileFormat(recruiterMobile)).findUnique();

        if(existingRecruiter != null) {
            // If recruiter exists
            RecruiterAuth recruiterAuth = RecruiterAuth.find.where().eq("recruiter_id",
                    existingRecruiter.getRecruiterProfileId()).findUnique();

            if(recruiterAuth != null){
                // If recruiter exists and has a password, reset the old password
                setNewPassword(recruiterAuth, recruiterPassword);
                RecruiterAuth.savePassword(recruiterAuth);
                recruiterAuth.setAuthSessionId(UUID.randomUUID().toString());
                recruiterAuth.setAuthSessionIdExpiryMillis(System.currentTimeMillis() + 24 * 60 * 60 * 1000);

                // adding session details
                addSession(recruiterAuth, existingRecruiter);
                recruiterAuth.setRecruiterAuthStatus(ServerConstants.RECRUITER_STATUS_VERIFIED);
                recruiterAuth.update();
                recruiterSignUpResponse.setStatus(RecruiterSignUpResponse.STATUS_SUCCESS);
                recruiterSignUpResponse.setRecruiterMobile(existingRecruiter.getRecruiterProfileMobile());
            } else {
                RecruiterAuth auth = new RecruiterAuth();
                auth.setRecruiterId(existingRecruiter);
                setNewPassword(auth, recruiterPassword);
                auth.setRecruiterAuthStatus(ServerConstants.RECRUITER_STATUS_VERIFIED);
                RecruiterAuth.savePassword(auth);
                auth.setAuthSessionId(UUID.randomUUID().toString());
                auth.setAuthSessionIdExpiryMillis(System.currentTimeMillis() + 24 * 60 * 60 * 1000);

                // adding session details
                addSession(auth, existingRecruiter);

                //adding recruiter interaction
                createInteractionForRecruiterAddPasswordViaWebsite(existingRecruiter.getRecruiterProfileUUId());
                recruiterSignUpResponse.setStatus(CandidateSignUpResponse.STATUS_SUCCESS);

                try {
                    RecruiterProfileStatus recruiterProfileStatus = RecruiterProfileStatus.find.where().eq("profile_status_id", ServerConstants.RECRUITER_STATE_ACTIVE).findUnique();
                    existingRecruiter.setRecruiterprofilestatus(recruiterProfileStatus);
                    if(recruiterProfileStatus != null){
                        recruiterSignUpResponse.setStatus(RecruiterSignUpResponse.STATUS_SUCCESS);
                    }
                } catch (NullPointerException n) {
                    Logger.info("Oops recruiterStatusId"+ " doesnot exists");
                    recruiterSignUpResponse.setStatus(RecruiterSignUpResponse.STATUS_FAILURE);
                }

                String createdBy = "Not specified";

                if(session().get("sessionUsername") != null){
                    createdBy = "Support: " + session().get("sessionUsername");
                }

                if(existingRecruiter.getRecruiterAccessLevel() == ServerConstants.RECRUITER_ACCESS_LEVEL_OPEN){
                    //assigning some free contact unlock credits for the recruiter
                    addCredits(existingRecruiter, ServerConstants.RECRUITER_CATEGORY_CONTACT_UNLOCK, ServerConstants.RECRUITER_FREE_CONTACT_CREDITS, createdBy, null);

                    //sending welcome email and sms to recruiter
                    EmailUtil.sendRecruiterWelcomeEmailForSelfSignup(existingRecruiter);
                    SmsUtil.sendRecruiterWelcomeSmsForSelfSignup(existingRecruiter.getRecruiterProfileName(),
                            existingRecruiter.getRecruiterProfileMobile());

                    recruiterSignUpResponse.setFirstTime(ServerConstants.RECRUITER_FIRST_TIME);
                } else {
                    String startDateString = "2020-12-31";
                    DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
                    Date startDate = null;
                    try {
                        startDate = df.parse(startDateString);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    addCredits(existingRecruiter, ServerConstants.RECRUITER_CATEGORY_INTERVIEW_UNLOCK, ServerConstants.RECRUITER_DEFAULT_INTERVIEW_CREDITS, createdBy, startDate);
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
        session().put("sessionUsername", "RID-"+String.valueOf(recruiterProfile.getRecruiterProfileId()));
        session().put("recruiterId", String.valueOf(recruiterProfile.getRecruiterProfileId()));
        session().put("recruiterName", String.valueOf(recruiterProfile.getRecruiterProfileName()));
        session().put("recruiterMobile", String.valueOf(recruiterProfile.getRecruiterProfileMobile()));
        session().put("sessionExpiry", String.valueOf(existingAuth.getAuthSessionIdExpiryMillis()));
        session().put("sessionUsername", String.valueOf(recruiterProfile.getRecruiterProfileName() + "_recruiter"));
        session().put("sessionChannel", String.valueOf(InteractionConstants.INTERACTION_CHANNEL_RECRUITER_WEBSITE));
        Logger.info("set-sessionId"+ session().get("sessionId"));
    }
}
