package pt.ulisboa.tecnico.meic.cmu.locmess.presentation;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import pt.ulisboa.tecnico.meic.cmu.locmess.R;

import static pt.ulisboa.tecnico.meic.cmu.locmess.R.layout.register;

/**
 * Created by jp_s on 4/12/2017.
 */

public class Register extends AppCompatActivity {

    private Toolbar toolbar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(register);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }
}
