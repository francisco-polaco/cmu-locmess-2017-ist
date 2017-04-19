package pt.ulisboa.tecnico.meic.cmu.locmess;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by jp_s on 4/18/2017.
 */

public class EditProfile extends AppCompatActivity {

    private SimpleAdapter adapter;
    private HashMap<String, String> KeyValues;
    private Toolbar toolbar;
    private List<HashMap<String, String>> itemlist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.editprofile);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Edit Profile");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        KeyValues = new HashMap<String, String>();
        itemlist = new ArrayList<>();
        adapter = new SimpleAdapter(this,itemlist, R.layout.listview,
                new String[]{"First Line","Second Line"},
                new int[]{R.id.textView, R.id.textView2} );
        ListView listValues = (ListView) findViewById(R.id.keyvalue);
        listValues.setAdapter(adapter);
    }

    public void DisplayKeyValues(View view){
        ListView listValues = (ListView) findViewById(R.id.keyvalue);
        EditText value = (EditText) findViewById(R.id.Value);
        EditText key = (EditText) findViewById(R.id.Key);
        KeyValues.put(key.getText().toString(),value.getText().toString());

        Iterator it = KeyValues.entrySet().iterator();
        while(it.hasNext()){
            HashMap<String, String> resultmap = new HashMap<String, String>();
            Map.Entry pair = (Map.Entry) it.next();
            resultmap.put("First Line",pair.getKey().toString());
            resultmap.put("Second Line", pair.getValue().toString());
            itemlist.add(resultmap);
        }
        listValues.setAdapter(adapter);
    }
}
