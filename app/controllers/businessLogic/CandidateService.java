package controllers.businessLogic;

import api.ServerConstants;
import api.http.*;
import com.avaje.ebean.Query;
import com.google.api.client.repackaged.com.google.common.base.Strings;
import models.entity.Auth;
import models.entity.Candidate;
import models.entity.Interaction;
import models.entity.Lead;
import models.entity.OM.*;
import models.entity.OO.CandidateCurrentJobDetail;
import models.entity.OO.CandidateEducation;
import models.entity.OO.TimeShiftPreference;
import models.entity.Static.*;
import models.util.Util;
import play.Logger;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static models.util.Util.generateOtp;
import static play.mvc.Controller.session;

/**
 * Created by batcoder1 on 3/5/16.
 */
public class CandidateService {

    public static Candidate isCandidateExists(String mobile){
        Candidate existingCandidate = Candidate.find.where().eq("candidateMobile", mobile).findUnique();
        if(existingCandidate != null) {
            return existingCandidate;
        } else {return null;}
    }

    public static Lead isLeadExists(String mobile){
        Lead existingLead = Lead.find.where().eq("leadMobile", mobile).findUnique();
        if(existingLead != null) {
            return existingLead;
        } else {return null;}
    }

    public static CandidateSignUpResponse signUpCandidate(Candidate candidate, boolean isSupport, int leadSourceId){
        CandidateSignUpResponse candidateSignUpResponse = new CandidateSignUpResponse();
        String result = "";
        String objectAUUId = "";
        Logger.info("Checking this mobile : " + candidate.candidateMobile );
        Candidate existingCandidate = isCandidateExists(candidate.candidateMobile);
        Lead existingLead = isLeadExists(candidate.candidateMobile);
        try {
            if(existingCandidate == null) {
                Logger.info("inside! existingCandidate of signUpCandidate");
                // if no candidate exists
                if(existingLead == null){
                    LeadService.createLead(getLeadFromCandidate(candidate, leadSourceId, isSupport), isSupport);
                }
                else {
                    existingLead.setLeadType(ServerConstants.TYPE_CANDIDATE);
                    existingLead.setLeadStatus(ServerConstants.LEAD_STATUS_WON);
                    existingLead.setLeadSource(getLeadSourceFromLeadSourceId(leadSourceId));
                    if(existingLead.getLeadName().trim().isEmpty()){
                        existingLead.setLeadName(candidate.candidateName + " " + candidate.candidateLastName);
                    }
                    candidate.setLead(existingLead);
                    Logger.info("Check mobile no " + existingLead.leadMobile);
                }
                CandidateProfileStatus candidateProfileStatus = CandidateProfileStatus.find.where().eq("profileStatusId", ServerConstants.CANDIDATE_STATE_NEW).findUnique();
                if(candidateProfileStatus != null){
                    candidate.setCandidateprofilestatus(candidateProfileStatus);
                    candidate.registerCandidate();
                    Logger.info("Candidate successfully registered " + candidate);
                } else {
                    candidateSignUpResponse.setStatus(CandidateSignUpResponse.STATUS_FAILURE);
                }
                if(!isSupport){
                    // triggers when candidate is self created
                    triggerOtp(candidate, candidateSignUpResponse);
                    result = ServerConstants.INTERACTION_RESULT_NEW_CANDIDATE;
                    objectAUUId = candidate.candidateUUId;
                }
            } else {
                Logger.info("CandidateExists: " + existingCandidate.candidateId + " | LeadExists: " + existingLead.leadId);
                existingLead.setLeadType(ServerConstants.TYPE_CANDIDATE);
                existingLead.setLeadStatus(ServerConstants.LEAD_STATUS_WON);
                existingLead.setLeadName(existingCandidate.candidateName);
                // TODO: need to check if already leadSource is set or not and accordingly set
                existingLead.setLeadSource(getLeadSourceFromLeadSourceId(leadSourceId));
                existingCandidate.setLead(existingLead);
                Auth auth = Auth.find.where().eq("CandidateId", existingCandidate.candidateId).findUnique();
                if(auth == null ) {
                    Logger.info("auth doesn't exists for this candidate");
                    existingCandidate.setCandidateName(candidate.candidateName);
                    existingCandidate.setCandidateLastName(candidate.candidateLastName);
                    resetLocalityAndJobPref(existingCandidate, candidate.localityPreferenceList, candidate.jobPreferencesList);
                    if(!isSupport){
                        triggerOtp(candidate, candidateSignUpResponse);
                        candidateSignUpResponse.setStatus(CandidateSignUpResponse.STATUS_SUCCESS);
                        result = ServerConstants.INTERACTION_RESULT_EXISTING_CANDIDATE_VERIFICATION;
                        objectAUUId = existingCandidate.candidateUUId;

                    } else {//TODO: will never come to this point, hence to be removed
                        createAndSaveDummyAuthFor(candidate);
                        candidateSignUpResponse.setStatus(CandidateSignUpResponse.STATUS_EXISTS);
                        result = ServerConstants.INTERACTION_RESULT_EXISTING_CANDIDATE_VERIFICATION;
                    }
                } else{
                    candidateSignUpResponse.setStatus(CandidateSignUpResponse.STATUS_EXISTS);
                    result = ServerConstants.INTERACTION_RESULT_EXISTING_CANDIDATE_SIGNUP;
                }

                existingCandidate.candidateUpdate();
            }

            // Insert Interaction only for self sign up as interaction for sign up support will be handled in createCandidateProfile
            if(!isSupport){
                Interaction interaction = new Interaction(
                        objectAUUId,
                        ServerConstants.OBJECT_TYPE_CANDIDATE,
                        ServerConstants.INTERACTION_TYPE_WEBSITE,
                        ServerConstants.INTERACTION_NOTE_SELF_SIGNEDUP,
                        result,
                        ServerConstants.INTERACTION_CREATED_SELF
                );
                InteractionService.createInteraction(interaction);
            }

        } catch (NullPointerException n){
            n.printStackTrace();
            candidateSignUpResponse.setStatus(CandidateSignUpResponse.STATUS_FAILURE);
        }
        return candidateSignUpResponse;
    }

