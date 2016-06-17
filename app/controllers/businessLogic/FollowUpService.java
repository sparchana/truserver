package controllers.businessLogic;

import api.ServerConstants;
import api.http.httpRequest.AddOrUpdateFollowUpRequest;
import api.http.httpResponse.AddOrUpdateFollowUpResponse;
import models.entity.Lead;
import models.entity.OO.FollowUp;
import play.Logger;

import java.sql.Timestamp;

/**
 * Created by zero on 16/6/16.
 */
public class FollowUpService {

    public static AddOrUpdateFollowUpResponse CreateOrUpdateFollowUp(AddOrUpdateFollowUpRequest request) {
        AddOrUpdateFollowUpResponse response = new AddOrUpdateFollowUpResponse();
        Logger.info(" CreateOrUpdateFollowUp Triggered with leadMobile:" + request.getLeadMobile() + " scheduleTimeStamp: " + request.getFollowUpDateTime());
        boolean freshFollowUp = false;

        FollowUp followUp = FollowUp.find.where().eq("followUpMobile", request.getLeadMobile()).findUnique();
        if(followUp == null){
            followUp = new FollowUp();
            followUp.setFollowUpMobile(request.getLeadMobile());
            freshFollowUp = true;
        }
        followUp.setFollowUpTimeStamp(request.getFollowUpDateTime());
        followUp.setFollowUpUpdateTimeStamp(new Timestamp(System.currentTimeMillis()));

        if(request.getFollowUpDateTime() == null || request.getFollowUpDateTime().equals("")){
            Logger.info("deactivating followup req for leadMobile: " + request.getLeadMobile());
            response.setStatus(AddOrUpdateFollowUpResponse.STATUS_FOLLOWUP_DEACTIVATED);
            if(!followUp.isFollowUpStatusRequired()){
                // already deactivated
                return response;
            }
            followUp.setFollowUpStatus(ServerConstants.FOLLOW_UP_DEACTIVATE);
        } else {
            followUp.setFollowUpStatus(ServerConstants.FOLLOW_UP_ACTIVATE);
        }

        // TODO: update() instead of save should work in both case
        if(freshFollowUp){
            response.setStatus(AddOrUpdateFollowUpResponse.STATUS_FOLLOWUP_CREATE_SUCCESS);
        } else {
            followUp.setFollowUpUpdateTimeStamp(new Timestamp(System.currentTimeMillis()));
            response.setStatus(AddOrUpdateFollowUpResponse.STATUS_FOLLOWUP_UPDATE_SUCCESS);
        }

        FollowUp updatedFollowUp = FollowUp.find.where().eq("followUpMobile", request.getLeadMobile()).findUnique();
        if(updatedFollowUp != null){
            Logger.info("Updated followUp val:" + updatedFollowUp.isFollowUpStatusRequired());
        }

        Lead lead = LeadService.isLeadExists(request.getLeadMobile());
        if(lead != null){
            lead.setFollowUp(followUp);
            lead.update();
        } else {
            response.setStatus(AddOrUpdateFollowUpResponse.STATUS_FOLLOWUP_FAILURE);
            return response;
        }

        InteractionService.createInteractionForFollowUpRequest(request.getLeadMobile(), request.getFollowUpDateTime());

        return response;
    }
}
