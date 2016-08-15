package controllers;

import api.ServerConstants;
import api.http.CandidateKnownLanguage;
import api.http.CandidateSkills;
import api.http.FormValidator;
import api.http.httpRequest.*;
import api.http.httpResponse.CandidateSignUpResponse;
import api.http.httpResponse.LoginResponse;
import com.google.api.client.util.Base64;
import com.google.protobuf.InvalidProtocolBufferException;
import controllers.businessLogic.AuthService;
import controllers.businessLogic.CandidateService;
import controllers.businessLogic.JobService;
import controllers.businessLogic.MatchingEngineService;
import in.trujobs.proto.*;
import in.trujobs.proto.ApplyJobRequest;
import models.entity.Candidate;
import models.entity.Company;
import models.entity.JobPost;
import models.entity.OM.*;
import models.entity.Static.*;
import play.Logger;
import play.mvc.Result;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import static models.util.Validator.isValidLocalityName;
import static play.mvc.Http.Context.Implicit.request;
import static play.mvc.Results.badRequest;
import static play.mvc.Results.ok;

/**
 * Created by zero on 25/7/16.
 */
public class TrudroidController {
    public static Result getTestProto() {
        TestMessage testMessage = null;
        try {
            TestMessage.Builder pseudoTestMessage = TestMessage.newBuilder();
            pseudoTestMessage.setTestName("Testing");
            pseudoTestMessage.setTestPage("Page 1");

            testMessage = testMessage.parseFrom(Base64.decodeBase64(Base64.encodeBase64String(pseudoTestMessage.build().toByteArray())));
        } catch (InvalidProtocolBufferException e) {
            Logger.info("Unable to parse message");
        }

        if (testMessage == null) {
            Logger.info("Invalid message");
            return badRequest();
        }

        return ok(Base64.encodeBase64String(testMessage.toByteArray()));
    }

    public static Result mLoginSubmit() {
        LogInRequest pLogInRequest = null;
        LogInResponse.Builder loginResponseBuilder = LogInResponse.newBuilder();

        try {
            String requestString = request().body().asText();
            pLogInRequest = LogInRequest.parseFrom(Base64.decodeBase64(requestString));
            LoginRequest loginRequest = new LoginRequest();
            loginRequest.setCandidateLoginMobile(pLogInRequest.getCandidateMobile());
            loginRequest.setCandidateLoginPassword(pLogInRequest.getCandidatePassword());

            LoginResponse loginResponse = CandidateService.login(loginRequest.getCandidateLoginMobile(), loginRequest.getCandidateLoginPassword());
            loginResponseBuilder.setStatus(LogInResponse.Status.valueOf(loginResponse.getStatus()));
            if (loginResponse.getStatus() == loginResponse.STATUS_SUCCESS) {
                loginResponseBuilder.setCandidateFirstName(loginResponse.getCandidateFirstName());
                if (loginResponse.getCandidateLastName() != null) {
                    loginResponseBuilder.setCandidateLastName(loginResponse.getCandidateLastName());
                } else {
                    loginResponseBuilder.setCandidateLastName("");
                }
                loginResponseBuilder.setCandidateId(loginResponse.getCandidateId());
                loginResponseBuilder.setCandidateIsAssessed(loginResponse.getIsAssessed());
                loginResponseBuilder.setLeadId(loginResponse.getLeadId());
                loginResponseBuilder.setCandidateJobPrefStatus(loginResponse.getCandidateJobPrefStatus());
                loginResponseBuilder.setCandidateHomeLocalityStatus(loginResponse.getCandidateHomeLocalityStatus());
            }

            Logger.info("Status returned = " + loginResponseBuilder.getStatus());
        } catch (InvalidProtocolBufferException e) {
            Logger.info("Unable to parse message");
        }

        if (pLogInRequest == null) {
            Logger.info("Invalid message");
            return badRequest();
        }

        return ok(Base64.encodeBase64String(loginResponseBuilder.build().toByteArray()));
    }

    public static Result mSignUp() {
        SignUpRequest pSignUpRequest = null;
        SignUpResponse.Builder signUpResponseBuilder = SignUpResponse.newBuilder();

        try {
            String requestString = request().body().asText();
            pSignUpRequest = SignUpRequest.parseFrom(Base64.decodeBase64(requestString));
            CandidateSignUpRequest candidateSignUpRequest = new CandidateSignUpRequest();
            candidateSignUpRequest.setCandidateFirstName(pSignUpRequest.getName());
            candidateSignUpRequest.setCandidateMobile(pSignUpRequest.getMobile());

            boolean isSupport = false;
            CandidateSignUpResponse candidateSignUpResponse = CandidateService.signUpCandidate(candidateSignUpRequest, isSupport, ServerConstants.LEAD_SOURCE_UNKNOWN);
            signUpResponseBuilder.setStatus(SignUpResponse.Status.valueOf(candidateSignUpResponse.getStatus()));
            signUpResponseBuilder.setGeneratedOtp(candidateSignUpResponse.getOtp());

            Logger.info("Status returned = " + signUpResponseBuilder.getStatus());

        } catch (InvalidProtocolBufferException e) {
            Logger.info("Unable to parse message");
        }

        if (pSignUpRequest == null) {
            Logger.info("Invalid message");
            return badRequest();
        }
        return ok(Base64.encodeBase64String(signUpResponseBuilder.build().toByteArray()));
    }

    public static Result mAddPassword() {
        LogInRequest pLoginRequest = null;
        LogInResponse.Builder loginResponseBuilder = LogInResponse.newBuilder();

        try {
            String requestString = request().body().asText();
            pLoginRequest = LogInRequest.parseFrom(Base64.decodeBase64(requestString));
            CandidateSignUpResponse candidateSignUpResponse = AuthService.savePassword(FormValidator.convertToIndianMobileFormat(pLoginRequest.getCandidateMobile()), pLoginRequest.getCandidatePassword());
            loginResponseBuilder.setStatus(LogInResponse.Status.valueOf(candidateSignUpResponse.getStatus()));
            loginResponseBuilder.setCandidateFirstName(candidateSignUpResponse.getCandidateFirstName());
            if (candidateSignUpResponse.getCandidateLastName() != null) {
                loginResponseBuilder.setCandidateLastName(candidateSignUpResponse.getCandidateLastName());
            }
            loginResponseBuilder.setCandidateId(candidateSignUpResponse.getCandidateId());
            loginResponseBuilder.setCandidateIsAssessed(candidateSignUpResponse.getIsAssessed());
            loginResponseBuilder.setLeadId(candidateSignUpResponse.getLeadId());
            loginResponseBuilder.setMinProfile(candidateSignUpResponse.getMinProfile());
            loginResponseBuilder.setCandidateJobPrefStatus(candidateSignUpResponse.getCandidateJobPrefStatus());
            loginResponseBuilder.setCandidateHomeLocalityStatus(candidateSignUpResponse.getCandidateHomeLocalityStatus());

            Logger.info("Status returned = " + loginResponseBuilder.getStatus());

        } catch (InvalidProtocolBufferException e) {
            Logger.info("Unable to parse message");
        }

        if (pLoginRequest == null) {
            Logger.info("Invalid message");
            return badRequest();
        }
        return ok(Base64.encodeBase64String(loginResponseBuilder.build().toByteArray()));
    }

