package controllers.businessLogic;

/**
 * Created by zero on 29/7/16.
 */

/**
 * Matching Engine Service receives a {latitude, longitude} pair and try to determine list of jobPost
 * available within a defined radius rad. List is ordered by its distance from center.
 *
 * Distance between two co-ordinates in a spherical surface is calculate using
 * Haversine formula is used in getDistanceFromCenter method to get distance from
 * given center co-ordinates {distance in KiloMeter}
 *
 * @see <a href="https://en.wikipedia.org/wiki/Haversine_formula">Haversine formula</a>
 */
public class MatchingEngineService {


    /**
     * getDistanceFromCenter takes center and point {lat, lng} value & returns distance
     * between the two co-ordinates {in kilometers}
     * for testing run MatchingEngineServiceTest.class
     */
    public Double getDistanceFromCenter(Double centerLat, Double centerLng, Double pointLat, Double pointLng){
        if(centerLat == null || centerLng == null || pointLat == null || pointLng == null){
            return null;
        }
        double earthRadius = 6371.0 ; // kilometers (or 3958.75 in miles)
        double dLat = Math.toRadians(pointLat-centerLat);
        double dLng = Math.toRadians(pointLng-centerLng);
        double sindLat = Math.sin(dLat / 2);
        double sindLng = Math.sin(dLng / 2);
        double a = Math.pow(sindLat, 2) + Math.pow(sindLng, 2)
                * Math.cos(Math.toRadians(centerLat)) * Math.cos(Math.toRadians(pointLat));
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        return c * earthRadius;
    }
}
