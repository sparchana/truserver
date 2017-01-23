package Utility;

import common.TestConstants;
import models.util.Util;
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
 * Created by zero on 20/1/17.
 *
 * prod/activator-1.3.9-minimal/bin/activator "test-only Utility.UtilTest"
 *
 */

@RunWith(Parameterized.class)
public class UtilTest {


    private enum MethodType {
        generateShortURL,
    }


    private Long candidateId;
    private Long jobPostId;
    private MethodType type;


    @Before
    public void setUp() throws Exception {
    }

    public UtilTest(long candidateId, long jobPostId) {
        this.candidateId = candidateId;
        this.jobPostId = jobPostId;
        this.type = type;
    }

    @Parameterized.Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{
                {100020003, 958},
                {100020003, 1079},
        });
    }

    @Test
    public void testGenerateApplyInShortURL() {
            Application fakeApp = fakeApplication();
            TestServer server = testServer(TestConstants.TEST_SERVER_PORT, fakeApp);
            running(server, () -> {
                Logger.info("URL: " + Util.generateApplyInShortUrl(this.candidateId, this.jobPostId));
            });
    }

}
