package controllers.businessLogic;

import api.GoogleSheetHttpRequest;
import api.InteractionConstants;
import api.ServerConstants;
import api.http.FormValidator;
import api.http.httpRequest.AddCandidateRequest;
import api.http.httpRequest.AddJobPostRequest;
import api.http.httpRequest.ApplyJobRequest;
import api.http.httpResponse.AddJobPostResponse;
import api.http.httpResponse.ApplyJobResponse;
import api.http.httpResponse.CallToApplyResponse;
import api.http.httpResponse.CandidateWorkflowData;
import api.http.httpResponse.Workflow.PreScreenPopulateResponse;
import api.http.httpResponse.interview.InterviewDateTime;
import api.http.httpResponse.interview.InterviewResponse;
import com.amazonaws.util.json.JSONException;
import com.avaje.ebean.Model;
import controllers.businessLogic.JobWorkflow.JobPostWorkflowEngine;
import controllers.scheduler.SchedulerConstants;
import dao.JobPostDAO;
import dao.JobPostWorkFlowDAO;
import models.entity.Candidate;
import models.entity.Company;
import models.entity.JobPost;
import models.entity.OM.*;
import models.entity.Partner;
import models.entity.Recruiter.RecruiterProfile;
import models.entity.Static.*;
import models.util.EmailUtil;
import models.util.InterviewUtil;
import models.util.NotificationUtil;
import models.util.SmsUtil;
import play.Logger;
import play.api.Play;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.sql.Timestamp;
import java.util.*;

import static api.InteractionConstants.*;
import static controllers.businessLogic.InteractionService.createInteractionForNewJobPost;
import static models.util.EmailUtil.sendRecruiterJobPostLiveEmail;
import static models.util.InterviewUtil.getDayVal;
import static models.util.InterviewUtil.getMonthVal;
import static models.util.SmsUtil.sendRecruiterFreeJobPostingSms;
import static models.util.SmsUtil.sendRecruiterJobPostActivationSms;
import static play.mvc.Controller.session;

/**
 * Created by batcoder1 on 17/6/16.
 */
public class JobService {
    public static AddJobPostResponse addJobPost(AddJobPostRequest addJobPostRequest,
                                                int channelType)
    {
        AddJobPostResponse addJobPostResponse = new AddJobPostResponse();
        List<Integer> jobPostLocalityList = addJobPostRequest.getJobPostLocalities();
        /* checking if jobPost already exists or not */

        String createdBy;
        String objAUuid = "";
        String objBUuid;
        String result;
        Integer channel;
        Integer objAType;
        Integer interactionType;
        boolean isSendJobActivationAlert = false;

        /* TODO add validation for critical incoming data like localityList etc */

        JobPost existingJobPost = JobPostDAO.findById(addJobPostRequest.getJobPostId());
        if(existingJobPost == null){
            Logger.info("Job post does not exists. Creating a new job Post");
            existingJobPost = new JobPost();
            existingJobPost = getAndSetJobPostValues(addJobPostRequest, existingJobPost, jobPostLocalityList);

            existingJobPost.save();

            if(addJobPostRequest.getInterviewTimeSlot() != null)
                createInterviewDetails(addJobPostRequest, existingJobPost);

            saveOrUpdatePreScreenRequirements(existingJobPost);

            addJobPostResponse.setJobPost(existingJobPost);
            addJobPostResponse.setStatus(AddJobPostResponse.STATUS_SUCCESS);

            // if support creates a job post in new status, no alert is sent to recruiter
            if (existingJobPost.getJobPostStatus().getJobStatusId() == ServerConstants.JOB_STATUS_ACTIVE) {
                isSendJobActivationAlert = true;
            } else if (channelType == InteractionConstants.INTERACTION_CHANNEL_CANDIDATE_WEBSITE) {
                // These sms and mail alerts are sent only in case of self-job posts by recruiter
                // in this case the default status is 'new'
                // if support creates a job post in new status, no alert is sent to recruiter
                RecruiterProfile recruiterProfile =
                        RecruiterProfile.find.where().eq("RecruiterProfileId", session().get("recruiterId")).findUnique();
                if(recruiterProfile != null){
                    sendRecruiterFreeJobPostingSms(recruiterProfile.getRecruiterProfileMobile(),
                            recruiterProfile.getRecruiterProfileName());

                    EmailUtil.sendRecruiterNewJobPostEmail(recruiterProfile, existingJobPost);
                }
            }

            objBUuid = existingJobPost.getJobPostUUId();
            result = InteractionConstants.INTERACTION_RESULT_NEW_JOB_CREATED + " with status "
                    + existingJobPost.getJobPostStatus().getJobStatusName();

            interactionType = InteractionConstants.INTERACTION_TYPE_NEW_JOB_CREATED;
            Logger.info("JobPost with jobId: " + existingJobPost.getJobPostId() + " and job title: "
                    + existingJobPost.getJobPostTitle() + " created successfully");

        } else {
            Logger.info("Job post already exists. Updating existing job Post");
            Integer previousStatus = 0;

            if(existingJobPost.getJobPostStatus() != null){
                previousStatus = existingJobPost.getJobPostStatus().getJobStatusId();
            }

            existingJobPost = getAndSetJobPostValues(addJobPostRequest, existingJobPost, jobPostLocalityList);

            //trigger sms and mail when a job post is made 'Active' from 'New'
            if(addJobPostRequest.getJobPostStatusId() != null){
                if(addJobPostRequest.getJobPostStatusId() == ServerConstants.JOB_STATUS_ACTIVE
                        && previousStatus == ServerConstants.JOB_STATUS_NEW)
                {
                    isSendJobActivationAlert = true;
                }
            }

            if(addJobPostRequest.getInterviewTimeSlot() != null){
                resetInterviewDetails(addJobPostRequest, existingJobPost);
                createInterviewDetails(addJobPostRequest, existingJobPost);
            }
            existingJobPost.update();

            saveOrUpdatePreScreenRequirements(existingJobPost);

            addJobPostResponse.setJobPost(existingJobPost);
            addJobPostResponse.setStatus(AddJobPostResponse.STATUS_UPDATE_SUCCESS);

            objBUuid = existingJobPost.getJobPostUUId();

            result = InteractionConstants.INTERACTION_RESULT_EXISTING_JOB_POST_UPDATED;
            interactionType = InteractionConstants.INTERACTION_TYPE_EXISTING_JOB_UPDATED;

            Logger.info("JobPost with jobId: " + existingJobPost.getJobPostId() + " and job title: " + existingJobPost.getJobPostTitle() + " updated successfully");
        }

        if (isSendJobActivationAlert) {
            Logger.info("Notifying recruiter about the job");

            //trigger SMS to recruiter
            sendRecruiterJobPostActivationSms(existingJobPost.getRecruiterProfile(), existingJobPost);

            //send email to recruiter
            sendRecruiterJobPostLiveEmail(existingJobPost.getRecruiterProfile(), existingJobPost);

            //send sms to all the matching candidate
            JobPost jobPost = existingJobPost;

            new Thread(() -> {
                sendSmsToCandidateMatchingWithJobPost(jobPost);
            }).start();

        }

        if(channelType == INTERACTION_CHANNEL_SUPPORT_WEBSITE){
            createdBy = session().get("sessionUsername");
            objAUuid = ServerConstants.SUPPORT_DEFAULT_UUID;
            objAType = ServerConstants.OBJECT_TYPE_SUPPORT;
            channel = INTERACTION_CHANNEL_SUPPORT_WEBSITE;
        } else {
            createdBy = InteractionConstants.INTERACTION_CREATED_SELF;
            objAType = ServerConstants.OBJECT_TYPE_RECRUTER;
            channel = InteractionConstants.INTERACTION_CHANNEL_RECRUITER_WEBSITE;

            RecruiterProfile recruiterProfile = RecruiterProfile.find.where().eq("RecruiterProfileId", session().get("recruiterId")).findUnique();
            if(recruiterProfile != null){
                objAUuid = recruiterProfile.getRecruiterProfileUUId();
            }
        }

        //creating interaction
        createInteractionForNewJobPost(objAUuid, objBUuid, objAType, interactionType, result, createdBy, channel);

        if(Play.isDev(Play.current()) == false){
            addJobPostResponse.setFormUrl(ServerConstants.PROD_GOOGLE_FORM_FOR_JOB_POSTS);
        } else{
            addJobPostResponse.setFormUrl(ServerConstants.DEV_GOOGLE_FORM_FOR_JOB_POSTS);
        }
        return addJobPostResponse;
    }

    private static void resetInterviewDetails(AddJobPostRequest addJobPostRequest, JobPost existingJobPost) {
        List<InterviewDetails> interviewDetailList = InterviewDetails.find.where().eq("jobPost.jobPostId", addJobPostRequest.getJobPostId()).findList();
        if(interviewDetailList.size() > 0) {
            interviewDetailList.forEach(Model::delete);
        }
    }

