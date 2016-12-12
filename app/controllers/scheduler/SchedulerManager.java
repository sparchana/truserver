package controllers.scheduler;

import controllers.scheduler.task.NextDayInterviewAlertTask;
import controllers.scheduler.task.SameDayInterviewAlertTask;
import play.Logger;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by zero on 8/12/16.
 */
public class SchedulerManager implements Runnable {
    /**
     *
     * Declare property constraints here
     *
     * */

    protected Timer timer = new Timer(); // Instantiate Timer Object


    @Override
    public void run() {
        // init required
        createSameDayInterviewAlertEvent(3);
        createNextDayInterviewAlertEvent();
    }

    public void createSameDayInterviewAlertEvent(int hr) {

        if (hr < 1) return;

        long xHr = hr * 1000 * 60 * 60; // 3 hr

        SameDayInterviewAlertTask sameDayInterviewTask = new SameDayInterviewAlertTask(hr);
        timer.schedule(sameDayInterviewTask, 0, xHr);
    }

    public void createNextDayInterviewAlertEvent() {

        long oneDay = 24 * 1000 * 60 * 60; // 24 hr

        NextDayInterviewAlertTask nextDayInterviewAlertTask = new NextDayInterviewAlertTask();
        timer.schedule(nextDayInterviewAlertTask, 0, oneDay);
    }


    // test methods TODO remove the following
    public void testSchedulerSecond() {
        ScheduledTask st = new ScheduledTask(); // Instantiate ScheduledTask class
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                Date now = new Date();

                Logger.info(" second task Time is :" + now); // Display current time
            }
        }, 0, 10000); // Create Repetitively task for every 1 secs
    }

    public void testScheduler() throws InterruptedException {
        ScheduledTask st = new ScheduledTask(); // Instantiate ScheduledTask class
        timer.schedule(st, 0, 4000); // Create Repetitively task for every 1 secs
    }

}
