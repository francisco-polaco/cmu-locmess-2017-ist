package pt.ulisboa.tecnico.meic.cmu.locmess.dto;

import java.util.List;

/**
 * Created by Diogo on 27/04/2017.
 */

public class APLocation extends Location {

    public final String type = "APLocation";


    public APLocation(){
    }

    public APLocation(String name, List<String> wifiLocations){
        super(name);
    }

    public APLocation(Integer id, String name, List<String> wifiLocations){
            super(id, name);
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
}
