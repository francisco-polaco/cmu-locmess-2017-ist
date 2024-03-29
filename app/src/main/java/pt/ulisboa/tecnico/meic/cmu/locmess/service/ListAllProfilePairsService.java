package pt.ulisboa.tecnico.meic.cmu.locmess.service;

import android.content.Context;

import java.util.Arrays;
import java.util.List;

import pt.ulisboa.tecnico.meic.cmu.locmess.R;
import pt.ulisboa.tecnico.meic.cmu.locmess.dto.Pair;
import pt.ulisboa.tecnico.meic.cmu.locmess.dto.Result;
import pt.ulisboa.tecnico.meic.cmu.locmess.handler.LocmessRestHandler;
import pt.ulisboa.tecnico.meic.cmu.locmess.interfaces.ActivityCallback;
import pt.ulisboa.tecnico.meic.cmu.locmess.interfaces.LocmessCallback;

/**
 * Created by Diogo on 01/05/2017.
 */

public class ListAllProfilePairsService extends LocmessWebService implements LocmessCallback {

    public ListAllProfilePairsService(Context context, ActivityCallback activityCallback) {
        super(context, activityCallback);
    }

    @Override
    protected void dispatch() {
        String endpoint = getContext().getString(R.string.webserver_endpoint_misc_listpairs);
        String contentType = getContext().getString(R.string.content_type_json);
        getHttpService().get(endpoint, null, contentType, new LocmessRestHandler(this));
    }

    @Override
    public void onSuccess(Object object) {
        Pair[] pairsList = (Pair[]) getJsonService().transformJsonToObj(object.toString(), Pair[].class);
        List<Pair> pairs = Arrays.asList(pairsList);
        getActivityCallback().onSuccess(new Result("Successfully retrieved all profile pairs!", pairs));
    }

    @Override
    public void onFailure(Object object) {
        getActivityCallback().onFailure(new Result("Failed to retrieve all profiles pairs!"));
    }

}
