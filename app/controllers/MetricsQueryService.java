package controllers;

import api.InteractionConstants;
import api.ServerConstants;
import com.avaje.ebean.Ebean;
import com.avaje.ebean.Query;
import com.avaje.ebean.SqlQuery;
import com.avaje.ebean.SqlRow;
import models.entity.Developer;
import models.entity.Static.LeadSource;
import org.apache.commons.lang3.time.DateUtils;
import play.Logger;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by archana on 7/8/16.
 */
public class MetricsQueryService
{
    /**
     * date format for metrics sheets
     */
    private static final String SDF_FORMAT_YYYYMMDD = "yyyy-MM-dd";
    private static final SimpleDateFormat sfd_yyyymmdd = new SimpleDateFormat(SDF_FORMAT_YYYYMMDD);

    private static final String SDF_FORMAT_DD_MMM = "dd-MMM";
    private static final SimpleDateFormat sfd_ddMMM = new SimpleDateFormat(SDF_FORMAT_DD_MMM);

    private static final String SDF_FORMAT_HH_SS = "HH-SS";
    private static final SimpleDateFormat sfd_hhss = new SimpleDateFormat(SDF_FORMAT_HH_SS);

    private static final GregorianCalendar gCal = new GregorianCalendar();

    public static Map<String, Map<Date, Map<Integer, Map<String, Object>>>> queryAndUpdateMetrics(List<String> metricCategories,
                                                                         Date startDate,
                                                                         Date endDate,
                                                                         boolean isWriteToSheet)
    {
        Map<String, Map<Date, Map<Integer, Map<String, Object>>>> metricToDateToHeaderToValues =
                new LinkedHashMap<String, Map<Date, Map <Integer, Map<String, Object>>>>();

        for (String metricCategory : metricCategories) {

            gCal.setTime(startDate);
            Date toDate = DateUtils.addDays(endDate, 1);
            if(endDate.after(new Date())){
                endDate = new Date();
            }
            Date metricDate;
            Date nextDate;

            String metricDateString;
            String nextDateString;

            Map<Date, Map<Integer, Map<String, Object>>> dateToHeaderToValueMap = new LinkedHashMap<>();
            Integer index = 1;

            while (gCal.getTime().before(toDate)) {

                metricDate = gCal.getTime();
                nextDate = DateUtils.addDays(metricDate, 1);

                metricDateString = sfd_yyyymmdd.format(metricDate);
                nextDateString = sfd_yyyymmdd.format(nextDate);

                Logger.info("Getting statistics from " + metricDate.toString() + " to " + nextDate.toString());

                if (metricCategory.equals(MetricsConstants.METRIC_INPUT_SUMMARY)) {

                    Map<Integer, Map<String, Object>> indexToHeaderToValueMap = new LinkedHashMap<>();
                    Map<String, Object> headerToValueMap = new LinkedHashMap<>();

                    indexToHeaderToValueMap.put(index, headerToValueMap);

                    getLeadsByChannel(indexToHeaderToValueMap, metricDateString, nextDateString, index);

                    getSignupsByChannel(indexToHeaderToValueMap, metricDateString, nextDateString, index);

                    getCandidatesByActivityCount(indexToHeaderToValueMap, metricDateString, nextDateString, index);

                    dateToHeaderToValueMap.put(metricDate, indexToHeaderToValueMap);

                } else if (metricCategory.equals(MetricsConstants.METRIC_INPUT_SUPPORT)) {

                    Map<Integer, Map<String, Object>> indexToHeaderToValueMap = new LinkedHashMap<>();
                    Map<String, Object> headerToValueMap = new LinkedHashMap<>();

                    indexToHeaderToValueMap.put(index, headerToValueMap);

                    getCandidatesBySupportAgents(indexToHeaderToValueMap, metricDateString, nextDateString, index);

                    dateToHeaderToValueMap.put(metricDate, indexToHeaderToValueMap);

                } else if (metricCategory.equals(MetricsConstants.METRIC_INPUT_LEAD_SOURCES)) {

                    Map<Integer, Map<String, Object>> indexToHeaderToValueMap = new LinkedHashMap<>();
                    Map<String, Object> headerToValueMap = new LinkedHashMap<>();

                    indexToHeaderToValueMap.put(index, headerToValueMap);

                    getLeadSources(indexToHeaderToValueMap, metricDateString, nextDateString, index);

                    dateToHeaderToValueMap.put(metricDate, indexToHeaderToValueMap);
                } else if (metricCategory.equals(MetricsConstants.METRIC_INPUT_ACTIVE_CANDIDATES)) {
                    Logger.info(" Querying active candidates ");
                    Map<Integer, Map<String, Object>> indexToHeaderToValueMap = new LinkedHashMap<>();

                    getActiveCandidates(indexToHeaderToValueMap, metricDateString, nextDateString, index);
                    dateToHeaderToValueMap.put(metricDate, indexToHeaderToValueMap);
                }

                gCal.add(Calendar.DAY_OF_YEAR, 1);
            }
            metricToDateToHeaderToValues.put(metricCategory, dateToHeaderToValueMap);
        }


        if(isWriteToSheet){
            // if channel set to true, write to google sheet
            Logger.info(" Results to write " + metricToDateToHeaderToValues.toString());
            TruJobsSheets.updateMetricsSheet(metricToDateToHeaderToValues);
        }

        return metricToDateToHeaderToValues;
    }

