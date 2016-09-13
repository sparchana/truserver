package controllers.businessLogic;

import api.ServerConstants;
import api.http.FormValidator;
import com.avaje.ebean.Query;
import in.trujobs.proto.*;
import models.entity.Candidate;
import models.entity.JobPost;
import models.entity.OM.JobPostToLocality;
import models.entity.Static.Locality;
import play.Logger;

import java.util.ArrayList;
import java.util.List;

import static com.avaje.ebean.Expr.eq;
import static controllers.businessLogic.MatchingEngineService.sortJobPostList;
import static models.util.Validator.isValidLocalityName;
import static play.libs.Json.toJson;

/**
 * Created by zero on 15/8/16.
 */
public class JobPostService {
    public static List<JobPostObject> getJobPostObjectListFromJobPostList(List<JobPost> jobPostList) {
        List<JobPostObject> jobPostListToReturn = new ArrayList<>();

        if (jobPostList == null) {
            return jobPostListToReturn;
        }

        for (models.entity.JobPost jobPost : jobPostList) {
            JobPostObject.Builder jobPostBuilder
                    = JobPostObject.newBuilder();
            jobPostBuilder.setJobPostCreationMillis(jobPost.getJobPostCreateTimestamp().getTime());
            jobPostBuilder.setJobPostId(jobPost.getJobPostId());
            jobPostBuilder.setJobPostTitle(jobPost.getJobPostTitle());
            jobPostBuilder.setJobPostCompanyName(jobPost.getCompany().getCompanyName());
            jobPostBuilder.setJobPostMinSalary(jobPost.getJobPostMinSalary());
            jobPostBuilder.setJobPostMaxSalary(jobPost.getJobPostMaxSalary());
            jobPostBuilder.setVacancies(jobPost.getJobPostVacancies());

            JobRoleObject.Builder jobRoleBuilder = JobRoleObject.newBuilder();

            if (jobPost.getJobRole() != null) {
                jobPostBuilder.setJobRole(jobPost.getJobRole().getJobName());
            }

            jobPostBuilder.setJobPostCompanyLogo(jobPost.getCompany().getCompanyLogo());

            ExperienceObject.Builder experienceBuilder = ExperienceObject.newBuilder();
            experienceBuilder.setExperienceId(jobPost.getJobPostExperience().getExperienceId());
            experienceBuilder.setExperienceType(jobPost.getJobPostExperience().getExperienceType());
            jobPostBuilder.setJobPostExperience(experienceBuilder);

            if (jobPost.getJobPostShift() != null) {
                TimeShiftObject.Builder timeShiftBuilder = TimeShiftObject.newBuilder();

                timeShiftBuilder.setTimeShiftId(jobPost.getJobPostShift().getTimeShiftId());
                timeShiftBuilder.setTimeShiftName(jobPost.getJobPostShift().getTimeShiftName());
                jobPostBuilder.setJobPostShift(timeShiftBuilder);
            }

            List<LocalityObject> jobPostLocalities = new ArrayList<>();
            List<JobPostToLocality> localityList = jobPost.getJobPostToLocalityList();
            for (JobPostToLocality locality : localityList) {
                LocalityObject.Builder localityBuilder
                        = LocalityObject.newBuilder();
                localityBuilder.setLocalityId(locality.getLocality().getLocalityId());
                localityBuilder.setLocalityName(locality.getLocality().getLocalityName());
                jobPostLocalities.add(localityBuilder.build());
            }
            jobPostBuilder.addAllJobPostLocality(jobPostLocalities);

            jobPostListToReturn.add(jobPostBuilder.build());
        }

        return jobPostListToReturn;
    }

    public static List<JobPost> mGetAllJobPostsRaw() {
        return JobPost.find.where()
                .eq("jobPostIsHot", ServerConstants.IS_HOT)
                .or(eq("source", null), eq("source", ServerConstants.SOURCE_INTERNAL))
                .findList();
    }

