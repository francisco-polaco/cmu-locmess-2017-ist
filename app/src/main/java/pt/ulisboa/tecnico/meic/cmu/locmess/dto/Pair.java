package pt.ulisboa.tecnico.meic.cmu.locmess.dto;

/**
 * Created by Diogo on 20/04/2017.
 */

public class Pair {

    private String key;
    private String value;

    public Pair(){
    }

    public Pair(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
