package controllers.businessLogic;

import api.InteractionConstants;
import api.ServerConstants;
import api.http.CandidateKnownLanguage;
import api.http.CandidateSkills;
import api.http.FormValidator;
import api.http.httpRequest.*;
import api.http.httpRequest.Workflow.preScreenEdit.*;
import api.http.httpResponse.CandidateSignUpResponse;
import api.http.httpResponse.LoginResponse;
import api.http.httpResponse.ResetPasswordResponse;
import api.http.httpResponse.TruResponse;
import api.http.httpResponse.hirewand.HireWandResponse;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.PutObjectResult;
import com.avaje.ebean.Query;
import com.google.api.client.repackaged.com.google.common.base.Strings;
import controllers.businessLogic.hirewand.HWHTTPException;
import controllers.businessLogic.hirewand.HireWandService;
import controllers.businessLogic.hirewand.InvalidRequestException;
import api.http.httpResponse.ongrid.OngridAadhaarVerificationResponse;
import controllers.businessLogic.ongrid.AadhaarService;
import controllers.businessLogic.ongrid.OnGridConstants;
import dao.staticdao.IdProofDAO;
import in.trujobs.proto.*;
import in.trujobs.proto.AddFeedbackRequest;
import models.entity.Auth;
import models.entity.Candidate;
import models.entity.Lead;
import models.entity.OM.*;
import models.entity.OO.CandidateEducation;
import models.entity.OO.CandidateStatusDetail;
import models.entity.OO.TimeShiftPreference;
import models.entity.Partner;
import models.entity.Static.*;
import models.util.SmsUtil;
import models.util.Util;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import play.Logger;
import org.json.JSONObject;

import javax.persistence.NonUniqueResultException;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

import static api.InteractionConstants.*;
import static controllers.businessLogic.InteractionService.*;
import static controllers.businessLogic.LeadService.createOrUpdateConvertedLead;
import static models.util.Util.generateOtp;
import static play.libs.Json.toJson;
import static play.mvc.Controller.session;


/**
 * Created by batcoder1 on 3/5/16.
 */
public class CandidateService
{
    public static LoginResponse login(String loginMobile, String loginPassword, int channelType){
        LoginResponse loginResponse = new LoginResponse();
        Logger.info(" login mobile: " + loginMobile);
        Candidate existingCandidate = CandidateService.isCandidateExists(loginMobile);
        if(existingCandidate == null){
            loginResponse.setStatus(LoginResponse.STATUS_NO_USER);
            Logger.info("User Does not Exists");
        }
        else {
            long candidateId = existingCandidate.getCandidateId();
            Auth existingAuth = Auth.find.where().eq("candidateId", candidateId).findUnique();
            if(existingAuth != null){
                if ((existingAuth.getPasswordMd5().equals(Util.md5(loginPassword + existingAuth.getPasswordSalt())))) {
                    Logger.info(existingCandidate.getCandidateFirstName() + " " + existingCandidate.getCandidateprofilestatus().getProfileStatusId());
                    loginResponse.setCandidateId(existingCandidate.getCandidateId());
                    loginResponse.setCandidateFirstName(existingCandidate.getCandidateFirstName());
                    loginResponse.setCandidateLastName(existingCandidate.getCandidateLastName());
                    loginResponse.setIsAssessed(existingCandidate.getCandidateIsAssessed());
                    loginResponse.setMinProfile(existingCandidate.getIsMinProfileComplete());
                    loginResponse.setLeadId(existingCandidate.getLead().getLeadId());
                    loginResponse.setCandidateJobPrefStatus(0);
                    loginResponse.setCandidateHomeLocalityStatus(0);
                    loginResponse.setGender(0);
                    if(existingCandidate.getCandidateGender() != null){
                        if(existingCandidate.getCandidateGender() == 1){
                            loginResponse.setGender(1);
                        }
                    }

                    // START : to cater specifically the app need
                    if(existingCandidate.getCandidateLocalityLat() != null
                            || existingCandidate.getCandidateLocalityLng() != null ){
                        loginResponse.setCandidateHomeLat(existingCandidate.getCandidateLocalityLat());
                        loginResponse.setCandidateHomeLng(existingCandidate.getCandidateLocalityLng());
                    }
                    if(!existingCandidate.getJobPreferencesList().isEmpty()){
                        if(existingCandidate.getJobPreferencesList().size()>0 && existingCandidate.getJobPreferencesList().get(0)!= null)
                            loginResponse.setCandidatePrefJobRoleIdOne(existingCandidate.getJobPreferencesList().get(0).getJobRole().getJobRoleId());
                        if(existingCandidate.getJobPreferencesList().size()>1 && existingCandidate.getJobPreferencesList().get(1)!= null)
                            loginResponse.setCandidatePrefJobRoleIdTwo(existingCandidate.getJobPreferencesList().get(1).getJobRole().getJobRoleId());
                        if(existingCandidate.getJobPreferencesList().size()>2 && existingCandidate.getJobPreferencesList().get(2)!= null)
                            loginResponse.setCandidatePrefJobRoleIdThree(existingCandidate.getJobPreferencesList().get(2).getJobRole().getJobRoleId());
                    }

                    // END
                    if(existingCandidate.getJobPreferencesList().size() > 0){
                        loginResponse.setCandidateJobPrefStatus(1);
                    }
                    if(existingCandidate.getCandidateLocalityLat() != null && existingCandidate.getCandidateLocalityLng() != null){
                        loginResponse.setCandidateHomeLocalityStatus(1);
                    }
                    if(existingCandidate.getLocality()!= null && existingCandidate.getLocality().getLocalityName()!=null){
                        loginResponse.setCandidateHomeLocalityName(existingCandidate.getLocality().getLocalityName());
                    }
                    loginResponse.setStatus(LoginResponse.STATUS_SUCCESS);

                    existingAuth.setAuthSessionId(UUID.randomUUID().toString());
                    existingAuth.setAuthSessionIdExpiryMillis(System.currentTimeMillis() + 24 * 60 * 60 * 1000);

                    loginResponse.setAuthSessionId(existingAuth.getAuthSessionId());
                    loginResponse.setSessionExpiryInMilliSecond(existingAuth.getAuthSessionIdExpiryMillis());

                    loginResponse.setIsCandidateVerified(existingAuth.getAuthStatus());

                    // adding session details
                    AuthService.addSession(existingAuth,existingCandidate);
                    existingAuth.update();
                    if(channelType == INTERACTION_CHANNEL_CANDIDATE_WEBSITE){
                        InteractionService.createInteractionForLoginCandidateViaWebsite(existingCandidate.getCandidateUUId());
                    } else {
                        InteractionService.createInteractionForLoginCandidateViaAndroid(existingCandidate.getCandidateUUId());
                    }

                    Logger.info("Login Successful for " + loginMobile);
                }
                else {
                    loginResponse.setStatus(LoginResponse.STATUS_WRONG_PASSWORD);
                    Logger.info("Incorrect Password " + loginMobile);
                }
            }
            else {
                loginResponse.setStatus(LoginResponse.STATUS_NO_PASSWORD);
                Logger.info("No User " + loginMobile);
            }
        }
        return loginResponse;
    }

    public static ResetPasswordResponse findUserAndSendOtp(String candidateMobile, int channelType){
        ResetPasswordResponse resetPasswordResponse = new ResetPasswordResponse();
        Candidate existingCandidate = isCandidateExists(candidateMobile);
        if(existingCandidate != null){
            Logger.info("Candidate Exists " + candidateMobile);
            Auth existingAuth = Auth.find.where().eq("candidateId", existingCandidate.getCandidateId()).findUnique();
            if(existingAuth == null){
                resetPasswordResponse.setStatus(LoginResponse.STATUS_NO_PASSWORD);
                Logger.info("reset password not allowed as Auth doesnt exist");
            } else {
                int randomPIN = generateOtp();
                existingCandidate.update();
                SmsUtil.sendResetPasswordOTPSms(randomPIN, existingCandidate.getCandidateMobile(), channelType);

                String interactionResult = InteractionConstants.INTERACTION_RESULT_CANDIDATE_TRIED_TO_RESET_PASSWORD;
                String objAUUID = "";
                objAUUID = existingCandidate.getCandidateUUId();
                if (channelType == INTERACTION_CHANNEL_CANDIDATE_WEBSITE) {
                    InteractionService.createInteractionForResetPasswordAttemptViaWebsite(objAUUID, interactionResult);
                } else{
                    InteractionService.createInteractionForResetPasswordAttemptViaAndroid(objAUUID, interactionResult);
                }
                resetPasswordResponse.setOtp(randomPIN);
                resetPasswordResponse.setStatus(LoginResponse.STATUS_SUCCESS);
            }
        } else{
            resetPasswordResponse.setStatus(LoginResponse.STATUS_NO_USER);
            Logger.info("Eeset password not allowed as Candidate doesnt exist");
        }
        return resetPasswordResponse;
    }

    public static List<JobPreference> getCandidateJobPreferenceList(List<Integer> jobsList, Candidate candidate) {
        List<JobPreference> candidateJobPreferences = new ArrayList<>();
        for(Integer s : jobsList) {
            JobPreference candidateJobPreference = new JobPreference();
            candidateJobPreference.setCandidate(candidate);
            JobRole jobRole = JobRole.find.where().eq("JobRoleId", s).findUnique();
            candidateJobPreference.setJobRole(jobRole);
            candidateJobPreferences.add(candidateJobPreference);
        }
        return candidateJobPreferences;
    }

    public static List<LocalityPreference> getCandidateLocalityPreferenceList(List<Integer> localityList, Candidate candidate) {
        List<LocalityPreference> candidateLocalityPreferenceList = new ArrayList<>();
        for(Integer  localityId : localityList) {
            LocalityPreference candidateLocalityPreference = new LocalityPreference();
            candidateLocalityPreference.setCandidate(candidate);

            Locality locality = Locality.find.where()
                    .eq("localityId", localityId).findUnique();

            candidateLocalityPreference.setLocality(locality);
            candidateLocalityPreferenceList.add(candidateLocalityPreference);
        }
        return candidateLocalityPreferenceList;
    }

