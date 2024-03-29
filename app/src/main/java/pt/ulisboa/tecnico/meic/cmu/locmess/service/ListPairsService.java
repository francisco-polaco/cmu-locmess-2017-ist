package pt.ulisboa.tecnico.meic.cmu.locmess.service;

import android.content.Context;

import java.util.Arrays;

import pt.ulisboa.tecnico.meic.cmu.locmess.R;
import pt.ulisboa.tecnico.meic.cmu.locmess.dto.Pair;
import pt.ulisboa.tecnico.meic.cmu.locmess.dto.Result;
import pt.ulisboa.tecnico.meic.cmu.locmess.handler.LocmessRestHandler;
import pt.ulisboa.tecnico.meic.cmu.locmess.interfaces.ActivityCallback;
import pt.ulisboa.tecnico.meic.cmu.locmess.interfaces.LocmessCallback;

/**
 * Created by Diogo on 26/04/2017.
 */

public class ListPairsService extends LocmessWebService implements LocmessCallback {

    public ListPairsService(Context context, ActivityCallback activityCallback) {
        super(context, activityCallback);
    }

    @Override
    protected void dispatch() {
        String endpoint = getContext().getString(R.string.webserver_endpoint_user_listpairs);
        String contentType = getContext().getString(R.string.content_type_json);
        getHttpService().get(endpoint, null, contentType, new LocmessRestHandler(this));
    }

    @Override
    public void onSuccess(Object object) {
        Pair[] pairsList = (Pair[]) getJsonService().transformJsonToObj(object.toString(), Pair[].class);
        Result r = new Result("Successfully retrieved profile pairs!", Arrays.asList(pairsList));
        getActivityCallback().onSuccess(r);
    }

    @Override
    public void onFailure(Object object) {
        getActivityCallback().onFailure(new Result("Failed to retrieve profile pairs!"));
    }

}
