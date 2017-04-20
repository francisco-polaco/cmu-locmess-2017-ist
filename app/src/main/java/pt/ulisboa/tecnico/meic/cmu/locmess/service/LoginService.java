package pt.ulisboa.tecnico.meic.cmu.locmess.service;


import android.content.Context;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

import cz.msebera.android.httpclient.entity.StringEntity;
import pt.ulisboa.tecnico.meic.cmu.locmess.R;
import pt.ulisboa.tecnico.meic.cmu.locmess.domain.God;
import pt.ulisboa.tecnico.meic.cmu.locmess.dto.AsyncResult;
import pt.ulisboa.tecnico.meic.cmu.locmess.dto.Message;
import pt.ulisboa.tecnico.meic.cmu.locmess.dto.Token;
import pt.ulisboa.tecnico.meic.cmu.locmess.dto.User;
import pt.ulisboa.tecnico.meic.cmu.locmess.handler.LocmessRestHandler;
import pt.ulisboa.tecnico.meic.cmu.locmess.interfaces.ActivityCallback;
import pt.ulisboa.tecnico.meic.cmu.locmess.interfaces.LocmessCallback;

public final class LoginService extends LocmessService implements LocmessCallback {

    private final User user;

    public LoginService(Context context, ActivityCallback activityCallback, User user) {
        super(context, activityCallback);
        this.user = user;
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
    public void onSucess(JSONObject object) {
        Token token = (Token) getJsonService().transformJsonToObj(object.toString(), Token.class);
        God.getInstance().setToken(token);
        getActivityCallback().onSuccess(null);
        System.out.println(token.getToken());
    }

    @Override
    public void onFailure(JSONObject object) {
        Message message = (Message) getJsonService().transformJsonToObj(object.toString(), Message.class);
        getActivityCallback().onFailure(message);
        System.out.println(message);
    }
}
