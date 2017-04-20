package pt.ulisboa.tecnico.meic.cmu.locmess.service;

import android.content.Context;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

import cz.msebera.android.httpclient.entity.StringEntity;
import pt.ulisboa.tecnico.meic.cmu.locmess.R;
import pt.ulisboa.tecnico.meic.cmu.locmess.dto.Message;
import pt.ulisboa.tecnico.meic.cmu.locmess.dto.User;
import pt.ulisboa.tecnico.meic.cmu.locmess.handler.LocmessRestHandler;
import pt.ulisboa.tecnico.meic.cmu.locmess.interfaces.ActivityCallback;
import pt.ulisboa.tecnico.meic.cmu.locmess.interfaces.LocmessCallback;

/**
 * Created by Diogo on 20/04/2017.
 */

public class SignupService extends LocmessService implements LocmessCallback {

    private User user;

    public SignupService(Context context, ActivityCallback activityCallback, User user){
        super(context, activityCallback);
        this.user = user;
    }

    @Override
    protected void dispatch() {
        String user = new JsonService().transformObjToJson(this.user);
        String endpoint = getContext().getString(R.string.webserver_endpoint_user_signup);
        String contentType = getContext().getString(R.string.content_type_json);
        try {
            getHttpService().post(endpoint, new StringEntity(user), contentType, new LocmessRestHandler(this));
        } catch (UnsupportedEncodingException ignored) {
        }
    }

    @Override
    public void onSucess(JSONObject object) {
        Message message = (Message) new JsonService().transformJsonToObj(object.toString(), Message.class);
        System.out.println(message.getMessage());
        getActivityCallback().onSuccess(message);

        System.out.println(message.getMessage());
    }

    @Override
    public void onFailure(JSONObject object) {
        Message message = (Message) new JsonService().transformJsonToObj(object.toString(), Message.class);
        getActivityCallback().onFailure(message);
        System.out.println(message.getMessage());
    }
}
