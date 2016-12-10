package controllers.scheduler;

import java.util.Date;
import java.util.TimerTask;

/**
 * Created by zero on 8/12/16.
 */
public class ScheduledTask extends TimerTask {

    @Override
    public void run()
    {
        Date now = new Date();
        System.out.println("Time is :" + now); // Display current time
    }
}j
