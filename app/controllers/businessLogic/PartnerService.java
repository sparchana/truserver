package controllers.businessLogic;

import api.InteractionConstants;
import api.ServerConstants;
import api.http.FormValidator;
import api.http.httpRequest.*;
import api.http.httpResponse.*;
import models.entity.*;
import models.entity.OM.PartnerToCandidate;
import models.entity.Static.Locality;
import models.entity.Static.PartnerProfileStatus;
import models.entity.Static.PartnerType;
import models.util.SmsUtil;
import models.util.Util;
import play.Logger;

import javax.persistence.NonUniqueResultException;
import java.util.List;
import java.util.UUID;

import static controllers.businessLogic.PartnerInteractionService.createInteractionForPartnerLogin;
import static controllers.businessLogic.PartnerInteractionService.createInteractionForPartnerResetPassword;
import static controllers.businessLogic.PartnerInteractionService.createInteractionForPartnerSignUp;
import static models.util.Util.generateOtp;
import static play.mvc.Controller.session;
import static play.mvc.Results.ok;

/**
 * Created by adarsh on 9/9/16.
 */
public class PartnerService {
    private static PartnerSignUpResponse createNewPartner(Partner partner, Lead lead) {
        PartnerSignUpResponse partnerSignUpResponse = new PartnerSignUpResponse();
        PartnerProfileStatus partnerProfileStatus = PartnerProfileStatus.find.where().eq("profile_status_id", ServerConstants.PARTNER_STATE_ACTIVE).findUnique();
        if(partnerProfileStatus != null){
            partner.setPartnerprofilestatus(partnerProfileStatus);
            partner.setLead(lead);
            partner.registerPartner();
            partnerSignUpResponse.setStatus(PartnerSignUpResponse.STATUS_SUCCESS);
            Logger.info("Partner successfully registered " + partner);
        } else {
            partnerSignUpResponse.setStatus(PartnerSignUpResponse.STATUS_FAILURE);
        }
        return partnerSignUpResponse;
    }

    public static Partner isPartnerExists(String mobile) {
        try{
            Partner existingPartner = Partner.find.where().eq("partner_mobile",
                    FormValidator.convertToIndianMobileFormat(mobile)).findUnique();
            if(existingPartner != null) {
                return existingPartner;
            }
        } catch (NonUniqueResultException nu){
            // get the list of partners and sort by partnerId
            // return the lowest primary key partner Object
            // register the event with proper info

            List<Partner> existingPartnerList = Partner.find.where().eq("partner_mobile", mobile).findList();
            if(!existingPartnerList.isEmpty()){
                existingPartnerList.sort((l1, l2) -> l1.getPartnerId() <= l2.getPartnerId() ? 1 : 0);
                Logger.info("Duplicate partner Encountered with mobile no: "+ mobile + "- Returned PartnerId = "
                        + existingPartnerList.get(0).getPartnerId() + " UUID-:"+existingPartnerList.get(0).getPartnerUUId());
                SmsUtil.sendDuplicatePartnerSmsToDevTeam(mobile);
                return existingPartnerList.get(0);
            }
        }
        return null;
    }

