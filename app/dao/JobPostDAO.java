package dao;

import api.ServerConstants;
import com.avaje.ebean.Ebean;
import com.avaje.ebean.RawSql;
import com.avaje.ebean.RawSqlBuilder;
import models.entity.Company;
import models.entity.JobPost;
import models.entity.OM.JobApplication;
import play.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by zero on 18/12/16.
 */
public class JobPostDAO {
    public static JobPost findById(Long jobPostId) {
        if(jobPostId == null) return null;
        return JobPost.find.where().eq("jobPostId", jobPostId).findUnique();
    }

    public static List<JobPost> findByIdList(List<Long> jobPostIdList) {
        if(jobPostIdList == null || jobPostIdList.size() == 0) return null;
        return JobPost.find.where().in("jobPostId", jobPostIdList).findList();
    }

    public static List<JobPost> getAllActiveHotNonPrivateJobsPostOfCompany(List<Long> companyIdList) {
        return JobPost.find.where()
                .eq("job_post_access_level", ServerConstants.JOB_POST_TYPE_PRIVATE)
                .in("CompanyId", companyIdList)
                .eq("JobStatus", ServerConstants.JOB_STATUS_ACTIVE)
                .eq("Source", ServerConstants.SOURCE_INTERNAL)
                .eq("job_post_access_level", ServerConstants.JOB_POST_TYPE_PRIVATE)
                .orderBy().desc("jobPostUpdateTimestamp")
                .findList();
    }

    public static List<JobPost> getAllJobPostWithRecruitersWithInterviewCredits() {
        List<JobPost> jobPostListToReturn = new ArrayList<>();

        List<JobPost> jobPostList = JobPost.find.where()
                .isNotNull("JobRecruiterId")
                .eq("JobStatus", ServerConstants.JOB_STATUS_ACTIVE)
                .eq("Source", ServerConstants.SOURCE_INTERNAL)
                .findList();

        for(JobPost j: jobPostList){
            if(j.getRecruiterProfile().getInterviewCreditCount() > 0){
                jobPostListToReturn.add(j);
            }
        }
        return jobPostListToReturn;
    }

    public static List<JobApplication> getThisWeeksApplication(JobPost jobPost, int daysToDeduct) {

        String jobPostQueryBuilder = " SELECT jobapplicationid FROM jobapplication" +
                " where jobapplicationcreatetimestamp > curdate() - " + daysToDeduct + " and jobpostid = " +jobPost.getJobPostId();

        RawSql rawSql = RawSqlBuilder.parse(jobPostQueryBuilder)
                .columnMapping("jobapplicationid", "jobApplicationId")
                .create();

        return Ebean.find(JobApplication.class)
                .setRawSql(rawSql)
                .findList();

    }

    public static Map<?, JobPost> findMapByRecruiterId(Long targetRecruiterId, Integer accessLevel) {
        if(targetRecruiterId == null) return null;

        if(accessLevel == null) {
            accessLevel = 0;
        }
        return JobPost.find.where()
                .eq("recruiterProfile.recruiterProfileId", targetRecruiterId)
                .eq("jobPostAccessLevel", accessLevel)
                .setMapKey("jobPostId").findMap();
    }
}
