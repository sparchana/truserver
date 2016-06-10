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
        Logger.info("Checking for mobile number: " + candidate.getCandidateMobile());
        Candidate existingCandidate = isCandidateExists(candidate.getCandidateMobile());
        Lead existingLead = isLeadExists(candidate.getCandidateMobile());
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
                        existingLead.setLeadName(candidate.getCandidateName()+ " " + candidate.getCandidateLastName());
                    }
                    candidate.setLead(existingLead);
                    Logger.info("Check mobile no " + existingLead.getLeadMobile());
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
                    objectAUUId = candidate.getCandidateUUId();
                }
            } else {
                Logger.info("CandidateExists: " + existingCandidate.getCandidateId() + " | LeadExists: " + existingLead.getLeadId());
                existingLead.setLeadType(ServerConstants.TYPE_CANDIDATE);
                existingLead.setLeadStatus(ServerConstants.LEAD_STATUS_WON);
                existingLead.setLeadName(existingCandidate.getCandidateName());
                // TODO: need to check if already leadSource is set or not and accordingly set
                existingLead.setLeadSource(getLeadSourceFromLeadSourceId(leadSourceId));
                existingCandidate.setLead(existingLead);
                Auth auth = Auth.find.where().eq("CandidateId", existingCandidate.getCandidateId()).findUnique();
                if(auth == null ) {
                    Logger.info("auth doesn't exists for this candidate");
                    existingCandidate.setCandidateName(candidate.getCandidateName());
                    existingCandidate.setCandidateLastName(candidate.getCandidateLastName());
                    resetLocalityAndJobPref(existingCandidate, candidate.getLocalityPreferenceList(), candidate.getJobPreferencesList());
                    if(!isSupport){
                        triggerOtp(candidate, candidateSignUpResponse);
                        candidateSignUpResponse.setStatus(CandidateSignUpResponse.STATUS_SUCCESS);
                        result = ServerConstants.INTERACTION_RESULT_EXISTING_CANDIDATE_VERIFICATION;
                        objectAUUId = existingCandidate.getCandidateUUId();

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
            candidate.setCandidateName(request.getCandidateFirstName());
            candidate.setCandidateLastName(request.getCandidateSecondName());
            candidate.setCandidateMobile(request.getCandidateMobile());
            CandidateProfileStatus newcandidateProfileStatus = CandidateProfileStatus.find.where().eq("profileStatusId", 1).findUnique();
            if(newcandidateProfileStatus != null){
                candidate.setCandidateprofilestatus(newcandidateProfileStatus);
            } else {
                Logger.info("Profile status static Table is empty");
                response.setStatus(CandidateSignUpResponse.STATUS_FAILURE);
            }
            Logger.info(" reqJobPref: " + request.candidateJobInterest);

            candidate.setLocalityPreferenceList(getCandidateLocalityPreferenceList(request.candidateLocality, candidate));
            candidate.setJobPreferencesList(getCandidateJobPreferenceList(request.candidateJobInterest, candidate));
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
            Lead existingLead = isLeadExists(candidate.getCandidateMobile());
            if(existingLead == null){
                Logger.info("Candidate Found but no corresponding Lead Found !!!");
                response.setStatus(CandidateSignUpResponse.STATUS_FAILURE);
                return response;
            }
            Logger.info(" reqJobPref: " + request.candidateJobInterest);
            try{
                candidate.setLocalityPreferenceList(getCandidateLocalityPreferenceList(request.candidateLocality, candidate));
            } catch(Exception e){
                Logger.info(" try catch exception localityPreferenceList  = " + e);
            }
            try{
                candidate.setJobPreferencesList(getCandidateJobPreferenceList(request.candidateJobInterest, candidate));
            } catch(Exception e){
                Logger.info(" try catch exception jobPreferencesList  = " + e);
            }
            Logger.info("CandidateExists: " + candidate.getCandidateId() + " | LeadExists: " + existingLead.getLeadId());
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
                Logger.info(" try catch exception andidate = " + e);
            }

            try{
                candidate.setCandidateGender(request.getCandidateGender());
            } catch(Exception e){
                Logger.info(" try catch exception setCandidateGender = " + e);
            }

            try{
                candidate.setTimeShiftPreference(getTimeShiftPrefFromAddSupportCandidate(request, candidate));
            } catch(Exception e){
                Logger.info(" try catch exception timeShiftPreference  = " + e);
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
                    Logger.info(" try catch exception setCandidateTotalExperience = " + e);
                }

                try{
                    candidate.setCandidateIsEmployed(addCandidateExperienceRequest.getCandidateIsEmployed());
                } catch(Exception e){
                    Logger.info(" try catch exception setCandidateIsEmployed = " + e);
                }

                try{
                    candidate.setMotherTongue(Language.find.where().eq("languageId", addCandidateExperienceRequest.getCandidateMotherTongue()).findUnique());
                } catch(Exception e){
                    Logger.info(" try catch exception setMotherTongue = " + e);
                }

                try{
                    CandidateCurrentJobDetail candidateCurrentJobDetail = getCandidateCurrentJobDetailFromAddSupportCandidate(addCandidateExperienceRequest, candidate, isSupport);
                    candidate.setCandidateCurrentJobDetail(candidateCurrentJobDetail);
                } catch(Exception e){
                    Logger.info(" try catch exception Current job candidateCurrentJobDetail  = " + e);
                }
                try{
                    candidate.setCandidateSkillList(getCandidateSkillListFromAddSupportCandidate(addCandidateExperienceRequest, candidate));
                } catch(Exception e){
                    Logger.info(" try catch exception candidateSkillList  = " + e);
                }

                try{
                    candidate.setLanguageKnownList(getCandidateLanguageFromSupportCandidate(addCandidateExperienceRequest, candidate));
                } catch(Exception e){
                    Logger.info(" try catch exception languageKnownList  = " + e);
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
                    candidate.setCandidateEducation(getCandidateEducationFromAddSupportCandidate(addCandidateEducationRequest, candidate));
                } catch(Exception e){
                    Logger.info(" try catch exception candidateEducation  = " + e);
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
            try{
                candidate.setLocality(Locality.find.where().eq("localityId", supportCandidateRequest.getCandidateHomeLocality()).findUnique());
            } catch(Exception e){
                Logger.info(" try catch exception = " + e);
            }
            try{
                candidate.setCandidatePhoneType(supportCandidateRequest.getCandidatePhoneType());
            } catch(Exception e){
                Logger.info(" try catch exception setCandidatePhoneType = " + e);
            }
            try{
                if(supportCandidateRequest.getCandidateEmail() != null)
                    candidate.setCandidateEmail(supportCandidateRequest.getCandidateEmail());
            } catch(Exception e){
                Logger.info(" try catch exception andidate = " + e);
            }
            try{
                if(supportCandidateRequest.getCandidateMaritalStatus() != null)
                    candidate.setCandidateMaritalStatus(supportCandidateRequest.getCandidateMaritalStatus());
            } catch(Exception e){
                Logger.info(" try catch exception andidate = " + e);
            }
            try{
                candidate.setCandidateAppointmentLetter(supportCandidateRequest.getCandidateAppointmentLetter());
            } catch(Exception e){
                Logger.info(" try catch exception setCandidateAppointmentLetter = " + e);
            }
            try{
                candidate.setCandidateSalarySlip(supportCandidateRequest.getCandidateSalarySlip());
            } catch(Exception e){
                Logger.info(" try catch exception setCandidateSalarySlip = " + e);
            }
/*
            try{
                CandidateCurrentJobDetail candidateCurrentJobDetail = getCandidateCurrentJobDetailFromAddSupportCandidate(supportCandidateRequest, candidate);
                candidate.setCandidateCurrentJobDetail(candidateCurrentJobDetail);
            } catch(Exception e){
                Logger.info(" try catch exception candidateCurrentJobDetail  = " + e);
            }
*/
            try{
                candidate.setJobHistoryList(getJobHistoryListFromAddSupportCandidate(supportCandidateRequest, candidate));
            } catch(Exception e){
                Logger.info(" try catch exception jobHistoryList  = " + e);
            }
            try{
                candidate.setIdProofReferenceList(getCandidateIdProofListFromAddSupportCandidate(supportCandidateRequest.candidateIdProof, candidate));
            } catch(Exception e){
                Logger.info(" try catch exception idProofReferenceList  = " + e);
            }
        }
        String interactionNote = ServerConstants.INTERACTION_NOTE_SELF_PROFILE_CREATION;
        Auth auth = Auth.find.where().eq("CandidateId", candidate.getCandidateId()).findUnique();
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
                candidate.getCandidateUUId(),
                ServerConstants.OBJECT_TYPE_CANDIDATE,
                interactionType,
                interactionNote,
                interactionResult,
                createdBy
        );

        InteractionService.createInteraction(interaction);

        /* check Min Profile */
        if(candidate.getCandidateName() != null && candidate.getCandidateLastName() != null && candidate.getCandidateMobile() != null && candidate.getCandidateDOB() != null &&
                candidate.getCandidateGender() != null && candidate.getCandidateTotalExperience() != null && candidate.getCandidateEducation() != null &&
                candidate.getTimeShiftPreference() != null && candidate.getLanguageKnownList().size() > 0){
            candidate.setIsMinProfileComplete(ServerConstants.CANDIDATE_MIN_PROFILE_COMPLETE);
        }
        else{
            candidate.setIsMinProfileComplete(ServerConstants.CANDIDATE_MIN_PROFILE_NOT_COMPLETE);
        }
        candidate.update();
        Logger.info("candidate CreatedBySupportSuccessfully " + candidate.getCandidateMobile());
        response.setStatus(CandidateSignUpResponse.STATUS_SUCCESS);

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
        String msg = "Welcome to Trujobs.in! Use OTP " + randomPIN + " to register";
        SendOtpService.sendSms(candidate.getCandidateMobile(), msg);

        candidateSignUpResponse.setCandidateId(candidate.getCandidateId());
        candidateSignUpResponse.setCandidateName(candidate.getCandidateName());
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
        CandidateEducation response  = CandidateEducation.find.where().eq("candidateId", candidate.getCandidateId()).findUnique();
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
        TimeShiftPreference response = TimeShiftPreference.find.where().eq("candidateId", candidate.getCandidateId()).findUnique();
        if(response == null){
            response = new TimeShiftPreference();
            response.setCandidate(candidate);
        }
        if(request.getCandidateTimeShiftPref() == null){
            Logger.info("timeshiftPref not provided");
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
        CandidateCurrentJobDetail response =  CandidateCurrentJobDetail.find.where().eq("candidateId", candidate.getCandidateId()).findUnique();
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
            long candidateId = existingCandidate.getCandidateId();
            Auth existingAuth = Auth.find.where().eq("candidateId", candidateId).findUnique();
            if(existingAuth != null){
                if ((existingAuth.getPasswordMd5().equals(Util.md5(loginPassword + existingAuth.getPasswordSalt())))) {
                    Logger.info(existingCandidate.getCandidateName() + " " + existingCandidate.getCandidateprofilestatus().getProfileStatusId());
                    loginResponse.setCandidateId(existingCandidate.getCandidateId());
                    loginResponse.setCandidateName(existingCandidate.getCandidateName());
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
        Candidate existingCandidate = isCandidateExists("+91"+candidateMobile);
        if(existingCandidate != null){
            Logger.info("CandidateExists");
            Auth existingAuth = Auth.find.where().eq("candidateId", existingCandidate.getCandidateId()).findUnique();
            if(existingAuth == null){
                resetPasswordResponse.setStatus(LoginResponse.STATUS_NO_USER);
                Logger.info("reset password not allowed as Auth don't exists");
            } else {
                    int randomPIN = generateOtp();
                    existingCandidate.update();
                    String msg = "Welcome to Trujobs.in! Use OTP " + randomPIN + " to reset password";
                    SendOtpService.sendSms(existingCandidate.getCandidateMobile(), msg);
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

    // extract lead features from candidate obj and returns a lead object
    private static Lead getLeadFromCandidate(Candidate candidate, int leadSourceId, boolean isSupport) {
        // call this function only to create new lead
        int leadChannel = ServerConstants.LEAD_CHANNEL_WEBSITE;
        if(isSupport){
            leadChannel = ServerConstants.LEAD_CHANNEL_SUPPORT;
        }
        Lead lead = new Lead(
                candidate.getCandidateName(),
                candidate.getCandidateMobile(),
                leadChannel,
                ServerConstants.TYPE_CANDIDATE,
                leadSourceId
        );
        lead.setLeadStatus(ServerConstants.LEAD_STATUS_WON);
        candidate.setLead(lead);
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
        Auth authToken = new Auth(); // constructor instantiate createtimestamp, updatetimestamp, sessionid, authpasswordsalt
        String dummyPassword = String.valueOf(Util.randomLong());
        authToken.setAuthStatus(ServerConstants.CANDIDATE_STATUS_NOT_VERIFIED);
        authToken.setCandidateId(candidate.getCandidateId());
        authToken.setPasswordMd5(Util.md5(dummyPassword + authToken.getPasswordSalt()));
        authToken.save();
        String msg = "Welcome to Trujobs.in! Your login details are Username: " + candidate.getCandidateMobile().substring(3, 13) + " and password: " +dummyPassword+ ". Use this to login at trujobs.in !!";
        SendOtpService.sendSms(candidate.getCandidateMobile(), msg);
        Logger.info("Dummy auth created + otp triggered + auth saved");
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
