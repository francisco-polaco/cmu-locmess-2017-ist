package pt.ulisboa.tecnico.meic.cmu.locmess.service;

public abstract class LocmessThreadedService {

    public void execute() {
        new Thread() {
            @Override
            public void run() {
                dispatch();
            }
        }.start();
    }

    protected abstract void dispatch();
}
