package controllers.businessLogic;

import api.ServerConstants;
import api.http.FormValidator;
import api.http.httpRequest.search.helper.FilterParamRequest;
import api.http.httpResponse.JobPostResponse;
import com.avaje.ebean.Expr;
import com.avaje.ebean.Junction;
import com.avaje.ebean.PagedList;
import com.avaje.ebean.Query;
import controllers.AnalyticsLogic.JobRelevancyEngine;
import dao.JobPostDAO;
import in.trujobs.proto.JobFilterRequest;
import models.entity.Candidate;
import models.entity.Company;
import models.entity.JobPost;
import models.entity.OM.JobPreference;
import models.entity.Static.Education;
import models.entity.Static.Experience;
import models.entity.Static.JobRole;
import models.entity.Static.Locality;
import play.Logger;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import static api.ServerConstants.*;
import static play.mvc.Controller.session;

/**
 * Created by zero on 15/8/16.
 */
public class JobSearchService {

    public static List<JobPost> getAllHotJobPosts() {
        return JobPost.find.where()
                .eq("jobPostIsHot", ServerConstants.IS_HOT)
                .findList();
    }

    public static List<JobPost> getAllJobPosts() {
        return JobPost.find.where()
                      .ne("JobStatus", ServerConstants.JOB_STATUS_DEACTIVATED)
                      .findList();
    }


    /**
     * This method fetches all the 'relevant' job post within the given distance.
     * 'relevant' means that we search not only for the given jobrole ids, but also expand the search to include all
     * 'related job role ids'
     *
     * @param latitude Latitude for anchoring the search
     * @param longitude Logitude for anchoring the search
     * @param jobRoleIds All job role ids to be searched
     * @param filterParams Filter params such as 'salary'/'Education'/'Experience'/'Gender'
     * @param sortBy Sort parameter like 'salary','date posted','nearby'
     * @param isHot Whether the search should consider only hot jobs
     * @param isAllSources Whether the search should consider both internal and external job post sources
     *
     * @return A list of JobPosts that match the given query criteria and arranged in the given sort order
     */
    public static List<JobPost> getRelevantJobPostsWithinDistance(Double latitude, Double longitude,
                                                                  List<Long> jobRoleIds,
                                                                  JobFilterRequest filterParams,
                                                                  Integer sortBy,
                                                                  boolean isHot,
                                                                  boolean isAllSources)
    {
        List<JobPost> resultJobPosts = new ArrayList<JobPost>();
        List<Integer> jobPostSources = new ArrayList<Integer>();

        // form job post sources
        if (isAllSources) {
            // This order of inserting is very important to maintain sort order in the returned result
            jobPostSources.add(ServerConstants.SOURCE_INTERNAL);
            jobPostSources.add(ServerConstants.SOURCE_BABAJOBS);
        }
        else {
            // For now, assumption we are only supporting the use case where clients can fetch either 'All' job posts
            // or only 'internal' job posts
            jobPostSources.add(ServerConstants.SOURCE_INTERNAL);
        }

        for (Integer source : jobPostSources) {

            // We are collecting results for job posts matching the exact jobrole ids and other relevant job role ids
            // in different lists so that we can maintain sort order within these groups
            // Eg. For a telecaller job search, we want to first show all telecaller jobs sorted in the given order
            // followed by by all BPO jobs

            List<JobPost> exactJobRoleJobs = queryAndReturnJobPosts(jobRoleIds, filterParams, sortBy, isHot, source, ServerConstants.JOB_POST_TYPE_OPEN);

            List<Long> relatedJobRoleIds = JobRelevancyEngine.getRelatedJobRoleIds(jobRoleIds);

            List<JobPost> relatedJobRoleJobs = queryAndReturnJobPosts(relatedJobRoleIds, filterParams, sortBy, isHot, source, ServerConstants.JOB_POST_TYPE_OPEN);

            // if we have lat-long info, then lets go ahead and filter the job post lists based on distance criteria
            if (latitude != null && longitude != null && latitude != 0.0 && longitude != 0.0) {

                List<JobPost> exactJobsWithinDistance = MatchingEngineService.filterByDistance(exactJobRoleJobs,
                        latitude, longitude,
                        ServerConstants.DEFAULT_MATCHING_ENGINE_RADIUS);

                List<JobPost> relatedJobsWithinDistance = MatchingEngineService.filterByDistance(relatedJobRoleJobs,
                        latitude, longitude,
                        ServerConstants.DEFAULT_MATCHING_ENGINE_RADIUS);

                if (sortBy == SORT_DEFAULT) {
                    //segregation
                    resultJobPosts = sortJobPostListAccordingToHotJobs(sortByDistance(exactJobsWithinDistance), sortByDistance(relatedJobsWithinDistance));

                }
                else {
                    //segregation
                    resultJobPosts = sortJobPostListAccordingToHotJobs(exactJobsWithinDistance, relatedJobsWithinDistance);
                }
            }
            else {
                //segregation
                resultJobPosts = sortJobPostListAccordingToHotJobs(exactJobRoleJobs, relatedJobRoleJobs);
            }
        }

        return resultJobPosts;
    }

