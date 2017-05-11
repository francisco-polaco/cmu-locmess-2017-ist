package pt.ulisboa.tecnico.meic.cmu.locmess.dto;

import android.util.Log;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Diogo on 27/04/2017.
 */

public class APLocation extends Location implements Serializable{

    public final String type = "APLocation";
    private List<String> aps;


    public APLocation() {
    }

    public APLocation(String name, List<String> wifiLocations){
        super(name);
        this.aps = wifiLocations;
    }

    public APLocation(Integer id, String name, List<String> wifiLocations){
            super(id, name);
            this.aps = wifiLocations;
    }

    public String getType() {
        return type;
    }
    
    @Override
    public String toString() {
        return "APLocation{" +
                "name='" + getName() + '\'' +
                '}';
    }

    public List<String> getAps() {
        return aps;
    }

    public String getApVal() {
        String res = "";
        for (String s : aps)
            res = res + s;
        return res;
    }

    public void setAps(List<String> aps) {
        this.aps = aps;
    }

    public boolean equalAPLocation (APLocation apLocation){
            if (aps.equals(apLocation.getAps()))
                return true;
            else
                return false;
    }
}
