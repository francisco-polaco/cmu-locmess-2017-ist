package pt.ulisboa.tecnico.meic.cmu.locmess.dto;

import java.util.List;

/**
 * Created by Diogo on 27/04/2017.
 */

public class APLocation extends Location {

    public final String type = "APLocation";
    private List<String> aps;

    public APLocation(){
    }

    public APLocation(String name){
        super(name);
    }

    public APLocation(Integer id, String name, List<String> aps){
            super(id, name);
        this.aps = aps;
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

    public void setAps(List<String> aps) {
        this.aps = aps;
    }
}
