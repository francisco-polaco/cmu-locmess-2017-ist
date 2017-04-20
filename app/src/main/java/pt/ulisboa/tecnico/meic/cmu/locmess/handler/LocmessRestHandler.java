package pt.ulisboa.tecnico.meic.cmu.locmess.handler;

import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;
import pt.ulisboa.tecnico.meic.cmu.locmess.interfaces.LocmessCallback;

/**
 * Created by Diogo on 20/04/2017.
 */

public class LocmessRestHandler extends JsonHttpResponseHandler {

    private LocmessCallback callback;

    public LocmessRestHandler(LocmessCallback callback){
        this.callback = callback;
    }

    @Override
    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
        callback.onSucess(response);
    }

    @Override
    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
        callback.onFailure(errorResponse);
    }
}
