package controllers;

import api.ServerConstants;
import api.http.FormValidator;
import api.http.httpRequest.CandidateSignUpRequest;
import api.http.httpRequest.LoginRequest;
import api.http.httpResponse.CandidateSignUpResponse;
import api.http.httpResponse.LoginResponse;
import com.google.api.client.util.Base64;
import com.google.protobuf.InvalidProtocolBufferException;
import controllers.businessLogic.AuthService;
import controllers.businessLogic.CandidateService;
import controllers.businessLogic.JobService;
import controllers.businessLogic.MatchingEngineService;
import in.trujobs.proto.*;
import models.entity.Candidate;
import models.entity.Company;
import models.entity.JobPost;
import models.entity.OM.JobPostToLocality;
import models.entity.OM.JobPreference;
import models.entity.OM.LocalityPreference;
import models.entity.Static.Locality;
import play.Logger;
import play.mvc.Result;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
            if(loginResponse.getStatus() == loginResponse.STATUS_SUCCESS){
                loginResponseBuilder.setCandidateFirstName(loginResponse.getCandidateFirstName());
                if(loginResponse.getCandidateLastName() != null){
                    loginResponseBuilder.setCandidateLastName(loginResponse.getCandidateLastName());
                } else{
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
            CandidateSignUpResponse candidateSignUpResponse = CandidateService.signUpCandidate(candidateSignUpRequest,isSupport, ServerConstants.LEAD_SOURCE_UNKNOWN);
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
            if(candidateSignUpResponse.getCandidateLastName() != null){
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

        List<JobRoleObject> jobRoleListToReturn = new ArrayList<>();
        for (models.entity.Static.JobRole jobRole: jobRoleList) {
            JobRoleObject.Builder jobRoleBuilder
                    = JobRoleObject.newBuilder();
            jobRoleBuilder.setJobRoleId(jobRole.getJobRoleId());
            jobRoleBuilder.setJobRoleName(jobRole.getJobName());
            if(jobRole.getJobRoleIcon() != null){
                jobRoleBuilder.setJobRoleIcon(jobRole.getJobRoleIcon());
            } else {
                jobRoleBuilder.setJobRoleIcon("");
            }

            jobRoleListToReturn.add(jobRoleBuilder.build());
        }
        jobRoleResponseBuilder.addAllJobRole(jobRoleListToReturn);
        return ok(Base64.encodeBase64String(jobRoleResponseBuilder.build().toByteArray()));
    }

    public static Result mGetAllJobPosts() {
        JobPostResponse.Builder jobPostResponseBuilder = JobPostResponse.newBuilder();
        List<JobPost> jobPostList = JobPost.find.where().eq("jobPostIsHot", ServerConstants.IS_HOT).findList();
        List<JobPostObject> jobPostListToReturn = getJobPostObjectListFromJobPostList(jobPostList);
        jobPostResponseBuilder.addAllJobPost(jobPostListToReturn);
        return ok(Base64.encodeBase64String(jobPostResponseBuilder.build().toByteArray()));
    }

    private static List<JobPostObject> getJobPostObjectListFromJobPostList(List<JobPost> jobPostList) {
        List<JobPostObject> jobPostListToReturn = new ArrayList<>();
        for (models.entity.JobPost jobPost: jobPostList) {
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

            if(jobPost.getJobRole() != null){
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
            for (JobPostToLocality locality: localityList) {
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
            if(pAddJobPrefRequest.getJobRolePrefOneId() != 0){
                jobPrefList.add(Math.toIntExact(pAddJobPrefRequest.getJobRolePrefOneId()));
            }
            if(pAddJobPrefRequest.getJobRolePrefTwoId() != 0){
                jobPrefList.add(Math.toIntExact(pAddJobPrefRequest.getJobRolePrefTwoId()));
            }
            if(pAddJobPrefRequest.getJobRolePrefThreeId() != 0){
                jobPrefList.add(Math.toIntExact(pAddJobPrefRequest.getJobRolePrefThreeId()));
            }

            addJobRoleResponseBuilder.setStatus(AddJobRoleResponse.Status.valueOf(2));

            Logger.info("Checking for mobile number: " + FormValidator.convertToIndianMobileFormat(pAddJobPrefRequest.getCandidateMobile()));
            Candidate candidate = CandidateService.isCandidateExists(FormValidator.convertToIndianMobileFormat(pAddJobPrefRequest.getCandidateMobile()));
            if(candidate != null){
                Logger.info(CandidateService.getCandidateJobPreferenceList(jobPrefList, candidate).size() + " ---- ");
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
        GetCandidateInformationResponse.Builder getJobPostDetailsResponse = GetCandidateInformationResponse.newBuilder();
        CandidateObject.Builder candidateBuilder = CandidateObject.newBuilder();
        try {
            String requestString = request().body().asText();
            pCandidateInformationRequest = CandidateInformationRequest.parseFrom(Base64.decodeBase64(requestString));

            Candidate candidate = CandidateService.isCandidateExists(FormValidator.convertToIndianMobileFormat(pCandidateInformationRequest.getCandidateMobile()));
            if(candidate != null){
                getJobPostDetailsResponse.setStatus(GetCandidateInformationResponse.Status.valueOf(1));
                candidateBuilder.setCandidateId(candidate.getCandidateId());
                candidateBuilder.setCandidateMobile(candidate.getCandidateMobile());
                candidateBuilder.setCandidateFirstName(candidate.getCandidateFirstName());
                candidateBuilder.setCandidateLastName(candidate.getCandidateLastName());
                candidateBuilder.setCandidateLastName(candidate.getCandidateLastName());
                candidateBuilder.setCandidateIsAssessed(candidate.getCandidateIsAssessed());
                candidateBuilder.setCandidateMinProfileComplete(candidate.getIsMinProfileComplete());
                if(candidate.getCandidateGender() != null){
                    candidateBuilder.setCandidateGender(candidate.getCandidateGender());
                }
                if(candidate.getCandidateTotalExperience() != null){
                    candidateBuilder.setCandidateTotalExperience(candidate.getCandidateTotalExperience());
                }
                //TODO: return age of a candidate

                //getting home locality
                LocalityObject.Builder localityBuilder = LocalityObject.newBuilder();
                if(candidate.getLocality() != null){
                    localityBuilder.setLocalityId(candidate.getLocality().getLocalityId());
                    localityBuilder.setLocalityName(candidate.getLocality().getLocalityName());
                    candidateBuilder.setCandidateHomelocality(localityBuilder);
                }

                //getting timeShift
                TimeShiftObject.Builder timeShiftBuilder = TimeShiftObject.newBuilder();
                if(candidate.getTimeShiftPreference() != null){
                    timeShiftBuilder.setTimeShiftId(candidate.getTimeShiftPreference().getTimeShift().getTimeShiftId());
                    timeShiftBuilder.setTimeShiftName(candidate.getTimeShiftPreference().getTimeShift().getTimeShiftName());
                    candidateBuilder.setCandidateTimeShiftPref(timeShiftBuilder);
                }

                //getting jobPrefs
                List<JobPreference> jobPreferenceList = JobPreference.find.where().eq("candidateId", candidate.getCandidateId()).findList();
                if(jobPreferenceList.size() > 0){
                    List<JobRoleObject> jobRolePrefListToReturn = new ArrayList<>();
                    for (JobPreference jobRole: jobPreferenceList) {
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
                if(localityPreferenceList.size() > 0){
                    List<LocalityObject> localityPrefListToReturn = new ArrayList<>();
                    for (LocalityPreference locality: localityPreferenceList) {
                        LocalityObject.Builder localityPrefBuilder
                                = LocalityObject.newBuilder();
                        localityPrefBuilder.setLocalityId(locality.getLocality().getLocalityId());
                        localityPrefBuilder.setLocalityName(locality.getLocality().getLocalityName());
                        localityPrefListToReturn.add(localityPrefBuilder.build());
                    }
                    candidateBuilder.addAllCandidateLocationPref(localityPrefListToReturn);
                }
                getJobPostDetailsResponse.setCandidate(candidateBuilder);
            } else {
                getJobPostDetailsResponse.setStatus(GetCandidateInformationResponse.Status.valueOf(2));
            }
        } catch (Exception e) {
            Logger.info("Unable to parse message");
        }

        if (pCandidateInformationRequest == null) {
            Logger.info("Invalid message");
            return badRequest();
        }
        Logger.info("Status returned = " + getJobPostDetailsResponse.getStatus());
        return ok(Base64.encodeBase64String(getJobPostDetailsResponse.build().toByteArray()));
    }

    public static Result mGetJobPostInfo() {
        GetJobPostDetailsRequest pGetJobPostDetailsRequest = null;
        GetJobPostDetailsResponse.Builder getJobPostDetailsResponse = GetJobPostDetailsResponse.newBuilder();
        JobPostObject.Builder jobPostBuilder = JobPostObject.newBuilder();
        CompanyObject.Builder companyBuilder = CompanyObject.newBuilder();
        try {
            String requestString = request().body().asText();
            pGetJobPostDetailsRequest = GetJobPostDetailsRequest.parseFrom(Base64.decodeBase64(requestString));

            JobPost jobPost = JobPost.find.where().eq("jobPostId", pGetJobPostDetailsRequest.getJobPostId()).findUnique();
            if(jobPost != null){
                getJobPostDetailsResponse.setStatus(GetJobPostDetailsResponse.Status.valueOf(1));
                JobRoleObject.Builder jobRoleBuilder = JobRoleObject.newBuilder();

                if(jobPost.getJobRole() != null){
                    jobRoleBuilder.setJobRoleName(jobPost.getJobRole().getJobName());
                    jobRoleBuilder.setJobRoleId(jobPost.getJobRole().getJobRoleId());
                    jobPostBuilder.setJobRole(jobRoleBuilder.build());
                }

                jobPostBuilder.setJobPostCreationMillis(jobPost.getJobPostCreateTimestamp().getTime());
                jobPostBuilder.setVacancies(jobPost.getJobPostVacancies());
                if(jobPost.getJobPostStartTime() == null){
                    jobPostBuilder.setJobPostStartTime(-1);
                } else{
                    jobPostBuilder.setJobPostStartTime(jobPost.getJobPostStartTime());
                }
                if(jobPost.getJobPostEndTime() == null){
                    jobPostBuilder.setJobPostEndTime(-1);
                } else{
                    jobPostBuilder.setJobPostEndTime(jobPost.getJobPostEndTime());
                }

                jobPostBuilder.setJobPostId(jobPost.getJobPostId());
                jobPostBuilder.setJobPostTitle(jobPost.getJobPostTitle());
                jobPostBuilder.setJobPostMinSalary(jobPost.getJobPostMinSalary());
                jobPostBuilder.setJobPostMaxSalary(jobPost.getJobPostMaxSalary());
                jobPostBuilder.setJobPostDescription(jobPost.getJobPostDescription());
                jobPostBuilder.setJobPostIncentives(jobPost.getJobPostIncentives());
                jobPostBuilder.setJobPostMinRequirements(jobPost.getJobPostMinRequirement());
                jobPostBuilder.setJobPostAddress(jobPost.getJobPostAddress());
                if(jobPost.getJobPostWorkingDays() != null ){
                    jobPostBuilder.setJobPostWorkingDays(Integer.toString(jobPost.getJobPostWorkingDays(),2));
                } else{
                    jobPostBuilder.setJobPostWorkingDays("");
                }

                List<LocalityObject> jobPostLocalities = new ArrayList<>();
                List<JobPostToLocality> localityList = jobPost.getJobPostToLocalityList();
                for (JobPostToLocality locality: localityList) {
                    LocalityObject.Builder localityBuilder = LocalityObject.newBuilder();
                    localityBuilder.setLocalityId(locality.getLocality().getLocalityId());
                    localityBuilder.setLocalityName(locality.getLocality().getLocalityName());
                    jobPostLocalities.add(localityBuilder.build());
                }
                jobPostBuilder.addAllJobPostLocality(jobPostLocalities);

                if(jobPost.getJobPostExperience() != null){
                    ExperienceObject.Builder experienceBuilder = ExperienceObject.newBuilder();
                    experienceBuilder.setExperienceId(jobPost.getJobPostExperience().getExperienceId());
                    experienceBuilder.setExperienceType(jobPost.getJobPostExperience().getExperienceType());
                    jobPostBuilder.setJobPostExperience(experienceBuilder);
                }

                TimeShiftObject.Builder timeShiftBuilder = TimeShiftObject.newBuilder();
                timeShiftBuilder.setTimeShiftId(jobPost.getJobPostShift().getTimeShiftId());
                timeShiftBuilder.setTimeShiftName(jobPost.getJobPostShift().getTimeShiftName());
                jobPostBuilder.setJobPostShift(timeShiftBuilder);

                if(jobPost.getJobPostEducation() != null){
                    EducationObject.Builder educationBuilder = EducationObject.newBuilder();
                    educationBuilder.setEducationId(jobPost.getJobPostEducation().getEducationId());
                    educationBuilder.setEducationName(jobPost.getJobPostEducation().getEducationName());
                    jobPostBuilder.setEducation(educationBuilder);
                }

                Company company = Company.find.where().eq("companyId", jobPost.getCompany().getCompanyId()).findUnique();
                if(company != null){
                    companyBuilder.setCompanyName(company.getCompanyName());
                    companyBuilder.setCompanyId(company.getCompanyId());
                    companyBuilder.setCompanyAddress(company.getCompanyAddress());

                    LocalityObject.Builder companyLocality = LocalityObject.newBuilder();

                    if(company.getCompanyLocality() != null){
                        companyLocality.setLocalityName(company.getCompanyLocality().getLocalityName());
                        companyLocality.setLocalityId(company.getCompanyLocality().getLocalityId());
                        companyBuilder.setCompanyLocality(companyLocality.build());
                    }

                    if(company.getCompanyDescription() != null){
                        companyBuilder.setCompanyDescription(company.getCompanyDescription());
                    }
                    if(company.getCompanyEmployeeCount() != null){
                        companyBuilder.setCompanyEmployeeCount(company.getCompanyEmployeeCount());
                    }
                    if(company.getCompanyLogo() != null){
                        companyBuilder.setCompanyLogo(company.getCompanyLogo());
                    }

                    if(company.getCompType() != null){
                        CompanyTypeObject.Builder companyTypeBuilder = CompanyTypeObject.newBuilder();
                        companyTypeBuilder.setCompanyTypeId(company.getCompType().getCompanyTypeId());
                        companyTypeBuilder.setCompanyTypeName(company.getCompType().getCompanyTypeName());
                        companyBuilder.setCompanyType(companyTypeBuilder);
                    }

                    if(company.getCompanyWebsite() != null){
                        companyBuilder.setCompanyWebsite(company.getCompanyWebsite());
                    }

                    List<JobPost> similarJobs = JobPost.find.where().eq("jobPostIsHot", "1").eq("companyId", company.getCompanyId()).findList();
                    List<JobPostObject> similarJobPostListToReturn = new ArrayList<>();
                    for (models.entity.JobPost companyJobPost: similarJobs) {
                        if(companyJobPost.getJobPostId() != jobPost.getJobPostId()){
                            JobPostObject.Builder companyJobPostBuilder
                                    = JobPostObject.newBuilder();

                            JobRoleObject.Builder similarJobRoleBuilder = JobRoleObject.newBuilder();

                            if(companyJobPost.getJobRole() != null){
                                similarJobRoleBuilder.setJobRoleName(jobPost.getJobRole().getJobName());
                                similarJobRoleBuilder.setJobRoleId(jobPost.getJobRole().getJobRoleId());
                                companyJobPostBuilder.setJobRole(similarJobRoleBuilder.build());
                            }

                            companyJobPostBuilder.setJobPostId(companyJobPost.getJobPostId());
                            companyJobPostBuilder.setJobPostTitle(companyJobPost.getJobPostTitle());
                            companyJobPostBuilder.setJobPostMinSalary(companyJobPost.getJobPostMinSalary());
                            companyJobPostBuilder.setJobPostMaxSalary(companyJobPost.getJobPostMaxSalary());

                            List<LocalityObject> similarJobPostLocalities = new ArrayList<>();
                            List<JobPostToLocality> similarJobLocalityList = companyJobPost.getJobPostToLocalityList();
                            for (JobPostToLocality locality: similarJobLocalityList) {
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
                }
                getJobPostDetailsResponse.setJobPost(jobPostBuilder);
                getJobPostDetailsResponse.setCompany(companyBuilder);

                Logger.info("Status returned = " + getJobPostDetailsResponse.getStatus());
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
            if(pHomeLocalityRequest != null){
                Logger.info("Received CandidateMobile:"+pHomeLocalityRequest.getCandidateMobile());
                Candidate existingCandidate = CandidateService.isCandidateExists(pHomeLocalityRequest.getCandidateMobile());
                if (existingCandidate != null){
                    Logger.info("lat/lng:"+pHomeLocalityRequest.getLat()+"/"+pHomeLocalityRequest.getLng());
                    Logger.info("Address"+pHomeLocalityRequest.getAddress());
                    List<String> localityList = Arrays.asList(pHomeLocalityRequest.getAddress().split(","));
                    if(localityList.size() >= 4) {
                        String localityName = localityList.get(localityList.size() - 4);
                        existingCandidate.setLocality(getOrCreateLocality(localityName));
                        Logger.info("Locality:"+existingCandidate.getLocality().getLocalityName());
                    } else if(localityList.size() == 2){
                        String localityName = localityList.get(localityList.size() - 1);
                        existingCandidate.setLocality(getOrCreateLocality(localityName));
                        Logger.info("Locality:"+existingCandidate.getLocality().getLocalityName());
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
        if(localityName != null && isValidLocalityName(localityName)){
            Locality locality = Locality.find.where().eq("localityName", localityName).findUnique();
            if(locality != null){
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
        if(mobile != null) {
            Logger.info("getMatchingJob for Mobile: " + mobile);
            Candidate existingCandidate = CandidateService.isCandidateExists(mobile);
            if(existingCandidate != null){
                if(existingCandidate.getCandidateLocalityLat() == null || existingCandidate.getCandidateLocalityLng() == null){
                    return mGetAllJobPosts();
                } else {
                    List<JobPostObject> jobPostListToReturn  =
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
}
