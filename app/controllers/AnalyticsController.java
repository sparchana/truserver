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
        Logger.info("json:- " + request().body().asJson());
        JsonNode analyticsJsonNode = request().body().asJson();
        ObjectMapper newMapper = new ObjectMapper();
        AnalyticsRequest analyticsRequest = new AnalyticsRequest();
        try {
            analyticsRequest = newMapper.readValue(analyticsJsonNode.toString(), AnalyticsRequest.class);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if(analyticsJsonNode == null){
            return badRequest();
        }

        switch (vId) {
            case 2:
                return ok(toJson(GlobalAnalyticsService.getGlobalStatsService(analyticsRequest)));
            case 1:
                // other analytics
                Logger.info("testing metrice service");
                Date sd = analyticsRequest.getFromThisDate();
                Date ed = analyticsRequest.getToThisDate();

                List<String> headerList = new ArrayList<String>();
                headerList.add(MetricsConstants.METRIC_INPUT_ALL);
                headerList.add(MetricsConstants.METRIC_INPUT_SUPPORT);
                headerList.add(MetricsConstants.METRIC_INPUT_LEAD_SOURCES);

                Map<String, Map<String, Object>> mapOfHeaderMap = MetricsQueryService.queryAndUpdateLeads(headerList, sd, ed, false);
                Logger.info("mapofHeaderMap:" + toJson(mapOfHeaderMap));
                return ok(toJson(mapOfHeaderMap));
        }
        return ok("test");
    }
}
