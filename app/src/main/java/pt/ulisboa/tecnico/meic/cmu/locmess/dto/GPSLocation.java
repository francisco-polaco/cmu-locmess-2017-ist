package pt.ulisboa.tecnico.meic.cmu.locmess.dto;

import com.google.android.gms.maps.model.LatLng;

public final class GPSLocation extends Location {
    public final String type = "GPSLocation";
    private double latitude;
    private double longitude;
    private float radius;

    public GPSLocation(String name, LatLng location, float radius) {
        super(name);
        latitude = location.latitude;
        longitude = location.longitude;
        this.radius = radius;
    }

    public GPSLocation(Integer id, String name, LatLng location, float radius) {
        super(id, name);
        latitude = location.latitude;
        longitude = location.longitude;
        this.radius = radius;
    }

    public GPSLocation() {
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        GPSLocation that = (GPSLocation) o;

        if (Double.compare(that.getLatitude(), getLatitude()) != 0) return false;
        if (Double.compare(that.getLongitude(), getLongitude()) != 0) return false;
        if (Float.compare(that.getRadius(), getRadius()) != 0) return false;
        return type != null ? type.equals(that.type) : that.type == null;

    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = type != null ? type.hashCode() : 0;
        temp = Double.doubleToLongBits(getLatitude());
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(getLongitude());
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        result = 31 * result + (getRadius() != +0.0f ? Float.floatToIntBits(getRadius()) : 0);
        return result;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public float getRadius() {
        return radius;
    }

    @Override
    public String toString() {
        return "[latitude=" + latitude +
                ", longitude=" + longitude +
                ", radius=" + radius + "]";
    }

}