    public static Result mFindUserAndSendOtp() {
        ResetPasswordRequest pResetPasswordRequest = null;
        ResetPasswordResponse.Builder resetPasswordResponseBuilder = ResetPasswordResponse.newBuilder();

        try {
            String requestString = request().body().asText();
            pResetPasswordRequest = ResetPasswordRequest.parseFrom(Base64.decodeBase64(requestString));
            api.http.httpResponse.ResetPasswordResponse resetPasswordResponse = CandidateService.findUserAndSendOtp(FormValidator.convertToIndianMobileFormat(pResetPasswordRequest.getMobile()));
            resetPasswordResponseBuilder.setStatus(ResetPasswordResponse.Status.valueOf(resetPasswordResponse.getStatus()));
            resetPasswordResponseBuilder.setOtp(resetPasswordResponse.getOtp());

            Logger.info("Status returned = " + resetPasswordResponseBuilder.getStatus());

        } catch (InvalidProtocolBufferException e) {
            Logger.info("Unable to parse message");
        }

        if (pResetPasswordRequest == null) {
            Logger.info("Invalid message");
            return badRequest();
        }
        return ok(Base64.encodeBase64String(resetPasswordResponseBuilder.build().toByteArray()));
    }

    public static Result mGetAllJobRoles() {
        JobRoleResponse.Builder jobRoleResponseBuilder = JobRoleResponse.newBuilder();
        List<models.entity.Static.JobRole> jobRoleList = models.entity.Static.JobRole.find.all();

        jobRoleResponseBuilder.addAllJobRole(getJobRoleObjectListFromJobRoleList(jobRoleList));
        return ok(Base64.encodeBase64String(jobRoleResponseBuilder.build().toByteArray()));
    }

    public static Result mGetAllJobPosts() {
        JobPostResponse.Builder jobPostResponseBuilder = JobPostResponse.newBuilder();
        List<JobPost> jobPostList = JobPost.find.where().eq("jobPostIsHot", ServerConstants.IS_HOT).findList();
        List<JobPostObject> jobPostListToReturn = getJobPostObjectListFromJobPostList(jobPostList);
        jobPostResponseBuilder.addAllJobPost(jobPostListToReturn);
        return ok(Base64.encodeBase64String(jobPostResponseBuilder.build().toByteArray()));
    }

    private static List<JobRoleObject> getJobRoleObjectListFromJobRoleList(List<JobRole> jobRoleList) {
        List<JobRoleObject> jobRoleListToReturn = new ArrayList<>();
        for (JobRole jobRole : jobRoleList) {
            JobRoleObject.Builder jobRoleBuilder
                    = JobRoleObject.newBuilder();
            jobRoleBuilder.setJobRoleId(jobRole.getJobRoleId());
            jobRoleBuilder.setJobRoleName(jobRole.getJobName());
            if (jobRole.getJobRoleIcon() != null) {
                jobRoleBuilder.setJobRoleIcon(jobRole.getJobRoleIcon());
            } else {
                jobRoleBuilder.setJobRoleIcon("");
            }
            jobRoleListToReturn.add(jobRoleBuilder.build());
        }
        return jobRoleListToReturn;
    }

    private static List<JobPostObject> getJobPostObjectListFromJobPostList(List<JobPost> jobPostList) {
        List<JobPostObject> jobPostListToReturn = new ArrayList<>();
        for (models.entity.JobPost jobPost : jobPostList) {
            JobPostObject.Builder jobPostBuilder
                    = JobPostObject.newBuilder();
            jobPostBuilder.setJobPostCreationMillis(jobPost.getJobPostCreateTimestamp().getTime());
            jobPostBuilder.setJobPostId(jobPost.getJobPostId());
            jobPostBuilder.setJobPostTitle(jobPost.getJobPostTitle());
            jobPostBuilder.setJobPostCompanyName(jobPost.getCompany().getCompanyName());
            jobPostBuilder.setJobPostMinSalary(jobPost.getJobPostMinSalary());
            jobPostBuilder.setJobPostMaxSalary(jobPost.getJobPostMaxSalary());
            jobPostBuilder.setVacancies(jobPost.getJobPostVacancies());

            JobRoleObject.Builder jobRoleBuilder = JobRoleObject.newBuilder();

            if (jobPost.getJobRole() != null) {
                jobRoleBuilder.setJobRoleName(jobPost.getJobRole().getJobName());
                jobRoleBuilder.setJobRoleId(jobPost.getJobRole().getJobRoleId());
                jobPostBuilder.setJobRole(jobRoleBuilder.build());
            }

            jobPostBuilder.setJobPostCompanyLogo(jobPost.getCompany().getCompanyLogo());

            ExperienceObject.Builder experienceBuilder = ExperienceObject.newBuilder();
            experienceBuilder.setExperienceId(jobPost.getJobPostExperience().getExperienceId());
            experienceBuilder.setExperienceType(jobPost.getJobPostExperience().getExperienceType());
            jobPostBuilder.setJobPostExperience(experienceBuilder);

            TimeShiftObject.Builder timeShiftBuilder = TimeShiftObject.newBuilder();
            timeShiftBuilder.setTimeShiftId(jobPost.getJobPostShift().getTimeShiftId());
            timeShiftBuilder.setTimeShiftName(jobPost.getJobPostShift().getTimeShiftName());
            jobPostBuilder.setJobPostShift(timeShiftBuilder);

            List<LocalityObject> jobPostLocalities = new ArrayList<>();
            List<JobPostToLocality> localityList = jobPost.getJobPostToLocalityList();
            for (JobPostToLocality locality : localityList) {
                LocalityObject.Builder localityBuilder
                        = LocalityObject.newBuilder();
                localityBuilder.setLocalityId(locality.getLocality().getLocalityId());
                localityBuilder.setLocalityName(locality.getLocality().getLocalityName());
                jobPostLocalities.add(localityBuilder.build());
            }
            jobPostBuilder.addAllJobPostLocality(jobPostLocalities);

            jobPostListToReturn.add(jobPostBuilder.build());
        }
        return jobPostListToReturn;
    }

    public static Result mApplyJob() {
        ApplyJobRequest pApplyJobRequest = null;
        ApplyJobResponse.Builder applyJobResponseBuilder = ApplyJobResponse.newBuilder();

        try {
            String requestString = request().body().asText();
            pApplyJobRequest = ApplyJobRequest.parseFrom(Base64.decodeBase64(requestString));
            api.http.httpRequest.ApplyJobRequest applyJobRequest = new api.http.httpRequest.ApplyJobRequest();
            applyJobRequest.setJobId(Math.toIntExact(pApplyJobRequest.getJobPostId()));
            applyJobRequest.setLocalityId(Math.toIntExact(pApplyJobRequest.getLocalityId()));
            applyJobRequest.setCandidateMobile(FormValidator.convertToIndianMobileFormat(pApplyJobRequest.getCandidateMobile()));
            api.http.httpResponse.ApplyJobResponse applyJobResponse = JobService.applyJob(applyJobRequest);
            applyJobResponseBuilder.setStatus(ApplyJobResponse.Status.valueOf(applyJobResponse.getStatus()));

            Logger.info("Status returned = " + applyJobResponseBuilder.getStatus());

        } catch (InvalidProtocolBufferException e) {
            Logger.info("Unable to parse message");
        }

        if (pApplyJobRequest == null) {
            Logger.info("Invalid message");
            return badRequest();
        }
        return ok(Base64.encodeBase64String(applyJobResponseBuilder.build().toByteArray()));
    }

