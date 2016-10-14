package controllers.businessLogic;

/**
 * Created by zero on 25/8/16.
 */

import api.ServerConstants;
import models.entity.Static.Locality;
import models.util.LatLng;
import models.util.LatLngBounds;
import models.util.SmsUtil;
import models.util.SphericalUtil;
import org.apache.commons.lang3.text.WordUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import play.Logger;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.*;

import static com.avaje.ebean.Expr.eq;
import static com.avaje.ebean.Expr.like;
import static com.avaje.ebean.Expr.or;
import static models.util.Util.RoundTo6Decimals;
import static play.libs.Json.toJson;


/**
 * Address Resolve Service receives a {latitude, longitude} pair
 * and tries to determine an exact/approximate valid address for a given pair.
 * This module handles the ambiguity in address resolution provided by google's reverse geocoding API (latlng to address).
 *
 * Some lat/lng aren't fully resolved to locality level hence this module tries to find
 * a nearby valid resolved address.
 *
 * This module also inserts a newly found locality name along with its right latlng, into db
 *
 * In every vicinity, most of the time the end word is city name. Since that is also trimmed off, it
 * decreases count of city name. Hence increases the count of locality name.
 *
 * toBeRemovedList is a list which is a bag_of_city_name which is used in Sanitization
 * before counting happens. Hence further increases change of getting good data.
 *
 * Note: Precision of Lat/Lng in Db is upto 6 decimals, DB auto rounds if it exceeds
 * Also try using RoundTo6Decimals() method available in utils, in comparing between lat/lng from google_db and tru_db
 *
 *
 * There is always a chance of error in resolution. Further optimization should reduce it
 *
 * Tag: v2.1
 */

public class AddressResolveService {

    /* EXTERNAL API URL */
    private static final String GOOGLE_MAPS_API_BASE_URL = "https://maps.googleapis.com/maps/api";
    private static final String TYPE_GEOCODE = "/geocode";
    private static final String TYPE_PLACE = "/place";
    private static final String NEAR_BY_SEARCH = "/nearbysearch";
    private static final String OUT_JSON = "/json";
    private static final String API_KEY = ServerConstants.GOOGLE_SERVER_API_KEY;
    private static final int DEFAULT_RADIUS = 500; // in meters
    private static final int RADIUS_INCREMENT = 200; // in meters
    private static final int API_CALL_LIMIT = 5;

    private static final int DEFAULT_BOUND_RADIUS_IN_KM = 2; // ofcourse in km

    protected static double latitude;
    protected static double longitude;

    public AddressResolveService(){
    }

    /* Sanitization params*/
    private static String[] toBeRemovedList = {"Bangalore", "bengaluru","Karnataka","India"};

    public static Locality getLocalityForLatLng(Double appxLatitude, Double appxLongitude) {
        List<String> nearByAddressList = new ArrayList<>();
        nearByAddressList.addAll(fetchNearByLocality(appxLatitude, appxLongitude, null));
        Locality locality = Locality.find.setMaxRows(1).where().eq("localityName", determineLocality(nearByAddressList).trim().toLowerCase()).findUnique();
        if(locality == null) {
            Logger.info("Locality is null!!");
        } else if((locality.getLat()==null || locality.getLat() == 0 || locality.getPlaceId() == null)) {
            locality = insertOrUpdateLocality(locality.getLocalityName(), appxLatitude, appxLongitude);
        }
        return locality;
    }

    public static Locality getLocalityForPlaceId(String placeId){
        LatLng latLng = getLatLngForPlaceId(placeId);
        Locality locality =  Locality.find.setMaxRows(1).where().eq("placeId", placeId).findUnique();
        if(locality== null){
            locality = getLocalityForLatLng(latLng.latitude, latLng.longitude);
        }
        /* Modify Locality object to contain the given latlng instead of locality's latlng */
        if(locality!= null && locality.getLat() !=0 &&
                (locality.getLat() != latitude && latitude != 0 || locality.getLng() != longitude && longitude != 0)){
            locality.setLat(latLng.latitude);
            locality.setLng(latLng.longitude);
        }
        return locality;
    }

