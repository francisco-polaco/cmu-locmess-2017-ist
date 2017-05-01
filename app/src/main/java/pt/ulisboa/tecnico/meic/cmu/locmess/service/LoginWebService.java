package pt.ulisboa.tecnico.meic.cmu.locmess.service;


import android.content.Context;
import android.util.Log;

import java.io.UnsupportedEncodingException;

import cz.msebera.android.httpclient.entity.StringEntity;
import pt.ulisboa.tecnico.meic.cmu.locmess.R;
import pt.ulisboa.tecnico.meic.cmu.locmess.domain.God;
import pt.ulisboa.tecnico.meic.cmu.locmess.dto.Message;
import pt.ulisboa.tecnico.meic.cmu.locmess.dto.Token;
import pt.ulisboa.tecnico.meic.cmu.locmess.dto.User;
import pt.ulisboa.tecnico.meic.cmu.locmess.handler.LocmessRestHandler;
import pt.ulisboa.tecnico.meic.cmu.locmess.interfaces.ActivityCallback;
import pt.ulisboa.tecnico.meic.cmu.locmess.interfaces.LocmessCallback;

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
        if(autologin) God.getInstance().saveCredentials(user.getUsername(), user.getPassword());
        getActivityCallback().onSuccess(null);
        Log.d(TAG, token.getToken());
    }

    @Override
    public void onFailure(Object object) {
        Message message;
        if(object != null)
            message = (Message) getJsonService().transformJsonToObj(object.toString(), Message.class);
        else
            message = new Message("NULL");
        getActivityCallback().onFailure(message);
    }
}
