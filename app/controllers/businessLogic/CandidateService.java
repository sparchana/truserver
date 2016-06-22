package controllers.businessLogic;

import api.ServerConstants;
import api.http.CandidateKnownLanguage;
import api.http.CandidateSkills;
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
import models.entity.OO.CandidateCurrentJobDetail;
import models.entity.OO.CandidateEducation;
import models.entity.OO.TimeShiftPreference;
import models.entity.Static.*;
import models.util.SmsUtil;
import models.util.Util;
import play.Logger;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static controllers.businessLogic.InteractionService.createInteractionForSignUpCandidate;
import static controllers.businessLogic.LeadService.createOrUpdateConvertedLead;
import static models.util.Util.generateOtp;
import static play.mvc.Controller.session;

/**
 * Created by batcoder1 on 3/5/16.
 */
public class CandidateService
{
    private static CandidateSignUpResponse createNewCandidate(Candidate candidate, Lead lead) {

        CandidateSignUpResponse candidateSignUpResponse = new CandidateSignUpResponse();
        CandidateProfileStatus candidateProfileStatus = CandidateProfileStatus.find.where().eq("profileStatusId", ServerConstants.CANDIDATE_STATE_NEW).findUnique();
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
        Candidate existingCandidate = Candidate.find.where().eq("candidateMobile", mobile).findUnique();
        if(existingCandidate != null) {
            return existingCandidate;
        } else {return null;}
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
                candidate.setLocalityPreferenceList(getCandidateLocalityPreferenceList(localityList, candidate));
                candidate.setJobPreferencesList(getCandidateJobPreferenceList(jobsList, candidate));
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

                    resetLocalityAndJobPref(candidate, getCandidateLocalityPreferenceList(localityList, candidate), getCandidateJobPreferenceList(jobsList, candidate));

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
            interactionNote = ServerConstants.INTERACTION_NOTE_CALL_OUTBOUNDS;
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

        // Now we check if we are dealing with the reqeust to update basic profile details from website (or)
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
                Logger.info("Error while updating basic profile of candidate with mobile " + candidate.getCandidateMobile());
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

            if(candidate.getCandidateIsEmployed() != null) {

                if(candidate.getCandidateIsEmployed() == 0 ) {
                    return ServerConstants.CANDIDATE_MIN_PROFILE_COMPLETE;
                }
                else{
                    if(candidate.getCandidateCurrentJobDetail().getCandidateCurrentSalary() != null){
                        return ServerConstants.CANDIDATE_MIN_PROFILE_COMPLETE;
                    }
                }
            }
            else{
                return ServerConstants.CANDIDATE_MIN_PROFILE_COMPLETE;
            }
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
            candidate.setCandidateSalarySlip(supportCandidateRequest.getCandidateSalarySlip());
        } catch(Exception e){
            Logger.info(" Exception while setting salary slip flag type");
            e.printStackTrace();
        }

        try{
            candidate.setJobHistoryList(getJobHistoryListFromAddSupportCandidate(supportCandidateRequest, candidate));
        } catch(Exception e){
            Logger.info(" Exception while setting pas job details");
            e.printStackTrace();
        }

