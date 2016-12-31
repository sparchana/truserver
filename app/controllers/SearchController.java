package controllers;

import api.ServerConstants;
import api.http.httpRequest.search.SearchJobRequest;
import com.avaje.ebean.Expr;
import com.avaje.ebean.ExpressionList;
import com.avaje.ebean.Junction;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import controllers.businessLogic.SearchJobService;
import models.entity.Company;
import models.entity.JobPost;
import models.entity.Static.JobRole;
import org.apache.commons.lang3.text.WordUtils;
import play.Logger;
import play.api.Play;
import play.mvc.Controller;
import play.mvc.Result;

import java.io.IOException;
import java.util.*;

import static play.libs.Json.toJson;

/**
 * Created by zero on 23/12/16.
 */
public class SearchController extends Controller {
    private static boolean isDevMode = Play.isDev(Play.current()) || Play.isTest(Play.current());

    public static Result renderSearch(String searchText) {
        if(searchText != null && !searchText.trim().isEmpty()) {
            searchText = searchText.replaceAll("[^A-Za-z0-9 ]", " ");
            searchText = WordUtils.capitalize(searchText);
            Logger.info("search Text url " + searchText);
        } else {
            searchText = "Jobs In Bangalore";
        }
        return ok(views.html.Search.search.render(searchText));
    }

    public static Result getSearchSuggestion(String key) {

        /* TODO find a way to remove already added keys from the next suggestion list
        *  currently this is being done on js side.
        * */
        List<String> keywordList = new ArrayList<>();
        Map<String, String> suggestionMap = new LinkedHashMap<>();
        if(key != null && key.trim().isEmpty()) {
            if(!key.contains(",")){
                key+= ",";
            }
            keywordList = Arrays.asList(key.split(", "));
        }

        // job role name match
        ExpressionList<JobRole> jobroleEL = JobRole.find.where();
        Junction<JobRole> jobRoleJunction = jobroleEL.disjunction();
        for(String keyword : keywordList) {
            if(keyword.replace(" ", "").trim().isEmpty()){
                continue;
            }
            jobRoleJunction.add(Expr.like("jobName", keyword + "%"));
        }
        jobRoleJunction.endJunction();

        // extract string from  the list
        for(JobRole jobRole:  jobroleEL.findList()){
            suggestionMap.putIfAbsent(jobRole.getJobName(), jobRole.getJobName());
        }


        // company name match || query to match against company with source: Internal
        // and status: Active

        ExpressionList<Company> companyEL = Company.find.where();
        Junction<Company> companyJunction = companyEL.disjunction();
        for(String keyword : keywordList){
            if(keyword.replace(" ", "").trim().isEmpty()){
                continue;
            }
            companyJunction.add(
                    Expr.and(
                            Expr.eq("CompStatus", ServerConstants.COMPANY_STATUS_ACTIVE),
                            Expr.and(
                                    Expr.eq("Source", ServerConstants.SOURCE_INTERNAL),
                                    Expr.like("companyName", keyword + "%")
                            )
                    )
            );

        }
        companyJunction.endJunction();

        // extract string from  the list
        for(Company company:  companyEL.findList()){
            suggestionMap.putIfAbsent(company.getCompanyName(), company.getCompanyName());
        }

        // jobPostTitle name match || query to match against job with source: Internal
        // and status: Active
        ExpressionList<JobPost> jobPostEL = JobPost.find.where();
        Junction<JobPost> jobPostJunction = jobPostEL.disjunction();
        for(String keyword : keywordList){
            if(keyword.replace(" ", "").trim().isEmpty()){
                continue;
            }
            companyJunction.add(
                    Expr.and(
                            Expr.eq("Source", ServerConstants.SOURCE_INTERNAL),
                            Expr.and(
                                    Expr.eq("JobStatus", ServerConstants.JOB_STATUS_ACTIVE),
                                    Expr.like("jobPostTitle", keyword + "%")
                            )
                    )
            );
        }
        jobPostJunction.endJunction();

        // extract string from  the list
        for(JobPost jobPost:  jobPostEL.findList()) {
            suggestionMap.putIfAbsent(jobPost.getJobPostTitle(), jobPost.getJobPostTitle());
        }

        return ok(toJson(suggestionMap));
    }

    public static Result search(Integer page) {
        JsonNode searchReq = request().body().asJson();
        Logger.info("Browser: " +  request().getHeader("User-Agent") + "; Req JSON : " + searchReq );
        if(searchReq == null) {
            return badRequest();
        }

        SearchJobRequest searchJobRequest = new SearchJobRequest();
        ObjectMapper newMapper = new ObjectMapper();

        try {
            searchJobRequest = newMapper.readValue(searchReq.toString(), SearchJobRequest.class);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // can also pass this as param
        if(page != null)  {
            searchJobRequest.setPage(page);
        } else {
            searchJobRequest.setPage(1);
        }
        // init search Service
        SearchJobService searchJobService = new SearchJobService();

        Logger.info(""+ toJson(searchJobRequest));
        Long cId = null;
        if(( session().get("candidateId") != null)){
            cId = Long.valueOf(session().get("candidateId"));
        }
        return ok(toJson(searchJobService.searchJobs(searchJobRequest, cId)));
    }
}
