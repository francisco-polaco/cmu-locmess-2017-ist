package pt.ulisboa.tecnico.meic.cmu.locmess.service;

public abstract class LocmessService {

    public void execute() {
        dispatch();
    }

    protected abstract void dispatch();
}
