package controllers.businessLogic.JobWorkflow;

import api.InteractionConstants;
import api.ServerConstants;
import api.http.httpRequest.SelectedCandidateRequest;
import api.http.httpResponse.CandidateMatchingJobPost;
import com.avaje.ebean.*;
import controllers.businessLogic.MatchingEngineService;
import models.entity.Candidate;
import models.entity.Interaction;
import models.entity.JobPost;
import models.entity.OM.CandidateAssessmentAttempt;
import models.entity.OM.JobApplication;
import models.entity.OM.JobPostLanguageRequirement;
import models.entity.OM.JobPostToLocality;
import models.entity.Static.Locality;
import play.Logger;

import java.text.SimpleDateFormat;
import java.util.*;

import static com.avaje.ebean.Expr.eq;
import static play.libs.Json.toJson;


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
    public static Map<Long, CandidateMatchingJobPost> getMatchingCandidate(Long jobPostId,
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
        Map<Long, CandidateMatchingJobPost> matchedCandidateMap = new LinkedHashMap<>();
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);

        JobPost jobPost = JobPost.find.where().eq("jobPostId", jobPostId).findUnique();

        // geDurationFromExperience returns minExperience req. (in Months)
        ExperienceValue experience = getDurationFromExperience(experienceId);

        // get jobrolepref for candidate

        Query<Candidate> query = Candidate.find.query();

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

        List<Candidate> candidateList = filterByLatLngOrHomeLocality(query.findList(), jobPostLocalityIdList);

        Map<Long, Map<CandidateMatchingJobPost.FEATURE, Object>> allFeature = computeFeatureMap(candidateList, jobPost);

        for (Candidate candidate : candidateList) {
            CandidateMatchingJobPost candidateMatchingJobPost = new CandidateMatchingJobPost();
            candidateMatchingJobPost.setCandidate(candidate);
            candidateMatchingJobPost.setFeatureMap(allFeature.get(candidate.getCandidateId()));
            matchedCandidateMap.put(candidate.getCandidateId(), candidateMatchingJobPost);
        }

        return matchedCandidateMap;
    }

    /**
     * @param jobPostId
     * Prepare params and calls getMatchingCandidate
     */
    public static Map<Long, CandidateMatchingJobPost> getMatchingCandidate(Long jobPostId)
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

                if ((candidate.getLocality() == null || ! jobPostLocalityIdList.contains(candidate.getLocality().getLocalityId()))) {

                    if((candidate.getCandidateLocalityLat() != null && candidate.getCandidateLocalityLng()!= null)){
                        // is candidate within x km from any of the jobpost locality
                        for (Locality locality : localityList) {
                            if (MatchingEngineService.getDistanceFromCenter(
                                    locality.getLat(),
                                    locality.getLng(),
                                    candidate.getCandidateLocalityLat(),
                                    candidate.getCandidateLocalityLng()
                            ) > ServerConstants.DEFAULT_MATCHING_ENGINE_RADIUS) {
                                // candidate is not within req distance from jobPost latlng
                                filteredCandidateList.remove(candidate);
                                break;
                            }
                        }
                    } else {
                        filteredCandidateList.remove(candidate);
                    }
                }
            }
        }

        return filteredCandidateList;
    }

    private static Map<Long, Map<CandidateMatchingJobPost.FEATURE, Object>> computeFeatureMap(List<Candidate> candidateList, JobPost jobPost) {
        SimpleDateFormat sfd = new SimpleDateFormat(ServerConstants.SDF_FORMAT_HH);

        // candidateId --> featureMap
        Map<Long, Map<CandidateMatchingJobPost.FEATURE, Object>> allFeature = new LinkedHashMap<>();

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
        Map<Long, Object> jobApplicationMap = new HashMap<>();
        for (JobApplication jobApplication: allJobApplication) {
            jobApplicationMap.put(jobApplication.getCandidate().getCandidateId(), sfd.format(jobApplication.getJobApplicationCreateTimeStamp()));
        }

        // prep candidate->jobApplication mapping to reduce lookup time to O(1)
        Map<Long, Object> assessmentMap = new HashMap<>();
        for (CandidateAssessmentAttempt attempt: allAssessmentAttempt) {
            assessmentMap.put(attempt.getCandidate().getCandidateId(), attempt.getAttemptId());
        }


        // query all interactions of all candidate and order the list by most recent interaction on top
        // will be used for different types of inference
        List<Interaction> allInteractions = Interaction.find.where()
                .in("objectAUUId", candidateUUIdList)
                .setDistinct(true)
                .orderBy().desc("creationTimestamp")
                .findList();

        // linked hash map maintain the insertion order
        Map<String, ArrayList<Interaction>> objAUUIDToInteractions = new LinkedHashMap<>();

        for (Interaction interaction : allInteractions) {
            String objectAUUID = interaction.getObjectAUUId();

            ArrayList<Interaction> interactionsOfCandidate = objAUUIDToInteractions.get(objectAUUID);

            if (interactionsOfCandidate == null) {
                interactionsOfCandidate = new ArrayList<Interaction>();
                objAUUIDToInteractions.put(objectAUUID, interactionsOfCandidate);
            }
            interactionsOfCandidate.add(interaction);
        }


        for (Candidate candidate: candidateList) {
            Map<CandidateMatchingJobPost.FEATURE, Object> featureMap = allFeature.get(candidate.getCandidateId());

            if( featureMap == null) {
                featureMap = new LinkedHashMap<>();

                // compute applied on
                featureMap.put(CandidateMatchingJobPost.FEATURE.APPLIED_ON, jobApplicationMap.get(candidate.getCandidateId()));

                // compute last active on
                ArrayList<Interaction> interactionsOfCandidate = objAUUIDToInteractions.get(candidate.getCandidateUUId());
                if(interactionsOfCandidate != null) {
                    Interaction mostRecentInteraction = interactionsOfCandidate.get(0);
                    featureMap.put(CandidateMatchingJobPost.FEATURE.LAST_ACTIVE, sfd.format(mostRecentInteraction.getCreationTimestamp()));
                }

                // compute 'has attempted assessment' for this JobPost-JobRole, If yes then this contains assessmentId
                featureMap.put(CandidateMatchingJobPost.FEATURE.ASSESSMENT_ATTEMPT_ID, assessmentMap.get(candidate.getCandidateId()));

                // other intelligent scoring will come here
            }

            allFeature.put(candidate.getCandidateId(), featureMap);
        }

        return allFeature;
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

    public static Object saveSelectedCandidates(List<Long> selectedCandidateIdList) {
        List<Candidate> selectedCandidateList = Candidate.find.where().in("candidateId", selectedCandidateIdList).findList();
        Logger.info("SelectedCandidateList" + toJson(selectedCandidateList ));
        return selectedCandidateList;
    }

    private static class ExperienceValue {
        int minExperienceValue;
        int maxExperienceValue;
    }
}
