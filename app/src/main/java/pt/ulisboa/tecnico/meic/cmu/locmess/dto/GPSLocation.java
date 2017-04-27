package pt.ulisboa.tecnico.meic.cmu.locmess.dto;

import com.google.android.gms.maps.model.LatLng;

public final class GPSLocation extends Location {
    public final String type = "GPSLocation";
    private double latitude;
    private double longitude;
    private double radius;

    public GPSLocation(String name, LatLng location, float radius) {
        super(name);
        latitude = location.latitude;
        longitude = location.longitude;
        this.radius = radius;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public double getRadius() {
        return radius;
    }

    @Override
    public String toString() {
        return "GPSLocation{" +
                "name=" + getName() +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", radius=" + radius +
                '}';
    }
}
