package controllers.businessLogic;

import api.ServerConstants;
import models.entity.Interaction;
import models.entity.Lead;
import play.Logger;

/**
 * Created by batcoder1 on 5/5/16.
 */
public class LeadService {
    public static void createLead(Lead lead){
        Lead existingLead = Lead.find.where().eq("leadMobile",lead.leadMobile).findUnique();
        Interaction interaction = new Interaction();
        if(existingLead == null){
            //if lead does not exists
            Lead.addLead(lead);
            interaction.result = "New lead through website";
            Logger.info("Lead added");
            interaction.objectAUUId = lead.leadUUId;
            interaction.objectAType = lead.getLeadType();
        }
        else{
            // lead exists
            interaction.result = "Existing lead made contact through website";
            interaction.objectAUUId = existingLead.leadUUId;
            interaction.objectAType = existingLead.getLeadType();
        }

        interaction.interactionType = ServerConstants.INTERACTION_TYPE_WEBSITE;
        InteractionService.createInteraction(interaction);
    }
}
