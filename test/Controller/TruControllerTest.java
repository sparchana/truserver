package Controller;

import common.TestConstants;
import controllers.businessLogic.JobPostService;
import in.trujobs.proto.JobFilterRequest;
import models.entity.JobPost;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import play.Application;
import play.Logger;
import play.test.TestServer;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static play.libs.Json.toJson;
import static play.test.Helpers.*;

/**
 * Created by zero on 11/8/16.
 */

@RunWith(Parameterized.class)
public class TruControllerTest {
    private JobFilterRequest.Builder jobFilterRequest;
    private int expectedSize;

    public TruControllerTest(JobFilterRequest.Builder jobFilterRequest, int expectedSize) {
        this.jobFilterRequest = jobFilterRequest;
        this.expectedSize = expectedSize;
    }

    public static JobFilterRequest.Builder getFilterObjectFromParams(String mobile,
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


    @Parameterized.Parameters
    public static Collection getFilterTestCases() {
        return Arrays.asList(new Object[][]{
                {getFilterObjectFromParams(TestConstants.testCandidateMobile, null, null,
                        null, JobFilterRequest.Gender.MALE, false, false), 2},
                {getFilterObjectFromParams(TestConstants.testCandidateMobile, null, null,
                        null, JobFilterRequest.Gender.FEMALE, false, false), 2},
                {getFilterObjectFromParams(TestConstants.testCandidateMobile, JobFilterRequest.Education.PG,
                        null, null, null, true, false), 2},
                {getFilterObjectFromParams(TestConstants.testCandidateMobile, null,
                        JobFilterRequest.Experience.EXPERIENCED, null, null, false, true), 3},
                {getFilterObjectFromParams(TestConstants.testCandidateMobile, null,
                        null, JobFilterRequest.Salary.EIGHT_K_PLUS, null, true, false), 3},
                {getFilterObjectFromParams(TestConstants.testCandidateMobile, null,
                        null, JobFilterRequest.Salary.TWENTY_K_PLUS, null, true, false), 2},
                {getFilterObjectFromParams(TestConstants.testCandidateMobile, null,
                        null, JobFilterRequest.Salary.FIFTEEN_K_PLUS, null, true, false), 2}
        });
    }

    @Test
    public void testFilterJobs() {
        Application fakeApp = fakeApplication();
        TestServer server = testServer(TestConstants.TEST_SERVER_PORT, fakeApp);
        running(server, () -> {
            List<JobPost> jobPostList = JobPostService.filterJobs(jobFilterRequest.build(), null);
            if (jobPostList == null) return;
            assertEquals(expectedSize, jobPostList.size());
            System.out.println("[test case] testFilterJobs: resultSize:-----------------------------------------------" + jobPostList.size());
            Logger.info(String.valueOf(toJson(jobPostList.size())));
        });
    }
}
