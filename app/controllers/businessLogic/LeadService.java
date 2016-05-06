package controllers.businessLogic;

import api.ServerConstants;
import api.http.AddLeadResponse;
import models.entity.Interaction;
import models.entity.Lead;
import play.Logger;

/**
 * Created by batcoder1 on 5/5/16.
 */
public class LeadService {
    public static AddLeadResponse createLead(Lead lead){
        AddLeadResponse addLeadResponse = new AddLeadResponse();
        Logger.info("mobile: " + lead.leadMobile);
        Lead existingLead = Lead.find.where().eq("leadMobile",lead.leadMobile).findUnique();
        if(existingLead == null){
            Lead.addLead(lead);
            Interaction interaction = new Interaction();
            interaction.objectAUUId = existingLead.leadUUId;
            interaction.objectAType = existingLead.getLeadType();
            interaction.interactionType = ServerConstants.INTERACTION_TYPE_WEBSITE;
            interaction.result = "New lead through website";
            InteractionService.createIntraction(interaction);
            Logger.info("Lead added");
            addLeadResponse.setStatus(AddLeadResponse.STATUS_SUCCESS);
        }
        else{
            Interaction interaction = new Interaction();
            interaction.objectAUUId = existingLead.leadUUId;
            interaction.objectAType = existingLead.getLeadType();
            interaction.interactionType = ServerConstants.INTERACTION_TYPE_WEBSITE;
            interaction.result = "Existing lead made contact through website";
            InteractionService.createIntraction(interaction);
            Logger.info("Lead already exists");
        }
        return addLeadResponse;
    }
}