    private static void getLeadsByChannel(Map<Integer, Map<String, Object>> indexToHeaderToValueMap,
                                          String metricDate, String nextDate, Integer index)
    {
        // Build the query string
        StringBuilder leadQueryBuilder = new StringBuilder("select leadchannel, count(*) from lead where leadtype = 4");

        if (metricDate != null) {
            leadQueryBuilder.append(" and leadcreationtimestamp >= '" + metricDate + "' ");
        }

        if (nextDate != null) {
            leadQueryBuilder.append(" and  leadcreationtimestamp <= '" + nextDate + "' ");
        }

        leadQueryBuilder.append(" group by leadchannel");
        Logger.debug(" Lead Query: " + leadQueryBuilder.toString());

        // Execute Query
        SqlQuery leadSqlQuery = Ebean.createSqlQuery(leadQueryBuilder.toString());
        List<SqlRow> leadResults = leadSqlQuery.findList();
        Logger.debug (" Lead Results: " + leadResults.toString());

        // if we dint get any results, simply return
        if (leadResults == null) {
            Logger.error("Error: No leads found for date: " + metricDate);
            return;
        }

        // iterate and fine leads by channel
        Iterator<SqlRow> leadResultsItr = leadResults.listIterator();

        Integer websiteLeadsCount = 0;
        Integer knowlarityLeadsCount = 0;
        Integer supportLeadsCount = 0;
        Integer androidLeadsCount = 0;
        Integer partnerLeadsCount = 0;

        while (leadResultsItr.hasNext()) {
            SqlRow leadRow = leadResultsItr.next();
            if (leadRow.get("leadchannel").equals(ServerConstants.LEAD_CHANNEL_WEBSITE)) {
                websiteLeadsCount = leadRow.getInteger("count(*)");
            }
            else if (leadRow.get("leadchannel").equals(ServerConstants.LEAD_CHANNEL_KNOWLARITY)) {
                knowlarityLeadsCount = leadRow.getInteger("count(*)");
            }
            else if (leadRow.get("leadchannel").equals(ServerConstants.LEAD_CHANNEL_SUPPORT)) {
                supportLeadsCount = leadRow.getInteger("count(*)");
            }
            else if (leadRow.get("leadchannel").equals(ServerConstants.LEAD_CHANNEL_ANDROID)) {
                androidLeadsCount = leadRow.getInteger("count(*)");
            }
            else if (leadRow.get("leadchannel").equals(ServerConstants.LEAD_CHANNEL_PARTNER)) {
                partnerLeadsCount = leadRow.getInteger("count(*)");
            }
        }

        Integer totalLeads = websiteLeadsCount + knowlarityLeadsCount + supportLeadsCount + androidLeadsCount + partnerLeadsCount;

        Map<String, Object> headerToValueMap = indexToHeaderToValueMap.get(index);

        headerToValueMap.put(MetricsConstants.METRIC_HEADER_TOTAL_LEADS, totalLeads);
        headerToValueMap.put(MetricsConstants.METRIC_HEADER_WEBSITE_LEADS, websiteLeadsCount);
        headerToValueMap.put(MetricsConstants.METRIC_HEADER_KNOWLARITY_LEADS, knowlarityLeadsCount);
        headerToValueMap.put(MetricsConstants.METRIC_HEADER_ANDROID_LEADS, androidLeadsCount);
        headerToValueMap.put(MetricsConstants.METRIC_HEADER_PARTNER_LEADS, partnerLeadsCount);

        indexToHeaderToValueMap.put(index, headerToValueMap);

        Logger.debug("Results map after getLeadsByChannel: " + indexToHeaderToValueMap.toString());
    }

