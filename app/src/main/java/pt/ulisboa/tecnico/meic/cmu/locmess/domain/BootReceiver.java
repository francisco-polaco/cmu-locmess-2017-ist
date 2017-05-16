package pt.ulisboa.tecnico.meic.cmu.locmess.domain;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.List;

import pt.ulisboa.tecnico.meic.cmu.locmess.dto.Location;
import pt.ulisboa.tecnico.meic.cmu.locmess.dto.Result;
import pt.ulisboa.tecnico.meic.cmu.locmess.interfaces.ActivityCallback;
import pt.ulisboa.tecnico.meic.cmu.locmess.service.ListLocationsService;


@SuppressWarnings("unchecked")
public final class BootReceiver extends BroadcastReceiver {

    private static final String TAG = BootReceiver.class.getSimpleName();

    @Override
    public void onReceive(final Context context, Intent intent) {
        Log.d(TAG, "Boot finished.");
        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
            new ListLocationsService(context, new ActivityCallback() {
                @Override
                public void onSuccess(Result result) {
                    if (((List<Location>) result.getPiggyback()).size() != 0)
                        if (!Utils.isMyServiceRunning(context, UpdateLocationService.class))
                            context.startService(new Intent(context, UpdateLocationService.class));
                }

                @Override
                public void onFailure(Result result) {
                }
            }).execute();
        }
    }
}
