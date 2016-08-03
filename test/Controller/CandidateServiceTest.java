package Controller;

import api.ServerConstants;
import api.http.httpRequest.AddCandidateRequest;
import api.http.httpRequest.AddSupportCandidateRequest;
import common.TestConstants;
import controllers.businessLogic.CandidateService;
import controllers.businessLogic.LeadService;
import models.entity.Auth;
import models.entity.Candidate;
import models.entity.Interaction;
import models.entity.Lead;
import models.entity.OM.JobHistory;
import models.entity.Static.Locality;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.InjectMocks;
import play.Application;
import play.api.mvc.RequestHeader;
import play.mvc.Http;
import play.test.TestServer;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static common.TestConstants.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static play.mvc.Controller.session;
import static play.test.Helpers.*;

/**
 * Created by zero on 7/6/16.
 */
@Ignore
public class CandidateServiceTest {

    @InjectMocks
    private CandidateServiceTest candidateServiceTest;

    private Application fakeApp;

    private AddCandidateRequest req;

    private AddSupportCandidateRequest supportCandidateRequest = new AddSupportCandidateRequest();

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

    public void setUpSignUpWebsiteMandatoryFields() {
        req = new AddCandidateRequest();
        req.setCandidateFirstName(testCandidateName);
        req.setCandidateSecondName(testCandidateLastName);
        req.setCandidateMobile(testCandidateMobile);
        req.setCandidateJobPref(testCandidateJobInterest);
        req.setCandidateLocality(testCandidateLocalityPreference);
        fakeApp = fakeApplication();
    }
    @Before
    public void setUpSignSupportMandatoryFields() {
        supportCandidateRequest.setCandidateFirstName(testCandidateName);
        supportCandidateRequest.setCandidateSecondName(testCandidateLastName);
        supportCandidateRequest.setCandidateMobile(testCandidateMobile);
        supportCandidateRequest.setCandidateJobPref(testCandidateJobInterest );
        supportCandidateRequest.setCandidateLocality(testCandidateLocalityPreference);
        fakeApp = fakeApplication();
    }
    @Before
    public void setUpCandidateBasicProfile() {
        setUpSignUpWebsiteMandatoryFields();
        supportCandidateRequest.setCandidateDob(testCandidateDob);
        supportCandidateRequest.setCandidateGender(testCandidateGender);
        supportCandidateRequest.setCandidateTimeShiftPref(testCandidateTimeShiftPref);
        fakeApp = fakeApplication();
    }
    @Before
    public void setUpCandidateSkillProfile() {
        setUpCandidateBasicProfile();
        supportCandidateRequest.setCandidateTotalExperience(testCandidateTotalExperience);
        supportCandidateRequest.setCandidateIsEmployed(testCandidateIsEmployed);
        supportCandidateRequest.setCandidateMotherTongue(testCandidateMotherTongue);

        supportCandidateRequest.setCandidateCurrentCompany(testCandidateCurrentCompany);

        supportCandidateRequest.setCandidateSkills(testCandidateSkillList);
        supportCandidateRequest.setCandidateLanguageKnown(testCandidateLanguageKnownList);
        fakeApp = fakeApplication();
    }
    @Before
    public void setUpCandidateEducationProfile() {
        setUpCandidateSkillProfile();
        supportCandidateRequest.setCandidateEducationInstitute(testCandidateEducationInstitute);
        supportCandidateRequest.setCandidateDegree(testCandidateDegree);
        supportCandidateRequest.setCandidateEducationLevel(testCandidateEducationLevel);
        fakeApp = fakeApplication();
    }

