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
import api.http.httpResponse.ongrid.OngridAadhaarVerificationResponse;
import com.avaje.ebean.Query;
import com.google.api.client.repackaged.com.google.common.base.Strings;
import controllers.businessLogic.ongrid.AadhaarService;
import dao.staticdao.IdProofDAO;
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
import play.Logger;

import javax.persistence.NonUniqueResultException;
import java.sql.Timestamp;
import java.util.*;

import static controllers.businessLogic.InteractionService.*;
import static controllers.businessLogic.LeadService.createOrUpdateConvertedLead;
import static models.util.Util.generateOtp;
import static play.libs.Json.toJson;
import static play.mvc.Controller.session;

import controllers.businessLogic.InteractionService.InteractionChannelType;

/**
 * Created by batcoder1 on 3/5/16.
 */
public class CandidateService
{
    private static CandidateSignUpResponse createNewCandidate(Candidate candidate, Lead lead) {

        CandidateSignUpResponse candidateSignUpResponse = new CandidateSignUpResponse();
        CandidateProfileStatus candidateProfileStatus =
                CandidateProfileStatus.find.where().eq("profileStatusId", ServerConstants.CANDIDATE_STATE_ACTIVE).findUnique();

        if(candidateProfileStatus != null){
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

    public static Candidate isCandidateExists(String mobile) {
        try{
            Candidate existingCandidate = Candidate.find.where().eq("candidateMobile",
                    FormValidator.convertToIndianMobileFormat(mobile)).findUnique();
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
                                                          InteractionChannelType channelType,
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
                if(channelType == InteractionChannelType.SELF_ANDROID || channelType == InteractionChannelType.SELF){
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

                    if(candidate.getLocality() != null){
                        Locality locality = Locality.find.where().eq("localityId", candidateSignUpRequest.getCandidateHomeLocality()).findUnique();
                        if(locality != null){
                            candidate.setLocality(locality);
                            resetLocalityAndJobPref(candidate, getCandidateLocalityPreferenceList(localityList, candidate), getCandidateJobPreferenceList(jobsList, candidate));
                        }
                    }

                    // triggers when candidate is self created
                    if(channelType == InteractionChannelType.SELF_ANDROID || channelType == InteractionChannelType.SELF){
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
            if(channelType == InteractionChannelType.SELF){
                // candidate sign up via website
                createInteractionForSignUpCandidateViaWebsite(objectAUUId, result, interactionTypeVal);
            } else if(channelType == InteractionChannelType.SELF_ANDROID) {
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
                                                                 InteractionChannelType channelType,
                                                                 int profileUpdateFlag) {
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
            if(request.getCandidateSecondMobile()!= null){
                candidate.setCandidateSecondMobile(request.getCandidateSecondMobile());
                Logger.info("Candidate with 2nd mobile number : " + request.getCandidateSecondMobile() + " added/updated");
            }
            if(request.getCandidateThirdMobile()!= null){
                candidate.setCandidateThirdMobile(request.getCandidateThirdMobile());
                Logger.info("Candidate with 3rd mobile number : " + request.getCandidateThirdMobile() + " added/updated");
            }

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
            } else{
                localityList.add((int) candidate.getLocality().getLocalityId());
                candidate.setLocalityPreferenceList(getCandidateLocalityPreferenceList(localityList, candidate));
            }

            if(request.getCandidateFirstName()!= null && !request.getCandidateFirstName().trim().isEmpty()) {
                candidate.setCandidateFirstName(request.getCandidateFirstName());
            }
            if(request.getCandidateSecondName()!= null) {
                candidate.setCandidateLastName(request.getCandidateSecondName());
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
            if(channelType == InteractionChannelType.SUPPORT || channelType == InteractionChannelType.PARTNER){
                // TODO: differentiate between in/out call
                Boolean isSupport = false;
                if(channelType == InteractionChannelType.SUPPORT){
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

        if(channelType == InteractionChannelType.SUPPORT || channelType == InteractionChannelType.PARTNER){
            // update additional fields that are part of the support request
            updateOthersBySupport(candidate, request);
            AddSupportCandidateRequest supportCandidateRequest = (AddSupportCandidateRequest) request;

            if(channelType == InteractionChannelType.SUPPORT){
                createdBy = session().get("sessionUsername");
                interactionNote = supportCandidateRequest.getSupportNote();
                if(isNewCandidate) {
                    interactionType = InteractionConstants.INTERACTION_TYPE_CANDIDATE_PROFILE_CREATED;
                    interactionResult = InteractionConstants.INTERACTION_RESULT_NEW_CANDIDATE_SUPPORT;
                } else{
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
                } else{
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

            if(channelType == InteractionChannelType.SELF_ANDROID){
                InteractionService.createInteractionForCreateCandidateProfileViaAndroidByCandidate(objAUUId, objBUUId, objBType,
                        interactionType, interactionNote, interactionResult);
            } else{
                InteractionService.createInteractionForCreateCandidateProfileViaWebsiteByCandidate(objAUUId, objBUUId, objBType,
                        interactionType, interactionNote, interactionResult);
            }
        }

        candidate.update();
        Logger.info("Candidate with mobile " +  candidate.getCandidateMobile() + " created/updated successfully");
        candidateSignUpResponse.setStatus(CandidateSignUpResponse.STATUS_SUCCESS);
        candidateSignUpResponse.setMinProfile(candidate.getIsMinProfileComplete());

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

        Logger.info("Handling updationg of additional fields by support");

        AddSupportCandidateRequest supportCandidateRequest = (AddSupportCandidateRequest) request;

/*
        try{
            candidate.setLocality(Locality.find.where().eq("localityId", supportCandidateRequest.getCandidateHomeLocality()).findUnique());
        } catch(Exception e){
            Logger.info(" Exception while setting home locality");
            e.printStackTrace();
        }
*/

        try{
            candidate.setCandidatePhoneType(supportCandidateRequest.getCandidatePhoneType());
        } catch(Exception e){
            Logger.info(" Exception while setting phone type");
            e.printStackTrace();
        }

        try{
            if(supportCandidateRequest.getCandidateEmail() != null)
                candidate.setCandidateEmail(supportCandidateRequest.getCandidateEmail());
        } catch(Exception e){
            Logger.info(" Exception while setting candidate email");
            e.printStackTrace();
        }

        try{
            if(supportCandidateRequest.getCandidateMaritalStatus() != null)
                candidate.setCandidateMaritalStatus(supportCandidateRequest.getCandidateMaritalStatus());
        } catch(Exception e){
            Logger.info(" Exception while setting marital status");
            e.printStackTrace();
        }

        try{
            candidate.setCandidateAppointmentLetter(supportCandidateRequest.getCandidateAppointmentLetter());
        } catch(Exception e){
            Logger.info(" Exception while setting appointment letter flag");
            e.printStackTrace();
        }
        try{
            Boolean hasExperienceLetter = null;
            if(supportCandidateRequest.getCandidateExperienceLetter() != null){
                hasExperienceLetter = supportCandidateRequest.getCandidateExperienceLetter() == 1;
            }
            candidate.setCandidateExperienceLetter(hasExperienceLetter);
        } catch(Exception e){
            Logger.info(" Exception while setting exp letter flag");
            e.printStackTrace();
        }

        try{
            candidate.setCandidateStatusDetail(getCandidateStatusDetail(supportCandidateRequest, candidate));
        } catch(Exception e){
            Logger.info(" Exception while setting CandidateStatusDetail data");
            e.printStackTrace();
        }

        try{
            candidate.setCandidateSalarySlip(supportCandidateRequest.getCandidateSalarySlip());
        } catch(Exception e){
            Logger.info(" Exception while setting salary slip flag type");
            e.printStackTrace();
        }

        if(supportCandidateRequest.getPastCompanyList() != null ){
            candidate.setJobHistoryList(getJobHistoryListFromAddSupportCandidate(supportCandidateRequest.getPastCompanyList(), candidate));
        }

//        if(supportCandidateRequest.getCandidateIdProofList() != null ){
//            candidate.setIdProofReferenceList(getCandidateIdProofListFromAddSupportCandidate(supportCandidateRequest.getCandidateIdProofList(), candidate));
//        }

        if(supportCandidateRequest.getExpList() != null ){
            candidate.setCandidateExpList(getCandidateExpListFromAddSupportCandidate(supportCandidateRequest.getExpList(), candidate));
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
                && supportCandidateRequest.getDeactivationExpiryDate() != null ){
            /* Add Canidate to candidateStatusDetail and Change candidateStatus to Cold */
            candidate.setCandidateprofilestatus(CandidateProfileStatus.find.where().eq("profileStatusId", ServerConstants.CANDIDATE_STATE_DEACTIVE).findUnique());
            candidateStatusDetail.setReason(Reason.find.where().eq("ReasonId", supportCandidateRequest.getDeactivationReason()).findUnique());
            candidateStatusDetail.setStatusExpiryDate(supportCandidateRequest.getDeactivationExpiryDate());
            InteractionService.createInteractionForDeactivateCandidate(candidate.getCandidateUUId(), true);
            return candidateStatusDetail;
        } else if(candidate.getCandidateStatusDetail() != null && !supportCandidateRequest.getDeactivationStatus()){
            /* Remove from candidateStatusDetail and change candidateStatus to Active */
            candidate.setCandidateprofilestatus(CandidateProfileStatus.find.where().eq("profileStatusId", ServerConstants.CANDIDATE_STATE_ACTIVE).findUnique());

            InteractionService.createInteractionForActivateCandidate(candidate.getCandidateUUId(), true);
            return null;
        }
        return null;
    }

    private static List<CandidateExp> getCandidateExpListFromAddSupportCandidate(List<AddSupportCandidateRequest.ExpList> expList, Candidate candidate) {
        List<CandidateExp> candidateExpList = new ArrayList<>();
        /* Here List can be empty but not null */
        for (AddSupportCandidateRequest.ExpList exp : expList){
            if(exp == null || exp.getJobExpResponseIdArray() == null ){
                continue;
            }
            Logger.info("------------"+exp.getJobExpResponseIdArray());
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
        Logger.info("--- " + toJson(expList));
        return candidateExpList;
    }

    private static CandidateSignUpResponse updateEducationProfile(Candidate candidate,
                                                                  AddCandidateEducationRequest addCandidateEducationRequest) {

        CandidateSignUpResponse candidateSignUpResponse = new CandidateSignUpResponse();
        Logger.info("Inside Education profile update");
        candidateSignUpResponse.setStatus(CandidateSignUpResponse.STATUS_SUCCESS);
        candidateSignUpResponse.setMinProfile(candidate.getIsMinProfileComplete());
        try {
            candidate.setCandidateEducation(getCandidateEducationFromAddSupportCandidate(addCandidateEducationRequest, candidate));
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

        Logger.info("Inside Skills profile update");

        // initialize to default value. We will change this value later if any exception occurs
        candidateSignUpResponse.setStatus(CandidateSignUpResponse.STATUS_SUCCESS);

        try {
            candidate.setCandidateTotalExperience(addCandidateExperienceRequest.getCandidateTotalExperience());
        }
        catch(Exception e){
            candidateSignUpResponse.setStatus(CandidateSignUpResponse.STATUS_FAILURE);
            Logger.info("Exception while setting total experience");
            e.printStackTrace();
        }

        try {
            candidate.setCandidateIsEmployed(addCandidateExperienceRequest.getCandidateIsEmployed());
        } catch(Exception e){
            candidateSignUpResponse.setStatus(CandidateSignUpResponse.STATUS_FAILURE);
            Logger.info("Exception while setting employment status ");
            e.printStackTrace();
        }

        try {
            candidate.setMotherTongue(Language.find.where().eq("languageId", addCandidateExperienceRequest.getCandidateMotherTongue()).findUnique());
        } catch(Exception e){
            candidateSignUpResponse.setStatus(CandidateSignUpResponse.STATUS_FAILURE);
            Logger.info("Exception while setting mother tongue");
            e.printStackTrace();
        }

       /* try {
            CandidateCurrentJobDetail candidateCurrentJobDetail = getCandidateCurrentJobDetailFromAddSupportCandidate(addCandidateExperienceRequest, candidate, isSupport);
            candidate.setCandidateCurrentJobDetail(candidateCurrentJobDetail);
        } catch(Exception e){
            candidateSignUpResponse.setStatus(CandidateSignUpResponse.STATUS_FAILURE);
            Logger.info(" try catch exception Current job candidateCurrentJobDetail  = " + e);
        }*/

        /* TODO: remove this after changing flow to use support flow for multiple company name*/
        candidate.setJobHistoryList(getCurrentCompanyNameAsList(addCandidateExperienceRequest, candidate));

        try{
            candidate.setCandidateLastWithdrawnSalary(addCandidateExperienceRequest.getCandidateLastWithdrawnSalary());
        } catch(Exception e){
            Logger.info(" Exception while setting current company for website under update skill");
            e.printStackTrace();
        }

        try {
            candidate.setCandidateSkillList(getCandidateSkillListFromAddSupportCandidate(addCandidateExperienceRequest, candidate));
        } catch(Exception e){
            candidateSignUpResponse.setStatus(CandidateSignUpResponse.STATUS_FAILURE);
            Logger.info("Exception while setting skills list");
            e.printStackTrace();
        }

        try {
            candidate.setLanguageKnownList(getCandidateLanguageFromSupportCandidate(addCandidateExperienceRequest, candidate));
        } catch(Exception e){
            candidateSignUpResponse.setStatus(CandidateSignUpResponse.STATUS_FAILURE);
            Logger.info("Exception while setting languages known list");
            e.printStackTrace();
        }

        return candidateSignUpResponse;
    }

    private static List<JobHistory> getCurrentCompanyNameAsList(AddCandidateExperienceRequest candidateCurrentCompany, Candidate candidate) {
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

        // not just update but createOrUpdateConvertedLead
        Logger.info("Inside updateBasicProfile");


        // initialize to default value. We will change this value later if any exception occurs
        candidateSignUpResponse.setStatus(CandidateSignUpResponse.STATUS_SUCCESS);

        /// Basic Profile Section Starts
        candidate.setCandidateFirstName(request.getCandidateFirstName());
        candidate.setCandidateLastName(request.getCandidateSecondName());
        candidate.setCandidateUpdateTimestamp(new Timestamp(System.currentTimeMillis()));

        //this case comes when saving basic profile from android app
        if(request.getCandidateHomeLocality() == null){
            if(candidate.getLocality() != null){
                request.setCandidateHomeLocality((int) candidate.getLocality().getLocalityId());
            }
        }
        try{
            candidate.setLocality(Locality.find.where().eq("localityId", request.getCandidateHomeLocality()).findUnique());
        } catch(Exception e){
            Logger.info(" Exception while setting home locality");
            e.printStackTrace();
        }

        try {
            if(request.getCandidateDob() != null)
                candidate.setCandidateDOB(request.getCandidateDob()); // age gets calc inside this method
        }
        catch(Exception e) {
            candidateSignUpResponse.setStatus(CandidateSignUpResponse.STATUS_FAILURE);
            Logger.info("Exception while setting dob");
            e.printStackTrace();
        }

        try {
            candidate.setCandidateGender(request.getCandidateGender());
        }
        catch(Exception e) {
            candidateSignUpResponse.setStatus(CandidateSignUpResponse.STATUS_FAILURE);
            Logger.info("Exception while setting gender");
            e.printStackTrace();
        }

        try {
            candidate.setTimeShiftPreference(getTimeShiftPrefFromAddSupportCandidate(request, candidate));
        }
        catch(Exception e) {
            candidateSignUpResponse.setStatus(CandidateSignUpResponse.STATUS_FAILURE);
            Logger.info("Exception while setting timeshift preferences");
            e.printStackTrace();
        }
        try {
            candidate.setCandidateAssetList(getAssetListFromAddSupportCandidate(request, candidate));
        }
        catch(Exception e) {
            candidateSignUpResponse.setStatus(CandidateSignUpResponse.STATUS_FAILURE);
            Logger.info("Exception while setting candidate asset list");
            e.printStackTrace();
        }


        if(request.getCandidateIdProofList() != null ){
            candidate.setIdProofReferenceList(getCandidateIdProofListFromAddSupportCandidate(request.getCandidateIdProofList(), candidate));
        }

        try {
            if(request.getCandidateIdProofList() != null ){
                candidate.setIdProofReferenceList(getCandidateIdProofListFromAddSupportCandidate(request.getCandidateIdProofList(), candidate));
            }        }
        catch(Exception e) {
            candidateSignUpResponse.setStatus(CandidateSignUpResponse.STATUS_FAILURE);
            Logger.info("Exception while setting candidate idproof list");
            e.printStackTrace();
        }

        Logger.info("Added Basic Profile details");

        return candidateSignUpResponse;
    }

    private static List<CandidateAsset> getAssetListFromAddSupportCandidate(AddCandidateRequest request, Candidate candidate) {
        ArrayList<CandidateAsset> response = new ArrayList<>();
        List<Asset> assetList = Asset.find.where().in("assetId", request.getCandidateAssetList()).findList();

        if(assetList.size() == 0){
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

    private static List<LanguageKnown> getCandidateLanguageFromSupportCandidate(AddCandidateExperienceRequest request, Candidate candidate) {
        List<LanguageKnown> languageKnownList = new ArrayList<>();
        for(CandidateKnownLanguage candidateKnownLanguage : request.candidateLanguageKnown){
            LanguageKnown languageKnown = new LanguageKnown();
            Language language = Language.find.where().eq("LanguageId", candidateKnownLanguage.getId()).findUnique();
            if(language == null) {
                Logger.info("Language static table is empty for:" + candidateKnownLanguage.getId());
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

    private static void triggerOtp(Candidate candidate, CandidateSignUpResponse candidateSignUpResponse, InteractionService.InteractionChannelType channelType) {
        int randomPIN = generateOtp();
        SmsUtil.sendOTPSms(randomPIN, candidate.getCandidateMobile(), channelType);

        candidateSignUpResponse.setCandidateId(candidate.getCandidateId());
        candidateSignUpResponse.setCandidateFirstName(candidate.getCandidateFirstName());
        candidateSignUpResponse.setOtp(randomPIN);
        candidateSignUpResponse.setStatus(CandidateSignUpResponse.STATUS_SUCCESS);
    }

    private static List<IDProofReference> getCandidateIdProofListFromAddSupportCandidate(List<AddSupportCandidateRequest.IdProofWithValue> idProofList, Candidate candidate) {
        ArrayList<IDProofReference> response = new ArrayList<>();
        for(AddSupportCandidateRequest.IdProofWithValue idf : idProofList) {
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

    private static CandidateEducation getCandidateEducationFromAddSupportCandidate(AddCandidateEducationRequest request, Candidate candidate) {
        CandidateEducation response  = candidate.getCandidateEducation();
        Education education = Education.find.where().eq("educationId", request.getCandidateEducationLevel()).findUnique();
        Degree degree = Degree.find.where().eq("degreeId", request.getCandidateDegree()).findUnique();
        if(response == null){
            response = new CandidateEducation();
            response.setCandidate(candidate);
        }

        if (education == null && degree == null) {
            return null;
        }

        if(education != null){
            response.setEducation(education);
        }

        if(degree != null){
            response.setDegree(degree);
        }
        if(request.getCandidateEducationCompletionStatus() != null){
            response.setCandidateEducationCompletionStatus(request.getCandidateEducationCompletionStatus());
        }
        if(!Strings.isNullOrEmpty(request.getCandidateEducationInstitute())){
            response.setCandidateLastInstitute(request.getCandidateEducationInstitute());
        }
        return response;
    }

    private static List<CandidateSkill> getCandidateSkillListFromAddSupportCandidate(AddCandidateExperienceRequest request, Candidate candidate) {
        List<CandidateSkill> response = new ArrayList<>();
        for(CandidateSkills item: request.candidateSkills){
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
            Logger.info("skill........ " + item.getAnswer());
        }
        return response;
    }

    private static List<JobHistory> getJobHistoryListFromAddSupportCandidate(List<AddSupportCandidateRequest.PastCompany> pastCompanyList, Candidate candidate) {
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

    private static TimeShiftPreference getTimeShiftPrefFromAddSupportCandidate(AddCandidateRequest request, Candidate candidate) {
        TimeShiftPreference response = candidate.getTimeShiftPreference();
        if(response == null){
            response = new TimeShiftPreference();
            response.setCandidate(candidate);
        }
        if(request.getCandidateTimeShiftPref() == null){
            Logger.info("timeshiftPref not provided");
            return null;
        } else {
            TimeShift existingTimeShift = TimeShift.find.where().eq("timeShiftId", request.getCandidateTimeShiftPref()).findUnique();
            if(existingTimeShift == null) {
                Logger.info("timeshift static table empty for Pref: " + request.getCandidateTimeShiftPref());
                return null;
            }
            response.setTimeShift(existingTimeShift);
        }
        return response;
    }

    /*private static CandidateCurrentJobDetail getCandidateCurrentJobDetailFromAddSupportCandidate(AddCandidateExperienceRequest request, Candidate candidate, boolean isSupport) {
        CandidateCurrentJobDetail response = candidate.getCandidateCurrentJobDetail();
        if(response == null){
            response = new CandidateCurrentJobDetail();
            response.setCandidate( candidate);
        }

        Logger.info("inserting current Job details");
        try{
            response.setCandidateCurrentCompany( request.getCandidateCurrentCompany());
            response.setCandidateCurrentSalary(request.getCandidateLastWithdrawnSalary());

            if(isSupport) {
                AddSupportCandidateRequest supportCandidateRequest = (AddSupportCandidateRequest) request;

                if(supportCandidateRequest.getCandidateCurrentJobDesignation() == null
                    && supportCandidateRequest.getCandidateCurrentJobDuration() == null
                    && supportCandidateRequest.getCandidateCurrentWorkShift() == null
                    && supportCandidateRequest.getCandidateCurrentJobRole() == null
                    && supportCandidateRequest.getCandidateCurrentJobLocation() == null
                    && request.getCandidateLastWithdrawnSalary() == null
                    && request.getCandidateCurrentCompany() == null
                        ) {
                    return null;
                }

                response.setCandidateCurrentDesignation(supportCandidateRequest.getCandidateCurrentJobDesignation());
                response.setCandidateCurrentJobDuration(supportCandidateRequest.getCandidateCurrentJobDuration());
                response.setCandidateCurrentEmployerRefMobile("na");
                response.setCandidateCurrentEmployerRefName("na");



                TransportationMode transportationMode = TransportationMode.find.where().eq("transportationModeId", supportCandidateRequest.getCandidateTransportation()).findUnique();
                TimeShift timeShift = TimeShift.find.where().eq("timeShiftId", supportCandidateRequest.getCandidateCurrentWorkShift()).findUnique();
                JobRole jobRole = JobRole.find.where().eq("jobRoleId",supportCandidateRequest.getCandidateCurrentJobRole()).findUnique();
                Locality locality = Locality.find.where().eq("localityId", supportCandidateRequest.getCandidateCurrentJobLocation()).findUnique();
                if(timeShift == null && jobRole == null && locality == null && request.getCandidateLastWithdrawnSalary() == null &&
                        (request.getCandidateCurrentCompany() == null || request.getCandidateCurrentCompany().trim().isEmpty()))
                {
                    return null;
                }

                response.setCandidateTransportationMode(transportationMode);
                response.setCandidateCurrentWorkShift(timeShift);
                response.setCandidateCurrentJobLocation(locality);
                response.setJobRole(jobRole);
            }
        } catch(Exception e){
            Logger.info(" Try catch exception while inserting current job detail");
        }
        Logger.info("done insertion current Job details");
        return response;
    }
*/
    public static LoginResponse login(String loginMobile, String loginPassword, InteractionChannelType channelType){
        LoginResponse loginResponse = new LoginResponse();
        Logger.info(" login mobile: " + loginMobile);
        Candidate existingCandidate = CandidateService.isCandidateExists(loginMobile);
        if(existingCandidate == null){
            loginResponse.setStatus(loginResponse.STATUS_NO_USER);
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


                    /* START : to cater specifically the app need */
                    if(existingCandidate.getCandidateLocalityLat() != null
                            || existingCandidate.getCandidateLocalityLng() != null ){
                        loginResponse.setCandidateHomeLat(existingCandidate.getCandidateLocalityLat());
                        loginResponse.setCandidateHomeLng(existingCandidate.getCandidateLocalityLng());
                    }
                    if(!existingCandidate.getJobPreferencesList().isEmpty()){
                        if(existingCandidate.getJobPreferencesList().size()>0 && existingCandidate.getJobPreferencesList().get(0)!= null)
                            loginResponse.setCandidatePrefJobRoleIdOne(existingCandidate.getJobPreferencesList().get(0).getJobRole().getJobRoleId());
                        if(existingCandidate.getJobPreferencesList().size()>1 &&existingCandidate.getJobPreferencesList().get(1)!= null)
                            loginResponse.setCandidatePrefJobRoleIdTwo(existingCandidate.getJobPreferencesList().get(1).getJobRole().getJobRoleId());
                        if(existingCandidate.getJobPreferencesList().size()>2 &&existingCandidate.getJobPreferencesList().get(2)!= null)
                            loginResponse.setCandidatePrefJobRoleIdThree(existingCandidate.getJobPreferencesList().get(2).getJobRole().getJobRoleId());
                    }
                    /* END */
                    if(existingCandidate.getJobPreferencesList().size() > 0){
                        loginResponse.setCandidateJobPrefStatus(1);
                    }
                    if(existingCandidate.getCandidateLocalityLat() != null && existingCandidate.getCandidateLocalityLng() != null){
                        loginResponse.setCandidateHomeLocalityStatus(1);
                    }
                    if(existingCandidate.getLocality()!= null && existingCandidate.getLocality().getLocalityName()!=null){
                        loginResponse.setCandidateHomeLocalityName(existingCandidate.getLocality().getLocalityName());
                    }
                    loginResponse.setStatus(loginResponse.STATUS_SUCCESS);

                    existingAuth.setAuthSessionId(UUID.randomUUID().toString());
                    existingAuth.setAuthSessionIdExpiryMillis(System.currentTimeMillis() + 24 * 60 * 60 * 1000);

                    loginResponse.setAuthSessionId(existingAuth.getAuthSessionId());
                    loginResponse.setSessionExpiryInMilliSecond(existingAuth.getAuthSessionIdExpiryMillis());

                    loginResponse.setIsCandidateVerified(existingAuth.getAuthStatus());
                    /* adding session details */
                    AuthService.addSession(existingAuth,existingCandidate);
                    existingAuth.update();
                    if(channelType == InteractionChannelType.SELF){
                        InteractionService.createInteractionForLoginCandidateViaWebsite(existingCandidate.getCandidateUUId());
                    } else{
                        InteractionService.createInteractionForLoginCandidateViaAndroid(existingCandidate.getCandidateUUId());
                    }
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

    public static ResetPasswordResponse findUserAndSendOtp(String candidateMobile, InteractionChannelType channelType){
        ResetPasswordResponse resetPasswordResponse = new ResetPasswordResponse();
        Candidate existingCandidate = isCandidateExists(candidateMobile);
        if(existingCandidate != null){
            Logger.info("CandidateExists");
            Auth existingAuth = Auth.find.where().eq("candidateId", existingCandidate.getCandidateId()).findUnique();
            if(existingAuth == null){
                resetPasswordResponse.setStatus(LoginResponse.STATUS_NO_USER);
                Logger.info("reset password not allowed as Auth don't exists");
            } else {
                int randomPIN = generateOtp();
                existingCandidate.update();
                SmsUtil.sendResetPasswordOTPSms(randomPIN, existingCandidate.getCandidateMobile(), channelType);

                String interactionResult = InteractionConstants.INTERACTION_RESULT_CANDIDATE_TRIED_TO_RESET_PASSWORD;
                String objAUUID = "";
                objAUUID = existingCandidate.getCandidateUUId();
                if (channelType == InteractionChannelType.SELF) {
                    InteractionService.createInteractionForResetPasswordAttemptViaWebsite(objAUUID, interactionResult);
                } else{
                    InteractionService.createInteractionForResetPasswordAttemptViaAndroid(objAUUID, interactionResult);
                }
                resetPasswordResponse.setOtp(randomPIN);
                resetPasswordResponse.setStatus(LoginResponse.STATUS_SUCCESS);
            }
        } else{
            resetPasswordResponse.setStatus(LoginResponse.STATUS_NO_USER);
            Logger.info("reset password not allowed as Candidate don't exists");
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
            Logger.info("candiateLocalitypref"+candidateLocalityPreference.getLocality() + " == " + localityId);
            candidateLocalityPreferenceList.add(candidateLocalityPreference);
        }
        return candidateLocalityPreferenceList;
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

    // individual update service for pre-screen-edit
    public static void updateCandidateDocument(Candidate candidate, UpdateCandidateDocument updateCandidateDocument) {

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

        List<IDProofReference> newCandidateIdProofList = new ArrayList<>();
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

            Map<?, IdProof> staticIdProofMap = IdProofDAO.getIdProofRecordList(idProofIdList);

            for (UpdateCandidateDocument.IdProofWithIdNumber idProofWithIdNumber :
                    updateCandidateDocument.getIdProofWithIdNumberList())
            {
                // if the candidate already had details pertaining to this idproof, then update the record
                if (existingIdProofIdToReference.containsKey(idProofWithIdNumber.getIdProofId())) {
                    IDProofReference existingIdProofRef = existingIdProofIdToReference.get(idProofWithIdNumber.getIdProofId());

                    if (existingIdProofRef.getIdProofNumber() == null
                            || !existingIdProofRef.getIdProofNumber().equals(idProofWithIdNumber.getIdNumber()))
                    {
                        existingIdProofRef.setIdProofNumber(idProofWithIdNumber.getIdNumber());
                        //newCandidateIdProofList.add(existingIdProofRef);
                        existingIdProofRef.update();

                        // if aadhaar details changed (and the number was not removed during the update), then
                        // enable verification flag
                        if (idProofWithIdNumber.getIdProofId() == IdProofDAO.IDPROOF_AADHAAR_ID
                                && (idProofWithIdNumber.getIdNumber() != null || !idProofWithIdNumber.getIdNumber().isEmpty()))
                        {
                            isVerifyAadhaar = true;
                        }
                    }
                }
                // if this is the first time we are getting this idproof details for candidate, then create new record
                else {
                    IDProofReference idProofReference = new IDProofReference();
                    idProofReference.setCandidate(candidate);
                    idProofReference.setIdProof(staticIdProofMap.get(idProofWithIdNumber.getIdProofId()));
                    idProofReference.setIdProofNumber(idProofWithIdNumber.getIdNumber());
                    //newCandidateIdProofList.add(idProofReference);
                    idProofReference.save();

                    if (idProofWithIdNumber.getIdProofId() == IdProofDAO.IDPROOF_AADHAAR_ID) {
                        isVerifyAadhaar = true;
                    }
                }
            }

            // iterate on ids that were removed and delete them
            for (Integer idProofId : existingIdProofIdToReference.keySet()) {
                if (!idProofIdList.contains(idProofId)) {
                    // delete this entry
                    existingIdProofIdToReference.get(idProofId).delete();
                }
            }

            //candidate.setIdProofReferenceList(newCandidateIdProofList);
            //candidate.update();

            // if Aadhaar details were inserted or updated then lets send aadhaar verification request
            if (isVerifyAadhaar) {
                new Thread(() -> {
                    OngridAadhaarVerificationResponse response = AadhaarService.sendAadharSyncVerificationRequest(candidate);
                }).start();
            }
        }
    }

    public static void updateCandidateLanguageKnown(Candidate candidate, UpdateCandidateLanguageKnown updateCandidateLanguageKnown){
        AddCandidateExperienceRequest candidateLanguageShell = new AddCandidateExperienceRequest();
        candidateLanguageShell.setCandidateLanguageKnown(updateCandidateLanguageKnown.getCandidateKnownLanguageList());
        candidate.setLanguageKnownList(CandidateService.getCandidateLanguageFromSupportCandidate(candidateLanguageShell, candidate));
        candidate.update();
    }
    public static void updateCandidateAssetOwned(Candidate candidate, UpdateCandidateAsset asset){
        if(asset!= null && asset.getAssetIdList() != null && asset.getAssetIdList().size()>0) {
            /*List<Asset> assetList = Asset.find.where().in("assetId", asset.getAssetIdList()).findList();
            List<CandidateAsset> candidateAssetList = new ArrayList<>();

            if(assetList.size() == 0) {
                Logger.info("asset not found");
                return;
            }
            for (Asset asset1 : assetList) {
                CandidateAsset candidateAsset = new CandidateAsset();
                candidateAsset.setCandidate(candidate);
                candidateAsset.setAsset(asset1);

                candidateAssetList.add(candidateAsset);
            }*/
            AddCandidateRequest candidateAssetShell = new AddCandidateRequest();
            candidateAssetShell.setCandidateAssetList(asset.getAssetIdList());
            candidate.setCandidateAssetList(CandidateService.getAssetListFromAddSupportCandidate(candidateAssetShell, candidate));
            candidate.update();
        }
    }
    public static void updateCandidateDOB(Candidate candidate, UpdateCandidateDob updateCandidateDob){
        candidate.setCandidateDOB(updateCandidateDob.getCandidateDob());
        candidate.update();
    }
    public static void updateCandidateWorkExperience(Candidate candidate, UpdateCandidateWorkExperience workExperience){
        candidate.setCandidateTotalExperience(workExperience.getCandidateTotalExperience());
        if(workExperience.getExtraDetailAvailable()!= null && workExperience.getExtraDetailAvailable()) {
            if(workExperience.getPastCompanyList() != null ) {
                candidate.setJobHistoryList(getJobHistoryListFromAddSupportCandidate(workExperience.getPastCompanyList(), candidate));
            }
        }
        candidate.update();
    }
    public static void updateCandidateEducation(Candidate candidate, UpdateCandidateEducation candidateEducation){
        AddCandidateEducationRequest educationShell = new AddCandidateEducationRequest();
        educationShell.setCandidateDegree(candidateEducation.getCandidateDegree());
        educationShell.setCandidateEducationInstitute(candidateEducation.getCandidateEducationInstitute());
        educationShell.setCandidateEducationLevel(candidateEducation.getCandidateEducationLevel());
        educationShell.setEducationStatus(candidateEducation.getCandidateEducationCompletionStatus());

        candidate.setCandidateEducation(getCandidateEducationFromAddSupportCandidate(educationShell, candidate));
        candidate.update();
    }
    public static void updateCandidateLastWithdrawnSalary(Candidate candidate, UpdateCandidateLastWithdrawnSalary lastWithdrawnSalary){
        candidate.setCandidateLastWithdrawnSalary(lastWithdrawnSalary.getCandidateLastWithdrawnSalary());
        candidate.update();
    }
    public static void updateCandidateGender(Candidate candidate, UpdateCandidateGender gender){
        candidate.setCandidateGender(gender.getCandidateGender());
        candidate.update();
    }
    public static void updateCandidateHomeLocality(Candidate candidate, UpdateCandidateHomeLocality homeLocality){
        if(homeLocality.getCandidateHomeLocality() != null){
            Locality locality = Locality.find.where().eq("localityId", homeLocality.getCandidateHomeLocality()).findUnique();
            if(locality != null){
                candidate.setLocality(locality);
            }
        }

        candidate.update();
    }
    public static void updateCandidateWorkshift(Candidate candidate, UpdateCandidateTimeShiftPreference timeShiftPreference){
        AddCandidateRequest requestShell = new AddCandidateRequest();
        requestShell.setCandidateTimeShiftPref(timeShiftPreference.getCandidateTimeShiftPref());
        candidate.setTimeShiftPreference(getTimeShiftPrefFromAddSupportCandidate(requestShell, candidate));

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

        Logger.info("returning results " + resultList.size());
        return resultList;
    }
}
