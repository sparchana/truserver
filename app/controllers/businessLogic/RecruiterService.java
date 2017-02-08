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
import api.http.httpResponse.Recruiter.recruiterAdmin.*;
import api.http.httpResponse.ResetPasswordResponse;
import api.http.httpResponse.interview.InterviewResponse;
import controllers.businessLogic.Recruiter.RecruiterAuthService;
import controllers.businessLogic.Recruiter.RecruiterInteractionService;
import controllers.businessLogic.Recruiter.RecruiterLeadService;
import dao.*;
import models.entity.Candidate;
import models.entity.Company;
import models.entity.JobPost;
import models.entity.OM.CandidateResume;
import models.entity.OM.InterviewDetails;
import models.entity.OM.JobPostWorkflow;
import models.entity.Recruiter.OM.RecruiterToCandidateUnlocked;
import models.entity.Recruiter.RecruiterAuth;
import models.entity.Recruiter.RecruiterLead;
import models.entity.Recruiter.RecruiterProfile;
import models.entity.Recruiter.Static.RecruiterCreditCategory;
import models.entity.Recruiter.Static.RecruiterStatus;
import models.entity.RecruiterCreditHistory;
import models.entity.Static.JobStatus;
import models.util.EmailUtil;
import models.util.SmsUtil;
import models.util.Util;
import org.joda.time.DateTime;
import org.joda.time.Days;
import play.Logger;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import static api.InteractionConstants.INTERACTION_CHANNEL_CANDIDATE_WEBSITE;
import static controllers.businessLogic.Recruiter.RecruiterInteractionService.*;
import static models.util.Util.generateOtp;
import static play.mvc.Controller.session;
import static play.mvc.Results.TODO;

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

            Company existingCompany = null;
            if(recruiterSignUpRequest.getCompanyCode() == null){
                //checking if company exists or not
                existingCompany = Company.find.where().eq("companyId", recruiterSignUpRequest.getRecruiterCompany()).findUnique();
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
            } else{
                existingCompany = Company.find.where().eq("CompanyCode", recruiterSignUpRequest.getCompanyCode()).findUnique();
                if(existingCompany != null){
                    newRecruiter.setRecruiterAccessLevel(ServerConstants.RECRUITER_ACCESS_LEVEL_PRIVATE);
                } else{
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

                String createdBy = "Not specified";

                if(session().get("sessionUsername") != null){
                    createdBy = "Support: " + session().get("sessionUsername");
                }

                newRecruiter.save();

                if(recruiterSignUpRequest.getRecruiterType() != null &&
                        recruiterSignUpRequest.getRecruiterType() > ServerConstants.RECRUITER_ACCESS_LEVEL_OPEN) {

                    newRecruiter.setRecruiterAccessLevel(recruiterSignUpRequest.getRecruiterType());
                    newRecruiter.update();
                    
                    String startDateString = "2020-12-31";
                    DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
                    Date startDate = null;
                    try {
                        startDate = df.parse(startDateString);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    addCredits(newRecruiter, ServerConstants.RECRUITER_CATEGORY_INTERVIEW_UNLOCK, ServerConstants.RECRUITER_DEFAULT_INTERVIEW_CREDITS, createdBy, startDate);
                } else{
                    //assigning free contact unlock credits for the recruiter
                    addCredits(newRecruiter, ServerConstants.RECRUITER_CATEGORY_CONTACT_UNLOCK, ServerConstants.RECRUITER_FREE_CONTACT_CREDITS, createdBy, recruiterSignUpRequest.getExpiryDate());
                }

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
                        (recruiterSignUpRequest.getInterviewCredits() != null && recruiterSignUpRequest.getInterviewCredits() > 0) ||
                        (recruiterSignUpRequest.getCtaCredits() != null && recruiterSignUpRequest.getCtaCredits() > 0))
                {
                    //TODO CTA Modify Messaging for CTA credits
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

        String createdBy = "Not specified";

        if(session().get("sessionUsername") != null){
            createdBy = "Support: " + session().get("sessionUsername");
        }

        //setting values for candidate contact unlock credits
        if(addRecruiterRequest.getContactCredits() != null && addRecruiterRequest.getContactCredits() != 0){
            if(addRecruiterRequest.getContactCredits() > 0){

                addCredits(existingRecruiter, ServerConstants.RECRUITER_CATEGORY_CONTACT_UNLOCK, addRecruiterRequest.getContactCredits(), createdBy, addRecruiterRequest.getExpiryDate());
            } else{

                //debit
                debitCredits(existingRecruiter, ServerConstants.RECRUITER_CATEGORY_CONTACT_UNLOCK, addRecruiterRequest.getContactCredits(), createdBy);
            }
        }

        //setting values for interview unlock credits
        if(addRecruiterRequest.getInterviewCredits() != null && addRecruiterRequest.getInterviewCredits() != 0){

            if(addRecruiterRequest.getInterviewCredits() > 0){

                //credit
                addCredits(existingRecruiter, ServerConstants.RECRUITER_CATEGORY_INTERVIEW_UNLOCK, addRecruiterRequest.getInterviewCredits(), createdBy, addRecruiterRequest.getExpiryDate());

            } else{
                //debit
                debitCredits(existingRecruiter, ServerConstants.RECRUITER_CATEGORY_INTERVIEW_UNLOCK, addRecruiterRequest.getInterviewCredits(), createdBy);

            }
        }

        //setting values for cta credits
        if(addRecruiterRequest.getCtaCredits() != null && addRecruiterRequest.getCtaCredits() != 0){
            if(addRecruiterRequest.getCtaCredits() > 0){
                //credit
                addCredits(existingRecruiter, ServerConstants.RECRUITER_CATEGORY_CTA_CREDIT, addRecruiterRequest.getCtaCredits(), createdBy, addRecruiterRequest.getExpiryDate());
            } else{
                //debit
                debitCredits(existingRecruiter, ServerConstants.RECRUITER_CATEGORY_CTA_CREDIT, addRecruiterRequest.getCtaCredits(), createdBy);
            }
        }

    }

    public static UnlockContactResponse unlockCandidate(RecruiterProfile recruiterProfile, Long candidateId) {
        UnlockContactResponse unlockContactResponse = new UnlockContactResponse();
        Candidate candidate = Candidate.find.where().eq("CandidateId", candidateId).findUnique();
        if(candidate != null){
            RecruiterToCandidateUnlocked existingUnlockedCandidate = RecruiterToCandidateUnlocked.find.where()
                    .eq("recruiterProfileId", recruiterProfile.getRecruiterProfileId())
                    .eq("CandidateId", candidate.getCandidateId())
                    .findUnique();

            String createdBy = "Not specified";

            if(session().get("sessionUsername") != null){
                createdBy = "Support: " + session().get("sessionUsername");
            }

            if(existingUnlockedCandidate == null){
                // this candidate has not been unlocked by the recruiter, hence unlock it
                Logger.info("Recruiter with mobile no: " + recruiterProfile.getRecruiterProfileMobile() + " is unlocking candidate with mobile: " + candidate.getCandidateMobile());

                Boolean unlockCandidate = false;
                Boolean isPrivateRecruiter = false;
                if(recruiterProfile.getRecruiterAccessLevel() >= ServerConstants.RECRUITER_ACCESS_LEVEL_PRIVATE){
                    isPrivateRecruiter = true;
                }
                if(recruiterProfile.getContactCreditCount() > 0 || recruiterProfile.getRecruiterAccessLevel() >= ServerConstants.RECRUITER_ACCESS_LEVEL_PRIVATE){
                    unlockCandidate = true;
                }

                if(unlockCandidate){

                    if(!isPrivateRecruiter){
                        //recruiter has contact credits
                        debitCredits(recruiterProfile, ServerConstants.RECRUITER_CATEGORY_CONTACT_UNLOCK, -1, createdBy);

                        RecruiterToCandidateUnlocked recruiterToCandidateUnlocked = new RecruiterToCandidateUnlocked();

                        recruiterToCandidateUnlocked.setRecruiterProfile(recruiterProfile);
                        recruiterToCandidateUnlocked.setCandidate(candidate);

                        //saving unlocked candidate
                        recruiterToCandidateUnlocked.save();

                        //adding interaction
                        String objAUuid = candidate.getCandidateUUId();
                        String objBUuid = recruiterProfile.getRecruiterProfileUUId();
                        createInteractionForRecruiterUnlockCandidateContact(objAUuid, objBUuid);

                        // Send sms to candidate that a recruiter has unlocked their profile
                        SmsUtil.sendCandidateUnlockSms(recruiterProfile.getCompany().getCompanyName(),
                                recruiterProfile.getRecruiterProfileName(), candidate.getCandidateMobile(), candidate.getCandidateFirstName());
                    }
                    unlockContactResponse.setStatus(UnlockContactResponse.STATUS_SUCCESS);
                    unlockContactResponse.setCandidateMobile(candidate.getCandidateMobile());
                    unlockContactResponse.setCandidateId(candidate.getCandidateId());

                    //CandidateResume resume = CandidateResume.find.where().eq("CandidateId", candidate.getCandidateId()).findUnique();
                    CandidateResumeService candidateResumeService= new CandidateResumeService();
                    CandidateResume resume = (CandidateResume) candidateResumeService.fetchLatestResumeForCandidate(Long.toString(candidate.getCandidateId())).getEntity();
                    if(resume != null){
                        unlockContactResponse.setResumeLink(resume.getFilePath());
                    }

                    return unlockContactResponse;

                } else{

                    //recruiter doesn't have contact credits
                    unlockContactResponse.setStatus(UnlockContactResponse.STATUS_NO_CREDITS);
                    unlockContactResponse.setCandidateMobile(null);
                    unlockContactResponse.setCandidateId(null);
                    return unlockContactResponse;

                }

            } else {
                unlockContactResponse.setStatus(UnlockContactResponse.STATUS_ALREADY_UNLOCKED);
                unlockContactResponse.setCandidateMobile(candidate.getCandidateMobile());
                unlockContactResponse.setCandidateId(candidate.getCandidateId());
                CandidateResume resume = CandidateResume.find.where().eq("CandidateId", candidate.getCandidateId()).findUnique();
                if(resume != null){
                    unlockContactResponse.setResumeLink(resume.getFilePath());
                }

                return unlockContactResponse;
            }
        }
        Logger.info("Recruiter with mobile no: " + recruiterProfile.getRecruiterProfileMobile() + " does not have credits to unlock candidate");
        unlockContactResponse.setStatus(UnlockContactResponse.STATUS_FAILURE);
        unlockContactResponse.setCandidateMobile(null);
        unlockContactResponse.setCandidateId(null);
        return unlockContactResponse;
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

            //if a recruiter is switching to a new company, close all the previous jobs associated with the recruiter
            if(newRecruiter.getCompany() != null && newRecruiter.getRecruiterProfileId() != null){
                if(!Objects.equals(newRecruiter.getCompany().getCompanyId(), existingCompany.getCompanyId())){

                    //TODO: RE-association of credits on company change
                    JobStatus statusClosed = JobStatus.find.where().eq("JobStatusId", ServerConstants.JOB_STATUS_CLOSED).findUnique();
                    List<JobPost> recruiterJobPostList = JobPost.find.where().eq("JobRecruiterId", newRecruiter.getRecruiterProfileId()).findList();
                    for(JobPost jobPost : recruiterJobPostList){
                        jobPost.setJobPostStatus(statusClosed);
                        jobPost.update();
                    }

                    //send sms to recruiter to notify company change
                    SmsUtil.sendCompanyChangeSmsToRecruiter(newRecruiter, newRecruiter.getCompany().getCompanyName(),
                            existingCompany.getCompanyName());
                }
            }

            //assigning new company
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

    public static InterviewResponse isInterviewRequired(JobPost jobPost) {
        InterviewResponse interviewResponse = new InterviewResponse();
        if (jobPost == null) {
            interviewResponse.setStatus(ServerConstants.ERROR);
            interviewResponse.setStatusTitle("ERROR");
            return interviewResponse;
        }
        int validCount = 0;
        if (jobPost.getRecruiterProfile() == null) {
            // don't show interview modal if no recruiter is set for a jobpost
            interviewResponse.setStatus(ServerConstants.INTERVIEW_NOT_REQUIRED);
            interviewResponse.setStatusTitle("INTERVIEW_NOT_REQUIRED");
            return interviewResponse;
        }

        if (jobPost.getRecruiterProfile().getInterviewCreditCount() == 0
                && jobPost.getRecruiterProfile().getContactCreditCount() == 0) {

            Calendar newCalendar = Calendar.getInstance();

            // 1-> sunday
            // 2-> Monday
            int todayDate = newCalendar.get(Calendar.DAY_OF_WEEK);
            int weekDaysDeduct;
            if(todayDate > 1){
                weekDaysDeduct = todayDate - 2;
            } else{
                weekDaysDeduct = 6;
            }

            //checking weekly job application limit
            if(JobPostDAO.getThisWeeksApplication(jobPost, weekDaysDeduct).size()
                    >= ServerConstants.FREE_JOB_APPLICATION_DEFAULT_LIMIT_IN_A_WEEK){

                Logger.info("Interview closed for this week");
                // interview closed
                interviewResponse.setStatus(ServerConstants.INTERVIEW_CLOSED);
                interviewResponse.setStatusTitle("INTERVIEW_CLOSED");
                return interviewResponse;
            }

        }

        List<InterviewDetails> interviewDetailsList = InterviewDetails.find.where().eq("JobPostId", jobPost.getJobPostId()).findList();

        if (jobPost.getRecruiterProfile().getInterviewCreditCount() > 0) {
            // When recruiter credit available then show Interview UI
            validCount++;
        }

        if (interviewDetailsList.size() > 0) {
            // When slot available then  show Interview UI
            validCount++;
        }

        if (validCount == 2) {
            interviewResponse.setStatus(ServerConstants.INTERVIEW_REQUIRED);
            interviewResponse.setStatusTitle("INTERVIEW_REQUIRED");
            return interviewResponse;
        }

        interviewResponse.setStatus(ServerConstants.INTERVIEW_NOT_REQUIRED);
        interviewResponse.setStatusTitle("INTERVIEW_NOT_REQUIRED");
        return interviewResponse;
    }

    public static void debitCredits(RecruiterProfile existingRecruiter, Integer creditType, int credits, String createdBy) {

        Integer creditsToBeDebited = (-1) * credits;
        List<RecruiterCreditHistory> recruiterPacks = RecruiterCreditHistoryDAO.getAllActiveRecruiterPacks(existingRecruiter,
                creditType);

        RecruiterCreditCategory recruiterCreditCategory = RecruiterCreditCategory.find.where()
                .eq("recruiter_credit_category_id", creditType)
                .findUnique();

        Integer remainingDebit;

        Boolean toBreak;

        for(RecruiterCreditHistory history : recruiterPacks){
            if(history.getRecruiterCreditsAvailable() != null && history.getRecruiterCreditsUsed() != null){

                //if both available credits and used credits atre not null
                if(history.getRecruiterCreditsAvailable() > 0){
                    RecruiterCreditHistory recruiterCreditHistory = new RecruiterCreditHistory();
                    if (recruiterCreditCategory != null){
                        recruiterCreditHistory.setRecruiterCreditCategory(recruiterCreditCategory);
                    } else{
                        Logger.info("recruiter category static table empty");
                        break;
                    }

                    if(history.getRecruiterCreditsAvailable() < creditsToBeDebited){
                        recruiterCreditHistory.setRecruiterCreditsUsed(history.getRecruiterCreditsUsed() + history.getRecruiterCreditsAvailable());
                        recruiterCreditHistory.setUnits(-history.getRecruiterCreditsAvailable());
                        remainingDebit = creditsToBeDebited - history.getRecruiterCreditsAvailable();
                        recruiterCreditHistory.setRecruiterCreditsAvailable(0);
                        creditsToBeDebited = remainingDebit;
                        toBreak = false;

                    } else {
                        toBreak = true;
                        recruiterCreditHistory.setUnits(-creditsToBeDebited);
                        recruiterCreditHistory.setRecruiterCreditsUsed(history.getRecruiterCreditsUsed() + creditsToBeDebited);
                        recruiterCreditHistory.setRecruiterCreditsAvailable(history.getRecruiterCreditsAvailable() - creditsToBeDebited);
                    }

                    //setting recruiter profile
                    recruiterCreditHistory.setRecruiterProfile(existingRecruiter);

                    recruiterCreditHistory.setRecruiterCreditPackNo(history.getRecruiterCreditPackNo());

                    recruiterCreditHistory.setRecruiterCreditsAddedBy(createdBy);
                    recruiterCreditHistory.setExpiryDate(history.getExpiryDate());
                    recruiterCreditHistory.setCreditIsExpired(false);
                    recruiterCreditHistory.setLatest(true);

                    history.setLatest(false);
                    history.update();

                    //saving the values
                    recruiterCreditHistory.save();

                    if(toBreak){
                        break;
                    }
                }

            }
        }
    }

    public static void addCredits(RecruiterProfile existingRecruiter, Integer creditType, Integer totalCredits, String createdBy, Date customExpiryDate) {

        Integer availableCredits = 0;
        Integer usedCredits = 0;

        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MONTH, 1);
        Date expiryDate = cal.getTime();

        if(customExpiryDate != null){
            expiryDate = customExpiryDate;
        }

        RecruiterCreditCategory recruiterCreditCategory = RecruiterCreditCategory.find.where()
                .eq("recruiter_credit_category_id", creditType)
                .findUnique();

        RecruiterCreditHistory recruiterCreditHistory = new RecruiterCreditHistory();

        if (recruiterCreditCategory != null){
            recruiterCreditHistory.setRecruiterCreditCategory(recruiterCreditCategory);
            recruiterCreditHistory.setUnits(totalCredits);

            //setting recruiter profile
            recruiterCreditHistory.setRecruiterProfile(existingRecruiter);

            //credit
            RecruiterCreditHistory latestPack = RecruiterCreditHistoryDAO.getLastAddedRecruiterCreditPack(
                    existingRecruiter);

            if (latestPack == null){
                recruiterCreditHistory.setRecruiterCreditPackNo(1);
            } else{
                recruiterCreditHistory.setRecruiterCreditPackNo(latestPack.getRecruiterCreditPackNo() + 1); //adding a new pack
            }
            recruiterCreditHistory.setRecruiterCreditsAvailable(availableCredits + totalCredits);
            recruiterCreditHistory.setRecruiterCreditsUsed(usedCredits);
            recruiterCreditHistory.setExpiryDate(expiryDate);
            recruiterCreditHistory.setCreditIsExpired(false);
            recruiterCreditHistory.setLatest(true);

            recruiterCreditHistory.setRecruiterCreditsAddedBy(createdBy);

            //saving the values
            recruiterCreditHistory.save();

        } else{
            Logger.info("recruiter category static table empty");
        }
    }

    public static AddRecruiterResponse updateExistingRecruiterPack(RecruiterProfile recruiterProfile,
                                                                   Integer packId,
                                                                   Integer credits,
                                                                   String createdBy,
                                                                   Date customExpiryDate)
    {

        AddRecruiterResponse addRecruiterResponse = new AddRecruiterResponse();

        RecruiterCreditHistory history = RecruiterCreditHistoryDAO.getCreditPackByPackNo(recruiterProfile, packId);

        if(history != null){

            history.setLatest(false);
            history.update();

            RecruiterCreditHistory newHistory = new RecruiterCreditHistory();

            newHistory.setRecruiterCreditsAvailable(history.getRecruiterCreditsAvailable() + (credits));
            if(credits > 0){

                //credit
                newHistory.setRecruiterCreditsUsed(history.getRecruiterCreditsUsed());
            } else{

                //debit
                newHistory.setRecruiterCreditsUsed(history.getRecruiterCreditsUsed() + ((-1) * credits));
            }

            newHistory.setRecruiterCreditPackNo(history.getRecruiterCreditPackNo());
            newHistory.setRecruiterProfile(history.getRecruiterProfile());
            newHistory.setCreditIsExpired(history.getCreditIsExpired());
            newHistory.setRecruiterCreditsAddedBy(createdBy);
            newHistory.setUnits(credits);
            newHistory.setRecruiterCreditCategory(history.getRecruiterCreditCategory());
            newHistory.setLatest(true);
            if(customExpiryDate != null){
                newHistory.setExpiryDate(customExpiryDate);
            } else{
                newHistory.setExpiryDate(history.getExpiryDate());
            }

            Logger.info("Creating a new row for the updated pack by debiting/crediting by " + credits + " for recruiter: " +
                recruiterProfile.getRecruiterProfileName() + ", ID: " + recruiterProfile.getRecruiterProfileId());
            newHistory.save();
            addRecruiterResponse.setStatus(AddRecruiterResponse.STATUS_SUCCESS);
        } else{

            //No pack found
            addRecruiterResponse.setStatus(AddRecruiterResponse.STATUS_FAILURE);
        }

        return addRecruiterResponse;
    }

    public static AddRecruiterResponse expireCreditPack(AddRecruiterRequest addRecruiterRequest) {
        AddRecruiterResponse addRecruiterResponse = new AddRecruiterResponse();

        Calendar cal = Calendar.getInstance();
        Date expiryDate = cal.getTime();

        RecruiterProfile recruiterProfile = RecruiterProfile.find.where()
                .eq("RecruiterProfileMobile", FormValidator.convertToIndianMobileFormat(addRecruiterRequest.getRecruiterMobile()))
                .findUnique();

        if(recruiterProfile != null){

            RecruiterCreditHistory history = RecruiterCreditHistoryDAO.getCreditPackByPackNo(recruiterProfile, addRecruiterRequest.getPackId());

            if(history != null) {
                RecruiterCreditHistory newHistory = new RecruiterCreditHistory();
                copyCreditObject(newHistory, history);

                newHistory.setCreditIsExpired(true);
                newHistory.setLatest(true);
                newHistory.setUnits(0);
                newHistory.setExpiryDate(expiryDate);

                //updating the old pack's isLatest value
                history.setLatest(false);
                history.update();

                newHistory.save();

                addRecruiterResponse.setStatus(AddRecruiterResponse.STATUS_SUCCESS);
            } else{
                addRecruiterResponse.setStatus(AddRecruiterResponse.STATUS_FAILURE);
            }
        } else{
            addRecruiterResponse.setStatus(AddRecruiterResponse.STATUS_FAILURE);
        }
        return addRecruiterResponse;
    }

    public static RecruiterCreditHistory copyCreditObject(RecruiterCreditHistory newHistory, RecruiterCreditHistory oldHistory) {

        String createdBy = "Not specified";

        if(session().get("sessionUsername") != null){
            createdBy = "Support: " + session().get("sessionUsername");
        }

        newHistory.setRecruiterCreditsAddedBy(createdBy);
        newHistory.setCreditIsExpired(oldHistory.getCreditIsExpired());
        newHistory.setLatest(oldHistory.getLatest());
        newHistory.setExpiryDate(oldHistory.getExpiryDate());
        newHistory.setRecruiterProfile(oldHistory.getRecruiterProfile());
        newHistory.setRecruiterCreditPackNo(oldHistory.getRecruiterCreditPackNo());
        newHistory.setRecruiterCreditsAvailable(oldHistory.getRecruiterCreditsAvailable());
        newHistory.setRecruiterCreditsUsed(oldHistory.getRecruiterCreditsUsed());
        newHistory.setRecruiterCreditCategory(oldHistory.getRecruiterCreditCategory());
        newHistory.setUnits(oldHistory.getUnits());

        return newHistory;
    }

    public RecruiterSummaryResponse getRecruiterSummary(Long companyId, Long callerRecruiterId, String from, String to) {

        if(callerRecruiterId == null) {
            return new RecruiterSummaryResponse();
        }

        RecruiterSummaryResponse response = new RecruiterSummaryResponse();

        final SimpleDateFormat sdf = new SimpleDateFormat(ServerConstants.SDF_FORMAT_YYYYMMDD);
        Date startDate = null;
        Date endDate = null;
        if(from != null && to != null
                && !from.equalsIgnoreCase("null") && !to.equalsIgnoreCase("null")) {
            try {
                startDate = sdf.parse(from);
                endDate = sdf.parse(to);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        if(companyId == null) {
            RecruiterProfile recruiterProfile = RecruiterProfile.find.where().eq("recruiterProfileId", callerRecruiterId).findUnique();
            if(recruiterProfile == null || recruiterProfile.getCompany() == null) {
                return new RecruiterSummaryResponse();
            }
            companyId = recruiterProfile.getCompany().getCompanyId();
            response.setCompanyName(recruiterProfile.getCompany().getCompanyName());
        }

        List<RecruiterSummary> recruiterSummaryList = new ArrayList<>();
        Map<?, RecruiterProfile> recruiterProfileMap = RecruiterDAO.findMapByCompanyId(companyId, ServerConstants.RECRUITER_ACCESS_LEVEL_PRIVATE);

        for(Map.Entry entry: recruiterProfileMap.entrySet()) {

            RecruiterSummary recruiterSummary = new RecruiterSummary();
            RecruiterProfile recruiterProfile = (RecruiterProfile) entry.getValue();

            List<Long> jobPostIdList = new ArrayList<>();
            List<JobPost> jobPostList = new ArrayList<>();
            for(JobPost jobPost: recruiterProfile.getJobPosts()) {
                if(jobPost.getJobPostAccessLevel() != ServerConstants.JOB_POST_TYPE_PRIVATE) continue;
                if(jobPost.getJobPostStatus().getJobStatusId() != ServerConstants.JOB_STATUS_ACTIVE) continue;

                if(startDate != null && endDate != null) {
                    if( jobPost.getJobPostCreateTimestamp().after(endDate) )
                    {
                        continue;
                    }
                }
                jobPostList.add(jobPost);
                jobPostIdList.add(jobPost.getJobPostId());
            }

            recruiterSummary.setRecruiterId(recruiterProfile.getRecruiterProfileId());
            recruiterSummary.setRecruiterName(recruiterProfile.getRecruiterProfileName());
            recruiterSummary.setRecruiterMobile(recruiterProfile.getRecruiterProfileMobile() +
                    ((recruiterProfile.getRecruiterAlternateMobile() == null) ? "": "/"+recruiterProfile.getRecruiterAlternateMobile()));

            recruiterSummary.setNoOfJobPosted(jobPostList.size());
            recruiterSummary.setTotalCandidatesApplied(computeTotalApplicant(jobPostIdList, startDate, endDate));
            recruiterSummary.setTotalInterviewConducted(computeTotalInterviewConducted(jobPostIdList,  startDate, endDate));
            recruiterSummary.setTotalSelected(computeTotalSelected(jobPostIdList, startDate, endDate));

            recruiterSummary.setPercentageFulfillment(
                    formatPercentageFulfilled(computePercentageFulfilled(jobPostList, recruiterSummary.getTotalSelected()))
                                     );

            recruiterSummaryList.add(recruiterSummary);
        }

        response.setRecruiterSummaryList(recruiterSummaryList);
        return response;
    }

    private PercentageBundle computePercentageFulfilled(List<JobPost> jobPostList, Integer totalSelected) {
        int totalVacancy = 0;
        for(JobPost jobPost: jobPostList) {
            if(jobPost.getJobPostVacancies() == null) continue;

            totalVacancy += jobPost.getJobPostVacancies();
        }
        if(totalVacancy == 0) {
            return null;
        }
        float percentage = Float.parseFloat( new DecimalFormat("###.##").format( ((float) totalSelected*100/totalVacancy)));
        return new PercentageBundle(totalSelected, totalVacancy, percentage);
    }

    private String formatPercentageFulfilled(PercentageBundle percentageBundle) {
        if(percentageBundle == null)  return "NA";
        return percentageBundle.getPercentage()+" % ("+percentageBundle.getSelected() + " out of "+percentageBundle.getTotal() + ")";

    }

    private int computeTotalSelected(List<Long> jobPostIdList) {
        List<Integer> statusList = new ArrayList<>();

        statusList.add(ServerConstants.JWF_STATUS_CANDIDATE_FEEDBACK_STATUS_COMPLETE_SELECTED);

        return JobPostWorkFlowDAO.getRecords(jobPostIdList, statusList).size();
    }

    private int computeTotalSelected(List<Long> jobPostIdList, Date fromDate, Date toDate) {
        if(fromDate == null || toDate == null) {
            return computeTotalSelected(jobPostIdList);
        }

        List<Integer> statusList = new ArrayList<>();

        statusList.add(ServerConstants.JWF_STATUS_CANDIDATE_FEEDBACK_STATUS_COMPLETE_SELECTED);

        return JobPostWorkFlowDAO.getRecords(jobPostIdList, statusList, fromDate, toDate).size();
    }

    private int computeTotalApplicant(List<Long> jobPostIdList, Date fromDate, Date toDate ) {
        if(fromDate == null || toDate == null ){
            return computeTotalApplicant(jobPostIdList);
        }

        List<Integer> statusList = new ArrayList<>();
        statusList.add(ServerConstants.JWF_STATUS_SELECTED);

        return JobPostWorkFlowDAO.getRecords(jobPostIdList, statusList, fromDate, toDate).size();
    }

    private int computeTotalApplicant(List<Long> jobPostIdList) {
        List<Integer> statusList = new ArrayList<>();
        statusList.add(ServerConstants.JWF_STATUS_SELECTED);

        return JobPostWorkFlowDAO.getRecords(jobPostIdList, statusList).size();
    }

    private int computeTotalInterviewConducted(List<Long> jobPostIdList) {
        List<Integer> statusList = new ArrayList<>();

        statusList.add(ServerConstants.JWF_STATUS_CANDIDATE_FEEDBACK_STATUS_COMPLETE_SELECTED);
        statusList.add(ServerConstants.JWF_STATUS_CANDIDATE_FEEDBACK_STATUS_COMPLETE_REJECTED);
        statusList.add(ServerConstants.JWF_STATUS_CANDIDATE_FEEDBACK_STATUS_NOT_QUALIFIED);
        statusList.add(ServerConstants.JWF_STATUS_CANDIDATE_FEEDBACK_STATUS_NO_SHOW);

        return JobPostWorkFlowDAO.getRecords(jobPostIdList, statusList).size();
    }

    private int computeTotalInterviewConducted(List<Long> jobPostIdList, Date fromDate, Date toDate) {
        if(fromDate == null || toDate == null) {
            return computeTotalInterviewConducted(jobPostIdList);
        }

        List<Integer> statusList = new ArrayList<>();

        statusList.add(ServerConstants.JWF_STATUS_CANDIDATE_FEEDBACK_STATUS_COMPLETE_SELECTED);
        statusList.add(ServerConstants.JWF_STATUS_CANDIDATE_FEEDBACK_STATUS_COMPLETE_REJECTED);
        statusList.add(ServerConstants.JWF_STATUS_CANDIDATE_FEEDBACK_STATUS_NOT_QUALIFIED);
        statusList.add(ServerConstants.JWF_STATUS_CANDIDATE_FEEDBACK_STATUS_NO_SHOW);

        return JobPostWorkFlowDAO.getRecords(jobPostIdList, statusList, fromDate, toDate).size();
    }

    public JobPostSummaryResponse getAllJobPostPerRecruiterSummary(Long targetRecruiterId, Long callerRecruiterId) {
        if(targetRecruiterId == null || callerRecruiterId == null) return new JobPostSummaryResponse();

        JobPostSummaryResponse response = new JobPostSummaryResponse();

        List<JobPostSummary> jobPostSummaryList = new ArrayList<>();
        Map<?, JobPost> jobPostMap = JobPostDAO.findMapByRecruiterId(targetRecruiterId, ServerConstants.JOB_POST_TYPE_PRIVATE);

        SimpleDateFormat sdf = new SimpleDateFormat(ServerConstants.SDF_FORMAT_DDMMYYYY);

        for(Map.Entry entry: jobPostMap.entrySet()) {

            JobPostSummary jobPostSummary = new JobPostSummary();
            JobPost jobPost = (JobPost) entry.getValue();

            if(jobPost == null) continue;
            if(response.getRecruiterName().isEmpty()){
                response.setRecruiterName(jobPost.getRecruiterProfile().getRecruiterProfileName());
            }
            String jobTitle = jobPost.getJobPostTitle() + " ("+jobPost.getJobPostStatus().getJobStatusName()+")";
            // forming individual responses again each jobpost
            jobPostSummary.setJobPostId(jobPost.getJobPostId());
            jobPostSummary.setJobTitle(jobTitle);
            jobPostSummary.setJobPostedOn(sdf.format(jobPost.getJobPostCreateTimestamp()));

            // not using the jobpost.getapplication since support matching doesn't goes here
            // need to clarify if support can interact with private flow or not
            // for now this uses the jobpost workflow to figure out these info
            jobPostSummary.setTotalApplicants(computeTotalApplicant(new ArrayList<>(Arrays.asList(jobPost.getJobPostId()))));
            jobPostSummary.setTotalInterviewConducted(computeTotalInterviewConducted(new ArrayList<>(Arrays.asList(jobPost.getJobPostId()))));
            jobPostSummary.setPercentageFulfillment(
                    formatPercentageFulfilled(computePercentageFulfilled(new ArrayList<>(Arrays.asList(jobPost)),
                    computeTotalSelected(new ArrayList<>(Arrays.asList(jobPost.getJobPostId()))))));

            try {
                jobPostSummary.setCycleTime(formatCycleTime(computeAvgCycleTime(jobPost.getJobPostId(), jobPost.getJobPostCreateTimestamp())));
            } catch (ParseException e) {
                e.printStackTrace();
                Logger.error("unable to parse date for date diff in computeAvgCycleTime");
            }
            // TODO move this to map and then use it here
            jobPostSummary.setTotalSmsSent(SmsReportDAO.getTotalSMSByRecruiterNJobPost(targetRecruiterId, jobPost.getJobPostId()));

            // adding it to the list
            jobPostSummaryList.add(jobPostSummary);
        }

        response.setJobPostSummaryList(jobPostSummaryList);
        return response;
    }

    private String formatCycleTime(float i) {
        if(i < 0) {
            return "NA";
        }
        return i + " Day(s)";
    }

    /**
     * @param jobPostId
     * @param jobPostedOn
     * @return no of days between jobPosted on and first candidate got selected
     * @throws ParseException
     */
    private float computeAvgCycleTime(Long jobPostId, Timestamp jobPostedOn) throws ParseException {
        // first selection data - job posted date

        List<JobPostWorkflow> jobPostWorkflowList = JobPostWorkFlowDAO.findAllJobSelection(jobPostId);

        if(jobPostWorkflowList.size() == 0) {
            return -1;
        }
        int totalSize = jobPostWorkflowList.size();
        float totalDays = 0;

        for(JobPostWorkflow jobPostWorkflow : jobPostWorkflowList) {
            DateTime dt1 = new DateTime(jobPostedOn.getTime());
            DateTime dt2 = new DateTime(jobPostWorkflow.getCreationTimestamp().getTime());

            totalDays += Days.daysBetween(dt1, dt2).getDays();
        }


        return totalDays/totalSize;
    }

    public static String modifySMS(String smsMessage, Candidate candidate, JobPost jobPost) {
        String applyInShortURL = Util.generateApplyInShortUrl(candidate, jobPost);
        StringBuilder modifiedSMS = new StringBuilder();
        if(applyInShortURL != null) {

            modifiedSMS.append("Job Offer! ");
            modifiedSMS.append(applyInShortURL);
            modifiedSMS.append("\n\n");
        }

        modifiedSMS.append(smsMessage);

        if(applyInShortURL != null) {
            modifiedSMS.append("\n\n");

            modifiedSMS.append("Apply Now: ");
            modifiedSMS.append(applyInShortURL);
        }

        return modifiedSMS.toString();
    }

    public static InterviewResponse isCTAAllowed(JobPost jobPost) {
        InterviewResponse interviewResponse = new InterviewResponse();
        if (jobPost == null || jobPost.getRecruiterProfile() == null) {
            // don't show CTA if no recruiter is set for a jobpost
            interviewResponse.setStatus(ServerConstants.ERROR);
            return interviewResponse;
        }
        if (jobPost.getRecruiterProfile().getCtaCreditCount() > 0) {
            interviewResponse.setStatus(ServerConstants.CALL_TO_APPLY);
            return interviewResponse;
        }
        interviewResponse.setStatus(ServerConstants.ERROR);
        return interviewResponse;
    }

}