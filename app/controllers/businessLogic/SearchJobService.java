package controllers.businessLogic;

import api.ServerConstants;
import api.http.httpRequest.search.SearchJobRequest;
import api.http.httpResponse.JobPostResponse;
import api.http.httpResponse.search.SearchJobResponse;
import api.http.httpResponse.search.helper.FilterParamsResponse;
import api.http.httpResponse.search.helper.SearchParamsResponse;
import dao.JobPostWorkFlowDAO;
import models.entity.Candidate;
import models.entity.JobPost;
import models.entity.OM.JobPostWorkflow;
import models.entity.OM.JobPreference;
import models.entity.OM.LanguageKnown;
import models.entity.Static.*;
import org.apache.commons.lang3.StringUtils;
import play.Logger;

import java.util.*;

/**
 * Created by zero on 24/12/16.
 */
public class SearchJobService {

    public SearchJobResponse searchJobs(SearchJobRequest request, Long candidateId) {
        SearchJobResponse response = new SearchJobResponse();
        // figure out keyword from the list
        boolean isUrlInvalid = false;

        SearchParamsResponse searchParamsResponse = new SearchParamsResponse();
        FilterParamsResponse filterParamsResponse = new FilterParamsResponse();
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
        if (request.getSearchParamRequest().getLocationName() != null) {

            searchParamsResponse.setLocality(
                    determineLocality(request.getSearchParamRequest().getLocationName()));
            if(searchParamsResponse.getLocality() == null) {
                isUrlInvalid = true;
            }
        }

        // determine education and append it to response
        if (request.getSearchParamRequest().getEducationText() != null) {

            searchParamsResponse.setEducation(
                    determineEducation(request.getSearchParamRequest().getEducationText()));

            if(searchParamsResponse.getEducation() == null) {
                isUrlInvalid = true;
            }
        }

        // if req was not null and static table doesn't have it , that means req is invalid
        response.setURLInvalid(isUrlInvalid);

        // determine experience and append it to response
        if (request.getSearchParamRequest().getExperienceText() != null) {

            searchParamsResponse.setExperience(
                    determineExperience(request.getSearchParamRequest().getExperienceText()));

            if(searchParamsResponse.getExperience() == null) {
                isUrlInvalid = true;
            }
        }

        // if candidate has logged in then default search should be hooked with its preference
        Candidate candidate = null;
        JobPostResponse jobPostResponse;

        List<Language> languageList = new ArrayList<>();
        List<String> languageStringList = new ArrayList<>();

        if (candidateId != null) {
            response.setUserLoggedIn(true);
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
                && (request.getFilterParamRequest().getSelectedGender() == null
                && request.getFilterParamRequest().getSelectedLanguageIdList().size() == 0))
        {

            // overriding gender filter & prep response filter object
            request.getFilterParamRequest().setSelectedGender(candidate.getCandidateGender());
            List<Long> languageIdList = new ArrayList<>();
            for (LanguageKnown languageKnown : candidate.getLanguageKnownList()) {
                languageIdList.add(Long.valueOf(languageKnown.getLanguage().getLanguageId()));
                languageList.add(languageKnown.getLanguage());
                languageStringList.add(languageKnown.getLanguage().getLanguageName());
            }

            // candidate job role
            List<String> jobRoleNameList = new ArrayList<>();
            for(JobPreference jobPreference: candidate.getJobPreferencesList()){
                String jobRoleName = jobPreference.getJobRole().getJobName().replaceAll("[^\\w\\s]"," ").toLowerCase();
                jobRoleNameList.addAll(Arrays.asList(jobRoleName.split("\\s+")));
            }
            request.getSearchParamRequest().setKeywordList(jobRoleNameList);
            searchParamsResponse.setSearchKeywords(jobRoleNameList);

            // overriding language filter
            request.getFilterParamRequest().setSelectedLanguageIdList(languageIdList);

            // override response
            searchParamsResponse.setLocality(candidate.getLocality());
            searchParamsResponse.setEducation(candidate.getCandidateEducation().getEducation());

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

        if (languageList.isEmpty()
                && filterParamsResponse != null
                && filterParamsResponse.getLanguageList() != null
                && !filterParamsResponse.getLanguageList().isEmpty()) {
            languageList = new ArrayList<>();
            languageList.addAll(
                    Language.find.where().in("languageId", request.getFilterParamRequest().getSelectedLanguageIdList()).findList());

            for (Language language : languageList) {
                languageStringList.add(language.getLanguageName());
            }
        }
        if (request.getFilterParamRequest() != null) {
            filterParamsResponse.setLanguageList(languageList);
            if (request.getFilterParamRequest().getSelectedGender() != null)
                filterParamsResponse.setGender(request.getFilterParamRequest().getSelectedGender());
        }

        response.setSearchParams(searchParamsResponse);
        response.setFilterParams(filterParamsResponse);

        response.setResults(jobPostResponse);
        response.setPage(request.getPage());

        // create interaction params
        StringBuilder result = new StringBuilder();
        result.append("Search for ");
        result.append(StringUtils.join(", ", searchParamsResponse.getSearchKeywords()) + " jobs ");
        result.append("@" + (searchParamsResponse.getLocality() != null ? searchParamsResponse.getLocality().getLocalityName() : "All Bangalore"));
        result.append(" with filter ");
        result.append(" Edu: " + (searchParamsResponse.getEducation() != null ? searchParamsResponse.getEducation().getEducationName() : " ANY_EDUCATION"));
        result.append(" Exp: " + (searchParamsResponse.getExperience() != null ? searchParamsResponse.getExperience().getExperienceType() : " ANY_EXPERIENCE"));


        if (request.getFilterParamRequest() != null) {
            String gender = "";
            if (request.getFilterParamRequest().getSelectedGender() == null
                    || request.getFilterParamRequest().getSelectedGender() == ServerConstants.GENDER_ANY) {
                gender = "ANY_GENDER";
            } else {
                gender = request.getFilterParamRequest().getSelectedGender()
                        == ServerConstants.GENDER_MALE ? "MALE" : "FEMALE";
            }
            result.append(" Gender: " + gender);
            result.append(" Language: " + ((languageStringList != null || languageStringList.size() > 0) ? String.join(",", languageStringList) : " ANY_LANGUAGE"));
            result.append(" Salary: >= " + (request.getFilterParamRequest().getSelectedSalary() != 0 ? request.getFilterParamRequest().getSelectedSalary() : " ANY_SALARY"));
        }

        result.append(" returned (" + response.getResults().getTotalJobs() + ") Jobs");

        String objectAUUID;
        if (candidateId == null) {
            objectAUUID = ServerConstants.TRU_WEB_NOT_LOGGED_UUID;
        } else {
            if (candidate != null) {
                objectAUUID = candidate.getCandidateUUId();
            } else {
                objectAUUID = ServerConstants.TRU_WEB_NOT_LOGGED_UUID;
            }
        }

        // create interaction
        InteractionService.createInteractionForWebSearch(objectAUUID, result.toString());

        // modify result jobPosts add CTA in it
        computeCTA(response.getResults().getAllJobPost(), candidateId);

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


    /**
     * CTA: Call to Action
     * uses Transient field 'applyBtnStatus' from JobPost
     * @param jobPostList
     * @return
     */
    public void computeCTA(List<JobPost> jobPostList, Long candidateId){

        List<Long> jobPostIdList = new ArrayList<>();
        Map<Long, JobPostWorkflow> jobApplicationMap = new HashMap<>();

        if(candidateId != null) {
            List<JobPostWorkflow> candidateAppliedJobs = JobPostWorkFlowDAO.candidateAppliedJobs(candidateId);
            for(JobPostWorkflow jobPostWorkflow: candidateAppliedJobs){
                jobApplicationMap.putIfAbsent(jobPostWorkflow.getJobPost().getJobPostId(), jobPostWorkflow);
            }
        }

        for(JobPost jobPost: jobPostList){

            // Add a check for already applied candidate
            if(candidateId != null) {
                Logger.info("candidateId: " + candidateId);
                if(jobApplicationMap.get(jobPost.getJobPostId()) != null){
                    jobPost.setApplyBtnStatus(ServerConstants.ALREADY_APPLIED);
                }
            }
            // change only if its not set prev, i.e its set to default value
            if(jobPost.getApplyBtnStatus() == 0){
                if(RecruiterService.isInterviewRequired(jobPost).getStatus() == ServerConstants.INTERVIEW_REQUIRED){
                    jobPost.setApplyBtnStatus(ServerConstants.INTERVIEW_REQUIRED);
                } else {
                    jobPost.setApplyBtnStatus(ServerConstants.APPLY);
                }
            }

            jobPostIdList.add(jobPost.getJobPostId());
        }

        return;
    }

    public void computeCTA(JobPost jobPost, Long candidateId){
        List<JobPost> jobPostList = new ArrayList<>();
        jobPostList.add(jobPost);

        computeCTA(jobPostList, candidateId);
    }
}
