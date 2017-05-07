package pt.ulisboa.tecnico.meic.cmu.locmess.dto;

/**
 * Created by Diogo on 27/04/2017.
 */

public abstract class Location {

    private Integer id;
    private String name;

    public Location() {
    }

    public Location(String name) {
        this.name = name;
    }

    public Location(Integer id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
