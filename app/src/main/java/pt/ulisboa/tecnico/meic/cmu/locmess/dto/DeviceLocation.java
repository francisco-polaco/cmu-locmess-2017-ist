package pt.ulisboa.tecnico.meic.cmu.locmess.dto;

public final class DeviceLocation {
    private double latitude;
    private double longitude;
    private double altitude;

    public DeviceLocation(android.location.Location location){
        latitude = location.getLatitude();
        longitude = location.getLongitude();
        altitude = location.getAltitude();
    }

    public double getLatitude() {
        return latitude;
    }
    public double getLongitude() {
        return longitude;
    }
    public double getAltitude() {
        return altitude;
    }

    @Override
    public String toString() {
        return "DeviceLocation{" +
                "latitude=" + latitude +
                ", longitude=" + longitude +
                ", altitude=" + altitude +
                '}';
    }
}
