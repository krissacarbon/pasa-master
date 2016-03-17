package com.pasabuy.pasabuy;

import android.app.Activity;
import android.app.ProgressDialog;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v7.widget.AppCompatButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;

import java.util.Calendar;

import butterknife.Bind;

public class AddJourney extends Fragment {

    private static String date;
    private static String city;
    private static String country;


    public AddJourney() {

    }

    @Override
    public void onStart() {
        super.onStart();
        this.getActivity().setTitle("Add Journey");
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_add_journey, container, false);


        //Get a new instance of Calendar
        Calendar c = Calendar.getInstance();
        int year = c.get(c.YEAR);
        int month = c.get(c.MONTH) + 1;
        int dayOfMonth = c.get(c.DAY_OF_MONTH);

        //Get the widgets reference from XML layout
        final TextView tv = (TextView) rootView.findViewById(R.id.return_date);
        DatePicker dp = (DatePicker) rootView.findViewById(R.id.dp);
        Spinner lscountry = (Spinner) rootView.findViewById(R.id.SpinnerFeedbackTypeCountry);
        Spinner lscity = (Spinner) rootView.findViewById(R.id.SpinnerFeedbackTypeCity);

        //get location input
        city = lscity.getSelectedItem().toString();
        country = lscountry.getSelectedItem().toString();

        //Display the DatePicker initial date
        String twoDigitMonth = String.format("%02d", month);
        tv.setText(year + "-" + twoDigitMonth + "-" + dayOfMonth);
        date = year + "-" + twoDigitMonth + "-" + dayOfMonth;

        //init(int year, int monthOfYear, int dayOfMonth, DatePicker.OnDateChangedListener onDateChangedListener) Initialize the state.
        dp.init(
                year,
                month,
                dayOfMonth,
                new DatePicker.OnDateChangedListener() {

                    @Override
                    public void onDateChanged(
                            DatePicker view,
                            int year,
                            int monthOfYear,
                            int dayOfMonth) {
                        monthOfYear++;
                        String twoDigitMonth = String.format("%02d", monthOfYear);
                        tv.setText(year + "-" + twoDigitMonth + "-" + dayOfMonth);
                        date = year + "-" + twoDigitMonth + "-" + dayOfMonth;
                    }
                });

        AppCompatButton addJourneyBtn = (AppCompatButton) rootView.findViewById(R.id.btn_addjourney);

        addJourneyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addJourney();
            }
        });

        return rootView;
    }

    public  void addJourney(){
        new addJourneyTask().execute();
    }

    private class addJourneyTask extends AsyncTask<String, Void, Boolean> {
        final ProgressDialog progressDialog = new ProgressDialog(getActivity(),R.style.AppTheme_Dark_Dialog);

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog.setIndeterminate(true);
            progressDialog.setMessage("Adding Journey...");
            progressDialog.show();

        }

        @Override
        protected Boolean doInBackground(String... params) {
            boolean add_journey = false;
            try {
                add_journey = Utility.add_journey(city, country, date);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return add_journey;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            if(result) {
                Toast.makeText(getActivity().getApplicationContext(), "Journey successfully added!", Toast.LENGTH_LONG).show();
            } else {
            }
            progressDialog.dismiss();
        }


    }
}
