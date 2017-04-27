package pt.ulisboa.tecnico.meic.cmu.locmess.service;

import android.content.Context;

import java.io.UnsupportedEncodingException;

import cz.msebera.android.httpclient.entity.StringEntity;
import pt.ulisboa.tecnico.meic.cmu.locmess.R;
import pt.ulisboa.tecnico.meic.cmu.locmess.domain.God;
import pt.ulisboa.tecnico.meic.cmu.locmess.dto.GPSLocation;
import pt.ulisboa.tecnico.meic.cmu.locmess.dto.Message;
import pt.ulisboa.tecnico.meic.cmu.locmess.handler.LocmessRestHandler;
import pt.ulisboa.tecnico.meic.cmu.locmess.interfaces.ActivityCallback;
import pt.ulisboa.tecnico.meic.cmu.locmess.interfaces.LocmessCallback;

/**
 * Created by Diogo on 26/04/2017.
 */

public class RemoveLocationService extends LocmessWebService implements LocmessCallback {

    private GPSLocation location;
    private int index;

    public RemoveLocationService(Context context, ActivityCallback activityCallback, GPSLocation location, int index) {
        super(context, activityCallback);
        this.location = location;
        this.index = index;
    }

    @Override
    protected void dispatch() {
        String location = new JsonService().transformObjToJson(this.location);
        String endpoint = getContext().getString(R.string.webserver_endpoint_location_remove);
        String contentType = getContext().getString(R.string.content_type_json);
        try {
            getHttpService().post(endpoint, new StringEntity(location), contentType, new LocmessRestHandler(this));
        } catch (UnsupportedEncodingException ignored) {
        }
    }

    @Override
    public void onSuccess(Object object) {
        getActivityCallback().onSuccess(new Message(getContext().getString(R.string.LM_2), index));
    }

    @Override
    public void onFailure(Object object) {
        getActivityCallback().onSuccess(new Message(getContext().getString(R.string.LM_2)));
    }


}

