package pt.ulisboa.tecnico.meic.cmu.locmess.domain;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.concurrent.ConcurrentHashMap;

import pt.ulisboa.tecnico.meic.cmu.locmess.domain.exception.ImpossibleToGetLocationException;
import pt.ulisboa.tecnico.meic.cmu.locmess.domain.exception.PermissionNotGrantedException;
import pt.ulisboa.tecnico.meic.cmu.locmess.dto.Token;
import pt.ulisboa.tecnico.meic.cmu.locmess.googleapi.GoogleAPI;

public class God {

    private static final String TAG = God.class.getSimpleName();
    private static God ourInstance;
    private Context context;
    private Token token;

    private God(Context context) {
        this.context = context;
        loadState();
    }

    public static God getInstance() {
        if (ourInstance == null) throw new RuntimeException(God.class.getSimpleName() +
                " not initialized.");
        return ourInstance;
    }

    public static void init(Context context) {
        ourInstance = new God(context);
    }

    public boolean isLogged() {
        return token != null;
    }

    public LatLng getLastLocation() {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
            throw new PermissionNotGrantedException("Location");
        }
        Location lastLocation = LocationServices.FusedLocationApi.getLastLocation(
                GoogleAPI.getInstance().getGoogleApiClient());
        if (lastLocation == null) {
            Log.d(TAG, "getLastLocation: lastlocation is null");
            throw new ImpossibleToGetLocationException();
        }
        return new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude());

    }

    public void startLocationUpdates() {
        Log.d(TAG, "Starting up the update location service.");
        context.startService(new Intent(context, UpdateLocationService.class));
    }

    public void stopLocationUpdates() {
        Log.d(TAG, "Shutting down the update location service.");
        context.stopService(new Intent(context, UpdateLocationService.class));
    }

    public Token getToken() {
        return token;
    }

    public void setToken(Token token) {
        this.token = token;
        saveState();
    }

    public void saveState(){
        if(token == null) return;
        try(ObjectOutputStream objectOutputStream = new ObjectOutputStream(new BufferedOutputStream(
                context.openFileOutput(Constants.TOKEN_FILENAME, Context.MODE_PRIVATE)))) {
            objectOutputStream.writeObject(token);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadState(){
        try(ObjectInputStream objectInputStream = new ObjectInputStream(new BufferedInputStream(
                context.openFileInput(Constants.TOKEN_FILENAME)))){
            token = (Token) objectInputStream.readObject();
        } catch (ClassNotFoundException | IOException e) {
            e.printStackTrace();
        }
    }
}
