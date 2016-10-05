package controllers.businessLogic;

import api.InteractionConstants;
import api.http.FormValidator;
import api.http.httpRequest.AddCompanyRequest;
import api.http.httpRequest.Recruiter.AddRecruiterRequest;
import api.http.httpRequest.Recruiter.RecruiterSignUpRequest;
import api.http.httpResponse.AddCompanyResponse;
import api.http.httpResponse.CandidateSignUpResponse;
import api.http.httpResponse.LoginResponse;
import api.http.httpResponse.Recruiter.AddRecruiterResponse;
import api.http.httpResponse.Recruiter.RecruiterSignUpResponse;
import models.entity.*;
import models.entity.Static.Locality;
import models.util.SmsUtil;
import models.util.Util;
import play.Logger;

import javax.persistence.NonUniqueResultException;
import java.util.List;
import java.util.UUID;

import static controllers.businessLogic.PartnerInteractionService.createInteractionForPartnerLogin;
import static models.util.Util.generateOtp;
import static play.libs.Json.toJson;
import static play.mvc.Controller.session;

/**
 * Created by batcoder1 on 7/7/16.
 */
public class RecruiterService {
    public static RecruiterProfile isRecruiterExists(String mobile) {
        RecruiterProfile recruiterProfile = RecruiterProfile.find.where().eq("RecruiterProfileMobile",
                FormValidator.convertToIndianMobileFormat(mobile)).findUnique();
        if(recruiterProfile != null) {
            return recruiterProfile;
        }
        return null;
    }

    public static LoginResponse login(String loginMobile, String loginPassword){
        LoginResponse loginResponse = new LoginResponse();
        Logger.info(" login mobile: " + loginMobile);
        RecruiterProfile existingRecruiter = isRecruiterExists(FormValidator.convertToIndianMobileFormat(loginMobile));
        if(existingRecruiter == null){
            loginResponse.setStatus(LoginResponse.STATUS_NO_USER);
            Logger.info("Recruiter Does not Exists");
        } else {
            long recruiterId = existingRecruiter.getRecruiterProfileId();
            RecruiterAuth recruiterAuth = RecruiterAuth.find.where().eq("recruiter_id", recruiterId).findUnique();
            if(recruiterAuth != null){
                if ((recruiterAuth.getPasswordMd5().equals(Util.md5(loginPassword + recruiterAuth.getPasswordSalt())))) {
                    loginResponse.setCandidateId(existingRecruiter.getRecruiterProfileId());
                    loginResponse.setCandidateFirstName(existingRecruiter.getRecruiterProfileName());

                    loginResponse.setStatus(LoginResponse.STATUS_SUCCESS);

                    recruiterAuth.setAuthSessionId(UUID.randomUUID().toString());
                    recruiterAuth.setAuthSessionIdExpiryMillis(System.currentTimeMillis() + 24 * 60 * 60 * 1000);

                    loginResponse.setAuthSessionId(recruiterAuth.getAuthSessionId());
                    loginResponse.setSessionExpiryInMilliSecond(recruiterAuth.getAuthSessionIdExpiryMillis());

                    /* adding session details */
                    RecruiterAuthService.addSession(recruiterAuth, existingRecruiter);
                    String sessionId = session().get("sessionId");
                    recruiterAuth.update();
                    Logger.info("Login Successful");
                } else {
                    loginResponse.setStatus(loginResponse.STATUS_WRONG_PASSWORD);
                    Logger.info("Incorrect Password");
                }
            } else {
                loginResponse.setStatus(loginResponse.STATUS_NO_USER);
                Logger.info("No User");
            }
        }
        return loginResponse;
    }


