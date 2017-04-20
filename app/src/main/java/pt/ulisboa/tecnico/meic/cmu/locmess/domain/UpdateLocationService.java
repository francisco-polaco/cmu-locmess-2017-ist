package pt.ulisboa.tecnico.meic.cmu.locmess.domain;

import android.Manifest;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;

import pt.ulisboa.tecnico.meic.cmu.locmess.googleapi.GoogleAPI;

public final class UpdateLocationService extends Service implements LocationListener {

    private static final String TAG = UpdateLocationService.class.getSimpleName();

    private static final int UPDATE_INTERVAL = 1000 * 5 * 60;
    private static final int FASTEST_UPDATE_INTERVAL = 1000;
    private static final int TIME_MS_UPDATE_ACTIVITY = 1000;
    private static final int TWO_MINUTES = 1000 * 60 * 2;
    private GoogleApiClient mGoogleApiClient;
    private LatLng mLocation;
    private Location oldLocation;

    // A request to connect to Location Services
    private LocationRequest mLocationRequest;

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    @Override
    public void onCreate() {
        mGoogleApiClient = GoogleAPI.getInstance().getGoogleApiClient();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, UpdateLocationService.class.getSimpleName() + " onStartCommand");
        super.onStartCommand(intent, flags, startId);
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy service");
        stopLocationUpdates();
        super.onDestroy();
    }

    private void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(
                mGoogleApiClient, this);
    }

    private void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            /*NotificationManager.getInstance(getApplicationContext())
                    .sendPermissionErrorLocationNotification(getApplicationContext().getString(R.string.app_name));
            */return;
        }
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL);
        mLocationRequest.setNumUpdates(5);
        mLocationRequest.setExpirationDuration(5000);
        LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient, mLocationRequest, this);
    }

    @Override
    public void onLocationChanged(Location location) {
        // comment operation below to get the old way back
        //stopLocationUpdates();
        if(isBetterLocation(oldLocation, location)) {
            oldLocation = location;
            mLocation = new LatLng(location.getLatitude(), location.getLongitude());
        }
    }

    protected boolean isBetterLocation(Location location, Location currentBestLocation) {
        if (currentBestLocation == null) {
            // A new location is always better than no location //keeping this comment because it's awesome
            return true;
        }

        long timeDelta = location.getTime() - currentBestLocation.getTime();
        boolean isSignificantlyNewer = timeDelta > TWO_MINUTES;
        boolean isSignificantlyOlder = timeDelta < -TWO_MINUTES;
        boolean isNewer = timeDelta > 0;

        if (isSignificantlyNewer) {
            return true;
        } else if (isSignificantlyOlder) {
            return false;
        }

        int accuracyDelta = (int) (location.getAccuracy() - currentBestLocation.getAccuracy());
        boolean isLessAccurate = accuracyDelta > 0;
        boolean isMoreAccurate = accuracyDelta < 0;
        boolean isSignificantlyLessAccurate = accuracyDelta > 200;

        boolean isFromSameProvider = isSameProvider(location.getProvider(),
                currentBestLocation.getProvider());

        if (isMoreAccurate) {
            return true;
        } else if (isNewer && !isLessAccurate) {
            return true;
        } else if (isNewer && !isSignificantlyLessAccurate && isFromSameProvider) {
            return true;
        }
        return false;
    }

    private boolean isSameProvider(String provider1, String provider2) {
        if (provider1 == null) {
            return provider2 == null;
        }
        return provider1.equals(provider2);
    }

}