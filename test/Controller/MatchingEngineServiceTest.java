package Controller;

import controllers.businessLogic.MatchingEngineService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.Collection;

import static org.junit.Assert.assertEquals;

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

    private MatchingEngineService matchingEngineService;
    @Before
    public void initialize() {
        matchingEngineService = new MatchingEngineService();
    }

    // Each parameter should be placed as an argument here
    // Every time runner triggers, it will pass the arguments
    // from parameters we defined in MatchingEngineService.getDistanceFromCenter() method
    public MatchingEngineServiceTest(Double centerLat, Double centerLng, Double pointLat,
                                     Double pointLng, Double expectedResult) {
        this.centerLat = centerLat;
        this.centerLng = centerLng;
        this.pointLat = pointLat;
        this.pointLng = pointLng;
        this.expectedResult = expectedResult;
    }
    /**
     * validated using following url
     * http://www.movable-type.co.uk/scripts/latlong.html
     */
    public Double RoundTo2Decimals(Double val) {
        DecimalFormat df2 = new DecimalFormat("#####.##");
        return Double.valueOf(df2.format(val));
    }

    @Parameterized.Parameters
    public static Collection getDistanceFromCenter(){
        return Arrays.asList(new Object[][]{
                {12.926031, 77.676246, 12.927923, 77.627108, 5.33},
                {12.826031, 77.276246, 12.927923, 77.627108, 39.68}
        });
    }


    @Test
    public void testMatchingEngineService() {
        System.out.println("[test case] Parameter center(lat/lnt):"+centerLat+"/"+centerLng+", point(lat/lng): " + pointLat + "/"+pointLng);
        assertEquals(expectedResult,
                RoundTo2Decimals(matchingEngineService.getDistanceFromCenter(centerLat, centerLng, pointLat, pointLng)));
    }
}
