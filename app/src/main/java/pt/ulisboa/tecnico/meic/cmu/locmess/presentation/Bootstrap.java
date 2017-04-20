package pt.ulisboa.tecnico.meic.cmu.locmess.presentation;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;

import pt.ulisboa.tecnico.meic.cmu.locmess.domain.God;
import pt.ulisboa.tecnico.meic.cmu.locmess.googleapi.GoogleAPI;

/**
 * Created by franc on 19/04/2017.
 */

public class Bootstrap extends Activity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Initialization of Singletons
        God.init(getApplicationContext());
        GoogleAPI.init(getApplicationContext(), false);

        Intent intent = new Intent();
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        if (God.getInstance().isLogged())
            intent.setClass(getApplicationContext(), MainScreen.class);
        else
            intent.setClass(getApplicationContext(), Login.class);
        startActivity(intent);
    }
}
