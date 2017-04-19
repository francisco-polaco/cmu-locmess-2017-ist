package pt.ulisboa.tecnico.meic.cmu.locmess.presentation;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import pt.ulisboa.tecnico.meic.cmu.locmess.R;
import pt.ulisboa.tecnico.meic.cmu.locmess.interfaces.OnLoginCallback;
import pt.ulisboa.tecnico.meic.cmu.locmess.service.LoginService;

/**
 * Created by jp_s on 4/12/2017.
 */

public class Login extends AppCompatActivity implements OnLoginCallback {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
    }

    public void registerScreen(View view) {
        Intent intent = new Intent(this, Register.class);
        startActivity(intent);
    }

    public void mainScreen(View view) {
        new LoginService(this).execute();
    }

    @Override
    public void OnSuccessLogin() {
        Intent intent = new Intent(this, MainScreen.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    @Override
    public void OnFailedLogin() {
        Toast.makeText(getApplicationContext(), R.string.toast_login_error, Toast.LENGTH_LONG).show();
    }
}