    private static void createInterviewDetails(AddJobPostRequest addJobPostRequest, JobPost jobPost){
        List<Integer> interviewSlots = addJobPostRequest.getInterviewTimeSlot();

        if(interviewSlots != null){
            Boolean flag = false;
            String interviewDays = addJobPostRequest.getJobPostInterviewDays();
            for(int i = 0; i<interviewDays.length(); i++){
                if(interviewDays.charAt(i) == '1'){
                    flag = true;
                    break;
                }
            }

            if(jobPost != null){
                List<InterviewDetails> interviewDetailsList = InterviewDetails.find.where().eq("JobPostId", jobPost.getJobPostId()).findList();
                for(InterviewDetails interviewDetails: interviewDetailsList){
                    interviewDetails.delete();
                }
            }
            //create multiple entries in interview details table
            for(Integer slot: interviewSlots){
                InterviewDetails interviewDetails = new InterviewDetails();
                interviewDetails.setJobPost(jobPost);
                InterviewTimeSlot interviewTimeSlot = InterviewTimeSlot.find.where().eq("interview_time_slot_id", slot).findUnique();

                if(interviewTimeSlot != null){
                    interviewDetails.setInterviewTimeSlot(interviewTimeSlot);
                }

                if(flag){
                    Byte interviewDaysByte = Byte.parseByte(addJobPostRequest.getJobPostInterviewDays(), 2);
                    interviewDetails.setInterviewDays(interviewDaysByte);
                }

                interviewDetails.save();
            }
            Logger.info("Interview details saved");
        }
    }

    public static JobPost getAndSetJobPostValues(AddJobPostRequest addJobPostRequest,
                                                 JobPost newJobPost,
                                                 List<Integer> jobPostLocalityList)
    {

        if(addJobPostRequest.getJobPostMinSalary() != null)
            newJobPost.setJobPostMinSalary(addJobPostRequest.getJobPostMinSalary());

        if(addJobPostRequest.getJobPostMaxSalary() != null)
            newJobPost.setJobPostMaxSalary(addJobPostRequest.getJobPostMaxSalary());

        if(addJobPostRequest.getJobPostStartTime() != null)
            newJobPost.setJobPostStartTime(addJobPostRequest.getJobPostStartTime());

        if(addJobPostRequest.getJobPostEndTime() != null)
            newJobPost.setJobPostEndTime(addJobPostRequest.getJobPostEndTime());

        if(addJobPostRequest.getJobPostIsHot() != null)
            newJobPost.setJobPostIsHot(addJobPostRequest.getJobPostIsHot());

        if(addJobPostRequest.getJobPostDescription() != null)
            newJobPost.setJobPostDescription(addJobPostRequest.getJobPostDescription());

        if(addJobPostRequest.getJobPostTitle() != null)
            newJobPost.setJobPostTitle(addJobPostRequest.getJobPostTitle());

        if(addJobPostRequest.getJobPostIncentives() != null)
            newJobPost.setJobPostIncentives(addJobPostRequest.getJobPostIncentives());

        if(addJobPostRequest.getJobPostMinRequirement() != null)
            newJobPost.setJobPostMinRequirement(addJobPostRequest.getJobPostMinRequirement());

        if(addJobPostRequest.getJobPostInterviewLocationLat() != null)
            newJobPost.setLatitude(addJobPostRequest.getJobPostInterviewLocationLat());

        if(addJobPostRequest.getJobPostInterviewLocationLng() != null)
            newJobPost.setLongitude(addJobPostRequest.getJobPostInterviewLocationLng());

        if(addJobPostRequest.getReviewApplications() != null)
            newJobPost.setReviewApplication(addJobPostRequest.getReviewApplications());

        if(addJobPostRequest.getJobPostAddress() != null)
            newJobPost.setJobPostAddress(addJobPostRequest.getJobPostAddress());

        if(addJobPostRequest.getJobPostPinCode() != null)
            newJobPost.setJobPostPinCode(addJobPostRequest.getJobPostPinCode());

        if(addJobPostRequest.getJobPostAddressBuildingNo() != null)
            newJobPost.setInterviewBuildingNo(addJobPostRequest.getJobPostAddressBuildingNo());

        if(addJobPostRequest.getJobPostAddressLandmark() != null)
            newJobPost.setInterviewLandmark(addJobPostRequest.getJobPostAddressLandmark());

        if(addJobPostRequest.getJobPostVacancies() != null)
            newJobPost.setJobPostVacancies(addJobPostRequest.getJobPostVacancies());

        if(addJobPostRequest.getJobPostDescriptionAudio() != null)
            newJobPost.setJobPostDescriptionAudio(addJobPostRequest.getJobPostDescriptionAudio());

        if(addJobPostRequest.getJobPostWorkFromHome() != null)
            newJobPost.setJobPostWorkFromHome(addJobPostRequest.getJobPostWorkFromHome());

        if(addJobPostRequest.getPartnerInterviewIncentive() != null)
            newJobPost.setJobPostPartnerInterviewIncentive(addJobPostRequest.getPartnerInterviewIncentive());

        if(addJobPostRequest.getPartnerJoiningIncentive() != null)
            newJobPost.setJobPostPartnerJoiningIncentive(addJobPostRequest.getPartnerJoiningIncentive());

        if(jobPostLocalityList != null){
            newJobPost.setJobPostToLocalityList(getJobPostLocality(jobPostLocalityList, newJobPost));
        }

        if(addJobPostRequest.getJobPostGender() != null)
            newJobPost.setGender(addJobPostRequest.getJobPostGender());

        if(addJobPostRequest.getJobPostLanguage() != null)
            newJobPost.setJobPostLanguageRequirements(getJobPostLanguageRequirement(addJobPostRequest.getJobPostLanguage(), newJobPost));

        if(addJobPostRequest.getJobPostAsset() != null)
            newJobPost.setJobPostAssetRequirements(getJobPostAssetRequirement(addJobPostRequest.getJobPostAsset(), newJobPost));

        if(addJobPostRequest.getJobPostDocument() != null)
            newJobPost.setJobPostDocumentRequirements(getJobPostDocumentRequirement(addJobPostRequest.getJobPostDocument(), newJobPost));

        if(addJobPostRequest.getJobPostMaxAge() != null)
            newJobPost.setJobPostMaxAge(addJobPostRequest.getJobPostMaxAge());

        if (addJobPostRequest.getJobPostWorkingDays() != null) {
            Byte workingDayByte = Byte.parseByte(addJobPostRequest.getJobPostWorkingDays(), 2);
            newJobPost.setJobPostWorkingDays(workingDayByte);
        }

        if (addJobPostRequest.getJobPostPricingPlanId() != null) {
            PricingPlanType pricingPlanType = PricingPlanType.find.where().eq("pricingPlanTypeId", addJobPostRequest.getJobPostPricingPlanId()).findUnique();
            newJobPost.setPricingPlanType(pricingPlanType);
        }

        if (addJobPostRequest.getJobPostStatusId() != null) {

            JobStatus jobStatus = JobStatus.find.where().eq("jobStatusId", addJobPostRequest.getJobPostStatusId()).findUnique();
            newJobPost.setJobPostStatus(jobStatus);
            if(session().get("recruiterId") != null) {
                RecruiterProfile recruiterProfile = RecruiterProfile.find.where().eq("recruiterProfileId", session().get("recruiterId")).findUnique();
                if (recruiterProfile != null && recruiterProfile.getRecruiterAccessLevel() >= ServerConstants.RECRUITER_ACCESS_LEVEL_PRIVATE) {

                    //setting job status and job post access level as active whn a private recruiter adds a job

                    newJobPost.setJobPostAccessLevel(ServerConstants.JOB_POST_TYPE_PRIVATE);

                    jobStatus = JobStatus.find.where().eq("jobStatusId", ServerConstants.JOB_STATUS_ACTIVE).findUnique();
                    newJobPost.setJobPostStatus(jobStatus);
                }
            }

            if(addJobPostRequest.getJobPostStatusId() == ServerConstants.JOB_STATUS_PAUSED){
                jobStatus = JobStatus.find.where().eq("jobStatusId", addJobPostRequest.getJobPostStatusId()).findUnique();
                newJobPost.setJobPostStatus(jobStatus);
                newJobPost.setResumeApplicationDate(addJobPostRequest.getResumeApplicationDate());

                if(newJobPost.getJobPostId() != null){
                    Calendar now = Calendar.getInstance();
                    Date today = now.getTime();

                    List<JobPostWorkflow> jobPostWorkflowList =
                            JobPostWorkFlowDAO.getConfirmedInterviewsBetweenDate(
                                    newJobPost.getJobPostId(),
                                    today, addJobPostRequest.getResumeApplicationDate());

                    for(JobPostWorkflow jobPostWorkflow : jobPostWorkflowList){
                        SmsUtil.sendPausedJobSmsAlert(jobPostWorkflow);
                    }
                }

            } else{
                newJobPost.setResumeApplicationDate(null);
            }

            if(addJobPostRequest.getJobPostStatusId() == ServerConstants.JOB_STATUS_CLOSED){
                jobStatus = JobStatus.find.where().eq("jobStatusId", addJobPostRequest.getJobPostStatusId()).findUnique();
                newJobPost.setJobPostStatus(jobStatus);

                Calendar now = Calendar.getInstance();
                Date today = now.getTime();

                List<JobPostWorkflow> jobPostWorkflowList =
                        JobPostWorkFlowDAO.getAllConfirmedInterviewsFromToday(
                                newJobPost.getJobPostId(),
                                today);

                for(JobPostWorkflow jobPostWorkflow : jobPostWorkflowList){
                    SmsUtil.sendClosedJobSmsAlert(jobPostWorkflow);
                }
            }
        } else{
            JobStatus jobStatus = JobStatus.find.where().eq("jobStatusId", ServerConstants.JOB_STATUS_ACTIVE).findUnique();
            newJobPost.setJobPostStatus(jobStatus);
            newJobPost.setResumeApplicationDate(null);
        }

        if (addJobPostRequest.getJobPostJobRoleId() != null) {
            JobRole jobRole = JobRole.find.where().eq("jobRoleId", addJobPostRequest.getJobPostJobRoleId()).findUnique();
            newJobPost.setJobRole(jobRole);
        }

        if (addJobPostRequest.getJobPostCompanyId() != null) {
            Company company = Company.find.where().eq("companyId", addJobPostRequest.getJobPostCompanyId()).findUnique();
            newJobPost.setCompany(company);
        }

        if (addJobPostRequest.getJobPostShiftId() != null) {
            TimeShift timeShift = TimeShift.find.where().eq("timeShiftId", addJobPostRequest.getJobPostShiftId()).findUnique();
            newJobPost.setJobPostShift(timeShift);
        }

        if (addJobPostRequest.getJobPostExperienceId() != null) {
            Experience experience = Experience.find.where().eq("experienceId", addJobPostRequest.getJobPostExperienceId()).findUnique();
            newJobPost.setJobPostExperience(experience);
        }

        if (addJobPostRequest.getJobPostEducationId() != null) {
            Education education = Education.find.where().eq("educationId", addJobPostRequest.getJobPostEducationId()).findUnique();
            newJobPost.setJobPostEducation(education);
        }
        if (addJobPostRequest.getJobPostRecruiterId() != null) {
            RecruiterProfile recruiterProfile = RecruiterProfile.find.where().eq("recruiterProfileId", addJobPostRequest.getJobPostRecruiterId()).findUnique();
            newJobPost.setRecruiterProfile(recruiterProfile);
        }
        return newJobPost;
    }

