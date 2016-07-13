package controllers.AnalyticsLogic;


import api.http.httpRequest.AnalyticsRequest;
import api.http.httpResponse.GlobalAnalyticsResponse;
import com.avaje.ebean.Query;
import models.entity.Candidate;
import play.Logger;

import java.util.List;

/**
 * Created by zero on 12/7/16.
 */
public class GlobalAnalyticsService {
    public static GlobalAnalyticsResponse getGlobalStatsService(AnalyticsRequest analyticsRequest){
        GlobalAnalyticsResponse globalAnalyticsResponse = new GlobalAnalyticsResponse();
        Query<Candidate> query = Candidate.find.query();

        if(analyticsRequest.getFromThisDate() != null) {
            query = query.where()
                    .ge("candidateCreateTimestamp", analyticsRequest.getFromThisDate())
                    .query();
        }
        if(analyticsRequest.getToThisDate() != null) {
            query = query.where()
                    .le("candidateCreateTimestamp", analyticsRequest.getToThisDate())
                    .query();
        }

        List<Candidate> candidateResponseList = query.findList();
        globalAnalyticsResponse.setTotalNumberOfCandidate(candidateResponseList.size());
        if(candidateResponseList.size() <1){
            Logger.info("Search Response empty");
        }
        return globalAnalyticsResponse;
    }
}
