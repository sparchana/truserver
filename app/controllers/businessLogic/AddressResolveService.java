package controllers.businessLogic;

/**
 * Created by zero on 25/8/16.
 */

import api.ServerConstants;
import models.entity.Static.Locality;
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
 */

public class AddressResolveService {

    /* EXTERNAL API URL */
    public static final String GOOGLE_MAPS_API_BASE_URL = "https://maps.googleapis.com/maps/api";
    public static final String TYPE_GEOCODE = "/geocode";
    public static final String TYPE_PLACE = "/place";
    public static final String NEAR_BY_SEARCH = "/nearbysearch";
    public static final String OUT_JSON = "/json";
    public static final String API_KEY = ServerConstants.GOOGLE_SERVER_API_KEY;
    public static final int RADIUS = 500; // in meters
    public static final int RADIUS_INCREMENT = 200; // in meters
    public static final int API_CALL_LIMIT = 5;

    /* Sanitization params */
    public static String[] toBeRemovedList = {"Bangalore", "bengaluru","Karnataka","India"};

    public static String resolveLocalityFor(Double latitude, Double longitude) {
        List<String> nearyByAddressList = new ArrayList<>();
        nearyByAddressList.addAll(fetchNearByLocality(latitude, longitude, null));
        return determineLocality(nearyByAddressList);
    }

    public static String resolveLocalityFor(Double latitude, Double longitude, Integer radius) {
        List<String> nearyByAddressList = new ArrayList<>();
        nearyByAddressList.addAll(fetchNearByLocality(latitude, longitude, radius));
        return determineLocality(nearyByAddressList);
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
        if(radius == null || radius == 0) radius = RADIUS; // Default: start within 500 meters
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
                    String placeAddress = placeOfInterest.getString("vicinity");
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
                    String locationName = address_components.getJSONObject(0).getString("long_name");
                    JSONArray typesArray = address_components.getJSONObject(0).getJSONArray("types");
                    JSONArray typesArrayForCity = address_components.getJSONObject(1).getJSONArray("types");
                    JSONArray typesArrayForState = address_components.getJSONObject(addressJsonObj.getJSONArray("address_components").length() - 2).getJSONArray("types");

                    for(int j = 0; j<typesArray.length(); ++j) {
                        if(typesArray.get(j).toString().equalsIgnoreCase("sublocality_level_1")) {
                            isDesiredData = true;
                        }
                    }
                    for(int j = 0; j<typesArrayForCity.length() && isDesiredData; ++j) {
                        if(typesArrayForCity.get(j).toString().equalsIgnoreCase("locality")) {
                            cityName = address_components.getJSONObject(1).getString("long_name");
                        }
                    }
                    for(int j = 0; j<typesArrayForState.length() && isDesiredData; ++j) {
                        if(typesArrayForState.get(j).toString().equalsIgnoreCase("administrative_area_level_1")) {
                            stateName = addressJsonObj.getJSONArray("address_components").getJSONObject(0).getString("long_name");
                        }
                    }
                    if(isDesiredData && locationName.trim().equalsIgnoreCase(localityName.trim().toLowerCase())){
                        JSONObject geometry = addressJsonObj.getJSONObject("geometry");
                        latitude = geometry.getJSONObject("location").getDouble("lat");
                        longitude = geometry.getJSONObject("location").getDouble("lng");
                        placeId = addressJsonObj.getString("place_id");

                        freshLocality = new Locality();
                        freshLocality.setLocalityName(WordUtils.capitalize(localityName));
                        freshLocality.setLat(latitude);
                        freshLocality.setLng(longitude);
                        freshLocality.setCity(WordUtils.capitalize(cityName));
                        freshLocality.setState(WordUtils.capitalize(stateName));
                        freshLocality.setCountry("India");
                        freshLocality.setPlaceId(placeId);
                        freshLocality.save();
                        Locality newlocality = Locality.find.where().eq("placeId", freshLocality.getPlaceId()).findUnique();
                        if(newlocality!= null){
                            Logger.info("Successfully saved new found locality i.e. "+freshLocality.getLocalityName()+" into db");
                            return newlocality;
                        } else {
                            Logger.info("Error while saving new found locality into db");
                        }
                        break;
                    }

                }
            }
        } catch (JSONException e) {
            Logger.error("Cannot process JSON results", e);
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
            finalPredictedLocalityName = sortMapByValue(countByWord).entrySet().iterator().next().getKey();
            Logger.info("no match founnd in db for:"+finalPredictedLocalityName );
            Locality freshLocality = insertLocality(finalPredictedLocalityName);
            if(freshLocality!= null){
                Logger.info("New Locality saved successfully, in db");
            } else {
                Logger.error("Error while fetching and saving new locality");
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
        HttpURLConnection conn = null;
        StringBuilder jsonResults = new StringBuilder();

        try {
            StringBuilder sb = new StringBuilder(GOOGLE_MAPS_API_BASE_URL + TYPE_PLACE + NEAR_BY_SEARCH + OUT_JSON);
            sb.append("?key=" + API_KEY);
            sb.append("&location="+latitude+","+longitude);
            sb.append("&radius="+radius);

            URL url = new URL(sb.toString());
            conn = (HttpURLConnection) url.openConnection();
            InputStreamReader in = new InputStreamReader(conn.getInputStream());

            Logger.warn("url: "+sb.toString());
            // Load the results into a StringBuilder
            int read;
            char[] buff = new char[1024];
            while ((read = in.read(buff)) != -1) {
                jsonResults.append(buff, 0, read);
            }
        } catch (MalformedURLException e) {
            Logger.error("Error processing Place Nearby Search API URL", e);
            return jsonResults;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            Logger.error("Error processing Place Nearby Search API URL", e);
            return jsonResults;
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
        return jsonResults;
    }

    public static StringBuilder getJSONForAddressToLatLng(String addressToResolve) {
        HttpURLConnection conn = null;
        StringBuilder jsonResults = new StringBuilder();

        try {
            StringBuilder sb = new StringBuilder(GOOGLE_MAPS_API_BASE_URL + TYPE_GEOCODE + OUT_JSON);
            sb.append("?key=" + API_KEY);
            sb.append("&address="+ URLEncoder.encode(addressToResolve, "utf-8"));

            URL url = new URL(sb.toString());
            conn = (HttpURLConnection) url.openConnection();
            InputStreamReader in = new InputStreamReader(conn.getInputStream());

            Logger.warn("url: "+sb.toString());
            // Load the results into a StringBuilder
            int read;
            char[] buff = new char[1024];
            while ((read = in.read(buff)) != -1) {
                jsonResults.append(buff, 0, read);
            }
        } catch (MalformedURLException e) {
            Logger.error("Error processing Address to LatLng Resolver API URL", e);
            return jsonResults;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            Logger.error("Error processing Address to LatLng Resolver API URL", e);
            return jsonResults;
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
        return jsonResults;
    }

}
