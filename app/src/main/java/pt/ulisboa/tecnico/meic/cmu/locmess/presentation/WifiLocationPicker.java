package pt.ulisboa.tecnico.meic.cmu.locmess.presentation;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import pt.ulisboa.tecnico.meic.cmu.locmess.R;
import pt.ulisboa.tecnico.meic.cmu.locmess.domain.God;
import pt.ulisboa.tecnico.meic.cmu.locmess.googleapi.GoogleAPI;
import pt.ulisboa.tecnico.meic.cmu.locmess.interfaces.ActivityCallback;

/**
 * Created by jp_s on 4/27/2017.
 */

public class WifiLocationPicker extends AppCompatActivity {

        private static final String TAG = pt.ulisboa.tecnico.meic.cmu.locmess.presentation.WifiLocationPicker.class.getSimpleName();
        private Toolbar toolbar;
        WifiManager wifi;
        BroadcastReceiver broadcastreceiver;
        List<ScanResult> results = wifi.getScanResults();
        private int size;


    @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.location_wifi_list);

            toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);



            wifi = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            if (wifi.isWifiEnabled() == false)
            {
                Toast.makeText(getApplicationContext(), "wifi is disabled..making it enabled", Toast.LENGTH_LONG).show();
                wifi.setWifiEnabled(true);
            }

            broadcastreceiver = new BroadcastReceiver()
            {
                @Override
                public void onReceive(Context c, Intent intent)
                {
                    results = wifi.getScanResults();
                    size = results.size();
                }
            };
            getApplicationContext().registerReceiver(broadcastreceiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));

            getApplicationContext().unregisterReceiver(broadcastreceiver);
    }


    public void onStart(View view)
    {
        wifi.startScan();

        Toast.makeText(this, "Scanning...." + size, Toast.LENGTH_SHORT).show();
        try
        {
            size = size - 1;
            while (size >= 0)
            {
                /*HashMap<String, String> item = new HashMap<String, String>();
                item.put(ITEM_KEY, results.get(size).SSID + "  " + results.get(size).capabilities);

                arraylist.add(item);
                size--;
                adapter.notifyDataSetChanged();*/
            }
        }
        catch (Exception e)
        { }
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
                Toast.makeText(getApplicationContext(), "yey accepted", Toast.LENGTH_LONG).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}