    public static RecruiterSignUpResponse recruiterSignUp(RecruiterSignUpRequest recruiterSignUpRequest){
        RecruiterSignUpResponse recruiterSignUpResponse = new RecruiterSignUpResponse();
        RecruiterProfile newRecruiter = new RecruiterProfile();

        Logger.info("Checking for mobile number: " + FormValidator.convertToIndianMobileFormat(recruiterSignUpRequest.getRecruiterMobile()));
        RecruiterProfile recruiterProfile = isRecruiterExists(FormValidator.convertToIndianMobileFormat(recruiterSignUpRequest.getRecruiterMobile()));

        recruiterSignUpResponse.setRecruiterMobile(recruiterSignUpRequest.getRecruiterMobile());
        if(recruiterProfile == null){
            //checking if company exists or not
            Company existingCompany = Company.find.where().eq("companyName", recruiterSignUpRequest.getRecruiterCompany()).findUnique();
            if(existingCompany == null){
                AddCompanyResponse addCompanyResponse;
                AddCompanyRequest addCompanyRequest = new AddCompanyRequest();
                addCompanyRequest.setCompanyName(recruiterSignUpRequest.getRecruiterCompanyName());
                addCompanyRequest.setCompanyStatus(1);
                addCompanyResponse = CompanyService.addCompany(addCompanyRequest);

                existingCompany = Company.find.where().eq("companyId", addCompanyResponse.getCompanyId()).findUnique();
                if(existingCompany == null){
                    recruiterSignUpResponse.setStatus(RecruiterSignUpResponse.getStatusFailure());
                    return recruiterSignUpResponse;
                }
            }

            newRecruiter.setRecruiterProfileMobile(FormValidator.convertToIndianMobileFormat(recruiterSignUpRequest.getRecruiterMobile()));
            newRecruiter = getAndSetRecruiterValues(recruiterSignUpRequest, newRecruiter, existingCompany);
            triggerOtp(newRecruiter, recruiterSignUpResponse);
            newRecruiter.save();

            recruiterSignUpResponse.setStatus(AddRecruiterResponse.STATUS_SUCCESS);
            recruiterSignUpResponse.setRecruiterId(newRecruiter.getRecruiterProfileId());
        } else{
            RecruiterAuth auth = RecruiterAuthService.isAuthExists(recruiterProfile.getRecruiterProfileId());
            if(auth == null ) {
                Logger.info("recruiter auth doesn't exists for this recruiter");
                getAndSetRecruiterValues(recruiterSignUpRequest, recruiterProfile, null);
                recruiterSignUpResponse.setStatus(AddRecruiterResponse.STATUS_SUCCESS);
                recruiterSignUpResponse.setRecruiterId(recruiterProfile.getRecruiterProfileId());
                recruiterSignUpResponse.setRecruiterMobile(recruiterProfile.getRecruiterProfileMobile());

                triggerOtp(newRecruiter, recruiterSignUpResponse);
            } else{
                recruiterSignUpResponse.setStatus(RecruiterSignUpResponse.STATUS_EXISTS);
            }
            recruiterProfile.update();
        }

        Logger.info("Recruiter successfully saved");

        return recruiterSignUpResponse;
    }

    public static AddRecruiterResponse createRecruiterProfile(RecruiterSignUpRequest recruiterSignUpRequest) {
        AddRecruiterResponse addRecruiterResponse = new AddRecruiterResponse();
        Company existingCompany = Company.find.where().eq("companyId", recruiterSignUpRequest.getRecruiterCompany()).findUnique();
        if(existingCompany != null){
            RecruiterProfile existingRecruiter = isRecruiterExists(FormValidator.convertToIndianMobileFormat(recruiterSignUpRequest.getRecruiterMobile()));
            if(existingRecruiter == null){
                RecruiterProfile newRecruiter = new RecruiterProfile();
                newRecruiter.setRecruiterProfileMobile(FormValidator.convertToIndianMobileFormat(recruiterSignUpRequest.getRecruiterMobile()));
                newRecruiter = getAndSetRecruiterValues(recruiterSignUpRequest, newRecruiter, existingCompany);

                newRecruiter.save();
                Logger.info("3");

                addRecruiterResponse.setStatus(AddRecruiterResponse.STATUS_SUCCESS);
                addRecruiterResponse.setRecruiterId(newRecruiter.getRecruiterProfileId());

                RecruiterSignUpResponse recruiterSignUpResponse = new RecruiterSignUpResponse();
//                triggerOtp(newRecruiter, recruiterSignUpResponse);
                Logger.info("Recruiter successfully saved");
            } else{
                existingRecruiter = getAndSetRecruiterValues(recruiterSignUpRequest, existingRecruiter, existingCompany);
                existingRecruiter.update();
                addRecruiterResponse.setRecruiterId(existingRecruiter.getRecruiterProfileId());
                addRecruiterResponse.setStatus(AddRecruiterResponse.STATUS_UPDATE);
                Logger.info("Recruiter already exists. Updated The recruiter");
            }
        } else{
            addRecruiterResponse.setStatus(AddRecruiterResponse.STATUS_FAILURE);
            Logger.info("Company Does not exists");
        }
        return addRecruiterResponse;
    }

    public static RecruiterProfile getAndSetRecruiterValues(AddRecruiterRequest addRecruiterRequest, RecruiterProfile newRecruiter, Company existingCompany){
        if(existingCompany != null){
            newRecruiter.setRecCompany(existingCompany);
        }
        if(addRecruiterRequest.getRecruiterName() != null){
            newRecruiter.setRecruiterProfileName(addRecruiterRequest.getRecruiterName());
        }
        if(addRecruiterRequest.getRecruiterLandline() != null){
            newRecruiter.setRecruiterProfileLandline(addRecruiterRequest.getRecruiterLandline());
        } else{
            newRecruiter.setRecruiterProfileLandline("0");
        }
        if(addRecruiterRequest.getRecruiterEmail() != null){
            newRecruiter.setRecruiterProfileEmail(addRecruiterRequest.getRecruiterEmail());
        }
        return newRecruiter;
    }

    private static void triggerOtp(RecruiterProfile recruiterProfile, RecruiterSignUpResponse recruiterSignUpResponse) {
        int randomPIN = generateOtp();
        SmsUtil.sendRecruiterOTPSms(randomPIN, recruiterProfile.getRecruiterProfileMobile());

        recruiterSignUpResponse.setStatus(RecruiterSignUpResponse.getStatusSuccess());
        recruiterSignUpResponse.setOtp(randomPIN);
    }
}