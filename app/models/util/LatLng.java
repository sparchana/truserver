package models.util;

/**
 * Created by zero on 27/8/16.
 */
public class LatLng {
    /**
     * The latitude of this location.
     */
    public double latitude;

    /**
     * The longitude of this location.
     */
    public double longitude;

    /**
     * Construct a location with a latitude longitude pair.
     */
    public LatLng(double lat, double lng) {
        this.latitude = lat;
        this.longitude = lng;
    }

    @Override
    public String toString() {
        return latitude + "," + longitude;
    }
}