    public static Candidate isCandidateExists(String mobile) {
        try{
            Candidate existingCandidate = Candidate.find.where().or(com.avaje.ebean.Expr.eq("candidateMobile",
                    FormValidator.convertToIndianMobileFormat(mobile)),com.avaje.ebean.Expr.eq("candidateSecondMobile",
                    FormValidator.convertToIndianMobileFormat(mobile))).findUnique();
            if(existingCandidate != null) {
                return existingCandidate;
            }
        } catch (NonUniqueResultException nu){
            // get the list of candidate and sort by candidateId
            // return the lowest primary key candidate Object
            // register the event with proper info

            List<Candidate> existingCandidateList = Candidate.find.where().eq("candidateMobile", mobile).findList();
            if(!existingCandidateList.isEmpty()){
                existingCandidateList.sort((l1, l2) -> l1.getCandidateId() <= l2.getCandidateId() ? 1 : 0);
                Logger.info("Duplicate Candidate Encountered with mobile no: "+ mobile + "- Returned CandidateId = "
                        + existingCandidateList.get(0).getCandidateId() + " UUID-:"+existingCandidateList.get(0).getCandidateUUId());
                SmsUtil.sendDuplicateCandidateSmsToDevTeam(mobile);
                return existingCandidateList.get(0);
            }
        }
        return null;
    }

    public static CandidateSignUpResponse signUpCandidate(CandidateSignUpRequest candidateSignUpRequest,
                                                          int channelType,
                                                          int leadSourceId) {
        List<Integer> localityList = new ArrayList<>();
        localityList.add(candidateSignUpRequest.getCandidateHomeLocality());
        List<Integer> jobsList = candidateSignUpRequest.getCandidateJobPref();

        CandidateSignUpResponse candidateSignUpResponse = new CandidateSignUpResponse();
        String result = "";
        String objectAUUId = "";
        Logger.info("Checking for mobile number: " + candidateSignUpRequest.getCandidateMobile());
        Candidate candidate = isCandidateExists(candidateSignUpRequest.getCandidateMobile());
        String leadName = candidateSignUpRequest.getCandidateFirstName()+ " " + candidateSignUpRequest.getCandidateSecondName();
        Lead lead = LeadService.createOrUpdateConvertedLead(leadName, FormValidator.convertToIndianMobileFormat(candidateSignUpRequest.getCandidateMobile()), leadSourceId, channelType, LeadService.LeadType.CANDIDATE);
        Integer interactionTypeVal;
        try {
            if(candidate == null) {
                candidate = new Candidate();
                Logger.info("creating new candidate");
                if(candidateSignUpRequest.getCandidateFirstName()!= null){
                    candidate.setCandidateFirstName(candidateSignUpRequest.getCandidateFirstName());
                }
                if(candidateSignUpRequest.getCandidateSecondName()!= null){
                    candidate.setCandidateLastName(candidateSignUpRequest.getCandidateSecondName());
                }
                if(candidateSignUpRequest.getCandidateMobile()!= null){
                    candidate.setCandidateMobile(candidateSignUpRequest.getCandidateMobile());
                }

                if(candidateSignUpRequest.getCandidateSecondMobile() != null){
                    candidate.setCandidateSecondMobile(candidateSignUpRequest.getCandidateSecondMobile());
                }
                if(candidateSignUpRequest.getCandidateThirdMobile() != null){
                    candidate.setCandidateThirdMobile(candidateSignUpRequest.getCandidateThirdMobile());
                }
                if(candidateSignUpRequest.getCandidateHomeLocality() != null){
                    Locality locality = Locality.find.where().eq("localityId", candidateSignUpRequest.getCandidateHomeLocality()).findUnique();
                    if(locality != null){
                        candidate.setLocality(locality);
                        candidate.setLocalityPreferenceList(getCandidateLocalityPreferenceList(localityList, candidate));
                    }
                }
                if(jobsList != null){
                    candidate.setJobPreferencesList(getCandidateJobPreferenceList(jobsList, candidate));
                }

                candidateSignUpResponse = createNewCandidate(candidate, lead);

                // triggers when candidate is self created
                if(channelType == INTERACTION_CHANNEL_CANDIDATE_ANDROID || channelType == INTERACTION_CHANNEL_CANDIDATE_WEBSITE){
                    triggerOtp(candidate, candidateSignUpResponse, channelType);
                }

                result = InteractionConstants.INTERACTION_RESULT_NEW_CANDIDATE;
                objectAUUId = candidate.getCandidateUUId();
                interactionTypeVal = InteractionConstants.INTERACTION_TYPE_CANDIDATE_SIGN_UP;

            } else {
                Auth auth = AuthService.isAuthExists(candidate.getCandidateId());
                if(auth == null ) {
                    Logger.info("auth doesn't exists for this candidate");
                    candidate.setCandidateFirstName(candidateSignUpRequest.getCandidateFirstName());
                    candidate.setCandidateLastName(candidateSignUpRequest.getCandidateSecondName());

                    if(candidateSignUpRequest.getCandidateHomeLocality() != null){
                        Locality locality = Locality.find.where().eq("localityId", candidateSignUpRequest.getCandidateHomeLocality()).findUnique();
                        if(locality != null){
                            candidate.setLocality(locality);
                            candidate.setLocalityPreferenceList(getCandidateLocalityPreferenceList(localityList, candidate));
                        }
                    }
                    if(jobsList != null){
                        candidate.setJobPreferencesList(getCandidateJobPreferenceList(jobsList, candidate));
                    }

                    // triggers when candidate is self created
                    if(channelType == INTERACTION_CHANNEL_CANDIDATE_ANDROID || channelType == INTERACTION_CHANNEL_CANDIDATE_WEBSITE){
                        triggerOtp(candidate, candidateSignUpResponse, channelType);
                    }
                    interactionTypeVal = InteractionConstants.INTERACTION_TYPE_EXISTING_CANDIDATE_TRIED_SIGNUP;
                    result = InteractionConstants.INTERACTION_RESULT_EXISTING_CANDIDATE_VERIFICATION;
                    objectAUUId = candidate.getCandidateUUId();
                    candidateSignUpResponse.setStatus(CandidateSignUpResponse.STATUS_SUCCESS);
                } else{
                    result = InteractionConstants.INTERACTION_RESULT_EXISTING_CANDIDATE_SIGNUP;
                    interactionTypeVal = InteractionConstants.INTERACTION_TYPE_EXISTING_CANDIDATE_TRIED_SIGNUP_AND_SIGNUP_NOT_ALLOWED;
                    objectAUUId = candidate.getCandidateUUId();
                    candidateSignUpResponse.setStatus(CandidateSignUpResponse.STATUS_EXISTS);
                }
                candidate.candidateUpdate();
            }

            // Insert Interaction only for self sign up as interaction for sign up support will be handled in createCandidateProfile
            if(channelType == INTERACTION_CHANNEL_CANDIDATE_WEBSITE){
                // candidate sign up via website
                createInteractionForSignUpCandidateViaWebsite(objectAUUId, result, interactionTypeVal);
            } else if(channelType == INTERACTION_CHANNEL_CANDIDATE_ANDROID) {
                // candidate sign up via partner
                createInteractionForSignUpCandidateViaAndroid(objectAUUId, result, interactionTypeVal);
            }
        } catch (NullPointerException n){
            n.printStackTrace();
            candidateSignUpResponse.setStatus(CandidateSignUpResponse.STATUS_FAILURE);
        }
        return candidateSignUpResponse;
    }

