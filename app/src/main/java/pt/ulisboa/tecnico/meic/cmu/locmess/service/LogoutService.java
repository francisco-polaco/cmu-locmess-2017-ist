package pt.ulisboa.tecnico.meic.cmu.locmess.service;


import pt.ulisboa.tecnico.meic.cmu.locmess.domain.God;

public final class LogoutService extends LocmessThreadedService {
    @Override
    protected void dispatch() {
        God.getInstance().logout();
    }
}