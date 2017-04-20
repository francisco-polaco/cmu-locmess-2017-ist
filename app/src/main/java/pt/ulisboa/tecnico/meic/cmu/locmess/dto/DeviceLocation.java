package pt.ulisboa.tecnico.meic.cmu.locmess.dto;

import android.location.Location;

public final class DeviceLocation {
    public final String SERVER_CLASS = "GPSLocation";
    private double latitude;
    private double longitude;

    public DeviceLocation(Location location) {
        latitude = location.getLatitude();
        longitude = location.getLongitude();
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    @Override
    public String toString() {
        return "DeviceLocation{" +
                "latitude=" + latitude +
                ", longitude=" + longitude +
                '}';
    }
}
