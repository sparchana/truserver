package controllers.businessLogic;

import api.ServerConstants;
import models.entity.Interaction;

/**
 * Created by adarsh on 12/9/16.
 */
public class PartnerInterationService {
    public static void createInteractionForPartnerSignUp(String objectAUUId, String result, InteractionService.InteractionChannelType channelType) {
        if(channelType == InteractionService.InteractionChannelType.SELF || channelType == InteractionService.InteractionChannelType.SELF_ANDROID ){
            Interaction interaction = new Interaction(
                    objectAUUId,
                    ServerConstants.OBJECT_TYPE_PARTNER,
                    channelType == InteractionService.InteractionChannelType.SELF ? ServerConstants.INTERACTION_TYPE_WEBSITE : ServerConstants.INTERACTION_TYPE_ANDROID_SIGNUP,
                    ServerConstants.INTERACTION_NOTE_BLANK,
                    result,
                    channelType.toString()
            );
            InteractionService.createInteraction(interaction);
        }
    }

    public static void createInteractionForPartnerLogin(String objectAUUId, InteractionService.InteractionChannelType channelType) {
        if(channelType == InteractionService.InteractionChannelType.SELF || channelType == InteractionService.InteractionChannelType.SELF_ANDROID){
            Interaction interaction = new Interaction(
                    objectAUUId,
                    ServerConstants.OBJECT_TYPE_PARTNER,
                    channelType == InteractionService.InteractionChannelType.SELF ? ServerConstants.INTERACTION_TYPE_WEBSITE : ServerConstants.INTERACTION_TYPE_ANDROID_LOGIN,
                    ServerConstants.INTERACTION_NOTE_BLANK,
                    ServerConstants.INTERACTION_RESULT_SELF_SIGNEDIN,
                    channelType.toString()
            );
            InteractionService.createInteraction(interaction);
        }
    }


    public static void createInteractionForPartnerResetPassword(String objectAUUId, String result, InteractionService.InteractionChannelType channelType){
        Interaction interaction = new Interaction(
                objectAUUId,
                ServerConstants.OBJECT_TYPE_PARTNER,
                ServerConstants.INTERACTION_TYPE_PASSWORD_RESET_SUCCESS,
                ServerConstants.INTERACTION_NOTE_BLANK,
                result,
                channelType.toString()
        );
        InteractionService.createInteraction(interaction);
    }

    public static void createInteractionForPartnerProfileUpdate(String uuId, Integer interactionType, String interactionNote, String interactionResult, String createdBy){
        Interaction interaction = new Interaction(
                uuId,
                ServerConstants.OBJECT_TYPE_PARTNER,
                interactionType,
                interactionNote,
                interactionResult,
                createdBy
        );

        InteractionService.createInteraction(interaction);
    }


}
