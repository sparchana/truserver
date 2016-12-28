package controllers.businessLogic;

import api.ServerConstants;
import api.http.FormValidator;
import api.http.httpRequest.search.helper.FilterParamRequest;
import api.http.httpResponse.JobPostResponse;
import com.avaje.ebean.*;
import controllers.AnalyticsLogic.JobRelevancyEngine;
import in.trujobs.proto.JobFilterRequest;
import models.entity.Candidate;
import models.entity.JobPost;
import models.entity.OM.JobPreference;
import models.entity.Static.Education;
import models.entity.Static.Experience;
import models.entity.Static.JobRole;
import models.entity.Static.Locality;
import play.Logger;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static api.ServerConstants.SORT_BY_DATE_POSTED;
import static api.ServerConstants.SORT_BY_SALARY;

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
        return JobPost.find.all();
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

            List<JobPost> exactJobRoleJobs = queryAndReturnJobPosts(jobRoleIds, filterParams, sortBy, isHot, source);

            List<Long> relatedJobRoleIds = JobRelevancyEngine.getRelatedJobRoleIds(jobRoleIds);

            List<JobPost> relatedJobRoleJobs = queryAndReturnJobPosts(relatedJobRoleIds, filterParams, sortBy, isHot, source);

            // if we have lat-long info, then lets go ahead and filter the job post lists based on distance criteria
            if (latitude != null && longitude != null && latitude != 0.0 && longitude != 0.0) {

                List<JobPost> exactJobsWithinDistance = MatchingEngineService.filterByDistance(exactJobRoleJobs,
                        latitude, longitude,
                        ServerConstants.DEFAULT_MATCHING_ENGINE_RADIUS);

                List<JobPost> relatedJobsWithinDistance = MatchingEngineService.filterByDistance(relatedJobRoleJobs,
                        latitude, longitude,
                        ServerConstants.DEFAULT_MATCHING_ENGINE_RADIUS);

                if (sortBy == ServerConstants.SORT_DEFAULT) {
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

            List<JobPost> exactJobRoleJobs = queryAndReturnJobPosts(jobRoleIds, filterParams, sortBy, isHot, source);

            if (latitude != null && longitude != null && latitude != 0.0 && longitude != 0.0) {

                List<JobPost> exactJobsWithinDistance = MatchingEngineService.filterByDistance(exactJobRoleJobs,
                        latitude, longitude,
                        ServerConstants.DEFAULT_MATCHING_ENGINE_RADIUS);

                if (sortBy == ServerConstants.SORT_DEFAULT) {
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
    public static List<JobPost> getAllJobsForCandidate(String mobile) {

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
                    false, ServerConstants.SOURCE_INTERNAL);

            List<Long> relevantJobRoleIds = JobRelevancyEngine.getRelatedJobRoleIds(jobRoleIds);

            List<JobPost> relevantJobRoleJobs = queryAndReturnJobPosts(relevantJobRoleIds, null, SORT_BY_DATE_POSTED,
                    false, ServerConstants.SOURCE_INTERNAL);

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
                    true, ServerConstants.SOURCE_INTERNAL);

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
                                                        Integer source)
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
                                                         FilterParamRequest filterParamRequest)
    {
        int MAX_ROW = 5;
        JobPostResponse response = new JobPostResponse();
        response.setJobsPerPage(MAX_ROW);

        if (source == null) {
            source = ServerConstants.SOURCE_INTERNAL;
        }

        Query<JobPost> query = JobPost.find.query();

        // when no search params provided, return all active jobs
        if((keywordList == null||keywordList.size() ==0 )
                && locality == null
                && education == null
                && experience == null
                && sortBy == null
                && (filterParamRequest.getSelectedGender() == null
                && filterParamRequest.getSelectedLanguageIdList().size() == 0
        )){
            response = getAllActiveJobsPaginated(Long.valueOf((page-1)*MAX_ROW));
            response.setJobsPerPage(MAX_ROW);

            return response;
        }


        if (keywordList != null && keywordList.size() >0 ) {

            Junction<JobPost> junction = query.select("*")
                    .fetch("jobRole")
                    .fetch("company")
                    .where()
                    .disjunction();

            for(String keyword : keywordList){
                if (keyword != null && !keyword.trim().isEmpty()) {
                    keyword = keyword.trim();
                    Logger.info("keyword: " + keyword);
                    query = junction
                            .add(Expr.like("jobPostTitle", "%" + keyword + "%"))
                            .add(Expr.like("jobRole.jobName", "%" + keyword + "%"))
                            .add(Expr.like("company.companyName", "%" + keyword + "%"))
                            .endJunction().query();
                }
            }
        }

        if(locality != null) {
            query = query.select("*").fetch("jobPostToLocalityList")
                    .where()
                    .eq("jobPostToLocalityList.locality.localityId", locality.getLocalityId())
                    .query();
        }

        if(education != null) {
            query = query.select("*").fetch("jobPostEducation")
                    .where()
                    .eq("jobPostEducation.educationId", education.getEducationId())
                    .query();
        }
        if(experience != null) {
            query = query.select("*").fetch("jobPostExperience")
                    .where()
                    .eq("jobPostExperience.experienceId", experience.getExperienceId())
                    .query();
        }

        if (filterParamRequest!= null) {

            // apply gender filter
            if(filterParamRequest.getSelectedGender() != null){
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
            if(filterParamRequest.getSelectedLanguageIdList() != null
                    && filterParamRequest.getSelectedLanguageIdList().size() > 0 ) {
                query = query.select("*").fetch("jobPostLanguageRequirements")
                        .where()
                        .in("jobPostLanguageRequirements.language.languageId", filterParamRequest.getSelectedLanguageIdList())
                        .query();
            }
        }

//        if (isHot) {
//            query = query.where().eq("jobPostIsHot", "1").query();
//        }

        query = query.where().eq("source", source).query();

        query = query.where().eq("JobStatus", ServerConstants.JOB_STATUS_ACTIVE).query();


        // sort params
        if(sortBy != null){
            if (sortBy == 0) {
                query = query.orderBy().asc("jobPostMinSalary");

            } else if (sortBy == 1) {
                query = query.orderBy().desc("jobPostMinSalary");
            } else if (sortBy == SORT_BY_DATE_POSTED) {
                query = query.orderBy().desc("jobPostCreateTimestamp");
            }
        } else {
            // default orders
            query = query.orderBy().asc("source");
            query = query.orderBy().desc("JobPostIsHot");
            query = query.orderBy().desc("jobPostUpdateTimestamp");
        }


        response.setTotalJobs(query.findRowCount());
        Logger.info("total jobs: " + response.getTotalJobs());
        query = query.setFirstRow((page - 1) * MAX_ROW).setMaxRows(MAX_ROW);

        response.setAllJobPost(query.findPagedList().getList());
        return  response;
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
    public static JobPostResponse getAllHotJobsPaginated(Long index) {
        JobPostResponse jobPostResponse = new JobPostResponse();
        if (index != null) {
            PagedList<JobPost> pagedList = JobPost.find
                    .where()
                    .eq("jobPostIsHot", "1")
                    .eq("JobStatus", ServerConstants.JOB_STATUS_ACTIVE)
                    .eq("Source", ServerConstants.SOURCE_INTERNAL)
                    .setFirstRow(Math.toIntExact(index))
                    .setMaxRows(5)
                    .orderBy().asc("source")
                    .orderBy().desc("jobPostUpdateTimestamp")
                    .findPagedList();
            List<JobPost> jobPostList = pagedList.getList();
            jobPostResponse.setAllJobPost(jobPostList);
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
                                          .eq("JobStatus", ServerConstants.JOB_STATUS_ACTIVE)
                                          .eq("Source", ServerConstants.SOURCE_INTERNAL)
                                          .setFirstRow(Math.toIntExact(index))
                                          .setMaxRows(5)
                                          .orderBy().asc("source")
                                          .orderBy().desc("jobPostIsHot")
                                          .orderBy().desc("jobPostUpdateTimestamp")
                                          .findPagedList();
            List<JobPost> jobPostList = pagedList.getList();
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
                    .eq("JobStatus", ServerConstants.JOB_STATUS_ACTIVE)
                    .eq("Source", ServerConstants.SOURCE_INTERNAL)
                    .setFirstRow(Math.toIntExact(index))
                    .setMaxRows(5)
                    .orderBy().asc("source")
                    .orderBy().desc("jobPostUpdateTimestamp")
                    .findList();
            jobPostResponse.setAllJobPost(jobPostList);
            jobPostResponse.setTotalJobs(JobPost.find.where().eq("jobRole.jobRoleId",jobRoleId).eq("JobStatus", ServerConstants.JOB_STATUS_ACTIVE).eq("Source", ServerConstants.SOURCE_INTERNAL).findRowCount());
        }
    return jobPostResponse;
    }

}
