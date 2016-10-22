package controllers.AnalyticsLogic;


import api.InteractionConstants;
import api.http.httpRequest.AnalyticsRequest;
import api.http.httpResponse.GlobalAnalyticsResponse;
import com.avaje.ebean.Ebean;
import com.avaje.ebean.Query;
import com.avaje.ebean.SqlQuery;
import com.avaje.ebean.SqlRow;
import models.entity.Candidate;
import models.entity.Interaction;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import play.Logger;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by zero on 12/7/16.
 */
public class GlobalAnalyticsService {

    public static final Map<String, Double> ACTIVITY_DURATION_TO_WEIGHT = new HashMap<String, Double>();

    private static final GregorianCalendar gCal = new GregorianCalendar();
    private static final String SDF_FORMAT_YYYYMMDD = "yyyy-MM-dd";
    private static final SimpleDateFormat sfd_yyyymmdd = new SimpleDateFormat(SDF_FORMAT_YYYYMMDD);

    private static final Map<Integer, Double> INTERACTION_TYPE_TO_WEIGHT = new HashMap<Integer, Double>();

    public static class ActivityScoreData {

        public Double getActivityScore() {
            return activityScore;
        }

        public void setActivityScore(Double activityScore) {
            this.activityScore = activityScore;
        }
        
        private Double activityScore = 0D;
        private Map<String, Map<Integer, Long>> durationToResultMap;

        private Map<Integer, Long> oneDayInteractionTypeToCount;
        private Map<Integer, Long> threeDaysInteractionTypeToCount;

        public Long getOneDayInteractionCount() {
            Long sum = 0L;
            for (Long count : oneDayInteractionTypeToCount.values()) {
                sum += count;
            }
            return sum;
        }


        public Long getThreeDaysInteractionCount() {
            Long sum = 0L;
            for (Long count : threeDaysInteractionTypeToCount.values()) {
                sum += count;
            }
            return sum;
        }

        public Long getOneWeekInteractionCount() {
            Long sum = 0L;
            for (Long count : oneWeekInteractionTypeToCount.values()) {
                sum += count;
            }
            return sum;
        }

        public Long getTwoWeeksInteractionCount() {
            Long sum = 0L;
            for (Long count : twoWeeksInteractionTypeToCount.values()) {
                sum += count;
            }
            return sum;
        }

        public Long getOneMonthInteractionCount() {
            Long sum = 0L;
            for (Long count : oneMonthInteractionTypeToCount.values()) {
                sum += count;
            }
            return sum;
        }

        public Long getTwoMonthsInteractionCount() {
            Long sum = 0L;
            for (Long count : twoMonthsInteractionTypeToCount.values()) {
                sum += count;
            }
            return sum;
        }

        public Long getThreeMonthsInteractionCount() {
            Long sum = 0L;
            for (Long count : threeMonthsInteractionTypeToCount.values()) {
                sum += count;
            }
            return sum;
        }

        private Map<Integer, Long> oneWeekInteractionTypeToCount;
        private Map<Integer, Long> twoWeeksInteractionTypeToCount;
        private Map<Integer, Long> oneMonthInteractionTypeToCount;
        private Map<Integer, Long> twoMonthsInteractionTypeToCount;
        private Map<Integer, Long> threeMonthsInteractionTypeToCount;


        public ActivityScoreData() {
            oneDayInteractionTypeToCount = new HashMap<Integer, Long>();
            threeDaysInteractionTypeToCount = new HashMap<Integer, Long>();
            oneWeekInteractionTypeToCount = new HashMap<Integer, Long>();
            twoWeeksInteractionTypeToCount = new HashMap<Integer, Long>();
            oneMonthInteractionTypeToCount = new HashMap<Integer, Long>();
            twoMonthsInteractionTypeToCount = new HashMap<Integer, Long>();
            threeMonthsInteractionTypeToCount = new HashMap<Integer, Long>();

            durationToResultMap = new HashMap<String, Map<Integer, Long>>();
            durationToResultMap.put("Last 24 hrs", oneDayInteractionTypeToCount);
            durationToResultMap.put("Last 3 Days", threeDaysInteractionTypeToCount);
            durationToResultMap.put("Last One Week", oneWeekInteractionTypeToCount);
            durationToResultMap.put("Last Two Weeks", twoWeeksInteractionTypeToCount);
            durationToResultMap.put("Last Month", oneMonthInteractionTypeToCount);
            durationToResultMap.put("Last Two Months", twoMonthsInteractionTypeToCount);
            durationToResultMap.put("Beyond Two Months", threeMonthsInteractionTypeToCount);
        }

