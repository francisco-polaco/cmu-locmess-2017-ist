package pt.ulisboa.tecnico.meic.cmu.locmess.domain;

import android.Manifest;
import android.app.ActivityManager;
import android.app.Application;
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
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import pt.ulisboa.tecnico.meic.cmu.locmess.domain.exception.ImpossibleToGetLocationException;
import pt.ulisboa.tecnico.meic.cmu.locmess.domain.exception.NotInitializedException;
import pt.ulisboa.tecnico.meic.cmu.locmess.domain.exception.PermissionNotGrantedException;
import pt.ulisboa.tecnico.meic.cmu.locmess.domain.geofence.GeofenceManager;
import pt.ulisboa.tecnico.meic.cmu.locmess.domain.geofence.MyGeofence;
import pt.ulisboa.tecnico.meic.cmu.locmess.dto.GPSLocation;
import pt.ulisboa.tecnico.meic.cmu.locmess.dto.Pair;
import pt.ulisboa.tecnico.meic.cmu.locmess.dto.Token;
import pt.ulisboa.tecnico.meic.cmu.locmess.googleapi.GoogleAPI;

public class God {

    private static final String TAG = God.class.getSimpleName();
    private static God ourInstance;
    private Context context;
    private Token token;
    // profile represents the key values of the user
    private List<Pair> profile;
    private ArrayList<pt.ulisboa.tecnico.meic.cmu.locmess.dto.Location> locations;
    private List<String> titleMessages;
    private List<String> cachedMessages;

    private God(Context context) {
        this.context = context;
        loadState();
    }

    public static God getInstance() {
        if (ourInstance == null) throw new NotInitializedException(God.class.getSimpleName());
        return ourInstance;
    }

    public static void init(Context context) {
        ourInstance = new God(context);
        GeofenceManager.init(context);
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
        if(!Utils.isMyServiceRunning(context, UpdateLocationService.class))
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
    }

    public void saveCredentials(String username, String password) {
        try (ObjectOutputStream objectOutputStream = new ObjectOutputStream(new BufferedOutputStream(
                context.openFileOutput(Constants.CREDENTIALS_FILENAME, Context.MODE_PRIVATE)))) {
            objectOutputStream.writeUTF(username);
            objectOutputStream.writeUTF(password);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void saveState() {
        try (ObjectOutputStream objectOutputStream = new ObjectOutputStream(new BufferedOutputStream(
                context.openFileOutput(Constants.CACHED_MGS, Context.MODE_PRIVATE)))) {
            objectOutputStream.writeObject(cachedMessages);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String[] getCredentials() throws IOException {
        String[] credentials = new String[2];
        try (ObjectInputStream objectInputStream = new ObjectInputStream(new BufferedInputStream(
                context.openFileInput(Constants.CREDENTIALS_FILENAME)))) {
            credentials[0] = objectInputStream.readUTF();
            credentials[1] = objectInputStream.readUTF();
            return credentials;
        }
    }

    public void loadState() {
        try (ObjectInputStream objectInputStream = new ObjectInputStream(new BufferedInputStream(
                context.openFileInput(Constants.CREDENTIALS_FILENAME)))) {
            cachedMessages = (List<String>) objectInputStream.readObject();
        } catch (ClassNotFoundException | IOException e) {
            cachedMessages = new ArrayList<>();
        }
    }

    public void clearCredentials() throws IOException {
        File file = new File(context.getFilesDir().getPath() + "/" + Constants.CREDENTIALS_FILENAME);
        if (file.exists()) {
            boolean delete = file.delete();
            Log.d(TAG, "File was " + delete);
        }
    }

    public void setProfile(List<Pair> profile) {
        this.profile = profile;
    }

    public List<Pair> getProfile() {
        return profile;
    }

    public void setLocations(List<pt.ulisboa.tecnico.meic.cmu.locmess.dto.Location> locations) {
        if(this.locations != null && this.locations.equals(locations))return;
        Log.d(TAG, "Setting locations and renewing all geofences.");
        this.locations = new ArrayList<>(locations);
        /*GeofenceManager.getInstance().removeAllGeofences();
        ArrayList<MyGeofence> myGeofenceArrayList = new ArrayList<>();
        for(pt.ulisboa.tecnico.meic.cmu.locmess.dto.Location l : locations){
            myGeofenceArrayList.add(new MyGeofence(l.getName(), l.getLatitude(), l.getLongitude(), l.getRadius() +1.0f));
        }
        Log.d(TAG, ""+myGeofenceArrayList);
        if(myGeofenceArrayList.size() == 0){
            // No locations, it's useless to keep trying to get our location
            // Lets wait a bit and then check again
            stopLocationUpdates();
            new Thread(){
                @Override
                public void run() {
                    try {
                        Thread.sleep(Constants.UPDATE_INTERVAL);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    startLocationUpdates();
                }
            }.start();
            return;
        }
        startLocationUpdates();
        GeofenceManager.getInstance().addGeofences(myGeofenceArrayList);*/
    }

    public List<pt.ulisboa.tecnico.meic.cmu.locmess.dto.Location> getLocations() {
        return locations;
    }

    public void setTitleMessages(List<String> titleMessages) {
        this.titleMessages = titleMessages;
    }

    public List<String> getCachedMessages() {
        return cachedMessages;
    }

    public String getMessage(int index){
        // call service
        // when return
        cachedMessages.add(titleMessages.get(index));
        return titleMessages.get(index);
    }
}
