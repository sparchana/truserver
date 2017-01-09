package Scheduler;

import common.TestConstants;
import controllers.scheduler.SchedulerManager;
import dao.CandidateDAO;
import models.entity.Candidate;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import play.Application;
import play.Logger;
import play.test.TestServer;

import java.util.Arrays;
import java.util.Collection;

import static play.test.Helpers.*;

/**
 * Created by zero on 8/12/16.
 * <p>
 * <p>
 * prod/activator-1.3.9-minimal/bin/activator "test-only Scheduler.SchedulerManagerTest"
 */

@RunWith(Parameterized.class)
public class SchedulerManagerTest {
    enum MethodType {
        SMS,
        EMAIL,
        IN_APP_NOTIFICATION,
        COMPUTE_DELAY,
        COMPUTE_DELAY_SDI,
        SDI,
        DC // DeActive candidate
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
                {MethodType.COMPUTE_DELAY, 20, 00, 0},
                {MethodType.SDI, 0, 2, 0},
                {MethodType.COMPUTE_DELAY_SDI, 2, 0, 0},
                {MethodType.DC, 2, 0, 0},
        });
    }


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
    public void testComputeDelayForSDI() {
        if (type == MethodType.COMPUTE_DELAY_SDI) {
            Application fakeApp = fakeApplication();
            TestServer server = testServer(TestConstants.TEST_SERVER_PORT, fakeApp);
            running(server, () -> Logger.info("delay: " + schedulerManager.computeDelayForSDI(this.hr)
            ));
        }
    }
    public void testCreateSameDayInterviewAlertEvent() {
        if (type == MethodType.SDI) {
            Application fakeApp = fakeApplication();
            TestServer server = testServer(TestConstants.TEST_SERVER_PORT, fakeApp);
            running(server, () -> {
//                schedulerManager.createSameDayInterviewAlertEvent(this.min);
            });
        }
    }

    public void testDeactiveCandidate() {
        if (type == MethodType.DC) {
            Application fakeApp = fakeApplication();
            TestServer server = testServer(TestConstants.TEST_SERVER_PORT, fakeApp);
            running(server, () -> {
                for(Candidate candidate: CandidateDAO.getNextDayDueDeActivatedCandidates()){
                    Logger.info("candidate deActivated");
                    Logger.info("candidateId: " + candidate.getCandidateId() +
                            " status: " + candidate.getCandidateStatusDetail().getCandidateStatusDetailId()
                            + " expiry: " + candidate.getCandidateStatusDetail().getStatusExpiryDate());
                }
            });
        }
    }
}
