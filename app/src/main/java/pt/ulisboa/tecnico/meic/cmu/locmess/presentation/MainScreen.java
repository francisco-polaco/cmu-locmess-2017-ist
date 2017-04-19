package pt.ulisboa.tecnico.meic.cmu.locmess.presentation;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import pt.ulisboa.tecnico.meic.cmu.locmess.R;
import pt.ulisboa.tecnico.meic.cmu.locmess.googleapi.GoogleAPI;
import pt.ulisboa.tecnico.meic.cmu.locmess.service.LogoutService;

/**
 * Created by jp_s on 4/14/2017.
 */

public class MainScreen extends AppCompatActivity {

    private static final String TAG = MainScreen.class.getSimpleName();
    private static final int PERMISSION_REQUEST_CODE = 666;
    private Toolbar toolbar;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle drawerToggle;
    private NavigationView nvDrawer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mainscreen);
        noMessageDisplay();

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        toolbar.setTitle("Messages");

        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        drawerToggle = setupDrawerToggle();
        drawerLayout.addDrawerListener(drawerToggle);

        nvDrawer = (NavigationView) findViewById(R.id.nvView);
        setupDrawerContent(nvDrawer);

        Log.d(TAG, "HELLO");
        GoogleAPI googleAPI = GoogleAPI.getInstance();
        googleAPI.connect();

    }

    @Override
    protected void onStart() {
        super.onStart();
        checkBasePermission();
    }

    //toolbar reference.
    private ActionBarDrawerToggle setupDrawerToggle() {
        return new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close);
    }

    public void NewMessage(View view) {
        Intent intent = new Intent(this, NewMessage.class);
        startActivity(intent);
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

    public void noMessageDisplay() {
        ListView listview = (ListView) findViewById(R.id.MessageList);
        TextView textView = (TextView) findViewById(R.id.empty);
        textView.setText(R.string.main_no_messages);
        listview.setEmptyView(textView);
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

    public void selectDrawerItem(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.Message:
                Intent message = new Intent(this, MainScreen.class);
                startActivity(message);
                break;
            case R.id.Locations:
                Intent location = new Intent(this, LocationScreen.class);
                startActivity(location);
                break;
            case R.id.EditProfile:
                Intent editprofile = new Intent(this, EditProfile.class);
                startActivity(editprofile);
                break;
            case R.id.Logout:
                Intent intent = new Intent(this, Login.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                new LogoutService().execute();
                startActivity(intent);
                break;
        }
        menuItem.setCheckable(false);
        drawerLayout.closeDrawers();

    }


    private void checkBasePermission() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "Permission is not granted, requesting");
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Provide an additional rationale to the user if the permission was not granted
                // and the user would benefit from additional context for the use of the permission.
                // For example, if the request has been denied previously.
                Log.i(TAG,
                        "Displaying location permission rationale to provide additional context.");
                showPermissionDialog();
            } else {
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_REQUEST_CODE);
            }
        } else {
            Log.d(TAG, "Permission is granted");
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "Permission has been granted");
                //setUserInteraction(true);
            } else {
                Log.d(TAG, "Permission has been denied or request cancelled");
                showPermissionDialog();
                //setUserInteraction(false);
            }
        }
    }

    private void showPermissionDialog() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.title_rationale_dialog)
                .setMessage(R.string.message_rationale_dialog)
                .setPositiveButton(R.string.positive_rationale_dialog, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        Uri uri = Uri.fromParts("package", MainScreen.this.getPackageName(), null);
                        intent.setData(uri);
                        startActivity(intent);
                        dialog.dismiss();
                    }
                })
                .setNegativeButton(R.string.negative_rationale_dialog, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        MainScreen.this.finish();
                    }
                })
                .show();
    }
}
