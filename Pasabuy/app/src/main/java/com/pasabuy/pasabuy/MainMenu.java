package com.pasabuy.pasabuy;


import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class MainMenu extends Fragment {

	public MainMenu(){}
	
	@Override
	public void onStart() {
		super.onStart();
		this.getActivity().setTitle("Main Menu");
		//getActivity().finish();
		

		// Launch Login Screen
		//Intent dashboard = new Intent(getActivity().getApplicationContext(), Login.class);
		//startActivity(dashboard);
	}

	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
 
        View rootView = inflater.inflate(R.layout.main_menu, container, false);
         
        return rootView;
    }
	
}