    public static List<JobPost> sortJobPostListAccordingToHotJobs(List<JobPost> exactJobPostList, List<JobPost> relevantJobPostList){
        List<JobPost> finalList = new ArrayList<>();

        List<JobPost> exactHotJobs = new ArrayList<>();
        List<JobPost> exactOtherJobs = new ArrayList<>();

        List<JobPost> relevantHotJobs = new ArrayList<>();
        List<JobPost> relevantOtherJobs = new ArrayList<>();

        //segregation
        for(JobPost jobPost : exactJobPostList){
            if(jobPost.getJobPostIsHot()){
                exactHotJobs.add(jobPost);
            } else{
                exactOtherJobs.add(jobPost);
            }
        }

        for(JobPost jobPost : relevantJobPostList){
            if(jobPost.getJobPostIsHot()){
                relevantHotJobs.add(jobPost);
            } else{
                relevantOtherJobs.add(jobPost);
            }
        }

        finalList.addAll(exactHotJobs);
        finalList.addAll(exactOtherJobs);
        finalList.addAll(relevantHotJobs);
        finalList.addAll(relevantOtherJobs);

        return finalList;
    }


    /**
     * This method fetches only the job posts matching the given job roles ids within the given distance.
     * 'Job-role-relevancy' based search is not supported here.
     *
     * @param latitude Latitude for anchoring the search
     * @param longitude Logitude for anchoring the search
     * @param jobRoleIds All job role ids to be searched
     * @param filterParams Filter params such as 'salary'/'Education'/'Experience'/'Gender'
     * @param sortBy Sort parameter like 'salary','date posted','nearby'
     * @param isHot Whether the search should consider only hot jobs
     * @param isAllSources Whether the search should consider both internal and external job post sources
     *
     * @return A list of JobPosts that match the given query criteria and arranged in the given sort order
     */
    public static List<JobPost> getExactJobPostsWithinDistance(Double latitude, Double longitude,
                                                               List<Long> jobRoleIds,
                                                               JobFilterRequest filterParams,
                                                               Integer sortBy,
                                                               boolean isHot,
                                                               boolean isAllSources)
    {
        List<JobPost> resultJobPosts = new ArrayList<JobPost>();
        List<Integer> jobPostSources = new ArrayList<Integer>();

        // form job post sources
        if (isAllSources) {
            // This order of inserting is very important to maintain sort order in the returned result
            jobPostSources.add(ServerConstants.SOURCE_INTERNAL);
            jobPostSources.add(ServerConstants.SOURCE_BABAJOBS);
        }
        else {
            // For now, assumption we are only supporting the use case where clients can fetch either 'All; job posts
            // or only 'internal' job posts
            jobPostSources.add(ServerConstants.SOURCE_INTERNAL);
        }

        for (Integer source : jobPostSources) {

            List<JobPost> exactJobRoleJobs = queryAndReturnJobPosts(jobRoleIds, filterParams, sortBy, isHot, source, ServerConstants.JOB_POST_TYPE_OPEN);

            if (latitude != null && longitude != null && latitude != 0.0 && longitude != 0.0) {

                List<JobPost> exactJobsWithinDistance = MatchingEngineService.filterByDistance(exactJobRoleJobs,
                        latitude, longitude,
                        ServerConstants.DEFAULT_MATCHING_ENGINE_RADIUS);

                if (sortBy == SORT_DEFAULT) {
                    resultJobPosts.addAll(sortByDistance(exactJobsWithinDistance));
                }
                else {
                    resultJobPosts.addAll(exactJobsWithinDistance);
                }

            }
            else {
                resultJobPosts.addAll(exactJobRoleJobs);
            }
        }

        return resultJobPosts;
    }

