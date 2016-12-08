package Scheduler;

import common.TestConstants;
import controllers.scheduler.ScheduledTask;
import controllers.scheduler.SchedulerMain;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import play.Application;
import play.Logger;
import play.test.TestServer;
import java.util.Arrays;
import java.util.Collection;
import static play.libs.Json.toJson;

/**
 * Created by zero on 8/12/16.
 *
 *
 * prod/activator-1.3.9-minimal/bin/activator "test-only Scheduler.ScheduledTaskTest"
 *
 */


import static play.libs.Json.toJson;
import static play.test.Helpers.fakeApplication;
import static play.test.Helpers.running;
import static play.test.Helpers.testServer;

@RunWith(Parameterized.class)
public class ScheduledTaskTest {
    enum MethodType {
        TEST,
        SMS,
        EMAIL,
        IN_APP_NOTIFICATION
    }

    private ScheduledTaskTest.MethodType type;

    @Before
    public void initialize() {

    }

    // Each parameter should be placed as an argument here
    // Every time runner triggers, it will pass the arguments
    public ScheduledTaskTest(ScheduledTaskTest.MethodType type) {
        this.type = type;
    }

    @Parameterized.Parameters
    public static Collection getTestDataSet() {
        return Arrays.asList(new Object[][]{
                {MethodType.TEST}
        });
    }


    @Test
    public void testSchedulerMainInterviewSMS() {
        if (type == ScheduledTaskTest.MethodType.TEST) {
            Application fakeApp = fakeApplication();
            TestServer server = testServer(TestConstants.TEST_SERVER_PORT, fakeApp);
            running(server, () -> {
                try {
                    SchedulerMain.testScheduler();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });
        }
    }
}
