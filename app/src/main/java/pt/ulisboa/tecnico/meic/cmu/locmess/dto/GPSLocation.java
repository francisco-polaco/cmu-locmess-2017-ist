package pt.ulisboa.tecnico.meic.cmu.locmess.dto;

import com.google.android.gms.maps.model.LatLng;

public final class GPSLocation {
    public final String SERVER_CLASS = "GPSLocation";
    private String name;
    private double latitude;
    private double longitude;
    private double radius;

    public GPSLocation(String name, LatLng location, float radius) {
        this.name = name;
        latitude = location.latitude;
        longitude = location.longitude;
        this.radius = radius;
    }

    public String getName() {
        return name;
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
                "name=" + name +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", radius=" + radius +
                '}';
    }
}
