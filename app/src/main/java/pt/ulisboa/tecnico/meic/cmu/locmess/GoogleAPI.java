package pt.ulisboa.tecnico.meic.cmu.locmess;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.location.LocationServices;

import java.util.ArrayList;

import static com.google.android.gms.common.api.GoogleApiClient.*;

/**
 * Created by franc on 17/04/2017.
 */

public final class GoogleAPI implements ConnectionCallbacks, OnConnectionFailedListener {
    
    private static GoogleAPI ourInstance;
    private static final String TAG = GoogleAPI.class.getSimpleName();

    public static GoogleAPI getInstance(Context context) {
        if (ourInstance == null) ourInstance = new GoogleAPI(context);
        else ourInstance.setContext(context);
        return ourInstance;
    }

    public static GoogleAPI getInstance(Context context, boolean multithreaded) {
        if(ourInstance == null) ourInstance = new GoogleAPI(context, multithreaded);
        else{
            ourInstance.setContext(context);
            ourInstance.setMultithreaded(multithreaded);
        }
        return ourInstance;
    }

    public static GoogleAPI getInstance() {
        if(ourInstance == null) throw new RuntimeException("You didn't retrieve GoogleAPI first " +
                "with getInstance(Context) or getInstance(Context, boolean). If it is the first " +
                "time you are calling this object, you should use the other method.");
        return ourInstance;
    }

    private GoogleApiClient mGoogleApiClient;
    private ConnectionCallbacks mClassToCallback;
    private Context mContext;
    private ArrayList<ConnectionCallbacks> connectionCallbackses;
    private ArrayList<GoogleApiClient.OnConnectionFailedListener> connectionFailedListeners;
    private boolean mIsMultithreaded;

    private GoogleAPI(Context context) {
        mIsMultithreaded = false;
        init(context);
    }

    private void init(Context context) {
        mContext = context;
        connectionCallbackses = new ArrayList<>();
        connectionFailedListeners = new ArrayList<>();
        buildGoogleApiClient();
    }

    private GoogleAPI(Context context, boolean multithreaded) {
        mIsMultithreaded = multithreaded;
        init(context);
    }

    private synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new Builder(mContext)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        Log.d(TAG, "Building Google API");
    }

    @Override
    public void onConnected(@Nullable final Bundle bundle) {
        // Do what needs to be done when the system is connected.
        Thread[] threads = null;
        if(mIsMultithreaded){
            threads = new Thread[connectionCallbackses.size()];
            for (int i = 0; i < connectionCallbackses.size(); i++) {
                final ConnectionCallbacks cb = connectionCallbackses.get(i);
                threads[i] = new Thread(){
                    @Override
                    public void run() {
                        cb.onConnected(bundle);
                    }
                };
                threads[i].start();
            }
        }else{
            for(ConnectionCallbacks cb: connectionCallbackses){
                cb.onConnected(bundle);
            }
        }

        if(mIsMultithreaded && threads != null) {
            for (Thread thread : threads) {
                try {
                    thread.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        //if(mClassToCallback != null) mClassToCallback.onConnected(bundle);
    }

    @Override
    public void onConnectionSuspended(final int i) {
        // Do what needs to be done when the connection is suspended.
        Thread[] threads = null;
        if(mIsMultithreaded){
            threads = new Thread[connectionCallbackses.size()];
            for (int j = 0; j < connectionCallbackses.size(); j++) {
                final ConnectionCallbacks cb = connectionCallbackses.get(j);
                threads[j] = new Thread(){
                    @Override
                    public void run() {
                        cb.onConnectionSuspended(i);
                    }
                };
                threads[j].start();
            }
        }else{
            for(ConnectionCallbacks cb: connectionCallbackses){
                cb.onConnectionSuspended(i);
            }
        }

        if(mIsMultithreaded && threads != null) {
            for (Thread thread : threads) {
                try {
                    thread.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        //if(mClassToCallback != null) mClassToCallback.onConnectionSuspended(i);
    }

    @Override
    public void onConnectionFailed(@NonNull final ConnectionResult connectionResult) {
        // Do what needs to be done when the connection fails.
        Thread[] threads = null;
        if(mIsMultithreaded){
            threads = new Thread[connectionFailedListeners.size()];
            for (int j = 0; j < connectionFailedListeners.size(); j++) {
                final OnConnectionFailedListener cb = connectionFailedListeners.get(j);
                threads[j] = new Thread(){
                    @Override
                    public void run() {
                        cb.onConnectionFailed(connectionResult);
                    }
                };
                threads[j].start();
            }
        }else{
            for(OnConnectionFailedListener cb: connectionFailedListeners){
                cb.onConnectionFailed(connectionResult);
            }
        }

        if(mIsMultithreaded && threads != null) {
            for (Thread thread : threads) {
                try {
                    thread.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void connect(){
        mGoogleApiClient.connect();
    }

    public void connect(ConnectionCallbacks classToCallback){
        mClassToCallback = classToCallback;
        mGoogleApiClient.connect();
    }

    public void disconnect(){
        mGoogleApiClient.disconnect();
    }

    public void disconnect(ConnectionCallbacks classToCallback){
        mClassToCallback = classToCallback;
        mGoogleApiClient.disconnect();
    }

    public boolean isConnected(){
        return mGoogleApiClient.isConnected();
    }

    public boolean isConnecting(){
        return mGoogleApiClient.isConnecting();
    }

    public void registerConnectionCallbacks(ConnectionCallbacks connectionCallbacks){
        connectionCallbackses.add(connectionCallbacks);
    }

    public void registerOnConnectionFailedListener(OnConnectionFailedListener onConnectionFailedListener){
        connectionFailedListeners.add(onConnectionFailedListener);
    }

    public void unregisterConnectionCallbacks(ConnectionCallbacks connectionCallbacks){
        connectionCallbackses.remove(connectionCallbacks);
    }

    public void unregisterOnConnectionFailedListener(OnConnectionFailedListener onConnectionFailedListener){
        connectionFailedListeners.remove(onConnectionFailedListener);
    }

    public boolean isMultithreaded() {
        return mIsMultithreaded;
    }

    public void setMultithreaded(boolean isMultithread) {
        this.mIsMultithreaded = isMultithread;
    }

    private void setContext(Context context) {
        this.mContext = context;
    }
}
