package Service;

import api.ServerConstants;
import common.TestConstants;
import controllers.businessLogic.AddressResolveService;
import controllers.businessLogic.JobSearchService;
import in.trujobs.proto.JobFilterRequest;
import models.entity.JobPost;
import models.entity.Static.Locality;
import models.util.LatLng;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import play.Application;
import play.Logger;
import play.test.TestServer;

import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static play.libs.Json.toJson;
import static play.test.Helpers.*;

/**
 * Created by zero on 25/8/16.
 *
 * prod/activator-1.3.9-minimal/bin/activator "test-only Service.AddressResolverServiceTest"
 */

@RunWith(Parameterized.class)
public class JobSearchServiceTest {

    enum MethodType {
        getRelevantJobPostsWithinDistance,
        getExactJobPostsWithinDistance,
    }

    private JobSearchServiceTest.MethodType type;
    private Double latitude;
    private Double longitude;
    private List<Long> jobRoleIds;
    private JobFilterRequest.Builder filterParams;
    private Integer sortBy;
    private Boolean isHot;
    private Boolean isAllSources;

    private Integer expectedSize;
    private JobPost expectedFirstJobPost;
    private JobSearchService jobSearchService;


    @Before
    public void initialize() {
        jobSearchService = new JobSearchService();
    }

    // Each parameter should be placed as an argument here
    // Every time runner triggers, it will pass the arguments
    public JobSearchServiceTest(MethodType type, Double latitude, Double longitude, List<Long> jobRoleIds,
                                JobFilterRequest.Builder filterParams, Integer sortBy,
                                Boolean isHot, Boolean isAllSources) {
        this.type = type;
        if(latitude != null)this.latitude = RoundTo6Decimals(latitude);
        if(longitude != null)this.longitude = RoundTo6Decimals(longitude);
        if(jobRoleIds != null) this.jobRoleIds = jobRoleIds;
        if(filterParams != null) this.filterParams = filterParams;
        if(sortBy != null) this.sortBy = sortBy;
        if(isHot != null) this.isHot = isHot;
        if(isAllSources != null) this.isAllSources = isAllSources;
    }

    @Parameterized.Parameters
    public static Collection getTestDataSet() {
        // bellandur {12.926031, 77.676246}
        return Arrays.asList(new Object[][] {
                // #1 (Accountant, telecaller, other roles), Education:UG, Experienced; 10000+ sal
                // sort by date, only ishot
                /*{MethodType.getJobPosts, 12.926031,77.676246, Arrays.asList(1, 5, 34),
                        getFilterObjectFromParams(TestConstants.testCandidateMobile,
                                                  JobFilterRequest.Education.UG,
                                                  JobFilterRequest.Experience.EXPERIENCED,
                                                  JobFilterRequest.Salary.TEN_K_PLUS,
                                                  JobFilterRequest.Gender.ANY_GENDER, false, false),
                        ServerConstants.SORT_BY_DATE_POSTED, true, true},

                // #2 (Accountant, telecaller, driver), Experienced;
                // sort by date, only ishot
                {MethodType.getJobPosts, 12.926031,77.676246, Arrays.asList(1l, 5l, 12l),
                        getFilterObjectFromParams(TestConstants.testCandidateMobile,
                                JobFilterRequest.Education.ANY_EDUCATION,
                                JobFilterRequest.Experience.EXPERIENCED,
                                JobFilterRequest.Salary.ANY_SALARY,
                                JobFilterRequest.Gender.ANY_GENDER, false, false),
                        ServerConstants.SORT_BY_DATE_POSTED, true, true},


                // #3 (Delivery, telecaller, driver), 15000+ salary;
                // sort by salary, hot and nothot
                {MethodType.getJobPosts, 12.926031,77.676246, Arrays.asList(11l, 5l, 12l),
                        getFilterObjectFromParams(TestConstants.testCandidateMobile,
                                JobFilterRequest.Education.ANY_EDUCATION,
                                JobFilterRequest.Experience.ANY_EXPERIENCE,
                                JobFilterRequest.Salary.FIFTEEN_K_PLUS,
                                JobFilterRequest.Gender.ANY_GENDER, false, false),
                        ServerConstants.SORT_BY_SALARY, true, true},

                // #4 (Accountant, telecaller, other roles);
                // sort by date, only ishot
                {MethodType.getJobPosts, 12.926031,77.676246, Arrays.asList(1l, 5l, 34l),
                        getFilterObjectFromParams(TestConstants.testCandidateMobile, null,
                                null, null, null, false, false),
                        ServerConstants.SORT_BY_SALARY, true, true},*/

                // #5 (delivery); Around bellandur
                // sort by date, 'All Jobs sources
                /*{MethodType.getRelevantJobPostsWithinDistance, 12.926031,77.676246, Arrays.asList(11l),
                        getFilterObjectFromParams(TestConstants.testCandidateMobile, null,
                                null, null, null, false, false),
                        ServerConstants.SORT_DEFAULT, false, true},

                // #6 (delivery); All Bangalore
                // sort by distance, 'All Jobs sources
                {MethodType.getRelevantJobPostsWithinDistance, null, null, Arrays.asList(11l),
                        getFilterObjectFromParams(TestConstants.testCandidateMobile, null,
                                null, null, null, false, false),
                        ServerConstants.SORT_DEFAULT, false, true},*/

                // #7 (delivery); All Bangalore
                // sort by salary, 'All Jobs sources
                // filter 15k+ salary
                {MethodType.getRelevantJobPostsWithinDistance, 12.926031,77.676246, Arrays.asList(3l),
                        getFilterObjectFromParams(TestConstants.testCandidateMobile, null,
                                null, JobFilterRequest.Salary.FIFTEEN_K_PLUS, null, false, false),
                        ServerConstants.SORT_DEFAULT, false, true},


                // #8 (delivery); Around bellandur
                // sort by date, only internal jobs
                /*{MethodType.getExactJobPostsWithinDistance, 12.926031,77.676246, Arrays.asList(11l),
                        getFilterObjectFromParams(TestConstants.testCandidateMobile, null,
                                null, null, null, false, false),
                        ServerConstants.SORT_BY_NEARBY, false, false},

                // #9 (delivery); Around bellandur
                // sort by date, 'All Jobs sources
                {MethodType.getExactJobPostsWithinDistance, 12.926031,77.676246, Arrays.asList(11l),
                        getFilterObjectFromParams(TestConstants.testCandidateMobile, null,
                                null, null, null, false, false),
                        ServerConstants.SORT_BY_NEARBY, false, true},

                // #10 (delivery); All bangalore
                // sort by date, only internal jobs
                {MethodType.getExactJobPostsWithinDistance, null,null, Arrays.asList(11l),
                        getFilterObjectFromParams(TestConstants.testCandidateMobile, null,
                                null, null, null, false, false),
                        ServerConstants.SORT_BY_NEARBY, false, false},*/
        });
    }

