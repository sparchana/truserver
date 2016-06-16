package controllers.businessLogic;

import api.ServerConstants;
import api.http.httpRequest.AddOrUpdateFollowUpRequest;
import api.http.httpResponse.AddOrUpdateFollowUpResponse;
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
            freshFollowUp = true;
        }
        followUp.setFollowUpTimeStamp(request.getFollowUpDateTime());

        if(request.getFollowUpDateTime() == null || request.getFollowUpDateTime().equals("")){
            Logger.info("deactivating followup req for leadMobile: " + request.getLeadMobile());
            followUp.setFollowUpStatus(ServerConstants.FOLLOW_UP_DEACTIVATE);
            followUp.setFollowUpUpdateTimeStamp(new Timestamp(System.currentTimeMillis()));
            response.setStatus(AddOrUpdateFollowUpResponse.STATUS_FOLLOWUP_REMOVED_SUCCESS);
        } else {
            followUp.setFollowUpStatus(ServerConstants.FOLLOW_UP_ACTIVATE);
        }
        if(freshFollowUp){
            followUp.save();
            response.setStatus(AddOrUpdateFollowUpResponse.STATUS_FOLLOWUP_CREATE_SUCCESS);
        } else {
            Logger.info("FollowUp Updated");
            followUp.setFollowUpUpdateTimeStamp(new Timestamp(System.currentTimeMillis()));
            followUp.update();
            response.setStatus(AddOrUpdateFollowUpResponse.STATUS_FOLLOWUP_UPDATE_SUCCESS);
        }
        FollowUp updatedFollowUp = FollowUp.find.where().eq("followUpMobile", request.getLeadMobile()).findUnique();
        if(updatedFollowUp != null){
            Logger.info("Updated followUp val:" + updatedFollowUp.isFollowUpStatusRequired());
        }

        InteractionService.createInteractionForFollowUpRequest(request.getLeadMobile(), request.getFollowUpDateTime());

        return response;
    }
}
