package controllers;

import api.ServerConstants;
import api.http.httpRequest.SearchCandidateRequest;
import api.http.httpResponse.SearchCandidateResponse;
import com.avaje.ebean.Query;
import models.entity.Candidate;
import models.entity.Developer;
import models.entity.SupportUserSearchHistory;
import models.entity.SupportUserSearchPermissions;
import models.util.SmsUtil;
import play.Logger;
import play.api.Play;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by zero on 6/7/16.
 */
public class SupportSearchService {

    private static boolean isDevMode = Play.isDev(Play.current()) || Play.isTest(Play.current());

    private static final SimpleDateFormat sfd_yyyymmdd = new SimpleDateFormat(ServerConstants.SDF_FORMAT_YYYYMMDD);

    public static SearchCandidateResponse searchCandidateBySupport(SearchCandidateRequest searchCandidateRequest,
                                                                   Developer developer)
    {
        SearchCandidateResponse searchCandidateResponse = new SearchCandidateResponse();
        List<Candidate> candidateResponseList = new ArrayList<>();

        // Set limits to max values by default
        Integer queryLimit = 2147483647;
        Integer dailyLimit = 2147483647;
        Integer additionalPermissibleCount = 0;

        Timestamp today = new Timestamp(System.currentTimeMillis());
        String queryDateString = sfd_yyyymmdd.format(today);

        if (developer == null) {
            Logger.error(" Developer details not provided. Unable to proceed with search request");
            searchCandidateResponse.setStatus(SearchCandidateResponse.STATUS_FAILURE);
            return searchCandidateResponse;
        }

        SupportUserSearchPermissions searchPermissions = null;
        if (developer.getUserSearchPermissions() == null) {
            Logger.info("No search permissions mapped to user " + developer.getDeveloperName()
                    + ". Not applying search limitations");
        }
        else {
            // query allowed limit
            searchPermissions =
                    SupportUserSearchPermissions.find.where().eq("supportUserSearchPermissionsId",
                            developer.getUserSearchPermissions().getSupportUserSearchPermissionsId()).findUnique();
        }

        if (searchPermissions == null) {
            Logger.info("Something went wrong in fetching search persmissions for user " + developer.getDeveloperName()
                    + ". Not applying search limitations");
        }
        else {
            queryLimit = searchPermissions.getSingleQueryLimit();
            dailyLimit = searchPermissions.getDailyQueryLimit();
        }

        // query consumed limit
        List<SupportUserSearchHistory> todayHistoryList =
                SupportUserSearchHistory.find.where().eq("developer", developer)
                        .where().ge("searchDateTime", queryDateString).findList();
        Integer todaySearchedCount = 0;

        if (todayHistoryList != null && todayHistoryList.size() > 0) {
            for (SupportUserSearchHistory item : todayHistoryList) {
                todaySearchedCount += item.getDailySearchSum();
            }
        }

        Logger.info(" Support user " + developer.getDeveloperName() + " has used " + todaySearchedCount
                + " / " + dailyLimit + " of search limit");

        // compare to check if user has exhausted today's search limit
        if (todaySearchedCount >= dailyLimit) {
            Logger.warn(" Support user " + developer.getDeveloperName() + " has exhausted daily search limit of " + dailyLimit);
            searchCandidateResponse.setStatus(SearchCandidateResponse.STATUS_LIMIT_EXHAUSTED);
            if(!isDevMode) sendAlertSmsToAdmin(developer.getDeveloperName(), dailyLimit);
            return searchCandidateResponse;
        } else {
            additionalPermissibleCount = dailyLimit - todaySearchedCount;
        }

        // TODO:check searchCandidateRequest member variable for special char, null value
        List<Integer> jobInterestIdList = searchCandidateRequest.candidateJobInterest;
        List<Integer> localityPreferenceIdList = searchCandidateRequest.candidateLocality;
        List<Integer> idProofList = searchCandidateRequest.getIdProofs();

        Query<Candidate> query = Candidate.find.query();

        if (jobInterestIdList != null && !jobInterestIdList.isEmpty()) {
            query = query.select("*").fetch("jobPreferencesList")
                    .where()
                    .in("jobPreferencesList.jobRole.jobRoleId", jobInterestIdList)
                    .query();
        }
        if (localityPreferenceIdList != null && !localityPreferenceIdList.isEmpty()) {
            query = query.select("*").fetch("localityPreferenceList")
                    .where()
                    .in("localityPreferenceList.locality.localityId", localityPreferenceIdList)
                    .query();
        }
        if (idProofList != null && !idProofList.isEmpty()) {
            query = query.select("*").fetch("idProofReferenceList")
                    .where()
                    .in("idProofReferenceList.idProof.idProofId", idProofList)
                    .query();
        }
        if (searchCandidateRequest.getCandidateFirstName() != null && !searchCandidateRequest.getCandidateFirstName().isEmpty()) {
            query = query.where().like("candidateFirstName",
                    searchCandidateRequest.getCandidateFirstName() + "%").query();
        }

        if (searchCandidateRequest.getCandidateMobile() != null && !searchCandidateRequest.getCandidateMobile().isEmpty()) {
            query = query
                    .where()
                    .in("candidateMobile", searchCandidateRequest.getCandidateMobile())
                    .query();
        }
        if (searchCandidateRequest.getFromThisDate() != null) {
            query = query.where()
                    .ge("candidateCreateTimestamp", searchCandidateRequest.getFromThisDate())
                    .query();
        }
        if (searchCandidateRequest.getToThisDate() != null) {
            query = query.where()
                    .le("candidateCreateTimestamp", searchCandidateRequest.getToThisDate())
                    .query();
        }
        if (searchCandidateRequest.getLanguageKnownList() != null && !searchCandidateRequest.getLanguageKnownList().isEmpty()) {
            List<Integer> languageIdList = searchCandidateRequest.getLanguageKnownList();
            query = query.select("*").fetch("languageKnownList")
                    .where()
                    .in("languageKnownList.language.languageId", languageIdList)
                    .query();
        }

        // query only no. of rows allowed for this support user for today

        Integer rowLimit = additionalPermissibleCount < queryLimit ? additionalPermissibleCount : queryLimit;

        if (rowLimit > 0) {
            candidateResponseList = query.orderBy("candidateCreateTimestamp, candidateCreateTimestamp desc").setMaxRows(rowLimit).findList();
            if (candidateResponseList.size() < 1) {
                Logger.info("Search Response empty");
            }

            // Update daily searched records for this user.
            // Always create a new record, so as to keep track of all searches in a day
            SupportUserSearchHistory updatedSearchHistory = new SupportUserSearchHistory();

            updatedSearchHistory.setDeveloper(developer);
            updatedSearchHistory.setSearchDateTime(new Timestamp(System.currentTimeMillis()));
            updatedSearchHistory.setDailySearchSum(candidateResponseList.size());
            updatedSearchHistory.setSearchQuery(query.getGeneratedSql());
            updatedSearchHistory.save();

            searchCandidateResponse.setStatus(SearchCandidateResponse.STATUS_SUCCESS);
            searchCandidateResponse.setCandidateList(candidateResponseList);
        }

        return searchCandidateResponse;
    }

    public static void sendAlertSmsToAdmin(String userName, Integer limit)
    {
        String message = "TRUJOBS SUPPORT SEARCH ALERT: Support user " + userName
                + " has exhausted daily search limit of " + limit + " records";
        SmsUtil.sendSms(ServerConstants.devTeamMobile.get("Archana"), message);
        SmsUtil.sendSms(ServerConstants.devTeamMobile.get("Avishek"), message);
        SmsUtil.sendSms(ServerConstants.devTeamMobile.get("Chillu"), message);
    }
}