    /**
     * This method is called from the following front ends:
     *  website: when a user self-edits basic profile details
     *  website: when a user self-edits experience/skills details
     *  website: when a user self-edits eduction details
     *  support UI: when an agent creates/updates any part of a candidate profile
     *
     * @param request AddCandidateRequest or one of its child classes
     *                (AddCandidateEducationRequest/AddCandidateExperienceRequest/AddSupportCandidateRequest)
     * @param channelType Indicates whether this method is being called from support ui or from website or from Android app
     * @param profileUpdateFlag Indicates which part of candidate's profile is being updated
     * @return
     */
    public static CandidateSignUpResponse createCandidateProfile(AddCandidateRequest request,
                                                                 int channelType,
                                                                 int profileUpdateFlag)
    {
        CandidateSignUpResponse candidateSignUpResponse = new CandidateSignUpResponse();
        // get candidateBasic obj from req
        // Handle jobPrefList and any other list with , as break point at application only
        Logger.info("Creating candidate profile for mobile " + request.getCandidateMobile());

        // Check if this candidate already exists
        Candidate candidate = isCandidateExists(FormValidator.convertToIndianMobileFormat(request.getCandidateMobile()));

        // Initialize some basic interaction details
        String createdBy = InteractionConstants.INTERACTION_CREATED_SELF;
        String interactionResult = InteractionConstants.INTERACTION_RESULT_CANDIDATE_INFO_UPDATED_SELF;
        Integer interactionType = InteractionConstants.INTERACTION_TYPE_CANDIDATE_PROFILE_UPDATE;

        String interactionNote;
        boolean isNewCandidate = false;

        String objAUUId = "";
        String objBUUId = "";

        Integer objBType = 0;

        if(candidate == null){
            Logger.info("Candidate with mobile number: " + request.getCandidateMobile() + " doesn't exist");
            CandidateSignUpRequest candidateSignUpRequest = ( CandidateSignUpRequest ) request;

            // sign this candidate up as a first step
            candidateSignUpResponse = signUpCandidate(candidateSignUpRequest, channelType, request.getLeadSource());

            if(candidateSignUpResponse.getStatus() != CandidateSignUpResponse.STATUS_SUCCESS){
                Logger.info("Error while signing up candidate with mobile number: " + request.getCandidateMobile());
                return candidateSignUpResponse;
            } else {
                candidate = isCandidateExists(request.getCandidateMobile());
                // Make a note that this was the first time the candidate has been created. We will use it later
                // to set the right interaction details
                isNewCandidate = true;
            }

        } else {

            Logger.info("Candidate with mobile number: " + request.getCandidateMobile() + " already exists");

            // update new job preferences
            if(request.getCandidateJobPref() != null) {
                candidate.setJobPreferencesList(getCandidateJobPreferenceList(request.getCandidateJobPref(), candidate));
            }

            List<Integer> localityList = new ArrayList<>();
            if(request.getCandidateHomeLocality() != null){
                Locality locality = Locality.find.where().eq("localityId", request.getCandidateHomeLocality()).findUnique();

                if(locality != null){
                    candidate.setLocality(locality);
                    localityList.add(request.getCandidateHomeLocality());
                    candidate.setLocalityPreferenceList(getCandidateLocalityPreferenceList(localityList, candidate));
                }
            } else {
                if(candidate.getLocality() != null){
                    localityList.add((int) candidate.getLocality().getLocalityId());
                    candidate.setLocalityPreferenceList(getCandidateLocalityPreferenceList(localityList, candidate));
                }
            }

            if(request.getCandidateMobile() != null){
                // If a lead already exists for this candiate, update its status to 'WON'. If not create a new lead
                // with status 'WON'
                Lead lead = createOrUpdateConvertedLead(request.getCandidateFirstName() + " " + request.getCandidateSecondName(), request.getCandidateMobile(),
                        request.getLeadSource(), channelType, LeadService.LeadType.CANDIDATE);
                Logger.info("Lead : " + lead.getLeadId() + " created or updated for candidate with mobile: "
                        + request.getCandidateMobile());
                candidate.setLead(lead);
            }
        }

        // Now we check if we are dealing with the request to update basic profile details from website (or)
        // dealing with a create/update candidate profile request from support
        if(profileUpdateFlag == ServerConstants.UPDATE_BASIC_PROFILE ||
                profileUpdateFlag == ServerConstants.UPDATE_ALL_BY_SUPPORT) {

            candidateSignUpResponse = updateBasicProfile(candidate, request);

            // In case of errors, return at this point
            if(candidateSignUpResponse.getStatus() != CandidateSignUpResponse.STATUS_SUCCESS){
                Logger.info("Error while updating basic profile of candidate with mobile " + candidate.getCandidateMobile());
                return candidateSignUpResponse;
            }

            // Set the appropriate interaction result
            if(profileUpdateFlag == ServerConstants.UPDATE_BASIC_PROFILE) {
                interactionResult = InteractionConstants.INTERACTION_RESULT_CANDIDATE_BASIC_PROFILE_INFO_UPDATED_SELF;
            }
        }

        // Now we check if we are dealing with the reqeust to update skills/experience profile details from website (or)
        // dealing with a create/update candidate profile request from support
        if(profileUpdateFlag == ServerConstants.UPDATE_SKILLS_PROFILE ||
                profileUpdateFlag == ServerConstants.UPDATE_ALL_BY_SUPPORT) {

            candidateSignUpResponse = updateSkillProfile(candidate, (AddCandidateExperienceRequest) request);

            // In case of errors, return at this point
            if(candidateSignUpResponse.getStatus() != CandidateSignUpResponse.STATUS_SUCCESS){
                Logger.info("Error while updating experience profile of candidate with mobile " + candidate.getCandidateMobile());
                return candidateSignUpResponse;
            }

            // Set the appropriate interaction result
            if(profileUpdateFlag == ServerConstants.UPDATE_SKILLS_PROFILE){
                interactionResult = InteractionConstants.INTERACTION_RESULT_CANDIDATE_SKILLS_PROFILE_INFO_UPDATED_SELF;
            }
        }

        // Now we check if we are dealing with the reqeust to update education profile details from website (or)
        // dealing with a create/update candidate profile request from support
        if(profileUpdateFlag == ServerConstants.UPDATE_EDUCATION_PROFILE ||
                profileUpdateFlag == ServerConstants.UPDATE_ALL_BY_SUPPORT) {

            candidateSignUpResponse = updateEducationProfile(candidate, (AddCandidateEducationRequest) request);

            // In case of errors, return at this point
            if(candidateSignUpResponse.getStatus() != CandidateSignUpResponse.STATUS_SUCCESS){
                Logger.info("Error while updating education profile of candidate with mobile " + candidate.getCandidateMobile());
                return candidateSignUpResponse;
            }

            // Set the appropriate interaction result
            if(profileUpdateFlag == ServerConstants.UPDATE_EDUCATION_PROFILE){
                interactionResult = InteractionConstants.INTERACTION_RESULT_CANDIDATE_EDUCATION_PROFILE_INFO_UPDATED_SELF;
            }
        }

        // set the default interaction note string
        interactionNote = InteractionConstants.INTERACTION_NOTE_BLANK;

        /**
         *  By default, all the interaction values (interactionType, createdBy, channel etc.) are initialized by
         *  values of a user self action
         */

        // check if we have auth record for this candidate. if we dont have, create one with a temporary password
        Auth auth = AuthService.isAuthExists(candidate.getCandidateId());
        if (auth == null) {
            if(channelType == INTERACTION_CHANNEL_SUPPORT_WEBSITE || channelType == INTERACTION_CHANNEL_PARTNER_WEBSITE){
                // TODO: differentiate between in/out call
                Boolean isSupport = false;
                if(channelType == INTERACTION_CHANNEL_SUPPORT_WEBSITE){
                    isSupport = true;
                }
                createAndSaveDummyAuthFor(candidate, isSupport);
                interactionResult += " & " + InteractionConstants.INTERACTION_NOTE_DUMMY_PASSWORD_CREATED;
            }
        }
        // check if we have enough details required to complete the minimum profile
        candidate.setIsMinProfileComplete(isMinProfileComplete(candidate));
        objAUUId = candidate.getCandidateUUId();

        /**
         *  In this step, we are checking various channel for candidate profile create/update (Support, candidate_web, candidate_android, partner)
         *  in each case, we are checking if "isNewCandiate" status to determine weather the candidate is being created or updated
         *  Finally creating respective interaction according to the case
         */

        if(channelType == INTERACTION_CHANNEL_SUPPORT_WEBSITE || channelType == INTERACTION_CHANNEL_PARTNER_WEBSITE){
            // update additional fields that are part of the support request
            updateOthersBySupport(candidate, request);
            AddSupportCandidateRequest supportCandidateRequest = (AddSupportCandidateRequest) request;

            if(channelType == INTERACTION_CHANNEL_SUPPORT_WEBSITE){
                createdBy = session().get("sessionUsername");
                interactionNote = supportCandidateRequest.getSupportNote();
                if(isNewCandidate) {
                    interactionType = InteractionConstants.INTERACTION_TYPE_CANDIDATE_PROFILE_CREATED;
                    interactionResult = InteractionConstants.INTERACTION_RESULT_NEW_CANDIDATE_SUPPORT;
                } else {
                    interactionType = InteractionConstants.INTERACTION_TYPE_CANDIDATE_PROFILE_UPDATE;
                    interactionResult = InteractionConstants.INTERACTION_RESULT_CANDIDATE_INFO_UPDATED_SYSTEM;
                }

                InteractionService.createInteractionForCreateCandidateProfileViaSupport(objAUUId, objBUUId, objBType,
                        interactionType, interactionNote, interactionResult, createdBy);

            } else{
                // candidate being created by partner
                createdBy = session().get("partnerName");
                if(isNewCandidate) {
                    interactionType = InteractionConstants.INTERACTION_TYPE_CANDIDATE_PROFILE_CREATED;
                    interactionResult = InteractionConstants.INTERACTION_RESULT_NEW_CANDIDATE_PARTNER;
                    Partner partner = Partner.find.where().eq("partner_id", session().get("partnerId")).findUnique();
                    if(partner != null){
                        objBType = ServerConstants.OBJECT_TYPE_PARTNER;
                        objBUUId = partner.getPartnerUUId();
                    }
                } else {
                    interactionType = InteractionConstants.INTERACTION_TYPE_CANDIDATE_PROFILE_UPDATE;
                    interactionResult = InteractionConstants.INTERACTION_RESULT_CANDIDATE_INFO_UPDATED_PARTNER;
                    Partner partner = Partner.find.where().eq("partner_id", session().get("partnerId")).findUnique();
                    if(partner != null){
                        objBType = ServerConstants.OBJECT_TYPE_PARTNER;
                        objBUUId = partner.getPartnerUUId();
                    }
                }
                InteractionService.createInteractionForCreateCandidateProfileViaPartner(objAUUId, objBUUId, objBType,
                        interactionType, interactionNote, interactionResult, createdBy);
            }

        } else{
            //getting updated by the candidate
            if(isNewCandidate) {
                interactionType = InteractionConstants.INTERACTION_TYPE_CANDIDATE_PROFILE_CREATED;
            } else{
                interactionType = InteractionConstants.INTERACTION_TYPE_CANDIDATE_PROFILE_UPDATE;
            }

            if(channelType == INTERACTION_CHANNEL_CANDIDATE_ANDROID){
                InteractionService.createInteractionForCreateCandidateProfileViaAndroidByCandidate(objAUUId, objBUUId, objBType,
                        interactionType, interactionNote, interactionResult);
            } else{
                InteractionService.createInteractionForCreateCandidateProfileViaWebsiteByCandidate(objAUUId, objBUUId, objBType,
                        interactionType, interactionNote, interactionResult);
            }
        }

        candidate.update();

        // Trigger aadhaar verification
        verifyAadhaar(candidate.getCandidateMobile());

        candidateSignUpResponse.setStatus(CandidateSignUpResponse.STATUS_SUCCESS);
        candidateSignUpResponse.setMinProfile(candidate.getIsMinProfileComplete());
        candidateSignUpResponse.setCandidateFirstName(candidate.getCandidateFirstName());
        candidateSignUpResponse.setCandidateId(candidate.getCandidateId());

        return candidateSignUpResponse;
    }

    public static void resetLocalityAndJobPref(Candidate existingCandidate, List<LocalityPreference> localityPreferenceList, List<JobPreference> jobPreferencesList) {

        // reset pref
        List<LocalityPreference> allLocality = LocalityPreference.find.where().eq("CandidateId", existingCandidate.getCandidateId()).findList();
        for(LocalityPreference candidateLocality : allLocality){
            candidateLocality.delete();
        }

        resetJobPref(existingCandidate, jobPreferencesList);
        existingCandidate.setLocalityPreferenceList(localityPreferenceList);
    }

    public static void resetJobPref(Candidate existingCandidate, List<JobPreference> jobPreferencesList) {
        Logger.info("Resetting existing jobPrefs");
        List<JobPreference> allJob = JobPreference.find.where().eq("CandidateId", existingCandidate.getCandidateId()).findList();
        for(JobPreference candidateJobs : allJob){
            candidateJobs.delete();
        }
        existingCandidate.setJobPreferencesList(jobPreferencesList);
    }