        private void setScoreData(String activityDuration, Integer interactionType, Long interactionCount) {
            Map<Integer, Long> resultsMap = durationToResultMap.get(activityDuration);

            resultsMap.put(interactionType, interactionCount);
        }

        private void computeScores() {

            for (Map.Entry<String, Map<Integer, Long>> mapEntry : durationToResultMap.entrySet()) {

                String duration = mapEntry.getKey();
                Double durationWeight = ACTIVITY_DURATION_TO_WEIGHT.get(duration);
                Double score = 0D;

                for (Map.Entry<Integer, Long> childEntry : mapEntry.getValue().entrySet()) {
                    Double interactionTypeWeight = INTERACTION_TYPE_TO_WEIGHT.get(childEntry.getKey());
                    score += interactionTypeWeight * childEntry.getValue();
                }
                activityScore += score * durationWeight;
            }
        }
    }

    static {
        INTERACTION_TYPE_TO_WEIGHT.put(InteractionConstants.INTERACTION_TYPE_APPLIED_JOB, 0.25);
        INTERACTION_TYPE_TO_WEIGHT.put(InteractionConstants.INTERACTION_TYPE_CANDIDATE_PROFILE_UPDATE, 0.25);
        INTERACTION_TYPE_TO_WEIGHT.put(InteractionConstants.INTERACTION_TYPE_JOP_POST_VIEW, 0.1);
        INTERACTION_TYPE_TO_WEIGHT.put(InteractionConstants.INTERACTION_TYPE_SEARCH, 0.1);
        INTERACTION_TYPE_TO_WEIGHT.put(InteractionConstants.INTERACTION_TYPE_CANDIDATE_SIGN_UP, 0.1);
        INTERACTION_TYPE_TO_WEIGHT.put(InteractionConstants.INTERACTION_TYPE_CANDIDATE_PROFILE_CREATED, 0.1);
        INTERACTION_TYPE_TO_WEIGHT.put(InteractionConstants.INTERACTION_TYPE_CANDIDATE_LOG_IN , 0.05);
        INTERACTION_TYPE_TO_WEIGHT.put(InteractionConstants.INTERACTION_TYPE_CANDIDATE_ASSESSMENT_ATTEMPTED, 0.05);

        ACTIVITY_DURATION_TO_WEIGHT.put("Last 24 hrs", 0.4);
        ACTIVITY_DURATION_TO_WEIGHT.put("Last 3 Days", 0.25);
        ACTIVITY_DURATION_TO_WEIGHT.put("Last One Week", 0.15);
        ACTIVITY_DURATION_TO_WEIGHT.put("Last Two Weeks", 0.125);
        ACTIVITY_DURATION_TO_WEIGHT.put("Last Month", 0.075);
        ACTIVITY_DURATION_TO_WEIGHT.put("Last Two Months", 0.00);
        ACTIVITY_DURATION_TO_WEIGHT.put("Beyond Two Months", 0.00);
    }

    public static GlobalAnalyticsResponse getGlobalStatsService(AnalyticsRequest analyticsRequest){
        GlobalAnalyticsResponse globalAnalyticsResponse = new GlobalAnalyticsResponse();
        Query<Candidate> query = Candidate.find.query();

        if (analyticsRequest.getFromThisDate() != null) {
            query = query.where()
                    .ge("candidateCreateTimestamp", analyticsRequest.getFromThisDate())
                    .query();
        }
        if(analyticsRequest.getToThisDate() != null) {
            query = query.where()
                    .le("candidateCreateTimestamp", analyticsRequest.getToThisDate())
                    .query();
        }

        List<Candidate> candidateResponseList = query.findList();
        globalAnalyticsResponse.setTotalNumberOfCandidate(candidateResponseList.size());
        if (candidateResponseList.size() < 1){
            Logger.info("Search Response empty");
        }
        return globalAnalyticsResponse;
    }

