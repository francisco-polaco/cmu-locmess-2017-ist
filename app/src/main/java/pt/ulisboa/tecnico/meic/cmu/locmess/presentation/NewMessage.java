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
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.thomashaertel.widget.MultiSpinner;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import pt.ulisboa.tecnico.meic.cmu.locmess.R;
import pt.ulisboa.tecnico.meic.cmu.locmess.domain.God;
import pt.ulisboa.tecnico.meic.cmu.locmess.dto.Location;
import pt.ulisboa.tecnico.meic.cmu.locmess.dto.Message;
import pt.ulisboa.tecnico.meic.cmu.locmess.dto.Pair;
import pt.ulisboa.tecnico.meic.cmu.locmess.dto.Result;
import pt.ulisboa.tecnico.meic.cmu.locmess.interfaces.ActivityCallback;
import pt.ulisboa.tecnico.meic.cmu.locmess.service.ListAllProfilePairsService;
import pt.ulisboa.tecnico.meic.cmu.locmess.service.ListLocationsService;
import pt.ulisboa.tecnico.meic.cmu.locmess.service.PostMessageService;

/**
 * Created by jp_s on 4/15/2017.
 */

public class NewMessage extends AppCompatActivity implements ActivityCallback {

    private ProgressDialog dialog;
    private Toolbar toolbar;

    private Spinner spinner;

    private ArrayAdapter spinnerKeys;
    private ArrayAdapter spinnerLocations;
    private MultiSpinner multispinner;
    private List<Pair> keysInPairs;
    private List<String> keys = new ArrayList<>();
    private List<String> locations = new ArrayList<>();

    private MultiSpinner.MultiSpinnerListener onSelectedListener = new MultiSpinner.MultiSpinnerListener() {
        public void onItemsSelected(boolean[] selected) {
            // Do something here with the selected items
            String s = "";
            for (int i = 0; i < selected.length; i++)
                if (selected[i])
                    s += " " + spinnerKeys.getItem(i).toString() + "\n";

            multispinner = (MultiSpinner) findViewById(R.id.spinnerMulti);
            multispinner.setText(s.equals("") ? getText(R.string.multispinner_placeholder) : s);
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.newmessage);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        dialog = WidgetConstructors.getLoadingDialog(this, getString(R.string.dialog_retrieve_profile));
        dialog.show();
    }

    @Override
    protected void onStart() {
        super.onStart();

        // the order here is important, since I am relying on it to close the dialog!
        spinner = (Spinner) findViewById(R.id.spinner);
        new LocationsListener(this).execute();

        multispinner = (MultiSpinner) findViewById(R.id.spinnerMulti);
        new PairsListener(this).execute();
    }

    public void showDatePickerDialog(View v) {
        Bundle bundle = new Bundle();
        bundle.putInt("type", v.getId() == R.id.EndDate ? 1 : 0);
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.setArguments(bundle);
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }

    public void showTimePickerDialog(View v) {
        Bundle bundle = new Bundle();
        bundle.putInt("type", v.getId() == R.id.EndTime ? 1 : 0);
        DialogFragment newFragment = new TimePickerFragment();
        newFragment.setArguments(bundle);
        newFragment.show(getSupportFragmentManager(), "timePicker");
    }

