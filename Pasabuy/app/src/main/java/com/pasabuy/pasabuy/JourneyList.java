package com.pasabuy.pasabuy;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class JourneyList extends Fragment {

	public JourneyList(){}
	public ArrayList<JourneyObject> mJourneys = new ArrayList<JourneyObject>();

    private ListView mJourneyList;
    private JourneyAdapter mAdapter;
    private ProgressDialog mProgressDialog;

    private JSONObject json;
    private boolean mResult;

    private final SimpleDateFormat df = new SimpleDateFormat("dd MMM yyyy");
	//return the current View
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {

        LayoutInflater factory = LayoutInflater.from(getActivity());
        //View headerView = factory.inflate(R.layout.journey_list_item,null);
        View rootView = inflater.inflate(R.layout.fragment_journey, container, false);
        this.mJourneyList = (ListView) rootView.findViewById(R.id.journey_list);
        //mOrderList.addHeaderView(headerView);
        //this.mAdapter = new JourneyAdapter(getActivity().getBaseContext(),R.layout.journey_list_item, this.mJourneys);
        //this.mJourneyList.setAdapter(this.mAdapter);
        //this.mAdapter.notifyDataSetChanged();

        return rootView;
    }


    public void onBackButtonPressed(){

    }

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) this.getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null;
    }

    @Override
    public void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        if(this.isNetworkConnected()) {
            new getJourneyTask().execute();
        }
        else{
            Toast.makeText(getActivity().getApplicationContext(), "No internet connection!", Toast.LENGTH_LONG).show();
        }

    }

    public class JourneyAdapter extends ArrayAdapter<JourneyObject>{
        private LayoutInflater mInflater ;


        private class ViewHolder {

            private TextView locationTextView;
            private TextView returnTextView;
            private Button deleteButton;

        }

        public JourneyAdapter(Context context, int textViewResourceId, ArrayList<JourneyObject> items) {
            super(context, textViewResourceId, items);
        }

        public JourneyAdapter(Context context, JourneyObject[] values){
            super(context,-1,values);

        }

        public View getView(int position, View convertView, ViewGroup parent) {

            View vi = convertView;
            ViewHolder viewHolder;
            if (convertView == null) {
                convertView = LayoutInflater.from(this.getContext())
                        .inflate(R.layout.journey_list_item, parent, false);

                viewHolder = new ViewHolder();
                viewHolder.locationTextView = (TextView) convertView.findViewById(R.id.location_tv);
                viewHolder.returnTextView = (TextView) convertView.findViewById(R.id.return_date_tv);
                viewHolder.deleteButton = (Button) convertView.findViewById(R.id.del_btn);
                viewHolder.deleteButton.setBackgroundResource(R.drawable.delete_selector);

                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            JourneyObject item = getItem(position);

            viewHolder.locationTextView.setText(item.getCity()+", "+item.getCountry());
            viewHolder.returnTextView.setText(item.getReturnDate());
            viewHolder.deleteButton.setTag(item.getJourneyId());
            viewHolder.deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View v) {
                    final String jId = v.getTag().toString();
                    AlertDialog alertDialog = new AlertDialog.Builder(JourneyList.this.getActivity()).create(); //Read Update
                    alertDialog.setTitle("Are you sure?");
                    alertDialog.setMessage("Delete Journey");
                    alertDialog.setCancelable(false);

                    alertDialog.setButton(DialogInterface.BUTTON_POSITIVE,
                            "OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    new deleteJourneyTask().execute(jId);
                                }
                            });
                    alertDialog.setButton(DialogInterface.BUTTON_NEGATIVE,
                            "Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.cancel();
                                }
                            });

                    alertDialog.show();

                }
            });

            return convertView;
        }
    }

    private class getJourneyTask extends AsyncTask<Void, Void, Void>
    {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            JourneyList.this.mJourneys.clear();
            //this method will be running on UI thread
            if (null == JourneyList.this.mProgressDialog) {
                JourneyList.this.mProgressDialog = new ProgressDialog(getActivity());
            }

            if(!(JourneyList.this.mProgressDialog.isShowing())) {
                JourneyList.this.mProgressDialog.setMessage("\tLoading...");
                JourneyList.this.mProgressDialog.setCancelable(false);
                JourneyList.this.mProgressDialog.show();
            }
        }
        @Override
        protected Void doInBackground(Void... params) {

            //this method will be running on background thread so don't update UI frome here
            //do your long running http tasks here,you dont want to pass argument and u can access the parent class' variable url over here

            try {
                json = Utility.get_journey("17");
            } catch (JSONException e) {
                e.printStackTrace();
            }

            JSONArray spi = null;
            if(null!=json) {
                try {
                    spi = json.getJSONArray("journey_array");
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

                for (int i = 0; i < spi.length(); i++) {
                    try {
                        JSONObject c = spi.getJSONObject(i);
                        JSONObject location = c.getJSONObject("locationDescription");

                        long returnSeconds = Long.valueOf(c.getString("returnDate")).longValue();
                        Date date = new Date((returnSeconds));

                        String country = location.getString("country");
                        String city = location.getString("city");
                        String returnDate = JourneyList.this.df.format(date);
                        String journeyId = c.getString("id");

                        JourneyObject jo = new JourneyObject(country, city, returnDate, journeyId);

                        JourneyList.this.mJourneys.add(jo);
                    } catch (JSONException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }

                }
            }


            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

            //this method will be running on UI thread
            JourneyList.this.mJourneyList = (ListView) getView().findViewById(R.id.journey_list);
            JourneyList.this.mAdapter = new JourneyAdapter(getActivity(),R.layout.journey_list_item,JourneyList.this.mJourneys);
            JourneyList.this.mJourneyList.setAdapter(JourneyList.this.mAdapter);
            if ( JourneyList.this.mProgressDialog.isShowing() ) {
                JourneyList.this.mProgressDialog.dismiss();
            }
        }
    }

    private class deleteJourneyTask extends AsyncTask<String, Void, Void>
    {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            if (null == JourneyList.this.mProgressDialog) {
                JourneyList.this.mProgressDialog = new ProgressDialog(getActivity());
            }

            if(!(JourneyList.this.mProgressDialog.isShowing())) {
                JourneyList.this.mProgressDialog.setMessage("\tLoading...");
                JourneyList.this.mProgressDialog.show();
            }
        }
        @Override
        protected Void doInBackground(String... params) {

            //this method will be running on background thread so don't update UI frome here
            //do your long running http tasks here,you dont want to pass argument and u can access the parent class' variable url over here

            try {
                mResult = Utility.delete_journey(params[0]);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

            new getJourneyTask().execute();
            //this method will be running on UI thread
//            JourneyList.this.mJourneyList = (ListView) getView().findViewById(R.id.journey_list);
//            JourneyList.this.mAdapter = new JourneyAdapter(getActivity(),R.layout.journey_list_item,JourneyList.this.mJourneys);
//            JourneyList.this.mJourneyList.setAdapter(JourneyList.this.mAdapter);
            if ( JourneyList.this.mProgressDialog.isShowing() ) {
                JourneyList.this.mProgressDialog.dismiss();
            }
        }
    }

}