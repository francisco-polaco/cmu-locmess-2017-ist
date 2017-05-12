package pt.ulisboa.tecnico.meic.cmu.locmess.service;


import android.content.Context;
import android.util.Log;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.UnsupportedEncodingException;

import cz.msebera.android.httpclient.entity.StringEntity;
import pt.ulisboa.tecnico.meic.cmu.locmess.R;
import pt.ulisboa.tecnico.meic.cmu.locmess.domain.God;
import pt.ulisboa.tecnico.meic.cmu.locmess.domain.StaticFields;
import pt.ulisboa.tecnico.meic.cmu.locmess.dto.Result;
import pt.ulisboa.tecnico.meic.cmu.locmess.dto.Token;
import pt.ulisboa.tecnico.meic.cmu.locmess.dto.User;
import pt.ulisboa.tecnico.meic.cmu.locmess.handler.LocmessRestHandler;
import pt.ulisboa.tecnico.meic.cmu.locmess.interfaces.ActivityCallback;
import pt.ulisboa.tecnico.meic.cmu.locmess.interfaces.LocmessCallback;

// TODO: CHECKED!
public final class LoginWebService extends LocmessWebService implements LocmessCallback {

    private static final String TAG = LoginWebService.class.getSimpleName();
    private final User user;
    private boolean autologin;

    public LoginWebService(Context context, ActivityCallback activityCallback, User user, boolean autologin) {
        super(context, activityCallback);
        this.user = user;
        this.autologin = autologin;
    }

    @Override
    protected void dispatch() {
        String user = new JsonService().transformObjToJson(this.user);
        String endpoint = getContext().getString(R.string.webserver_endpoint_user_login);
        String contentType = getContext().getString(R.string.content_type_json);
        try {
            getHttpService().post(endpoint, new StringEntity(user), contentType, new LocmessRestHandler(this));
        } catch (UnsupportedEncodingException ignored) {
        }
    }

    @Override
    public void onSuccess(Object object) {
        Token token = (Token) getJsonService().transformJsonToObj(object.toString(), Token.class);
        God.getInstance().setToken(token);
        StaticFields.username = user.getUsername();
        if (autologin) saveCredentials(user.getUsername(), user.getPassword());
        getActivityCallback().onSuccess(null);
        Log.d(TAG, token.getToken());
    }

    @Override
    public void onFailure(Object object) {
        Result result;
        if (object != null)
            result = (Result) getJsonService().transformJsonToObj(object.toString(), Result.class);
        else
            result = new Result("NULL");
        getActivityCallback().onFailure(result);
    }

    private void saveCredentials(String username, String password) {
        try (ObjectOutputStream objectOutputStream = new ObjectOutputStream(new BufferedOutputStream(
                getContext().openFileOutput(getContext().getString(R.string.credentials_filename), Context.MODE_PRIVATE)))) {
            objectOutputStream.writeUTF(username);
            objectOutputStream.writeUTF(password);
        } catch (IOException ignored) {
        }
    }
}
