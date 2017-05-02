package pt.ulisboa.tecnico.meic.cmu.locmess.dto;

/**
 * Created by Diogo on 20/04/2017.
 */

public class Result {

    private String message;
    // usually used to pass additional objects to the domain logic
    // ex: index to remove an item on remove pair
    private Object piggyback;

    public Result(String message) {
        this.message = message;
    }

    public Result(String message, Object piggyback) {
        this.message = message;
        this.piggyback = piggyback;
    }

    public Result() {
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Object getPiggyback() {
        return piggyback;
    }

    public void setPiggyback(Object piggyback) {
        this.piggyback = piggyback;
    }
}