    private static void getSignupsByChannel(Map<Integer, Map<String, Object>> indexToHeaderToValueMap, String metricDate,
                                            String nextDate, Integer index)
    {
        // Build the query string
        StringBuilder interactionQueryBuilder =
                new StringBuilder("select interaction.interactionchannel, count(distinct(interaction.objectauuid)) from candidate " +
                                  "join interaction " +
                                  "on interaction.objectauuid=candidate.candidateuuid where " +
                                  "interaction.interactiontype IN(12,10) ");

        if (metricDate != null) {
            interactionQueryBuilder.append("and candidate.candidatecreatetimestamp >= '" + metricDate + "' ");
        }

        if (nextDate != null) {
            interactionQueryBuilder.append(" and  candidate.candidatecreatetimestamp <= '" + nextDate + "' ");
        }

        interactionQueryBuilder.append("group by interaction.interactionchannel");
        Logger.info(" Signup Query: " + interactionQueryBuilder.toString());

        // Execute Query
        SqlQuery interactionSignUpSqlQuery = Ebean.createSqlQuery(interactionQueryBuilder.toString());
        List<SqlRow> signupResults = interactionSignUpSqlQuery.findList();


        // if we dint get any results, simply return
        if (signupResults == null) {
            Logger.error("Error: No signups found for date: " + metricDate);
            return;
        }

        // iterate and fine leads by channel
        Iterator<SqlRow> signupResultsItr = signupResults.listIterator();

        Integer websiteSignupsCount = 0;
        Integer supportSignupsCount = 0;
        Integer androidSignupsCount = 0;
        Integer partnerSignupsCount = 0;

        Integer totalSignUpsCount;

        while (signupResultsItr.hasNext()) {
            SqlRow signupRow = signupResultsItr.next();
            if (signupRow.get("interactionchannel").equals(InteractionConstants.INTERACTION_CHANNEL_CANDIDATE_WEBSITE)) {
                websiteSignupsCount = signupRow.getInteger("count(distinct(interaction.objectauuid))");
            }
            else if (signupRow.get("interactionchannel").equals(InteractionConstants.INTERACTION_CHANNEL_CANDIDATE_ANDROID)) {
                androidSignupsCount = signupRow.getInteger("count(distinct(interaction.objectauuid))");
            }
            else if (signupRow.get("interactionchannel").equals(InteractionConstants.INTERACTION_CHANNEL_PARTNER_WEBSITE)) {
                partnerSignupsCount = signupRow.getInteger("count(distinct(interaction.objectauuid))");
            }
            else {
                supportSignupsCount = signupRow.getInteger("count(distinct(interaction.objectauuid))");
            }
        }

        totalSignUpsCount = websiteSignupsCount + supportSignupsCount + androidSignupsCount + partnerSignupsCount;

        Map<String, Object> headerToValueMap = indexToHeaderToValueMap.get(index);

        headerToValueMap.put(MetricsConstants.METRIC_HEADER_TOTAL_CANDIDATES, totalSignUpsCount);
        headerToValueMap.put(MetricsConstants.METRIC_HEADER_WEBSITE_CANDIDATES, websiteSignupsCount);
        headerToValueMap.put(MetricsConstants.METRIC_HEADER_SUPPORT_CANDIDATES, supportSignupsCount);
        headerToValueMap.put(MetricsConstants.METRIC_HEADER_ANDROID_CANDIDATES, androidSignupsCount);
        headerToValueMap.put(MetricsConstants.METRIC_HEADER_PARTNER_CANDIDATES, partnerSignupsCount);

        indexToHeaderToValueMap.put(index, headerToValueMap);

        Logger.info("Results map after getSignupsByChannel: " + indexToHeaderToValueMap.toString());
    }