    // individual update service for pre-screen-edit
    public static boolean updateCandidateDocument(Candidate candidate,
                                                                 UpdateCandidateDocument updateCandidateDocument)
    {

        // get candidate's existing idproof list
        List<IDProofReference> existingIdProofList = candidate.getIdProofReferenceList();
        Map<Integer, IDProofReference> existingIdProofIdToReference = new HashMap<Integer, IDProofReference>();
        boolean isVerifyAadhaar = false;

        // create a map of existing idproofid to idproofnumber
        if (existingIdProofList != null && !existingIdProofList.isEmpty()) {
            for (IDProofReference idProof : existingIdProofList) {
                existingIdProofIdToReference.put(idProof.getIdProof().getIdProofId(), idProof);
            }
        }

        List<IDProofReference> candidateIdProofListToSave = new ArrayList<>();
        List<Integer> idProofIdList = new ArrayList<>();

        if (updateCandidateDocument.getIdProofWithIdNumberList() != null
                && updateCandidateDocument.getIdProofWithIdNumberList().size() > 0)
        {
            // Get a list of all ids of idproofs that this candidate update request contains
            for (UpdateCandidateDocument.IdProofWithIdNumber idProofWithIdNumber :
                    updateCandidateDocument.getIdProofWithIdNumberList())
            {
                if (idProofWithIdNumber.getIdProofId() != null) idProofIdList.add(idProofWithIdNumber.getIdProofId());
            }

            Map<?, IdProof> staticIdProofMap = new IdProofDAO().getIdToRecordMap(idProofIdList);

            for (UpdateCandidateDocument.IdProofWithIdNumber idProofWithIdNumber :
                    updateCandidateDocument.getIdProofWithIdNumberList())
            {
                // if the candidate already had details pertaining to this idproof, then update the record
                if (existingIdProofIdToReference.containsKey(idProofWithIdNumber.getIdProofId())) {
                    IDProofReference existingIdProofRef = existingIdProofIdToReference.get(idProofWithIdNumber.getIdProofId());

                    if (existingIdProofRef.getIdProofNumber() == null
                            || existingIdProofRef.getIdProofNumber().trim().isEmpty()
                            || !existingIdProofRef.getIdProofNumber().equals(idProofWithIdNumber.getIdNumber()))
                    {
                        existingIdProofRef.setIdProofNumber(idProofWithIdNumber.getIdNumber());
                        candidateIdProofListToSave.add(existingIdProofRef);
                        //existingIdProofRef.update();

                        // if aadhaar details changed (and the number was not removed during the update), then
                        // enable verification flag
                        if (idProofWithIdNumber.getIdProofId() == IdProofDAO.IDPROOF_AADHAAR_ID
                                && (idProofWithIdNumber.getIdNumber() != null || !idProofWithIdNumber.getIdNumber().isEmpty()))
                        {
                            Logger.info("Updating aadhaar for candidate " + candidate.getCandidateMobile());
                            isVerifyAadhaar = true;
                        }
                    } else {
                        // if the incoming data is exactly same as already in db, add it directly to the candidateIdProofList,
                        // since the cascade overrides all the data of a foreign key.
                        candidateIdProofListToSave.add(existingIdProofRef);
                    }
                }
                // if this is the first time we are getting this idproof details for candidate, then create new record
                else {
                    IDProofReference idProofReference = new IDProofReference();
                    idProofReference.setCandidate(candidate);
                    idProofReference.setIdProof(staticIdProofMap.get(idProofWithIdNumber.getIdProofId()));
                    idProofReference.setIdProofNumber(idProofWithIdNumber.getIdNumber());
                    candidateIdProofListToSave.add(idProofReference);
                    //idProofReference.save();

                    if (idProofWithIdNumber.getIdProofId() == IdProofDAO.IDPROOF_AADHAAR_ID) {
                        Logger.info("Saving new aadhaar for candidate " + candidate.getCandidateMobile());
                        isVerifyAadhaar = true;
                    }
                }
            }

            Logger.info(String.valueOf(toJson(candidateIdProofListToSave)));
            candidate.setIdProofReferenceList(candidateIdProofListToSave);
            candidate.update();
        }

        return isVerifyAadhaar;
    }

    public static void updateCandidateLanguageKnown(Candidate candidate,
                                                    UpdateCandidateLanguageKnown updateCandidateLanguageKnown)
    {

        List<LanguageKnown> languageKnownList =
                getLanguagesKnown(updateCandidateLanguageKnown.getCandidateKnownLanguageList(), candidate);

        if (languageKnownList != null) {
            candidate.setLanguageKnownList(languageKnownList);
            candidate.update();
        }
    }

    public static void updateCandidateAssetOwned(Candidate candidate, UpdateCandidateAsset asset){
        if(asset!= null && asset.getAssetIdList() != null && asset.getAssetIdList().size() > 0) {
            candidate.setCandidateAssetList(getAssetList(asset.getAssetIdList(), candidate));
            candidate.update();
        }
    }

    public static void updateCandidateDOB(Candidate candidate, UpdateCandidateDob updateCandidateDob){
        candidate.setCandidateDOB(updateCandidateDob.getCandidateDob());
        candidate.update();
    }

    public static void updateCandidateWorkExperience(Candidate candidate, UpdateCandidateWorkExperience workExperience)
    {
        candidate.setCandidateTotalExperience(workExperience.getCandidateTotalExperience());
        if(workExperience.getExtraDetailAvailable()!= null && workExperience.getExtraDetailAvailable()) {
            if(workExperience.getPastCompanyList() != null ) {
                candidate.setJobHistoryList(getJobHistoryList(workExperience.getPastCompanyList(), candidate));
            }
        }
        candidate.update();
    }

    public static void updateCandidateEducation(Candidate candidate, UpdateCandidateEducation candidateEducation)
    {
        CandidateEducation educationRecord = getCandidateEducation(candidateEducation.getCandidateEducationLevel(),
                candidateEducation.getCandidateDegree(),
                candidateEducation.getCandidateEducationCompletionStatus(),
                candidateEducation.getCandidateEducationInstitute(),candidate);

        if (educationRecord != null) {
            candidate.setCandidateEducation(educationRecord);
            candidate.update();
        }
    }

    public static void updateCandidateLastWithdrawnSalary(Candidate candidate,
                                                          UpdateCandidateLastWithdrawnSalary lastWithdrawnSalary)
    {
        candidate.setCandidateLastWithdrawnSalary(lastWithdrawnSalary.getCandidateLastWithdrawnSalary());
        candidate.update();
    }

    public static void updateCandidateGender(Candidate candidate, UpdateCandidateGender gender) {
        candidate.setCandidateGender(gender.getCandidateGender());
        candidate.update();
    }

    public static void updateCandidateHomeLocality(Candidate candidate, UpdateCandidateHomeLocality homeLocality) {
        if (homeLocality.getCandidateHomeLocality() != null) {
            Locality locality = Locality.find.where().eq("localityId", homeLocality.getCandidateHomeLocality()).findUnique();
            if (locality != null){
                candidate.setLocality(locality);
            }
        }

        candidate.update();
    }

    public static void updateCandidateWorkshift(Candidate candidate, UpdateCandidateTimeShiftPreference timeShiftPreference)
    {
        candidate.setTimeShiftPreference(getTimeShiftPref(timeShiftPreference.getCandidateTimeShiftPref(), candidate));

        candidate.update();
    }

    public static List<Candidate> computeAllProfileCompletionScores(Date startDate, Date endDate, Float scoreLimit) {
        Query<Candidate> candidateQuery = Candidate.find.query();

        if (startDate != null) {
            candidateQuery.where().gt("candidateCreateTimestamp", startDate);
        }

        if (endDate != null) {
            candidateQuery.where().lt("candidateCreateTimestamp", endDate);
        }

        List<Candidate> candidateList = candidateQuery.findList();

        List<Candidate> resultList = new ArrayList<Candidate>();

        for (Candidate candidate : candidateList) {
            float profileCompletionScore = getProfileCompletionPercent(candidate.getCandidateMobile());
            if (profileCompletionScore <= scoreLimit) {
                candidate.setProfileCompletionScore(profileCompletionScore);
                resultList.add(candidate);
            }
        }

        return resultList;
    }

    public static float getProfileCompletionPercent(String candidateMobile) {

        Candidate candidate = CandidateService.isCandidateExists(candidateMobile);

        // To keep track of % of fields filled in each priority group
        float p0CompletionPercent = 0;
        float p1CompletionPercent = 0;
        float p2CompletionPercent = 0;
        float profileCompletionPercent = 0;

        float p0Weight = 0.7f;
        float p1Weight = 0.25f;
        float p2Weight = 0.05f;

        if (candidate != null) {

            p0CompletionPercent = getP0FieldsCompletionPercent(candidate);
            p1CompletionPercent = getP1FieldsCompletionPercent(candidate);
            p2CompletionPercent = getP2FieldsCompletionPercent(candidate);
            // For now we do not care about fields in p3 category (last name, third mobile, locality pref list)
        }

        profileCompletionPercent = p0CompletionPercent * p0Weight
                + p1CompletionPercent * p1Weight
                + p2CompletionPercent * p2Weight;

        return profileCompletionPercent;
    }

    public static float getP0FieldsCompletionPercent(Candidate candidate) {

        float p0FieldCount = 0;
        float p0CompletedFieldCount = 0;

        if (candidate != null) {

            // check for number of completed fields in P0 category

            p0FieldCount++;
            if (candidate.getCandidateFirstName() != null) {
                p0CompletedFieldCount++;
            }

            p0FieldCount++;
            if (candidate.getCandidateMobile() != null) {
                p0CompletedFieldCount++;
            }

            p0FieldCount++;
            if (candidate.getJobPreferencesList() != null && !candidate.getJobPreferencesList().isEmpty()) {
                p0CompletedFieldCount++;
            }

            p0FieldCount++;
            if (candidate.getLocality() != null) {
                p0CompletedFieldCount++;
            }

            p0FieldCount++;
            if (candidate.getCandidateGender() != null) {
                p0CompletedFieldCount++;
            }

            p0FieldCount++;
            if (candidate.getCandidateIsEmployed() != null) {
                p0CompletedFieldCount++;
            }

            p0FieldCount++;
            if (candidate.getCandidateTotalExperience() != null) {
                p0CompletedFieldCount++;

                if (candidate.getCandidateTotalExperience() > 0) {
                    p0FieldCount++;
                    if (candidate.getCandidateLastWithdrawnSalary() != null) {
                        p0CompletedFieldCount++;
                    }
                }
            }

            p0FieldCount++;
            if (candidate.getCandidateEducation() != null) {
                CandidateEducation education = candidate.getCandidateEducation();

                if (education.getEducation() != null) {
                    p0CompletedFieldCount++;
                }
            }

            p0FieldCount++;
            if (candidate.getLanguageKnownList() != null) {
                if (candidate.getLanguageKnownList().size() >= 1) {
                    p0CompletedFieldCount++;
                }
            }

            p0FieldCount++;
            if (candidate.getCandidateSkillList() != null) {
                if (candidate.getCandidateSkillList().size() >= 1) {
                    p0CompletedFieldCount++;
                }
            }

        }
        return (p0CompletedFieldCount / p0FieldCount);
    }

