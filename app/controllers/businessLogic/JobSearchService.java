package controllers.businessLogic;

import api.ServerConstants;
import api.http.FormValidator;
import com.avaje.ebean.Ebean;
import com.avaje.ebean.Expr;
import com.avaje.ebean.Query;
import controllers.AnalyticsLogic.JobRelevancyEngine;
import in.trujobs.proto.*;
import models.entity.Candidate;
import models.entity.JobPost;
import models.entity.OM.JobPreference;
import models.entity.Static.JobRole;
import play.Logger;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static api.ServerConstants.SORT_BY_DATE_POSTED;
import static api.ServerConstants.SORT_BY_SALARY;
import static api.ServerConstants.SORT_DEFAULT;

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

            // We are collecting results for job posts mathcing the exact jobrole ids and other relevant job role ids
            // in different lists so that we can maintain sort order within these groups
            // Eg. For a telecaller job search, we want to first show all telecaller jobs sorted in the given order
            // followed by by all BPO jobs

            List<JobPost> exactJobRoleJobs = queryAndReturnJobPosts(jobRoleIds, filterParams, sortBy, isHot, source);

            // TODO: This should be changed to a fetch from DB. Sbould not be computed upon every run.
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
                    resultJobPosts.addAll(sortByDistance(exactJobsWithinDistance));
                    resultJobPosts.addAll(sortByDistance(relatedJobsWithinDistance));
                }
                else {
                    resultJobPosts.addAll(exactJobsWithinDistance);
                    resultJobPosts.addAll(relatedJobsWithinDistance);
                }
            }
            else {
                resultJobPosts.addAll(exactJobRoleJobs);
                resultJobPosts.addAll(relatedJobRoleJobs);
            }
        }

        return resultJobPosts;
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

            return getRelevantJobPostsWithinDistance(lat, lng, jobRoleIds, null, ServerConstants.SORT_DEFAULT, false, false);
        }

        return getAllJobPosts();
    }

    /**
     * returns all available INTERNAL jobs sorted based on candidate's job role preferences
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

            List<JobPost> exactJobRoleJobs = queryAndReturnJobPosts(jobRoleIds, null, null, false, ServerConstants.SOURCE_INTERNAL);

            List<Long> relevantJobRoleIds = JobRelevancyEngine.getRelatedJobRoleIds(jobRoleIds);

            List<JobPost> relevantJobRoleJobs = queryAndReturnJobPosts(relevantJobRoleIds, null, null, false, ServerConstants.SOURCE_INTERNAL);

            //getting all jobroles excluding candidate's job role preference & relevant job roles
            List<JobRole> jobRoleList = JobRole.find.where()
                    .notIn("jobRoleId", jobRoleIds)
                    .notIn("jobRoleId", relevantJobRoleIds)
                    .findList();

            List<Long> otherJobRoleList = jobRoleList.stream().map(JobRole::getJobRoleId).collect(Collectors.toList());

            //getting all the internal jobs apart form candidate's job role pref & relevant job roles
            List<JobPost> otherJobRoleJobs = queryAndReturnJobPosts(otherJobRoleList, null, null, false, ServerConstants.SOURCE_INTERNAL);

            exactJobRoleJobs.addAll(relevantJobRoleJobs);
            exactJobRoleJobs.addAll(otherJobRoleJobs);

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

        if(sortBy != null){
            if (sortBy == SORT_BY_DATE_POSTED) {
                query = query.orderBy().desc("jobPostCreateTimestamp");

            } else if (sortBy == SORT_BY_SALARY) {
                query = query.orderBy().desc("jobPostMinSalary");
            }
        }

        return query.findList();
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
}
