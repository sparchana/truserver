package dao;

import api.ServerConstants;
import models.entity.JobPost;
import models.entity.ongrid.OnGridVerificationStatus;

import java.util.ArrayList;
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

    public static List<JobPost> getAllJobPostWithRecruitersWithInterviewCredits() {
        List<JobPost> jobPostListToReturn = new ArrayList<>();

        List<JobPost> jobPostList = JobPost.find.where()
                .isNotNull("JobRecruiterId")
                .eq("JobStatus", ServerConstants.JOB_STATUS_ACTIVE)
                .eq("Source", ServerConstants.SOURCE_INTERNAL)
                .findList();

        for(JobPost j: jobPostList){
            if(j.getRecruiterProfile().totalInterviewCredits() > 0){
                jobPostListToReturn.add(j);
            }
        }
        return jobPostListToReturn;
    }

}