    public static List<JobPost> getRelevantJobsPostsForCandidate(String mobile) {

        String candidateMobile = FormValidator.convertToIndianMobileFormat(mobile);

        Candidate existingCandidate = CandidateService.isCandidateExists(candidateMobile);

        double lat = 0.00;
        double lng = 0.00;
        List<Long> jobRoleIds = new ArrayList<Long>();

        if (existingCandidate != null) {
            // check if this candidate has lat-long details, of so use this for search
            if (existingCandidate.getCandidateLocalityLat() != null && existingCandidate.getCandidateLocalityLng() != null &&
                    existingCandidate.getCandidateLocalityLat() != 0.0 && existingCandidate.getCandidateLocalityLng() != 0.0)
            {
                lat = existingCandidate.getCandidateLocalityLat();
                lng = existingCandidate.getCandidateLocalityLng();
            }
            else if (existingCandidate.getLocality() != null &&
                     existingCandidate.getLocality().getLat() != null &&
                     existingCandidate.getLocality().getLng() != null &&
                     existingCandidate.getLocality().getLat() != 0.0 &&
                     existingCandidate.getLocality().getLat() != 0.0)
            {
                // if candidate's home locality is mentioned, we will use that for search
                lat = existingCandidate.getLocality().getLat();
                lng = existingCandidate.getLocality().getLng();
            }

            if (existingCandidate.getJobPreferencesList() != null && !existingCandidate.getJobPreferencesList().isEmpty()) {

                List<JobPreference> jobPrefsList = existingCandidate.getJobPreferencesList();
                for (JobPreference jobPref : jobPrefsList) {
                    jobRoleIds.add(jobPref.getJobRole().getJobRoleId());
                }
            }

            return getRelevantJobPostsWithinDistance(lat, lng, jobRoleIds, null, ServerConstants.SORT_BY_DATE_POSTED, true, false);
        }

        return getAllJobPosts();
    }

