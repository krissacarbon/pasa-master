package com.pasabuy.pasabuy;


import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Profile extends Fragment {

	public Profile(){}
	private JSONObject json;
	private TextView mUsername;
	private TextView mJoined;
	private TextView mEmail;
	private TextView mContact;
	private TextView mName;
	private TextView mBank;
	private TextView mAccountNumber;
	private TextView mShippingAddress;
	private final SimpleDateFormat df = new SimpleDateFormat("dd MMM yyyy");
	private ProgressDialog mProgressDialog;
	
	@Override
	public void onStart() {
		super.onStart();
	}

	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
 
        View rootView = inflater.inflate(R.layout.main_menu, container, false);
		this.mUsername = (TextView) rootView.findViewById(R.id.username_tv);
		this.mJoined = (TextView) rootView.findViewById(R.id.join_tv);
		this.mEmail = (TextView) rootView.findViewById(R.id.email_tv);
		this.mContact = (TextView) rootView.findViewById(R.id.contact_number_tv);
		this.mName = (TextView) rootView.findViewById(R.id.account_holder_tv);
		this.mBank = (TextView) rootView.findViewById(R.id.bank_tv);
		this.mAccountNumber = (TextView) rootView.findViewById(R.id.account_number_tv);
		this.mShippingAddress = (TextView) rootView.findViewById(R.id.shipping_address_tv);
		return rootView;
    }

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		if(isNetworkConnected()) {
			new getProfileTask().execute();
		} else {
			Toast.makeText(getActivity().getApplicationContext(), "No internet connection!", Toast.LENGTH_LONG).show();
		}
	}

	private boolean isNetworkConnected() {
		ConnectivityManager cm = (ConnectivityManager) this.getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);

		return cm.getActiveNetworkInfo() != null;
	}

	private class getProfileTask extends AsyncTask<Void, Void, Void>
	{
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			//this method will be running on UI thread
			if (null == Profile.this.mProgressDialog) {
				Profile.this.mProgressDialog = new ProgressDialog(getActivity());
			}

			if(!(Profile.this.mProgressDialog.isShowing())) {
				Profile.this.mProgressDialog.setMessage("\tLoading...");
				Profile.this.mProgressDialog.show();
			}
		}
		@Override
		protected Void doInBackground(Void... params) {

			//do your long running http tasks here,you dont want to pass argument and u can access the parent class' variable url over here
			try {
				Profile.this.json = Utility.get_user("17");
			} catch (JSONException e) {
				e.printStackTrace();
				Profile.this.json = new JSONObject();
			}

			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			//this method will be running on UI thread
			try {

				long returnSeconds = Long.valueOf(Profile.this.json.getString("createdDate")).longValue();
				Date date = new Date((returnSeconds));

				Profile.this.mUsername.setText(Profile.this.json.getString("name"));
				Profile.this.mJoined.setText(Profile.this.df.format(date));
				Profile.this.mEmail.setText(Profile.this.json.getString("email"));
				Profile.this.mContact.setText(Profile.this.json.getString("contactNumber"));
				Profile.this.mName.setText(Profile.this.json.getString("accountHolderName"));
				Profile.this.mBank.setText(Profile.this.json.getString("bankName"));
				Profile.this.mAccountNumber.setText(Profile.this.json.getString("accountNumber"));
				Profile.this.mShippingAddress.setText(Profile.this.json.getString("shippingAddress"));

			} catch (JSONException e) {

			}

			if ( Profile.this.mProgressDialog.isShowing() ) {
				Profile.this.mProgressDialog.dismiss();
			}
		}
	}
	
}
