package pt.ulisboa.tecnico.meic.cmu.locmess.domain.geofence;

import android.support.annotation.NonNull;

import com.google.android.gms.location.Geofence;

import java.util.ArrayList;

public class MyGeofence {

    private static final String TAG = MyGeofence.class.getSimpleName();

    private String mName;
    private double mLatitude;
    private double mLongitude;
    private float mRadius;
    private transient Geofence mGeofence;

    public MyGeofence(String name, double lat, double longi, float radius) {
        mName = name;
        mLatitude = lat;
        mLongitude = longi;
        mRadius = radius;
        rebuildGeofence();
    }

    public String getMyGeofenceName() {
        return mName;
    }

    public void setMyGeofenceName(String name) {
        mName = name;
    }

    public double getMyGeofenceLatitude() {
        return mLatitude;
    }

    public void setMyGeofenceLatitude(double latitude) {
        mLatitude = latitude;
    }

    public double getMyGeofenceLongitude() {
        return mLongitude;
    }

    public void setMyGeofenceLongitude(double longitude) {
        mLongitude = longitude;
    }

    public float getMyGeofenceRadius() {
        return mRadius;
    }

    public void setMyGeofenceRadius(float radius) {
        mRadius = radius;
    }

    public Geofence getGeofence() {
        return mGeofence;
    }

    private void rebuildGeofence() {
        mGeofence = setupGeofence(mName, mLatitude, mLongitude, mRadius,
                Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_EXIT);
    }

    @NonNull
    private Geofence setupGeofence(String name, double latitude, double longitude, float radius, int transitionType) {
        return new Geofence.Builder()
                // Set the request ID of the geofence. This is a string to identify this
                // geofence.
                .setRequestId(name)
                .setCircularRegion(
                        latitude,
                        longitude,
                        radius
                )
                .setExpirationDuration(Geofence.NEVER_EXPIRE)
                .setTransitionTypes(transitionType)
                //.setLoiteringDelay(TIME_UNTIL_BEING_ALERTED)
                //.setNotificationResponsiveness(TIME_TO_REFRESH_POSITION)
                .build();
    }

    @Override
    public String toString() {
        return "MyGeofence{" +
                "mName='" + mName + '\'' +
                ", mLatitude=" + mLatitude +
                ", mLongitude=" + mLongitude +
                ", mRadius=" + mRadius +
                '}';
    }

}
