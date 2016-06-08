package Controller;

import api.ServerConstants;
import api.http.AddCandidateRequest;
import common.TestConstants;
import controllers.businessLogic.CandidateService;
import models.entity.Candidate;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import play.Application;
import play.api.mvc.RequestHeader;
import play.mvc.Http;
import play.test.TestServer;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static play.test.Helpers.*;

/**
 * Created by zero on 7/6/16.
 */
public class CandidateServiceTest {

    @InjectMocks
    private CandidateServiceTest candidateServiceTest;

    private static final Logger LOGGER = LoggerFactory.getLogger(CandidateServiceTest.class.getName());

    private Application fakeApp;

    private AddCandidateRequest req;

    Http.Request requestMock = mock(Http.Request.class);

    Http.Session sessionMock;

    @Before
    public void initSessionRequestAndResponse() {
        final Map<String, String> data = new HashMap<>();
        data.put("adminid", TestConstants.testAdminId);
        data.put("adminpass", TestConstants.testAdminPassword);

        Map<String, String> flashData = Collections.emptyMap();
        Map<String, Object> argData = Collections.emptyMap();
        Long id = 2L;
        RequestHeader header = mock(RequestHeader.class);
        sessionMock = mock(Http.Session.class);
        sessionMock.put("sessionId", "cec1c6a6-6deb-4835-8eda-17831e45f8b4");
        sessionMock.put("sessionUsername", "junitTest");
        Http.Context context = new Http.Context(id, header, requestMock, sessionMock, flashData, argData);
        Http.Context.current.set(context);
    }

    @Before
    public void setUp() {
        req = new AddCandidateRequest();
        req.setCandidateFirstName(TestConstants.testCandidateName);
        req.setCandidateSecondName(TestConstants.testCandidateLastName);
        req.setCandidateMobile(TestConstants.testCandidateMobile);
        req.setCandidateJobInterest(TestConstants.testCandidateJobInterest );
        req.setCandidateLocality(TestConstants.testCandidateLocalityPreference);
        req.setLeadSource(TestConstants.testLeadSource);

        fakeApp = fakeApplication();
    }

    @Test
    public void testCreateCandidateBySupport() {
        TestServer server = testServer(TestConstants.TEST_SERVER_PORT, fakeApp);
        running(server, () -> {
            CandidateService.createCandidateProfile(req, false, ServerConstants.UPDATE_BASIC_PROFILE);
            Candidate candidate = CandidateService.isCandidateExists(TestConstants.testCandidateMobile);
            assertTrue(candidate != null);
        });
    }

    @After
    public void cleanTestCreateCandidateBySupportMess() {

    }

}
