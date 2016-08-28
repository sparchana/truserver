package controllers.businessLogic;

/**
 * Created by zero on 25/8/16.
 */

import api.ServerConstants;
import in.trujobs.proto.LocalityObject;
import models.entity.Static.Locality;
import models.util.LatLng;
import models.util.LatLngBounds;
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
 * TODO: modify to take "type":"route" into account
 *
 *
 */

public class AddressResolveService {

    /* EXTERNAL API URL */
    public static final String GOOGLE_MAPS_API_BASE_URL = "https://maps.googleapis.com/maps/api";
    public static final String TYPE_GEOCODE = "/geocode";
    public static final String TYPE_PLACE = "/place";
    public static final String NEAR_BY_SEARCH = "/nearbysearch";
    public static final String OUT_JSON = "/json";
    public static final String API_KEY = ServerConstants.GOOGLE_SERVER_API_KEY;
    public static final int DEFAULT_RADIUS = 500; // in meters
    public static final int RADIUS_INCREMENT = 200; // in meters
    public static final int API_CALL_LIMIT = 5;

    public static final int DEFAULT_RADIUS_IN_KM = 2; // ofcourse in km

    protected static double latitude;
    protected static double longitude;

    public AddressResolveService(){
    }

    public AddressResolveService(Double lat, Double lng){
        if(lat!=null)latitude = lat;
        if(lng!=null)longitude = lng;
    }

    /* Sanitization params*/
    public static String[] toBeRemovedList = {"Bangalore", "bengaluru","Karnataka","India"};

    public static String resolveLocalityFor(Double latitude, Double longitude) {
        new AddressResolveService(latitude, longitude);
        List<String> nearyByAddressList = new ArrayList<>();
        nearyByAddressList.addAll(fetchNearByLocality(latitude, longitude, null));
        return determineLocality(nearyByAddressList);
    }

    public static String resolveLocalityFor(Double latitude, Double longitude, Integer radius) {
        new AddressResolveService(latitude, longitude);
        List<String> nearyByAddressList = new ArrayList<>();
        nearyByAddressList.addAll(fetchNearByLocality(latitude, longitude, radius));
        return determineLocality(nearyByAddressList);
    }

    public static Locality getLocalityForLatLng(Double appxLatitude, Double appxLongitude) {
        new AddressResolveService(appxLatitude, appxLongitude);
        List<String> nearyByAddressList = new ArrayList<>();
        nearyByAddressList.addAll(fetchNearByLocality(appxLatitude, appxLongitude, null));
        return Locality.find.where().eq("localityName", determineLocality(nearyByAddressList)).findUnique();
    }

    public static Locality getLocalityForPlaceId(String placeId){
        LatLng latLng = getLatLngForPlaceId(placeId);
        return getLocalityForLatLng(latLng.latitude, latLng.longitude);
    }

