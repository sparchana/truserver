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
import in.trujobs.proto.*;
import models.entity.Candidate;
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
        List<models.entity.JobPost> jobPostList = models.entity.JobPost.find.all();
        List<JobPost> jobPostListToReturn = new ArrayList<>();
        for (models.entity.JobPost jobPost: jobPostList) {
            JobPost.Builder jobPostBuilder
                    = JobPost.newBuilder();
            jobPostBuilder.setJobPostId(jobPost.getJobPostId());
            jobPostBuilder.setJobPostTitle(jobPost.getJobPostTitle());
            jobPostBuilder.setJobPostCompanyName(jobPost.getCompany().getCompanyName());
            jobPostBuilder.setJobPostMinSalary(jobPost.getJobPostMinSalary());
            jobPostBuilder.setJobPostMaxSalary(jobPost.getJobPostMaxSalary());

            jobPostListToReturn.add(jobPostBuilder.build());
        }
        builder.addAllJobPost(jobPostListToReturn);
        return ok(Base64.encodeBase64String(builder.build().toByteArray()));
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
}