    /**
     * returns all available INTERNAL jobs in ACTIVE state sorted based on candidate's job role preferences
     *
     * @param mobile candidates mobile
     * @return
     */
    public static List<JobPost> getAllJobsForCandidate(String mobile, Integer accessLevel) {

        String candidateMobile = FormValidator.convertToIndianMobileFormat(mobile);

        Candidate existingCandidate = CandidateService.isCandidateExists(candidateMobile);

        List<Long> jobRoleIds = new ArrayList<Long>();

        if (existingCandidate != null) {

            if (existingCandidate.getJobPreferencesList() != null && !existingCandidate.getJobPreferencesList().isEmpty()) {

                List<JobPreference> jobPrefsList = existingCandidate.getJobPreferencesList();
                for (JobPreference jobPref : jobPrefsList) {
                    jobRoleIds.add(jobPref.getJobRole().getJobRoleId());
                }
            }

            List<JobPost> exactJobRoleJobs = queryAndReturnJobPosts(jobRoleIds, null, SORT_BY_DATE_POSTED,
                    true, ServerConstants.SOURCE_INTERNAL, accessLevel);

            List<Long> relevantJobRoleIds = JobRelevancyEngine.getRelatedJobRoleIds(jobRoleIds);

            List<JobPost> relevantJobRoleJobs = queryAndReturnJobPosts(relevantJobRoleIds, null, SORT_BY_DATE_POSTED,
                    true, ServerConstants.SOURCE_INTERNAL, accessLevel);

            List<Long> finalJobRoleIdList = new ArrayList<>();
            finalJobRoleIdList.addAll(jobRoleIds);
            finalJobRoleIdList.addAll(relevantJobRoleIds);

            //getting all jobroles excluding candidate's job role preference & relevant job roles
            List<JobRole> jobRoleList = JobRole.find.where()
                    .notIn("jobRoleId", finalJobRoleIdList )
                    .findList();

            List<Long> otherJobRoleIdList = jobRoleList.stream().map(JobRole::getJobRoleId).collect(Collectors.toList());

            //getting all the internal jobs apart form candidate's job role pref & relevant job roles
            List<JobPost> otherJobRoleJobs = queryAndReturnJobPosts(otherJobRoleIdList, null, SORT_BY_DATE_POSTED,
                    true, ServerConstants.SOURCE_INTERNAL, accessLevel);

            for(JobPost jobPost : relevantJobRoleJobs) {
                if(!exactJobRoleJobs.contains(jobPost)) {
                    exactJobRoleJobs.add(jobPost);
                }
            }

            for(JobPost jobPost : otherJobRoleJobs) {
                if(!exactJobRoleJobs.contains(jobPost)) {
                    exactJobRoleJobs.add(jobPost);
                }
            }

            return exactJobRoleJobs;
        }

        return getAllJobPosts();
    }

    private static List<JobPost> queryAndReturnJobPosts(List<Long> jobRoleIds,
                                                        JobFilterRequest filterParams,
                                                        Integer sortBy,
                                                        boolean isHot,
                                                        Integer source,
                                                        Integer accessLevel)
    {

        if (source == null) {
            source = ServerConstants.SOURCE_INTERNAL;
        }

        Query<JobPost> query = JobPost.find.query();

        if (jobRoleIds != null && !jobRoleIds.isEmpty() ) {
            query = query.select("*").fetch("jobRole")
                    .where()
                    .in("jobRole.jobRoleId", jobRoleIds)
                    .query();
        }

        if (isHot) {
            query = query.where().eq("jobPostIsHot", "1").query();
        }

        query = query.where().eq("source", source).query();

        //checking if job post is private or not
        query = query.where().eq("job_post_access_level", accessLevel).query();

        query = query.where().eq("JobStatus", ServerConstants.JOB_STATUS_ACTIVE).query();

        // filter params
        if (filterParams != null) {
            if (filterParams.getSalary() != null && filterParams.getSalary() != JobFilterRequest.Salary.ANY_SALARY) {
                Long salValue = getSalaryValue(filterParams.getSalaryValue());
                query = query.where().or(Expr.ge("jobPostMinSalary", salValue), Expr.ge("jobPostMaxSalary", salValue)).query();
            }

            if (filterParams.getExp() != null && filterParams.getExpValue() != JobFilterRequest.Experience.ANY_EXPERIENCE_VALUE) {
                if (filterParams.getExpValue() == JobFilterRequest.Experience.FRESHER_VALUE) {
                    query = query.select("*").fetch("jobPostExperience")
                            .where()
                            .or(Expr.eq("jobPostExperience.experienceId", ServerConstants.EXPERIENCE_TYPE_FRESHER_ID),
                                    Expr.eq("jobPostExperience.experienceId", ServerConstants.EXPERIENCE_TYPE_ANY_ID))
                            .query();
                } else if (filterParams.getExpValue() == JobFilterRequest.Experience.EXPERIENCED_VALUE) {
                    query = query.select("*").fetch("jobPostExperience")
                            .where()
                            .or(Expr.ne("jobPostExperience.experienceId", ServerConstants.EXPERIENCE_TYPE_FRESHER_ID),
                                    Expr.eq("jobPostExperience.experienceId", ServerConstants.EXPERIENCE_TYPE_ANY_ID))
                            .query();
                }
            }

            if (filterParams.getEdu() != null && filterParams.getEdu() != JobFilterRequest.Education.ANY_EDUCATION) {
                query = query.select("*").fetch("jobPostEducation")
                        .where()
                        .eq("jobPostEducation.educationId", filterParams.getEduValue())
                        .query();
            }

            if (filterParams.getGender() != null && filterParams.getGender() != JobFilterRequest.Gender.ANY_GENDER) {

                if (filterParams.getGender() == JobFilterRequest.Gender.MALE) {
                    query = query
                            .where()
                            .or(Expr.isNull("gender"), Expr.or(
                                    Expr.eq("gender", ServerConstants.GENDER_MALE),
                                    Expr.eq("gender", ServerConstants.GENDER_ANY)))
                            .query();

                } else if (filterParams.getGender() == JobFilterRequest.Gender.FEMALE) {
                    query = query
                            .where()
                            .or(Expr.isNull("gender"), Expr.or(
                                    Expr.eq("gender", ServerConstants.GENDER_FEMALE),
                                    Expr.eq("gender", ServerConstants.GENDER_ANY)))
                            .query();
                }
            }
        }

        query = query.orderBy().desc("JobPostIsHot");

        if(sortBy != null){
            if (sortBy == SORT_BY_DATE_POSTED) {
                query = query.orderBy().desc("jobPostCreateTimestamp");

            } else if (sortBy == SORT_BY_SALARY) {
                query = query.orderBy().desc("jobPostMinSalary");
            }
        }

        return query.findList();
    }