    public static String determineLocality(List<String> localityList) {
        Map<String, Integer> countByLocality = new HashMap<String, Integer>();
        StringBuilder sb = new StringBuilder();
        for(String fullAddress : localityList){
            sb.append(fullAddress.toLowerCase().trim()+", ");
        }
        String allAddress = performSanitization(sb.toString());
        List<String> finalWordList = Arrays.asList(allAddress.toString().split("\\s*,\\s*"));
        for (String locality: finalWordList) {
            locality = locality.trim().toLowerCase();
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

    public static List<String> fetchNearByLocality(Double latitude, Double longitude, Integer radius) {
        new AddressResolveService(latitude, longitude);
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
            for (int i = 0; i < jsonResultArray.length(); ++i) {
                try {
                    JSONObject placeOfInterest = jsonResultArray.getJSONObject(i);
                    String placeAddress = placeOfInterest.getString("vicinity").trim();
                    placeAddress = placeAddress.lastIndexOf(",") > placeAddress.indexOf(",")? placeAddress.substring(0, placeAddress.lastIndexOf(",")) : placeAddress;
                    nearbyLocalityAddressList.add(placeAddress.trim());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        return nearbyLocalityAddressList;
    }

    private static Locality insertLocality(String localityName){
       Locality freshLocality = null;

        JSONObject jsonObj;
        JSONArray jsonResultArray;
        Double latitude = 0D;
        Double longitude = 0D;
        String locationName = null;
        String placeId = null;
        String cityName = null;
        String stateName = null;
        /* Parse JSON */
        try {
            // Log.d(TAG, jsonResults.toString());
            StringBuilder jsonResults = getJSONForAddressToLatLng(localityName);

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
                    /* loop thourgh all address_component to find city, state, country*/
                    for(int k = 0; k< address_components.length(); ++k){
                        JSONObject objectOfInterest = address_components.getJSONObject(k);
                        JSONArray tempTypesArray = objectOfInterest.getJSONArray("types");
                        //Logger.info("objOfInterest"+objectOfInterest);
                        for(int j = 0; j < tempTypesArray.length(); ++j) {
                            if(tempTypesArray.get(j).toString().equalsIgnoreCase("sublocality_level_1")) {
                                locationName = objectOfInterest.getString("long_name");
                                if(!localityName.trim().equalsIgnoreCase(locationName.trim().toLowerCase())){
                                    Logger.info("Found locality which appears to sublocality of a locality. hence " +
                                            "changing PrevFoundName: "+localityName +" to new name = "+locationName);
                                    localityName = locationName;
                                }
                                isDesiredData = true;
                            }
                            else if(tempTypesArray.get(j).toString().equalsIgnoreCase("locality")) {
                                cityName = objectOfInterest.getString("long_name");
                            }
                            else if(tempTypesArray.get(j).toString().equalsIgnoreCase("administrative_area_level_1")) {
                                stateName = objectOfInterest.getString("long_name");
                            }
                        }
                    }
                    if(cityName!= null && cityName.equalsIgnoreCase(localityName)){
                        return null;
                    }
                    if(isDesiredData){
                        JSONObject geometry = addressJsonObj.getJSONObject("geometry");
                        latitude = geometry.getJSONObject("location").getDouble("lat");
                        longitude = geometry.getJSONObject("location").getDouble("lng");
                        placeId = addressJsonObj.getString("place_id");

                        freshLocality = Locality.find.where().eq("placeId", placeId).findUnique();
                        if(freshLocality==null) {
                            freshLocality = new Locality();
                            freshLocality.setLocalityName(WordUtils.capitalize(localityName));
                            freshLocality.setLat(latitude);
                            freshLocality.setLng(longitude);
                            freshLocality.setCity(WordUtils.capitalize(cityName));
                            freshLocality.setState(WordUtils.capitalize(stateName));
                            freshLocality.setCountry("India");
                            freshLocality.setPlaceId(placeId);
                            freshLocality.save();


                            /* Re-Check if it got saved */
                            Locality newlocality = Locality.find.where().eq("placeId", freshLocality.getPlaceId()).findUnique();
                            if(newlocality!= null){
                                Logger.info("Successfully saved new found locality i.e. "+freshLocality.getLocalityName()+" into db");
                                return newlocality;
                            } else {
                                Logger.info("Error while saving new found locality into db");
                            }
                        } else {
                           /* update the existing locality object if req */
                            if(freshLocality.getPlaceId() == null || freshLocality.getPlaceId().trim().isEmpty() ){
                                freshLocality.setPlaceId(placeId);
                            }
                            if(freshLocality.getLat()!=null || freshLocality.getLat() != 0){
                                freshLocality.setLat(latitude);
                            }
                            if(freshLocality.getLng()!=null || freshLocality.getLng() != 0){
                                freshLocality.setLng(longitude);
                            }
                            Logger.warn("Static Data "+freshLocality.getLocalityName() + " in Locality update");
                            freshLocality.update();
                        }
                        break;
                    } else {
                        Logger.warn("LatLng is of a Remote Area. Couldn't resolved "+localityName+" till locality level: found Incomplete final obj of interest as : "+locationName+"-"+ cityName+"-"+stateName);
                    }

                }
            }
        } catch (JSONException e) {
            Logger.error("Cannot process JSON results", e);
        }
        return freshLocality;
    }

    public static LatLng getLatLngForPlaceId(String placeId){
        LatLng latLng = null;
        Locality locality = Locality.find.where().eq("placeId", placeId).findUnique();
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
                   latLng = new LatLng(location.getDouble("lat"), location.getDouble("lng")) ;
                    return latLng;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
        return latLng;
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
        for(int i = 0; i < toBeRemovedList.length; ++i){
            paragraph = paragraph.toLowerCase().replaceAll(toBeRemovedList[i].toLowerCase(), "");
        }
        paragraph = paragraph.replaceAll(",,", ",");
        return paragraph;
    }

    public static List<String> getAllLocalityNames(){
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
     */
    public static String getMostFrequentLocality(Map<String, Integer> countByWord) {
        int COUNT_LIMIT = 2;

        Map<String, Integer> matchingLocalities = new HashMap<>();
        List<String> dbLocalityNameList = new ArrayList<>();
        dbLocalityNameList.addAll(getAllLocalityNames());
        for (String dbLocalityName : dbLocalityNameList) {
            dbLocalityName = dbLocalityName.trim().toLowerCase();
            if(countByWord.containsKey(dbLocalityName)){
                matchingLocalities.put(dbLocalityName, countByWord.get(dbLocalityName));
            }
        }

        String finalPredictedLocalityName = "";
        if(matchingLocalities.size() >0 ){
            finalPredictedLocalityName = sortMapByValue(matchingLocalities).entrySet().iterator().next().getKey();
            Logger.info("match founnd in db for:"+finalPredictedLocalityName );
        } else {
            Map<String, Integer> sortedMap = sortMapByValue(countByWord);
            Iterator it = sortedMap.entrySet().iterator();
            int n = 0;
            while (it.hasNext() && n++ < COUNT_LIMIT) {
                Map.Entry pair = (Map.Entry)it.next();
                Locality freshLocality = insertLocality(pair.getKey().toString());
                if(freshLocality != null){
                    finalPredictedLocalityName = freshLocality.getLocalityName();
                    break;
                }
            }
        }
        return finalPredictedLocalityName;
    }

    /**
    *  Sort a given map by its value in descending order
    */
    public static <K, V extends Comparable<? super V>> Map<K, V> sortMapByValue( Map<K, V> map ) {
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
    public static StringBuilder getJSONForNearByLocality(Double latitude, Double longitude, int radius) {
        StringBuilder jsonResults = null;

        StringBuilder sb = new StringBuilder(GOOGLE_MAPS_API_BASE_URL + TYPE_PLACE + NEAR_BY_SEARCH + OUT_JSON);
        sb.append("?key=" + API_KEY);
        sb.append("&location=" + latitude + "," + longitude);
        sb.append("&radius=" + radius);

        return executeUrl(sb.toString());
    }

    public static StringBuilder getJSONForAddressToLatLng(String addressToResolve) {

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

    /**
     *
     *  We have 3 flavours of toBound below to cater most of the req. in different scenario
     *
     */

    public static String toBounds() {
        return boundsToString(toBounds(new LatLng(latitude, longitude), DEFAULT_RADIUS_IN_KM));
    }

    public static String toBounds(LatLng center) {
        return boundsToString(toBounds(center, DEFAULT_RADIUS_IN_KM));
    }

    public static LatLngBounds toBounds(LatLng center, double radius) {
        LatLng southwest = SphericalUtil.computeOffset(center, radius * Math.sqrt(2.0), 225);
        LatLng northeast = SphericalUtil.computeOffset(center, radius * Math.sqrt(2.0), 45);
        return new LatLngBounds(southwest, northeast);
    }

    public static String boundsToString(LatLngBounds latlngbounds){
        /**
         *
         * https://developers.google.com/maps/documentation/geocoding/intro
         * The bounds parameter defines the latitude/longitude coordinates of the southwest and northeast corners of this bounding box using a pipe (|)
         *
         */
        return latlngbounds.getSouthwest().toString()+" | " + latlngbounds.getNortheast().toString();
    }

    public static StringBuilder executeUrl(String urlString){
        HttpURLConnection conn = null;
        StringBuilder jsonResults = new StringBuilder();
        try{
            URL url = new URL(urlString);
            conn = (HttpURLConnection) url.openConnection();
            InputStreamReader in = new InputStreamReader(conn.getInputStream());

            Logger.warn("url: "+urlString.toString());
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
}