    private static void getCandidatesByActivityCount(Map<Integer, Map<String, Object>> indexToHeaderToValueMap,
                                                     String metricDate, String nextDate, Integer index)
    {
        // Total OTP Verifications
        getOTPVerifications(indexToHeaderToValueMap, metricDate, nextDate, index);

        // Get self log-ins
        getSelfLogins(indexToHeaderToValueMap, metricDate, nextDate, index);

        // Basic profile Updates
        getSelfBasicProfileUpdates(indexToHeaderToValueMap, metricDate, nextDate, index);

        // Experience profile updates
        getSelfExperienceProfileUpdates(indexToHeaderToValueMap, metricDate, nextDate, index);

        // Education profile updates
        getSelfEducationProfileUpdates(indexToHeaderToValueMap, metricDate, nextDate, index);

        // Get Job Applications
        getJobApplications(indexToHeaderToValueMap, metricDate, nextDate, index);

        Logger.debug("Results map after getCandidatesByActivityCount: " + indexToHeaderToValueMap.toString());

    }

    private static void getOTPVerifications(Map<Integer, Map<String, Object>> indexToHeaderToValueMap, String metricDate,
                                            String nextDate, Integer index)
    {
        // Build the query string
        StringBuilder otpVerificationQueryBuilder =
                new StringBuilder("select count(*) from interaction " +
                        "where interactiontype = '16' ");

        if (metricDate != null) {
            otpVerificationQueryBuilder.append("and creationtimestamp  >= '" + metricDate + "' ");
        }

        if (nextDate != null) {
            otpVerificationQueryBuilder.append(" and  creationtimestamp <= '" + nextDate + "' ");
        }

        Logger.debug(" OTP Verification Query: " + otpVerificationQueryBuilder.toString());

        // Execute Query
        SqlQuery otpVerificationSqlQuery = Ebean.createSqlQuery(otpVerificationQueryBuilder.toString());
        List<SqlRow> otpVerificationResults = otpVerificationSqlQuery.findList();

        // if we dint get any results, simply return
        if (otpVerificationResults == null) {
            Logger.error("Error: No otp verification results found for date: " + metricDate);
            return;
        }

        Logger.debug(" OTP Verification results " + otpVerificationResults.toString());
        Map<String, Object> headerToValueMap = indexToHeaderToValueMap.get(index);

        headerToValueMap.put(MetricsConstants.METRIC_HEADER_OTP_VERIFICATIONS,
                otpVerificationResults.get(0).getInteger("count(*)"));

        indexToHeaderToValueMap.put(index, headerToValueMap);

    }

    private static void getSelfLogins(Map<Integer, Map<String, Object>> indexToHeaderToValueMap, String metricDate,
                                      String nextDate, Integer index)
    {
        // Build the query string
        StringBuilder loginQueryBuilder =
                new StringBuilder("select count(*) from interaction join candidate " +
                        "on interaction.objectauuid = candidate.candidateuuid " +
                        "where interactiontype = '9' ");

        if (metricDate != null) {
            loginQueryBuilder.append("and creationtimestamp  >= '" + metricDate + "' ");
        }

        if (nextDate != null) {
            loginQueryBuilder.append(" and  creationtimestamp <= '" + nextDate + "' ");
        }

        Logger.debug(" Login Query: " + loginQueryBuilder.toString());

        // Execute Query
        SqlQuery loginSqlQuery = Ebean.createSqlQuery(loginQueryBuilder.toString());
        List<SqlRow> loginResults = loginSqlQuery.findList();

        // if we dint get any results, simply return
        if (loginResults == null) {
            Logger.error("Error: No otp verification results found for date: " + metricDate);
            return;
        }

        Logger.debug(" Login results " + loginResults.toString());

        Map<String, Object> headerToValueMap = indexToHeaderToValueMap.get(index);

        headerToValueMap.put(MetricsConstants.METRIC_HEADER_LOGS_INS,
                loginResults.get(0).getInteger("count(*)"));

        indexToHeaderToValueMap.put(index, headerToValueMap);

    }

