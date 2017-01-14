package controllers.businessLogic.Recruiter;

import api.http.httpRequest.Recruiter.RecruiterLeadRequest;
import api.http.httpRequest.Recruiter.RecruiterLeadToJobRoleRequest;
import api.http.httpRequest.Recruiter.RecruiterLeadToLocalityRequest;
import api.http.httpRequest.TruRequest;
import api.http.httpResponse.Recruiter.RecruiterLeadResponse;
import api.http.httpResponse.TruResponse;
import com.avaje.ebean.Model;
import controllers.TruService;
import models.entity.Recruiter.OM.RecruiterLeadToJobRole;
import models.entity.Recruiter.OM.RecruiterLeadToLocality;
import models.entity.Static.JobRole;
import models.entity.Static.Locality;
import play.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by User on 24-11-2016.
 */
public class RecruiterLeadToLocalityService extends TruService {
    @Override
    public String getEntityClassName() {
        return "models.entity.Recruiter.OM.RecruiterLeadToLocality";
    }

    @Override
    public List<TruResponse> createAsChildren(Object request, Model parent) {
        Logger.info("Entering "+this.getClass().getSimpleName()+" createAsChildren");

        List<TruResponse> responseList = new ArrayList<>();

        // get all locations for this job role
        for(Object each:adjustRequest((TruRequest) request)){
            if(((RecruiterLeadToLocalityRequest)each).getLocality() > 0){
                // create job location for this job role
                TruResponse recruiterLeadToLocationResponse = (TruResponse) super.createAsChild(each,parent);
                responseList.add(recruiterLeadToLocationResponse);
            }
        }

        Logger.info("Exiting "+this.getClass().getSimpleName()+" createAsChildren");

        return responseList;
    }

    @Override
    public List<TruResponse> updateAsChildren(TruRequest request, Model parent) {
        Logger.info("Entering "+this.getClass().getSimpleName()+" updateAsChildren");
        Logger.info("Request="+request.getClass().getSimpleName()+", parent="+parent.getClass().getSimpleName());

        List<TruResponse> responseList = new ArrayList<>();

        // get all locations for this job role
        for(Object each:adjustRequest(request)){
            Logger.info("Each request="+each.getClass().getSimpleName());
            // update job location for this job role
            copyChangedFields(request, (TruRequest) each);
            TruResponse recruiterLeadToLocationResponse = (TruResponse) super.updateAsChild((TruRequest) each,parent);
            Logger.info("Each response="+recruiterLeadToLocationResponse.getClass().getSimpleName());
            if(isChanged(recruiterLeadToLocationResponse)){responseList.add(recruiterLeadToLocationResponse);}
        }

        Logger.info("Exiting "+this.getClass().getSimpleName()+" updateAsChildren. responseList has "+responseList.size()+" elements");

        return responseList;
    }

    @Override
    public Model requestJoinEntity(String attributeName, TruRequest request) {
        Logger.info(this.getClass().getSimpleName()+".requestJoinEntity: attributeName="+attributeName+". request="+request.getClass().getSimpleName());
        switch(attributeName) {
            case "locality":
                return Locality.find.where().eq("localityId", ((RecruiterLeadToLocalityRequest)request).getLocality()).findUnique();
        }
        return null;
    }

    @Override
    public TruResponse delete(TruRequest request) {
        Logger.info("Entering "+this.getClass().getSimpleName()+" delete. Request class: "+request.getClass().getSimpleName());
        TruRequest recruiterLeadToJobLocationRequest = copyDeleteFields(request, (TruRequest) adjustRequest(request).get(0));
        Logger.info(this.getClass().getSimpleName()+".delete: Request class="+recruiterLeadToJobLocationRequest.getClass().getSimpleName());
        return super.delete(recruiterLeadToJobLocationRequest);
    }

}
