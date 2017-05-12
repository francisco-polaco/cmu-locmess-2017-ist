package pt.ulisboa.tecnico.meic.cmu.locmess.service;

import android.content.Context;

import java.util.Arrays;
import java.util.TreeMap;

import pt.ulisboa.tecnico.meic.cmu.locmess.R;
import pt.ulisboa.tecnico.meic.cmu.locmess.domain.NotificationAgent;
import pt.ulisboa.tecnico.meic.cmu.locmess.dto.MessageDto;
import pt.ulisboa.tecnico.meic.cmu.locmess.dto.Result;
import pt.ulisboa.tecnico.meic.cmu.locmess.handler.LocmessRestHandler;
import pt.ulisboa.tecnico.meic.cmu.locmess.interfaces.ActivityCallback;
import pt.ulisboa.tecnico.meic.cmu.locmess.interfaces.LocmessCallback;

/**
 * Created by Diogo on 01/05/2017.
 */

public class ListMessagesService extends LocmessWebService implements LocmessCallback {

    private static TreeMap<Integer, MessageDto> messages = new TreeMap<>();

    public ListMessagesService(Context context, ActivityCallback activityCallback) {
        super(context, activityCallback);
    }

    @Override
    protected void dispatch() {
        String endpoint = getContext().getString(R.string.webserver_endpoint_message_list);
        String contentType = getContext().getString(R.string.content_type_json);
        getHttpService().get(endpoint, null, contentType, new LocmessRestHandler(this));
    }

    @Override
    public void onSuccess(Object object) {
        MessageDto[] messageDtos = (MessageDto[])
                getJsonService().transformJsonToObj(object.toString(), MessageDto[].class);
        TreeMap<Integer, MessageDto> messageDtoTreeMap = new TreeMap<>();
        for (MessageDto messageDto : Arrays.asList(messageDtos))
            messageDtoTreeMap.put(messageDto.getId(), messageDto);

        if (messageDtoTreeMap.size() == 0) {
            messages = messageDtoTreeMap;
        } else if (!messages.equals(messageDtoTreeMap))
            NotificationAgent.getInstance().sendNotification(getContext());

        Result lm = new Result("LM");
        lm.setPiggyback(messageDtoTreeMap);
        getActivityCallback().onSuccess(lm);
    }

    @Override
    public void onFailure(Object object) {
        getActivityCallback().onFailure(new Result("LM"));
    }

}
