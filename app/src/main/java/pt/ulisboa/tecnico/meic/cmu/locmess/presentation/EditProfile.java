package pt.ulisboa.tecnico.meic.cmu.locmess.presentation;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import pt.ulisboa.tecnico.meic.cmu.locmess.R;
import pt.ulisboa.tecnico.meic.cmu.locmess.domain.God;
import pt.ulisboa.tecnico.meic.cmu.locmess.dto.Message;
import pt.ulisboa.tecnico.meic.cmu.locmess.dto.Pair;
import pt.ulisboa.tecnico.meic.cmu.locmess.interfaces.ActivityCallback;
import pt.ulisboa.tecnico.meic.cmu.locmess.service.AddPairService;
import pt.ulisboa.tecnico.meic.cmu.locmess.service.ListPairsService;


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
    public void onSuccess(Message result) {
        if(result.getMessage().equals(getApplicationContext().getString(R.string.webserver_pair_list))){
            itemlist = parsePairs(God.getInstance().getProfile());
            adapter = new SimpleAdapter(this, itemlist, R.layout.listview,
                    new String[]{"Key", "Value"},
                    new int[]{R.id.textView, R.id.textView2});
            ListView listValues = (ListView) findViewById(R.id.keyvalue);
            listValues.setAdapter(adapter);
            if(dialog != null) dialog.cancel();
        }
        else if(result.getMessage().equals(getApplicationContext().getString(R.string.webserver_pair_create))) {
            HashMap<String, String> resultmap = new HashMap<String, String>();
            resultmap.put("Key", toAdd.getKey());
            resultmap.put("Value", toAdd.getValue());
            itemlist.add(resultmap);
            ListView listValues = (ListView) findViewById(R.id.keyvalue);
            listValues.setAdapter(adapter);
        }
        else
            Toast.makeText(getApplicationContext(), result.getMessage(), Toast.LENGTH_SHORT).show();
    }

    private List<HashMap<String,String>> parsePairs(List<Pair> profile) {
        List<HashMap<String, String>> newProfile = new ArrayList<>();
        for (Pair pair : profile){
            HashMap<String, String> toAdd = new HashMap<>();
            toAdd.put("Key", pair.getKey());
            toAdd.put("Value", pair.getValue());
            newProfile.add(toAdd);
        }
        return newProfile;
    }

    @Override
    public void onFailure(Message result) {
        if(result.getMessage().equals(getApplicationContext().getString(R.string.webserver_pair_list))) {
            if(dialog != null) dialog.cancel();
            Toast.makeText(getApplicationContext(), "Failed to retrieve the profile!", Toast.LENGTH_LONG).show();
            finish();
        }
        else if(result.getMessage().equals(getApplicationContext().getString(R.string.webserver_pair_create)))
            Toast.makeText(getApplicationContext(), "Failed to add pair to profile!", Toast.LENGTH_LONG).show();
        else
            Toast.makeText(getApplicationContext(), result.getMessage(), Toast.LENGTH_SHORT).show();
    }
}
