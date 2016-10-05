package controllers.businessLogic;

import api.ServerConstants;
import api.http.FormValidator;
import api.http.httpRequest.Recruiter.RecruiterLeadRequest;
import api.http.httpResponse.Recruiter.RecruiterLeadResponse;
import models.entity.OM.RecruiterLeadToJobRole;
import models.entity.OM.RecruiterLeadToLocality;
import models.entity.RecruiterLead;
import models.entity.Static.JobRole;
import models.entity.Static.Locality;
import play.Logger;

import javax.persistence.NonUniqueResultException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by dodo on 5/10/16.
 */
public class RecruiterLeadService {
    static RecruiterLead createOrUpdateConvertedRecruiterLead(String leadName, String leadMobile){
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

    public static RecruiterLeadResponse createLeadWithOtherDetails(RecruiterLeadRequest recruiterLeadRequest){
        RecruiterLeadResponse recruiterLeadResponse = new RecruiterLeadResponse();
        RecruiterLead existingLead = isLeadExists(FormValidator.convertToIndianMobileFormat(recruiterLeadRequest.getRecruiterMobile()));
        if(existingLead == null) {
            RecruiterLead lead = new RecruiterLead();
            List<Integer> jobRoleList = recruiterLeadRequest.getRecruiterJobRole();
            List<Integer> jobLocalityList = recruiterLeadRequest.getRecruiterJobLocality();

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

            RecruiterLead.addLead(lead);
            Logger.info("Recruiter Lead added");
            recruiterLeadResponse.setStatus(RecruiterLeadResponse.STATUS_SUCCESS);
        } else {
            Logger.info("Existing lead made contact");
            recruiterLeadResponse.setStatus(RecruiterLeadResponse.STATUS_SUCCESS);
        }
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
