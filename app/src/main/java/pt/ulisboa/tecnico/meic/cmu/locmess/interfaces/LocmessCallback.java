package pt.ulisboa.tecnico.meic.cmu.locmess.interfaces;

import org.json.JSONObject;

/**
 * Created by Diogo on 20/04/2017.
 */

public interface LocmessCallback {
    public void onSucess(JSONObject object);
    public void onFailure(JSONObject object);
}