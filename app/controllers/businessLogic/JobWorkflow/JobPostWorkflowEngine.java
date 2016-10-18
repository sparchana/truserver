package controllers.businessLogic.JobWorkflow;

import api.ServerConstants;
import api.http.httpRequest.Workflow.SelectedCandidateRequest;
import api.http.httpResponse.CandidateExtraData;
import api.http.httpResponse.CandidateWorkflowData;
import api.http.httpResponse.Workflow.WorkflowResponse;
import com.avaje.ebean.*;
import controllers.businessLogic.MatchingEngineService;
import models.entity.Candidate;
import models.entity.Interaction;
import models.entity.JobPost;
import models.entity.OM.*;
import models.entity.Static.JobPostWorkflowStatus;
import models.entity.Static.Locality;
import models.util.Util;
import play.Logger;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.*;

import static play.mvc.Controller.session;

/**
 * Created by zero on 4/10/16.
 */
public class JobPostWorkflowEngine {

    /**
     *
     *  @param jobPostId  match candidates for this jobPost
     *  @param minAge  min age criteria to be taken into consideration while matching
     *  @param maxAge  max range criteria to be taken into consideration while matching
     *  @param gender  gender criteria to be taken into consideration while matching
     *  @param experienceId experience duration to be taken into consideration while matching
     *  @param jobPostLocalityIdList  candidates to be matched within x Km of any of the provided locality
     *  @param languageIdList  candidate to be matched for any of this language. Output contains the
     *                       indication to show matching & non-matching language
     *
     *
    */
    public static Map<Long, CandidateWorkflowData> getMatchingCandidate(Long jobPostId,
                                                                        Integer minAge,
                                                                        Integer maxAge,
                                                                        Long minSalary,
                                                                        Long maxSalary,
                                                                        Integer gender,
                                                                        Integer experienceId,
                                                                        Long jobRoleId,
                                                                        Integer educationId,
                                                                        List<Long> jobPostLocalityIdList,
                                                                        List<Integer> languageIdList)
    {
        Map<Long, CandidateWorkflowData> matchedCandidateMap = new LinkedHashMap<>();

        JobPost jobPost = JobPost.find.where().eq("jobPostId", jobPostId).findUnique();

        // geDurationFromExperience returns minExperience req. (in Months)
        ExperienceValue experience = getDurationFromExperience(experienceId);

        Query<Candidate> query;

        // query with the passed filter values and return Query<candidate>
        query = getFilteredQuery(minAge, maxAge, minSalary, maxSalary,gender, jobRoleId, educationId, languageIdList, experience);

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

        List<Candidate> candidateList = filterByLatLngOrHomeLocality(query.findList(), jobPostLocalityIdList);

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

    private static Query<Candidate> getFilteredQuery(Integer minAge, Integer maxAge, Long minSalary, Long maxSalary, Integer gender, Long jobRoleId, Integer educationId, List<Integer> languageIdList, ExperienceValue experience) {
        Query<Candidate> query = Candidate.find.query();
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);

        // problem: all age is null/0 and dob is also null
        // select candidate falling under the specified age req
        if (minAge != null) {
            int endYear = currentYear - minAge;
            query = query
                    .where()
                    .isNotNull("candidateDOB")
                    .le("candidateDOB", endYear + "-01-01").query();
        }
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

    /**
     * @param jobPostId
     * Prepare params and calls getMatchingCandidate
     */
    public static Map<Long, CandidateWorkflowData> getMatchingCandidate(Long jobPostId)
    {
        // prep params for a jobPost
        JobPost jobPost = JobPost.find.where().eq("jobPostId", jobPostId).findUnique();

        Integer minAge = jobPost.getJobPostMinAge();
        Integer maxAge = jobPost.getJobPostMaxAge();
        Long minSalary = jobPost.getJobPostMinSalary();
        Long maxSalary = jobPost.getJobPostMaxSalary();
        Integer gender = jobPost.getGender();

        Long jobRoleId = jobPost.getJobRole().getJobRoleId();

        // geDurationFromExperience returns minExperience req. (in Months)
        Integer experienceId = jobPost.getJobPostExperience() != null ? jobPost.getJobPostExperience().getExperienceId(): null;

        Integer educationId = jobPost.getJobPostEducation() != null ? jobPost.getJobPostEducation().getEducationId() : null;

        List<JobPostToLocality> jobPostToLocalityList = jobPost.getJobPostToLocalityList();
        List<Long> localityIdList = new ArrayList<>();
        for (JobPostToLocality jobPostToLocality: jobPostToLocalityList) {
            localityIdList.add(jobPostToLocality.getLocality().getLocalityId());
        }

        List<JobPostLanguageRequirement> languageRequirements = jobPost.getJobPostLanguageRequirement();
        List<Integer> languageIdList = new ArrayList<>();
        for (JobPostLanguageRequirement requirement : languageRequirements) {
            languageIdList.add(requirement.getLanguage().getLanguageId());
        }

        Logger.info(" minAge : "+minAge + " - " + " maxAge : "+maxAge + " - " + " minSalary : "+minSalary + " - " + " maxSalary : "+maxSalary + " - " + " gender : "+gender + " - " + " experienceId : "+experienceId + " - " + " localityIdList : "+localityIdList + " - " + " languageIdList: "+languageIdList);
        // call master method
        return getMatchingCandidate(jobPostId, minAge, maxAge, minSalary, maxSalary, gender, experienceId, jobRoleId, educationId, localityIdList, languageIdList);
    }


    private static List<Candidate> filterByLatLngOrHomeLocality(List<Candidate> candidateList, List<Long> jobPostLocalityIdList) {
        List<Candidate> filteredCandidateList = new ArrayList<>();

        if (jobPostLocalityIdList == null){
            return filteredCandidateList;
        }

        List<Locality> localityList = Locality.find.where().in("localityId", jobPostLocalityIdList).findList();

        if (jobPostLocalityIdList.size() > 0) {
            filteredCandidateList.addAll(candidateList);
            for (Candidate candidate : candidateList) {

                // candidate home locality matches with the job post locality

                StringBuilder matchedLocation = new StringBuilder();
                if ((candidate.getLocality() == null || ! jobPostLocalityIdList.contains(candidate.getLocality().getLocalityId()))) {

                    if((candidate.getCandidateLocalityLat() != null && candidate.getCandidateLocalityLng()!= null)){
                        // is candidate within x km from any of the jobpost locality
                        for (Locality locality : localityList) {
                            double distance = MatchingEngineService.getDistanceFromCenter(
                                    locality.getLat(),
                                    locality.getLng(),
                                    candidate.getCandidateLocalityLat(),
                                    candidate.getCandidateLocalityLng()
                            );
                            if (distance > ServerConstants.DEFAULT_MATCHING_ENGINE_RADIUS) {
                                // candidate is not within req distance from jobPost latlng
                                filteredCandidateList.remove(candidate);
                                break;
                            } else {
                                matchedLocation.append(locality.getLocalityName() + "("+ Util.RoundTo1Decimals(distance)+" KM) " );
                            }
                        }
                    } else {
                        filteredCandidateList.remove(candidate);
                    }
                }
                candidate.setMatchedLocation(matchedLocation.toString());
            }
        }

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
        for (JobApplication jobApplication: allJobApplication) {
            jobApplicationMap.put(jobApplication.getCandidate().getCandidateId(), sfd.format(jobApplication.getJobApplicationCreateTimeStamp()));
        }

        // prep candidate->jobApplication mapping to reduce lookup time to O(1)
        Map<Long, Integer> assessmentMap = new HashMap<>();
        for (CandidateAssessmentAttempt attempt: allAssessmentAttempt) {
            assessmentMap.put(attempt.getCandidate().getCandidateId(), attempt.getAttemptId());
        }

        String candidateListString = String.join("', '", candidateUUIdList);

        Logger.info("before interaction query: " + new Timestamp(System.currentTimeMillis()));
        Map<String, Interaction> lastActiveInteraction= Ebean.find(Interaction.class)
                .setRawSql(getRawSqlForInteraction(candidateListString))
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

                // other intelligent scoring will come here
            }

            candidateExtraDataMap.put(candidate.getCandidateId(), candidateExtraData);
        }

