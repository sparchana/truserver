package controllers.businessLogic;

import api.ServerConstants;
import api.http.CandidateKnownLanguage;
import api.http.CandidateSkills;
import api.http.FormValidator;
import api.http.httpRequest.*;
import api.http.httpResponse.CandidateSignUpResponse;
import api.http.httpResponse.LoginResponse;
import api.http.httpResponse.ResetPasswordResponse;
import com.avaje.ebean.Query;
import com.google.api.client.repackaged.com.google.common.base.Strings;
import models.entity.Auth;
import models.entity.Candidate;
import models.entity.Lead;
import models.entity.OM.*;
import models.entity.OO.CandidateEducation;
import models.entity.OO.CandidateStatusDetail;
import models.entity.OO.TimeShiftPreference;
import models.entity.Static.*;
import models.util.SmsUtil;
import models.util.Util;
import play.Logger;

import javax.persistence.NonUniqueResultException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static controllers.businessLogic.InteractionService.createInteractionForSignUpCandidate;
import static controllers.businessLogic.LeadService.createOrUpdateConvertedLead;
import static models.util.Util.generateOtp;
import static play.libs.Json.toJson;
import static play.mvc.Controller.session;

/**
 * Created by batcoder1 on 3/5/16.
 */
public class CandidateService
{
    private static CandidateSignUpResponse createNewCandidate(Candidate candidate, Lead lead) {

        CandidateSignUpResponse candidateSignUpResponse = new CandidateSignUpResponse();
        CandidateProfileStatus candidateProfileStatus = CandidateProfileStatus.find.where().eq("profileStatusId", ServerConstants.CANDIDATE_STATE_ACTIVE).findUnique();
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
                                                          boolean isSupport,
                                                          int leadSourceId) {
        List<Integer> localityList = candidateSignUpRequest.getCandidateLocality();
        List<Integer> jobsList = candidateSignUpRequest.getCandidateJobPref();

        CandidateSignUpResponse candidateSignUpResponse = new CandidateSignUpResponse();
        String result = "";
        String objectAUUId = "";
        Logger.info("Checking for mobile number: " + candidateSignUpRequest.getCandidateMobile());
        Candidate candidate = isCandidateExists(candidateSignUpRequest.getCandidateMobile());
        String leadName = candidateSignUpRequest.getCandidateFirstName()+ " " + candidateSignUpRequest.getCandidateSecondName();
        Lead lead = LeadService.createOrUpdateConvertedLead(leadName, candidateSignUpRequest.getCandidateMobile(), leadSourceId, isSupport);
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
                if(localityList != null){
                    candidate.setLocalityPreferenceList(getCandidateLocalityPreferenceList(localityList, candidate));
                }
                if(jobsList != null){
                    candidate.setJobPreferencesList(getCandidateJobPreferenceList(jobsList, candidate));
                }

                candidateSignUpResponse = createNewCandidate(candidate, lead);
                if(!isSupport){
                    // triggers when candidate is self created
                    triggerOtp(candidate, candidateSignUpResponse);
                    result = ServerConstants.INTERACTION_RESULT_NEW_CANDIDATE;
                    objectAUUId = candidate.getCandidateUUId();
                }
            } else {
                Auth auth = AuthService.isAuthExists(candidate.getCandidateId());
                if(auth == null ) {
                    Logger.info("auth doesn't exists for this candidate");
                    candidate.setCandidateFirstName(candidateSignUpRequest.getCandidateFirstName());
                    candidate.setCandidateLastName(candidateSignUpRequest.getCandidateSecondName());
                    if(localityList != null) {
                        resetLocalityAndJobPref(candidate, getCandidateLocalityPreferenceList(localityList, candidate), getCandidateJobPreferenceList(jobsList, candidate));
                    }
                    if(!isSupport){
                        triggerOtp(candidate, candidateSignUpResponse);
                        result = ServerConstants.INTERACTION_RESULT_EXISTING_CANDIDATE_VERIFICATION;
                        objectAUUId = candidate.getCandidateUUId();
                        candidateSignUpResponse.setStatus(CandidateSignUpResponse.STATUS_SUCCESS);

                    } else {//TODO: will never come to this point, hence to be removed
                        createAndSaveDummyAuthFor(candidate);
                        result = ServerConstants.INTERACTION_RESULT_EXISTING_CANDIDATE_VERIFICATION;
                        candidateSignUpResponse.setStatus(CandidateSignUpResponse.STATUS_EXISTS);
                    }
                } else{
                    result = ServerConstants.INTERACTION_RESULT_EXISTING_CANDIDATE_SIGNUP;
                    candidateSignUpResponse.setStatus(CandidateSignUpResponse.STATUS_EXISTS);
                }
                candidate.candidateUpdate();
            }

