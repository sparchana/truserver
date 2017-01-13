package controllers;

import api.ServerConstants;
import controllers.businessLogic.JobWorkflow.JobPostWorkflowEngine;
import controllers.scheduler.SchedulerManager;
import controllers.scheduler.task.EODDebitCreditInterviewCreditTask;
import models.entity.Candidate;
import models.entity.Recruiter.RecruiterProfile;
import models.entity.Recruiter.Static.RecruiterCreditCategory;
import models.entity.RecruiterCreditHistory;
import models.util.NotificationUtil;
import models.util.Validator;
import notificationService.EmailEvent;
import notificationService.NotificationEvent;
import notificationService.SMSEvent;
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

    public static Result testnotification(){
        Candidate candidate = Candidate.find.where().eq("CandidateMobile", "+918971739586").findUnique();
        if(candidate.getCandidateAndroidToken() != null){
            NotificationUtil.addFcmToNotificationQueue("Hi", "Interview Selected", candidate.getCandidateAndroidToken(), ServerConstants.ANDROID_INTENT_ACTIVITY_JOB_DETAIL, 967L);
            return ok("1");
        }
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

}
