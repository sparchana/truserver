package controllers;

import api.ServerConstants;
import api.http.httpResponse.Workflow.smsJobApplyFlow.PostApplyInShortResponse;
import com.google.maps.GeoApiContext;
import com.google.maps.GeocodingApi;
import com.google.maps.model.GeocodingResult;
import controllers.businessLogic.CandidateService;
import controllers.businessLogic.JobWorkflow.JobPostWorkflowEngine;
import controllers.scheduler.SchedulerManager;
import models.entity.Recruiter.RecruiterProfile;
import models.entity.RecruiterCreditHistory;
import models.util.Validator;
import notificationService.EmailEvent;
import notificationService.NotificationEvent;
import notificationService.SMSEvent;
import play.Logger;
import play.mvc.Controller;
import play.mvc.Result;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static play.libs.Json.toJson;

/**
 * Created by zero on 21/11/16.
 */
public class TestController extends Controller{

    public static Result testValidator(Integer methodId, String methodValue) {
        if(methodId == null || methodValue == null) {
            return badRequest();
        }
        if(methodId == 1) {
            // validate Driving Licence Number
            return ok(toJson(Validator.validateDL(methodValue)));
        } else if(methodId == 2) {
            // validate Passport Number
            return ok(toJson(Validator.validatePASSPORT(methodValue)));
        } else if(methodId == 3) {
            // validate PAN Number
            return ok(toJson(Validator.validatePAN(methodValue)));
        } else if(methodId == 4) {
            // validate Aadhaar Number
            return ok(toJson(Validator.validateAadhaar(methodValue)));
        }
        return badRequest();
    }

    public static Result testMatchingCandidate(Long jpId) {
        return ok(toJson(JobPostWorkflowEngine.getMatchingCandidate(jpId)));
    }

    public static Result testScheduler() throws InterruptedException {

        SchedulerManager schedulerManager = new SchedulerManager();
        schedulerManager.run();
        return ok();
    }

    public static Result testnotification() {
/*        Candidate candidate = Candidate.find.where().eq("CandidateMobile", "+918971739586").findUnique();
        if(candidate.getCandidateAndroidToken() != null){
            NotificationUtil.addFcmToNotificationQueue("Hi", "Interview Selected", candidate.getCandidateAndroidToken(), ServerConstants.ANDROID_INTENT_ACTIVITY_JOB_DETAIL, 967L);
            return ok("1");
        }*/
        return ok("Null token");
    }

    public static Result addressResolver(String searchAddress){
        GeoApiContext context = new GeoApiContext().setApiKey("AIzaSyBsfsIw9OwmVtQDF3JUB0VWkEleebS217g");
        GeocodingResult[] results;
        String resolvedAddress = "Enter an address as param in the url (eg: .../addressResolver/?addr=<your address>)";
        try {
            results = GeocodingApi.geocode(context,
                    searchAddress).await();

            if(results.length > 0){
                resolvedAddress = results[0].formattedAddress
                        + " ("
                        + results[0].geometry.location.lat
                        + ", "
                        + results[0].geometry.location.lng
                        + ")";
            } else{
                resolvedAddress = "Unable to resolve. Try changing the search parameter";
            }

        } catch (Exception ignored) {}

        return ok("Resolved Address and lat/lng for \"" + searchAddress + "\" --->\n\n" + resolvedAddress);

    }

    public static Result checkSmsDelivery(){
        RecruiterController.checkDeliveryStatus();
        return ok("Null token!");

    }

    public static Result testQueue() {
        for(int i =0; i<5; ++i){
            NotificationEvent notificationEvent = new SMSEvent("+918971739586", "Test Queue message " + i);
            NotificationEvent n2 = new EmailEvent("sandeep.kumar@trujobs.in", "Test Queue message ", "testing email queue");
            Global.getmNotificationHandler().addToQueue(notificationEvent);
            Global.getmNotificationHandler().addToQueue(n2);
        }
        return ok("-");
    }

    public static Result testNewPS(Long jobPostId, Long candidateId) {

        return ok(toJson(JobPostWorkflowEngine.getShortJobApplyResponse(jobPostId, candidateId)));
    }

    public static Result testApplyInShortResponse() {
        PostApplyInShortResponse response = new PostApplyInShortResponse();
        response.setStatus(PostApplyInShortResponse.Status.BAD_PARAMS);
        return ok(toJson(response));
    }

    public static Result convertOldData() {
        List<RecruiterProfile> allRecs = RecruiterProfile.find.all();

        List<RecruiterCreditHistory> allHistory = RecruiterCreditHistory.find.all();

        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MONTH, 1);
        Date expiryDate = cal.getTime();

        for(RecruiterCreditHistory history : allHistory){
            history.setRecruiterCreditPackNo(1);
            history.setCreditIsExpired(false);
            history.setLatest(false);
            history.update();
        }

        for(RecruiterProfile recruiterProfile : allRecs){
            RecruiterCreditHistory recruiterCreditHistoryLatest = RecruiterCreditHistory.find.where()
                    .eq("RecruiterProfileId", recruiterProfile.getRecruiterProfileId())
                    .eq("RecruiterCreditCategory", ServerConstants.RECRUITER_CATEGORY_CONTACT_UNLOCK)
                    .setMaxRows(1)
                    .orderBy("create_timestamp desc")
                    .findUnique();

            Boolean hasContactCredits = false;

            if(recruiterCreditHistoryLatest != null){

                hasContactCredits = true;

                recruiterCreditHistoryLatest.setRecruiterCreditPackNo(1);
                recruiterCreditHistoryLatest.setCreditIsExpired(false);
                recruiterCreditHistoryLatest.setLatest(true);

                recruiterCreditHistoryLatest.update();
            }

            List<RecruiterCreditHistory> allInterviewCreditHistory = RecruiterCreditHistory.find
                    .where()
                    .eq("RecruiterCreditCategory", ServerConstants.RECRUITER_CATEGORY_INTERVIEW_UNLOCK).findList();

            Integer packNo = 1;
            if(hasContactCredits){
                packNo = 2;
            }

            for(RecruiterCreditHistory history : allInterviewCreditHistory){
                history.setRecruiterCreditPackNo(packNo);
                history.update();
            }

            recruiterCreditHistoryLatest = RecruiterCreditHistory.find.where()
                    .eq("RecruiterProfileId", recruiterProfile.getRecruiterProfileId())
                    .eq("RecruiterCreditCategory", ServerConstants.RECRUITER_CATEGORY_INTERVIEW_UNLOCK)
                    .setMaxRows(1)
                    .orderBy("create_timestamp desc")
                    .findUnique();

            if(recruiterCreditHistoryLatest != null){
                if(hasContactCredits){
                    recruiterCreditHistoryLatest.setRecruiterCreditPackNo(2);
                } else{
                    recruiterCreditHistoryLatest.setRecruiterCreditPackNo(1);
                }

                recruiterCreditHistoryLatest.setCreditIsExpired(false);
                recruiterCreditHistoryLatest.setLatest(true);
                recruiterCreditHistoryLatest.setExpiryDate(expiryDate);

                recruiterCreditHistoryLatest.update();
            }
        }
        return ok("-");
    }

    public static Result isPrivate(String mobile) {
        return ok(toJson(CandidateService.isCandidatePrivate(mobile)));
    }
}