    public static PartnerSignUpResponse signUpPartner(PartnerSignUpRequest partnerSignUpRequest,
                                                        InteractionService.InteractionChannelType channelType,
                                                        int leadSourceId) {

        PartnerSignUpResponse partnerSignUpResponse = new PartnerSignUpResponse();
        String result = "";
        String objectAUUId = "";
        Logger.info("Checking for mobile number: " + partnerSignUpRequest.getPartnerMobile());
        Partner partner = isPartnerExists(partnerSignUpRequest.getPartnerMobile());
        String leadName = partnerSignUpRequest.getPartnerName();
        Lead lead = LeadService.createOrUpdateConvertedLead(leadName, partnerSignUpRequest.getPartnerMobile(), leadSourceId, channelType, LeadService.LeadType.PARTNER);
        try {
            if(partner == null) {
                partner = new Partner();
                Logger.info("creating new partner");
                if(partnerSignUpRequest.getPartnerName()!= null){
                    partner.setPartnerFirstName(partnerSignUpRequest.getPartnerName());
                }
                if(partnerSignUpRequest.getPartnerLastName()!= null){
                    partner.setPartnerLastName(partnerSignUpRequest.getPartnerLastName());
                }
                if(partnerSignUpRequest.getPartnerMobile()!= null){
                    partner.setPartnerMobile(partnerSignUpRequest.getPartnerMobile());
                }
                resetPartnerTypeAndLocality(partner, partnerSignUpRequest);
                partnerSignUpResponse = createNewPartner(partner, lead);
                if(!(channelType == InteractionService.InteractionChannelType.SUPPORT)){
                    // triggers when partner is self created
                    triggerOtp(partner, partnerSignUpResponse);
                    result = InteractionConstants.INTERACTION_RESULT_NEW_PARTNER;
                    objectAUUId = partner.getPartnerUUId();
                }
            } else {
                PartnerAuth auth = PartnerAuthService.isAuthExists(partner.getPartnerId());
                if(auth == null ) {
                    Logger.info("auth doesn't exists for this partner");
                    partner.setPartnerFirstName(partnerSignUpRequest.getPartnerName());
                    resetPartnerTypeAndLocality(partner, partnerSignUpRequest);
                    if(!(channelType == InteractionService.InteractionChannelType.SUPPORT)){
                        triggerOtp(partner, partnerSignUpResponse);
                        result = InteractionConstants.INTERACTION_RESULT_EXISTING_PARTNER_VERIFICATION;
                        objectAUUId = partner.getPartnerUUId();
                        partnerSignUpResponse.setStatus(PartnerSignUpResponse.STATUS_SUCCESS);

                    }
                } else{
                    result = InteractionConstants.INTERACTION_RESULT_EXISTING_PARTNER_SIGNUP;
                    partnerSignUpResponse.setStatus(PartnerSignUpResponse.STATUS_EXISTS);
                }
                partner.partnerUpdate();
            }
            //creating interaction
            createInteractionForPartnerSignUp(objectAUUId, result);

        } catch (NullPointerException n){
            n.printStackTrace();
            partnerSignUpResponse.setStatus(PartnerSignUpResponse.STATUS_FAILURE);
        }
        return partnerSignUpResponse;
    }

    public static void resetPartnerTypeAndLocality(Partner existingPartner, PartnerSignUpRequest partnerSignUpRequest) {
        PartnerType existingPartnerType = PartnerType.find.where().eq("partner_type_id", partnerSignUpRequest.getPartnerType()).findUnique();
        if(existingPartnerType != null){
            existingPartner.setPartnerType(existingPartnerType);
        } else{
            Logger.info("Partner type : " + partnerSignUpRequest.getPartnerType() + " does not exists");
        }

        Locality existingLocality = Locality.find.where().eq("localityId", partnerSignUpRequest.getPartnerLocality()).findUnique();
        if(existingLocality != null){
            existingPartner.setLocality(existingLocality);
        } else{
            Logger.info("LocalityId : " + partnerSignUpRequest.getPartnerLocality() + " does not exists");
        }
    }


    private static void triggerOtp(Partner partner, PartnerSignUpResponse partnerSignUpResponse) {
        int randomPIN = generateOtp();
        SmsUtil.sendPartnerOTPSms(randomPIN, partner.getPartnerMobile());

        partnerSignUpResponse.setPartnerMobile(partner.getPartnerMobile());
        partnerSignUpResponse.setOtp(randomPIN);
        partnerSignUpResponse.setStatus(PartnerSignUpResponse.STATUS_SUCCESS);
    }

