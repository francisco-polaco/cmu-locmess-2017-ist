package pt.ulisboa.tecnico.meic.cmu.locmess.service;

import android.content.Context;
import android.location.Location;

import com.google.android.gms.maps.model.LatLng;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;

import cz.msebera.android.httpclient.entity.StringEntity;
import pt.ulisboa.tecnico.meic.cmu.locmess.R;
import pt.ulisboa.tecnico.meic.cmu.locmess.dto.GPSLocation;
import pt.ulisboa.tecnico.meic.cmu.locmess.dto.MessageDto;
import pt.ulisboa.tecnico.meic.cmu.locmess.dto.Result;
import pt.ulisboa.tecnico.meic.cmu.locmess.handler.LocmessRestHandler;
import pt.ulisboa.tecnico.meic.cmu.locmess.interfaces.ActivityCallback;
import pt.ulisboa.tecnico.meic.cmu.locmess.interfaces.LocmessCallback;

/**
 * Created by jp_s on 4/26/2017.
 */

public class LocationWebService extends LocmessWebService implements LocmessCallback {

    private static final String TAG = LocationWebService.class.getSimpleName();
    private final Location location;

    public LocationWebService(Context context, ActivityCallback activityCallback, Location location) {
        super(context, activityCallback);
        this.location = location;
    }

    @Override
    protected void dispatch() {
        String location = new JsonService().transformObjToJson(new GPSLocation("as",
                new LatLng(this.location.getLatitude(), this.location.getLongitude()), 0));
        String endpoint = getContext().getString(R.string.webserver_endpoint_hearthbeat);
        String contentType = getContext().getString(R.string.content_type_json);
        try {
            getHttpService().post(endpoint, new StringEntity(location), contentType, new LocmessRestHandler(this));
        } catch (UnsupportedEncodingException ignored) {
        }
    }


    @Override
    public void onSuccess(Object object) {
        MessageDto[] messageDtos;
        messageDtos = (MessageDto[]) getJsonService().transformJsonToObj(object.toString(), MessageDto[].class);
        getActivityCallback().onSuccess(new Result("result", Arrays.asList(messageDtos)));
    }

    @Override
    public void onFailure(Object object) {
        System.out.println("FUCK ME!!!");
    }
}