    public static CandidateSignUpResponse createCandidateProfile(AddCandidateRequest request, boolean isSupport, int flag){
        CandidateSignUpResponse response = new CandidateSignUpResponse();
        // get candidateBasic obj from req
        // Handle jobPrefList and any other list with , as break point at application only
        Logger.info("inside create candidate " + request.candidateMobile);
        Candidate candidate = isCandidateExists(request.candidateMobile);

        if(candidate == null){
            Logger.info("No existing candidate | New Candidate");
            candidate = new Candidate();
            candidate.candidateId = Util.randomLong();
            candidate.candidateUUId = UUID.randomUUID().toString();
            candidate.candidateName = request.getCandidateFirstName();
            candidate.candidateLastName = request.getCandidateSecondName();
            candidate.candidateMobile = request.getCandidateMobile();
            CandidateProfileStatus newcandidateProfileStatus = CandidateProfileStatus.find.where().eq("profileStatusId", 1).findUnique();
            if(newcandidateProfileStatus != null){
                candidate.candidateprofilestatus = newcandidateProfileStatus;
            } else {
                Logger.info("Profile status static Table is empty");
                response.setStatus(CandidateSignUpResponse.STATUS_FAILURE);
            }
            Logger.info(" reqJobPref: " + request.candidateJobInterest);
            candidate.localityPreferenceList  = getCandidateLocalityPreferenceList(Arrays.asList(request.candidateLocality.split("\\s*,\\s*")), candidate);
            candidate.jobPreferencesList = getCandidateJobPreferenceList(Arrays.asList(request.candidateJobInterest.split("\\s*,\\s*")), candidate);
            // lead is getting updated inside signUpCandidate

            CandidateSignUpResponse candidateSignUpResponse = signUpCandidate(candidate, isSupport, request.leadSource);

            // 1st call to basic signUpCandidate
            if(candidateSignUpResponse.equals(CandidateSignUpResponse.STATUS_FAILURE)) {
                Logger.info("error while creating candidate with basic info");
                response.setStatus(CandidateSignUpResponse.STATUS_FAILURE);
                return response;
            }
        } else{
            Logger.info("Candidate Exists | Existing Candidate");
            Lead existingLead = isLeadExists(candidate.candidateMobile);
            if(existingLead == null){
                Logger.info("Candidate Found but no corresponding Lead Found !!!");
                response.setStatus(CandidateSignUpResponse.STATUS_FAILURE);
                return response;
            }
            Logger.info(" reqJobPref: " + request.candidateJobInterest);
            try{
                candidate.localityPreferenceList = getCandidateLocalityPreferenceList(Arrays.asList(request.candidateLocality.split("\\s*,\\s*")), candidate);
            } catch(Exception e){
                Logger.info(" try catch exception = " + e);
            }
            try{
                candidate.jobPreferencesList = getCandidateJobPreferenceList(Arrays.asList(request.candidateJobInterest.split("\\s*,\\s*")), candidate);
            } catch(Exception e){
                Logger.info(" try catch exception = " + e);
            }
            Logger.info("CandidateExists: " + candidate.candidateId + " | LeadExists: " + existingLead.leadId);
            existingLead.setLeadType(ServerConstants.TYPE_CANDIDATE);
            existingLead.setLeadStatus(ServerConstants.LEAD_STATUS_WON);
            existingLead.setLeadSource(getLeadSourceFromLeadSourceId(request.leadSource));
            candidate.setCandidateName(request.getCandidateFirstName());
            candidate.setCandidateLastName(request.getCandidateSecondName());
            candidate.setLead(existingLead);
        }

        if(flag == ServerConstants.UPDATE_BASIC_PROFILE || flag == ServerConstants.UPDATE_ALL_BY_SUPPORT){
            Logger.info("Inside Basic profile update");
            /* Basic Profile Section Starts */
            candidate.setCandidateName(request.candidateFirstName);
            candidate.setCandidateLastName(request.candidateSecondName);
            candidate.setCandidateUpdateTimestamp(new Timestamp(System.currentTimeMillis()));

            try{
                if(request.getCandidateDob() != null)
                    candidate.setCandidateDOB(request.getCandidateDob()); // age gets calc inside this method
            } catch(Exception e){
                Logger.info(" try catch exception = " + e);
            }

            try{
                candidate.setCandidateGender(request.getCandidateGender());
            } catch(Exception e){
                Logger.info(" try catch exception = " + e);
            }

            try{
                candidate.timeShiftPreference = getTimeShiftPrefFromAddSupportCandidate(request, candidate);
            } catch(Exception e){
                Logger.info(" try catch exception = " + e);
            }
            Logger.info("Added Basic Profile details");

            /* Basic Profile Section ends */
        }

        if(flag == ServerConstants.UPDATE_SKILLS_PROFILE || flag == ServerConstants.UPDATE_ALL_BY_SUPPORT){
        /* Experience Section starts */
            Logger.info("Inside Skills profile update");
            try{
                AddCandidateExperienceRequest addCandidateExperienceRequest = (AddCandidateExperienceRequest) request;
                try{
                    candidate.setCandidateTotalExperience(addCandidateExperienceRequest.getCandidateTotalExperience());
                } catch(Exception e){
                    Logger.info(" try catch exception = " + e);
                }

                try{
                    candidate.setCandidateIsEmployed(addCandidateExperienceRequest.getCandidateIsEmployed());
                } catch(Exception e){
                    Logger.info(" try catch exception = " + e);
                }

                try{
                    candidate.setMotherTongue(Language.find.where().eq("languageId", addCandidateExperienceRequest.getCandidateMotherTongue()).findUnique());
                } catch(Exception e){
                    Logger.info(" try catch exception = " + e);
                }

                try{
                    CandidateCurrentJobDetail candidateCurrentJobDetail = getCandidateCurrentJobDetailFromAddSupportCandidate(addCandidateExperienceRequest, candidate, isSupport);
                    candidate.candidateCurrentJobDetail = candidateCurrentJobDetail;
                } catch(Exception e){
                    Logger.info(" try catch exception Current job = " + e);
                }
                try{
                    candidate.candidateSkillList = getCandidateSkillListFromAddSupportCandidate(addCandidateExperienceRequest, candidate);
                } catch(Exception e){
                    Logger.info(" try catch exception = " + e);
                }

                try{
                    candidate.languageKnownList = getCandidateLanguageFromSupportCandidate(addCandidateExperienceRequest, candidate);
                } catch(Exception e){
                    Logger.info(" try catch exception = " + e);
                }
            } catch(Exception e){
                Logger.info(" try catch exception = " + e);
            }
        }

        if(flag == ServerConstants.UPDATE_EDUCATION_PROFILE || flag == ServerConstants.UPDATE_ALL_BY_SUPPORT){
            Logger.info("Inside Education profile update");
            try{
                AddCandidateEducationRequest addCandidateEducationRequest = (AddCandidateEducationRequest) request;
                try{
                    candidate.candidateEducation = getCandidateEducationFromAddSupportCandidate(addCandidateEducationRequest, candidate);
                } catch(Exception e){
                    Logger.info(" try catch exception = " + e);
                }
            } catch (Exception e){
                Logger.info("Try Catch Exception Main = " + e);
            }
        }



        Logger.info("Checking if support");
        if(isSupport){
            Logger.info("Is a support request");
            /* full support profile */
            AddSupportCandidateRequest supportCandidateRequest = (AddSupportCandidateRequest) request;
            candidate.setLocality(Locality.find.where().eq("localityId", supportCandidateRequest.getCandidateHomeLocality()).findUnique());
            try{
                candidate.setCandidatePhoneType(supportCandidateRequest.getCandidatePhoneType());
            } catch(Exception e){
                Logger.info(" try catch exception = " + e);
            }
            try{
                if(supportCandidateRequest.getCandidateEmail() != null)
                    candidate.setCandidateEmail(supportCandidateRequest.getCandidateEmail());
            } catch(Exception e){
                Logger.info(" try catch exception = " + e);
            }
            try{
                if(supportCandidateRequest.getCandidateMaritalStatus() != null)
                    candidate.setCandidateMaritalStatus(supportCandidateRequest.getCandidateMaritalStatus());
            } catch(Exception e){
                Logger.info(" try catch exception = " + e);
            }
            try{
                candidate.setCandidateAppointmentLetter(supportCandidateRequest.getCandidateAppointmentLetter());
            } catch(Exception e){
                Logger.info(" try catch exception = " + e);
            }
            try{
                candidate.setCandidateSalarySlip(supportCandidateRequest.getCandidateSalarySlip());
            } catch(Exception e){
                Logger.info(" try catch exception = " + e);
            }
/*
            try{
                CandidateCurrentJobDetail candidateCurrentJobDetail = getCandidateCurrentJobDetailFromAddSupportCandidate(supportCandidateRequest, candidate);
                candidate.candidateCurrentJobDetail = candidateCurrentJobDetail;
            } catch(Exception e){
                Logger.info(" try catch exception = " + e);
            }
*/

            try{
                candidate.jobHistoryList = getJobHistoryListFromAddSupportCandidate(supportCandidateRequest, candidate);
            } catch(Exception e){
                Logger.info(" try catch exception = " + e);
            }
            try{
                candidate.idProofReferenceList = getCandidateIdProofListFromAddSupportCandidate(Arrays.asList(supportCandidateRequest.candidateIdProof.split("\\s*,\\s*")), candidate);
            } catch(Exception e){
                Logger.info(" try catch exception = " + e);
            }
        }
        String interactionNote = ServerConstants.INTERACTION_NOTE_SELF_PROFILE_CREATION;
        Auth auth = Auth.find.where().eq("CandidateId", candidate.candidateId).findUnique();
        if (auth == null) {
            if(isSupport){
                // TODO: differentiate between in/out call
                createAndSaveDummyAuthFor(candidate);
                interactionNote = ServerConstants.INTERACTION_NOTE_DUMMY_PASSWORD_CREATED;
            }
        }

        String createdBy = ServerConstants.INTERACTION_CREATED_SELF;
        Integer interactionType = ServerConstants.INTERACTION_TYPE_WEBSITE;
        String interactionResult = ServerConstants.INTERACTION_RESULT_CANDIDATE_INFO_UPDATED_SELF;
        if(isSupport){
            createdBy = session().get("sessionUsername");
            interactionResult = ServerConstants.INTERACTION_RESULT_CANDIDATE_INFO_UPDATED_SYSTEM;
            interactionType = ServerConstants.INTERACTION_TYPE_CALL_OUT;
            interactionNote = ServerConstants.INTERACTION_NOTE_CALL_OUTBOUNDS;
        }
        Interaction interaction = new Interaction(
                candidate.candidateUUId,
                ServerConstants.OBJECT_TYPE_CANDIDATE,
                interactionType,
                interactionNote,
                interactionResult,
                createdBy
        );

        InteractionService.createInteraction(interaction);

        /* check Min Profile */
        if(candidate.candidateName != null && candidate.candidateLastName != null && candidate.candidateMobile != null && candidate.candidateDOB != null &&
                candidate.candidateGender != null && candidate.candidateTotalExperience != null && candidate.candidateEducation != null &&
                candidate.timeShiftPreference != null && candidate.languageKnownList.size() > 0){
            candidate.setIsMinProfileComplete(ServerConstants.CANDIDATE_MIN_PROFILE_COMPLETE);
        }
        else{
            candidate.setIsMinProfileComplete(ServerConstants.CANDIDATE_MIN_PROFILE_NOT_COMPLETE);
        }
        candidate.update();
        Logger.info("candidate CreatedBySupportSuccessfully " + candidate.candidateMobile);
        response.setStatus(CandidateSignUpResponse.STATUS_SUCCESS);

        return response;
    }

