package dao;

import models.entity.JobPost;
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
}