    public static Result mAddJobPref() {
        AddJobRoleRequest pAddJobPrefRequest = null;
        AddJobRoleResponse.Builder addJobRoleResponseBuilder = AddJobRoleResponse.newBuilder();

        try {
            String requestString = request().body().asText();
            pAddJobPrefRequest = AddJobRoleRequest.parseFrom(Base64.decodeBase64(requestString));
            List<Integer> jobPrefList = new ArrayList<Integer>();
            if (pAddJobPrefRequest.getJobRolePrefOneId() != 0) {
                jobPrefList.add(Math.toIntExact(pAddJobPrefRequest.getJobRolePrefOneId()));
            }
            if (pAddJobPrefRequest.getJobRolePrefTwoId() != 0) {
                jobPrefList.add(Math.toIntExact(pAddJobPrefRequest.getJobRolePrefTwoId()));
            }
            if (pAddJobPrefRequest.getJobRolePrefThreeId() != 0) {
                jobPrefList.add(Math.toIntExact(pAddJobPrefRequest.getJobRolePrefThreeId()));
            }

            addJobRoleResponseBuilder.setStatus(AddJobRoleResponse.Status.valueOf(2));

            Candidate candidate = CandidateService.isCandidateExists(FormValidator.convertToIndianMobileFormat(pAddJobPrefRequest.getCandidateMobile()));
            if (candidate != null) {
                CandidateService.resetJobPref(candidate, CandidateService.getCandidateJobPreferenceList(jobPrefList, candidate));
                candidate.setJobPreferencesList(CandidateService.getCandidateJobPreferenceList(jobPrefList, candidate));
                candidate.update();
                addJobRoleResponseBuilder.setStatus(AddJobRoleResponse.Status.valueOf(1));
            }
            Logger.info("Status returned = " + addJobRoleResponseBuilder.getStatus());

        } catch (InvalidProtocolBufferException e) {
            Logger.info("Unable to parse message");
        }

        if (pAddJobPrefRequest == null) {
            Logger.info("Invalid message");
            return badRequest();
        }
        return ok(Base64.encodeBase64String(addJobRoleResponseBuilder.build().toByteArray()));
    }