    private static void getSelfBasicProfileUpdates(Map<Integer, Map<String, Object>> indexToHeaderToValueMap, String metricDate,
                                                   String nextDate, Integer index)
    {
        // Build the query string
        StringBuilder basicProfileQueryBuilder =
                new StringBuilder("select count(*) from interaction " +
                        "where result ='Candidate Self Updated Basic Profile Info' ");

        if (metricDate != null) {
            basicProfileQueryBuilder.append("and creationtimestamp  >= '" + metricDate + "' ");
        }

        if (nextDate != null) {
            basicProfileQueryBuilder.append(" and  creationtimestamp <= '" + nextDate + "' ");
        }

        Logger.debug(" Basic Profile Query: " + basicProfileQueryBuilder.toString());

        // Execute Query
        SqlQuery basicProfileSqlQuery = Ebean.createSqlQuery(basicProfileQueryBuilder.toString());
        List<SqlRow> basicProfileResults = basicProfileSqlQuery.findList();

        // if we dint get any results, simply return
        if (basicProfileResults == null) {
            Logger.error("Error: No basic profile update results found for date: " + metricDate);
            return;
        }

        Map<String, Object> headerToValueMap = indexToHeaderToValueMap.get(index);

        Logger.debug(" Basic Profile Updates Results " + basicProfileResults.toString());

        headerToValueMap.put(MetricsConstants.METRIC_HEADER_BASIC_PROFILE_UPDATES,
                basicProfileResults.get(0).getInteger("count(*)"));

        indexToHeaderToValueMap.put(index, headerToValueMap);
    }

    private static void getSelfExperienceProfileUpdates(Map<Integer, Map<String, Object>> indexToHeaderToValueMap,
                                                        String metricDate, String nextDate, Integer index)
    {
        // Build the query string
        StringBuilder experienceProfileQueryBuilder =
                new StringBuilder("select count(*) from interaction " +
                        "where result ='Candidate Self Updated Skill Profile Info' ");

        if (metricDate != null) {
            experienceProfileQueryBuilder.append("and creationtimestamp  >= '" + metricDate + "' ");
        }

        if (nextDate != null) {
            experienceProfileQueryBuilder.append(" and  creationtimestamp <= '" + nextDate + "' ");
        }

        Logger.debug(" Experience Profile Query: " + experienceProfileQueryBuilder.toString());

        // Execute Query
        SqlQuery experienceProfileSqlQuery = Ebean.createSqlQuery(experienceProfileQueryBuilder.toString());
        List<SqlRow> experienceProfileResults = experienceProfileSqlQuery.findList();

        // if we dint get any results, simply return
        if (experienceProfileResults == null) {
            Logger.error("Error: No experience profile updates results found for date: " + metricDate);
            return;
        }

        Logger.debug(" Experience Profile Updates Results " + experienceProfileResults.toString());

        Map<String, Object> headerToValueMap = indexToHeaderToValueMap.get(index);

        headerToValueMap.put(MetricsConstants.METRIC_HEADER_EXP_PROFILE_UPDATES,
                experienceProfileResults.get(0).getInteger("count(*)"));

        indexToHeaderToValueMap.put(index, headerToValueMap);
    }

