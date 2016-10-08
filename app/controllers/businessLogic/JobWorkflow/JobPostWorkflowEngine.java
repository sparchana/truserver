package controllers.businessLogic.JobWorkflow;

import api.ServerConstants;
import api.http.httpResponse.CandidateMatchingJobPost;
import com.avaje.ebean.*;
import controllers.businessLogic.MatchingEngineService;
import models.entity.Candidate;
import models.entity.JobPost;
import models.entity.OM.JobPostLanguageRequirement;
import models.entity.OM.JobPostToLocality;
import models.entity.Static.Locality;
import play.Logger;

import java.util.*;


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
        Map<Long, CandidateMatchingJobPost> matchedCandidateList = new LinkedHashMap<>();
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
                if(experience.maxExperienceValue != 0) {
                    query = query
                            .where()
                            .isNotNull("candidateTotalExperience")
                            .le("candidateTotalExperience", experience.maxExperienceValue).query();
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
        if (educationId != null && educationId != 0) {
            query = query.select("*").fetch("candidateEducation")
                    .where()
                    .isNotNull("candidateEducation")
                    .eq("candidateEducation.education.educationId", educationId)
                    .query();
        }

        List<Candidate> candidateList = filterByLatLngOrHomeLocality(query.findList(), jobPostLocalityIdList);

        for (Candidate candidate : candidateList) {
            CandidateMatchingJobPost candidateMatchingJobPost = new CandidateMatchingJobPost();
            candidateMatchingJobPost.setCandidate(candidate);
            candidateMatchingJobPost.setFeatureMap(computeFeatureMap(candidate, jobPost));
            matchedCandidateList.put(candidate.getCandidateId(), candidateMatchingJobPost);
        }

        return matchedCandidateList;
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
        List<Candidate> filteredCandidate = new ArrayList<>();

        if (jobPostLocalityIdList == null){
            return filteredCandidate;
        }

        List<Locality> localityList = Locality.find.where().in("localityId", jobPostLocalityIdList).findList();

        if (jobPostLocalityIdList.size() > 0) {
            filteredCandidate.addAll(candidateList);
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
                                filteredCandidate.remove(candidate);
                                break;
                            }
                        }
                    } else {
                        filteredCandidate.remove(candidate);
                    }
                }
            }
        }

        return filteredCandidate;
    }

    private static Map<CandidateMatchingJobPost.FEATURE, String> computeFeatureMap(Candidate candidate, JobPost jobPost) {
        Map<CandidateMatchingJobPost.FEATURE, String> featureMap = new LinkedHashMap<>();

        return featureMap;
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

    private static class ExperienceValue {
        int minExperienceValue;
        int maxExperienceValue;
    }
}
