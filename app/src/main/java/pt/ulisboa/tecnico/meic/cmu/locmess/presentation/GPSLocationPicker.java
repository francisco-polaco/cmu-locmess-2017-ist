package pt.ulisboa.tecnico.meic.cmu.locmess.presentation;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionButton;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import pt.ulisboa.tecnico.meic.cmu.locmess.R;
import pt.ulisboa.tecnico.meic.cmu.locmess.domain.exception.ImpossibleToGetLocationException;
import pt.ulisboa.tecnico.meic.cmu.locmess.dto.GPSLocation;
import pt.ulisboa.tecnico.meic.cmu.locmess.dto.Message;
import pt.ulisboa.tecnico.meic.cmu.locmess.interfaces.ActivityCallback;
import pt.ulisboa.tecnico.meic.cmu.locmess.service.AddLocationService;
import pt.ulisboa.tecnico.meic.cmu.locmess.service.GetLastLocationService;

public class GPSLocationPicker extends AppCompatActivity implements OnMapReadyCallback, ActivityCallback {

    private static final String TAG = GPSLocationPicker.class.getSimpleName();
    private static final int ZOOM_LEVEL = 17;
    float mRadius = 150;
    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private boolean circleValue = true;
    private LatLng latLong;
    private Place mPlace;
    private LatLng myLocation; //needs to e dynamic update
    private Circle mMapCircle;
    private ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_picker);
        setUpMapIfNeeded();
        Toolbar toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        toolbar.setTitle(getString(R.string.activity_name_gps_location));
        setSupportActionBar(toolbar);
        //getActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        SeekBar seekBar = (SeekBar) findViewById(R.id.seekBar);
        FloatingActionButton recenter = (FloatingActionButton) findViewById(R.id.my_location);
        seekBar.setProgress((int) mRadius);

        recenter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                zoomAtMe();
            }
        });
        /*mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                latLong = latLng;
                drawMarker();
                drawCenter();
                if (circleValue) {
                    drawCircle();
                }
            }
        });*/

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progresValue, boolean fromUser) {
                mRadius = progresValue + 100;
                drawMarker();
                drawCircle();
                drawCenter();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                //TODO passar o valor final do raio
            }
        });
        autocomplete();
        // Zoom do mapa
        //startInZoomedArea();
    }

    private void getLastKnownLocation() {
        GetLastLocationService service = new GetLastLocationService();
        try {
            service.execute();
            latLong = service.result();
            myLocation = service.result();
        } catch (ImpossibleToGetLocationException e) {
            latLong = new LatLng(38.7368192, -9.138705); // IST
            myLocation = new LatLng(38.7368192, -9.138705); // IST
            Toast.makeText(getApplicationContext(), R.string.toast_error_last_known_location,
                    Toast.LENGTH_LONG).show();
        }
    }

    private void drawCircle() {
        CircleOptions circleOptions = new CircleOptions();
        circleOptions.center(latLong);
        circleOptions.strokeWidth(5);
        circleOptions.radius(mRadius);
        circleOptions.strokeColor(Color.argb(200, 0, 255, 0));
        circleOptions.fillColor(Color.argb(50, 0, 255, 0));
        mMap.addCircle(circleOptions);
    }

    private void drawMarker() {
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLong);
        markerOptions.title(latLong.latitude + " : " + latLong.longitude);
        mMap.clear();
        mMap.addMarker(markerOptions);
    }

    private void drawCenter() {
        if (myLocation != null)
            drawMyPosition();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
    }


    /**
     * Sets up the map if it is possible to do so (i.e., the Google Play services APK is correctly
     * installed) and the map has not already been instantiated.. This will ensure that we only ever
     * call {@link #setUpMap()} once when {@link #mMap} is not null.
     * <p/>
     * If it isn't installed {@link SupportMapFragment} (and
     * {@link com.google.android.gms.maps.MapView MapView}) will show a prompt for the user to
     * install/update the Google Play services APK on their device.
     * <p/>
     * A user can return to this FragmentActivity after following the prompt and correctly
     * installing/updating/enabling the Google Play services. Since the FragmentActivity may not
     * have been completely destroyed during this process (it is likely that it would only be
     * stopped or paused), {@link #onCreate(Bundle)} may not be called again so we should call this
     * method in {@link #onResume()} to guarantee that it will be called.
     */
    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            /*mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();*/
            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.map);
            mapFragment.getMapAsync(this);
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap();
            }
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        getLastKnownLocation();
        mMap = googleMap;
        // Enabling MyLocation Layer of Google Map
        //googleMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setIndoorLevelPickerEnabled(false);
        mMap.getUiSettings().setCompassEnabled(true);
        mMap.getUiSettings().setMapToolbarEnabled(false);
        setUpMap();
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                latLong = latLng;
                drawMarker();
                drawCenter();
                if (circleValue) {
                    drawCircle();
                }
            }
        });

        startInZoomedArea();
    }

    /**
     * This is where we can add markers or lines, add listeners or move the camera. In this case, we
     * just add a marker near Africa.
     * <p/>
     * This should only be called once and when we are sure that {@link #mMap} is not null.
     */
    private void setUpMap() {
        mMap.addMarker(new MarkerOptions().position(latLong).title("Marker"));
        drawMarker();
        drawCircle();
        drawCenter();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_maps, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.action_accept:
                String name = ((EditText)findViewById(R.id.gps_location_name)).getText().toString();
                if(name.equals("")){
                    Toast.makeText(getApplicationContext(), "Name is empty.", Toast.LENGTH_LONG).show();
                    return true;
                }
                new AddLocationService(getApplicationContext(),
                        this,
                        new GPSLocation(((EditText)findViewById(R.id.gps_location_name)).getText().toString(),
                                latLong, mRadius)).execute();
                dialog = WidgetConstructors.getLoadingDialog(this, getString(R.string.dialog_message_add_gps_location));
                dialog.show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void autocomplete() {
        PlaceAutocompleteFragment autocompleteFragment = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);

        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                Log.d("Widget", "onPlaceSelected");
                mPlace = place;
                latLong = mPlace.getLatLng();
                zoomAtArea();
                drawMarker();
                drawCircle();
                drawCenter();
            }

            @Override
            public void onError(Status status) {
                Log.d("Widget", "onError " + status.getStatusMessage());
            }
        });
    }

    private void zoomAtArea() {
        if (latLong.longitude == 0 && latLong.latitude == 0)
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLong, 1));
        else
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLong, ZOOM_LEVEL));
    }

    private void startInZoomedArea() {
        if (latLong.longitude == 0 && latLong.latitude == 0)
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLong, 1));
        else
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLong, ZOOM_LEVEL));
    }

    private void zoomAtMe() {
        if (myLocation != null)
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(myLocation, ZOOM_LEVEL));
    }

    private void drawMyPosition() {
        if (mMapCircle != null) mMapCircle.remove();
        CircleOptions circleOptions = new CircleOptions();
        circleOptions.center(myLocation);
        circleOptions.strokeWidth(2);
        circleOptions.radius(10);
        circleOptions.strokeColor(Color.argb(255, 50, 200, 255));
        circleOptions.fillColor(Color.argb(100, 50, 200, 255));
        //mMapCircle = mMap.addCircle(circleOptions);
    }


    @Override
    public void onSuccess(Message result) {
        if(dialog != null) dialog.cancel();
        Log.d(TAG, result.getMessage());
        Toast.makeText(getApplicationContext(), result.getMessage(), Toast.LENGTH_LONG).show();
        finish();
    }

    @Override
    public void onFailure(Message result) {
        if(dialog != null) dialog.cancel();
        Log.d(TAG, result.getMessage());
        Toast.makeText(getApplicationContext(), result.getMessage(), Toast.LENGTH_LONG).show();
    }
}
