package controllers.businessLogic;

import api.InteractionConstants;
import api.ServerConstants;
import api.http.FormValidator;
import api.http.httpRequest.AddCompanyRequest;
import api.http.httpRequest.Recruiter.AddCreditRequest;
import api.http.httpRequest.Recruiter.AddRecruiterRequest;
import api.http.httpRequest.Recruiter.RecruiterSignUpRequest;
import api.http.httpResponse.AddCompanyResponse;
import api.http.httpResponse.LoginResponse;
import api.http.httpResponse.Recruiter.AddCreditResponse;
import api.http.httpResponse.Recruiter.AddRecruiterResponse;
import api.http.httpResponse.Recruiter.RecruiterSignUpResponse;
import api.http.httpResponse.Recruiter.UnlockContactResponse;
import api.http.httpResponse.ResetPasswordResponse;
import api.http.httpResponse.interview.InterviewResponse;
import controllers.businessLogic.Recruiter.RecruiterAuthService;
import controllers.businessLogic.Recruiter.RecruiterInteractionService;
import controllers.businessLogic.Recruiter.RecruiterLeadService;
import dao.RecruiterCreditHistoryDAO;
import models.entity.Recruiter.OM.RecruiterToCandidateUnlocked;
import models.entity.Recruiter.RecruiterAuth;
import models.entity.Recruiter.RecruiterLead;
import models.entity.Recruiter.RecruiterProfile;
import models.entity.*;
import models.entity.Recruiter.Static.RecruiterCreditCategory;
import models.entity.Recruiter.Static.RecruiterStatus;
import models.util.EmailUtil;
import models.util.SmsUtil;
import models.util.Util;
import play.Logger;
import play.mvc.Result;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import static api.InteractionConstants.INTERACTION_CHANNEL_CANDIDATE_ANDROID;
import static api.InteractionConstants.INTERACTION_CHANNEL_CANDIDATE_WEBSITE;
import static controllers.businessLogic.Recruiter.RecruiterInteractionService.*;
import static models.util.Util.generateOtp;
import static play.libs.Json.toJson;
import static play.mvc.Controller.session;
import static play.mvc.Results.created;
import static play.mvc.Results.ok;

/**
 * Created by batcoder1 on 7/7/16.
 */
public class RecruiterService {

    public static RecruiterProfile isRecruiterExists(String mobile) {

        RecruiterProfile recruiterProfile = RecruiterProfile.find.where().eq("RecruiterProfileMobile",
                FormValidator.convertToIndianMobileFormat(mobile)).findUnique();

        if(recruiterProfile != null) {
            return recruiterProfile;
        }

        return null;
    }

    public static LoginResponse login(String loginMobile, String loginPassword){
        LoginResponse loginResponse = new LoginResponse();
        Logger.info(" login mobile: " + loginMobile);
        RecruiterProfile existingRecruiter = isRecruiterExists(FormValidator.convertToIndianMobileFormat(loginMobile));
        if(existingRecruiter == null){
            loginResponse.setStatus(LoginResponse.STATUS_NO_USER);
            Logger.info("Recruiter Does not Exists");
        } else {
            long recruiterId = existingRecruiter.getRecruiterProfileId();
            RecruiterAuth recruiterAuth = RecruiterAuth.find.where().eq("recruiter_id", recruiterId).findUnique();
            if(recruiterAuth != null){
                if ((recruiterAuth.getPasswordMd5().equals(Util.md5(loginPassword + recruiterAuth.getPasswordSalt())))) {
                    loginResponse.setCandidateId(existingRecruiter.getRecruiterProfileId());
                    loginResponse.setCandidateFirstName(existingRecruiter.getRecruiterProfileName());

                    loginResponse.setStatus(LoginResponse.STATUS_SUCCESS);

                    recruiterAuth.setAuthSessionId(UUID.randomUUID().toString());
                    recruiterAuth.setAuthSessionIdExpiryMillis(System.currentTimeMillis() + 24 * 60 * 60 * 1000);

                    loginResponse.setAuthSessionId(recruiterAuth.getAuthSessionId());
                    loginResponse.setSessionExpiryInMilliSecond(recruiterAuth.getAuthSessionIdExpiryMillis());

                    loginResponse.setIsCandidateVerified(recruiterAuth.getRecruiterAuthStatus());

                    /* adding session details */
                    RecruiterAuthService.addSession(recruiterAuth, existingRecruiter);
                    String sessionId = session().get("sessionId");
                    recruiterAuth.update();

                    //adding login interaction
                    RecruiterInteractionService.createInteractionForRecruiterLogin(existingRecruiter.getRecruiterProfileUUId());
                    Logger.info("Login Successful");
                } else {
                    loginResponse.setStatus(LoginResponse.STATUS_WRONG_PASSWORD);
                    Logger.info("Incorrect Password");
                }
            } else {
                loginResponse.setStatus(LoginResponse.STATUS_NO_USER);
                Logger.info("No User");
            }
        }
        return loginResponse;
    }


