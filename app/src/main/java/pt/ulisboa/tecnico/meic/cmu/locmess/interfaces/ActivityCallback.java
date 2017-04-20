package pt.ulisboa.tecnico.meic.cmu.locmess.interfaces;

import pt.ulisboa.tecnico.meic.cmu.locmess.dto.AsyncResult;
import pt.ulisboa.tecnico.meic.cmu.locmess.dto.Message;

public interface ActivityCallback {
    void onSuccess(Message result);
    void onFailure(Message result);
}
