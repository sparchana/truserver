package controllers.businessLogic.Recruiter;

import controllers.TruService;

/**
 * Created by User on 29-11-2016.
 */
public class RecruiterLeadStatusService extends TruService {

    @Override
    public String getEntityClassName() {
        return "models.entity.Recruiter.Static.RecruiterLeadStatus";
    }

}