    public static RecruiterSignUpResponse recruiterSignUp(RecruiterSignUpRequest recruiterSignUpRequest){
        RecruiterSignUpResponse recruiterSignUpResponse = new RecruiterSignUpResponse();
        RecruiterProfile newRecruiter = new RecruiterProfile();

        Logger.info("Checking for mobile number: " + FormValidator.convertToIndianMobileFormat(recruiterSignUpRequest.getRecruiterMobile()));
        RecruiterProfile recruiterProfile = isRecruiterExists(FormValidator.convertToIndianMobileFormat(recruiterSignUpRequest.getRecruiterMobile()));

        String result = "";
        String objectAUUId = "";
        Integer interactionType;
        recruiterSignUpResponse.setRecruiterMobile(recruiterSignUpRequest.getRecruiterMobile());
        if(recruiterProfile == null) {
            //checking if company exists or not
            Company existingCompany = Company.find.where().eq("companyName", recruiterSignUpRequest.getRecruiterCompany()).findUnique();
            if(existingCompany == null) {
                AddCompanyResponse addCompanyResponse;
                AddCompanyRequest addCompanyRequest = new AddCompanyRequest();
                addCompanyRequest.setCompanyName(recruiterSignUpRequest.getRecruiterCompanyName());
                addCompanyRequest.setCompanyLogo(ServerConstants.DEFAULT_COMPANY_LOGO);
                addCompanyRequest.setCompanyStatus(1);
                addCompanyResponse = CompanyService.addCompany(addCompanyRequest);

                existingCompany = Company.find.where().eq("companyId", addCompanyResponse.getCompanyId()).findUnique();
                if(existingCompany == null){
                    recruiterSignUpResponse.setStatus(RecruiterSignUpResponse.getStatusFailure());
                    return recruiterSignUpResponse;
                }
            }

            newRecruiter.setRecruiterProfileMobile(FormValidator.convertToIndianMobileFormat(recruiterSignUpRequest.getRecruiterMobile()));
            newRecruiter = getAndSetRecruiterValues(recruiterSignUpRequest, newRecruiter, existingCompany);
            triggerOtp(newRecruiter, recruiterSignUpResponse);

            //setting recruiter lead
            String leadName = recruiterSignUpRequest.getRecruiterName();
            RecruiterLead lead = RecruiterLeadService.createOrUpdateConvertedRecruiterLead(leadName,
                    FormValidator.convertToIndianMobileFormat(recruiterSignUpRequest.getRecruiterMobile()),
                    ServerConstants.LEAD_CHANNEL_RECRUITER);
            newRecruiter.setRecruiterLead(lead);

            //setting recruiter status as "NEW"
            RecruiterStatus recruiterStatus = RecruiterStatus.find.where().eq("RecruiterStatusId", 1).findUnique();
            if(recruiterStatus != null){
                newRecruiter.setRecStatus(recruiterStatus);
            }

            newRecruiter.save();

            //assigning some free contact unlock credits for the recruiter
            addContactCredit(newRecruiter, ServerConstants.RECRUITER_FREE_CONTACT_CREDITS);

            interactionType = InteractionConstants.INTERACTION_TYPE_RECRUITER_SIGN_UP;
            result = InteractionConstants.INTERACTION_RESULT_NEW_RECRUITER;
            objectAUUId = newRecruiter.getRecruiterProfileUUId();

            recruiterSignUpResponse.setStatus(AddRecruiterResponse.STATUS_SUCCESS);
            recruiterSignUpResponse.setRecruiterId(newRecruiter.getRecruiterProfileId());
            Logger.info("Recruiter successfully saved");

        } else {
            RecruiterAuth auth = RecruiterAuthService.isAuthExists(recruiterProfile);
            if(auth == null ) {
                Logger.info("recruiter auth doesn't exists for this recruiter");
                getAndSetRecruiterValues(recruiterSignUpRequest, recruiterProfile, null);

                recruiterSignUpResponse.setStatus(AddRecruiterResponse.STATUS_SUCCESS);
                recruiterSignUpResponse.setRecruiterId(recruiterProfile.getRecruiterProfileId());
                recruiterSignUpResponse.setRecruiterMobile(recruiterProfile.getRecruiterProfileMobile());

                triggerOtp(recruiterProfile, recruiterSignUpResponse);

                interactionType = InteractionConstants.INTERACTION_TYPE_EXISTING_RECRUITER_TRIED_SIGNUP;
                result = InteractionConstants.INTERACTION_RESULT_EXISTING_RECRUITER_VERIFICATION;
                objectAUUId = recruiterProfile.getRecruiterProfileUUId();
            } else {
                Logger.info("Recruiter Already exists");
                interactionType = InteractionConstants.INTERACTION_TYPE_EXISTING_RECRUITER_TRIED_SIGNUP_AND_SIGNUP_NOT_ALLOWED;
                result = InteractionConstants.INTERACTION_RESULT_EXISTING_RECRUITER_SIGNUP;
                recruiterSignUpResponse.setStatus(RecruiterSignUpResponse.STATUS_EXISTS);
            }
            recruiterProfile.update();
        }

        //creating interaction
        Logger.info("Creating signup interaction for recruiter");
        String createdBy = InteractionConstants.INTERACTION_CREATED_SELF;
        RecruiterInteractionService.createInteractionForRecruiterSignUp(objectAUUId, result, interactionType, createdBy);

        return recruiterSignUpResponse;
    }

