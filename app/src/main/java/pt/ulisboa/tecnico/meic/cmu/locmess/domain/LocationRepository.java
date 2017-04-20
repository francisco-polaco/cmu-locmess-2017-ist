package pt.ulisboa.tecnico.meic.cmu.locmess.domain;

import android.location.Location;

import java.util.ArrayList;

public class LocationRepository {
    private static final LocationRepository ourInstance = new LocationRepository();

    public static LocationRepository getInstance() {
        return ourInstance;
    }

    private LocationRepository() {
    }

    private ArrayList<Location> locationsList = new ArrayList<>();

    public synchronized void addActualLocation(Location location){
        if(locationsList.size() == Constants.MAX_SIZE) locationsList.clear();
        locationsList.add(location);
    }

    public synchronized Location getMostRecentLocation(){
        return locationsList.get(locationsList.size() - 1);
    }
}
