package pt.ulisboa.tecnico.meic.cmu.locmess.domain;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.util.Log;

import pt.ulisboa.tecnico.meic.cmu.locmess.service.ListLocationsService;


public final class BootReceiver extends BroadcastReceiver {
    private static final String TAG = BootReceiver.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "Boot finished.");
        if(!intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
            Log.d("BOOT", "Spoofed broadcast, ignoring request.");
        }else {
            new ListLocationsService(context, null).execute();
        }
    }
}