    public static JobPostResponse queryAndReturnJobPosts(List<String> keywordList,
                                                         Locality locality,
                                                         Education education,
                                                         Experience experience,
                                                         Integer sortBy,
                                                         boolean isHot,
                                                         Integer source,
                                                         int page,
                                                         FilterParamRequest filterParamRequest,
                                                         Integer jobPostAccessLevel)
    {
        int MAX_ROW = 5;
        JobPostResponse response = new JobPostResponse();
        response.setJobsPerPage(MAX_ROW);

        if (source == null) {
            source = ServerConstants.SOURCE_INTERNAL;
        }

        Query<JobPost> query = JobPost.find.query();

        if (keywordList != null && keywordList.size() > 0) {

            Junction<JobPost> junction = query.select("*")
                    .fetch("jobRole")
                    .fetch("company")
                    .where()
                    .disjunction();

            for (String keyword : keywordList) {
                if (keyword != null && !keyword.trim().isEmpty()) {
                    keyword = keyword.trim();
                    query = junction
                            .add(Expr.like("jobPostTitle", keyword + "%"))
                            .add(Expr.like("jobRole.jobName", keyword + "%"))
                            .add(Expr.like("company.companyName", keyword + "%"))
                            .endJunction().query();
                }
            }
        }

        if (education != null) {
            query = query.select("*").fetch("jobPostEducation")
                    .where()
                    .or(Expr.eq("jobPostEducation.educationId", education.getEducationId()),
                            Expr.eq("jobPostEducation.educationId", ServerConstants.EDUCATION_TYPE_ANY))
                    .query();
        }

        if (experience != null) {
            query = query.select("*").fetch("jobPostExperience")
                    .where()
                    .or(
                            Expr.eq("jobPostExperience.experienceId", experience.getExperienceId()),
                            Expr.eq("jobPostExperience.experienceId", ServerConstants.EXPERIENCE_TYPE_ANY_ID)
                    )
                    .query();
        }

        if (filterParamRequest != null) {

            // apply gender filter
            if (filterParamRequest.getSelectedGender() != null) {
                if (filterParamRequest.getSelectedGender() == ServerConstants.GENDER_MALE) {
                    query = query
                            .where()
                            .or(Expr.isNull("gender"), Expr.or(
                                    Expr.eq("gender", ServerConstants.GENDER_MALE),
                                    Expr.eq("gender", ServerConstants.GENDER_ANY)))
                            .query();

                } else if (filterParamRequest.getSelectedGender() == ServerConstants.GENDER_FEMALE) {
                    query = query
                            .where()
                            .or(Expr.isNull("gender"), Expr.or(
                                    Expr.eq("gender", ServerConstants.GENDER_FEMALE),
                                    Expr.eq("gender", ServerConstants.GENDER_ANY)))
                            .query();
                }
            }

            // apply language filter
            if (filterParamRequest.getSelectedLanguageIdList() != null
                                && filterParamRequest.getSelectedLanguageIdList().size() > 0) {

                query = query.select("*").fetch("jobPostLanguageRequirements")
                             .where()
                             .in("jobPostLanguageRequirements.language.languageId", filterParamRequest.getSelectedLanguageIdList())
                             .query();
            }
            // apply salary filter
            if (filterParamRequest.getSelectedSalary() != null
                                && filterParamRequest.getSelectedSalary() != 0) {

                query = query.where().or(Expr.ge("jobPostMinSalary", filterParamRequest.getSelectedSalary()),
                                         Expr.ge("jobPostMaxSalary", filterParamRequest.getSelectedSalary())).query();
            }
        }

//       commented out for now,  to have more results
//        if (isHot) {
//            query = query.where().eq("jobPostIsHot", "1").query();
//        }

        query = query.where().eq("source", source).query();

        //checking if job post is private or not

        query = query.where().eq("job_post_access_level", jobPostAccessLevel).query();

        query = query.where().eq("JobStatus", ServerConstants.JOB_STATUS_ACTIVE).query();

        boolean doSortByDistance = false;
        int modifiedSortBy = sortBy;

        // sort params, this should ideally not exist now as we are externally sorting it anyways
        if ((sortBy == ServerConstants.SORT_BY_RELEVANCE) && locality == null) {

            // default orders
            query = query.orderBy().asc("source");
            query = query.orderBy().desc("JobPostIsHot");
            query = query.orderBy().desc("jobPostUpdateTimestamp");

            Logger.info("sort by " + sortBy);
            modifiedSortBy = SORT_BY_DATE_POSTED;
            doSortByDistance = false;
        }

//        query = query.setFirstRow((page - 1) * MAX_ROW).setMaxRows(MAX_ROW);

        List<JobPost> resultJobPosts = query.findList();

        // sort by relevance {hot jobs + internal + distance}
        List<JobPost> resultJobsWithinDistance;
        if (locality != null && locality.getLat() != null) {
            resultJobsWithinDistance = MatchingEngineService.filterByDistance(resultJobPosts,
                    locality.getLat(), locality.getLng(),
                    ServerConstants.WEB_SEARCH_MATCHING_ENGINE_RADIUS);
            doSortByDistance = true;
        } else {
            resultJobsWithinDistance = resultJobPosts;
        }

        // our current logic for sory by relevance is this
        // if relevant sort is not set then we dont segregate hotJobs with nonHotJobs
        // relevant criteria : {hot : with filters} then {nonHot: with filter} (Active, source->internal)
        if(sortBy == ServerConstants.SORT_BY_RELEVANCE){
            List<JobPost> hotJobPostList = new LinkedList<>();
            List<JobPost> nonHotJobPostList = new LinkedList<>();

            modifiedSortBy = SORT_BY_DATE_POSTED;

            // segregate hot with non H
            for(JobPost jobPost : resultJobsWithinDistance) {
                if(jobPost.getJobPostIsHot()){
                    hotJobPostList.add(jobPost);
                } else {
                    nonHotJobPostList.add(jobPost);
                }
            }

            // sort individually
            MatchingEngineService.sortJobPostList(hotJobPostList, modifiedSortBy, doSortByDistance);
            MatchingEngineService.sortJobPostList(nonHotJobPostList, modifiedSortBy, doSortByDistance);

            resultJobsWithinDistance.clear();
            resultJobsWithinDistance.addAll(hotJobPostList);
            resultJobsWithinDistance.addAll(nonHotJobPostList);
        } else {
            MatchingEngineService.sortJobPostList(resultJobsWithinDistance, modifiedSortBy, doSortByDistance);
        }


        response.setTotalJobs(resultJobsWithinDistance.size());
        Logger.info("total jobs: " + response.getTotalJobs());

        // find every thing and trim off the result based on page number
        List<JobPost> trimmedJobPosts = new ArrayList<>();
        int i = (page - 1) * MAX_ROW;
        for (int j = 0; j < MAX_ROW && i < resultJobsWithinDistance.size(); ++j) {
            trimmedJobPosts.add(resultJobsWithinDistance.get(i++));
        }

        response.setAllJobPost(trimmedJobPosts);

        return response;
    }