    public static float getP1FieldsCompletionPercent(Candidate candidate) {

        float p1FieldCount = 0;
        float p1CompletedFieldCount = 0;

        if (candidate != null) {

            // check for number of completed fields in P1 category
            if (candidate.getCandidateIsEmployed() != null) {
                p1FieldCount++;
                if (candidate.getCandidateCurrentJobDetail() != null) {
                    p1CompletedFieldCount++;
                }
            }

            if (candidate.getCandidateEducation() != null) {
                CandidateEducation education = candidate.getCandidateEducation();

                p1FieldCount++;
                if (education.getCandidateEducationCompletionStatus() != null) {
                    p1CompletedFieldCount++;
                }

                p1FieldCount++;
                if (education.getDegree() != null) {
                    p1CompletedFieldCount++;

                }
            }

            p1FieldCount++;
            if (candidate.getCandidateSecondMobile() != null) {
                p1CompletedFieldCount++;
            }

            p1FieldCount++;
            if (candidate.getTimeShiftPreference() != null) {
                p1CompletedFieldCount++;
            }

            p1FieldCount++;
            if (candidate.getCandidateDOB() != null) {
                p1CompletedFieldCount++;
            }
        }

        return (p1CompletedFieldCount / p1FieldCount);
    }

    public static float getP2FieldsCompletionPercent(Candidate candidate) {

        float p2FieldCount = 0;
        float p2CompletedFieldCount = 0;

        if (candidate != null) {
            // check for number of completed fields in P2 category

            if (candidate.getCandidateTotalExperience() != null) {
                if (candidate.getCandidateTotalExperience() > 0) {
                    p2FieldCount++;
                    if (candidate.getCandidateExpList() != null && candidate.getCandidateExpList().size() > 0) {
                        p2CompletedFieldCount++;
                    }
                }
            }

            if (candidate.getCandidateEducation() != null) {
                CandidateEducation education = candidate.getCandidateEducation();

                p2FieldCount++;
                if (education.getCandidateLastInstitute() != null) {
                    p2CompletedFieldCount++;
                }

            }

            p2FieldCount++;
            if (candidate.getLanguageKnownList() != null) {
                if (candidate.getLanguageKnownList().size() >= 3) {
                    p2CompletedFieldCount++;
                }
            }

            p2FieldCount++;
            if (candidate.getIdProofReferenceList() != null) {
                p2CompletedFieldCount++;
            }

            p2FieldCount++;
            if (candidate.getCandidateExperienceLetter() != null) {
                p2CompletedFieldCount++;
            }
        }

        return (p2CompletedFieldCount / p2FieldCount);
    }

    public static void sendDummyAuthForCandidateByPartner(Candidate candidate) {
        // create dummy auth
        Auth authToken = Auth.find.where().eq("candidateId", candidate.getCandidateId()).findUnique();
        if(authToken != null){
            String dummyPassword = String.valueOf(Util.randomLong());
            authToken.setCandidateId(candidate.getCandidateId());
            authToken.setPasswordMd5(Util.md5(dummyPassword + authToken.getPasswordSalt()));
            authToken.save();
            SmsUtil.sendWelcomeSmsFromSupport(candidate.getCandidateFirstName(), candidate.getCandidateMobile(), dummyPassword);
            Logger.info("Dummy auth saved and sent to " + candidate.getCandidateMobile());
        }
    }

    public static void verifyAadhaar(String candidateMobile) {
        Logger.info("verifying aadhaar for " + candidateMobile);
        new Thread(() -> {
            AadhaarService aadhaarService = new AadhaarService(OnGridConstants.AUTH_STRING,
                    OnGridConstants.COMMUNITY_ID, OnGridConstants.BASE_URL);

            OngridAadhaarVerificationResponse response =
                    aadhaarService.sendAadharSyncVerificationRequest(candidateMobile);
        }).start();
    }

    private static CandidateSignUpResponse createNewCandidate(Candidate candidate, Lead lead) {

        CandidateSignUpResponse candidateSignUpResponse = new CandidateSignUpResponse();
        CandidateProfileStatus candidateProfileStatus =
                CandidateProfileStatus.find.where().eq("profileStatusId", ServerConstants.CANDIDATE_STATE_ACTIVE).findUnique();

        if(candidateProfileStatus != null) {
            candidate.setCandidateprofilestatus(candidateProfileStatus);
            candidate.setLead(lead);
            candidate.registerCandidate();
            candidateSignUpResponse.setStatus(CandidateSignUpResponse.STATUS_SUCCESS);
            Logger.info("Candidate successfully registered " + candidate);
        } else {
            candidateSignUpResponse.setStatus(CandidateSignUpResponse.STATUS_FAILURE);
        }
        return candidateSignUpResponse;
    }

    private static int isMinProfileComplete(Candidate candidate) {

        // Mandatory fields for min profile logic:
        // First Name, Mobile, Job Prefs, Locality Prefs
        // DOB, Gender, Timeshift preference
        // Experience, Education Level, Languages known
        // current salary (if currently employed)
        if(candidate.getCandidateFirstName() != null && candidate.getCandidateMobile() != null
                && (candidate.getLocalityPreferenceList() != null && candidate.getLocalityPreferenceList().size() > 0)
                && (candidate.getJobPreferencesList() != null && candidate.getJobPreferencesList().size() > 0)
                && candidate.getCandidateDOB() != null &&
                candidate.getCandidateGender() != null && candidate.getCandidateTotalExperience() != null
                && candidate.getCandidateEducation() != null
                && candidate.getTimeShiftPreference() != null && candidate.getLanguageKnownList().size() > 0){

            if(candidate.getCandidateTotalExperience() > 0){
                // !Fresher
                if(candidate.getCandidateLastWithdrawnSalary() != null && candidate.getCandidateLastWithdrawnSalary() > 0) {
                    // has lastWithDrawnSalary
                    return ServerConstants.CANDIDATE_MIN_PROFILE_COMPLETE;
                } else {
                    return ServerConstants.CANDIDATE_MIN_PROFILE_NOT_COMPLETE;
                }
            }
            return ServerConstants.CANDIDATE_MIN_PROFILE_COMPLETE;
        }
        return ServerConstants.CANDIDATE_MIN_PROFILE_NOT_COMPLETE;
    }

    private static void updateOthersBySupport(Candidate candidate, AddCandidateRequest request) {

        AddSupportCandidateRequest supportCandidateRequest = (AddSupportCandidateRequest) request;

        candidate.setCandidatePhoneType(supportCandidateRequest.getCandidatePhoneType());

        if(supportCandidateRequest.getCandidateEmail() != null)
            candidate.setCandidateEmail(supportCandidateRequest.getCandidateEmail());

        if(supportCandidateRequest.getCandidateMaritalStatus() != null)
                candidate.setCandidateMaritalStatus(supportCandidateRequest.getCandidateMaritalStatus());

        candidate.setCandidateAppointmentLetter(supportCandidateRequest.getCandidateAppointmentLetter());

        Boolean hasExperienceLetter = null;
        if(supportCandidateRequest.getCandidateExperienceLetter() != null){
            hasExperienceLetter = supportCandidateRequest.getCandidateExperienceLetter() == 1;
        }
        candidate.setCandidateExperienceLetter(hasExperienceLetter);

        candidate.setCandidateStatusDetail(getCandidateStatusDetail(supportCandidateRequest, candidate));

        candidate.setCandidateSalarySlip(supportCandidateRequest.getCandidateSalarySlip());

        if(supportCandidateRequest.getPastCompanyList() != null ){
            candidate.setJobHistoryList(getJobHistoryList(supportCandidateRequest.getPastCompanyList(), candidate));
        }

        if(supportCandidateRequest.getExpList() != null ){
            candidate.setCandidateExpList(getCandidateExpList(supportCandidateRequest.getExpList(), candidate));
        }
    }

    private static CandidateStatusDetail getCandidateStatusDetail(AddSupportCandidateRequest supportCandidateRequest, Candidate candidate) {
        CandidateStatusDetail candidateStatusDetail = candidate.getCandidateStatusDetail();
        if(candidateStatusDetail == null && supportCandidateRequest.getDeactivationStatus()) {
            candidateStatusDetail = new CandidateStatusDetail();
        }

        if(supportCandidateRequest != null
                && supportCandidateRequest.getDeactivationStatus()
                && supportCandidateRequest.getDeactivationReason() > 0
                && supportCandidateRequest.getDeactivationExpiryDate() != null )
        {
            // Add Canidate to candidateStatusDetail and Change candidateStatus to Cold
            candidate.setCandidateprofilestatus(CandidateProfileStatus.find.where().eq("profileStatusId", ServerConstants.CANDIDATE_STATE_DEACTIVE).findUnique());
            candidateStatusDetail.setReason(Reason.find.where().eq("ReasonId", supportCandidateRequest.getDeactivationReason()).findUnique());
            candidateStatusDetail.setStatusExpiryDate(supportCandidateRequest.getDeactivationExpiryDate());
            InteractionService.createInteractionForDeactivateCandidate(candidate.getCandidateUUId(), true);
            return candidateStatusDetail;
        }
        else if(candidate.getCandidateStatusDetail() != null && !supportCandidateRequest.getDeactivationStatus()) {
            // Remove from candidateStatusDetail and change candidateStatus to Active
            candidate.setCandidateprofilestatus(CandidateProfileStatus.find.where().eq("profileStatusId", ServerConstants.CANDIDATE_STATE_ACTIVE).findUnique());

            InteractionService.createInteractionForActivateCandidate(candidate.getCandidateUUId(), true);
            return null;
        }
        return null;
    }

    private static List<CandidateExp> getCandidateExpList(List<AddSupportCandidateRequest.ExpList> expList,
                                                          Candidate candidate)
    {
        List<CandidateExp> candidateExpList = new ArrayList<>();

        // Here List can be empty but not null
        for (AddSupportCandidateRequest.ExpList exp : expList){
            if(exp == null || exp.getJobExpResponseIdArray() == null ){
                continue;
            }

            JobExpQuestion jobExpQuestion = JobExpQuestion.find.where().eq("jobExpQuestionId", exp.getJobExpQuestionId()).findUnique();
            Query<JobExpResponse> query = JobExpResponse.find.query();
            query = query.select("*")
                    .where()
                    .eq("jobExpQuestionId", exp.getJobExpQuestionId())
                    .in("jobExpResponseOptionId", exp.getJobExpResponseIdArray())
                    .query();

            List<JobExpResponse> jobExpResponseList = query.findList();

            for(JobExpResponse jobExpResponse : jobExpResponseList){
                CandidateExp candidateExp = new CandidateExp();
                candidateExp.setCandidate(candidate);
                candidateExp.setJobExpQuestion(jobExpQuestion);
                candidateExp.setJobExpResponse(jobExpResponse);
                candidateExpList.add(candidateExp);
            }
        }

        return candidateExpList;
    }

