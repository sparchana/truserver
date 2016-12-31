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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

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
        determineLocality,
        filterKeyWordList
    }

    private SearchJobService searchJobService;
    private MethodType type;
    private String searchText;
    private List<String> keywordList = new ArrayList<>();

    @Before
    public void setUp() throws Exception {
        searchJobService = new SearchJobService();
    }

    public SearchJobServiceTest(MethodType type, String searchText) {
        this.type = type;
        this.searchText = searchText;

        if(type == MethodType.filterKeyWordList) {
            keywordList.addAll(Arrays.asList(searchText.split(" ")));
        }
    }

    @Parameterized.Parameters
    public static Collection<Object[]> data() {
            return Arrays.asList(new Object[][]{
                    {MethodType.determineLocality, "abhay reddy layout"},
                    {MethodType.determineEducation, "less than 10th"},
                    {MethodType.determineExperience, "6 mths 2 yrs"},
                    {MethodType.determineExperience, "fresher"},
                    {MethodType.determineExperience, "4 6 yrs"},
                    {MethodType.filterKeyWordList, "ionxy global pvt ltd"},
                    {MethodType.filterKeyWordList, "Co co pvt ltd"},
                    {MethodType.filterKeyWordList, " p global pvt ltd"},
                    {MethodType.filterKeyWordList, "p pvt ltd"},
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
//    @Test
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
//    @Test
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
//    @Test
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
    @Test
    public void filterKeyWordList() throws Exception {
        if (type == MethodType.filterKeyWordList) {
            Application fakeApp = fakeApplication();
            TestServer server = testServer(TestConstants.TEST_SERVER_PORT, fakeApp);
            running(server, () -> {
                searchJobService.filterKeyWordList(this.keywordList);
            });
        }
    }
}

