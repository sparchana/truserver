package controllers.businessLogic;

import api.InteractionConstants;
import api.ServerConstants;
import models.entity.Interaction;

import static api.InteractionConstants.*;

/**
 * Created by adarsh on 12/9/16.
 */
public class PartnerInteractionService {
    public static void createInteractionForPartnerSignUp(String objectAUUId, String result) {
        Interaction interaction = new Interaction(
                objectAUUId,
                ServerConstants.OBJECT_TYPE_PARTNER,
                InteractionConstants.INTERACTION_TYPE_SIGN_UP,
                InteractionConstants.INTERACTION_NOTE_BLANK,
                result,
                InteractionConstants.INTERACTION_CREATED_SELF,
                InteractionConstants.INTERACTION_CHANNEL_PARTNER_WEBSITE
        );
        InteractionService.createInteraction(interaction);
    }

    public static void createInteractionForPartnerLogin(String objectAUUId, InteractionService.InteractionChannelType channelType) {
        Interaction interaction = new Interaction(
                objectAUUId,
                ServerConstants.OBJECT_TYPE_PARTNER,
                InteractionConstants.INTERACTION_TYPE_LOG_IN,
                InteractionConstants.INTERACTION_NOTE_BLANK,
                InteractionConstants.INTERACTION_RESULT_PARTNER_SIGNEDIN,
                channelType.toString(),
                InteractionConstants.INTERACTION_CHANNEL_PARTNER_WEBSITE
        );
        InteractionService.createInteraction(interaction);
    }


    public static void createInteractionForPartnerResetPassword(String objectAUUId, String result, InteractionService.InteractionChannelType channelType){
        Interaction interaction = new Interaction(
                objectAUUId,
                ServerConstants.OBJECT_TYPE_PARTNER,
                InteractionConstants.INTERACTION_TYPE_PASSWORD_RESET_SUCCESS,
                InteractionConstants.INTERACTION_NOTE_BLANK,
                result,
                channelType.toString(),
                InteractionConstants.INTERACTION_CHANNEL_PARTNER_WEBSITE
        );
        InteractionService.createInteraction(interaction);
    }

    public static void createInteractionForPartnerProfileUpdate(String uuId, String interactionNote, String interactionResult, String createdBy){
        Interaction interaction = new Interaction(
                uuId,
                ServerConstants.OBJECT_TYPE_PARTNER,
                InteractionConstants.INTERACTION_TYPE_PROFILE_UPDATE,
                interactionNote,
                interactionResult,
                createdBy + "(Partner)",
                InteractionConstants.INTERACTION_CHANNEL_PARTNER_WEBSITE
        );
        InteractionService.createInteraction(interaction);
    }

    public static void createInteractionForPartnerVerifyingCandidate(String objAUUID, String objBUUID, String partnerName) {
        Interaction interaction = new Interaction(
                objAUUID,
                ServerConstants.OBJECT_TYPE_PARTNER,
                objBUUID,
                ServerConstants.OBJECT_TYPE_CANDIDATE,
                INTERACTION_TYPE_CANDIDATE_VERIFIED,
                INTERACTION_NOTE_BLANK,
                INTERACTION_RESULT_CANDIDATE_VERIFICATION_SUCCESS,
                partnerName + "(Partner)",
                INTERACTION_CHANNEL_PARTNER_WEBSITE
        );
        InteractionService.createInteraction(interaction);
    }

    public static void createInteractionForPartnerTryingToVerifyCandidate(String objAUUID, String objBUUID, String partnerName) {
        Interaction interaction = new Interaction(
                objAUUID,
                ServerConstants.OBJECT_TYPE_PARTNER,
                objBUUID,
                ServerConstants.OBJECT_TYPE_CANDIDATE,
                INTERACTION_TYPE_CANDIDATE_TRIED_TO_VERIFY,
                INTERACTION_NOTE_BLANK,
                INTERACTION_RESULT_CANDIDATE_TRIED_TO_VERIFY,
                partnerName + "(Partner)",
                INTERACTION_CHANNEL_PARTNER_WEBSITE
        );
        InteractionService.createInteraction(interaction);
    }
}