    private static void saveOrUpdatePreScreenRequirements(JobPost jobPost) {
        if (jobPost == null) {
            return;
        }

        // TODO find a simpler approach *****

        Integer jobPostMaxAge = jobPost.getJobPostMaxAge();
        Experience jobPostExperience = jobPost.getJobPostExperience();
        Education jobPostEducation = jobPost.getJobPostEducation();
        List<JobPostLanguageRequirement> jobPostLanguageRequirementList = jobPost.getJobPostLanguageRequirements();
        List<JobPostDocumentRequirement> jobPostDocumentRequirementList = jobPost.getJobPostDocumentRequirements();
        List<JobPostAssetRequirement> jobPostAssetRequirementList = jobPost.getJobPostAssetRequirements();

        List<PreScreenRequirement> preScreenRequirementList = PreScreenRequirement.find.where().eq("jobPost.jobPostId", jobPost.getJobPostId()).findList();
        Map<?, ProfileRequirement> profileRequirementMap = ProfileRequirement.find.where().setMapKey("profileRequirementTitle").findMap();

        Map<String, PreScreenRequirement> singleEntityMap= new HashMap<>();
        Map<Integer, Map<Integer, PreScreenRequirement>> multiEntityMap = new HashMap<>();

        if(preScreenRequirementList.size() > 0 ) {
            for (PreScreenRequirement ps: preScreenRequirementList) {
                if( ps.getCategory() != ServerConstants.CATEGORY_PROFILE) {
                    // categories like docs, lang, asset will come here
                    Map<Integer, PreScreenRequirement> psMap = multiEntityMap.get(ps.getCategory());
                    if(psMap == null) {
                        psMap = new HashMap<>();
                    }
                    if(ps.getCategory() == ServerConstants.CATEGORY_DOCUMENT) {
                        psMap.put(ps.getIdProof().getIdProofId(), ps);
                    } else if (ps.getCategory() == ServerConstants.CATEGORY_LANGUAGE) {
                        psMap.put(ps.getLanguage().getLanguageId(), ps);
                    } else if (ps.getCategory() == ServerConstants.CATEGORY_ASSET) {
                        psMap.put(ps.getAsset().getAssetId(), ps);
                    }
                    multiEntityMap.put(ps.getCategory(), psMap);
                } else {
                    // categories with single entry per jobPost will accumulate here
                    singleEntityMap.put(ps.getProfileRequirement().getProfileRequirementTitle(), ps);
                }
            }
        }

        if(jobPostMaxAge != null) {
            PreScreenRequirement preScreenRequirementAge = singleEntityMap.get(ServerConstants.PROFILE_REQUIREMENT_TABLE_AGE);
            if (preScreenRequirementAge == null) {
                preScreenRequirementAge = new PreScreenRequirement();
                preScreenRequirementAge.setCategory(ServerConstants.CATEGORY_PROFILE);
                preScreenRequirementAge.setJobPost(jobPost);
            }
            preScreenRequirementAge.setProfileRequirement(profileRequirementMap.get(ServerConstants.PROFILE_REQUIREMENT_TABLE_AGE));
            preScreenRequirementAge.save();
        } else {
            PreScreenRequirement preScreenRequirementAge = singleEntityMap.get(ServerConstants.PROFILE_REQUIREMENT_TABLE_AGE);
            if(preScreenRequirementAge != null) {
                deletePreScreenResponses(preScreenRequirementAge);
                preScreenRequirementAge.delete();
            }
        }

        if (jobPostExperience != null) {
            PreScreenRequirement preScreenRequirementExp =  singleEntityMap.get(ServerConstants.PROFILE_REQUIREMENT_TABLE_EXPERIENCE);
            if (preScreenRequirementExp == null) {
                preScreenRequirementExp = new PreScreenRequirement();
                preScreenRequirementExp.setCategory(ServerConstants.CATEGORY_PROFILE);
                preScreenRequirementExp.setJobPost(jobPost);
            }
            preScreenRequirementExp.setProfileRequirement(profileRequirementMap.get(ServerConstants.PROFILE_REQUIREMENT_TABLE_EXPERIENCE));
            preScreenRequirementExp.save();
        } else {
            PreScreenRequirement preScreenRequirementExp =  singleEntityMap.get(ServerConstants.PROFILE_REQUIREMENT_TABLE_EXPERIENCE);
            if(preScreenRequirementExp != null){
                deletePreScreenResponses(preScreenRequirementExp);
                preScreenRequirementExp.delete();
            }
        }
        if (jobPostEducation != null) {
            PreScreenRequirement preScreenRequirementEdu = singleEntityMap.get(ServerConstants.PROFILE_REQUIREMENT_TABLE_EDUCATION);
            if (preScreenRequirementEdu == null) {
                preScreenRequirementEdu = new PreScreenRequirement();
                preScreenRequirementEdu.setCategory(ServerConstants.CATEGORY_PROFILE);
                preScreenRequirementEdu.setJobPost(jobPost);
            }
            preScreenRequirementEdu.setProfileRequirement(profileRequirementMap.get(ServerConstants.PROFILE_REQUIREMENT_TABLE_EDUCATION));
            preScreenRequirementEdu.save();
        } else {
            PreScreenRequirement preScreenRequirementEdu = singleEntityMap.get(ServerConstants.PROFILE_REQUIREMENT_TABLE_EDUCATION);
            if(preScreenRequirementEdu != null){
                deletePreScreenResponses(preScreenRequirementEdu);
                preScreenRequirementEdu.delete();
            }
        }

        if(jobPostLanguageRequirementList != null && jobPostLanguageRequirementList.size() > 0) {
            Map<Integer, PreScreenRequirement> map = multiEntityMap.get(ServerConstants.CATEGORY_LANGUAGE);
            List<Integer> idList = new ArrayList<>();
            for (JobPostLanguageRequirement languageRequirement: jobPostLanguageRequirementList) {
                idList.add(languageRequirement.getLanguage().getLanguageId());

                PreScreenRequirement preScreenRequirementLanguage = null;
                if(map != null) {
                    preScreenRequirementLanguage = map.get(languageRequirement.getLanguage().getLanguageId());
                }
                if(preScreenRequirementLanguage == null) {
                    preScreenRequirementLanguage = new PreScreenRequirement();
                    preScreenRequirementLanguage.setJobPost(jobPost);
                    preScreenRequirementLanguage.setCategory(ServerConstants.CATEGORY_LANGUAGE);
                }
                preScreenRequirementLanguage.setLanguage(languageRequirement.getLanguage());
                preScreenRequirementLanguage.save();
            }
            if (map != null) {
                // TODO Simplify this method
                for (Map.Entry<Integer, PreScreenRequirement> entry : map.entrySet()) {
                    if (!idList.contains(entry.getValue().getLanguage().getLanguageId())) {
                        deletePreScreenResponses(entry.getValue());
                        entry.getValue().delete();
                    }
                }
            }
        } else {
            Map<Integer, PreScreenRequirement> map = multiEntityMap.get(ServerConstants.CATEGORY_LANGUAGE);
            if(map != null) {
                for (Map.Entry<Integer, PreScreenRequirement> entry : map.entrySet()) {
                    deletePreScreenResponses(entry.getValue());
                    entry.getValue().delete();
                }
            }
        }

        if(jobPostDocumentRequirementList != null && jobPostDocumentRequirementList.size() > 0) {
            Map<Integer, PreScreenRequirement> map = multiEntityMap.get(ServerConstants.CATEGORY_DOCUMENT);
            List<Integer> idList = new ArrayList<>();
            for ( JobPostDocumentRequirement jobPostDocumentRequirement: jobPostDocumentRequirementList) {
                PreScreenRequirement preScreenRequirementDocument = null;
                idList.add(jobPostDocumentRequirement.getIdProof().getIdProofId());
                if(map != null) {
                    preScreenRequirementDocument = map.get(jobPostDocumentRequirement.getIdProof().getIdProofId());
                }
                if(preScreenRequirementDocument == null) {
                    preScreenRequirementDocument = new PreScreenRequirement();
                    preScreenRequirementDocument.setCategory(ServerConstants.CATEGORY_DOCUMENT);
                    preScreenRequirementDocument.setJobPost(jobPost);
                }
                preScreenRequirementDocument.setIdProof(jobPostDocumentRequirement.getIdProof());
                preScreenRequirementDocument.save();
            }
            if (map != null) {
                // TODO Simplify this method
                for (Map.Entry<Integer, PreScreenRequirement> entry : map.entrySet()) {
                    if (!idList.contains(entry.getValue().getIdProof().getIdProofId())) {
                        deletePreScreenResponses(entry.getValue());
                        entry.getValue().delete();
                    }
                }
            }
        } else {
            Map<Integer, PreScreenRequirement> map = multiEntityMap.get(ServerConstants.CATEGORY_DOCUMENT);
            if(map != null) {
                for (Map.Entry<Integer, PreScreenRequirement> entry : map.entrySet()) {
                    deletePreScreenResponses(entry.getValue());
                    entry.getValue().delete();
                }
            }
        }

        if (jobPostAssetRequirementList != null && jobPostAssetRequirementList.size() > 0) {
            Map<Integer, PreScreenRequirement> map = multiEntityMap.get(ServerConstants.CATEGORY_ASSET);
            List<Integer> idList = new ArrayList<>();
            for ( JobPostAssetRequirement jobPostAssetRequirement: jobPostAssetRequirementList) {
                PreScreenRequirement preScreenRequirementAsset = null;
                idList.add(jobPostAssetRequirement.getAsset().getAssetId());
                if (map != null) {
                    preScreenRequirementAsset = map.get(jobPostAssetRequirement.getAsset().getAssetId());
                }
                if (preScreenRequirementAsset == null) {
                    preScreenRequirementAsset = new PreScreenRequirement();
                    preScreenRequirementAsset.setCategory(ServerConstants.CATEGORY_ASSET);
                    preScreenRequirementAsset.setJobPost(jobPost);
                }
                preScreenRequirementAsset.setAsset(jobPostAssetRequirement.getAsset());
                preScreenRequirementAsset.save();
            }
            if (map != null) {
                // TODO Simplify this method
                for (Map.Entry<Integer, PreScreenRequirement> entry : map.entrySet()) {
                    if (!idList.contains(entry.getValue().getAsset().getAssetId())) {
                        deletePreScreenResponses(entry.getValue());
                        entry.getValue().delete();
                    }
                }
            }
        } else {
            Map<Integer, PreScreenRequirement> map = multiEntityMap.get(ServerConstants.CATEGORY_ASSET);
            if(map != null) {
                for (Map.Entry<Integer, PreScreenRequirement> entry : map.entrySet()) {
                    deletePreScreenResponses(entry.getValue());
                    entry.getValue().delete();
                }
            }
        }

        // common entities
        if ( jobPost.getJobPostMinSalary() != null) {
            PreScreenRequirement preScreenRequirementSalary = singleEntityMap.get(ServerConstants.PROFILE_REQUIREMENT_TABLE_SALARY);
            if (preScreenRequirementSalary == null) {
                preScreenRequirementSalary = new PreScreenRequirement();
                preScreenRequirementSalary.setCategory(ServerConstants.CATEGORY_PROFILE);
                preScreenRequirementSalary.setJobPost(jobPost);
            }
            preScreenRequirementSalary.setProfileRequirement(profileRequirementMap.get(ServerConstants.PROFILE_REQUIREMENT_TABLE_SALARY));
            preScreenRequirementSalary.save();
        } else {
            PreScreenRequirement preScreenRequirementSalary = singleEntityMap.get(ServerConstants.PROFILE_REQUIREMENT_TABLE_SALARY);
            if(preScreenRequirementSalary != null) {
                deletePreScreenResponses(preScreenRequirementSalary);
                preScreenRequirementSalary.delete();
            }
        }

        if ( jobPost.getGender() != null) {
            PreScreenRequirement preScreenRequirementGender = singleEntityMap.get(ServerConstants.PROFILE_REQUIREMENT_TABLE_GENDER);
            if (preScreenRequirementGender == null) {
                preScreenRequirementGender = new PreScreenRequirement();
                preScreenRequirementGender.setCategory(ServerConstants.CATEGORY_PROFILE);
                preScreenRequirementGender.setJobPost(jobPost);
            }
            preScreenRequirementGender.setProfileRequirement(profileRequirementMap.get(ServerConstants.PROFILE_REQUIREMENT_TABLE_GENDER));
            preScreenRequirementGender.save();
        } else {
            PreScreenRequirement preScreenRequirementGender = singleEntityMap.get(ServerConstants.PROFILE_REQUIREMENT_TABLE_GENDER);
            if(preScreenRequirementGender != null) {
                deletePreScreenResponses(preScreenRequirementGender);
                preScreenRequirementGender.delete();
            }
        }

        if ( jobPost.getJobPostToLocalityList() != null && jobPost.getJobPostToLocalityList().size()>0) {
            PreScreenRequirement preScreenRequirementLocation = singleEntityMap.get(ServerConstants.PROFILE_REQUIREMENT_TABLE_LOCATION);
            if (preScreenRequirementLocation == null) {
                preScreenRequirementLocation = new PreScreenRequirement();
                preScreenRequirementLocation.setCategory(ServerConstants.CATEGORY_PROFILE);
                preScreenRequirementLocation.setJobPost(jobPost);
            }
            preScreenRequirementLocation.setProfileRequirement(profileRequirementMap.get(ServerConstants.PROFILE_REQUIREMENT_TABLE_LOCATION));
            preScreenRequirementLocation.save();
        } else {
            PreScreenRequirement preScreenRequirementLocation = singleEntityMap.get(ServerConstants.PROFILE_REQUIREMENT_TABLE_LOCATION);
            if(preScreenRequirementLocation != null){
                deletePreScreenResponses(preScreenRequirementLocation);
                preScreenRequirementLocation.delete();
            }
        }

        if (jobPost.getJobPostShift() != null) {
            PreScreenRequirement preScreenRequirementWorkTimings = singleEntityMap.get(ServerConstants.PROFILE_REQUIREMENT_TABLE_WORKTIMINGS);
            if (preScreenRequirementWorkTimings == null) {
                preScreenRequirementWorkTimings = new PreScreenRequirement();
                preScreenRequirementWorkTimings.setCategory(ServerConstants.CATEGORY_PROFILE);
                preScreenRequirementWorkTimings.setJobPost(jobPost);
            }
            preScreenRequirementWorkTimings.setProfileRequirement(profileRequirementMap.get(ServerConstants.PROFILE_REQUIREMENT_TABLE_WORKTIMINGS));
            preScreenRequirementWorkTimings.save();
        } else {
            PreScreenRequirement preScreenRequirementWorkTimings = singleEntityMap.get(ServerConstants.PROFILE_REQUIREMENT_TABLE_WORKTIMINGS);
            if(preScreenRequirementWorkTimings != null){
                deletePreScreenResponses(preScreenRequirementWorkTimings);
                preScreenRequirementWorkTimings.delete();
            }
        }
    }

