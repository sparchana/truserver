package Utility;

import Service.AddressResolverServiceTest;
import common.TestConstants;
import controllers.businessLogic.AddressResolveService;
import models.util.Validator;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import play.Application;
import play.Logger;
import play.test.TestServer;

import java.util.Arrays;
import java.util.Collection;

import static org.junit.Assert.assertEquals;
import static play.test.Helpers.fakeApplication;
import static play.test.Helpers.running;
import static play.test.Helpers.testServer;

/**
 * Created by zero on 21/11/16.
 *
 * prod/activator-1.3.9-minimal/bin/activator "test-only Utility.ValidatorTest"
 */

@RunWith(Parameterized.class)
public class ValidatorTest {
    private String number;
    private boolean result;
    private ValidatorTest.MethodType type;

    enum MethodType {
        validateDL
    }

    @Before
    public void initialize() {
    }

    // Each parameter should be placed as an argument here
    // Every time runner triggers, it will pass the arguments
    public ValidatorTest(ValidatorTest.MethodType type, String number, boolean result) {
        this.type = type;
        this.number = number;
        this.result = result;
    }

    @Parameterized.Parameters
    public static Collection getTestDataSet() {
        return Arrays.asList(new Object[][]{
                {MethodType.validateDL, "32", false},
                {MethodType.validateDL, "TN7520130008800", true},
                {MethodType.validateDL, "TN-7520130008800", true}
        });
    }

    @Test
    public void validateDlTest() {
        if (type == MethodType.validateDL) {
            Application fakeApp = fakeApplication();
            TestServer server = testServer(TestConstants.TEST_SERVER_PORT, fakeApp);
            running(server, () -> {
                assertEquals(this.result, Validator.validateDL(this.number));
            });
        }
    }

}
