package pt.ulisboa.tecnico.meic.cmu.locmess.domain.geofence;

import android.Manifest;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import pt.ulisboa.tecnico.meic.cmu.locmess.domain.exception.NotInitializedException;
import pt.ulisboa.tecnico.meic.cmu.locmess.domain.exception.PermissionNotGrantedException;
import pt.ulisboa.tecnico.meic.cmu.locmess.googleapi.GoogleAPI;

public class GeofenceManager implements ResultCallback<Status> {
    private static final String TAG = GeofenceManager.class.getSimpleName();
    private static GeofenceManager ourInstance;
    private Context context;
    private PendingIntent geofencePendingIntent;

    private GeofenceManager(Context context) {
        this.context = context;
    }

    public static GeofenceManager getInstance() {
        if (ourInstance == null)
            throw new NotInitializedException(GeofenceManager.class.getSimpleName());
        return ourInstance;
    }

    public static void init(Context context) {
        ourInstance = new GeofenceManager(context);
    }

    public void addGeofence(MyGeofence toAdd) {
        Log.d(TAG, "Adding Geofence...");
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            throw new PermissionNotGrantedException("Fine location");
        }
        LocationServices.GeofencingApi.addGeofences(
                GoogleAPI.getInstance().getGoogleApiClient(),
                getGeofencingRequest(Collections.singletonList(toAdd.getGeofence())),
                getGeofencePendingIntent()
        ).setResultCallback(this);

    }

    public void addGeofences(List<MyGeofence> toAdd) {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            throw new PermissionNotGrantedException("Fine location");
        }
        if(toAdd.size() == 0) return;
        Log.d(TAG, "Adding Geofence...");
        ArrayList<Geofence> list = new ArrayList<>();
        for (MyGeofence geofence : toAdd) {
            list.add(geofence.getGeofence());
        }
        LocationServices.GeofencingApi.addGeofences(
                GoogleAPI.getInstance().getGoogleApiClient(),
                getGeofencingRequest(list),
                getGeofencePendingIntent()
        ).setResultCallback(this);

    }

    public void deleteGeofence(MyGeofence geofence) {
        LocationServices.GeofencingApi.removeGeofences(GoogleAPI.getInstance().getGoogleApiClient(),
                Collections.singletonList(geofence.getGeofence().getRequestId()));
    }

    public void deleteGeofences(List<MyGeofence> geofences) {
        ArrayList<String> list = new ArrayList<>();
        for (MyGeofence geofence : geofences) {
            list.add(geofence.getGeofence().getRequestId());
        }
        LocationServices.GeofencingApi.removeGeofences(GoogleAPI.getInstance().getGoogleApiClient(),
                list);
    }

    public void removeAllGeofences() {
        Log.d(TAG, "Removing all geofences.");
        LocationServices.GeofencingApi.removeGeofences(
                GoogleAPI.getInstance().getGoogleApiClient(),
                // This is the same pending intent that was used in addGeofences().
                getGeofencePendingIntent()
        ).setResultCallback(this);
    }

    @NonNull
    private GeofencingRequest getGeofencingRequest(List<Geofence> toAdd) {
        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();
        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER); //Utilizar dwel aqui
        builder.addGeofences(toAdd);
        Log.d(TAG, "Size geofences to add: " + toAdd.size());
        return builder.build();
    }

    private PendingIntent getGeofencePendingIntent() {
        // Reuse the PendingIntent if we already have it.
        if (geofencePendingIntent != null) {
            return geofencePendingIntent;
        }
        Intent intent = new Intent(context, GeofenceTransitionsIntentService.class);
        // We use FLAG_UPDATE_CURRENT so that we get the same pending intent back when
        // calling addGeofences() and removeGeofences().
        geofencePendingIntent = PendingIntent.getService(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        return geofencePendingIntent;
    }

    @Override
    public void onResult(@NonNull Status status) {
        if (status.isSuccess()) {
            //Log.i(TAG, "O servico executou com sucesso");
            Toast.makeText(
                    context,
                    "Geofance add/removed",
                    Toast.LENGTH_SHORT
            ).show();
        } else {
            Toast.makeText(
                    context,
                    "Error in thread",
                    Toast.LENGTH_SHORT
            ).show();
        }
    }
}
