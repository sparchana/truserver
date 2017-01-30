package controllers.businessLogic;

import api.http.httpRequest.CandidateResumeRequest;
import api.http.httpRequest.TruRequest;
import api.http.httpResponse.TruResponse;
import com.avaje.ebean.Model;
import controllers.TruService;
import models.entity.Candidate;
import models.entity.OM.CandidateResume;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
            if(save()) candidateResumeResponse.setStatus(TruResponse.STATUS_SUCCESS);
            else candidateResumeResponse.setStatus(TruResponse.STATUS_FAILURE);
        }

        return candidateResumeResponse;
    }

    @Override
    public TruResponse delete(TruRequest request) {
        return super.delete(request);
    }

    public TruResponse fetchLatestResumeForCandidate(String candidateId) {
        List<Map<String, String>> params = new ArrayList<>();
        Map<String, String> param = new HashMap<>();

        param.put("candidate", candidateId);
        params.add(param);
        List<TruResponse> candidateResumeList = new ArrayList<>();
        candidateResumeList = readByAttribute(params,"createTimestamp","DESC");
        if(candidateResumeList.size() > 0) return candidateResumeList.get(0);
        else return null;
    }

}
