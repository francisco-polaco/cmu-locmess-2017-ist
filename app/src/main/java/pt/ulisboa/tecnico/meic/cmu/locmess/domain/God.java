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
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import pt.ulisboa.tecnico.meic.cmu.locmess.domain.exception.ImpossibleToGetLocationException;
import pt.ulisboa.tecnico.meic.cmu.locmess.domain.exception.NotInitializedException;
import pt.ulisboa.tecnico.meic.cmu.locmess.domain.exception.PermissionNotGrantedException;
import pt.ulisboa.tecnico.meic.cmu.locmess.dto.Message;
import pt.ulisboa.tecnico.meic.cmu.locmess.dto.MessageDto;
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
    private TreeMap<Integer, MessageDto> messages;

    private List<Message> messageRepository;
    private TreeMap<Integer, MessageDto> cachedMessages;
    private boolean stateHasChanged = false;
    private String username;

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
        if (!Utils.isMyServiceRunning(context, UpdateLocationService.class))
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
        if (!stateHasChanged) return;
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
            username = credentials[0];
            credentials[1] = objectInputStream.readUTF();
            return credentials;
        }
    }

    public void loadState() {
        try (ObjectInputStream objectInputStream = new ObjectInputStream(new BufferedInputStream(
                context.openFileInput(Constants.CACHED_MGS)))) {
            cachedMessages = (TreeMap<Integer, MessageDto>) objectInputStream.readObject();
        } catch (ClassNotFoundException | IOException e) {
            e.printStackTrace();
            cachedMessages = new TreeMap<>();
        }
    }

    public void clearCredentials() throws IOException {
        for (String filename : new String[]{Constants.CREDENTIALS_FILENAME, Constants.CACHED_MGS}) {
            File file = new File(context.getFilesDir().getPath() + "/" + filename);
            if (file.exists()) file.delete();
        }
    }

    public List<Pair> getProfile() {
        return profile;
    }

    public void setProfile(List<Pair> profile) {
        this.profile = profile;
    }

    public List<pt.ulisboa.tecnico.meic.cmu.locmess.dto.Location> getLocations() {
        return locations;
    }

    public void setLocations(List<pt.ulisboa.tecnico.meic.cmu.locmess.dto.Location> locations) {
        if (this.locations != null && this.locations.equals(locations)) return;
        this.locations = new ArrayList<>(locations);
        if (this.locations.size() == 0) stopLocationUpdates();
        else startLocationUpdates();
    }

    public TreeMap<Integer, MessageDto> getMessages() {
        return messages;
    }

    public boolean setCachedMessages(TreeMap<Integer, MessageDto> messages) {
        Log.d(TAG, messages.toString());
        if (this.messages != null) return this.messages.equals(messages);
        else {
            this.messages = messages;
            return false;
        }
    }

    public void addToCache(Integer id) {
        if (messages.containsKey(id)) {
            cachedMessages.put(id, messages.get(id));
            stateHasChanged = true;
        }

        Log.d("CACHE", "CACHE " + id + cachedMessages.toString());
    }


    public void loadMessagesDescentralized() throws IOException {
        try (ObjectInputStream objectInputStream = new ObjectInputStream(new BufferedInputStream(
                context.openFileInput(Constants.MESSAGEREPOSITORY_FILENAME)))) {

            messageRepository = (List<Message>) objectInputStream.readObject();
        } catch (ClassNotFoundException e) {
            messageRepository = new ArrayList<>();
        }
    }

    public void saveMessagesDescentralized() throws IOException {
        try (ObjectOutputStream objectOutputStream = new ObjectOutputStream(new BufferedOutputStream(
                context.openFileOutput(Constants.MESSAGEREPOSITORY_FILENAME, Context.MODE_PRIVATE)))) {
            objectOutputStream.writeObject(messageRepository);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void addToMessageRepository(Message message) {
        messageRepository.add(message);
        //stateHasChanged = true;
    }


    public boolean amIPublisher(String publisher) {
        return publisher.equals(username);
    }

    public boolean inCache(MessageDto messageDto) {
        return cachedMessages.containsValue(messageDto);
    }
}
