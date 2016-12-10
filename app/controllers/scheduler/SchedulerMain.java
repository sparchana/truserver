package controllers.scheduler;

import java.util.Timer;

/**
 * Created by zero on 8/12/16.
 */
public class SchedulerMain {

    public static void testScheduler() throws InterruptedException {
        Timer time = new Timer(); // Instantiate Timer Object
        ScheduledTask st = new ScheduledTask(); // Instantiate ScheduledTask class
        time.schedule(st, 2000, 6000); // Create Repetitively task for every 1 secs

//        //for test .
//        for (int i = 0; i <= 5; i++) {
//            System.out.println("Execution in Main Thread...." + i);
//            Thread.sleep(2000);
//            if (i == 5) {
//                System.out.println("Application Terminates");
//                System.exit(0);
//            }
//        }
    }
}
