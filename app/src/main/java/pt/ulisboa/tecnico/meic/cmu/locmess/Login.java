package pt.ulisboa.tecnico.meic.cmu.locmess;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

/**
 * Created by jp_s on 4/12/2017.
 */

public class Login extends AppCompatActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
    }

    public void registerScreen(View view){
        Intent intent = new Intent(this, Register.class);
        startActivity(intent);
    }

    public void mainScreen(View view){
        Intent intent = new Intent(this, MainScreen.class);
        startActivity(intent);
    }

}

