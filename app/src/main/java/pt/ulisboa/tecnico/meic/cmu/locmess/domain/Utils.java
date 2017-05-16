package pt.ulisboa.tecnico.meic.cmu.locmess.domain;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class Utils {

    public static boolean isMyServiceRunning(Context context, Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo runningService : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(runningService.service.getClassName()))
                return true;
        }
        return false;
    }

    public static void startLocationUpdates(Context context) {
        Log.d("LocUp", "Starting up the update location service.");
        if (!Utils.isMyServiceRunning(context, UpdateLocationService.class))
            context.startService(new Intent(context, UpdateLocationService.class));
    }

    public static void stopLocationUpdates(Context context) {
        Log.d("LocUp", "Shutting down the update location service.");
        context.stopService(new Intent(context, UpdateLocationService.class));
    }
}
