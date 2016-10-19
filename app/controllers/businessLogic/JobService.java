package controllers.businessLogic;

import api.InteractionConstants;
import api.ServerConstants;
import api.http.FormValidator;
import api.http.httpRequest.AddJobPostRequest;
import api.http.httpRequest.ApplyJobRequest;
import api.GoogleSheetHttpRequest;
import api.http.httpResponse.AddJobPostResponse;
import api.http.httpResponse.ApplyJobResponse;
import com.amazonaws.util.json.JSONException;
import com.avaje.ebean.Model;
import models.entity.Recruiter.RecruiterProfile;
import models.entity.*;
import models.entity.OM.*;
import models.entity.Static.*;
import models.util.SmsUtil;
import play.Logger;
import play.api.Play;

import java.io.*;
import java.net.URLEncoder;
import java.sql.Timestamp;
import java.util.*;

import static api.InteractionConstants.INTERACTION_CHANNEL_CANDIDATE_WEBSITE;
import static controllers.businessLogic.InteractionService.createInteractionForNewJobPost;
import static play.mvc.Controller.session;
import static play.mvc.Http.Context.current;

/**
 * Created by batcoder1 on 17/6/16.
 */
public class JobService {
    public static AddJobPostResponse addJobPost(AddJobPostRequest addJobPostRequest, InteractionService.InteractionChannelType channelType) {
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

        JobPost existingJobPost = JobPost.find.where().eq("jobPostId", addJobPostRequest.getJobPostId()).findUnique();
        if(existingJobPost == null){
            Logger.info("Job post does not exists. Creating a new job Post");
            JobPost newJobPost = new JobPost();
            newJobPost = getAndSetJobPostValues(addJobPostRequest, newJobPost, jobPostLocalityList);

            createInterviewDetails(addJobPostRequest, newJobPost);
            newJobPost.save();

            saveOrUpdatePreScreenRequirements(newJobPost);

            addJobPostResponse.setJobPost(newJobPost);
            addJobPostResponse.setStatus(AddJobPostResponse.STATUS_SUCCESS);

            objBUuid = newJobPost.getJobPostUUId();

            result = InteractionConstants.INTERACTION_RESULT_NEW_JOB_CREATED;
            interactionType = InteractionConstants.INTERACTION_TYPE_NEW_JOB_CREATED;

            Logger.info("JobPost with jobId: " + newJobPost.getJobPostId() + " and job title: " + newJobPost.getJobPostTitle() + " created successfully");
        } else{
            Logger.info("Job post already exists. Updating existing job Post");
            existingJobPost = getAndSetJobPostValues(addJobPostRequest, existingJobPost, jobPostLocalityList);

            resetInterviewDetails(addJobPostRequest, existingJobPost);
            createInterviewDetails(addJobPostRequest, existingJobPost);
            existingJobPost.update();

            saveOrUpdatePreScreenRequirements(existingJobPost);

            addJobPostResponse.setJobPost(existingJobPost);
            addJobPostResponse.setStatus(AddJobPostResponse.STATUS_UPDATE_SUCCESS);

            objBUuid = existingJobPost.getJobPostUUId();

            result = InteractionConstants.INTERACTION_RESULT_EXISTING_JOB_POST_UPDATED;
            interactionType = InteractionConstants.INTERACTION_TYPE_EXISTING_JOB_UPDATED;

            Logger.info("JobPost with jobId: " + existingJobPost.getJobPostId() + " and job title: " + existingJobPost.getJobPostTitle() + " updated successfully");
        }

        if(channelType == InteractionService.InteractionChannelType.SUPPORT){
            createdBy = session().get("sessionUsername");
            objAType = ServerConstants.OBJECT_TYPE_SUPPORT;
            channel = InteractionConstants.INTERACTION_CHANNEL_SUPPORT_WEBSITE;
        } else{
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

        newJobPost.setJobPostVacancies(addJobPostRequest.getJobPostVacancies());
        newJobPost.setJobPostDescriptionAudio(addJobPostRequest.getJobPostDescriptionAudio());
        newJobPost.setJobPostWorkFromHome(addJobPostRequest.getJobPostWorkFromHome());

        newJobPost.setJobPostPartnerInterviewIncentive(addJobPostRequest.getPartnerInterviewIncentive());
        newJobPost.setJobPostPartnerJoiningIncentive(addJobPostRequest.getPartnerJoiningIncentive());

        newJobPost.setJobPostToLocalityList(getJobPostLocality(jobPostLocalityList, newJobPost));

        newJobPost.setGender(addJobPostRequest.getJobPostGender());
        newJobPost.setJobPostLanguageRequirements(getJobPostLanguageRequirement(addJobPostRequest.getJobPostLanguage(), newJobPost));
        newJobPost.setJobPostAssetRequirements(getJobPostAssetRequirement(addJobPostRequest.getJobPostAsset(), newJobPost));
        newJobPost.setJobPostDocumentRequirements(getJobPostDocumentRequirement(addJobPostRequest.getJobPostDocument(), newJobPost));

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
        } else{
            JobStatus jobStatus = JobStatus.find.where().eq("jobStatusId", ServerConstants.JOB_STATUS_ACTIVE).findUnique();
            newJobPost.setJobPostStatus(jobStatus);
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

        List<PreScreenRequirement> preScreenRequirementList = PreScreenRequirement.find.where().eq("job_post_id", jobPost.getJobPostId()).findList();
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
            if(preScreenRequirementAge != null) preScreenRequirementAge.delete();
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
            if(preScreenRequirementExp != null) preScreenRequirementExp.delete();
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
            if(preScreenRequirementEdu != null) preScreenRequirementEdu.delete();
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
                        entry.getValue().delete();
                    }
                }
            }
        } else {
            Map<Integer, PreScreenRequirement> map = multiEntityMap.get(ServerConstants.CATEGORY_LANGUAGE);
            if(map != null) {
                for (Map.Entry<Integer, PreScreenRequirement> entry : map.entrySet()) {
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
                        entry.getValue().delete();
                    }
                }
            }
        } else {
            Map<Integer, PreScreenRequirement> map = multiEntityMap.get(ServerConstants.CATEGORY_DOCUMENT);
            if(map != null) {
                for (Map.Entry<Integer, PreScreenRequirement> entry : map.entrySet()) {
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
                        entry.getValue().delete();
                    }
                }
            }
        } else {
            Map<Integer, PreScreenRequirement> map = multiEntityMap.get(ServerConstants.CATEGORY_ASSET);
            if(map != null) {
                for (Map.Entry<Integer, PreScreenRequirement> entry : map.entrySet()) {
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
            if(preScreenRequirementSalary != null) preScreenRequirementSalary.delete();
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
            if(preScreenRequirementGender != null) preScreenRequirementGender.delete();
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
            if(preScreenRequirementLocation != null) preScreenRequirementLocation.delete();
        }

        if ( jobPost.getJobPostWorkingDays() != 0 && jobPost.getJobPostWorkingDays() != null && jobPost.getJobPostShift() != null) {
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
            if(preScreenRequirementWorkTimings != null) preScreenRequirementWorkTimings.delete();
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

    private static List<JobPostDocumentRequirement> getJobPostDocumentRequirement(List<Long> jobPostDocumentList, JobPost newJobPost) {

        List<JobPostDocumentRequirement> jobPostDocumentRequirementList = new ArrayList<>();
        if(jobPostDocumentList == null || jobPostDocumentList.size() == 0) {
            return jobPostDocumentRequirementList;
        }
        List<IdProof> idProofList = IdProof.find.where().in("IdProofId", jobPostDocumentList).findList();
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
                                            InteractionService.InteractionChannelType channelType)
            throws IOException, JSONException
    {
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

                    String interactionResult = InteractionConstants.INTERACTION_RESULT_CANDIDATE_SELF_APPLIED_JOB;
                    Partner partner = null;
                    if(applyJobRequest.getPartner()){
                        // this job is being applied by a partner for a candidate, hence we need to det partner Id in the job Application table
                        partner = Partner.find.where().eq("partner_id", session().get("partnerId")).findUnique();
                        if(partner != null){
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
                            //setting partner
                            jobApplication.setPartner(partner);
                            SmsUtil.sendJobApplicationSmsViaPartner(existingCandidate.getCandidateFirstName(), existingJobPost.getJobPostTitle(), existingJobPost.getCompany().getCompanyName(), existingCandidate.getCandidateMobile(), jobApplication.getLocality().getLocalityName(), partner.getPartnerFirstName());
                            SmsUtil.sendJobApplicationSmsToPartner(existingCandidate.getCandidateFirstName(), existingJobPost.getJobPostTitle(), existingJobPost.getCompany().getCompanyName(), partner.getPartnerMobile(), jobApplication.getLocality().getLocalityName(), partner.getPartnerFirstName());
                            interactionResult = InteractionConstants.INTERACTION_RESULT_PARTNER_APPLIED_TO_JOB;
                        }
                    } else{
                        SmsUtil.sendJobApplicationSms(existingCandidate.getCandidateFirstName(), existingJobPost.getJobPostTitle(), existingJobPost.getCompany().getCompanyName(), existingCandidate.getCandidateMobile(), jobApplication.getLocality().getLocalityName(), channelType);
                    }

                    jobApplication.save();
                    writeJobApplicationToGoogleSheet(existingJobPost.getJobPostId(), applyJobRequest.getCandidateMobile(), channelType, applyJobRequest.getLocalityId(), partner, applyJobRequest);

                    if (channelType == InteractionService.InteractionChannelType.SELF) {
                        // job application coming from website
                        InteractionService.createInteractionForJobApplicationViaWebsite(
                                existingCandidate.getCandidateUUId(),
                                existingJobPost.getJobPostUUId(),
                                interactionResult + existingJobPost.getJobPostTitle() + " at " + existingJobPost.getCompany().getCompanyName() + "@" + locality.getLocalityName()
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

                } else{
                    applyJobResponse.setStatus(ApplyJobResponse.STATUS_EXISTS);
                    Logger.info("candidate: " + existingCandidate.getCandidateFirstName() + " with mobile: " + existingCandidate.getCandidateMobile() + " already applied to jobPost with jobId:" + existingJobPost.getJobPostId());
                }

                // also create entry in jobPostWorkflow table
                JobPostWorkflow jobPostWorkflow = JobPostWorkflow.find
                        .where()
                        .eq("candidate_id", existingCandidate.getCandidateId())
                        .eq("job_post_id", existingJobPost.getJobPostId()).setMaxRows(1).findUnique();

                if (jobPostWorkflow == null) {
                    jobPostWorkflow = new JobPostWorkflow();
                    jobPostWorkflow.setCandidate(existingCandidate);
                    jobPostWorkflow.setJobPost(existingJobPost);
                    jobPostWorkflow.setStatus(JobPostWorkflowStatus.find.where().eq("statusId", ServerConstants.JWF_STATUS_SELECTED).findUnique());

                    if(channelType == InteractionService.InteractionChannelType.SELF ||
                            channelType == InteractionService.InteractionChannelType.SELF_ANDROID ){
                        jobPostWorkflow.setCreatedBy(channelType.toString());
                    } else {
                        // partner, support, recruiter
                        jobPostWorkflow.setCreatedBy(session().get("sessionUsername"));
                    }
                    jobPostWorkflow.save();
                }
            }
        } else{
            applyJobResponse.setStatus(ApplyJobResponse.STATUS_NO_CANDIDATE);
            Logger.info("Candidate Does not exists");
        }
        return applyJobResponse;
    }

    public static void writeJobApplicationToGoogleSheet(Long jobPostId, String candidateMobile, InteractionService.InteractionChannelType channelType, Integer localityId, Partner partner, ApplyJobRequest applyJobRequest) throws UnsupportedEncodingException {
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

        if(channelType == InteractionService.InteractionChannelType.SELF_ANDROID){
            jobApplicationChannelVal = "Android";
        } else if(channelType == InteractionService.InteractionChannelType.SELF){
            jobApplicationChannelVal = "Website";
        }

        Locality preScreenLocality = Locality.find.where().eq("localityId", localityId).findUnique();
        if(preScreenLocality != null){
            candidatePrescreenLocationVal = preScreenLocality.getLocalityName();
        }

        JobPost jobpost = JobPost.find.where().eq("jobPostId", jobPostId).findUnique();
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
}