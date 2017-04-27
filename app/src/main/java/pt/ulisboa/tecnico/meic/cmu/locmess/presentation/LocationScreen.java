package pt.ulisboa.tecnico.meic.cmu.locmess.presentation;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import pt.ulisboa.tecnico.meic.cmu.locmess.R;
import pt.ulisboa.tecnico.meic.cmu.locmess.domain.God;
import pt.ulisboa.tecnico.meic.cmu.locmess.domain.geofence.GeofenceManager;
import pt.ulisboa.tecnico.meic.cmu.locmess.domain.geofence.MyGeofence;
import pt.ulisboa.tecnico.meic.cmu.locmess.dto.GPSLocation;
import pt.ulisboa.tecnico.meic.cmu.locmess.dto.Location;
import pt.ulisboa.tecnico.meic.cmu.locmess.dto.Message;
import pt.ulisboa.tecnico.meic.cmu.locmess.googleapi.GoogleAPI;
import pt.ulisboa.tecnico.meic.cmu.locmess.interfaces.ActivityCallback;
import pt.ulisboa.tecnico.meic.cmu.locmess.service.ListLocationsService;

import static android.support.v4.widget.SwipeRefreshLayout.*;

/**
 * Created by jp_s on 4/14/2017.
 */

public class LocationScreen extends AppCompatActivity implements ActivityCallback {

    private static final String TAG = LocationScreen.class.getSimpleName();
    private Toolbar toolbar;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle drawerToggle;
    private ProgressDialog dialog;
    private ArrayList<String> locations;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.locationscreen);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        toolbar.setTitle("Locations");

        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        drawerToggle = setupDrawerToggle();
        drawerLayout.addDrawerListener(drawerToggle);
        NavigationView nvDrawer = (NavigationView) findViewById(R.id.nvView);
        setupDrawerContent(nvDrawer);
        noLocationDisplay();

        findViewById(R.id.gps_location).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), GPSLocationPicker.class));
            }
        });
        findViewById(R.id.wifi_location).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!debug) {
                    God.getInstance().startLocationUpdates();
                   // GeofenceManager.getInstance().addGeofence(new MyGeofence("teste", 38.7355793, -9.1329183, 20000.0f));
                }else {
                    God.getInstance().stopLocationUpdates();
                    //GeofenceManager.getInstance().removeAllGeofences();
                }
                debug = !debug;
                //Toast.makeText(getApplicationContext(), "WIFI", Toast.LENGTH_SHORT).show();
                //startActivity(new Intent(getApplicationContext(), GPSLocationPicker.class));
            }
        });

        SwipeRefreshLayout swip = (SwipeRefreshLayout) findViewById(R.id.swipe_container);
        swip.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshLocations();
            }
        });
        swip.setColorSchemeResources(R.color.accent_material_light, R.color.colorPrimary);;

    }

    boolean debug;//FIXME delete


    @Override
    protected void onStart() {
        super.onStart();
        GoogleAPI.getInstance().connect();
        refreshLocations();
    }

    private void refreshLocations() {
        new ListLocationsService(getApplicationContext(), this).execute();
        dialog = WidgetConstructors.getLoadingDialog(this, "Getting locations...");
        dialog.show();
    }

    private void setupDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        selectDrawerItem(menuItem);
                        return true;
                    }
                });
    }

    //toolbar reference.
    private ActionBarDrawerToggle setupDrawerToggle() {
        return new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // The action bar home/up action should open or close the drawer.
        switch (item.getItemId()) {
            case android.R.id.home:
                drawerLayout.openDrawer(GravityCompat.START);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        drawerToggle.syncState();
    }

    public void noLocationDisplay() {
        ListView listview = (ListView) findViewById(R.id.LocationsList);
        TextView textView = (TextView) findViewById(R.id.EmptyList);
        textView.setText("No Locations To Show");
        listview.setEmptyView(textView);
    }

    public void selectDrawerItem(MenuItem menuItem) {
        DrawerCode.selectDrawerItem(menuItem, this, drawerLayout, getApplicationContext());
    }

    @Override
    public void onSuccess(Message result) {
        if(result.getMessage().equals("LLs")){
            ListView lv = (ListView) findViewById(R.id.LocationsList);
            locations = new ArrayList<>();
            for (GPSLocation location : God.getInstance().getLocations()){
                locations.add(location.toString());
            }
            ArrayAdapter<String> aa =
                    new ArrayAdapter<>(this,
                            android.R.layout.simple_list_item_1,
                            locations);
            lv.setAdapter(aa);
            if(dialog != null) dialog.cancel();
        }else{
            Intent intent = new Intent(this, Login.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }
    }

    @Override
    public void onFailure(Message result) {
        if(dialog != null) dialog.cancel();
    }

    @Override
    protected void onDestroy() {
        God.getInstance().saveState();
        //GoogleAPI.getInstance().disconnect();
        super.onDestroy();
    }
}
