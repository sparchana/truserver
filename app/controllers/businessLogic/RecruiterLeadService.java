package controllers.businessLogic;

import api.InteractionConstants;
import api.ServerConstants;
import api.http.FormValidator;
import models.entity.Interaction;
import models.entity.Lead;
import models.entity.RecruiterLead;
import models.entity.Static.LeadSource;
import models.util.SmsUtil;
import play.Logger;

import javax.persistence.NonUniqueResultException;
import java.util.List;

import static play.mvc.Controller.session;

/**
 * Created by dodo on 5/10/16.
 */
public class RecruiterLeadService {
    public static RecruiterLead createOrUpdateConvertedRecruiterLead(String leadName, String leadMobile){
        RecruiterLead existingLead = isLeadExists(leadMobile);
        if(existingLead == null){
            RecruiterLead lead = new RecruiterLead(
                    leadName,
                    leadMobile,
                    1
            );
            lead.setRecruiterLeadStatus(ServerConstants.LEAD_STATUS_WON);
            RecruiterLeadService.createLead(lead);
            Logger.info("New Lead Created Successfully");
            return lead;
        }
        else {
            //TODO: No leadUpdateTimeStamp available though lead is updatable
            existingLead.setRecruiterLeadStatus(ServerConstants.LEAD_STATUS_WON);
            if(existingLead.getRecruiterLeadName().trim().isEmpty()){
                existingLead.setRecruiterLeadName(leadName);
            }
            Logger.info("Lead Updated Successfully");
        }
        return existingLead;
    }

    public static RecruiterLead isLeadExists(String mobile){
        try {
            RecruiterLead existingLead = RecruiterLead.find.where().eq("recruiter_lead_mobile", FormValidator.convertToIndianMobileFormat(mobile)).findUnique();
            if(existingLead != null) {
                return existingLead;
            }
        } catch (NonUniqueResultException nu){}
        return null;
    }

    public static void createLead(RecruiterLead lead){
        RecruiterLead existingLead = isLeadExists(FormValidator.convertToIndianMobileFormat(lead.getRecruiterLeadMobile()));
        if(existingLead == null) {
            RecruiterLead.addLead(lead);
            Logger.info("Recruiter Lead added");
        } else {
            Logger.info("Existing lead made contact");
        }
    }
}