    public static LatLng getLatLngForPlaceId(String placeId){
        LatLng latLng = null;
        Locality locality =  Locality.find.setMaxRows(1).where().eq("placeId", placeId).findUnique();
        if(locality!= null){
            return new LatLng(locality.getLat(), locality.getLng());
        } else {
            StringBuilder sb = new StringBuilder(GOOGLE_MAPS_API_BASE_URL + TYPE_GEOCODE + OUT_JSON);
            sb.append("?key=" + API_KEY);
            sb.append("&place_id=" + placeId);

            /* getJson String */
            StringBuilder jsonResults = executeUrl(sb.toString());

            /* parse Json */
            try {
                JSONObject jsonObj = new JSONObject(jsonResults.toString());
                /**
                 * It will always return only one result.
                 */
                if(jsonObj.getString("status").trim().equalsIgnoreCase("ok")){
                    JSONObject location = jsonObj.getJSONArray("results")
                            .getJSONObject(0)
                            .getJSONObject("geometry")
                            .getJSONObject("location");

                    //parseAndGetLocality(jsonResults);
                    latLng = new LatLng(RoundTo6Decimals(location.getDouble("lat")), RoundTo6Decimals(location.getDouble("lng"))) ;
                    return latLng;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
        return latLng;
    }

    public static List<String> fetchNearByLocality(Double latitude, Double longitude, Integer radius) {
        setLatLngGlobally(latitude, longitude);
        if(radius == null || radius == 0) radius = DEFAULT_RADIUS; // Default: start within 500 meters
        List<String> nearbyLocalityAddressList = new ArrayList<>();

        JSONObject jsonObj;
        JSONArray jsonResultArray = null;
        // Create a JSON object hierarchy from the results
            int count = 0;
            while( count < API_CALL_LIMIT ){
                /* Get JSON */
                StringBuilder jsonResults = getJSONForNearByLocality(latitude, longitude, radius);

                /* Parse JSON */
                try {
                    // Log.d(TAG, jsonResults.toString());
                    jsonObj = new JSONObject(jsonResults.toString());
                    String status = jsonObj.getString("status");
                    if(status.trim().equalsIgnoreCase("ok")) {
                        jsonResultArray = jsonObj.getJSONArray("results");
                        //Logger.info("result size: "+jsonResultArray.length());
                        if(jsonResultArray.length() >=20) {
                           break;
                        }
                    }
                    /* TODO instead of increment linear try exponential increment */
                    radius += RADIUS_INCREMENT;
                    count++;
                } catch (JSONException e) {
                    Logger.error("Cannot process JSON results", e);
                }
            }

            /* after proper json object is fetched */
            /* extract vicinity */
            if(jsonResultArray != null){
                for (int i = 0; i < jsonResultArray.length(); ++i) {
                    try {
                        JSONObject placeOfInterest = jsonResultArray.getJSONObject(i);
                        String placeAddress = placeOfInterest.getString("vicinity").trim();
                    /* decreases the count of city from address since mostly the city name appears at the end of address */
                        placeAddress = placeAddress.lastIndexOf(",") > placeAddress.indexOf(",")? placeAddress.substring(0, placeAddress.lastIndexOf(",")) : placeAddress;
                        nearbyLocalityAddressList.add(placeAddress.trim());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

        return nearbyLocalityAddressList;
    }

    public static Locality insertOrUpdateLocality(String localityName, Double lat, Double lng){

        // Log.d(TAG, jsonResults.toString());
        StringBuilder jsonResults = getJSONForAddressToLatLng(localityName, lat, lng);

        Locality freshLocality = parseAndGetLocality(jsonResults);
        Logger.info("locality:"+toJson(freshLocality));
        return freshLocality;
    }

    private static String determineLocality(List<String> localityList) {
        Map<String, Integer> countByLocality = new HashMap<String, Integer>();
        StringBuilder sb = new StringBuilder();
        for(String fullAddress : localityList){
            sb.append(fullAddress.toLowerCase().trim()+", ");
        }
        String allAddress = performSanitization(sb.toString());
        List<String> finalWordList = Arrays.asList(allAddress.toString().split("\\s*,\\s*"));
        for (String locality: finalWordList) {
            locality = locality.trim().toLowerCase().replace(".", "");
            if(!locality.trim().isEmpty() && Arrays.asList(locality.split("\\s* \\s*")).size() < 4 ){
                if (countByLocality.containsKey(locality)) {
                    countByLocality.put(locality, countByLocality.get(locality) + 1);
                } else {
                    countByLocality.put(locality, 1);
                }
            }
        }
        return getMostFrequentLocality(countByLocality);
    }

    private static Locality parseAndGetLocality(StringBuilder jsonResults) {
        Locality freshLocality = null;
        JSONObject jsonObj;
        JSONArray jsonResultArray;
        Double latitude = 0D;
        Double longitude = 0D;
        String locationName = null;
        String locationNameShort = null;
        String placeId = null;
        String cityName = null;
        String stateName = null;
        String country = null;
        /* Parse JSON */
        try {
            jsonObj = new JSONObject(jsonResults.toString());
            String status = jsonObj.getString("status");
            if(status.trim().equalsIgnoreCase("ok")) {
                jsonResultArray = jsonObj.getJSONArray("results");
                for(int i = 0; i<jsonResultArray.length(); ++i){
                    Boolean isDesiredData = false;
                    JSONObject addressJsonObj = jsonResultArray.getJSONObject(i);
                    JSONArray address_components = addressJsonObj.getJSONArray("address_components");
                    /**
                     *
                     * Data set is limited to 20 by api, hence below code shouldn't be a time hogging task
                     * TODO: still find a better way to do the same
                     */
                    /* loop through all address_component to find city, state, country*/
                    for(int k = 0; k< address_components.length(); ++k){
                        JSONObject objectOfInterest = address_components.getJSONObject(k);
                        JSONArray tempTypesArray = objectOfInterest.getJSONArray("types");
                        //Logger.info("objOfInterest"+objectOfInterest);
                        for(int j = 0; j < tempTypesArray.length(); ++j) {
                            /* Below locality name will be overriden at every address_component
                            *  since api's hierarchy is such that sublocality_2 is found before sublocality_1
                            *  and both have sublocality as type. hence in hierarchy whichever element with type sublocality
                            *  is found at last that value persists through out the process
                            *
                            *  administrative_area_level_2 gives city/district level info
                            * */
                            if(tempTypesArray.get(j).toString().equalsIgnoreCase("sublocality")
                                    || tempTypesArray.get(j).toString().equalsIgnoreCase("route")
                                    || tempTypesArray.get(j).toString().equalsIgnoreCase("neighborhood") ) {
                                locationName = objectOfInterest.getString("long_name");
                                locationNameShort = objectOfInterest.getString("short_name");
                                Logger.info("Found locationName: "+locationName );
                                isDesiredData = true;
                            } else if(tempTypesArray.get(j).toString().equalsIgnoreCase("locality")) {
                                if(locationName != null){
                                    cityName = objectOfInterest.getString("long_name");
                                    Logger.info("cityName: "+cityName);
                                } else {
                                    locationName = objectOfInterest.getString("long_name");
                                    locationNameShort = objectOfInterest.getString("short_name");
                                    Logger.info("LocationName:: " + locationName);
                                    isDesiredData = true;
                                }
                            }
                            else if(cityName == null && tempTypesArray.get(j).toString().equalsIgnoreCase("administrative_area_level_2")) {
                                cityName = objectOfInterest.getString("long_name");
                                Logger.info("cityName: "+cityName);
                            }
                            else if(tempTypesArray.get(j).toString().equalsIgnoreCase("administrative_area_level_1")) {
                                stateName = objectOfInterest.getString("long_name");
                                Logger.info("stateName: "+stateName);
                            }
                            else if(tempTypesArray.get(j).toString().equalsIgnoreCase("country")) {
                                country = objectOfInterest.getString("long_name");
                                Logger.info("country: "+country);
                            }
                        }
                    }
                    if(cityName!= null && locationName!=null && cityName.trim().equalsIgnoreCase(locationName.trim())){
                        return null;
                    }

                    if(isDesiredData){
                        JSONObject geometry = addressJsonObj.getJSONObject("geometry");
                        latitude = RoundTo6Decimals(geometry.getJSONObject("location").getDouble("lat"));
                        longitude =RoundTo6Decimals(geometry.getJSONObject("location").getDouble("lng"));
                        placeId = addressJsonObj.getString("place_id");
                        Logger.info("DesiredData Found - placeId: "+ placeId + " locationName:"+ locationName);

                        freshLocality  = Locality.find.setMaxRows(1).where()
                                .or(eq("placeId", placeId), or(
                                        like("localityName", locationName.trim().toLowerCase() + "%"),
                                        like("localityName", locationNameShort.trim().toLowerCase() + "%")
                                    )
                                )
                                .findUnique();

                        if(freshLocality==null) {
                            freshLocality = new Locality();
                            freshLocality.setLocalityName(WordUtils.capitalize(locationName));
                            freshLocality.setLat(latitude);
                            freshLocality.setLng(longitude);
                            freshLocality.setCity(WordUtils.capitalize(cityName));
                            freshLocality.setState(WordUtils.capitalize(stateName));
                            freshLocality.setCountry(country);
                            freshLocality.setPlaceId(placeId);
                            freshLocality.save();


                            /* Re-Check if it got saved */
                            Locality newlocality =  Locality.find.setMaxRows(1).where().eq("placeId", freshLocality.getPlaceId()).findUnique();
                            if(newlocality!= null){
                                Logger.info("Successfully saved new found locality i.e. "+freshLocality.getLocalityName()+" into db");
                                return newlocality;
                            } else {
                                Logger.info("Error while saving new found locality into db");
                            }
                        } else {
                           /* update the existing locality object if req */
                            boolean isChanged = false;
                            if(freshLocality.getPlaceId() == null || freshLocality.getPlaceId().trim().isEmpty() ){
                                freshLocality.setPlaceId(placeId); isChanged=true;
                            }
                            if(freshLocality.getLat()==null || freshLocality.getLat() == 0){
                                freshLocality.setLat(latitude); isChanged=true;
                            }
                            if(freshLocality.getLng()==null || freshLocality.getLng() == 0){
                                freshLocality.setLng(longitude); isChanged=true;
                            }
                            if(freshLocality.getCity() == null || freshLocality.getCity().trim().isEmpty()) {
                                freshLocality.setCity(cityName); isChanged=true;
                            }
                            if(freshLocality.getState() == null || freshLocality.getState().trim().isEmpty()){
                                freshLocality.setState(stateName); isChanged=true;
                            }
                            if(freshLocality.getCountry() == null || freshLocality.getCountry().trim().isEmpty()) {
                                freshLocality.setCountry(country); isChanged=true;
                            }
                            if(isChanged){
                                Logger.warn("Static Data "+freshLocality.getLocalityName() + " in Locality update");
                                freshLocality.update();
                            }
                        }
                        break;
                    } else {
                        Logger.warn("LatLng is of a Remote Area. Couldn't resolved "+locationName+" till locality level. Found Incomplete final obj of interest as : "+locationName+"-"+ cityName+"-"+stateName);
                        SmsUtil.sendLocalityNotResolvedSmsToDevTeam(locationName, cityName, stateName);
                    }
                }
            }
        } catch (JSONException e) {
            Logger.error("Cannot process JSON results", e);
            return null;
        }
        return freshLocality;
    }

    private static String performSanitization(String paragraph) {
        /*
         * Add other city names into toBeRemovedList[].
         * Any String in the list is matched in the given paragraph
         * and replaced with empty string irrespective of its case.
         *
         * TODO: add better regex expression pattern match to remove noise further
         * */
        paragraph = paragraph.toLowerCase().trim();
        for (String aToBeRemovedList : toBeRemovedList) {
            paragraph = paragraph.toLowerCase().replaceAll(aToBeRemovedList.toLowerCase(), "");
        }
        paragraph = paragraph.replaceAll(",,", ",");
        paragraph = paragraph.replaceAll(",\\s,\\s", ",");
        paragraph = paragraph.replaceAll(",\\s", ",");
        paragraph = paragraph.replaceAll(",road", ",");
        paragraph = paragraph.replaceAll(",\\sroad\\s", ",");
        return paragraph;
    }

    private static List<String> getAllLocalityNames(){
        /* TODO use query to get the same insted of for-loop*/
        List<Locality> allLocalities = Locality.find.all();
        List<String> allLocalityNameList = new ArrayList<>();
        for(Locality locality: allLocalities){
            allLocalityNameList.add(locality.getLocalityName());
        }
        return allLocalityNameList;
    }

    /**
     * Return the most probable locality name from a Map<LocalityName, count>
     * COUNT_LIMIT prevents the loop to go beyond top 2 sanitized locality name
     * since after that the map may contains vague names like cross, street, names etc
     *
     */
    private static String getMostFrequentLocality(Map<String, Integer> countByWord) {
        int COUNT_LIMIT = 2;

        Map<String, Integer> matchingLocalities = new HashMap<>();
        List<String> dbLocalityNameList = new ArrayList<>();
        dbLocalityNameList.addAll(getAllLocalityNames());
        for (String dbLocalityName : dbLocalityNameList) {
            dbLocalityName = dbLocalityName.toLowerCase().replace(".", "").trim();
            if(countByWord.containsKey(dbLocalityName)) {
                matchingLocalities.put(dbLocalityName, countByWord.get(dbLocalityName));
            }
        }

        String finalPredictedLocalityName = "";
        if(matchingLocalities.size() > 0 ) {
            finalPredictedLocalityName = sortMapByValue(matchingLocalities).entrySet().iterator().next().getKey();
            Logger.info("match found in db for:"+finalPredictedLocalityName );
        } else {
            Map<String, Integer> sortedMap = sortMapByValue(countByWord);
            Iterator it = sortedMap.entrySet().iterator();
            int n = 0;
            while (it.hasNext() && n++ < COUNT_LIMIT) {
                Map.Entry pair = (Map.Entry)it.next();
                Locality freshLocality = insertOrUpdateLocality(pair.getKey().toString(), latitude, longitude);
                if(freshLocality != null) {
                    finalPredictedLocalityName = freshLocality.getLocalityName();
                    break;
                }
            }
        }
        Logger.info("finalPredicted Locality name "+finalPredictedLocalityName);
        return finalPredictedLocalityName;
    }

    /**
    *  Sort a given map by its value in descending order
    */
    private static <K, V extends Comparable<? super V>> Map<K, V> sortMapByValue(Map<K, V> map) {
        List<Map.Entry<K, V>> list = new LinkedList<>( map.entrySet() );
        Collections.sort( list, new Comparator<Map.Entry<K, V>>() {
            @Override
            public int compare( Map.Entry<K, V> o1, Map.Entry<K, V> o2 ) {
                return ( o2.getValue() ).compareTo( o1.getValue() );
            }
        } );

        Map<K, V> result = new LinkedHashMap<>();
        for (Map.Entry<K, V> entry : list) {
            Logger.info("Locality: " + entry.getKey() +" count: " + entry.getValue());
            result.put( entry.getKey(), entry.getValue() );
        }
        return result;
    }


    /**
     * API Calls : Return Type is a String containing JSON
     */
    private static StringBuilder getJSONForNearByLocality(Double latitude, Double longitude, int radius) {
        StringBuilder jsonResults = null;

        StringBuilder sb = new StringBuilder(GOOGLE_MAPS_API_BASE_URL + TYPE_PLACE + NEAR_BY_SEARCH + OUT_JSON);
        sb.append("?key=" + API_KEY);
        sb.append("&location=" + latitude + "," + longitude);
        sb.append("&radius=" + radius);

        return executeUrl(sb.toString());
    }

    private static StringBuilder getJSONForAddressToLatLng(String addressToResolve, Double latitude, Double longitude) {

        StringBuilder sb = null;
        try {
            sb = new StringBuilder(GOOGLE_MAPS_API_BASE_URL + TYPE_GEOCODE + OUT_JSON);
            sb.append("?key=" + API_KEY);
            sb.append("&address="+ URLEncoder.encode(addressToResolve, "utf-8"));
            sb.append("&bounds="+ URLEncoder.encode(toBounds(latitude, longitude), "utf-8"));

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return executeUrl(sb.toString());
    }

    private static StringBuilder executeUrl(String urlString){
        HttpURLConnection conn = null;
        StringBuilder jsonResults = new StringBuilder();
        try{
            URL url = new URL(urlString);
            conn = (HttpURLConnection) url.openConnection();
            InputStreamReader in = new InputStreamReader(conn.getInputStream());

            Logger.warn("url: "+ urlString);
            // Load the results into a StringBuilder
            int read;
            char[] buff = new char[1024];
            while ((read = in.read(buff)) != -1) {
                jsonResults.append(buff, 0, read);
            }
        } catch (MalformedURLException e) {
            Logger.error("Error processing URL", e);
            return jsonResults;
        } catch (IOException e) {
            Logger.error("Error processing URL", e);
            return jsonResults;
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
        return jsonResults;
    }

    private static void setLatLngGlobally(Double latitude, Double longitude){
        AddressResolveService.latitude = latitude;
        AddressResolveService.longitude = longitude;
    }

    /**
     *
     *  We have 4 flavours of toBound below
     *
     */
    public static LatLngBounds toBounds(LatLng center, double radius) {
        LatLng southwest = SphericalUtil.computeOffset(center, radius * Math.sqrt(2.0), 225);
        LatLng northeast = SphericalUtil.computeOffset(center, radius * Math.sqrt(2.0), 45);
        return new LatLngBounds(southwest, northeast);
    }

    private static String toBounds(Double latitude, Double longitude) {
        return boundsToString(toBounds(new LatLng(latitude, longitude), DEFAULT_BOUND_RADIUS_IN_KM));
    }

    private static String toBounds() {
        return boundsToString(toBounds(new LatLng(latitude, longitude), DEFAULT_BOUND_RADIUS_IN_KM));
    }

    private static String toBounds(LatLng center) {
        return boundsToString(toBounds(center, DEFAULT_BOUND_RADIUS_IN_KM));
    }

    private static String boundsToString(LatLngBounds latlngbounds){
        /**
         *
         * https://developers.google.com/maps/documentation/geocoding/intro
         * The bounds parameter defines the latitude/longitude coordinates of the southwest and northeast corners of this bounding box using a pipe (|)
         *
         */
        return latlngbounds.getSouthwest().toString()+" | " + latlngbounds.getNortheast().toString();
    }

    private static StringBuilder getJSONForAddressToLatLng(String addressToResolve) {

        StringBuilder sb = null;
        try {
            sb = new StringBuilder(GOOGLE_MAPS_API_BASE_URL + TYPE_GEOCODE + OUT_JSON);
            sb.append("?key=" + API_KEY);
            sb.append("&address="+ URLEncoder.encode(addressToResolve, "utf-8"));
            sb.append("&bounds="+ URLEncoder.encode(toBounds(), "utf-8"));

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return executeUrl(sb.toString());
    }

    public static String resolveLocalityFor(Double latitude, Double longitude) {
        setLatLngGlobally(latitude, longitude);
        List<String> nearByAddressList = new ArrayList<>();
        nearByAddressList.addAll(fetchNearByLocality(latitude, longitude, null));
        return determineLocality(nearByAddressList);
    }

    public static String resolveLocalityFor(Double latitude, Double longitude, Integer radius) {
        setLatLngGlobally(latitude, longitude);
        List<String> nearyByAddressList = new ArrayList<>();
        nearyByAddressList.addAll(fetchNearByLocality(latitude, longitude, radius));
        return determineLocality(nearyByAddressList);
    }


    /**
     * Important !!
     * Both methods belows are used to prepopulate the db. It doesn't resolves all locality correctly
     * and is unstable as of now.
     *
     */
    private static StringBuilder getJSONForAddress(String addressToResolve, LatLng southwest, LatLng northeast) {
        Logger.info("addressToResolve: "+addressToResolve);
        StringBuilder sb = null;
        try {
            sb = new StringBuilder(GOOGLE_MAPS_API_BASE_URL + TYPE_GEOCODE + OUT_JSON);
            sb.append("?key=" + API_KEY);
            sb.append("&address="+ URLEncoder.encode(addressToResolve, "utf-8"));
            sb.append("&bounds="+ URLEncoder.encode(boundsToString(new LatLngBounds(southwest, northeast)), "utf-8"));

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return executeUrl(sb.toString());
    }

    public static Locality insertOrUpdateLocality(String localityName, LatLng southwest, LatLng northeast){

        // Log.d(TAG, jsonResults.toString());
        StringBuilder jsonResults = getJSONForAddress(localityName, southwest, northeast);

        Locality freshLocality = parseAndGetLocality(jsonResults);
        Logger.info("locality:"+toJson(freshLocality));
        return freshLocality;
    }
}
