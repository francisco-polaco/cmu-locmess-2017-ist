package pt.ulisboa.tecnico.meic.cmu.locmess.handler;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;

import pt.ulisboa.tecnico.meic.cmu.locmess.dto.APLocation;
import pt.ulisboa.tecnico.meic.cmu.locmess.dto.GPSLocation;
import pt.ulisboa.tecnico.meic.cmu.locmess.dto.Location;

/**
 * Created by Diogo on 02/05/2017.
 */

public class LocationDeserializer implements JsonDeserializer<Location> {

    @Override
    public Location deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
            throws JsonParseException {

        String type = json.getAsJsonObject().get("type").getAsString();

        Class<?> c = type.equals("GPSLocation") ? GPSLocation.class : APLocation.class;
        return context.deserialize(json, c);
    }
}
