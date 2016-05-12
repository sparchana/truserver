package controllers.businessLogic;

import models.entity.Interaction;
import play.Logger;

/**
 * Created by batcoder1 on 5/5/16.
 */
public class InteractionService {
    public static void createInteraction(Interaction interaction){
        Interaction.addInteraction(interaction);
        Logger.info("Interaction saved");
    }
}