    private static void getSelfEducationProfileUpdates(Map<Integer, Map<String, Object>> indexToHeaderToValueMap,
                                                       String metricDate, String nextDate, Integer index)
    {
        // Build the query string
        StringBuilder educationProfileQueryBuilder =
                new StringBuilder("select count(*) from interaction " +
                        "where result ='Candidate Self Updated Education Profile Info' ");

        if (metricDate != null) {
            educationProfileQueryBuilder.append("and creationtimestamp  >= '" + metricDate + "' ");
        }

        if (nextDate != null) {
            educationProfileQueryBuilder.append(" and  creationtimestamp <= '" + nextDate + "' ");
        }

        Logger.debug(" Education Profile Query: " + educationProfileQueryBuilder.toString());

        // Execute Query
        SqlQuery educationProfileSqlQuery = Ebean.createSqlQuery(educationProfileQueryBuilder.toString());
        List<SqlRow> educationProfileResults = educationProfileSqlQuery.findList();

        // if we dint get any results, simply return
        if (educationProfileResults == null) {
            Logger.error("Error: No education profile updates results found for date: " + metricDate);
            return;
        }

        Logger.debug("Education Profile Updates Results " + educationProfileResults.toString());

        Map<String, Object> headerToValueMap = indexToHeaderToValueMap.get(index);

        headerToValueMap.put(MetricsConstants.METRIC_HEADER_EDU_PROFILE_UPDATES,
                educationProfileResults.get(0).getInteger("count(*)"));

        indexToHeaderToValueMap.put(index, headerToValueMap);
    }

    private static void getJobApplications(Map<Integer, Map<String, Object>> indexToHeaderToValueMap, String metricDate,
                                           String nextDate, Integer index)
    {
        // Build the query string
        StringBuilder jobAppQueryBuilder =
                new StringBuilder("select count(*) from interaction " +
                        "where interactiontype = '2' ");

        if (metricDate != null) {
            jobAppQueryBuilder.append("and creationtimestamp  >= '" + metricDate + "' ");
        }

        if (nextDate != null) {
            jobAppQueryBuilder.append(" and creationtimestamp <= '" + nextDate + "' ");
        }

        Logger.debug(" Job Applications Query: " + jobAppQueryBuilder.toString());

        // Execute Query
        SqlQuery jobAppSqlQuery = Ebean.createSqlQuery(jobAppQueryBuilder.toString());
        List<SqlRow> jobAppResults = jobAppSqlQuery.findList();

        // if we dint get any results, simply return
        if (jobAppResults == null) {
            Logger.error("Error: No job applciation updates results found for date: " + metricDate);
            return;
        }

        Logger.debug("Job Application Results " + jobAppResults.toString());
        Map<String, Object> headerToValueMap = indexToHeaderToValueMap.get(index);

        headerToValueMap.put(MetricsConstants.METRIC_HEADER_JOB_APPLICATIONS,
                jobAppResults.get(0).getInteger("count(*)"));

        indexToHeaderToValueMap.put(index, headerToValueMap);
    }

    private static void getCandidatesBySupportAgents (Map<Integer, Map<String, Object>> indexToHeaderToValueMap,
                                                      String metricDate, String nextDate, Integer index)
    {
        //TODO result needs to be replaced by type
        // Build the query string
        StringBuilder supportActivityQueryBuilder =
                new StringBuilder("select interaction.createdby, count(distinct(interaction.objectauuid)) from candidate " +
                        "join interaction on interaction.objectauuid=candidate.candidateuuid where interaction.interactiontype = 12 " +
                        " and interaction.interactionchannel = '4' ");

        if (metricDate != null) {
            supportActivityQueryBuilder.append("and candidate.candidatecreatetimestamp  >= '" + metricDate + "' ");
        }

        if (nextDate != null) {
            supportActivityQueryBuilder.append(" and candidate.candidatecreatetimestamp <= '" + nextDate + "' ");
        }

        supportActivityQueryBuilder.append("group by interaction.createdby ");

        Logger.info(" Support Activity Query: " + supportActivityQueryBuilder.toString());

        // Execute Query
        SqlQuery supportActivitySqlQuery = Ebean.createSqlQuery(supportActivityQueryBuilder.toString());
        List<SqlRow> supportActivityResults = supportActivitySqlQuery.findList();


        // if we dint get any results, simply return
        if (supportActivityResults == null) {
            Logger.error("Error: No support activity found for date: " + metricDate);
            return;
        }

        Logger.info("Support activity results: " + supportActivityResults.toString());

        Map<String, Object> headerToValueMap = indexToHeaderToValueMap.get(index);

        // iterate and find candidates added based on support agents
        Iterator<SqlRow> supportResultsItr = supportActivityResults.listIterator();

        Map<String, Object> agentNameToCandidateCount = new HashMap<String, Object>();

        while (supportResultsItr.hasNext()) {
            SqlRow supportRow = supportResultsItr.next();
            // TODO: change key to 'channel'
            agentNameToCandidateCount.put((String) supportRow.get("createdby"),
                     supportRow.get("count(distinct(interaction.objectauuid))"));
        }

        List<String> allDevNames = getAllDeveloperLogins();

        Iterator<String> devNamesItr = allDevNames.iterator();

        while (devNamesItr.hasNext()) {

            String devName = devNamesItr.next();
            Object count = agentNameToCandidateCount.get(devName);
            headerToValueMap.put(devName, count == null? 0 : count);

        }

        indexToHeaderToValueMap.put(index, headerToValueMap);

        Logger.info("Results map after getCandidatesBySupportAgents: " + indexToHeaderToValueMap.toString());
    }