    private static List<LanguageKnown> getCandidateLanguageFromSupportCandidate(AddCandidateExperienceRequest request, Candidate candidate) {
        List<LanguageKnown> languageKnownList = new ArrayList<>();
        for(LanguageClass languageClass: request.candidateLanguageKnown){
            LanguageKnown languageKnown = new LanguageKnown();
            Language language = Language.find.where().eq("LanguageId", languageClass.getId()).findUnique();
            if(language == null) {
                Logger.info("Language static table is empty for:" + languageClass.getId());
                return null;
            }
            languageKnown.setUpdateTimeStamp(new Timestamp(System.currentTimeMillis()));
            languageKnown.setLanguage(language);
            languageKnown.setReadingAbility(languageClass.getR());
            languageKnown.setWritingAbility(languageClass.getW());
            languageKnown.setVerbalAbility(languageClass.getS());
            languageKnownList.add(languageKnown);
        }
        return languageKnownList;
    }

    private static void triggerOtp(Candidate candidate, CandidateSignUpResponse candidateSignUpResponse) {
        int randomPIN = generateOtp();
        String msg = "Welcome to Trujobs.in! Use OTP " + randomPIN + " to register";
        SendOtpService.sendSms(candidate.candidateMobile, msg);

        candidateSignUpResponse.setCandidateId(candidate.candidateId);
        candidateSignUpResponse.setCandidateName(candidate.candidateName);
        candidateSignUpResponse.setOtp(randomPIN);
        candidateSignUpResponse.setStatus(CandidateSignUpResponse.STATUS_SUCCESS);
    }

