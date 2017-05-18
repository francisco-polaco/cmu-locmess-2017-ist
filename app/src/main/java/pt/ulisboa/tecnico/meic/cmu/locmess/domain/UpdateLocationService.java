package pt.ulisboa.tecnico.meic.cmu.locmess.domain;

import android.Manifest;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Messenger;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.lang.reflect.Array;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicReference;

import pt.inesc.termite.wifidirect.SimWifiP2pBroadcast;
import pt.inesc.termite.wifidirect.SimWifiP2pDevice;
import pt.inesc.termite.wifidirect.SimWifiP2pDeviceList;
import pt.inesc.termite.wifidirect.SimWifiP2pInfo;
import pt.inesc.termite.wifidirect.SimWifiP2pManager;
import pt.inesc.termite.wifidirect.service.SimWifiP2pService;
import pt.inesc.termite.wifidirect.sockets.SimWifiP2pSocket;
import pt.inesc.termite.wifidirect.sockets.SimWifiP2pSocketManager;
import pt.inesc.termite.wifidirect.sockets.SimWifiP2pSocketServer;
import pt.ulisboa.tecnico.meic.cmu.locmess.R;
import pt.ulisboa.tecnico.meic.cmu.locmess.dto.APLocation;
import pt.ulisboa.tecnico.meic.cmu.locmess.dto.Message;
import pt.ulisboa.tecnico.meic.cmu.locmess.dto.MessageDto;
import pt.ulisboa.tecnico.meic.cmu.locmess.dto.Pair;
import pt.ulisboa.tecnico.meic.cmu.locmess.dto.Result;
import pt.ulisboa.tecnico.meic.cmu.locmess.googleapi.GoogleAPI;
import pt.ulisboa.tecnico.meic.cmu.locmess.interfaces.ActivityCallback;
import pt.ulisboa.tecnico.meic.cmu.locmess.interfaces.GoogleApiCallbacks;
import pt.ulisboa.tecnico.meic.cmu.locmess.presentation.NewMessage;
import pt.ulisboa.tecnico.meic.cmu.locmess.service.ListLocationsService;
import pt.ulisboa.tecnico.meic.cmu.locmess.service.ListMessagesService;
import pt.ulisboa.tecnico.meic.cmu.locmess.service.LocationWebService;

import static pt.ulisboa.tecnico.meic.cmu.locmess.R.array.keys;


