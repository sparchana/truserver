package controllers;

import controllers.businessLogic.JobWorkflow.JobPostWorkflowEngine;
import controllers.scheduler.SchedulerManager;
import models.util.Validator;
import play.mvc.Controller;
import play.mvc.Result;

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

        schedulerManager.testScheduler();
        schedulerManager.testSchedulerSecond();

        return ok();
    }
}
