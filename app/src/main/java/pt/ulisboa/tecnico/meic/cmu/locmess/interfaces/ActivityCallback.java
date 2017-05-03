package pt.ulisboa.tecnico.meic.cmu.locmess.interfaces;

import pt.ulisboa.tecnico.meic.cmu.locmess.dto.Result;

public interface ActivityCallback {
    void onSuccess(Result result);

    void onFailure(Result result);
}