    public static AddRecruiterResponse createRecruiterProfile(RecruiterSignUpRequest recruiterSignUpRequest,
                                                              int channelType)
    {
        AddRecruiterResponse addRecruiterResponse = new AddRecruiterResponse();
        String result = "";
        Integer interactionType;
        Company existingCompany = Company.find.where().eq("companyId", recruiterSignUpRequest.getRecruiterCompany()).findUnique();
        int leadChannel = (channelType == INTERACTION_CHANNEL_CANDIDATE_WEBSITE) ?
                ServerConstants.LEAD_CHANNEL_RECRUITER :
                ServerConstants.LEAD_CHANNEL_SUPPORT;

        if(existingCompany != null){
            RecruiterProfile existingRecruiter = isRecruiterExists(FormValidator.convertToIndianMobileFormat(recruiterSignUpRequest.getRecruiterMobile()));
            if(existingRecruiter == null){
                RecruiterProfile newRecruiter = new RecruiterProfile();
                newRecruiter.setRecruiterProfileMobile(FormValidator.convertToIndianMobileFormat(recruiterSignUpRequest.getRecruiterMobile()));
                newRecruiter = getAndSetRecruiterValues(recruiterSignUpRequest, newRecruiter, existingCompany);

                //setting recruiter status as "ACTIVE"
                RecruiterStatus recruiterStatus = RecruiterStatus.find.where().eq("RecruiterStatusId", 2).findUnique();
                if(recruiterStatus != null){
                    newRecruiter.setRecStatus(recruiterStatus);
                }

                //setting recruiter lead
                String leadName = recruiterSignUpRequest.getRecruiterName();
                RecruiterLead lead = RecruiterLeadService.createOrUpdateConvertedRecruiterLead(leadName,
                        FormValidator.convertToIndianMobileFormat(recruiterSignUpRequest.getRecruiterMobile()),
                        leadChannel);

                newRecruiter.setRecruiterLead(lead);

                newRecruiter.save();

                //assigning free contact unlock credits for the recruiter
                addContactCredit(newRecruiter, ServerConstants.RECRUITER_FREE_CONTACT_CREDITS);

                //setting all the credit values
                setCreditHistoryValues(newRecruiter, recruiterSignUpRequest);

                addRecruiterResponse.setStatus(AddRecruiterResponse.STATUS_SUCCESS);
                addRecruiterResponse.setRecruiterId(newRecruiter.getRecruiterProfileId());

                RecruiterSignUpResponse recruiterSignUpResponse = new RecruiterSignUpResponse();
                createAndSaveDummyAuthFor(newRecruiter);

                result = InteractionConstants.INTERACTION_RESULT_RECRUITER_SIGNUP_VIA_SUPPORT;
                interactionType = InteractionConstants.INTERACTION_TYPE_RECRUITER_SIGN_UP;

                //creating interaction
                Logger.info("Creating signup interaction for recruiter");
                String createdBy = session().get("sessionUsername");
                RecruiterInteractionService.createInteractionForRecruiterSignUp(newRecruiter.getRecruiterProfileUUId(), result, interactionType, createdBy);
                Logger.info("Recruiter successfully saved");
            } else {
                existingRecruiter = getAndSetRecruiterValues(recruiterSignUpRequest, existingRecruiter, existingCompany);

                //setting recruiter status as "ACTIVE"
                RecruiterStatus recruiterStatus = RecruiterStatus.find.where().eq("RecruiterStatusId", 2).findUnique();
                if(recruiterStatus != null){
                    existingRecruiter.setRecStatus(recruiterStatus);
                }

                //setting recruiter lead
                String leadName = recruiterSignUpRequest.getRecruiterName();
                RecruiterLead lead = RecruiterLeadService.createOrUpdateConvertedRecruiterLead(leadName,
                        FormValidator.convertToIndianMobileFormat(recruiterSignUpRequest.getRecruiterMobile()),
                        leadChannel);
                existingRecruiter.setRecruiterLead(lead);

                existingRecruiter.update();

                //setting all the credit values
                setCreditHistoryValues(existingRecruiter, recruiterSignUpRequest);

                if ((recruiterSignUpRequest.getContactCredits() != null && recruiterSignUpRequest.getContactCredits() > 0) ||
                        (recruiterSignUpRequest.getInterviewCredits() != null && recruiterSignUpRequest.getInterviewCredits() > 0))
                {
                    EmailUtil.sendRecruiterCreditTopupMail(existingRecruiter,
                            recruiterSignUpRequest.getContactCredits(),
                            recruiterSignUpRequest.getInterviewCredits());

                    SmsUtil.sendRecruiterCreditTopupSms(existingRecruiter,
                            recruiterSignUpRequest.getContactCredits(),
                            recruiterSignUpRequest.getInterviewCredits());
                }

                if (channelType == INTERACTION_CHANNEL_CANDIDATE_WEBSITE) {
                    result = InteractionConstants.INTERACTION_RESULT_RECRUITER_INFO_UPDATED_SELF;
                } else {
                    result = InteractionConstants.INTERACTION_RESULT_RECRUITER_INFO_UPDATED_SUPPORT;
                }
                //adding interaction
                createInteractionForRecruiterProfileUpdate(existingRecruiter.getRecruiterProfileUUId(), result, channelType);

                addRecruiterResponse.setRecruiterId(existingRecruiter.getRecruiterProfileId());
                addRecruiterResponse.setStatus(AddRecruiterResponse.STATUS_UPDATE);
                Logger.info("Recruiter already exists. Updated The recruiter");
            }
        } else{
            addRecruiterResponse.setStatus(AddRecruiterResponse.STATUS_FAILURE);
            Logger.info("Company Does not exists");
        }
        return addRecruiterResponse;
    }

