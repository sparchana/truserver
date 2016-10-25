package controllers.businessLogic.Recruiter;

import api.InteractionConstants;
import api.ServerConstants;
import api.http.FormValidator;
import api.http.httpRequest.Recruiter.RecruiterLeadRequest;
import api.http.httpResponse.Recruiter.RecruiterLeadResponse;
import models.entity.Recruiter.OM.RecruiterLeadToJobRole;
import models.entity.Recruiter.OM.RecruiterLeadToLocality;
import models.entity.Recruiter.RecruiterLead;
import models.entity.Static.JobRole;
import models.entity.Static.Locality;
import models.util.SmsUtil;
import play.Logger;
import play.core.server.Server;

import javax.persistence.NonUniqueResultException;
import java.util.ArrayList;
import java.util.List;

import static controllers.businessLogic.Recruiter.RecruiterInteractionService.createInteractionForRecruiterLead;
import static play.mvc.Controller.session;

/**
 * Created by dodo on 5/10/16.
 */
public class RecruiterLeadService {
    public static RecruiterLead createOrUpdateConvertedRecruiterLead(String leadName, String leadMobile, int leadChannel)
    {
        RecruiterLead existingLead = isLeadExists(leadMobile);
        if(existingLead == null){
            RecruiterLead lead = new RecruiterLead(
                leadName,
                leadMobile,
                leadChannel
            );
            lead.setRecruiterLeadStatus(ServerConstants.LEAD_STATUS_WON);
            RecruiterLeadService.createLead(lead);
            Logger.info("New Lead Created Successfully");
            return lead;
        } else {
            existingLead.setRecruiterLeadStatus(ServerConstants.LEAD_STATUS_WON);
            if(existingLead.getRecruiterLeadName().trim().isEmpty()){
                existingLead.setRecruiterLeadName(leadName);
            }
            Logger.info("Lead Updated Successfully");
        }
        return existingLead;
    }

    private static RecruiterLead isLeadExists(String mobile){
        try {
            RecruiterLead existingLead = RecruiterLead.find.where().eq("recruiter_lead_mobile", FormValidator.convertToIndianMobileFormat(mobile)).findUnique();
            if(existingLead != null) {
                return existingLead;
            }
        } catch (NonUniqueResultException nu){}
        return null;
    }

    private static void createLead(RecruiterLead lead){
        RecruiterLead existingLead = isLeadExists(FormValidator.convertToIndianMobileFormat(lead.getRecruiterLeadMobile()));
        if(existingLead == null) {
            RecruiterLead.addLead(lead);
            Logger.info("Recruiter Lead added");
        } else {
            Logger.info("Existing lead made contact");
        }
    }

