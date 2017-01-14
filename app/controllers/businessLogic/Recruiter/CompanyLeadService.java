package controllers.businessLogic.Recruiter;

import api.http.httpRequest.Recruiter.CompanyLeadRequest;
import api.http.httpRequest.Recruiter.RecruiterLeadToJobRoleRequest;
import api.http.httpRequest.TruRequest;
import api.http.httpResponse.TruResponse;
import com.avaje.ebean.Model;
import controllers.TruService;
import models.entity.Static.CompanyType;
import models.entity.Static.JobRole;
import play.Logger;

/**
 * Created by User on 03-12-2016.
 */

public class CompanyLeadService extends TruService {

    @Override
    public String getEntityClassName() {
        return "models.entity.Recruiter.MO.CompanyLead";
    }

    @Override
    public Object create(Object request) {
        Logger.info("Entered "+this.getClass().getSimpleName()+" Create. Request class: "+request.getClass().getSimpleName());
        return super.create(adjustRequest((TruRequest) request).get(0));
    }

    @Override
    public TruResponse update(TruRequest request) {
        Logger.info("Entered "+this.getClass().getSimpleName()+" Update. Request class: "+request.getClass().getSimpleName());
        return super.update(copyChangedFields(request, (TruRequest) adjustRequest(request).get(0)));
    }

    @Override
    public TruResponse delete(TruRequest request) {
        Logger.info("Entered "+this.getClass().getSimpleName()+" Delete. Request class: "+request.getClass().getSimpleName());
        return super.delete(copyDeleteFields(request, (TruRequest) adjustRequest(request).get(0)));
    }

    @Override
    public Model requestJoinEntity(String attributeName, TruRequest request) {
        Logger.info(this.getClass().getSimpleName()+".requestJoinEntity: attributeName="+attributeName+". request="+request.getClass().getSimpleName());
        switch(attributeName) {
            case "companyLeadType":
                Logger.info(this.getClass().getSimpleName()+".requestJoinEntity: attributeName="+attributeName+". val="+((CompanyLeadRequest)request).getCompanyLeadType());
                return CompanyType.find.where().eq("companyTypeId", ((CompanyLeadRequest)request).getCompanyLeadType()).findUnique();
        }
        return null;
        //return super.requestJoinEntity(attributeName, request);
    }

}
