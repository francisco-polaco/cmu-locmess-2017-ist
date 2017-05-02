package pt.ulisboa.tecnico.meic.cmu.locmess.service;

import android.content.Context;

import pt.ulisboa.tecnico.meic.cmu.locmess.R;
import pt.ulisboa.tecnico.meic.cmu.locmess.domain.God;
import pt.ulisboa.tecnico.meic.cmu.locmess.dto.Result;
import pt.ulisboa.tecnico.meic.cmu.locmess.interfaces.ActivityCallback;

public abstract class LocmessWebService {

    private Context context;
    private ActivityCallback activityCallback;
    private JsonService jsonService;
    private HttpService httpService;

    public LocmessWebService(Context context, ActivityCallback activityCallback) {
        this.context = context;
        this.activityCallback = activityCallback;
        this.jsonService = new JsonService();
        this.httpService = new HttpService(context);
        // for calls which need the token as header
        addTokenHeader();
    }

    public void execute() {
        dispatch();
    }

    protected abstract void dispatch();

    public Context getContext() {
        return context;
    }

    public JsonService getJsonService() {
        return jsonService;
    }

    public HttpService getHttpService() {
        return httpService;
    }

    private void addTokenHeader() {
        if (God.getInstance().getToken() != null) {
            this.httpService.getHttpClient().addHeader(getContext().getString(R.string.token_header),
                    God.getInstance().getToken().getToken());
        }
    }

    public ActivityCallback getActivityCallback() {
        // just to avoid null ptrs
        if(activityCallback == null) return new ActivityCallback() {
            @Override
            public void onSuccess(Result result) {

            }

            @Override
            public void onFailure(Result result) {

            }
        };
        return activityCallback;
    }
}