        try{
            candidate.setIdProofReferenceList(getCandidateIdProofListFromAddSupportCandidate(supportCandidateRequest.getCandidateIdProof(), candidate));
        } catch(Exception e){
            Logger.info(" Exception while setting idproof reference list");
            e.printStackTrace();
        }

    }

    private static CandidateSignUpResponse updateEducationProfile(Candidate candidate,
                                                                  AddCandidateEducationRequest addCandidateEducationRequest) {

        CandidateSignUpResponse candidateSignUpResponse = new CandidateSignUpResponse();
        Logger.info("Inside Education profile update");
        candidateSignUpResponse.setStatus(CandidateSignUpResponse.STATUS_SUCCESS);

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

        try {
            CandidateCurrentJobDetail candidateCurrentJobDetail = getCandidateCurrentJobDetailFromAddSupportCandidate(addCandidateExperienceRequest, candidate, isSupport);
            candidate.setCandidateCurrentJobDetail(candidateCurrentJobDetail);
        } catch(Exception e){
            candidateSignUpResponse.setStatus(CandidateSignUpResponse.STATUS_FAILURE);
            Logger.info(" try catch exception Current job candidateCurrentJobDetail  = " + e);
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
            languageKnown.setUpdateTimeStamp(new Timestamp(System.currentTimeMillis()));
            languageKnown.setLanguage(language);
            languageKnown.setReadingAbility(candidateKnownLanguage.getR());
            languageKnown.setWritingAbility(candidateKnownLanguage.getW());
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
            idProofReference.setUpdateTimeStamp(new Timestamp(System.currentTimeMillis()));
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
        response.setUpdateTimeStamp(new Timestamp(System.currentTimeMillis()));
        if(!Strings.isNullOrEmpty(request.getCandidateEducationInstitute())){
            response.setCandidateLastInstitute(request.getCandidateEducationInstitute());
        }
        return response;
    }

    private static List<CandidateSkill> getCandidateSkillListFromAddSupportCandidate(AddCandidateExperienceRequest request, Candidate candidate) {
        List<CandidateSkill> response = new ArrayList<>();
        for(CandidateSkills item: request.candidateSkills){
            item.getQualifier();
            CandidateSkill candidateSkill = new CandidateSkill();
            Skill skill = Skill.find.where().eq("skillId", item.getId()).findUnique();
            if(skill == null) {
                Logger.info("skill static table empty");
                return null;
            }
            SkillQualifier skillQualifier =SkillQualifier.find.where()
                    .eq("skillId", item.getId())
                    .eq("qualifier", item.getQualifier())
                    .findUnique();
            if(skillQualifier == null){
                Logger.info("skillQualifier static table is empty");
                return null;
            }
            candidateSkill.setCandidate(candidate);
            candidateSkill.setUpdateTimeStamp(new Timestamp(System.currentTimeMillis()));
            candidateSkill.setSkill(skill);
            candidateSkill.setSkillQualifier(skillQualifier);
            response.add(candidateSkill);
            Logger.info("skill........ " + skillQualifier.getQualifier());
        }
        return response;
    }

    private static List<JobHistory> getJobHistoryListFromAddSupportCandidate(AddSupportCandidateRequest request, Candidate candidate) {
        List<JobHistory> response = new ArrayList<>();
        // TODO: loop through the req and then store it in List
        JobHistory jobHistory = new JobHistory();
        jobHistory.setCandidate(candidate);
        if((request.getCandidatePastJobSalary() == null) && (request.getCandidatePastJobCompany() == null || request.getCandidatePastJobCompany().isEmpty()) && request.getCandidatePastJobRole() == null ){
            Logger.info("No info related to Candidate Past Job was Provided");
            return null;
        }
        jobHistory.setUpdateTimeStamp(new Timestamp(System.currentTimeMillis()));
        jobHistory.setCandidatePastSalary(request.getCandidatePastJobSalary());
        jobHistory.setCandidatePastCompany(request.getCandidatePastJobCompany());
        if(request.getCandidatePastJobRole() != null){
            JobRole jobRole = JobRole.find.where().eq("jobRoleId",request.getCandidatePastJobRole()).findUnique();
            if(jobRole == null) {
                Logger.info("jobRole static table empty. Error : Adding jobHistory");
                return null;
            }
            jobHistory.setJobRole(jobRole);
        }
        response.add(jobHistory);
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
        response.setUpdateTimeStamp(new Timestamp(System.currentTimeMillis()));
        return response;
    }

    private static CandidateCurrentJobDetail getCandidateCurrentJobDetailFromAddSupportCandidate(AddCandidateExperienceRequest request, Candidate candidate, boolean isSupport) {
        CandidateCurrentJobDetail response = candidate.getCandidateCurrentJobDetail();
        if(response == null){
            response = new CandidateCurrentJobDetail();
            response.setCandidate( candidate);
        }

        Logger.info("inserting current Job details");
        try{
            response.setUpdateTimeStamp( new Timestamp(System.currentTimeMillis()));
            response.setCandidateCurrentCompany( request.getCandidateCurrentCompany());
            response.setCandidateCurrentSalary(request.getCandidateCurrentSalary());

            if(isSupport) {
                AddSupportCandidateRequest supportCandidateRequest = (AddSupportCandidateRequest) request;

                if(supportCandidateRequest.getCandidateCurrentJobDesignation() == null
                    && supportCandidateRequest.getCandidateCurrentJobDuration() == null
                    && supportCandidateRequest.getCandidateCurrentWorkShift() == null
                    && supportCandidateRequest.getCandidateCurrentJobRole() == null
                    && supportCandidateRequest.getCandidateCurrentJobLocation() == null
                    && request.getCandidateCurrentSalary() == null
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
                if(timeShift == null && jobRole == null && locality == null && request.getCandidateCurrentSalary() == null &&
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

    public static LoginResponse login(String loginMobile, String loginPassword){
        LoginResponse loginResponse = new LoginResponse();
        Logger.info(" login mobile: " + loginMobile);
        Candidate existingCandidate = Candidate.find.where().eq("candidateMobile", loginMobile).findUnique();
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
                    loginResponse.setLeadId(existingCandidate.getLead().getLeadId());
                    loginResponse.setStatus(loginResponse.STATUS_SUCCESS);

                    existingAuth.setAuthSessionId(UUID.randomUUID().toString());
                    existingAuth.setAuthSessionIdExpiryMillis(System.currentTimeMillis() + 24 * 60 * 60 * 1000);
                    session("sessionId", existingAuth.getAuthSessionId());
                    session("sessionExpiry", String.valueOf(existingAuth.getAuthSessionIdExpiryMillis()));
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
                SmsUtil.sendOTPSms(randomPIN, existingCandidate.getCandidateMobile());

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
        for(Integer  s : jobsList) {
            JobPreference candidateJobPreference = new JobPreference();
            candidateJobPreference.setCandidate(candidate);
            candidateJobPreference.setUpdateTimeStamp(new Timestamp(System.currentTimeMillis()));
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
            candidateLocalityPreference.setUpdateTimeStamp(new Timestamp(System.currentTimeMillis()));
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
    private static void resetLocalityAndJobPref(Candidate existingCandidate, List<LocalityPreference> localityPreferenceList, List<JobPreference> jobPreferencesList) {

        // reset pref
        List<LocalityPreference> allLocality = LocalityPreference.find.where().eq("CandidateId", existingCandidate.getCandidateId()).findList();
        for(LocalityPreference candidateLocality : allLocality){
            candidateLocality.delete();
        }

        List<JobPreference> allJob = JobPreference.find.where().eq("CandidateId", existingCandidate.getCandidateId()).findList();
        for(JobPreference candidateJobs : allJob){
            candidateJobs.delete();
        }
        existingCandidate.setLocalityPreferenceList(localityPreferenceList);
        existingCandidate.setJobPreferencesList(jobPreferencesList);
    }

    public static List<Candidate> searchCandidateBySupport(SearchCandidateRequest searchCandidateRequest) {
        // TODO:check searchCandidateRequest member variable for special char, null value
        List<Integer> jobInterestIdList = searchCandidateRequest.candidateJobInterest;
        List<Integer> localityPreferenceIdList = searchCandidateRequest.candidateLocality;

       // Logger.info("fromdate :" + searchCandidateRequest.getFromThisDate().getTime() + "-" + " toThisDate" + searchCandidateRequest.getToThisDate().getTime());
        Query<Candidate> query = Candidate.find.query();

        if(jobInterestIdList != null && jobInterestIdList.get(0) != null) {
           query = query.select("*").fetch("jobPreferencesList")
                    .where()
                    .in("jobPreferencesList.jobRole.jobRoleId", jobInterestIdList)
                    .query();
        }
        if(localityPreferenceIdList != null && localityPreferenceIdList.get(0) != null) {
            query = query.select("*").fetch("localityPreferenceList")
                    .where()
                    .in("localityPreferenceList.locality.localityId", localityPreferenceIdList)
                    .query();
        }
        if(searchCandidateRequest.getCandidateFirstName() != null && !searchCandidateRequest.getCandidateFirstName().isEmpty()) {
            query = query.where().like("candidateFirstName",
                    searchCandidateRequest.getCandidateFirstName() + "%").query();
        }

        if(searchCandidateRequest.getCandidateMobile() != null && !searchCandidateRequest.getCandidateMobile().isEmpty()) {
            query = query.where().like("candidateMobile",
                    "%" + searchCandidateRequest.getCandidateMobile() + "%").query();
        }
        if(searchCandidateRequest.getFromThisDate() != null) {
            query = query.where()
                    .ge("candidateCreateTimestamp", searchCandidateRequest.getFromThisDate())
                    .query();
        }
        if(searchCandidateRequest.getToThisDate() != null) {
            query = query.where()
                    .le("candidateCreateTimestamp", searchCandidateRequest.getToThisDate())
                    .query();
        }
        List<Candidate> candidateResponseList = query.findList();
        return candidateResponseList;
    }
}
