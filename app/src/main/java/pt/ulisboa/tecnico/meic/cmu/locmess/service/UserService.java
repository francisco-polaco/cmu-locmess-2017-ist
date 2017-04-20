package pt.ulisboa.tecnico.meic.cmu.locmess.service;

import android.content.Context;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;
import pt.ulisboa.tecnico.meic.cmu.locmess.R;
import pt.ulisboa.tecnico.meic.cmu.locmess.dto.User;

/**
 * Created by Diogo on 19/04/2017.
 */

public class UserService {

    private Context context;

    public UserService(Context context) {
        this.context = context;
    }

    public void signup(String username, String password) {
        String user = new JsonService().transformObjToJson(new User(username, password));
        String endpoint = context.getString(R.string.webserver_endpoint_user_signup);

        try {
            new HttpService(context).post(endpoint, new StringEntity(user), "application/json",
                    new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    // TODO : continue tommorrow
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    // TODO : continue tommorrow
                }
            });
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }




}
