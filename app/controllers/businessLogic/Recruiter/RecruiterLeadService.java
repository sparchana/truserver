package controllers.businessLogic.Recruiter;

import api.InteractionConstants;
import api.ServerConstants;
import api.http.FormValidator;
import api.http.httpRequest.Recruiter.RecruiterLeadRequest;
import api.http.httpRequest.TruRequest;
import api.http.httpResponse.Recruiter.RecruiterLeadResponse;
import api.http.httpResponse.TruResponse;
import controllers.TruService;
import models.entity.Recruiter.OM.RecruiterLeadToJobRole;
import models.entity.Recruiter.OM.RecruiterLeadToLocality;
import models.entity.Recruiter.RecruiterLead;
import models.entity.Static.JobRole;
import models.entity.Static.Locality;
import models.util.Message;
import play.Logger;
import javax.persistence.NonUniqueResultException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static controllers.businessLogic.Recruiter.RecruiterInteractionService.createInteractionForRecruiterLead;

/**
 * Created by dodo on 5/10/16.
 */
public class RecruiterLeadService extends TruService{

    @Override
    public String getEntityClassName() {
        return "models.entity.Recruiter.RecruiterLead";
    }

    @Override
    public String getResponseClassName() {
        return "api.http.httpResponse.Recruiter.RecruiterLeadResponse";
    }

    @Override
    public Object create(Object request) {
        Logger.info("Entering "+this.getClass().getSimpleName()+" Create. Request class: "+request.getClass().getSimpleName());
        // create recruiter lead
        TruResponse recruiterLeadResponse = (TruResponse) super.create(request);
        if(recruiterLeadResponse.getStatus() == TruResponse.STATUS_SUCCESS){
            // create company for recruiter lead
            CompanyLeadService companyLeadService = new CompanyLeadService();
            TruResponse companyResponse = (TruResponse) companyLeadService.create(request);
            List<TruResponse> companyResponseList = new ArrayList<>();
            companyResponseList.add(companyResponse);
            // aggregate messages (from company to recruiter)
            recruiterLeadResponse = Message.collateMessages(companyResponseList,recruiterLeadResponse);
            // set company reference in recruiter lead
            setChildReference(companyResponse,"companyLead");

            // create job roles for recruiter lead
            RecruiterLeadToJobRoleService recruiterLeadToJobRoleService = new RecruiterLeadToJobRoleService();
            List<TruResponse> childResponse = recruiterLeadToJobRoleService.createAsChildren(request,recruiterLeadResponse.getEntity());
            // aggregate messages (from all children to the parent)
            recruiterLeadResponse = Message.collateMessages(childResponse,recruiterLeadResponse);
            // set job role(s) reference in recruiter lead
            setChildrenReference(childResponse,"recruiterLeadToJobRoleList");

            // save, if allowed
            if(checkSaveAllowed(recruiterLeadResponse)){
                companyLeadService.save();
                save();
                recruiterLeadResponse.setStatus(TruResponse.STATUS_SUCCESS);
                addMessage(recruiterLeadResponse,Message.MESSAGE_INFO,"Lead "+((RecruiterLead) recruiterLeadResponse.getEntity()).getRecruiterLeadId()+" created successfully!");
            }
            else {
                recruiterLeadResponse.setStatus(TruResponse.STATUS_FAILURE);
                addMessage(recruiterLeadResponse,Message.MESSAGE_ERROR,"Lead could not be created. Fix the errors and submit");
            }
        }

        // remove duplicate messages
        recruiterLeadResponse.setMessages(Message.removeDuplicateText(recruiterLeadResponse.getMessages()));

        Logger.info("Exiting "+this.getClass().getSimpleName()+" Create");

        return recruiterLeadResponse;
    }

