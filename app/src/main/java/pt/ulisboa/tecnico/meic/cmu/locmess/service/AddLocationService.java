package pt.ulisboa.tecnico.meic.cmu.locmess.service;

import android.content.Context;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

import cz.msebera.android.httpclient.entity.StringEntity;
import pt.ulisboa.tecnico.meic.cmu.locmess.R;
import pt.ulisboa.tecnico.meic.cmu.locmess.dto.DeviceLocation;
import pt.ulisboa.tecnico.meic.cmu.locmess.dto.Pair;
import pt.ulisboa.tecnico.meic.cmu.locmess.handler.LocmessRestHandler;
import pt.ulisboa.tecnico.meic.cmu.locmess.interfaces.ActivityCallback;
import pt.ulisboa.tecnico.meic.cmu.locmess.interfaces.LocmessCallback;

/**
 * Created by Diogo on 26/04/2017.
 */

public class AddLocationService extends LocmessWebService implements LocmessCallback {

    private DeviceLocation location;

    public AddLocationService(Context context, ActivityCallback activityCallback, DeviceLocation location) {
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
    public void onSuccess(JSONObject object) {
        System.out.println(object.toString());
    }

    @Override
    public void onFailure(JSONObject object) {
        System.out.println(object.toString());
    }


}

