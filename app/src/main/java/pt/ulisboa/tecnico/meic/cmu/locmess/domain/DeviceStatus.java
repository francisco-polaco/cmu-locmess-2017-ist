package pt.ulisboa.tecnico.meic.cmu.locmess.domain;

import android.content.Context;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class DeviceStatus {
    private static final DeviceStatus ourInstance = new DeviceStatus();

    public static DeviceStatus getInstance() {
        return ourInstance;
    }

    private DeviceStatus() {
    }

    // FIXME CHECK IF NEEDED
   /* public void isLocationServicesActive(Context context){
        LocationManager lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        boolean isGPSEnabled = false;
        boolean isNetworkEnabled = false;

        isGPSEnabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);

        isNetworkEnabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

        if(isGPSEnabled && !isNetworkEnabled){
            throw new NetworkLocationDisabledException();
        }else if(!isGPSEnabled && isNetworkEnabled){
            throw new GPSDisabledException();
        }else if(!isGPSEnabled && !isNetworkEnabled){
            // worst case
            throw new LocationServicesDisabledException();
        }
    }
*/
    public boolean isInternetAvailable(Context context){
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
    }
}
