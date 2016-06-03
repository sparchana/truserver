package controllers.businessLogic;

import api.ServerConstants;
import models.entity.Interaction;
import models.entity.Lead;
import play.Logger;

/**
 * Created by batcoder1 on 5/5/16.
 */
public class LeadService {
    public static void createLead(Lead lead, boolean isSupport){
        Lead existingLead = Lead.find.where().eq("leadMobile",lead.leadMobile).findUnique();
        String objectAUUId;
        String result;
        String note = "";
        int interactionType = ServerConstants.INTERACTION_TYPE_WEBSITE;
        int objectAType;
        String createdBy = "System";
        if(!isSupport) {
            createdBy = ServerConstants.INTERACTION_CREATED_SELF;
        } else {
            interactionType = ServerConstants.INTERACTION_TYPE_CALL_OUT; //TODO: Call Out/In need to be distinguished
        }
        if(existingLead == null){
            //if lead does not exists
            Lead.addLead(lead);
            result = ServerConstants.INTERACTION_RESULT_NEW_LEAD;
            Logger.info("Lead added");
            objectAUUId = lead.leadUUId;
            objectAType = lead.getLeadType();
        } else {
            // lead exists
            result = ServerConstants.INTERACTION_RESULT_EXISTING_LEAD;
            objectAUUId = existingLead.leadUUId;
            objectAType = existingLead.getLeadType();
        }

        Interaction interaction = new Interaction(
                objectAUUId,
                objectAType,
                interactionType,
                note,
                result,
                createdBy
        );

        InteractionService.createInteraction(interaction);
    }
}