    public static Result mGetCandidateInformation() {
        CandidateInformationRequest pCandidateInformationRequest = null;
        GetCandidateInformationResponse.Builder getCandidateInfoBulder = GetCandidateInformationResponse.newBuilder();
        CandidateObject.Builder candidateBuilder = CandidateObject.newBuilder();


        try {
            String requestString = request().body().asText();
            pCandidateInformationRequest = CandidateInformationRequest.parseFrom(Base64.decodeBase64(requestString));

            Candidate candidate = CandidateService.isCandidateExists(FormValidator.convertToIndianMobileFormat(pCandidateInformationRequest.getCandidateMobile()));
            if (candidate != null) {
                getCandidateInfoBulder.setStatus(GetCandidateInformationResponse.Status.valueOf(1));
                candidateBuilder.setCandidateId(candidate.getCandidateId());
                candidateBuilder.setCandidateMobile(candidate.getCandidateMobile());
                candidateBuilder.setCandidateFirstName(candidate.getCandidateFirstName());
                if (candidate.getCandidateLastName() != null) {
                    candidateBuilder.setCandidateLastName(candidate.getCandidateLastName());
                } else {
                    candidateBuilder.setCandidateLastName("");
                }
                if(candidate.getCandidateIsEmployed() != null ){
                    candidateBuilder.setCandidateIsEmployed(candidate.getCandidateIsEmployed());
                }
                candidateBuilder.setCandidateIsAssessed(candidate.getCandidateIsAssessed());
                candidateBuilder.setCandidateMinProfileComplete(candidate.getIsMinProfileComplete());
                if (candidate.getCandidateGender() != null) {
                    candidateBuilder.setCandidateGender(candidate.getCandidateGender());
                }
                if (candidate.getCandidateTotalExperience() != null) {
                    candidateBuilder.setCandidateTotalExperience(candidate.getCandidateTotalExperience());
                }

                //getting candidate DOB
                if(candidate.getCandidateDOB() != null){
                    Calendar c = Calendar.getInstance();
                    c.setTime(candidate.getCandidateDOB());
                    long time = c.getTimeInMillis();
                    candidateBuilder.setCandidateDobMillis(time);
                } else{
                    candidateBuilder.setCandidateDobMillis(0);
                }

                //getting home locality
                LocalityObject.Builder localityBuilder = LocalityObject.newBuilder();
                if (candidate.getLocality() != null) {
                    localityBuilder.setLocalityId(candidate.getLocality().getLocalityId());
                    localityBuilder.setLocalityName(candidate.getLocality().getLocalityName());
                    candidateBuilder.setCandidateHomelocality(localityBuilder);
                }

                if (candidate.getCandidateLastWithdrawnSalary() != null) {
                    candidateBuilder.setCandidateLastWithdrawnSalary(candidate.getCandidateLastWithdrawnSalary());
                }

                List<JobHistory> jobHistoryList = JobHistory.find.where().eq("candidateId", candidate.getCandidateId()).findList();
                for (JobHistory jobHistory : jobHistoryList) {
                    if (jobHistory.getCurrentJob() == true) {
                        candidateBuilder.setCandidateCurrentCompany(jobHistory.getCandidatePastCompany());
                        JobRoleObject.Builder jobRoleBuilder = JobRoleObject.newBuilder();
                        jobRoleBuilder.setJobRoleName(jobHistory.getJobRole().getJobName());
                        jobRoleBuilder.setJobRoleId(jobHistory.getJobRole().getJobRoleId());
                        candidateBuilder.setCandidateCurrentJobRole(jobRoleBuilder.build());
                        break;
                    }
                }

                //getting education
                CandidateEducationObject.Builder candidateEducationBuilder = CandidateEducationObject.newBuilder();

                if (candidate.getCandidateEducation() != null) {
                    candidateEducationBuilder.setCandidateInstitute(candidate.getCandidateEducation().getCandidateLastInstitute());
                    DegreeObject.Builder degreeBuilder = DegreeObject.newBuilder();
                    if (candidate.getCandidateEducation().getDegree() != null) {
                        degreeBuilder.setDegreeId(candidate.getCandidateEducation().getDegree().getDegreeId());
                        degreeBuilder.setDegreeName(candidate.getCandidateEducation().getDegree().getDegreeName());
                        candidateEducationBuilder.setDegree(degreeBuilder);
                    }
                    EducationObject.Builder educationBuilder = EducationObject.newBuilder();
                    if (candidate.getCandidateEducation().getEducation() != null) {
                        educationBuilder.setEducationId(candidate.getCandidateEducation().getEducation().getEducationId());
                        educationBuilder.setEducationName(candidate.getCandidateEducation().getEducation().getEducationName());
                        candidateEducationBuilder.setEducation(educationBuilder);
                    }
                    candidateBuilder.setCandidateEducation(candidateEducationBuilder);
                }

                //getting timeShift
                TimeShiftObject.Builder timeShiftBuilder = TimeShiftObject.newBuilder();
                if (candidate.getTimeShiftPreference() != null) {
                    timeShiftBuilder.setTimeShiftId(candidate.getTimeShiftPreference().getTimeShift().getTimeShiftId());
                    timeShiftBuilder.setTimeShiftName(candidate.getTimeShiftPreference().getTimeShift().getTimeShiftName());
                    candidateBuilder.setCandidateTimeShiftPref(timeShiftBuilder);
                }

                //getting jobPrefs
                List<JobPreference> jobPreferenceList = JobPreference.find.where().eq("candidateId", candidate.getCandidateId()).findList();
                if (jobPreferenceList.size() > 0) {
                    List<JobRoleObject> jobRolePrefListToReturn = new ArrayList<>();
                    for (JobPreference jobRole : jobPreferenceList) {
                        JobRoleObject.Builder jobRolePrefBuilder
                                = JobRoleObject.newBuilder();
                        jobRolePrefBuilder.setJobRoleId(jobRole.getJobRole().getJobRoleId());
                        jobRolePrefBuilder.setJobRoleName(jobRole.getJobRole().getJobName());
                        jobRolePrefListToReturn.add(jobRolePrefBuilder.build());
                    }
                    candidateBuilder.addAllCandidateJobRolePref(jobRolePrefListToReturn);
                }

                //getting localityPrefs
                List<LocalityPreference> localityPreferenceList = LocalityPreference.find.where().eq("candidateId", candidate.getCandidateId()).findList();
                if (localityPreferenceList.size() > 0) {
                    List<LocalityObject> localityPrefListToReturn = new ArrayList<>();
                    for (LocalityPreference locality : localityPreferenceList) {
                        LocalityObject.Builder localityPrefBuilder
                                = LocalityObject.newBuilder();
                        localityPrefBuilder.setLocalityId(locality.getLocality().getLocalityId());
                        localityPrefBuilder.setLocalityName(locality.getLocality().getLocalityName());
                        localityPrefListToReturn.add(localityPrefBuilder.build());
                    }
                    candidateBuilder.addAllCandidateLocationPref(localityPrefListToReturn);
                }

                //getting candidate language known
                List<LanguageKnown> languageKnownList = LanguageKnown.find.where().eq("candidateId", candidate.getCandidateId()).findList();
                if(languageKnownList.size() > 0){
                    List<LanguageKnownObject> languageKnownListToReturn = new ArrayList<LanguageKnownObject>();
                    for (LanguageKnown languageKnown : languageKnownList) {
                        LanguageKnownObject.Builder languageKnownObj
                                = LanguageKnownObject.newBuilder();
                        languageKnownObj.setLanguageKnownId(languageKnown.getLanguage().getLanguageId());
                        languageKnownObj.setLanguageReadWrite(languageKnown.getReadWrite());
                        languageKnownObj.setLanguageSpeak(languageKnown.getVerbalAbility());
                        languageKnownObj.setLanguageUnderstand(languageKnown.getUnderstanding());
                        languageKnownListToReturn.add(languageKnownObj.build());
                    }
                    candidateBuilder.addAllLanguageKnownObject(languageKnownListToReturn);
                }

                //getting candidate skills
                List<CandidateSkill> candidateSkillList = CandidateSkill.find.where().eq("candidateId", candidate.getCandidateId()).findList();
                if(candidateSkillList.size() > 0){
                    List<CandidateSkillObject> candidateSkillListToReturn = new ArrayList<CandidateSkillObject>();
                    for (CandidateSkill candidateSkill : candidateSkillList) {
                        CandidateSkillObject.Builder candidateSkillObj
                                = CandidateSkillObject.newBuilder();
                        candidateSkillObj.setSkillId(candidateSkill.getSkill().getSkillId());
                        candidateSkillObj.setAnswer(candidateSkill.isCandidateSkillResponse());
                        candidateSkillListToReturn.add(candidateSkillObj.build());
                    }
                    candidateBuilder.addAllCandidateSkillObject(candidateSkillListToReturn);
                }
                getCandidateInfoBulder.setCandidate(candidateBuilder);

                List<models.entity.Static.JobRole> jobRoleList = models.entity.Static.JobRole.find.all();
                getCandidateInfoBulder.addAllJobRoles(getJobRoleObjectListFromJobRoleList(jobRoleList));
            } else {
                getCandidateInfoBulder.setStatus(GetCandidateInformationResponse.Status.valueOf(2));
            }
        } catch (Exception e) {
            Logger.info("Unable to parse message");
        }

        if (pCandidateInformationRequest == null) {
            Logger.info("Invalid message");
            return badRequest();
        }
        return ok(Base64.encodeBase64String(getCandidateInfoBulder.build().toByteArray()));
    }

