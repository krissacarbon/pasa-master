package com.pasabuy.pasabuy;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v7.widget.AppCompatButton;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import butterknife.Bind;

public class AddJourney extends Fragment {

    private static String date;
    private static String city;
    private static String country;
    private ProgressDialog mProgressDialog;
    private JSONObject mData;
    private JSONArray mLocations;
    private ArrayList<String> aCountry = new ArrayList<String>();
    private ArrayList<String> aCity = new ArrayList<String>();


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
        final Spinner lscountry = (Spinner) rootView.findViewById(R.id.SpinnerFeedbackTypeCountry);
        final Spinner lscity = (Spinner) rootView.findViewById(R.id.SpinnerFeedbackTypeCity);

        //get location input
        //city = lscity.getSelectedItem().toString();
        //country = lscountry.getSelectedItem().toString();

        lscountry.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                country = lscountry.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        lscity.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                city = lscity.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //Display the DatePicker initial date
        String twoDigitMonth = String.format("%02d", month);
        tv.setText(year + "-" + twoDigitMonth + "-" + dayOfMonth);
        date = year + "-" + twoDigitMonth + "-" + dayOfMonth;

        //init(int year, int monthOfYear, int dayOfMonth, DatePicker.OnDateChangedListener onDateChangedListener) Initialize the state.
        dp.init(
                year,
                month-1,
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

    @Override
    public void onResume(){
        super.onResume();
        new SearchTask().execute();
    }

    private class SearchTask extends AsyncTask<Void, Void, Void>
    {
        List<Pair<String,String>> search_params = new ArrayList<>();

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //this method will be running on UI thread

            if (null == AddJourney.this.mProgressDialog) {
                AddJourney.this.mProgressDialog = new ProgressDialog(AddJourney.this.getActivity());
            }

            if(!(AddJourney.this.mProgressDialog.isShowing())) {
                AddJourney.this.mProgressDialog.setMessage("\tLoading...");
                AddJourney.this.mProgressDialog.setCancelable(false);
                AddJourney.this.mProgressDialog.show();
            }
        }
        @Override
        protected Void doInBackground(Void... params) {

            try {
                AddJourney.this.mData = Utility.search_api(this.search_params);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            if(null != AddJourney.this.mData){

            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

            try {

                if (AddJourney.this.mData.has("locations")) {
                    AddJourney.this.mLocations = AddJourney.this.mData.getJSONArray("locations");
                    //AddJourney.this.aCity.add("Anywhere");
                    for ( int i = 0; i < AddJourney.this.mLocations.length() ; i++ ) {
                        //String locString = (AddJourney.this.mLocations.getJSONObject(i).getString("city")+" " +SearchView.this.mLocations.getJSONObject(i).getString("country")).trim();
                        if(!AddJourney.this.aCity.contains(AddJourney.this.mLocations.getJSONObject(i).getString("city").trim())) {
                            AddJourney.this.aCity.add(AddJourney.this.mLocations.getJSONObject(i).getString("city").trim());
                        }
                        if (!AddJourney.this.aCountry.contains(AddJourney.this.mLocations.getJSONObject(i).getString("country").trim())) {
                            AddJourney.this.aCountry.add(AddJourney.this.mLocations.getJSONObject(i).getString("country").trim());
                        }
                    }
                }
                final Spinner sLoc = (Spinner) AddJourney.this.getActivity().findViewById(R.id.SpinnerFeedbackTypeCity);
                ArrayAdapter<String> adapterLoc = new ArrayAdapter<String>(AddJourney.this.getActivity(),
                        android.R.layout.simple_spinner_item, AddJourney.this.aCity);
                sLoc.setAdapter(adapterLoc);

                final Spinner sCat = (Spinner) AddJourney.this.getActivity().findViewById(R.id.SpinnerFeedbackTypeCountry);
                ArrayAdapter<String> adapterCat = new ArrayAdapter<String>(AddJourney.this.getActivity(),
                        android.R.layout.simple_spinner_item, AddJourney.this.aCountry);
                sCat.setAdapter(adapterCat);

            } catch (JSONException e) {
                Log.e("ERROR",e.getMessage());
            }
            if ( AddJourney.this.mProgressDialog.isShowing() ) {
                AddJourney.this.mProgressDialog.dismiss();
            }
            try {
                Log.d("DATA", AddJourney.this.mData.toString());
            } catch (Exception e) {

            }

        }
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
                Toast.makeText(getActivity().getApplicationContext(), "Failed to add journey!", Toast.LENGTH_LONG).show();
            }
            progressDialog.dismiss();
        }


    }
}
