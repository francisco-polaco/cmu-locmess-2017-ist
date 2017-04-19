package pt.ulisboa.tecnico.meic.cmu.locmess.googleapi;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.location.LocationServices;

import java.util.ArrayList;

import static com.google.android.gms.common.api.GoogleApiClient.Builder;
import static com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;


public final class GoogleAPI implements ConnectionCallbacks, OnConnectionFailedListener {

    private static final String TAG = GoogleAPI.class.getSimpleName();
    private static GoogleAPI ourInstance;
    private GoogleApiClient mGoogleApiClient;

    private ConnectionCallbacks mObjectToCallback;
    private OnConnectionFailedListener mObjectToCallbackFail;
    private ArrayList<ConnectionCallbacks> connectionCallbackses;
    private ArrayList<GoogleApiClient.OnConnectionFailedListener> connectionFailedListeners;
    private boolean mMultithreaded;

    private GoogleAPI(Context context, boolean multithreaded) {
        mMultithreaded = multithreaded;
        connectionCallbackses = new ArrayList<>();
        connectionFailedListeners = new ArrayList<>();
        buildGoogleApiClient(context);
    }

    public static GoogleAPI getInstance() {
        if (ourInstance == null)
            throw new RuntimeException("You didn't initialize GoogleAPI first. " +
                    "If it is the first time you are calling this object," +
                    " you should use the init method first.");
        return ourInstance;
    }

    public static void init(Context context, boolean multithreaded) {
        ourInstance = new GoogleAPI(context, multithreaded);
    }

    private synchronized void buildGoogleApiClient(Context context) {
        mGoogleApiClient = new Builder(context)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        Log.d(TAG, "Building Google API");
    }

    @Override
    public void onConnected(@Nullable final Bundle bundle) {
        Log.d(TAG, "API Connected");
        Toast.makeText(getGoogleApiClient().getContext(), "API Connected", Toast.LENGTH_SHORT).show();
        // Do what needs to be done when the system is connected.
        //callOnConnectedToRegisteredCallbacks(bundle);
        /* if(mObjectToCallback != null) mObjectToCallback.onConnected(bundle);
         mObjectToCallback = null;*/
    }

    @Override
    public void onConnectionSuspended(final int i) {
        Log.d(TAG, "API Suspended");
        Toast.makeText(getGoogleApiClient().getContext(), "API Suspended", Toast.LENGTH_SHORT).show();

        // Do what needs to be done when the connection is suspended.
        //callOnConnectionSuspendedToRegisteredCallbacks(i);
        /*if(mObjectToCallback != null) mObjectToCallback.onConnectionSuspended(i);
        mObjectToCallback = null;*/
    }

    @Override
    public void onConnectionFailed(@NonNull final ConnectionResult connectionResult) {
        Log.d(TAG, "API Failed");
        Toast.makeText(getGoogleApiClient().getContext(), "API Failed", Toast.LENGTH_SHORT).show();

        // Do what needs to be done when the connection fails.
        //callOnConnectionFailedToRegisteredCallbacks(connectionResult);
        /*if(mObjectToCallbackFail != null) mObjectToCallbackFail.onConnectionFailed(connectionResult);
        mObjectToCallbackFail = null;*/
    }

    private void callOnConnectedToRegisteredCallbacks(@Nullable final Bundle bundle) {
        Thread[] threads = null;
        if (mMultithreaded) {
            threads = new Thread[connectionCallbackses.size()];
            for (int i = 0; i < connectionCallbackses.size(); i++) {
                final ConnectionCallbacks cb = connectionCallbackses.get(i);
                threads[i] = new Thread() {
                    @Override
                    public void run() {
                        cb.onConnected(bundle);
                    }
                };
                threads[i].start();
            }
        } else {
            for (ConnectionCallbacks cb : connectionCallbackses) {
                cb.onConnected(bundle);
            }
        }

        if (mMultithreaded && threads != null) {
            for (Thread thread : threads) {
                try {
                    thread.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void callOnConnectionSuspendedToRegisteredCallbacks(final int i) {
        Thread[] threads = null;
        if (mMultithreaded) {
            threads = new Thread[connectionCallbackses.size()];
            for (int j = 0; j < connectionCallbackses.size(); j++) {
                final ConnectionCallbacks cb = connectionCallbackses.get(j);
                threads[j] = new Thread() {
                    @Override
                    public void run() {
                        cb.onConnectionSuspended(i);
                    }
                };
                threads[j].start();
            }
        } else {
            for (ConnectionCallbacks cb : connectionCallbackses) {
                cb.onConnectionSuspended(i);
            }
        }

        if (mMultithreaded && threads != null) {
            for (Thread thread : threads) {
                try {
                    thread.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void callOnConnectionFailedToRegisteredCallbacks(@NonNull final ConnectionResult connectionResult) {
        Thread[] threads = null;
        if (mMultithreaded) {
            threads = new Thread[connectionFailedListeners.size()];
            for (int j = 0; j < connectionFailedListeners.size(); j++) {
                final OnConnectionFailedListener cb = connectionFailedListeners.get(j);
                threads[j] = new Thread() {
                    @Override
                    public void run() {
                        cb.onConnectionFailed(connectionResult);
                    }
                };
                threads[j].start();
            }
        } else {
            for (OnConnectionFailedListener cb : connectionFailedListeners) {
                cb.onConnectionFailed(connectionResult);
            }
        }

        if (mMultithreaded && threads != null) {
            for (Thread thread : threads) {
                try {
                    thread.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public GoogleApiClient getGoogleApiClient() {
        return mGoogleApiClient;
    }

    public void connect() {
        Log.d(TAG, "Connecting...");
        mGoogleApiClient.connect();
    }

    public void connect(ConnectionCallbacks classToCallback) {
        mObjectToCallback = classToCallback;
        mGoogleApiClient.connect();
    }

    public void disconnect() {
        mGoogleApiClient.disconnect();
    }

    public void disconnect(ConnectionCallbacks classToCallback) {
        mObjectToCallback = classToCallback;
        mGoogleApiClient.disconnect();
    }

    public boolean isConnected() {
        return mGoogleApiClient.isConnected();
    }

    public boolean isConnecting() {
        return mGoogleApiClient.isConnecting();
    }

    public boolean isMultithreaded() {
        return mMultithreaded;
    }

    public void setMultithreaded(boolean isMultithread) {
        this.mMultithreaded = isMultithread;
    }

    public void registerConnectionCallbacks(ConnectionCallbacks connectionCallbacks) {
        connectionCallbackses.add(connectionCallbacks);
    }

    public void registerOnConnectionFailedListener(OnConnectionFailedListener onConnectionFailedListener) {
        connectionFailedListeners.add(onConnectionFailedListener);
    }

    public void unregisterConnectionCallbacks(ConnectionCallbacks connectionCallbacks) {
        connectionCallbackses.remove(connectionCallbacks);
    }

    public void unregisterOnConnectionFailedListener(OnConnectionFailedListener onConnectionFailedListener) {
        connectionFailedListeners.remove(onConnectionFailedListener);
    }

}
