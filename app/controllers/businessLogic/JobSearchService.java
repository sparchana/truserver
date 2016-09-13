package controllers.businessLogic;

import api.ServerConstants;
import api.http.FormValidator;
import com.avaje.ebean.Query;
import in.trujobs.proto.*;
import models.entity.Candidate;
import models.entity.JobPost;
import models.entity.Static.JobRole;
import play.Logger;

import java.util.ArrayList;
import java.util.List;

import static api.ServerConstants.IS_HOT;
import static controllers.businessLogic.MatchingEngineService.sortJobPostList;
import static play.libs.Json.toJson;

/**
 * Created by zero on 15/8/16.
 */
public class JobSearchService {

    public static List<JobPost> getAllJobPosts() {
        return JobPost.find.where().eq("jobPostIsHot", ServerConstants.IS_HOT).findList();
    }

    public static List<JobPost> getMatchingJobPosts(List<Long> jobRoleIds, Integer sortOrder) {
        if(sortOrder == null){
            sortOrder = ServerConstants.SORT_DEFAULT;
        }

        Query<JobPost> query = JobPost.find.query();

        query = query
                .where()
                .eq("jobPostIsHot", IS_HOT)
                .query();

        if(jobRoleIds != null && !jobRoleIds.isEmpty() ) {
            query = query.select("*").fetch("jobRole")
                    .where()
                    .in("jobRole.jobRoleId", jobRoleIds)
                    .query();
        }

        List<JobPost> jobPostsResponseList = query.findList();

        boolean doDefaultSort = false;

        sortJobPostList(jobPostsResponseList, sortOrder, doDefaultSort);

        return jobPostsResponseList;
    }

    public static List<JobPost> getMatchingJobPosts(Double latitude, Double longitude,
                                                    List<Long> jobRoleIds, Integer sortOrder)
    {
        sortOrder = sortOrder == null ? ServerConstants.SORT_DEFAULT:sortOrder;

        return MatchingEngineService.fetchMatchingJobPostForLatLng(
                latitude, longitude, null, jobRoleIds, sortOrder);
    }

    public static List<JobPost> getRelevantJobsPostsForCandidate(String mobile) {

        String candidateMobile = FormValidator.convertToIndianMobileFormat(mobile);

        Candidate existingCandidate = CandidateService.isCandidateExists(mobile);

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
                for (int i = 0; i <= existingCandidate.getJobPreferencesList().size(); i++) {
                    jobRoleIds.add(existingCandidate.getJobPreferencesList().get(i).getJobRole().getJobRoleId());
                }
            }

