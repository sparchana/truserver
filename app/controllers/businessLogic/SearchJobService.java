package controllers.businessLogic;

import api.http.httpRequest.search.SearchJobRequest;
import api.http.httpResponse.search.SearchJobResponse;
import api.http.httpResponse.search.helper.SearchParamsResponse;
import models.entity.Static.Education;
import models.entity.Static.Experience;
import models.entity.Static.Locality;
import play.Logger;

import java.util.Arrays;
import java.util.List;

/**
 * Created by zero on 24/12/16.
 */
public class SearchJobService {

    public SearchJobResponse searchJobs(SearchJobRequest request) {
        SearchJobResponse response = new SearchJobResponse();
        // figure out keyword from the list

        if (request.getSearchParamRequest() != null) {
            SearchParamsResponse searchParamsResponse = new SearchParamsResponse();
            searchParamsResponse.setSearchKeywords(request.getSearchParamRequest().getKeywordList());
            searchParamsResponse
                    .setPositiveKeywords(
                            segregateKeywords(
                            request.getSearchParamRequest().getKeywordList()).getPositiveKeywords());
            searchParamsResponse
                    .setNegativeKeywords(
                            segregateKeywords(
                            request.getSearchParamRequest().getKeywordList()).getNegativeKeywords());

            Logger.info("keywordList: size: " + searchParamsResponse.getPositiveKeywords().size());


            // determine locality and append it to response
            if(request.getSearchParamRequest().getLocationName() != null) {

                searchParamsResponse.setLocality(
                                     determineLocality(request.getSearchParamRequest().getLocationName()));
            }

            // determine education and append it to response
            if(request.getSearchParamRequest().getEducationText() != null) {

                searchParamsResponse.setEducation(
                        determineEducation(request.getSearchParamRequest().getEducationText()));
            }

            // determine experience and append it to response
            if(request.getSearchParamRequest().getExperienceText() != null) {

                searchParamsResponse.setExperience(
                        determineExperience(request.getSearchParamRequest().getExperienceText()));
            }


            response.setSearchParams(searchParamsResponse);
        }
        return response;
    }

    public Experience determineExperience(String experienceText) {
        if (experienceText != null || !experienceText.trim().isEmpty()) {
            List<String> stringList = Arrays.asList(experienceText.split("\\s+"));
            if(stringList.size() > 2){
                experienceText = stringList.get(stringList.size()-2) + " " + stringList.get(stringList.size()-1);
            }
            return Experience.find.where().ilike("experienceType", "%" + experienceText).setMaxRows(1).findUnique();
        }
        return null;
    }

    public Education determineEducation(String educationText) {
        if (educationText != null || !educationText.trim().isEmpty()) {
            return Education.find.where().ilike("educationName", educationText + "%").setMaxRows(1).findUnique();
        }
        return null;
    }

    public Locality determineLocality(String localityStr) {
        if (localityStr != null || !localityStr.trim().isEmpty()) {
            return Locality.find.where().ilike("localityName", localityStr + "%").setMaxRows(1).findUnique();
        }
        return null;
    }

    private static SearchParamsResponse segregateKeywords(List<String> keywordList) {
        // positive keyword for which there are no match and negative keyword for which there is a match
        SearchParamsResponse response = new SearchParamsResponse();

        // TODO determine positive keywords and negative keywords here and append accordingly
        Logger.info("keywordList: size: " + keywordList.size());
        response.setNegativeKeywords(keywordList);
        response.setPositiveKeywords(keywordList);

        return response;
    }


}
