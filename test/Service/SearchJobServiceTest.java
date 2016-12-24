package Service;

import common.TestConstants;
import controllers.businessLogic.SearchJobService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import play.Application;
import play.Logger;
import play.test.TestServer;

import java.util.Arrays;
import java.util.Collection;

import static org.junit.Assert.assertNotNull;
import static play.libs.Json.toJson;
import static play.test.Helpers.*;

/**
 * Created by zero on 24/12/16.
 *
 * prod/activator-1.3.9-minimal/bin/activator "test-only Service.SearchJobServiceTest"
 */

@RunWith(Parameterized.class)
public class SearchJobServiceTest {

    private enum MethodType {
        searchJob,
        determineExperience,
        determineEducation,
        determineLocality
    }

    private SearchJobService searchJobService;
    private MethodType type;
    private String searchText;

    @Before
    public void setUp() throws Exception {
        searchJobService = new SearchJobService();
    }

    public SearchJobServiceTest(MethodType type, String searchText) {
        this.type = type;
        this.searchText = searchText;
    }

    @Parameterized.Parameters
    public static Collection<Object[]> data() {
            return Arrays.asList(new Object[][]{
                    {MethodType.determineLocality, "abhay reddy layout"},
                    {MethodType.determineEducation, "less than 10th"},
                    {MethodType.determineExperience, "6 mths 2 yrs"},
                    {MethodType.determineExperience, "fresher"},
                    {MethodType.determineExperience, "4 6 yrs"},
            });
    }

    public void searchJobTest() throws Exception {
        if (type == SearchJobServiceTest.MethodType.searchJob) {
            Application fakeApp = fakeApplication();
            TestServer server = testServer(TestConstants.TEST_SERVER_PORT, fakeApp);
            running(server, () -> {
                Logger.info("test");
                Logger.info("delay: ");
            });
        }
    }
    @Test
    public void determineLocalityTest() throws Exception {
        if (type == MethodType.determineLocality) {
            Application fakeApp = fakeApplication();
            TestServer server = testServer(TestConstants.TEST_SERVER_PORT, fakeApp);
            running(server, () -> {
                Logger.info("testText: " + this.searchText);
                assertNotNull(searchJobService.determineLocality(this.searchText));
                Logger.info("data: " + toJson(searchJobService.determineLocality(this.searchText)));
            });
        }
    }
    @Test
    public void determineEducationTest() throws Exception {
        if (type == MethodType.determineEducation) {
            Application fakeApp = fakeApplication();
            TestServer server = testServer(TestConstants.TEST_SERVER_PORT, fakeApp);
            running(server, () -> {
                Logger.info("testText: " + this.searchText);
                assertNotNull(searchJobService.determineEducation(this.searchText));
                Logger.info("data: " + toJson(searchJobService.determineEducation(this.searchText)));
            });
        }
    }
    @Test
    public void determineExperienceTest() throws Exception {
        if (type == MethodType.determineExperience) {
            Application fakeApp = fakeApplication();
            TestServer server = testServer(TestConstants.TEST_SERVER_PORT, fakeApp);
            running(server, () -> {
                Logger.info("testText: " + this.searchText);
                assertNotNull(searchJobService.determineExperience(this.searchText));
                Logger.info("data: " + toJson(searchJobService.determineExperience(this.searchText)));
            });
        }
    }
}

