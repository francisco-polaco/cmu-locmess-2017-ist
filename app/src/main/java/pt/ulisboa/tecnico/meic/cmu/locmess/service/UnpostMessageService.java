package pt.ulisboa.tecnico.meic.cmu.locmess.service;

import android.content.Context;

import java.io.UnsupportedEncodingException;

import cz.msebera.android.httpclient.entity.StringEntity;
import pt.ulisboa.tecnico.meic.cmu.locmess.R;
import pt.ulisboa.tecnico.meic.cmu.locmess.domain.God;
import pt.ulisboa.tecnico.meic.cmu.locmess.dto.Message;
import pt.ulisboa.tecnico.meic.cmu.locmess.dto.MessageDto;
import pt.ulisboa.tecnico.meic.cmu.locmess.dto.Result;
import pt.ulisboa.tecnico.meic.cmu.locmess.handler.LocmessRestHandler;
import pt.ulisboa.tecnico.meic.cmu.locmess.interfaces.ActivityCallback;
import pt.ulisboa.tecnico.meic.cmu.locmess.interfaces.LocmessCallback;

/**
 * Created by Diogo on 06/05/2017.
 */

public class UnpostMessageService extends LocmessWebService implements LocmessCallback {

    private MessageDto message;

    public UnpostMessageService(Context context, ActivityCallback activityCallback, MessageDto message) {
        super(context, activityCallback);
        this.message = message;
    }

    @Override
    protected void dispatch() {
        String message = new JsonService().transformObjToJson(this.message);
        String endpoint = getContext().getString(R.string.webserver_endpoint_message_unpost);
        String contentType = getContext().getString(R.string.content_type_json);
        try {
            getHttpService().post(endpoint, new StringEntity(message), contentType, new LocmessRestHandler(this));
        } catch (UnsupportedEncodingException ignored) {
        }
    }

    @Override
    public void onSuccess(Object object) {
        God.getInstance().getCachedMessages().remove(message.getId());
        getActivityCallback().onSuccess(new Result("Removed message successfully!"));
    }

    @Override
    public void onFailure(Object object) {
        Result result = (Result) new JsonService().transformJsonToObj(object.toString(), Result.class);
        getActivityCallback().onFailure(result);

    }
}
