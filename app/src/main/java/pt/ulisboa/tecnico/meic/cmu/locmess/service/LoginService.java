package pt.ulisboa.tecnico.meic.cmu.locmess.service;


import pt.ulisboa.tecnico.meic.cmu.locmess.domain.God;
import pt.ulisboa.tecnico.meic.cmu.locmess.interfaces.OnLoginCallback;

public final class LoginService extends LocmessThreadedService {
    private OnLoginCallback cb;

    public LoginService(OnLoginCallback cb) {
        this.cb = cb;
    }

    @Override
    protected void dispatch() {
        if (God.getInstance().login())
            cb.OnSuccessLogin();
        else
            cb.OnFailedLogin();
    }
}
