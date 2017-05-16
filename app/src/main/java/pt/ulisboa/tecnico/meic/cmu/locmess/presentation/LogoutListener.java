package pt.ulisboa.tecnico.meic.cmu.locmess.presentation;

import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import pt.ulisboa.tecnico.meic.cmu.locmess.R;
import pt.ulisboa.tecnico.meic.cmu.locmess.dto.Result;
import pt.ulisboa.tecnico.meic.cmu.locmess.interfaces.ActivityCallback;
import pt.ulisboa.tecnico.meic.cmu.locmess.interfaces.LocmessListener;


public class LogoutListener extends LocmessListener implements ActivityCallback {

    protected LogoutListener(Context context) {
        super(context);
    }

    @Override
    public void onSuccess(Result result) {
        Intent intent = new Intent(getContext(), Login.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        getContext().startActivity(intent);
    }

    @Override
    public void onFailure(Result result) {
        Toast.makeText(getContext(), R.string.error_logout, Toast.LENGTH_LONG).show();
    }
}
