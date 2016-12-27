package controllers.businessLogic;

import api.http.httpRequest.CandidateResumeRequest;
import api.http.httpRequest.TruRequest;
import api.http.httpResponse.TruResponse;
import com.avaje.ebean.Model;
import controllers.TruService;
import models.entity.Candidate;

/**
 * Created by User on 24-12-2016.
 */
public class CandidateResumeService extends TruService {

    @Override
    public String getEntityClassName() {
        return "models.entity.OM.CandidateResume";
    }

    @Override
    public Object create(Object request) {
        TruResponse candidateResumeResponse = (TruResponse) super.create(request);
        // save, if allowed
        if(checkSaveAllowed(candidateResumeResponse)) {
            save();
            candidateResumeResponse.setStatus(TruResponse.STATUS_SUCCESS);
        }

        return candidateResumeResponse;
    }

    @Override
    public Model requestJoinEntity(String attributeName, TruRequest request) {
        switch(attributeName) {
            case "candidate":
                return Candidate.find.where().eq("candidateId", ((CandidateResumeRequest)request).getCandidate()).findUnique();
        }
        return null;
    }

    @Override
    public TruResponse update(TruRequest request) {
        TruResponse candidateResumeResponse = (TruResponse) super.update(request);
        // save, if allowed
        if(checkSaveAllowed(candidateResumeResponse)) {
            save();
            candidateResumeResponse.setStatus(TruResponse.STATUS_SUCCESS);
        }

        return candidateResumeResponse;
    }

}