    public static JobPostObject getJobPostInformationFromJobPostObject(JobPost jobPost) {
        JobPostObject jobPostObject;
        JobPostObject.Builder jobPostBuilder = JobPostObject.newBuilder();
        JobRoleObject.Builder jobRoleBuilder = JobRoleObject.newBuilder();
        if (jobPost.getJobRole() != null) {
            jobRoleBuilder.setJobRoleName(jobPost.getJobRole().getJobName());
            jobRoleBuilder.setJobRoleId(jobPost.getJobRole().getJobRoleId());
            jobPostBuilder.setJobRole(jobRoleBuilder.build());
        }

        jobPostBuilder.setJobPostCreationMillis(jobPost.getJobPostCreateTimestamp().getTime());
        jobPostBuilder.setVacancies(jobPost.getJobPostVacancies());
        if (jobPost.getJobPostStartTime() == null) {
            jobPostBuilder.setJobPostStartTime(-1);
        } else {
            jobPostBuilder.setJobPostStartTime(jobPost.getJobPostStartTime());
        }
        if (jobPost.getJobPostEndTime() == null) {
            jobPostBuilder.setJobPostEndTime(-1);
        } else {
            jobPostBuilder.setJobPostEndTime(jobPost.getJobPostEndTime());
        }

        jobPostBuilder.setJobPostId(jobPost.getJobPostId());
        jobPostBuilder.setJobPostCompanyName(jobPost.getCompany().getCompanyName());
        jobPostBuilder.setJobPostCompanyLogo(jobPost.getCompany().getCompanyLogo());
        jobPostBuilder.setJobPostTitle(jobPost.getJobPostTitle());
        jobPostBuilder.setJobPostMinSalary(jobPost.getJobPostMinSalary());
        jobPostBuilder.setJobPostMaxSalary(jobPost.getJobPostMaxSalary());
        jobPostBuilder.setJobPostDescription(jobPost.getJobPostDescription());
        jobPostBuilder.setJobPostIncentives(jobPost.getJobPostIncentives());
        jobPostBuilder.setJobPostMinRequirements(jobPost.getJobPostMinRequirement());
        jobPostBuilder.setJobPostAddress(jobPost.getJobPostAddress());
        if (jobPost.getJobPostWorkingDays() != null) {
            jobPostBuilder.setJobPostWorkingDays(Integer.toString(jobPost.getJobPostWorkingDays(), 2));
        } else {
            jobPostBuilder.setJobPostWorkingDays("");
        }

        List<LocalityObject> jobPostLocalities = new ArrayList<>();
        List<JobPostToLocality> localityList = jobPost.getJobPostToLocalityList();
        for (JobPostToLocality locality : localityList) {
            LocalityObject.Builder localityBuilder = LocalityObject.newBuilder();
            localityBuilder.setLocalityId(locality.getLocality().getLocalityId());
            localityBuilder.setLocalityName(locality.getLocality().getLocalityName());
            jobPostLocalities.add(localityBuilder.build());
        }
        jobPostBuilder.addAllJobPostLocality(jobPostLocalities);

        if (jobPost.getJobPostExperience() != null) {
            ExperienceObject.Builder experienceBuilder = ExperienceObject.newBuilder();
            experienceBuilder.setExperienceId(jobPost.getJobPostExperience().getExperienceId());
            experienceBuilder.setExperienceType(jobPost.getJobPostExperience().getExperienceType());
            jobPostBuilder.setJobPostExperience(experienceBuilder);
        }

        TimeShiftObject.Builder timeShiftBuilder = TimeShiftObject.newBuilder();
        timeShiftBuilder.setTimeShiftId(jobPost.getJobPostShift().getTimeShiftId());
        timeShiftBuilder.setTimeShiftName(jobPost.getJobPostShift().getTimeShiftName());
        jobPostBuilder.setJobPostShift(timeShiftBuilder);

        if (jobPost.getJobPostEducation() != null) {
            EducationObject.Builder educationBuilder = EducationObject.newBuilder();
            educationBuilder.setEducationId(jobPost.getJobPostEducation().getEducationId());
            educationBuilder.setEducationName(jobPost.getJobPostEducation().getEducationName());
            jobPostBuilder.setEducation(educationBuilder);
        }
        jobPostObject = jobPostBuilder.build();

        return jobPostObject;

    }

    public static CompanyObject getCompanyInfoFromCompanyObject(Company company, JobPost jobPost){
        CompanyObject.Builder companyBuilder = CompanyObject.newBuilder();
        companyBuilder.setCompanyName(company.getCompanyName());
        companyBuilder.setCompanyId(company.getCompanyId());
        companyBuilder.setCompanyAddress(company.getCompanyAddress());

        LocalityObject.Builder companyLocality = LocalityObject.newBuilder();

        if (company.getCompanyLocality() != null) {
            companyLocality.setLocalityName(company.getCompanyLocality().getLocalityName());
            companyLocality.setLocalityId(company.getCompanyLocality().getLocalityId());
            companyBuilder.setCompanyLocality(companyLocality.build());
        }

        if (company.getCompanyDescription() != null) {
            companyBuilder.setCompanyDescription(company.getCompanyDescription());
        }
        if (company.getCompanyEmployeeCount() != null) {
            companyBuilder.setCompanyEmployeeCount(company.getCompanyEmployeeCount());
        }
        if (company.getCompanyLogo() != null) {
            companyBuilder.setCompanyLogo(company.getCompanyLogo());
        }

        if (company.getCompType() != null) {
            CompanyTypeObject.Builder companyTypeBuilder = CompanyTypeObject.newBuilder();
            companyTypeBuilder.setCompanyTypeId(company.getCompType().getCompanyTypeId());
            companyTypeBuilder.setCompanyTypeName(company.getCompType().getCompanyTypeName());
            companyBuilder.setCompanyType(companyTypeBuilder);
        }

        if (company.getCompanyWebsite() != null) {
            companyBuilder.setCompanyWebsite(company.getCompanyWebsite());
        }

        List<JobPost> similarJobs = JobPost.find.where().eq("jobPostIsHot", "1").eq("companyId", company.getCompanyId()).findList();
        List<JobPostObject> similarJobPostListToReturn = new ArrayList<>();
        for (models.entity.JobPost companyJobPost : similarJobs) {
            if (companyJobPost.getJobPostId() != jobPost.getJobPostId()) {
                JobPostObject.Builder companyJobPostBuilder
                        = JobPostObject.newBuilder();

                JobRoleObject.Builder similarJobRoleBuilder = JobRoleObject.newBuilder();

                if (companyJobPost.getJobRole() != null) {
                    similarJobRoleBuilder.setJobRoleName(jobPost.getJobRole().getJobName());
                    similarJobRoleBuilder.setJobRoleId(jobPost.getJobRole().getJobRoleId());
                    companyJobPostBuilder.setJobRole(similarJobRoleBuilder.build());
                }

                companyJobPostBuilder.setJobPostId(companyJobPost.getJobPostId());
                companyJobPostBuilder.setJobPostTitle(companyJobPost.getJobPostTitle());
                companyJobPostBuilder.setJobPostMinSalary(companyJobPost.getJobPostMinSalary());
                companyJobPostBuilder.setJobPostMaxSalary(companyJobPost.getJobPostMaxSalary());

                List<LocalityObject> similarJobPostLocalities = new ArrayList<>();
                List<LocalityObject> jobPostLocalities = new ArrayList<>();
                List<JobPostToLocality> similarJobLocalityList = companyJobPost.getJobPostToLocalityList();
                for (JobPostToLocality locality : similarJobLocalityList) {
                    LocalityObject.Builder localityBuilder
                            = LocalityObject.newBuilder();
                    localityBuilder.setLocalityId(locality.getLocality().getLocalityId());
                    localityBuilder.setLocalityName(locality.getLocality().getLocalityName());
                    similarJobPostLocalities.add(localityBuilder.build());
                }
                companyJobPostBuilder.addAllJobPostLocality(jobPostLocalities);

                similarJobPostListToReturn.add(companyJobPostBuilder.build());
            }
        }
        companyBuilder.addAllCompanyOtherJobs(similarJobPostListToReturn);

        CompanyObject companyObject = companyBuilder.build();
        return companyObject;
    }

    public static Result mGetJobPostInfo() {
        GetJobPostDetailsRequest pGetJobPostDetailsRequest = null;
        GetJobPostDetailsResponse.Builder getJobPostDetailsResponse = GetJobPostDetailsResponse.newBuilder();
        try {
            String requestString = request().body().asText();
            pGetJobPostDetailsRequest = GetJobPostDetailsRequest.parseFrom(Base64.decodeBase64(requestString));

            JobPost jobPost = JobPost.find.where().eq("jobPostId", pGetJobPostDetailsRequest.getJobPostId()).findUnique();
            if (jobPost != null) {
                getJobPostDetailsResponse.setStatus(GetJobPostDetailsResponse.Status.valueOf(1));
                getJobPostDetailsResponse.setJobPost(getJobPostInformationFromJobPostObject(jobPost));

                Logger.info("Status returned = " + getJobPostDetailsResponse.getStatus());
            }
            Company company = Company.find.where().eq("companyId", jobPost.getCompany().getCompanyId()).findUnique();
            if (company != null) {
                getJobPostDetailsResponse.setCompany(getCompanyInfoFromCompanyObject(company, jobPost));
            }

        } catch (Exception e) {
            Logger.info("Unable to parse message");
        }

        if (pGetJobPostDetailsRequest == null) {
            Logger.info("Invalid message");
            return badRequest();
        }
        return ok(Base64.encodeBase64String(getJobPostDetailsResponse.build().toByteArray()));
    }

