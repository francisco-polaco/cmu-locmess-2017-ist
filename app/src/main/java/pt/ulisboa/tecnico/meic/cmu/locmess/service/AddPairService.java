package pt.ulisboa.tecnico.meic.cmu.locmess.service;

import android.content.Context;

import java.io.UnsupportedEncodingException;

import cz.msebera.android.httpclient.entity.StringEntity;
import pt.ulisboa.tecnico.meic.cmu.locmess.R;
import pt.ulisboa.tecnico.meic.cmu.locmess.dto.Message;
import pt.ulisboa.tecnico.meic.cmu.locmess.dto.Pair;
import pt.ulisboa.tecnico.meic.cmu.locmess.handler.LocmessRestHandler;
import pt.ulisboa.tecnico.meic.cmu.locmess.interfaces.ActivityCallback;
import pt.ulisboa.tecnico.meic.cmu.locmess.interfaces.LocmessCallback;

/**
 * Created by Diogo on 26/04/2017.
 */

public class AddPairService extends LocmessWebService implements LocmessCallback {

    private Pair pair;

    public AddPairService(Context context, ActivityCallback activityCallback, Pair pair) {
        super(context, activityCallback);
        this.pair = pair;
    }

    @Override
    protected void dispatch() {
        String pair = new JsonService().transformObjToJson(this.pair);
        String endpoint = getContext().getString(R.string.webserver_endpoint_user_addpair);
        String contentType = getContext().getString(R.string.content_type_json);
        try {
            getHttpService().post(endpoint, new StringEntity(pair), contentType, new LocmessRestHandler(this));
        } catch (UnsupportedEncodingException ignored) {
        }
    }

    @Override
    public void onSuccess(Object object) {
        getActivityCallback().onSuccess(
                new Message(getContext().getString(R.string.webserver_pair_create)));
    }

    @Override
    public void onFailure(Object object) {
        getActivityCallback().onFailure(
                new Message(getContext().getString(R.string.webserver_pair_create)));
    }


}
