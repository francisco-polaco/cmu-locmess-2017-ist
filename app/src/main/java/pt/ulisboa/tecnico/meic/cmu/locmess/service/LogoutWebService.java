package pt.ulisboa.tecnico.meic.cmu.locmess.service;


import android.content.Context;
import android.util.Log;

import java.io.IOException;

import pt.ulisboa.tecnico.meic.cmu.locmess.R;
import pt.ulisboa.tecnico.meic.cmu.locmess.domain.God;
import pt.ulisboa.tecnico.meic.cmu.locmess.handler.LocmessRestHandler;
import pt.ulisboa.tecnico.meic.cmu.locmess.interfaces.ActivityCallback;
import pt.ulisboa.tecnico.meic.cmu.locmess.interfaces.LocmessCallback;

public final class LogoutWebService extends LocmessWebService implements LocmessCallback {

    private static final String TAG = LogoutWebService.class.getSimpleName();

    public LogoutWebService(Context context, ActivityCallback activityCallback) {
        super(context, activityCallback);
    }

    @Override
    protected void dispatch() {
        String endpoint = getContext().getString(R.string.webserver_endpoint_user_logout);
        String contentType = getContext().getString(R.string.content_type_json);
        getHttpService().post(endpoint, null, contentType, new LocmessRestHandler(this));
    }

    @Override
    public void onSuccess(Object object) {
        Log.d(TAG, object.toString());
        try {
            God.getInstance().clearCredentials();
        } catch (IOException e) {
            e.printStackTrace();
        }
        getActivityCallback().onSuccess(null);
    }

    @Override
    public void onFailure(Object object) {
        System.out.println(object.toString());
        getActivityCallback().onFailure(null);

    }
}