    public static Result mAddHomeLocality() {
        HomeLocalityRequest pHomeLocalityRequest = null;
        HomeLocalityResponse.Builder builder = HomeLocalityResponse.newBuilder();

        try {
            String requestString = request().body().asText();
            pHomeLocalityRequest = HomeLocalityRequest.parseFrom(Base64.decodeBase64(requestString));
            if (pHomeLocalityRequest != null) {
                Logger.info("Received CandidateMobile:" + pHomeLocalityRequest.getCandidateMobile());
                Candidate existingCandidate = CandidateService.isCandidateExists(pHomeLocalityRequest.getCandidateMobile());
                if (existingCandidate != null) {
                    Logger.info("lat/lng:" + pHomeLocalityRequest.getLat() + "/" + pHomeLocalityRequest.getLng());
                    Logger.info("Address" + pHomeLocalityRequest.getAddress());
                    List<String> localityList = Arrays.asList(pHomeLocalityRequest.getAddress().split(","));
                    if (localityList.size() >= 4) {
                        String localityName = localityList.get(localityList.size() - 4);
                        existingCandidate.setLocality(getOrCreateLocality(localityName));
                        Logger.info("Locality:" + existingCandidate.getLocality().getLocalityName());
                    } else if (localityList.size() == 2) {
                        String localityName = localityList.get(localityList.size() - 1);
                        existingCandidate.setLocality(getOrCreateLocality(localityName));
                        Logger.info("Locality:" + existingCandidate.getLocality().getLocalityName());
                    }
                    existingCandidate.setCandidateLocalityLat(pHomeLocalityRequest.getLat());
                    existingCandidate.setCandidateLocalityLng(pHomeLocalityRequest.getLng());
                    existingCandidate.candidateUpdate();
                    builder.setStatus(HomeLocalityResponse.Status.valueOf(HomeLocalityResponse.Status.SUCCESS_VALUE));
                } else {
                    builder.setStatus(HomeLocalityResponse.Status.valueOf(HomeLocalityResponse.Status.USER_NOT_FOUND_VALUE));
                }
            } else {
                builder.setStatus(HomeLocalityResponse.Status.valueOf(HomeLocalityResponse.Status.FAILURE_VALUE));
            }
            Logger.info("Status returned = " + builder.getStatus());
        } catch (InvalidProtocolBufferException e) {
            Logger.info("Unable to parse message");
        }

        if (pHomeLocalityRequest == null) {
            Logger.info("Invalid message");
            return badRequest();
        }
        return ok(Base64.encodeBase64String(builder.build().toByteArray()));
    }

    private static Locality getOrCreateLocality(String localityName) {
        // validate localityName
        localityName = localityName.trim();
        if (localityName != null && isValidLocalityName(localityName)) {
            Locality locality = Locality.find.where().eq("localityName", localityName).findUnique();
            if (locality != null) {
                return locality;
            }
        }
        Locality locality = new Locality();
        locality.setLocalityName(localityName);
        locality.save();
        locality = Locality.find.where().eq("localityName", localityName).findUnique();
        return locality;
    }

    public static Result mGetMatchingJobPosts(String mobile) {
        JobPostResponse.Builder jobPostResponseBuilder = JobPostResponse.newBuilder();
        mobile = FormValidator.convertToIndianMobileFormat(mobile);
        if (mobile != null) {
            Logger.info("getMatchingJob for Mobile: " + mobile);
            Candidate existingCandidate = CandidateService.isCandidateExists(mobile);
            if (existingCandidate != null) {
                if (existingCandidate.getCandidateLocalityLat() == null || existingCandidate.getCandidateLocalityLng() == null) {
                    return mGetAllJobPosts();
                } else {
                    List<JobPostObject> jobPostListToReturn =
                            getJobPostObjectListFromJobPostList(
                                    MatchingEngineService.fetchMatchingJobPostForLatLng(
                                            existingCandidate.getCandidateLocalityLat(), existingCandidate.getCandidateLocalityLng(), null)
                            );
                    jobPostResponseBuilder.addAllJobPost(jobPostListToReturn);
                }
            }
        }
        return ok(Base64.encodeBase64String(jobPostResponseBuilder.build().toByteArray()));
    }

    public static Result mGetCandidateJobApplication() {
        CandidateAppliedJobsRequest candidateAppliedJobsRequest = null;
        CandidateAppliedJobsResponse.Builder candidateAppliedJobsBuilder = CandidateAppliedJobsResponse.newBuilder();

        try {
            String requestString = request().body().asText();
            candidateAppliedJobsRequest = CandidateAppliedJobsRequest.parseFrom(Base64.decodeBase64(requestString));
            Candidate existingCandidate = CandidateService.isCandidateExists(FormValidator.convertToIndianMobileFormat(candidateAppliedJobsRequest.getCandidateMobile()));
            if (existingCandidate != null) {
                JobPostResponse.Builder jobPostResponseBuilder = JobPostResponse.newBuilder();
                JobApplicationObject.Builder jobApplicationBuilder = JobApplicationObject.newBuilder();
                List<JobApplication> jobApplicationList = JobApplication.find.where().eq("candidateId", existingCandidate.getCandidateId()).orderBy("jobApplicationId desc").findList();
                List<JobApplicationObject> jobApplicationListToReturn = new ArrayList<JobApplicationObject>();
                for (JobApplication jobApplication : jobApplicationList) {
                    jobApplicationBuilder.setJobApplicationAppliedMillis(jobApplication.getJobApplicationCreateTimeStamp().getTime());

                    if (jobApplication.getLocality() != null) {
                        LocalityObject.Builder localityBuilder = LocalityObject.newBuilder();
                        Locality locality = Locality.find.where().eq("localityId", jobApplication.getLocality().getLocalityId()).findUnique();
                        if (locality != null) {
                            localityBuilder.setLocalityName(locality.getLocalityName());
                            jobApplicationBuilder.setPreScreenLocation(localityBuilder.build());
                        }
                    }

                    jobApplicationBuilder.setJobPost(getJobPostInformationFromJobPostObject(jobApplication.getJobPost()));
                    jobApplicationListToReturn.add(jobApplicationBuilder.build());
                }

                candidateAppliedJobsBuilder.addAllJobApplication(jobApplicationListToReturn);

                return ok(Base64.encodeBase64String(candidateAppliedJobsBuilder.build().toByteArray()));
            }
        } catch (InvalidProtocolBufferException e) {
            e.printStackTrace();
        }
        return ok("0");
    }

