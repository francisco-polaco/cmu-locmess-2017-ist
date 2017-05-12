package pt.ulisboa.tecnico.meic.cmu.locmess.presentation;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import pt.ulisboa.tecnico.meic.cmu.locmess.R;
import pt.ulisboa.tecnico.meic.cmu.locmess.dto.Location;
import pt.ulisboa.tecnico.meic.cmu.locmess.dto.Result;
import pt.ulisboa.tecnico.meic.cmu.locmess.googleapi.GoogleAPI;
import pt.ulisboa.tecnico.meic.cmu.locmess.handler.LocationRvAdapter;
import pt.ulisboa.tecnico.meic.cmu.locmess.interfaces.ActivityCallback;
import pt.ulisboa.tecnico.meic.cmu.locmess.interfaces.LocmessListener;
import pt.ulisboa.tecnico.meic.cmu.locmess.service.ListLocationsService;
import pt.ulisboa.tecnico.meic.cmu.locmess.service.RemoveLocationService;

import static android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;

/**
 * Created by jp_s on 4/14/2017.
 */

public class LocationScreen extends AppCompatActivity {

    private static final String TAG = LocationScreen.class.getSimpleName();
    private Toolbar toolbar;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle drawerToggle;
    private ProgressDialog dialog;
    private ArrayList<Location> locations = new ArrayList<>();
    ;
    private LocationRvAdapter adapter;
    private RecyclerView locListView;

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

        findViewById(R.id.gps_location).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), GPSLocationPicker.class));
            }
        });
        findViewById(R.id.wifi_location).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), WifiLocationPicker.class));
            }
        });

        final SwipeRefreshLayout swip = (SwipeRefreshLayout) findViewById(R.id.swipe_container);
        swip.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshLocations();
                if (swip.isRefreshing()) {
                    swip.setRefreshing(false);
                }
            }
        });
        swip.setColorSchemeResources(R.color.accent_material_light, R.color.colorPrimary);
    }

    @Override
    protected void onStart() {
        super.onStart();
        GoogleAPI.getInstance().connect();
        initRecyclerView();
        refreshLocations();
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        drawerToggle.syncState();
    }

    private void refreshLocations() {
        new ListLocationsListener(getApplicationContext());
        dialog = WidgetConstructors.getLoadingDialog(this, "Getting locations...");
        dialog.show();
    }

    private void initRecyclerView() {
        locListView = (RecyclerView) findViewById(R.id.LocationsList);
        adapter = new LocationRvAdapter(locations, getApplicationContext());
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        locListView.setLayoutManager(mLayoutManager);
        locListView.setItemAnimator(new DefaultItemAnimator());
        locListView.setAdapter(adapter);
        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
                new RemoveLocationListener(getApplicationContext(), adapter.getMessageById(viewHolder.getAdapterPosition()));
            }
        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(locListView);
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

    public void selectDrawerItem(MenuItem menuItem) {
        DrawerCode.selectDrawerItem(menuItem, this, drawerLayout, getApplicationContext());
    }

    private class ListLocationsListener extends LocmessListener implements ActivityCallback {

        protected ListLocationsListener(Context context) {
            super(context);
            new ListLocationsService(getApplicationContext(), this).execute();
        }

        @Override
        public void onSuccess(Result result) {
            List<Location> allLocations = (List<Location>) result.getPiggyback();
            locations.clear();
            for (Location location : allLocations)
                adapter.addLoc(location);
            if (dialog != null) dialog.cancel();
        }

        @Override
        public void onFailure(Result result) {
            if (dialog != null) dialog.cancel();
            Toast.makeText(getContext(), result.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private class RemoveLocationListener extends LocmessListener implements ActivityCallback {

        private Location location;

        protected RemoveLocationListener(Context context, Location location) {
            super(context);
            this.location = location;
            new RemoveLocationService(context, this, location).execute();
        }

        @Override
        public void onSuccess(Result result) {
            adapter.removeLoc(location);
            Toast.makeText(getContext(), result.getMessage(), Toast.LENGTH_LONG).show();
        }

        @Override
        public void onFailure(Result result) {
            Toast.makeText(getContext(), result.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

}
