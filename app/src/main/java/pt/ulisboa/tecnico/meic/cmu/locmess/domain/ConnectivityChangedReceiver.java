package pt.ulisboa.tecnico.meic.cmu.locmess.domain;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.util.Log;


public final class ConnectivityChangedReceiver extends BroadcastReceiver {
    private static final String TAG = ConnectivityChangedReceiver.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "Connectivity has changed.");
        if (!intent.getAction().equals(ConnectivityManager.CONNECTIVITY_ACTION)){
            Log.d(TAG, "Spoofed broadcast, ignoring request.");
        }else{
            int attempts = 0;
            while(!DeviceStatus.getInstance().isInternetAvailable(context) && attempts < 3){
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                attempts++;
            }
            if(DeviceStatus.getInstance().isInternetAvailable(context)){
                Log.d(TAG, "Starting update server.");
                // Voltar aos heartbeats
            }else{
                Log.d(TAG, "No Internet. Stop Contacting the server.");
                // Desligar Heartbeats
            }
        }
    }
}