    private static Long getSalaryValue(Integer id) {
        switch (id) {
            case JobFilterRequest.Salary.ANY_SALARY_VALUE:
                return 0L;
            case JobFilterRequest.Salary.EIGHT_K_PLUS_VALUE:
                return 8000L;
            case JobFilterRequest.Salary.TEN_K_PLUS_VALUE:
                return 10000L;
            case JobFilterRequest.Salary.TWELVE_K_PLUS_VALUE:
                return 12000L;
            case JobFilterRequest.Salary.FIFTEEN_K_PLUS_VALUE:
                return 15000L;
            case JobFilterRequest.Salary.TWENTY_K_PLUS_VALUE:
                return 20000L;
            default:
                return 0L;
        }
    }

    private static List<JobPost> sortByDistance(List<JobPost> jobPostsResponseList) {
        Collections.sort(jobPostsResponseList, (a, b) -> a.getJobPostToLocalityList().get(0).getDistance()
                .compareTo(b.getJobPostToLocalityList().get(0).getDistance()));

        return jobPostsResponseList;
    }

    /** return all available Hot jobs in Active state
     * @param index index from where the set of jobs to be shown
     * @return hot jobs w.r.t index value and total number of hot jobs
     */
    public static JobPostResponse getAllHotJobsPaginated(Integer index, Integer sessionSalt) {
        JobPostResponse jobPostResponse = new JobPostResponse();
        if (index != null) {
/*
            PagedList<JobPost> pagedList = JobPost.find
                    .where()
                    .eq("job_post_access_level", ServerConstants.JOB_POST_TYPE_OPEN)
                    .eq("jobPostIsHot", "1")
                    .eq("JobStatus", ServerConstants.JOB_STATUS_ACTIVE)
                    .eq("Source", ServerConstants.SOURCE_INTERNAL)
                    .setFirstRow(Math.toIntExact(index))
                    .setMaxRows(5)
                    .orderBy().asc("source")
                    .orderBy().desc("jobPostUpdateTimestamp")
                    .findPagedList();
            List<JobPost> jobPostList = pagedList.getList();
*/
            List<JobPost> jobPostList = JobPostDAO.getRandomJobPost(index, sessionSalt);

            Long cId = null;
            if((session().get("candidateId") != null)){
                cId = Long.valueOf(session().get("candidateId"));
            }

            SearchJobService.computeCTA(jobPostList, cId);
            jobPostResponse.setAllJobPost(jobPostList);

            // sanitize data
            SearchJobService.removeSensitiveDetail(jobPostList);

            jobPostResponse.setTotalJobs(JobPost.find.where().eq("jobPostIsHot", "1").eq("JobStatus", ServerConstants.JOB_STATUS_ACTIVE).eq("Source", ServerConstants.SOURCE_INTERNAL).findRowCount());
        }
        return jobPostResponse;

    }