    public static LoginResponse login(String loginMobile, String loginPassword, InteractionService.InteractionChannelType channelType){
        LoginResponse loginResponse = new LoginResponse();
        Logger.info(" login mobile: " + loginMobile);
        Partner existingPartner = isPartnerExists(FormValidator.convertToIndianMobileFormat(loginMobile));
        if(existingPartner == null){
            loginResponse.setStatus(LoginResponse.STATUS_NO_USER);
            Logger.info("Partner Does not Exists");
        } else {
            long partnerId = existingPartner.getPartnerId();
            PartnerAuth existingAuth = PartnerAuth.find.where().eq("partner_id", partnerId).findUnique();
            if(existingAuth != null){
                if ((existingAuth.getPasswordMd5().equals(Util.md5(loginPassword + existingAuth.getPasswordSalt())))) {
                    Logger.info(existingPartner.getPartnerFirstName() + " " + existingPartner.getPartnerprofilestatus().getProfileStatusId());
                    loginResponse.setCandidateId(existingPartner.getPartnerId());
                    loginResponse.setCandidateFirstName(existingPartner.getPartnerFirstName());

                    loginResponse.setStatus(LoginResponse.STATUS_SUCCESS);

                    existingAuth.setAuthSessionId(UUID.randomUUID().toString());
                    existingAuth.setAuthSessionIdExpiryMillis(System.currentTimeMillis() + 24 * 60 * 60 * 1000);

                    loginResponse.setAuthSessionId(existingAuth.getAuthSessionId());
                    loginResponse.setSessionExpiryInMilliSecond(existingAuth.getAuthSessionIdExpiryMillis());

                    /* adding session details */
                    PartnerAuthService.addSession(existingAuth,existingPartner);
                    String sessionId = session().get("sessionId");
                    existingAuth.update();
                    createInteractionForPartnerLogin(existingPartner.getPartnerUUId(), channelType);
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

    public static ResetPasswordResponse findPartnerAndSendOtp(String partnerMobile, InteractionService.InteractionChannelType channelType){
        ResetPasswordResponse resetPasswordResponse = new ResetPasswordResponse();
        Partner existingPartner = isPartnerExists(partnerMobile);
        if(existingPartner != null){
            Logger.info("Partner Exists");
            PartnerAuth existingAuth = PartnerAuth.find.where().eq("partner_id", existingPartner.getPartnerId()).findUnique();
            if(existingAuth == null){
                resetPasswordResponse.setStatus(LoginResponse.STATUS_NO_USER);
                Logger.info("reset password not allowed as Auth don't exists");
            } else {
                int randomPIN = generateOtp();
                existingPartner.update();
                SmsUtil.sendResetPasswordOTPSms(randomPIN, existingPartner.getPartnerMobile());

                String interactionResult = InteractionConstants.INTERACTION_RESULT_PARTNER_TRIED_TO_RESET_PASSWORD;
                String objAUUID = "";
                objAUUID = existingPartner.getPartnerUUId();
                createInteractionForPartnerResetPassword(objAUUID, interactionResult, channelType);
                resetPasswordResponse.setOtp(randomPIN);
                resetPasswordResponse.setStatus(LoginResponse.STATUS_SUCCESS);
            }
        } else{
            resetPasswordResponse.setStatus(LoginResponse.STATUS_NO_USER);
            Logger.info("reset password not allowed as password don't exists");
        }
        return resetPasswordResponse;
    }

    public static PartnerSignUpResponse createPartnerProfile(PartnerProfileRequest partnerProfileRequest, InteractionService.InteractionChannelType channelType,
                                                             int profileUpdateFlag) {
        PartnerSignUpResponse partnerSignUpResponse = new PartnerSignUpResponse();
        // get partnerBasic obj from req
        Logger.info("partner profile for mobile " + partnerProfileRequest.getPartnerMobile());

        // Check if this partner exists
        Partner partner = isPartnerExists(FormValidator.convertToIndianMobileFormat(partnerProfileRequest.getPartnerMobile()));

        if(partner != null){

            // Initialize some basic interaction details
            String createdBy = InteractionConstants.INTERACTION_CREATED_SELF;
            String interactionResult = InteractionConstants.INTERACTION_RESULT_PARTNER_INFO_UPDATED_SELF;

            String interactionNote;

            // Now we check if we are dealing with the request to update basic profile details from website (or)
            if(profileUpdateFlag == ServerConstants.UPDATE_BASIC_PROFILE ||
                    profileUpdateFlag == ServerConstants.UPDATE_ALL_BY_SUPPORT) {

                partnerSignUpResponse = updateBasicProfile(partner, partnerProfileRequest);

                // In case of errors, return at this point
                if(partnerSignUpResponse.getStatus() != PartnerSignUpResponse.STATUS_SUCCESS){
                    Logger.info("Error while updating basic profile of partner with mobile " + partner.getPartnerMobile());
                    return partnerSignUpResponse;
                }

                // Set the appropriate interaction result
                if(profileUpdateFlag == ServerConstants.UPDATE_BASIC_PROFILE) {
                    interactionResult = InteractionConstants.INTERACTION_RESULT_PARTNER_BASIC_PROFILE_INFO_UPDATED_SELF;
                }
            }

            // set the default interaction note string
            interactionNote = InteractionConstants.INTERACTION_NOTE_BLANK;

            PartnerInteractionService.createInteractionForPartnerProfileUpdate(partner.getPartnerUUId(), interactionNote, interactionResult, createdBy);

            partner.update();

            Logger.info("partner with mobile " + partner.getPartnerMobile() + " updated successfully");
            partnerSignUpResponse.setStatus(PartnerSignUpResponse.STATUS_SUCCESS);
        } else{
            //partner profile does not exists
            Logger.info("no Partner exists");
        }

        return partnerSignUpResponse;
    }

    private static PartnerSignUpResponse updateBasicProfile(Partner partner, PartnerProfileRequest request) {

        PartnerSignUpResponse partnerSignUpResponse = new PartnerSignUpResponse();

        // not just update but createOrUpdateConvertedLead
        Logger.info("Inside updateBasicProfile");

        // initialize to default value. We will change this value later if any exception occurs
        partnerSignUpResponse.setStatus(PartnerSignUpResponse.STATUS_SUCCESS);

        /// Basic Profile Section Starts
        if(request.getPartnerName() != null){
            partner.setPartnerFirstName(request.getPartnerName());
        }
        if(request.getPartnerLastName() != null){
            partner.setPartnerLastName(request.getPartnerLastName());
        }

        try {
            if(request.getPartnerType() != null){
                PartnerType partnerType = PartnerType.find.where().eq("partner_type_id", request.getPartnerType()).findUnique();
                if(partnerType != null){
                    partner.setPartnerType(partnerType);
                }
            }
        } catch(Exception e) {
            partnerSignUpResponse.setStatus(PartnerSignUpResponse.STATUS_FAILURE);
            Logger.info("Exception while setting partner Type");
            e.printStackTrace();
        }

        try {
            if(request.getPartnerOrganizationName() != null){
                partner.setPartnerCompany(request.getPartnerOrganizationName());
            }
        } catch(Exception e) {
            partnerSignUpResponse.setStatus(PartnerSignUpResponse.STATUS_FAILURE);
            Logger.info("Exception while setting partner company");
            e.printStackTrace();
        }

        try {
            if(request.getPartnerEmail() != null){
                partner.setPartnerEmail(request.getPartnerEmail());
            }
        } catch(Exception e) {
            partnerSignUpResponse.setStatus(PartnerSignUpResponse.STATUS_FAILURE);
            Logger.info("Exception while setting partner email");
            e.printStackTrace();
        }

        try {
            if(request.getPartnerLocality() != null){
                Locality locality  = Locality.find.where().eq("localityId", request.getPartnerLocality()).findUnique();
                if(locality != null){
                    partner.setLocality(locality);
                }
            }
        } catch(Exception e) {
            partnerSignUpResponse.setStatus(PartnerSignUpResponse.STATUS_FAILURE);
            Logger.info("Exception while setting partner organization locality");
            e.printStackTrace();
        }

        Logger.info("Added Basic Profile details");

        return partnerSignUpResponse;
    }

    public static CandidateSignUpResponse createPartnerToCandidateMapping(Partner partner, String candidateMobile) {
        Logger.info("Checking candidate with mobile: " + candidateMobile);
        CandidateSignUpResponse candidateSignUpResponse = new CandidateSignUpResponse();
        Candidate existingCandidate = CandidateService.isCandidateExists(FormValidator.convertToIndianMobileFormat(candidateMobile));
        if(existingCandidate != null){
            PartnerToCandidate partnerToCandidate = new PartnerToCandidate();
            partnerToCandidate.setCandidate(existingCandidate);
            partnerToCandidate.setPartner(partner);
            partnerToCandidate.savePartnerToCandidate(partnerToCandidate);

            candidateSignUpResponse.setStatus(CandidateSignUpResponse.STATUS_SUCCESS);
        } else {
            candidateSignUpResponse.setStatus(CandidateSignUpResponse.STATUS_FAILURE);
        }
        return candidateSignUpResponse;
    }

    public static void sendCandidateVerificationSms(Candidate existingCandidate) {
        Partner partner = Partner.find.where().eq("partner_id", session().get("partnerId")).findUnique();
        if(partner != null){
            Auth existingAuth = Auth.find.where().eq("candidateId", existingCandidate.getCandidateId()).findUnique();
            if(existingAuth != null){
                Integer dummyOtp = Util.generateOtp();
                AuthService.setNewPassword(existingAuth, String.valueOf(dummyOtp));
                existingAuth.setOtp(dummyOtp);
                existingAuth.update();
                SmsUtil.sendOtpToPartnerCreatedCandidate(dummyOtp, existingCandidate.getCandidateMobile());

                String objAUUID = existingCandidate.getCandidateUUId();
                String objBUUID = partner.getPartnerUUId();

                //creating interaction
                PartnerInteractionService.createInteractionForPartnerTryingToVerifyCandidate(objAUUID, objBUUID, partner.getPartnerFirstName());
            } else{
                Logger.info("Auth doesnot exists");
            }
        }
    }

    public static VerifyCandidateResponse verifyCandidateByPartner(VerifyCandidateRequest verifyCandidateRequest) {
        VerifyCandidateResponse verifyCandidateResponse = new VerifyCandidateResponse();
        Partner partner = Partner.find.where().eq("partner_id", session().get("partnerId")).findUnique();
        if(partner != null){
            Candidate existingCandidate = CandidateService.isCandidateExists(FormValidator.convertToIndianMobileFormat(verifyCandidateRequest.getCandidateMobile()));
            if(existingCandidate != null){
                Auth existingAuth = Auth.find.where().eq("candidateId", existingCandidate.getCandidateId()).findUnique();
                if(existingAuth != null){
                    // auth for the user is present
                    if(verifyCandidateRequest.getUserOtp() == existingAuth.getOtp()){
                        verifyCandidateResponse.setStatus(VerifyCandidateResponse.STATUS_SUCCESS);
                        existingAuth.setAuthStatus(ServerConstants.CANDIDATE_STATUS_VERIFIED);
                        existingAuth.update();
                        CandidateService.sendDummyAuthForCandidateByPartner(existingCandidate);
                        String objAUUID = existingCandidate.getCandidateUUId();
                        String objBUUID = partner.getPartnerUUId();

                        //creating interaction
                        PartnerInteractionService.createInteractionForPartnerVerifyingCandidate(objAUUID, objBUUID, partner.getPartnerFirstName());
                    } else{
                        verifyCandidateResponse.setStatus(VerifyCandidateResponse.STATUS_WRONG_OTP);
                    }
                } else{
                    // no auth found for the user
                    verifyCandidateResponse.setStatus(VerifyCandidateResponse.STATUS_FAILURE);
                }
            } else{
                // no candidate found
                verifyCandidateResponse.setStatus(VerifyCandidateResponse.STATUS_FAILURE);
            }
        } else{
            // no partner session found
            verifyCandidateResponse.setStatus(VerifyCandidateResponse.STATUS_FAILURE);
        }
        return verifyCandidateResponse;
    }
}
