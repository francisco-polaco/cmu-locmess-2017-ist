package pt.ulisboa.tecnico.meic.cmu.locmess.service;

import android.content.Context;
import android.location.Location;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

import cz.msebera.android.httpclient.entity.StringEntity;
import pt.ulisboa.tecnico.meic.cmu.locmess.R;
import pt.ulisboa.tecnico.meic.cmu.locmess.dto.Result;
import pt.ulisboa.tecnico.meic.cmu.locmess.handler.LocmessRestHandler;
import pt.ulisboa.tecnico.meic.cmu.locmess.interfaces.ActivityCallback;
import pt.ulisboa.tecnico.meic.cmu.locmess.interfaces.LocmessCallback;

/**
 * Created by jp_s on 4/26/2017.
 */

public class LocationWebService extends LocmessWebService implements LocmessCallback{

    private static final String TAG = LocationWebService.class.getSimpleName();
    private final Location location;

    public LocationWebService(Context context, ActivityCallback activityCallback,Location location) {
        super(context, activityCallback);
        this.location = location;
    }

    @Override
    protected void dispatch() {
        String location = new JsonService().transformObjToJson(this.location);
        String endpoint = getContext().getString(R.string.webserver_endpoint_hearthbeat);
        String contentType = getContext().getString(R.string.content_type_json);
        try {
            getHttpService().post(endpoint, new StringEntity(location), contentType, new LocmessRestHandler(this));
        } catch (UnsupportedEncodingException ignored) {
        }
    }

    public void onSuccess(JSONObject object) {
        Result result;
        if(object != null)
            result = (Result) getJsonService().transformJsonToObj(object.toString(), Result.class);
        else
            result = new Result("NULL");
        getActivityCallback().onSuccess(result);
        System.out.println(result);
    }


    public void onFailure(JSONObject object) {
        Result result;
        if(object != null)
            result = (Result) getJsonService().transformJsonToObj(object.toString(), Result.class);
        else
            result = new Result("NULL");
        getActivityCallback().onFailure(result);
        System.out.println(result);
    }

    @Override
    public void onSuccess(Object object) {

    }

    @Override
    public void onFailure(Object object) {

    }
}
