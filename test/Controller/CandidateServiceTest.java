package Controller;

import api.ServerConstants;
import api.http.AddCandidateRequest;
import api.http.AddSupportCandidateRequest;
import common.TestConstants;
import controllers.businessLogic.CandidateService;
import models.entity.Candidate;
import models.entity.Interaction;
import models.entity.Lead;
import models.entity.OM.JobPreference;
import models.entity.OM.LocalityPreference;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import play.Application;
import play.api.mvc.RequestHeader;
import play.mvc.Http;
import play.test.TestServer;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static play.mvc.Controller.session;
import static play.test.Helpers.*;

/**
 * Created by zero on 7/6/16.
 */
public class CandidateServiceTest {

    @InjectMocks
    private CandidateServiceTest candidateServiceTest;

    private static final Logger Logger = LoggerFactory.getLogger(CandidateServiceTest.class.getName());

    private Application fakeApp;

    private AddCandidateRequest req;

    private AddSupportCandidateRequest supportCandidateRequest;

    public static Http.Context context;


    @BeforeClass
    public static void initSessionRequestAndResponse() {
        Map<String, String> flashData = Collections.emptyMap();
        Map<String, Object> argData = Collections.emptyMap();
        Long id = 2L;

        Http.Request requestMock = mock(Http.Request.class);
        Http.Session sessionMock = mock(Http.Session.class);
        RequestHeader header = mock(RequestHeader.class);

        sessionMock.put("sessionId", "72779678-ac51-4005-b08a-0f53d31f0b51");
        sessionMock.put("sessionUsername", "junitTest");
        context = new Http.Context(id, header, requestMock, sessionMock, flashData, argData);
        Http.Context.current.set(context);
    }

    @Before
    public void setUpSignUpWebsiteMandatoryFields() {
        req = new AddCandidateRequest();
        req.setCandidateFirstName(TestConstants.testCandidateName);
        req.setCandidateSecondName(TestConstants.testCandidateLastName);
        req.setCandidateMobile(TestConstants.testCandidateMobile);
        req.setCandidateJobInterest(TestConstants.testCandidateJobInterest );
        req.setCandidateLocality(TestConstants.testCandidateLocalityPreference);
        fakeApp = fakeApplication();

    }
    @Before
    public void setUpSignSupportMandatoryFields() {
        supportCandidateRequest = new AddSupportCandidateRequest();
        supportCandidateRequest.setCandidateFirstName(TestConstants.testCandidateName);
        supportCandidateRequest.setCandidateSecondName(TestConstants.testCandidateLastName);
        supportCandidateRequest.setCandidateMobile(TestConstants.testCandidateMobile);
        supportCandidateRequest.setCandidateJobInterest(TestConstants.testCandidateJobInterest );
        supportCandidateRequest.setCandidateLocality(TestConstants.testCandidateLocalityPreference);
        fakeApp = fakeApplication();

    }

    public void testSignUpWebsiteMandatoryFields() {
        TestServer server = testServer(TestConstants.TEST_SERVER_PORT, fakeApp);
        running(server, () -> {
            cleanDB();
            CandidateService.createCandidateProfile(req, false, ServerConstants.UPDATE_BASIC_PROFILE);
            CandidateMandatoryCheck(false);
        });
    }

    @Test
    public void testSignUpSupportMandatoryFields() {
        TestServer server = testServer(TestConstants.TEST_SERVER_PORT, fakeApp);
        running(server, () -> {
            cleanDB();
            CandidateService.createCandidateProfile(supportCandidateRequest, true, ServerConstants.UPDATE_ALL_BY_SUPPORT);
            CandidateMandatoryCheck(true);
        });
    }

    // helper methods
    private void cleanDB(){
        Candidate candidate = CandidateService.isCandidateExists(TestConstants.testCandidateMobile);
        if(candidate != null){
            candidate.delete();
        }
    }

    private void CandidateMandatoryCheck(Boolean isSupport) {
        Lead lead  = Lead.find.where().eq("leadMobile", TestConstants.testCandidateMobile).findUnique();
        Logger.info("Session Id: " + session().get("sessionId") + " sessionUsername" + session().get("sessionUsername"));

        assertTrue(lead != null);
        assertEquals(lead.getLeadStatus(), ServerConstants.LEAD_STATUS_WON);
        assertEquals(lead.getLeadType(), ServerConstants.TYPE_CANDIDATE);

        Candidate candidate = CandidateService.isCandidateExists(TestConstants.testCandidateMobile);
        assertTrue(candidate != null);
        assertEquals(candidate.getCandidateName(), TestConstants.testCandidateName);
        assertEquals(candidate.getCandidateLastName(), TestConstants.testCandidateLastName);
        assertEquals(candidate.getCandidateMobile(), TestConstants.testCandidateMobile);
        for(LocalityPreference localityPreference : candidate.getLocalityPreferenceList()){
            assertTrue(TestConstants.testCandidateLocalityPreference.contains(String.valueOf(localityPreference.getLocality().getLocalityId())));
        }
        for(JobPreference jobPreference : candidate.getJobPreferencesList()){
            assertTrue(TestConstants.testCandidateJobInterest.contains(String.valueOf(jobPreference.getJobRole().getJobRoleId())));
        }

        List<Interaction> interactionList = Interaction.find.where().eq("objectAUUId", candidate.getCandidateUUId()).findList();
        assertTrue(interactionList != null);
        if(isSupport){
            //assertEquals(interactionList.get(0).createdBy, ServerConstants.INTERACTION_CREATED_SYSTEM);
        } else {
            assertEquals(interactionList.get(0).getCreatedBy(), ServerConstants.INTERACTION_CREATED_SELF);
        }
    }
}
