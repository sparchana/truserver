package controllers.businessLogic;

import api.ServerConstants;
import api.http.*;
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

    public static CandidateSignUpResponse createCandidate(Candidate candidate, boolean isSupport){
        CandidateSignUpResponse candidateSignUpResponse = new CandidateSignUpResponse();
        Interaction interaction = new Interaction();
        Logger.info("Checking this mobile : " + candidate.candidateMobile );
        Candidate existingCandidate = isCandidateExists(candidate.candidateMobile);
        Lead existingLead = isLeadExists(candidate.candidateMobile);
        try {
            if(existingCandidate == null) {
                Logger.info("inside !existingCandidate of createCandidate");
                // if no candidate exists
                if(existingLead == null){
                    LeadService.createLead(getLeadFromCandidate(candidate), isSupport);
                }
                else {
                    existingLead.setLeadType(ServerConstants.TYPE_CANDIDATE);
                    existingLead.setLeadStatus(ServerConstants.LEAD_STATUS_WON);
                    candidate.setLead(existingLead);
                    Logger.info("Check mobile no " + existingLead.leadMobile);
                }
                CandidateProfileStatus candidateProfileStatus = CandidateProfileStatus.find.where().eq("profileStatusId", ServerConstants.CANDIDATE_STATE_NEW).findUnique();
                if(candidateProfileStatus != null){
                    candidate.setCandidateprofilestatus(candidateProfileStatus);
                    Logger.info("Candidate successfully registered " + candidate);
                    candidate.registerCandidate();
                    if(Candidate.find.where().eq("candidateId", candidate.candidateId).findUnique() != null){
                        Logger.info("stupid break");
                    }
                } else {
                    candidateSignUpResponse.setStatus(CandidateSignUpResponse.STATUS_FAILURE);
                }
                interaction.result = "New Candidate Added";
                if(!isSupport){
                    triggerOtp(candidate, candidateSignUpResponse);
                    interaction.result = "New Candidate Added by support";
                }

                interaction.objectAUUId = candidate.candidateUUId;
                interaction.objectAType = ServerConstants.OBJECT_TYPE_CANDIDATE;
                interaction.interactionType = ServerConstants.INTERACTION_TYPE_WEBSITE;
                InteractionService.createInteraction(interaction);

            } else {
                Logger.info("CandidateExists: " + existingCandidate.candidateId + " | LeadExists: " + existingLead.leadId);
                existingLead.setLeadType(ServerConstants.TYPE_CANDIDATE);
                existingLead.setLeadStatus(ServerConstants.LEAD_STATUS_WON);
                existingCandidate.setLead(existingLead);
                Auth auth = Auth.find.where().eq("CandidateId", existingCandidate.candidateId).findUnique();
                if(auth == null ){
                    Logger.info("auth doesn't exists for this canidate");
                    resetLocalityAndJobPref(existingCandidate, candidate.localityPreferenceList, candidate.jobPreferencesList);
                    if(!isSupport){
                        triggerOtp(candidate, candidateSignUpResponse);
                        candidateSignUpResponse.setStatus(CandidateSignUpResponse.STATUS_SUCCESS);
                        interaction.interactionType = ServerConstants.INTERACTION_TYPE_WEBSITE;
                        interaction.setCreatedBy("Self");
                    } else {
                        createAndSaveDummpyAuthFor(candidate);
                        candidateSignUpResponse.setStatus(CandidateSignUpResponse.STATUS_EXISTS);
                        interaction.interactionType = ServerConstants.INTERACTION_TYPE_WEBSITE;
                        interaction.setNote("Candidate got Registered with Mandatory Info and dummy password by system");
                        interaction.setCreatedBy("System");
                    }
                } else{
                    candidateSignUpResponse.setStatus(CandidateSignUpResponse.STATUS_EXISTS);
                }

                interaction.setObjectAUUId(existingCandidate.candidateUUId);
                interaction.setObjectAType(ServerConstants.OBJECT_TYPE_CANDIDATE);
                interaction.result = "Candidate updated jobPref and locality pref";
                interaction.setCreationTimestamp(new Timestamp(System.currentTimeMillis()));
                InteractionService.createInteraction(interaction);

                existingCandidate.candidateUpdate();
            }
        } catch (NullPointerException n){
            n.printStackTrace();
            candidateSignUpResponse.setStatus(CandidateSignUpResponse.STATUS_FAILURE);
        }
        return candidateSignUpResponse;
    }

    public static CandidateSignUpResponse createCandidateBySupport(AddSupportCandidateRequest request){
        CandidateSignUpResponse response = new CandidateSignUpResponse();
        // get candidateBasic obj from req
        // Handle jobPrefList and any other list with , as break point at application only
        boolean isSupport = true;
        Candidate candidate = isCandidateExists(request.candidateMobile);
        if(candidate == null){
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
            // lead is getting updated inside createCandidate 
            CandidateSignUpResponse candidateSignUpResponse = createCandidate(candidate, isSupport);

            // 1st call to basic createCandidate
            if(candidateSignUpResponse == null) {
                Logger.info("error while creating candidate with basic info");
                response.setStatus(CandidateSignUpResponse.STATUS_FAILURE);
                return response;
            }
        } else{
            Lead existingLead = isLeadExists(candidate.candidateMobile);
            Logger.info(" reqJobPref: " + request.candidateJobInterest);
            candidate.localityPreferenceList  = getCandidateLocalityPreferenceList(Arrays.asList(request.candidateLocality.split("\\s*,\\s*")), candidate);
            candidate.jobPreferencesList = getCandidateJobPreferenceList(Arrays.asList(request.candidateJobInterest.split("\\s*,\\s*")), candidate);
            Logger.info("CandidateExists: " + candidate.candidateId + " | LeadExists: " + existingLead.leadId);
            existingLead.setLeadType(ServerConstants.TYPE_CANDIDATE);
            existingLead.setLeadStatus(ServerConstants.LEAD_STATUS_WON);
            candidate.setLead(existingLead);
        }
        candidate.setCandidateUpdateTimestamp(new Timestamp(System.currentTimeMillis()));
        candidate.setCandidatePhoneType(request.getCandidatePhoneType());
        candidate.setCandidateTotalExperience(request.getCandidateTotalExperience());
        candidate.setCandidateDOB(request.getCandidateDob()); // age gets calc inside this method
        candidate.setCandidateEmail(request.getCandidateEmail());
        candidate.setCandidateGender(request.getCandidateGender());
        candidate.setCandidateIsEmployed(request.getCandidateIsEmployed());
        candidate.setCandidateMaritalStatus(request.getCandidateMaritalStatus());
        candidate.setLocality(Locality.find.where().eq("localityId", request.getCandidateHomeLocality()).findUnique());
        candidate.setMotherTongue(Language.find.where().eq("languageId", request.getCandidateMotherTongue()).findUnique());

        CandidateCurrentJobDetail candidateCurrentJobDetail = getCandidateCurrentJobDetailFromAddSupportCandidate(request, candidate);
        candidate.candidateCurrentJobDetail = candidateCurrentJobDetail;
        candidate.timeShiftPreference = getTimeShiftPrefFromAddSupportCandidate(request, candidate);
        candidate.jobHistoryList = getJobHistoryListFromAddSupportCandidate(request, candidate);
        candidate.idProofReferenceList = getCandidateIdProofListFromAddSupportCandidate(Arrays.asList(request.candidateIdProof.split("\\s*,\\s*")), candidate);
        candidate.candidateSkillList = getCandidateSkillListFromAddSupportCandidate(request, candidate);
        candidate.candidateEducation = getCandidateEducationFromAddSupportCandidate(request, candidate);
        candidate.languageKnownList = getCandidateLanguageFromSupportCandidate(request, candidate);

        Interaction interaction = new Interaction();
        Auth auth = Auth.find.where().eq("CandidateId", candidate.candidateId).findUnique();
        if (auth == null) {
            createAndSaveDummpyAuthFor(candidate);
            interaction.setNote("Candidate got Registered with dummy password by system");
            interaction.setCreatedBy("System");
        }

        interaction.interactionType = ServerConstants.INTERACTION_TYPE_CALL_OUT;
        interaction.setObjectAUUId(candidate.candidateUUId);
        interaction.setObjectAType(ServerConstants.OBJECT_TYPE_CANDIDATE);
        interaction.setResult("Candidate Info got updated by System");
        interaction.setNote("Out Bound Call");
        InteractionService.createInteraction(interaction);

        candidate.update();
        response.setStatus(CandidateSignUpResponse.STATUS_SUCCESS);

        return response;
    }

    private static List<LanguageKnown> getCandidateLanguageFromSupportCandidate(AddSupportCandidateRequest request, Candidate candidate) {
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

    private static CandidateEducation getCandidateEducationFromAddSupportCandidate(AddSupportCandidateRequest request, Candidate candidate) {
        CandidateEducation response  = CandidateEducation.find.where().eq("candidateId", candidate.candidateId).findUnique();
        Education education = Education.find.where().eq("educationId", request.getCandidateEducationLevel()).findUnique();
        Degree degree = Degree.find.where().eq("degreeId", request.getCandidateDegree()).findUnique();
        if(response == null){
            response = new CandidateEducation();
            response.setCandidate(candidate);
        }
        if(education == null){
            Logger.info("education static table empty! Error: while adding education");
            return null;
        } if(degree == null){
            Logger.info("degree static table empty! Error: while adding education");
            return null;
        } else {
            response.setUpdateTimeStamp(new Timestamp(System.currentTimeMillis()));
            response.setEducation(education);
            response.setDegree(degree);
            response.setCandidateLastInstitute(request.getCandidateEducationInstitute());
        }
        return response;
    }

    private static List<CandidateSkill> getCandidateSkillListFromAddSupportCandidate(AddSupportCandidateRequest request, Candidate candidate) {
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
        JobRole jobRole = JobRole.find.where().eq("jobRoleId",request.getCandidatePastJobRole()).findUnique();
        if(jobRole == null) {
            Logger.info("jobRole staic table empty. Error : Adding jobHistory");
            return null;
        }
        jobHistory.setJobRole(jobRole);
        response.add(jobHistory);
        return response;
    }

    private static TimeShiftPreference getTimeShiftPrefFromAddSupportCandidate(AddSupportCandidateRequest request, Candidate candidate) {
        TimeShiftPreference response = TimeShiftPreference.find.where().eq("candidateId",candidate.candidateId).findUnique();
        if(response == null){
            response = new TimeShiftPreference();
            TimeShift existingTimeShift = TimeShift.find.where().eq("timeShiftId", request.getCandidateTimeShiftPref()).findUnique();
            if(existingTimeShift == null) {
                Logger.info("timeshift staic table empty for Pref: " + request.getCandidateTimeShiftPref());
                return null;
            }
            response.setTimeShift(existingTimeShift);
            response.candidate = candidate;
            response.setUpdateTimeStamp(new Timestamp(System.currentTimeMillis()));
        }
        return response;
    }

    private static CandidateCurrentJobDetail getCandidateCurrentJobDetailFromAddSupportCandidate(AddSupportCandidateRequest request, Candidate candidate) {
        CandidateCurrentJobDetail response =  CandidateCurrentJobDetail.find.where().eq("candidateId", candidate.candidateId).findUnique();
        if(response == null){
            response = new CandidateCurrentJobDetail();
            response.setCandidate( candidate);
        }
        response.setUpdateTimeStamp( new Timestamp(System.currentTimeMillis()));
        response.setCandidateCurrentCompany( request.getCandidateCurrentCompany());
        response.setCandidateCurrentDesignation( request.getCandidateCurrentJobDesignation());
        response.setCandidateCurrentSalary(request.getCandidateCurrentSalary());
        response.setCandidateCurrentJobDuration(request.getCandidateCurrentJobDuration());
        response.setCandidateCurrentEmployerRefMobile("na");
        response.setCandidateCurrentEmployerRefName("na");

        TransportationMode transportationMode = TransportationMode.find.where().eq("transportationModeId", request.getCandidateTransportation()).findUnique();
        TimeShift timeShift = TimeShift.find.where().eq("timeShiftId", request.getCandidateCurrentWorkShift()).findUnique();
        JobRole jobRole = JobRole.find.where().eq("jobRoleId",request.getCandidateCurrentJobRole()).findUnique();
        Locality locality = Locality.find.where().eq("localityId", request.getCandidateCurrentJobLocation()).findUnique();
        if(timeShift == null || jobRole == null || locality == null){
            return null;
        }
        response.setCandidateTransportationMode(transportationMode);
        response.setCandidateCurrentWorkShift(timeShift);
        response.setCandidateCurrentJobLocation(locality);
        response.setJobRole(jobRole);
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
                    loginResponse.setAccountStatus(existingCandidate.candidateprofilestatus.profileStatusId);
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
    private static Lead getLeadFromCandidate(Candidate candidate) {
        // call this fuction only to create new lead
        Lead lead = new Lead();
        lead.leadId = Util.randomLong();
        lead.leadUUId = UUID.randomUUID().toString();
        lead.leadName = candidate.candidateName;
        lead.leadMobile = candidate.candidateMobile;
        lead.leadChannel = ServerConstants.LEAD_CHANNEL_WEBSITE;
        lead.leadType = ServerConstants.TYPE_CANDIDATE;
        lead.leadStatus = ServerConstants.LEAD_STATUS_WON;
        candidate.lead = lead;
        return lead;
    }

    private static void createAndSaveDummpyAuthFor(Candidate candidate) {
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
        String msg = "Welcome to Trujobs.in! Your login details are Username: " + candidate.candidateMobile + " and password: " +dummyPassword+ ". Use this to login at trujobs.in !!";
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
}
