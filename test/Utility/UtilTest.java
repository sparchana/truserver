package Utility;

import common.TestConstants;
import models.util.Util;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import play.Application;
import play.test.TestServer;

import java.util.Arrays;
import java.util.Collection;

import static play.test.Helpers.*;

/**
 * Created by zero on 25/1/17.
 *
 * prod/activator-1.3.9-minimal/bin/activator "test-only Utility.UtilTest"
 *
 */
@RunWith(Parameterized.class)
public class UtilTest {

    private long id;
    enum MethodType {
        idToCode,
    }
    private MethodType type;


    @Before
    public void initialize() {
    }

    public UtilTest(MethodType type, long id) {
        this.id = id;
        this.type = type;
    }



    @Parameterized.Parameters
    public static Collection getTestDataSet() {
        return Arrays.asList(new Object[][]{
                {MethodType.idToCode, 0},
                {MethodType.idToCode, 1},
                {MethodType.idToCode, 11111}
        });
    }

    @Test
    public void idToCodeTest() {
        if (type == MethodType.idToCode) {
            Application fakeApp = fakeApplication();
            TestServer server = testServer(TestConstants.TEST_SERVER_PORT, fakeApp);
            running(server, () -> {
                Util.idToCode(this.id);
            });
        }
    }

}