    private static void setCreditHistoryValues(RecruiterProfile existingRecruiter, RecruiterSignUpRequest addRecruiterRequest) {
        //setting values for candidate contact unlock credits
        if(addRecruiterRequest.getContactCredits() != null && addRecruiterRequest.getContactCredits() != 0){
            if(addRecruiterRequest.getContactCredits() > 0){

                addCredits(existingRecruiter, ServerConstants.RECRUITER_CATEGORY_CONTACT_UNLOCK, addRecruiterRequest.getContactCredits());
            } else{
                //debit

                Integer creditsToDebited = addRecruiterRequest.getContactCredits() * (-1);
                debitCredits(existingRecruiter, ServerConstants.RECRUITER_CATEGORY_CONTACT_UNLOCK, addRecruiterRequest.getContactCredits());
            }
        }

        //setting values for interview unlock credits
        if(addRecruiterRequest.getInterviewCredits() != null && addRecruiterRequest.getInterviewCredits() != 0){

            if(addRecruiterRequest.getInterviewCredits() > 0){
                //credit
                addCredits(existingRecruiter, ServerConstants.RECRUITER_CATEGORY_INTERVIEW_UNLOCK, addRecruiterRequest.getInterviewCredits());

            } else{
                //debit
                Integer creditsToDebited = addRecruiterRequest.getInterviewCredits() * (-1);
                debitCredits(existingRecruiter, ServerConstants.RECRUITER_CATEGORY_INTERVIEW_UNLOCK, addRecruiterRequest.getInterviewCredits());

            }
        }
    }