    /** return all available jobs in Active state
     * @param index index form where the set of jobs to be shown
     * @return all jobs with active state w.r.t index value and also total number of active jobs
     */
    public static JobPostResponse getAllActiveJobsPaginated(Long index){
        JobPostResponse jobPostResponse = new JobPostResponse();
        if(index != null){
            PagedList<JobPost> pagedList = JobPost.find
                    .where()
                    .eq("job_post_access_level", ServerConstants.JOB_POST_TYPE_OPEN)
                    .eq("JobStatus", ServerConstants.JOB_STATUS_ACTIVE)
                    .eq("Source", ServerConstants.SOURCE_INTERNAL)
                    .setFirstRow(Math.toIntExact(index))
                    .setMaxRows(5)
                    .orderBy().asc("source")
                    .orderBy().desc("jobPostIsHot")
                    .orderBy().desc("jobPostUpdateTimestamp")
                    .findPagedList();
            List<JobPost> jobPostList = pagedList.getList();

            SearchJobService.computeCTA(jobPostList, null);
            jobPostResponse.setAllJobPost(jobPostList);
            jobPostResponse.setTotalJobs(JobPost.find.where().eq("JobStatus", ServerConstants.JOB_STATUS_ACTIVE).eq("Source", ServerConstants.SOURCE_INTERNAL).findRowCount());
        }
     return jobPostResponse;
    }