    private static void deletePreScreenResponses(PreScreenRequirement preScreenRequirement) {
        List<PreScreenResponse> responseList = PreScreenResponse.find.where().eq("preScreenRequirement.preScreenRequirementId", preScreenRequirement.getPreScreenRequirementId()).findList();
        if(responseList.size() > 0){
            for(PreScreenResponse response : responseList ){
                response.delete();
            }
        }
    }

    private static List<JobPostLanguageRequirement> getJobPostLanguageRequirement(List<Long> jobPostLanguageList, JobPost newJobPost) {

        List<JobPostLanguageRequirement> languageRequirementList = new ArrayList<>();
        List<Language> languageList = Language.find.where().in("LanguageId", jobPostLanguageList).findList();
        for(Language language: languageList){
            JobPostLanguageRequirement jobPostLanguageRequirement = new JobPostLanguageRequirement();
            jobPostLanguageRequirement.setLanguage(language);
            jobPostLanguageRequirement.setJobPost(newJobPost);
            languageRequirementList.add(jobPostLanguageRequirement);
        }
        return languageRequirementList;
    }

    private static List<JobPostDocumentRequirement> getJobPostDocumentRequirement(List<Long> jobPostDocumentIdList, JobPost newJobPost) {

        List<JobPostDocumentRequirement> jobPostDocumentRequirementList = new ArrayList<>();
        if(jobPostDocumentIdList == null || jobPostDocumentIdList.size() == 0) {
            return jobPostDocumentRequirementList;
        }
        List<IdProof> idProofList = IdProof.find.where().in("IdProofId", jobPostDocumentIdList).findList();
        for(IdProof idProof: idProofList){
            JobPostDocumentRequirement jobPostDocumentRequirement = new JobPostDocumentRequirement();
            jobPostDocumentRequirement.setIdProof(idProof);
            jobPostDocumentRequirement.setJobPost(newJobPost);
            jobPostDocumentRequirementList.add(jobPostDocumentRequirement);
        }
        return jobPostDocumentRequirementList;
    }

    private static List<JobPostAssetRequirement> getJobPostAssetRequirement(List<Long> jobPostAssetList, JobPost newJobPost) {

        List<JobPostAssetRequirement> jobPostAssetRequirementList = new ArrayList<>();
        if(jobPostAssetList == null || jobPostAssetList.size() == 0) {
            return jobPostAssetRequirementList;
        }
        List<Asset> assetList = Asset.find.where().in("asset_id", jobPostAssetList).findList();
        for(Asset asset: assetList){
            JobPostAssetRequirement jobPostAssetRequirement = new JobPostAssetRequirement();
            jobPostAssetRequirement.setAsset(asset);
            jobPostAssetRequirement.setJobPost(newJobPost);
            jobPostAssetRequirementList.add(jobPostAssetRequirement);
        }
        return jobPostAssetRequirementList;
    }