public final class UpdateLocationService extends Service implements
        LocationListener, GoogleApiCallbacks,
        SimWifiP2pManager.PeerListListener, SimWifiP2pManager.GroupInfoListener {

    private static final String TAG = UpdateLocationService.class.getSimpleName();

    private static final int UPDATE_INTERVAL = 1000 /** 60*/
            ;
    private static final int INTERVAL = UPDATE_INTERVAL;
    private static final int FASTEST_UPDATE_INTERVAL = 1000;
    private Location oldLocation;
    private APLocation oldAPLocation;
    private List<String> IpDeviceList = new ArrayList<>();
    private List<String> peersStr = new ArrayList<>();

    private List<APLocation> APLog = new ArrayList<>();

    private SimWifiP2pSocketServer mSrvSocket = null;
    private SimWifiP2pSocket mCliSocket = null;
    private SimWifiP2pManager mManager = null;
    private SimWifiP2pManager.Channel mChannel = null;
    private SimWifiP2pBroadcastReceiver mReceiver;
    public static boolean wifion = false;
    public boolean connected = false;
    private HashMap<MessageDto, Message> msgLst = new HashMap<>();
    ;

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, UpdateLocationService.class.getSimpleName() + " onStartCommand");
        super.onStartCommand(intent, flags, startId);
        GoogleAPI.init(getApplicationContext(), false);
        GoogleAPI.getInstance().connect(this);

        // register broadcast receiver
        SimWifiP2pSocketManager.Init(getApplicationContext());

        IntentFilter filter = new IntentFilter();
        filter.addAction(SimWifiP2pBroadcast.WIFI_P2P_STATE_CHANGED_ACTION);
        filter.addAction(SimWifiP2pBroadcast.WIFI_P2P_PEERS_CHANGED_ACTION);
        filter.addAction(SimWifiP2pBroadcast.WIFI_P2P_NETWORK_MEMBERSHIP_CHANGED_ACTION);
        filter.addAction(SimWifiP2pBroadcast.WIFI_P2P_GROUP_OWNERSHIP_CHANGED_ACTION);
        mReceiver = new SimWifiP2pBroadcastReceiver(this);
        registerReceiver(mReceiver, filter);

        Intent wifiDintent = new Intent(getApplicationContext(), SimWifiP2pService.class);
        bindService(wifiDintent, mConnection, Context.BIND_AUTO_CREATE);
        new IncommingCommTask().executeOnExecutor(
                AsyncTask.THREAD_POOL_EXECUTOR);

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        stopLocationUpdates();
        unbindService(mConnection);
        unregisterReceiver(mReceiver);
        super.onDestroy();
    }

    private void stopLocationUpdates() {
        Log.d(TAG, "Stop Location Updates");
        LocationServices.FusedLocationApi.removeLocationUpdates(
                GoogleAPI.getInstance().getGoogleApiClient(), this);
    }

    private void startLocationUpdates() {
        Log.d(TAG, "Starting Location Updates");
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        LocationRequest mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL);
        LocationServices.FusedLocationApi.requestLocationUpdates(
                GoogleAPI.getInstance().getGoogleApiClient(), mLocationRequest, this);
    }

    @Override
    public void onLocationChanged(Location location) {
        if (wifion) {
            mManager.requestPeers(mChannel, this);
            mManager.requestGroupInfo(mChannel, this);
        }

        new LocationWebService(getApplicationContext(), new ActivityCallback() {
            @Override
            public void onSuccess(Result result) {
            }

            @Override
            public void onFailure(Result result) {
                Toast.makeText(getApplicationContext(), result.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }, location).execute();

        new ListLocationsService(getApplicationContext(), null).execute();
        new ListMessagesService(getApplicationContext(), new ActivityCallback() {
            @Override
            public void onSuccess(Result result) {
            }

            @Override
            public void onFailure(Result result) {

            }
        }).execute();
    }

    protected boolean isBetterLocation(Location location, Location currentBestLocation) {
        if (currentBestLocation == null) {
            // A new location is always better than no location //keeping this comment because it's awesome
            return true;
        } else if (location == null) {
            return false;
        }

        long timeDelta = location.getTime() - currentBestLocation.getTime();
        boolean isSignificantlyNewer = timeDelta > INTERVAL;
        boolean isSignificantlyOlder = timeDelta < -INTERVAL;
        boolean isNewer = timeDelta > 0;

        if (isSignificantlyNewer) {
            return true;
        } else if (isSignificantlyOlder) {
            return false;
        }

        int accuracyDelta = (int) (location.getAccuracy() - currentBestLocation.getAccuracy());
        boolean isLessAccurate = accuracyDelta > 0;
        boolean isMoreAccurate = accuracyDelta < 0;
        boolean isSignificantlyLessAccurate = accuracyDelta > 200;

        boolean isFromSameProvider = isSameProvider(location.getProvider(),
                currentBestLocation.getProvider());

        if (isMoreAccurate) {
            return true;
        } else if (isNewer && !isLessAccurate) {
            return true;
        } else if (isNewer && !isSignificantlyLessAccurate && isFromSameProvider) {
            return true;
        }
        return false;
    }

    private boolean isSameProvider(String provider1, String provider2) {
        if (provider1 == null) {
            return provider2 == null;
        }
        return provider1.equals(provider2);
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        startLocationUpdates();
    }

    @Override
    public void onConnectionSuspended(int i) {
        stopLocationUpdates();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        stopLocationUpdates();
    }

    /*
     * Asynctasks implementing message exchange
	 */

    public void DescentralizedMessageSend() {
        msgLst.clear();

        pt.ulisboa.tecnico.meic.cmu.locmess.dto.Location location = null;

        for (MessageDto messageDto : PersistenceManager.getInstance().getMessageRepository().keySet()) {
            location = PersistenceManager.getInstance().getMessage(messageDto).getLocation();
            if (oldAPLocation != null && location instanceof APLocation) {
                if (oldAPLocation.equalAPLocation((APLocation) location)) {
                    msgLst.put(messageDto, PersistenceManager.getInstance().getMessage(messageDto));
                }
            }
        }

        HashMap<MessageDto,Message> MessageDisplay = new HashMap<>();
        for (MessageDto messageDto : PersistenceManager.getInstance().getMessageToCarry().keySet()) {
            Log.d(TAG, "DescentralizedMessageSend: Checking Messages that Device Carry");
            location = PersistenceManager.getInstance().getMessageToCarry().get(messageDto).getLocation();
            if (oldAPLocation != null && location instanceof APLocation) {
                if (oldAPLocation.equalAPLocation((APLocation) location)) {
                    msgLst.put(messageDto, PersistenceManager.getInstance().getMessageToCarry().get(messageDto));
                    MessageDisplay.put(messageDto,PersistenceManager.getInstance().getMessageToCarry().get(messageDto));
                    PersistenceManager.getInstance().getMessageToCarry().remove(messageDto);
                    PersistenceManager.getInstance().setMessageCounter(PersistenceManager.getInstance().getMessageCounter()+1);
                }
            }
        }
        if(!MessageDisplay.isEmpty()) {
            Log.d(TAG, "DescentralizedMessageSend: Confirming Message to display on screen");
            ConfirmMessage(MessageDisplay);
        }



        if (!msgLst.isEmpty()) {
            for (String s : IpDeviceList) {
                new OutgoingCommTask().executeOnExecutor(
                        AsyncTask.THREAD_POOL_EXECUTOR, s
                );

                final String position = msgLst.toString();

                new Thread() {
                    @Override
                    public void run() {
                        while (connected == false) {
                        }
                        new SendCommTask().executeOnExecutor(
                                AsyncTask.THREAD_POOL_EXECUTOR, position);
                    }
                }.start();
            }
        }

        //---------------------------Relay Route--------------------------------------------------

        else {
            Log.d(TAG, "DescentralizedMessageSend: RelayRoute: Starting");
            final int numberOfDevicesToPick = IpDeviceList.size()/2;
            //final int numberOfDevicesToPick = IpDeviceList.size(); // Testar para apenas 1 device
            if (!IpDeviceList.isEmpty() && numberOfDevicesToPick>0) {
                Log.d(TAG, "DescentralizedMessageSend: RelayRoute: Have Devices to Send Messages");
                final int APLogSize = APLog.size();
                final ArrayList<Integer> cache = new ArrayList();
                final int size = IpDeviceList.size();

                for (String s : IpDeviceList) {
                    final String ip = s;
                    final int position = IpDeviceList.indexOf(s);

                    new Thread() {
                        @Override
                        public void run() {
                            try {
                                int n = numberOfDevicesToPick;
                                //int n = numberOfDevicesToPick;
                                Log.d(TAG, "DescentralizedMessageSend: RelayRoute: Thread: Connecting..." );
                                SimWifiP2pSocket CliSocket = new SimWifiP2pSocket(ip,
                                        Integer.parseInt(getString(R.string.port)));
                                Log.d(TAG, "DescentralizedMessageSend: RelayRoute: Thread: Connected" );


                                OutputStream os = CliSocket.getOutputStream();
                                ObjectOutputStream oos = new ObjectOutputStream(os);
                                Log.d(TAG, "DescentralizedMessageSend: RelayRoute: Thread: Out and In Streams created" );
                                oos.writeObject("APListSize");
                                Log.d(TAG, "DescentralizedMessageSend: RelayRoute: Thread: APListSize Sent" );
                                InputStream is = CliSocket.getInputStream();
                                ObjectInputStream ois = new ObjectInputStream(is);
                                int myApsize = (Integer) (ois.readObject());
                                cache.add(position, myApsize);

                                Log.d(TAG, "DescentralizedMessageSend: RelayRoute: Thread: Waiting for other threads write" );
                                while(cache.size() < size){}


                                for (int i : cache){
                                    if(myApsize < i){
                                        --n;
                                    }
                                }

                                Log.d(TAG, "run: Thread: N:" + n);

                                if( n > 0){
                                    Log.d(TAG, "DescentralizedMessageSend: RelayRoute: Thread: Selected Device" );
                                    HashMap<MessageDto,Message> hashMap = new HashMap<MessageDto, Message>();
                                    for (MessageDto m : PersistenceManager.getInstance().getMessageRepository().keySet()){
                                        hashMap.put(m, PersistenceManager.getInstance().getMessage(m));
                                    }

                                    Log.d(TAG, "DescentralizedMessageSend: RelayRoute: Thread: Sending Messages" );
                                    oos.writeObject("Messages");
                                    oos.writeObject(hashMap);
                                    oos.close();
                                    os.close();
                                    CliSocket.close();
                                }

                                else if (n==0) {
                                    Log.d(TAG, "DescentralizedMessageSend: RelayRoute: Thread: Closing-Small Aplist" );
                                   oos.writeObject("\n");
                                    CliSocket.close();
                                }

                            } catch (IOException e) {
                                e.printStackTrace();
                            } catch (ClassNotFoundException e) {
                                e.printStackTrace();
                            }
                        }
                    }.start();
                }
            }
        }
        //----------------------------------------------------------------------------------------

    }

    public void ConfirmMessage(HashMap<MessageDto, Message> messagesReceived) {
        Message message = null;
        for (MessageDto messagedto : messagesReceived.keySet()) {
            message = messagesReceived.get(messagedto);
            if (!PersistenceManager.getInstance().inMessageRepository(messagedto)) {
                    if (message.getPairs().size() != 0) {
                        if (message.getPolicy().equals("W")) {
                            if (PersistenceManager.getInstance().getProfile().containsAll(message.getPairs())) {
                                PersistenceManager.getInstance().getMessageRepository().put(messagedto, message);
                                PersistenceManager.getInstance().saveMessagesDescentralized(getApplicationContext());
                            }
                        } else if (message.getPolicy().equals("B")) {
                            if (!PersistenceManager.getInstance().getProfile().containsAll(message.getPairs())) {
                                PersistenceManager.getInstance().getMessageRepository().put(messagedto, message);
                                PersistenceManager.getInstance().saveMessagesDescentralized(getApplicationContext());
                            }
                        }
                    } else {
                        PersistenceManager.getInstance().getMessageRepository().put(messagedto, message);
                        PersistenceManager.getInstance().saveMessagesDescentralized(getApplicationContext());
                    }
                //}
            }
        }
    }


    @Override
    public void onPeersAvailable(SimWifiP2pDeviceList peers) {
        ArrayList<String> peersStr = new ArrayList<>();

        // compile list of devices in range
        for (SimWifiP2pDevice device : peers.getDeviceList()) {
            String devstr = device.deviceName + " (" + device.getVirtIp() + ")";
            peersStr.add(devstr);
        }
        oldAPLocation = new APLocation(peersStr.toString(), peersStr);

        APLog.add(oldAPLocation);
        Log.d(TAG, "onPeersAvailable: APlocation added to APlog");
        //TODO
    }

    @Override
    public void onGroupInfoAvailable(SimWifiP2pDeviceList devices,
                                     SimWifiP2pInfo groupInfo) {
        IpDeviceList.clear();
        // compile list of network members
        for (String deviceName : groupInfo.getDevicesInNetwork()) {
            SimWifiP2pDevice device = devices.getByName(deviceName);
            String[] s = device.getVirtIp().split(":");
            IpDeviceList.add(s[0]);
        }
        DescentralizedMessageSend();
    }

    public class IncommingCommTask extends AsyncTask<Void, String, Void> {

        @Override
        protected Void doInBackground(Void... params) {

            Log.d(TAG, "IncommingCommTask started (" + this.hashCode() + ").");

            try {
                mSrvSocket = new SimWifiP2pSocketServer(
                        Integer.parseInt(getString(R.string.port)));
            } catch (IOException e) {
                e.printStackTrace();
            }
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    SimWifiP2pSocket sock = mSrvSocket.accept();
                    try {
                        Log.d(TAG, "doInBackground: IncommingTask:Connected");
                        InputStream is = sock.getInputStream();
                        ObjectInputStream ois = new ObjectInputStream(is);
                        String st = (String) ois.readObject();
                        Log.d(TAG, "doInBackground: IncommingTask:Catch Tag String:"+ st);
                        if(st.equals("APListSize")){
                            OutputStream os = sock.getOutputStream();
                            ObjectOutputStream oos = new ObjectOutputStream(os);
                            Log.d(TAG, "doInBackground: Incomming: Relay Route Selected");
                            oos.writeObject(APLog.size());
                            st = (String) ois.readObject();

                            if (st.equals("\n")){
                                Log.d(TAG, "doInBackground: Incomming: Device Not Selected");
                                break;
                            }

                            else{
                                if (PersistenceManager.getInstance().getMessageCounter() == 0) {
                                    Log.d(TAG, "doInBackground: Incomming: Full on messages to carry");
                                    break;
                                }
                                else {
                                    Log.d(TAG, "doInBackground: Incomming: Receiving messages...");
                                    HashMap<MessageDto, Message> hashMap = (HashMap) ois.readObject();

                                    Log.d(TAG, "doInBackground: Incomming: Selecting messages...");
                                    MessageDto m;
                                    while(PersistenceManager.getInstance().getMessageCounter() > 0) {

                                        m = (MessageDto) hashMap.keySet().toArray()[0];
                                        boolean toAdd = false;

                                        int i = 0;
                                        int j = 0;
                                        for (MessageDto messageDto : hashMap.keySet()) {
                                            for (APLocation l : APLog){
                                                if(l.equalAPLocation((APLocation) hashMap.get(m).getLocation())){
                                                    j++;
                                                }
                                            }
                                           if (j >= i && !PersistenceManager.getInstance().getMessageToCarry().containsKey(m)){
                                                    i = j;
                                                    j = 0;
                                                    m = messageDto;
                                                    toAdd = true;
                                            }
                                        }
                                        if(toAdd) {
                                            Log.d(TAG, "doInBackground: Incomming: Adding message ...");
                                            PersistenceManager.getInstance().getMessageToCarry().put(m, hashMap.get(m));
                                            PersistenceManager.getInstance().setMessageCounter(PersistenceManager.getInstance().getMessageCounter() - 1);
                                        }
                                        else{
                                            Log.d(TAG, "doInBackground: Incomming: No more Messages ...");
                                            break;
                                        }
                                        ois.close();
                                        is.close();

                                        for (MessageDto dto : PersistenceManager.getInstance().getMessageToCarry().keySet())
                                            Log.d(TAG, "doInBackground: Incomming " + dto );
                                    }
                                }
                            }
                            oos.close();
                            os.close();
                        }

                        else {
                            Log.d(TAG, "doInBackground: Incomming: APLocation Receiving ...");
                            HashMap<MessageDto,Message> m = (HashMap) ois.readObject();
                            ConfirmMessage(m);
                        }
                        ois.close();
                        is.close();
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    } finally {
                        sock.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    break;
                }
            }
            return null;
        }
    }




    public class OutgoingCommTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            try {
                mCliSocket = new SimWifiP2pSocket(params[0],
                        Integer.parseInt(getString(R.string.port)));
                connected = true;
            } catch (UnknownHostException e) {
                return "Unknown Host:" + e.getMessage();
            } catch (IOException e) {
                return "IO error:" + e.getMessage();
            }
            return null;
        }
    }

    public class SendCommTask extends AsyncTask<String, String, Void> {

        @Override
        protected Void doInBackground(String... msg) {
            try {
                Log.d(TAG, "doInBackground: Notifying Devices about Sending Messages on APL");
                OutputStream os = mCliSocket.getOutputStream();
                ObjectOutputStream oos = new ObjectOutputStream(os);
                oos.writeObject("MessagesOnAPL");
                Log.d(TAG, "doInBackground: Sending Messages on APL");
                oos.writeObject(msgLst);
                connected = false;
                oos.close();
                os.close();
                mCliSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            mCliSocket = null;
            return null;
        }

    }
    
    private ServiceConnection mConnection = new ServiceConnection() {
        // callbacks for service binding, passed to bindService()

        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            mManager = new SimWifiP2pManager(new Messenger(service));
            mChannel = mManager.initialize(getApplication(), getMainLooper(), null);
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mManager = null;
            mChannel = null;
        }
    };
}