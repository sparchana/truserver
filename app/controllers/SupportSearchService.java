package controllers;

import api.http.httpRequest.SearchCandidateRequest;
import com.avaje.ebean.Query;
import models.entity.Candidate;
import play.Logger;

import java.util.List;

/**
 * Created by zero on 6/7/16.
 */
public class SupportSearchService {
    public static List<Candidate> searchCandidateBySupport(SearchCandidateRequest searchCandidateRequest) {
        // TODO:check searchCandidateRequest member variable for special char, null value
        List<Integer> jobInterestIdList = searchCandidateRequest.candidateJobInterest;
        List<Integer> localityPreferenceIdList = searchCandidateRequest.candidateLocality;

        // Logger.info("fromdate :" + searchCandidateRequest.getFromThisDate().getTime() + "-" + " toThisDate" + searchCandidateRequest.getToThisDate().getTime());
        Query<Candidate> query = Candidate.find.query();

        if(jobInterestIdList != null && jobInterestIdList.get(0) != null) {
            query = query.select("*").fetch("jobPreferencesList")
                    .where()
                    .in("jobPreferencesList.jobRole.jobRoleId", jobInterestIdList)
                    .query();
        }
        if(localityPreferenceIdList != null && localityPreferenceIdList.get(0) != null) {
            query = query.select("*").fetch("localityPreferenceList")
                    .where()
                    .in("localityPreferenceList.locality.localityId", localityPreferenceIdList)
                    .query();
        }
        if(searchCandidateRequest.getCandidateFirstName() != null && !searchCandidateRequest.getCandidateFirstName().isEmpty()) {
            query = query.where().like("candidateFirstName",
                    searchCandidateRequest.getCandidateFirstName() + "%").query();
        }

        if(searchCandidateRequest.getCandidateMobile() != null && !searchCandidateRequest.getCandidateMobile().isEmpty()) {
            query = query
                    .where()
                    .in("candidateMobile", searchCandidateRequest.getCandidateMobile())
                    .query();
        }
        if(searchCandidateRequest.getFromThisDate() != null) {
            query = query.where()
                    .ge("candidateCreateTimestamp", searchCandidateRequest.getFromThisDate())
                    .query();
        }
        if(searchCandidateRequest.getToThisDate() != null) {
            query = query.where()
                    .le("candidateCreateTimestamp", searchCandidateRequest.getToThisDate())
                    .query();
        }
        if(searchCandidateRequest.getLanguageKnownList() != null && !searchCandidateRequest.getLanguageKnownList().isEmpty()) {
            List<Integer> languageIdList = searchCandidateRequest.getLanguageKnownList();
            query = query.select("*").fetch("languageKnownList")
                    .where()
                    .in("languageKnownList.language.languageId", languageIdList)
                    .query();
        }

        Logger.info("fromThisDate+ " +searchCandidateRequest.getFromThisDate());

        List<Candidate> candidateResponseList = query.findList();
        if(candidateResponseList.size() <1){
            Logger.info("Search Response empty");
        }
        return candidateResponseList;
    }
}
