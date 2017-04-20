package pt.ulisboa.tecnico.meic.cmu.locmess.service;


import android.content.Context;

import org.json.JSONObject;

import pt.ulisboa.tecnico.meic.cmu.locmess.R;
import pt.ulisboa.tecnico.meic.cmu.locmess.handler.LocmessRestHandler;
import pt.ulisboa.tecnico.meic.cmu.locmess.interfaces.ActivityCallback;
import pt.ulisboa.tecnico.meic.cmu.locmess.interfaces.LocmessCallback;

public final class LogoutService extends LocmessService implements LocmessCallback {

    public LogoutService(Context context, ActivityCallback activityCallback) {
        super(context, activityCallback);
    }

    @Override
    protected void dispatch() {
        String endpoint = getContext().getString(R.string.webserver_endpoint_user_logout);
        String contentType = getContext().getString(R.string.content_type_json);
        getHttpService().post(endpoint, null, contentType, new LocmessRestHandler(this));
    }

    @Override
    public void onSucess(JSONObject object) {
        System.out.println(object.toString());
    }

    @Override
    public void onFailure(JSONObject object) {
        System.out.println(object.toString());
    }
}
