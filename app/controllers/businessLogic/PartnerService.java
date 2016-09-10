package controllers.businessLogic;

import api.ServerConstants;
import api.http.FormValidator;
import api.http.httpRequest.PartnerSignUpRequest;
import api.http.httpResponse.CandidateSignUpResponse;
import api.http.httpResponse.PartnerSignUpResponse;
import models.entity.*;
import models.entity.Static.CandidateProfileStatus;
import models.entity.Static.PartnerProfileStatus;
import models.util.SmsUtil;
import play.Logger;

import javax.persistence.NonUniqueResultException;
import java.util.List;

import static controllers.businessLogic.CandidateService.isCandidateExists;
import static controllers.businessLogic.InteractionService.createInteractionForSignUpCandidate;
import static models.util.Util.generateOtp;

/**
 * Created by adarsh on 9/9/16.
 */
public class PartnerService {
    private static PartnerSignUpResponse createNewPartner(Partner partner, Lead lead) {

        PartnerSignUpResponse partnerSignUpResponse = new PartnerSignUpResponse();
        PartnerProfileStatus partnerProfileStatus = PartnerProfileStatus.find.where().eq("profile_status_is", ServerConstants.PARTNER_STATE_ACTIVE).findUnique();
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
            // get the list of candidate and sort by candidateId
            // return the lowest primary key candidate Object
            // register the event with proper info

            List<Partner> existingPartnerList = Partner.find.where().eq("partner_mobile", mobile).findList();
            if(!existingPartnerList.isEmpty()){
                existingPartnerList.sort((l1, l2) -> l1.getPartnerId() <= l2.getPartnerId() ? 1 : 0);
                Logger.info("Duplicate partner Encountered with mobile no: "+ mobile + "- Returned PartnerId = "
                        + existingPartnerList.get(0).getPartnerId() + " UUID-:"+existingPartnerList.get(0).getPartnerUUId());
                SmsUtil.sendDuplicateCandidateSmsToDevTeam(mobile);
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
        Lead lead = LeadService.createOrUpdateConvertedLead(leadName, partnerSignUpRequest.getPartnerMobile(), leadSourceId, channelType);
        try {
            if(partner == null) {
                partner = new Partner();
                Logger.info("creating new partner");
                if(partnerSignUpRequest.getPartnerName()!= null){
                    partner.setPartnerFirstName(partnerSignUpRequest.getPartnerName());
                }
                if(partnerSignUpRequest.getPartnerMobile()!= null){
                    partner.setPartnerMobile(partnerSignUpRequest.getPartnerMobile());
                }

                partnerSignUpResponse = createNewPartner(partner, lead);
                if(!(channelType == InteractionService.InteractionChannelType.SUPPORT)){
                    // triggers when partner is self created
                    triggerOtp(partner, partnerSignUpResponse);
                    result = ServerConstants.INTERACTION_RESULT_NEW_PARTNER;
                    objectAUUId = partner.getPartnerUUId();
                }
            } else {
                PartnerAuth auth = PartnerAuthService.isAuthExists(partner.getPartnerId());
                if(auth == null ) {
                    Logger.info("auth doesn't exists for this partner");
                    partner.setPartnerFirstName(partnerSignUpRequest.getPartnerName());
                    if(!(channelType == InteractionService.InteractionChannelType.SUPPORT)){
                        triggerOtp(partner, partnerSignUpResponse);
                        result = ServerConstants.INTERACTION_RESULT_EXISTING_PARTNER_VERIFICATION;
                        objectAUUId = partner.getPartnerUUId();
                        partnerSignUpResponse.setStatus(PartnerSignUpResponse.STATUS_SUCCESS);

                    }
                } else{
                    result = ServerConstants.INTERACTION_RESULT_EXISTING_PARTNER_SIGNUP;
                    partnerSignUpResponse.setStatus(CandidateSignUpResponse.STATUS_EXISTS);
                }
                partner.partnerUpdate();
            }

            // Insert Interaction only for self sign up as interaction for sign up support will be handled in createCandidateProfile
            //TODO: partner interaction

        } catch (NullPointerException n){
            n.printStackTrace();
            partnerSignUpResponse.setStatus(PartnerSignUpResponse.STATUS_FAILURE);
        }
        return partnerSignUpResponse;
    }

    private static void triggerOtp(Partner partner, PartnerSignUpResponse partnerSignUpResponse) {
        int randomPIN = generateOtp();
        SmsUtil.sendOTPSms(randomPIN, partner.getPartnerMobile());

        partnerSignUpResponse.setPartnerMobile(partner.getPartnerMobile());
        partnerSignUpResponse.setOtp(randomPIN);
        partnerSignUpResponse.setStatus(PartnerSignUpResponse.STATUS_SUCCESS);
    }

}
