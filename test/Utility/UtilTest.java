package Utility;

import common.TestConstants;
import models.util.Util;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import play.Application;
import play.Logger;
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
    private long startKey;
    private long endKey;
    enum MethodType {
        idToCode,
    }
    private MethodType type;


    @Before
    public void initialize() {
    }

    public UtilTest(MethodType type, long startKey, long endKey) {
        this.startKey = startKey;
        this.endKey = endKey;
        this.type = type;
    }



    @Parameterized.Parameters
    public static Collection getTestDataSet() {
        return Arrays.asList(new Object[][]{
                {MethodType.idToCode, 1, 10},
                {MethodType.idToCode, 90, 100},
                {MethodType.idToCode, 990, 1000},
                {MethodType.idToCode, 9990, 10000},
                {MethodType.idToCode, 99990, 100000},
                {MethodType.idToCode, 999990, 1000000},
                {MethodType.idToCode, 9999990, 10000000},
                {MethodType.idToCode, 99999990, 100000000},
                {MethodType.idToCode, 999999990, 1000000000},
        });
    }

    @Test
    public void idToCodeTest() {
        if (type == MethodType.idToCode) {
            Application fakeApp = fakeApplication();
            TestServer server = testServer(TestConstants.TEST_SERVER_PORT, fakeApp);
            running(server, () -> {
                for(long key = this.startKey; key<= this.endKey; key++){
                    String code = Util.idToCode(key);
                    Logger.warn("test id #"+key + " code: " + code);
                    Assert.assertEquals(key, Util.codeToId(code));

                }
            });
        }
    }

}