    public static ApplyJobResponse applyJob(ApplyJobRequest applyJobRequest,
                                            int channelType, int interactionType)
            throws IOException, JSONException
    {
        Logger.info("checking user and jobId: " + applyJobRequest.getCandidateMobile() + " + " + applyJobRequest.getJobId());
        ApplyJobResponse applyJobResponse = new ApplyJobResponse();
        Candidate existingCandidate = CandidateService.isCandidateExists(applyJobRequest.getCandidateMobile());
        if(existingCandidate != null) {
            JobPost existingJobPost = JobPostDAO.findById(Long.valueOf(applyJobRequest.getJobId()));
            Boolean limitJobApplication = false;

            if(existingJobPost == null ){
                applyJobResponse.setStatus(ApplyJobResponse.STATUS_NO_JOB);
                Logger.info("JobPost with jobId: " + applyJobRequest.getJobId() + " does not exists");
            }
            else{

                Logger.info("req app version code: " +applyJobRequest.getAppVersionCode());

                /* this takes care of deactivated candidate in app and website */

                if((channelType == InteractionConstants.INTERACTION_CHANNEL_CANDIDATE_WEBSITE ||
                        channelType == InteractionConstants.INTERACTION_CHANNEL_PARTNER_WEBSITE||
                    applyJobRequest.getAppVersionCode() >= ServerConstants.DEACTIVATION_APP_VERSION_CODE) &&
                        existingCandidate.getCandidateprofilestatus().getProfileStatusId() == ServerConstants.CANDIDATE_STATE_DEACTIVE) {

                    Logger.info("Couldn't proceed with Job Application (JpId: "+applyJobRequest.getJobId()+") as candidate  is deactivated (candidateId: " + existingCandidate.getCandidateId() + ")");

                    Date expiryDate = existingCandidate.getCandidateStatusDetail().getStatusExpiryDate();

                    applyJobResponse.setStatus(ApplyJobResponse.STATUS_SUCCESS);
                    applyJobResponse.setCandidateDeActive(true);
                    String deActivationMessage =
                           SmsUtil.getDeactivationMessage(existingCandidate.getCandidateFullName(), expiryDate);

                    applyJobResponse.setDeActiveHeadMessage("Unable to process your application");
                    applyJobResponse.setDeActiveTitleMessage("Application failed !");
                    applyJobResponse.setDeActiveBodyMessage(deActivationMessage);

                    return applyJobResponse;
                }

                JobApplication existingJobApplication = JobApplication.find.where().eq("candidateId", existingCandidate.getCandidateId()).eq("jobPostId", applyJobRequest.getJobId()).findUnique();
                if(existingJobApplication == null){

                    if(existingJobPost.getRecruiterProfile() != null) {
                        if((existingJobPost.getRecruiterProfile().getContactCreditCount() == 0) &&
                                (existingJobPost.getRecruiterProfile().getInterviewCreditCount() == 0)){

                            Calendar newCalendar = Calendar.getInstance();

                            // 1-> sunday
                            // 2-> Monday
                            int todayDate = newCalendar.get(Calendar.DAY_OF_WEEK);
                            int weekDaysDeduct;
                            if(todayDate > 1){
                                weekDaysDeduct = todayDate - 2;
                            } else{
                                weekDaysDeduct = 6;
                            }

                            //checking weekly job application limit
                            if(JobPostDAO.getThisWeeksApplication(existingJobPost, weekDaysDeduct).size()
                                    >= ServerConstants.FREE_JOB_APPLICATION_DEFAULT_LIMIT_IN_A_WEEK){

                                Logger.info("Free Job weekly limit of " + ServerConstants.FREE_JOB_APPLICATION_DEFAULT_LIMIT_IN_A_WEEK
                                    + " crossed for job Post title: " + existingJobPost.getJobPostTitle() + " and ID: " + existingJobPost.getJobPostId());

                                //setting flag to limit job application for this job role
                                limitJobApplication = true;
                            }
                        }
                    }

                    if(limitJobApplication){
                        applyJobResponse.setStatus(ApplyJobResponse.STATUS_APPLICATION_LIMIT_REACHED);
                        applyJobResponse.setInterviewAvailable(false);
                    } else{
                        JobApplication jobApplication = new JobApplication();
                        jobApplication.setCandidate(existingCandidate);
                        jobApplication.setJobPost(existingJobPost);

                        //setting time slot
                        if(applyJobRequest.getTimeSlot() != null){
                            InterviewTimeSlot interviewTimeSlot = InterviewTimeSlot.find.where().eq("interview_time_slot_id", applyJobRequest.getTimeSlot()).findUnique();
                            if(interviewTimeSlot != null){
                                jobApplication.setInterviewTimeSlot(interviewTimeSlot);
                            }
                        }
                        //setting scheduled interview date
                        if(applyJobRequest.getScheduledInterviewDate() != null){
                            jobApplication.setScheduledInterviewDate(applyJobRequest.getScheduledInterviewDate());
                        }
                        Locality locality = Locality.find.where().eq("localityId", applyJobRequest.getLocalityId()).findUnique();
                        if(locality != null){
                            jobApplication.setLocality(locality);
                        } else{
                            Logger.info("Location with locality ID: " + applyJobRequest.getLocalityId() + " does not exists");
                        }

                        String interactionResult = InteractionConstants.INTERACTION_RESULT_CANDIDATE_SELF_APPLIED_JOB;
                        Partner partner = null;
                        if(applyJobRequest.getPartner()!= null && applyJobRequest.getPartner()){
                            // this job is being applied by a partner for a candidate, hence we need to get partner Id in the job Application table
                            partner = Partner.find.where().eq("partner_id", session().get("partnerId")).findUnique();
                            if(partner != null){
                                //setting partner
                                jobApplication.setPartner(partner);
                                SmsUtil.sendJobApplicationSmsViaPartner(existingCandidate.getCandidateFirstName(), existingJobPost.getJobPostTitle(), existingJobPost.getCompany().getCompanyName(), existingCandidate.getCandidateMobile(), jobApplication.getLocality().getLocalityName(), partner.getPartnerFirstName());
                                SmsUtil.sendJobApplicationSmsToPartner(existingCandidate.getCandidateFirstName(), existingJobPost.getJobPostTitle(), existingJobPost.getCompany().getCompanyName(), partner.getPartnerMobile(), jobApplication.getLocality().getLocalityName(), partner.getPartnerFirstName());
                                interactionResult = InteractionConstants.INTERACTION_RESULT_PARTNER_APPLIED_TO_JOB;
                            }
                        } else{
                            SmsUtil.sendJobApplicationSms(existingCandidate.getCandidateFirstName(), existingJobPost.getJobPostTitle(), existingJobPost.getCompany().getCompanyName(), existingCandidate.getCandidateMobile(), jobApplication.getLocality().getLocalityName(), channelType);

                            //sending notification
                            NotificationUtil.sendJobApplicationNotification(existingCandidate, existingJobPost.getJobPostTitle(), existingJobPost.getCompany().getCompanyName(), jobApplication.getLocality().getLocalityName());
                        }

                        jobApplication.save();
                        writeJobApplicationToGoogleSheet(existingJobPost.getJobPostId(), applyJobRequest.getCandidateMobile(), channelType, applyJobRequest.getLocalityId(), partner, applyJobRequest);

                        if (channelType == INTERACTION_CHANNEL_CANDIDATE_WEBSITE) {
                            // job application coming from website
                            InteractionService.createInteractionForJobApplicationViaWebsite(
                                    existingCandidate.getCandidateUUId(),
                                    existingJobPost.getJobPostUUId(),
                                    interactionResult + existingJobPost.getJobPostTitle() + " at " + existingJobPost.getCompany().getCompanyName() + "@" + locality.getLocalityName(),
                                    interactionType
                            );
                        } else{
                            InteractionService.createInteractionForJobApplicationViaAndroid(
                                    existingCandidate.getCandidateUUId(),
                                    existingJobPost.getJobPostUUId(),
                                    interactionResult + existingJobPost.getJobPostTitle() + " at " + existingJobPost.getCompany().getCompanyName() + "@" + locality.getLocalityName()
                            );
                        }

                        Logger.info("candidate: " + existingCandidate.getCandidateFirstName() + " with mobile: " + existingCandidate.getCandidateMobile() + " applied to the jobPost of JobPostId:" + existingJobPost.getJobPostId());

                        applyJobResponse.setStatus(ApplyJobResponse.STATUS_SUCCESS);

                    }

                } else{
                    applyJobResponse.setStatus(ApplyJobResponse.STATUS_EXISTS);
                    Logger.info("candidate: " + existingCandidate.getCandidateFirstName() + " with mobile: " + existingCandidate.getCandidateMobile() + " already applied to jobPost with jobId:" + existingJobPost.getJobPostId());
                }

                if(!limitJobApplication){
                    // assuming job apply to a particular jobpost is a one time event, this will push candidate into selected state
                    String interactionResult = InteractionConstants.INTERACTION_RESULT_CANDIDATE_SELECTED_FOR_PRESCREEN;
                    interactionResult += existingJobPost.getJobPostId() + ": " + existingJobPost.getJobRole().getJobName();
                    if (existingJobPost.getCompany() != null) {
                        interactionResult += "@" + existingJobPost.getCompany().getCompanyName();
                    }


                    //  Each initial application should also have initial job post workflow entry, this methods takes care
                    //  of job Post workflow entry + corresponding interaction
                    createJobPostWorkflowEntry( existingCandidate, existingJobPost, channelType,
                            ServerConstants.JWF_STATUS_SELECTED,
                            InteractionConstants.INTERACTION_TYPE_CANDIDATE_SELECTED_FOR_PRESCREEN,
                            interactionResult);
                }
            }

            PreScreenPopulateResponse populateResponse = JobPostWorkflowEngine.getJobPostVsCandidate(Long.valueOf(applyJobRequest.getJobId()),
                    existingCandidate.getCandidateId(), false);
            if(populateResponse.isVisible()){
                applyJobResponse.setPreScreenAvailable(true);
            } else {
                applyJobResponse.setPreScreenAvailable(false);

                // if there is no pre_screen_requirement with the jobpost then create entry ()
                // with prescreen completed, for this candidate
                // also create entry in jobPostWorkflow table
                JobPostWorkflow jobPostWorkflow = JobPostWorkflow.find
                        .where()
                        .eq("candidate_id", existingCandidate.getCandidateId())
                        .eq("status_id", ServerConstants.JWF_STATUS_PRESCREEN_COMPLETED)
                        .eq("job_post_id", existingJobPost.getJobPostId()).setMaxRows(1).findUnique();

                if(jobPostWorkflow == null) {
                    JobPostWorkflowEngine.savePreScreenResultForCandidateUpdate(existingCandidate.getCandidateId(),
                            existingJobPost.getJobPostId(),
                            channelType);
                }
            }
            InterviewResponse interviewResponse = RecruiterService.isInterviewRequired(existingJobPost);
            if(interviewResponse.getStatus() < ServerConstants.INTERVIEW_REQUIRED){
                applyJobResponse.setInterviewAvailable(false);
            } else {
                applyJobResponse.setInterviewAvailable(true);
            }

            // adding only those field that are req by interview UI messaging
            applyJobResponse.setJobRoleTitle(existingJobPost.getJobRole().getJobName());
            applyJobResponse.setJobTitle(existingJobPost.getJobPostTitle());
            applyJobResponse.setCompanyName(existingJobPost.getCompany().getCompanyName());
            applyJobResponse.setJobPostId(existingJobPost.getJobPostId());
        } else{
            applyJobResponse.setStatus(ApplyJobResponse.STATUS_NO_CANDIDATE);
            Logger.info("Candidate Does not exists");
        }

        return applyJobResponse;
    }

