package controllers;

import api.http.httpRequest.search.SearchJobRequest;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import controllers.businessLogic.SearchJobService;
import models.entity.Static.JobRole;
import play.Logger;
import play.api.Play;
import play.mvc.Controller;
import play.mvc.Result;

import java.io.IOException;

import static play.libs.Json.toJson;

/**
 * Created by zero on 23/12/16.
 */
public class SearchController extends Controller {
    private static boolean isDevMode = Play.isDev(Play.current()) || Play.isTest(Play.current());

    public static Result renderSearch(String searchText) {
        return ok(views.html.Search.search.render());
    }

    public static Result getSearchSuggestion(String key) {
        return ok(toJson(JobRole.find.all()));
    }

    public static Result search() {
        JsonNode searchReq = request().body().asJson();
        Logger.info("Browser: " +  request().getHeader("User-Agent") + "; Req JSON : " + searchReq );
        if(searchReq == null) {
            return badRequest();
        }

        SearchJobRequest searchJobRequest= new SearchJobRequest();
        ObjectMapper newMapper = new ObjectMapper();

        try {
            searchJobRequest = newMapper.readValue(searchReq.toString(), SearchJobRequest.class);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // init search Service
        SearchJobService searchJobService = new SearchJobService();

        Logger.info(""+ toJson(searchJobRequest));
        return ok(toJson(searchJobService.searchJobs(searchJobRequest)));
    }
}
