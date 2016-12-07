package controllers;

import api.InteractionConstants;
import api.ServerConstants;
import api.http.CandidateKnownLanguage;
import api.http.CandidateSkills;
import api.http.FormValidator;
import api.http.httpRequest.*;
import api.http.httpRequest.Workflow.InterviewDateTime.AddCandidateInterviewSlotDetail;
import api.http.httpRequest.Workflow.PreScreenRequest;
import api.http.httpRequest.Workflow.preScreenEdit.*;
import api.http.httpResponse.CandidateSignUpResponse;
import api.http.httpResponse.LoginResponse;
import api.http.httpResponse.Workflow.PreScreenPopulateResponse;
import api.http.httpResponse.interview.InterviewResponse;
import com.amazonaws.util.json.JSONException;
import com.google.api.client.util.Base64;
import com.google.protobuf.InvalidProtocolBufferException;
import controllers.businessLogic.*;
import controllers.businessLogic.JobWorkflow.JobPostWorkflowEngine;
import dao.JobPostWorkFlowDAO;
import dao.staticdao.RejectReasonDAO;
import in.trujobs.proto.*;
import in.trujobs.proto.ApplyJobRequest;
import models.entity.Candidate;
import models.entity.Company;
import models.entity.JobPost;
import models.entity.OM.*;
import models.entity.Static.*;
import models.entity.Static.InterviewTimeSlot;
import models.util.InterviewUtil;
import models.util.SmsUtil;
import play.Logger;
import play.mvc.Result;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import static api.ServerConstants.*;
import static controllers.businessLogic.JobSearchService.*;
import static models.util.InterviewUtil.getDayVal;
import static models.util.InterviewUtil.getMonthVal;
import static models.util.Util.generateOtp;
import static models.util.Validator.isValidLocalityName;
import static play.libs.Json.toJson;
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

            LoginResponse loginResponse = CandidateService.login(loginRequest.getCandidateLoginMobile(),
                    loginRequest.getCandidateLoginPassword(), InteractionConstants.INTERACTION_CHANNEL_CANDIDATE_ANDROID);
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
                if (loginResponse.getCandidateHomeLocalityName() != null)
                    loginResponseBuilder.setCandidateHomeLocalityName(loginResponse.getCandidateHomeLocalityName());
                if (loginResponse.getCandidateHomeLat() != null)
                    loginResponseBuilder.setCandidateHomeLatitude(loginResponse.getCandidateHomeLat());
                if (loginResponse.getCandidateHomeLng() != null)
                    loginResponseBuilder.setCandidateHomeLongitude(loginResponse.getCandidateHomeLng());
                if (loginResponse.getCandidatePrefJobRoleIdOne() != null)
                    loginResponseBuilder.setCandidatePrefJobRoleIdOne(loginResponse.getCandidatePrefJobRoleIdOne());
                if (loginResponse.getCandidatePrefJobRoleIdTwo() != null)
                    loginResponseBuilder.setCandidatePrefJobRoleIdTwo(loginResponse.getCandidatePrefJobRoleIdTwo());
                if (loginResponse.getCandidatePrefJobRoleIdThree() != null)
                    loginResponseBuilder.setCandidatePrefJobRoleIdThree(loginResponse.getCandidatePrefJobRoleIdThree());
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

            //boolean isSupport = false;
            CandidateSignUpResponse candidateSignUpResponse = CandidateService.signUpCandidate(candidateSignUpRequest,
                    InteractionConstants.INTERACTION_CHANNEL_CANDIDATE_ANDROID, ServerConstants.LEAD_SOURCE_UNKNOWN);
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
            CandidateSignUpResponse candidateSignUpResponse = AuthService.savePassword(
                    FormValidator.convertToIndianMobileFormat(pLoginRequest.getCandidateMobile()),
                    pLoginRequest.getCandidatePassword(),
                    InteractionConstants.INTERACTION_CHANNEL_CANDIDATE_ANDROID
            );
            if (candidateSignUpResponse.getStatus() == candidateSignUpResponse.STATUS_SUCCESS) {
                loginResponseBuilder.setCandidateFirstName(candidateSignUpResponse.getCandidateFirstName());
                if (candidateSignUpResponse.getCandidateLastName() != null) {
                    loginResponseBuilder.setCandidateLastName(candidateSignUpResponse.getCandidateLastName());
                } else {
                    loginResponseBuilder.setCandidateLastName("");
                }
                loginResponseBuilder.setCandidateId(candidateSignUpResponse.getCandidateId());
                loginResponseBuilder.setCandidateIsAssessed(candidateSignUpResponse.getIsAssessed());
                loginResponseBuilder.setLeadId(candidateSignUpResponse.getLeadId());
                loginResponseBuilder.setCandidateJobPrefStatus(candidateSignUpResponse.getCandidateJobPrefStatus());
                loginResponseBuilder.setCandidateHomeLocalityStatus(candidateSignUpResponse.getCandidateHomeLocalityStatus());
                if (candidateSignUpResponse.getCandidateHomeLocalityName() != null)
                    loginResponseBuilder.setCandidateHomeLocalityName(candidateSignUpResponse.getCandidateHomeLocalityName());
                if (candidateSignUpResponse.getCandidateHomeLat() != null)
                    loginResponseBuilder.setCandidateHomeLatitude(candidateSignUpResponse.getCandidateHomeLat());
                if (candidateSignUpResponse.getCandidateHomeLng() != null)
                    loginResponseBuilder.setCandidateHomeLongitude(candidateSignUpResponse.getCandidateHomeLng());
                if (candidateSignUpResponse.getCandidatePrefJobRoleIdOne() != null)
                    loginResponseBuilder.setCandidatePrefJobRoleIdOne(candidateSignUpResponse.getCandidatePrefJobRoleIdOne());
                if (candidateSignUpResponse.getCandidatePrefJobRoleIdTwo() != null)
                    loginResponseBuilder.setCandidatePrefJobRoleIdTwo(candidateSignUpResponse.getCandidatePrefJobRoleIdTwo());
                if (candidateSignUpResponse.getCandidatePrefJobRoleIdThree() != null)
                    loginResponseBuilder.setCandidatePrefJobRoleIdThree(candidateSignUpResponse.getCandidatePrefJobRoleIdThree());
                loginResponseBuilder.setStatus(LogInResponse.Status.SUCCESS);

            } else {
                loginResponseBuilder.setStatus(LogInResponse.Status.FAILURE);
            }
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

    public static Result mResendOtp() {
        ResetPasswordRequest pResetPasswordRequest = null;
        ResetPasswordResponse.Builder resetPasswordResponseBuilder = ResetPasswordResponse.newBuilder();

        try {
            String requestString = request().body().asText();
            pResetPasswordRequest = ResetPasswordRequest.parseFrom(Base64.decodeBase64(requestString));
            int randomPIN = generateOtp();
            SmsUtil.sendResetPasswordOTPSms(randomPIN, FormValidator.convertToIndianMobileFormat(pResetPasswordRequest.getMobile()), InteractionConstants.INTERACTION_CHANNEL_CANDIDATE_ANDROID);
            resetPasswordResponseBuilder.setOtp(randomPIN);
            resetPasswordResponseBuilder.setStatus(ResetPasswordResponse.Status.SUCCESS);

        } catch (InvalidProtocolBufferException e) {
            resetPasswordResponseBuilder.setStatus(ResetPasswordResponse.Status.FAILURE);
            Logger.info("Unable to parse message");
        }

        return ok(Base64.encodeBase64String(resetPasswordResponseBuilder.build().toByteArray()));
    }

    public static Result mFindUserAndSendOtp() {
        ResetPasswordRequest pResetPasswordRequest = null;
        ResetPasswordResponse.Builder resetPasswordResponseBuilder = ResetPasswordResponse.newBuilder();

        try {
            String requestString = request().body().asText();
            pResetPasswordRequest = ResetPasswordRequest.parseFrom(Base64.decodeBase64(requestString));
            api.http.httpResponse.ResetPasswordResponse resetPasswordResponse = CandidateService.findUserAndSendOtp(
                    FormValidator.convertToIndianMobileFormat(pResetPasswordRequest.getMobile()),
                    InteractionConstants.INTERACTION_CHANNEL_CANDIDATE_ANDROID
            );
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
        List<models.entity.Static.JobRole> jobRoleList =
                models.entity.Static.JobRole.find.where().orderBy().asc("jobName").findList();

        jobRoleResponseBuilder.addAllJobRole(getJobRoleObjectListFromJobRoleList(jobRoleList));
        return ok(Base64.encodeBase64String(jobRoleResponseBuilder.build().toByteArray()));
    }

    public static Result mGetAllJobPosts() {
        JobPostResponse.Builder jobPostResponseBuilder = JobPostResponse.newBuilder();
        jobPostResponseBuilder.addAllJobPost(convertToJobPostResponseList(getAllJobPosts()));
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

    private static List<JobPostObject> convertToJobPostResponseList(List<JobPost> jobPostList) {
        List<JobPostObject> jobPostListToReturn = new ArrayList<>();

        if (jobPostList == null) {
            return jobPostListToReturn;
        }

        for (models.entity.JobPost jobPost : jobPostList) {
            JobPostObject.Builder jobPostBuilder
                    = JobPostObject.newBuilder();
            jobPostBuilder.setJobPostCreationMillis(jobPost.getJobPostCreateTimestamp().getTime());
            jobPostBuilder.setJobPostId(jobPost.getJobPostId());
            jobPostBuilder.setJobPostTitle(jobPost.getJobPostTitle());
            jobPostBuilder.setJobPostCompanyName(jobPost.getCompany().getCompanyName());
            jobPostBuilder.setJobPostMinSalary(jobPost.getJobPostMinSalary());

            // TODO: Temporary hack to set null value to zero in the response object.
            // Android app is right now handling only != 0 check and not null check
            // Once apk change is made, we need to change this code.
            if (jobPost.getJobPostMaxSalary() != null) {
                jobPostBuilder.setJobPostMaxSalary(jobPost.getJobPostMaxSalary());
            } else {
                jobPostBuilder.setJobPostMaxSalary(0);
            }

            if (jobPost.getJobPostVacancies() != null) {
                jobPostBuilder.setVacancies(jobPost.getJobPostVacancies());
            }

            if (jobPost.getJobRole() != null) {
                jobPostBuilder.setJobRole(jobPost.getJobRole().getJobName());
            }

            jobPostBuilder.setJobPostCompanyLogo(jobPost.getCompany().getCompanyLogo());

            ExperienceObject.Builder experienceBuilder = ExperienceObject.newBuilder();
            experienceBuilder.setExperienceId(jobPost.getJobPostExperience().getExperienceId());
            experienceBuilder.setExperienceType(jobPost.getJobPostExperience().getExperienceType());
            jobPostBuilder.setJobPostExperience(experienceBuilder);

            if (jobPost.getJobPostShift() != null) {
                TimeShiftObject.Builder timeShiftBuilder = TimeShiftObject.newBuilder();

                timeShiftBuilder.setTimeShiftId(jobPost.getJobPostShift().getTimeShiftId());
                timeShiftBuilder.setTimeShiftName(jobPost.getJobPostShift().getTimeShiftName());
                jobPostBuilder.setJobPostShift(timeShiftBuilder);
            }

            List<JobPostToLocality> localityList = jobPost.getJobPostToLocalityList();
            jobPostBuilder.addAllJobPostLocality(getLocalityFromJobToLocalityObject(localityList));

            jobPostBuilder.setJobPostSource(jobPost.getSource());

            jobPostListToReturn.add(jobPostBuilder.build());
        }

        return jobPostListToReturn;
    }

    public static List<LocalityObject> getLocalityFromJobToLocalityObject(List<JobPostToLocality> jobPostToLocalityList) {
        List<LocalityObject> jobPostLocalities = new ArrayList<>();
        for (JobPostToLocality locality : jobPostToLocalityList) {
            LocalityObject.Builder localityBuilder
                    = LocalityObject.newBuilder();
            localityBuilder.setLocalityId(locality.getLocality().getLocalityId());
            localityBuilder.setLocalityName(locality.getLocality().getLocalityName());
            jobPostLocalities.add(localityBuilder.build());
        }
        return jobPostLocalities;
    }

    public static Result mApplyJob() throws IOException, JSONException {
        ApplyJobRequest pApplyJobRequest = null;
        ApplyJobResponse.Builder applyJobResponseBuilder = ApplyJobResponse.newBuilder();

        try {
            String requestString = request().body().asText();
            pApplyJobRequest = ApplyJobRequest.parseFrom(Base64.decodeBase64(requestString));

            //conversion from proto to http request object
            api.http.httpRequest.ApplyJobRequest applyJobRequest = new api.http.httpRequest.ApplyJobRequest();
            applyJobRequest.setJobId(Math.toIntExact(pApplyJobRequest.getJobPostId()));
            applyJobRequest.setLocalityId(Math.toIntExact(pApplyJobRequest.getLocalityId()));
            applyJobRequest.setPartner(false);
            applyJobRequest.setScheduledInterviewDate(null);
            applyJobRequest.setTimeSlot(null);
            applyJobRequest.setCandidateMobile(FormValidator.convertToIndianMobileFormat(pApplyJobRequest.getCandidateMobile()));

            //applying job
            api.http.httpResponse.ApplyJobResponse applyJobResponse = JobService.applyJob(applyJobRequest, InteractionConstants.INTERACTION_CHANNEL_CANDIDATE_ANDROID);

            //setting status response
            applyJobResponseBuilder.setStatus(ApplyJobResponse.Status.valueOf(applyJobResponse.getStatus()));
            applyJobResponseBuilder.setIsPreScreenAvailable(applyJobResponse.isPreScreenAvailable());
            applyJobResponseBuilder.setIsInterviewAvailable(applyJobResponse.isInterviewAvailable());
            applyJobResponseBuilder.setCompanyName(applyJobResponse.getCompanyName());
            applyJobResponseBuilder.setJobRoleTitle(applyJobResponse.getJobRoleTitle());
            applyJobResponseBuilder.setJobTitle(applyJobResponse.getJobTitle());
            applyJobResponseBuilder.setJobPostId(applyJobResponse.getJobPostId());
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
        GetCandidateInformationResponse.Builder getCandidateInfoBuilder = GetCandidateInformationResponse.newBuilder();
        CandidateObject.Builder candidateBuilder = CandidateObject.newBuilder();
        try {
            String requestString = request().body().asText();
            pCandidateInformationRequest = CandidateInformationRequest.parseFrom(Base64.decodeBase64(requestString));

            Candidate candidate = CandidateService.isCandidateExists(FormValidator.convertToIndianMobileFormat(pCandidateInformationRequest.getCandidateMobile()));
            if (candidate != null) {
                getCandidateInfoBuilder.setStatus(GetCandidateInformationResponse.Status.valueOf(1));
                candidateBuilder.setCandidateId(candidate.getCandidateId());
                candidateBuilder.setCandidateMobile(candidate.getCandidateMobile());
                candidateBuilder.setCandidateFirstName(candidate.getCandidateFirstName());
                if (candidate.getCandidateLastName() != null) {
                    candidateBuilder.setCandidateLastName(candidate.getCandidateLastName());
                }
                candidateBuilder.setCandidateIsEmployed(-1);
                if (candidate.getCandidateIsEmployed() != null) {
                    candidateBuilder.setCandidateIsEmployed(candidate.getCandidateIsEmployed());
                }
                int scale = (int) Math.pow(10, 2);
                float percentValue = (float) Math.round(CandidateService.getProfileCompletionPercent(FormValidator.convertToIndianMobileFormat(candidate.getCandidateMobile())) * 100 * scale) / scale;
                candidateBuilder.setCandidateProfileCompletePercent(percentValue);
                candidateBuilder.setCandidateIsAssessed(candidate.getCandidateIsAssessed());
                candidateBuilder.setCandidateMinProfileComplete(candidate.getIsMinProfileComplete());

                candidateBuilder.setCandidateGender(-1);
                if (candidate.getCandidateGender() != null) {
                    candidateBuilder.setCandidateGender(candidate.getCandidateGender());
                }
                candidateBuilder.setCandidateTotalExperience(-1);
                if (candidate.getCandidateTotalExperience() != null) {
                    candidateBuilder.setCandidateTotalExperience(candidate.getCandidateTotalExperience());
                }

                candidateBuilder.setAppliedJobs(candidate.getJobApplicationList().size());

                //getting candidate DOB
                if (candidate.getCandidateDOB() != null) {
                    Calendar c = Calendar.getInstance();
                    c.setTime(candidate.getCandidateDOB());
                    long time = c.getTimeInMillis();
                    candidateBuilder.setCandidateDobMillis(time);
                }

                //getting home locality
                LocalityObject.Builder localityBuilder = LocalityObject.newBuilder();
                if (candidate.getLocality() != null) {
                    localityBuilder.setLocalityId(candidate.getLocality().getLocalityId());
                    localityBuilder.setLocalityName(candidate.getLocality().getLocalityName());
                    if (candidate.getCandidateLocalityLat() != null) {
                        localityBuilder.setLat(candidate.getCandidateLocalityLat());
                    }
                    if (candidate.getCandidateLocalityLng() != null) {
                        localityBuilder.setLng(candidate.getCandidateLocalityLng());
                    }
                    candidateBuilder.setCandidateHomelocality(localityBuilder);
                }

                if (candidate.getCandidateLastWithdrawnSalary() != null) {
                    candidateBuilder.setCandidateLastWithdrawnSalary(candidate.getCandidateLastWithdrawnSalary());
                }

                List<JobHistory> jobHistoryList = JobHistory.find.where().eq("candidateId", candidate.getCandidateId()).findList();
                for (JobHistory jobHistory : jobHistoryList) {
                    if (jobHistory.getCurrentJob()) {
                        if (jobHistory.getCandidatePastCompany() != null) {
                            candidateBuilder.setCandidateCurrentCompany(jobHistory.getCandidatePastCompany());
                        } else {
                            candidateBuilder.setCandidateCurrentCompany("");
                        }
                        JobRoleObject.Builder jobRoleBuilder = JobRoleObject.newBuilder();
                        if (jobHistory.getJobRole() != null) {
                            jobRoleBuilder.setJobRoleName(jobHistory.getJobRole().getJobName());
                            jobRoleBuilder.setJobRoleId(jobHistory.getJobRole().getJobRoleId());
                            candidateBuilder.setCandidateCurrentJobRole(jobRoleBuilder.build());
                        }
                        break;
                    }
                }

                //getting education
                CandidateEducationObject.Builder candidateEducationBuilder = CandidateEducationObject.newBuilder();
                if (candidate.getCandidateEducation() != null) {
                    if (candidate.getCandidateEducation().getCandidateLastInstitute() != null) {
                        candidateEducationBuilder.setCandidateInstitute(candidate.getCandidateEducation().getCandidateLastInstitute());
                    }
                    candidateEducationBuilder.setCandidateEducationCompletionStatus(-1);
                    if (candidate.getCandidateEducation().getCandidateEducationCompletionStatus() != null) {
                        candidateEducationBuilder.setCandidateEducationCompletionStatus(candidate.getCandidateEducation().getCandidateEducationCompletionStatus());
                    }
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
                if (languageKnownList.size() > 0) {
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
                if (candidateSkillList.size() > 0) {
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
                getCandidateInfoBuilder.setCandidate(candidateBuilder);

                List<models.entity.Static.JobRole> jobRoleList = models.entity.Static.JobRole.find.all();
                getCandidateInfoBuilder.addAllJobRoles(getJobRoleObjectListFromJobRoleList(jobRoleList));
            } else {
                getCandidateInfoBuilder.setStatus(GetCandidateInformationResponse.Status.valueOf(2));
            }
        } catch (Exception e) {
            Logger.info("Unable to parse message");
            getCandidateInfoBuilder.setStatus(GetCandidateInformationResponse.Status.valueOf(2));
        }

        if (pCandidateInformationRequest == null) {
            Logger.info("Invalid message");
            getCandidateInfoBuilder.setStatus(GetCandidateInformationResponse.Status.valueOf(2));
            return badRequest();
        }
        return ok(Base64.encodeBase64String(getCandidateInfoBuilder.build().toByteArray()));
    }

    public static JobPostObject getJobPostInformationFromJobPostObject(JobPost jobPost) {
        //initializing jobPostObject (proto) to return
        JobPostObject jobPostObject;

        JobPostObject.Builder jobPostBuilder = JobPostObject.newBuilder();
        if (jobPost.getJobRole() != null) {
            jobPostBuilder.setJobRole(jobPost.getJobRole().getJobName());
        }

        jobPostBuilder.setJobPostCreationMillis(jobPost.getJobPostCreateTimestamp().getTime());
        if (jobPost.getJobPostVacancies() != null) jobPostBuilder.setVacancies(jobPost.getJobPostVacancies());
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

        //setting values to jobPostObject from Model object
        jobPostBuilder.setJobPostId(jobPost.getJobPostId());
        jobPostBuilder.setJobPostCompanyName(jobPost.getCompany().getCompanyName());
        jobPostBuilder.setJobPostCompanyLogo(jobPost.getCompany().getCompanyLogo());
        jobPostBuilder.setJobPostTitle(jobPost.getJobPostTitle());
        jobPostBuilder.setJobPostMinSalary(jobPost.getJobPostMinSalary());
        if (jobPost.getJobPostMaxSalary() != null) jobPostBuilder.setJobPostMaxSalary(jobPost.getJobPostMaxSalary());
        if (jobPost.getJobPostDescription() != null)
            jobPostBuilder.setJobPostDescription(jobPost.getJobPostDescription());
        if (jobPost.getJobPostIncentives() != null) jobPostBuilder.setJobPostIncentives(jobPost.getJobPostIncentives());
        if (jobPost.getJobPostMinRequirement() != null)
            jobPostBuilder.setJobPostMinRequirements(jobPost.getJobPostMinRequirement());
        if (jobPost.getJobPostAddress() != null) jobPostBuilder.setJobPostAddress(jobPost.getJobPostAddress());
        if (jobPost.getJobPostWorkingDays() != null) {
            jobPostBuilder.setJobPostWorkingDays(Integer.toString(jobPost.getJobPostWorkingDays(), 2));
        } else {
            jobPostBuilder.setJobPostWorkingDays("");
        }

        //adding jobPost localities
        List<JobPostToLocality> localityList = jobPost.getJobPostToLocalityList();
        jobPostBuilder.addAllJobPostLocality(getLocalityFromJobToLocalityObject(localityList));

        //adding job post experience
        if (jobPost.getJobPostExperience() != null) {
            ExperienceObject.Builder experienceBuilder = ExperienceObject.newBuilder();
            experienceBuilder.setExperienceId(jobPost.getJobPostExperience().getExperienceId());
            experienceBuilder.setExperienceType(jobPost.getJobPostExperience().getExperienceType());
            jobPostBuilder.setJobPostExperience(experienceBuilder);
        }

        //adding job post time Shift
        if (jobPost.getJobPostShift() != null) {
            TimeShiftObject.Builder timeShiftBuilder = TimeShiftObject.newBuilder();
            timeShiftBuilder.setTimeShiftId(jobPost.getJobPostShift().getTimeShiftId());
            timeShiftBuilder.setTimeShiftName(jobPost.getJobPostShift().getTimeShiftName());
            jobPostBuilder.setJobPostShift(timeShiftBuilder);
        }

        //adding job post education
        if (jobPost.getJobPostEducation() != null) {
            EducationObject.Builder educationBuilder = EducationObject.newBuilder();
            educationBuilder.setEducationId(jobPost.getJobPostEducation().getEducationId());
            educationBuilder.setEducationName(jobPost.getJobPostEducation().getEducationName());
            jobPostBuilder.setEducation(educationBuilder);
        }
        jobPostObject = jobPostBuilder.build();

        return jobPostObject;
    }

    public static CompanyObject getCompanyInfoFromCompanyObject(Company company, JobPost jobPost) {
        CompanyObject.Builder companyBuilder = CompanyObject.newBuilder();

        //setting values of company model object to proto company object
        companyBuilder.setCompanyName(company.getCompanyName());
        companyBuilder.setCompanyId(company.getCompanyId());
        if (company.getCompanyAddress() != null) companyBuilder.setCompanyAddress(company.getCompanyAddress());

        //adding company Locality
        LocalityObject.Builder companyLocality = LocalityObject.newBuilder();

        if (company.getCompanyLocality() != null) {
            companyLocality.setLocalityName(company.getCompanyLocality().getLocalityName());
            companyLocality.setLocalityId(company.getCompanyLocality().getLocalityId());
            companyBuilder.setCompanyLocality(companyLocality.build());
        }
        //adding company description
        if (company.getCompanyDescription() != null) {
            companyBuilder.setCompanyDescription(company.getCompanyDescription());
        }

        //adding company employee count
        if (company.getCompanyEmployeeCount() != null) {
            companyBuilder.setCompanyEmployeeCount(company.getCompanyEmployeeCount());
        }

        //adding company logo
        if (company.getCompanyLogo() != null) {
            companyBuilder.setCompanyLogo(company.getCompanyLogo());
        }

        //adding company type
        if (company.getCompType() != null) {
            CompanyTypeObject.Builder companyTypeBuilder = CompanyTypeObject.newBuilder();
            companyTypeBuilder.setCompanyTypeId(company.getCompType().getCompanyTypeId());
            companyTypeBuilder.setCompanyTypeName(company.getCompType().getCompanyTypeName());
            companyBuilder.setCompanyType(companyTypeBuilder);
        }

        if (company.getCompanyWebsite() != null) {
            companyBuilder.setCompanyWebsite(company.getCompanyWebsite());
        }

        //getting other active (Hot Jobs) jobs from the above company

        //getting list of all the other jobs from Model
        List<JobPost> similarJobs = JobPost.find.where().eq("jobPostIsHot", "1").eq("JobStatus", ServerConstants.JOB_STATUS_ACTIVE).eq("companyId", company.getCompanyId()).findList();

        //creating a new list of type proto which will contain all the other job being offered by a company
        List<JobPostObject> similarJobPostListToReturn = new ArrayList<>();

        //iterating in the list of other jobs (model list) and setting in proto builder objects
        for (models.entity.JobPost companyJobPost : similarJobs) {
            //checking if the job is the same as the above job which we are fetching the detail
            if (companyJobPost.getJobPostId() != jobPost.getJobPostId()) {
                // new builder to get info of one particular job post
                JobPostObject.Builder companyOtherJobPostBuilder
                        = JobPostObject.newBuilder();

                // jobRole object of type proto to get jobRole details
                if (companyJobPost.getJobRole() != null) {
                    companyOtherJobPostBuilder.setJobRole(companyJobPost.getJobRole().getJobName());
                }

                //setting other values to the other jobs post builder
                companyOtherJobPostBuilder.setJobPostId(companyJobPost.getJobPostId());
                companyOtherJobPostBuilder.setJobPostTitle(companyJobPost.getJobPostTitle());
                companyOtherJobPostBuilder.setJobPostMinSalary(companyJobPost.getJobPostMinSalary());
                companyOtherJobPostBuilder.setJobPostMaxSalary(companyJobPost.getJobPostMaxSalary());

                // list of all the localities of the job post
                List<JobPostToLocality> jobPostToLocalityList = companyJobPost.getJobPostToLocalityList();

                //adding all the other jobs of a company
                companyOtherJobPostBuilder.addAllJobPostLocality(getLocalityFromJobToLocalityObject(jobPostToLocalityList));

                similarJobPostListToReturn.add(companyOtherJobPostBuilder.build());
            }
        }
        companyBuilder.addAllCompanyOtherJobs(similarJobPostListToReturn);

        CompanyObject companyObject = companyBuilder.build();
        return companyObject;
    }

    public static Result mGetJobPostInfo() {
        /* Interaction Params */
        String objectAUUId = null;
        String jobPostUUId = null;
        String result = null;

        GetJobPostDetailsRequest pGetJobPostDetailsRequest = null;
        GetJobPostDetailsResponse.Builder getJobPostDetailsResponse = GetJobPostDetailsResponse.newBuilder();
        try {
            String requestString = request().body().asText();
            pGetJobPostDetailsRequest = GetJobPostDetailsRequest.parseFrom(Base64.decodeBase64(requestString));

            //getting jobPost model object
            JobPost jobPost = JobPost.find.where().eq("jobPostId", pGetJobPostDetailsRequest.getJobPostId()).findUnique();
            if (jobPost != null) {
                getJobPostDetailsResponse.setStatus(GetJobPostDetailsResponse.Status.SUCCESS);
                getJobPostDetailsResponse.setJobPost(getJobPostInformationFromJobPostObject(jobPost));
                Logger.info("Status returned = " + getJobPostDetailsResponse.getStatus());
                // save Interaction for global
                jobPostUUId = jobPost.getJobPostUUId();
                result = "Viewed JobPost: " + jobPost.getJobPostTitle();
            }

            //getting company object from DB
            Company company = Company.find.where().eq("companyId", jobPost.getCompany().getCompanyId()).findUnique();
            if (company != null) {
                getJobPostDetailsResponse.setCompany(getCompanyInfoFromCompanyObject(company, jobPost));
                result += " @ " + company.getCompanyName();
            }

            //checking if the candidate has applied to this job or now not
            getJobPostDetailsResponse.setAlreadyApplied(false);
            if (!pGetJobPostDetailsRequest.getCandidateMobile().trim().isEmpty()) {
                Candidate existingCandidate = CandidateService.isCandidateExists(FormValidator.convertToIndianMobileFormat(pGetJobPostDetailsRequest.getCandidateMobile()));
                if (existingCandidate != null) {
                    JobApplication jobApplication = JobApplication.find.where()
                            .eq("candidateId", existingCandidate.getCandidateId())
                            .eq("jobPostId", pGetJobPostDetailsRequest.getJobPostId())
                            .findUnique();
                    if (jobApplication != null) {
                        getJobPostDetailsResponse.setAlreadyApplied(true);
                    }
                    // save Interaction against candidate
                    objectAUUId = existingCandidate.getCandidateUUId();
                }
            }
            getJobPostDetailsResponse.setStatus(GetJobPostDetailsResponse.Status.SUCCESS);

        } catch (Exception e) {
            e.printStackTrace();
            Logger.info("Unable to parse message");
            getJobPostDetailsResponse.setStatus(GetJobPostDetailsResponse.Status.NO_JOB);
        }

        if (pGetJobPostDetailsRequest == null) {
            Logger.info("Invalid message");
            getJobPostDetailsResponse.setStatus(GetJobPostDetailsResponse.Status.NO_JOB);
            return badRequest();
        }

        /* save interaction */
        InteractionService.createInteractionForViewJobPostInfo(objectAUUId, jobPostUUId, result);
        return ok(Base64.encodeBase64String(getJobPostDetailsResponse.build().toByteArray()));
    }

    public static Result mAddHomeLocality() {
        /* Received Home Locality Object's latlng may differ as db only has locality and not specific place's latlng */
        HomeLocalityRequest pHomeLocalityRequest = null;
        HomeLocalityResponse.Builder builder = HomeLocalityResponse.newBuilder();

        try {
            String requestString = request().body().asText();
            pHomeLocalityRequest = HomeLocalityRequest.parseFrom(Base64.decodeBase64(requestString));
            if (pHomeLocalityRequest != null) {
                Candidate existingCandidate = CandidateService.isCandidateExists(pHomeLocalityRequest.getCandidateMobile());
                if (existingCandidate != null) {
                    Logger.info("lat/lng:" + pHomeLocalityRequest.getLat() + "/" + pHomeLocalityRequest.getLng());
                    Logger.info("Address:" + pHomeLocalityRequest.getLocalityName());
                    existingCandidate.setLocality(getOrCreateLocality(pHomeLocalityRequest.getLocalityName(),
                            pHomeLocalityRequest.getLat(), pHomeLocalityRequest.getLng(), pHomeLocalityRequest.getPlaceId()));
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

    private static Locality getOrCreateLocality(String localityName, Double latitude, Double longitude, String placeId) {
        // validate localityName
        localityName = localityName.trim();
        Logger.info("setting home loality to " + localityName);
        Locality mLocality = null;
        if (placeId != null || !placeId.trim().isEmpty()) {
            Locality locality;
            mLocality = Locality.find.setMaxRows(1).where().eq("placeId", placeId).findUnique();
            if (mLocality != null) {
                return mLocality;
            }
        } else if (localityName != null && isValidLocalityName(localityName)) {
            mLocality = Locality.find.setMaxRows(1).where().eq("localityName", localityName).findUnique();
            if (mLocality != null) {
                if (mLocality.getLat() == null || mLocality.getLat() == 0.0
                        || mLocality.getLng() == null || mLocality.getLng() == 0.0) {
                    Logger.info("updating lat lng for : " + localityName + " in static table Locality");
                    mLocality.setLat(latitude);
                    mLocality.setLng(longitude);
                    mLocality.setPlaceId(placeId);
                    mLocality.update();
                }
                return mLocality;
            }
        }
        Locality locality = new Locality();
        locality.setLocalityName(localityName);
        locality.setLat(latitude);
        locality.setLng(longitude);
        locality.setPlaceId(placeId);
        locality.save();
        locality = Locality.find.setMaxRows(1).where().eq("localityName", localityName).findUnique();
        return locality;
    }

    public static Result mGetCandidateJobApplication() {
        CandidateAppliedJobsRequest candidateAppliedJobsRequest = null;
        //Main jobApplication response builder
        CandidateAppliedJobsResponse.Builder candidateAppliedJobsBuilder = CandidateAppliedJobsResponse.newBuilder();

        try {
            String requestString = request().body().asText();
            candidateAppliedJobsRequest = CandidateAppliedJobsRequest.parseFrom(Base64.decodeBase64(requestString));
            Candidate existingCandidate = CandidateService.isCandidateExists(FormValidator.convertToIndianMobileFormat(candidateAppliedJobsRequest.getCandidateMobile()));
            if (existingCandidate != null) {

                //job Application list builder which will contain all the job application
                List<JobApplicationObject> jobApplicationListToReturn = new ArrayList<JobApplicationObject>();


                //Getting list of all the job applications applied by a user from model
                List<JobApplication> jobApplicationList = JobApplication.find.where().eq("candidateId", existingCandidate.getCandidateId()).orderBy("jobApplicationId desc").findList();

                //Job Application Object (Proto) to get all the job application applied by the candidate (list object)
                JobApplicationObject.Builder jobApplicationBuilder = JobApplicationObject.newBuilder();

                //iterating all the applied jobs
                for (JobApplication jobApplication : jobApplicationList) {

                    //setting all the values form model object to proto object (builder)
                    jobApplicationBuilder.setJobApplicationAppliedMillis(jobApplication.getJobApplicationCreateTimeStamp().getTime());

                    //getting pre screened locality
                    if (jobApplication.getLocality() != null) {
                        LocalityObject.Builder localityBuilder = LocalityObject.newBuilder();
                        Locality locality = Locality.find.where().eq("localityId", jobApplication.getLocality().getLocalityId()).findUnique();
                        if (locality != null) {
                            localityBuilder.setLocalityName(locality.getLocalityName());
                            jobApplicationBuilder.setPreScreenLocation(localityBuilder.build());
                        }
                    }

                    //setting the job post of the applied job
                    jobApplicationBuilder.setJobPost(getJobPostInformationFromJobPostObject(jobApplication.getJobPost()));

                    //adding the jobApplicationBuilder to the main list to be returned
                    jobApplicationListToReturn.add(jobApplicationBuilder.build());
                }
                //adding the list to the main response builder
                candidateAppliedJobsBuilder.addAllJobApplication(jobApplicationListToReturn);
                candidateAppliedJobsBuilder.setStatus(CandidateAppliedJobsResponse.Status.valueOf(1));
                return ok(Base64.encodeBase64String(candidateAppliedJobsBuilder.build().toByteArray()));
            }
        } catch (InvalidProtocolBufferException e) {
            e.printStackTrace();
        }
        return ok("0");
    }

    public static Result mGetCandidateJobApplicationViaWorkFlow() {
        CandidateAppliedJobsRequest candidateAppliedJobsRequest = null;
        //Main jobApplication response builder
        CandidateAppliedJobPostWorkFlowResponse.Builder candidateAppliedJobPostWorkFlowResponse = CandidateAppliedJobPostWorkFlowResponse.newBuilder();

        try {
            String requestString = request().body().asText();
            candidateAppliedJobsRequest = CandidateAppliedJobsRequest.parseFrom(Base64.decodeBase64(requestString));
            Candidate existingCandidate = CandidateService.isCandidateExists(FormValidator.convertToIndianMobileFormat(candidateAppliedJobsRequest.getCandidateMobile()));
            if (existingCandidate != null) {

                //job Application list builder which will contain all the job application
                List<JobPostWorkFlowObject> jobApplicationListToReturn = new ArrayList<JobPostWorkFlowObject>();

                //Getting list of all the job applications applied by a user from model
                List<JobPostWorkflow> appliedJobsList = new JobPostWorkFlowDAO().candidateAppliedJobs(existingCandidate.getCandidateId());

                //Job Application Object (Proto) to get all the job application applied by the candidate (list object)
                JobPostWorkFlowObject.Builder jobPostWorkFlowObjBuilder = JobPostWorkFlowObject.newBuilder();

                //iterating all the applied jobs
                for (JobPostWorkflow jwpf : appliedJobsList) {

                    //setting all the values form model object to proto object (builder)
                    jobPostWorkFlowObjBuilder.setJobPostWorkflowId(jwpf.getJobPostWorkflowId());
                    jobPostWorkFlowObjBuilder.setWorkflowUUId(jwpf.getJobPostWorkflowUUId());
                    if (jwpf.getInterviewLocationLat() != null) {
                        jobPostWorkFlowObjBuilder.setInterviewLat(jwpf.getInterviewLocationLat());
                        jobPostWorkFlowObjBuilder.setInterviewLng(jwpf.getInterviewLocationLng());
                    }

                    jobPostWorkFlowObjBuilder.setCreationTimeMillis(jwpf.getCreationTimestamp().getTime());

                    if (jwpf.getScheduledInterviewDate() != null) {
                        jobPostWorkFlowObjBuilder.setInterviewDateMillis(jwpf.getScheduledInterviewDate().getTime());
                    }

                    if (jwpf.getScheduledInterviewTimeSlot() != null) {
                        InterviewTimeSlotObject.Builder interviewTimeSlotObjectBuilder = InterviewTimeSlotObject.newBuilder();
                        interviewTimeSlotObjectBuilder.setSlotId(jwpf.getScheduledInterviewTimeSlot().getInterviewTimeSlotId());
                        interviewTimeSlotObjectBuilder.setSlotTitle(jwpf.getScheduledInterviewTimeSlot().getInterviewTimeSlotName());

                        jobPostWorkFlowObjBuilder.setInterviewTimeSlotObject(interviewTimeSlotObjectBuilder.build());
                    }

                    if (jwpf.getStatus() != null) {
                        JobPostWorkFlowStatus.Builder jobPostWorkFlowStatusBuilder = JobPostWorkFlowStatus.newBuilder();
                        jobPostWorkFlowStatusBuilder.setStatusId(jwpf.getStatus().getStatusId());
                        jobPostWorkFlowStatusBuilder.setStatusTitle(jwpf.getStatus().getStatusTitle());

                        jobPostWorkFlowObjBuilder.setCandidateInterviewStatus(jobPostWorkFlowStatusBuilder.build());
                    }

                    if (jwpf.getJobPost() != null) {
                        JobPostObject.Builder jobPostObjectBuilder = JobPostObject.newBuilder();
                        jobPostObjectBuilder.setJobPostTitle(jwpf.getJobPost().getJobPostTitle());
                        jobPostObjectBuilder.setJobPostId(jwpf.getJobPost().getJobPostId());

                        //company
                        jobPostObjectBuilder.setJobPostCompanyName(jwpf.getJobPost().getCompany().getCompanyName());
                        jobPostObjectBuilder.setJobPostCompanyLogo(jwpf.getJobPost().getCompany().getCompanyLogo());

                        jobPostObjectBuilder.setJobPostAddress(jwpf.getJobPost().getJobPostAddress());
                        if(jwpf.getJobPost().getRecruiterProfile() != null){
                            jobPostObjectBuilder.setRecruiterName(jwpf.getJobPost().getRecruiterProfile().getRecruiterProfileName());
                        }

                        jobPostObjectBuilder.setJobPostMinSalary(jwpf.getJobPost().getJobPostMinSalary());
                        if (jwpf.getJobPost().getJobPostMaxSalary() == 0 || jwpf.getJobPost().getJobPostMaxSalary() == null) {
                            jobPostObjectBuilder.setJobPostMaxSalary(0);
                        } else {
                            jobPostObjectBuilder.setJobPostMaxSalary(jwpf.getJobPost().getJobPostMaxSalary());
                        }

                        //experience
                        if(jwpf.getJobPost().getJobPostExperience() != null){
                            ExperienceObject.Builder experienceObjectBuilder = ExperienceObject.newBuilder();
                            experienceObjectBuilder.setExperienceId(jwpf.getJobPost().getJobPostExperience().getExperienceId());
                            experienceObjectBuilder.setExperienceType(jwpf.getJobPost().getJobPostExperience().getExperienceType());
                            jobPostObjectBuilder.setJobPostExperience(experienceObjectBuilder.build());
                        }


                        if(jwpf.getJobPost().getJobPostEducation() != null){
                            //education
                            EducationObject.Builder educationObjectBuilder = EducationObject.newBuilder();
                            if(jwpf.getJobPost().getJobPostEducation() != null){
                                educationObjectBuilder.setEducationId(jwpf.getJobPost().getJobPostEducation().getEducationId());
                                educationObjectBuilder.setEducationName(jwpf.getJobPost().getJobPostEducation().getEducationName());
                            }
                            jobPostObjectBuilder.setEducation(educationObjectBuilder.build());
                        }

                        jobPostWorkFlowObjBuilder.setJobPostObject(jobPostObjectBuilder.build());
                    }

                    jobApplicationListToReturn.add(jobPostWorkFlowObjBuilder.build());
                }
                //adding the list to the main response builder
                candidateAppliedJobPostWorkFlowResponse.addAllJobPostWorkFlowObject(jobApplicationListToReturn);

                candidateAppliedJobPostWorkFlowResponse.setStatus(CandidateAppliedJobPostWorkFlowResponse.Status.valueOf(1));
                return ok(Base64.encodeBase64String(candidateAppliedJobPostWorkFlowResponse.build().toByteArray()));
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

            //getting all the values from the request builder and converting it to http request object
            AddCandidateRequest addCandidateRequest = new AddCandidateRequest();
            addCandidateRequest.setCandidateMobile(updateCandidateBasicProfileRequest.getCandidateMobile());
            addCandidateRequest.setCandidateFirstName(updateCandidateBasicProfileRequest.getCandidateFirstName());
            addCandidateRequest.setCandidateSecondName(updateCandidateBasicProfileRequest.getCandidateLastName());
            List<Integer> jobRoleIdList = new ArrayList<Integer>();
            for (JobRoleObject jobRoleObject : updateCandidateBasicProfileRequest.getJobRolePrefList()) {
                jobRoleIdList.add(Math.toIntExact(jobRoleObject.getJobRoleId()));
            }
            addCandidateRequest.setCandidateJobPref(jobRoleIdList);
            addCandidateRequest.setCandidateGender(updateCandidateBasicProfileRequest.getCandidateGender());
            addCandidateRequest.setCandidateAssetList(new ArrayList<>());
            //converting candidate DOB from string to Date
            String startDateString = updateCandidateBasicProfileRequest.getCandidateDOB();
            DateFormat df = new SimpleDateFormat("dd-MM-yyyy");
            Date startDate;
            try {
                startDate = df.parse(startDateString);
                addCandidateRequest.setCandidateDob(startDate);
            } catch (ParseException e) {
            }

            addCandidateRequest.setCandidateTimeShiftPref(String.valueOf(updateCandidateBasicProfileRequest.getCandidateTimeshiftPref()));

            //boolean isSupport = false;
            CandidateSignUpResponse candidateSignUpResponse = CandidateService.createCandidateProfile(addCandidateRequest,
                    InteractionConstants.INTERACTION_CHANNEL_CANDIDATE_ANDROID, ServerConstants.UPDATE_BASIC_PROFILE);

            //setting status response
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

            //getting all the values from the experience request builder and converting it to http request object
            AddCandidateExperienceRequest addCandidateExperienceRequest = new AddCandidateExperienceRequest();

            List<CandidateKnownLanguage> candidateKnownLanguageList = new ArrayList<CandidateKnownLanguage>();
            List<CandidateSkills> candidateSkillList = new ArrayList<CandidateSkills>();

            //getting language and adding in a list
            for (LanguageKnownObject languageKnown : updateCandidateExperienceProfileRequest.getCandidateLanguageList()) {
                CandidateKnownLanguage candidateKnownLanguage = new CandidateKnownLanguage();
                candidateKnownLanguage.setId(String.valueOf(languageKnown.getLanguageKnownId()));
                candidateKnownLanguage.setRw(languageKnown.getLanguageReadWrite());
                candidateKnownLanguage.setU(languageKnown.getLanguageUnderstand());
                candidateKnownLanguage.setS(languageKnown.getLanguageSpeak());
                candidateKnownLanguageList.add(candidateKnownLanguage);
            }

            //getting skills and adding in a list
            for (CandidateSkillObject candidateSkill : updateCandidateExperienceProfileRequest.getCandidateSkillList()) {
                CandidateSkills skill = new CandidateSkills();
                skill.setId(String.valueOf(candidateSkill.getSkillId()));
                skill.setAnswer(candidateSkill.getAnswer());
                candidateSkillList.add(skill);
            }

            addCandidateExperienceRequest.setCandidateMobile(FormValidator.convertToIndianMobileFormat(updateCandidateExperienceProfileRequest.getCandidateMobile()));

            //current Company
            if (updateCandidateExperienceProfileRequest.getCandidateCurrentCompany() != null) {
                addCandidateExperienceRequest.setCandidateCurrentCompany(updateCandidateExperienceProfileRequest.getCandidateCurrentCompany());
            }

            //current job role
            if (updateCandidateExperienceProfileRequest.getCurrentJobRole() != null) {
                addCandidateExperienceRequest.setCandidateCurrentJobRoleId(updateCandidateExperienceProfileRequest.getCurrentJobRole().getJobRoleId());
            }
            //last withdrawn salary, total experience and is Employed
            addCandidateExperienceRequest.setCandidateLastWithdrawnSalary(updateCandidateExperienceProfileRequest.getCandidateCurrentSalary());
            addCandidateExperienceRequest.setCandidateTotalExperience(updateCandidateExperienceProfileRequest.getCandidateTotalExperience());
            addCandidateExperienceRequest.setCandidateIsEmployed(updateCandidateExperienceProfileRequest.getCandidateIsEmployed());

            //setting language known
            addCandidateExperienceRequest.setCandidateLanguageKnown(candidateKnownLanguageList);
            //setting candidate skills
            addCandidateExperienceRequest.setCandidateSkills(candidateSkillList);

            //boolean isSupport = false;
            CandidateSignUpResponse candidateSignUpResponse = CandidateService.createCandidateProfile(addCandidateExperienceRequest,
                    InteractionConstants.INTERACTION_CHANNEL_CANDIDATE_ANDROID, ServerConstants.UPDATE_SKILLS_PROFILE);

            //setting status response
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

            //getting all the values from the education request builder and converting it to http request object
            addCandidateEducationRequest.setCandidateMobile(updateCandidateEducationProfileRequest.getCandidateMobile());
            addCandidateEducationRequest.setCandidateDegree(Math.toIntExact(updateCandidateEducationProfileRequest.getCandidateDegree()));
            addCandidateEducationRequest.setCandidateEducationLevel(Math.toIntExact(updateCandidateEducationProfileRequest.getCandidateEducationLevel()));
            addCandidateEducationRequest.setEducationStatus(updateCandidateEducationProfileRequest.getCandidateEducationCompletionStatus());
            addCandidateEducationRequest.setCandidateEducationInstitute(updateCandidateEducationProfileRequest.getCandidateEducationInstitute());

            //boolean isSupport = false;
            CandidateSignUpResponse candidateSignUpResponse = CandidateService.createCandidateProfile(addCandidateEducationRequest,
                    InteractionConstants.INTERACTION_CHANNEL_CANDIDATE_ANDROID, ServerConstants.UPDATE_EDUCATION_PROFILE);

            //setting status response
            updateCandidateProfileResponse.setStatus(UpdateCandidateBasicProfileResponse.Status.valueOf(candidateSignUpResponse.getStatus()));
            Logger.info("Status returned = " + updateCandidateProfileResponse.getStatus());

            if (updateCandidateEducationProfileRequest.getIsFinalFragment()) {
                Candidate candidate = CandidateService.isCandidateExists(updateCandidateEducationProfileRequest.getCandidateMobile());
                JobPostWorkflowEngine.savePreScreenResultForCandidateUpdate(candidate.getCandidateId(), updateCandidateEducationProfileRequest.getJobPostId(), InteractionConstants.INTERACTION_CHANNEL_CANDIDATE_ANDROID);
            }
        } catch (InvalidProtocolBufferException e) {
            Logger.info("Unable to parse message");
        }

        if (updateCandidateEducationProfileRequest == null) {
            Logger.info("Invalid message");
            return badRequest();
        }

        return ok(Base64.encodeBase64String(updateCandidateProfileResponse.build().toByteArray()));
    }

    public static Result mGetCandidateUpdateBasicProfileStatics() {
        //initializing response builder (proto)
        GetCandidateBasicProfileStaticResponse.Builder getCandidateBasicProfileStaticResponse = GetCandidateBasicProfileStaticResponse.newBuilder();

        try {
            //getting all the time shifts available from model
            List<TimeShift> timeShiftList = TimeShift.find.all();

            //creating a list of TimeShiftObject (proto)
            List<TimeShiftObject> timeShiftObjectList = new ArrayList<>();
            TimeShiftObject.Builder timeShiftBuilder = TimeShiftObject.newBuilder();
            for (TimeShift timeShift : timeShiftList) {
                timeShiftBuilder.setTimeShiftId(timeShift.getTimeShiftId());
                timeShiftBuilder.setTimeShiftName(timeShift.getTimeShiftName());
                timeShiftObjectList.add(timeShiftBuilder.build());
            }

            //adding the time shifts in response builder
            getCandidateBasicProfileStaticResponse.addAllTimeShiftList(timeShiftObjectList);
            getCandidateBasicProfileStaticResponse.setStatus(GetCandidateBasicProfileStaticResponse.Status.valueOf(1));

        } catch (Exception e) {
            getCandidateBasicProfileStaticResponse.setStatus(GetCandidateBasicProfileStaticResponse.Status.valueOf(2));
            Logger.info("Unable to parse message");
        }

        return ok(Base64.encodeBase64String(getCandidateBasicProfileStaticResponse.build().toByteArray()));
    }

    public static Result mGetCandidateUpdateEducationProfileStatics() {
        //initializing response builder (proto)
        GetCandidateEducationProfileStaticResponse.Builder getCandidateEducationProfileStaticResponse = GetCandidateEducationProfileStaticResponse.newBuilder();
        try {
            //getting all the education levels form the model
            List<Education> educationListToReturn = new ArrayList<>();
            List<Education> educationList = Education.find.all();

            for (Education e : educationList) {
                if (e.getEducationId() != 6) { //excluding any
                    educationListToReturn.add(e);
                }
            }
            //creating a list of EducationObject (proto) to get all the education levels
            List<EducationObject> educationObjectList = new ArrayList<>();
            EducationObject.Builder educationObjectBuilder = EducationObject.newBuilder();
            for (Education education : educationListToReturn) {
                educationObjectBuilder.setEducationId(education.getEducationId());
                educationObjectBuilder.setEducationName(education.getEducationName());
                educationObjectList.add(educationObjectBuilder.build());
            }
            //adding all the education levels in the response builder
            getCandidateEducationProfileStaticResponse.addAllEducationObject(educationObjectList);

            //getting all the degrees form the model
            List<Degree> degreeList = Degree.find.all();

            //creating a list of DegreeObject (proto) to get all the degrees
            List<DegreeObject> degreeObjectList = new ArrayList<>();
            DegreeObject.Builder degreeObjectBuilder = DegreeObject.newBuilder();
            for (Degree degree : degreeList) {
                degreeObjectBuilder.setDegreeId(degree.getDegreeId());
                degreeObjectBuilder.setDegreeName(degree.getDegreeName());
                degreeObjectList.add(degreeObjectBuilder.build());
            }
            //adding all the degrees in the response builder
            getCandidateEducationProfileStaticResponse.addAllDegreeObject(degreeObjectList);
            getCandidateEducationProfileStaticResponse.setStatus(GetCandidateEducationProfileStaticResponse.Status.valueOf(1));

        } catch (Exception e) {
            getCandidateEducationProfileStaticResponse.setStatus(GetCandidateEducationProfileStaticResponse.Status.valueOf(2));
            Logger.info("Unable to parse message");
        }
        return ok(Base64.encodeBase64String(getCandidateEducationProfileStaticResponse.build().toByteArray()));
    }

    public static Result mGetCandidateUpdateExperienceProfileStatics(String jobRoles) {
        GetCandidateExperienceProfileStaticResponse.Builder getCandidateExperienceProfileStaticResponse = GetCandidateExperienceProfileStaticResponse.newBuilder();

        try {
            //getting all languages from the model
            List<Language> languageList = Language.find.all();

            //creating a list of LanguageObject (proto) to get all the languages
            List<LanguageObject> languageObjectList = new ArrayList<>();
            LanguageObject.Builder languageBuilder = LanguageObject.newBuilder();

            for (Language language : languageList) {
                languageBuilder.setLanguageId(language.getLanguageId());
                languageBuilder.setLanguageName(language.getLanguageName());
                languageObjectList.add(languageBuilder.build());
            }

            //getting skills

            //"jobRoles" is a string variable which is a string of jobROles separated by commas (3,4,6). Comes from the user
            List<String> jobPrefIdList = Arrays.asList(jobRoles.split("\\s*,\\s*"));

            //creating a list of LanguageObject (proto) to get all the skills
            List<SkillObject> skillObjectList = new ArrayList<>();

            //skill builder (proto)
            SkillObject.Builder skillBuilder = SkillObject.newBuilder();

            //getting skills of particular job roles
            List<JobToSkill> response = new ArrayList<>();
            int flag = 0;
            for (String jobId : jobPrefIdList) {
                List<JobToSkill> jobToSkillList = JobToSkill.find.where().eq("JobRoleId", jobId).findList();
                if (response.isEmpty()) {
                    for (JobToSkill jobToSkill : jobToSkillList) {
                        skillBuilder.setSkillId(jobToSkill.getSkill().getSkillId());
                        skillBuilder.setSkillName(jobToSkill.getSkill().getSkillName());
                        skillBuilder.setSkillQuestion(jobToSkill.getSkill().getSkillQuestion());
                        skillObjectList.add(skillBuilder.build());
                    }
                    response.addAll(jobToSkillList);
                } else {
                    for (JobToSkill dbItem : jobToSkillList) {
                        flag = 0;
                        for (JobToSkill item : response) {
                            if (item.getSkill().getSkillId() == dbItem.getSkill().getSkillId()) {
                                flag = 1;
                                break;
                            }
                        }
                        if (flag == 0) {
                            response.add(dbItem);
                            skillBuilder.setSkillId(dbItem.getSkill().getSkillId());
                            skillBuilder.setSkillName(dbItem.getSkill().getSkillName());
                            skillBuilder.setSkillQuestion(dbItem.getSkill().getSkillQuestion());
                            skillObjectList.add(skillBuilder.build());
                        }
                    }
                }
            }
            //getting all the job roles from model
            List<models.entity.Static.JobRole> jobRoleList = models.entity.Static.JobRole.find.all();

            //setting all the job roles in response builder
            getCandidateExperienceProfileStaticResponse.addAllJobRole(getJobRoleObjectListFromJobRoleList(jobRoleList));

            //adding language and skill list to response object
            getCandidateExperienceProfileStaticResponse.addAllLanguageObject(languageObjectList);
            getCandidateExperienceProfileStaticResponse.addAllSkillObject(skillObjectList);
            getCandidateExperienceProfileStaticResponse.setStatus(GetCandidateExperienceProfileStaticResponse.Status.valueOf(1));

        } catch (Exception e) {
            getCandidateExperienceProfileStaticResponse.setStatus(GetCandidateExperienceProfileStaticResponse.Status.valueOf(2));
            Logger.info("Unable to parse message");
        }

        return ok(Base64.encodeBase64String(getCandidateExperienceProfileStaticResponse.build().toByteArray()));
    }

    public static Result mFetchCandidateAlert() {
        String requestString = request().body().asText();

        FetchCandidateAlertRequest fetchCandidateAlertProtoRequest = null;

        try {
            fetchCandidateAlertProtoRequest = FetchCandidateAlertRequest.parseFrom(Base64.decodeBase64(requestString));
        } catch (InvalidProtocolBufferException e) {
            e.printStackTrace();
        }

        String candidateMobile = fetchCandidateAlertProtoRequest.getCandidateMobile();

        FetchCandidateAlertResponse r = CandidateAlertService.getAlertForCandidate(FormValidator.convertToIndianMobileFormat(candidateMobile));

        return ok(Base64.encodeBase64String(r.toByteArray()));
    }

    /*
    * check out the following link to understand the default value when
    * a variable is not set
    * https://developers.google.com/protocol-buffers/docs/proto3#default
    */
    public static Result mSearchJobs() {
        JobSearchRequest jobSearchRequest = null;
        try {
            String requestString = request().body().asText();
            jobSearchRequest = JobSearchRequest.parseFrom(Base64.decodeBase64(requestString));
        } catch (InvalidProtocolBufferException e) {
            e.printStackTrace();
        }

        JobPostResponse.Builder jobPostResponseBuilder = JobPostResponse.newBuilder();
        JobFilterRequest.Builder jobFilterRequestBuilder = null;
        JobSearchByJobRoleRequest.Builder jobSearchByJobRoleRequest;
        List<Long> jobRoleIdList = new ArrayList<>();
        List<JobPost> jobPostList = new ArrayList<>();

        /*
         * Interaction Params
         */
        String objectAUUID = "";
        String interactionParamJobRole = "";
        String interactionParamLocality = "";
        String interactionParamFilter = "";
        String interactionResult = "Search for ";

        if (jobSearchRequest.hasJobSearchByJobRoleRequest()) {
            jobSearchByJobRoleRequest = jobSearchRequest.getJobSearchByJobRoleRequest().toBuilder();
            if (jobSearchByJobRoleRequest.getJobRoleIdOne() != 0) {
                Logger.info("1. Filter By JobRole : " + jobSearchByJobRoleRequest.getJobRoleIdOne());
                jobRoleIdList.add(jobSearchByJobRoleRequest.getJobRoleIdOne());
            }
            if (jobSearchByJobRoleRequest.getJobRoleIdTwo() != 0) {
                Logger.info("2. Filter By JobRole : " + jobSearchByJobRoleRequest.getJobRoleIdTwo());
                jobRoleIdList.add(jobSearchByJobRoleRequest.getJobRoleIdTwo());
            }
            if (jobSearchByJobRoleRequest.getJobRoleIdThree() != 0) {
                Logger.info("3. Filter By JobRole : " + jobSearchByJobRoleRequest.getJobRoleIdThree());
                jobRoleIdList.add(jobSearchByJobRoleRequest.getJobRoleIdThree());
            }
            /* setting interaction params*/
            if (jobRoleIdList.size() > 0) {
                List<JobRole> jobRoleList = JobRole.find.where().in("jobRoleId", jobRoleIdList).findList();
                if (jobRoleList != null) {
                    for (JobRole jobRole : jobRoleList) {
                        interactionParamJobRole += jobRole.getJobName() + ", ";
                    }
                    interactionParamJobRole = interactionParamJobRole.substring(0, interactionParamJobRole.length() - 2);
                }
            }
        }

        if (jobSearchRequest.hasJobFilterRequest()) {
            Logger.info("Filter by other filter options  triggered ");
            jobFilterRequestBuilder = jobSearchRequest.getJobFilterRequest().toBuilder();

            int sortby = ServerConstants.SORT_DEFAULT;
            if (jobFilterRequestBuilder.getSortBySalary()) {
                sortby = ServerConstants.SORT_BY_SALARY;
            } else if (jobFilterRequestBuilder.getSortByDatePosted()) {
                sortby = ServerConstants.SORT_BY_DATE_POSTED;
            }

            interactionParamFilter = "Sal: " + jobFilterRequestBuilder.getSalary() +
                    ", Edu: " + jobFilterRequestBuilder.getEdu() + ", Exp: " + jobFilterRequestBuilder.getExp()
                    + ", Gen: " + jobFilterRequestBuilder.getGender();

            // override the filter candidateMobile with search candidateMobile
            if (jobFilterRequestBuilder.getCandidateMobile().trim().isEmpty()) {
                jobFilterRequestBuilder.setCandidateMobile(jobSearchRequest.getCandidateMobile());
            }
            jobPostList.addAll(JobSearchService.getRelevantJobPostsWithinDistance(jobSearchRequest.getLatitude(),
                    jobSearchRequest.getLongitude(), jobRoleIdList, jobFilterRequestBuilder.build(),
                    sortby, false, false));

        } else {
            if (jobSearchRequest.getLatitude() != 0.0 && jobSearchRequest.getLongitude() != 0.0) {
                jobPostList.addAll(
                        JobSearchService.getRelevantJobPostsWithinDistance(jobSearchRequest.getLatitude(),
                                jobSearchRequest.getLongitude(), jobRoleIdList, null, ServerConstants.SORT_DEFAULT, false, false));
            } else {
                jobPostList.addAll(JobSearchService.getRelevantJobPostsWithinDistance(null,
                        null, jobRoleIdList, null, ServerConstants.SORT_DEFAULT, false, false));
            }
        }

        List<JobPostObject> jobPostListToReturn = convertToJobPostResponseList(jobPostList);

        //checking if the job is already applied or not
        if ((jobSearchRequest != null && !jobSearchRequest.getCandidateMobile().trim().isEmpty())
                || (jobFilterRequestBuilder != null && !jobFilterRequestBuilder.getCandidateMobile().trim().isEmpty())) {
            String candidateMobile;
            if (!jobSearchRequest.getCandidateMobile().trim().isEmpty()) {
                candidateMobile = jobSearchRequest.getCandidateMobile();
            } else {
                candidateMobile = jobFilterRequestBuilder.getCandidateMobile();
            }
            Candidate existingCandidate = CandidateService.isCandidateExists(FormValidator.convertToIndianMobileFormat(candidateMobile));

            if (existingCandidate != null) {
                objectAUUID = existingCandidate.getCandidateUUId();

                List<JobApplication> jobApplicationList = JobApplication.find.where().eq("candidateId", existingCandidate.getCandidateId()).findList();
                List<Long> appliedJobPostIdList = new ArrayList<Long>();
                for (JobApplication jobApplication : jobApplicationList) {
                    appliedJobPostIdList.add(jobApplication.getJobPost().getJobPostId());
                }

                for (int i = 0; i < jobPostListToReturn.size(); i++) {
                    if (appliedJobPostIdList.contains(jobPostListToReturn.get(i).getJobPostId())) {
                        JobPostObject.Builder newJobPostBuilder = jobPostListToReturn.get(i).toBuilder();
                        newJobPostBuilder.setIsApplied(1);
                        jobPostListToReturn.remove(i);
                        jobPostListToReturn.add(i, newJobPostBuilder.build());
                    }
                }
            } else {
                Logger.info("Null candidate Found!");
            }
        }

        jobPostResponseBuilder.addAllJobPost(jobPostListToReturn);
        Logger.info("Total Jobs Found: " + jobPostList.size());

        /* Interaction */
        if (objectAUUID.isEmpty()) {
            objectAUUID = ServerConstants.TRU_DROID_NOT_LOGGED_UUID;
        }
        interactionParamLocality = jobSearchRequest.getLocalityName().trim();
        if (interactionParamLocality.trim().isEmpty()) {
            /* Blank input is all bangalore */
            interactionParamLocality = "All Bangalore";
        }
        if (interactionParamJobRole.trim().isEmpty()) {
            /* Blank input is all job roles */
            interactionParamJobRole = "All JobRole";
        }
        /* TODO: Improve format for displaying info */
        if (interactionParamFilter.trim().isEmpty()) {
            interactionParamFilter = "Sal: ANY_SALARY, Edu: ANY_EDUCATION, Exp: ANY_EXPERIENCE, Gen: ANY_GENDER";
        }
        interactionResult += interactionParamJobRole + " @ " + interactionParamLocality + " with filter - " + interactionParamFilter + " returned (" + jobPostList.size() + ") Jobs";
        Logger.info("interactionResult : " + interactionResult);
        InteractionService.createInteractionForSearch(objectAUUID, interactionResult);

        return ok(Base64.encodeBase64String(jobPostResponseBuilder.build().toByteArray()));
    }

    /*
    * Test API
    * Only for testing the Address Resolver method Accuracy
    * */
    public static Result mResolveLatLng(String latlng) {
        List<String> LatLng = Arrays.asList(latlng.trim().split(","));
        Double latitude = 0D;
        Double longitude = 0D;
        try {
            latitude = Double.parseDouble(LatLng.get(0));
            longitude = Double.parseDouble(LatLng.get(1));
        } catch (NumberFormatException nfe) {
            return ok("Invalid Format");
        }
        return ok(toJson("LatLng: " + latlng + " Locality: " + controllers.businessLogic.AddressResolveService.resolveLocalityFor(latitude, longitude)));
    }


    /**
     * If locality is a specific place then the returned locality object contains that place's latlng instead of
     * locality latlng. Hence different from database.
     * Ex: if oceanus vista is selected whose locality is kasavanahalli
     * then returned locality object has oceanus vista's latlng not kasavanahalli lat lng
     * Logic for this resides inside businessLogic.AddressResolveService.getLocalityForPlaceId()
     * This insures that the person's home-locality is resolved to locality name yet the selected lat lng is preserved
     */
    public static Result mGetLocalityForLatLngOrPlaceId() {
        LatLngOrPlaceIdRequest latLngOrPlaceIdRequest = null;
        try {
            String requestString = request().body().asText();
            latLngOrPlaceIdRequest = LatLngOrPlaceIdRequest.parseFrom(Base64.decodeBase64(requestString));
        } catch (InvalidProtocolBufferException e) {
            e.printStackTrace();
        }
        Locality locality = null;
        LocalityObjectResponse.Builder localityObjectResponse = LocalityObjectResponse.newBuilder();

        if (!latLngOrPlaceIdRequest.getPlaceId().trim().isEmpty()) {
            locality = controllers.businessLogic.AddressResolveService
                    .getLocalityForPlaceId(latLngOrPlaceIdRequest.getPlaceId());
            localityObjectResponse.setType(LocalityObjectResponse.Type.FOR_PLACEID);
        }
        if (latLngOrPlaceIdRequest.getLatitude() != 0 && latLngOrPlaceIdRequest.getLongitude() != 0) {
            locality = controllers.businessLogic.AddressResolveService
                    .getLocalityForLatLng(latLngOrPlaceIdRequest.getLatitude(), latLngOrPlaceIdRequest.getLongitude());
            localityObjectResponse.setType(LocalityObjectResponse.Type.FOR_LATLNG);
        }
        LocalityObject.Builder localityObject = LocalityObject.newBuilder();
        if (locality != null) {
            localityObject.setLocalityName(locality.getLocalityName());
            localityObject.setLat(locality.getLat());
            localityObject.setLng(locality.getLng());
            localityObject.setLocalityId(locality.getLocalityId());
            if (locality.getPlaceId() != null) localityObject.setPlaceId(locality.getPlaceId());
            localityObjectResponse.setLocality(localityObject.build());
            localityObjectResponse.setStatus(LocalityObjectResponse.Status.SUCCESS);
            Logger.info("returned Locality name: " + locality.getLocalityName());
        } else {
            Logger.error("Unable to find locality for placeId:" + latLngOrPlaceIdRequest.getPlaceId() + " or lat/lng:" + latLngOrPlaceIdRequest.getLatitude() +
                    "/" + latLngOrPlaceIdRequest.getLatitude());
            localityObjectResponse.setStatus(LocalityObjectResponse.Status.UNKNOWN);
        }
        return ok(Base64.encodeBase64String(localityObjectResponse.build().toByteArray()));
    }

    public static Result mGetJobPostVsCandidate() {
        PreScreenPopulateProtoRequest preScreenPopulateRequest = null;
        try {
            String requestString = request().body().asText();
            preScreenPopulateRequest = PreScreenPopulateProtoRequest.parseFrom(Base64.decodeBase64(requestString));
        } catch (InvalidProtocolBufferException e) {
            e.printStackTrace();
        }

        PreScreenPopulateProtoResponse.Builder response = PreScreenPopulateProtoResponse.newBuilder();

        Logger.info("candidateMobile: " + preScreenPopulateRequest.getCandidateMobile());
        Logger.info("jobPostId: " + preScreenPopulateRequest.getJobPostId());

        if (preScreenPopulateRequest.getCandidateMobile() == null
                || preScreenPopulateRequest.getCandidateMobile().isEmpty()
                || preScreenPopulateRequest.getJobPostId() == 0) {
            response.setStatus(PreScreenPopulateProtoResponse.Status.FAILURE);
            return ok(Base64.encodeBase64String(response.build().toByteArray()));
        }

        Candidate candidate = CandidateService.isCandidateExists(preScreenPopulateRequest.getCandidateMobile());
        PreScreenPopulateResponse populateResponse = JobPostWorkflowEngine.getJobPostVsCandidate(preScreenPopulateRequest.getJobPostId(),
                candidate.getCandidateId(), false);

        JobPost jobPost = JobPost.find.where().eq("jobPostId", preScreenPopulateRequest.getJobPostId()).findUnique();
        if (jobPost == null) {
            return badRequest();
        } else {
            response.setPreScreenCompanyName(jobPost.getCompany().getCompanyName());
            response.setPreScreenJobRoleTitle(jobPost.getJobRole().getJobName());
            response.setPreScreenJobTitle(jobPost.getJobPostTitle());
        }

        response.setShouldShow(populateResponse.isVisible());
        for (PreScreenPopulateResponse.PreScreenElement pe : populateResponse.getElementList()) {
            if (pe != null && !pe.isMatching() && pe.getCandidateElement() == null) {
                // show UI to collect candidate missing data
                // add constructor object for only those object which are not filled by candidate
                response.setCandidateId(candidate.getCandidateId());
                response.setJobPostId(preScreenPopulateRequest.getJobPostId());

                /*
                * In app, experience fragment contains Last Withdrawn Salary
                * Locality will always be available if a candidate has signed up
                * via both {app, website}
                *
                * Hence ignoring both id
                *
                * */
                if(pe.getPropertyId() == PROPERTY_TYPE_LOCALITY ||
                        pe.getPropertyId() == PROPERTY_TYPE_SALARY){
                    continue;
                }
                response.addPropertyId(pe.getPropertyId());

                switch (pe.getPropertyId()) {
                    case PROPERTY_TYPE_DOCUMENT:
                        PreScreenDocumentObject.Builder preScreenDocument = PreScreenDocumentObject.newBuilder();
                        List<IdProofObject> idProofObjectList = new ArrayList<>();
                        for (Object object : pe.getJobPostElementList()) {
                            PreScreenPopulateResponse.PreScreenCustomObject customObject = (PreScreenPopulateResponse.PreScreenCustomObject) object;

                            IdProof idProof = (IdProof) customObject.getObject();
                            IdProofObject.Builder idProofObject = IdProofObject.newBuilder();
                            idProofObject.setIdProofId(idProof.getIdProofId());
                            idProofObject.setIdProofName(idProof.getIdProofName());
                            idProofObjectList.add(idProofObject.build());
                        }
                        preScreenDocument.setIsMatching(pe.isMatching());
                        preScreenDocument.addAllJobPostIdProof(idProofObjectList);
                        preScreenDocument.setPropertyTitle(ServerConstants.PROPERTY_TYPE_MAP.get(PROPERTY_TYPE_DOCUMENT));
                        preScreenDocument.setPropertyId((PROPERTY_TYPE_DOCUMENT));
                        response.addHpPropertyId(PROPERTY_TYPE_DOCUMENT);
                        response.setDocumentList(preScreenDocument.build());
                        break;
                    case PROPERTY_TYPE_LANGUAGE:
                        PreScreenLanguageObject.Builder preScreenLanguage = PreScreenLanguageObject.newBuilder();
                        List<LanguageObject> languageObjectList = new ArrayList<>();
                        for (Object object : pe.getJobPostElementList()) {
                            PreScreenPopulateResponse.PreScreenCustomObject customObject = (PreScreenPopulateResponse.PreScreenCustomObject) object;

                            Language language = (Language) customObject.getObject();
                            LanguageObject.Builder languageObject = LanguageObject.newBuilder();
                            languageObject.setLanguageId(language.getLanguageId());
                            languageObject.setLanguageName(language.getLanguageName());
                            languageObjectList.add(languageObject.build());
                        }
                        preScreenLanguage.setIsMatching(pe.isMatching());
                        preScreenLanguage.addAllJobPostLanguage(languageObjectList);
                        preScreenLanguage.setPropertyTitle(ServerConstants.PROPERTY_TYPE_MAP.get(PROPERTY_TYPE_LANGUAGE));
                        preScreenLanguage.setPropertyId((PROPERTY_TYPE_LANGUAGE));
                        response.addHpPropertyId(PROPERTY_TYPE_LANGUAGE);
                        response.setLanguageList(preScreenLanguage.build());
                        break;
                    case PROPERTY_TYPE_ASSET_OWNED:
                        PreScreenAssetObject.Builder preScreenAsset = PreScreenAssetObject.newBuilder();
                        List<AssetObject> assetObjectList = new ArrayList<>();
                        for (Object object : pe.getJobPostElementList()) {
                            PreScreenPopulateResponse.PreScreenCustomObject customObject = (PreScreenPopulateResponse.PreScreenCustomObject) object;

                            Asset asset = (Asset) customObject.getObject();
                            AssetObject.Builder assetObject = AssetObject.newBuilder();
                            assetObject.setAssetId(asset.getAssetId());
                            assetObject.setAssetTitle(asset.getAssetTitle());
                            assetObjectList.add(assetObject.build());
                        }
                        preScreenAsset.setIsMatching(pe.isMatching());
                        preScreenAsset.addAllJobPostAsset(assetObjectList);
                        preScreenAsset.setPropertyTitle(ServerConstants.PROPERTY_TYPE_MAP.get(PROPERTY_TYPE_ASSET_OWNED));
                        preScreenAsset.setPropertyId((PROPERTY_TYPE_ASSET_OWNED));
                        response.setAssetList(preScreenAsset.build());
                        break;
                    case PROPERTY_TYPE_MAX_AGE:
                        PreScreenAgeObject.Builder preScreenAge = PreScreenAgeObject.newBuilder();
                        preScreenAge.setIsMatching(pe.isMatching());
                        preScreenAge.setPropertyTitle(ServerConstants.PROPERTY_TYPE_MAP.get(PROPERTY_TYPE_MAX_AGE));
                        preScreenAge.setPropertyId((PROPERTY_TYPE_MAX_AGE));
                        response.setAge(preScreenAge.build());
                        break;
                    case PROPERTY_TYPE_EXPERIENCE:
                        PreScreenExperienceObject.Builder preScreenExperience = PreScreenExperienceObject.newBuilder();
                        preScreenExperience.setIsMatching(pe.isMatching());
                        preScreenExperience.setPropertyTitle(ServerConstants.PROPERTY_TYPE_MAP.get(PROPERTY_TYPE_EXPERIENCE));
                        preScreenExperience.setPropertyId((PROPERTY_TYPE_EXPERIENCE));
                        response.addHpPropertyId(PROPERTY_TYPE_EXPERIENCE);
                        response.setExperience(preScreenExperience.build());
                        break;
                    case PROPERTY_TYPE_EDUCATION:
                        PreScreenEducationObject.Builder preScreenEducation = PreScreenEducationObject.newBuilder();
                        preScreenEducation.setIsMatching(pe.isMatching());
                        preScreenEducation.setPropertyTitle(ServerConstants.PROPERTY_TYPE_MAP.get(PROPERTY_TYPE_EDUCATION));
                        preScreenEducation.setPropertyId((PROPERTY_TYPE_EDUCATION));
                        response.addHpPropertyId(PROPERTY_TYPE_EDUCATION);
                        response.setEducation(preScreenEducation.build());
                        break;
                    case PROPERTY_TYPE_GENDER:
                        PreScreenGenderObject.Builder preScreenGender = PreScreenGenderObject.newBuilder();
                        preScreenGender.setIsMatching(pe.isMatching());
                        preScreenGender.setPropertyTitle(ServerConstants.PROPERTY_TYPE_MAP.get(PROPERTY_TYPE_GENDER));
                        preScreenGender.setPropertyId((PROPERTY_TYPE_GENDER));
                        response.setGender(preScreenGender.build());
                        break;
                    case PROPERTY_TYPE_SALARY:
                        PreScreenSalaryObject.Builder preScreenSalary = PreScreenSalaryObject.newBuilder();
                        preScreenSalary.setIsMatching(pe.isMatching());
                        preScreenSalary.setPropertyTitle(ServerConstants.PROPERTY_TYPE_MAP.get(PROPERTY_TYPE_SALARY));
                        preScreenSalary.setPropertyId((PROPERTY_TYPE_SALARY));
                        response.setSalary(preScreenSalary.build());
                        break;
                }
            }
        }

        return ok(Base64.encodeBase64String(response.build().toByteArray()));
    }

    public static Result mUpdateCandidateDocument() {
        UpdateCandidateDocumentRequest updateCandidateDocumentRequest = null;
        GenericResponse.Builder responseBuilder = GenericResponse.newBuilder();

        try {
            String requestString = request().body().asText();
            updateCandidateDocumentRequest = UpdateCandidateDocumentRequest.parseFrom(Base64.decodeBase64(requestString));

            if (updateCandidateDocumentRequest == null) {
                Logger.info("Invalid message");
                return badRequest();
            }

            Candidate candidate = CandidateService.isCandidateExists(updateCandidateDocumentRequest.getCandidateMobile());
            if (candidate == null) {
                // candidate not found
                responseBuilder.setStatus(GenericResponse.Status.INVALID);
                return ok(Base64.encodeBase64String(responseBuilder.build().toByteArray()));
            }

            if (updateCandidateDocumentRequest.getIdProofList().size() < 1) {
                // nothing to update
                responseBuilder.setStatus(GenericResponse.Status.SUCCESS);
                return ok(Base64.encodeBase64String(responseBuilder.build().toByteArray()));
            }

            UpdateCandidateDocument updateCandidateDocument = new UpdateCandidateDocument();

            List<UpdateCandidateDocument.IdProofWithIdNumber> idNumberList = new ArrayList<>();
            for (IdProofObjectWithNumber idProofObjectWithNumber : updateCandidateDocumentRequest.getIdProofList()) {
                UpdateCandidateDocument.IdProofWithIdNumber idProofWithIdNumber = new UpdateCandidateDocument.IdProofWithIdNumber();
                idProofWithIdNumber.setIdProofId(idProofObjectWithNumber.getIdProof().getIdProofId());
                idProofWithIdNumber.setIdNumber(idProofObjectWithNumber.getIdProofNumber());
                idNumberList.add(idProofWithIdNumber);
            }
            updateCandidateDocument.setIdProofWithIdNumberList(idNumberList);

            CandidateService.updateCandidateDocument(candidate, updateCandidateDocument);

            responseBuilder.setStatus(GenericResponse.Status.SUCCESS);
            if (updateCandidateDocumentRequest.getIsFinalFragment()) {
                JobPostWorkflowEngine.savePreScreenResultForCandidateUpdate(candidate.getCandidateId(),
                        updateCandidateDocumentRequest.getJobPostId(),
                        InteractionConstants.INTERACTION_CHANNEL_CANDIDATE_ANDROID);
            }
        } catch (InvalidProtocolBufferException e) {
            Logger.info("Unable to parse message");
        }

        return ok(Base64.encodeBase64String(responseBuilder.build().toByteArray()));
    }

    public static Result mUpdateCandidateLanguage() {
        UpdateCandidateLanguageRequest languageRequest = null;
        GenericResponse.Builder response = GenericResponse.newBuilder();

        try {
            String requestString = request().body().asText();
            languageRequest = UpdateCandidateLanguageRequest.parseFrom(Base64.decodeBase64(requestString));

            if (languageRequest == null) {
                Logger.info("Invalid message");
                return badRequest();
            }

            Candidate candidate = CandidateService.isCandidateExists(languageRequest.getCandidateMobile());
            if (candidate == null) {
                // candidate not found
                response.setStatus(GenericResponse.Status.INVALID);
                return ok(Base64.encodeBase64String(response.build().toByteArray()));
            }

            UpdateCandidateLanguageKnown updateCandidateLanguageKnown = new UpdateCandidateLanguageKnown();
            List<CandidateKnownLanguage> candidateKnownLanguageList = new ArrayList<>();
            for (LanguageKnownObject languageKnownObject : languageRequest.getLanguageKnownObjectList()) {
                CandidateKnownLanguage candidateKnownLanguage = new CandidateKnownLanguage();
                candidateKnownLanguage.setId(String.valueOf(languageKnownObject.getLanguageKnownId()));
                candidateKnownLanguage.setRw(languageKnownObject.getLanguageReadWrite());
                candidateKnownLanguage.setS(languageKnownObject.getLanguageSpeak());
                candidateKnownLanguage.setU(languageKnownObject.getLanguageUnderstand());
                candidateKnownLanguageList.add(candidateKnownLanguage);
            }

            updateCandidateLanguageKnown.setCandidateKnownLanguageList(candidateKnownLanguageList);
            CandidateService.updateCandidateLanguageKnown(candidate, updateCandidateLanguageKnown);

            response.setStatus(GenericResponse.Status.SUCCESS);

            if (languageRequest.getIsFinalFragment()) {
                JobPostWorkflowEngine.savePreScreenResultForCandidateUpdate(
                        candidate.getCandidateId(), languageRequest.getJobPostId(),
                        InteractionConstants.INTERACTION_CHANNEL_CANDIDATE_ANDROID);
            }
        } catch (InvalidProtocolBufferException e) {
            Logger.info("Unable to parse message");
        }

        return ok(Base64.encodeBase64String(response.build().toByteArray()));
    }

    /* for candidate education, app uses the existing UpdateCandidateEducationProfile api and same class */

    public static Result mUpdateCandidateExperience() {
        UpdateCandidateExperienceRequest experienceRequest = null;
        GenericResponse.Builder response = GenericResponse.newBuilder();

        try {
            String requestString = request().body().asText();
            experienceRequest = UpdateCandidateExperienceRequest.parseFrom(Base64.decodeBase64(requestString));

            if (experienceRequest == null) {
                Logger.info("Invalid message");
                return badRequest();
            }

            Candidate candidate = CandidateService.isCandidateExists(experienceRequest.getCandidateMobile());
            if (candidate == null) {
                // candidate not found
                response.setStatus(GenericResponse.Status.INVALID);
                return ok(Base64.encodeBase64String(response.build().toByteArray()));
            }

            UpdateCandidateWorkExperience updateCandidateWorkExperience = new UpdateCandidateWorkExperience();

            updateCandidateWorkExperience.setCandidateTotalExperience(experienceRequest.getCandidateTotalExperience());

            CandidateService.updateCandidateWorkExperience(candidate, updateCandidateWorkExperience);


            response.setStatus(GenericResponse.Status.SUCCESS);

            if (experienceRequest.getIsFinalFragment()) {
                JobPostWorkflowEngine.savePreScreenResultForCandidateUpdate(candidate.getCandidateId(), experienceRequest.getJobPostId(),
                        InteractionConstants.INTERACTION_CHANNEL_CANDIDATE_ANDROID);
            }

        } catch (InvalidProtocolBufferException e) {
            Logger.info("Unable to parse message");
        }

        return ok(Base64.encodeBase64String(response.build().toByteArray()));
    }

    public static Result mUpdateCandidateOther() {
        UpdateCandidateOtherRequest otherRequest = null;
        GenericResponse.Builder response = GenericResponse.newBuilder();

        try {
            String requestString = request().body().asText();
            otherRequest = UpdateCandidateOtherRequest.parseFrom(Base64.decodeBase64(requestString));

            if (otherRequest == null) {
                Logger.info("Invalid message");
                return badRequest();
            }

            Candidate candidate = CandidateService.isCandidateExists(otherRequest.getCandidateMobile());
            if (candidate == null) {
                // candidate not found
                response.setStatus(GenericResponse.Status.INVALID);
                Logger.info(" candidate not found , invalid status returned");
                return ok(Base64.encodeBase64String(response.build().toByteArray()));
            }
            for (int propId : otherRequest.getPropertyIdList()) {

                switch (propId) {
                    case ServerConstants.PROPERTY_TYPE_ASSET_OWNED:
                        if(otherRequest.getAssetIdCount()>0){
                            UpdateCandidateAsset updateCandidateAsset = new UpdateCandidateAsset();
                            updateCandidateAsset.setAssetIdList(otherRequest.getAssetIdList());

                            CandidateService.updateCandidateAssetOwned(candidate, updateCandidateAsset);
                        }
                        break;
                    case ServerConstants.PROPERTY_TYPE_GENDER:
                        UpdateCandidateGender updateCandidateGender = new UpdateCandidateGender();

                        updateCandidateGender.setCandidateGender(otherRequest.getCandidateGender());
                        CandidateService.updateCandidateGender(candidate, updateCandidateGender);
                        break;
                    case ServerConstants.PROPERTY_TYPE_SALARY:
                        // this we collect with experience itself
//                      UpdateCandidateLastWithdrawnSalary lastWithdrawnSalary = new UpdateCandidateLastWithdrawnSalary();
//                      lastWithdrawnSalary.setCandidateLastWithdrawnSalary(otherRequest.getCandidateLastWithdrawnSalary());
//                      CandidateService.updateCandidateLastWithdrawnSalary(candidate, lastWithdrawnSalary);
                        break;
                    case ServerConstants.PROPERTY_TYPE_MAX_AGE:
                        UpdateCandidateDob updateCandidateDob = new UpdateCandidateDob();

                        String startDateString = otherRequest.getCandidateDOB();
                        DateFormat df = new SimpleDateFormat("dd-MM-yyyy");
                        Date dobDate;
                        try {
                            dobDate = df.parse(startDateString);
                            updateCandidateDob.setCandidateDob(dobDate);
                        } catch (ParseException e) {

                        }

                        CandidateService.updateCandidateDOB(candidate, updateCandidateDob);
                        break;
                    case ServerConstants.PROPERTY_TYPE_WORK_SHIFT:
                        UpdateCandidateTimeShiftPreference timeShiftPreference = new UpdateCandidateTimeShiftPreference();
                        timeShiftPreference.setCandidateTimeShiftPref(String.valueOf(otherRequest.getCandidateTimeshiftPref()));
                        CandidateService.updateCandidateWorkshift(candidate, timeShiftPreference);
                        break;
                }
            }

            if (otherRequest.getIsFinalFragment()) {
                JobPostWorkflowEngine.savePreScreenResultForCandidateUpdate(candidate.getCandidateId(), otherRequest.getJobPostId(), InteractionConstants.INTERACTION_CHANNEL_CANDIDATE_ANDROID);
            }

            response.setStatus(GenericResponse.Status.SUCCESS);
        } catch (InvalidProtocolBufferException e) {
            Logger.info("Unable to parse message");
        }

        return ok(Base64.encodeBase64String(response.build().toByteArray()));
    }

    public static Result mUpdateCandidateInterviewDetail() {
        UpdateCandidateInterviewDetailRequest interviewDetailRequest = null;
        GenericResponse.Builder response = GenericResponse.newBuilder();

        String requestString = request().body().asText();
        try {
            interviewDetailRequest = UpdateCandidateInterviewDetailRequest.parseFrom(Base64.decodeBase64(requestString));

            // validate conversion
            if (interviewDetailRequest == null) {
                return badRequest();
            }

            // check candidate existence
            Candidate candidate = CandidateService.isCandidateExists(interviewDetailRequest.getCandidateMobile());
            if (candidate == null) {
                // candidate not found
                response.setStatus(GenericResponse.Status.INVALID);
                Logger.info(" candidate not found , invalid status returned");
                return ok(Base64.encodeBase64String(response.build().toByteArray()));
            }

            AddCandidateInterviewSlotDetail interviewSlotDetail = new AddCandidateInterviewSlotDetail();

            interviewSlotDetail.setTimeSlot(interviewDetailRequest.getTimeSlotId());
            interviewSlotDetail.setScheduledInterviewDate(new Date(interviewDetailRequest.getScheduledInterviewDateInMills()));

            String resp = JobPostWorkflowEngine.updateCandidateInterviewDetail(candidate.getCandidateId(), interviewDetailRequest.getJobPostId(), interviewSlotDetail, InteractionConstants.INTERACTION_CHANNEL_CANDIDATE_ANDROID);

            if(resp == null) {
                response.setStatus(GenericResponse.Status.FAILURE);
            } else if(resp == "OK"){
                response.setStatus(GenericResponse.Status.SUCCESS);
            }
        } catch (InvalidProtocolBufferException e) {
            e.printStackTrace();
        }

        return ok(Base64.encodeBase64String(response.build().toByteArray()));
    }

    public static Result mGetInterviewSlot() {
        GetInterviewSlotsRequest interviewSlotsRequest = null;
        GetInterviewSlotsResponse.Builder response = GetInterviewSlotsResponse.newBuilder();

        String requestString = request().body().asText();
        try {
            interviewSlotsRequest = GetInterviewSlotsRequest.parseFrom(Base64.decodeBase64(requestString));

            // validate conversion
            if (interviewSlotsRequest == null) {
                return badRequest();
            }

            JobPost jobPost = JobPost.find.where().eq("jobPostId", interviewSlotsRequest.getJobPostId()).findUnique();

            if (jobPost == null) {
                return badRequest();
            }

            Map<String, InterviewDateTime> interviewSlotMap = new LinkedHashMap<>();
            // get today's date
            Calendar newCalendar = Calendar.getInstance();
            newCalendar.get(Calendar.YEAR);
            newCalendar.get(Calendar.MONTH);
            newCalendar.get(Calendar.DAY_OF_MONTH);
            Date today = newCalendar.getTime();

            for (int k = 2; k < 9; ++k) {

                Calendar c = Calendar.getInstance();
                c.setTime(today);
                c.add(Calendar.DATE, k);
                Date future = c.getTime();

                for (InterviewDetails details : jobPost.getInterviewDetailsList()) {
                /* while converting from decimal to binary, preceding zeros are ignored. to fix, follow below*/
                    String interviewDays = InterviewUtil.fixPrecedingZero(Integer.toBinaryString(details.getInterviewDays()));

                    if (InterviewUtil.checkSlotAvailability(future, interviewDays)) {

                        InterviewTimeSlotObject.Builder timeSlot = InterviewTimeSlotObject.newBuilder();
                        timeSlot.setSlotId(details.getInterviewTimeSlot().getInterviewTimeSlotId());
                        timeSlot.setSlotTitle(details.getInterviewTimeSlot().getInterviewTimeSlotName());

                        InterviewDateTime.Builder interviewDateTime = InterviewDateTime.newBuilder();
                        interviewDateTime.setInterviewTimeSlot(timeSlot.build());
                        interviewDateTime.setInterviewDateMillis(future.getTime());

                        String slotString = getDayVal(future.getDay())+ ", "
                                + future.getDate() + " " + getMonthVal((future.getMonth() + 1))
                                + " (" + details.getInterviewTimeSlot().getInterviewTimeSlotName() + ")" ;

                        interviewSlotMap.put(slotString, interviewDateTime.build());
                    }
                }
            }

            response.putAllInterviewSlotsMap(interviewSlotMap);

        } catch (InvalidProtocolBufferException e) {
            e.printStackTrace();
        }

        return ok(Base64.encodeBase64String(response.build().toByteArray()));
    }


    public static Result mConfirmInterview() {
        UpdateInterviewRequest updateInterviewRequest = null;
        try {
            String requestString = request().body().asText();
            updateInterviewRequest = UpdateInterviewRequest.parseFrom(Base64.decodeBase64(requestString));
        } catch (InvalidProtocolBufferException e) {
            e.printStackTrace();
        }
        UpdateInterviewResponse.Builder updateInterviewResponse = UpdateInterviewResponse.newBuilder();

        Candidate candidate = Candidate.find.where().eq("CandidateMobile", FormValidator.convertToIndianMobileFormat(updateInterviewRequest.getCandidateMobile())).findUnique();
        if (candidate != null) {
            if (JobPostWorkflowEngine.confirmCandidateInterview(updateInterviewRequest.getJpId(), updateInterviewRequest.getInterviewStatus(), candidate, InteractionConstants.INTERACTION_CHANNEL_CANDIDATE_ANDROID) == 1) {
                updateInterviewResponse.setStatus(UpdateInterviewResponse.Status.SUCCESS);
            } else {
                updateInterviewResponse.setStatus(UpdateInterviewResponse.Status.FAILURE);
            }
        }


        return ok(Base64.encodeBase64String(updateInterviewResponse.build().toByteArray()));
    }

    public static Result mUpdateCandidateStatus() {
        UpdateCandidateStatusRequest updateCandidateStatusRequest = null;
        try {
            String requestString = request().body().asText();
            updateCandidateStatusRequest = UpdateCandidateStatusRequest.parseFrom(Base64.decodeBase64(requestString));
        } catch (InvalidProtocolBufferException e) {
            e.printStackTrace();
        }

        UpdateCandidateStatusResponse.Builder updateCandidateStatusResponse = UpdateCandidateStatusResponse.newBuilder();

        Candidate candidate = Candidate.find.where().eq("CandidateMobile", FormValidator.convertToIndianMobileFormat(updateCandidateStatusRequest.getCandidateMobile())).findUnique();
        if (candidate != null) {
            JobPost jobPost = JobPost.find.where().eq("JobPostId", updateCandidateStatusRequest.getJpId()).findUnique();
            if (JobPostWorkflowEngine.updateCandidateInterviewStatus(candidate, jobPost,
                    Long.valueOf(updateCandidateStatusRequest.getCandidateStatus()),
                    updateCandidateStatusRequest.getNotGoingReason(), InteractionConstants.INTERACTION_CHANNEL_CANDIDATE_ANDROID) == 1) {

                updateCandidateStatusResponse.setStatus(UpdateCandidateStatusResponse.Status.SUCCESS);
            } else {
                updateCandidateStatusResponse.setStatus(UpdateCandidateStatusResponse.Status.FAILURE);
            }
        }

        return ok(Base64.encodeBase64String(updateCandidateStatusResponse.build().toByteArray()));
    }

    public static Result mGetAllNotGoingReason() {
        NotGoingReasonResponse.Builder notGoingReasonResponse = NotGoingReasonResponse.newBuilder();
        List<models.entity.Static.JobRole> jobRoleList =
                models.entity.Static.JobRole.find.where().orderBy().asc("jobName").findList();

        List<ReasonObject> reasonObjectList = new ArrayList<>();
        List<RejectReason> reason = new RejectReasonDAO().getByType(ServerConstants.INTERVIEW_NOT_GOING_TYPE_REASON);

        for (RejectReason rejectReason : reason) {
            ReasonObject.Builder rejectReasonBuilder = ReasonObject.newBuilder();
            rejectReasonBuilder.setReasonId(rejectReason.getReasonId());
            rejectReasonBuilder.setReasonTitle(rejectReason.getReasonName());
            reasonObjectList.add(rejectReasonBuilder.build());
        }

        notGoingReasonResponse.addAllReasonObject(reasonObjectList);
        return ok(Base64.encodeBase64String(notGoingReasonResponse.build().toByteArray()));
    }

    public static Result mCheckInterviewSlotAvailability() {
        CheckInterviewSlotRequest checkInterviewSlotRequest = null;

        try {
            String requestString = request().body().asText();
            checkInterviewSlotRequest  = CheckInterviewSlotRequest.parseFrom(Base64.decodeBase64(requestString));
        } catch (InvalidProtocolBufferException e) {
            e.printStackTrace();
        }
        CheckInterviewSlotResponse.Builder checkInterviewSlotResponse = CheckInterviewSlotResponse.newBuilder();
        checkInterviewSlotResponse.setStatus(CheckInterviewSlotResponse.Status.SUCCESS);

        if(checkInterviewSlotRequest.getJobPostId() > 0) {
            JobPost jobPost = JobPost.find.where().eq("jobPostId", checkInterviewSlotRequest.getJobPostId()).findUnique();
            if(jobPost == null) {
                checkInterviewSlotResponse.setStatus(CheckInterviewSlotResponse.Status.FAILURE);
            }
            if(JobPostWorkflowEngine.isInterviewRequired(jobPost).getStatus() == ServerConstants.INTERVIEW_REQUIRED){
                checkInterviewSlotResponse.setShouldShowInterview(true);
            } else {
                checkInterviewSlotResponse.setShouldShowInterview(false);
            }
        } else {
            checkInterviewSlotResponse.setStatus(CheckInterviewSlotResponse.Status.INVALID);
        }

        return ok(Base64.encodeBase64String(checkInterviewSlotResponse.build().toByteArray()));
    }

    public static Result mUpdateCandidateToken() {
        UpdateTokenRequest updateTokenRequest = null;
        UpdateTokenResponse.Builder updateTokenResponseBuilder = UpdateTokenResponse.newBuilder();

        try {
            String requestString = request().body().asText();
            updateTokenRequest = UpdateTokenRequest.parseFrom(Base64.decodeBase64(requestString));

            if(CandidateService.updateAndroidToken(updateTokenRequest.getToken(), updateTokenRequest.getCandidateId()) == 1){
                updateTokenResponseBuilder.setStatus(UpdateTokenResponse.Status.SUCCESS);
            } else{
                updateTokenResponseBuilder.setStatus(UpdateTokenResponse.Status.FAILURE);
            }

        } catch (InvalidProtocolBufferException e) {
            e.printStackTrace();
        }

        return ok(Base64.encodeBase64String(updateTokenResponseBuilder.build().toByteArray()));
    }

    public static Result mCandidateLogout() {
        LogoutCandidateRequest logoutCandidateRequest = null;
        LogoutCandidateResponse.Builder logoutCandidateResponseBuilder = LogoutCandidateResponse.newBuilder();

        try {
            String requestString = request().body().asText();
            logoutCandidateRequest = LogoutCandidateRequest.parseFrom(Base64.decodeBase64(requestString));

            if (CandidateService.logoutTrudroidCandidate(logoutCandidateRequest) == 1) {
                logoutCandidateResponseBuilder.setStatus(LogoutCandidateResponse.Status.SUCCESS);
            } else {
                logoutCandidateResponseBuilder.setStatus(LogoutCandidateResponse.Status.FAILURE);
            }

        } catch (InvalidProtocolBufferException e) {
            e.printStackTrace();
        }

        return ok(Base64.encodeBase64String(logoutCandidateResponseBuilder.build().toByteArray()));
    }
}
