package Controller;

import common.TestConstants;
import controllers.businessLogic.MatchingEngineService;
import models.entity.JobPost;
import models.entity.OM.JobPostToLocality;
import org.apache.commons.lang3.StringUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import play.Application;
import play.Logger;
import play.test.TestServer;

import java.text.DecimalFormat;
import java.util.*;

import static org.junit.Assert.assertEquals;
import static play.libs.Json.toJson;
import static play.test.Helpers.*;

/**
 * Created by zero on 29/7/16.
 * Trying to follow : "code a little, test a little, code a little, test a little" approach
 * Hope this will take care of it.
 *
 * prod/activator-1.3.9-minimal/bin/activator "test-only Controller.MatchingEngineServiceTest"
 */

@RunWith(Parameterized.class)
public class MatchingEngineServiceTest {
    private Double centerLat;
    private Double centerLng;
    private Double pointLat;
    private Double pointLng;
    private Double expectedResult;
    private Double radius;
    private List<Long> iDs;
    private MatchingEngineService matchingEngineService;

    @Before
    public void initialize() {
        iDs = new ArrayList<>();
        matchingEngineService = new MatchingEngineService();
    }

    // Each parameter should be placed as an argument here
    // Every time runner triggers, it will pass the arguments
    // from parameters we defined in MatchingEngineService.getDistanceFromCenter() method
    public MatchingEngineServiceTest(Double centerLat, Double centerLng, Double pointLat,
                                     Double pointLng, Double expectedResult, Double radius, List<Long> ids) {
        this.centerLat = centerLat;
        this.centerLng = centerLng;
        this.pointLat = pointLat;
        this.pointLng = pointLng;
        this.expectedResult = expectedResult;
        this.radius = radius;
        if(iDs == null){
            iDs = new ArrayList<>();
        }
        this.iDs.addAll(ids);
    }
    /**
     * validated using following url
     * @see <a href="http://www.movable-type.co.uk/scripts/latlong.html"/>
     */
    public Double RoundTo2Decimals(Double val) {
        DecimalFormat df2 = new DecimalFormat("#####.##");
        return Double.valueOf(df2.format(val));
    }

    @Parameterized.Parameters
    public static Collection getDistanceFromCenter() {
        // bellandur {12.926031, 77.676246}
        return Arrays.asList(new Object[][]{
                {12.926031, 77.676246, 12.927923, 77.627108, 5.33, 1.0, Arrays.asList( 2, 5, 11)},
                {12.926031, 77.676246, 12.927923, 77.627108, 5.33, 5.0, Arrays.asList( 2, 5, 11)},
                {12.926031, 77.676246, 12.927923, 77.627108, 5.33, 8.0, Arrays.asList( 2, 5, 11)},
                {12.926031, 77.676246, 12.927923, 77.627108, 5.33, 9.5, Arrays.asList( 2, 5, 11)},
                {12.926031, 77.676246, 12.927923, 77.627108, 5.33, 10.0, Arrays.asList(2, 5, 11)},
                {12.926031, 77.676246, 12.927923, 77.627108, 5.33, 12.0, Arrays.asList(2, 5, 11)},
                {12.926031, 77.676246, 12.927923, 77.627108, 5.33, 15.5, Arrays.asList(2, 5, 11)},
                {12.826031, 77.276246, 12.927923, 77.627108, 39.68, 1.0, Arrays.asList(2, 5, 11)}
        });
    }


    @Test
    public void testMatchingEngineService() {
        System.out.println("[test case] testMatchingEngineService: Parameter center(lat/lnt):"+centerLat+"/"+centerLng
                + ", point(lat/lng): " + pointLat + "/"+pointLng);
        assertEquals(expectedResult,
                RoundTo2Decimals(matchingEngineService.getDistanceFromCenter(centerLat, centerLng, pointLat, pointLng)));
    }

    @Test
    public void testFetcher() {
        Application fakeApp = fakeApplication();
        TestServer server = testServer(TestConstants.TEST_SERVER_PORT, fakeApp);
        running(server, () -> {
            int totalLocations = 0;
            List<String> matches = new ArrayList<>();
            List<JobPost> jobPostList = matchingEngineService.fetchMatchingJobPostForLatLng(centerLat, centerLng, radius, iDs, null);
            if(jobPostList == null) return;
            for(JobPost jobPost: jobPostList) {
                List<String> localityName = new ArrayList<>();
                totalLocations += jobPost.getJobPostToLocalityList().size();
                for(JobPostToLocality jobPostToLocality: jobPost.getJobPostToLocalityList()) {
                    localityName.add(jobPostToLocality.getLocality().getLocalityName()
                            +"("+RoundTo2Decimals(jobPostToLocality.getDistance())+" km)");
                }
                matches.add(jobPost.getCompany().getCompanyName() + "-"+jobPost.getJobPostTitle() +"<->"+
                        StringUtils.join(localityName, ','));
            }
            System.out.println("[test case] testFetcher: Parameter center(lat/lnt):"+centerLat+"/"+centerLng
                    + "-----------------------------------WITHIN "+radius+" KM ("+totalLocations
                    +") -----------------------------------------------");
            Logger.info(String.valueOf(toJson(matches)));
        });
    }
}
