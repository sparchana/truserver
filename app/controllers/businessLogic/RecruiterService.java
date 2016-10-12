package controllers.businessLogic;

import api.ServerConstants;
import api.http.FormValidator;
import api.http.httpRequest.AddCompanyRequest;
import api.http.httpRequest.Recruiter.AddRecruiterRequest;
import api.http.httpRequest.Recruiter.RecruiterSignUpRequest;
import api.http.httpResponse.AddCompanyResponse;
import api.http.httpResponse.LoginResponse;
import api.http.httpResponse.Recruiter.AddRecruiterResponse;
import api.http.httpResponse.Recruiter.RecruiterSignUpResponse;
import models.entity.Recruiter.RecruiterAuth;
import models.entity.Recruiter.RecruiterLead;
import models.entity.Recruiter.RecruiterPayment;
import models.entity.Recruiter.RecruiterProfile;
import models.entity.*;
import models.entity.Recruiter.Static.RecruiterCreditCategory;
import models.entity.Recruiter.Static.RecruiterStatus;
import models.util.SmsUtil;
import models.util.Util;
import play.Logger;
import play.mvc.Result;

import java.util.UUID;

import static models.util.Util.generateOtp;
import static play.mvc.Controller.session;
import static play.mvc.Results.ok;

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

            //setting recruiter lead
            String leadName = recruiterSignUpRequest.getRecruiterName();
            RecruiterLead lead = RecruiterLeadService.createOrUpdateConvertedRecruiterLead(leadName, FormValidator.convertToIndianMobileFormat(recruiterSignUpRequest.getRecruiterMobile()));
            newRecruiter.setRecruiterLead(lead);

            //setting recruiter status as "NEW"
            RecruiterStatus recruiterStatus = RecruiterStatus.find.where().eq("RecruiterStatusId", 1).findUnique();
            if(recruiterStatus != null){
                newRecruiter.setRecStatus(recruiterStatus);
            }

            newRecruiter.save();

            recruiterSignUpResponse.setStatus(AddRecruiterResponse.STATUS_SUCCESS);
            recruiterSignUpResponse.setRecruiterId(newRecruiter.getRecruiterProfileId());
            Logger.info("Recruiter successfully saved");

        } else{
            RecruiterAuth auth = RecruiterAuthService.isAuthExists(recruiterProfile.getRecruiterProfileId());
            if(auth == null ) {
                Logger.info("recruiter auth doesn't exists for this recruiter");
                getAndSetRecruiterValues(recruiterSignUpRequest, recruiterProfile, null);
                recruiterSignUpResponse.setStatus(AddRecruiterResponse.STATUS_SUCCESS);
                recruiterSignUpResponse.setRecruiterId(recruiterProfile.getRecruiterProfileId());
                recruiterSignUpResponse.setRecruiterMobile(recruiterProfile.getRecruiterProfileMobile());

                triggerOtp(recruiterProfile, recruiterSignUpResponse);
            } else{
                Logger.info("Recruiter Already exists");
                recruiterSignUpResponse.setStatus(RecruiterSignUpResponse.STATUS_EXISTS);
            }
            recruiterProfile.update();
        }


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

                //setting recruiter status as "ACTIVE"
                RecruiterStatus recruiterStatus = RecruiterStatus.find.where().eq("RecruiterStatusId", 2).findUnique();
                if(recruiterStatus != null){
                    newRecruiter.setRecStatus(recruiterStatus);
                }

                //setting recruiter lead
                String leadName = recruiterSignUpRequest.getRecruiterName();
                RecruiterLead lead = RecruiterLeadService.createOrUpdateConvertedRecruiterLead(leadName, FormValidator.convertToIndianMobileFormat(recruiterSignUpRequest.getRecruiterMobile()));
                newRecruiter.setRecruiterLead(lead);

                //new recruiter hence giving 5 free contact unlock credits
                RecruiterCreditHistory recruiterCreditHistory = new RecruiterCreditHistory();

                RecruiterCreditCategory recruiterCreditCategory = RecruiterCreditCategory.find.where().eq("recruiter_credit_category_id", ServerConstants.RECRUITER_CATEGORY_CONTACT_UNLOCK).findUnique();
                if(recruiterCreditCategory != null){
                    recruiterCreditHistory.setRecruiterCreditCategory(recruiterCreditCategory);
                }
                newRecruiter.setRecruiterCandidateUnlockCredits(5);
                newRecruiter.setRecruiterInterviewUnlockCredits(0);
                recruiterCreditHistory.setRecruiterProfile(newRecruiter);
                recruiterCreditHistory.setRecruiterCreditsAvailable(5);
                recruiterCreditHistory.setRecruiterCreditsUsed(0);
                recruiterCreditHistory.save();

                newRecruiter.save();

                //setting all the credit values
                setCreditHistoryValues(newRecruiter, recruiterSignUpRequest);

                addRecruiterResponse.setStatus(AddRecruiterResponse.STATUS_SUCCESS);
                addRecruiterResponse.setRecruiterId(newRecruiter.getRecruiterProfileId());

                RecruiterSignUpResponse recruiterSignUpResponse = new RecruiterSignUpResponse();
