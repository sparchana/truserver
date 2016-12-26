package Scheduler;

import common.TestConstants;
import controllers.scheduler.SchedulerManager;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import play.Application;
import play.Logger;
import play.test.TestServer;
import java.util.Arrays;
import java.util.Collection;

/**
 * Created by zero on 8/12/16.
 *
 *
 * prod/activator-1.3.9-minimal/bin/activator "test-only Scheduler.SchedulerManagerTest"
 *
 */


import static play.test.Helpers.fakeApplication;
import static play.test.Helpers.running;
import static play.test.Helpers.testServer;

@RunWith(Parameterized.class)
public class SchedulerManagerTest {
    enum MethodType {
        SMS,
        EMAIL,
        IN_APP_NOTIFICATION,
        COMPUTE_DELAY
    }

    private SchedulerManagerTest.MethodType type;
    private SchedulerManager schedulerManager;
    private int hr;
    private int min;
    private int sec;

    @Before
    public void initialize() {
        schedulerManager = new SchedulerManager();
    }

    // Each parameter should be placed as an argument here
    // Every time runner triggers, it will pass the arguments
    public SchedulerManagerTest(SchedulerManagerTest.MethodType type,
                                int hr,
                                int min,
                                int sec) {
        this.type = type;
        this.hr = hr;
        this.min = min;
        this.sec = sec;
    }

    @Parameterized.Parameters
    public static Collection getTestDataSet() {
        return Arrays.asList(new Object[][]{
                {MethodType.COMPUTE_DELAY, 20, 30, 0},
                {MethodType.COMPUTE_DELAY, 20, 20, 0},
                {MethodType.COMPUTE_DELAY, 20, 20, 0},
                {MethodType.COMPUTE_DELAY, 20, 00, 0}
        });
    }


    @Test
    public void testComputeDelay() {
        if (type == MethodType.COMPUTE_DELAY) {
            Application fakeApp = fakeApplication();
            TestServer server = testServer(TestConstants.TEST_SERVER_PORT, fakeApp);
            running(server, () -> Logger.info("delay: " + schedulerManager.computeDelay(
                    this.hr,
                    this.min,
                    this.sec)
            ));
        }
    }
}
