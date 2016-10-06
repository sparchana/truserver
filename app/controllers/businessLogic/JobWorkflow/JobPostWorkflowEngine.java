package controllers.businessLogic.JobWorkflow;

import api.ServerConstants;
import api.http.httpResponse.CandidateMatchingJobPost;
import com.avaje.ebean.*;
import controllers.businessLogic.MatchingEngineService;
import models.entity.Candidate;
import models.entity.JobPost;
import models.entity.OM.JobPostLanguageRequirement;
import models.entity.OM.JobPostToLocality;
import models.entity.Static.Experience;
import models.entity.Static.Locality;
import org.h2.tools.Server;
import org.mockito.cglib.core.Local;
import play.Logger;

import java.util.*;

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
     *  @param experience  experience duration to be taken into consideration while matching
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
                                                                           Experience experience,
                                                                           List<Long> jobPostLocalityIdList,
                                                                           List<Integer> languageIdList)
    {
        Map<Long, CandidateMatchingJobPost> matchedCandidateList = new LinkedHashMap<>();
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);

        JobPost jobPost = JobPost.find.where().eq("jobPostId", jobPostId).findUnique();

        Long jobRoleId = jobPost.getJobRole().getJobRoleId();

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
        if (gender != null && gender != ServerConstants.GENDER_ANY) {
            query = query
                    .where()
                    .isNotNull("candidateGender")
                    .eq("candidateGender", gender).query();
        }

        // select candidate whose totalExperience falls under the req exp
        if (experience != null && experience.getExperienceId() != 5) {

            // geDurationFromExperience returns minExperience req. (in Months)
            Integer minExperience = getDurationFromExperience(experience);

            if(minExperience == 0) {
                query = query
                        .where()
                        .isNotNull("candidateTotalExperience")
                        .eq("candidateTotalExperience", minExperience).query();
            } else {
                query = query
                        .where()
                        .isNotNull("candidateTotalExperience")
                        .ge("candidateTotalExperience", minExperience).query();
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
                    .eq("jobPreferencesList.jobRole.jobRoleId", jobRoleId)
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

    private static List<Candidate> filterByLatLngOrHomeLocality(List<Candidate> candidateList, List<Long> jobPostLocalityIdList) {
        List<Candidate> filteredCandidate = new ArrayList<>();

        List<Locality> localityList = Locality.find.where().in("localityId", jobPostLocalityIdList).findList();

        if (jobPostLocalityIdList.size() > 0) {
            filteredCandidate.addAll(candidateList);
            for (Candidate candidate : candidateList) {

                // candidate home locality matches with the job post locality

                if ((candidate.getLocality() == null || ! jobPostLocalityIdList.contains(candidate.getLocality().getLocalityId()))
                        && (candidate.getCandidateLocalityLat() != null && candidate.getCandidateLocalityLng()!= null)) {

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
                        }
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
        Experience experience = jobPost.getJobPostExperience();

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

        Logger.info(" minAge : "+minAge + " - " + " maxAge : "+maxAge + " - " + " minSalary : "+minSalary + " - " + " maxSalary : "+maxSalary + " - " + " gender : "+gender + " - " + " experience : "+experience + " - " + " localityIdList : "+localityIdList + " - " + " languageIdList: "+languageIdList);
        // call master method
        return getMatchingCandidate(jobPostId, minAge, maxAge, minSalary, maxSalary, gender, experience, localityIdList, languageIdList);
    }

    private static int getDurationFromExperience(Experience experience) {

        // experience table should have a minValue column containing the minValue
        // which should be considered while computation

        int minExperienceValue = 0; // in months

        if(experience.getExperienceId() == 2) {
            minExperienceValue = 6;
        } else if(experience.getExperienceId() == 3) {
            minExperienceValue = 24;
        } else if(experience.getExperienceId() == 4) {
            minExperienceValue = 48;
        }

        return minExperienceValue;
    }
}
