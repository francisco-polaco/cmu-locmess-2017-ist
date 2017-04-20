package pt.ulisboa.tecnico.meic.cmu.locmess.dto;

/**
 * Created by Diogo on 20/04/2017.
 */

public class AsyncResult {

    private boolean success;
    private Message message;

    public AsyncResult(boolean success, Message message) {
        this.success = success;
        this.message = message;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public Message getMessage() {
        return message;
    }

    public void setMessage(Message message) {
        this.message = message;
    }
}
