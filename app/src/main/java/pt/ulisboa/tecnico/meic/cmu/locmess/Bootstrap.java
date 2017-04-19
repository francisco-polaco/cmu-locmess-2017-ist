package pt.ulisboa.tecnico.meic.cmu.locmess;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;

import java.io.File;

/**
 * Created by franc on 19/04/2017.
 */

public class Bootstrap extends Activity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(new File(getApplicationContext().getFilesDir().getPath() + "/logged.dat").exists()){
            startActivity(new Intent(getApplicationContext(), MainScreen.class));
        }else {
            startActivity(new Intent(getApplicationContext(), Login.class));
        }
    }
}
