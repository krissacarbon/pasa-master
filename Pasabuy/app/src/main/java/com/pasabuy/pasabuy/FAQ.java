package com.pasabuy.pasabuy;


import android.app.Fragment;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;

public class FAQ extends Fragment {

	public FAQ(){}
	private TextView mFAQ;
	
	@Override
	public void onStart() {
		super.onStart();
	}

	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
 
        View rootView = inflater.inflate(R.layout.fragment_faq, container, false);

//		this.mFAQ = (TextView) rootView.findViewById(R.id.faq_text);
//		this.mFAQ.setText(Html.fromHtml(this.getActivity().getResources().getString(R.string.FAQ)));
		return rootView;
    }


}
