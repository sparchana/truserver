package Service;

import common.TestConstants;
import controllers.businessLogic.AddressResolveService;
import models.entity.Static.Locality;
import models.util.LatLng;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import play.Application;
import play.Logger;
import play.test.TestServer;

import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.Collection;

import static org.junit.Assert.assertEquals;
import static play.libs.Json.toJson;
import static play.test.Helpers.fakeApplication;
import static play.test.Helpers.running;
import static play.test.Helpers.testServer;

/**
 * Created by zero on 25/8/16.
 */

@RunWith(Parameterized.class)
public class AddressResolverServiceTest {
    enum MethodType {
        fetchNearByLocality,
        getJSONForNearByLocality,
        resolveLocalityFor,
        getLatLngForPlaceId,
        getLocalityForPlaceId
    }
    private AddressResolverServiceTest.MethodType type;
    private Double latitude;
    private Double longitude;
    private int radius;
    private int expectedInt;
    private String expectedString;
    private String placeId;
    private AddressResolveService addressResolveService;


    @Before
    public void initialize() {
        addressResolveService = new AddressResolveService();
    }

    // Each parameter should be placed as an argument here
    // Every time runner triggers, it will pass the arguments
    public AddressResolverServiceTest(MethodType type, Double latitude, Double longitude, Integer radius,
                                      Integer expectedInt, String expectedString, String placeId) {
        this.type = type;
        this.latitude = RoundTo6Decimals(latitude);
        this.longitude = RoundTo6Decimals(longitude);
        if(radius!=null) this.radius = radius;
        if(expectedInt!=null) this.expectedInt = expectedInt;
        if(expectedString!=null) this.expectedString = expectedString;
        if(placeId!=null) this.placeId = placeId;
    }

    @Parameterized.Parameters
    public static Collection getTestDataSet() {
        // bellandur {12.926031, 77.676246}
        return Arrays.asList(new Object[][] {
                {MethodType.fetchNearByLocality, 12.926031,77.676246, null, 20, null, null},
                {MethodType.fetchNearByLocality, 12.906137,77.677868, 10, 20, null, null},
                {MethodType.getJSONForNearByLocality ,12.906137,77.677868, 500, 20, null, null},
                {MethodType.getJSONForNearByLocality ,12.906137,77.677868, 10, 1, null, null},
                {MethodType.resolveLocalityFor ,12.906137,77.677868, null, 1, "Kasavanahalli".toLowerCase(), null},
                {MethodType.resolveLocalityFor ,12.9063828,77.6774415, null, 1, "Kasavanahalli".toLowerCase(), null},
                {MethodType.getJSONForNearByLocality ,12.906137,77.677868, 10, 1, null, null},
                {MethodType.getLatLngForPlaceId , 13.1347859, 77.96529900000002, null, null, null,"ChIJndUwpLH9rTsR6X9HIfbID4M"},
                {MethodType.getLocalityForPlaceId , 12.933704, 77.66220267, null, null, null,"ChIJH8rxiYUTrjsRFYKr5ANmrCw"},
                {MethodType.getLocalityForPlaceId , 23.3706492, 85.3200837, null, null, null,"ChIJp72psxzh9DkRFVqU6q1Qgfw"},
        });
    }

    public Double RoundTo6Decimals(Double val) {
        DecimalFormat df2 = new DecimalFormat("#####.######");
        return Double.valueOf(df2.format(val));
    }

    //@Ignore
    public void testFetchNearByLocalityMethod() {
        Application fakeApp = fakeApplication();
        TestServer server = testServer(TestConstants.TEST_SERVER_PORT, fakeApp);
        running(server, () -> {
            if(type ==MethodType.fetchNearByLocality){
                assertEquals(expectedInt, addressResolveService.fetchNearByLocality(latitude, longitude, radius).size());
            }
        });
    }
    //@Ignore
    public void testGetJSONForNearByLocality() {
        Application fakeApp = fakeApplication();
        TestServer server = testServer(TestConstants.TEST_SERVER_PORT, fakeApp);
        running(server, () -> {
            if(type == MethodType.getJSONForNearByLocality){
                try {
                    JSONObject jsonObj = new JSONObject( addressResolveService.getJSONForNearByLocality(latitude, longitude, radius).toString());
                    String status = jsonObj.getString("status");
                    if(status.trim().equalsIgnoreCase("ok")) {
                        JSONArray resultArray = jsonObj.getJSONArray("results");
                        assertEquals( expectedInt, resultArray.length());
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }
    @Ignore
    public void testResolveLocalityFor() {
        Application fakeApp = fakeApplication();
        TestServer server = testServer(TestConstants.TEST_SERVER_PORT, fakeApp);
        running(server, () -> {
            if(type == MethodType.resolveLocalityFor){
                String respones = addressResolveService.resolveLocalityFor(latitude, longitude);
                assertEquals(expectedString, respones);
                Logger.info("lat/lng " + latitude+"/"+longitude + " falls withing " + respones);
            }
        });
    }
    @Ignore
    public void testToBounds() {
        LatLng latLng = new LatLng(latitude, longitude);
        System.out.println("--testing ToBounds for LatLng"+latitude+","+longitude+" : " + toJson(addressResolveService.toBounds(latLng, 2)));
    }
    @Ignore
    public void testGetLatLngForPlaceId() {
        Application fakeApp = fakeApplication();
        TestServer server = testServer(TestConstants.TEST_SERVER_PORT, fakeApp);
        running(server, () -> {
            if(type == MethodType.getLatLngForPlaceId){
                LatLng latLng = addressResolveService.getLatLngForPlaceId(placeId);
                assertEquals(latitude, (Double) latLng.latitude);
                assertEquals(longitude, (Double) latLng.longitude);
            }
        });
    }
    @Test
    public void testGetLocalityForPlaceId() {
        Application fakeApp = fakeApplication();
        TestServer server = testServer(TestConstants.TEST_SERVER_PORT, fakeApp);
        running(server, () -> {
            if(type == MethodType.getLocalityForPlaceId){
                Locality locality = addressResolveService.getLocalityForPlaceId(placeId);
                assertEquals(latitude, locality.getLat());
                assertEquals(longitude, locality.getLng());
                assertEquals(placeId, locality.getPlaceId());
            }
        });
    }
}