    private static CandidateSignUpResponse updateEducationProfile(Candidate candidate,
                                                                  AddCandidateEducationRequest addCandidateEducationRequest) {

        CandidateSignUpResponse candidateSignUpResponse = new CandidateSignUpResponse();

        candidateSignUpResponse.setStatus(CandidateSignUpResponse.STATUS_SUCCESS);
        candidateSignUpResponse.setMinProfile(candidate.getIsMinProfileComplete());
        try {
            candidate.setCandidateEducation(getCandidateEducation(addCandidateEducationRequest.getCandidateEducationLevel(),
                    addCandidateEducationRequest.getCandidateDegree(),
                    addCandidateEducationRequest.getCandidateEducationCompletionStatus(),
                    addCandidateEducationRequest.getCandidateEducationInstitute(),
                    candidate));
        }
        catch(Exception e){
            candidateSignUpResponse.setStatus(CandidateSignUpResponse.STATUS_FAILURE);
            Logger.info("Exception while setting education details");
            e.printStackTrace();
        }

        return candidateSignUpResponse;
    }

    private static CandidateSignUpResponse updateSkillProfile(Candidate candidate,
                                                              AddCandidateExperienceRequest addCandidateExperienceRequest) {

        CandidateSignUpResponse candidateSignUpResponse = new CandidateSignUpResponse();
        candidateSignUpResponse.setMinProfile(candidate.getIsMinProfileComplete());

        // initialize to default value. We will change this value later if any exception occurs
        candidateSignUpResponse.setStatus(CandidateSignUpResponse.STATUS_SUCCESS);

        candidate.setCandidateTotalExperience(addCandidateExperienceRequest.getCandidateTotalExperience());

        candidate.setCandidateIsEmployed(addCandidateExperienceRequest.getCandidateIsEmployed());


        try {
            candidate.setMotherTongue(Language.find.where().eq("languageId", addCandidateExperienceRequest.getCandidateMotherTongue()).findUnique());
        } catch (NonUniqueResultException e){
            candidateSignUpResponse.setStatus(CandidateSignUpResponse.STATUS_FAILURE);
            Logger.info("Exception while setting mother tongue " + e.getMessage());
            e.printStackTrace();
        }

        /* TODO: remove this after changing flow to use support flow for multiple company name*/
        candidate.setJobHistoryList(getCurrentCompanyNameAsList(addCandidateExperienceRequest, candidate));

        candidate.setCandidateLastWithdrawnSalary(addCandidateExperienceRequest.getCandidateLastWithdrawnSalary());

        candidate.setCandidateSkillList(getCandidateSkillList(addCandidateExperienceRequest.getCandidateSkills(),
                candidate));

        candidate.setLanguageKnownList(getLanguagesKnown(addCandidateExperienceRequest.getCandidateLanguageKnown(), candidate));

        return candidateSignUpResponse;
    }

    private static List<JobHistory> getCurrentCompanyNameAsList(AddCandidateExperienceRequest candidateCurrentCompany,
                                                                Candidate candidate)
    {
        List<JobHistory> jobHistoryList = candidate.getJobHistoryList();
        JobRole jobRole = JobRole.find.where().eq("jobRoleId", candidateCurrentCompany.getCandidateCurrentJobRoleId()).findUnique();
        if(jobRole == null){
            return null;
        }

        if(jobHistoryList == null || jobHistoryList.isEmpty()){
            // create new currentJob entry
            jobHistoryList = new ArrayList<>();
            JobHistory jobHistory = new JobHistory();
            jobHistory.setCurrentJob(true);
            jobHistory.setCandidatePastCompany(candidateCurrentCompany.getCandidateCurrentCompany());
            jobHistory.setCandidate(candidate);
            jobHistory.setJobRole(jobRole);
            Logger.info(candidateCurrentCompany.getCandidateCurrentCompany() + " + " + candidateCurrentCompany.getCandidateCurrentJobRoleId() + " ============");
            jobHistoryList.add(jobHistory);
        } else {
            // update currentJob entry
            Boolean flag = false;
            for(JobHistory jobHistory: jobHistoryList){
                if(jobHistory.getCurrentJob() != null && jobHistory.getCurrentJob()){
                    flag = true;
                    jobHistory.setJobRole(jobRole);
                    jobHistory.setCurrentJob(true);
                    jobHistory.setCandidatePastCompany(candidateCurrentCompany.getCandidateCurrentCompany());
                }
            }
            if(!flag){
                JobHistory jobHistory = new JobHistory();
                jobHistory.setCurrentJob(true);
                jobHistory.setCandidatePastCompany(candidateCurrentCompany.getCandidateCurrentCompany());
                jobHistory.setCandidate(candidate);
                jobHistory.setJobRole(jobRole);
                Logger.info(candidateCurrentCompany.getCandidateCurrentCompany() + " + " + candidateCurrentCompany.getCandidateCurrentJobRoleId() + " ============");
                jobHistoryList.add(jobHistory);
            }
        }

        return jobHistoryList;
    }

    private static CandidateSignUpResponse updateBasicProfile(Candidate candidate, AddCandidateRequest request) {

        CandidateSignUpResponse candidateSignUpResponse = new CandidateSignUpResponse();

        // initialize to default value. We will change this value later if any exception occurs
        candidateSignUpResponse.setStatus(CandidateSignUpResponse.STATUS_SUCCESS);

        /// Basic Profile Section Starts
        candidate.setCandidateFirstName(request.getCandidateFirstName());
        candidate.setCandidateLastName(request.getCandidateSecondName());

        if(request.getCandidateSecondMobile()!= null){
            candidate.setCandidateSecondMobile(request.getCandidateSecondMobile());
        }

        if(request.getCandidateThirdMobile()!= null){
            candidate.setCandidateThirdMobile(request.getCandidateThirdMobile());
        }

        // this applies while saving basic profile from android app
        if(request.getCandidateHomeLocality() == null) {
            if(candidate.getLocality() != null) {
                request.setCandidateHomeLocality((int) candidate.getLocality().getLocalityId());
            }
        }

        try {
            candidate.setLocality(Locality.find.where().eq("localityId", request.getCandidateHomeLocality()).findUnique());
        } catch (NonUniqueResultException e) {
            Logger.info(" Exception while setting home locality");
            e.printStackTrace();
        }

        if(request.getCandidateDob() != null) {
            candidate.setCandidateDOB(request.getCandidateDob());
        }

        if(request.getCandidateGender() != null) {
            candidate.setCandidateGender(request.getCandidateGender());
        }

        if (request.getCandidateTimeShiftPref() != null) {
            candidate.setTimeShiftPreference(getTimeShiftPref(request.getCandidateTimeShiftPref(), candidate));
        }

        if (request.getCandidateAssetList() != null  && request.getCandidateAssetList().size() > 0) {
            candidate.setCandidateAssetList(getAssetList(request.getCandidateAssetList() , candidate));
        }

        if(request.getCandidateIdProofList() != null ){
            candidate.setIdProofReferenceList(getCandidateIdProofList(request.getCandidateIdProofList(), candidate));
        }

        return candidateSignUpResponse;
    }

    private static List<CandidateAsset> getAssetList(List<Integer> candidateAssetList, Candidate candidate) {
        ArrayList<CandidateAsset> response = new ArrayList<>();
        List<Asset> assetList = Asset.find.where().in("assetId", candidateAssetList).findList();

        if(assetList == null || assetList.isEmpty()) {
            return response;
        }

        for(Asset asset : assetList) {
            CandidateAsset candidateAsset = new CandidateAsset();
            if(asset == null) {
                continue;
            }
            candidateAsset.setAsset(asset);
            candidateAsset.setCandidate(candidate);
            response.add(candidateAsset);
        }
        return response;
    }

    private static List<LanguageKnown> getLanguagesKnown(List<CandidateKnownLanguage> languagesList, Candidate candidate)
    {
        List<LanguageKnown> languageKnownList = new ArrayList<>();
        for(CandidateKnownLanguage candidateKnownLanguage : languagesList) {
            LanguageKnown languageKnown = new LanguageKnown();
            Language language = Language.find.where().eq("LanguageId", candidateKnownLanguage.getId()).findUnique();

            if(language == null) {
                Logger.warn("Language static table is empty for:" + candidateKnownLanguage.getId());
                return null;
            }

            languageKnown.setLanguage(language);
            languageKnown.setUnderstanding(candidateKnownLanguage.getU()); // understanding
            languageKnown.setReadWrite(candidateKnownLanguage.getRw());
            languageKnown.setVerbalAbility(candidateKnownLanguage.getS());
            languageKnownList.add(languageKnown);
        }
        return languageKnownList;
    }

    private static void triggerOtp(Candidate candidate, CandidateSignUpResponse candidateSignUpResponse, int channelType) {
        int randomPIN = generateOtp();
        SmsUtil.sendOTPSms(randomPIN, candidate.getCandidateMobile(), channelType);

        candidateSignUpResponse.setCandidateId(candidate.getCandidateId());
        candidateSignUpResponse.setCandidateFirstName(candidate.getCandidateFirstName());
        candidateSignUpResponse.setOtp(randomPIN);
        candidateSignUpResponse.setStatus(CandidateSignUpResponse.STATUS_SUCCESS);
    }

    private static CandidateEducation getCandidateEducation(Integer educationLevelId,
                                                            Integer degreeId,
                                                            Integer degreeCompletionStatus,
                                                            String institute,
                                                            Candidate candidate)
    {
        CandidateEducation response  = candidate.getCandidateEducation();
        Education education = Education.find.where().eq("educationId", educationLevelId).findUnique();
        Degree degree = Degree.find.where().eq("degreeId", degreeId).findUnique();

        if(response == null){
            response = new CandidateEducation();
            response.setCandidate(candidate);
        }

        if (education == null) {
            Logger.warn("Unable to create education record for education id " + educationLevelId + " and degree id " + degreeId);
            return null;
        }

        response.setEducation(education);

        if(degree != null) {
            response.setDegree(degree);
        }

        if(degreeCompletionStatus != null) {
            response.setCandidateEducationCompletionStatus(degreeCompletionStatus);
        }

        if(!Strings.isNullOrEmpty(institute)) {
            response.setCandidateLastInstitute(institute);
        }

        return response;
    }

