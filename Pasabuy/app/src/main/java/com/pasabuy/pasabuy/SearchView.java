package com.pasabuy.pasabuy;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatButton;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsoluteLayout;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
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

public class SearchView extends Fragment {

//    private static String date;
//    private static String city;
//    private static String country;
    public ArrayList<ProductObject> mProducts = new ArrayList<ProductObject>();
    private SearchViewAdapter mSearchViewAdapter;
    private ListView mProductList;
    private List<Pair<String,String>> params;
    private SharedPreferences mSharedPrefs;
    private ProgressDialog mProgressDialog;
    private JSONObject mData;

    public SearchView() {

    }

    @Override
    public void onStart() {
        super.onStart();
        this.getActivity().setTitle("Search");
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(
            Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_main, menu);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_search, container, false);
        this.mSharedPrefs = this.getActivity().getPreferences(Context.MODE_PRIVATE);
        this.params = new ArrayList<>();

        return rootView;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar actions click
        switch (item.getItemId()) {
            case R.id.action_search:
                final Dialog dialog = new Dialog(SearchView.this.getActivity());
                dialog.setContentView(R.layout.search_dialog);
                dialog.setTitle("Search");

                Button dialogButton = (Button) dialog.findViewById(R.id.btn_submit_search);
                dialogButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        SearchView.this.params.add(new Pair<>("minimumPrice",((EditText) dialog.findViewById(R.id.min_price_edit)).getText().toString()));
                        SearchView.this.params.add(new Pair<>("maximumPrice", ((EditText) dialog.findViewById(R.id.min_price_edit)).getText().toString()));
                        new SearchTask(SearchView.this.params).execute();
                        dialog.dismiss();
                    }
                });
                dialog.show();
                dialog.getWindow().setLayout(AbsoluteLayout.LayoutParams.FILL_PARENT, AbsoluteLayout.LayoutParams.FILL_PARENT);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onResume(){
        super.onResume();
        new SearchTask(this.params).execute();
    }

    public void Search(){
        new SearchTask(this.params).execute();
    }


    public class SearchViewAdapter extends ArrayAdapter<ProductObject> {
        private LayoutInflater mInflater ;


        private class ViewHolder {

            private TextView nameTextView;
            private TextView tagTextView;
            private ImageView productIcon;
            private String productId;
            private String imageUrl;

        }

        public SearchViewAdapter(Context context, int textViewResourceId, ArrayList<ProductObject> items) {
            super(context, textViewResourceId, items);
        }

        public SearchViewAdapter(Context context, ProductObject[] values){
            super(context,-1,values);

        }

        public View getView(int position, View convertView, ViewGroup parent) {

            View vi = convertView;
            ViewHolder viewHolder;
            if (convertView == null) {
                convertView = LayoutInflater.from(this.getContext())
                        .inflate(R.layout.product_list_item, parent, false);

                viewHolder = new ViewHolder();
                viewHolder.nameTextView = (TextView) convertView.findViewById(R.id.item_name_tv);
                viewHolder.tagTextView = (TextView) convertView.findViewById(R.id.tag_tv);
                viewHolder.productIcon = (ImageView) convertView.findViewById(R.id.icon);
                //((MainActivity)SearchView.this.getActivity()).DownloadImageTask().
                viewHolder.productIcon.setBackgroundResource(R.drawable.delete_selector);

                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            ProductObject item = getItem(position);

            viewHolder.nameTextView.setText(item.getName());
            viewHolder.tagTextView.setText(item.getTag());

            return convertView;
        }
    }

    private class SearchTask extends AsyncTask<Void, Void, Void>
    {
        List<Pair<String,String>> search_params = new ArrayList<>();

        public SearchTask(List<Pair<String,String>> params) {
            this.search_params = params;
        }

        public SearchTask() {

        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //this method will be running on UI thread
            SearchView.this.mProducts.clear();

            if (null == SearchView.this.mProgressDialog) {
                SearchView.this.mProgressDialog = new ProgressDialog(SearchView.this.getActivity());
            }

            if(!(SearchView.this.mProgressDialog.isShowing())) {
                SearchView.this.mProgressDialog.setMessage("\tLoading...");
                SearchView.this.mProgressDialog.show();
            }
        }
        @Override
        protected Void doInBackground(Void... params) {

            JSONArray productList = new JSONArray();
            JSONArray productLiked = new JSONArray();
            try {
                SearchView.this.mData = Utility.search_api(this.search_params);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            if(null != SearchView.this.mData){
                try {
                    productList = SearchView.this.mData.getJSONArray("productList");
                    productLiked = SearchView.this.mData.getJSONArray("productsLiked");

                    for ( int i = 0 ; i < productList.length() ; i++ ) {
                        JSONObject jo = productList.getJSONObject(i);
                        String name = jo.getString("title");
                        String tag = jo.getString("status");
                        //Log.e("PRODUCT",name + " " + tag);

                        String imageUrl = "TBA";
                        String pid = jo.getString("id");

                        ProductObject po = new ProductObject(name,tag,imageUrl,"","",pid);
                        SearchView.this.mProducts.add(po);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            //this method will be running on UI thread
            SearchView.this.mProductList = (ListView) getView().findViewById(R.id.product_list);
            SearchView.this.mSearchViewAdapter = new SearchViewAdapter(getActivity(),R.layout.product_list_item,SearchView.this.mProducts);
            SearchView.this.mProductList.setAdapter(SearchView.this.mSearchViewAdapter);

            if ( SearchView.this.mProgressDialog.isShowing() ) {
                SearchView.this.mProgressDialog.dismiss();
            }
        }
    }
}
