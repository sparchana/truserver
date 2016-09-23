package controllers.businessLogic;

import api.InteractionConstants;
import api.ServerConstants;
import api.http.httpResponse.CandidateSignUpResponse;
import api.http.httpResponse.PartnerSignUpResponse;
import models.entity.*;
import models.entity.Static.CandidateProfileStatus;
import models.entity.Static.PartnerProfileStatus;
import models.util.SmsUtil;
import models.util.Util;
import play.Logger;

import java.util.UUID;

import static controllers.businessLogic.PartnerInteractionService.createInteractionForPartnerAddPasswordViaWebsite;
import static controllers.businessLogic.PartnerInteractionService.createInteractionForPartnerResetPasswordViaWebsite;
import static play.mvc.Controller.session;

/**
 * Created by adarsh on 10/9/16.
 */
public class PartnerAuthService {
    public static PartnerAuth isAuthExists(Long partnerId){
        return PartnerAuth.find.where().eq("partner_id", partnerId).findUnique();
    }

    public static void setNewPassword(PartnerAuth partnerAuth, String password){
        partnerAuth.setPasswordMd5(Util.md5(password + partnerAuth.getPasswordSalt()));
        partnerAuth.setAuthSessionIdExpiryMillis(System.currentTimeMillis() + 24 * 60 * 60 * 1000);
        session("sessionId", partnerAuth.getAuthSessionId());
        session("sessionExpiry", String.valueOf(partnerAuth.getAuthSessionIdExpiryMillis()));
    }

    public static PartnerSignUpResponse savePassword(String mobile, String password, InteractionService.InteractionChannelType channelType){
        PartnerSignUpResponse partnerSignUpResponse = new PartnerSignUpResponse();

        Logger.info("to check: " + mobile);
        Partner existingPartner = Partner.find.where().eq("partner_mobile", mobile).findUnique();

        if(existingPartner != null) {
            // If partner exists
            PartnerAuth existingAuth = PartnerAuth.find.where().eq("partner_id", existingPartner.getPartnerId()).findUnique();
            if(existingAuth != null){
                // If partner exists and has a password, reset the old password
                Logger.info("Resetting password");
                setNewPassword(existingAuth, password);
                PartnerAuth.savePassword(existingAuth);
                String interactionResult = InteractionConstants.INTERACTION_RESULT_PARTNER_RESET_PASSWORD_SUCCESS;
                String objAUUID = "";
                Partner partner = Partner.find.where().eq("partner_id", existingPartner.getPartnerId()).findUnique();
                if(partner != null){
                    objAUUID = partner.getPartnerUUId();
                }
                createInteractionForPartnerResetPasswordViaWebsite(objAUUID, interactionResult);
                existingAuth.setAuthSessionId(UUID.randomUUID().toString());
                existingAuth.setAuthSessionIdExpiryMillis(System.currentTimeMillis() + 24 * 60 * 60 * 1000);
                /* adding session details */
                addSession(existingAuth, existingPartner);
                partnerSignUpResponse.setStatus(PartnerSignUpResponse.STATUS_SUCCESS);
                partnerSignUpResponse.setPartnerMobile(existingPartner.getPartnerMobile());
            } else {
                Logger.info("8");
                PartnerAuth auth = new PartnerAuth();
                auth.setPartnerId(existingPartner.getPartnerId());
                setNewPassword(auth, password);
                auth.setPartnerAuthStatus(ServerConstants.PARTNER_STATUS_VERIFIED);
                PartnerAuth.savePassword(auth);
                auth.setAuthSessionId(UUID.randomUUID().toString());
                auth.setAuthSessionIdExpiryMillis(System.currentTimeMillis() + 24 * 60 * 60 * 1000);
                /* adding session details */
                addSession(auth, existingPartner);

                partnerSignUpResponse.setStatus(CandidateSignUpResponse.STATUS_SUCCESS);

                String objAUUID = existingPartner.getPartnerUUId();
                createInteractionForPartnerAddPasswordViaWebsite(objAUUID);

                try {
                    existingPartner.setPartnerprofilestatus(PartnerProfileStatus.find.where().eq("profile_status_id", ServerConstants.PARTNER_STATE_ACTIVE).findUnique());
                    partnerSignUpResponse.setStatus(PartnerSignUpResponse.STATUS_SUCCESS);
                } catch (NullPointerException n) {
                    Logger.info("Oops ProfileStatusId"+ " doesnot exists");
                    partnerSignUpResponse.setStatus(PartnerSignUpResponse.STATUS_FAILURE);
                }
                Logger.info("9");

                existingPartner.update();
                Logger.info("partner status confirmed");

                Lead existingLead = Lead.find.where().eq("leadId", existingPartner.getLead().getLeadId()).findUnique();
                existingLead.setLeadStatus(ServerConstants.LEAD_STATUS_WON);
                existingLead.update();
                Logger.info("Lead converted in Partner");

                SmsUtil.sendWelcomeSmsToPartnerFromWebsite(existingPartner.getPartnerFirstName(), existingPartner.getPartnerMobile());

                partnerSignUpResponse.setPartnerMobile(existingPartner.getPartnerMobile());
            }
            Logger.info("Auth Save Successful");
        }
        else {
            Logger.info("10");
            Logger.info("Partner Does not Exist!");
            partnerSignUpResponse.setStatus(PartnerSignUpResponse.STATUS_FAILURE);
        }
        return partnerSignUpResponse;
    }

    public static void addSession(PartnerAuth existingAuth, Partner partner){
        session().put("sessionId", existingAuth.getAuthSessionId());
        session().put("partnerId", String.valueOf(partner.getPartnerId()));
        session().put("partnerName", String.valueOf(partner.getPartnerFirstName()));
        session().put("partnerMobile", String.valueOf(partner.getPartnerMobile()));
        session().put("leadId", String.valueOf(partner.getLead().getLeadId()));
        session().put("sessionExpiry", String.valueOf(existingAuth.getAuthSessionIdExpiryMillis()));
        Logger.info("set-sessionId"+ session().get("sessionId"));
    }
}