    public static Result mCandidateUpdateBasicProfile() {
        UpdateCandidateBasicProfileRequest updateCandidateBasicProfileRequest = null;
        UpdateCandidateBasicProfileResponse.Builder updateCandidateProfileResponse = UpdateCandidateBasicProfileResponse.newBuilder();

        try {
            String requestString = request().body().asText();
            updateCandidateBasicProfileRequest = UpdateCandidateBasicProfileRequest.parseFrom(Base64.decodeBase64(requestString));

            AddCandidateRequest addCandidateRequest = new AddCandidateRequest();
            addCandidateRequest.setCandidateMobile(updateCandidateBasicProfileRequest.getCandidateMobile());
            addCandidateRequest.setCandidateFirstName(updateCandidateBasicProfileRequest.getCandidateFirstName());
            addCandidateRequest.setCandidateSecondName(updateCandidateBasicProfileRequest.getCandidateLastName());
            List<Integer> jobRoleIdList = new ArrayList<Integer>();
            for(JobRoleObject jobRoleObject : updateCandidateBasicProfileRequest.getJobRolePrefList()){
                jobRoleIdList.add(Math.toIntExact(jobRoleObject.getJobRoleId()));
            }
            addCandidateRequest.setCandidateJobPref(jobRoleIdList);
            addCandidateRequest.setCandidateGender(updateCandidateBasicProfileRequest.getCandidateGender());
            String startDateString = updateCandidateBasicProfileRequest.getCandidateDOB();
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
            Date startDate;
            try {
                startDate = df.parse(startDateString);
                addCandidateRequest.setCandidateDob(startDate);
            } catch (ParseException e) {
            }

            addCandidateRequest.setCandidateTimeShiftPref(String.valueOf(updateCandidateBasicProfileRequest.getCandidateTimeshiftPref()));

            boolean isSupport = false;
            CandidateSignUpResponse candidateSignUpResponse = CandidateService.createCandidateProfile(addCandidateRequest, isSupport, ServerConstants.UPDATE_BASIC_PROFILE);
            updateCandidateProfileResponse.setStatus(UpdateCandidateBasicProfileResponse.Status.valueOf(candidateSignUpResponse.getStatus()));
            Logger.info("Status returned = " + updateCandidateProfileResponse.getStatus());

        } catch (InvalidProtocolBufferException e) {
            Logger.info("Unable to parse message");
        }

        if (updateCandidateBasicProfileRequest == null) {
            Logger.info("Invalid message");
            return badRequest();
        }

        return ok(Base64.encodeBase64String(updateCandidateProfileResponse.build().toByteArray()));
    }

    public static Result mCandidateUpdateExperienceProfile() {
        UpdateCandidateExperienceProfileRequest updateCandidateExperienceProfileRequest = null;
        UpdateCandidateBasicProfileResponse.Builder updateCandidateProfileResponse = UpdateCandidateBasicProfileResponse.newBuilder();

        try {
            String requestString = request().body().asText();
            updateCandidateExperienceProfileRequest = UpdateCandidateExperienceProfileRequest.parseFrom(Base64.decodeBase64(requestString));
            AddCandidateExperienceRequest addCandidateExperienceRequest = new AddCandidateExperienceRequest();

            List<CandidateKnownLanguage> candidateKnownLanguageList = new ArrayList<CandidateKnownLanguage>();
            List<CandidateSkills> candidateSkillList = new ArrayList<CandidateSkills>();

            for(LanguageKnownObject languageKnown : updateCandidateExperienceProfileRequest.getCandidateLanguageList()){
                CandidateKnownLanguage candidateKnownLanguage = new CandidateKnownLanguage();
                candidateKnownLanguage.setId(String.valueOf(languageKnown.getLanguageKnownId()));
                candidateKnownLanguage.setRw(languageKnown.getLanguageReadWrite());
                candidateKnownLanguage.setU(languageKnown.getLanguageUnderstand());
                candidateKnownLanguage.setS(languageKnown.getLanguageSpeak());
                candidateKnownLanguageList.add(candidateKnownLanguage);
            }

            for(CandidateSkillObject candidateSkill : updateCandidateExperienceProfileRequest.getCandidateSkillList()){
                CandidateSkills skill = new CandidateSkills();
                skill.setId(String.valueOf(candidateSkill.getSkillId()));
                skill.setAnswer(candidateSkill.getAnswer());
                candidateSkillList.add(skill);
            }
            addCandidateExperienceRequest.setCandidateMobile(FormValidator.convertToIndianMobileFormat(updateCandidateExperienceProfileRequest.getCandidateMobile()));
            if(updateCandidateExperienceProfileRequest.getCandidateCurrentCompany() != null){
                addCandidateExperienceRequest.setCandidateCurrentCompany(updateCandidateExperienceProfileRequest.getCandidateCurrentCompany());
            }
            if(updateCandidateExperienceProfileRequest.getCurrentJobRole() != null){
                addCandidateExperienceRequest.setCandidateCurrentJobRoleId(updateCandidateExperienceProfileRequest.getCurrentJobRole().getJobRoleId());
            }
            addCandidateExperienceRequest.setCandidateLastWithdrawnSalary(updateCandidateExperienceProfileRequest.getCandidateCurrentSalary());
            addCandidateExperienceRequest.setCandidateTotalExperience(updateCandidateExperienceProfileRequest.getCandidateTotalExperience());
            addCandidateExperienceRequest.setCandidateIsEmployed(updateCandidateExperienceProfileRequest.getCandidateIsEmployed());

            //setting language known
            addCandidateExperienceRequest.setCandidateLanguageKnown(candidateKnownLanguageList);
            //setting candidate skills
            addCandidateExperienceRequest.setCandidateSkills(candidateSkillList);

            boolean isSupport = false;
            CandidateSignUpResponse candidateSignUpResponse = CandidateService.createCandidateProfile(addCandidateExperienceRequest, isSupport, ServerConstants.UPDATE_SKILLS_PROFILE);
            updateCandidateProfileResponse.setStatus(UpdateCandidateBasicProfileResponse.Status.valueOf(candidateSignUpResponse.getStatus()));
            Logger.info("Status returned = " + updateCandidateProfileResponse.getStatus());

        } catch (InvalidProtocolBufferException e) {
            Logger.info("Unable to parse message");
        }

        if (updateCandidateExperienceProfileRequest == null) {
            Logger.info("Invalid message");
            return badRequest();
        }

        return ok(Base64.encodeBase64String(updateCandidateProfileResponse.build().toByteArray()));
    }