    private static List<CandidateSkill> getCandidateSkillList(List<CandidateSkills> candidateSkillList,
                                                              Candidate candidate)
    {
        List<CandidateSkill> response = new ArrayList<>();
        for(CandidateSkills item: candidateSkillList){
            CandidateSkill candidateSkill = new CandidateSkill();
            Skill skill = Skill.find.where().eq("skillId", item.getId()).findUnique();

            if(skill == null) {
                Logger.info("skill static table empty");
                return null;
            }
            candidateSkill.setCandidate(candidate);
            candidateSkill.setSkill(skill);
            candidateSkill.setCandidateSkillResponse(item.getAnswer());
            response.add(candidateSkill);
        }

        return response;
    }

    private static List<JobHistory> getJobHistoryList(List<AddSupportCandidateRequest.PastCompany> pastCompanyList,
                                                      Candidate candidate)
    {
        List<JobHistory> response = new ArrayList<>();
        // TODO: loop through the req and then store it in List
        for(AddSupportCandidateRequest.PastCompany pastCompany: pastCompanyList){
            if((pastCompany == null) || (pastCompany.getJobRoleId() == null)){
                Logger.info("Past company name not mentioned");
            } else{
                JobRole jobRole = JobRole.find.where().eq("jobRoleId", pastCompany.getJobRoleId()).findUnique();
                if(jobRole != null && pastCompany.getCompanyName()!= null && !pastCompany.getCompanyName().equals("")){
                    JobHistory jobHistory = new JobHistory();
                    jobHistory.setCandidate(candidate);
                    jobHistory.setCandidatePastCompany(pastCompany.getCompanyName());
                    jobHistory.setJobRole(jobRole);
                    jobHistory.setCurrentJob(pastCompany.getCurrent());

                    response.add(jobHistory);
                }
            }
        }
        return response;
    }

    private static TimeShiftPreference getTimeShiftPref(String newTimeShiftPreference,
                                                        Candidate candidate)
    {
        TimeShiftPreference timeShiftPreference = null;

        if(newTimeShiftPreference == null){
            return null;
        } else {

            TimeShift staticTimeShiftRecord =
                    TimeShift.find.where().eq("timeShiftId", newTimeShiftPreference).findUnique();

            if(staticTimeShiftRecord == null) {
                Logger.warn("Timeshift static table empty for Pref: " + newTimeShiftPreference);
                return null;
            }

            timeShiftPreference = candidate.getTimeShiftPreference();
            if (timeShiftPreference == null) {
                timeShiftPreference = new TimeShiftPreference();
                timeShiftPreference.setCandidate(candidate);
            }
            timeShiftPreference.setTimeShift(staticTimeShiftRecord);
        }

        return timeShiftPreference;
    }

    private static void createAndSaveDummyAuthFor(Candidate candidate, Boolean isSupport) {
        // create dummy auth
        Auth authToken = new Auth(); // constructor instantiate createtimestamp, updatetimestamp, sessionid, authpasswordsalt
        String dummyPassword = String.valueOf(Util.randomLong());
        authToken.setAuthStatus(ServerConstants.CANDIDATE_STATUS_NOT_VERIFIED);
        authToken.setCandidateId(candidate.getCandidateId());
        authToken.setPasswordMd5(Util.md5(dummyPassword + authToken.getPasswordSalt()));
        authToken.save();

        if(isSupport){
            SmsUtil.sendWelcomeSmsFromSupport(candidate.getCandidateFirstName(), candidate.getCandidateMobile(), dummyPassword);
            Logger.info("Dummy auth created + otp triggered + auth saved for " + candidate.getCandidateMobile());
        }
    }

    private static List<IDProofReference> getCandidateIdProofList(
            List<AddSupportCandidateRequest.IdProofWithValue> idProofList, Candidate candidate)
    {
        /*ArrayList<IDProofReference> response = new ArrayList<>();

        UpdateCandidateDocument updateDocumentReq = new UpdateCandidateDocument();
        List<UpdateCandidateDocument.IdProofWithIdNumber> newList = new ArrayList<UpdateCandidateDocument.IdProofWithIdNumber>();

        for(AddSupportCandidateRequest.IdProofWithValue idf : idProofList) {

            UpdateCandidateDocument.IdProofWithIdNumber idProofWithIdNumber = new UpdateCandidateDocument.IdProofWithIdNumber();
            idProofWithIdNumber.setIdNumber(idf.getIdProofValue());
            idProofWithIdNumber.setIdProofId(idf.getIdProofId());
            newList.add(idProofWithIdNumber);
        }

        updateDocumentReq.setIdProofWithIdNumberList(newList);

        return updateCandidateDocument(candidate, updateDocumentReq);*/
        ArrayList<IDProofReference> response = new ArrayList<>();
        for (AddSupportCandidateRequest.IdProofWithValue idf : idProofList) {
            IDProofReference idProofReference = new IDProofReference();
            IdProof idProof = IdProof.find.where().eq("idProofId", idf.getIdProofId()).findUnique();

            if(idProof == null) {
                return null;
            }

            idProofReference.setIdProof(idProof);
            idProofReference.setIdProofNumber(idf.getIdProofValue());
            idProofReference.setCandidate(candidate);
            response.add(idProofReference);
        }

        return response;
    }

    public static Integer updateAndroidToken(String token, String candidateId) {
        Logger.info("Updating token for " + candidateId);
        Candidate candidate = Candidate.find.where().eq("CandidateId", candidateId).findUnique();
        if(candidate != null){
            candidate.setCandidateAndroidToken(token);
            candidate.update();
            return 1;
        }
        return 0;
    }

    public static int logoutTrudroidCandidate(LogoutCandidateRequest logoutCandidateRequest) {
        Candidate candidate = Candidate.find.where().eq("CandidateId", logoutCandidateRequest.getCandidateId()).findUnique();
        if(candidate != null){
            Logger.info("Clearing android token for candidate and logging out from app");
            candidate.setCandidateAndroidToken(null);
            candidate.update();
            return 1;
        }
        return 0;
    }

    public static int addTrudroidFeedback(AddFeedbackRequest addFeedbackRequest) {
        Candidate candidate = Candidate.find.where().eq("CandidateId", addFeedbackRequest.getCandidateId()).findUnique();
        if(candidate != null){
            CandidateFeedback candidateFeedback = new CandidateFeedback();
            candidateFeedback.setCandidate(candidate);
            candidateFeedback.setFeedbackComments(addFeedbackRequest.getFeedbackComment());
            candidateFeedback.setFeedbackRating(addFeedbackRequest.getRatingScore());
            candidateFeedback.setFeedbackChannel(INTERACTION_CHANNEL_CANDIDATE_ANDROID);
            candidateFeedback.save();

            for(FeedbackReasonObject feedbackReasonObject : addFeedbackRequest.getFeedbackReasonObjectList()){
                FeedbackToReason feedbackToReason = new FeedbackToReason();
                feedbackToReason.setCandidateFeedback(candidateFeedback);
                feedbackToReason.setCandidateFeedbackReason(CandidateFeedbackReason.find.where()
                        .eq("reason_id", feedbackReasonObject.getReasonId()).findUnique());
                feedbackToReason.save();
            }

            Logger.info("Feedback added successfully");
            return 1;
        }
        return 0;
    }

    public static Boolean uploadResume(File resume, String fileName, Long candidateId) {

        JSONObject responseJson = null;
        JSONObject profileJson = null;
        String personId;

        Candidate existingCandidate = null;
        if(candidateId > 0) {
            // we already have an existing candidate for this resume
            existingCandidate = Candidate.find.byId(Long.toString(candidateId));
            if(existingCandidate == null) candidateId = 0L;
            else {
                // update filename
                fileName = createCandidateResumeFilename(Long.toString(candidateId),existingCandidate.getCandidateFirstName(),fileName);
            }
        }

        // get handle to HireWand
        HireWandService hw = HireWandService.get();
        try {
            hw.login("avishek@trujobs.in","hirewandswatkats");
            hw.setCallback("http://trujobs.in/receive-parsed-resume");
        } catch (HWHTTPException e) {
            e.printStackTrace();
            return Boolean.FALSE;
        }

        HashMap paramMap = new HashMap();
        try {
            // stream of resume
            InputStream stream = new FileInputStream(resume);
            // Create a HashMap with Resume's filename, Resume's Stream
            paramMap.put("filename",fileName);
            paramMap.put("resume",stream);
            Logger.info("Sending to HireWand with filename = "+fileName);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return Boolean.FALSE;
        }

        // Upload resume to HireWand
        String response = null;
        try {
            response = hw.call("upload",paramMap);
        } catch (InvalidRequestException | HWHTTPException e) {
            e.printStackTrace();
            return Boolean.FALSE;
        }

        Logger.info("response= "+response);
            JSONObject result = null;
            try {
                result = new JSONObject(String.valueOf(response));
            } catch (JSONException e) {
                e.printStackTrace();
                return Boolean.FALSE;
            }

        try {

            Logger.info("result.get(\"status\")="+result.getString("status"));

            if(result.getString("status").equalsIgnoreCase("success")){

                Logger.info("Success in uploading to HireWand");

                // Get Unique HireWand Id
                personId = String.valueOf(result.getString("personid"));
                Logger.info("personId="+personId);

                    String awsName;
                    if(candidateId > 0){
                        // store resume in AWS with standard naming (candidateId_firstName_TimeStamp)
                        awsName = fileName;
                    }
                    else {
                        // store resume in AWS with person Id
                        awsName = personId;
                        awsName += fileName.substring(fileName.lastIndexOf("."));
                    }
                    Logger.info("AWS upload fileName ="+awsName);

                    String path = uploadResumeToAWS(resume,awsName);
                    if(path == "") {
                        // Resume could not be uploaded to S3
                        return  Boolean.FALSE;
                    }
                    else {

                        // Resume uploaded into S3
                        Logger.info("AWS file Path = "+path);
                        path = "https://s3.amazonaws.com/"+path;

                        // Need to make an entry in the CandidateResume table
                        CandidateResumeService candidateResumeService = new CandidateResumeService();
                        CandidateResumeRequest candidateResumeRequest = new CandidateResumeRequest();

                        // Prepare the Request
                        if(candidateId > 0) candidateResumeRequest.setCandidate(candidateId);
                        candidateResumeRequest.setCreatedBy("Self");
                        candidateResumeRequest.setExternalKey(personId);
                        candidateResumeRequest.setFilePath(path);
                        //candidateResumeRequest.setParsedResume(profileJson.toString());

                        // Create the entry
                        TruResponse candidateResumeResponse = (TruResponse) candidateResumeService.create(candidateResumeRequest);

                        if(candidateResumeResponse.getStatus() == TruResponse.STATUS_FAILURE) {
                            // error reported, delete the AWS entry
                            removeResumeFromAws(path.replace("https://s3.amazonaws.com/trujobs.in/",""));
                            return Boolean.FALSE;
                        }

                    }

                }

            }
            catch (JSONException e){
                e.printStackTrace();
                return Boolean.FALSE;
            }

        return Boolean.TRUE;

    }