    private static List<IDProofReference> getCandidateIdProofListFromAddSupportCandidate(List<String> idProofList, Candidate candidate) {
        ArrayList<IDProofReference> response = new ArrayList<>();
        for(String  idProofId : idProofList) {
            IDProofReference idProofReference = new IDProofReference();
            IdProof idProof= IdProof.find.where().eq("idProofId", idProofId).findUnique();
            if(idProof == null) {
                return null;
            }
            idProofReference.idProof = idProof;
            idProofReference.candidate = candidate;
            idProofReference.setUpdateTimeStamp(new Timestamp(System.currentTimeMillis()));
            response.add(idProofReference);
        }
        return response;
    }

    private static CandidateEducation getCandidateEducationFromAddSupportCandidate(AddCandidateEducationRequest request, Candidate candidate) {
        CandidateEducation response  = CandidateEducation.find.where().eq("candidateId", candidate.candidateId).findUnique();
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
        for(SkillMapClass item: request.candidateSkills){
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
            Logger.info("skill........ " + skillQualifier.qualifier);
        }
        return response;
    }

    private static List<JobHistory> getJobHistoryListFromAddSupportCandidate(AddSupportCandidateRequest request, Candidate candidate) {
        List<JobHistory> response = new ArrayList<>();
        // TODO: loop through the req and then store it in List
        JobHistory jobHistory = new JobHistory();
        jobHistory.setCandidate(candidate);
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
        TimeShiftPreference response = TimeShiftPreference.find.where().eq("candidateId",candidate.candidateId).findUnique();
        if(response == null){
            response = new TimeShiftPreference();
            response.candidate = candidate;
        }
        TimeShift existingTimeShift = TimeShift.find.where().eq("timeShiftId", request.getCandidateTimeShiftPref()).findUnique();
        if(existingTimeShift == null) {
            Logger.info("timeshift static table empty for Pref: " + request.getCandidateTimeShiftPref());
            return null;
        }
        response.setTimeShift(existingTimeShift);
        response.setUpdateTimeStamp(new Timestamp(System.currentTimeMillis()));
        return response;
    }

