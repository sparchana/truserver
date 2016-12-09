package controllers.businessLogic.JobWorkflow;

import api.InteractionConstants;
import api.ServerConstants;
import api.http.httpRequest.AddFeedbackRequest;
import api.http.httpRequest.Recruiter.InterviewStatusRequest;
import api.http.httpRequest.Recruiter.InterviewTodayRequest;
import api.http.httpRequest.Workflow.InterviewDateTime.AddCandidateInterviewSlotDetail;
import api.http.httpRequest.Workflow.PreScreenRequest;
import api.http.httpRequest.Workflow.SelectedCandidateRequest;
import api.http.httpResponse.CandidateExtraData;
import api.http.httpResponse.CandidateScoreData;
import api.http.httpResponse.CandidateWorkflowData;
import api.http.httpResponse.Recruiter.InterviewTodayResponse;
import api.http.httpResponse.Workflow.PreScreenPopulateResponse;
import api.http.httpResponse.Workflow.WorkflowResponse;
import api.http.httpResponse.interview.InterviewResponse;
import com.avaje.ebean.Ebean;
import com.avaje.ebean.Query;
import com.avaje.ebean.RawSql;
import com.avaje.ebean.RawSqlBuilder;
import controllers.businessLogic.CandidateService;
import controllers.businessLogic.InteractionService;
import controllers.businessLogic.MatchingEngineService;
import dao.staticdao.RejectReasonDAO;
import models.entity.*;
import models.entity.OM.*;
import models.entity.Recruiter.Static.RecruiterCreditCategory;
import models.entity.Static.*;
import models.util.NotificationUtil;
import models.util.SmsUtil;
import models.util.Util;
import org.apache.commons.lang3.StringUtils;
import play.Logger;
import play.mvc.Result;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import static api.ServerConstants.*;
import static controllers.businessLogic.Recruiter.RecruiterInteractionService.*;
import static models.util.SmsUtil.*;
import static play.mvc.Controller.session;
import static play.mvc.Results.ok;

/**
 * Created by zero on 4/10/16.
 */
public class JobPostWorkflowEngine {

    /**
     * @param jobPostId             match candidates for this jobPost
     * @param maxAge                max range criteria to be taken into consideration while matching
     * @param gender                gender criteria to be taken into consideration while matching
     * @param experienceIdList      experience duration to be taken into consideration while matching
     * @param jobPostLocalityIdList candidates to be matched within x Km of any of the provided locality
     * @param languageIdList        candidate to be matched for any of this language. Output contains the
     *                              indication to show matching & non-matching language
     */
    public static Map<Long, CandidateWorkflowData> getMatchingCandidate(Long jobPostId,
                                                                        Integer maxAge,
                                                                        Long minSalary,
                                                                        Long maxSalary,
                                                                        Integer gender,
                                                                        List<Integer> experienceIdList,
                                                                        Long jobRoleId,
                                                                        List<Integer> educationIdList,
                                                                        List<Long> jobPostLocalityIdList,
                                                                        List<Integer> languageIdList,
                                                                        List<Integer> jobPostDocumentIdList,
                                                                        List<Integer> jobPostAssetIdList,
                                                                        Double radius) {
        Map<Long, CandidateWorkflowData> matchedCandidateMap = new LinkedHashMap<>();
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);

        JobPost jobPost = JobPost.find.where().eq("jobPostId", jobPostId).findUnique();

        List<Integer> minExperienceList = new ArrayList<>();
        List<Integer> maxExperienceList = new ArrayList<>();

        // get jobrolepref for candidate

        Query<Candidate> query = Candidate.find.query();

        // problem: all age is null/0 and dob is also null
        // select candidate falling under the specified age req
        if (maxAge != null && maxAge != 0) {
            int startYear = currentYear - maxAge;
            query = query
                    .where()
                    .isNotNull("candidateDOB")
                    .ge("candidateDOB", startYear + "-01-01").query();
        }

        // select candidate based on specific gender req, else pass
        if (gender != null && gender >= 0 && gender != ServerConstants.GENDER_ANY) {
            query = query
                    .where()
                    .isNotNull("candidateGender")
                    .eq("candidateGender", gender).query();
        }

        // select candidate whose totalExperience falls under the req exp
        if (experienceIdList != null && experienceIdList.size() > 0) {
            // geDurationFromExperience returns minExperience req. (in Months)
            int minima;
            int maxima;
            for (Integer experienceId : experienceIdList) {
                ExperienceValue experience = getDurationFromExperience(experienceId);
                if (experience != null) {
                    minExperienceList.add(experience.minExperienceValue);
                    maxExperienceList.add(experience.maxExperienceValue);
                } else {
                    break;
                }
            }
            if (minExperienceList.size() > 0) {
                Collections.sort(maxExperienceList, Collections.reverseOrder());
                Collections.sort(minExperienceList);

                minima = minExperienceList.get(0);
                maxima = maxExperienceList.get(0);

                if (minima == 0 && maxima == 0) {
                    query = query
                            .where()
                            .isNotNull("candidateTotalExperience")
                            .eq("candidateTotalExperience", minima).query();
                } else {
                    query = query
                            .where()
                            .isNotNull("candidateTotalExperience")
                            .ge("candidateTotalExperience", minima).query();
                }
                if (maxima != 0) {
                    query = query
                            .where()
                            .isNotNull("candidateTotalExperience")
                            .le("candidateTotalExperience", maxima).query();
                }
            }
        }

        // select candidate w.r.t candidateLastWithdrawnSalary
        if (maxSalary != null && maxSalary != 0) {
            query = query
                    .where()
                    .isNotNull("candidateLastWithdrawnSalary")
                    .le("candidateLastWithdrawnSalary", maxSalary)
                    .query();
        } else if (minSalary != null && minSalary != 0) {
            query = query
                    .where()
                    .isNotNull("candidateLastWithdrawnSalary")
                    .le("candidateLastWithdrawnSalary", minSalary)
                    .query();
        }
        // select candidate w.r.t language
        if (languageIdList != null && languageIdList.size() > 0) {
            query = query.select("*").fetch("languageKnownList")
                    .where()
                    .in("languageKnownList.language.languageId", languageIdList)
                    .query();
        }

        /*// select candidate whose LatLng/HomeLocality in within (X) KM of jobPost LatLng
        if (localityIdList != null && localityIdList.size() > 0) {
            query =  query.select("*").fetch("locality")
                    .where()
                    .in("locality.localityId", localityIdList)
                    .query();
        }*/

        // jobpref-jobrole match with jobpost-jobrole
        if (jobRoleId != null) {
            query = query.select("*").fetch("jobPreferencesList")
                    .where()
                    .in("jobPreferencesList.jobRole.jobRoleId", jobRoleId)
                    .query();
        }

        // education match
        if (educationIdList != null && educationIdList.size() > 0 && !educationIdList.contains(ServerConstants.EDUCATION_TYPE_ANY)) {
            query = query.select("*").fetch("candidateEducation")
                    .where()
                    .isNotNull("candidateEducation")
                    .in("candidateEducation.education.educationId", educationIdList)
                    .query();
        }

        if (jobPostDocumentIdList != null && jobPostDocumentIdList.size() > 0) {
            query = query.select("*").fetch("idProofReferenceList")
                    .where()
                    .isNotNull("idProofReferenceList")
                    .in("idProofReferenceList.idProof.idProofId", jobPostDocumentIdList)
                    .query();
        }
        if (jobPostAssetIdList != null && jobPostAssetIdList.size() > 0) {
            query = query.select("*").fetch("candidateAssetList")
                    .where()
                    .isNotNull("candidateAssetList")
                    .in("candidateAssetList.asset.assetId", jobPostAssetIdList)
                    .query();
        }

        // should not be in workflow table
        List<Long> selectedCandidateIdList = new ArrayList<>();
        for (JobPostWorkflow jpwf : JobPostWorkflow.find.where().eq("job_post_id", jobPostId).findList()) {
            selectedCandidateIdList.add(jpwf.getCandidate().getCandidateId());
        }

        query = query.where()
                .notIn("candidateId", selectedCandidateIdList)
                .query();

        // should be an active candidate
        query = query.select("*").fetch("candidateprofilestatus")
                .where()
                .eq("candidateprofilestatus.profileStatusId", ServerConstants.CANDIDATE_STATE_ACTIVE)
                .query();

        List<Candidate> candidateList = filterByLatLngOrHomeLocality(query.findList(), jobPostLocalityIdList, radius, true);

        Map<Long, CandidateExtraData> allFeature = computeExtraData(candidateList, jobPost, null);

