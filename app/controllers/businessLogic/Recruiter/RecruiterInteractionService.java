package controllers.businessLogic.Recruiter;

import api.InteractionConstants;
import api.ServerConstants;
import controllers.businessLogic.InteractionService;
import models.entity.Interaction;

import static api.InteractionConstants.INTERACTION_CHANNEL_PARTNER_WEBSITE;
import static api.InteractionConstants.INTERACTION_CHANNEL_RECRUITER_WEBSITE;
import static api.InteractionConstants.INTERACTION_CREATED_SELF;

/**
 * Created by dodo on 19/10/16.
 */
public class RecruiterInteractionService {
    public static void createInteractionForRecruiterSignUp(String objectAUUId, String result, Integer interactionType, String createdBy) {
        Interaction interaction = new Interaction(
                objectAUUId,
                ServerConstants.OBJECT_TYPE_RECRUTER,
                interactionType,
                InteractionConstants.INTERACTION_NOTE_BLANK,
                result,
                createdBy,
                InteractionConstants.INTERACTION_CHANNEL_RECRUITER_WEBSITE
        );
        InteractionService.createInteraction(interaction);
    }

    public static void createInteractionForRecruiterLogin(String objectAUUId) {
        Interaction interaction = new Interaction(
                objectAUUId,
                ServerConstants.OBJECT_TYPE_RECRUTER,
                InteractionConstants.INTERACTION_TYPE_RECRUITER_LOG_IN,
                InteractionConstants.INTERACTION_NOTE_BLANK,
                InteractionConstants.INTERACTION_RESULT_RECRUITER_SIGNEDIN,
                InteractionConstants.INTERACTION_CREATED_SELF,
                InteractionConstants.INTERACTION_CHANNEL_RECRUITER_WEBSITE
        );
        InteractionService.createInteraction(interaction);
    }

    public static void createInteractionForRecruiterAddPasswordViaWebsite(String objectAUUId){
        Interaction interaction = new Interaction(
                objectAUUId,
                ServerConstants.OBJECT_TYPE_RECRUTER,
                InteractionConstants.INTERACTION_TYPE_RECRUITER_PASSWORD_ADDED,
                InteractionConstants.INTERACTION_NOTE_BLANK,
                InteractionConstants.INTERACTION_RESULT_NEW_RECRUITER + " & " + InteractionConstants.INTERACTION_NOTE_RECRUITER_PASSWORD_CHANGED,
                INTERACTION_CREATED_SELF,
                INTERACTION_CHANNEL_RECRUITER_WEBSITE
        );
        InteractionService.createInteraction(interaction);
    }

    public static void createInteractionForRecruiterProfileUpdate(String uuId, String result, InteractionService.InteractionChannelType channelType){
        Interaction interaction = new Interaction(
                uuId,
                ServerConstants.OBJECT_TYPE_RECRUTER,
                InteractionConstants.INTERACTION_TYPE_RECRUITER_PROFILE_UPDATE,
                InteractionConstants.INTERACTION_NOTE_BLANK,
                result,
                channelType.toString(),
                InteractionConstants.INTERACTION_CHANNEL_RECRUITER_WEBSITE
        );
        InteractionService.createInteraction(interaction);
    }

    public static void createInteractionForRecruiterSearchCandidate(String uuId, String result){
        Interaction interaction = new Interaction(
                uuId,
                ServerConstants.OBJECT_TYPE_RECRUTER,
                InteractionConstants.INTERACTION_TYPE_RECRUITER_SEARCH_CANDIDATE,
                InteractionConstants.INTERACTION_NOTE_BLANK,
                result,
                INTERACTION_CREATED_SELF,
                InteractionConstants.INTERACTION_CHANNEL_RECRUITER_WEBSITE
        );
        InteractionService.createInteraction(interaction);
    }

}
