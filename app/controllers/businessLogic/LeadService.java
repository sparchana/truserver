package controllers.businessLogic;

import api.InteractionConstants;
import api.ServerConstants;
import api.http.FormValidator;
import models.entity.Interaction;
import models.entity.Lead;
import models.entity.Static.LeadSource;
import models.util.SmsUtil;
import play.Logger;

import javax.persistence.NonUniqueResultException;
import java.util.List;

import static api.InteractionConstants.*;
import static play.mvc.Controller.session;

/**
 * Created by batcoder1 on 5/5/16.
 */
public class LeadService {
    public enum LeadType {
        UNKNOWN,
        CANDIDATE,
        RECRUITER,
        PARTNER;

        public String toString() {
            return name().charAt(0) + name().substring(1).toLowerCase();
        }
    }

    public static Lead createOrUpdateConvertedLead(String leadName, String leadMobile, int leadSourceId, int channelType, LeadType leadType){
        Lead existingLead = isLeadExists(leadMobile);
        int leadTypeVal = 0;
        if(leadType == LeadType.CANDIDATE){
            leadTypeVal = ServerConstants.TYPE_CANDIDATE;
        } else if(leadType == LeadType.PARTNER){
            leadTypeVal = ServerConstants.TYPE_PARTNER;
        }
        if(existingLead == null){
            int leadChannel = getLeadChannel(channelType);
            Lead lead = new Lead(
                    leadName,
                    leadMobile,
                    leadChannel,
                    leadTypeVal,
                    leadSourceId
            );
            lead.setLeadStatus(ServerConstants.LEAD_STATUS_WON);
            lead.setLeadType(leadTypeVal);
            LeadService.createLead(lead, channelType);
            Logger.info("New Lead Created Successfully");
            return lead;
        }
        else {
            //TODO: No leadUpdateTimeStamp available though lead is updatable
            existingLead.setLeadStatus(ServerConstants.LEAD_STATUS_WON);
            existingLead.setLeadType(leadTypeVal);
            existingLead.setLeadSource(getLeadSourceFromLeadSourceId(leadSourceId));
            if(existingLead.getLeadName().trim().isEmpty()){
                existingLead.setLeadName(leadName);
            }
            Logger.info("Lead Updated Successfully");
        }
        return existingLead;
    }

    public static int getLeadChannel(int channelType) {
        int response = ServerConstants.LEAD_CHANNEL_UNKNOWN;
        switch (channelType){
            case INTERACTION_CHANNEL_CANDIDATE_WEBSITE:
                response = ServerConstants.LEAD_CHANNEL_WEBSITE;
                break;
            case INTERACTION_CHANNEL_CANDIDATE_ANDROID:
                response = ServerConstants.LEAD_CHANNEL_ANDROID;
                break;
            case INTERACTION_CHANNEL_SUPPORT_WEBSITE:
                response = ServerConstants.LEAD_CHANNEL_SUPPORT;
                break;
            case INTERACTION_CHANNEL_KNOWLARITY:
                response = ServerConstants.LEAD_CHANNEL_KNOWLARITY;
                break;
            case INTERACTION_CHANNEL_PARTNER_WEBSITE:
                response = ServerConstants.LEAD_CHANNEL_PARTNER;
                break;
            default:
                break;
        }
        return response;
    }

    public static Lead isLeadExists(String mobile){
        try {
            Lead existingLead = Lead.find.where().eq("leadMobile", FormValidator.convertToIndianMobileFormat(mobile)).findUnique();
            if(existingLead != null) {
                return existingLead;
            }
        } catch (NonUniqueResultException nu){
            List<Lead> existingLeadList = Lead.find.where().eq("leadMobile", FormValidator.convertToIndianMobileFormat(mobile)).findList();
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

    public static void createLead(Lead lead, int channelType){
        Lead existingLead = isLeadExists(FormValidator.convertToIndianMobileFormat(lead.getLeadMobile()));
        String objectAUUId;
        String result;
        int channel;
        String note = InteractionConstants.INTERACTION_NOTE_BLANK;
        int interactionType = InteractionConstants.INTERACTION_TYPE_CANDIDATE_NEW_LEAD;
        int objectAType;
        String createdBy;
        if(channelType == InteractionConstants.INTERACTION_CHANNEL_SUPPORT_WEBSITE) {
            interactionType = InteractionConstants.INTERACTION_TYPE_CANDIDATE_NEW_LEAD;
            createdBy = session().get("sessionUsername");
            channel = InteractionConstants.INTERACTION_CHANNEL_SUPPORT_WEBSITE;
        } else if(channelType == InteractionConstants.INTERACTION_CHANNEL_PARTNER_WEBSITE) {
            interactionType = InteractionConstants.INTERACTION_TYPE_PARTNER_NEW_LEAD;
            createdBy = InteractionConstants.INTERACTION_CREATED_PARTNER;
            channel = InteractionConstants.INTERACTION_CHANNEL_PARTNER_WEBSITE;
        } else if(channelType == INTERACTION_CHANNEL_CANDIDATE_ANDROID){
            createdBy = InteractionConstants.INTERACTION_TYPE_MAP.get(InteractionConstants.INTERACTION_CHANNEL_CANDIDATE_ANDROID);
            channel = InteractionConstants.INTERACTION_CHANNEL_CANDIDATE_ANDROID;
        } else if(channelType == InteractionConstants.INTERACTION_CHANNEL_KNOWLARITY) {
            createdBy = InteractionConstants.INTERACTION_TYPE_MAP.get(InteractionConstants.INTERACTION_CHANNEL_KNOWLARITY);
            channel = InteractionConstants.INTERACTION_CHANNEL_KNOWLARITY;
        } else {
            createdBy = InteractionConstants.INTERACTION_TYPE_MAP.get(InteractionConstants.INTERACTION_CHANNEL_CANDIDATE_WEBSITE);
            channel = InteractionConstants.INTERACTION_CHANNEL_CANDIDATE_WEBSITE;
        }
        if(existingLead == null) {
            Lead.addLead(lead);
            result = InteractionConstants.INTERACTION_RESULT_NEW_LEAD;
            Logger.info("Lead added");
            objectAUUId = lead.getLeadUUId();
        } else {
            interactionType = InteractionConstants.INTERACTION_TYPE_EXISTING_LEAD_CONTACT;
            result = InteractionConstants.INTERACTION_RESULT_EXISTING_LEAD;
            objectAUUId = existingLead.getLeadUUId();
        }
        objectAType = ServerConstants.OBJECT_TYPE_LEAD;

        Interaction interaction = new Interaction(
                objectAUUId,
                objectAType,
                interactionType,
                note,
                result,
                createdBy,
                channel
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