package pt.ulisboa.tecnico.meic.cmu.locmess.service;

import android.content.Context;
import android.location.Location;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

import cz.msebera.android.httpclient.entity.StringEntity;
import pt.ulisboa.tecnico.meic.cmu.locmess.R;
import pt.ulisboa.tecnico.meic.cmu.locmess.domain.God;
import pt.ulisboa.tecnico.meic.cmu.locmess.dto.GPSLocation;
import pt.ulisboa.tecnico.meic.cmu.locmess.dto.Message;
import pt.ulisboa.tecnico.meic.cmu.locmess.dto.Token;
import pt.ulisboa.tecnico.meic.cmu.locmess.dto.User;
import pt.ulisboa.tecnico.meic.cmu.locmess.handler.LocmessRestHandler;
import pt.ulisboa.tecnico.meic.cmu.locmess.interfaces.ActivityCallback;
import pt.ulisboa.tecnico.meic.cmu.locmess.interfaces.LocmessCallback;

/**
 * Created by jp_s on 4/26/2017.
 */

public class LocationWebService extends LocmessWebService implements LocmessCallback{

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
        /*try {
            getHttpService().post(endpoint, new StringEntity(location), contentType, new LocmessRestHandler(this));
        } catch (UnsupportedEncodingException ignored) {
        }*/
    }

    public void onSuccess(JSONObject object) {
        Message message;
        if(object != null)
            message = (Message) getJsonService().transformJsonToObj(object.toString(), Message.class);
        else
            message = new Message("NULL");
        getActivityCallback().onSuccess(message);
        System.out.println(message);
    }


    public void onFailure(JSONObject object) {
        Message message;
        if(object != null)
            message = (Message) getJsonService().transformJsonToObj(object.toString(), Message.class);
        else
            message = new Message("NULL");
        getActivityCallback().onFailure(message);
        System.out.println(message);
    }


    @Override
    public void onSuccess(Object object) {

    }

    @Override
    public void onFailure(Object object) {

    }
}