    @Override
    public TruResponse update(TruRequest request) {
        Logger.info("Entering "+this.getClass().getSimpleName()+" Update. Request class: "+request.getClass().getSimpleName());
        // update recruiter lead
        TruResponse recruiterLeadResponse = (TruResponse) super.update(request);

        // update company for recruiter lead
        CompanyLeadService companyLeadService = new CompanyLeadService();
        if(companyLeadService.areChangedFieldsRelevant(request)){
            TruResponse companyResponse = (TruResponse) companyLeadService.update(request);
            if(companyLeadService.isNew(companyResponse)){
                // set company reference in recruiter lead
                setChildReference(companyResponse,"companyLead");
            }
            List<TruResponse> companyResponseList = new ArrayList<>();
            companyResponseList.add(companyResponse);
            // aggregate messages (from company to recruiter)
            recruiterLeadResponse = Message.collateMessages(companyResponseList,recruiterLeadResponse);
        }

        // update job roles for recruiter lead
        RecruiterLeadToJobRoleService recruiterLeadToJobRoleService = new RecruiterLeadToJobRoleService();
        List<TruResponse> childResponse = recruiterLeadToJobRoleService.updateAsChildren(request,recruiterLeadResponse.getEntity());

        Logger.info(this.getClass().getSimpleName()+".update: Job Role update method response returned "+childResponse.size()+" elements");

        // aggregate messages (from all children to the parent)
        recruiterLeadResponse = Message.collateMessages(childResponse,recruiterLeadResponse);
        // set job role(s) reference in recruiter lead
        //appendChildrenReference(recruiterLeadToJobRoleService.filterNew(childResponse),"recruiterLeadToJobRoleList");
        setChildrenReference(childResponse,"recruiterLeadToJobRoleList");

        // save, if allowed
        if(checkSaveAllowed(recruiterLeadResponse)){
            companyLeadService.save();
            save();
            recruiterLeadToJobRoleService.save();
            recruiterLeadResponse.setStatus(TruResponse.STATUS_SUCCESS);
            addMessage(recruiterLeadResponse,Message.MESSAGE_INFO,"Lead"+" "+((RecruiterLead) recruiterLeadResponse.getEntity()).getRecruiterLeadId()+" updated successfully!");
            //addMessage(recruiterLeadResponse,Message.MESSAGE_INFO,"Lead"+((recruiterLead != null)?(" "+recruiterLead.getRecruiterLeadId()):"")+" updated successfully!");
        }
        else {
            recruiterLeadResponse.setStatus(TruResponse.STATUS_FAILURE);
            addMessage(recruiterLeadResponse,Message.MESSAGE_ERROR,"Lead could not be updated. Fix the errors and submit");
        }

        // remove duplicate messages
        recruiterLeadResponse.setMessages(Message.removeDuplicateText(recruiterLeadResponse.getMessages()));

        Logger.info("Exiting "+this.getClass().getSimpleName()+" Update");

        return recruiterLeadResponse;
    }

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
        /*
        List<Integer> jobRoleList = recruiterLeadRequest.getRecruiterJobRole();
        List<Integer> jobLocalityList = null;//recruiterLeadRequest.getRecruiterJobLocality();

        String objectAUUId;
        String result;

        Integer interactionType;

        RecruiterLead existingLead = null;//isLeadExists(FormValidator.convertToIndianMobileFormat(recruiterLeadRequest.getRecruiterMobile()));
        if(existingLead == null) {
            RecruiterLead lead = new RecruiterLead();

            //lead.setRecruiterLeadMobile(FormValidator.convertToIndianMobileFormat(recruiterLeadRequest.getRecruiterMobile()));
            if(jobRoleList != null){
                lead.setRecruiterLeadToJobRoleList(getRecruiterJobPreferenceList(jobRoleList, lead));
            }
            if(jobLocalityList != null){
                //lead.setRecruiterLeadToLocalityList(getRecruiterJobLocalityList(jobLocalityList, lead));
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
                //existingLead.setRecruiterLeadToLocalityList(getRecruiterJobLocalityList(jobLocalityList, existingLead));
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
        //SmsUtil.sendRecruiterLeadMsg(FormValidator.convertToIndianMobileFormat(recruiterLeadRequest.getRecruiterMobile()));
        */
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
            //recruiterLeadToLocality.setRecruiterLead(lead);
            Locality locality = Locality.find.where().eq("LocalityId", s).findUnique();
            recruiterLeadToLocality.setLocality(locality);
            recruiterJobLocality.add(recruiterLeadToLocality);
        }
        return recruiterJobLocality;
    }

    @Override
    public List<TruResponse> readById(List<Long> ids) {
        return super.readById(ids);
    }

    @Override
    public Map<String, Boolean> getDeleteDependencies() {
        Map<String,Boolean> dependencies = new HashMap<String,Boolean>();
        dependencies.put("RecruiterLeadToJobRole",Boolean.TRUE);
        dependencies.put("CompanyLead",Boolean.FALSE);
        return dependencies;
    }

    @Override
    public TruResponse delete(TruRequest request) {
        TruResponse recruiterLeadResponse = new TruResponse();

        Logger.info("Entering "+this.getClass().getSimpleName()+" Delete. Request class: "+request.getClass().getSimpleName());

        // deleting parent will delete everything
        if(isDeleteRelevant(request)){
            recruiterLeadResponse = super.delete(request);
        }
        else {
            RecruiterLeadToJobRoleService recruiterLeadToJobRoleService = new RecruiterLeadToJobRoleService();
            recruiterLeadResponse = recruiterLeadToJobRoleService.delete(request);
            Logger.info(this.getClass().getSimpleName()+".delete: recruiterLeadResponse="+recruiterLeadResponse.getClass().getSimpleName());
            // aggregate messages (from job role to recruiter)
            //recruiterLeadResponse = Message.collateMessages(recruiterLeadToJobRoleResponse,recruiterLeadResponse);
        }

        // call company delete
        CompanyLeadService companyLeadService = new CompanyLeadService();
        TruResponse companyLeadResponse = companyLeadService.delete(request);
        Logger.info(this.getClass().getSimpleName()+".delete: companyLeadResponse="+companyLeadResponse.getClass().getSimpleName());
        // aggregate messages (from company to recruiter)
        recruiterLeadResponse = Message.collateMessages(companyLeadResponse,recruiterLeadResponse);

        // remove duplicate messages
        recruiterLeadResponse.setMessages(Message.removeDuplicateText(recruiterLeadResponse.getMessages()));

        Logger.info("Exiting "+this.getClass().getSimpleName()+" Delete");

        return recruiterLeadResponse;
    }

}