    public static Map<String, ActivityScoreData> computeActivityScore() {

        Map<String, ActivityScoreData> candidateToActivityScoringData = new HashMap<String, ActivityScoreData>();

        for (String durationString : ACTIVITY_DURATION_TO_WEIGHT.keySet()) {

            String[] ts = getTimestampFromDuration(durationString);

            // Only interested in the below interaction types
            // INTERACTION_TYPE_APPLIED_JOB = 2; INTERACTION_TYPE_JOP_POST_VIEW = 8;
            // INTERACTION_TYPE_SEARCH = 7;
            // INTERACTION_TYPE_CANDIDATE_LOG_IN = 9; INTERACTION_TYPE_CANDIDATE_SIGN_UP = 10;
            // INTERACTION_TYPE_CANDIDATE_PROFILE_UPDATE = 11; INTERACTION_TYPE_CANDIDATE_PROFILE_CREATED = 12;
            // INTERACTION_TYPE_CANDIDATE_ASSESSMENT_ATTEMPTED = 33;
            String interactionTypesConstraint = StringUtils.join(INTERACTION_TYPE_TO_WEIGHT.keySet(), ",");

            StringBuilder interactionQueryBuilder =
                    new StringBuilder("select objectauuid, interactiontype, count(*) from interaction "
                            + " where creationtimestamp >= '" + ts[0] + "' "
                            + " and creationtimestamp <= '" + ts[1] + "' "
                            + " and interactiontype in (" + interactionTypesConstraint + ") "
                            + " group by objectauuid, interactiontype");

            SqlQuery interactionSignUpSqlQuery = Ebean.createSqlQuery(interactionQueryBuilder.toString());
            List<SqlRow> interactionResults = interactionSignUpSqlQuery.findList();

            for (SqlRow resultRow : interactionResults) {
                String objectauuid = (String) resultRow.get("objectauuid");
                Integer interactionType = (Integer) resultRow.get("interactiontype");
                Long count = (Long) resultRow.get("count(*)");

                ActivityScoreData scoreData = candidateToActivityScoringData.get(objectauuid);

                if (scoreData == null) {
                    scoreData = new ActivityScoreData();
                    candidateToActivityScoringData.put(objectauuid, scoreData);
                }

                scoreData.setScoreData(durationString, interactionType, count);
            }
        }

        for (ActivityScoreData data : candidateToActivityScoringData.values()) {
            data.computeScores();
        }

        return candidateToActivityScoringData;
    }

    private static String[] getTimestampFromDuration(String duration) {
        Date today = gCal.getTime();
        String fromDate = sfd_yyyymmdd.format(DateUtils.addDays(today, -1));
        String toDate = sfd_yyyymmdd.format(DateUtils.addDays(today, 1));

        switch (duration) {
            case "Last 24 hrs":
                fromDate = sfd_yyyymmdd.format(DateUtils.addDays(today, -1));
                toDate = sfd_yyyymmdd.format(DateUtils.addDays(today, 1));
                break;

            case "Last 3 Days":
                fromDate = sfd_yyyymmdd.format(DateUtils.addDays(today, -3));
                toDate = sfd_yyyymmdd.format(DateUtils.addDays(today, -1));
                break;

            case "Last One Week":
                fromDate = sfd_yyyymmdd.format(DateUtils.addDays(today, -7));
                toDate = sfd_yyyymmdd.format(DateUtils.addDays(today, -3));
                break;

            case "Last Two Weeks":
                fromDate = sfd_yyyymmdd.format(DateUtils.addDays(today, -14));
                toDate = sfd_yyyymmdd.format(DateUtils.addDays(today, -7));
                break;

            case "Last Month":
                fromDate = sfd_yyyymmdd.format(DateUtils.addDays(today, -30));
                toDate = sfd_yyyymmdd.format(DateUtils.addDays(today, -14));
                break;

            case "Last Two Months":
                fromDate = sfd_yyyymmdd.format(DateUtils.addDays(today, -60));
                toDate = sfd_yyyymmdd.format(DateUtils.addDays(today, -30));
                break;

            case "Beyond Two Months":
                fromDate = sfd_yyyymmdd.format(DateUtils.addDays(today, -3650));
                toDate = sfd_yyyymmdd.format(DateUtils.addDays(today, -60));
                break;
        }

        String[] dates = {fromDate, toDate};

        return dates;
    }
}