    public void sendMessage(View view) {
        String title = ((EditText) this.findViewById(R.id.msgtitle)).getText().toString();
        if (title.equals("")) {
            Toast.makeText(this, "You need a title!", Toast.LENGTH_SHORT).show();
            return;
        }
        String content = ((EditText) this.findViewById(R.id.content)).getText().toString();
        if (content.equals("")) {
            Toast.makeText(this, "A message has to have content!", Toast.LENGTH_SHORT).show();
            return;
        }
        Location location = God.getInstance().getLocations().get(spinner.getSelectedItemPosition());
        if (location == null) {
            Toast.makeText(this, "You must add locations first!", Toast.LENGTH_SHORT).show();
            return;
        }

        String beginTime = ((TextView) this.findViewById(R.id.BeginTime)).getText().toString();
        String beginDate = ((TextView) this.findViewById(R.id.BeginDate)).getText().toString();
        if (beginDate.equals("") || beginTime.equals("")) {
            Toast.makeText(this, "You must fill the begin date!", Toast.LENGTH_SHORT).show();
            return;
        }

        String endTime = ((TextView) this.findViewById(R.id.EndTime)).getText().toString();
        String endDate = ((TextView) this.findViewById(R.id.EndDate)).getText().toString();
        if (endDate.equals("") || endTime.equals("")) {
            Toast.makeText(this, "You must fill the expiration date!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (keysInPairs == null)
            keysInPairs = new ArrayList<>();

        RadioGroup group = ((RadioGroup) this.findViewById(R.id.radio));
        int id = group.getCheckedRadioButtonId();
        View radioButton = this.findViewById(id);
        int radioId = group.indexOfChild(radioButton);
        RadioButton btn = (RadioButton) group.getChildAt(radioId);
        String policy = (String) btn.getText();

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        String bDate = simpleDateFormat.format(new Date(beginTime + " " + beginDate));
        String eDate = simpleDateFormat.format(new Date(endTime + " " + endDate));


        Message message = new Message(title, location, policy, keysInPairs, bDate, eDate, content);
        new PostMessageService(getApplicationContext(), this, message).execute();
    }

    @Override
    public void onSuccess(Result result) {
        if (dialog != null) dialog.cancel();
        String message = result.getMessage();
        if (message != null) {
            if (message.equals(getString(R.string.LM_2))) {
                message = "Message was posted with success!";
                //TODO : maybe there is a better way
                reset();
            }
            Toast.makeText(this, message, Toast.LENGTH_LONG).show();
        }
    }

    private void reset() {
        ((EditText) this.findViewById(R.id.msgtitle)).setText("");
        ((EditText) this.findViewById(R.id.content)).setText("");
        ((TextView) this.findViewById(R.id.BeginTime)).setText(getString(R.string.select_time));
        ((TextView) this.findViewById(R.id.BeginDate)).setText(getString(R.string.select_date));
        ((TextView) this.findViewById(R.id.EndTime)).setText(getString(R.string.select_time));
        ((TextView) this.findViewById(R.id.EndDate)).setText(getString(R.string.select_date));
        multispinner.setText(getText(R.string.multispinner_placeholder));
    }

    @Override
    public void onFailure(Result result) {
        if (dialog != null) dialog.cancel();
        Toast.makeText(this, result.getMessage(), Toast.LENGTH_LONG).show();
    }

    public static class TimePickerFragment extends DialogFragment
            implements TimePickerDialog.OnTimeSetListener {

        // 1 represents end date
        // 0 represents begin date
        private int type;

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {

            this.type = getArguments().getInt("type");
            // Use the current time as the default values for the picker
            final Calendar c = Calendar.getInstance();
            int hour = c.get(Calendar.HOUR_OF_DAY);
            int minute = c.get(Calendar.MINUTE);

            // Create a new instance of TimePickerDialog and return it
            return new TimePickerDialog(getActivity(), this, hour, minute,
                    DateFormat.is24HourFormat(getActivity()));
        }

        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            TextView textView = type == 1 ? (TextView) getActivity().findViewById(R.id.EndTime) :
                    (TextView) getActivity().findViewById(R.id.BeginTime);
            textView.setText(hourOfDay + ":" + minute);
        }
    }

    public static class DatePickerFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {

        // 1 represents end date
        // 0 represents begin date
        private int type;

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {

            this.type = getArguments().getInt("type");
            // Use the current date as the default date in the picker
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            // Create a new instance of DatePickerDialog and return it
            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {
            TextView textView = type == 1 ? (TextView) getActivity().findViewById(R.id.EndDate) :
                    (TextView) getActivity().findViewById(R.id.BeginDate);
            textView.setText(day + "/" + month + "/" + year);
        }
    }

    public class Listener {

        private Context context;

        public Listener(Context context) {
            this.context = context;
        }

        public Context getContext() {
            return context;
        }
    }

    public class LocationsListener extends Listener implements ActivityCallback {

        public LocationsListener(Context context) {
            super(context);
        }

        public void execute() {
            new ListLocationsService(getContext(), this).execute();
        }

        @Override
        public void onSuccess(Result result) {
            setLocations(God.getInstance().getLocations());
        }

        @Override
        public void onFailure(Result result) {
            NewMessage.this.onFailure(new Result("Failed to retrieve the list of locations!"));
        }

        private void setLocations(List<Location> locs) {
            if (locs == null)
                return;

            locations.clear();
            for (Location l : locs)
                locations.add(l.toString());

            if (spinnerLocations == null) {
                spinnerLocations = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, locations);
                spinnerLocations.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinner.setAdapter(spinnerLocations);
            } else
                spinnerLocations.notifyDataSetChanged();
        }
    }


    public class PairsListener extends Listener implements ActivityCallback {

        public PairsListener(Context context) {
            super(context);
        }

        public void execute() {
            new ListAllProfilePairsService(getContext(), this).execute();
        }

        @Override
        public void onSuccess(Result result) {
            setPairs((List<Pair>) result.getPiggyback());
            NewMessage.this.onSuccess(new Result());
        }

        @Override
        public void onFailure(Result result) {
            NewMessage.this.onFailure(new Result("Failed to retrieve the list of pairs!"));
        }

        private void setPairs(List<Pair> pairs) {
            if (pairs == null)
                return;

            keysInPairs = pairs;

            keys.clear();
            for (Pair p : pairs)
                keys.add(p.toString() + "");

            if (spinnerKeys == null) {
                spinnerKeys = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, keys);
                spinnerKeys.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                multispinner.setAdapter(spinnerKeys, false, onSelectedListener);
                multispinner.setText(getContext().getText(R.string.multispinner_placeholder));
            } else
                spinnerKeys.notifyDataSetChanged();
        }
    }


}