    public static Result unlockCandidate(RecruiterProfile recruiterProfile, Long candidateId) {
        UnlockContactResponse unlockContactResponse = new UnlockContactResponse();
        Candidate candidate = Candidate.find.where().eq("CandidateId", candidateId).findUnique();
        if(candidate != null){
            RecruiterToCandidateUnlocked existingUnlockedCandidate = RecruiterToCandidateUnlocked.find.where()
                    .eq("recruiterProfileId", recruiterProfile.getRecruiterProfileId())
                    .eq("CandidateId", candidate.getCandidateId())
                    .findUnique();

            if(existingUnlockedCandidate == null){
                // this candidate has not been unlocked by the recruiter, hence unlock it
                Logger.info("Recruiter with mobile no: " + recruiterProfile.getRecruiterProfileMobile() + " is unlocking candidate with mobile: " + candidate.getCandidateMobile());

                //making an entry in recruiter credit history table
                RecruiterCreditHistory recruiterCreditHistory = new RecruiterCreditHistory();
                recruiterCreditHistory.setRecruiterProfile(recruiterProfile);

                RecruiterCreditCategory recruiterCreditCategory = RecruiterCreditCategory.find.where().eq("recruiter_credit_category_id", ServerConstants.RECRUITER_CATEGORY_CONTACT_UNLOCK).findUnique();
                if(recruiterCreditCategory != null){
                    recruiterCreditHistory.setRecruiterCreditCategory(recruiterCreditCategory);
                }

                RecruiterCreditHistory recruiterCreditHistoryLatest = RecruiterCreditHistory.find.where()
                        .eq("RecruiterProfileId", recruiterProfile.getRecruiterProfileId())
                        .eq("RecruiterCreditCategory", ServerConstants.RECRUITER_CATEGORY_CONTACT_UNLOCK)
                        .setMaxRows(1)
                        .orderBy("create_timestamp desc")
                        .findUnique();

                if(recruiterCreditHistoryLatest != null){
                    if(recruiterCreditHistoryLatest.getRecruiterCreditsAvailable() > 0){
                        recruiterCreditHistory.setRecruiterCreditsAvailable(recruiterCreditHistoryLatest.getRecruiterCreditsAvailable() - 1);
                        recruiterCreditHistory.setRecruiterCreditsUsed(recruiterCreditHistoryLatest.getRecruiterCreditsUsed() + 1);
                        //adding a entry in recruiterToCandidateUnblock table
                        RecruiterToCandidateUnlocked recruiterToCandidateUnlocked = new RecruiterToCandidateUnlocked();

                        recruiterToCandidateUnlocked.setRecruiterProfile(recruiterProfile);
                        recruiterToCandidateUnlocked.setCandidate(candidate);
                        recruiterCreditHistory.setRecruiterCreditsAddedBy(ServerConstants.SELF_UNLOCKED_CANDIDATE_CONTACT);
                        recruiterCreditHistory.setUnits(-1);

                        //saving/updating all the rows
                        recruiterCreditHistory.save();
                        recruiterToCandidateUnlocked.save();

                        //adding interaction
                        String objAUuid = candidate.getCandidateUUId();
                        String objBUuid = recruiterProfile.getRecruiterProfileUUId();
                        createInteractionForRecruiterUnlockCandidateContact(objAUuid, objBUuid);

                        unlockContactResponse.setStatus(UnlockContactResponse.STATUS_SUCCESS);
                        unlockContactResponse.setCandidateMobile(candidate.getCandidateMobile());
                        unlockContactResponse.setCandidateId(candidate.getCandidateId());

                        // Send sms to candidate that a recruiter has unlocked their profile
                        SmsUtil.sendCandidateUnlockSms(recruiterProfile.getCompany().getCompanyName(),
                                recruiterProfile.getRecruiterProfileName(), candidate.getCandidateMobile(), candidate.getCandidateFirstName());

                        return ok(toJson(unlockContactResponse));
                    } else {
                        unlockContactResponse.setStatus(UnlockContactResponse.STATUS_NO_CREDITS);
                        unlockContactResponse.setCandidateMobile(null);
                        unlockContactResponse.setCandidateId(null);
                        return ok(toJson(unlockContactResponse));
                    }
                } else{
                    unlockContactResponse.setStatus(UnlockContactResponse.STATUS_NO_CREDITS);
                    unlockContactResponse.setCandidateMobile(null);
                    unlockContactResponse.setCandidateId(null);
                    return ok(toJson(unlockContactResponse));
                }
            } else {
                unlockContactResponse.setStatus(UnlockContactResponse.STATUS_ALREADY_UNLOCKED);
                unlockContactResponse.setCandidateMobile(candidate.getCandidateMobile());
                unlockContactResponse.setCandidateId(candidate.getCandidateId());
                return ok(toJson(unlockContactResponse));
            }
        }
        Logger.info("Recruiter with mobile no: " + recruiterProfile.getRecruiterProfileMobile() + " does not have credits to unlock candidate");
        unlockContactResponse.setStatus(UnlockContactResponse.STATUS_FAILURE);
        unlockContactResponse.setCandidateMobile(null);
        unlockContactResponse.setCandidateId(null);
        return ok(toJson(unlockContactResponse));
    }

    public static AddCreditResponse requestCreditForRecruiter(AddCreditRequest addCreditRequest){
        AddCreditResponse addCreditResponse = new AddCreditResponse();
        if(session().get("recruiterId") != null){
            RecruiterProfile recruiterProfile = RecruiterProfile.find.where().eq("recruiterProfileId", session().get("recruiterId")).findUnique();
            if(recruiterProfile != null){
                Logger.info("Sending credit request Sms");
                SmsUtil.sendRequestCreditSms(recruiterProfile, addCreditRequest);

                Logger.info("Sending credit request Email");
                EmailUtil.sendRecruiterRequestCreditEmail(recruiterProfile, addCreditRequest);

                //adding interaction
                String result = InteractionConstants.INTERACTION_RESULT_RECRUITER_CREDIT_REQUEST + addCreditRequest.getNoOfContactCredits()
                        + " contact credits and " + addCreditRequest.getNoOfInterviewCredits() + " interview credits";
                createInteractionForRecruiterCreditRequest(recruiterProfile.getRecruiterProfileUUId(), result);
                Logger.info("Interaction Saved");

                addCreditResponse.setStatus(AddCreditResponse.STATUS_SUCCESS);
            } else {
                addCreditResponse.setStatus(AddCreditResponse.STATUS_FAILURE);
            }
        } else{
            addCreditResponse.setStatus(AddCreditResponse.STATUS_FAILURE);
        }
        return addCreditResponse;
    }

