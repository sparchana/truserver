package controllers.businessLogic;

import api.ServerConstants;
import models.entity.Interaction;
import models.entity.Lead;
import models.entity.Static.LeadSource;
import models.util.SmsUtil;
import play.Logger;

import javax.persistence.NonUniqueResultException;
import java.util.List;

import static play.mvc.Controller.session;

/**
 * Created by batcoder1 on 5/5/16.
 */
public class LeadService {

    public static Lead createOrUpdateConvertedLead(String leadName, String leadMobile, int leadSourceId, InteractionService.InteractionChannelType channelType){
        Lead existingLead = isLeadExists(leadMobile);
        if(existingLead == null){
            int leadChannel = getLeadChannel(channelType);
            Lead lead = new Lead(
                    leadName,
                    leadMobile,
                    leadChannel,
                    ServerConstants.TYPE_CANDIDATE,
                    leadSourceId
            );
            lead.setLeadStatus(ServerConstants.LEAD_STATUS_WON);
            lead.setLeadType(ServerConstants.TYPE_CANDIDATE);
            LeadService.createLead(lead, channelType);
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

    public static int getLeadChannel(InteractionService.InteractionChannelType channelType){
        int response = ServerConstants.LEAD_CHANNEL_UNKNOWN;
        switch (channelType){
            case SELF:
                response = ServerConstants.LEAD_CHANNEL_WEBSITE;
                break;
            case SELF_ANDROID:
                response = ServerConstants.LEAD_CHANNEL_ANDROID;
                break;
            case  SUPPORT:
                response = ServerConstants.LEAD_CHANNEL_SUPPORT;
                break;
            case KNOWLARITY:
                response = ServerConstants.LEAD_CHANNEL_KNOWLARITY;
                break;
            default:
                break;
        }
        return response;
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
                existingLeadList.sort((l1, l2) -> l1.getLeadId() >= l2.getLeadId() ? 1 : 0);
                Logger.info("Duplicate Candidate Encountered with mobile no: "+ mobile + "- Returned CandidateId = "
                        + existingLeadList.get(0).getLeadId() + " UUID-:"+existingLeadList.get(0).getLeadUUId());
                SmsUtil.sendDuplicateLeadSmsToDevTeam(mobile);
                return existingLeadList.get(0);
            }
        }
        return null;
    }

    public static void createLead(Lead lead, InteractionService.InteractionChannelType channelType){
        Lead existingLead = isLeadExists(lead.getLeadMobile());
        String objectAUUId;
        String result;
        String note = ServerConstants.INTERACTION_NOTE_BLANK;
        int interactionType = ServerConstants.INTERACTION_TYPE_WEBSITE;
        int objectAType;
        String createdBy;
        if(channelType == InteractionService.InteractionChannelType.SUPPORT) {
            interactionType = ServerConstants.INTERACTION_TYPE_CALL_OUT; //TODO: Call Out/In need to be distinguished
            createdBy = session().get("sessionUsername");
        } else {
            createdBy = channelType.toString();
            interactionType = channelType == InteractionService.InteractionChannelType.SELF_ANDROID
                    ? ServerConstants.INTERACTION_TYPE_ANDROID : ServerConstants.INTERACTION_TYPE_WEBSITE;
        }
        if(existingLead == null) {
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

}