    public static AddSupportCandidateRequest mapFromHWToCandidate(HireWandResponse.Profile profile, Candidate existingCandidate){
        Logger.info("Entering mapFromHWToCandidate");
        AddSupportCandidateRequest addSupportCandidateRequest = new AddSupportCandidateRequest();

        // initialize all lists (else, we're looking at NPEs during creation)
        List<Integer> candidateIdProof = new ArrayList<>();
        List<CandidateSkills> candidateSkills = new ArrayList<>();
        addSupportCandidateRequest.setCandidateSkills(candidateSkills);
        List<CandidateKnownLanguage> candidateLanguageKnown = new ArrayList<>();
        addSupportCandidateRequest.setCandidateLanguageKnown(candidateLanguageKnown);
        addSupportCandidateRequest.setDeactivationStatus(Boolean.FALSE);

        if(profile == null) return addSupportCandidateRequest;
        else if (existingCandidate == null){

            // candidate firstName and secondName
            String candidateName = profile.Name;
            if(candidateName != null && StringUtils.isAlphaSpace(candidateName)){
                candidateName = candidateName.trim();
                String[] nameArray = candidateName.split("\\s+");
                addSupportCandidateRequest.setCandidateFirstName(nameArray[0]);
                candidateName = "";
                for(Integer i=1; i < nameArray.length;i++) {candidateName += " "+nameArray[i];}
                candidateName = candidateName.trim();
                addSupportCandidateRequest.setCandidateSecondName(candidateName);
            }

            // candidate Mobile
            // get all phone number(s) for this candidate
            for (int i = 0; i < profile.PhoneNos.size(); i++) {
                if(i == 0){
                    addSupportCandidateRequest.setCandidateMobile(StringUtils.right(profile.PhoneNos.get(i),10));
                }
                else if(i == 1) {
                    addSupportCandidateRequest.setCandidateSecondMobile(StringUtils.right(profile.PhoneNos.get(i),10));
                }
                else if(i == 2) {
                    addSupportCandidateRequest.setCandidateThirdMobile(StringUtils.right(profile.PhoneNos.get(i),10));
                }
                else break;
            }

        }
        else if (existingCandidate == null) {
            addSupportCandidateRequest.setCandidateMobile(existingCandidate.getCandidateMobile());
        }

        Logger.info("Exiting mapFromHWToCandidate");

        return addSupportCandidateRequest;
    }

    public static String uploadResumeToAWS(File resume, String fileName){

        Logger.info("Entering uploadResumeToAWS with fileName = "+fileName);

        try{
            AWSCredentials credentials = new BasicAWSCredentials(
                    play.Play.application().configuration().getString("aws.accesskey"),
                    play.Play.application().configuration().getString("aws.secretAccesskey"));

            AmazonS3 s3client = new AmazonS3Client(credentials);
            String bucketName = ServerConstants.AWS_S3_BUCKET_NAME;
            String path = ServerConstants.AWS_S3_CANDIDATE_RESUME_FOLDER+"/"+ fileName;

            PutObjectResult putObjectResult = s3client.putObject(new PutObjectRequest(bucketName, path, resume)
                    .withCannedAcl(CannedAccessControlList.PublicRead));
            if (putObjectResult.getETag() != null && putObjectResult.getETag().length() > 0) {
                Logger.info("Resume uploaded successfully into AWS. ETag= "+putObjectResult.getETag());
                return bucketName+"/"+path;
            }
            else {
                Logger.info("Resume upload into AWS failed. No ETag returned");
                return "";
            }
        } catch (Exception e){
            Logger.info("Exception while uploading resume into AWS " + e.getMessage());
            return "";
        }

    }

    public static void copyResumeinAws(String fromKey, String toKey){
        try{
            AWSCredentials credentials = new BasicAWSCredentials(
                    play.Play.application().configuration().getString("aws.accesskey"),
                    play.Play.application().configuration().getString("aws.secretAccesskey"));

            AmazonS3 s3client = new AmazonS3Client(credentials);
            String bucketName = ServerConstants.AWS_S3_BUCKET_NAME;
            s3client.copyObject(bucketName,(ServerConstants.AWS_S3_CANDIDATE_RESUME_FOLDER+"/"+fromKey),bucketName,(ServerConstants.AWS_S3_CANDIDATE_RESUME_FOLDER+"/"+toKey));
        } catch (Exception e){
            Logger.info("Exception while copying resume from AWS with key ="+(ServerConstants.AWS_S3_CANDIDATE_RESUME_FOLDER+"/"+fromKey)+" Exception: " + e);
        }
    }

    public static void removeResumeFromAws(String key) {
        try{
            AWSCredentials credentials = new BasicAWSCredentials(
                    play.Play.application().configuration().getString("aws.accesskey"),
                    play.Play.application().configuration().getString("aws.secretAccesskey"));

            AmazonS3 s3client = new AmazonS3Client(credentials);
            String bucketName = ServerConstants.AWS_S3_BUCKET_NAME;
            s3client.deleteObject(bucketName,(ServerConstants.AWS_S3_CANDIDATE_RESUME_FOLDER+"/"+key));
        } catch (Exception e){
            Logger.info("Exception while deleting resume from AWS with key ="+(ServerConstants.AWS_S3_CANDIDATE_RESUME_FOLDER+"/"+key)+" Exception: " + e);
        }
    }

    public static Boolean updateResume(String personId, HireWandResponse.Profile profile, Boolean duplicate){

        // get the existing entry from CandidateResume
        CandidateResume candidateResume = CandidateResume.find.where().eq("external_key",personId).findUnique();
        Candidate candidate = null;
        Boolean isNew = Boolean.FALSE;

        // check if candidate is already known
        if(candidateResume.getCandidate() != null){
            candidate = candidateResume.getCandidate();
            Logger.info("Updating resume for existing candidate with ID = "+candidate.getCandidateId());
        }
        else{
            //get candidate mobile number
            // get all phone number(s) for this candidate
            List<String> mobileNos = new ArrayList<>();

            for (int j = 0; j < profile.PhoneNos.size(); j++) {
                mobileNos.add(StringUtils.right(profile.PhoneNos.get(j).toString(),10));
                Logger.info("mobileNos(j)="+mobileNos.get(j));
            }

            // primary key missing!!! Cannot create candidate
            if(mobileNos.size() == 0) return Boolean.FALSE;

            // get first matching candidate
            for(String m:mobileNos){
                candidate = isCandidateExists(m);
                if(candidate != null) {
                    Logger.info("Candidate found for mobile no = "+m);
                    break;
                }
                Logger.info("Candidate not found for mobile no = "+m);
            }
        }

        // no candidate found... Create
        if(candidate == null) {
            isNew = Boolean.TRUE;
            Logger.info("Attempting to create new candidate ...");
        }
        else {
            Logger.info("Attempting to update existing candidate ...");
            isNew = Boolean.FALSE;
        }

        // map to candidate request
        AddSupportCandidateRequest addSupportCandidateRequest = mapFromHWToCandidate(profile, candidate);

        // create/update candidate
        Logger.info("About to call CandidateService.createCandidateProfile");
        CandidateSignUpResponse candidateSignUpResponse = CandidateService.createCandidateProfile(addSupportCandidateRequest,
                InteractionConstants.INTERACTION_CHANNEL_SUPPORT_WEBSITE,
                ServerConstants.UPDATE_ALL_BY_SUPPORT);

        Long candidateId = 0L;
        String candidateName = "";

        // get candidate Id, Name
        candidateId = candidateSignUpResponse.getCandidateId();
        candidateName = candidateSignUpResponse.getCandidateFirstName();
        Logger.info("New/Updated candidateId ="+candidateId);

        if(candidateResume.getCandidate() == null){
            // recreate existing AWS object key
            String existingKey = candidateResume.getFilePath();
            existingKey = existingKey.replace("https://s3.amazonaws.com/trujobs.in/","");
            // create new key
            String awsName = createCandidateResumeFilename(Long.toString(candidateId),candidateName,existingKey);//"candidateResumes/"+candidateId+"_"+candidateName;
            // rename (copy and delete) resume in AWS
            Logger.info("AWS replacing old key="+existingKey+ " with new key="+awsName);
            copyResumeinAws(existingKey,awsName);
            removeResumeFromAws(existingKey);
        }

        // Update CandidateResume Object
        CandidateResumeRequest candidateResumeRequest = new CandidateResumeRequest();
        candidateResumeRequest.setCandidateResumeId(candidateResume.getCandidateResumeId());
        candidateResumeRequest.setParsedResume(profile.ProfileJSON);

        List<String> changedFields;
        // check if candidate is already known
        if(candidateResume.getCandidate() == null){
            // not known - need to set candidate reference
            if(isNew) candidateResumeRequest.setCandidate(candidateId);
            else candidateResumeRequest.setCandidate(candidateResume.getCandidate().getCandidateId());
            changedFields = new ArrayList<String>(Arrays.asList("parsedResume","candidate"));
        }
        else{
            // already known - only updating parsed resume string
            changedFields = new ArrayList<String>(Arrays.asList("parsedResume"));
        }

        candidateResumeRequest.setChangedFields(changedFields);
        CandidateResumeService candidateResumeService = new CandidateResumeService();
        Logger.info("UpdateResume(): About to call candidateResumeService.update for id = "+candidateResume.getCandidateResumeId()+" referencing Candidate Id = "+candidateId);
        if(candidateResumeService.update(candidateResumeRequest).getStatus() == TruResponse.STATUS_SUCCESS) return Boolean.TRUE;
        else return Boolean.FALSE;

    }

    public static String createCandidateResumeFilename(String candidateId, String candidateName, String fileName){
        String name = candidateId+"_"+candidateName;
        name += "_"+new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss").format(new Date());
        name += fileName.substring(fileName.lastIndexOf("."));
        return name;
    }

}
