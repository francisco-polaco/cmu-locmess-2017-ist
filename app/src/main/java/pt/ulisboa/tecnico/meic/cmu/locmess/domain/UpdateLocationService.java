package pt.ulisboa.tecnico.meic.cmu.locmess.domain;

import android.Manifest;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;


import pt.ulisboa.tecnico.meic.cmu.locmess.R;
import pt.ulisboa.tecnico.meic.cmu.locmess.domain.exception.NotInitializedException;
import pt.ulisboa.tecnico.meic.cmu.locmess.dto.Message;
import pt.ulisboa.tecnico.meic.cmu.locmess.dto.Result;

import pt.ulisboa.tecnico.meic.cmu.locmess.googleapi.GoogleAPI;
import pt.ulisboa.tecnico.meic.cmu.locmess.interfaces.ActivityCallback;
import pt.ulisboa.tecnico.meic.cmu.locmess.interfaces.GoogleApiCallbacks;
import pt.ulisboa.tecnico.meic.cmu.locmess.service.ListLocationsService;
import pt.ulisboa.tecnico.meic.cmu.locmess.service.LocationWebService;

public final class UpdateLocationService extends Service implements LocationListener, GoogleApiCallbacks, ActivityCallback{

    private static final String TAG = UpdateLocationService.class.getSimpleName();

    private Location oldLocation;

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, UpdateLocationService.class.getSimpleName() + " onStartCommand");
        super.onStartCommand(intent, flags, startId);
        GoogleAPI.init(getApplicationContext(), false);
        GoogleAPI.getInstance().connect(this);
        try {
            God.getInstance();
        }catch (NotInitializedException e){
            God.init(getApplicationContext());
        }
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy service");
        stopLocationUpdates();
        super.onDestroy();
    }

    private void stopLocationUpdates() {
        Log.d(TAG, "Stop Location Updates");
        LocationServices.FusedLocationApi.removeLocationUpdates(
                GoogleAPI.getInstance().getGoogleApiClient(), this);
    }

    private void startLocationUpdates() {
        Log.d(TAG, "Starting Location Updates");
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            /*NotificationManager.getInstance(getApplicationContext())
                    .sendPermissionErrorLocationNotification(getApplicationContext().getString(R.string.app_name));
            */
            Log.d(TAG, "No Permissions");
            return;
        }
        LocationRequest mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(Constants.UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(Constants.FASTEST_UPDATE_INTERVAL);
        /*mLocationRequest.setNumUpdates(5);
        mLocationRequest.setExpirationDuration(5000);*/
        // TODO: BLOWING UP
        LocationServices.FusedLocationApi.requestLocationUpdates(
                GoogleAPI.getInstance().getGoogleApiClient(), mLocationRequest, this);
    }

    @Override
    public void onLocationChanged(Location location) {
       // if (isBetterLocation(oldLocation, location)) {
        Log.d(TAG, location.toString());
            oldLocation = location;
            new LocationWebService(getApplicationContext(), new ActivityCallback() {
                @Override
                public void onSuccess(Result result) {
                    Log.d(TAG, "Location updated");
                }

                @Override
                public void onFailure(Result result) {
                    Log.d(TAG, "Location update failed.");

                }
            }, location).execute();
            new ListLocationsService(getApplicationContext(), this).execute();
            //update server
            //LocationRepository.getInstance().addActualLocation(location);
       // }
    }

    protected boolean isBetterLocation(Location location, Location currentBestLocation) {
        if (currentBestLocation == null) {
            // A new location is always better than no location //keeping this comment because it's awesome
            return true;
        } else if (location == null) {
            return false;
        }

        long timeDelta = location.getTime() - currentBestLocation.getTime();
        boolean isSignificantlyNewer = timeDelta > Constants.TWO_MINUTES;
        boolean isSignificantlyOlder = timeDelta < -Constants.TWO_MINUTES;
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

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        startLocationUpdates();
    }

    @Override
    public void onConnectionSuspended(int i) {
        stopLocationUpdates();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        stopLocationUpdates();
    }

    @Override
    public void onSuccess(Result result) {
        Log.d(TAG, "Heartbeat Sucess");
    }

    @Override
    public void onFailure(Result result) {
        Log.d(TAG, "Heartbeat Failed");
    }
}