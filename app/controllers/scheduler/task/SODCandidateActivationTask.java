package controllers.scheduler.task;

import controllers.businessLogic.DeactivationService;
import controllers.scheduler.SchedulerManager;
import dao.CandidateDAO;
import models.entity.Candidate;
import models.entity.scheduler.SchedulerStats;
import models.entity.scheduler.Static.SchedulerSubType;
import models.entity.scheduler.Static.SchedulerType;
import play.Logger;

import java.sql.Timestamp;
import java.util.List;
import java.util.TimerTask;

import static controllers.scheduler.SchedulerConstants.*;

/**
 * Created by zero on 9/1/17.
 *
 *
 * Daily Candidate Activation Task to activate all candidate who are due activation next day
 *
 */
public class SODCandidateActivationTask extends TimerTask {

    private final ClassLoader classLoader;

    public SODCandidateActivationTask(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    @Override
    public void run() {
        Logger.info("Daily Candidate Activation Task  started...");


        Thread.currentThread().setContextClassLoader(classLoader);
        // Determine if this task is required to launch
        boolean shouldRunThisTask = SchedulerManager.checkIfEODTaskShouldRun(SCHEDULER_TYPE_SYSTEM_TASK,
                SCHEDULER_SUB_TYPE_CANDIDATE_ACTIVATION);


        if(shouldRunThisTask) {

            // building scheduler stat obj starts
            Timestamp startTime = new Timestamp(System.currentTimeMillis());
            SchedulerType type = SchedulerType.find.where()
                    .eq("schedulerTypeId", SCHEDULER_TYPE_SYSTEM_TASK).findUnique();

            SchedulerSubType subType = SchedulerSubType.find.where()
                    .eq("schedulerSubTypeId", SCHEDULER_SUB_TYPE_CANDIDATE_ACTIVATION)
                    .findUnique();

            String note = "Daily Candidate Activation Task to activate next day due activation";

            SchedulerStats newSchedulerStats = new SchedulerStats();

            newSchedulerStats.setStartTimestamp(new Timestamp(System.currentTimeMillis()) );
            // building scheduler stat obj breaks

            // Scheduler Task Actual work start
            List<Candidate> candidateList = CandidateDAO.getNextDayDueDeActivatedCandidates();

            if(candidateList.isEmpty()) {
                Logger.info("No Candidates found due activation for tomorrow !");
                return;
            }

            /**
             *  when task runner runs, there is no session available, to avoid 'No HTTP Context found Exception'
             *  we pass false as param
             * */
            DeactivationService.activateCandidates(candidateList, false);
            // Scheduler Task Actual work ends

            // building scheduler stat obj resume
            Timestamp endTime = new Timestamp(System.currentTimeMillis());
            // building scheduler stat obj ends

            // save scheduler stats
            SchedulerManager.saveNewSchedulerStats(startTime, type, subType, note, endTime, true);

            Logger.info("Daily Candidate Activation Task  Completed !!");

        }

    }
}
