package pt.ulisboa.tecnico.meic.cmu.locmess.presentation;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
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
        toolbar.setTitle("Edit Profile");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        new ListPairsListener(getApplicationContext());
        dialog = WidgetConstructors.getLoadingDialog(this, getString(R.string.dialog_retrieve_profile));
        dialog.show();
    }

    @Override
    protected void onStart() {
        super.onStart();
        initRecyclerView();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        } else
            return super.onOptionsItemSelected(item);
    }


    public void addPair(View view) {
        EditText value = (EditText) findViewById(R.id.Value);
        EditText key = (EditText) findViewById(R.id.Key);

        new AddPairListener(getApplicationContext(), new Pair(key.getText().toString(), value.getText().toString()));
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
