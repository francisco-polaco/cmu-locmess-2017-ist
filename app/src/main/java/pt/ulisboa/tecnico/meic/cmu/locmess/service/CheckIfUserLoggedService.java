package pt.ulisboa.tecnico.meic.cmu.locmess.service;

import pt.ulisboa.tecnico.meic.cmu.locmess.domain.God;

public class CheckIfUserLoggedService extends LocmessService {
    private boolean result;

    @Override
    protected void dispatch() {
        result = God.getInstance().checkIfLogged();
    }

    public boolean result() {
        return result;
    }
}
