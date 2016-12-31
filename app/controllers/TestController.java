package controllers;

import api.ServerConstants;
import api.http.httpResponse.CandidateWorkflowData;
import controllers.businessLogic.JobService;
import controllers.businessLogic.JobWorkflow.JobPostWorkflowEngine;
import controllers.scheduler.SchedulerManager;
import models.entity.Candidate;
import models.entity.JobPost;
import models.entity.Recruiter.RecruiterProfile;
import models.entity.Recruiter.Static.RecruiterCreditCategory;
import models.entity.RecruiterCreditHistory;
import models.entity.scheduler.SchedulerStats;
import models.entity.scheduler.Static.SchedulerSubType;
import models.entity.scheduler.Static.SchedulerType;
import models.util.NotificationUtil;
import models.util.SmsUtil;
import models.util.Validator;
import notificationService.EmailEvent;
import notificationService.FCMEvent;
import notificationService.NotificationEvent;
import notificationService.SMSEvent;
import play.Logger;
import play.mvc.Controller;
import play.mvc.Result;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static controllers.scheduler.SchedulerConstants.SCHEDULER_SUB_TYPE_CANDIDATE_SOD_JOB_ALERT;
import static controllers.scheduler.SchedulerConstants.SCHEDULER_TYPE_FCM;
import static controllers.scheduler.SchedulerConstants.SCHEDULER_TYPE_SMS;
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

/*
    public static Result testnotification(){
        Candidate candidate = Candidate.find.where().eq("CandidateMobile", "+918971739586").findUnique();
        if(candidate.getCandidateAndroidToken() != null){
            NotificationUtil.addFcmToNotificationQueue("Hi", "Interview Selected", candidate.getCandidateAndroidToken(), ServerConstants.ANDROID_INTENT_ACTIVITY_MY_JOBS_CONFIRMED);
            return ok("1");
        }
        return ok("Null token!");

    }
*/

    public static Result testQueue() {
        for(int i =0; i<5; ++i){
            NotificationEvent notificationEvent = new SMSEvent("+918971739586", "Test Queue message " + i);
            NotificationEvent n2 = new EmailEvent("sandeep.kumar@trujobs.in", "Test Queue message ", "testing email queue");
            Global.getmNotificationHandler().addToQueue(notificationEvent);
            Global.getmNotificationHandler().addToQueue(n2);
        }
        return ok("-");
    }

    public static Result testnotification() {
        List<RecruiterProfile> allRecs = RecruiterProfile.find.all();

        String createdBy = "Not specified";

        if(session().get("sessionUsername") != null){
            createdBy = "Support: " + session().get("sessionUsername");
        }

        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MONTH, 1);
        Date expiryDate = cal.getTime();


        for(RecruiterProfile recruiterProfile : allRecs){
            Integer availableCredits = 0;
            Integer usedCredits = 0;

            RecruiterCreditHistory recruiterCreditHistoryLatest = RecruiterCreditHistory.find.where()
                    .eq("RecruiterProfileId", recruiterProfile.getRecruiterProfileId())
                    .eq("RecruiterCreditCategory", ServerConstants.RECRUITER_CATEGORY_CONTACT_UNLOCK)
                    .setMaxRows(1)
                    .orderBy("create_timestamp desc")
                    .findUnique();

            if(recruiterCreditHistoryLatest != null){
                RecruiterCreditHistory recruiterCreditHistory = new RecruiterCreditHistory();

                availableCredits = recruiterCreditHistoryLatest.getRecruiterCreditsAvailable();
                usedCredits = recruiterCreditHistoryLatest.getRecruiterCreditsUsed();

                recruiterCreditHistory.setRecruiterCreditPackNo(1);
                recruiterCreditHistory.setCreditIsExpired(false);
                recruiterCreditHistory.setLatest(true);

                recruiterCreditHistory.setRecruiterCreditCategory(RecruiterCreditCategory.find.where()
                        .eq("recruiter_credit_category_id", ServerConstants.RECRUITER_CATEGORY_CONTACT_UNLOCK)
                        .findUnique());

                recruiterCreditHistory.setExpiryDate(expiryDate);
                recruiterCreditHistory.setUnits(availableCredits);
                recruiterCreditHistory.setRecruiterCreditsAvailable(availableCredits);
                recruiterCreditHistory.setRecruiterCreditsUsed(usedCredits);
                recruiterCreditHistory.setRecruiterProfile(recruiterProfile);
                recruiterCreditHistory.setRecruiterCreditsAddedBy(createdBy);
                recruiterCreditHistory.setCreditsAdded(availableCredits);
                recruiterCreditHistory.save();
            }

            recruiterCreditHistoryLatest = RecruiterCreditHistory.find.where()
                    .eq("RecruiterProfileId", recruiterProfile.getRecruiterProfileId())
                    .eq("RecruiterCreditCategory", ServerConstants.RECRUITER_CATEGORY_INTERVIEW_UNLOCK)
                    .setMaxRows(1)
                    .orderBy("create_timestamp desc")
                    .findUnique();

            RecruiterCreditHistory withExistingPack = RecruiterCreditHistory.find.where()
                    .eq("RecruiterProfileId", recruiterProfile.getRecruiterProfileId())
                    .isNotNull("recruiterCreditPackNo")
                    .setMaxRows(1)
                    .orderBy("recruiterCreditPackNo desc")
                    .findUnique();

            if(recruiterCreditHistoryLatest != null){
                RecruiterCreditHistory recruiterCreditHistory = new RecruiterCreditHistory();

                availableCredits = recruiterCreditHistoryLatest.getRecruiterCreditsAvailable();
                usedCredits = recruiterCreditHistoryLatest.getRecruiterCreditsUsed();

                recruiterCreditHistory.setRecruiterCreditPackNo(1);
                if(withExistingPack != null){
                    recruiterCreditHistory.setRecruiterCreditPackNo(withExistingPack.getRecruiterCreditPackNo() + 1);
                }
                recruiterCreditHistory.setCreditIsExpired(false);
                recruiterCreditHistory.setLatest(true);

                recruiterCreditHistory.setRecruiterCreditCategory(RecruiterCreditCategory.find.where()
                        .eq("recruiter_credit_category_id", ServerConstants.RECRUITER_CATEGORY_INTERVIEW_UNLOCK)
                        .findUnique());

                recruiterCreditHistory.setExpiryDate(expiryDate);
                recruiterCreditHistory.setUnits(availableCredits);
                recruiterCreditHistory.setRecruiterCreditsAvailable(availableCredits);
                recruiterCreditHistory.setRecruiterCreditsUsed(usedCredits);
                recruiterCreditHistory.setRecruiterProfile(recruiterProfile);
                recruiterCreditHistory.setRecruiterCreditsAddedBy(createdBy);
                recruiterCreditHistory.setCreditsAdded(availableCredits);
                recruiterCreditHistory.save();
            }
        }

        return ok("-");
    }

}
