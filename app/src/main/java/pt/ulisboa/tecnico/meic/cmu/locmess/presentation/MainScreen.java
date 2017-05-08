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
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import pt.ulisboa.tecnico.meic.cmu.locmess.R;
import pt.ulisboa.tecnico.meic.cmu.locmess.domain.God;
import pt.ulisboa.tecnico.meic.cmu.locmess.domain.exception.NotInitializedException;
import pt.ulisboa.tecnico.meic.cmu.locmess.dto.MessageDto;
import pt.ulisboa.tecnico.meic.cmu.locmess.dto.Result;
import pt.ulisboa.tecnico.meic.cmu.locmess.googleapi.GoogleAPI;
import pt.ulisboa.tecnico.meic.cmu.locmess.handler.MessagesRvAdapter;
import pt.ulisboa.tecnico.meic.cmu.locmess.interfaces.ActivityCallback;
import pt.ulisboa.tecnico.meic.cmu.locmess.service.ListMessagesService;
import pt.ulisboa.tecnico.meic.cmu.locmess.service.UnpostMessageService;

/**
 * Created by jp_s on 4/14/2017.
 */

public class MainScreen extends AppCompatActivity implements ActivityCallback {

    private static final String TAG = MainScreen.class.getSimpleName();
    private static final int PERMISSION_REQUEST_CODE = 666;
    private Toolbar toolbar;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle drawerToggle;
    private RecyclerView msgListView;
    private MessagesRvAdapter adapter;
    private List<MessageDto> messages = new ArrayList<>();

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

        NavigationView nvDrawer = (NavigationView) findViewById(R.id.nvView);
        setupDrawerContent(nvDrawer);

        final SwipeRefreshLayout swip = (SwipeRefreshLayout) findViewById(R.id.swipe_container);
        swip.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new ListMessageListener();
                if (swip.isRefreshing()) {
                    swip.setRefreshing(false);
                }
            }
        });
        swip.setColorSchemeResources(R.color.accent_material_light, R.color.colorPrimary);

        try {
            God.getInstance();
        } catch (NotInitializedException e) {
            God.init(getApplicationContext());
        }
        GoogleAPI.init(getApplicationContext(), false);
        God.getInstance().startLocationUpdates();
        initRecyclerView();
    }

    private void initRecyclerView() {
        msgListView = (RecyclerView) findViewById(R.id.MessageList);
        adapter = new MessagesRvAdapter(messages, getApplicationContext());
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        msgListView.setLayoutManager(mLayoutManager);
        msgListView.setItemAnimator(new DefaultItemAnimator());
        msgListView.setAdapter(adapter);
        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
                new RemoveMessageListener(adapter.getMessageById(viewHolder.getAdapterPosition()));
            }
        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(msgListView);
    }

    @Override
    protected void onStart() {
        super.onStart();
        checkBasePermission();
        new ListMessageListener();
        GoogleAPI.getInstance().connect();
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
        /*ListView listview = (ListView) findViewById(R.id.MessageList);
        TextView textView = (TextView) findViewById(R.id.empty);
        textView.setText(R.string.main_no_messages);
        listview.setEmptyView(textView);*/
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
        DrawerCode.selectDrawerItem(menuItem, this, drawerLayout, getApplicationContext());
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


    @Override
    public void onSuccess(Result result) {
        Log.d(TAG, "success");
        Intent intent = new Intent(this, Login.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    @Override
    public void onFailure(Result result) {
        Toast.makeText(getApplicationContext(), "Can't logout", Toast.LENGTH_LONG).show();
    }


    public class ListMessageListener {

        public ListMessageListener() {
            new ListMessagesService(getApplicationContext(), new ActivityCallback() {
                @Override
                public void onSuccess(Result result) {
                    messages.clear();
                    messages.addAll(God.getInstance().getMessages().values());
                    adapter.notifyDataSetChanged();
                }

                @Override
                public void onFailure(Result result) {
                    Toast.makeText(getApplicationContext(), "Failed to retrieve messages!", Toast.LENGTH_LONG).show();
                }
            }).execute();
        }
    }

    public class RemoveMessageListener {

        public RemoveMessageListener(final MessageDto messageDto) {

            new UnpostMessageService(getApplicationContext(), new ActivityCallback() {
                @Override
                public void onSuccess(Result result) {
                    Toast.makeText(getApplicationContext(), result.getMessage(), Toast.LENGTH_LONG).show();
                    adapter.removeMsg(messageDto);
                }

                @Override
                public void onFailure(Result result) {
                    adapter.notifyDataSetChanged();
                    Toast.makeText(getApplicationContext(), result.getMessage(), Toast.LENGTH_LONG).show();
                }
            }, messageDto).execute();
        }
    }


}