    private static void getLeadSources(Map<Integer, Map<String, Object>> indexToHeaderToValueMap, String metricDate,
                                       String nextDate, Integer index)
    {
        // Build the query string
        StringBuilder leadSourcesQueryBuilder =
                new StringBuilder("select ls.leadsourcename, count(*) as c from lead l " +
                        " join leadsource ls " +
                        " on l.leadsourceid = ls.leadsourceid " +
                        " where l.leadchannel in (0,1,2,3,5) and leadtype = '4' ");

        if (metricDate != null) {
            leadSourcesQueryBuilder.append("and l.leadcreationtimestamp  >= '" + metricDate + "' ");
        }

        if (nextDate != null) {
            leadSourcesQueryBuilder.append(" and l.leadcreationtimestamp  <= '" + nextDate + "' ");
        }

        leadSourcesQueryBuilder.append("group by ls.leadsourcename order by c ");

        Logger.debug(" Lead Sources Query: " + leadSourcesQueryBuilder.toString());

        // Execute Query
        SqlQuery leadSourcesSqlQuery = Ebean.createSqlQuery(leadSourcesQueryBuilder.toString());
        List<SqlRow> leadSourcesResults = leadSourcesSqlQuery.findList();


        // if we dint get any results, simply return
        if (leadSourcesResults == null) {
            Logger.error("Error: No lead source data found for date: " + metricDate);
            return;
        }

        Logger.debug("Lead Sources results: " + leadSourcesResults.toString());

        // iterate and find candidates added based on support agents
        Iterator<SqlRow> leadSourcesResultsItr = leadSourcesResults.listIterator();

        Map<String, Object> sourceToLeadCount = new HashMap<String, Object>();

        while (leadSourcesResultsItr.hasNext()) {
            SqlRow supportRow = leadSourcesResultsItr.next();
            sourceToLeadCount.put((String) supportRow.get("leadsourcename"),
                    supportRow.get("c"));
        }

        List<String> allSourceNames = getAllLeadSources();

        Iterator<String> leadNamesItr = allSourceNames.iterator();

        Map<String, Object> headerToValueMap = indexToHeaderToValueMap.get(index);

        while (leadNamesItr.hasNext()) {

            String leadSourceName = leadNamesItr.next();
            Object count = sourceToLeadCount.get(leadSourceName);
            headerToValueMap.put(leadSourceName, count == null? 0 : count);

        }

        indexToHeaderToValueMap.put(index, headerToValueMap);

        Logger.debug("Results map after getLeadSources: " + indexToHeaderToValueMap.toString());
    }