    /** return all available jobs which is private
     * @param index index form where the set of jobs to be shown
     * @return all jobs with active state w.r.t index value and also total number of active jobs
     */
    public static JobPostResponse getAllPrivateJobsOfCompany(Long index, Company company){
        JobPostResponse jobPostResponse = new JobPostResponse();
        if(index != null){
            PagedList<JobPost> pagedList = JobPost.find
                    .where()
                    .eq("JobStatus", ServerConstants.JOB_STATUS_ACTIVE)
                    .eq("job_post_access_level", ServerConstants.JOB_POST_TYPE_PRIVATE)
                    .eq("CompanyId", company.getCompanyId())
                    .eq("Source", ServerConstants.SOURCE_INTERNAL)
                    .setFirstRow(Math.toIntExact(index))
                    .setMaxRows(5)
                    .orderBy().asc("source")
                    .orderBy().desc("jobPostUpdateTimestamp")
                    .findPagedList();

            List<JobPost> jobPostList = pagedList.getList();

            SearchJobService.computeCTA(jobPostList, null);
            jobPostResponse.setAllJobPost(jobPostList);
            jobPostResponse.setTotalJobs(JobPost.find.where().eq("JobStatus", ServerConstants.JOB_STATUS_ACTIVE).eq("Source", ServerConstants.SOURCE_INTERNAL).findRowCount());
        }
     return jobPostResponse;
    }

    public static JobPostResponse getActiveJobsForJobRolePaginated(Long jobRoleId, Long index){
        JobPostResponse jobPostResponse = new JobPostResponse();
        if(index!=null){
            List<JobPost> jobPostList = JobPost.find.where()
                    .eq("jobRole.jobRoleId",jobRoleId)
                    .eq("job_post_access_level", ServerConstants.JOB_POST_TYPE_OPEN)
                    .eq("JobStatus", ServerConstants.JOB_STATUS_ACTIVE)
                    .eq("Source", ServerConstants.SOURCE_INTERNAL)
                    .setFirstRow(Math.toIntExact(index))
                    .setMaxRows(5)
                    .orderBy().asc("source")
                    .orderBy().desc("jobPostUpdateTimestamp")
                    .findList();

            Long cId = null;
            if((session().get("candidateId") != null)){
                cId = Long.valueOf(session().get("candidateId"));
            }
            SearchJobService.computeCTA(jobPostList, cId);
            jobPostResponse.setAllJobPost(jobPostList);

            jobPostResponse.setTotalJobs(JobPost.find.where().eq("jobRole.jobRoleId",jobRoleId).eq("JobStatus", ServerConstants.JOB_STATUS_ACTIVE).eq("Source", ServerConstants.SOURCE_INTERNAL).findRowCount());
        }
    return jobPostResponse;
    }

}
