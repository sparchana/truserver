package controllers.businessLogic;

import api.http.AddLeadRequest;
import api.http.AddLeadResponse;
import models.entity.Lead;

/**
 * Created by batcoder1 on 3/5/16.
 */
public class CandidateService {
    public static AddLeadResponse createLead(AddLeadRequest addLeadRequest){
        AddLeadResponse addLeadResponse = null;
        addLeadResponse = Lead.addLead(addLeadRequest);
        return addLeadResponse;
    }

}
