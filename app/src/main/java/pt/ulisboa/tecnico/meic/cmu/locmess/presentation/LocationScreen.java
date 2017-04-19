package pt.ulisboa.tecnico.meic.cmu.locmess.presentation;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import pt.ulisboa.tecnico.meic.cmu.locmess.R;

/**
 * Created by jp_s on 4/14/2017.
 */

public class LocationScreen extends AppCompatActivity {

    private Toolbar toolbar;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle drawerToggle;

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
                Toast.makeText(getApplicationContext(), "WIFI", Toast.LENGTH_SHORT).show();
                //startActivity(new Intent(getApplicationContext(), GPSLocationPicker.class));
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

}
