package pt.ulisboa.tecnico.meic.cmu.locmess.presentation;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.format.DateFormat;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.thomashaertel.widget.MultiSpinner;

import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Random;

import pt.ulisboa.tecnico.meic.cmu.locmess.R;
import pt.ulisboa.tecnico.meic.cmu.locmess.domain.God;
import pt.ulisboa.tecnico.meic.cmu.locmess.dto.Message;
import pt.ulisboa.tecnico.meic.cmu.locmess.dto.Pair;
import pt.ulisboa.tecnico.meic.cmu.locmess.interfaces.ActivityCallback;
import pt.ulisboa.tecnico.meic.cmu.locmess.interfaces.LocmessCallback;
import pt.ulisboa.tecnico.meic.cmu.locmess.service.ListAllProfilePairsService;

/**
 * Created by jp_s on 4/15/2017.
 */

public class NewMessage extends AppCompatActivity implements ActivityCallback {

    private ProgressDialog dialog;
    private Toolbar toolbar;
    private ArrayAdapter spinnerKeys;
    private MultiSpinner multispinner;
    private MultiSpinner.MultiSpinnerListener onSelectedListener = new MultiSpinner.MultiSpinnerListener() {
        public void onItemsSelected(boolean[] selected) {
            // Do something here with the selected items
            String s = "";
            for (int i = 0; i < selected.length; i++)
                if (selected[i] == true)
                    s += " " + spinnerKeys.getItem(i).toString() + "\n";

            multispinner = (MultiSpinner) findViewById(R.id.spinnerMulti);
            multispinner.setText(s);
        }
    };
    private List<String> keys = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.newmessage);

        //-----------------------Toolbar-------------------------
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //-------------------------------------------------------

        //--------------------------Spinner------------------------
        Spinner spinner = (Spinner) findViewById(R.id.spinner);
        ArrayAdapter<CharSequence> spinnerLocations = ArrayAdapter.createFromResource(this, R.array.locations, android.R.layout.simple_spinner_item);
        spinnerLocations.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(spinnerLocations);
        //-----------------------------------------------------------


        //--------------------------MultiSpinner--------------------------------
        multispinner = (MultiSpinner) findViewById(R.id.spinnerMulti);
        multispinner.setOnTouchListener(new PairsListener(this, this));

        //------------------------------------------------------------------------

    }

    public void showDatePickerDialog(View v) {
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }

    public void showTimePickerDialog(View v) {
        DialogFragment newFragment = new TimePickerFragment();
        newFragment.show(getSupportFragmentManager(), "timePicker");
    }

    public void setSpinnerKeys(ArrayAdapter spinnerKeys) {
        this.spinnerKeys = spinnerKeys;
    }

    public static class TimePickerFragment extends DialogFragment
            implements TimePickerDialog.OnTimeSetListener {


        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current time as the default values for the picker
            final Calendar c = Calendar.getInstance();
            int hour = c.get(Calendar.HOUR_OF_DAY);
            int minute = c.get(Calendar.MINUTE);

            // Create a new instance of TimePickerDialog and return it
            return new TimePickerDialog(getActivity(), this, hour, minute,
                    DateFormat.is24HourFormat(getActivity()));
        }

        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            TextView textView = (TextView) getActivity().findViewById(R.id.Time);
            textView.setText("Hour: " + view.getCurrentHour() + " Minute: " + view.getCurrentMinute());
        }
    }

    public static class DatePickerFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            // Create a new instance of DatePickerDialog and return it
            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {
            TextView textView = (TextView) getActivity().findViewById(R.id.Date);
            textView.setText("Month: " + view.getMonth() + " Day: " + view.getDayOfMonth());
        }
    }


    @Override
    public void onSuccess(Message result) {

    }

    @Override
    public void onFailure(Message result) {

    }

    public class PairsListener implements View.OnTouchListener, ActivityCallback {

        private Context context;
        private NewMessage newMessage;

        public PairsListener(Context context, NewMessage newMessage){
            this.context = context;
            this.newMessage = newMessage;
        }

        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            if(motionEvent.getAction()  == MotionEvent.ACTION_DOWN){
                dialog = WidgetConstructors.getLoadingDialog(context, getString(R.string.dialog_retrieve_profile));
                dialog.show();
                new ListAllProfilePairsService(context, this).execute();
            }
            return false;
        }

        @Override
        public void onSuccess(Message result) {
            List<Pair> piggyback = (List<Pair>) result.getPiggyback();
            setPairs(piggyback);
        }

        @Override
        public void onFailure(Message result) {
            // if no connection use my own profile
            setPairs(God.getInstance().getProfile());
            Toast.makeText(context, "Using the profile keypairs!", Toast.LENGTH_LONG).show();
        }

        public void setPairs(List<Pair> pairs) {
            if(pairs == null)
                return;

            newMessage.keys.clear();
            for(Pair p : pairs)
                newMessage.keys.add(p.toString() + "");

            if(newMessage.spinnerKeys == null){
                newMessage.spinnerKeys = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, newMessage.keys);
                newMessage.spinnerKeys.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                newMessage.multispinner.setAdapter(newMessage.spinnerKeys, false, newMessage.onSelectedListener);
                multispinner.setText("Select Keys");
            }
            else
                 newMessage.spinnerKeys.notifyDataSetChanged();

            if(dialog != null) dialog.cancel();
        }
    }



}