    public static Result mCandidateUpdateEducationProfile() {
        UpdateCandidateEducationProfileRequest updateCandidateEducationProfileRequest = null;
        UpdateCandidateBasicProfileResponse.Builder updateCandidateProfileResponse = UpdateCandidateBasicProfileResponse.newBuilder();

        AddCandidateEducationRequest addCandidateEducationRequest = new AddCandidateEducationRequest();
        try {
            String requestString = request().body().asText();
            updateCandidateEducationProfileRequest = UpdateCandidateEducationProfileRequest.parseFrom(Base64.decodeBase64(requestString));

            addCandidateEducationRequest.setCandidateMobile(updateCandidateEducationProfileRequest.getCandidateMobile());
            addCandidateEducationRequest.setCandidateDegree(Math.toIntExact(updateCandidateEducationProfileRequest.getCandidateDegree()));
            addCandidateEducationRequest.setCandidateEducationLevel(Math.toIntExact(updateCandidateEducationProfileRequest.getCandidateEducationLevel()));
            addCandidateEducationRequest.setEducationStatus(updateCandidateEducationProfileRequest.getCandidateEducationCompletionStatus());
            addCandidateEducationRequest.setCandidateEducationInstitute(updateCandidateEducationProfileRequest.getCandidateEducationInstitute());

            boolean isSupport = false;
            CandidateSignUpResponse candidateSignUpResponse = CandidateService.createCandidateProfile(addCandidateEducationRequest, isSupport, ServerConstants.UPDATE_EDUCATION_PROFILE);
            updateCandidateProfileResponse.setStatus(UpdateCandidateBasicProfileResponse.Status.valueOf(candidateSignUpResponse.getStatus()));
            Logger.info("Status returned = " + updateCandidateProfileResponse.getStatus());
        } catch (InvalidProtocolBufferException e) {
            Logger.info("Unable to parse message");
        }

        if (updateCandidateEducationProfileRequest == null) {
            Logger.info("Invalid message");
            return badRequest();
        }

        boolean isSupport = false;
        CandidateSignUpResponse candidateSignUpResponse = CandidateService.createCandidateProfile(addCandidateEducationRequest, isSupport, ServerConstants.UPDATE_EDUCATION_PROFILE);

        return ok(Base64.encodeBase64String(updateCandidateProfileResponse.build().toByteArray()));
    }

    public static Result mGetCandidateUpdateBasicProfileStatics() {
        GetCandidateBasicProfileStaticResponse.Builder getCandidateBasicProfileStaticResponse = GetCandidateBasicProfileStaticResponse.newBuilder();

        try {
            List<TimeShiftObject> timeShiftObjectList = new ArrayList<>();
            List<TimeShift> timeShiftList = TimeShift.find.all();
            TimeShiftObject.Builder timeShiftBuilder = TimeShiftObject.newBuilder();
            for (TimeShift timeShift : timeShiftList) {
                timeShiftBuilder.setTimeShiftId(timeShift.getTimeShiftId());
                timeShiftBuilder.setTimeShiftName(timeShift.getTimeShiftName());
                timeShiftObjectList.add(timeShiftBuilder.build());
            }
            getCandidateBasicProfileStaticResponse.addAllTimeShiftList(timeShiftObjectList);

        } catch (Exception e) {
            Logger.info("Unable to parse message");
        }

        return ok(Base64.encodeBase64String(getCandidateBasicProfileStaticResponse.build().toByteArray()));
    }

    public static Result mGetCandidateUpdateEducationProfileStatics() {
        GetCandidateEducationProfileStaticResponse.Builder getCandidateEducationProfileStaticResponse = GetCandidateEducationProfileStaticResponse.newBuilder();

        try {
            List<EducationObject> educationObjectList = new ArrayList<>();
            List<Education> educationList = Education.find.all();
            EducationObject.Builder educationObjectBuilder = EducationObject.newBuilder();
            for (Education education : educationList) {
                educationObjectBuilder.setEducationId(education.getEducationId());
                educationObjectBuilder.setEducationName(education.getEducationName());
                educationObjectList.add(educationObjectBuilder.build());
            }
            getCandidateEducationProfileStaticResponse.addAllEducationObject(educationObjectList);

            List<DegreeObject> degreeObjectList = new ArrayList<>();
            List<Degree> degreeList = Degree.find.all();
            DegreeObject.Builder degreeObjectBuilder = DegreeObject.newBuilder();
            for (Degree degree : degreeList) {
                degreeObjectBuilder.setDegreeId(degree.getDegreeId());
                degreeObjectBuilder.setDegreeName(degree.getDegreeName());
                degreeObjectList.add(degreeObjectBuilder.build());
            }
            getCandidateEducationProfileStaticResponse.addAllDegreeObject(degreeObjectList);

        } catch (Exception e) {
            Logger.info("Unable to parse message");
        }

        return ok(Base64.encodeBase64String(getCandidateEducationProfileStaticResponse.build().toByteArray()));
    }

    public static Result mGetCandidateUpdateExperienceProfileStatics(String jobRoles) {
        GetCandidateExperienceProfileStaticResponse.Builder getCandidatExperienceProfileStaticResponse = GetCandidateExperienceProfileStaticResponse.newBuilder();

        try {
            //getting all languages
            List<LanguageObject> languageObjectList = new ArrayList<>();
            List<Language> languageList = Language.find.all();
            LanguageObject.Builder languageBuilder = LanguageObject.newBuilder();

            for (Language language : languageList) {
                languageBuilder.setLanguageId(language.getLanguageId());
                languageBuilder.setLanguageName(language.getLanguageName());
                languageObjectList.add(languageBuilder.build());
            }

            //getting skills
            List<String> jobPrefIdList = Arrays.asList(jobRoles.split("\\s*,\\s*"));

            List<SkillObject> skillObjectList = new ArrayList<>();
            SkillObject.Builder skillBuilder = SkillObject.newBuilder();

            List<JobToSkill> response = new ArrayList<>();
            int flag = 0;
            for(String jobId: jobPrefIdList) {
                List<JobToSkill> jobToSkillList = JobToSkill.find.where().eq("JobRoleId", jobId).findList();
                if(response.isEmpty()){
                    for(JobToSkill jobToSkill: jobToSkillList){
                        skillBuilder.setSkillId(jobToSkill.getSkill().getSkillId());
                        skillBuilder.setSkillName(jobToSkill.getSkill().getSkillName());
                        skillBuilder.setSkillQuestion(jobToSkill.getSkill().getSkillQuestion());
                        skillObjectList.add(skillBuilder.build());
                    }
                    response.addAll(jobToSkillList);
                } else {
                    for (JobToSkill dbItem: jobToSkillList){
                        flag = 0;
                        for(JobToSkill item: response){
                            if(item.getSkill().getSkillId() == dbItem.getSkill().getSkillId()){
                                flag = 1;
                                break;
                            }
                        }
                        if(flag == 0){
                            response.add(dbItem);
                            skillBuilder.setSkillId(dbItem.getSkill().getSkillId());
                            skillBuilder.setSkillName(dbItem.getSkill().getSkillName());
                            skillBuilder.setSkillQuestion(dbItem.getSkill().getSkillQuestion());
                            skillObjectList.add(skillBuilder.build());
                        }
                    }
                }
            }
            List<models.entity.Static.JobRole> jobRoleList = models.entity.Static.JobRole.find.all();
            getCandidatExperienceProfileStaticResponse.addAllJobRole(getJobRoleObjectListFromJobRoleList(jobRoleList));

            getCandidatExperienceProfileStaticResponse.addAllLanguageObject(languageObjectList);
            getCandidatExperienceProfileStaticResponse.addAllSkillObject(skillObjectList);

        } catch (Exception e) {
            Logger.info("Unable to parse message");
        }

        return ok(Base64.encodeBase64String(getCandidatExperienceProfileStaticResponse.build().toByteArray()));
    }

}