    public static ResetPasswordResponse findRecruiterAndSendOtp(String mobile) {
        ResetPasswordResponse resetPasswordResponse = new ResetPasswordResponse();
        RecruiterProfile existingRecruiter = isRecruiterExists(mobile);
        if(existingRecruiter != null){
            Logger.info("Recruiter Exists");
            RecruiterAuth existingAuth = RecruiterAuth.find.where().eq("recruiter_id", existingRecruiter.getRecruiterProfileId()).findUnique();
            if(existingAuth == null){
                resetPasswordResponse.setStatus(LoginResponse.STATUS_NO_USER);
                Logger.info("reset password not allowed as Auth don't exists");
            } else {
                int randomPIN = generateOtp();
                existingRecruiter.update();
                SmsUtil.sendResetPasswordOTPSmsToRecruiter(randomPIN, existingRecruiter.getRecruiterProfileMobile());

                String interactionResult = InteractionConstants.INTERACTION_RESULT_RECRUITER_TRIED_TO_RESET_PASSWORD;
                String objAUUID = "";
                objAUUID = existingRecruiter.getRecruiterProfileUUId();
                createInteractionForRecruiterTriedToResetPassword(objAUUID, interactionResult);

                resetPasswordResponse.setOtp(randomPIN);
                resetPasswordResponse.setStatus(LoginResponse.STATUS_SUCCESS);
            }
        } else{
            resetPasswordResponse.setStatus(LoginResponse.STATUS_NO_USER);
            Logger.info("reset password not allowed as password don't exists");
        }
        return resetPasswordResponse;
    }

    private static RecruiterProfile getAndSetRecruiterValues(AddRecruiterRequest addRecruiterRequest, RecruiterProfile newRecruiter, Company existingCompany){
        if(existingCompany != null){
            newRecruiter.setRecCompany(existingCompany);
        }
        if(addRecruiterRequest.getRecruiterName() != null){
            newRecruiter.setRecruiterProfileName(addRecruiterRequest.getRecruiterName());
        }
        if(addRecruiterRequest.getRecruiterLandline() != null){
            newRecruiter.setRecruiterProfileLandline(addRecruiterRequest.getRecruiterLandline());
        } else{
            newRecruiter.setRecruiterProfileLandline("0");
        }
        if(addRecruiterRequest.getRecruiterEmail() != null){
            newRecruiter.setRecruiterProfileEmail(addRecruiterRequest.getRecruiterEmail());
        }
        if(addRecruiterRequest.getRecruiterAlternateMobile() != null){
            newRecruiter.setRecruiterAlternateMobile(addRecruiterRequest.getRecruiterAlternateMobile());
        }
        if(addRecruiterRequest.getRecruiterLinkedinProfile() != null){
            newRecruiter.setRecruiterLinkedinProfile(addRecruiterRequest.getRecruiterLinkedinProfile());
        }
        return newRecruiter;
    }

    private static void createAndSaveDummyAuthFor(RecruiterProfile recruiterProfile) {
        // create dummy auth
        RecruiterAuth authToken = new RecruiterAuth(); // constructor instantiate createtimestamp, updatetimestamp, sessionid, authpasswordsalt
        String dummyPassword = String.valueOf(Util.randomLong());
        authToken.setRecruiterAuthStatus(ServerConstants.RECRUITER_STATUS_NOT_VERIFIED);
        authToken.setRecruiterId(recruiterProfile);
        authToken.setPasswordMd5(Util.md5(dummyPassword + authToken.getPasswordSalt()));
        authToken.save();

        SmsUtil.sendRecruiterWelcomeSmsForSupportSignup(recruiterProfile.getRecruiterProfileName(),
                recruiterProfile.getRecruiterProfileMobile(), dummyPassword);

        if (recruiterProfile.getRecruiterProfileEmail() != null) {
            EmailUtil.sendRecruiterWelcomeEmailForSupportSignup(recruiterProfile, dummyPassword);
        }

        Logger.info("Dummy auth created + otp triggered + auth saved for recruiter " +
                recruiterProfile.getRecruiterProfileMobile());
    }

    private static void triggerOtp(RecruiterProfile recruiterProfile, RecruiterSignUpResponse recruiterSignUpResponse) {
        int randomPIN = generateOtp();
        SmsUtil.sendRecruiterOTPSms(randomPIN, recruiterProfile.getRecruiterProfileMobile());

        recruiterSignUpResponse.setStatus(RecruiterSignUpResponse.getStatusSuccess());
        recruiterSignUpResponse.setOtp(randomPIN);
    }

