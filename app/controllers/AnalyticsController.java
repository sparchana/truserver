package controllers;

import api.http.httpRequest.AnalyticsRequest;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import controllers.AnalyticsLogic.GlobalAnalyticsService;
import play.Logger;
import play.mvc.Controller;
import play.mvc.Result;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static play.libs.Json.toJson;

/**
 * Created by zero on 12/7/16.
 */
public class AnalyticsController extends Controller {

    public static Result alphaHandler(Integer vId) {

        JsonNode analyticsJsonNode = request().body().asJson();
        ObjectMapper newMapper = new ObjectMapper();
        AnalyticsRequest analyticsRequest = new AnalyticsRequest();
        try {
            analyticsRequest = newMapper.readValue(analyticsJsonNode.toString(), AnalyticsRequest.class);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if(analyticsJsonNode == null)
        {
            return badRequest();
        } else {
            Logger.info("FromDate" + analyticsRequest.getFromThisDate());
            Logger.info("toDate" + analyticsRequest.getToThisDate());
            if(analyticsRequest.getMetrics() != null){
                Logger.info("sizeOfMetricsArray" + analyticsRequest.getMetrics().size());
            }
        }

        switch (vId) {
            case 1:
                return ok(toJson(GlobalAnalyticsService.getGlobalStatsService(analyticsRequest)));
            case 2:
                // other analytics
                Date sd = analyticsRequest.getFromThisDate();
                Date ed = analyticsRequest.getToThisDate();
                Boolean shouldUploadToGs = false;
                if(analyticsRequest.getUpdateGoogleSheet() != null && analyticsRequest.getUpdateGoogleSheet() ){
                    //shouldUploadToGs = true;
                }

                List<String> headerList = new ArrayList<>();
                if(analyticsRequest.getMetrics() != null && !analyticsRequest.getMetrics().isEmpty()){
                    headerList.addAll(analyticsRequest.getMetrics());
                } else {
                    headerList.add(MetricsConstants.METRIC_INPUT_SUMMARY);
                    headerList.add(MetricsConstants.METRIC_INPUT_SUPPORT);
                    headerList.add(MetricsConstants.METRIC_INPUT_LEAD_SOURCES);
                    headerList.add(MetricsConstants.METRIC_INPUT_ACTIVE_CANDIDATES);
                }

                Map<String, Map<Date, Map<Integer, Map<String, Object>>>> mapOfHeaderMap =
                        MetricsQueryService.queryAndUpdateMetrics(headerList, sd, ed, shouldUploadToGs);
                //Logger.info("Metrics Query JSON Result:" + toJson(mapOfHeaderMap));
                return ok(toJson(mapOfHeaderMap));
        }
        return ok("test");
    }
}
