package pt.ulisboa.tecnico.meic.cmu.locmess.domain;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;


public final class ConnectivityChangedReceiver extends BroadcastReceiver {

    private static final String TAG = ConnectivityChangedReceiver.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "Connectivity has changed.");
        if (intent.getAction().equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
            int attempts = 0;
            while (!isInternetAvailable(context) && attempts < 3) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                attempts++;
            }
            synchronized (this) {
                if (isInternetAvailable(context)) {
                    Log.d(TAG, "Start heartbeat server.");
                    if (!Utils.isMyServiceRunning(context, UpdateLocationService.class))
                        context.startService(new Intent(context, UpdateLocationService.class));
                } else {
                    Log.d(TAG, "No Internet. Stop Contacting the server.");
                    context.stopService(new Intent(context, UpdateLocationService.class));
                }
            }
        }
    }

    private boolean isInternetAvailable(Context context) {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
    }
}
