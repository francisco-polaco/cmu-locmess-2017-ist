package pt.ulisboa.tecnico.meic.cmu.locmess.service;

import android.content.Context;
import android.util.Log;

import java.util.Arrays;
import java.util.List;

import pt.ulisboa.tecnico.meic.cmu.locmess.R;
import pt.ulisboa.tecnico.meic.cmu.locmess.dto.Message;
import pt.ulisboa.tecnico.meic.cmu.locmess.dto.MessageDto;
import pt.ulisboa.tecnico.meic.cmu.locmess.dto.Pair;
import pt.ulisboa.tecnico.meic.cmu.locmess.dto.Result;
import pt.ulisboa.tecnico.meic.cmu.locmess.handler.LocmessRestHandler;
import pt.ulisboa.tecnico.meic.cmu.locmess.interfaces.ActivityCallback;
import pt.ulisboa.tecnico.meic.cmu.locmess.interfaces.LocmessCallback;

/**
 * Created by Diogo on 01/05/2017.
 */

public class ListMessagesService extends LocmessWebService implements LocmessCallback {

    public ListMessagesService(Context context, ActivityCallback activityCallback) {
        super(context, activityCallback);
    }

    @Override
    protected void dispatch() {
        String endpoint = "/message/list";
        String contentType = getContext().getString(R.string.content_type_json);
        getHttpService().get(endpoint, null, contentType, new LocmessRestHandler(this));
    }

    @Override
    public void onSuccess(Object object) {
        if(object == null) Log.d("message", "yey");
        MessageDto[] messageDtos = (MessageDto[])
                getJsonService().transformJsonToObj(object.toString(), MessageDto[].class);
        List<MessageDto> messageDtoList = Arrays.asList(messageDtos);
        getActivityCallback().onSuccess(new Result("LM", messageDtoList));
    }

    @Override
    public void onFailure(Object object) {
        getActivityCallback().onFailure(new Result("LM"));
    }

}
