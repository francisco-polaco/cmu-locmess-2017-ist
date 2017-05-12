package pt.ulisboa.tecnico.meic.cmu.locmess.presentation;

import android.app.ProgressDialog;
import android.content.Context;
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
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import pt.ulisboa.tecnico.meic.cmu.locmess.R;
import pt.ulisboa.tecnico.meic.cmu.locmess.dto.Pair;
import pt.ulisboa.tecnico.meic.cmu.locmess.dto.Result;
import pt.ulisboa.tecnico.meic.cmu.locmess.handler.ProfileRvAdapter;
import pt.ulisboa.tecnico.meic.cmu.locmess.interfaces.ActivityCallback;
import pt.ulisboa.tecnico.meic.cmu.locmess.interfaces.LocmessListener;
import pt.ulisboa.tecnico.meic.cmu.locmess.service.AddPairService;
import pt.ulisboa.tecnico.meic.cmu.locmess.service.ListPairsService;
import pt.ulisboa.tecnico.meic.cmu.locmess.service.RemovePairService;


public class EditProfile extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle drawerToggle;
    private ProfileRvAdapter adapter;
    private Toolbar toolbar;
    private ProgressDialog dialog;
    private List<Pair> pairs = new ArrayList<>();
    private RecyclerView pairsListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.editprofile);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        toolbar.setTitle(R.string.edit_profile_title);

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
                new ListPairsListener(getApplicationContext());
                if (swip.isRefreshing()) {
                    swip.setRefreshing(false);
                }
            }
        });
        swip.setColorSchemeResources(R.color.accent_material_light, R.color.colorPrimary);

        new ListPairsListener(getApplicationContext());
        dialog = WidgetConstructors.getLoadingDialog(this, getString(R.string.dialog_retrieve_profile));
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

    public void selectDrawerItem(MenuItem menuItem) {
        DrawerCode.selectDrawerItem(menuItem, this, drawerLayout, getApplicationContext());
    }

    //toolbar reference.
    private ActionBarDrawerToggle setupDrawerToggle() {
        return new ActionBarDrawerToggle(this, drawerLayout,
                toolbar, R.string.drawer_open, R.string.drawer_close);
    }

    @Override
    protected void onStart() {
        super.onStart();
        initRecyclerView();
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


    public void addPair(View view) {
        EditText value = (EditText) findViewById(R.id.Value);
        EditText key = (EditText) findViewById(R.id.Key);

        if (value.getText().toString().equals("") || key.getText().toString().equals("")) {
            value.setText("");
            key.setText("");
            Toast.makeText(getApplicationContext(), "Key or Value should have a value!", Toast.LENGTH_SHORT).show();
            return;
        }

        Pair pair = new Pair(key.getText().toString(), value.getText().toString());
        if (pairs.contains(pair)) {
            Toast.makeText(getApplicationContext(), "Pair already exists!", Toast.LENGTH_SHORT).show();
            return;
        }
        new AddPairListener(getApplicationContext(), pair);
    }

    private void initRecyclerView() {
        pairsListView = (RecyclerView) findViewById(R.id.PairsList);
        adapter = new ProfileRvAdapter(pairs, getApplicationContext());
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        pairsListView.setLayoutManager(mLayoutManager);
        pairsListView.setItemAnimator(new DefaultItemAnimator());
        pairsListView.setAdapter(adapter);
        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
                new EditProfile.RemovePairListener(getApplicationContext(), adapter.getMessageById(viewHolder.getAdapterPosition()));
            }
        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(pairsListView);
    }

    private class ListPairsListener extends LocmessListener implements ActivityCallback {

        protected ListPairsListener(Context context) {
            super(context);
            new ListPairsService(context, this).execute();
        }

        @Override
        public void onSuccess(Result result) {
            List<Pair> profilePairs = (List<Pair>) result.getPiggyback();
            pairs.clear();
            for (Pair pair : profilePairs)
                adapter.addPair(pair);
            if (dialog != null) dialog.cancel();
            Toast.makeText(getContext(), result.getMessage(), Toast.LENGTH_LONG).show();
        }

        @Override
        public void onFailure(Result result) {
            if (dialog != null) dialog.cancel();
            Toast.makeText(getContext(), result.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private class AddPairListener extends LocmessListener implements ActivityCallback {

        private Pair pair;

        protected AddPairListener(Context context, Pair pair) {
            super(context);
            this.pair = pair;
            new AddPairService(context, this, pair).execute();
        }

        @Override
        public void onSuccess(Result result) {
            adapter.addPair(pair);
            Toast.makeText(getContext(), result.getMessage(), Toast.LENGTH_LONG).show();
        }

        @Override
        public void onFailure(Result result) {
            Toast.makeText(getContext(), result.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private class RemovePairListener extends LocmessListener implements ActivityCallback {

        private Pair pair;

        protected RemovePairListener(Context context, Pair pair) {
            super(context);
            this.pair = pair;
            new RemovePairService(context, this, pair).execute();
        }

        @Override
        public void onSuccess(Result result) {
            adapter.removePair(pair);
            Toast.makeText(getContext(), result.getMessage(), Toast.LENGTH_LONG).show();
        }

        @Override
        public void onFailure(Result result) {
            Toast.makeText(getContext(), result.getMessage(), Toast.LENGTH_LONG).show();
        }
    }


}
