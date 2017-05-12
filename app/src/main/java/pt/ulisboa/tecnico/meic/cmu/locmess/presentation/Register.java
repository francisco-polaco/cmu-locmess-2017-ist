package pt.ulisboa.tecnico.meic.cmu.locmess.presentation;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.io.IOException;

import pt.ulisboa.tecnico.meic.cmu.locmess.R;
import pt.ulisboa.tecnico.meic.cmu.locmess.domain.PersistenceManager;
import pt.ulisboa.tecnico.meic.cmu.locmess.dto.Result;
import pt.ulisboa.tecnico.meic.cmu.locmess.dto.User;
import pt.ulisboa.tecnico.meic.cmu.locmess.interfaces.ActivityCallback;
import pt.ulisboa.tecnico.meic.cmu.locmess.interfaces.LocmessListener;
import pt.ulisboa.tecnico.meic.cmu.locmess.service.SignupWebService;

import static pt.ulisboa.tecnico.meic.cmu.locmess.R.layout.register;


public class Register extends AppCompatActivity {

    // TODO: Refactorized!

    private static final String TAG = Register.class.getSimpleName();
    private ProgressDialog dialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(register);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    public void register(View view) {
        String username = ((EditText) this.findViewById(R.id.User)).getText().toString();
        String password = ((EditText) this.findViewById(R.id.Pass)).getText().toString();
        String repeatPassword = ((EditText) this.findViewById(R.id.RepeatPass)).getText().toString();

        if (!password.equals(repeatPassword)) {
            Toast.makeText(this, "Passwords don't match!", Toast.LENGTH_LONG).show();
            return;
        }

        dialog = WidgetConstructors.getLoadingDialog(this, getString(R.string.dialog_create_account));
        new RegisterListener(getApplicationContext(),username,password);
    }

    private class RegisterListener extends LocmessListener implements ActivityCallback {

        public RegisterListener(Context context, String username, String password) {
            super(context);
            new SignupWebService(context, this, new User(username, password)).execute();
        }

        @Override
        public void onSuccess(Result result) {
            try {
                PersistenceManager.getInstance().clearState(getContext());
            } catch (IOException ignored) {
            }
            if (dialog != null) dialog.cancel();
            Log.d(TAG,"Successfully registered!");
            Intent intent = new Intent(getContext(), Login.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }

        @Override
        public void onFailure(Result result) {
            if (dialog != null) dialog.cancel();
            Toast.makeText(getApplicationContext(), result.getMessage(), Toast.LENGTH_SHORT).show();
            Log.d(TAG,"Failed to register!");
        }
    }
}