            if (lat == 0.0 || lng == 0.0) {
                return getMatchingJobPosts(jobRoleIds, ServerConstants.SORT_DEFAULT);
            }
            else {
                return getMatchingJobPosts(lat, lng, jobRoleIds, ServerConstants.SORT_DEFAULT);
            }
        }

        return getAllJobPosts();
    }

    public static List<JobPost> filterJobs(JobFilterRequest jobFilterRequest, List<Long> jobRoleIds) {
        List<JobPost> filteredJobPostList = new ArrayList<>();
        Integer sortOrder = ServerConstants.SORT_DEFAULT;
        if (jobFilterRequest != null) {
            if(jobFilterRequest.getSortByDatePosted()){
                sortOrder = ServerConstants.SORT_BY_DATE_POSTED;
            } else if (jobFilterRequest.getSortBySalary()){
                sortOrder = ServerConstants.SORT_BY_SALARY;
            }
            List<JobPost> jobPostList;

            /* filter on searched lat/lng */
            if(jobFilterRequest.getJobSearchLongitude() != 0.0
                    && jobFilterRequest.getJobSearchLatitude() != 0.0) {
                jobPostList = getMatchingJobPosts(jobFilterRequest.getJobSearchLatitude(),
                        jobFilterRequest.getJobSearchLongitude(), jobRoleIds, sortOrder);
            } else {
                // if no lat long is available return all jobs matching given jobrole ids in given sort order
                jobPostList = getMatchingJobPosts(jobRoleIds, sortOrder);
            }

            if (jobPostList != null) {
                Logger.info("jobFilterRequest sal: " + toJson(jobFilterRequest.getSalary()));
                Logger.info("jobFilterRequest exp: " + toJson(jobFilterRequest.getExp()));
                Logger.info("jobFilterRequest edu: " + toJson(jobFilterRequest.getEdu()));
                Logger.info("jobFilterRequest gender: " + toJson(jobFilterRequest.getGender()));
                Logger.info("jobFilterRequest sort_by_datePosted: " + toJson(jobFilterRequest.getSortByDatePosted()));
                Logger.info("jobFilterRequest sort_by_sortBySalary: " + toJson(jobFilterRequest.getSortBySalary()));
                for (JobPost jobPost : jobPostList) {
                    boolean shouldContinue = false;
                    if (jobFilterRequest.getSalary() != null && jobFilterRequest.getSalary() != JobFilterRequest.Salary.ANY_SALARY) {
                        if ((jobPost.getJobPostMaxSalary() != null
                                && (getSalaryValue(jobFilterRequest.getSalaryValue()) <= jobPost.getJobPostMaxSalary()))
                                || (jobPost.getJobPostMinSalary() != null
                                && getSalaryValue(jobFilterRequest.getSalaryValue()) <= jobPost.getJobPostMinSalary())) {
                            shouldContinue = true;
                        } else {
                            shouldContinue = false;
                        }
                    } else {
                        shouldContinue = true;
                    }
                    /* filter result take Exp_Type:Any* into-account */
                    if (shouldContinue && jobPost.getJobPostExperience() != null
                            && jobFilterRequest.getExp() != null) {
                        if (jobFilterRequest.getExpValue() == JobFilterRequest.Experience.FRESHER_VALUE
                                && (jobPost.getJobPostExperience().getExperienceId() == ServerConstants.EXPERIENCE_TYPE_FRESHER_ID
                                || jobPost.getJobPostExperience().getExperienceId() == ServerConstants.EXPERIENCE_TYPE_ANY_ID)) {
                            shouldContinue = true;
                        } else if (jobFilterRequest.getExpValue() == JobFilterRequest.Experience.EXPERIENCED_VALUE
                                && jobPost.getJobPostExperience().getExperienceId() > ServerConstants.EXPERIENCE_TYPE_FRESHER_ID) {
                            shouldContinue = true;
                        } else if (jobFilterRequest.getExp() == JobFilterRequest.Experience.ANY_EXPERIENCE) {
                            shouldContinue = true;
                        } else {
                            shouldContinue = false;
                        }
                    }
                    /* filter education */
                    if (shouldContinue && jobPost.getJobPostEducation() != null
                            && jobFilterRequest.getEdu() != null) {
                        if (jobFilterRequest.getEduValue() == JobFilterRequest.Education.LT_TEN_VALUE
                                && jobPost.getJobPostEducation().getEducationId() == ServerConstants.EDUCATION_TYPE_LT_10TH_ID) {
                            shouldContinue = true;
                        } else if (jobFilterRequest.getEduValue() == JobFilterRequest.Education.TEN_PASS_VALUE
                                && jobPost.getJobPostEducation().getEducationId() == ServerConstants.EDUCATION_TYPE_10TH_PASS_ID) {
                            shouldContinue = true;
                        } else if (jobFilterRequest.getEduValue() == JobFilterRequest.Education.TWELVE_PASS_VALUE
                                && jobPost.getJobPostEducation().getEducationId() == ServerConstants.EDUCATION_TYPE_12TH_PASS_ID) {
                            shouldContinue = true;
                        } else if (jobFilterRequest.getEduValue() == JobFilterRequest.Education.UG_VALUE
                                && jobPost.getJobPostEducation().getEducationId() == ServerConstants.EDUCATION_TYPE_UG) {
                            shouldContinue = true;
                        } else if (jobFilterRequest.getEduValue() == JobFilterRequest.Education.PG_VALUE
                                && jobPost.getJobPostEducation().getEducationId() == ServerConstants.EDUCATION_TYPE_PG) {
                            shouldContinue = true;
                        } else if (jobFilterRequest.getEdu() == JobFilterRequest.Education.ANY_EDUCATION) {
                            shouldContinue = true;
                        } else {
                            shouldContinue = false;
                        }
                    }
                    /* filter gender */
                    if (shouldContinue && jobFilterRequest.getGender() != null && jobFilterRequest.getGender() != JobFilterRequest.Gender.ANY_GENDER) {
                        if ((jobPost.getGender() == null
                                || jobPost.getGender() == ServerConstants.GENDER_MALE
                                || jobPost.getGender() == ServerConstants.GENDER_ANY) && jobFilterRequest.getGender() == JobFilterRequest.Gender.MALE) {
                            shouldContinue = true;
                        } else if ((jobPost.getGender() == null
                                || jobPost.getGender() == ServerConstants.GENDER_FEMALE
                                || jobPost.getGender() == ServerConstants.GENDER_ANY) && jobFilterRequest.getGender() == JobFilterRequest.Gender.FEMALE) {
                            shouldContinue = true;
                        } else {
                            shouldContinue = false;
                        }
                    }
                    if (shouldContinue) {
                        filteredJobPostList.add(jobPost);
                    }
                }
            }
        }
        return filteredJobPostList;
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
}
