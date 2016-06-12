package controllers.businessLogic;

import api.ServerConstants;
import models.entity.Interaction;
import play.Logger;

/**
 * Created by batcoder1 on 5/5/16.
 */
public class InteractionService {

    public static void createInteractionForSignUpCandidate(String objectAUUId, String result, boolean isSupport) {
        if(!isSupport){
            Interaction interaction = new Interaction(
                    objectAUUId,
                    ServerConstants.OBJECT_TYPE_CANDIDATE,
                    ServerConstants.INTERACTION_TYPE_WEBSITE,
                    ServerConstants.INTERACTION_NOTE_SELF_SIGNEDUP,
                    result,
                    ServerConstants.INTERACTION_CREATED_SELF
            );
            InteractionService.createInteraction(interaction);
        }
    }

    public static void createInteractionForCreateCandidateProfile(String uuId, Integer interactionType, String interactionNote, String interactionResult, String createdBy){
        Interaction interaction = new Interaction(
                uuId,
                ServerConstants.OBJECT_TYPE_CANDIDATE,
                interactionType,
                interactionNote,
                interactionResult,
                createdBy
        );

        InteractionService.createInteraction(interaction);
    }

    public static void createInteraction(Interaction interaction){
        Interaction.addInteraction(interaction);
        Logger.info("Interaction saved");
    }
}
