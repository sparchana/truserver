package controllers;

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
    private static final String SDF_FORMAT = "yyyy-MM-dd";
    private static final SimpleDateFormat sfd = new SimpleDateFormat(SDF_FORMAT);
    private static final GregorianCalendar gCal = new GregorianCalendar();

    public static Map<String, Map<Date, Map<String, Object>>> queryAndUpdateMetrics(List<String> metricCategories,
                                                                         Date startDate,
                                                                         Date endDate,
                                                                         boolean isWriteToSheet)
    {
        Map<String, Map<Date, Map<String, Object>>> metricToDateToHeaderToValues =
                new LinkedHashMap<String, Map<Date, Map<String, Object>>>();


        for (String metricCategory : metricCategories) {

            gCal.setTime(startDate);
            Date toDate = DateUtils.addDays(endDate, 1);
            if(endDate.after(new Date())){
                endDate = new Date();
            }
            Date metricDate;
            Date nextDate;
            Map<Date, Map<String, Object>> dateToHeaderToValueMap = new LinkedHashMap<>();

            while (gCal.getTime().before(toDate)) {

                metricDate = gCal.getTime();
                nextDate = DateUtils.addDays(metricDate, 1);

                Logger.info("Getting statistics from " + metricDate.toString() + " to " + nextDate.toString());

                if (metricCategory.equals(MetricsConstants.METRIC_INPUT_ALL)) {
                    Map<String, Object> headerToValueMap = new LinkedHashMap<>();
                    getLeadsByChannel(headerToValueMap, sfd.format(metricDate), sfd.format(nextDate));

                    getSignupsByChannel(headerToValueMap, sfd.format(metricDate), sfd.format(nextDate));

                    getCandidatesByActivityCount(headerToValueMap, sfd.format(metricDate), sfd.format(nextDate));

                    dateToHeaderToValueMap.put(metricDate, headerToValueMap);

                } else if (metricCategory.equals(MetricsConstants.METRIC_INPUT_SUPPORT)) {
                    Map<String, Object> headerToValueMap = new LinkedHashMap<>();

                    getCandidatesBySupportAgents(headerToValueMap, sfd.format(metricDate), sfd.format(nextDate));

                    dateToHeaderToValueMap.put(metricDate, headerToValueMap);

                } else if (metricCategory.equals(MetricsConstants.METRIC_INPUT_LEAD_SOURCES)) {
                    Map<String, Object> headerToValueMap = new LinkedHashMap<>();

                    getLeadSources(headerToValueMap, sfd.format(metricDate), sfd.format(nextDate));

                    dateToHeaderToValueMap.put(metricDate, headerToValueMap);
                }

                gCal.add(Calendar.DAY_OF_YEAR, 1);
            }
            metricToDateToHeaderToValues.put(metricCategory, dateToHeaderToValueMap);
        }


        if(isWriteToSheet){
            // if channel set to true, write to google sheet

            TruJobsSheets.updateMetricsSheet(metricToDateToHeaderToValues);
        }

        return metricToDateToHeaderToValues;
    }

    private static void getLeadsByChannel(Map<String, Object> headerToValueMap, String metricDate, String nextDate)
    {
        // Build the query string
        StringBuilder leadQueryBuilder = new StringBuilder("select leadchannel, count(*) from lead ");

        if (metricDate != null) {
            leadQueryBuilder.append("where leadcreationtimestamp >= '" + metricDate + "' ");
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
        }

        Integer totalLeads = websiteLeadsCount + knowlarityLeadsCount + supportLeadsCount;
        headerToValueMap.put(MetricsConstants.METRIC_HEADER_TOTAL_LEADS, totalLeads);

        headerToValueMap.put(MetricsConstants.METRIC_HEADER_WEBSITE_LEADS, websiteLeadsCount);
        headerToValueMap.put(MetricsConstants.METRIC_HEADER_KNOWLARITY_LEADS, knowlarityLeadsCount);

        Logger.debug("Results map after getLeadsByChannel: " + headerToValueMap.toString());
    }

    private static void getSignupsByChannel(Map<String, Object> headerToValueMap, String metricDate, String nextDate)
    {
        // Build the query string
        StringBuilder interactionQueryBuilder =
                new StringBuilder("select interaction.createdby, count(distinct(interaction.objectauuid)) from candidate " +
                                  "join interaction " +
                                  "on interaction.objectauuid=candidate.candidateuuid where " +
                                  "interaction.result like '%New Candidate Added%' ");

        if (metricDate != null) {
            interactionQueryBuilder.append("and candidate.candidatecreatetimestamp >= '" + metricDate + "' ");
        }

        if (nextDate != null) {
            interactionQueryBuilder.append(" and  candidate.candidatecreatetimestamp <= '" + nextDate + "' ");
        }

        interactionQueryBuilder.append("group by interaction.createdby");

        Logger.debug(" Signup Query: " + interactionQueryBuilder.toString());

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
        Integer totalSignUpsCount = 0;

        while (signupResultsItr.hasNext()) {
            SqlRow signupRow = signupResultsItr.next();
            if (signupRow.get("createdby").equals(ServerConstants.INTERACTION_CREATED_SELF)) {
                websiteSignupsCount = signupRow.getInteger("count(distinct(interaction.objectauuid))");
            }
            else {
                supportSignupsCount += signupRow.getInteger("count(distinct(interaction.objectauuid))");
            }
        }

        totalSignUpsCount = websiteSignupsCount + supportSignupsCount;
        headerToValueMap.put(MetricsConstants.METRIC_HEADER_TOTAL_CANDIDATES, totalSignUpsCount);

        headerToValueMap.put(MetricsConstants.METRIC_HEADER_WEBSITE_CANDIDATES, websiteSignupsCount);
        headerToValueMap.put(MetricsConstants.METRIC_HEADER_SUPPORT_CANDIDATES, supportSignupsCount);

        Logger.debug("Results map after getSignupsByChannel: " + headerToValueMap.toString());
    }

    private static void getCandidatesByActivityCount(Map<String, Object> headerToValueMap, String metricDate, String nextDate)
    {

        // Total OTP Verifications
        getOTPVerifications(headerToValueMap, metricDate, nextDate);

        // Basic profile Updates
        getSelfBasicProfileUpdates(headerToValueMap, metricDate, nextDate);

        // Experience profile updates
        getSelfExperienceProfileUpdates(headerToValueMap, metricDate, nextDate);

        // Education profile updates
        getSelfEducationProfileUpdates(headerToValueMap, metricDate, nextDate);

        // Get Job Applications
        getJobApplications(headerToValueMap, metricDate, nextDate);

        Logger.debug("Results map after getCandidatesByActivityCount: " + headerToValueMap.toString());

    }

    private static void getOTPVerifications(Map<String, Object> headerToValueMap, String metricDate, String nextDate)
    {
        // Build the query string
        StringBuilder otpVerificationQueryBuilder =
                new StringBuilder("select count(*) from interaction " +
                        "where result ='New Candidate Added & Candidate Self Updated Password' ");

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
        if (otpVerificationSqlQuery == null) {
            Logger.error("Error: No otp verification results found for date: " + metricDate);
            return;
        }

        Logger.debug(" OTP Verification results " + otpVerificationResults.toString());

        headerToValueMap.put(MetricsConstants.METRIC_HEADER_OTP_VERIFICATIONS,
                otpVerificationResults.get(0).getInteger("count(*)"));

    }

    private static void getSelfBasicProfileUpdates(Map<String, Object> headerToValueMap, String metricDate, String nextDate)
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

        Logger.debug(" Basic Profile Updates Results " + basicProfileResults.toString());

        headerToValueMap.put(MetricsConstants.METRIC_HEADER_BASIC_PROFILE_UPDATES, basicProfileResults.get(0).getInteger("count(*)"));
    }

    private static void getSelfExperienceProfileUpdates(Map<String, Object> headerToValueMap, String metricDate, String nextDate)
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

        headerToValueMap.put(MetricsConstants.METRIC_HEADER_EXP_PROFILE_UPDATES,
                experienceProfileResults.get(0).getInteger("count(*)"));
    }

    private static void getSelfEducationProfileUpdates(Map<String, Object> headerToValueMap, String metricDate, String nextDate)
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

        headerToValueMap.put(MetricsConstants.METRIC_HEADER_EDU_PROFILE_UPDATES,
                educationProfileResults.get(0).getInteger("count(*)"));
    }

    private static void getJobApplications(Map<String, Object> headerToValueMap, String metricDate, String nextDate)
    {
        // Build the query string
        StringBuilder jobAppQueryBuilder =
                new StringBuilder("select count(*) from interaction " +
                        "where result like 'Candidate applied to a job%' ");

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

        headerToValueMap.put(MetricsConstants.METRIC_HEADER_JOB_APPLICATIONS,
                jobAppResults.get(0).getInteger("count(*)"));
    }

    private static void getCandidatesBySupportAgents (Map<String, Object> headerToValueMap, String metricDate, String nextDate)
    {
        // Build the query string
        StringBuilder supportActivityQueryBuilder =
                new StringBuilder("select interaction.createdby, count(distinct(interaction.objectauuid)) from candidate " +
                        "join interaction on interaction.objectauuid=candidate.candidateuuid where interaction.result " +
                        " like '%New Candidate Added%' ");

        if (metricDate != null) {
            supportActivityQueryBuilder.append("and candidate.candidatecreatetimestamp  >= '" + metricDate + "' ");
        }

        if (nextDate != null) {
            supportActivityQueryBuilder.append(" and candidate.candidatecreatetimestamp <= '" + nextDate + "' ");
        }

        supportActivityQueryBuilder.append("group by interaction.createdby ");

        Logger.debug(" Support Activity Query: " + supportActivityQueryBuilder.toString());

        // Execute Query
        SqlQuery supportActivitySqlQuery = Ebean.createSqlQuery(supportActivityQueryBuilder.toString());
        List<SqlRow> supportActivityResults = supportActivitySqlQuery.findList();


        // if we dint get any results, simply return
        if (supportActivityResults == null) {
            Logger.error("Error: No support activity found for date: " + metricDate);
            return;
        }

        Logger.debug("Support activity results: " + supportActivityResults.toString());

        // iterate and find candidates added based on support agents
        Iterator<SqlRow> supportResultsItr = supportActivityResults.listIterator();

        Map<String, Object> agentNameToCandidateCount = new HashMap<String, Object>();

        while (supportResultsItr.hasNext()) {
            SqlRow supportRow = supportResultsItr.next();
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

        Logger.debug("Results map after getCandidatesBySupportAgents: " + headerToValueMap.toString());
    }

    private static void getLeadSources(Map<String, Object> headerToValueMap, String metricDate, String nextDate)
    {
        // Build the query string
        StringBuilder leadSourcesQueryBuilder =
                new StringBuilder("select ls.leadsourcename, count(*) as c from lead l " +
                        " join leadsource ls " +
                        " on l.leadsourceid = ls.leadsourceid " +
                        " where l.leadchannel in (1,2) ");

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

        while (leadNamesItr.hasNext()) {

            String leadSourceName = leadNamesItr.next();
            Object count = sourceToLeadCount.get(leadSourceName);
            headerToValueMap.put(leadSourceName, count == null? 0 : count);

        }

        Logger.debug("Results map after getLeadSources: " + headerToValueMap.toString());

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
