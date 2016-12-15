package controllers.scheduler;

import controllers.scheduler.task.EODRecruiterEmailAlertTask;
import controllers.scheduler.task.NextDayInterviewAlertTask;
import controllers.scheduler.task.SameDayInterviewAlertTask;
import models.entity.scheduler.SchedulerStats;
import models.entity.scheduler.Static.SchedulerSubType;
import models.entity.scheduler.Static.SchedulerType;
import play.Logger;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;


/**
 * Created by zero on 8/12/16.
 */
public class SchedulerManager implements Runnable {
    /**
     *
     * Declare property constraints here
     *
     * */

    private final Timer timer = new Timer(); // Instantiate Timer Object


    @Override
    public void run() {
        // createSameDayInterviewAlertEvent method takes time period (in hrs) as input
        createSameDayInterviewAlertEvent(3);

        createNextDayInterviewAlertEvent();

        createRecruiterEODEmailAlertEvent();
    }

    private void createSameDayInterviewAlertEvent(int hr) {

        if (hr < 1) return;

        long xHr = hr * 1000 * 60 * 60; // 3 hr

        SameDayInterviewAlertTask sameDayInterviewTask = new SameDayInterviewAlertTask(hr);
        timer.schedule(sameDayInterviewTask, 0, xHr);
    }

    private void createNextDayInterviewAlertEvent() {

        long oneDay = 24 * 1000 * 60 * 60; // 24 hr

        NextDayInterviewAlertTask nextDayInterviewAlertTask = new NextDayInterviewAlertTask();
        timer.schedule(nextDayInterviewAlertTask, 0, oneDay);
    }

    private void createRecruiterEODEmailAlertEvent(){
        long oneDay = 24 * 1000 * 60 * 60; // 24 hr

        EODRecruiterEmailAlertTask eodRecruiterEmailAlertTask = new EODRecruiterEmailAlertTask();
        timer.schedule(eodRecruiterEmailAlertTask, 0, oneDay);
    }


    public static void saveNewSchedulerStats(Timestamp startTime, SchedulerType schedulerType,
                                             SchedulerSubType schedulerSubType,
                                             String note, Timestamp endTime, boolean status){
        // make entry in db for this.
        SchedulerStats newSchedulerStats = new SchedulerStats();
        newSchedulerStats.setStartTimestamp(startTime);
        newSchedulerStats.setCompletionStatus(status);
        newSchedulerStats.setEndTimestamp(endTime);

        newSchedulerStats.setSchedulerType(schedulerType);
        newSchedulerStats.setSchedulerSubType(schedulerSubType);

        newSchedulerStats.setNote(note);
        newSchedulerStats.save();
    }


    public static boolean checkIfEODTaskShouldRun(int type, int subType){
        Calendar newCalendar = Calendar.getInstance();
        Date today = newCalendar.getTime();
        boolean shouldRunThisTask = false;

        SchedulerStats schedulerStats = SchedulerStats.find.where()
                .eq("schedulerType.schedulerTypeId", type)
                .eq("schedulerSubType.schedulerSubTypeId", subType)
                .orderBy().desc("startTimestamp").setMaxRows(1).findUnique();

        if(schedulerStats == null) {
            // task has definitely not yet running so run it
            shouldRunThisTask = true;

        } else {

            if(schedulerStats.getStartTimestamp().getDate() < (today).getDate()) {
                // last run was 'x++' hr back, hence re run
                shouldRunThisTask = true;
            }
        }
        return shouldRunThisTask;
    }
}