    private static void createJobPostWorkflowEntry(Candidate existingCandidate, JobPost existingJobPost,
                                                   int channelType, int status,
                                                   int interactionType, String interactionResult ) {
        // also create entry in jobPostWorkflow table
        JobPostWorkflow jobPostWorkflow = JobPostWorkflow.find
                .where()
                .eq("candidate_id", existingCandidate.getCandidateId())
                .eq("status_id", status)
                .eq("job_post_id", existingJobPost.getJobPostId()).setMaxRows(1).findUnique();

        // if no entry for the given status then create entry
        if (jobPostWorkflow == null) {
            jobPostWorkflow = new JobPostWorkflow();
            jobPostWorkflow.setCandidate(existingCandidate);
            jobPostWorkflow.setJobPost(existingJobPost);
            jobPostWorkflow.setStatus(JobPostWorkflowStatus.find.where().eq("statusId", status).findUnique());


            jobPostWorkflow.setCreatedBy(session().get("sessionUsername") == null ?InteractionConstants.INTERACTION_CHANNEL_MAP.get(channelType) : session().get("sessionUsername") );
            jobPostWorkflow.setChannel(channelType);
            jobPostWorkflow.save();

            // save the interaction
            InteractionService.createWorkflowInteraction(
                    jobPostWorkflow.getJobPostWorkflowUUId(),
                    existingCandidate.getCandidateUUId(),
                    interactionType,
                    null,
                    interactionResult,
                    channelType
            );
        }
    }

    public static void writeJobApplicationToGoogleSheet(Long jobPostId, String candidateMobile, int channelType, Integer localityId, Partner partner, ApplyJobRequest applyJobRequest) throws UnsupportedEncodingException {
        String jobIdVal = "-";
        String companyNameVal = "-";
        String jobPostNameVal = "-";
        String candidateLeadIdVal = "-";
        String candidateNameVal = "-";
        String candidateMobileVal = "-";
        String candidateGenderVal = "-";
        String candidateTotalExpVal = "-";
        String candidateIsEmployedVal = "-";
        String candidateIsAssessedVal = "-";
        String candidateLanguageKnownVal = "";
        String candidateMotherTongueVal = "-";
        String candidateHomeLocalityVal = "-";
        String candidateLocalityPrefVal = "";
        String candidateJobPrefVal = "";
        String candidateCurrentSalaryVal = "-";
        String candidateEducationVal = "-";
        String candidateSkillsVal = "";
        String candidateCreationVal = "-";
        String candidatePrescreenLocationVal = "-";
        String candidateProfileStatusVal = "-";
        String candidateExpiryDateVal = "-";
        String candidateAgeVal = "-";
        String jobApplicationChannelVal = "-";
        String jobIsHotVal = "";
        String partnerNameVal = "";
        String partnerMobileVal = "";
        String partnerIdVal = "";
        String interviewDateVal = "";
        String interviewTimeVal = "";
        int sheetId = ServerConstants.SHEET_MAIN;

        if(channelType == INTERACTION_CHANNEL_CANDIDATE_ANDROID){
            jobApplicationChannelVal = "Android";
        } else if(channelType == INTERACTION_CHANNEL_CANDIDATE_WEBSITE){
            jobApplicationChannelVal = "Website";
        }

        Locality preScreenLocality = Locality.find.where().eq("localityId", localityId).findUnique();
        if(preScreenLocality != null){
            candidatePrescreenLocationVal = preScreenLocality.getLocalityName();
        }

        JobPost jobpost = JobPostDAO.findById(jobPostId);
        if(jobpost != null){
            jobIdVal = String.valueOf(jobpost.getJobPostId());
            jobPostNameVal = jobpost.getJobPostTitle();
            companyNameVal = jobpost.getCompany().getCompanyName();
            if(jobpost.getJobPostIsHot()){
                jobIsHotVal = "Hot";
            } else{
                jobIsHotVal = "Not Hot";
            }

            /* check source for the job and save it to appropriate sheet */
            if(jobpost.getSource() != null && jobpost.getSource() != ServerConstants.SOURCE_INTERNAL){
                sheetId = ServerConstants.SHEET_SCRAPPED;
            }
        }

        // get candidate information
        Candidate candidate = CandidateService.isCandidateExists(FormValidator.convertToIndianMobileFormat(candidateMobile));
        if(candidate!=null){
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(candidate.getCandidateCreateTimestamp().getTime());

            int mYear = calendar.get(Calendar.YEAR);
            int mMonth = calendar.get(Calendar.MONTH);
            int mDay = calendar.get(Calendar.DAY_OF_MONTH);
            int mHour = calendar.get(Calendar.HOUR_OF_DAY);
            int mMin = calendar.get(Calendar.MINUTE);
            int mSecond = calendar.get(Calendar.SECOND);

            candidateCreationVal = mDay + "/" + mMonth + "/" + mYear + " " + mHour + ":" + mMin + ":" + mSecond;
            candidateMobileVal = String.valueOf(candidate.getCandidateMobile());

            if(candidate.getCandidateLastName() == null){
                candidateNameVal = candidate.getCandidateFirstName();
            } else{
                candidateNameVal = candidate.getCandidateFirstName() + " " +candidate.getCandidateLastName();
            }
            candidateLeadIdVal = String.valueOf(candidate.getLead().getLeadId());

            if(candidate.getCandidateGender() != null){
                if(candidate.getCandidateGender() == 0){
                    candidateGenderVal = "Male";
                } else{
                    candidateGenderVal = "Female";
                }
            }

            if(candidate.getCandidateTotalExperience() != null){
                candidateTotalExpVal = String.valueOf(Math.round((candidate.getCandidateTotalExperience()/12)*100)/100);
            }
            if(candidate.getCandidateIsAssessed() == 0){
                candidateIsAssessedVal = "No";
            } else{
                candidateIsAssessedVal = "Yes";
            }

            if(candidate.getCandidateIsEmployed() != null){
                if(candidate.getCandidateIsEmployed() == 1){
                    candidateIsEmployedVal = "No";
                } else{
                    candidateIsEmployedVal = "Yes";
                }
            }

            //Languages Known
            if(candidate.getLanguageKnownList() != null && candidate.getLanguageKnownList().size() > 0) {
                List<LanguageKnown> languageKnownList = candidate.getLanguageKnownList();

                for(LanguageKnown l : languageKnownList){
                    candidateLanguageKnownVal += l.getLanguage().getLanguageName() + "(" + l.getUnderstanding() + ", " +
                            l.getVerbalAbility() + ", " + l.getReadWrite() + "), ";
                }
            }

            //Skill
            if(candidate.getCandidateSkillList()!= null && candidate.getCandidateSkillList().size() > 0){
                List<CandidateSkill> candidateSkillList = candidate.getCandidateSkillList();

                for(CandidateSkill skill : candidateSkillList){
                    candidateSkillsVal += skill.getSkill().getSkillName() + ", ";
                }
            }

            if(candidate.getLocality() != null){
                candidateHomeLocalityVal = candidate.getLocality().getLocalityName();
            }
            if(candidate.getCandidateCurrentJobDetail() != null){
                candidateCurrentSalaryVal = String.valueOf(candidate.getCandidateCurrentJobDetail().getCandidateCurrentSalary());
            }
            if(candidate.getCandidateEducation() != null){
                candidateEducationVal = candidate.getCandidateEducation().getEducation().getEducationName();
            }

            //Job Pref
            List<JobPreference> jobRolePrefList = candidate.getJobPreferencesList();
            for(JobPreference job : jobRolePrefList){
                candidateJobPrefVal += job.getJobRole().getJobName() + ", ";
            }

            //Locality Pref
            List<LocalityPreference> localityPrefList = candidate.getLocalityPreferenceList();
            for(LocalityPreference locality : localityPrefList){
                candidateLocalityPrefVal += locality.getLocality().getLocalityName() + ", ";
            }

            candidateProfileStatusVal = candidate.getCandidateprofilestatus().getProfileStatusName();
            if(candidate.getCandidateprofilestatus().getProfileStatusId() == ServerConstants.CANDIDATE_STATE_DEACTIVE){
                Date expDate = candidate.getCandidateStatusDetail().getStatusExpiryDate();
                if(expDate != null){
                    calendar.setTimeInMillis(candidate.getCandidateStatusDetail().getStatusExpiryDate().getTime());
                    mYear = calendar.get(Calendar.YEAR);
                    mMonth = calendar.get(Calendar.MONTH);
                    mDay = calendar.get(Calendar.DAY_OF_MONTH);
                    mHour = calendar.get(Calendar.HOUR_OF_DAY);
                    mMin = calendar.get(Calendar.MINUTE);
                    mSecond = calendar.get(Calendar.SECOND);

                    candidateExpiryDateVal = mDay + "/" + mMonth + "/" + mYear + " " + mHour + ":" + mMin + ":" + mSecond;
                } else{
                    candidateExpiryDateVal = String.valueOf("-");
                }
            } else{
                candidateExpiryDateVal = String.valueOf("-");
            }

            if(candidate.getCandidateDOB() != null){
                Date current = new Date();
                Date bday = new Date(candidate.getCandidateDOB().getTime());

                final Calendar calender = new GregorianCalendar();
                calender.set(Calendar.HOUR_OF_DAY, 0);
                calender.set(Calendar.MINUTE, 0);
                calender.set(Calendar.SECOND, 0);
                calender.set(Calendar.MILLISECOND, 0);
                calender.setTimeInMillis(current.getTime() - bday.getTime());

                int age = 0;
                age = calender.get(Calendar.YEAR) - 1970;
                age += (float) calender.get(Calendar.MONTH) / (float) 12;
                candidateAgeVal = String.valueOf(age);
            }
        }

        if(partner != null){
            partnerNameVal = partner.getPartnerFirstName();
            partnerMobileVal = partner.getPartnerMobile();
            partnerIdVal = String.valueOf(partner.getPartnerId());

            if(applyJobRequest.getScheduledInterviewDate() != null){
                Date scheduledInterviewDate = new Date(applyJobRequest.getScheduledInterviewDate().getTime());
                Calendar cal = Calendar.getInstance();
                cal.setTime(scheduledInterviewDate);
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);
                interviewDateVal = day + "/" + month + "/" +year;
            }
            if(applyJobRequest.getTimeSlot() != null){
                InterviewTimeSlot interviewTimeSlot = InterviewTimeSlot.find.where().eq("interview_time_slot_id", applyJobRequest.getTimeSlot()).findUnique();
                if(interviewTimeSlot != null){
                    interviewTimeVal = interviewTimeSlot.getInterviewTimeSlotName();
                }
            }
        }

