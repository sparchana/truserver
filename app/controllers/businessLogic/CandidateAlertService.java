package controllers.businessLogic;

import api.ServerConstants;
import in.trujobs.proto.FetchCandidateAlertResponse;
import models.entity.Candidate;
import models.entity.JobPost;
import models.entity.OM.JobPreference;
import play.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * Use this service to fetch the most important alert that needs to be displayed to a candidate at any point
 *
 * If critical profile fields are incomplete, then alert message will prompt the candidate to complete profile
 * TODO Else, if candidate has any upcoming interviews, then alert message will remind the candidate about interview timing
 * TODO Else, if candidate has been selected for any job opening, then alert message will convey joining data
 * TODO Else, if candidate is deactivated for any reason, alert message will indicate the candidate to re-activate profile
 * Else, candidate gets a message conveying number of jobs in his/her locality and a prompt to browse and apply to jobs
 *
 */
public class CandidateAlertService {

    public static FetchCandidateAlertResponse getAlertForCandidate(String candidateMobile) {

        FetchCandidateAlertResponse.Builder fetchCandidateResponseBuilder = FetchCandidateAlertResponse.newBuilder();

        Candidate candidate = CandidateService.isCandidateExists(candidateMobile);
        if (candidate == null) {
            Logger.error("Candidate with mobile " + candidateMobile + " doesnt exist. No alerts applicable");
            return null;
        }

        // check the profile completion of candidate. if incomplete, we want push profile completion alert
        if (CandidateService.getP0FieldsCompletionPercent(candidate) < 1 ||
                CandidateService.getP1FieldsCompletionPercent(candidate) < 0.5) {
            fetchCandidateResponseBuilder.setAlertMessage(
                    "Uh ho! Looks like your profile is incomplete. Complete your profile now to find a job faster!!");
            fetchCandidateResponseBuilder.setAlertType(
                    FetchCandidateAlertResponse.Type.valueOf(FetchCandidateAlertResponse.Type.COMPLETE_PROFILE_VALUE));
        }
        else {

            // TODO check if the user has any interview schedule alerts

            // TODO check if the user has any joining date alerts

            // TODO check if this user has been intentionally deactivated, if so display corressponding message

            // if no alerts are applicable for this candidate, then display an info/engagement message that
            // mentions the number of new jobs posted since the user's last visit

            int jobsCount = 0;

            if (candidate.getCandidateLocalityLat() == null || candidate.getCandidateLocalityLng() == null) {
                Logger.error("Candidate with mobile " + candidateMobile + " doesnt have lat-long info");
                jobsCount = JobPost.find.all().size();
            } else {
                // fetch jobs near this candidate according to its jobPreference
                List<Long> jobPrefId = new ArrayList<>();
                for(JobPreference jobPreference: candidate.getJobPreferencesList()){
                    jobPrefId.add(jobPreference.getJobRole().getJobRoleId());
                }
                jobsCount = MatchingEngineService.fetchMatchingJobPostForLatLng(candidate.getCandidateLocalityLat(),
                        candidate.getCandidateLocalityLat(),
                        ServerConstants.DEFAULT_MATCHING_ENGINE_RADIUS, jobPrefId, ServerConstants.SORT_DEFAULT).size();
            }

            if (jobsCount > 0) {
                fetchCandidateResponseBuilder.setAlertMessage(
                        "Whoa! " + jobsCount + " new jobs available in your locality! Start applying now!!");
                fetchCandidateResponseBuilder.setAlertType(
                        FetchCandidateAlertResponse.Type.valueOf(FetchCandidateAlertResponse.Type.NEW_JOBS_IN_LOCALITY_VALUE));
            }
        }
        return fetchCandidateResponseBuilder.build();
    }
}