        if (candidateList.size() != 0) {
            for (Candidate candidate : candidateList) {
                CandidateWorkflowData candidateWorkflowData = new CandidateWorkflowData();
                candidateWorkflowData.setCandidate(candidate);
                candidateWorkflowData.setExtraData(allFeature.get(candidate.getCandidateId()));
                matchedCandidateMap.put(candidate.getCandidateId(), candidateWorkflowData);
            }
        }
        return matchedCandidateMap;
    }


    /**
     * this is being used by the recruiter for searching candidates
     *
     * @param maxAge                max range criteria to be taken into consideration while matching
     * @param gender                gender criteria to be taken into consideration while matching
     * @param experienceIdList      experience duration to be taken into consideration while matching
     * @param jobPostLocalityIdList candidates to be matched within x Km of any of the provided locality
     * @param languageIdList        candidate to be matched for any of this language. Output contains the
     *                              indication to show matching & non-matching language
     */
    public static Map<Long, CandidateWorkflowData> getCandidateForRecruiterSearch(Integer maxAge,
                                                                                  Long minSalary,
                                                                                  Long maxSalary,
                                                                                  Integer gender,
                                                                                  List<Integer> experienceIdList,
                                                                                  Long jobRoleId,
                                                                                  List<Integer> educationIdList,
                                                                                  List<Long> jobPostLocalityIdList,
                                                                                  List<Integer> languageIdList,
                                                                                  Double radius) {
        List<Integer> minExperienceList = new ArrayList<>();
        List<Integer> maxExperienceList = new ArrayList<>();


        Map<Long, CandidateWorkflowData> matchedCandidateMap = new LinkedHashMap<>();

        Query<Candidate> query = Candidate.find.query();

        // select candidate whose totalExperience falls under the req exp
        if (experienceIdList != null && experienceIdList.size() > 0) {
            // geDurationFromExperience returns minExperience req. (in Months)
            int minima;
            int maxima;
            for (Integer experienceId : experienceIdList) {
                ExperienceValue experience = getDurationFromExperience(experienceId);
                if (experience != null) {
                    minExperienceList.add(experience.minExperienceValue);
                    maxExperienceList.add(experience.maxExperienceValue);
                } else {
                    break;
                }
            }
            if (minExperienceList.size() > 0) {
                Collections.sort(maxExperienceList, Collections.reverseOrder());
                Collections.sort(minExperienceList);

                minima = minExperienceList.get(0);
                maxima = maxExperienceList.get(0);

                if (minima == 0 && maxima == 0) {
                    query = query
                            .where()
                            .isNotNull("candidateTotalExperience")
                            .eq("candidateTotalExperience", minima).query();
                } else {
                    query = query
                            .where()
                            .isNotNull("candidateTotalExperience")
                            .ge("candidateTotalExperience", minima).query();
                }
                if (maxima != 0) {
                    query = query
                            .where()
                            .isNotNull("candidateTotalExperience")
                            .le("candidateTotalExperience", maxima).query();
                }
            }
        }

        int currentYear = Calendar.getInstance().get(Calendar.YEAR);

        // problem: all age is null/0 and dob is also null
        // select candidate falling under the specified age req
        if (maxAge != null && maxAge != 0) {
            int startYear = currentYear - maxAge;
            query = query
                    .where()
                    .isNotNull("candidateDOB")
                    .ge("candidateDOB", startYear + "-01-01").query();
        }

        // select candidate based on specific gender req, else pass
        if (gender != null && gender >= 0 && gender != ServerConstants.GENDER_ANY) {
            query = query
                    .where()
                    .isNotNull("candidateGender")
                    .eq("candidateGender", gender).query();
        }

        // select candidate w.r.t candidateLastWithdrawnSalary
        if (maxSalary != null && maxSalary != 0) {
            query = query
                    .where()
                    .isNotNull("candidateLastWithdrawnSalary")
                    .le("candidateLastWithdrawnSalary", maxSalary)
                    .query();
        } else if (minSalary != null && minSalary != 0) {
            query = query
                    .where()
                    .isNotNull("candidateLastWithdrawnSalary")
                    .le("candidateLastWithdrawnSalary", minSalary)
                    .query();
        }
        // select candidate w.r.t language
        if (languageIdList != null && languageIdList.size() > 0) {
            query = query.select("*").fetch("languageKnownList")
                    .where()
                    .in("languageKnownList.language.languageId", languageIdList)
                    .query();
        }

        /*// select candidate whose LatLng/HomeLocality in within (X) KM of jobPost LatLng
        if (localityIdList != null && localityIdList.size() > 0) {
            query =  query.select("*").fetch("locality")
                    .where()
                    .in("locality.localityId", localityIdList)
                    .query();
        }*/

        // jobpref-jobrole match with jobpost-jobrole
        if (jobRoleId != null) {
            query = query.select("*").fetch("jobPreferencesList")
                    .where()
                    .in("jobPreferencesList.jobRole.jobRoleId", jobRoleId)
                    .query();
        }

        // education match
        if (educationIdList != null && educationIdList.size() > 0 && !educationIdList.contains(ServerConstants.EDUCATION_TYPE_ANY)) {
            query = query.select("*").fetch("candidateEducation")
                    .where()
                    .isNotNull("candidateEducation")
                    .in("candidateEducation.education.educationId", educationIdList)
                    .query();
        }

        // should be an active candidate
        query = query.select("*").fetch("candidateprofilestatus")
                .where()
                .eq("candidateprofilestatus.profileStatusId", ServerConstants.CANDIDATE_STATE_ACTIVE)
                .query();


/*        //query candidate query with the filter params
        query = getFilteredQuery(maxAge, minSalary, maxSalary,gender, jobRoleId, educationId, languageIdList, experience);*/

        List<Candidate> candidateList = filterByLatLngOrHomeLocality(query.findList(), jobPostLocalityIdList, radius, true);

        Map<Long, CandidateExtraData> allFeature = computeExtraDataForRecruiterSearchResult(candidateList);

        if (candidateList.size() != 0) {
            for (Candidate candidate : candidateList) {
                if (CandidateService.getP0FieldsCompletionPercent(candidate) > 0.5) {
                    CandidateWorkflowData candidateWorkflowData = new CandidateWorkflowData();
                    candidateWorkflowData.setCandidate(candidate);
                    candidateWorkflowData.setExtraData(allFeature.get(candidate.getCandidateId()));
                    matchedCandidateMap.put(candidate.getCandidateId(), candidateWorkflowData);
                }
            }
        }


        return matchedCandidateMap;
    }

    /**
     * @param jobPostId Prepare params and calls getMatchingCandidate
     */
    public static Map<Long, CandidateWorkflowData> getMatchingCandidate(Long jobPostId) {
        // prep params for a jobPost
        JobPost jobPost = JobPost.find.where().eq("jobPostId", jobPostId).findUnique();

        Integer maxAge = jobPost.getJobPostMaxAge();
        Long minSalary = jobPost.getJobPostMinSalary();
        Long maxSalary = jobPost.getJobPostMaxSalary();
        Integer gender = jobPost.getGender();

        Long jobRoleId = jobPost.getJobRole().getJobRoleId();

        // geDurationFromExperience returns minExperience req. (in Months)
        Integer experienceId = jobPost.getJobPostExperience() != null ? jobPost.getJobPostExperience().getExperienceId() : null;

        Integer educationId = jobPost.getJobPostEducation() != null ? jobPost.getJobPostEducation().getEducationId() : null;

        List<Integer> experienceIdList = new ArrayList<>();
        experienceIdList.add(experienceId);

        List<Integer> educationIdList = new ArrayList<>();
        educationIdList.add(educationId);
        List<JobPostToLocality> jobPostToLocalityList = jobPost.getJobPostToLocalityList();
        List<Long> localityIdList = new ArrayList<>();
        for (JobPostToLocality jobPostToLocality : jobPostToLocalityList) {
            localityIdList.add(jobPostToLocality.getLocality().getLocalityId());
        }

        List<JobPostLanguageRequirement> languageRequirements = jobPost.getJobPostLanguageRequirements();
        List<Integer> languageIdList = new ArrayList<>();
        for (JobPostLanguageRequirement requirement : languageRequirements) {
            languageIdList.add(requirement.getLanguage().getLanguageId());
        }

        List<Integer> jobPostDocumentIdList = new ArrayList<>();
        for (JobPostDocumentRequirement jobPostDocumentRequirement : jobPost.getJobPostDocumentRequirements()) {
            jobPostDocumentIdList.add(jobPostDocumentRequirement.getIdProof().getIdProofId());
        }

        List<Integer> jobPostAssetIdList = new ArrayList<>();
        for (JobPostAssetRequirement jobPostAssetRequirement : jobPost.getJobPostAssetRequirements()) {
            jobPostAssetIdList.add(jobPostAssetRequirement.getAsset().getAssetId());
        }

        // call master method
        return getMatchingCandidate(jobPostId, maxAge, minSalary, maxSalary, gender, experienceIdList, jobRoleId, educationIdList, localityIdList, languageIdList, jobPostDocumentIdList, jobPostAssetIdList, ServerConstants.DEFAULT_MATCHING_ENGINE_RADIUS);
    }

    public static Map<Long, CandidateWorkflowData> getSelectedCandidates(Long jobPostId) {

        Integer status = ServerConstants.JWF_STATUS_SELECTED;
        StringBuilder workFlowQueryBuilder = new StringBuilder("select createdby, candidate_id, creation_timestamp, job_post_id, status_id from job_post_workflow i " +
                " where i.job_post_id " +
                " = ('" + jobPostId + "') " +
                " and (status_id = '" + ServerConstants.JWF_STATUS_SELECTED + "' or status_id = '" + ServerConstants.JWF_STATUS_PRESCREEN_ATTEMPTED + "') " +
                " and creation_timestamp = " +
                " (select max(creation_timestamp) from job_post_workflow " +
                "     where i.candidate_id = job_post_workflow.candidate_id " +
                "     and i.job_post_id = job_post_workflow.job_post_id)  " +
                " order by creation_timestamp desc ");


        RawSql rawSql = RawSqlBuilder.parse(workFlowQueryBuilder.toString())
                .columnMapping("creation_timestamp", "creationTimestamp")
                .columnMapping("job_post_id", "jobPost.jobPostId")
                .columnMapping("status_id", "status.statusId")
                .columnMapping("candidate_id", "candidate.candidateId")
                .columnMapping("createdby", "createdBy")
                .create();

        List<JobPostWorkflow> jobPostWorkflowList = Ebean.find(JobPostWorkflow.class)
                .setRawSql(rawSql)
                .findList();

//        List<JobPostWorkflow> jobPostWorkflowList = JobPostWorkflow.find.where()
//                .eq("jobPost.jobPostId", jobPostId)
//                .or(eq("status.statusId", ServerConstants.JWF_STATUS_SELECTED), eq("status.statusId", ServerConstants.JWF_STATUS_PRESCREEN_ATTEMPTED))
//                .setDistinct(true)
//                .orderBy().desc("creation_timestamp").findList();

        List<Candidate> candidateList = new ArrayList<>();

        Map<Long, CandidateWorkflowData> selectedCandidateMap = new LinkedHashMap<>();


        // until view is not available over this table, this loop get the distinct candidate who
        // got selected for a job post recently
        for (JobPostWorkflow jpwf : jobPostWorkflowList) {
            candidateList.add(jpwf.getCandidate());
        }
        // prep params for a jobPost
        JobPost jobPost = JobPost.find.where().eq("jobPostId", jobPostId).findUnique();

        if (jobPost == null) {
            Logger.error("JobPostId: " + jobPostId + " does not exists");
            return selectedCandidateMap;
        }

        List<JobPostToLocality> jobPostToLocalityList = jobPost.getJobPostToLocalityList();
        List<Long> localityIdList = new ArrayList<>();
        for (JobPostToLocality jobPostToLocality : jobPostToLocalityList) {
            localityIdList.add(jobPostToLocality.getLocality().getLocalityId());
        }

        candidateList = filterByLatLngOrHomeLocality(candidateList, localityIdList, ServerConstants.DEFAULT_MATCHING_ENGINE_RADIUS, false);
        Map<Long, CandidateExtraData> candidateExtraDataMap = computeExtraData(candidateList, JobPost.find.where().eq("jobPostId", jobPostId).findUnique(), new ArrayList<Integer>(Arrays.asList(status)));

        for (Candidate candidate : candidateList) {
            CandidateWorkflowData candidateWorkflowData = new CandidateWorkflowData();
            candidateWorkflowData.setCandidate(candidate);
            candidateWorkflowData.setExtraData(candidateExtraDataMap.get(candidate.getCandidateId()));
            selectedCandidateMap.put(candidate.getCandidateId(), candidateWorkflowData);
        }

        return selectedCandidateMap;
    }

    public static WorkflowResponse saveSelectedCandidates(SelectedCandidateRequest request) {
        String interactionResult = InteractionConstants.INTERACTION_RESULT_CANDIDATE_SELECTED_FOR_PRESCREEN;
        WorkflowResponse response = new WorkflowResponse();
        List<Candidate> selectedCandidateList = Candidate.find.where().in("candidateId", request.getSelectedCandidateIdList()).findList();

        if (request.getSelectedCandidateIdList() != null
                && request.getSelectedCandidateIdList().size() == 0) {
            response.setStatus(WorkflowResponse.STATUS.FAILED);
            response.setMessage("Something Went Wrong ! Please try again");
            return response;
        }

        List<Long> selectedCandidateIdList = new ArrayList<>();
        for (Candidate candidate : selectedCandidateList) {
            selectedCandidateIdList.add(candidate.getCandidateId());
        }

        JobPost jobPost = JobPost.find.where().eq("jobPostId", request.getJobPostId()).findUnique();
        JobPostWorkflowStatus status = JobPostWorkflowStatus.find.where().eq("statusId", ServerConstants.JWF_STATUS_SELECTED).findUnique();

        Map<?, JobPostWorkflow> workflowMap = JobPostWorkflow.find
                .where()
                .eq("job_post_id", request.getJobPostId())
                .eq("status_id", status.getStatusId())
                .in("candidate_id", selectedCandidateIdList)
                .setMapKey("candidate_id")
                .findMap();

        for (Candidate candidate : selectedCandidateList) {
            JobPostWorkflow jobPostWorkflow = workflowMap.get(candidate.getCandidateId());
            if (jobPostWorkflow == null) {
                jobPostWorkflow = new JobPostWorkflow();
                jobPostWorkflow.setJobPost(jobPost);
                jobPostWorkflow.setCandidate(candidate);
                jobPostWorkflow.setCreatedBy(session().get("sessionUsername"));
                jobPostWorkflow.setChannel(Integer.valueOf(session().get("sessionChannel")));
                jobPostWorkflow.setStatus(status);
                jobPostWorkflow.save();
                response.setStatus(WorkflowResponse.STATUS.SUCCESS);
                response.setMessage("Selection completed successfully.");
                // not redirecting user to next page.
                response.setNextView("match_view");

                interactionResult += jobPostWorkflow.getJobPost().getJobPostId() + ": " + jobPostWorkflow.getJobPost().getJobRole().getJobName();
                // chances are the scrapped data may not have proper company.
                if (jobPostWorkflow.getJobPost().getCompany() != null) {
                    interactionResult += "@" + jobPostWorkflow.getJobPost().getCompany().getCompanyName();
                }

                // save the interaction
                InteractionService.createWorkflowInteraction(
                        jobPostWorkflow.getJobPostWorkflowUUId(),
                        candidate.getCandidateUUId(),
                        InteractionConstants.INTERACTION_TYPE_CANDIDATE_SELECTED_FOR_PRESCREEN,
                        null,
                        interactionResult,
                        null
                );
            } else {
                Logger.error("Error! Candidate already exists in another status");
                // TODO handle this case as error in response as well
                response.setStatus(WorkflowResponse.STATUS.FAILED);
                response.setMessage("Selection Error.");
                response.setNextView("match_view");
            }
        }

        response.setRedirectUrl("/support/workflow/");

        return response;
    }

    public static PreScreenPopulateResponse getJobPostVsCandidate(Long jobPostId, Long candidateId, Boolean rePreScreen) {
        PreScreenPopulateResponse populateResponse = new PreScreenPopulateResponse();

        boolean isCandidateDataMissing = false;
        Candidate candidate = Candidate.find.where().eq("candidateId", candidateId).findUnique();
        if (candidate == null) {
            populateResponse.setStatus(PreScreenPopulateResponse.Status.FAILURE);
            return populateResponse;
        }

        if (rePreScreen == null) {
            // Default behaviour
            rePreScreen = true;
        }

        JobPost jobPost = JobPost.find.where().eq("jobPostId", jobPostId).findUnique();
        if (jobPost == null) {
            populateResponse.setStatus(PreScreenPopulateResponse.Status.FAILURE);
            return populateResponse;
        }

        if (!rePreScreen) {
            // fetch existing workflow old
            JobPostWorkflow jobPostWorkflowOld = JobPostWorkflow.find.where()
                    .eq("jobPost.jobPostId", jobPostId)
                    .eq("candidate.candidateId", candidateId)
                    .orderBy().desc("creationTimestamp").setMaxRows(1).findUnique();

            if ((jobPostWorkflowOld != null) && (jobPostWorkflowOld.getStatus().getStatusId() >= ServerConstants.JWF_STATUS_PRESCREEN_FAILED)) {
                populateResponse.setStatus(PreScreenPopulateResponse.Status.INVALID);
                return populateResponse;
            }
        }

        List<PreScreenRequirement> preScreenRequirementList = PreScreenRequirement.find.where()
                .eq("jobPost.jobPostId", jobPostId).orderBy().asc("category").findList();


        // constructor for this class make all default flag as true, we will mark it false wherever its not satisfied
        populateResponse.jobPostId = jobPostId;
        populateResponse.candidateId = candidateId;

        populateResponse.setJobPostMinReq(jobPost.getJobPostMinRequirement());

        PreScreenPopulateResponse.PreScreenElement preScreenElement;

        Map<Integer, List<PreScreenRequirement>> preScreenMap = new HashMap<>();

        for (PreScreenRequirement preScreenRequirement : preScreenRequirementList) {
            List<PreScreenRequirement> preScreenRequirements = preScreenMap.get(preScreenRequirement.getCategory());
            if (preScreenRequirements == null) {
                preScreenRequirements = new ArrayList<>();
            }
            preScreenRequirements.add(preScreenRequirement);
            preScreenMap.put(preScreenRequirement.getCategory(), preScreenRequirements);
        }

        for (Map.Entry<Integer, List<PreScreenRequirement>> entry : preScreenMap.entrySet()) {
            List<String> jobPostPlaceHolderList = new ArrayList<>();
            List<String> candidatePlaceHolderList = new ArrayList<>();

            switch (entry.getKey()) {
                case ServerConstants.CATEGORY_DOCUMENT:
                    boolean isAvailable = false;
                    preScreenElement = new PreScreenPopulateResponse.PreScreenElement();
                    preScreenElement.setPropertyTitle(ServerConstants.PROPERTY_TYPE_MAP.get(PROPERTY_TYPE_DOCUMENT));
                    preScreenElement.setPropertyId(PROPERTY_TYPE_DOCUMENT);

                    preScreenElement.jobPostElementList = new ArrayList<>();
                    preScreenElement.candidateElementList = new ArrayList<>();
                    if (candidate.getIdProofReferenceList() != null && candidate.getIdProofReferenceList().size() > 0) {
                        isAvailable = true;
                    }
                    Map<Integer, IDProofReference> candidateIdProofMap = new HashMap<>();
                    for (IDProofReference idProofReference : candidate.getIdProofReferenceList()) {
                        candidateIdProofMap.put(idProofReference.getIdProof().getIdProofId(), idProofReference);
                    }

                    for (PreScreenRequirement preScreenRequirement : entry.getValue()) {
                        boolean addToList = false;
                        // property ID
                        preScreenElement.propertyIdList.add(preScreenRequirement.getPreScreenRequirementId());

                        // candidateElement list
                        IDProofReference idProofReference = candidateIdProofMap.get(preScreenRequirement.getIdProof().getIdProofId());

                        if (isAvailable && idProofReference != null) {
                            // candidate placeholder
                            candidatePlaceHolderList.add(idProofReference.getIdProof().getIdProofName());

                            if ((idProofReference.getIdProofNumber() == null || idProofReference.getIdProofNumber().trim().isEmpty())) {
                                preScreenElement.isMatching = false;
                                addToList = true;
                            }
                            preScreenElement.candidateElementList.add(new PreScreenPopulateResponse.PreScreenCustomObject(idProofReference,
                                    preScreenRequirement.getIdProof().getIdProofName(), true));
                        } else {
                            preScreenElement.isMatching = false;
                            addToList = true;
                        }


                        // jobpost placeholder, contains all idProofName required by jobpost
                        jobPostPlaceHolderList.add(preScreenRequirement.getIdProof().getIdProofName());

                        // jobPostElement list
                        if (addToList) {

                            preScreenElement.jobPostElementList.add(
                                    new PreScreenPopulateResponse.PreScreenCustomObject(preScreenRequirement.getIdProof(),
                                            preScreenRequirement.getIdProof().getIdProofName(), true));
                        }
                    }
                    preScreenElement.jobPostPlaceHolder = StringUtils.join(jobPostPlaceHolderList, ", ");
                    preScreenElement.candidatePlaceHolder = StringUtils.join(candidatePlaceHolderList, ", ");
                    preScreenElement.isSingleEntity = false;
                    populateResponse.elementList.add(preScreenElement);
                    break;
                case ServerConstants.CATEGORY_LANGUAGE:
                    Map<Integer, Language> candidateLanguageMap = new HashMap<>();
                    List<Language> jobPostLanguageList = new ArrayList<>();

                    preScreenElement = new PreScreenPopulateResponse.PreScreenElement();
                    preScreenElement.setPropertyTitle(ServerConstants.PROPERTY_TYPE_MAP.get(PROPERTY_TYPE_LANGUAGE));
                    preScreenElement.setPropertyId(PROPERTY_TYPE_LANGUAGE);

                    preScreenElement.jobPostElementList = new ArrayList<>();
                    preScreenElement.candidateElementList = new ArrayList<>();

                    if (candidate.getLanguageKnownList() != null && candidate.getLanguageKnownList().size() > 0) {
                        for (LanguageKnown lk : candidate.getLanguageKnownList()) {
                            candidateLanguageMap.put(lk.getLanguage().getLanguageId(), lk.getLanguage());
                        }
                    } else {
                        preScreenElement.isMatching = false;
                    }
                    for (PreScreenRequirement preScreenRequirement : entry.getValue()) {
                        preScreenElement.propertyIdList.add(preScreenRequirement.getPreScreenRequirementId());

                        // jobpost placeholder
                        jobPostPlaceHolderList.add(preScreenRequirement.getLanguage().getLanguageName());

                        // add language object which are not provided by candidate
                        if (candidateLanguageMap.get(preScreenRequirement.getLanguage().getLanguageId()) == null) {
                            preScreenElement.jobPostElementList.add(new PreScreenPopulateResponse.PreScreenCustomObject(preScreenRequirement.getLanguage(),
                                    preScreenRequirement.getLanguage().getLanguageName(), true));
                        }

                        jobPostLanguageList.add(preScreenRequirement.getLanguage());
                    }

                    if (jobPostLanguageList.size() > 0) {
                        for (Language language : jobPostLanguageList) {
                            if (candidateLanguageMap.get(language.getLanguageId()) != null) {
                                preScreenElement.candidateElementList.add(new PreScreenPopulateResponse.PreScreenCustomObject(language,
                                        language.getLanguageName(), true));

                                // candidate placeholder
                                candidatePlaceHolderList.add(language.getLanguageName());

                            } else {
                                preScreenElement.isMatching = false;
                            }
                        }
                    }

                    preScreenElement.jobPostPlaceHolder = StringUtils.join(jobPostPlaceHolderList, ", ");
                    preScreenElement.candidatePlaceHolder = StringUtils.join(candidatePlaceHolderList, ", ");
                    preScreenElement.isSingleEntity = false;
                    populateResponse.elementList.add(preScreenElement);
                    break;
                case ServerConstants.CATEGORY_ASSET:

                    Map<Integer, Asset> candidateAssetMap = new HashMap<>();
                    List<Asset> jobPostAssetList = new ArrayList<>();
                    // we don't capture candidate asset detail into asset object
                    preScreenElement = new PreScreenPopulateResponse.PreScreenElement();
                    preScreenElement.setPropertyTitle(ServerConstants.PROPERTY_TYPE_MAP.get(PROPERTY_TYPE_ASSET_OWNED));
                    preScreenElement.setPropertyId(PROPERTY_TYPE_ASSET_OWNED);

                    preScreenElement.jobPostElementList = new ArrayList<>();
                    preScreenElement.candidateElementList = new ArrayList<>();

                    if (candidate.getCandidateAssetList() != null && candidate.getCandidateAssetList().size() > 0) {
                        for (CandidateAsset ca : candidate.getCandidateAssetList()) {
                            candidateAssetMap.put(ca.getAsset().getAssetId(), ca.getAsset());
                        }
                    } else {
                        preScreenElement.isMatching = false;
                    }

                    for (PreScreenRequirement preScreenRequirement : entry.getValue()) {
                        // jobpost placeholder
                        jobPostPlaceHolderList.add(preScreenRequirement.getAsset().getAssetTitle());


                        // add only those asset which are not provided by candidate
                        if (candidateAssetMap.get(preScreenRequirement.getAsset().getAssetId()) == null) {
                            preScreenElement.jobPostElementList.add(new PreScreenPopulateResponse.PreScreenCustomObject(preScreenRequirement.getAsset(),
                                    preScreenRequirement.getAsset().getAssetTitle(), true));
                        }

                        // TODO rename everywhere propertyIdList to requirementIdList
                        preScreenElement.propertyIdList.add(preScreenRequirement.getPreScreenRequirementId());

                        jobPostAssetList.add(preScreenRequirement.getAsset());

                    }


                    for (Asset asset : jobPostAssetList) {
                        if (candidateAssetMap.get(asset.getAssetId()) != null) {
                            preScreenElement.candidateElementList.add(new PreScreenPopulateResponse.PreScreenCustomObject(asset,
                                    asset.getAssetTitle(), true));

                            // candidate placeholder
                            candidatePlaceHolderList.add(asset.getAssetTitle());
                        } else {
                            preScreenElement.isMatching = false;
                        }
                    }

                    // set final placeholders here
                    preScreenElement.jobPostPlaceHolder = StringUtils.join(jobPostPlaceHolderList, ", ");
                    preScreenElement.candidatePlaceHolder = StringUtils.join(candidatePlaceHolderList, ", ");

                    preScreenElement.isSingleEntity = false;
                    preScreenElement.isMinReq = true;

                    populateResponse.elementList.add(preScreenElement);
                    break;
                case ServerConstants.CATEGORY_PROFILE:
                    for (PreScreenRequirement preScreenRequirement : entry.getValue()) {
                        if (preScreenRequirement != null && preScreenRequirement.getProfileRequirement() != null) {
                            if (preScreenRequirement.getProfileRequirement().getProfileRequirementTitle().equalsIgnoreCase("age")) {
                                if (jobPost.getJobPostMaxAge() != null && jobPost.getJobPostMaxAge() > 0) {
                                    preScreenElement = new PreScreenPopulateResponse.PreScreenElement();

                                    preScreenElement.setPropertyTitle(ServerConstants.PROPERTY_TYPE_MAP.get(PROPERTY_TYPE_MAX_AGE));
                                    preScreenElement.setPropertyId(PROPERTY_TYPE_MAX_AGE);

                                    preScreenElement.propertyIdList.add(preScreenRequirement.getPreScreenRequirementId());
                                    preScreenElement.jobPostElement = (new PreScreenPopulateResponse.PreScreenCustomObject(null,
                                            jobPost.getJobPostMaxAge(), false));

                                    // set final jobPost placeholders here
                                    preScreenElement.jobPostPlaceHolder = jobPost.getJobPostMaxAge();

                                    if (candidate.getCandidateAge() != null && candidate.getCandidateAge() > 0) {
                                        preScreenElement.candidateElement = (new PreScreenPopulateResponse.PreScreenCustomObject(null,
                                                candidate.getCandidateAge(), false));

                                        // set final candidate placeholders here
                                        preScreenElement.candidatePlaceHolder = candidate.getCandidateAge();

                                        if (!(jobPost.getJobPostMaxAge() >= candidate.getCandidateAge())) {
                                            preScreenElement.isMatching = false;
                                        }
                                    } else {
                                        // candidate age is not available hence not a match
                                        preScreenElement.isMatching = false;
                                    }
                                    populateResponse.elementList.add(preScreenElement);
                                }
                            } else if (preScreenRequirement.getProfileRequirement().getProfileRequirementTitle().equalsIgnoreCase("experience")) {
                                if (jobPost.getJobPostExperience() != null) {
                                    preScreenElement = new PreScreenPopulateResponse.PreScreenElement();
                                    preScreenElement.setPropertyTitle(ServerConstants.PROPERTY_TYPE_MAP.get(PROPERTY_TYPE_EXPERIENCE));
                                    preScreenElement.setPropertyId(PROPERTY_TYPE_EXPERIENCE);

                                    preScreenElement.propertyIdList.add(preScreenRequirement.getPreScreenRequirementId());
                                    ExperienceValue jobPostMinMaxExp = getDurationFromExperience(jobPost.getJobPostExperience().getExperienceId());
                                    preScreenElement.jobPostElement = (new PreScreenPopulateResponse.PreScreenCustomObject(null,
                                            jobPost.getJobPostExperience().getExperienceType(), false));


                                    // set final jobPost placeholders here
                                    preScreenElement.jobPostPlaceHolder = jobPost.getJobPostExperience().getExperienceType();

                                    if (candidate.getCandidateTotalExperience() != null && jobPostMinMaxExp != null) {
                                        double totalExpInYrs = ((double) candidate.getCandidateTotalExperience()) / 12;
                                        preScreenElement.candidateElement = (new PreScreenPopulateResponse.PreScreenCustomObject(jobPost.getJobPostExperience(),
                                                (Util.RoundTo2Decimals(totalExpInYrs) + " Yrs"), true));

                                        // set final candidate placeholders here
                                        preScreenElement.candidatePlaceHolder = (Util.RoundTo2Decimals(totalExpInYrs) + " Yrs");


                                        if (!(jobPostMinMaxExp.minExperienceValue > 0 && candidate.getCandidateTotalExperience() > 0
                                                && jobPostMinMaxExp.minExperienceValue <= candidate.getCandidateTotalExperience())) {
                                            if ((jobPostMinMaxExp.minExperienceValue == 0 && candidate.getCandidateTotalExperience() == 0)) {
                                                preScreenElement.isMatching = true;
                                            } else {
                                                preScreenElement.isMatching = false;
                                            }
                                        }
                                    } else if (jobPostMinMaxExp == null && jobPost.getJobPostExperience().getExperienceType().equalsIgnoreCase("any")) {
                                        preScreenElement.isMatching = true;
                                        if (candidate.getCandidateTotalExperience() != null) {
                                            double totalExpInYrs = ((double) candidate.getCandidateTotalExperience()) / 12;
                                            preScreenElement.candidateElement = (new PreScreenPopulateResponse.PreScreenCustomObject(jobPost.getJobPostExperience(),
                                                    (Util.RoundTo2Decimals(totalExpInYrs) + " Yrs"), true));

                                            // set final candidate placeholders here
                                            preScreenElement.candidatePlaceHolder = (Util.RoundTo2Decimals(totalExpInYrs) + " Yrs");

                                        }
                                    } else {
                                        // candidate exp is not available hence not a match
                                        preScreenElement.isMatching = false;
                                    }
                                    populateResponse.elementList.add(preScreenElement);
                                }
                            } else if (preScreenRequirement.getProfileRequirement().getProfileRequirementTitle().equalsIgnoreCase("education")) {
                                if (jobPost.getJobPostEducation() != null) {
                                    preScreenElement = new PreScreenPopulateResponse.PreScreenElement();
                                    preScreenElement.setPropertyTitle(ServerConstants.PROPERTY_TYPE_MAP.get(PROPERTY_TYPE_EDUCATION));
                                    preScreenElement.setPropertyId(PROPERTY_TYPE_EDUCATION);

                                    preScreenElement.propertyIdList.add(preScreenRequirement.getPreScreenRequirementId());
                                    preScreenElement.jobPostElement = (new PreScreenPopulateResponse.PreScreenCustomObject(jobPost.getJobPostEducation(),
                                            jobPost.getJobPostEducation().getEducationName(), true));

                                    // set final jobPost placeholders here
                                    preScreenElement.jobPostPlaceHolder = jobPost.getJobPostEducation().getEducationName();

                                    if (candidate.getCandidateEducation() != null && candidate.getCandidateEducation().getEducation() != null) {
                                        preScreenElement.candidateElement = (new PreScreenPopulateResponse.PreScreenCustomObject(candidate.getCandidateEducation().getEducation(),
                                                candidate.getCandidateEducation().getEducation().getEducationName(), true));

                                        // set final candidate placeholders here
                                        preScreenElement.candidatePlaceHolder = candidate.getCandidateEducation().getEducation().getEducationName();

                                        if (!(jobPost.getJobPostEducation().getEducationName().trim().equalsIgnoreCase("any")) && !((candidate.getCandidateEducation().getEducation().getEducationId() - jobPost.getJobPostEducation().getEducationId()) >= 0)) {
                                            preScreenElement.isMatching = false;
                                        }
                                    } else {
                                        // candidate edu is not available hence not a match
                                        preScreenElement.isMatching = false;
                                    }
                                    populateResponse.elementList.add(preScreenElement);
                                }
                            } else if (preScreenRequirement.getProfileRequirement().getProfileRequirementTitle().equalsIgnoreCase("gender")) {
                                if (jobPost.getGender() != null) {
                                    preScreenElement = new PreScreenPopulateResponse.PreScreenElement();
                                    preScreenElement.setPropertyTitle(ServerConstants.PROPERTY_TYPE_MAP.get(PROPERTY_TYPE_GENDER));
                                    preScreenElement.setPropertyId(PROPERTY_TYPE_GENDER);

                                    preScreenElement.propertyIdList.add(preScreenRequirement.getPreScreenRequirementId());
                                    preScreenElement.jobPostElement = new PreScreenPopulateResponse.PreScreenCustomObject(jobPost.getGender(),
                                            jobPost.getGender() == ServerConstants.GENDER_ANY ? "Any" : jobPost.getGender() == ServerConstants.GENDER_MALE ? "Male" : "Female", true);

                                    // set final jobPost placeholders here
                                    preScreenElement.jobPostPlaceHolder = jobPost.getGender() == ServerConstants.GENDER_ANY ? "Any" : jobPost.getGender() == ServerConstants.GENDER_MALE ? "Male" : "Female";


                                    if (candidate.getCandidateGender() != null) {
                                        preScreenElement.candidateElement = new PreScreenPopulateResponse.PreScreenCustomObject(candidate.getCandidateGender(),
                                                candidate.getCandidateGender() == ServerConstants.GENDER_MALE ? "Male" : "Female", true);

                                        // set final candidate placeholders here
                                        preScreenElement.candidatePlaceHolder = candidate.getCandidateGender() == ServerConstants.GENDER_MALE ? "Male" : "Female";


                                        if (candidate.getCandidateGender() != jobPost.getGender() && jobPost.getGender() != ServerConstants.GENDER_ANY) {
                                            preScreenElement.isMatching = false;
                                        }
                                    } else {
                                        preScreenElement.isMatching = false;
                                    }
                                    preScreenElement.isMinReq = false;

                                    populateResponse.elementList.add(preScreenElement);
                                }
                            } else if (preScreenRequirement.getProfileRequirement().getProfileRequirementTitle().equalsIgnoreCase("salary")) {
                                if (jobPost.getJobPostMinSalary() != null && jobPost.getJobPostMinSalary() != 0) {
                                    preScreenElement = new PreScreenPopulateResponse.PreScreenElement();
                                    preScreenElement.setPropertyTitle(ServerConstants.PROPERTY_TYPE_MAP.get(PROPERTY_TYPE_SALARY));
                                    preScreenElement.setPropertyId(PROPERTY_TYPE_SALARY);

                                    preScreenElement.propertyIdList.add(preScreenRequirement.getPreScreenRequirementId());
                                    String result = ("Rs." + jobPost.getJobPostMinSalary());

                                    if (jobPost.getJobPostMaxSalary() != null && jobPost.getJobPostMaxSalary() != 0) {
                                        result += " - Rs." + jobPost.getJobPostMaxSalary();
                                    }
                                    preScreenElement.jobPostElement = new PreScreenPopulateResponse.PreScreenCustomObject(jobPost.getJobPostMinSalary(),
                                            result, true);

                                    // set final jobPost placeholders here
                                    preScreenElement.jobPostPlaceHolder = result;

                                    if (candidate.getCandidateLastWithdrawnSalary() != null) {
                                        preScreenElement.candidateElement = new PreScreenPopulateResponse.PreScreenCustomObject((candidate.getCandidateLastWithdrawnSalary()),
                                                (candidate.getCandidateLastWithdrawnSalary()), true);

                                        // set final candidate placeholders here
                                        preScreenElement.candidatePlaceHolder = (candidate.getCandidateLastWithdrawnSalary());

                                        if (candidate.getCandidateLastWithdrawnSalary() > jobPost.getJobPostMinSalary()) {
                                            if (jobPost.getJobPostMaxSalary() != null
                                                    && jobPost.getJobPostMaxSalary() != 0
                                                    && (candidate.getCandidateLastWithdrawnSalary() > jobPost.getJobPostMaxSalary())) {
                                                preScreenElement.isMatching = false;
                                            }
                                        }
                                    } else {
                                        // if candidate is fresher then don't match for last withdrawn salary
                                        if ((candidate.getCandidateTotalExperience() != null && candidate.getCandidateTotalExperience() > 0)) {
                                            preScreenElement.isMatching = false;
                                        }
                                    }
                                    preScreenElement.isMinReq = false;

                                    populateResponse.elementList.add(preScreenElement);
                                }
                            } else if (preScreenRequirement.getProfileRequirement().getProfileRequirementTitle().equalsIgnoreCase("locality")) {

                                // if we don't have a job Req we assume candidate satisfies the req
                                if (jobPost.getJobPostToLocalityList() != null && jobPost.getJobPostToLocalityList().size() > 0) {

                                    preScreenElement = new PreScreenPopulateResponse.PreScreenElement();
                                    preScreenElement.setPropertyTitle(ServerConstants.PROPERTY_TYPE_MAP.get(PROPERTY_TYPE_LOCALITY));
                                    preScreenElement.setPropertyId(PROPERTY_TYPE_LOCALITY);

                                    preScreenElement.jobPostElementList = new ArrayList<>();
                                    preScreenElement.candidateElementList = new ArrayList<>();

                                    preScreenElement.isMatching = true;

                                    preScreenElement.propertyIdList.add(preScreenRequirement.getPreScreenRequirementId());
                                    List<Long> localityIdList = new ArrayList<>();
                                    Iterator<JobPostToLocality> iterator = jobPost.getJobPostToLocalityList().iterator();
                                    StringBuilder jobPostLocalityString = new StringBuilder("");
                                    while (iterator.hasNext()) {
                                        JobPostToLocality jobPostToLocality = iterator.next();
                                        localityIdList.add(jobPostToLocality.getLocality().getLocalityId());
                                        jobPostLocalityString.append(jobPostToLocality.getLocality().getLocalityName());
                                        if (iterator.hasNext()) {
                                            jobPostLocalityString.append(", ");
                                        }
                                        preScreenElement.jobPostElementList.add(new PreScreenPopulateResponse.PreScreenCustomObject((jobPostToLocality.getLocality()),
                                                (jobPostToLocality.getLocality().getLocalityName()), true));
                                    }

                                    // set final candidate placeholders here
                                    preScreenElement.jobPostPlaceHolder = jobPostLocalityString;

                                    List<Candidate> candidateList = filterByLatLngOrHomeLocality(new ArrayList<>(Arrays.asList(candidate)), localityIdList, ServerConstants.DEFAULT_MATCHING_ENGINE_RADIUS, false);
                                    if (candidateList.size() > 0) {
                                        preScreenElement.candidateElementList.add(new PreScreenPopulateResponse.PreScreenCustomObject((candidateList.get(0).getLocality()),
                                                (candidateList.get(0).getMatchedLocation()), true));

                                        if (candidateList.get(0).getLocality() == null && candidateList.get(0).getCandidateLocalityLat() == null) {
                                            preScreenElement.isMatching = false;
                                            preScreenElement.candidateElementList = new ArrayList<>();
                                        }
                                        if (candidateList.get(0).getMatchedLocation().isEmpty()) {
                                            preScreenElement.isMatching = false;
                                        }


                                        // set final candidate placeholders here
                                        preScreenElement.candidatePlaceHolder = (candidateList.get(0).getMatchedLocation());

                                    }
                                    preScreenElement.isMinReq = false;
                                    preScreenElement.isSingleEntity = false;

                                    populateResponse.elementList.add(preScreenElement);
                                }
                            } else if (preScreenRequirement.getProfileRequirement().getProfileRequirementTitle().equalsIgnoreCase("worktimings")) {
                                if (jobPost.getJobPostShift() != null) {
                                    preScreenElement = new PreScreenPopulateResponse.PreScreenElement();
                                    preScreenElement.setPropertyTitle(ServerConstants.PROPERTY_TYPE_MAP.get(PROPERTY_TYPE_WORK_SHIFT));
                                    preScreenElement.setPropertyId(PROPERTY_TYPE_WORK_SHIFT);

                                    preScreenElement.propertyIdList.add(preScreenRequirement.getPreScreenRequirementId());
                                    String timeShift = jobPost.getJobPostShift().getTimeShiftName();
                                    String time = "";
                                    if (jobPost.getJobPostStartTime() != null && jobPost.getJobPostEndTime() != null) {
                                        int startTime = jobPost.getJobPostStartTime();
                                        int endTime = jobPost.getJobPostEndTime();
                                        time = startTime + " AM ";
                                        if (startTime > 12) {
                                            startTime = startTime - 12;
                                            time = startTime + " PM ";
                                        }
                                        if (endTime > 12) {
                                            endTime = endTime - 12;
                                            time += "- " + endTime + " PM ";
                                        }
                                        timeShift += " ( " + time + ") ";
                                    }
                                    preScreenElement.jobPostElement = new PreScreenPopulateResponse.PreScreenCustomObject((jobPost.getJobPostShift()),
                                            (timeShift), true);

                                    // set final jobpost placeholders here
                                    preScreenElement.jobPostPlaceHolder = (timeShift);


                                    if (candidate.getTimeShiftPreference() != null) {
                                        preScreenElement.candidateElement = new PreScreenPopulateResponse.PreScreenCustomObject((candidate.getTimeShiftPreference().getTimeShift()),
                                                (candidate.getTimeShiftPreference().getTimeShift().getTimeShiftName()), true);

                                        // set final candidate placeholders here
                                        preScreenElement.candidatePlaceHolder = (candidate.getTimeShiftPreference().getTimeShift().getTimeShiftName());


                                        if (jobPost.getJobPostShift().getTimeShiftId() != candidate.getTimeShiftPreference().getTimeShift().getTimeShiftId()
                                                && !candidate.getTimeShiftPreference().getTimeShift().getTimeShiftName().trim().equalsIgnoreCase("any")) {
                                            preScreenElement.isMatching = false;
                                        }
                                    } else {
                                        preScreenElement.isMatching = false;
                                    }
                                    preScreenElement.isMinReq = false;

                                    populateResponse.elementList.add(preScreenElement);
                                }
                            }
                        }
                    }
                    break;
            }
        }
        if (populateResponse.elementList != null && populateResponse.elementList.size() > 0) {
            for (PreScreenPopulateResponse.PreScreenElement pe : populateResponse.elementList) {
                if (pe != null && !pe.isMatching()) {
                    if ((pe.isSingleEntity() && pe.getCandidateElement() == null)) {
                        isCandidateDataMissing = true;
                        break;
                    } else if (!pe.isSingleEntity()) {
                        isCandidateDataMissing = true;
                        break;
                    }
                }
            }
        } else {
            isCandidateDataMissing = false;
        }
        populateResponse.setStatus(PreScreenPopulateResponse.Status.SUCCESS);
        populateResponse.setVisible(isCandidateDataMissing);
        return populateResponse;
    }

    public static boolean updatePreScreenAttempt(Long jobPostId, Long candidateId, String callStatus, int channel) {

        // Interaction for PreScreen Call Attempt
        String interactionResult;
        boolean responseMsg = false;

        Candidate candidate = Candidate.find.where().eq("candidateId", candidateId).findUnique();

        // fetch existing workflow old
        JobPostWorkflow jobPostWorkflowOld = JobPostWorkflow.find.where()
                .eq("jobPost.jobPostId", jobPostId)
                .eq("candidate.candidateId", candidateId)
                .orderBy().desc("creationTimestamp").setMaxRows(1).findUnique();

        // A value is for overriding leadStatus is also there in Lead Model setLeadStatus
        if (candidate != null && jobPostWorkflowOld != null) {

            if (jobPostWorkflowOld.getStatus().getStatusId() == ServerConstants.JWF_STATUS_PRESCREEN_COMPLETED) {
                Logger.info("PreScreen Already Completed");
                responseMsg = true;
                return responseMsg;
            }
            // If call was connected just set the right interaction result
            if (callStatus.equals("CONNECTED")) {
                interactionResult = "Pre Screen Out Bound Call Successfully got connected";
            } else {
                // if call was not connected, set the interaction result and send an sms
                // to lead/candidate saying we tried reaching
                interactionResult = "Pre Screen Out Bound Call UnSuccessful : Callee is " + callStatus;

                if (callStatus.equals(ServerConstants.CALL_STATUS_BUSY)
                        || callStatus.equals(ServerConstants.CALL_STATUS_DND)
                        || callStatus.equals(ServerConstants.CALL_STATUS_NA)
                        || callStatus.equals(ServerConstants.CALL_STATUS_NR)
                        || callStatus.equals(ServerConstants.CALL_STATUS_SWITCHED_OFF)) {

                    SmsUtil.sendTryingToCallSms(candidate.getCandidateMobile());
                }
                responseMsg = true;
            }

            // Setting the existing jobpostworkflow status to attempted
            JobPostWorkflow jobPostWorkflowNew = saveNewJobPostWorkflow(candidate.getCandidateId(), jobPostId, jobPostWorkflowOld, channel, ServerConstants.JWF_STATUS_PRESCREEN_ATTEMPTED);

            JobPostWorkflowStatus status = JobPostWorkflowStatus.find.where().eq("statusId", ServerConstants.JWF_STATUS_PRESCREEN_ATTEMPTED).findUnique();
            jobPostWorkflowNew.setStatus(status);
            jobPostWorkflowNew.update();

            // save the interaction
            InteractionService.createWorkflowInteraction(
                    jobPostWorkflowOld.getJobPostWorkflowUUId(),
                    candidate.getCandidateUUId(),
                    InteractionConstants.INTERACTION_TYPE_CANDIDATE_PRE_SCREEN_ATTEMPTED,
                    null,
                    interactionResult,
                    channel
            );
            return responseMsg;
        }
        return false;
    }

    public static InterviewResponse savePreScreenResult(PreScreenRequest preScreenRequest, int channel, int statusId) {
        InterviewResponse interviewResponse = new InterviewResponse();

        // fetch existing workflow old
        JobPostWorkflow jobPostWorkflowOld = JobPostWorkflow.find.where()
                .eq("jobPost.jobPostId", preScreenRequest.getJobPostId())
                .eq("candidate.candidateId", preScreenRequest.getCandidateId())
                .orderBy().desc("creationTimestamp").setMaxRows(1).findUnique();

        // save PreScreen candidate
        JobPostWorkflow jobPostWorkflowNew = saveNewJobPostWorkflow(preScreenRequest.getCandidateId(), preScreenRequest.getJobPostId(), jobPostWorkflowOld, channel, statusId);

        if (jobPostWorkflowNew == null) {
            interviewResponse.setStatus(ServerConstants.ERROR);
            return interviewResponse;
        }

        // fetch the last attempted pre-screen result for this jobpost workflow
        Query<PreScreenResult> query = PreScreenResult.find.query();
        query = query.select("*").fetch("jobPostWorkflow")
                .where()
                .eq("jobPostWorkflow.jobPostWorkflowId", jobPostWorkflowOld.getJobPostWorkflowId())
                .query();

        PreScreenResult existingPreScreenResult = query.findUnique();

        // Create new result obj to be saved for this current attempt
        PreScreenResult preScreenResult = new PreScreenResult();

        // make sure we increment the attempt count if needed
        if (existingPreScreenResult == null) {
            preScreenResult.setAttemptCount(1);
        } else {
            preScreenResult.setAttemptCount(existingPreScreenResult.getAttemptCount() + 1);
        }

        preScreenResult.setUpdateTimestamp(new Timestamp(System.currentTimeMillis()));


        // fetch total no. of pre screen requirements for this jobpost. this is needed to score the attempt
        List<PreScreenRequirement> preScreenRequirementList = PreScreenRequirement.find.where()
                .eq("jobPost.jobPostId", preScreenRequest.getJobPostId())
                .findList();

        if (preScreenRequirementList == null || preScreenRequirementList.size() == 0) {
            Logger.error("PreScreen Requirement empty for jobPostId:" + preScreenRequest.getJobPostId());
            interviewResponse.setStatus(ServerConstants.ERROR);
            return interviewResponse;
        }

        if (preScreenRequest.getPreScreenNote() != null && !preScreenRequest.getPreScreenNote().trim().isEmpty()) {
            preScreenResult.setPreScreenResultNote(preScreenRequest.getPreScreenNote());
        }

        double score = ((double) preScreenRequest.getPreScreenIdList().size() / (double) preScreenRequirementList.size());

        preScreenResult.setResultScore(Util.RoundTo2Decimals(score));
        preScreenResult.setForceSet(preScreenRequest.isPass());

        preScreenResult.setJobPostWorkflow(jobPostWorkflowNew);
        preScreenResult.save();

        // support user can decide whether a candidate passed or failed pre-screen,
        // score doesn't play any role in deciding as of now.

        String interactionResult = "";
        Integer interactionType = null;
        if (preScreenRequest.isPass() != null) {
            JobPostWorkflowStatus status;
            if (preScreenRequest.isPass()) {
                // passed
                status = JobPostWorkflowStatus.find.where().eq("statusId", ServerConstants.JWF_STATUS_PRESCREEN_COMPLETED).findUnique();
                interactionResult = InteractionConstants.INTERACTION_RESULT_CANDIDATE_PRE_SCREEN_PASSED;
                interactionType = InteractionConstants.INTERACTION_TYPE_CANDIDATE_PRE_SCREEN_PASSED;
            } else {
                // failed
                status = JobPostWorkflowStatus.find.where().eq("statusId", ServerConstants.JWF_STATUS_PRESCREEN_FAILED).findUnique();
                interactionResult = InteractionConstants.INTERACTION_RESULT_CANDIDATE_PRE_SCREEN_FAILED;
                interactionType = InteractionConstants.INTERACTION_TYPE_CANDIDATE_PRE_SCREEN_FAILED;
            }
            jobPostWorkflowNew.setStatus(status);
            jobPostWorkflowNew.update();
        } else {
            // here code will come to judge candidate pre screen response solely based on score
        }

        // prep interaction
        interactionResult += jobPostWorkflowNew.getJobPost().getJobPostId() + ": " + jobPostWorkflowNew.getJobPost().getJobRole().getJobName();
        // chances are the scrapped data may not have proper company.
        if (jobPostWorkflowNew.getJobPost().getCompany() != null) {
            interactionResult += "@" + jobPostWorkflowNew.getJobPost().getCompany().getCompanyName();
        }
        // save interaction
        InteractionService.createWorkflowInteraction(
                jobPostWorkflowNew.getJobPostWorkflowUUId(),
                jobPostWorkflowNew.getCandidate().getCandidateUUId(),
                interactionType,
                preScreenResult.getPreScreenResultNote(),
                interactionResult,
                channel
        );

        // Now lets save all the individual responses for this current pre screen attempt
        for (PreScreenRequirement preScreenRequirement : preScreenRequirementList) {
            PreScreenResponse preScreenResponse = new PreScreenResponse();
            preScreenResponse.setPreScreenRequirement(preScreenRequirement);
            preScreenResponse.setPreScreenResult(preScreenResult);
            preScreenResponse.setUpdateTimestamp(new Timestamp(System.currentTimeMillis()));

            if (preScreenRequest.getPreScreenIdList().contains(preScreenRequirement.getPreScreenRequirementId())) {
                preScreenResponse.setResponse(true);
            } else {
                preScreenResponse.setResponse(false);
            }
            preScreenResponse.save();
        }
        if (preScreenRequest.isPass() != null && !(preScreenRequest.isPass())) {
            // candidate failed prescren, then don't show interview
            interviewResponse.setStatus(ServerConstants.ERROR);
            return interviewResponse;
        }
        return isInterviewRequired(jobPostWorkflowNew.getJobPost());
    }

    public static InterviewResponse isInterviewRequired(JobPost jobPost) {
        InterviewResponse interviewResponse = new InterviewResponse();
        if (jobPost == null) {
            interviewResponse.setStatus(ServerConstants.ERROR);
            return interviewResponse;
        }
        int validCount = 0;
        if (jobPost.getRecruiterProfile() == null) {
            // don't show interview modal if no recruiter is set for a jobpost
            interviewResponse.setStatus(ServerConstants.INTERVIEW_NOT_REQUIRED);
            return interviewResponse;
        }
        Long recruiterId = jobPost.getRecruiterProfile().getRecruiterProfileId();
        RecruiterCreditHistory recruiterCreditHistory = RecruiterCreditHistory.find.where()
                .eq("recruiterProfile.recruiterProfileId", recruiterId)
                .eq("recruiterCreditCategory.recruiterCreditCategoryId", ServerConstants.RECRUITER_CATEGORY_INTERVIEW_UNLOCK)
                .orderBy().desc("createTimestamp").setMaxRows(1).findUnique();
        if (recruiterCreditHistory != null &&
                recruiterCreditHistory.getRecruiterCreditCategory().getRecruiterCreditCategoryId()
                        == ServerConstants.RECRUITER_CATEGORY_INTERVIEW_UNLOCK
                && recruiterCreditHistory.getRecruiterCreditsAvailable() > 0) {
            // When recruiter credit available then show Interview UI
            validCount++;
        }

        if (jobPost.getInterviewDetailsList() != null && jobPost.getInterviewDetailsList().size() > 0) {
            // When slot available then  show Interview UI
            validCount++;
        }

        if (validCount == 2) {
            interviewResponse.setStatus(ServerConstants.INTERVIEW_REQUIRED);
            return interviewResponse;
        }

        interviewResponse.setStatus(ServerConstants.INTERVIEW_NOT_REQUIRED);
        return interviewResponse;
    }


    public static Map<Long, CandidateWorkflowData> getPendingInterviewScheduleCandidates(Long jobPostId) {
        String statusSql;
        Integer jpwfStatus;

        jpwfStatus = ServerConstants.JWF_STATUS_PRESCREEN_COMPLETED;
        statusSql = " and (status_id in (" + ServerConstants.JWF_STATUS_PRESCREEN_COMPLETED + ")) ";

        StringBuilder workFlowQueryBuilder = new StringBuilder("select createdby, candidate_id, creation_timestamp, job_post_id, status_id from job_post_workflow i " +
                " where i.job_post_id " +
                " = ('" + jobPostId + "') " +
                statusSql +
                " and creation_timestamp = " +
                " (select max(creation_timestamp) from job_post_workflow " +
                "       where i.candidate_id = job_post_workflow.candidate_id " +
                "       and i.job_post_id = job_post_workflow.job_post_id) " +
                " order by creation_timestamp desc ");

        RawSql rawSql = RawSqlBuilder.parse(workFlowQueryBuilder.toString())
                .columnMapping("creation_timestamp", "creationTimestamp")
                .columnMapping("job_post_id", "jobPost.jobPostId")
                .columnMapping("status_id", "status.statusId")
                .columnMapping("candidate_id", "candidate.candidateId")
                .columnMapping("createdby", "createdBy")
                .create();

        List<JobPostWorkflow> jobPostWorkflowList = Ebean.find(JobPostWorkflow.class)
                .setRawSql(rawSql)
                .findList();

        List<Candidate> candidateList = new ArrayList<>();

        Map<Long, CandidateWorkflowData> selectedCandidateMap = new LinkedHashMap<>();
        // until view is not available over this table, this loop get the distinct candidate who
        // got selected for a job post recently

        for (JobPostWorkflow jpwf : jobPostWorkflowList) {
            candidateList.add(jpwf.getCandidate());
        }
        // prep params for a jobPost
        JobPost jobPost = JobPost.find.where().eq("jobPostId", jobPostId).findUnique();

        List<JobPostToLocality> jobPostToLocalityList = jobPost.getJobPostToLocalityList();
        List<Long> localityIdList = new ArrayList<>();
        for (JobPostToLocality jobPostToLocality : jobPostToLocalityList) {
            localityIdList.add(jobPostToLocality.getLocality().getLocalityId());
        }

        candidateList = filterByLatLngOrHomeLocality(candidateList, localityIdList, ServerConstants.DEFAULT_MATCHING_ENGINE_RADIUS, false);
        Map<Long, CandidateExtraData> candidateExtraDataMap = computeExtraData(candidateList, JobPost.find.where().eq("jobPostId", jobPostId).findUnique(), new ArrayList<Integer>(Arrays.asList(jpwfStatus)));

        for (Candidate candidate : candidateList) {
            CandidateWorkflowData candidateWorkflowData = new CandidateWorkflowData();
            candidateWorkflowData.setCandidate(candidate);
            candidateWorkflowData.setExtraData(candidateExtraDataMap.get(candidate.getCandidateId()));
            selectedCandidateMap.put(candidate.getCandidateId(), candidateWorkflowData);
        }

        return selectedCandidateMap;
    }


    public static Map<Long, CandidateWorkflowData> getAllPendingInterviewAndRescheduleConfirmation(Long jobPostId, Long status) {
        String statusSql;
        Integer jpwfStatus;
        if (status == 1) { //all
            jpwfStatus = ServerConstants.JWF_STATUS_INTERVIEW_SCHEDULED;
            statusSql = " and (status_id in (" + ServerConstants.JWF_STATUS_INTERVIEW_SCHEDULED + ", " + ServerConstants.JWF_STATUS_INTERVIEW_RESCHEDULE + ", "
                    + ServerConstants.JWF_STATUS_INTERVIEW_REJECTED_BY_RECRUITER_SUPPORT + ", " + ServerConstants.JWF_STATUS_INTERVIEW_REJECTED_BY_CANDIDATE + ")) ";
        } else if (status == 2) {
            jpwfStatus = ServerConstants.JWF_STATUS_INTERVIEW_SCHEDULED;
            statusSql = " and (status_id in (" + ServerConstants.JWF_STATUS_PRESCREEN_COMPLETED + ", "
                    + ServerConstants.JWF_STATUS_INTERVIEW_SCHEDULED + ")) ";
        } else if (status == 3) {
            jpwfStatus = ServerConstants.JWF_STATUS_INTERVIEW_RESCHEDULE;
            statusSql = " and (status_id in (" + ServerConstants.JWF_STATUS_INTERVIEW_RESCHEDULE + ")) ";
        } else if (status == 4) {
            jpwfStatus = ServerConstants.JWF_STATUS_INTERVIEW_REJECTED_BY_RECRUITER_SUPPORT;
            statusSql = " and (status_id in (" + ServerConstants.JWF_STATUS_INTERVIEW_REJECTED_BY_RECRUITER_SUPPORT + ")) ";
        } else {
            jpwfStatus = ServerConstants.JWF_STATUS_INTERVIEW_REJECTED_BY_CANDIDATE;
            statusSql = " and (status_id in (" + ServerConstants.JWF_STATUS_INTERVIEW_REJECTED_BY_CANDIDATE + ")) ";
        }

        StringBuilder workFlowQueryBuilder = new StringBuilder("select createdby, candidate_id, creation_timestamp, job_post_id, status_id from job_post_workflow i " +
                " where i.job_post_id " +
                " = ('" + jobPostId + "') " +
                statusSql +
                " and creation_timestamp = " +
                " (select max(creation_timestamp) from job_post_workflow " +
                "       where i.candidate_id = job_post_workflow.candidate_id " +
                "       and i.job_post_id = job_post_workflow.job_post_id) " +
                " order by creation_timestamp desc ");

        RawSql rawSql = RawSqlBuilder.parse(workFlowQueryBuilder.toString())
                .columnMapping("creation_timestamp", "creationTimestamp")
                .columnMapping("job_post_id", "jobPost.jobPostId")
                .columnMapping("status_id", "status.statusId")
                .columnMapping("candidate_id", "candidate.candidateId")
                .columnMapping("createdby", "createdBy")
                .create();

        List<JobPostWorkflow> jobPostWorkflowList = Ebean.find(JobPostWorkflow.class)
                .setRawSql(rawSql)
                .findList();

//        List<JobPostWorkflow> jobPostWorkflowList = JobPostWorkflow.find.where()
//                .eq("jobPost.jobPostId", jobPostId)
//                .or(eq("status.statusId", ServerConstants.JWF_STATUS_SELECTED), eq("status.statusId", ServerConstants.JWF_STATUS_PRESCREEN_ATTEMPTED))
//                .setDistinct(true)
//                .orderBy().desc("creation_timestamp").findList();

        List<Candidate> candidateList = new ArrayList<>();

        Map<Long, CandidateWorkflowData> selectedCandidateMap = new LinkedHashMap<>();
        // until view is not available over this table, this loop get the distinct candidate who
        // got selected for a job post recently

        for (JobPostWorkflow jpwf : jobPostWorkflowList) {
            candidateList.add(jpwf.getCandidate());
        }
        // prep params for a jobPost
        JobPost jobPost = JobPost.find.where().eq("jobPostId", jobPostId).findUnique();

        List<JobPostToLocality> jobPostToLocalityList = jobPost.getJobPostToLocalityList();
        List<Long> localityIdList = new ArrayList<>();
        for (JobPostToLocality jobPostToLocality : jobPostToLocalityList) {
            localityIdList.add(jobPostToLocality.getLocality().getLocalityId());
        }

        candidateList = filterByLatLngOrHomeLocality(candidateList, localityIdList, ServerConstants.DEFAULT_MATCHING_ENGINE_RADIUS, false);
        Map<Long, CandidateExtraData> candidateExtraDataMap = computeExtraData(candidateList, JobPost.find.where().eq("jobPostId", jobPostId).findUnique(), new ArrayList<Integer>(Arrays.asList(jpwfStatus)));

        for (Candidate candidate : candidateList) {
            CandidateWorkflowData candidateWorkflowData = new CandidateWorkflowData();
            candidateWorkflowData.setCandidate(candidate);
            candidateWorkflowData.setExtraData(candidateExtraDataMap.get(candidate.getCandidateId()));
            selectedCandidateMap.put(candidate.getCandidateId(), candidateWorkflowData);
        }

        return selectedCandidateMap;
    }

    public static Map<Long, CandidateWorkflowData> getConfirmedInterviewCandidates(Long jobPostId, String start, String end) {
        Integer status = ServerConstants.JWF_STATUS_INTERVIEW_CONFIRMED;

        List<JobPostWorkflow> jobPostWorkflowList;

        if (Objects.equals(start, "null")) { //checking if there is any date filter or not
            jobPostWorkflowList = JobPostWorkflow.find.where()
                    .eq("jobPost.jobPostId", jobPostId)
                    .eq("status_id", status)
                    .findList();

        } else {
            jobPostWorkflowList = JobPostWorkflow.find.where()
                    .eq("jobPost.jobPostId", jobPostId)
                    .eq("status_id", status)
                    .ge("scheduled_interview_date", start)
                    .le("scheduled_interview_date", end)
                    .findList();
        }

        List<Candidate> candidateList = new ArrayList<>();

        Map<Long, CandidateWorkflowData> selectedCandidateMap = new LinkedHashMap<>();

        // until view is not available over this table, this loop get the distinct candidate who
        // got selected for a job post recently
        for (JobPostWorkflow jpwf : jobPostWorkflowList) {
            candidateList.add(jpwf.getCandidate());
        }
        // prep params for a jobPost
        JobPost jobPost = JobPost.find.where().eq("jobPostId", jobPostId).findUnique();

        List<JobPostToLocality> jobPostToLocalityList = jobPost.getJobPostToLocalityList();
        List<Long> localityIdList = new ArrayList<>();
        for (JobPostToLocality jobPostToLocality : jobPostToLocalityList) {
            localityIdList.add(jobPostToLocality.getLocality().getLocalityId());
        }

        candidateList = filterByLatLngOrHomeLocality(candidateList, localityIdList, ServerConstants.DEFAULT_MATCHING_ENGINE_RADIUS, false);
        Map<Long, CandidateExtraData> candidateExtraDataMap = computeExtraData(candidateList, JobPost.find.where().eq("jobPostId", jobPostId).findUnique(), new ArrayList<Integer>(Arrays.asList(status)));

        for (Candidate candidate : candidateList) {
            CandidateWorkflowData candidateWorkflowData = new CandidateWorkflowData();
            candidateWorkflowData.setCandidate(candidate);
            candidateWorkflowData.setExtraData(candidateExtraDataMap.get(candidate.getCandidateId()));
            selectedCandidateMap.put(candidate.getCandidateId(), candidateWorkflowData);
        }

        return selectedCandidateMap;
    }

    public static Map<Long, CandidateWorkflowData> getAllCompletedInterviews(Long jobPostId) {
        List<Integer> statusList = new ArrayList<>();
        statusList.add(ServerConstants.JWF_STATUS_CANDIDATE_FEEDBACK_STATUS_COMPLETE_SELECTED);
        statusList.add(ServerConstants.JWF_STATUS_CANDIDATE_FEEDBACK_STATUS_COMPLETE_REJECTED);
        statusList.add(ServerConstants.JWF_STATUS_CANDIDATE_FEEDBACK_STATUS_NOT_QUALIFIED);
        statusList.add(ServerConstants.JWF_STATUS_CANDIDATE_FEEDBACK_STATUS_NO_SHOW);

        List<JobPostWorkflow> jobPostWorkflowList;
        jobPostWorkflowList = JobPostWorkflow.find.where()
                .eq("jobPost.jobPostId", jobPostId)
                .in("status_id", statusList)
                .findList();

        List<Candidate> candidateList = new ArrayList<>();

        Map<Long, CandidateWorkflowData> selectedCandidateMap = new LinkedHashMap<>();

        for (JobPostWorkflow jpwf : jobPostWorkflowList) {
            candidateList.add(jpwf.getCandidate());
        }

        JobPost jobPost = JobPost.find.where().eq("jobPostId", jobPostId).findUnique();

        List<JobPostToLocality> jobPostToLocalityList = jobPost.getJobPostToLocalityList();
        List<Long> localityIdList = new ArrayList<>();
        for (JobPostToLocality jobPostToLocality : jobPostToLocalityList) {
            localityIdList.add(jobPostToLocality.getLocality().getLocalityId());
        }

        candidateList = filterByLatLngOrHomeLocality(candidateList, localityIdList, ServerConstants.DEFAULT_MATCHING_ENGINE_RADIUS, false);
        Map<Long, CandidateExtraData> candidateExtraDataMap = computeExtraData(candidateList, JobPost.find.where().eq("jobPostId", jobPostId)
                .findUnique(), statusList);

        for (Candidate candidate : candidateList) {
            CandidateWorkflowData candidateWorkflowData = new CandidateWorkflowData();
            candidateWorkflowData.setCandidate(candidate);
            candidateWorkflowData.setExtraData(candidateExtraDataMap.get(candidate.getCandidateId()));
            selectedCandidateMap.put(candidate.getCandidateId(), candidateWorkflowData);
        }

        return selectedCandidateMap;
    }

    public static List<InterviewTodayResponse> getTodaysInterviewDetails(InterviewTodayRequest interviewTodayRequest) {
        Integer status = ServerConstants.JWF_STATUS_INTERVIEW_CONFIRMED;
        List<InterviewTodayResponse> responseList = new ArrayList<>();

        Calendar now = Calendar.getInstance();
        int mMonth = (now.get(Calendar.MONTH) + 1);
        int mDay = now.get(Calendar.DAY_OF_MONTH);
        int mYear = now.get(Calendar.YEAR);

        String cDay = String.valueOf(mDay);
        String cMonth = String.valueOf(mMonth);

        if(mDay < 10){
            cDay = "0" + mDay;
        }

        if(mMonth < 10){
            cMonth = "0" + mMonth;
        }

        String todaysDate = mYear + "-" + cMonth + "-" + cDay;

        List<JobPostWorkflow> jobPostWorkflowList = JobPostWorkflow.find.where()
                .in("job_post_id", interviewTodayRequest.getJpId())
                .eq("status_id", status)
                .eq("scheduled_interview_date", todaysDate)
                .orderBy().asc("job_post_id")
                .findList();

        for (JobPostWorkflow jpWf : jobPostWorkflowList) {
            InterviewTodayResponse response = new InterviewTodayResponse();
            response.setCandidate(jpWf.getCandidate());
            response.setJobPostWorkflow(jpWf);
            JobPostWorkflow jobPostWorkFlow = JobPostWorkflow.find.where()
                    .eq("job_post_id", jpWf.getJobPost().getJobPostId())
                    .eq("candidate_id", jpWf.getCandidate().getCandidateId())
                    .orderBy().desc("creation_timestamp")
                    .setMaxRows(1)
                    .findUnique();
            response.setCurrentStatus(jobPostWorkFlow.getStatus());

            responseList.add(response);
        }
        return responseList;
    }

    public static Integer processDataPendingApproval(InterviewTodayRequest interviewTodayRequest) {
        if (interviewTodayRequest.getJpId().size() == 0) {
            return 0;
        }

        Integer count = 0;

        List<JobPostWorkflow> jobPostWorkflowList = JobPostWorkflow.find.where()
                .in("job_post_id", interviewTodayRequest.getJpId())
                .eq("status_id", ServerConstants.JWF_STATUS_INTERVIEW_SCHEDULED)
                .findList();

        for (JobPostWorkflow jpWf : jobPostWorkflowList) {
            JobPostWorkflow jobPostWorkFlow = JobPostWorkflow.find.where()
                    .eq("job_post_id", jpWf.getJobPost().getJobPostId())
                    .eq("candidate_id", jpWf.getCandidate().getCandidateId())
                    .orderBy().desc("creation_timestamp")
                    .setMaxRows(1)
                    .findUnique();
            if (jobPostWorkFlow.getStatus().getStatusId() == ServerConstants.JWF_STATUS_INTERVIEW_SCHEDULED) {
                count++;
            }
        }

        return count;
    }

    public static void savePreScreenResultForCandidateUpdate(long candidateId, long jobPostId, int channel) {
        // make entry into prescreen result/response table

        PreScreenRequest preScreenRequest = new PreScreenRequest();
        preScreenRequest.setCandidateId(candidateId);
        preScreenRequest.setJobPostId(jobPostId);
        preScreenRequest.setPreScreenNote("Candidate Self PreScreen");
        preScreenRequest.setPass(true);
        preScreenRequest.setPreScreenIdList(new ArrayList<>());
        JobPostWorkflowEngine.savePreScreenResult(preScreenRequest, channel, ServerConstants.JWF_STATUS_PRESCREEN_COMPLETED);
    }

    public static class LastActiveValue {
        public Integer lastActiveValueId;
        public String lastActiveValueName;
    }

    public static LastActiveValue getDateCluster(Long timeInMill) {

        Map<Integer, String> clusterLabel = new HashMap<>();
        clusterLabel.put(1, ServerConstants.ACTIVE_WITHIN_24_HOURS);
        clusterLabel.put(2, ServerConstants.ACTIVE_LAST_3_DAYS);
        clusterLabel.put(3, ServerConstants.ACTIVE_LAST_7_DAYS);
        clusterLabel.put(4, ServerConstants.ACTIVE_LAST_14_DAYS);
        clusterLabel.put(5, ServerConstants.ACTIVE_LAST_1_MONTH);
        clusterLabel.put(6, ServerConstants.ACTIVE_LAST_2_MONTHS);
        clusterLabel.put(7, ServerConstants.ACTIVE_BEYOND_2_MONTHS);

        Calendar cal = Calendar.getInstance();
        Calendar currentCal = Calendar.getInstance();
        cal.setTimeInMillis(timeInMill);

        LastActiveValue lastActiveValue = new LastActiveValue();
        int currentDay = currentCal.get(Calendar.DAY_OF_YEAR);
        int doyDiff = currentDay - cal.get(Calendar.DAY_OF_YEAR);

        if (doyDiff > 60) {
            lastActiveValue.lastActiveValueId = 7;
            lastActiveValue.lastActiveValueName = clusterLabel.get(7);
        } else if (doyDiff > 30) {
            lastActiveValue.lastActiveValueId = 6;
            lastActiveValue.lastActiveValueName = clusterLabel.get(6);
        } else if (doyDiff > 15) {
            lastActiveValue.lastActiveValueId = 5;
            lastActiveValue.lastActiveValueName = clusterLabel.get(5);
        } else if (doyDiff > 7) {
            lastActiveValue.lastActiveValueId = 4;
            lastActiveValue.lastActiveValueName = clusterLabel.get(4);
        } else if (doyDiff > 3) {
            lastActiveValue.lastActiveValueId = 3;
            lastActiveValue.lastActiveValueName = clusterLabel.get(3);
        } else if (doyDiff > 1) {
            lastActiveValue.lastActiveValueId = 2;
            lastActiveValue.lastActiveValueName = clusterLabel.get(2);
        } else {
            lastActiveValue.lastActiveValueId = 1;
            lastActiveValue.lastActiveValueName = clusterLabel.get(1);
        }
        return lastActiveValue;
    }

    private static List<Candidate> filterByLatLngOrHomeLocality(List<Candidate> candidateList, List<Long> jobPostLocalityIdList, Double distanceRadius, boolean shouldRemoveCandidate) {
        List<Candidate> filteredCandidateList = new ArrayList<>();

        if (jobPostLocalityIdList == null || jobPostLocalityIdList.isEmpty()) {
            return candidateList;
        }

        List<Locality> jobPostLocalityList = Locality.find.where().in("localityId", jobPostLocalityIdList).findList();

        if (jobPostLocalityIdList.size() > 0) {
            filteredCandidateList.addAll(candidateList);
            for (Candidate candidate : candidateList) {
                int localityIncludeCount = 0;

                // candidate home locality matches with the job post locality
                Double candidateLat;
                Double candidateLng;
                StringBuilder matchedLocation = new StringBuilder();

                if ((candidate.getCandidateLocalityLat() != null && candidate.getCandidateLocalityLng() != null)) {
                    candidateLat = candidate.getCandidateLocalityLat();
                    candidateLng = candidate.getCandidateLocalityLng();
                } else if (candidate.getLocality() != null
                        && (candidate.getLocality().getLat() != null
                        || candidate.getLocality().getLng() != null)) {
                    candidateLat = candidate.getLocality().getLat();
                    candidateLng = candidate.getLocality().getLng();
                } else {
                    if (shouldRemoveCandidate) filteredCandidateList.remove(candidate);
                    continue;
                }

                for (Locality locality : jobPostLocalityList) {
                    Double distance = MatchingEngineService.getDistanceFromCenter(
                            locality.getLat(),
                            locality.getLng(),
                            candidateLat,
                            candidateLng
                    );

                    if(distance == null){
                        //Lat/Lng is null for this locality
                        continue;
                    }
                    Double searchRadius = ServerConstants.DEFAULT_MATCHING_ENGINE_RADIUS;
                    if (distanceRadius != null) {
                        searchRadius = distanceRadius;
                    }
                    if (distance > searchRadius && candidate.getMatchedLocation() == null) {
                        localityIncludeCount++;
                    } else {
                        matchedLocation.append(locality.getLocalityName() + " (" + Util.RoundTo1Decimals(distance) + " KM) ");
                    }
                }

                if (localityIncludeCount == jobPostLocalityList.size()) {
                    // candidate is not within req distance from any jobPost latlng
                    if (shouldRemoveCandidate) filteredCandidateList.remove(candidate);
                    localityIncludeCount = 0;
                }
                candidate.setMatchedLocation(matchedLocation.toString());
            }
        }
        return filteredCandidateList;
    }

    private static Map<Long, CandidateExtraData> computeExtraData(List<Candidate> candidateList, JobPost jobPost, List<Integer> statusList) {
        if (statusList == null || statusList.size() == 0) {
            statusList = new ArrayList<>();
            statusList.add(ServerConstants.JWF_STATUS_SELECTED);
        }

        SimpleDateFormat sfd = new SimpleDateFormat(ServerConstants.SDF_FORMAT_HH);
        SimpleDateFormat sfd_date = new SimpleDateFormat(ServerConstants.SDF_FORMAT_DDMMYYYY);

        if (candidateList.size() == 0) return null;
        // candidateId --> featureMap
        Map<Long, CandidateExtraData> candidateExtraDataMap = new LinkedHashMap<>();

        List<String> candidateUUIdList = new ArrayList<>();
        List<Long> candidateIdList = new ArrayList<>();

        for (Candidate candidate : candidateList) {
            candidateUUIdList.add(candidate.getCandidateUUId());
            candidateIdList.add(candidate.getCandidateId());
        }
        /* */
        List<JobApplication> allJobApplication = JobApplication.find.where().in("candidateId", candidateIdList).eq("jobPostId", jobPost.getJobPostId()).findList();

        List<CandidateAssessmentAttempt> allAssessmentAttempt
                = CandidateAssessmentAttempt.find
                .where()
                .in("candidateId", candidateIdList)
                .eq("jobRoleId", jobPost.getJobRole().getJobRoleId())
                .findList();

        // prep candidate->jobApplication mapping to reduce lookup time to O(1)
        Map<Long, String> jobApplicationMap = new HashMap<>();
        Map<Long, String> jobApplicationModeMap = new HashMap<>();
        for (JobApplication jobApplication : allJobApplication) {
            String jobApplicationMode;
            jobApplicationMap.put(jobApplication.getCandidate().getCandidateId(), sfd.format(jobApplication.getJobApplicationCreateTimeStamp()));
            if (jobApplication.getPartner() == null) {
                jobApplicationMode = "Self";
            } else {
                jobApplicationMode = String.valueOf(jobApplication.getPartner().getPartnerId());
            }
            jobApplicationModeMap.put(jobApplication.getCandidate().getCandidateId(), jobApplicationMode);
        }

        // prep candidate->jobApplication mapping to reduce lookup time to O(1)
        Map<Long, Integer> assessmentMap = new HashMap<>();
        for (CandidateAssessmentAttempt attempt : allAssessmentAttempt) {
            assessmentMap.put(attempt.getCandidate().getCandidateId(), attempt.getAttemptId());
        }

        String candidateListString = String.join("', '", candidateUUIdList);

//
//        StringBuilder interactionQueryBuilder = new StringBuilder("select distinct objectauuid, creationtimestamp from interaction i " +
//                " where i.objectauuid " +
//                " in ('" + candidateListString + "') " +
//                " and creationtimestamp = " +
//                " (select max(creationtimestamp) from interaction where i.objectauuid = interaction.objectauuid) " +
//                " order by creationTimestamp desc ");
//
//        RawSql rawSql = RawSqlBuilder.parse(interactionQueryBuilder.toString())
//                .tableAliasMapping("i", "interaction")
//                .columnMapping("objectauuid", "objectAUUId")
//                .columnMapping("creationtimestamp", "creationTimestamp")
//                .create();


        Map<String, Interaction> lastActiveInteraction = Ebean.find(Interaction.class)
                .setRawSql(getRawSqlForInteraction(candidateListString))
                .findMap("objectAUUId", String.class);

        List<JobPostWorkflow> jobPostWorkflowList = JobPostWorkflow.find.where()
                .in("status.statusId", statusList)
                .isNotNull("candidate")
                .eq("job_post_id", jobPost.getJobPostId())
                .in("candidate.candidateId", candidateIdList)
                .findList();

        Map<Long, JobPostWorkflow> candidateToJobPostWorkflowMap = new HashMap<>();
        for (JobPostWorkflow jobPostWorkflow : jobPostWorkflowList) {
            candidateToJobPostWorkflowMap.put(jobPostWorkflow.getCandidate().getCandidateId(), jobPostWorkflow);
        }

        List<Interaction> allPreScreenCallAttemptInteractions = Interaction.find
                .where()
                .ge("interactionType", InteractionConstants.INTERACTION_TYPE_CANDIDATE_PRE_SCREEN_ATTEMPTED)
                .le("interactionType", InteractionConstants.INTERACTION_TYPE_CANDIDATE_PRE_SCREEN_PASSED)
                .in("objectBUUId", candidateUUIdList)
                .orderBy()
                .desc("objectBUUId")
                .findList();
        Map<String, Integer> candidateWithPreScreenAttemptCountMap = new TreeMap<>();

        for (Interaction interaction : allPreScreenCallAttemptInteractions) {
            Integer count = candidateWithPreScreenAttemptCountMap.get(interaction.getObjectBUUId());
            if (count == null) {
                count = 0;
            }
            count++;
            candidateWithPreScreenAttemptCountMap.put(interaction.getObjectBUUId(), count);
        }

        for (Candidate candidate : candidateList) {

            CandidateExtraData candidateExtraData = candidateExtraDataMap.get(candidate.getCandidateId());

            if (candidateExtraData == null) {

                candidateExtraData = new CandidateExtraData();

                // compute applied on
                candidateExtraData.setAppliedOn(jobApplicationMap.get(candidate.getCandidateId()));

                // compute last active on
                Interaction interactionsOfCandidate = lastActiveInteraction.get(candidate.getCandidateUUId());
                if (interactionsOfCandidate != null) {
                    candidateExtraData.setLastActive(getDateCluster(interactionsOfCandidate.getCreationTimestamp().getTime()));
                }

                // compute 'has attempted assessment' for this JobPost-JobRole, If yes then this contains assessmentId
                candidateExtraData.setAssessmentAttemptId(assessmentMap.get(candidate.getCandidateId()));

                if (candidateWithPreScreenAttemptCountMap.size() > 0) {
                    candidateExtraData.setPreScreenCallAttemptCount(candidateWithPreScreenAttemptCountMap.get(candidate.getCandidateUUId()));
                }
                // other intelligent scoring will come here

                // Job Application Mode (Self/Partner).
                if (jobApplicationModeMap.size() > 0) {
                    candidateExtraData.setJobApplicationMode(jobApplicationModeMap.get(candidate.getCandidateId()));
                }

                // 'Pre-screen selection timestamp' along with jobPostWorkflowId, uuid
                if (candidateToJobPostWorkflowMap.size() > 0) {
                    JobPostWorkflow jobPostWorkflow = candidateToJobPostWorkflowMap.get(candidate.getCandidateId());

                    if (jobPostWorkflow != null) {
                        candidateExtraData.setPreScreenSelectionTimeStamp(jobPostWorkflow.getCreationTimestamp());
                        candidateExtraData.setWorkflowId(jobPostWorkflow.getJobPostWorkflowId());
                        candidateExtraData.setWorkflowUUId(jobPostWorkflow.getJobPostWorkflowUUId());
                        candidateExtraData.setCreatedBy(jobPostWorkflow.getCreatedBy());

                        if (jobPostWorkflow.getStatus().getStatusId() >= ServerConstants.JWF_STATUS_PRESCREEN_COMPLETED) {
                            List<Interaction> interactionList = Interaction.find
                                    .where().eq("objectAUUId", jobPostWorkflow.getJobPostWorkflowUUId())
                                    .findList();

                            candidateExtraData.setAllInteractionCount(interactionList.size());

                            String interviewDatetime = "";
                            if (jobPostWorkflow.getScheduledInterviewTimeSlot() != null) {
                                interviewDatetime = jobPostWorkflow.getScheduledInterviewTimeSlot().getInterviewTimeSlotName() + " ";
                            }
                            if (jobPostWorkflow.getScheduledInterviewDate() != null) {
                                interviewDatetime += sfd_date.format(jobPostWorkflow.getScheduledInterviewDate());
                            }
                            if (interviewDatetime.isEmpty()) {
                                interviewDatetime = "NA";
                            }
                            candidateExtraData.setInterviewSchedule(interviewDatetime);
                            candidateExtraData.setInterviewLat(jobPostWorkflow.getInterviewLocationLat());
                            candidateExtraData.setInterviewLng(jobPostWorkflow.getInterviewLocationLng());

                            JobPostWorkflow jobPostWorkflowLatest = JobPostWorkflow.find.where()
                                    .eq("candidate_id", candidate.getCandidateId())
                                    .eq("Job_post_id", jobPostWorkflow.getJobPost().getJobPostId())
                                    .orderBy().desc("creation_timestamp").setMaxRows(1).findUnique();

                            candidateExtraData.setWorkflowStatus(jobPostWorkflowLatest.getStatus());
                            candidateExtraData.setInterviewDate(jobPostWorkflowLatest.getScheduledInterviewDate());
                            candidateExtraData.setInterviewSlot(jobPostWorkflowLatest.getScheduledInterviewTimeSlot());


                            CandidateInterviewStatusUpdate candidateInterviewStatusUpdate = CandidateInterviewStatusUpdate.find.where()
                                    .eq("candidateId", candidate.getCandidateId())
                                    .eq("JobPostId", jobPostWorkflow.getJobPost().getJobPostId())
                                    .orderBy().desc("create_timestamp").setMaxRows(1).findUnique();


                            candidateExtraData.setCandidateInterviewStatus(null);
                            if (candidateInterviewStatusUpdate != null) {
                                candidateExtraData.setCandidateInterviewStatus(candidateInterviewStatusUpdate.getJobPostWorkflow().getStatus());
                                candidateExtraData.setInterviewDate(candidateInterviewStatusUpdate.getJobPostWorkflow().getScheduledInterviewDate());
                                candidateExtraData.setInterviewSlot(candidateInterviewStatusUpdate.getJobPostWorkflow().getScheduledInterviewTimeSlot());
                            }
                        }
                    }
                }
            }

            candidateExtraDataMap.put(candidate.getCandidateId(), candidateExtraData);
        }

        return candidateExtraDataMap;
    }

    private static class ExperienceValue {
        int minExperienceValue;
        int maxExperienceValue;
    }

    private static ExperienceValue getDurationFromExperience(Integer experienceId) {

        // experience table should have a minValue column containing the minValue
        // which should be considered while computation
        ExperienceValue experienceValue = new ExperienceValue();
        experienceValue.minExperienceValue = 0; // in months
        experienceValue.maxExperienceValue = 0; // in months


        if (experienceId == null) {
            return null;
        } else if (experienceId == 1) {
            // fresher
            experienceValue.minExperienceValue = 0;
            experienceValue.maxExperienceValue = 0; // in months
        } else if (experienceId == 2) {
            experienceValue.minExperienceValue = 6;
            experienceValue.maxExperienceValue = 24; // in months
        } else if (experienceId == 3) {
            experienceValue.minExperienceValue = 24;
            experienceValue.maxExperienceValue = 48; // in months
        } else if (experienceId == 4) {
            experienceValue.minExperienceValue = 48;
            experienceValue.maxExperienceValue = 72; // in months
        } else {
            // any
            return null;
        }

        return experienceValue;
    }

    // this methods take the old jobpost uuid and set the new jobpost uuid to old jobpost uuid.
    private static JobPostWorkflow saveNewJobPostWorkflow(Long candidateId, Long jobPostId, JobPostWorkflow jobPostWorkflowOld, int channel, int statusId) {
        if (statusId > ServerConstants.JWF_STATUS_PRESCREEN_COMPLETED) {
            Logger.warn("saveNewJobPostWorkflow called with unacceptable statusId:");
            return null;
        }
        JobPostWorkflowStatus status = JobPostWorkflowStatus.find.where().eq("statusId", statusId).findUnique();

        JobPost jobPost = JobPost.find.where().eq("jobPostId", jobPostId).findUnique();
        Candidate candidate = Candidate.find.where().in("candidateId", candidateId).findUnique();
        String toBePreservedUUId = jobPostWorkflowOld.getJobPostWorkflowUUId();

        // check if status is already selected or pre_screen_attempted, throw error if not
        if (jobPostWorkflowOld.getStatus().getStatusId() == ServerConstants.JWF_STATUS_PRESCREEN_ATTEMPTED
                || jobPostWorkflowOld.getStatus().getStatusId() == ServerConstants.JWF_STATUS_SELECTED
                || jobPostWorkflowOld.getStatus().getStatusId() == ServerConstants.JWF_STATUS_PRESCREEN_COMPLETED) {
            // save new workflow with status pre_screen_attempted, later this obj status will change to pre_screen_completed
            jobPostWorkflowOld = new JobPostWorkflow();
            jobPostWorkflowOld.setJobPostWorkflowUUId(toBePreservedUUId);
            jobPostWorkflowOld.setJobPost(jobPost);
            jobPostWorkflowOld.setCandidate(candidate);
            jobPostWorkflowOld.setCreatedBy(session().get("sessionUsername") == null ? InteractionConstants.INTERACTION_CHANNEL_MAP.get(channel) : session().get("sessionUsername"));
            jobPostWorkflowOld.setChannel(channel);
            jobPostWorkflowOld.setStatus(status);
            jobPostWorkflowOld.save();
            return jobPostWorkflowOld;
        } else {
            Logger.error("Error ! JobPostWorkflow status is not PRESCREEN_ATTEMPTED or SELECTED");
        }
        return null;
    }

    // this methods take the old jobpost uuid and set the new jobpost uuid to old jobpost uuid.
    public static JobPostWorkflow saveNewJobPostWorkflow(Long candidateId, Long jobPostId, JobPostWorkflow jobPostWorkflowOld,
                                                         Integer oldStatus, Integer newStatus, Integer interviewSlot, Date interviewDate, int channel) {

        JobPostWorkflowStatus status = JobPostWorkflowStatus.find.where().eq("statusId", newStatus).findUnique();
        JobPost jobPost = JobPost.find.where().eq("jobPostId", jobPostId).findUnique();
        Candidate candidate = Candidate.find.where().in("candidateId", candidateId).findUnique();
        String toBePreservedUUId = jobPostWorkflowOld.getJobPostWorkflowUUId();

        // interview validation
        if (interviewDate == null || interviewSlot == null) {
            Logger.info("Null interview Date/Slot received");
            return null;
        }

        // check if status is already selected or pre_screen_attempted, throw error if not
        if (jobPostWorkflowOld.getStatus().getStatusId() == oldStatus) {

            jobPostWorkflowOld = new JobPostWorkflow();
            jobPostWorkflowOld.setJobPostWorkflowUUId(toBePreservedUUId);
            jobPostWorkflowOld.setJobPost(jobPost);
            jobPostWorkflowOld.setInterviewLocationLat(null);
            jobPostWorkflowOld.setInterviewLocationLng(null);
            if (jobPost.getInterviewDetailsList() != null) {
                jobPostWorkflowOld.setInterviewLocationLat(jobPost.getInterviewDetailsList().get(0).getLat());
                jobPostWorkflowOld.setInterviewLocationLng(jobPost.getInterviewDetailsList().get(0).getLng());
            }
            jobPostWorkflowOld.setCandidate(candidate);

            jobPostWorkflowOld.setCreatedBy(session().get("sessionUsername") == null ? InteractionConstants.INTERACTION_CHANNEL_MAP.get(channel) : session().get("sessionUsername"));

            jobPostWorkflowOld.setChannel(channel);

            jobPostWorkflowOld.setStatus(status);

            if (interviewSlot != null) {
                InterviewTimeSlot interviewTimeSlot = InterviewTimeSlot.find.where().eq("interview_time_slot_id", interviewSlot).findUnique();
                if (interviewTimeSlot != null) {
                    jobPostWorkflowOld.setScheduledInterviewTimeSlot(interviewTimeSlot);
                }
            }
            if (interviewDate != null) {
                jobPostWorkflowOld.setScheduledInterviewDate(interviewDate);
            }
            Logger.info("interview slot saved");
            jobPostWorkflowOld.save();
            return jobPostWorkflowOld;
        } else {
            Logger.error("Error ! JobPostWorkflow status is not as prevStatus");
        }
        return null;
    }

    private static Map<Long, CandidateExtraData> computeExtraDataForRecruiterSearchResult(List<Candidate> candidateList) {

        if (candidateList.size() == 0) return null;
        // candidateId --> featureMap
        Map<Long, CandidateExtraData> candidateExtraDataMap = new LinkedHashMap<>();

        List<String> candidateUUIdList = new ArrayList<>();
        List<Long> candidateIdList = new ArrayList<>();

        for (Candidate candidate : candidateList) {
            candidateUUIdList.add(candidate.getCandidateUUId());
            candidateIdList.add(candidate.getCandidateId());
        }

        String candidateListString = String.join("', '", candidateUUIdList);

        Map<String, Interaction> lastActiveInteraction = Ebean.find(Interaction.class)
                .setRawSql(getRawSqlForInteraction(candidateListString))
                .findMap("objectAUUId", String.class);

        for (Candidate candidate : candidateList) {
            CandidateExtraData candidateExtraData = candidateExtraDataMap.get(candidate.getCandidateId());

            if (candidateExtraData == null) {
                candidateExtraData = new CandidateExtraData();

                // compute last active on
                Interaction interactionsOfCandidate = lastActiveInteraction.get(candidate.getCandidateUUId());
                if (interactionsOfCandidate != null) {
                    candidateExtraData.setLastActive(getDateCluster(interactionsOfCandidate.getCreationTimestamp().getTime()));
                }
                // other intelligent scoring will come here
            }

            candidateExtraDataMap.put(candidate.getCandidateId(), candidateExtraData);
        }

        return candidateExtraDataMap;
    }

    private static RawSql getRawSqlForInteraction(String candidateListString) {
        String interactionChannelString =
                InteractionConstants.INTERACTION_CHANNEL_CANDIDATE_ANDROID
                        + ", " + InteractionConstants.INTERACTION_CHANNEL_CANDIDATE_WEBSITE
                        + ", " + InteractionConstants.INTERACTION_CHANNEL_PARTNER_WEBSITE
                        + ", " + InteractionConstants.INTERACTION_CHANNEL_SUPPORT_WEBSITE;

        StringBuilder interactionQueryBuilder = new StringBuilder("select distinct objectauuid, creationtimestamp, interactionchannel from interaction i " +
                " where " +
                " (" +
                " i.objectauuid " +
                " in ('" + candidateListString + "') " +
                " or " +
                " i.objectbuuid  " +
                " in ('" + candidateListString + "') " +
                " )" +
                " and i.interactionchannel " +
                " in ('" + interactionChannelString + "') " +
                " and creationtimestamp = " +
                " (select max(creationtimestamp) from interaction where i.objectauuid = interaction.objectauuid) " +
                " order by creationTimestamp desc ");

        RawSql rawSql = RawSqlBuilder.parse(interactionQueryBuilder.toString())
                .tableAliasMapping("i", "interaction")
                .columnMapping("objectauuid", "objectAUUId")
                .columnMapping("creationtimestamp", "creationTimestamp")
                .columnMapping("interactionchannel", "interactionChannel")
                .create();

        return rawSql;
    }

    public static Result updateInterviewStatus(InterviewStatusRequest interviewStatusRequest, int channel) {
        Candidate candidate = Candidate.find.where().eq("candidateId", interviewStatusRequest.getCandidateId()).findUnique();
        if (candidate != null) {
            int jwStatus = ServerConstants.INTERVIEW_STATUS_ACCEPTED;
            int jwType = InteractionConstants.INTERACTION_TYPE_RECRUITER_ACCEPT_JOB_APPLICATION_INTERVIEW;
            String interactionResult = "";

            JobPost jobPost = JobPost.find.where().eq("jobPostId", interviewStatusRequest.getJobPostId()).findUnique();

            Date date = null;
            if (interviewStatusRequest.getRescheduledDate() != null) { //rescheduling in job application table
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

                try {
                    date = format.parse(interviewStatusRequest.getRescheduledDate());
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }

            JobApplication jobApplication = JobApplication.find.where()
                    .eq("candidateId", candidate.getCandidateId())
                    .eq("jobPostId", interviewStatusRequest.getJobPostId())
                    .findUnique();

            // fetch existing workflow old
            JobPostWorkflow jobPostWorkflowOld = JobPostWorkflow.find.where()
                    .eq("jobPost.jobPostId", interviewStatusRequest.getJobPostId())
                    .eq("candidate.candidateId", candidate.getCandidateId())
                    .orderBy().desc("creationTimestamp").setMaxRows(1).findUnique();

            if (interviewStatusRequest.getInterviewStatus() == ServerConstants.INTERVIEW_STATUS_ACCEPTED) { // accept
                Logger.info("Sending interview confirm sms to candidate");
                jwStatus = ServerConstants.JWF_STATUS_INTERVIEW_CONFIRMED;

                if (interviewStatusRequest.getInterviewSchedule() == null) { //candidate shortlisted because slots and interview days is not available
                    sendInterviewShortlistSms(jobPost, candidate);

                    //sending notification
                    NotificationUtil.sendInterviewShortlistNotification(candidate, jobPostWorkflowOld);

                    // creating interaction
                    createInteractionForRecruiterShortlistJobApplicationWithoutDate(candidate.getCandidateUUId(), jobPost.getRecruiterProfile().getRecruiterProfileUUId());
                    jwType = InteractionConstants.INTERACTION_TYPE_RECRUITER_SHORTLIST_JOB_APPLICATION_INTERVIEW;

                    interactionResult = " interview shortlisted without interview date and slot";
                } else {
                    sendInterviewConfirmationSms(jobPostWorkflowOld, candidate);

                    // creating interaction
                    createInteractionForRecruiterAcceptingInterviewDate(candidate.getCandidateUUId(), jobPost.getRecruiterProfile().getRecruiterProfileUUId());

                    //sending notification
                    NotificationUtil.sendInterviewConfirmationNotification(candidate, jobPostWorkflowOld);

                    if (jobApplication != null && jobApplication.getPartner() != null) {
                        sendInterviewConfirmationSmsToPartner(jobPostWorkflowOld, candidate, jobApplication.getPartner());
                    }

                    jwType = InteractionConstants.INTERACTION_TYPE_RECRUITER_ACCEPT_JOB_APPLICATION_INTERVIEW;
                    interactionResult = " interview confirmed";
                }

            } else if (interviewStatusRequest.getInterviewStatus() == ServerConstants.INTERVIEW_STATUS_REJECTED_BY_RECRUITER) { // reject
                Logger.info("Sending interview rejection sms to candidate");
                sendInterviewRejectionSms(jobPostWorkflowOld, candidate);

                // creating interaction
                createInteractionForRecruiterRejectingInterviewDate(candidate.getCandidateUUId(), jobPostWorkflowOld.getJobPost().getRecruiterProfile().getRecruiterProfileUUId());

                //sending notification
                NotificationUtil.sendInterviewNotShortlistNotification(candidate, jobPostWorkflowOld);

                if (jobApplication != null && jobApplication.getPartner() != null) {
                    sendInterviewRejectionSmsToPartner(jobPostWorkflowOld, candidate, jobApplication.getPartner());
                }

                jwStatus = ServerConstants.JWF_STATUS_INTERVIEW_REJECTED_BY_RECRUITER_SUPPORT;
                jwType = InteractionConstants.INTERACTION_TYPE_RECRUITER_REJECT_JOB_APPLICATION_INTERVIEW;
                interactionResult = " interview rejected";

            } else if (interviewStatusRequest.getInterviewStatus() == ServerConstants.INTERVIEW_STATUS_RESCHEDULED) { // reschedule
                jwStatus = ServerConstants.JWF_STATUS_INTERVIEW_RESCHEDULE;
                jwType = InteractionConstants.INTERACTION_TYPE_RECRUITER_RESCHEDULE_JOB_APPLICATION_INTERVIEW;
                interactionResult = " interview rescheduled";

                InterviewTimeSlot interviewTimeSlot = InterviewTimeSlot.find.where().eq("interview_time_slot_id", interviewStatusRequest.getRescheduledSlot()).findUnique();

                //sending notification
                NotificationUtil.sendInterviewRescheduledNotification(jobPostWorkflowOld, candidate, date, interviewTimeSlot);

                sendInterviewReschedulingSms(jobPostWorkflowOld, candidate, date, interviewTimeSlot);

                if (jobApplication != null && jobApplication.getPartner() != null) {
                    sendInterviewReschedulingSmsToPartner(jobPostWorkflowOld, candidate, date, interviewTimeSlot, jobApplication.getPartner());
                }
            }

            // Setting the existing jobpostworkflow status to confirmed
            JobPostWorkflow jobPostWorkflowNew = saveNewJobPostWorkflow(candidate.getCandidateId(),
                    Long.valueOf(interviewStatusRequest.getJobPostId()),
                    jobPostWorkflowOld, jobPostWorkflowOld.getStatus().getStatusId(),
                    jwStatus, interviewStatusRequest.getRescheduledSlot(), date, channel);

            if (jobPostWorkflowNew != null) {
                jobPostWorkflowNew.setStatus(JobPostWorkflowStatus.find.where().eq("statusId", jwStatus).findUnique());
            }
            jobPostWorkflowNew.update();

            InterviewScheduleStatusUpdate interviewScheduleStatusUpdate = new InterviewScheduleStatusUpdate();
            interviewScheduleStatusUpdate.setJobPostWorkflow(jobPostWorkflowNew);
            interviewScheduleStatusUpdate.setStatus(JobPostWorkflowStatus.find.where().eq("status_id", jwStatus).findUnique());

            if (jwStatus == ServerConstants.JWF_STATUS_INTERVIEW_REJECTED_BY_RECRUITER_SUPPORT) {
                interviewScheduleStatusUpdate.setRejectReason(new RejectReasonDAO().getById(Long.valueOf(interviewStatusRequest.getReason())));
            }
            interviewScheduleStatusUpdate.save();

            // save the interaction
            InteractionService.createWorkflowInteraction(
                    jobPostWorkflowOld.getJobPostWorkflowUUId(),
                    candidate.getCandidateUUId(),
                    jwType,
                    null,
                    jobPostWorkflowOld.getJobPost().getJobPostId() + ": " + jobPostWorkflowOld.getJobPost().getJobRole().getJobName() + interactionResult,
                    channel
            );

            return ok("1");
        }
        return ok("0");
    }

    public static Integer confirmCandidateInterview(long jpId, long value, Candidate candidate, int channel) {

        String interactionResult = "";
        int interactionType = 0;
        Integer jwStatus = null;

        // fetch existing workflow old

        JobPostWorkflow jobPostWorkflowOld = JobPostWorkflow.find.where()
                .eq("jobPost.jobPostId", jpId)
                .eq("candidate.candidateId", candidate.getCandidateId())
                .orderBy().desc("creationTimestamp").setMaxRows(1).findUnique();

        if (value == ServerConstants.RESCHEULED_INTERVIEW_STATUS_ACCEPTED) { // accept
            interactionResult = " interview accepted by candidate";
            interactionType = InteractionConstants.INTERACTION_TYPE_CANDIDATE_ACCEPTS_RESCHEDULED_INTERVIEW;
            jwStatus = ServerConstants.JWF_STATUS_INTERVIEW_CONFIRMED;

            //sms to recruiter
            sendInterviewCandidateConfirmation(jobPostWorkflowOld, candidate);
            //sms to candidate
            sendInterviewConfirmationSms(jobPostWorkflowOld, candidate);

            //sending notification
            NotificationUtil.sendInterviewConfirmationNotification(candidate, jobPostWorkflowOld);

        } else if (value == ServerConstants.RESCHEULED_INTERVIEW_STATUS_REJECTED) { // reject
            interactionResult = " interview rejected by candidate";
            interactionType = InteractionConstants.INTERACTION_TYPE_CANDIDATE_REJECTS_RESCHEDULED_INTERVIEW;
            jwStatus = ServerConstants.JWF_STATUS_INTERVIEW_REJECTED_BY_CANDIDATE;

            //sending notification
            NotificationUtil.sendInterviewNotShortlistNotification(candidate, jobPostWorkflowOld);

            sendInterviewCandidateInterviewReject(jobPostWorkflowOld, candidate);
        }

        // Setting the existing jobpostworkflow status to confirmed
        JobPostWorkflow jobPostWorkflowNew = saveNewJobPostWorkflow(candidate.getCandidateId(), jpId,
                jobPostWorkflowOld, jobPostWorkflowOld.getStatus().getStatusId(), jwStatus,
                jobPostWorkflowOld.getScheduledInterviewTimeSlot().getInterviewTimeSlotId(),
                jobPostWorkflowOld.getScheduledInterviewDate(), channel);

        if (jobPostWorkflowNew != null) {
            jobPostWorkflowNew.setStatus(JobPostWorkflowStatus.find.where().eq("statusId", jwStatus).findUnique());
        }
        jobPostWorkflowNew.update();

        if (session().get("sessionChannel") == null) {
            channel = InteractionConstants.INTERACTION_CHANNEL_CANDIDATE_ANDROID;
        } else {
            channel = Integer.valueOf(session().get("sessionChannel"));
        }

        InterviewScheduleStatusUpdate interviewScheduleStatusUpdate = new InterviewScheduleStatusUpdate();
        interviewScheduleStatusUpdate.setJobPostWorkflow(jobPostWorkflowNew);
        interviewScheduleStatusUpdate.setStatus(JobPostWorkflowStatus.find.where().eq("status_id", jwStatus).findUnique());

        interviewScheduleStatusUpdate.save();
        // save the interaction
        InteractionService.createWorkflowInteraction(
                jobPostWorkflowOld.getJobPostWorkflowUUId(),
                candidate.getCandidateUUId(),
                interactionType,
                null,
                jobPostWorkflowOld.getJobPost().getJobPostId() + ": " + jobPostWorkflowOld.getJobPost().getJobRole().getJobName() + interactionResult,
                channel
        );
        return 1;
    }

    public static Map<Long, CandidateWorkflowData> getRecruiterJobLinedUpCandidates(Long jobPostId) {
        String statusSql = " and (status_id NOT IN ( '" + ServerConstants.JWF_STATUS_PRESCREEN_FAILED + "')) ";
        StringBuilder workFlowQueryBuilder = new StringBuilder("select createdby, candidate_id, creation_timestamp, job_post_id, status_id from job_post_workflow i " +
                " where i.job_post_id " +
                " = ('" + jobPostId + "') " +
                statusSql +
                " and creation_timestamp = " +
                " (select max(creation_timestamp) from job_post_workflow " +
                "       where i.candidate_id = job_post_workflow.candidate_id " +
                "       and i.job_post_id = job_post_workflow.job_post_id) " +
                " order by creation_timestamp desc ");

        RawSql rawSql = RawSqlBuilder.parse(workFlowQueryBuilder.toString())
                .columnMapping("creation_timestamp", "creationTimestamp")
                .columnMapping("job_post_id", "jobPost.jobPostId")
                .columnMapping("status_id", "status.statusId")
                .columnMapping("candidate_id", "candidate.candidateId")
                .columnMapping("createdby", "createdBy")
                .create();

        List<JobPostWorkflow> jobPostWorkflowList = Ebean.find(JobPostWorkflow.class)
                .setRawSql(rawSql)
                .findList();

        List<Candidate> candidateList = new ArrayList<>();
        Integer status = ServerConstants.JWF_STATUS_PRESCREEN_COMPLETED;

        Map<Long, CandidateWorkflowData> selectedCandidateMap = new LinkedHashMap<>();

        // until view is not available over this table, this loop get the distinct candidate who
        // got selected for a job post recently
        for (JobPostWorkflow jpwf : jobPostWorkflowList) {
            candidateList.add(jpwf.getCandidate());
        }
        // prep params for a jobPost
        JobPost jobPost = JobPost.find.where().eq("jobPostId", jobPostId).findUnique();

        List<JobPostToLocality> jobPostToLocalityList = jobPost.getJobPostToLocalityList();
        List<Long> localityIdList = new ArrayList<>();
        for (JobPostToLocality jobPostToLocality : jobPostToLocalityList) {
            localityIdList.add(jobPostToLocality.getLocality().getLocalityId());
        }

        candidateList = filterByLatLngOrHomeLocality(candidateList, localityIdList, ServerConstants.DEFAULT_MATCHING_ENGINE_RADIUS, false);
        Map<Long, CandidateExtraData> candidateExtraDataMap = computeExtraData(candidateList, JobPost.find.where().eq("jobPostId", jobPostId).findUnique(), new ArrayList<Integer>(Arrays.asList(status)));

        Map<Long, CandidateScoreData> candidateScoreDataMap = computeScoreData(candidateList, jobPostId);

        if (candidateScoreDataMap == null) {
            Logger.info("something went wrong while computing score data");
        }
        for (Candidate candidate : candidateList) {
            CandidateWorkflowData candidateWorkflowData = new CandidateWorkflowData();
            candidateWorkflowData.setCandidate(candidate);
            candidateWorkflowData.setExtraData(candidateExtraDataMap.get(candidate.getCandidateId()));
            if (candidateScoreDataMap != null)
                candidateWorkflowData.setScoreData(candidateScoreDataMap.get(candidate.getCandidateId()));
            selectedCandidateMap.put(candidate.getCandidateId(), candidateWorkflowData);
        }

        return selectedCandidateMap;
    }

    private static Map<Long, CandidateScoreData> computeScoreData(List<Candidate> candidateList, Long jobPostId) {

        if (candidateList == null || candidateList.size() == 0) {
            return null;
        }

        List<PreScreenPopulateResponse> populateResponseList = new ArrayList<>();
        Map<Long, CandidateScoreData> candidateScoreDataMap = new HashMap<>();
        for (Candidate candidate : candidateList) {
            PreScreenPopulateResponse populateResponse = getJobPostVsCandidate(jobPostId, candidate.getCandidateId(), true);
            if (populateResponse != null) {
                populateResponseList.add(populateResponse);
            }
        }

        double score;
        int band;
        int passed;
        int total;
        String matchingReason;
        String nonMatchingReason;
        for (PreScreenPopulateResponse response : populateResponseList) {
            if (response.getElementList().size() == 0) {
                continue;
            } else {
                passed = 0;
                total = 0;
                matchingReason = "";
                nonMatchingReason = "";
                for (PreScreenPopulateResponse.PreScreenElement pe : response.getElementList()) {
                    if (pe.isMatching()) {
                        matchingReason += pe.getPropertyTitle() + ", ";
//                        if(pe.isSingleEntity()) {
//                            matchingReason += pe.getCandidateElement().getPlaceHolder() + ", ";
//                        } else {
//                            for(PreScreenPopulateResponse.PreScreenCustomObject customObject: pe.getCandidateElementList()){
//                                matchingReason += customObject.getPlaceHolder() +
//                            }
//                        }
                        passed++;
                    } else {
                        nonMatchingReason += pe.getPropertyTitle() + ", ";
//                        if(pe.isSingleEntity()) {
//                            if(pe.getCandidateElement() != null) {
//                                nonMatchingReason += pe.getCandidateElement().getPlaceHolder() + ", ";
//                            }
//                        } else {
//                            if(pe.getCandidateElementList()!=null && pe.getCandidateElementList().size() > 0) {
//                                for(PreScreenPopulateResponse.PreScreenCustomObject customObject: pe.getCandidateElementList()){
//                                    nonMatchingReason += customObject.getPlaceHolder() + ", ";
//                                }
//                            }
//                        }
                    }
                    total++;
                }
                score = (double) passed / total;
                if (score > 0.75) {
                    band = 1;
                } else if (score < 0.50) {
                    band = 3;
                } else {
                    band = 2;
                }
                String finalReason = "";

                if (!matchingReason.isEmpty()) {
                    matchingReason = matchingReason.trim();
                    finalReason = "Matched for : " + matchingReason.trim().substring(0, matchingReason.length() - 1);
                }
                if (!nonMatchingReason.isEmpty()) {
                    nonMatchingReason = nonMatchingReason.trim();
                    finalReason += " <br/> Didn't Match   for : " + nonMatchingReason.trim().substring(0, nonMatchingReason.length() - 1);
                }
                candidateScoreDataMap.put(response.getCandidateId(), new CandidateScoreData(score, band, finalReason));
            }
        }

        return candidateScoreDataMap;
    }


    public static String updateCandidateInterviewDetail(Long candidateId, Long jobPostId, AddCandidateInterviewSlotDetail interviewSlotDetail, int channel) {
        Candidate candidate = Candidate.find.where().eq("candidateId", candidateId).findUnique();

        if (candidate == null) {
            return "Candidate doesn't exists";
        }

        // fetch existing workflow old
        JobPostWorkflow jobPostWorkflowOld = JobPostWorkflow.find.where()
                .eq("jobPost.jobPostId", jobPostId)
                .eq("candidate.candidateId", candidateId)
                .orderBy().desc("creationTimestamp").setMaxRows(1).findUnique();

        if (jobPostWorkflowOld == null) {
            Logger.info("jobPostWorkflow old is null");

            return null;
        }

        // TODO find a better way to handle applicants who applied to a job directly and didn't went through prescreen, but choosen interview slot
        // we create prescreen completed here for them to process them to interview stage
        // hint to find all these candidate would be interview slot set in jobPostworkflow table with status 4
        if (((channel == InteractionConstants.INTERACTION_CHANNEL_CANDIDATE_ANDROID)
                || (channel == InteractionConstants.INTERACTION_CHANNEL_CANDIDATE_WEBSITE)
                || (channel == InteractionConstants.INTERACTION_CHANNEL_PARTNER_WEBSITE))
                && (jobPostWorkflowOld.getStatus().getStatusId() == ServerConstants.JWF_STATUS_SELECTED)) {
            JobPostWorkflow jobPostWorkflowNew = JobPostWorkflowEngine.saveNewJobPostWorkflow(candidateId,
                    jobPostId, jobPostWorkflowOld, ServerConstants.JWF_STATUS_SELECTED,
                    ServerConstants.JWF_STATUS_PRESCREEN_COMPLETED, interviewSlotDetail.getTimeSlot(),
                    interviewSlotDetail.getScheduledInterviewDate(), channel);
            jobPostWorkflowOld = jobPostWorkflowNew;
        }

        String interactionResult = "";

        interactionResult = InteractionConstants.INTERACTION_RESULT_CANDIDATE_INTERVIEW_SCHEDULED;

        Integer jwfStatus = ServerConstants.JWF_STATUS_INTERVIEW_SCHEDULED;

        if (jobPostWorkflowOld.getStatus().getStatusId() == ServerConstants.JWF_STATUS_PRESCREEN_COMPLETED) {
            JobPostWorkflow jobPostWorkflowNew = JobPostWorkflowEngine.saveNewJobPostWorkflow(candidateId,
                    jobPostId, jobPostWorkflowOld, ServerConstants.JWF_STATUS_PRESCREEN_COMPLETED,
                    jwfStatus, interviewSlotDetail.getTimeSlot(),
                    interviewSlotDetail.getScheduledInterviewDate(), channel);

            if (jobPostWorkflowNew == null) {
                return null;
            }
            interactionResult += jobPostWorkflowNew.getJobPost().getJobPostId() + ": " + jobPostWorkflowNew.getJobPost().getJobRole().getJobName() + "@" + jobPostWorkflowNew.getJobPost().getCompany().getCompanyName();
        }

        // save the interaction
        InteractionService.createWorkflowInteraction(
                jobPostWorkflowOld.getJobPostWorkflowUUId(),
                candidate.getCandidateUUId(),
                InteractionConstants.INTERACTION_TYPE_CANDIDATE_INTERVIEW_SCHEDULED,
                null,
                interactionResult,
                channel
        );

        if (jobPostWorkflowOld.getJobPost().getInterviewDetailsList() != null && jobPostWorkflowOld.getJobPost().getInterviewDetailsList().size() > 0) {
            if (jobPostWorkflowOld.getJobPost().getInterviewDetailsList().get(0).getReviewApplication() != null
                    && jobPostWorkflowOld.getJobPost().getInterviewDetailsList().get(0).getReviewApplication() == 1) { // dont review applications, confirm it directly
                jwfStatus = ServerConstants.JWF_STATUS_INTERVIEW_CONFIRMED;
                try {

                    Thread.sleep(1000);
                    JobPostWorkflow jobPostWorkflowNew = JobPostWorkflowEngine.saveNewJobPostWorkflow(candidateId,
                            jobPostId, jobPostWorkflowOld, ServerConstants.JWF_STATUS_PRESCREEN_COMPLETED,
                            jwfStatus, interviewSlotDetail.getTimeSlot(),
                            interviewSlotDetail.getScheduledInterviewDate(), channel);

                    CandidateInterviewStatusUpdate candidateInterviewStatusUpdate = new CandidateInterviewStatusUpdate();
                    candidateInterviewStatusUpdate.setJobPostWorkflow(jobPostWorkflowNew);
                    candidateInterviewStatusUpdate.setJobPost(jobPostWorkflowOld.getJobPost());
                    candidateInterviewStatusUpdate.setCandidate(candidate);

                    sendInterviewConfirmationSms(jobPostWorkflowNew, candidate);

                    interactionResult = InteractionConstants.INTERACTION_RESULT_RECRUITER_AUTO_ACCEPT_JOB_INTERVIEW_DATE;

                    // save the interaction
                    InteractionService.createWorkflowInteraction(
                            jobPostWorkflowOld.getJobPostWorkflowUUId(),
                            candidate.getCandidateUUId(),
                            InteractionConstants.INTERACTION_TYPE_RECRUITER_ACCEPT_JOB_APPLICATION_INTERVIEW,
                            null,
                            interactionResult,
                            channel
                    );

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        return "OK";
    }

    public static JobPostWorkflow getCandidateLatestStatus(Candidate candidate, JobPost jobPost) {
        // fetch existing workflow old

        return JobPostWorkflow.find.where()
                .eq("jobPost.jobPostId", jobPost.getJobPostId())
                .eq("candidate.candidateId", candidate.getCandidateId())
                .orderBy().desc("creationTimestamp").setMaxRows(1).findUnique();
    }

    public static Integer updateFeedback(AddFeedbackRequest addFeedbackRequest, int channel) {
        // fetch existing workflow old
        JobPostWorkflow jobPostWorkflowOld = JobPostWorkflow.find.where()
                .eq("jobPost.jobPostId", addFeedbackRequest.getJobPostId())
                .eq("candidate.candidateId", addFeedbackRequest.getCandidateId())
                .orderBy().desc("creationTimestamp").setMaxRows(1).findUnique();

        if (jobPostWorkflowOld == null) {
            return 0;
        }
        RecruiterCreditHistory recruiterCreditHistoryLatest = RecruiterCreditHistory.find.where()
                .eq("RecruiterProfileId", jobPostWorkflowOld.getJobPost().getRecruiterProfile().getRecruiterProfileId())
                .eq("RecruiterCreditCategory", ServerConstants.RECRUITER_CATEGORY_INTERVIEW_UNLOCK)
                .orderBy().desc("create_timestamp").setMaxRows(1).findUnique();

        if (recruiterCreditHistoryLatest == null) {
            return -1;
        }

        if (recruiterCreditHistoryLatest.getRecruiterCreditsAvailable() == null || recruiterCreditHistoryLatest.getRecruiterCreditsAvailable() < 1) {
            return -1;
        }

        Boolean toDeductCredit = false;
        Integer jwStatus;
        Integer interactionType;
        String interactionResult;

        if (addFeedbackRequest.getFeedbackStatus() == ServerConstants.CANDIDATE_FEEDBACK_COMPLETE_SELECTED) {
            jwStatus = ServerConstants.JWF_STATUS_CANDIDATE_FEEDBACK_STATUS_COMPLETE_SELECTED;
            interactionType = InteractionConstants.INTERACTION_TYPE_CANDIDATE_FEEDBACK_SELECTED;
            interactionResult = InteractionConstants.INTERACTION_RESULT_CANDIDATE_SELECTED;

            toDeductCredit = true;
        } else if (addFeedbackRequest.getFeedbackStatus() == ServerConstants.CANDIDATE_FEEDBACK_COMPLETE_REJECTED) {
            jwStatus = ServerConstants.JWF_STATUS_CANDIDATE_FEEDBACK_STATUS_COMPLETE_REJECTED;
            interactionType = InteractionConstants.INTERACTION_TYPE_CANDIDATE_FEEDBACK_REJECTED;
            interactionResult = InteractionConstants.INTERACTION_RESULT_CANDIDATE_REJECTED;

            toDeductCredit = true;
        } else if (addFeedbackRequest.getFeedbackStatus() == ServerConstants.CANDIDATE_FEEDBACK_NO_SHOW) {
            jwStatus = ServerConstants.JWF_STATUS_CANDIDATE_FEEDBACK_STATUS_NO_SHOW;
            interactionType = InteractionConstants.INTERACTION_TYPE_CANDIDATE_FEEDBACK_NO_SHOW;
            interactionResult = InteractionConstants.INTERACTION_RESULT_CANDIDATE_NO_SHOW;

        } else {
            jwStatus = ServerConstants.JWF_STATUS_CANDIDATE_FEEDBACK_STATUS_NOT_QUALIFIED;
            interactionType = InteractionConstants.INTERACTION_TYPE_CANDIDATE_FEEDBACK_NOT_QUALIFIED;
            interactionResult = InteractionConstants.INTERACTION_RESULT_CANDIDATE_NOT_QUALIFIED;

        }

        if (toDeductCredit) {
            RecruiterCreditHistory recruiterCreditHistory = new RecruiterCreditHistory();
            recruiterCreditHistory.setRecruiterProfile(jobPostWorkflowOld.getJobPost().getRecruiterProfile());
            recruiterCreditHistory.setRecruiterCreditsAvailable(recruiterCreditHistoryLatest.getRecruiterCreditsAvailable() - 1);
            recruiterCreditHistory.setRecruiterCreditsUsed(recruiterCreditHistoryLatest.getRecruiterCreditsUsed() + 1);
            recruiterCreditHistory.setRecruiterCreditsAddedBy(ServerConstants.SELF_UNLOCKED_INTEVIEW);
            recruiterCreditHistory.setUnits(-1);
            recruiterCreditHistory.setRecruiterCreditCategory(RecruiterCreditCategory.find.where().eq("recruiter_credit_category_id", ServerConstants.RECRUITER_CATEGORY_INTERVIEW_UNLOCK).findUnique());

            //saving/updating all the rows
            recruiterCreditHistory.save();
        }

        // Setting the existing jobpostworkflow status to confirmed
        JobPostWorkflow jobPostWorkflowNew = saveNewJobPostWorkflow(addFeedbackRequest.getCandidateId(), addFeedbackRequest.getJobPostId(),
                jobPostWorkflowOld, jobPostWorkflowOld.getStatus().getStatusId(), jwStatus,
                jobPostWorkflowOld.getScheduledInterviewTimeSlot().getInterviewTimeSlotId(),
                jobPostWorkflowOld.getScheduledInterviewDate(), channel);

        if (jobPostWorkflowNew != null) {
            jobPostWorkflowNew.setStatus(JobPostWorkflowStatus.find.where().eq("statusId", jwStatus).findUnique());
        }
        jobPostWorkflowNew.update();

        InterviewFeedbackUpdate interviewFeedbackUpdate = new InterviewFeedbackUpdate();
        interviewFeedbackUpdate.setJobPostWorkflow(jobPostWorkflowNew);
        interviewFeedbackUpdate.setJobPost(jobPostWorkflowOld.getJobPost());
        interviewFeedbackUpdate.setCandidate(Candidate.find.where().eq("candidateId", addFeedbackRequest.getCandidateId()).findUnique());
        interviewFeedbackUpdate.setStatus(JobPostWorkflowStatus.find.where().eq("status_id", jwStatus).findUnique());
        interviewFeedbackUpdate.setCandidateInterviewStatusUpdateNote(addFeedbackRequest.getFeedbackComment());
        interviewFeedbackUpdate.setRejectReason(new RejectReasonDAO().getById(addFeedbackRequest.getRejectReason()));

        interviewFeedbackUpdate.save();

        Candidate candidate = Candidate.find.where().eq("candidateId", addFeedbackRequest.getCandidateId()).findUnique();

        if (jwStatus == ServerConstants.JWF_STATUS_CANDIDATE_FEEDBACK_STATUS_COMPLETE_SELECTED) {
            //sending notification
            NotificationUtil.sendInterviewSelectionNotification(candidate, jobPostWorkflowNew);

            //sending sms
            sendSelectedSmsToCandidate(jobPostWorkflowNew);
        } else {
            //sending notification
            NotificationUtil.sendInterviewRejectionNotification(candidate, jobPostWorkflowNew);

            //sending sms
            sendRejectedSmsToCandidate(jobPostWorkflowNew);
        }

        // save the interaction
        InteractionService.createWorkflowInteraction(
                jobPostWorkflowOld.getJobPostWorkflowUUId(),
                candidate.getCandidateUUId(),
                interactionType,
                null,
                interactionResult,
                channel
        );

        return 1;
    }


    public static Integer updateCandidateInterviewStatus(Candidate candidate, JobPost jobPost, Long val, Long reason, int channel) {
        // fetch existing workflow old
        JobPostWorkflow jobPostWorkflowOld = JobPostWorkflow.find.where()
                .eq("jobPost.jobPostId", jobPost.getJobPostId())
                .eq("candidate.candidateId", candidate.getCandidateId())
                .orderBy().desc("creationTimestamp").setMaxRows(1).findUnique();

        if (jobPostWorkflowOld == null) {
            return 0;
        }

        Integer jwStatus;
        Integer interactionType;
        String interactionResult;

        if (val == ServerConstants.CANDIDATE_INTERVIEW_STATUS_NOT_GOING) {
            jwStatus = ServerConstants.JWF_STATUS_CANDIDATE_INTERVIEW_STATUS_NOT_GOING;
            interactionType = InteractionConstants.INTERACTION_TYPE_CANDIDATE_STATUS_NOT_GOING;
            interactionResult = InteractionConstants.INTERACTION_RESULT_CANDIDATE_NOT_GOING;
        } else if (val == ServerConstants.CANDIDATE_INTERVIEW_STATUS_DELAYED) {
            jwStatus = ServerConstants.JWF_STATUS_CANDIDATE_INTERVIEW_STATUS_DELAYED;
            interactionType = InteractionConstants.INTERACTION_TYPE_CANDIDATE_STATUS_DELAYED;
            interactionResult = InteractionConstants.INTERACTION_RESULT_CANDIDATE_DELAYED;
        } else if (val == ServerConstants.CANDIDATE_INTERVIEW_STATUS_STARTED) {
            jwStatus = ServerConstants.JWF_STATUS_CANDIDATE_INTERVIEW_STATUS_STARTED;
            interactionType = InteractionConstants.INTERACTION_TYPE_CANDIDATE_STATUS_STARTED;
            interactionResult = InteractionConstants.INTERACTION_RESULT_CANDIDATE_STARTED;
        } else {
            jwStatus = ServerConstants.JWF_STATUS_CANDIDATE_INTERVIEW_STATUS_REACHED;
            interactionType = InteractionConstants.INTERACTION_TYPE_CANDIDATE_STATUS_REACHED;
            interactionResult = InteractionConstants.INTERACTION_RESULT_CANDIDATE_REACHED;
        }

        // Setting the existing jobpostworkflow status to confirmed
        JobPostWorkflow jobPostWorkflowNew = saveNewJobPostWorkflow(candidate.getCandidateId(), jobPost.getJobPostId(),
                jobPostWorkflowOld, jobPostWorkflowOld.getStatus().getStatusId(), jwStatus,
                jobPostWorkflowOld.getScheduledInterviewTimeSlot().getInterviewTimeSlotId(),
                jobPostWorkflowOld.getScheduledInterviewDate(), channel);

        if (jobPostWorkflowNew != null) {
            jobPostWorkflowNew.setStatus(JobPostWorkflowStatus.find.where().eq("statusId", jwStatus).findUnique());
        }
        jobPostWorkflowNew.update();

        CandidateInterviewStatusUpdate candidateInterviewStatusUpdate = new CandidateInterviewStatusUpdate();
        candidateInterviewStatusUpdate.setJobPostWorkflow(jobPostWorkflowNew);
        candidateInterviewStatusUpdate.setJobPost(jobPostWorkflowOld.getJobPost());
        candidateInterviewStatusUpdate.setCandidate(candidate);
        if (reason != null && reason > 0) {
            candidateInterviewStatusUpdate.setRejectReason(new RejectReasonDAO().getById(reason));
        }
        candidateInterviewStatusUpdate.save();

        if (jwStatus == ServerConstants.JWF_STATUS_CANDIDATE_INTERVIEW_STATUS_REACHED) {
            updateRecruiterWithCandidateStatus(jobPostWorkflowOld, candidate);
        }

        if (session().get("sessionChannel") == null) {
            channel = InteractionConstants.INTERACTION_CHANNEL_CANDIDATE_ANDROID;
        } else {
            channel = Integer.valueOf(session().get("sessionChannel"));
        }
        // save the interaction
        InteractionService.createWorkflowInteraction(
                jobPostWorkflowOld.getJobPostWorkflowUUId(),
                candidate.getCandidateUUId(),
                interactionType,
                null,
                interactionResult,
                channel
        );

        return 1;
    }

    public static List<JobPostWorkflow> getPartnerAppliedJobsForCandidate(Candidate candidate, Partner partner) {
        List<JobPostWorkflow> appliedJobsList = new ArrayList<>();

        List<JobApplication> jobApplicationList = JobApplication.find.where()
                .eq("CandidateId", candidate.getCandidateId())
                .eq("partner_id", partner.getPartnerId())
                .orderBy("jobApplicationCreateTimeStamp desc")
                .findList();
        if (jobApplicationList.size() == 0) {
            return appliedJobsList;
        }

        if (jobApplicationList.size() == 0) {
            return appliedJobsList;
        }

        String jobPostIdString = "";
        List<Long> jobPostIdList = new ArrayList<>();
        for (JobApplication jobApplication : jobApplicationList) {
            jobPostIdList.add(jobApplication.getJobPost().getJobPostId());
            jobPostIdString += jobApplication.getJobPost().getJobPostId() + ", ";
        }

        jobPostIdString = jobPostIdString.substring(0, jobPostIdString.length() - 2);

        String candidateAppliedJobsSql = "select job_post_id, status_id, scheduled_interview_time_slot, scheduled_interview_date, interview_location_lat, interview_location_lng " +
                "from job_post_workflow jwf where jwf.creation_timestamp = (select max(creation_timestamp)\n" +
                " from job_post_workflow where jwf.job_post_id = job_post_workflow.job_post_id and job_post_workflow.candidate_id = " + candidate.getCandidateId() + ") " +
                "and jwf.job_post_id in (" + jobPostIdString + ")";

        RawSql rawSql = RawSqlBuilder.parse(candidateAppliedJobsSql)
                .tableAliasMapping("jwf", "job_post_workflow")
                .columnMapping("job_post_id", "jobPost.jobPostId")
                .columnMapping("status_id", "status.statusId")
                .columnMapping("scheduled_interview_time_slot", "scheduledInterviewTimeSlot.interviewTimeSlotId")
                .columnMapping("scheduled_interview_date", "scheduledInterviewDate")
                .columnMapping("interview_location_lat", "interviewLocationLat")
                .columnMapping("interview_location_lng", "interviewLocationLng")
                .create();

        appliedJobsList = Ebean.find(JobPostWorkflow.class)
                .setRawSql(rawSql)
                .findList();

        List<PreScreenRequirement> preScreenRequirementList = PreScreenRequirement.find.where().in("job_post_id", jobPostIdList).findList();

        Map<Long, PreScreenRequirement> preScreenRequirementMap = new HashMap<>();
        for (PreScreenRequirement preScreenRequirement : preScreenRequirementList) {
            preScreenRequirementMap.putIfAbsent(preScreenRequirement.getJobPost().getJobPostId(), preScreenRequirement);
        }

        Logger.info("preScreenReqList: " + preScreenRequirementList.size());

        for (JobPostWorkflow jobPostWorkflowObj : appliedJobsList) {
            if (preScreenRequirementMap.get(jobPostWorkflowObj.getJobPost().getJobPostId()) == null) {
                jobPostWorkflowObj.setPreScreenRequired(false);
            } else if (jobPostWorkflowObj.getStatus().getStatusId() >= ServerConstants.JWF_STATUS_PRESCREEN_ATTEMPTED) {
                jobPostWorkflowObj.setPreScreenRequired(false);
            }
        }

        return appliedJobsList;
    }
}
