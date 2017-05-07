package pt.ulisboa.tecnico.meic.cmu.locmess.service;

import android.content.Context;

import java.io.UnsupportedEncodingException;

import cz.msebera.android.httpclient.entity.StringEntity;
import pt.ulisboa.tecnico.meic.cmu.locmess.R;
import pt.ulisboa.tecnico.meic.cmu.locmess.dto.APLocation;
import pt.ulisboa.tecnico.meic.cmu.locmess.dto.GPSLocation;
import pt.ulisboa.tecnico.meic.cmu.locmess.dto.Result;
import pt.ulisboa.tecnico.meic.cmu.locmess.handler.LocmessRestHandler;
import pt.ulisboa.tecnico.meic.cmu.locmess.interfaces.ActivityCallback;
import pt.ulisboa.tecnico.meic.cmu.locmess.interfaces.LocmessCallback;

/**
 * Created by jp_s on 5/7/2017.
 */

public class AdWifiLocationService extends LocmessWebService implements LocmessCallback {

    private APLocation location;

    public AdWifiLocationService(Context context, ActivityCallback activityCallback, APLocation location) {
        super(context, activityCallback);
        this.location = location;
    }

    @Override
    protected void dispatch() {
        String location = new JsonService().transformObjToJson(this.location);
        String endpoint = getContext().getString(R.string.webserver_endpoint_location_create);
        String contentType = getContext().getString(R.string.content_type_json);
        try {
            getHttpService().post(endpoint, new StringEntity(location), contentType, new LocmessRestHandler(this));
        } catch (UnsupportedEncodingException ignored) {
        }
    }

    @Override
    public void onSuccess(Object object) {
        getActivityCallback().onSuccess(new Result("Added location with success!"));
    }

    @Override
    public void onFailure(Object object) {
        getActivityCallback().onFailure(new Result("Failed to add location!"));

    }


}
