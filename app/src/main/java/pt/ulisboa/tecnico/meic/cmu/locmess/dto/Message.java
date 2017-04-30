package pt.ulisboa.tecnico.meic.cmu.locmess.dto;

/**
 * Created by Diogo on 20/04/2017.
 */

public class Message {

    private String message;
    // usually used to pass additional objects to the domain logic
    // ex: index to remove an item on remove pair
    private Object piggyback;

    public Message(String message) {
        this.message = message;
    }

    public Message(String message, Object piggyback) {
        this.message = message;
        this.piggyback = piggyback;
    }

    public Message() {
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
