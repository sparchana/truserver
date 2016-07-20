package controllers.businessLogic;

import api.ServerConstants;
import api.http.httpRequest.DeactivatedCandidateRequest;
import api.http.httpRequest.DeactiveToActiveRequest;
import api.http.httpResponse.DeactiveToActiveResponse;
import com.avaje.ebean.Query;
import models.entity.Candidate;
import models.entity.OO.CandidateStatusDetail;
import models.entity.Static.CandidateProfileStatus;
import play.Logger;

import java.util.List;

/**
 * Created by zero on 19/7/16.
 */
public class DeactivationService {
    public static List<Candidate> getDeactivatedCandidates(DeactivatedCandidateRequest deactivatedCandidateRequest) {
        Query<Candidate> query = Candidate.find.query();
        query = query.select("*").fetch("candidateprofilestatus")
                .where()
                .eq("candidateprofilestatus.profileStatusId", ServerConstants.CANDIDATE_STATE_DEACTIVE)
                .query();
        if(deactivatedCandidateRequest.getFromThisDate() != null) {
            query = query.select("*").fetch("candidateStatusDetail")
                    .where()
                    .ge("candidateStatusDetail.statusExpiryDate", deactivatedCandidateRequest.getFromThisDate())
                    .query();
        }
        if(deactivatedCandidateRequest.getToThisDate() != null) {
            query = query.select("*").fetch("candidateStatusDetail")
                    .where()
                    .le("candidateStatusDetail.statusExpiryDate", deactivatedCandidateRequest.getToThisDate())
                    .query();
        }

        List<Candidate> deactivatedCandidateList = query.findList();
        if(deactivatedCandidateList.size() < 1) {
            Logger.info("deactivatedCandidateList empty for specified period");
        }
        return deactivatedCandidateList;
    }

    public static DeactiveToActiveResponse deactivateToActive(DeactiveToActiveRequest deactiveToActiveRequest) {
        DeactiveToActiveResponse response = new DeactiveToActiveResponse();
        if(deactiveToActiveRequest.getDeactiveToActiveList()!= null && !deactiveToActiveRequest.getDeactiveToActiveList().isEmpty()){
            Query<Candidate> query = Candidate.find.query();
            List<Long> leadList = deactiveToActiveRequest.getDeactiveToActiveList();
            query = query.select("*").fetch("lead")
                        .where()
                        .eq("candidateprofilestatus.profileStatusId", ServerConstants.CANDIDATE_STATE_DEACTIVE)
                        .in("lead.leadId", leadList)
                        .query();
            List<Candidate> candidateList = query.findList();
            CandidateProfileStatus active = CandidateProfileStatus.find.where().eq("profileStatusId", ServerConstants.CANDIDATE_STATE_ACTIVE).findUnique();
            for(Candidate candidate: candidateList){
                candidate.setCandidateprofilestatus(active);
                Integer candidateStatusDetailId = candidate.getCandidateStatusDetail().getCandidateStatusDetailId();
                candidate.setCandidateStatusDetail(null);
                candidate.candidateUpdate();
                // remove candidate deactivation residue
                CandidateStatusDetail candidateStatusDetail = CandidateStatusDetail.find.where().eq("candidateStatusDetailId", candidateStatusDetailId).findUnique();
                candidateStatusDetail.delete();
            }
            response.setCandidateList(candidateList);
            response.setStatus(DeactiveToActiveResponse.STATUS_SUCCESS);
        }else {
            response.setStatus(DeactiveToActiveResponse.STATUS_FAILURE);
        }

        return response;
    }
}