    private static void getActiveCandidates(Map<Integer, Map<String, Object>> indexToHeaderToValueMap, String metricDate,
                                            String nextDate, Integer index)
    {
        //TODO change result to type. Include only candidate interaction to types, created by to channel(web, android)
        // Build the query string

        StringBuilder activeCandidatesQueryBuilder =
                new StringBuilder("select result, interactionchannel, interactiontype, creationtimestamp, candidatename, candidatemobile, candidateid, localityname, candidate.candidatecreatetimestamp " +
                        " from interaction join candidate " +
                        " on interaction.objectauuid = candidate.candidateuuid " +
                        " left join locality " +
                        " on locality.localityid = candidate.candidatehomelocality " +
                        " where interactionchannel in (1,2) ");

        if (metricDate != null) {
            activeCandidatesQueryBuilder.append("and creationtimestamp >= '" + metricDate + "' ");
        }

        if (nextDate != null) {
            activeCandidatesQueryBuilder.append(" and creationtimestamp <= '" + nextDate + "' ");
        }

        activeCandidatesQueryBuilder.append(" order by result ");

        Logger.debug(" Active Candidates Query: " + activeCandidatesQueryBuilder.toString());

        // Execute Query
        SqlQuery activeCandidatesSqlQuery = Ebean.createSqlQuery(activeCandidatesQueryBuilder.toString());
        List<SqlRow> activeCandidatesResults = activeCandidatesSqlQuery.findList();


        // if we dint get any results, simply return
        if (activeCandidatesResults == null) {
            Logger.error("Error: No active candidates data found for date: " + metricDate);
            return;
        }

        Logger.debug("Active Candidates results: " + activeCandidatesResults.toString());

        Map<String, Object> headerToValueMap;

        // iterate and find candidates added based on support agents
        Iterator<SqlRow> activeCandidatessResultsItr = activeCandidatesResults.listIterator();

        Integer index1 = 0;
        try {
            while (activeCandidatessResultsItr.hasNext()) {
                ++index1;
                headerToValueMap = new LinkedHashMap<String, Object>();

                SqlRow activeCandidateRow = activeCandidatessResultsItr.next();
                // TODO: change key to 'type, channel'
                headerToValueMap.put("Type", InteractionConstants.INTERACTION_TYPE_MAP.get((Integer) activeCandidateRow.get("interactiontype")));
                headerToValueMap.put("Activity", (String) activeCandidateRow.get("result"));
                if(Objects.equals(activeCandidateRow.get("interactionchannel").toString(), "1")){
                    headerToValueMap.put("Channel", "Web");
                } else{
                    headerToValueMap.put("Channel", "Android");
                }
                headerToValueMap.put("Candidate Name", (String) activeCandidateRow.get("candidatename"));
                headerToValueMap.put("Candidate Mobile", (String) activeCandidateRow.get("candidatemobile"));
                headerToValueMap.put("Candidate ID", (Long) activeCandidateRow.get("candidateid"));
                headerToValueMap.put("Candidate Home Locality", (String) activeCandidateRow.get("localityname"));
                headerToValueMap.put("Interaction Timestamp", sfd_hhss.format(activeCandidateRow.get("creationtimestamp")));
                headerToValueMap.put("Candidate Created Timestamp",
                        sfd_ddMMM.format((activeCandidateRow.get("candidatecreatetimestamp"))));

                indexToHeaderToValueMap.put(index1, headerToValueMap);
            }
        }
        catch (Exception pEx) {
            pEx.printStackTrace();
        }

        Logger.debug("Results map after getActiveCandidates: " + indexToHeaderToValueMap.toString());
    }

    private static List<String> getAllDeveloperLogins()
    {

        Query<Developer> query = Developer.find.query();

        query.select("developerName").orderBy("developerName");

        List<Developer> developerResults = query.findList();

        Iterator<Developer> devItr = developerResults.iterator();

        List<String> devNames = new ArrayList<String>();

        while (devItr.hasNext()) {
            devNames.add(devItr.next().getDeveloperName());
        }

        return devNames;

    }

    private static List<String> getAllLeadSources()
    {

        Query<LeadSource> query = LeadSource.find.query();

        query.select("leadSourceName").orderBy("leadSourceName");

        List<LeadSource> leadSourceResults = query.findList();

        Iterator<LeadSource> leasSourceItr = leadSourceResults.iterator();

        List<String> leadSourceNames = new ArrayList<String>();

        while (leasSourceItr.hasNext()) {
            leadSourceNames.add(leasSourceItr.next().getLeadSourceName());
        }

        return leadSourceNames;
    }
}