        return candidateExtraDataMap;
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
            }
        }

        response.setStatus(WorkflowResponse.STATUS.SUCCESS);
        response.setMessage("Selection completed successfully.");
        response.setRedirectUrl("/support/workflow/");
        response.setNextView("pre_screen_view");

        return response;
    }

    public static Map<Long, CandidateWorkflowData> getSelectedCandidates(Long jobPostId) {

        List<JobPostWorkflow> jobPostWorkflowList = JobPostWorkflow.find
                .where()
                .eq("job_post_id", jobPostId)
                .eq("status_id", ServerConstants.JWF_STATUS_SELECTED)
                .setDistinct(true)
                .orderBy().desc("creation_timestamp").findList();

        List<Candidate> candidateList = new ArrayList<>();

        Map<Long, CandidateWorkflowData> selectedCandidateMap = new LinkedHashMap<>();

        // until view is not available over this table, this loop get the distinct candidate who
        // got selected for a job post recently
        for( JobPostWorkflow jpwf: jobPostWorkflowList) {
            candidateList.add(jpwf.getCandidate());
        }

        Map<Long, CandidateExtraData> candidateExtraDataMap = computeExtraData(candidateList, JobPost.find.where().eq("jobPostId", jobPostId).findUnique());

        for ( Candidate candidate: candidateList) {
            CandidateWorkflowData candidateWorkflowData = new CandidateWorkflowData();
            candidateWorkflowData.setCandidate(candidate);
            candidateWorkflowData.setExtraData(candidateExtraDataMap.get(candidate.getCandidateId()));
            selectedCandidateMap.put(candidate.getCandidateId(), candidateWorkflowData);
        }

        return selectedCandidateMap;
    }

    private static class ExperienceValue {
        int minExperienceValue;
        int maxExperienceValue;
    }

    public static String getDateCluster(Long timeInMill) {
        String clusterLable;
        Calendar cal = Calendar.getInstance();
        Calendar currentCal = Calendar.getInstance();
        cal.setTimeInMillis(timeInMill);

        int currentDay = currentCal.get(Calendar.DAY_OF_YEAR);
        int doyDiff = currentDay - cal.get(Calendar.DAY_OF_YEAR);

        if( doyDiff > 60) {
            clusterLable = "Beyond two months";
        } else if( doyDiff > 30) {
            clusterLable = "Last two months";
        } else if( doyDiff > 15) {
            clusterLable = "Last one month";
        } else if (doyDiff > 7) {
            clusterLable = "Last 14 days";
        } else if ( doyDiff > 3) {
            clusterLable = "Last 7 days";
        } else if ( doyDiff > 1) {
            clusterLable = "Last 3 days";
        } else {
            clusterLable = "Within 24 hrs";
        }
        return clusterLable;
    }

    /**
     *
     * this is being used by the recruiter for searching candidates
     *
     *  @param minAge  min age criteria to be taken into consideration while matching
     *  @param maxAge  max range criteria to be taken into consideration while matching
     *  @param gender  gender criteria to be taken into consideration while matching
     *  @param experienceId experience duration to be taken into consideration while matching
     *  @param jobPostLocalityIdList  candidates to be matched within x Km of any of the provided locality
     *  @param languageIdList  candidate to be matched for any of this language. Output contains the
     *                       indication to show matching & non-matching language
     *
     *
     */
    public static Map<Long, CandidateWorkflowData> getCandidateForRecruiterSearch(Integer minAge,
                                                                        Integer maxAge,
                                                                        Long minSalary,
                                                                        Long maxSalary,
                                                                        Integer gender,
                                                                        Integer experienceId,
                                                                        Long jobRoleId,
                                                                        Integer educationId,
                                                                        List<Long> jobPostLocalityIdList,
                                                                        List<Integer> languageIdList)
    {
        Map<Long, CandidateWorkflowData> matchedCandidateMap = new LinkedHashMap<>();

        // geDurationFromExperience returns minExperience req. (in Months)
        ExperienceValue experience = getDurationFromExperience(experienceId);

        Query<Candidate> query = Candidate.find.query();

        //query candidate query with the filter params
        query = getFilteredQuery(minAge, maxAge, minSalary, maxSalary,gender, jobRoleId, educationId, languageIdList, experience);

        List<Candidate> candidateList = filterByLatLngOrHomeLocality(query.findList(), jobPostLocalityIdList);

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

        Logger.info("before interaction query: " + new Timestamp(System.currentTimeMillis()));
        Map<String, Interaction> lastActiveInteraction= Ebean.find(Interaction.class)
                .setRawSql(getRawSqlForInteraction(candidateListString))
                .findMap("objectAUUId", String.class);

        Logger.info("after interaction query: " + new Timestamp(System.currentTimeMillis()));

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


        Logger.info(interactionQueryBuilder.toString());

        RawSql rawSql = RawSqlBuilder.parse(interactionQueryBuilder.toString())
                .tableAliasMapping("i", "interaction")
                .columnMapping("objectauuid", "objectAUUId")
                .columnMapping("creationtimestamp", "creationTimestamp")
                .create();

        return rawSql;
    }

}
