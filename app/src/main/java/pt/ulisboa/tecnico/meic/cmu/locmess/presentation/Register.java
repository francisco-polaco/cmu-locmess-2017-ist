package pt.ulisboa.tecnico.meic.cmu.locmess.presentation;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;

import pt.ulisboa.tecnico.meic.cmu.locmess.R;
import pt.ulisboa.tecnico.meic.cmu.locmess.service.UserService;

import static pt.ulisboa.tecnico.meic.cmu.locmess.R.layout.register;

/**
 * Created by jp_s on 4/12/2017.
 */

public class Register extends AppCompatActivity {

    private Toolbar toolbar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(register);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }

    public void register(View view) {
        String username = ((EditText) this.findViewById(R.id.User)).getText().toString();
        String password = ((EditText) this.findViewById(R.id.Pass)).getText().toString();
        String repeatPassword = ((EditText) this.findViewById(R.id.RepeatPass)).getText().toString();

        if(!password.equals(repeatPassword)){
            // TODO : Toast
        }
        System.out.println(username + " " + password + " " + repeatPassword);

        new UserService(this).signup(username, password);
    }

}
