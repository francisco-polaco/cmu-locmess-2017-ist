package pt.ulisboa.tecnico.meic.cmu.locmess.interfaces;

import android.content.Context;

/**
 * Created by Diogo on 11/05/2017.
 */

public abstract class LocmessListener {

    private Context context;

    protected LocmessListener(Context context) {
        this.context = context;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }
}
