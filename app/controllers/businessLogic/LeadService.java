package controllers.businessLogic;

import api.ServerConstants;
import models.entity.Interaction;
import models.entity.Lead;
import play.Logger;

import static play.mvc.Controller.session;

/**
 * Created by batcoder1 on 5/5/16.
 */
public class LeadService {
    public static void createLead(Lead lead, boolean isSupport){
        Lead existingLead = Lead.find.where().eq("leadMobile",lead.getLeadMobile()).findUnique();
        String objectAUUId;
        String result;
        String note = "";
        int interactionType = ServerConstants.INTERACTION_TYPE_WEBSITE;
        int objectAType;
        String createdBy;
        if(!isSupport) {
            createdBy = ServerConstants.INTERACTION_CREATED_SELF;
        } else {
            interactionType = ServerConstants.INTERACTION_TYPE_CALL_OUT; //TODO: Call Out/In need to be distinguished
            createdBy = session().get("sessionUsername");
        }
        if(existingLead == null){
            //if lead does not exists
            Lead.addLead(lead);
            result = ServerConstants.INTERACTION_RESULT_NEW_LEAD;
            Logger.info("Lead added");
            objectAUUId = lead.getLeadUUId();
        } else {
            // lead exists
            result = ServerConstants.INTERACTION_RESULT_EXISTING_LEAD;
            objectAUUId = existingLead.getLeadUUId();
        }
        objectAType = ServerConstants.OBJECT_TYPE_LEAD;
        Interaction interaction = new Interaction(
                objectAUUId,
                objectAType,
                interactionType,
                note,
                result,
                createdBy
        );
        Logger.info("Interaction CreatedBy : " + createdBy);
        InteractionService.createInteraction(interaction);
    }
}
