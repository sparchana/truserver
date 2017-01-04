package dao;

import api.ServerConstants;
import com.avaje.ebean.Ebean;
import com.avaje.ebean.RawSql;
import com.avaje.ebean.RawSqlBuilder;
import models.entity.JobPost;
import models.entity.OM.JobApplication;
import models.entity.OM.JobPostWorkflow;
import models.entity.ongrid.OnGridVerificationStatus;

import java.util.List;

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
}
