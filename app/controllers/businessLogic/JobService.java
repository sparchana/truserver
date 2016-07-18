package controllers.businessLogic;

import api.ServerConstants;
import api.http.httpRequest.AddJobPostRequest;
import api.http.httpRequest.ApplyJobRequest;
import api.http.httpResponse.AddJobPostResponse;
import api.http.httpResponse.ApplyJobResponse;
import models.entity.Candidate;
import models.entity.Company;
import models.entity.JobPost;
import models.entity.OM.JobApplication;
import models.entity.OM.JobPostToLocality;
import models.entity.RecruiterProfile;
import models.entity.Static.*;
import models.util.SmsUtil;
import play.Logger;
import play.api.Play;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by batcoder1 on 17/6/16.
 */
public class JobService {
    public static AddJobPostResponse addJobPost(AddJobPostRequest addJobPostRequest) {
        AddJobPostResponse addJobPostResponse = new AddJobPostResponse();
        List<Integer> jobPostLocalityList = addJobPostRequest.getJobPostLocalities();
        /* checking if jobPost already exists or not */
        JobPost existingJobPost = JobPost.find.where().eq("jobPostId", addJobPostRequest.getJobPostId()).findUnique();
        if(existingJobPost == null){
            Logger.info("Job post does not exists. Creating a new job Post");
            JobPost newJobPost = new JobPost();
            newJobPost = getAndSetJobPostValues(addJobPostRequest, newJobPost, jobPostLocalityList);
            newJobPost.save();
            addJobPostResponse.setJobPost(newJobPost);
            addJobPostResponse.setStatus(AddJobPostResponse.STATUS_SUCCESS);
            Logger.info("JobPost with jobId: " + newJobPost.getJobPostId() + " and job title: " + newJobPost.getJobPostTitle() + " created successfully");
        } else{
            Logger.info("Job post already exists. Updating existing job Post");
            existingJobPost = getAndSetJobPostValues(addJobPostRequest, existingJobPost, jobPostLocalityList);
            existingJobPost.update();
            addJobPostResponse.setJobPost(existingJobPost);
            addJobPostResponse.setStatus(AddJobPostResponse.STATUS_UPDATE_SUCCESS);
            Logger.info("JobPost with jobId: " + existingJobPost.getJobPostId() + " and job title: " + existingJobPost.getJobPostTitle() + " updated successfully");
        }
        if(Play.isDev(Play.current()) == false){
            addJobPostResponse.setFormUrl(ServerConstants.PROD_GOOGLE_FORM_FOR_JOB_POSTS);
        } else{
            addJobPostResponse.setFormUrl(ServerConstants.DEV_GOOGLE_FORM_FOR_JOB_POSTS);
        }
        return addJobPostResponse;
    }

    public static JobPost getAndSetJobPostValues(AddJobPostRequest addJobPostRequest, JobPost newJobPost, List<Integer> jobPostLocalityList){
        newJobPost.setJobPostMinSalary(addJobPostRequest.getJobPostMinSalary());
        newJobPost.setJobPostMaxSalary(addJobPostRequest.getJobPostMaxSalary());
        newJobPost.setJobPostStartTime(addJobPostRequest.getJobPostStartTime());
        newJobPost.setJobPostEndTime(addJobPostRequest.getJobPostEndTime());
        newJobPost.setJobPostIsHot(addJobPostRequest.getJobPostIsHot());
        newJobPost.setJobPostIsHot(addJobPostRequest.getJobPostIsHot());
        newJobPost.setJobPostDescription(addJobPostRequest.getJobPostDescription());
        newJobPost.setJobPostTitle(addJobPostRequest.getJobPostTitle());
        newJobPost.setJobPostIncentives(addJobPostRequest.getJobPostIncentives());
        newJobPost.setJobPostMinRequirement(addJobPostRequest.getJobPostMinRequirement());
        newJobPost.setJobPostAddress(addJobPostRequest.getJobPostAddress());
        newJobPost.setJobPostPinCode(addJobPostRequest.getJobPostPinCode());
        newJobPost.setJobPostVacancies(addJobPostRequest.getJobPostVacancies());
        newJobPost.setJobPostDescriptionAudio(addJobPostRequest.getJobPostDescriptionAudio());
        newJobPost.setJobPostWorkFromHome(addJobPostRequest.getJobPostWorkFromHome());

        if (addJobPostRequest.getJobPostWorkingDays() != null) {
            Byte workingDayByte = Byte.parseByte(addJobPostRequest.getJobPostWorkingDays(), 2);
            newJobPost.setJobPostWorkingDays(workingDayByte);
        }
        newJobPost.setJobPostToLocalityList(getJobPostLocality(jobPostLocalityList, newJobPost));

        PricingPlanType pricingPlanType = PricingPlanType.find.where().eq("pricingPlanTypeId", addJobPostRequest.getJobPostPricingPlanId()).findUnique();
        newJobPost.setPricingPlanType(pricingPlanType);

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

        RecruiterProfile recruiterProfile = RecruiterProfile.find.where().eq("recruiterProfileId", addJobPostRequest.getJobPostRecruiterId()).findUnique();
        newJobPost.setRecruiterProfile(recruiterProfile);

        return newJobPost;
    }

    public static ApplyJobResponse applyJob(ApplyJobRequest applyJobRequest) {
        Logger.info("checking user and jobId: " + applyJobRequest.getCandidateMobile() + " + " + applyJobRequest.getJobId());
        ApplyJobResponse applyJobResponse = new ApplyJobResponse();
        Candidate existingCandidate = CandidateService.isCandidateExists(applyJobRequest.getCandidateMobile());
        if(existingCandidate != null){
            JobPost existingJobPost = JobPost.find.where().eq("jobPostId",applyJobRequest.getJobId()).findUnique();
            if(existingJobPost == null ){
                applyJobResponse.setStatus(ApplyJobResponse.STATUS_NO_JOB);
                Logger.info("JobPost with jobId: " + applyJobRequest.getJobId() + " does not exists");
            }
            else{
                JobApplication existingJobApplication = JobApplication.find.where().eq("candidateId", existingCandidate.getCandidateId()).eq("jobPostId", applyJobRequest.getJobId()).findUnique();
                if(existingJobApplication == null){
                    JobApplication jobApplication = new JobApplication();
                    jobApplication.setCandidate(existingCandidate);
                    jobApplication.setJobPost(existingJobPost);

                    Locality locality = Locality.find.where().eq("localityId", applyJobRequest.getLocalityId()).findUnique();
                    if(locality != null){
                        jobApplication.setLocality(locality);
                    } else{
                        Logger.info("Location with locality ID: " + applyJobRequest.getLocalityId() + " does not exists");
                    }

                    String interactionResult = ServerConstants.INTERACTION_RESULT_CANDIDATE_SELF_APPLIED_JOB;
                    InteractionService.createInteractionForJobApplication(existingCandidate.getCandidateUUId(), existingJobPost.getJobPostUUId(), interactionResult + existingJobPost.getJobPostTitle() + " at " + existingJobPost.getCompany().getCompanyName() + "@" + locality.getLocalityName() );

                    jobApplication.save();
                    Logger.info("candidate: " + existingCandidate.getCandidateFirstName() + " with mobile: " + existingCandidate.getCandidateMobile() + " applied to the jobPost of JobPostId:" + existingJobPost.getJobPostId());

                    SmsUtil.sendJobApplicationSms(existingCandidate.getCandidateFirstName(), existingJobPost.getJobPostTitle(), existingJobPost.getCompany().getCompanyName(), existingCandidate.getCandidateMobile(), jobApplication.getLocality().getLocalityName());
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