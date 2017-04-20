package pt.ulisboa.tecnico.meic.cmu.locmess.service;

import com.google.gson.Gson;

/**
 * Created by Diogo on 19/04/2017.
 */

public class JsonService {

    private Gson mapper;

    public JsonService() {
        this.mapper = new Gson();
    }

    public String transformObjToJson(Object object) {
        return mapper.toJson(object);
    }

    public Object transformJsonToObj(String json, Class cl) {
        return mapper.fromJson(json, cl);
    }

}
