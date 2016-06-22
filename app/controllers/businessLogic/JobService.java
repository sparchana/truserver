package controllers.businessLogic;

import api.http.httpRequest.AddJobPostRequest;
import api.http.httpResponse.ApplyJobResponse;
import models.entity.Candidate;
import models.entity.Company;
import models.entity.JobPost;
import models.entity.OM.JobApplication;
import models.entity.OM.JobPostToLocality;
import models.entity.Static.*;
import play.Logger;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by batcoder1 on 17/6/16.
 */
public class JobService {
    public static Integer addJobPost(AddJobPostRequest addJobPostRequest) {
        List<Integer> jobPostLocalityList = addJobPostRequest.getJobPostLocality();
        JobPost newJobPost = new JobPost();

        newJobPost.setJobPostMinSalary(addJobPostRequest.getJobPostMinSalary());
        newJobPost.setJobPostMaxSalary(addJobPostRequest.getJobPostMaxSalary());
        newJobPost.setJobPostStartTime(addJobPostRequest.getJobPostStartTime());
        newJobPost.setJobPostEndTime(addJobPostRequest.getJobPostEndTime());
        newJobPost.setJobPostWorkFromHome(addJobPostRequest.getJobPostWorkFromHome());
        newJobPost.setJobPostDescription(addJobPostRequest.getJobPostDescription());
        newJobPost.setJobPostDescriptionAudio(addJobPostRequest.getJobPostDescriptionAudio());
        newJobPost.setJobPostTitle(addJobPostRequest.getJobPostTitle());
        newJobPost.setJobPostVacancy(addJobPostRequest.getJobPostVacancy());
        newJobPost.setJobPostToLocalityList(getJobPostLocality(jobPostLocalityList, newJobPost));

        JobStatus jobStatus = JobStatus.find.where().eq("jobStatusId", "1").findUnique();
        newJobPost.setJobPostStatus(jobStatus);

        JobRole jobRole = JobRole.find.where().eq("jobRoleId", "5").findUnique();
        newJobPost.setJobRole(jobRole);

        Company company = Company.find.where().eq("companyId","1").findUnique();
        newJobPost.setCompany(company);

        TimeShift timeShift = TimeShift.find.where().eq("timeShiftId","1").findUnique();
        newJobPost.setJobPostShift(timeShift);

        Experience experience = Experience.find.where().eq("experienceId", "1").findUnique();
        newJobPost.setJobPostExperience(experience);

        Education education = Education.find.where().eq("educationId","1").findUnique();
        newJobPost.setJobPostEducation(education);

        newJobPost.save();
        Logger.info("Sample JobPost Saved");

        return 0;
    }

    public static ApplyJobResponse applyJob(String candidateMobile, Integer jobId) {
        Logger.info(candidateMobile + " " + jobId);
        ApplyJobResponse applyJobResponse = new ApplyJobResponse();
        Candidate existingCandidate = CandidateService.isCandidateExists(candidateMobile);
        if(existingCandidate != null){
            JobPost existingJobPost = JobPost.find.where().eq("jobPostId",jobId).findUnique();
            if(existingJobPost == null ){
                applyJobResponse.setStatus(ApplyJobResponse.STATUS_NO_JOB);
                Logger.info("JobPost does not exists");
            }
            else{
                JobApplication existingJobApplication = JobApplication.find.where().eq("candidateId", existingCandidate.getCandidateId()).eq("jobPostId", jobId).findUnique();
                if(existingJobApplication == null){
                    JobApplication jobApplication = new JobApplication();
                    jobApplication.setCandidate(existingCandidate);
                    jobApplication.setJobPost(existingJobPost);
                    jobApplication.save();
                    Logger.info("Job Added Successfully");
                    applyJobResponse.setStatus(ApplyJobResponse.STATUS_SUCCESS);
                } else{
                    applyJobResponse.setStatus(ApplyJobResponse.STATUS_EXISTS);
                    Logger.info("Candidate Already Applied");
                }
            }
        } else{
            applyJobResponse.setStatus(ApplyJobResponse.STATUS_NO_CANDIDATE);
            Logger.info("Candidate Does not exists");
        }
        return applyJobResponse;
    }

    public static List<JobPostToLocality> getJobPostLocality(List<Integer> localityList, JobPost jobPost) {
        List<JobPostToLocality> jobPostToLocalityList = new ArrayList<>();
        for(Integer  localityId : localityList) {
            JobPostToLocality jobPostToLocality = new JobPostToLocality();
            jobPostToLocality.setJobPost(jobPost);
            jobPostToLocality.setJobPostToLocalityCreateTimeStamp(new Timestamp(System.currentTimeMillis()));
            Locality locality = Locality.find.where()
                    .eq("localityId", localityId).findUnique();
            jobPostToLocality.setLocality(locality);
            jobPostToLocalityList.add(jobPostToLocality);
        }
        return jobPostToLocalityList;
    }
}