    public static List<JobPost> mGetAllJobPostsRaw(Integer sortOrder, List<Long> jobRoleIds) {
        if(sortOrder == null){
            sortOrder = ServerConstants.SORT_DEFAULT;
        }

        Query<JobPost> query = JobPost.find.query();
/*

        query = query
                .where()
                .eq("jobPostIsHot", IS_HOT)
                .query();
*/

        query = query
                .where()
                .or(eq("source", null), eq("source", ServerConstants.SOURCE_INTERNAL))
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



    public static Locality getOrCreateLocality(String localityName) {
        // validate localityName
        localityName = localityName.trim();
        if (localityName != null && isValidLocalityName(localityName)) {
            Locality locality = Locality.find.where().eq("localityName", localityName).findUnique();
            if (locality != null) {
                return locality;
            }
        }
        Locality locality = new Locality();
        locality.setLocalityName(localityName);
        locality.save();
        locality = Locality.find.where().eq("localityName", localityName).findUnique();
        return locality;
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
                jobPostList = mGetMatchingJobPostsByLatLngRaw(jobFilterRequest.getCandidateMobile(),
                        jobFilterRequest.getJobSearchLatitude(), jobFilterRequest.getJobSearchLongitude(), jobRoleIds, sortOrder);
            } else {
                /* filter over candidate's lat/lng or over all the the jobpost */
                jobPostList = mGetMatchingJobPostsRaw(jobFilterRequest.getCandidateMobile(), sortOrder, jobRoleIds);
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

    public static List<JobPost> mGetMatchingJobPostsByLatLngRaw(String mobile, Double latitude, Double longitude,
                                                                List<Long> jobRoleIds, Integer sortOrder) {
        mobile = FormValidator.convertToIndianMobileFormat(mobile);
        if (mobile != null && !mobile.trim().isEmpty()) {
            Logger.info("getMatchingJob for Mobile: " + mobile);
            Candidate existingCandidate = CandidateService.isCandidateExists(mobile);
            if (existingCandidate != null) {
                sortOrder = sortOrder == null ? ServerConstants.SORT_DEFAULT:sortOrder;
                return MatchingEngineService.fetchMatchingJobPostForLatLng(
                        latitude, longitude, null, jobRoleIds, sortOrder);
            }
        } else {
            Logger.info("Job Search Req with NO mobile Number.");
        }
        return MatchingEngineService.fetchMatchingJobPostForLatLng(
                latitude, longitude, null, jobRoleIds, sortOrder);
    }

    /**
     *
     * This method is called when lat/lng is 0 or not available
     * if its 0.0 then all jobs are returned
     *
     */
    public static List<JobPost> mGetMatchingJobPostsRaw(String mobile, Integer sortOrder, List<Long> jobRoleIds) {
        mobile = FormValidator.convertToIndianMobileFormat(mobile);
        if (mobile != null && !mobile.trim().isEmpty()) {
            Logger.info("getMatchingJob for Mobile: " + mobile);
            Candidate existingCandidate = CandidateService.isCandidateExists(mobile);
            if (existingCandidate != null) {
                if (existingCandidate.getCandidateLocalityLat() == null || existingCandidate.getCandidateLocalityLng() == null) {
                    return mGetAllJobPostsRaw();
                } else {
                    /*return MatchingEngineService.fetchMatchingJobPostForLatLng(
                            existingCandidate.getCandidateLocalityLat(), existingCandidate.getCandidateLocalityLng(), null
                            , jobRoleIds, sortOrder);*/
                    return mGetAllJobPostsRaw(sortOrder, jobRoleIds);
                }
            }
        } else {
            Logger.info("In mGetMatchingJobPostsRaw: No Mobile Number found. All job search triggered in App: ");
            return mGetAllJobPostsRaw(sortOrder, jobRoleIds);
        }
        return null;
    }
}