//                triggerOtp(newRecruiter, recruiterSignUpResponse);
                Logger.info("Recruiter successfully saved");
            } else{
                existingRecruiter = getAndSetRecruiterValues(recruiterSignUpRequest, existingRecruiter, existingCompany);

                //setting recruiter status as "ACTIVE"
                RecruiterStatus recruiterStatus = RecruiterStatus.find.where().eq("RecruiterStatusId", 2).findUnique();
                if(recruiterStatus != null){
                    existingRecruiter.setRecStatus(recruiterStatus);
                }

                //setting recruiter lead
                String leadName = recruiterSignUpRequest.getRecruiterName();
                RecruiterLead lead = RecruiterLeadService.createOrUpdateConvertedRecruiterLead(leadName, FormValidator.convertToIndianMobileFormat(recruiterSignUpRequest.getRecruiterMobile()));
                existingRecruiter.setRecruiterLead(lead);

                existingRecruiter.update();

                //setting all the credit values
                setCreditHistoryValues(existingRecruiter, recruiterSignUpRequest);

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

    private static void setCreditHistoryValues(RecruiterProfile existingRecruiter, RecruiterSignUpRequest addRecruiterRequest) {
        //setting values for candidate contact unlock credits
        if(addRecruiterRequest.getRecruiterContactCreditAmount() != null && addRecruiterRequest.getRecruiterContactCreditAmount() != 0){
            //has candidate contact unlock credit, hence make an entry in recruiterPayment table
            RecruiterPayment recruiterPayment = new RecruiterPayment();
            RecruiterCreditHistory recruiterCreditHistory = new RecruiterCreditHistory();

            //setting creditCategory
            RecruiterCreditCategory recruiterCreditCategory = RecruiterCreditCategory.find.where().eq("recruiter_credit_category_id", 1).findUnique();
            if(recruiterCreditCategory != null){
                recruiterPayment.setRecruiterCreditCategory(recruiterCreditCategory);
                recruiterCreditHistory.setRecruiterCreditCategory(recruiterCreditCategory);
            }

            //setting recruiter profile
            recruiterPayment.setRecruiterProfile(existingRecruiter);
            recruiterCreditHistory.setRecruiterProfile(existingRecruiter);

            //setting credit amount in rupees
            recruiterPayment.setRecruiterPaymentAmount(addRecruiterRequest.getRecruiterContactCreditAmount());

            //setting credit unit price
            if(addRecruiterRequest.getRecruiterContactCreditUnitPrice() != null || addRecruiterRequest.getRecruiterContactCreditUnitPrice() != 0){
                recruiterPayment.setRecruiterCreditUnitPrice(addRecruiterRequest.getRecruiterContactCreditUnitPrice());
            }
            //setting credit mode (pre pay, post pay)
            if(addRecruiterRequest.getRecruiterCreditMode() != null){
                recruiterPayment.setRecruiterPaymentMode(addRecruiterRequest.getRecruiterCreditMode());
            }
            //computing total credits with respect to unit price
            Integer availableCredits = 0;
            Integer usedCredits = 0;
            Integer currentCredits;
            RecruiterCreditHistory recruiterCreditHistoryLatest = RecruiterCreditHistory.find.where()
                    .eq("RecruiterProfileId", existingRecruiter.getRecruiterProfileId())
                    .eq("RecruiterCreditCategory", ServerConstants.RECRUITER_CATEGORY_CONTACT_UNLOCK)
                    .setMaxRows(1)
                    .orderBy("recruiter_credit_history_create_timestamp desc")
                    .findUnique();

            if(recruiterCreditHistoryLatest != null){
                availableCredits = recruiterCreditHistoryLatest.getRecruiterCreditsAvailable();
                usedCredits = recruiterCreditHistoryLatest.getRecruiterCreditsUsed();
            }

            if(addRecruiterRequest.getRecruiterContactCreditAmount() != 0 && addRecruiterRequest.getRecruiterContactCreditUnitPrice() != 0){
                currentCredits = (addRecruiterRequest.getRecruiterContactCreditAmount() / addRecruiterRequest.getRecruiterContactCreditUnitPrice());
                availableCredits += currentCredits;
                existingRecruiter.setRecruiterCandidateUnlockCredits(availableCredits);
                recruiterCreditHistory.setRecruiterCreditsAvailable(availableCredits);
                recruiterCreditHistory.setRecruiterCreditsUsed(usedCredits);
                existingRecruiter.update();
            }

            //saving the values
            recruiterCreditHistory.save();
            recruiterPayment.save();
        }
        //setting values for interview unlock credits
        if(addRecruiterRequest.getRecruiterInterviewCreditAmount() != null && addRecruiterRequest.getRecruiterInterviewCreditAmount() != 0){
            //has interview unlock credit, hence make an entry in recruiterPayment table
            RecruiterPayment recruiterPayment = new RecruiterPayment();
            RecruiterCreditHistory recruiterCreditHistory = new RecruiterCreditHistory();

            //setting creditCategory
            RecruiterCreditCategory recruiterCreditCategory = RecruiterCreditCategory.find.where().eq("recruiter_credit_category_id", 2).findUnique();
            if(recruiterCreditCategory != null){
                recruiterPayment.setRecruiterCreditCategory(recruiterCreditCategory);
                recruiterCreditHistory.setRecruiterCreditCategory(recruiterCreditCategory);
            }

            //setting recruiter profile
            recruiterPayment.setRecruiterProfile(existingRecruiter);
            recruiterCreditHistory.setRecruiterProfile(existingRecruiter);

            //setting credit amount in rupees
            recruiterPayment.setRecruiterPaymentAmount(addRecruiterRequest.getRecruiterInterviewCreditAmount());

            //setting credit unit price
            if(addRecruiterRequest.getRecruiterInterviewCreditUnitPrice() != null || addRecruiterRequest.getRecruiterInterviewCreditUnitPrice() != 0){
                recruiterPayment.setRecruiterCreditUnitPrice(addRecruiterRequest.getRecruiterInterviewCreditUnitPrice());
            }

            //setting credit mode (pre pay, post pay)
            if(addRecruiterRequest.getRecruiterCreditMode() != null){
                recruiterPayment.setRecruiterPaymentMode(addRecruiterRequest.getRecruiterCreditMode());
            }

            //computing total credits with respect to unit price
            Integer availableCredits = 0;
            Integer usedCredits = 0;
            Integer currentCredits;
            RecruiterCreditHistory recruiterCreditHistoryLatest = RecruiterCreditHistory.find.where()
                    .eq("RecruiterProfileId", existingRecruiter.getRecruiterProfileId())
                    .eq("RecruiterCreditCategory", ServerConstants.RECRUITER_CATEGORY_INTERVIEW_UNLOCK)
                    .setMaxRows(1)
                    .orderBy("recruiter_credit_history_create_timestamp desc")
                    .findUnique();

            if(recruiterCreditHistoryLatest != null){
                availableCredits = recruiterCreditHistoryLatest.getRecruiterCreditsAvailable();
                usedCredits = recruiterCreditHistoryLatest.getRecruiterCreditsUsed();
            }

            if(addRecruiterRequest.getRecruiterInterviewCreditAmount() != 0 && addRecruiterRequest.getRecruiterInterviewCreditUnitPrice() != null){
                currentCredits = (addRecruiterRequest.getRecruiterInterviewCreditAmount() / addRecruiterRequest.getRecruiterInterviewCreditUnitPrice());
                availableCredits += currentCredits;
                recruiterCreditHistory.setRecruiterCreditsAvailable(availableCredits);
                recruiterCreditHistory.setRecruiterCreditsUsed(usedCredits);
                existingRecruiter.setRecruiterInterviewUnlockCredits(availableCredits);
                existingRecruiter.update();
            }

            //saving the values
            recruiterCreditHistory.save();
            recruiterPayment.save();
        }
    }

    private static RecruiterProfile getAndSetRecruiterValues(AddRecruiterRequest addRecruiterRequest, RecruiterProfile newRecruiter, Company existingCompany){
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
        if(addRecruiterRequest.getRecruiterAlternateMobile() != null){
            newRecruiter.setRecruiterAlternateMobile(addRecruiterRequest.getRecruiterAlternateMobile());
        }
        if(addRecruiterRequest.getRecruiterLinkedinProfile() != null){
            newRecruiter.setRecruiterLinkedinProfile(addRecruiterRequest.getRecruiterLinkedinProfile());
        }
        return newRecruiter;
    }

    private static void triggerOtp(RecruiterProfile recruiterProfile, RecruiterSignUpResponse recruiterSignUpResponse) {
        int randomPIN = generateOtp();
        SmsUtil.sendRecruiterOTPSms(randomPIN, recruiterProfile.getRecruiterProfileMobile());

        recruiterSignUpResponse.setStatus(RecruiterSignUpResponse.getStatusSuccess());
        recruiterSignUpResponse.setOtp(randomPIN);
    }

    public static Result unlockCandidate(RecruiterProfile recruiterProfile, Long candidateId) {
        recruiterProfile.setRecruiterCandidateUnlockCredits(recruiterProfile.getRecruiterCandidateUnlockCredits() - 1);
        recruiterProfile.update();

        //making an entry in recruiter credit history table
        RecruiterCreditHistory recruiterCreditHistory = new RecruiterCreditHistory();
        recruiterCreditHistory.setRecruiterProfile(recruiterProfile);

        RecruiterCreditCategory recruiterCreditCategory = RecruiterCreditCategory.find.where().eq("recruiter_credit_category_id", ServerConstants.RECRUITER_CATEGORY_CONTACT_UNLOCK).findUnique();
        if(recruiterCreditCategory != null){
            recruiterCreditHistory.setRecruiterCreditCategory(recruiterCreditCategory);
        }

        RecruiterCreditHistory recruiterCreditHistoryLatest = RecruiterCreditHistory.find.where()
                .eq("RecruiterProfileId", recruiterProfile.getRecruiterProfileId())
                .eq("RecruiterCreditCategory", ServerConstants.RECRUITER_CATEGORY_CONTACT_UNLOCK)
                .setMaxRows(1)
                .orderBy("recruiter_credit_history_create_timestamp desc")
                .findUnique();

        if(recruiterCreditHistoryLatest != null){
            recruiterCreditHistory.setRecruiterCreditsAvailable(recruiterCreditHistoryLatest.getRecruiterCreditsAvailable() - 1);
            recruiterCreditHistory.setRecruiterCreditsUsed(recruiterCreditHistoryLatest.getRecruiterCreditsUsed() + 1);
            recruiterCreditHistory.save();
        }
        return ok("1");
    }
}