package controllers.businessLogic;

import api.ServerConstants;
import models.entity.Candidate;
import models.entity.Interaction;
import models.entity.Lead;
import models.entity.Static.LeadSource;
import play.Logger;

import javax.persistence.NonUniqueResultException;
import java.util.List;

import static play.mvc.Controller.session;

/**
 * Created by batcoder1 on 5/5/16.
 */
public class LeadService {

    public static Lead createOrUpdateConvertedLead(String leadName, String leadMobile, int leadSourceId, boolean isSupport){
        Lead existingLead = isLeadExists(leadMobile);
        if(existingLead == null){
            int leadChannel = isSupport ? ServerConstants.LEAD_CHANNEL_SUPPORT : ServerConstants.LEAD_CHANNEL_WEBSITE;
            Lead lead = new Lead(
                    leadName,
                    leadMobile,
                    leadChannel,
                    ServerConstants.TYPE_CANDIDATE,
                    leadSourceId
            );
            lead.setLeadStatus(ServerConstants.LEAD_STATUS_WON);
            lead.setLeadType(ServerConstants.TYPE_CANDIDATE);
            LeadService.createLead(lead, isSupport);
            Logger.info("New Lead Created Successfully");
            return lead;
        }
        else {
            //TODO: No leadUpdateTimeStamp available though lead is updatable
            existingLead.setLeadStatus(ServerConstants.LEAD_STATUS_WON);
            existingLead.setLeadType(ServerConstants.TYPE_CANDIDATE);
            existingLead.setLeadSource(getLeadSourceFromLeadSourceId(leadSourceId));
            if(existingLead.getLeadName().trim().isEmpty()){
                existingLead.setLeadName(leadName);
            }
            Logger.info("Lead Updated Successfully");
        }
        return existingLead;
    }

    public static Lead isLeadExists(String mobile){
        try {
            Lead existingLead = Lead.find.where().eq("leadMobile", mobile).findUnique();
            if(existingLead != null) {
                return existingLead;
            }
        } catch (NonUniqueResultException nu){
            List<Lead> existingLeadList = Lead.find.where().eq("leadMobile", mobile).findList();
            if(existingLeadList !=  null && existingLeadList.size() > 1) {
                return LeadService.DeleteLeadButPreserveOldest(mobile);
            }
        }
        return null;
    }

    public static void createLead(Lead lead, boolean isSupport){
        Lead existingLead = isLeadExists(lead.getLeadMobile());
        String objectAUUId;
        String result;
        String note = ServerConstants.INTERACTION_NOTE_BLANK;
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
            Lead.addLead(lead);
            result = ServerConstants.INTERACTION_RESULT_NEW_LEAD;
            Logger.info("Lead added");
            objectAUUId = lead.getLeadUUId();
        } else {
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


    public static LeadSource getLeadSourceFromLeadSourceId(int leadSourceId) {
        LeadSource leadSource = LeadSource.find.where().eq("leadSourceId", leadSourceId).findUnique();
        if(leadSource == null){
            Logger.info(" Static table Leadsource doesn't have entry for leadSourceId: " + leadSourceId);
        }
        return leadSource;
    }



    public static void DeleteLeadButPreserveOldestFromCandidate(Candidate candidate) {
        List<Lead> existingLeadList = Lead.find.where().eq("leadMobile", candidate.getCandidateMobile()).findList();
        Lead nonPerishedLead = new Lead();
        if(existingLeadList != null && existingLeadList.size() >1){
            // existingLeadList.sort((l1, l2) -> l1.getLeadId() >= l2.getLeadId() ? 1 : 0);
            for(Lead lead : existingLeadList) {
                // delete lead records only when its not tied up to the nonPerishCandidate
                if(lead.getLeadId() != candidate.getLead().getLeadId()){
                    List<Interaction> interactionList = Interaction.find.where().eq("objectAUUId", lead.getLeadUUId()).findList();
                    // delete lead interaction forever
                    for (Interaction interactionToDelete : interactionList) {
                        interactionToDelete.delete();
                    }
                    // delete lead forever
                    lead.delete();
                } else {
                    nonPerishedLead = lead;
                }
            }
        }
    }

    public static Lead DeleteLeadButPreserveOldest(String mobile) {
        CandidateService.DeleteCandidateButPreserveOldest(mobile);

        List<Lead> existingLeadList = Lead.find.where().eq("leadMobile", mobile).findList();
        if(existingLeadList != null && existingLeadList.size() > 1){
            existingLeadList.sort((l1, l2) -> l1.getLeadId() >= l2.getLeadId() ? 1 : 0);
            for(int i=1; i<existingLeadList.size(); i++) {
                // delete lead records only when its not tied up to the nonPerishCandidate
                List<Interaction> interactionList = Interaction.find.where().eq("objectAUUId", existingLeadList.get(i).getLeadUUId()).findList();
                // delete lead interaction forever
                for (Interaction interactionToDelete : interactionList) {
                    interactionToDelete.delete();
                }
                // delete lead forever
                existingLeadList.get(i).delete();
            }
        }
        return existingLeadList.get(0);
    }
}