    public static RecruiterLeadResponse createLeadWithOtherDetails(RecruiterLeadRequest recruiterLeadRequest, int leadChannel)
    {
        RecruiterLeadResponse recruiterLeadResponse = new RecruiterLeadResponse();
        List<Integer> jobRoleList = recruiterLeadRequest.getRecruiterJobRole();
        List<Integer> jobLocalityList = recruiterLeadRequest.getRecruiterJobLocality();

        String objectAUUId;
        String result;

        Integer interactionType;

        RecruiterLead existingLead = isLeadExists(FormValidator.convertToIndianMobileFormat(recruiterLeadRequest.getRecruiterMobile()));
        if(existingLead == null) {
            RecruiterLead lead = new RecruiterLead();

            lead.setRecruiterLeadMobile(FormValidator.convertToIndianMobileFormat(recruiterLeadRequest.getRecruiterMobile()));
            if(jobRoleList != null){
                lead.setRecruiterLeadToJobRoleList(getRecruiterJobPreferenceList(jobRoleList, lead));
            }
            if(jobLocalityList != null){
                lead.setRecruiterLeadToLocalityList(getRecruiterJobLocalityList(jobLocalityList, lead));
            }
            if(recruiterLeadRequest.getRecruiterRequirement() != null){
                lead.setRecruiterLeadRequirement(recruiterLeadRequest.getRecruiterRequirement());
            }

            lead.setRecruiterLeadChannel(leadChannel);
            lead.setRecruiterLeadStatus(ServerConstants.LEAD_STATUS_NEW);
            RecruiterLead.addLead(lead);

            objectAUUId = lead.getRecruiterLeadUUId();
            result = InteractionConstants.INTERACTION_RESULT_NEW_RECRUITER_LEAD_ADDED;
            interactionType = InteractionConstants.INTERACTION_TYPE_RECRUITER_NEW_LEAD;

            Logger.info("Recruiter Lead added");
            recruiterLeadResponse.setStatus(RecruiterLeadResponse.STATUS_SUCCESS);
        } else {
            Logger.info("Existing lead made contact");

            //resetting lead locality
            List<RecruiterLeadToLocality> allLocality = RecruiterLeadToLocality.find.where().eq("recruiter_lead_id", existingLead.getRecruiterLeadId()).findList();
            for(RecruiterLeadToLocality recruiterLeadToLocality : allLocality){
                recruiterLeadToLocality.delete();
            }
            if(jobLocalityList != null){
                existingLead.setRecruiterLeadToLocalityList(getRecruiterJobLocalityList(jobLocalityList, existingLead));
            }

            //resetting lead job roles
            List<RecruiterLeadToJobRole> allJobRoles = RecruiterLeadToJobRole.find.where().eq("recruiter_lead_id", existingLead.getRecruiterLeadId()).findList();
            for(RecruiterLeadToJobRole recruiterLeadToJobRole : allJobRoles){
                recruiterLeadToJobRole.delete();
            }
            if(jobRoleList != null){
                existingLead.setRecruiterLeadToJobRoleList(getRecruiterJobPreferenceList(jobRoleList, existingLead));
            }

            existingLead.setRecruiterLeadChannel(leadChannel);
            if (existingLead.getRecruiterLeadStatus() != ServerConstants.LEAD_STATUS_WON) {
                existingLead.setRecruiterLeadStatus(ServerConstants.LEAD_STATUS_NEW);
            }
            existingLead.update();

            objectAUUId = existingLead.getRecruiterLeadUUId();
            result = InteractionConstants.INTERACTION_RESULT_EXISTING_RECRUITER_MADE_CONTACT;
            interactionType = InteractionConstants.INTERACTION_TYPE_RECRUITER_EXISTING_LEAD;
            recruiterLeadResponse.setStatus(RecruiterLeadResponse.STATUS_SUCCESS);
        }

        createInteractionForRecruiterLead(objectAUUId, result, interactionType);
        SmsUtil.sendRecruiterLeadMsg(FormValidator.convertToIndianMobileFormat(recruiterLeadRequest.getRecruiterMobile()));
        return recruiterLeadResponse;
    }

    private static List<RecruiterLeadToJobRole> getRecruiterJobPreferenceList(List<Integer> jobRoleList, RecruiterLead lead) {
        List<RecruiterLeadToJobRole> recruiterJobPreferences = new ArrayList<>();
        for(Integer s : jobRoleList) {
            RecruiterLeadToJobRole recruiterLeadToJobRole = new RecruiterLeadToJobRole();
            recruiterLeadToJobRole.setRecruiterLead(lead);
            JobRole jobRole = JobRole.find.where().eq("JobRoleId", s).findUnique();
            recruiterLeadToJobRole.setJobRole(jobRole);
            recruiterJobPreferences.add(recruiterLeadToJobRole);
        }
        return recruiterJobPreferences;
    }

    private static List<RecruiterLeadToLocality> getRecruiterJobLocalityList(List<Integer> jobLocalityList, RecruiterLead lead) {
        List<RecruiterLeadToLocality> recruiterJobLocality = new ArrayList<>();
        for(Integer s : jobLocalityList) {
            RecruiterLeadToLocality recruiterLeadToLocality = new RecruiterLeadToLocality();
            recruiterLeadToLocality.setRecruiterLead(lead);
            Locality locality = Locality.find.where().eq("LocalityId", s).findUnique();
            recruiterLeadToLocality.setLocality(locality);
            recruiterJobLocality.add(recruiterLeadToLocality);
        }
        return recruiterJobLocality;
    }

}