    private static void addContactCredit(RecruiterProfile recruiterProfile, Integer creditCount){
        // new recruiter hence giving  free contact unlock credits
        RecruiterCreditHistory recruiterCreditHistory = new RecruiterCreditHistory();

        RecruiterCreditCategory recruiterCreditCategory = RecruiterCreditCategory.find.where().eq("recruiter_credit_category_id", ServerConstants.RECRUITER_CATEGORY_CONTACT_UNLOCK).findUnique();
        if(recruiterCreditCategory != null){
            recruiterCreditHistory.setRecruiterCreditCategory(recruiterCreditCategory);
        }
        recruiterCreditHistory.setRecruiterProfile(recruiterProfile);
        recruiterCreditHistory.setRecruiterCreditsAvailable(creditCount);
        recruiterCreditHistory.setRecruiterCreditsUsed(0);
        recruiterCreditHistory.setUnits(creditCount);

        if(session().get("sessionUsername") != null){
            recruiterCreditHistory.setRecruiterCreditsAddedBy("Support: " + session().get("sessionUsername"));
        } else{
            recruiterCreditHistory.setRecruiterCreditsAddedBy("Not specified");
        }
        recruiterCreditHistory.save();
    }

    public static InterviewResponse isInterviewRequired(JobPost jobPost) {
        InterviewResponse interviewResponse = new InterviewResponse();
        if (jobPost == null) {
            interviewResponse.setStatus(ServerConstants.ERROR);
            return interviewResponse;
        }
        int validCount = 0;
        if (jobPost.getRecruiterProfile() == null) {
            // don't show interview modal if no recruiter is set for a jobpost
            interviewResponse.setStatus(ServerConstants.INTERVIEW_NOT_REQUIRED);
            return interviewResponse;
        }
        Long recruiterId = jobPost.getRecruiterProfile().getRecruiterProfileId();
        RecruiterCreditHistory recruiterCreditHistory = RecruiterCreditHistory.find.where()
                .eq("recruiterProfile.recruiterProfileId", recruiterId)
                .eq("recruiterCreditCategory.recruiterCreditCategoryId", ServerConstants.RECRUITER_CATEGORY_INTERVIEW_UNLOCK)
                .orderBy().desc("createTimestamp").setMaxRows(1).findUnique();
        if (recruiterCreditHistory != null &&
                recruiterCreditHistory.getRecruiterCreditCategory().getRecruiterCreditCategoryId()
                        == ServerConstants.RECRUITER_CATEGORY_INTERVIEW_UNLOCK
                && recruiterCreditHistory.getRecruiterCreditsAvailable() > 0) {
            // When recruiter credit available then show Interview UI
            validCount++;
        }

        if (jobPost.getInterviewDetailsList() != null && jobPost.getInterviewDetailsList().size() > 0) {
            // When slot available then  show Interview UI
            validCount++;
        }

        if (validCount == 2) {
            interviewResponse.setStatus(ServerConstants.INTERVIEW_REQUIRED);
            return interviewResponse;
        }

        interviewResponse.setStatus(ServerConstants.INTERVIEW_NOT_REQUIRED);
        return interviewResponse;
    }

