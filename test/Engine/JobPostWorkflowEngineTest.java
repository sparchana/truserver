package Engine;

/**
 * Created by zero on 5/10/16.
 *
 * prod/activator-1.3.9-minimal/bin/activator "test-only Engine.JobPostWorkflowEngineTest"
 */

import Service.AddressResolverServiceTest;
import common.TestConstants;
import controllers.businessLogic.AddressResolveService;
import controllers.businessLogic.JobWorkflow.JobPostWorkflowEngine;
import models.util.LatLng;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import play.Application;
import play.Logger;
import play.test.TestServer;

import java.util.Arrays;
import java.util.Collection;

import static org.junit.Assert.assertEquals;
import static play.libs.Json.toJson;
import static play.test.Helpers.fakeApplication;
import static play.test.Helpers.running;
import static play.test.Helpers.testServer;

@RunWith(Parameterized.class)
public class JobPostWorkflowEngineTest {
    enum MethodType {
        getMatchingCandidateLite,
        getMatchingCandidate,
        getDurationFromExperience
    }

    private JobPostWorkflowEngineTest.MethodType type;
    private JobPostWorkflowEngine jobPostWorkflowEngine;
    private Long jobPostId;
    private int minAge;
    private int maxAge;
    private int gender;

    @Before
    public void initialize() {
        jobPostWorkflowEngine = new JobPostWorkflowEngine();
    }

    // Each parameter should be placed as an argument here
    // Every time runner triggers, it will pass the arguments
    public JobPostWorkflowEngineTest(JobPostWorkflowEngineTest.MethodType type, Long jobPostId) {
        this.type = type;
        if (jobPostId != null) this.jobPostId = jobPostId;

    }

    @Parameterized.Parameters
    public static Collection getTestDataSet() {
        return Arrays.asList(new Object[][]{
                {JobPostWorkflowEngineTest.MethodType.getMatchingCandidateLite, 534L},
        });
    }


    @Test
    public void testGetMatchingCandidateLite() {
        if (type == JobPostWorkflowEngineTest.MethodType.getMatchingCandidateLite) {
            Application fakeApp = fakeApplication();
            TestServer server = testServer(TestConstants.TEST_SERVER_PORT, fakeApp);
            running(server, () -> {
                Logger.info(String.valueOf(toJson(JobPostWorkflowEngine.getMatchingCandidate(jobPostId).size())));
            });
        }
    }
    @Ignore
    public void testGetMatchingCandidate() {
        if (type == JobPostWorkflowEngineTest.MethodType.getMatchingCandidate) {
            Application fakeApp = fakeApplication();
            TestServer server = testServer(TestConstants.TEST_SERVER_PORT, fakeApp);
            running(server, () -> {
                Logger.info(String.valueOf(toJson(JobPostWorkflowEngine.getMatchingCandidate(jobPostId).size())));
            });
        }
    }
}
