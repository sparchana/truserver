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
import org.mockito.cglib.core.Local;
import play.Application;
import play.Logger;
import play.test.TestServer;

import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static play.libs.Json.toJson;
import static play.test.Helpers.fakeApplication;
import static play.test.Helpers.running;
import static play.test.Helpers.testServer;

/**
 * Created by zero on 25/8/16.
 *
 * prod/activator-1.3.9-minimal/bin/activator "test-only Service.AddressResolverServiceTest"
 */

@RunWith(Parameterized.class)
public class AddressResolverServiceTest {
    enum MethodType {
        fetchNearByLocality,
        getJSONForNearByLocality,
        resolveLocalityFor,
        getLatLngForPlaceId,
        getLocalityForPlaceId,
        insertOrUpdateLocality,
        insertOrUpdateAllEmptyLocality,
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
        if(latitude != null)this.latitude = RoundTo6Decimals(latitude);
        if(longitude != null)this.longitude = RoundTo6Decimals(longitude);
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
                {MethodType.getLocalityForPlaceId , 12.9260308, 77.6762463, null, null, null,"ChIJL-k0LnUTrjsRrmqYb6Y0ssI"},
                {MethodType.getLocalityForPlaceId , 23.3706492, 85.3200837, null, null, null,"ChIJp72psxzh9DkRFVqU6q1Qgfw"},
                {MethodType.getLocalityForPlaceId , 13.0900634, 77.4855548, null, null, null,"ChIJlUeN4XAjrjsRmPV7w7hr4-0"},
                {MethodType.insertOrUpdateLocality , 12.9240482,77.652965, null, null, "Kadubeesanahalli", null},
                {MethodType.insertOrUpdateLocality , 12.9364468, 77.6261231, null, null, "Bellandur", null},
                {MethodType.insertOrUpdateLocality , 12.8780608, 77.4443807, null, null, "Kumbalgodu", null},
                {MethodType.insertOrUpdateLocality , 12.9527508, 76.57483089999999, null, null, "Hirisave", null},
                {MethodType.insertOrUpdateLocality , 23.4839689, 85.47907029999999, null, null, "Ormanjhi", null},
                {MethodType.insertOrUpdateLocality , 12.800494, 77.713612, null, null, "Chandapura", null},
                {MethodType.insertOrUpdateAllEmptyLocality, null, null, null, null, null, null},
        });
    }

    public Double RoundTo6Decimals(Double val) {
        DecimalFormat df2 = new DecimalFormat("#####.######");
        return Double.valueOf(df2.format(val));
    }

    //@Test
    public void testFetchNearByLocalityMethod() {
        Application fakeApp = fakeApplication();
        TestServer server = testServer(TestConstants.TEST_SERVER_PORT, fakeApp);
        running(server, () -> {
            if(type ==MethodType.fetchNearByLocality){
                assertEquals(expectedInt, addressResolveService.fetchNearByLocality(latitude, longitude, radius).size());
            }
        });
    }
    //@Test
    /*public void testGetJSONForNearByLocality() {
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
    }*/

    @Test
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
    @Test
    public void testToBounds() {
        if(latitude != null && longitude != null){
            LatLng latLng = new LatLng(latitude, longitude);
            System.out.println("--testing ToBounds for LatLng"+latitude+","+longitude+" : " + toJson(addressResolveService.toBounds(latLng, 2)));
        }
    }
    @Test
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
    @Test
    public void testInsertOrUpdateLocality() {
        Application fakeApp = fakeApplication();
        TestServer server = testServer(TestConstants.TEST_SERVER_PORT, fakeApp);
        running(server, () -> {
            if(type == MethodType.insertOrUpdateLocality){
                Locality locality = addressResolveService.insertOrUpdateLocality(expectedString, latitude, longitude);
                Logger.info("locality:"+toJson(locality));
            }
        });
    }

/*    @Ignore
    public void testPopulateLocality() {
        Application fakeApp = fakeApplication();
        TestServer server = testServer(TestConstants.TEST_SERVER_PORT, fakeApp);
        running(server, () -> {
            if(type == MethodType.insertOrUpdateAllEmptyLocality) {
                List<Locality> localityList = Locality.find.where().eq("placeId", null).findList();
                // bangalore
                LatLng northeast = new LatLng(13.173706, 77.88268080000002);
                LatLng southwest = new LatLng(12.7342888, 77.379198);

                // karnataka
                northeast = new LatLng(18.4411689, 78.58601);
                southwest = new LatLng(11.593352, 74.0928801);

                for(Locality loc: localityList){
                    addressResolveService.insertOrUpdateLocality(loc.getLocalityName() + ", Bangalore", southwest, northeast);
                }
            }
        });
    }*/
}