    private static CandidateCurrentJobDetail getCandidateCurrentJobDetailFromAddSupportCandidate(AddCandidateExperienceRequest request, Candidate candidate, boolean isSupport) {
        CandidateCurrentJobDetail response =  CandidateCurrentJobDetail.find.where().eq("candidateId", candidate.candidateId).findUnique();
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
        Candidate existingCandidate = Candidate.find.where().eq("candidateMobile", "+91" + loginMobile).findUnique();
        if(existingCandidate == null){
            loginResponse.setStatus(loginResponse.STATUS_NO_USER);
            Logger.info("User Does not Exists");
        }
        else {
            long candidateId = existingCandidate.candidateId;
            Auth existingAuth = Auth.find.where().eq("candidateId", candidateId).findUnique();
            if(existingAuth != null){
                if ((existingAuth.passwordMd5.equals(Util.md5(loginPassword + existingAuth.passwordSalt)))) {
                    Logger.info(existingCandidate.candidateName + " " + existingCandidate.candidateprofilestatus.profileStatusId);
                    loginResponse.setCandidateId(existingCandidate.candidateId);
                    loginResponse.setCandidateName(existingCandidate.candidateName);
                    loginResponse.setCandidateLastName(existingCandidate.candidateLastName);
                    loginResponse.setIsAssessed(existingCandidate.candidateIsAssessed);
                    loginResponse.setLeadId(existingCandidate.lead.leadId);
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
        Candidate existingCandidate = isCandidateExists("+91"+candidateMobile);
        if(existingCandidate != null){
            Logger.info("CandidateExists");
            Auth existingAuth = Auth.find.where().eq("candidateId", existingCandidate.candidateId).findUnique();
            if(existingAuth == null){
                resetPasswordResponse.setStatus(LoginResponse.STATUS_NO_USER);
                Logger.info("reset password not allowed as Auth don't exists");
            } else {
                    int randomPIN = generateOtp();
                    existingCandidate.update();
                    String msg = "Welcome to Trujobs.in! Use OTP " + randomPIN + " to reset password";
                    SendOtpService.sendSms(existingCandidate.candidateMobile, msg);
                    resetPasswordResponse.setOtp(randomPIN);
                    resetPasswordResponse.setStatus(LoginResponse.STATUS_SUCCESS);
            }
        } else{
            resetPasswordResponse.setStatus(LoginResponse.STATUS_NO_USER);
            Logger.info("reset password not allowed as Candidate don't exists");
        }
        return resetPasswordResponse;
    }

    public static List<JobPreference> getCandidateJobPreferenceList(List<String> jobsList, Candidate candidate) {
        List<JobPreference> candidateJobPreferences = new ArrayList<>();
        for(String  s : jobsList) {
            JobPreference candidateJobPreference = new JobPreference();
            candidateJobPreference.candidate = candidate;
            candidateJobPreference.setUpdateTimeStamp(new Timestamp(System.currentTimeMillis()));
            JobRole jobRole = JobRole.find.where().eq("JobRoleId", s).findUnique();
            candidateJobPreference.jobRole = jobRole;
            candidateJobPreferences.add(candidateJobPreference);
        }
        return candidateJobPreferences;
    }

    public static List<LocalityPreference> getCandidateLocalityPreferenceList(List<String> localityList, Candidate candidate) {
        List<LocalityPreference> candidateLocalityPreferenceList = new ArrayList<>();
        for(String  localityId : localityList) {
            LocalityPreference candidateLocalityPreference = new LocalityPreference();
            candidateLocalityPreference.candidate= candidate;
            candidateLocalityPreference.setUpdateTimeStamp(new Timestamp(System.currentTimeMillis()));
            Locality locality = Locality.find.where()
                    .eq("localityId", localityId).findUnique();
            candidateLocalityPreference.locality = locality;
            Logger.info("candiateLocalitypref"+candidateLocalityPreference.locality + " == " + localityId);
            candidateLocalityPreferenceList.add(candidateLocalityPreference);
        }
        return candidateLocalityPreferenceList;
    }

    // extract lead features from candidate obj and returns a lead object
    private static Lead getLeadFromCandidate(Candidate candidate, int leadSourceId, boolean isSupport) {
        // call this function only to create new lead
        int leadChannel = ServerConstants.LEAD_CHANNEL_WEBSITE;
        if(isSupport){
            leadChannel = ServerConstants.LEAD_CHANNEL_SUPPORT;
        }
        Lead lead = new Lead(
                candidate.candidateName,
                candidate.candidateMobile,
                leadChannel,
                ServerConstants.TYPE_CANDIDATE,
                leadSourceId
        );
        lead.setLeadStatus(ServerConstants.LEAD_STATUS_WON);
        candidate.lead = lead;
        return lead;
    }

    private static LeadSource getLeadSourceFromLeadSourceId(int leadSourceId) {
        LeadSource leadSource = LeadSource.find.where().eq("leadSourceId", leadSourceId).findUnique();
        if(leadSource == null){
            Logger.info(" Static table Leadsource doesn't have entry for leadSourceId: " + leadSourceId);
        }
        return leadSource;
    }

    private static void createAndSaveDummyAuthFor(Candidate candidate) {
        // create dummy auth
        Auth authToken = new Auth();
        String dummyPassword = String.valueOf(Util.randomLong());
        authToken.authStatus = ServerConstants.CANDIDATE_STATUS_NOT_VERIFIED;
        authToken.authCreateTimestamp = new Timestamp(System.currentTimeMillis());
        authToken.authUpdateTimestamp = new Timestamp(System.currentTimeMillis());
        authToken.candidateId = candidate.candidateId;
        authToken.passwordSalt = Util.randomInt();
        authToken.passwordMd5 = Util.md5(dummyPassword + authToken.passwordSalt);
        authToken.save();
        String msg = "Welcome to Trujobs.in! Your login details are Username: " + candidate.candidateMobile.substring(3, 13) + " and password: " +dummyPassword+ ". Use this to login at trujobs.in !!";
        SendOtpService.sendSms(candidate.candidateMobile, msg);
        Logger.info("Dummy auth created + otp triggered + auth saved");
    }
    private static void resetLocalityAndJobPref(Candidate existingCandidate, List<LocalityPreference> localityPreferenceList, List<JobPreference> jobPreferencesList) {

        // reset pref
        List<LocalityPreference> allLocality = LocalityPreference.find.where().eq("CandidateId", existingCandidate.candidateId).findList();
        for(LocalityPreference candidateLocality : allLocality){
            candidateLocality.delete();
        }

        List<JobPreference> allJob = JobPreference.find.where().eq("CandidateId", existingCandidate.candidateId).findList();
        for(JobPreference candidateJobs : allJob){
            candidateJobs.delete();
        }
        existingCandidate.localityPreferenceList = localityPreferenceList;
        existingCandidate.jobPreferencesList = jobPreferencesList;
    }

    public static List<Candidate> searchCandidateBySupport(SearchCandidateRequest searchCandidateRequest) {
        // TODO:check searchCandidateRequest member variable for special char, null value
        List<String> jobInterestIdList = Arrays.asList(searchCandidateRequest.candidateJobInterest.split("\\s*,\\s*"));
        List<String> localityPreferenceIdList = Arrays.asList(searchCandidateRequest.candidateLocality.split("\\s*,\\s*"));

       // Logger.info("fromdate :" + searchCandidateRequest.getFromThisDate().getTime() + "-" + " toThisDate" + searchCandidateRequest.getToThisDate().getTime());
        Query<Candidate> query = Candidate.find.query();

        if(jobInterestIdList != null && jobInterestIdList.get(0) != "") {
           query = query.select("*").fetch("jobPreferencesList")
                    .where()
                    .in("jobPreferencesList.jobRole.jobRoleId", jobInterestIdList)
                    .query();
        }
        if(localityPreferenceIdList != null && localityPreferenceIdList.get(0) != "") {
            query = query.select("*").fetch("localityPreferenceList")
                    .where()
                    .in("localityPreferenceList.locality.localityId", localityPreferenceIdList)
                    .query();
        }
        if(searchCandidateRequest.getCandidateName() != null && !searchCandidateRequest.getCandidateName().isEmpty()) {
            query = query.where().like("candidateName",
                    searchCandidateRequest.getCandidateName() + "%").query();
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
