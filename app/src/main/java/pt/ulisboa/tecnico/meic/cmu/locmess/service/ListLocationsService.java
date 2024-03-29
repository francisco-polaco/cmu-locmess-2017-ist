package pt.ulisboa.tecnico.meic.cmu.locmess.service;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.Arrays;
import java.util.List;

import pt.ulisboa.tecnico.meic.cmu.locmess.R;
import pt.ulisboa.tecnico.meic.cmu.locmess.domain.Utils;
import pt.ulisboa.tecnico.meic.cmu.locmess.dto.Location;
import pt.ulisboa.tecnico.meic.cmu.locmess.dto.Result;
import pt.ulisboa.tecnico.meic.cmu.locmess.handler.LocationDeserializer;
import pt.ulisboa.tecnico.meic.cmu.locmess.handler.LocmessRestHandler;
import pt.ulisboa.tecnico.meic.cmu.locmess.interfaces.ActivityCallback;
import pt.ulisboa.tecnico.meic.cmu.locmess.interfaces.LocmessCallback;

/**
 * Created by Diogo on 26/04/2017.
 */

public class ListLocationsService extends LocmessWebService implements LocmessCallback {

    public ListLocationsService(Context context, ActivityCallback activityCallback) {
        super(context, activityCallback);
    }

    @Override
    protected void dispatch() {
        String endpoint = getContext().getString(R.string.webserver_endpoint_location_list);
        String contentType = getContext().getString(R.string.content_type_json);
        getHttpService().get(endpoint, null, contentType, new LocmessRestHandler(this));
    }

    @Override
    public void onSuccess(Object object) {
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(Location.class, new LocationDeserializer())
                .create();

        Location[] locations = gson.fromJson(object.toString(), Location[].class);
        Result r = new Result("Successfully retrieved all locations!");
        List<Location> locationList = Arrays.asList(locations);
        r.setPiggyback(locationList);
        if (locationList.size() == 0) Utils.stopLocationUpdates(getContext());
        else Utils.startLocationUpdates(getContext());
        getActivityCallback().onSuccess(r);
    }

    @Override
    public void onFailure(Object object) {
        getActivityCallback().onFailure(new Result("Failed to retrieve the list of locations!"));
    }

}

