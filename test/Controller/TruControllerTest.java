package Controller;

import common.TestConstants;
import controllers.TrudroidController;
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

    public TruControllerTest(String mobile,
                             JobFilterRequest.Education education,
                             JobFilterRequest.Experience experience,
                             JobFilterRequest.Salary salary,
                             Boolean sortByDate,
                             Boolean sortBySalary,
                             int expectedSize){

        this.jobFilterRequest = JobFilterRequest.newBuilder();
        this.jobFilterRequest.setCandidateMobile(mobile);
        this.jobFilterRequest.setEdu(education);
        this.jobFilterRequest.setExp(experience);
        this.jobFilterRequest.setSalary(salary);
        this.jobFilterRequest.setSortByDatePosted(sortByDate);
        this.jobFilterRequest.setSortBySalary(sortBySalary);
        this.expectedSize = expectedSize;
    }

    @Parameterized.Parameters
    public static Collection getFilterTestCases(){
        return Arrays.asList(new Object[][]{
                {TestConstants.testCandidateMobile, JobFilterRequest.Education.ANY_EDUCATION, JobFilterRequest.Experience.ANY_EXPERIENCE,
                        JobFilterRequest.Salary.ANY_SALARY, true, true, 3},
                {TestConstants.testCandidateMobile, JobFilterRequest.Education.ANY_EDUCATION, JobFilterRequest.Experience.ANY_EXPERIENCE,
                        JobFilterRequest.Salary.ANY_SALARY, true, true, 3},
                {TestConstants.testCandidateMobile, JobFilterRequest.Education.TWELVE_PASS, JobFilterRequest.Experience.ANY_EXPERIENCE,
                        JobFilterRequest.Salary.ANY_SALARY, true, false, 3},
                {TestConstants.testCandidateMobile, JobFilterRequest.Education.UG, JobFilterRequest.Experience.ANY_EXPERIENCE,
                        JobFilterRequest.Salary.ANY_SALARY, false, true, 3},
                {TestConstants.testCandidateMobile, JobFilterRequest.Education.PG, JobFilterRequest.Experience.ANY_EXPERIENCE,
                        JobFilterRequest.Salary.ANY_SALARY, false, false, 3},
                {TestConstants.testCandidateMobile, JobFilterRequest.Education.ANY_EDUCATION, JobFilterRequest.Experience.FRESHER,
                        JobFilterRequest.Salary.ANY_SALARY, true, true, 3},
                {TestConstants.testCandidateMobile, JobFilterRequest.Education.ANY_EDUCATION, JobFilterRequest.Experience.EXPERIENCED,
                        JobFilterRequest.Salary.ANY_SALARY, true, true, 3},
                {TestConstants.testCandidateMobile, JobFilterRequest.Education.ANY_EDUCATION, JobFilterRequest.Experience.ANY_EXPERIENCE,
                        JobFilterRequest.Salary.FIFTEEN_K_PLUS, true, true, 3},
                {TestConstants.testCandidateMobile, JobFilterRequest.Education.ANY_EDUCATION, JobFilterRequest.Experience.ANY_EXPERIENCE,
                        JobFilterRequest.Salary.TEN_K_PLUS, true, true, 3}
        });
    }

    @Test
    public void testFilterJobs(){
        Application fakeApp = fakeApplication();
        TestServer server = testServer(TestConstants.TEST_SERVER_PORT, fakeApp);
        running(server, () -> {
            List<JobPost> jobPostList = TrudroidController.filterJobs(jobFilterRequest.build());
            if(jobPostList == null) return;
            assertEquals(expectedSize, jobPostList.size());
            System.out.println("[test case] testFilterJobs: resultSize:-----------------------------------------------"+jobPostList.size());
            Logger.info(String.valueOf(toJson(jobPostList.size())));
        });
    }
}
