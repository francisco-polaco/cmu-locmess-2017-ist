package pt.ulisboa.tecnico.meic.cmu.locmess.presentation;

import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Messenger;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import pt.inesc.termite.wifidirect.SimWifiP2pBroadcast;
import pt.inesc.termite.wifidirect.SimWifiP2pDevice;
import pt.inesc.termite.wifidirect.SimWifiP2pDeviceList;
import pt.inesc.termite.wifidirect.SimWifiP2pManager;
import pt.inesc.termite.wifidirect.service.SimWifiP2pService;
import pt.ulisboa.tecnico.meic.cmu.locmess.R;
import pt.ulisboa.tecnico.meic.cmu.locmess.domain.SimWifiP2pBroadcastReceiver;
import pt.ulisboa.tecnico.meic.cmu.locmess.dto.APLocation;
import pt.ulisboa.tecnico.meic.cmu.locmess.dto.Result;
import pt.ulisboa.tecnico.meic.cmu.locmess.interfaces.ActivityCallback;
import pt.ulisboa.tecnico.meic.cmu.locmess.service.AddWifiLocationService;

/**
 * Created by jp_s on 4/27/2017.
 */

public class WifiLocationPicker extends AppCompatActivity implements
        SimWifiP2pManager.PeerListListener, ActivityCallback {

    private static final String TAG = pt.ulisboa.tecnico.meic.cmu.locmess.presentation.WifiLocationPicker.class.getSimpleName();
    private Toolbar toolbar;
    private ProgressDialog dialog;
    private List<String> peersStr = new ArrayList<>();

    private SimWifiP2pManager mManager = null;
    private SimWifiP2pManager.Channel mChannel = null;
    private ServiceConnection mConnection = new ServiceConnection() {
        // callbacks for service binding, passed to bindService()

        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            Log.d(TAG, "mConnection: Entrei!");
            mManager = new SimWifiP2pManager(new Messenger(service));
            mChannel = mManager.initialize(getApplication(), getMainLooper(), null);
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mManager = null;
            mChannel = null;
        }
    };

    @Override

        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.location_wifi_list);

            toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }

    @Override
    public void onStart() {
        super.onStart();
        Intent intent = new Intent(getApplicationContext(), SimWifiP2pService.class);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
    }

    public void getDevices(View view) {
        mManager.requestPeers(mChannel, this);
    }

    @Override
    public void onStop() {
        super.onStop();
        unbindService(mConnection);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_maps, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.action_accept:
                new AddWifiLocationService(getApplicationContext(),
                        this,
                        new APLocation("", peersStr)).execute();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onPeersAvailable(SimWifiP2pDeviceList peers) {
        Log.d(TAG, "onPeersAvailable: Entrei111111");
        if (peers != null) {
            peersStr.clear();

            // compile list of devices in range
            for (SimWifiP2pDevice device : peers.getDeviceList()) {
                String devstr = device.deviceName + " (" + device.getVirtIp() + ")";
                peersStr.add(devstr);
            }


            ArrayAdapter adapter = new ArrayAdapter<String>(this,
                    android.R.layout.simple_list_item_1, peersStr);

            ListView listview = (ListView) findViewById(R.id.wifi_list);
            listview.setAdapter(adapter);
        }
    }

    @Override
    public void onSuccess(Result result) {
        if (dialog != null) dialog.cancel();
        Log.d(TAG, result.getMessage());
        Toast.makeText(getApplicationContext(), result.getMessage(), Toast.LENGTH_LONG).show();
        finish();
    }

    @Override
    public void onFailure(Result result) {
        if (dialog != null) dialog.cancel();
        Log.d(TAG, result.getMessage());
        Toast.makeText(getApplicationContext(), result.getMessage(), Toast.LENGTH_LONG).show();
    }
}