            // Insert Interaction only for self sign up as interaction for sign up support will be handled in createCandidateProfile
            //TODO: improve naming convention
            createInteractionForSignUpCandidate(objectAUUId, result, isSupport);

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
     * @param isSupport Indicates whether this method is being called from support ui or from website
     * @param profileUpdateFlag Indicates which part of candidate's profile is being updated
     * @return
     */
    public static CandidateSignUpResponse createCandidateProfile(AddCandidateRequest request,
                                                                 boolean isSupport,
                                                                 int profileUpdateFlag) {
        CandidateSignUpResponse candidateSignUpResponse = new CandidateSignUpResponse();
        // get candidateBasic obj from req
        // Handle jobPrefList and any other list with , as break point at application only
        Logger.info("Creating candidate profile for mobile " + request.getCandidateMobile());

        // Check if this candiate already exists
        Candidate candidate = isCandidateExists(request.getCandidateMobile());

        // Initialize some basic interaction details
        String createdBy = ServerConstants.INTERACTION_CREATED_SELF;
        String interactionResult = ServerConstants.INTERACTION_RESULT_CANDIDATE_INFO_UPDATED_SELF;
        Integer interactionType = ServerConstants.INTERACTION_TYPE_WEBSITE;
        String interactionNote;
        boolean isNewCandidate = false;

        if(isSupport){
            createdBy = session().get("sessionUsername");
            interactionResult = ServerConstants.INTERACTION_RESULT_CANDIDATE_INFO_UPDATED_SYSTEM;
            interactionType = ServerConstants.INTERACTION_TYPE_CALL_OUT;
        }

        if(candidate == null){
            Logger.info("Candidate with mobile number: " + request.getCandidateMobile() + " doesn't exist");
            CandidateSignUpRequest candidateSignUpRequest = ( CandidateSignUpRequest ) request;

            // sign this candidate up as a first step
            candidateSignUpResponse = signUpCandidate(candidateSignUpRequest, isSupport, request.getLeadSource());

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

            // update new locality preferences
            if(request.getCandidateLocality() != null){
                candidate.setLocalityPreferenceList(getCandidateLocalityPreferenceList(request.getCandidateLocality(), candidate));
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
                Lead lead = createOrUpdateConvertedLead(request.getCandidateFirstName() +" " + request.getCandidateSecondName(), request.getCandidateMobile(), request.getLeadSource(), isSupport);
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
                interactionResult = ServerConstants.INTERACTION_RESULT_CANDIDATE_BASIC_PROFILE_INFO_UPDATED_SELF;
            }

        }

        // Now we check if we are dealing with the reqeust to update skills/experience profile details from website (or)
        // dealing with a create/update candidate profile request from support
        if(profileUpdateFlag == ServerConstants.UPDATE_SKILLS_PROFILE ||
                profileUpdateFlag == ServerConstants.UPDATE_ALL_BY_SUPPORT) {

            candidateSignUpResponse = updateSkillProfile(candidate, (AddCandidateExperienceRequest) request, isSupport);

            // In case of errors, return at this point
            if(candidateSignUpResponse.getStatus() != CandidateSignUpResponse.STATUS_SUCCESS){
                Logger.info("Error while updating experience profile of candidate with mobile " + candidate.getCandidateMobile());
                return candidateSignUpResponse;
            }

            // Set the appropriate interaction result
            if(profileUpdateFlag == ServerConstants.UPDATE_SKILLS_PROFILE){
                interactionResult = ServerConstants.INTERACTION_RESULT_CANDIDATE_SKILLS_PROFILE_INFO_UPDATED_SELF;
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
                interactionResult = ServerConstants.INTERACTION_RESULT_CANDIDATE_EDUCATION_PROFILE_INFO_UPDATED_SELF;
            }
        }

        // set the default interaction note string
        interactionNote = ServerConstants.INTERACTION_NOTE_BLANK;

        if(isSupport){
            // update additional fields that are part of the support request
            updateOthersBySupport(candidate, request);

            AddSupportCandidateRequest supportCandidateRequest = (AddSupportCandidateRequest) request;

            createdBy = session().get("sessionUsername");
            interactionType = ServerConstants.INTERACTION_TYPE_CALL_OUT;
            interactionNote = supportCandidateRequest.getSupportNote();

            if (isNewCandidate) {
                interactionResult = ServerConstants.INTERACTION_RESULT_NEW_CANDIDATE_SUPPORT;
            }
            else {
                interactionResult = ServerConstants.INTERACTION_RESULT_CANDIDATE_INFO_UPDATED_SYSTEM;
            }
        }

        // check if we have auth record for this candidate. if we dont have, create one with a temporary password
        Auth auth = AuthService.isAuthExists(candidate.getCandidateId());
        if (auth == null) {
            if(isSupport){
                // TODO: differentiate between in/out call
                createAndSaveDummyAuthFor(candidate);
                interactionResult += " & " + ServerConstants.INTERACTION_NOTE_DUMMY_PASSWORD_CREATED;
            }
        }
        // check if we have enough details required to complete the minimum profile
        candidate.setIsMinProfileComplete(isMinProfileComplete(candidate));

        InteractionService.createInteractionForCreateCandidateProfile(candidate.getCandidateUUId(),
                interactionType, interactionNote, interactionResult, createdBy);

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

        try{
            candidate.setLocality(Locality.find.where().eq("localityId", supportCandidateRequest.getCandidateHomeLocality()).findUnique());
        } catch(Exception e){
            Logger.info(" Exception while setting home locality");
            e.printStackTrace();
        }

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

        try{
            candidate.setJobHistoryList(getJobHistoryListFromAddSupportCandidate(supportCandidateRequest.getPastCompanyList(), candidate));
        } catch(Exception e){
            Logger.info(" Exception while setting past job details");
            e.printStackTrace();
        }

        try{
            candidate.setIdProofReferenceList(getCandidateIdProofListFromAddSupportCandidate(supportCandidateRequest.getCandidateIdProof(), candidate));
        } catch(Exception e){
            Logger.info(" Exception while setting idproof reference list");
            e.printStackTrace();
        }

        try{
            candidate.setCandidateExpList(getCandidateExpListFromAddSupportCandidate(supportCandidateRequest.getExpList(), candidate));
        } catch(Exception e){
            Logger.info(" Exception while setting explist reference list");
            e.printStackTrace();
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
            InteractionService.CreateInteractionForDeactivateCandidate(candidate.getCandidateUUId(), true);
            return candidateStatusDetail;
        } else if(candidate.getCandidateStatusDetail() != null && !supportCandidateRequest.getDeactivationStatus()){
            /* Remove from candidateStatusDetail and change candidateStatus to Active */
            candidate.setCandidateprofilestatus(CandidateProfileStatus.find.where().eq("profileStatusId", ServerConstants.CANDIDATE_STATE_ACTIVE).findUnique());

            InteractionService.CreateInteractionForActivateCandidate(candidate.getCandidateUUId(), true);
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
                                                              AddCandidateExperienceRequest addCandidateExperienceRequest,
                                                              boolean isSupport) {

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

        Logger.info("Added Basic Profile details");

        return candidateSignUpResponse;
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

    private static void triggerOtp(Candidate candidate, CandidateSignUpResponse candidateSignUpResponse) {
        int randomPIN = generateOtp();
        SmsUtil.sendOTPSms(randomPIN, candidate.getCandidateMobile());

        candidateSignUpResponse.setCandidateId(candidate.getCandidateId());
        candidateSignUpResponse.setCandidateFirstName(candidate.getCandidateFirstName());
        candidateSignUpResponse.setOtp(randomPIN);
        candidateSignUpResponse.setStatus(CandidateSignUpResponse.STATUS_SUCCESS);
    }

    private static List<IDProofReference> getCandidateIdProofListFromAddSupportCandidate(List<Integer> idProofList, Candidate candidate) {
        ArrayList<IDProofReference> response = new ArrayList<>();
        for(Integer idProofId : idProofList) {
            IDProofReference idProofReference = new IDProofReference();
            IdProof idProof= IdProof.find.where().eq("idProofId", idProofId).findUnique();
            if(idProof == null) {
                return null;
            }
            idProofReference.setIdProof(idProof);
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
    public static LoginResponse login(String loginMobile, String loginPassword){
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

                    /* START : to cater specifically the app need */
                    if(existingCandidate.getCandidateLocalityLat() != null
                            || existingCandidate.getCandidateLocalityLng() != null ){
                        loginResponse.setCandidateHomeLat(existingCandidate.getCandidateLocalityLat());
                        loginResponse.setCandidateHomeLng(existingCandidate.getCandidateLocalityLng());
                    }
                    if(!existingCandidate.getJobPreferencesList().isEmpty()){
                        if(existingCandidate.getJobPreferencesList().get(0)!= null)
                            loginResponse.setCandidatePrefJobRoleIdOne(existingCandidate.getJobPreferencesList().get(0).getJobRole().getJobRoleId());
                        if(existingCandidate.getJobPreferencesList().get(1)!= null)
                            loginResponse.setCandidatePrefJobRoleIdTwo(existingCandidate.getJobPreferencesList().get(1).getJobRole().getJobRoleId());
                        if(existingCandidate.getJobPreferencesList().get(2)!= null)
                            loginResponse.setCandidatePrefJobRoleIdThree(existingCandidate.getJobPreferencesList().get(2).getJobRole().getJobRoleId());
                    }
                    /* END */
                    if(existingCandidate.getJobPreferencesList().size() > 0){
                        loginResponse.setCandidateJobPrefStatus(1);
                    }
                    if(existingCandidate.getCandidateLocalityLat() != null && existingCandidate.getCandidateLocalityLng() != null){
                        loginResponse.setCandidateHomeLocalityStatus(1);
                    }
                    loginResponse.setStatus(loginResponse.STATUS_SUCCESS);

                    existingAuth.setAuthSessionId(UUID.randomUUID().toString());
                    existingAuth.setAuthSessionIdExpiryMillis(System.currentTimeMillis() + 24 * 60 * 60 * 1000);

                    loginResponse.setAuthSessionId(existingAuth.getAuthSessionId());
                    loginResponse.setSessionExpiryInMilliSecond(existingAuth.getAuthSessionIdExpiryMillis());

                    /* adding session details */
                    AuthService.addSession(existingAuth,existingCandidate);
                    existingAuth.update();
                    InteractionService.createInteractionForLoginCandidate(existingCandidate.getCandidateUUId(), false);
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
                SmsUtil.sendResetPasswordOTPSms(randomPIN, existingCandidate.getCandidateMobile());

                String interactionResult = ServerConstants.INTERACTION_RESULT_CANDIDATE_TRIED_TO_RESET_PASSWORD;
                String objAUUID = "";
                objAUUID = existingCandidate.getCandidateUUId();
                InteractionService.CreateInteractionForResetPasswordAttempt(objAUUID, interactionResult);
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

    private static void createAndSaveDummyAuthFor(Candidate candidate) {
        // create dummy auth
        Auth authToken = new Auth(); // constructor instantiate createtimestamp, updatetimestamp, sessionid, authpasswordsalt
        String dummyPassword = String.valueOf(Util.randomLong());
        authToken.setAuthStatus(ServerConstants.CANDIDATE_STATUS_NOT_VERIFIED);
        authToken.setCandidateId(candidate.getCandidateId());
        authToken.setPasswordMd5(Util.md5(dummyPassword + authToken.getPasswordSalt()));
        authToken.save();

        SmsUtil.sendWelcomeSmsFromSupport(candidate.getCandidateFirstName(), candidate.getCandidateMobile(), dummyPassword);
        Logger.info("Dummy auth created + otp triggered + auth saved for " + candidate.getCandidateMobile());
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

        Logger.info("Candidate with mobile number " + candidateMobile + " has % profile completion as "
                + profileCompletionPercent);

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
                if (candidate.getLanguageKnownList().size() >= 2) {
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

        Logger.info(" Candidate with mobile number " + candidate.getCandidateMobile()
                + " has " + p0CompletedFieldCount + " p0 fields completed out of " + p0FieldCount);

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

        Logger.info(" Candidate with mobile number " + candidate.getCandidateMobile()
                + " has " + p1CompletedFieldCount + " p1 fields completed out of " + p1FieldCount);

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

        Logger.info(" Candidate with mobile number " + candidate.getCandidateMobile()
                + " has " + p2CompletedFieldCount + " p2 fields completed out of " + p2FieldCount);

        return (p2CompletedFieldCount / p2FieldCount);
    }
}
