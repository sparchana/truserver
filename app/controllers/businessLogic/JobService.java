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
        List<Integer> jobPostLocalityList = addJobPostRequest.getJobPostLocalities();
        JobPost newJobPost = new JobPost();

        newJobPost.setJobPostMinSalary(addJobPostRequest.getJobPostMinSalary());
        newJobPost.setJobPostMaxSalary(addJobPostRequest.getJobPostMaxSalary());
        newJobPost.setJobPostStartTime(addJobPostRequest.getJobPostStartTime());
        newJobPost.setJobPostEndTime(addJobPostRequest.getJobPostEndTime());
        newJobPost.setJobPostIsHot(addJobPostRequest.getJobPostIsHot());
        newJobPost.setJobPostDescription(addJobPostRequest.getJobPostDescription());
        newJobPost.setJobPostTitle(addJobPostRequest.getJobPostTitle());
        newJobPost.setJobPostIncentives(addJobPostRequest.getJobPostIncentives());
        newJobPost.setJobPostMinRequirement(addJobPostRequest.getJobPostMinRequirement());
        newJobPost.setJobPostAddress(addJobPostRequest.getJobPostAddress());
        newJobPost.setJobPostPinCode(addJobPostRequest.getJobPostPinCode());
        newJobPost.setJobPostVacancy(addJobPostRequest.getJobPostVacancy());
        newJobPost.setJobPostDescriptionAudio(addJobPostRequest.getJobPostDescriptionAudio());
        newJobPost.setJobPostWorkFromHome(addJobPostRequest.getJobPostWorkFromHome());

        newJobPost.setJobPostToLocalityList(getJobPostLocality(jobPostLocalityList, newJobPost));

        JobStatus jobStatus = JobStatus.find.where().eq("jobStatusId", addJobPostRequest.getJobPostStatusId()).findUnique();
        newJobPost.setJobPostStatus(jobStatus);

        JobRole jobRole = JobRole.find.where().eq("jobRoleId", addJobPostRequest.getJobPostJobRoleId()).findUnique();
        newJobPost.setJobRole(jobRole);

        Company company = Company.find.where().eq("companyId", addJobPostRequest.getJobPostCompanyId()).findUnique();
        newJobPost.setCompany(company);

        TimeShift timeShift = TimeShift.find.where().eq("timeShiftId", addJobPostRequest.getJobPostShiftId()).findUnique();
        newJobPost.setJobPostShift(timeShift);

        Experience experience = Experience.find.where().eq("experienceId", addJobPostRequest.getJobPostExperienceId()).findUnique();
        newJobPost.setJobPostExperience(experience);

        Education education = Education.find.where().eq("educationId", addJobPostRequest.getJobPostEducationId()).findUnique();
        newJobPost.setJobPostEducation(education);

        newJobPost.save();
        Logger.info("JobPost with jobId: " + newJobPost.getJobPostId() + " and job title: " + newJobPost.getJobPostTitle() + " created successfully");
        return 0;
    }

    public static ApplyJobResponse applyJob(String candidateMobile, Integer jobId) {
        Logger.info("checking user and jobId: " + candidateMobile + " + " + jobId);
        ApplyJobResponse applyJobResponse = new ApplyJobResponse();
        Candidate existingCandidate = CandidateService.isCandidateExists(candidateMobile);
        if(existingCandidate != null){
            JobPost existingJobPost = JobPost.find.where().eq("jobPostId",jobId).findUnique();
            if(existingJobPost == null ){
                applyJobResponse.setStatus(ApplyJobResponse.STATUS_NO_JOB);
                Logger.info("JobPost with jobId: " + jobId + " does not exists");
            }
            else{
                JobApplication existingJobApplication = JobApplication.find.where().eq("candidateId", existingCandidate.getCandidateId()).eq("jobPostId", jobId).findUnique();
                if(existingJobApplication == null){
                    JobApplication jobApplication = new JobApplication();
                    jobApplication.setCandidate(existingCandidate);
                    jobApplication.setJobPost(existingJobPost);
                    jobApplication.save();
                    Logger.info("candidate: " + existingCandidate.getCandidateFirstName() + " with mobile: " + existingCandidate.getCandidateMobile() + " applied to the jobPost of JobPostId:" + existingJobPost.getJobPostId());
                    applyJobResponse.setStatus(ApplyJobResponse.STATUS_SUCCESS);
                } else{
                    applyJobResponse.setStatus(ApplyJobResponse.STATUS_EXISTS);
                    Logger.info("candidate: " + existingCandidate.getCandidateFirstName() + " with mobile: " + existingCandidate.getCandidateMobile() + " already applied to jobPost with jobId:" + existingJobPost.getJobPostId());
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