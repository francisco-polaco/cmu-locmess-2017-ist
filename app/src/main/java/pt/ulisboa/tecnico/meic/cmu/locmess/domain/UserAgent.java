package pt.ulisboa.tecnico.meic.cmu.locmess.domain;

import android.content.Context;

import java.io.File;
import java.io.IOException;

class UserAgent {
    private static final UserAgent ourInstance = new UserAgent();

    static UserAgent getInstance() {
        return ourInstance;
    }

    private UserAgent() {
    }

    boolean checkIfUserLogged(Context context){
        return new File(context.getFilesDir().getPath() + "/logged.dat").exists();
    }

    boolean logout(Context context) {
        return new File(context.getFilesDir().getPath() + "/logged.dat").delete();
    }

    boolean login(Context context){
        try {
            new File(context.getFilesDir().getPath() + "/logged.dat").createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }
}