        /*
        * writing to job application sheet excel sheet
        * */

        // field key values
        String jobIdKey = "entry.1388755113";
        String companyNameKey = "entry.1115234203";
        String jobPostNameKey = "entry.1422779518";
        String candidateLeadIdKey = "entry.942294281";
        String candidateNameKey = "entry.1345077393";
        String candidateMobileKey = "entry.1859090779";
        String candidateGenderKey = "entry.2079461892";
        String candidateTotalExpKey = "entry.2071290015";
        String candidateIsEmployedKey = "entry.179139422";
        String candidateIsAssessedKey = "entry.1488146275";
        String candidateLanguageKnownKey = "entry.67497584";
        String candidateMotherTongueKey = "entry.441069988";
        String candidateHomeLocalityKey = "entry.1350761294";
        String candidateLocalityPrefKey = "entry.2057814300";
        String candidateJobPrefKey = "entry.598773915";
        String candidateCurrentSalaryKey = "entry.125850326";
        String candidateEducationKey = "entry.240702722";
        String candidateSkillsKey = "entry.190053755";
        String candidateCreationKey = "entry.971982828";
        String candidatePrescreenLocationKey = "entry.98308337";
        String candidateProfileStatusKey = "entry.46689276";
        String candidateExpiryDateKey = "entry.1180627971";
        String candidateAgeKey = "entry.791725694";
        String jobApplicationChannelKey = "entry.528024717";
        String jobIsHotKey = "entry.1165618058";
        String partnerNameKey = "entry.1066838351";
        String partnerIdKey = "entry.34374237";
        String partnerMobileKey = "entry.483855268";
        String interviewDateKey = "entry.1055797412";
        String interviewTime = "entry.414736621";

        String url;
        if(!Play.isDev(Play.current())){
            if(sheetId == ServerConstants.SHEET_MAIN){
                url = ServerConstants.PROD_GOOGLE_FORM_FOR_JOB_APPLICATION;
            } else {
                url = ServerConstants.PROD_GOOGLE_FORM_FOR_SCRAPPED_JOB_APPLICATION;
            }
        } else {
            url = ServerConstants.DEV_GOOGLE_FORM_FOR_JOB_APPLICATION;
        }
        String postBody;

        postBody = jobIdKey +"=" + URLEncoder.encode(jobIdVal,"UTF-8") + "&"
                + companyNameKey + "=" + URLEncoder.encode(companyNameVal,"UTF-8") + "&"
                + jobPostNameKey + "=" + URLEncoder.encode(jobPostNameVal,"UTF-8") + "&"
                + candidateLeadIdKey + "=" + URLEncoder.encode(candidateLeadIdVal,"UTF-8") + "&"
                + candidateNameKey + "=" + URLEncoder.encode(candidateNameVal,"UTF-8") + "&"
                + candidateMobileKey + "=" + URLEncoder.encode(candidateMobileVal,"UTF-8") + "&"
                + candidateGenderKey + "=" + URLEncoder.encode(candidateGenderVal,"UTF-8") + "&"
                + candidateTotalExpKey + "=" + URLEncoder.encode(candidateTotalExpVal,"UTF-8") + "&"
                + candidateIsEmployedKey + "=" + URLEncoder.encode(candidateIsEmployedVal,"UTF-8") + "&"
                + candidateIsAssessedKey + "=" + URLEncoder.encode(candidateIsAssessedVal,"UTF-8") + "&"
                + candidateLanguageKnownKey + "=" + URLEncoder.encode(candidateLanguageKnownVal,"UTF-8") + "&"
                + candidateMotherTongueKey + "=" + URLEncoder.encode(candidateMotherTongueVal,"UTF-8") + "&"
                + candidateHomeLocalityKey + "=" + URLEncoder.encode(candidateHomeLocalityVal,"UTF-8") + "&"
                + candidateLocalityPrefKey + "=" + URLEncoder.encode(candidateLocalityPrefVal,"UTF-8") + "&"
                + candidateJobPrefKey + "=" + URLEncoder.encode(candidateJobPrefVal,"UTF-8") + "&"
                + candidateCurrentSalaryKey + "=" + URLEncoder.encode(candidateCurrentSalaryVal,"UTF-8") + "&"
                + candidateEducationKey + "=" + URLEncoder.encode(candidateEducationVal,"UTF-8") + "&"
                + candidateSkillsKey + "=" + URLEncoder.encode(candidateSkillsVal,"UTF-8") + "&"
                + candidateCreationKey + "=" + URLEncoder.encode(candidateCreationVal,"UTF-8") + "&"
                + candidatePrescreenLocationKey + "=" + URLEncoder.encode(candidatePrescreenLocationVal,"UTF-8") + "&"
                + candidateProfileStatusKey + "=" + URLEncoder.encode(candidateProfileStatusVal,"UTF-8") + "&"
                + candidateAgeKey + "=" + URLEncoder.encode(candidateAgeVal,"UTF-8") + "&"
                + candidateExpiryDateKey + "=" + URLEncoder.encode(candidateExpiryDateVal,"UTF-8") + "&"
                + jobApplicationChannelKey + "=" + URLEncoder.encode(jobApplicationChannelVal,"UTF-8") + "&"
                + jobIsHotKey + "=" + URLEncoder.encode(jobIsHotVal,"UTF-8") + "&"
                + partnerNameKey + "=" + URLEncoder.encode(partnerNameVal,"UTF-8") + "&"
                + partnerIdKey + "=" + URLEncoder.encode(partnerIdVal,"UTF-8") + "&"
                + partnerMobileKey + "=" + URLEncoder.encode(partnerMobileVal,"UTF-8") + "&"
                + interviewDateKey + "=" + URLEncoder.encode(interviewDateVal,"UTF-8") + "&"
                + interviewTime + "=" + URLEncoder.encode(interviewTimeVal,"UTF-8");

                try {
                    GoogleSheetHttpRequest googleSheetHttpRequest = new GoogleSheetHttpRequest();
                    googleSheetHttpRequest.sendPost(url, postBody);
                }catch (Exception exception){
                    Logger.info("Exception in writing to google sheet");
                }
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

