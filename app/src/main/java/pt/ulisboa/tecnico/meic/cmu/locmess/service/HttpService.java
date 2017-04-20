package pt.ulisboa.tecnico.meic.cmu.locmess.service;

import android.content.Context;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import cz.msebera.android.httpclient.HttpEntity;
import pt.ulisboa.tecnico.meic.cmu.locmess.R;

/**
 * Created by Diogo on 19/04/2017.
 */

public class HttpService {

    private Context context;
    private AsyncHttpClient httpClient;

    public HttpService(Context context){
        this.context = context;
        this.httpClient = new AsyncHttpClient();
    }

    public void get(String endpoint, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        httpClient.get(absoluteURL(endpoint), params, responseHandler);
    }

    public void post(String endpoint, HttpEntity body, String contentType, AsyncHttpResponseHandler responseHandler) {
        httpClient.post(context, absoluteURL(endpoint), body, contentType, responseHandler);
    }

    private String absoluteURL(String endpoint) {
        return context.getString(R.string.webserver_url) + endpoint;
    }

}
