package controllers.businessLogic.JobWorkflow;

import api.InteractionConstants;
import api.ServerConstants;
import api.http.httpRequest.Workflow.PreScreenRequest;
import api.http.httpRequest.Workflow.SelectedCandidateRequest;
import api.http.httpResponse.CandidateExtraData;
import api.http.httpResponse.CandidateWorkflowData;
import api.http.httpResponse.Workflow.PreScreenPopulateResponse;
import api.http.httpResponse.Workflow.WorkflowResponse;
import com.avaje.ebean.*;
import controllers.businessLogic.InteractionService;
import controllers.businessLogic.MatchingEngineService;
import models.entity.Candidate;
import models.entity.Interaction;
import models.entity.JobPost;
import models.entity.OM.*;
import models.entity.Static.JobPostWorkflowStatus;
import models.entity.Static.JobRoleToDocument;
import models.entity.Static.Language;
import models.entity.Static.Locality;
import models.util.SmsUtil;
import models.util.Util;
import play.Logger;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.*;

import static com.avaje.ebean.Expr.eq;
import static play.libs.Json.toJson;
import static play.mvc.Controller.session;

/**
 * Created by zero on 4/10/16.
 */
public class JobPostWorkflowEngine {

    /**
     *
     *  @param jobPostId  match candidates for this jobPost
     *  @param maxAge  max range criteria to be taken into consideration while matching
     *  @param gender  gender criteria to be taken into consideration while matching
     *  @param experienceIdList experience duration to be taken into consideration while matching
     *  @param jobPostLocalityIdList  candidates to be matched within x Km of any of the provided locality
     *  @param languageIdList  candidate to be matched for any of this language. Output contains the
     *                       indication to show matching & non-matching language
     *
     *
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
                                                                        Double radius)
    {
        Map<Long, CandidateWorkflowData> matchedCandidateMap = new LinkedHashMap<>();
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);

        JobPost jobPost = JobPost.find.where().eq("jobPostId", jobPostId).findUnique();

        List<Integer> minExperienceList = new ArrayList<>();
        List<Integer> maxExperienceList = new ArrayList<>();

        // get jobrolepref for candidate

        Query<Candidate> query = Candidate.find.query();

        // problem: all age is null/0 and dob is also null
        // select candidate falling under the specified age req
        if (maxAge != null && maxAge !=0 ) {
            int startYear = currentYear - maxAge;
            query = query
                    .where()
                    .isNotNull("candidateDOB")
                    .ge("candidateDOB", startYear + "-01-01").query();
        }

        // select candidate based on specific gender req, else pass
        if (gender != null && gender>=0 && gender != ServerConstants.GENDER_ANY) {
            query = query
                    .where()
                    .isNotNull("candidateGender")
                    .eq("candidateGender", gender).query();
        }

        // select candidate whose totalExperience falls under the req exp
        if (experienceIdList != null && experienceIdList.size()>0) {
            // geDurationFromExperience returns minExperience req. (in Months)
            int minima;
            int maxima;
            for (Integer experienceId : experienceIdList) {
                ExperienceValue experience = getDurationFromExperience(experienceId);
                if(experience != null){
                    minExperienceList.add(experience.minExperienceValue);
                    maxExperienceList.add(experience.maxExperienceValue);
                } else {
                    break;
                }
            }
            if(minExperienceList.size() > 0){
                Collections.sort(maxExperienceList, Collections.reverseOrder());
                Collections.sort(minExperienceList);

                minima = minExperienceList.get(0);
                maxima = maxExperienceList.get(0);

                if(minima == 0 && maxima == 0) {
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
                if(maxima != 0) {
                    query = query
                            .where()
                            .isNotNull("candidateTotalExperience")
                            .le("candidateTotalExperience", maxima).query();
                }
            }
        }

        // select candidate w.r.t candidateLastWithdrawnSalary
        if (maxSalary != null && maxSalary != 0){
            query =  query
                    .where()
                    .isNotNull("candidateLastWithdrawnSalary")
                    .le("candidateLastWithdrawnSalary", maxSalary)
                    .query();
        } else if (minSalary != null && minSalary != 0) {
            query =  query
                    .where()
                    .isNotNull("candidateLastWithdrawnSalary")
                    .le("candidateLastWithdrawnSalary", minSalary)
                    .query();
        }
        // select candidate w.r.t language
        if (languageIdList != null && languageIdList.size() > 0) {
            query =  query.select("*").fetch("languageKnownList")
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
        if (educationIdList != null && educationIdList.size()> 0) {
            query = query.select("*").fetch("candidateEducation")
                    .where()
                    .isNotNull("candidateEducation")
                    .in("candidateEducation.education.educationId", educationIdList)
                    .query();
        }

        if(jobPostDocumentIdList != null && jobPostDocumentIdList.size() > 0) {
            query = query.select("*").fetch("idProofReferenceList")
                    .where()
                    .isNotNull("idProofReferenceList")
                    .in("idProofReferenceList.idProof.idProofId", jobPostDocumentIdList)
                    .query();
        }
        if(jobPostAssetIdList != null && jobPostAssetIdList.size() > 0) {
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

        Map<Long, CandidateExtraData> allFeature = computeExtraData(candidateList, jobPost);

        if(candidateList.size() != 0) {
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
     *
     * this is being used by the recruiter for searching candidates
     *
     *  @param maxAge  max range criteria to be taken into consideration while matching
     *  @param gender  gender criteria to be taken into consideration while matching
     *  @param experienceId experience duration to be taken into consideration while matching
     *  @param jobPostLocalityIdList  candidates to be matched within x Km of any of the provided locality
     *  @param languageIdList  candidate to be matched for any of this language. Output contains the
     *                       indication to show matching & non-matching language
     *
     *
     */
    public static Map<Long, CandidateWorkflowData> getCandidateForRecruiterSearch(Integer maxAge,
                                                                                  Long minSalary,
                                                                                  Long maxSalary,
                                                                                  Integer gender,
                                                                                  Integer experienceId,
                                                                                  Long jobRoleId,
                                                                                  Integer educationId,
                                                                                  List<Long> jobPostLocalityIdList,
                                                                                  List<Integer> languageIdList,
                                                                                  Double radius)
    {
        Map<Long, CandidateWorkflowData> matchedCandidateMap = new LinkedHashMap<>();

        // geDurationFromExperience returns minExperience req. (in Months)
        ExperienceValue experience = getDurationFromExperience(experienceId);

        Query<Candidate> query = Candidate.find.query();

        //query candidate query with the filter params
        query = getFilteredQuery(maxAge, minSalary, maxSalary,gender, jobRoleId, educationId, languageIdList, experience);

        List<Candidate> candidateList = filterByLatLngOrHomeLocality(query.findList(), jobPostLocalityIdList, radius, true);

        Map<Long, CandidateExtraData> allFeature = computeExtraDataForRecruiterSearchResult(candidateList);

        if(candidateList.size() != 0) {
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
     * @param jobPostId
     * Prepare params and calls getMatchingCandidate
     */
    public static Map<Long, CandidateWorkflowData> getMatchingCandidate(Long jobPostId)
    {
        // prep params for a jobPost
        JobPost jobPost = JobPost.find.where().eq("jobPostId", jobPostId).findUnique();

        Integer maxAge = jobPost.getJobPostMaxAge();
        Long minSalary = jobPost.getJobPostMinSalary();
        Long maxSalary = jobPost.getJobPostMaxSalary();
        Integer gender = jobPost.getGender();

        Long jobRoleId = jobPost.getJobRole().getJobRoleId();

        // geDurationFromExperience returns minExperience req. (in Months)
        Integer experienceId = jobPost.getJobPostExperience() != null ? jobPost.getJobPostExperience().getExperienceId(): null;

        Integer educationId = jobPost.getJobPostEducation() != null ? jobPost.getJobPostEducation().getEducationId() : null;

        List<Integer> experienceIdList = new ArrayList<>();
        experienceIdList.add(experienceId);

        List<Integer> educationIdList = new ArrayList<>();
        educationIdList.add(educationId);
        List<JobPostToLocality> jobPostToLocalityList = jobPost.getJobPostToLocalityList();
        List<Long> localityIdList = new ArrayList<>();
        for (JobPostToLocality jobPostToLocality: jobPostToLocalityList) {
            localityIdList.add(jobPostToLocality.getLocality().getLocalityId());
        }

        List<JobPostLanguageRequirement> languageRequirements = jobPost.getJobPostLanguageRequirements();
        List<Integer> languageIdList = new ArrayList<>();
        for (JobPostLanguageRequirement requirement : languageRequirements) {
            languageIdList.add(requirement.getLanguage().getLanguageId());
        }

        List<Integer> jobPostDocumentIdList = new ArrayList<>();
        for(JobPostDocumentRequirement jobPostDocumentRequirement: jobPost.getJobPostDocumentRequirements()) {
            jobPostDocumentIdList.add(jobPostDocumentRequirement.getIdProof().getIdProofId());
        }

        List<Integer> jobPostAssetIdList = new ArrayList<>();
        for(JobPostAssetRequirement jobPostAssetRequirement: jobPost.getJobPostAssetRequirements()) {
            jobPostAssetIdList.add(jobPostAssetRequirement.getAsset().getAssetId());
        }

        // call master method
        return getMatchingCandidate(jobPostId, maxAge, minSalary, maxSalary, gender, experienceIdList, jobRoleId, educationIdList, localityIdList, languageIdList, jobPostDocumentIdList, jobPostAssetIdList, ServerConstants.DEFAULT_MATCHING_ENGINE_RADIUS);
    }

    public static Map<Long, CandidateWorkflowData> getSelectedCandidates(Long jobPostId) {

        StringBuilder workFlowQueryBuilder = new StringBuilder("select createdby, candidate_id, creation_timestamp, job_post_id, status_id from job_post_workflow i " +
                " where i.job_post_id " +
                " = ('"+jobPostId+"') " +
                " and (status_id = '" +ServerConstants.JWF_STATUS_SELECTED+ "' or status_id = '" +ServerConstants.JWF_STATUS_PRESCREEN_ATTEMPTED+"') "+
                " and creation_timestamp = " +
                " (select max(creation_timestamp) from job_post_workflow where i.candidate_id = job_post_workflow.candidate_id) " +
                " order by creation_timestamp desc ");

        Logger.info("rawSql"+workFlowQueryBuilder.toString());

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
        for( JobPostWorkflow jpwf: jobPostWorkflowList) {
            candidateList.add(jpwf.getCandidate());
        }
        // prep params for a jobPost
        JobPost jobPost = JobPost.find.where().eq("jobPostId", jobPostId).findUnique();

        List<JobPostToLocality> jobPostToLocalityList = jobPost.getJobPostToLocalityList();
        List<Long> localityIdList = new ArrayList<>();
        for (JobPostToLocality jobPostToLocality: jobPostToLocalityList) {
            localityIdList.add(jobPostToLocality.getLocality().getLocalityId());
        }


        candidateList = filterByLatLngOrHomeLocality(candidateList, localityIdList, ServerConstants.DEFAULT_MATCHING_ENGINE_RADIUS, false);
        Map<Long, CandidateExtraData> candidateExtraDataMap = computeExtraData(candidateList, JobPost.find.where().eq("jobPostId", jobPostId).findUnique());

        for ( Candidate candidate: candidateList) {
            CandidateWorkflowData candidateWorkflowData = new CandidateWorkflowData();
            candidateWorkflowData.setCandidate(candidate);
            candidateWorkflowData.setExtraData(candidateExtraDataMap.get(candidate.getCandidateId()));
            selectedCandidateMap.put(candidate.getCandidateId(), candidateWorkflowData);
        }

        return selectedCandidateMap;
    }

    public static WorkflowResponse saveSelectedCandidates(SelectedCandidateRequest request) {
        WorkflowResponse response = new WorkflowResponse();
        List<Candidate> selectedCandidateList = Candidate.find.where().in("candidateId", request.getSelectedCandidateIdList()).findList();

        if (request.getSelectedCandidateIdList()!=null
                && request.getSelectedCandidateIdList().size() == 0) {
            response.setStatus(WorkflowResponse.STATUS.FAILED);
            response.setMessage("Something Went Wrong ! Please try again");
            return response;
        }

        List<Long> selectedCandidateIdList = new ArrayList<>();
        for (Candidate candidate: selectedCandidateList) {
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

        for (Candidate candidate: selectedCandidateList) {
            JobPostWorkflow jobPostWorkflow = workflowMap.get(candidate.getCandidateId());
            if(jobPostWorkflow == null) {
                jobPostWorkflow = new JobPostWorkflow();
                jobPostWorkflow.setJobPost(jobPost);
                jobPostWorkflow.setCandidate(candidate);
                jobPostWorkflow.setCreatedBy(session().get("sessionUsername"));
                jobPostWorkflow.setStatus(status);
                jobPostWorkflow.save();
                response.setStatus(WorkflowResponse.STATUS.SUCCESS);
                response.setMessage("Selection completed successfully.");
                // not redirecting user to next page.
                response.setNextView("match_view");
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

    public static PreScreenPopulateResponse getJobPostVsCandidate(Long jobPostId, Long candidateId) {
        PreScreenPopulateResponse populateResponse = new PreScreenPopulateResponse();

        Candidate candidate = Candidate.find.where().eq("candidateId", candidateId).findUnique();
        if (candidate == null){
            populateResponse.setStatus(PreScreenPopulateResponse.Status.FAILURE);
            return null;
        }

        JobPost jobPost = JobPost.find.where().eq("jobPostId", jobPostId).findUnique();
        if (jobPost == null){
            populateResponse.setStatus(PreScreenPopulateResponse.Status.FAILURE);
            return null;
        }

        List<PreScreenRequirement> preScreenRequirementList =  PreScreenRequirement.find.where()
                .eq("jobPost.jobPostId", jobPostId).orderBy().asc("category").findList();


        // constructor for this class make all default flag as true, we will mark it false wherever its not satisfied
        populateResponse.jobPostId = jobPostId;
        populateResponse.candidateId = candidateId;

        PreScreenPopulateResponse.PreScreenElement preScreenElement;

        Map<Integer, List<PreScreenRequirement>> preScreenMap = new HashMap<>();

        for (PreScreenRequirement preScreenRequirement: preScreenRequirementList) {
            List<PreScreenRequirement> preScreenRequirements = preScreenMap.get(preScreenRequirement.getCategory());
            if(preScreenRequirements == null ) {
                preScreenRequirements = new ArrayList<>();
            }
            preScreenRequirements.add(preScreenRequirement);
            preScreenMap.put(preScreenRequirement.getCategory(), preScreenRequirements);
        }

        for (Map.Entry<Integer, List<PreScreenRequirement>> entry: preScreenMap.entrySet()) {
            switch (entry.getKey()) {
                case ServerConstants.CATEGORY_DOCUMENT:
                        boolean isAvailable = false;

                        preScreenElement = new PreScreenPopulateResponse.PreScreenElement();
                        preScreenElement.setPropertyTitle("Document");

                        preScreenElement.jobPostElementList = new ArrayList<>();
                        preScreenElement.candidateElementList = new ArrayList<>();
                        if (candidate.getIdProofReferenceList() != null && candidate.getIdProofReferenceList().size() > 0) {
                            isAvailable = true;
                        }
                        for (PreScreenRequirement preScreenRequirement : entry.getValue()) {
                            preScreenElement.jobPostElementList.add(preScreenRequirement.getIdProof().getIdProofName());
                            preScreenElement.propertyIdList.add(preScreenRequirement.getPreScreenRequirementId());
                            if (isAvailable && candidate.getIdProofReferenceList().contains(preScreenRequirement.getIdProof())) {
                                preScreenElement.candidateElementList.add(preScreenRequirement.getIdProof().getIdProofName());
                            } else {
                                preScreenElement.isMatching = false;
                            }
                        }
                        preScreenElement.isSingleEntity = false;
                        populateResponse.elementList.add(preScreenElement);
                    break;
                case ServerConstants.CATEGORY_LANGUAGE:
                    List<Language> candidateLanguageList = new ArrayList<>();
                    List<Language> jobPostLanguageList = new ArrayList<>();

                    preScreenElement = new PreScreenPopulateResponse.PreScreenElement();
                    preScreenElement.setPropertyTitle("Language");

                    preScreenElement.jobPostElementList = new ArrayList<>();
                    preScreenElement.candidateElementList = new ArrayList<>();

                    for (PreScreenRequirement preScreenRequirement : entry.getValue()) {
                        preScreenElement.propertyIdList.add(preScreenRequirement.getPreScreenRequirementId());
                        preScreenElement.jobPostElementList.add(preScreenRequirement.getLanguage().getLanguageName());
                        jobPostLanguageList.add(preScreenRequirement.getLanguage());
                    }
                    if (candidate.getLanguageKnownList() != null && candidate.getLanguageKnownList().size()>0) {
                        for (LanguageKnown lk : candidate.getLanguageKnownList()){
                            candidateLanguageList.add(lk.getLanguage());
                        }
                        for (Language language: jobPostLanguageList) {
                            if (candidateLanguageList.contains(language)) {
                                preScreenElement.candidateElementList.add(language.getLanguageName());
                            } else {
                                preScreenElement.isMatching = false;
                            }
                        }
                    } else {
                        preScreenElement.isMatching = false;
                    }
                    preScreenElement.isSingleEntity = false;
                    populateResponse.elementList.add(preScreenElement);
                    break;
                case ServerConstants.CATEGORY_ASSET:
                    // we don't capture candidate asset detail into asset object
                    preScreenElement = new PreScreenPopulateResponse.PreScreenElement();
                    preScreenElement.setPropertyTitle("Asset Owned");
                    preScreenElement.jobPostElementList = new ArrayList<>();
                    preScreenElement.candidateElementList = new ArrayList<>();

                    for (PreScreenRequirement preScreenRequirement : entry.getValue()) {
                        preScreenElement.jobPostElementList.add(preScreenRequirement.getAsset().getAssetTitle());
                        preScreenElement.propertyIdList.add(preScreenRequirement.getPreScreenRequirementId());
                    }
                    preScreenElement.isSingleEntity = false;
                    preScreenElement.isMatching = true;
                    preScreenElement.isMinReq = true;

                    populateResponse.elementList.add(preScreenElement);
                    break;
                case ServerConstants.CATEGORY_PROFILE:
                    for (PreScreenRequirement preScreenRequirement: entry.getValue()) {
                        if (preScreenRequirement != null && preScreenRequirement.getProfileRequirement()!=null) {
                            if (preScreenRequirement.getProfileRequirement().getProfileRequirementTitle().equalsIgnoreCase("age")) {
                                if (jobPost.getJobPostMaxAge() != null && jobPost.getJobPostMaxAge() > 0) {
                                    preScreenElement = new PreScreenPopulateResponse.PreScreenElement();
                                    preScreenElement.setPropertyTitle("Age");
                                    preScreenElement.propertyIdList.add(preScreenRequirement.getPreScreenRequirementId());
                                    preScreenElement.jobPostElement = (jobPost.getJobPostMaxAge());
                                    if(candidate.getCandidateAge() != null &&  candidate.getCandidateAge() > 0) {
                                        preScreenElement.candidateElement = (candidate.getCandidateAge());

                                        if(!(jobPost.getJobPostMaxAge() >= candidate.getCandidateAge())) {
                                            preScreenElement.isMatching = false;
                                        }
                                    } else {
                                        // candidate age is not available hence not a match
                                        preScreenElement.isMatching = false;
                                    }
                                    populateResponse.elementList.add(preScreenElement);
                                }
                            } else if(preScreenRequirement.getProfileRequirement().getProfileRequirementTitle().equalsIgnoreCase("experience")) {
                                if (jobPost.getJobPostExperience() != null) {
                                    preScreenElement = new PreScreenPopulateResponse.PreScreenElement();
                                    preScreenElement.setPropertyTitle("Experience");

                                    preScreenElement.propertyIdList.add(preScreenRequirement.getPreScreenRequirementId());
                                    ExperienceValue jobPostMinMaxExp = getDurationFromExperience(jobPost.getJobPostExperience().getExperienceId());
                                    preScreenElement.jobPostElement=(jobPost.getJobPostExperience().getExperienceType());
                                    if(candidate.getCandidateTotalExperience() != null) {
                                        double totalExpInYrs= ((double)candidate.getCandidateTotalExperience())/12;
                                        preScreenElement.candidateElement = (Util.RoundTo2Decimals(totalExpInYrs)+ " Yrs");

                                        if(!(jobPostMinMaxExp.minExperienceValue <= candidate.getCandidateTotalExperience())) {
                                            preScreenElement.isMatching = false;
                                        }
                                    } else {
                                        // candidate exp is not available hence not a match
                                        preScreenElement.isMatching = false;
                                    }
                                    populateResponse.elementList.add(preScreenElement);
                                }
                            } else if(preScreenRequirement.getProfileRequirement().getProfileRequirementTitle().equalsIgnoreCase("education")) {
                                if (jobPost.getJobPostEducation() != null) {
                                    preScreenElement = new PreScreenPopulateResponse.PreScreenElement();
                                    preScreenElement.setPropertyTitle("Education");

                                    preScreenElement.propertyIdList.add(preScreenRequirement.getPreScreenRequirementId());
                                    preScreenElement.jobPostElement = (jobPost.getJobPostEducation().getEducationName());
                                    if(candidate.getCandidateEducation() != null) {
                                        preScreenElement.candidateElement = (candidate.getCandidateEducation().getEducation().getEducationName());

                                        if(!(jobPost.getJobPostEducation().equals(candidate.getCandidateEducation().getEducation()))) {
                                            preScreenElement.isMatching = false;
                                        }
                                    } else {
                                        // candidate edu is not available hence not a match
                                        preScreenElement.isMatching = false;
                                    }
                                    populateResponse.elementList.add(preScreenElement);
                                }
                            } else if(preScreenRequirement.getProfileRequirement().getProfileRequirementTitle().equalsIgnoreCase("gender")) {
                                if (jobPost.getGender() != null) {
                                    preScreenElement = new PreScreenPopulateResponse.PreScreenElement();
                                    preScreenElement.setPropertyTitle("Gender");

                                    preScreenElement.propertyIdList.add(preScreenRequirement.getPreScreenRequirementId());
                                    preScreenElement.jobPostElement = jobPost.getGender() == ServerConstants.GENDER_ANY ? "Any" : jobPost.getGender() == ServerConstants.GENDER_MALE ? "Male": "Female";
                                    if(candidate.getCandidateGender() != null) {
                                        preScreenElement.candidateElement = candidate.getCandidateGender() == ServerConstants.GENDER_MALE ? "Male" : "Female";
                                        if (candidate.getCandidateGender() != jobPost.getGender() && jobPost.getGender() != ServerConstants.GENDER_ANY) {
                                            preScreenElement.isMatching = false;
                                        }
                                    } else {
                                        preScreenElement.isMatching = false;
                                    }
                                    preScreenElement.isMinReq = false;

                                    populateResponse.elementList.add(preScreenElement);
                                }
                            } else if(preScreenRequirement.getProfileRequirement().getProfileRequirementTitle().equalsIgnoreCase("salary")) {
                                if (jobPost.getJobPostMinSalary() != null && jobPost.getJobPostMinSalary() != 0) {
                                    preScreenElement = new PreScreenPopulateResponse.PreScreenElement();
                                    preScreenElement.setPropertyTitle("Salary");

                                    preScreenElement.propertyIdList.add(preScreenRequirement.getPreScreenRequirementId());
                                    preScreenElement.jobPostElement = ("Rs."+jobPost.getJobPostMinSalary() + " - Rs."+jobPost.getJobPostMaxSalary());
                                    if (candidate.getCandidateLastWithdrawnSalary() != null) {
                                        preScreenElement.candidateElement = (candidate.getCandidateLastWithdrawnSalary());
                                        if (candidate.getCandidateLastWithdrawnSalary() > jobPost.getJobPostMinSalary()) {
                                            if (jobPost.getJobPostMaxSalary() != null
                                                    && jobPost.getJobPostMaxSalary() != 0
                                                    && (candidate.getCandidateLastWithdrawnSalary() > jobPost.getJobPostMaxSalary())) {
                                                preScreenElement.isMatching = false;
                                            }
                                        }
                                    } else {
                                        preScreenElement.isMatching = false;
                                    }
                                    preScreenElement.isMinReq = false;

                                    populateResponse.elementList.add(preScreenElement);
                                }
                            } else if(preScreenRequirement.getProfileRequirement().getProfileRequirementTitle().equalsIgnoreCase("locality")) {

                                // if we don't have a job Req we assume candidate satisfies the req
                                if (jobPost.getJobPostToLocalityList() != null &&  jobPost.getJobPostToLocalityList().size() > 0) {

                                    preScreenElement = new PreScreenPopulateResponse.PreScreenElement();
                                    preScreenElement.setPropertyTitle("Locality");

                                    preScreenElement.propertyIdList.add(preScreenRequirement.getPreScreenRequirementId());
                                    List<Long> localityIdList = new ArrayList<>();
                                    Iterator<JobPostToLocality> iterator = jobPost.getJobPostToLocalityList().iterator();
                                    StringBuilder jobPostLocalityString = new StringBuilder("");
                                    while (iterator.hasNext()) {
                                        JobPostToLocality jobPostToLocality = iterator.next();
                                        localityIdList.add(jobPostToLocality.getLocality().getLocalityId());
                                        jobPostLocalityString.append(jobPostToLocality.getLocality().getLocalityName());
                                        if (iterator.hasNext()){
                                            jobPostLocalityString.append(", ");
                                        }
                                    }
                                    preScreenElement.jobPostElement = jobPostLocalityString.toString();
                                    List<Candidate> candidateList = filterByLatLngOrHomeLocality(new ArrayList<>(Arrays.asList(candidate)), localityIdList, ServerConstants.DEFAULT_MATCHING_ENGINE_RADIUS, false);
                                    if(candidateList.size()>0) preScreenElement.candidateElement = candidateList.get(0).getMatchedLocation();
                                    preScreenElement.isMinReq = false;
                                    preScreenElement.isMatching = true;
                                    preScreenElement.isSingleEntity = true;

                                    populateResponse.elementList.add(preScreenElement);
                                }
                            } else if(preScreenRequirement.getProfileRequirement().getProfileRequirementTitle().equalsIgnoreCase("worktimings")) {
                                if (jobPost.getJobPostShift() != null) {
                                    preScreenElement = new PreScreenPopulateResponse.PreScreenElement();
                                    preScreenElement.setPropertyTitle("Work Shift");

                                    preScreenElement.propertyIdList.add(preScreenRequirement.getPreScreenRequirementId());
                                    preScreenElement.jobPostElement = jobPost.getJobPostShift().getTimeShiftName();
                                    if (candidate.getTimeShiftPreference() != null) {
                                        preScreenElement.candidateElement = candidate.getTimeShiftPreference().getTimeShift().getTimeShiftName();
                                        if (jobPost.getJobPostShift().getTimeShiftId() != candidate.getTimeShiftPreference().getTimeShift().getTimeShiftId()) {
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
        populateResponse.setStatus(PreScreenPopulateResponse.Status.SUCCESS);
        return populateResponse;
    }

    public static String updatePreScreenAttempt(Long jobPostId, Long candidateId, String callStatus) {
        // Interaction for PreScreen Call Attempt
        String interactionResult;
        String responseMsg;

            Candidate candidate = Candidate.find.where().eq("candidateId", candidateId).findUnique();

            // fetch existing workflow old
            JobPostWorkflow jobPostWorkflowOld = JobPostWorkflow.find.where()
                    .eq("jobPost.jobPostId", jobPostId)
                    .eq("candidate.candidateId", candidateId)
                    .orderBy().desc("creationTimestamp").setMaxRows(1).findUnique();

            // A value is for overriding leadStatus is also there in Lead Model setLeadStatus
            if(candidate != null && jobPostWorkflowOld != null) {

                // If call was connected just set the right interaction result
                if (callStatus.equals("CONNECTED")) {
                    interactionResult = "Pre Screen Out Bound Call Successfully got connected";
                    responseMsg = "call_success";
                }
                else {
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
                    responseMsg = "OK";
                }

                // Setting the existing jobpostworkflow status to attempted
                JobPostWorkflow jobPostWorkflowNew = saveNewJobPostWorkflow(candidate.getCandidateId(), jobPostId, jobPostWorkflowOld);

                JobPostWorkflowStatus status = JobPostWorkflowStatus.find.where().eq("statusId", ServerConstants.JWF_STATUS_PRESCREEN_ATTEMPTED).findUnique();
                jobPostWorkflowNew.setStatus(status);
                jobPostWorkflowNew.update();

                // save the interaction
                InteractionService.createInteractionForPreScreenAttempts(
                        jobPostWorkflowOld.getJobPostWorkflowUUId(),
                        candidate.getCandidateUUId(),
                        InteractionConstants.INTERACTION_TYPE_CANDIDATE_PRE_SCREEN_ATTEMPTED,
                        interactionResult
                );
                return responseMsg;
            }
        return "NA";
    }

    public static String savePreScreenResult(PreScreenRequest preScreenRequest) {
        // fetch existing workflow old
        JobPostWorkflow jobPostWorkflowOld = JobPostWorkflow.find.where()
                .eq("jobPost.jobPostId", preScreenRequest.getJobPostId())
                .eq("candidate.candidateId", preScreenRequest.getCandidateId())
                .orderBy().desc("creationTimestamp").setMaxRows(1).findUnique();

        // save PreScreen candidate
        JobPostWorkflow jobPostWorkflowNew = saveNewJobPostWorkflow(preScreenRequest.getCandidateId(), preScreenRequest.getJobPostId(), jobPostWorkflowOld);

        if (jobPostWorkflowNew == null) {
            return "Error";
        }
        // TODO: null check

        // fetch the last attempted pre-screen result for this jobpost workflow
        Query<PreScreenResult> query = PreScreenResult.find.query();
        query =  query.select("*").fetch("jobPostWorkflow")
                .where()
                .eq("jobPostWorkflow.jobPostWorkflowId", jobPostWorkflowOld.getJobPostWorkflowId())
                .query();

        PreScreenResult existingPreScreenResult = query.findUnique();

        // Create new result obj to be saved for this current attempt
        PreScreenResult preScreenResult = new PreScreenResult();

        // make sure we increment the attempt count if needed
        if( existingPreScreenResult == null) {
            preScreenResult.setAttemptCount(1);
        } else {
            preScreenResult.setAttemptCount(existingPreScreenResult.getAttemptCount() + 1);
        }

        preScreenResult.setUpdateTimestamp(new Timestamp(System.currentTimeMillis()));


        // fetch total no. of pre screen requirements for this jobpost. this is needed to score the attempt
        List<PreScreenRequirement> preScreenRequirementList = PreScreenRequirement.find.where()
                .eq("jobPost.jobPostId", preScreenRequest.getJobPostId())
                .findList();

        if(preScreenRequirementList == null || preScreenRequirementList.size() == 0) {
            Logger.error("PreScreen Requirement empty for jobPostId:" +preScreenRequest.getJobPostId());
            return "Error";
        }

        if(preScreenRequest.getPreScreenNote() != null && !preScreenRequest.getPreScreenNote().trim().isEmpty()){
            preScreenResult.setPreScreenResultNote(preScreenRequest.getPreScreenNote());
        }

        double score =  ((double) preScreenRequest.getPreScreenIdList().size()/(double) preScreenRequirementList.size());

        preScreenResult.setResultScore(Util.RoundTo2Decimals(score));
        preScreenResult.setForceSet(preScreenRequest.isPass());

        preScreenResult.setJobPostWorkflow(jobPostWorkflowNew);
        preScreenResult.save();

        // support user can decide whether a candidate passed or failed pre-screen,
        // score doesn't play any role in deciding as of now.

        if(preScreenRequest.isPass() != null){
            JobPostWorkflowStatus status;
            if(preScreenRequest.isPass()) {
                status = JobPostWorkflowStatus.find.where().eq("statusId", ServerConstants.JWF_STATUS_PRESCREEN_COMPLETED).findUnique();
            } else {
                status = JobPostWorkflowStatus.find.where().eq("statusId", ServerConstants.JWF_STATUS_PRESCREEN_FAILED).findUnique();
            }
            jobPostWorkflowNew.setStatus(status);
            jobPostWorkflowNew.update();
        } else {
            // here code will come to judge candidate pre screen response solely based on score
        }


        // Now lets save all the individual responses for this current pre screen attempt
        for (PreScreenRequirement preScreenRequirement : preScreenRequirementList) {
            PreScreenResponse preScreenResponse = new PreScreenResponse();
            preScreenResponse.setPreScreenRequirement(preScreenRequirement);
            preScreenResponse.setPreScreenResult(preScreenResult);
            preScreenResponse.setUpdateTimestamp(new Timestamp(System.currentTimeMillis()));

            if(preScreenRequest.getPreScreenIdList().contains(preScreenRequirement.getPreScreenRequirementId())){
                preScreenResponse.setResponse(true);
            } else {
                preScreenResponse.setResponse(false);
            }
            preScreenResponse.save();
        }
        return "OK";
    }

    public static class LastActiveValue{
        public Integer lastActiveValueId;
        public String lastActiveValueName;
    }

    public static LastActiveValue getDateCluster(Long timeInMill) {

        Map<Integer, String> clusterLabel = new HashMap<>();
        clusterLabel.put(1, ServerConstants.ACTIVE_WITHIN_24_HOURS);
        clusterLabel.put(2, ServerConstants.ACTIVE_LAST_3_DAYS);
        clusterLabel.put(3, ServerConstants.ACTIVE_LAST_7_DAYS);
        clusterLabel.put(4, ServerConstants.ACTIVE_LAST_12_DAYS);
        clusterLabel.put(5, ServerConstants.ACTIVE_LAST_1_MONTH);
        clusterLabel.put(6, ServerConstants.ACTIVE_LAST_2_MONTHS);
        clusterLabel.put(7, ServerConstants.ACTIVE_BEYOND_2_MONTHS);

        Calendar cal = Calendar.getInstance();
        Calendar currentCal = Calendar.getInstance();
        cal.setTimeInMillis(timeInMill);

        LastActiveValue lastActiveValue = new LastActiveValue();
        int currentDay = currentCal.get(Calendar.DAY_OF_YEAR);
        int doyDiff = currentDay - cal.get(Calendar.DAY_OF_YEAR);

        if( doyDiff > 60) {
            lastActiveValue.lastActiveValueId = 7;
            lastActiveValue.lastActiveValueName = clusterLabel.get(7);
        } else if( doyDiff > 30) {
            lastActiveValue.lastActiveValueId = 6;
            lastActiveValue.lastActiveValueName = clusterLabel.get(6);
        } else if( doyDiff > 15) {
            lastActiveValue.lastActiveValueId = 5;
            lastActiveValue.lastActiveValueName = clusterLabel.get(5);
        } else if (doyDiff > 7) {
            lastActiveValue.lastActiveValueId = 4;
            lastActiveValue.lastActiveValueName = clusterLabel.get(4);
        } else if ( doyDiff > 3) {
            lastActiveValue.lastActiveValueId = 3;
            lastActiveValue.lastActiveValueName = clusterLabel.get(3);
        } else if ( doyDiff > 1) {
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

        Logger.info("candidateList size before latlng filter: "+candidateList.size());
        if (jobPostLocalityIdList == null || jobPostLocalityIdList.isEmpty()){
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

                if ((candidate.getLocality() == null || ! jobPostLocalityIdList.contains(candidate.getLocality().getLocalityId()))) {
                    if((candidate.getCandidateLocalityLat() != null && candidate.getCandidateLocalityLng()!= null)) {
                        candidateLat = candidate.getCandidateLocalityLat();
                        candidateLng = candidate.getCandidateLocalityLng();
                    } else {
                        if(shouldRemoveCandidate) filteredCandidateList.remove(candidate);
                        continue;
                    }
                } else {
                    candidateLat = candidate.getLocality().getLat();
                    candidateLng = candidate.getLocality().getLng();
                }

                for (Locality locality : jobPostLocalityList) {
                    double distance = MatchingEngineService.getDistanceFromCenter(
                            locality.getLat(),
                            locality.getLng(),
                            candidateLat,
                            candidateLng
                    );
                    Double searchRadius = ServerConstants.DEFAULT_MATCHING_ENGINE_RADIUS;
                    if(distanceRadius != null){
                        searchRadius = distanceRadius;
                    }
                    if (distance > searchRadius && candidate.getMatchedLocation() == null) {
                        localityIncludeCount ++;
                    } else {
                        matchedLocation.append(locality.getLocalityName() + " ("+ Util.RoundTo1Decimals(distance)+" KM) " );
                    }
                }

                if(localityIncludeCount == jobPostLocalityList.size()){
                    // candidate is not within req distance from any jobPost latlng
                    if(shouldRemoveCandidate) filteredCandidateList.remove(candidate);
                    localityIncludeCount = 0;
                }
                candidate.setMatchedLocation(matchedLocation.toString());
            }
        }
        Logger.info("candidateList size after latlng filter: "+filteredCandidateList.size());

        return filteredCandidateList;
    }

    private static Map<Long, CandidateExtraData> computeExtraData(List<Candidate> candidateList, JobPost jobPost) {
        SimpleDateFormat sfd = new SimpleDateFormat(ServerConstants.SDF_FORMAT_HH);

        if(candidateList.size() == 0) return null;
        // candidateId --> featureMap
        Map<Long, CandidateExtraData> candidateExtraDataMap = new LinkedHashMap<>();

        List<String> candidateUUIdList = new ArrayList<>();
        List<Long> candidateIdList = new ArrayList<>();

        for (Candidate candidate: candidateList) {
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
        for (JobApplication jobApplication: allJobApplication) {
            String jobApplicationMode;
            jobApplicationMap.put(jobApplication.getCandidate().getCandidateId(), sfd.format(jobApplication.getJobApplicationCreateTimeStamp()));
            if(jobApplication.getPartner() == null) {
                jobApplicationMode = "Self";
            } else {
                jobApplicationMode = String.valueOf(jobApplication.getPartner().getPartnerId());
            }
            jobApplicationModeMap.put(jobApplication.getCandidate().getCandidateId(), jobApplicationMode);
        }

        // prep candidate->jobApplication mapping to reduce lookup time to O(1)
        Map<Long, Integer> assessmentMap = new HashMap<>();
        for (CandidateAssessmentAttempt attempt: allAssessmentAttempt) {
            assessmentMap.put(attempt.getCandidate().getCandidateId(), attempt.getAttemptId());
        }

        String candidateListString = String.join("', '", candidateUUIdList);

        StringBuilder interactionQueryBuilder = new StringBuilder("select distinct objectauuid, creationtimestamp from interaction i " +
                " where i.objectauuid " +
                " in ('"+candidateListString+"') " +
                " and creationtimestamp = " +
                " (select max(creationtimestamp) from interaction where i.objectauuid = interaction.objectauuid) " +
                " order by creationTimestamp desc ");


        RawSql rawSql = RawSqlBuilder.parse(interactionQueryBuilder.toString())
                .tableAliasMapping("i", "interaction")
                .columnMapping("objectauuid", "objectAUUId")
                .columnMapping("creationtimestamp", "creationTimestamp")
                .create();


//      TODO: Optimization: It takes 4+ sec for query to return map/list of this constraint
        Logger.info("before interaction query: " + new Timestamp(System.currentTimeMillis()));
        Map<String, Interaction> lastActiveInteraction = Ebean.find(Interaction.class)
                .setRawSql(rawSql)
                .findMap("objectAUUId", String.class);

//        List<Interaction> interactionList = Ebean.find(Interaction.class)
//                .setRawSql(rawSql)
//                .findList();
//
//        Map<String, Interaction> lastActiveInteraction = new HashMap<>();
//        for (Interaction interaction: interactionList) {
//            lastActiveInteraction.put(interaction.getObjectAUUId(), interaction);
//        }
        Logger.info("after interaction query: " + new Timestamp(System.currentTimeMillis()));

        List<JobPostWorkflow> jobPostWorkflowList = JobPostWorkflow.find.where()
                .eq("status.statusId", ServerConstants.JWF_STATUS_SELECTED)
                .isNotNull("candidate")
                .in("candidate.candidateId", candidateIdList)
                .findList();

        Map<Long, JobPostWorkflow> candidateToJobPostWorkflowMap =  new HashMap<>();
        for(JobPostWorkflow jobPostWorkflow : jobPostWorkflowList) {
            candidateToJobPostWorkflowMap.put(jobPostWorkflow.getCandidate().getCandidateId(), jobPostWorkflow);
        }

        List<Interaction> allPreScreenCallAttemptInteractions = Interaction.find
                .where()
                .eq("interactionType", InteractionConstants.INTERACTION_TYPE_CANDIDATE_PRE_SCREEN_ATTEMPTED)
                .in("objectBUUId", candidateUUIdList)
                .orderBy()
                .desc("objectBUUId")
                .findList();
        Map<String, Integer> candidateWithPreScreenAttemptCountMap = new TreeMap<>();


        for(Interaction interaction: allPreScreenCallAttemptInteractions){
            Integer count = candidateWithPreScreenAttemptCountMap.get(interaction.getObjectBUUId());
            if(count == null) {
                count = 0;
            }
            count++;
            candidateWithPreScreenAttemptCountMap.put(interaction.getObjectBUUId(), count);
        }

        for (Candidate candidate: candidateList) {
            CandidateExtraData candidateExtraData = candidateExtraDataMap.get(candidate.getCandidateId());

            if( candidateExtraData == null) {
                candidateExtraData = new CandidateExtraData();

                // compute applied on
                candidateExtraData.setAppliedOn(jobApplicationMap.get(candidate.getCandidateId()));

                // compute last active on
                Interaction interactionsOfCandidate = lastActiveInteraction.get(candidate.getCandidateUUId());
                if(interactionsOfCandidate != null) {
                    candidateExtraData.setLastActive(getDateCluster(interactionsOfCandidate.getCreationTimestamp().getTime()));
                }

                // compute 'has attempted assessment' for this JobPost-JobRole, If yes then this contains assessmentId
                candidateExtraData.setAssessmentAttemptId(assessmentMap.get(candidate.getCandidateId()));

                if(candidateWithPreScreenAttemptCountMap != null && candidateWithPreScreenAttemptCountMap.size()>0){
                    candidateExtraData.setPreScreenCallAttemptCount(candidateWithPreScreenAttemptCountMap.get(candidate.getCandidateUUId()));
                }
                // other intelligent scoring will come here

                // Job Application Mode (Self/Partner).
                if(jobApplicationModeMap != null && jobApplicationModeMap.size() > 0) {
                    candidateExtraData.setJobApplicationMode(jobApplicationModeMap.get(candidate.getCandidateId()));
                }
                // 'Pre-screed selection timestamp' along with jobPostWorkflowId, uuid
                if(candidateToJobPostWorkflowMap != null && candidateToJobPostWorkflowMap.size() > 0) {
                    JobPostWorkflow jobPostWorkflow = candidateToJobPostWorkflowMap.get(candidate.getCandidateId());
                    if(jobPostWorkflow!= null){
                        candidateExtraData.setPreScreenSelectionTimeStamp(jobPostWorkflow.getCreationTimestamp());
                        candidateExtraData.setWorkflowId(jobPostWorkflow.getJobPostWorkflowId());
                        candidateExtraData.setWorkflowUUId(jobPostWorkflow.getJobPostWorkflowUUId());
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


        if(experienceId == null ){
            return null;
        } else if (experienceId == 1){
            experienceValue.minExperienceValue = 0;
            experienceValue.maxExperienceValue = 0; // in months
        } else if(experienceId == 2) {
            experienceValue.minExperienceValue = 6;
            experienceValue.maxExperienceValue = 24; // in months
        } else if(experienceId == 3) {
            experienceValue.minExperienceValue = 24;
            experienceValue.maxExperienceValue = 48; // in months
        } else if(experienceId == 4) {
            experienceValue.minExperienceValue = 48;
            experienceValue.maxExperienceValue = 72; // in months
        } else {
            return null;
        }

        return experienceValue;
    }

    private static JobPostWorkflow saveNewJobPostWorkflow(Long candidateId, Long jobPostId, JobPostWorkflow jobPostWorkflowOld) {
        JobPostWorkflowStatus status = JobPostWorkflowStatus.find.where().eq("statusId", ServerConstants.JWF_STATUS_PRESCREEN_ATTEMPTED).findUnique();
        JobPost jobPost = JobPost.find.where().eq("jobPostId", jobPostId).findUnique();
        Candidate candidate = Candidate.find.where().in("candidateId", candidateId).findUnique();

        // check if status is already selected or pre_screen_attempted, throw error if not
        if (jobPostWorkflowOld.getStatus().getStatusId() == ServerConstants.JWF_STATUS_PRESCREEN_ATTEMPTED
                || jobPostWorkflowOld.getStatus().getStatusId() == ServerConstants.JWF_STATUS_SELECTED) {
            // save new workflow with status pre_screen_attempted, later this obj status will change to pre_screen_completed
            jobPostWorkflowOld = new JobPostWorkflow();
            jobPostWorkflowOld.setJobPost(jobPost);
            jobPostWorkflowOld.setCandidate(candidate);
            jobPostWorkflowOld.setCreatedBy(session().get("sessionUsername"));
            jobPostWorkflowOld.setStatus(status);
            jobPostWorkflowOld.save();
            return jobPostWorkflowOld;
        } else {
            Logger.error("Error ! JobPostWorkflow status is not PRESCREEN_ATTEMPTED or SELECTED");
        }
        return null;
    }


    private static Query<Candidate> getFilteredQuery(Integer maxAge, Long minSalary, Long maxSalary, Integer gender, Long jobRoleId, Integer educationId, List<Integer> languageIdList, ExperienceValue experience) {
        Query<Candidate> query = Candidate.find.query();
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);

        // problem: all age is null/0 and dob is also null
        // select candidate falling under the specified age req
        if (maxAge != null && maxAge !=0 ) {
            int startYear = currentYear - maxAge;
            query = query
                    .where()
                    .isNotNull("candidateDOB")
                    .ge("candidateDOB", startYear + "-01-01").query();
        }

        // select candidate based on specific gender req, else pass
        if (gender != null && gender>=0 && gender != ServerConstants.GENDER_ANY) {
            query = query
                    .where()
                    .isNotNull("candidateGender")
                    .eq("candidateGender", gender).query();
        }

        // select candidate whose totalExperience falls under the req exp
        if (experience != null) {

            if(experience.minExperienceValue == 0) {
                query = query
                        .where()
                        .isNotNull("candidateTotalExperience")
                        .eq("candidateTotalExperience", experience.minExperienceValue).query();
            } else {
                query = query
                        .where()
                        .isNotNull("candidateTotalExperience")
                        .ge("candidateTotalExperience", experience.minExperienceValue).query();
            }
            if(experience.maxExperienceValue != 0) {
                query = query
                        .where()
                        .isNotNull("candidateTotalExperience")
                        .le("candidateTotalExperience", experience.maxExperienceValue).query();
            }
        }

        // select candidate w.r.t candidateLastWithdrawnSalary
        if (maxSalary != null && maxSalary != 0){
            query =  query
                    .where()
                    .isNotNull("candidateLastWithdrawnSalary")
                    .le("candidateLastWithdrawnSalary", maxSalary)
                    .query();
        } else if (minSalary != null && minSalary != 0) {
            query =  query
                    .where()
                    .isNotNull("candidateLastWithdrawnSalary")
                    .le("candidateLastWithdrawnSalary", minSalary)
                    .query();
        }
        // select candidate w.r.t language
        if (languageIdList != null && languageIdList.size() > 0) {
            query =  query.select("*").fetch("languageKnownList")
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
        if (educationId != null && educationId != 0) {
            query = query.select("*").fetch("candidateEducation")
                    .where()
                    .isNotNull("candidateEducation")
                    .eq("candidateEducation.education.educationId", educationId)
                    .query();
        }

        // should be an active candidate
        query = query.select("*").fetch("candidateprofilestatus")
                .where()
                .eq("candidateprofilestatus.profileStatusId", ServerConstants.CANDIDATE_STATE_ACTIVE)
                .query();

        return query;
    }

    private static Map<Long, CandidateExtraData> computeExtraDataForRecruiterSearchResult(List<Candidate> candidateList) {

        if(candidateList.size() == 0) return null;
        // candidateId --> featureMap
        Map<Long, CandidateExtraData> candidateExtraDataMap = new LinkedHashMap<>();

        List<String> candidateUUIdList = new ArrayList<>();
        List<Long> candidateIdList = new ArrayList<>();

        for (Candidate candidate: candidateList) {
            candidateUUIdList.add(candidate.getCandidateUUId());
            candidateIdList.add(candidate.getCandidateId());
        }

        String candidateListString = String.join("', '", candidateUUIdList);

        Map<String, Interaction> lastActiveInteraction= Ebean.find(Interaction.class)
                .setRawSql(getRawSqlForInteraction(candidateListString))
                .findMap("objectAUUId", String.class);

        for (Candidate candidate: candidateList) {
            CandidateExtraData candidateExtraData = candidateExtraDataMap.get(candidate.getCandidateId());

            if( candidateExtraData == null) {
                candidateExtraData = new CandidateExtraData();

                // compute last active on
                Interaction interactionsOfCandidate = lastActiveInteraction.get(candidate.getCandidateUUId());
                if(interactionsOfCandidate != null) {
                    candidateExtraData.setLastActive(getDateCluster(interactionsOfCandidate.getCreationTimestamp().getTime()));
                }
                // other intelligent scoring will come here
            }

            candidateExtraDataMap.put(candidate.getCandidateId(), candidateExtraData);
        }

        return candidateExtraDataMap;
    }

    private static RawSql getRawSqlForInteraction(String candidateListString){
        //      TODO: Optimization: It takes 4+ sec for query to return map/list for this constraint, prev implementation was faster

        StringBuilder interactionQueryBuilder = new StringBuilder("select distinct objectauuid, creationtimestamp from interaction i " +
                " where i.objectauuid " +
                " in ('"+candidateListString+"') " +
                " and creationtimestamp = " +
                " (select max(creationtimestamp) from interaction where i.objectauuid = interaction.objectauuid) " +
                " order by creationTimestamp desc ");


        RawSql rawSql = RawSqlBuilder.parse(interactionQueryBuilder.toString())
                .tableAliasMapping("i", "interaction")
                .columnMapping("objectauuid", "objectAUUId")
                .columnMapping("creationtimestamp", "creationTimestamp")
                .create();

        return rawSql;
    }


}
