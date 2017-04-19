package pt.ulisboa.tecnico.meic.cmu.locmess.domain;

import android.content.Context;

import java.io.File;
import java.io.IOException;

public class God {

    private static God ourInstance;
    private Context context;

    private God(Context context) {
        this.context = context;
    }

    public static God getInstance() {
        if (ourInstance == null) throw new RuntimeException(God.class.getSimpleName() +
                " not initialized.");
        return ourInstance;
    }

    public static void init(Context context) {
        ourInstance = new God(context);
    }

    public boolean checkIfLogged() {
        return UserAgent.getInstance().checkIfUserLogged(context);
    }

    public boolean logout() {
        return UserAgent.getInstance().logout(context);
    }

    public boolean login() {
        return UserAgent.getInstance().login(context);
    }
}