    @Before
    public void setUpCandidateOtherInformationOnlyBySupport(){
        setUpCandidateEducationProfile();
        supportCandidateRequest.setCandidateHomeLocality(testCandidateHomeLocality);
        supportCandidateRequest.setCandidatePhoneType(testCandidatePhoneType);
        supportCandidateRequest.setCandidateEmail(testCandidateEmail);
        supportCandidateRequest.setCandidateMaritalStatus(testCandidateMaritalStatus);
        supportCandidateRequest.setCandidateAppointmentLetter(testCandidateAppointmentLetter);
        supportCandidateRequest.setCandidateSalarySlip(testCandidateSalarySlip);
        supportCandidateRequest.setCandidateIdProof(testCandidateIdProof);
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

    public void testSignUpSupportMandatoryFields() {
        TestServer server = testServer(TestConstants.TEST_SERVER_PORT, fakeApp);
        running(server, () -> {
            cleanDB();
            CandidateService.createCandidateProfile(supportCandidateRequest, true, ServerConstants.UPDATE_ALL_BY_SUPPORT);
            CandidateMandatoryCheck(true);
        });
    }


    @Test
    public void testSignUpSupportAllFields() {
        TestServer server = testServer(TestConstants.TEST_SERVER_PORT, fakeApp);
        running(server, () -> {
            cleanDB();
            CandidateService.createCandidateProfile(supportCandidateRequest, true, ServerConstants.UPDATE_ALL_BY_SUPPORT);
            //CandidateMandatoryCheck(true);
            checkCandidateBasicProfile();
            checkCandidateSkillProfile();
            checkCandidateEducationProfile();
            checkCandidateOtherInformationOnlyBySupport();
        });
    }

    public void checkCandidateBasicProfile() {
        Lead lead  = Lead.find.where().eq("leadMobile", testCandidateMobile).findUnique();
        System.out.println("Session Id: " + session().get("sessionId") + " sessionUsername " + session().get("sessionUsername"));

        assertTrue(lead != null);
        assertEquals(lead.getLeadStatus(), ServerConstants.LEAD_STATUS_WON);
        assertEquals(lead.getLeadType(), ServerConstants.TYPE_CANDIDATE);

        Candidate candidate = CandidateService.isCandidateExists(testCandidateMobile);
        assertTrue(candidate != null);

        assertEquals(candidate.getCandidateFirstName(), testCandidateName);
        assertEquals(candidate.getCandidateLastName(), testCandidateLastName);
        assertEquals(candidate.getCandidateMobile(), testCandidateMobile);


        assertTrue(candidate.getLocalityPreferenceList()!= null);
        for(int i=0; i< candidate.getLocalityPreferenceList().size(); i++){
            assertEquals(candidate.getLocalityPreferenceList().get(i).getLocality().getLocalityId(), (long) testCandidateLocalityPreference.get(i));
        }

        assertTrue(candidate.getJobPreferencesList()!= null);
        for(int i=0; i< candidate.getJobPreferencesList().size(); i++){
            assertEquals(candidate.getJobPreferencesList().get(i).getJobRole().getJobRoleId(), (long) testCandidateJobInterest.get(i));
        }

        assertTrue(candidate.getTimeShiftPreference()!= null);
        assertEquals(candidate.getTimeShiftPreference().getTimeShift().getTimeShiftId(), Integer.parseInt(testCandidateTimeShiftPref));

    }

    public void checkCandidateSkillProfile() {
        Lead lead  = Lead.find.where().eq("leadMobile", testCandidateMobile).findUnique();
        System.out.println("Session Id: " + session().get("sessionId") + " sessionUsername " + session().get("sessionUsername"));

        assertTrue(lead != null);
        assertEquals(lead.getLeadStatus(), ServerConstants.LEAD_STATUS_WON);
        assertEquals(lead.getLeadType(), ServerConstants.TYPE_CANDIDATE);

        Candidate candidate = CandidateService.isCandidateExists(testCandidateMobile);
        assertTrue(candidate != null);

        assertEquals(candidate.getMotherTongue().getLanguageId(), (int) testCandidateMotherTongue);
        assertEquals(candidate.getCandidateTotalExperience(), testCandidateTotalExperience);
        assertEquals(candidate.getCandidateIsEmployed(), testCandidateIsEmployed);

        assertTrue(candidate.getCandidateSkillList()!= null);
        for(int i=0; i< candidate.getCandidateSkillList().size(); i++) {
            assertEquals(candidate.getCandidateSkillList().get(i).getSkill().getSkillId(), Integer.parseInt(testCandidateSkillList.get(i).getId()));
        }

        assertTrue(candidate.getCandidateCurrentJobDetail()!= null);
        assertEquals(candidate.getCandidateCurrentJobDetail().getCandidateCurrentDesignation(), testCandidateCurrentJobDesignation);
        assertEquals(candidate.getCandidateCurrentJobDetail().getCandidateCurrentCompany(), testCandidateCurrentCompany);
        assertEquals(candidate.getCandidateCurrentJobDetail().getCandidateCurrentSalary(), testCandidateCurrentSalary);
        assertEquals(candidate.getCandidateCurrentJobDetail().getCandidateCurrentJobDuration(), testCandidateCurrentJobDuration);
        assertEquals(candidate.getCandidateCurrentJobDetail().getCandidateCurrentWorkShift().getTimeShiftId(), (int) testCandidateCurrentWorkShift);
        assertEquals(candidate.getCandidateCurrentJobDetail().getJobRole().getJobRoleId(), (int) testCandidateCurrentJobRole);
        assertEquals(candidate.getCandidateCurrentJobDetail().getCandidateCurrentJobLocation().getLocalityId(), (long) testCandidateCurrentJobLocation);
        assertEquals(candidate.getCandidateCurrentJobDetail().getCandidateTransportationMode().getTransportationModeId(), (int) testCandidateTransportation);

        assertTrue(candidate.getLanguageKnownList()!= null);
        for(int i=0; i< candidate.getLanguageKnownList().size(); i++){
            assertEquals(candidate.getLanguageKnownList().get(i).getLanguage().getLanguageId(), Integer.parseInt(testCandidateLanguageKnownList.get(i).getId()));
        }
    }

    public void checkCandidateEducationProfile() {
        Lead lead  = Lead.find.where().eq("leadMobile", testCandidateMobile).findUnique();
        System.out.println("Session Id: " + session().get("sessionId") + " sessionUsername " + session().get("sessionUsername"));

        assertTrue(lead != null);
        assertEquals(lead.getLeadStatus(), ServerConstants.LEAD_STATUS_WON);
        assertEquals(lead.getLeadType(), ServerConstants.TYPE_CANDIDATE);

        Candidate candidate = CandidateService.isCandidateExists(testCandidateMobile);
        assertTrue(candidate != null);

        assertTrue(candidate.getCandidateEducation()!= null);
        assertEquals(candidate.getCandidateEducation().getCandidateLastInstitute(),  testCandidateEducationInstitute);
        assertEquals(candidate.getCandidateEducation().getDegree().getDegreeId(), (int)  testCandidateDegree);
        assertEquals(candidate.getCandidateEducation().getEducation().getEducationId(), (int) testCandidateEducationLevel);

    }

    public void checkCandidateOtherInformationOnlyBySupport(){
        Locality homeLocality = new Locality();
        homeLocality.setLocalityId(testCandidateHomeLocality);

        Lead lead  = Lead.find.where().eq("leadMobile", testCandidateMobile).findUnique();
        System.out.println("Session Id: " + session().get("sessionId") + " sessionUsername " + session().get("sessionUsername"));

        assertTrue(lead != null);
        assertEquals(lead.getLeadStatus(), ServerConstants.LEAD_STATUS_WON);
        assertEquals(lead.getLeadType(), ServerConstants.TYPE_CANDIDATE);

        Candidate candidate = CandidateService.isCandidateExists(testCandidateMobile);
        assertTrue(candidate != null);

        assertEquals(candidate.getLocality(), homeLocality);
        assertEquals(candidate.getCandidatePhoneType(), testCandidatePhoneType);
        assertEquals(candidate.getCandidateEmail(), testCandidateEmail);
        assertEquals(candidate.getCandidateMaritalStatus(), testCandidateMaritalStatus);
        assertEquals(candidate.getCandidateAppointmentLetter(), testCandidateAppointmentLetter);
        assertEquals(candidate.getCandidateSalarySlip(), testCandidateSalarySlip);


        for(JobHistory jobHistory: candidate.getJobHistoryList()){
            assertEquals(jobHistory.getJobRole().getJobRoleId(), (long) testCandidatePastJobRole);
            assertEquals(jobHistory.getCandidatePastCompany(), testCandidatePastCompany);
            assertEquals(jobHistory.getCandidatePastSalary(), testCandidatePastJobSalary);
        }

        assertTrue(candidate.getIdProofReferenceList()!= null);
        for(int i=0; i< candidate.getIdProofReferenceList().size(); i++) {
            assertEquals(candidate.getIdProofReferenceList().get(i).getIdProof().getIdProofId(),(long) testCandidateIdProof.get(i));
        }
    }

    public void cleanup(){
        cleanDB();
    }

    // helper methods
    private void cleanDB(){
        Candidate candidate = CandidateService.isCandidateExists(TestConstants.testCandidateMobile);
        if(candidate != null){
            Auth auth = Auth.find.where().eq("candidateId", candidate.getCandidateId()).findUnique();
            if(auth != null){
                auth.delete();
            }
            List<Interaction> interactionList = Interaction.find.where().eq("objectAUUId", candidate.getCandidateUUId()).findList();
            for(Interaction interactionToDelete : interactionList){
                interactionToDelete.delete();
            }
            candidate.delete();
        }
        Lead lead = LeadService.isLeadExists(TestConstants.testCandidateMobile);
        if(lead != null){
            List<Interaction> interactionList = Interaction.find.where().eq("objectAUUId", lead.getLeadUUId()).findList();
            for(Interaction interactionToDelete : interactionList){
                interactionToDelete.delete();
            }
            lead.delete();
        }
    }

    private void CandidateMandatoryCheck(Boolean isSupport) {
        Lead lead  = Lead.find.where().eq("leadMobile", TestConstants.testCandidateMobile).findUnique();
        System.out.println("Session Id: " + session().get("sessionId") + " sessionUsername " + session().get("sessionUsername"));

        assertTrue(lead != null);
        assertEquals(lead.getLeadStatus(), ServerConstants.LEAD_STATUS_WON);
        assertEquals(lead.getLeadType(), ServerConstants.TYPE_CANDIDATE);

        Candidate candidate = CandidateService.isCandidateExists(TestConstants.testCandidateMobile);
        assertTrue(candidate != null);
        assertEquals(candidate.getCandidateFirstName(), TestConstants.testCandidateName);
        assertEquals(candidate.getCandidateLastName(), TestConstants.testCandidateLastName);
        assertEquals(candidate.getCandidateMobile(), TestConstants.testCandidateMobile);

        assertTrue(candidate.getLocalityPreferenceList()!= null);
        for(int i=0; i< candidate.getLocalityPreferenceList().size(); i++){
            assertEquals(candidate.getLocalityPreferenceList().get(i).getLocality().getLocalityId(),(long) testCandidateLocalityPreference.get(i));
        }

        assertTrue(candidate.getJobPreferencesList()!= null);
        for(int i=0; i< candidate.getJobPreferencesList().size(); i++){
            assertEquals(candidate.getJobPreferencesList().get(i).getJobRole().getJobRoleId(), (long) testCandidateJobInterest.get(i));
        }
        // unset fields
        assertTrue(candidate.getCandidateDOB() == null);
        assertTrue(candidate.getCandidateGender() == null);
        assertTrue(candidate.getCandidateMaritalStatus() == null);
        assertTrue(candidate.getCandidateIsAssessed() == 0);  //No
        assertTrue(candidate.getCandidateIsEmployed() == null);
        assertTrue(candidate.getCandidateEmail() == null);
        assertTrue(candidate.getCandidatePhoneType() == null);
        assertTrue(candidate.getCandidateAge() == null);
        assertTrue(candidate.getCandidateSalarySlip() == null);
        assertTrue(candidate.getIsMinProfileComplete() == 0); //No
        assertTrue(candidate.getCandidateprofilestatus().getProfileStatusId() == 1); //New
        assertTrue(candidate.getLocality() == null);
        assertTrue(candidate.getMotherTongue() == null);
        assertTrue(candidate.getCandidateCurrentJobDetail() == null);
        assertTrue(candidate.getCandidateCreateTimestamp() != null);
        assertTrue(candidate.getCandidateUpdateTimestamp() != null);

        List<Interaction> interactionList = Interaction.find.where().eq("objectAUUId", candidate.getCandidateUUId()).findList();
        assertTrue(interactionList != null);

        if(isSupport){
            //assertEquals(interactionList.get(0).createdBy, ServerConstants.INTERACTION_CREATED_SYSTEM);
        } else {
            assertEquals(interactionList.get(0).getCreatedBy(), ServerConstants.INTERACTION_CREATED_SELF);
        }
    }
}