    @Test
    public void testGetRelevantJobPostsWithinDistance() {
        Application fakeApp = fakeApplication();
        TestServer server = testServer(TestConstants.TEST_SERVER_PORT, fakeApp);
        running(server, () -> {
            if(type == MethodType.getRelevantJobPostsWithinDistance){
                List<JobPost> results =
                        jobSearchService.getRelevantJobPostsWithinDistance(latitude, longitude,
                                jobRoleIds, filterParams.build(), sortBy, isHot, isAllSources);

                System.out.println(" GRJPWD START OF TEST");
                System.out.println("Results Size:" + results.size());

                for (JobPost jp : results) {
                    System.out.println("Result Entry:" + jp.toString());
                }
            }
        });

        System.out.println("END OF TEST");
    }

    @Test
    public void testGetExactJobPostsWithinDistance() {
        Application fakeApp = fakeApplication();
        TestServer server = testServer(TestConstants.TEST_SERVER_PORT, fakeApp);
        running(server, () -> {
            if(type == MethodType.getExactJobPostsWithinDistance){
                List<JobPost> results =
                        jobSearchService.getExactJobPostsWithinDistance(latitude, longitude,
                                jobRoleIds, filterParams.build(), sortBy, isHot, isAllSources);

                System.out.println(" GEJPWD START OF TEST");
                System.out.println("Results Size:" + results.size());

                for (JobPost jp : results) {
                    System.out.println("Result Entry:" + jp.toString());
                }
            }
        });

        System.out.println("END OF TEST");
    }


    private Double RoundTo6Decimals(Double val) {
        DecimalFormat df2 = new DecimalFormat("#####.######");
        return Double.valueOf(df2.format(val));
    }

    private static JobFilterRequest.Builder getFilterObjectFromParams(String mobile,
                                                               Object education,
                                                               Object experience,
                                                               Object salary,
                                                               Object gender,
                                                               Boolean sortByDate,
                                                               Boolean sortBySalary) {

        JobFilterRequest.Builder jobFilterRequestobj = JobFilterRequest.newBuilder();
        jobFilterRequestobj.setCandidateMobile(mobile);
        if(education!=null) jobFilterRequestobj.setEdu((JobFilterRequest.Education ) education);
        if(experience!=null) jobFilterRequestobj.setExp((JobFilterRequest.Experience ) experience);
        if(salary!=null) jobFilterRequestobj.setSalary((JobFilterRequest.Salary ) salary);
        if(gender != null) jobFilterRequestobj.setGender((JobFilterRequest.Gender) gender);
        if(sortByDate!=null) jobFilterRequestobj.setSortByDatePosted(sortByDate);
        if(sortBySalary!=null) jobFilterRequestobj.setSortBySalary(sortBySalary);
        return jobFilterRequestobj;
    }


}
