package pt.ulisboa.tecnico.meic.cmu.locmess;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import com.thomashaertel.widget.MultiSpinner;

import java.util.Calendar;

/**
 * Created by jp_s on 4/15/2017.
 */

public class NewMessage extends AppCompatActivity {

    private Toolbar toolbar;
    private ArrayAdapter<CharSequence> spinnerKeys;
    private MultiSpinner multispinner;

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
        spinnerKeys = ArrayAdapter.createFromResource(this, R.array.keys, android.R.layout.simple_spinner_item);
        spinnerKeys.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        MultiSpinner multispinner = (MultiSpinner) findViewById(R.id.spinnerMulti);
        multispinner.setAdapter(spinnerKeys, false, onSelectedListener);

        multispinner.setText("Select Keys");
        //------------------------------------------------------------------------
    }

    private MultiSpinner.MultiSpinnerListener onSelectedListener = new MultiSpinner.MultiSpinnerListener() {
        public void onItemsSelected(boolean[] selected) {
            // Do something here with the selected items
            String s ="";
            for(int i = 0; i<selected.length ;i++)
                if (selected[i] == true)
                    s += " " + spinnerKeys.getItem(i).toString() + "\n";

            multispinner = (MultiSpinner) findViewById(R.id.spinnerMulti);
            multispinner.setText(s);
        }
    };


    public void showDatePickerDialog(View v) {
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }

    public void showTimePickerDialog(View v) {
        DialogFragment newFragment = new TimePickerFragment();
        newFragment.show(getSupportFragmentManager(), "timePicker");
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
            textView.setText("Hour: "+view.getCurrentHour()+" Minute: "+view.getCurrentMinute());
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
            textView.setText("Month: "+view.getMonth()+" Day: "+view.getDayOfMonth());
        }
    }

}