    public static void debitCredits(RecruiterProfile existingRecruiter, Integer creditType, int credits) {

        Integer creditsToBeDebited = (-1) * credits;
        List<RecruiterCreditHistory> recruiterPacks = RecruiterCreditHistoryDAO.getAllRecruiterPack(existingRecruiter,
                creditType);

        RecruiterCreditCategory recruiterCreditCategory = RecruiterCreditCategory.find.where()
                .eq("recruiter_credit_category_id", creditType)
                .findUnique();

        Integer remainingDebit = 0;
        String createdBy = "Not specified";

        if(session().get("sessionUsername") != null){
            createdBy = "Support: " + session().get("sessionUsername");
        }

        for(RecruiterCreditHistory history : recruiterPacks){
            if(history.getRecruiterCreditsAvailable() > 0){

                if(history.getRecruiterCreditsAvailable() < creditsToBeDebited){
                    RecruiterCreditHistory recruiterCreditHistory = new RecruiterCreditHistory();


                    if (recruiterCreditCategory != null){
                        //recruiterPayment.setRecruiterCreditCategory(recruiterCreditCategory);
                        recruiterCreditHistory.setRecruiterCreditCategory(recruiterCreditCategory);
                    }

                    //setting recruiter profile
                    //recruiterPayment.setRecruiterProfile(existingRecruiter);
                    recruiterCreditHistory.setRecruiterProfile(existingRecruiter);


                    remainingDebit = creditsToBeDebited - history.getRecruiterCreditsAvailable();
                    creditsToBeDebited = remainingDebit;

                    Integer available = history.getRecruiterCreditsAvailable();

                    recruiterCreditHistory.setRecruiterCreditsUsed(history.getRecruiterCreditsUsed() + history.getRecruiterCreditsAvailable());
                    recruiterCreditHistory.setRecruiterCreditsAvailable(0);
                    recruiterCreditHistory.setRecruiterCreditPackNo(history.getRecruiterCreditPackNo());

                    recruiterCreditHistory.setRecruiterCreditsAddedBy(createdBy);
                    recruiterCreditHistory.setCreditsAdded(0);
                    recruiterCreditHistory.setExpiryDate(history.getExpiryDate());
                    recruiterCreditHistory.setCreditIsExpired(false);
                    recruiterCreditHistory.setLatest(true);
                    recruiterCreditHistory.setUnits(-available);


                    recruiterCreditHistory.setRecruiterCreditsAddedBy(createdBy);

                    //saving the values
                    recruiterCreditHistory.save();

                    history.setLatest(false);
                    history.update();

                    //saving the values
                    recruiterCreditHistory.save();


                } else{


                    RecruiterCreditHistory recruiterCreditHistory = new RecruiterCreditHistory();

                    if (recruiterCreditCategory != null){
                        //recruiterPayment.setRecruiterCreditCategory(recruiterCreditCategory);
                        recruiterCreditHistory.setRecruiterCreditCategory(recruiterCreditCategory);
                    }

                    //setting recruiter profile
                    //recruiterPayment.setRecruiterProfile(existingRecruiter);
                    recruiterCreditHistory.setRecruiterProfile(existingRecruiter);

                    recruiterCreditHistory.setRecruiterCreditsUsed(history.getRecruiterCreditsUsed() + creditsToBeDebited);
                    recruiterCreditHistory.setRecruiterCreditsAvailable(history.getRecruiterCreditsAvailable() - creditsToBeDebited);
                    remainingDebit = 0;
                    recruiterCreditHistory.setRecruiterCreditPackNo(history.getRecruiterCreditPackNo());

                    recruiterCreditHistory.setRecruiterCreditsAddedBy(createdBy);
                    recruiterCreditHistory.setCreditsAdded(0);
                    recruiterCreditHistory.setUnits(-credits);

                    recruiterCreditHistory.setExpiryDate(history.getExpiryDate());
                    recruiterCreditHistory.setCreditIsExpired(false);
                    recruiterCreditHistory.setLatest(true);


                    //saving the values
                    recruiterCreditHistory.save();

                    history.setLatest(false);
                    history.update();

                    //saving the values
                    recruiterCreditHistory.save();

                }


                if(remainingDebit == 0){
                    break;
                }
            }
        }

    }

    public static void addCredits(RecruiterProfile existingRecruiter, Integer creditType, int totalCredits) {

        Integer availableCredits = 0;
        Integer usedCredits = 0;

        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MONTH, 1);
        Date expiryDate = cal.getTime();

        RecruiterCreditCategory recruiterCreditCategory = RecruiterCreditCategory.find.where()
                .eq("recruiter_credit_category_id", creditType)
                .findUnique();

        String createdBy = "Not specified";

        if(session().get("sessionUsername") != null){
            createdBy = "Support: " + session().get("sessionUsername");
        }

        RecruiterCreditHistory recruiterCreditHistory = new RecruiterCreditHistory();

        if (recruiterCreditCategory != null){
            //recruiterPayment.setRecruiterCreditCategory(recruiterCreditCategory);
            recruiterCreditHistory.setRecruiterCreditCategory(recruiterCreditCategory);
        }

        recruiterCreditHistory.setUnits(totalCredits);

        //setting recruiter profile
        recruiterCreditHistory.setRecruiterProfile(existingRecruiter);

        //credit
        RecruiterCreditHistory latestPack = RecruiterCreditHistoryDAO.getLatestRecruiterCreditPack(
                ServerConstants.RECRUITER_CATEGORY_CONTACT_UNLOCK);

        if (latestPack == null){
            recruiterCreditHistory.setRecruiterCreditPackNo(1);
        } else{

            RecruiterCreditHistory latest = RecruiterCreditHistoryDAO.getLatestRecruiterCreditPackDetails(existingRecruiter);
            recruiterCreditHistory.setRecruiterCreditPackNo(latest.getRecruiterCreditPackNo() + 1); //adding a new pack
        }
        recruiterCreditHistory.setRecruiterCreditsAvailable(availableCredits + totalCredits);
        recruiterCreditHistory.setRecruiterCreditsUsed(usedCredits);
        recruiterCreditHistory.setCreditsAdded(totalCredits);
        recruiterCreditHistory.setExpiryDate(expiryDate);
        recruiterCreditHistory.setCreditIsExpired(false);
        recruiterCreditHistory.setLatest(true);

        recruiterCreditHistory.setRecruiterCreditsAddedBy(createdBy);

        //saving the values
        recruiterCreditHistory.save();

    }
}