package controllers.businessLogic.Recruiter;

import api.http.httpRequest.Recruiter.RecruiterLeadRequest;
import api.http.httpRequest.Recruiter.RecruiterLeadToJobRoleRequest;
import api.http.httpRequest.TruRequest;
import api.http.httpResponse.TruResponse;
import com.avaje.ebean.Model;
import controllers.TruService;
import models.entity.Recruiter.OM.RecruiterLeadToJobRole;
import models.entity.Static.JobRole;
import models.util.Message;
import play.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by User on 23-11-2016.
 */
public class RecruiterLeadToJobRoleService extends TruService {

    @Override
    public String getEntityClassName() {
        return "models.entity.Recruiter.OM.RecruiterLeadToJobRole";
    }

    @Override
    public List<TruResponse> createAsChildren(Object request, Model parent) {
        Logger.info("Entering "+this.getClass().getSimpleName()+" createAsChildren. Request class: "+request.getClass().getSimpleName());

        List<TruResponse> responseList = new ArrayList<>();

        RecruiterLeadToLocalityService recruiterLeadToLocalityService = new RecruiterLeadToLocalityService();
        for(Object recruiterLeadToJobRoleRequest:adjustRequest((TruRequest) request)) {
            // create job role for this recruiter lead
            TruResponse recruiterLeadToJobRoleResponse = (TruResponse) super.createAsChild(recruiterLeadToJobRoleRequest,parent);
            responseList.add(recruiterLeadToJobRoleResponse);
            // create job location(s) for this job role
            List<TruResponse> recruiterLeadToLocalityResponseList = recruiterLeadToLocalityService.createAsChildren(recruiterLeadToJobRoleRequest,recruiterLeadToJobRoleResponse.getEntity());
            // set locality reference in job role
            setChildrenReference(recruiterLeadToLocalityResponseList,"recruiterLeadToLocalityList");
            responseList.addAll(recruiterLeadToLocalityResponseList);
        }

        Logger.info("Exiting "+this.getClass().getSimpleName()+" createAsChildren");

        return responseList;
    }

    @Override
    public List<Message> validateCreate(Model entity, List<Message> messageList) {
        if(((RecruiterLeadToJobRole)entity).getJobRole() == null){
            try {
                messageList.add(new Message(Message.MESSAGE_WARNING,"Please select a job role"));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return super.validateCreate(entity, messageList);
    }

    @Override
    public List<TruResponse> updateAsChildren(TruRequest request, Model parent) {
        Logger.info("Entering "+this.getClass().getSimpleName()+" updateAsChildren. Request class: "+request.getClass().getSimpleName());

        List<TruResponse> responseList = new ArrayList<>();

        RecruiterLeadToLocalityService recruiterLeadToLocalityService = new RecruiterLeadToLocalityService();
        for(Object recruiterLeadToJobRoleRequest:adjustRequest(request)) {

            // update job role for this recruiter lead
            copyChangedFields(request, (TruRequest) recruiterLeadToJobRoleRequest);
            TruResponse recruiterLeadToJobRoleResponse = (TruResponse) super.updateAsChild((TruRequest) recruiterLeadToJobRoleRequest,parent);
            if(isChanged(recruiterLeadToJobRoleResponse)){responseList.add(recruiterLeadToJobRoleResponse);}

            Logger.info(this.getClass().getSimpleName()+".updateAsChildren: After update, responseList has "+responseList.size()+" elements");

            if(recruiterLeadToJobRoleResponse != null){
                // update job location(s) for this job role
                List<TruResponse> recruiterLeadToLocalityResponseList = recruiterLeadToLocalityService.updateAsChildren((TruRequest) recruiterLeadToJobRoleRequest,recruiterLeadToJobRoleResponse.getEntity());
                responseList.addAll(recruiterLeadToLocalityResponseList);
                // set locality reference in job role
                setChildrenReference(recruiterLeadToLocalityResponseList,"recruiterLeadToLocalityList");
                //appendChildrenReference(recruiterLeadToLocalityService.filterNew(recruiterLeadToLocalityResponseList),"recruiterLeadToLocalityList");
            }

        }

        Logger.info("Exiting "+this.getClass().getSimpleName()+".updateAsChildren, responselist has "+responseList.size()+" elements");
        return responseList;
    }

    @Override
    public Model requestJoinEntity(String attributeName, TruRequest request) {
        Logger.info(this.getClass().getSimpleName()+".requestJoinEntity: attributeName="+attributeName+". request="+request.getClass().getSimpleName());
        switch(attributeName) {
            case "jobRole":
                Logger.info(this.getClass().getSimpleName()+".requestJoinEntity: attributeName="+attributeName+". val="+((RecruiterLeadToJobRoleRequest)request).getJobRole());
                return JobRole.find.where().eq("jobRoleId", ((RecruiterLeadToJobRoleRequest)request).getJobRole()).findUnique();
        }
        return null;
    }

    @Override
    public TruResponse delete(TruRequest request) {
        Logger.info("Entering "+this.getClass().getSimpleName()+" delete. Request class: "+request.getClass().getSimpleName());

        RecruiterLeadToJobRoleRequest recruiterLeadToJobRoleRequest = (RecruiterLeadToJobRoleRequest) copyDeleteFields(request, (TruRequest) adjustRequest(request).get(0));

        // deleting parent will delete everything
        if(isDeleteRelevant(recruiterLeadToJobRoleRequest)){
            Logger.info(this.getClass().getSimpleName()+".delete: Request class="+recruiterLeadToJobRoleRequest.getClass().getSimpleName());
            return super.delete(recruiterLeadToJobRoleRequest);
        }
        else {
            RecruiterLeadToLocalityService recruiterLeadToLocalityService = new RecruiterLeadToLocalityService();
            return recruiterLeadToLocalityService.delete(recruiterLeadToJobRoleRequest);
        }

    }

}
