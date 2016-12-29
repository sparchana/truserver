package controllers.businessLogic;

import api.ServerConstants;
import api.http.httpRequest.search.SearchJobRequest;
import api.http.httpResponse.JobPostResponse;
import api.http.httpResponse.search.SearchJobResponse;
import api.http.httpResponse.search.helper.SearchParamsResponse;
import models.entity.Candidate;
import models.entity.OM.LanguageKnown;
import models.entity.Static.Education;
import models.entity.Static.Experience;
import models.entity.Static.Locality;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by zero on 24/12/16.
 */
public class SearchJobService {

    public SearchJobResponse searchJobs(SearchJobRequest request, Long candidateId) {
        SearchJobResponse response = new SearchJobResponse();
        // figure out keyword from the list

        if (request.getSearchParamRequest() != null) {
            SearchParamsResponse searchParamsResponse = new SearchParamsResponse();
            searchParamsResponse
                    .setSearchKeywords(
                            segregateKeywords(
                            request.getSearchParamRequest().getKeywordList()).getSearchKeywords());
            searchParamsResponse
                    .setPositiveKeywords(
                            segregateKeywords(
                            request.getSearchParamRequest().getKeywordList()).getPositiveKeywords());
            searchParamsResponse
                    .setNegativeKeywords(
                            segregateKeywords(
                            request.getSearchParamRequest().getKeywordList()).getNegativeKeywords());


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

            // if candidate has logged in then default search should be hooked with its preference
            Candidate candidate = null;
            JobPostResponse jobPostResponse;

            if(candidateId != null) {
                candidate = Candidate.find.where().eq("candidateId", candidateId).findUnique();
            }
            // when no search params provided, and candidate is logged in, it returns
            // data filtered by candidates preference
            if (candidate != null &&
                    (searchParamsResponse.getSearchKeywords() == null
                    || searchParamsResponse.getSearchKeywords().size() == 0)
                    && searchParamsResponse.getLocality() == null
                    && searchParamsResponse.getEducation() == null
                    && searchParamsResponse.getExperience() == null
                    && request.getSortParamRequest().getSortBy() == null
                    && (request.getFilterParamRequest().getSelectedGender() == null
                    && request.getFilterParamRequest().getSelectedLanguageIdList().size() == 0
            )) {
                // overriding gender filter
                request.getFilterParamRequest().setSelectedGender(candidate.getCandidateGender());

                List<Long> languageIdList = new ArrayList<>();
                for (LanguageKnown languageKnown : candidate.getLanguageKnownList()) {
                    languageIdList.add(Long.valueOf(languageKnown.getLanguage().getLanguageId()));
                }
                // overriding language filter
                request.getFilterParamRequest().setSelectedLanguageIdList(languageIdList);

                jobPostResponse = JobSearchService
                        .queryAndReturnJobPosts(searchParamsResponse.getSearchKeywords(),
                                candidate.getLocality(),
                                candidate.getCandidateEducation().getEducation(),
                                searchParamsResponse.getExperience(),
                                request.getSortParamRequest().getSortBy(),
                                true,
                                null,
                                request.getPage(),
                                request.getFilterParamRequest());
            } else {
                jobPostResponse = JobSearchService
                        .queryAndReturnJobPosts(searchParamsResponse.getSearchKeywords(),
                                searchParamsResponse.getLocality(),
                                searchParamsResponse.getEducation(),
                                searchParamsResponse.getExperience(),
                                request.getSortParamRequest().getSortBy(),
                                true,
                                null,
                                request.getPage(),
                                request.getFilterParamRequest());
            }

            response.setSearchParams(searchParamsResponse);

            response.setResults(jobPostResponse);
            response.setPage(request.getPage());

            // create interaction params
            StringBuilder result = new StringBuilder();
            result.append("Search for ");
            result.append(StringUtils.join(", ",searchParamsResponse.getSearchKeywords()) + " jobs ");
            result.append("@" + (searchParamsResponse.getLocality() != null ? searchParamsResponse.getLocality().getLocalityName() : "All Bangalore"));
            result.append(" with filter ");
            result.append(" Edu: " + (searchParamsResponse.getEducation() != null ? searchParamsResponse.getEducation().getEducationName() : " ANY_EDUCATION"));
            result.append(" Exp: " + (searchParamsResponse.getExperience() != null ? searchParamsResponse.getExperience().getExperienceType() : " ANY_EXPERIENCE"));


            if(request.getFilterParamRequest() != null){
                String gender = "";
                if(request.getFilterParamRequest().getSelectedGender() == null
                        || request.getFilterParamRequest().getSelectedGender() == ServerConstants.GENDER_ANY){
                    gender = "ANY_GENDER";
                } else {
                   gender = request.getFilterParamRequest().getSelectedGender()
                            == ServerConstants.GENDER_MALE ? "MALE": "FEMALE";
                }
                result.append(" Gender: " + (request.getFilterParamRequest() != null ? gender : " ANY_GENDER"));
                result.append(" Language: " + (request.getFilterParamRequest() != null ? gender : " ANY_GENDER"));
            }

            String objectAUUID;
            if (candidateId == null) {
                objectAUUID = ServerConstants.TRU_WEB_NOT_LOGGED_UUID;
            } else {
                if(candidate != null){
                    objectAUUID = candidate.getCandidateUUId();
                } else {
                    objectAUUID = ServerConstants.TRU_WEB_NOT_LOGGED_UUID;
                }
            }


            // create interaction
            InteractionService.createInteractionForWebSearch(objectAUUID, result.toString());
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
        List<String> refinedKeywordList = new ArrayList<>();
        for(String keyword: keywordList){
            if(!(keyword == null || keyword.trim().isEmpty())){
                refinedKeywordList.add(keyword);
            }
        }

        // TODO determine positive keywords and negative keywords here and append accordingly
        response.setNegativeKeywords(new ArrayList<>());
        response.setPositiveKeywords(refinedKeywordList);
        response.setSearchKeywords(refinedKeywordList);
        return response;
    }


}