    public static void sendSmsToCandidateMatchingWithJobPost(JobPost jobPost){
        Map<Long, CandidateWorkflowData> candidateSearchMap = JobPostWorkflowEngine.getMatchingCandidate(
                jobPost.getJobPostId(),
                null, //age
                null, //min salary
                null, //max salary
                jobPost.getGender(), //gender
                null, //experience
                jobPost.getJobRole().getJobRoleId(), //jobRole
                null, //education
                null, //locality
                null, //language list,
                null, //document List
                null, //asset list
                SchedulerConstants.NEW_JOB_MATCHING_DEFAULT_DISTANCE_RADIUS);

        if(candidateSearchMap != null){
            Logger.info("Total matched candidate: " + candidateSearchMap.size() + " for jobPost: " + jobPost.getJobPostTitle());

            Boolean hasCredit = false;
            if(jobPost.getRecruiterProfile().getInterviewCreditCount() > 0){
                hasCredit = true;
            }

            List<Candidate> candidateList = new ArrayList<>();
            for (Map.Entry<Long, CandidateWorkflowData> candidate : candidateSearchMap.entrySet()) {
                candidateList.add(candidate.getValue().getCandidate());
            }

            Collections.shuffle(candidateList);

            Logger.info("Sending notification to " + SchedulerConstants.NEW_JOB_ALERT_LIMIT + " candidates regarding the jobPost: " + jobPost.getJobPostTitle());

            //adding to notification Handler queue
            for(int i = 0; i< SchedulerConstants.NEW_JOB_ALERT_LIMIT; i++){
                    SmsUtil.sendJobAlertSmsToCandidate(jobPost, candidateList.get(i), hasCredit);
                    NotificationUtil.sendJobAlertNotificationToCandidate(jobPost, candidateList.get(i), hasCredit);
            }
        }
    }

    public static Map<String, InterviewDateTime> getInterviewSlot(JobPost jobPost) {

        if(jobPost == null){
            return null;
        }

        Map<String, InterviewDateTime> interviewSlotMap = new LinkedHashMap<>();
        // get today's date
        Calendar newCalendar = Calendar.getInstance();
        newCalendar.get(Calendar.YEAR);
        newCalendar.get(Calendar.MONTH);
        newCalendar.get(Calendar.DAY_OF_MONTH);
        Date today = newCalendar.getTime();

        int k;
        // for those jobpost in which the auto confirm is marked as checked, we start line up from the next day
        if(jobPost.getReviewApplication() == null || jobPost.getReviewApplication() == ServerConstants.REVIEW_APPLICATION_AUTO){
            // validation for generated time slot is done inside for loop below
            k = 0;
        } else {
            k = 2;
        }
        // generate interview slots for next 3 days
        for (; k < 8; ++k) {

            Calendar c = Calendar.getInstance();
            c.setTime(today);
            c.add(Calendar.DATE, k);
            Date future = c.getTime();

            for (InterviewDetails details : jobPost.getInterviewDetailsList()) {
                /* while converting from decimal to binary, preceding zeros are ignored. to fix, follow below*/
                String interviewDays = InterviewUtil.fixPrecedingZero(Integer.toBinaryString(details.getInterviewDays()));

                if (InterviewUtil.checkSlotAvailability(future, interviewDays)) {

                    api.http.httpResponse.interview.InterviewTimeSlot timeSlot = new api.http.httpResponse.interview.InterviewTimeSlot();
                    timeSlot.setSlotId(details.getInterviewTimeSlot().getInterviewTimeSlotId());
                    timeSlot.setSlotTitle(details.getInterviewTimeSlot().getInterviewTimeSlotName());

                    api.http.httpResponse.interview.InterviewDateTime interviewDateTime = new api.http.httpResponse.interview.InterviewDateTime();
                    interviewDateTime.setInterviewTimeSlot(timeSlot);
                    interviewDateTime.setInterviewDateMillis(future.getTime());


                    if(!InterviewUtil.checkTimeSlot(future.getTime(), timeSlot)) {
                        continue;
                    }
                    
                    String slotString = getDayVal(future.getDay())+ ", "
                            + future.getDate() + " " + getMonthVal((future.getMonth() + 1))
                            + " (" + details.getInterviewTimeSlot().getInterviewTimeSlotName() + ")" ;


                    interviewSlotMap.put(slotString, interviewDateTime);
                }
            }
        }
        return interviewSlotMap;
    }

    /**
     *
     * API accepts only a name and a mobile number, fetch/create(leadSource: LooseCandidate)
     * a candidate then push it to apply flow,
     *
     *
     * @param applyJobRequest
     * @return ApplyJobResponse
     */
    public static CallToApplyResponse callToApply(ApplyJobRequest applyJobRequest){
        CallToApplyResponse callToApplyResponse = new CallToApplyResponse();

        if( applyJobRequest == null){
            callToApplyResponse.setMessage("Invalid Params");
            callToApplyResponse.setStatus(CallToApplyResponse.STATUS_INVALID_PARAMS);
            return callToApplyResponse;
        }

        JobPost jobPost = applyJobRequest.getJobId() == null ? null : JobPostDAO.findById(applyJobRequest.getJobId());
        if( jobPost == null)
        {
            callToApplyResponse.setMessage("Invalid Params");
            callToApplyResponse.setStatus(CallToApplyResponse.STATUS_INVALID_PARAMS);
            return callToApplyResponse;
        }

        String candidateMobile = FormValidator.convertToIndianMobileFormat(applyJobRequest.getCandidateMobile());

        if( candidateMobile == null) {
            callToApplyResponse.setMessage("Invalid Params");
            callToApplyResponse.setStatus(CallToApplyResponse.STATUS_INVALID_PARAMS);
            return callToApplyResponse;
        }

        Candidate candidate = CandidateService.isCandidateExists(candidateMobile);

        AddCandidateRequest addCandidateRequest = new AddCandidateRequest();
        addCandidateRequest.setCandidateMobile(candidateMobile);
        addCandidateRequest.setCandidateFirstName(applyJobRequest.getCandidateName());
        if( candidate == null) {
            addCandidateRequest.setLeadSource(ServerConstants.LEAD_SOURCE_CALL_TO_APPLY_WEBSITE);

            List<Integer> candidateJobPref = new ArrayList<>();
            candidateJobPref.add(Math.toIntExact(jobPost.getJobRole().getJobRoleId()));
            addCandidateRequest.setCandidateJobPref(candidateJobPref);

            if(jobPost.getJobPostToLocalityList().size() > 1) {
                // setting locality other ---- 345
                addCandidateRequest.setCandidateHomeLocality(345);
            } else {
                addCandidateRequest.setCandidateHomeLocality(
                        Math.toIntExact(jobPost.getJobPostToLocalityList()
                                .get(0)
                                .getLocality()
                                .getLocalityId()));

            }
            CandidateService.createCandidateProfile(addCandidateRequest, INTERACTION_CHANNEL_CANDIDATE_WEBSITE, ServerConstants.UPDATE_BASIC_PROFILE);

        } else {
            // direct update candidate, if required
            boolean shouldUpdate = false;
            // set locality if candidate locality is prev set to other
            if(candidate.getLocality() == null
                    || candidate.getLocality().getLocalityId() == 345
                    && jobPost.getJobPostToLocalityList().size() == 1)
            {

                candidate.setLocality(
                        jobPost.getJobPostToLocalityList()
                                .get(0)
                                .getLocality());

                shouldUpdate = true;
            }

            // we will append but not rotate job preference
            // update if job pref is not there and job pref is other

            Map<Long, Long> candidateJobPrefMap = new HashMap<>();
            if(candidate.getJobPreferencesList().size()<3){
                List<JobPreference> candidateJobPref = candidate.getJobPreferencesList();

                for (JobPreference jobPreference : candidate.getJobPreferencesList()) {
                    Long jobRoleId = candidateJobPrefMap.get(jobPreference.getJobRole().getJobRoleId());
                    if(jobRoleId == null) {
                        candidateJobPrefMap.put(jobPreference.getJobRole().getJobRoleId(), jobPreference.getJobRole().getJobRoleId());
                    }
                }

                for(JobPreference jobPreference : candidate.getJobPreferencesList()) {
                    if(candidateJobPrefMap.get(jobPreference.getJobRole().getJobRoleId()) == null){
                        JobPreference jobPref = new JobPreference();
                        jobPref.setCandidate(candidate);
                        jobPref.setJobRole(jobPreference.getJobRole());

                        candidateJobPref.add(jobPref);
                        shouldUpdate = true;
                    }
                }
            }

            if(shouldUpdate) candidate.update();
        }


        // prep apply date
        applyJobRequest.setLocalityId(Math.toIntExact(jobPost.getJobPostToLocalityList().get(0).getLocality().getLocalityId()));

        // push this candidate to apply flow

        try {
            ApplyJobResponse applyJobResponse = applyJob(applyJobRequest, INTERACTION_CHANNEL_CANDIDATE_WEBSITE, InteractionConstants.INTERACTION_TYPE_APPLY_JOB_VIA_CALL_TO_APPLY);

            callToApplyResponse.setResponse(applyJobResponse);
            callToApplyResponse.setMessage("Successfully Applied !");
            callToApplyResponse.setStatus(CallToApplyResponse.STATUS_SUCCESS);
            
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
        // TODO code to deduct the new credit will come here + response formation here with rec mobile and name

        return callToApplyResponse;
    }

}