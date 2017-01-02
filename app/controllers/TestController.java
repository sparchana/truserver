package controllers;

import api.ServerConstants;
import api.http.httpResponse.CandidateWorkflowData;
import com.sun.org.apache.xpath.internal.operations.Bool;
import controllers.businessLogic.JobService;
import controllers.businessLogic.JobWorkflow.JobPostWorkflowEngine;
import controllers.scheduler.SchedulerManager;
import dao.CandidateDAO;
import models.entity.Candidate;
import models.entity.JobPost;
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
import java.util.*;

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

    public static Result testnotification(){
/*
        Candidate candidate = Candidate.find.where().eq("CandidateMobile", "+918971739586").findUnique();
        if(candidate.getCandidateAndroidToken() != null){
            NotificationUtil.addFcmToNotificationQueue("Hi", "Interview Selected", candidate.getCandidateAndroidToken(), ServerConstants.ANDROID_INTENT_ACTIVITY_MY_JOBS_CONFIRMED);
            return ok("1");
        }
*/

        Logger.info(CandidateDAO.getAllActiveCandidateWithinProvidedDays(30).size() + " -  =========");
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
}
