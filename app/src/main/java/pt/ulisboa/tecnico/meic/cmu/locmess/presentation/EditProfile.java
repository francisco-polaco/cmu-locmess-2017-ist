package pt.ulisboa.tecnico.meic.cmu.locmess.presentation;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import pt.ulisboa.tecnico.meic.cmu.locmess.R;
import pt.ulisboa.tecnico.meic.cmu.locmess.domain.God;
import pt.ulisboa.tecnico.meic.cmu.locmess.dto.Pair;
import pt.ulisboa.tecnico.meic.cmu.locmess.dto.Result;
import pt.ulisboa.tecnico.meic.cmu.locmess.interfaces.ActivityCallback;
import pt.ulisboa.tecnico.meic.cmu.locmess.interfaces.LocmessListener;
import pt.ulisboa.tecnico.meic.cmu.locmess.service.AddPairService;
import pt.ulisboa.tecnico.meic.cmu.locmess.service.ListPairsService;
import pt.ulisboa.tecnico.meic.cmu.locmess.service.RemovePairService;


public class EditProfile extends AppCompatActivity implements ActivityCallback {

    private SimpleAdapter adapter;
    private Toolbar toolbar;
    private List<HashMap<String, String>> itemlist;
    private ProgressDialog dialog;
    private Pair toAdd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.editprofile);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Edit Profile");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        new ListPairsService(getApplicationContext(), this).execute();
        dialog = WidgetConstructors.getLoadingDialog(this, getString(R.string.dialog_retrieve_profile));
        dialog.show();
    }

    public void DisplayKeyValues(View view) {
        EditText value = (EditText) findViewById(R.id.Value);
        EditText key = (EditText) findViewById(R.id.Key);

        toAdd = new Pair(key.getText().toString(), value.getText().toString());
        new AddPairService(getApplicationContext(), this, toAdd).execute();
    }

    @Override
    public void onSuccess(Result result) {
        String toastText = "";
        if (result.getMessage().equals(getApplicationContext().getString(R.string.LM_0))) {
            itemlist = parsePairs(God.getInstance().getProfile());
            adapter = new SimpleAdapter(this, itemlist, R.layout.listview,
                    new String[]{"Key", "Value"},
                    new int[]{R.id.textView, R.id.textView2});
            final ListView listValues = (ListView) findViewById(R.id.keyvalue);
            listValues.setAdapter(adapter);

            // in order to remove an item from the listview
            listValues.setClickable(true);
            listValues.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> parent, View view, int arg2, long arg3) {
                    HashMap<String, String> keyValue = (HashMap<String, String>) parent.getItemAtPosition(arg2);
                    Pair toRemove = new Pair(keyValue.get("Key"), keyValue.get("Value"));
                    new RemovePairService(getApplicationContext(), EditProfile.this, toRemove, arg2).execute();
                    return true;
                }
            });
            if (dialog != null) dialog.cancel();
            return; // avoid Toast
        } else if (result.getMessage().equals(getApplicationContext().getString(R.string.LM_1))) {
            HashMap<String, String> toAdd = new HashMap<String, String>();
            toAdd.put("Key", this.toAdd.getKey());
            toAdd.put("Value", this.toAdd.getValue());
            itemlist.add(toAdd);
            ListView listValues = (ListView) findViewById(R.id.keyvalue);
            listValues.setAdapter(adapter);
            toastText += "Pair successfully added!";
        } else if (result.getMessage().equals(getApplicationContext().getString(R.string.LM_2))) {
            int index = (int) result.getPiggyback();
            HashMap<String, String> keyValue = (HashMap<String, String>) adapter.getItem(index);
            Pair toRemove = new Pair(keyValue.get("Key"), keyValue.get("Value"));
            God.getInstance().getProfile().remove(toRemove);
            itemlist.remove(keyValue);
            adapter.notifyDataSetChanged();
            toastText += "Pair successfully removed!";
        }
        Toast.makeText(getApplicationContext(), toastText, Toast.LENGTH_SHORT).show();
    }

    private List<HashMap<String, String>> parsePairs(List<Pair> profile) {
        List<HashMap<String, String>> newProfile = new ArrayList<>();
        for (Pair pair : profile) {
            HashMap<String, String> toAdd = new HashMap<>();
            toAdd.put("Key", pair.getKey());
            toAdd.put("Value", pair.getValue());
            newProfile.add(toAdd);
        }
        return newProfile;
    }

    @Override
    public void onFailure(Result result) {
        String toastText = "";
        if (result.getMessage().equals(getApplicationContext().getString(R.string.LM_0))) {
            if (dialog != null) dialog.cancel();
            toastText += "Failed to retrieve the profile!";
            finish();
        } else if (result.getMessage().equals(getApplicationContext().getString(R.string.LM_1)))
            toastText += "Failed to add pair!";
        else if (result.getMessage().equals(getApplicationContext().getString(R.string.LM_2)))
            toastText += "Failed to remove pair!";

        Toast.makeText(getApplicationContext(), toastText, Toast.LENGTH_SHORT).show();
    }


    private class ListPairsListener extends LocmessListener implements ActivityCallback {

        protected ListPairsListener(Context context) {
            super(context);
            new ListPairsService(context,this).execute();
        }

        @Override
        public void onSuccess(Result result) {
            itemlist = parsePairs(God.getInstance().getProfile());
            adapter = new SimpleAdapter(getContext(), itemlist, R.layout.listview,
                      new String[]{"Key", "Value"},
                      new int[]{R.id.textView, R.id.textView2});
            final ListView listValues = (ListView) findViewById(R.id.keyvalue);
            listValues.setAdapter(adapter);

            // in order to remove an item from the listview
            listValues.setClickable(true);
            listValues.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> parent, View view, int arg2, long arg3) {
                    HashMap<String, String> keyValue = (HashMap<String, String>) parent.getItemAtPosition(arg2);
                    Pair toRemove = new Pair(keyValue.get("Key"), keyValue.get("Value"));
                    new RemovePairService(getApplicationContext(), EditProfile.this, toRemove, arg2).execute();
                    return true;
                }
            });
            if (dialog != null) dialog.cancel();
        }

        @Override
        public void onFailure(Result result) {

        }
    }






}
