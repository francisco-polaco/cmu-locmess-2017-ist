package pt.ulisboa.tecnico.meic.cmu.locmess.service;

import android.content.Context;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

import cz.msebera.android.httpclient.entity.StringEntity;
import pt.ulisboa.tecnico.meic.cmu.locmess.R;
import pt.ulisboa.tecnico.meic.cmu.locmess.dto.DeviceLocation;
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
        getHttpService().post(endpoint, null, contentType, new LocmessRestHandler(this));
    }

    @Override
    public void onSuccess(JSONObject object) {
        System.out.println(object.toString());
    }

    @Override
    public void onFailure(JSONObject object) {
        System.out.println(object.toString());
    }

}

