package controllers.scheduler;

import controllers.scheduler.task.NextDayInterviewAlertTask;
import controllers.scheduler.task.SameDayInterviewAlertTask;

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

}
