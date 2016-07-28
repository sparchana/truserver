package controllers;

import api.ServerConstants;
import api.http.FormValidator;
import api.http.httpRequest.*;
import api.http.httpResponse.*;
import com.google.api.client.util.Base64;
import com.google.protobuf.InvalidProtocolBufferException;
import controllers.businessLogic.AuthService;
import controllers.businessLogic.CandidateService;
import controllers.businessLogic.JobService;
import in.trujobs.proto.*;
import in.trujobs.proto.ApplyJobRequest;
import in.trujobs.proto.ApplyJobResponse;
import in.trujobs.proto.ResetPasswordResponse;
import models.entity.OM.JobPostToLocality;
import play.Logger;
import play.mvc.Result;

import java.util.ArrayList;
import java.util.List;

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
        LogInResponse.Builder builder = LogInResponse.newBuilder();

        try {
            String requestString = request().body().asText();
            pLogInRequest = LogInRequest.parseFrom(Base64.decodeBase64(requestString));
            LoginRequest loginRequest = new LoginRequest();
            loginRequest.setCandidateLoginMobile(pLogInRequest.getCandidateMobile());
            loginRequest.setCandidateLoginPassword(pLogInRequest.getCandidatePassword());

            LoginResponse loginResponse = CandidateService.login(loginRequest.getCandidateLoginMobile(), loginRequest.getCandidateLoginPassword());
            builder.setStatus(LogInResponse.Status.valueOf(loginResponse.getStatus()));
            if(loginResponse.getStatus() == 1){
                builder.setCandidateFirstName(loginResponse.getCandidateFirstName());
                builder.setCandidateLastName(loginResponse.getCandidateLastName());
                builder.setCandidateId(loginResponse.getCandidateId());
                builder.setCandidateIsAssessed(loginResponse.getIsAssessed());
                builder.setLeadId(loginResponse.getLeadId());
            }

            Logger.info("Status returned = " + builder.getStatus());

        } catch (InvalidProtocolBufferException e) {
            Logger.info("Unable to parse message");
        }

        if (pLogInRequest == null) {
            Logger.info("Invalid message");
            return badRequest();
        }

        return ok(Base64.encodeBase64String(builder.build().toByteArray()));
    }

    public static Result mSignUp() {
        SignUpRequest pSignUpRequest = null;
        SignUpResponse.Builder builder = SignUpResponse.newBuilder();

        try {
            String requestString = request().body().asText();
            pSignUpRequest = SignUpRequest.parseFrom(Base64.decodeBase64(requestString));
            CandidateSignUpRequest candidateSignUpRequest = new CandidateSignUpRequest();
            candidateSignUpRequest.setCandidateFirstName(pSignUpRequest.getName());
            candidateSignUpRequest.setCandidateMobile(pSignUpRequest.getMobile());

            boolean isSupport = false;
            CandidateSignUpResponse candidateSignUpResponse = CandidateService.signUpCandidate(candidateSignUpRequest,isSupport, ServerConstants.LEAD_SOURCE_UNKNOWN);
            builder.setStatus(SignUpResponse.Status.valueOf(candidateSignUpResponse.getStatus()));
            builder.setGeneratedOtp(candidateSignUpResponse.getOtp());

            Logger.info("Status returned = " + builder.getStatus());

        } catch (InvalidProtocolBufferException e) {
            Logger.info("Unable to parse message");
        }

        if (pSignUpRequest == null) {
            Logger.info("Invalid message");
            return badRequest();
        }
        return ok(Base64.encodeBase64String(builder.build().toByteArray()));
    }

    public static Result mAddPassword() {
        LogInRequest pLoginRequest = null;
        LogInResponse.Builder builder = LogInResponse.newBuilder();

        try {
            String requestString = request().body().asText();
            pLoginRequest = LogInRequest.parseFrom(Base64.decodeBase64(requestString));
            CandidateSignUpResponse candidateSignUpResponse = AuthService.savePassword(FormValidator.convertToIndianMobileFormat(pLoginRequest.getCandidateMobile()), pLoginRequest.getCandidatePassword());
            builder.setStatus(LogInResponse.Status.valueOf(candidateSignUpResponse.getStatus()));
            builder.setCandidateFirstName(candidateSignUpResponse.getCandidateFirstName());
            if(candidateSignUpResponse.getCandidateLastName() != null){
                builder.setCandidateLastName(candidateSignUpResponse.getCandidateLastName());
            }
            builder.setCandidateId(candidateSignUpResponse.getCandidateId());
            builder.setCandidateIsAssessed(candidateSignUpResponse.getIsAssessed());
            builder.setLeadId(candidateSignUpResponse.getLeadId());
            builder.setMinProfile(candidateSignUpResponse.getMinProfile());
            Logger.info("Status returned = " + builder.getStatus());

        } catch (InvalidProtocolBufferException e) {
            Logger.info("Unable to parse message");
        }

        if (pLoginRequest == null) {
            Logger.info("Invalid message");
            return badRequest();
        }
        return ok(Base64.encodeBase64String(builder.build().toByteArray()));
    }

    public static Result mFindUserAndSendOtp() {
        ResetPasswordRequest pResetPasswordRequest = null;
        ResetPasswordResponse.Builder builder = ResetPasswordResponse.newBuilder();

        try {
            String requestString = request().body().asText();
            pResetPasswordRequest = ResetPasswordRequest.parseFrom(Base64.decodeBase64(requestString));
            api.http.httpResponse.ResetPasswordResponse resetPasswordResponse = CandidateService.findUserAndSendOtp(FormValidator.convertToIndianMobileFormat(pResetPasswordRequest.getMobile()));
            builder.setStatus(ResetPasswordResponse.Status.valueOf(resetPasswordResponse.getStatus()));
            builder.setOtp(resetPasswordResponse.getOtp());

            Logger.info("Status returned = " + builder.getStatus());

        } catch (InvalidProtocolBufferException e) {
            Logger.info("Unable to parse message");
        }

        if (pResetPasswordRequest == null) {
            Logger.info("Invalid message");
            return badRequest();
        }
        return ok(Base64.encodeBase64String(builder.build().toByteArray()));
    }

    public static Result mGetAllJobRoles() {
        JobRoleResponse.Builder builder = JobRoleResponse.newBuilder();
        List<models.entity.Static.JobRole> jobRoleList = models.entity.Static.JobRole.find.all();

        List<JobRole> jobRoleListToReturn = new ArrayList<>();
        for (models.entity.Static.JobRole jobRole: jobRoleList) {
            JobRole.Builder jobRoleBuilder
                    = JobRole.newBuilder();
            jobRoleBuilder.setJobRoleId(String.valueOf(jobRole.getJobRoleId()));
            jobRoleBuilder.setJobRoleName(jobRole.getJobName());

            jobRoleListToReturn.add(jobRoleBuilder.build());
        }
        builder.addAllJobRole(jobRoleListToReturn);
        return ok(Base64.encodeBase64String(builder.build().toByteArray()));
    }

    public static Result mGetAllJobPosts() {
        JobPostResponse.Builder builder = JobPostResponse.newBuilder();
        List<models.entity.JobPost> jobPostList = models.entity.JobPost.find.where().eq("jobPostIsHot", "1").findList();

        List<JobPost> jobPostListToReturn = new ArrayList<>();
        for (models.entity.JobPost jobPost: jobPostList) {
            JobPost.Builder jobPostBuilder
                    = JobPost.newBuilder();
            jobPostBuilder.setJobPostId(jobPost.getJobPostId());
            jobPostBuilder.setJobPostTitle(jobPost.getJobPostTitle());
            jobPostBuilder.setJobPostCompanyName(jobPost.getCompany().getCompanyName());
            jobPostBuilder.setJobPostMinSalary(jobPost.getJobPostMinSalary());
            jobPostBuilder.setJobPostMaxSalary(jobPost.getJobPostMaxSalary());

            jobPostBuilder.setJobPostCompanyLogo(jobPost.getCompany().getCompanyLogo());

            Experience.Builder experienceBuilder = Experience.newBuilder();
            experienceBuilder.setExperienceId(jobPost.getJobPostExperience().getExperienceId());
            experienceBuilder.setExperienceType(jobPost.getJobPostExperience().getExperienceType());
            jobPostBuilder.setJobPostExperience(experienceBuilder);

            TimeShift.Builder timeShiftBuilder = TimeShift.newBuilder();
            timeShiftBuilder.setTimeShiftId(jobPost.getJobPostShift().getTimeShiftId());
            timeShiftBuilder.setTimeShiftName(jobPost.getJobPostShift().getTimeShiftName());
            jobPostBuilder.setJobPostShift(timeShiftBuilder);

            List<Locality> jobPostLocalities = new ArrayList<>();
            List<JobPostToLocality> localityList = jobPost.getJobPostToLocalityList();
            for (JobPostToLocality locality: localityList) {
                Locality.Builder localityBuilder
                        = Locality.newBuilder();
                localityBuilder.setLocalityId(locality.getLocality().getLocalityId());
                localityBuilder.setLocalityName(locality.getLocality().getLocalityName());
                jobPostLocalities.add(localityBuilder.build());
            }
            jobPostBuilder.addAllJobPostLocality(jobPostLocalities);

            jobPostListToReturn.add(jobPostBuilder.build());
        }
        builder.addAllJobPost(jobPostListToReturn);
        return ok(Base64.encodeBase64String(builder.build().toByteArray()));
    }

    public static Result mApplyJob() {
        ApplyJobRequest pApplyJobRequest = null;
        ApplyJobResponse.Builder builder = ApplyJobResponse.newBuilder();

        try {
            String requestString = request().body().asText();
            pApplyJobRequest = ApplyJobRequest.parseFrom(Base64.decodeBase64(requestString));
            api.http.httpRequest.ApplyJobRequest applyJobRequest = new api.http.httpRequest.ApplyJobRequest();
            applyJobRequest.setJobId(Math.toIntExact(pApplyJobRequest.getJobPostId()));
            applyJobRequest.setLocalityId(Math.toIntExact(pApplyJobRequest.getLocalityId()));
            applyJobRequest.setCandidateMobile(FormValidator.convertToIndianMobileFormat(pApplyJobRequest.getCandidateMobile()));
            api.http.httpResponse.ApplyJobResponse applyJobResponse = JobService.applyJob(applyJobRequest);
            builder.setStatus(ApplyJobResponse.Status.valueOf(applyJobResponse.getStatus()));

            Logger.info("Status returned = " + builder.getStatus());

        } catch (InvalidProtocolBufferException e) {
            Logger.info("Unable to parse message");
        }

        if (pApplyJobRequest == null) {
            Logger.info("Invalid message");
            return badRequest();
        }
        return ok(Base64.encodeBase64String(builder.build().toByteArray()));
    }
}
