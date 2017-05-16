package pt.ulisboa.tecnico.meic.cmu.locmess.presentation;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;

import pt.ulisboa.tecnico.meic.cmu.locmess.R;
import pt.ulisboa.tecnico.meic.cmu.locmess.domain.PersistenceManager;
import pt.ulisboa.tecnico.meic.cmu.locmess.domain.StaticFields;
import pt.ulisboa.tecnico.meic.cmu.locmess.dto.Result;
import pt.ulisboa.tecnico.meic.cmu.locmess.dto.User;
import pt.ulisboa.tecnico.meic.cmu.locmess.interfaces.ActivityCallback;
import pt.ulisboa.tecnico.meic.cmu.locmess.interfaces.LocmessListener;
import pt.ulisboa.tecnico.meic.cmu.locmess.service.LoginWebService;

import static pt.ulisboa.tecnico.meic.cmu.locmess.presentation.WidgetConstructors.getLoadingDialog;

public class Login extends AppCompatActivity {

    private static final String TAG = Login.class.getSimpleName();
    private ProgressDialog dialog;
    private String[] credentials;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        PersistenceManager.getInstance().loadMessagesDescentralized(getApplicationContext());
        PersistenceManager.getInstance().flushAndLoadProfile(getApplicationContext());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
    }

    @Override
    protected void onStart() {
        super.onStart();
        String[] credentials = null;
        try {
            credentials = getCredentials();
        } catch (IOException ignored) {
        }
        if (credentials != null) {
            Log.d(TAG, "We have found credentials.");
            new LoginListener(getApplicationContext(), new User(credentials[0], credentials[1]), true);
        }
    }

    public void registerScreen(View view) {
        Intent intent = new Intent(this, Register.class);
        startActivity(intent);
    }

    public void login(View view) {
        String username = ((EditText) this.findViewById(R.id.Username)).getText().toString();
        String password = ((EditText) this.findViewById(R.id.Pass)).getText().toString();
        boolean autoLogin = ((CheckBox) findViewById(R.id.autologin)).isChecked();
        dialog = getLoadingDialog(Login.this, getString(R.string.dialog_login));
        new LoginListener(getApplicationContext(), new User(username, password), autoLogin);
        dialog.show();
    }

    public String[] getCredentials() throws IOException {
        if (this.credentials == null) {
            String[] credentials = new String[2];
            try (ObjectInputStream objectInputStream = new ObjectInputStream(new BufferedInputStream(
                    getApplicationContext().openFileInput(getString(R.string.credentials_filename))))) {
                credentials[0] = objectInputStream.readUTF();
                StaticFields.username = credentials[0];
                credentials[1] = objectInputStream.readUTF();
                return credentials;
            }
        } else return credentials;
    }

    private class LoginListener extends LocmessListener implements ActivityCallback {

        public LoginListener(Context context, User user, boolean autoLogin) {
            super(context);
            new LoginWebService(context, this, user, autoLogin).execute();
        }

        @Override
        public void onSuccess(Result result) {
            if (dialog != null)
                dialog.cancel();
            Intent intent = new Intent(getContext(), MessageScreen.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            Log.d(TAG, "Logged in!");
        }

        @Override
        public void onFailure(Result result) {
            if (dialog != null) dialog.cancel();
            Toast.makeText(getApplicationContext(), R.string.toast_login_error, Toast.LENGTH_LONG).show();
            Log.d(TAG, "Failed to login!");
        }
    }
}

