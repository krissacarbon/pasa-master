package com.pasabuy.pasabuy;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsoluteLayout;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SearchView extends Fragment {

    public ArrayList<ProductObject> mProducts = new ArrayList<ProductObject>();
    private LinearLayout mProductsLeft;
    private LinearLayout mProductsRight;
    private List<Pair<String,String>> params;
    private ProgressDialog mProgressDialog;
    private JSONObject mData;
    private JSONArray mLocations;
    private ArrayList<String> aLocations = new ArrayList<String>();
    private JSONArray mCategories;
    private ArrayList<String> aCategories = new ArrayList<String>();
    private HashMap<String,JSONObject> mSellers = new HashMap<String, JSONObject>();

    public SearchView() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        new SearchTask(this.params).execute();

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

                final Spinner sLoc = (Spinner) dialog.findViewById(R.id.SpinnerFeedbackTypeLocation);
                ArrayAdapter<String> adapterLoc = new ArrayAdapter<String>(SearchView.this.getActivity(),
                        android.R.layout.simple_spinner_item, this.aLocations);
                sLoc.setAdapter(adapterLoc);

                final Spinner sCat = (Spinner) dialog.findViewById(R.id.SpinnerFeedbackTypeCategory);
                ArrayAdapter<String> adapterCat = new ArrayAdapter<String>(SearchView.this.getActivity(),
                        android.R.layout.simple_spinner_item, this.aCategories);
                sCat.setAdapter(adapterCat);

                final Spinner sProd = (Spinner) dialog.findViewById(R.id.SpinnerFeedbackTypeProduct);

                Button dialogButton = (Button) dialog.findViewById(R.id.btn_submit_search);
                Button cancelButton = (Button) dialog.findViewById(R.id.btn_cancel);
                dialogButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        SearchView.this.params.clear();
                        String pType = sProd.getSelectedItemPosition() == 0 ? "" : sProd.getSelectedItem().toString();
                        try {
                        String locId = sLoc.getSelectedItemPosition() == 0 ? "" : SearchView.this.mLocations.getJSONObject((int) (sLoc.getSelectedItemPosition()-1)).getString("id");
                        String catId = sCat.getSelectedItemPosition() == 0 ? "" : SearchView.this.mCategories.getJSONObject((int) (sCat.getSelectedItemPosition()-1)).getString("id");

                            SearchView.this.params.add(new Pair<>("minimumPrice", ((EditText) dialog.findViewById(R.id.min_price_edit)).getText().toString()));
                            SearchView.this.params.add(new Pair<>("maximumPrice", ((EditText) dialog.findViewById(R.id.max_price_edit)).getText().toString()));
                            SearchView.this.params.add(new Pair<>("locationId", locId));
                            SearchView.this.params.add(new Pair<>("categoryId", catId));
                            SearchView.this.params.add(new Pair<>("productType", pType));
                            SearchView.this.params.add(new Pair<>("keyword", ((EditText) dialog.findViewById(R.id.search_edit)).getText().toString()));
                            new SearchTask(SearchView.this.params).execute();
                        } catch (Exception e ) {
                            Log.e("ExceptionSD", e.getMessage());
                        }
                        dialog.dismiss();
                    }
                });
                cancelButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
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
        //new SearchTask(this.params).execute();
    }

    @Override
    public void onStart(){
        super.onStart();
    }

    public void Search(){
        new SearchTask(this.params).execute();
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
                SearchView.this.mProgressDialog.setCancelable(false);
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
                    ArrayList<String> liked = new ArrayList<String>();
                    productList = SearchView.this.mData.getJSONArray("productList");
                    productLiked = SearchView.this.mData.getJSONArray("productsLiked");

                    for ( int i = 0 ; i < productLiked.length() ; i++ ) {
                        liked.add(productLiked.getJSONObject(i).getString("id"));
                    }

                    for ( int i = 0 ; i < productList.length() ; i++ ) {

                        JSONObject jo = productList.getJSONObject(i);

//                        String seller = "";
//                        String sellerImage = "";
//                        if ( jo.get("createdBy") instanceof JSONObject ) {
//                            JSONObject temp = jo.getJSONObject("createdBy");
//                            Log.e("temo",temp.toString());
//                            SearchView.this.mSellers.put(temp.getString("@id"),temp);
//                            seller = temp.getString("name");
//                            sellerImage = temp.getString("accountImageUrl");
//                        } else {
//                            Log.e("TAG", jo.getString("createdBy")+"NE "+SearchView.this.mSellers.values().toString());
//                            JSONObject temp = SearchView.this.mSellers.get(jo.getString("createdBy"));
//                            if ( null!= temp ) {
//                                seller = temp.getString("name");
//                                sellerImage = temp.getString("accountImageUrl");
//                            } else {
//                                seller = "none";
//                                sellerImage = "none";
//                            }
//                        }

                        String name = jo.getString("title");
                        String tag = jo.getString("status");
                        String description = jo.getString("description");
                        String city = jo.getJSONObject("userPlace").getJSONObject("locationDescription").getString("city");
                        String country = jo.getJSONObject("userPlace").getJSONObject("locationDescription").getString("country");
                        String price = jo.getJSONArray("productPriceRules").getJSONObject(0).getString("listedPrice");
                        String type = jo.getString("type");
                        String imageUrl = jo.getJSONArray("imageDescriptions").getJSONObject(0).getString("url");
                        String pid = jo.getString("id");

                        ProductObject po = new ProductObject(name,tag,imageUrl,price,pid);
//                        po.setSeller(seller);
//                        po.setSellerImage(sellerImage);
                        po.setDescription(description);
                        po.setLocation(city,country);
                        po.setRequest(type);
                        if (liked.contains(pid)) {
                            po.setLiked(true);
                        }else{
                            po.setLiked(false);
                        }
                        SearchView.this.mProducts.add(po);
                    }

                } catch (JSONException e) {
                    Log.e("ExceptionProdList", e.getMessage());
                }
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            SearchView.this.mProductsLeft = (LinearLayout) SearchView.this.getView().findViewById(R.id.left_ll);
            SearchView.this.mProductsLeft.removeAllViews();
            SearchView.this.mProductsRight = (LinearLayout) SearchView.this.getView().findViewById(R.id.right_ll);
            SearchView.this.mProductsRight.removeAllViews();

            for (int i = 0;i < SearchView.this.mProducts.size(); i+=2) {

                final ProductObject item = SearchView.this.mProducts.get(i);
                View layout = LayoutInflater.from(SearchView.this.getActivity()).inflate(R.layout.product_list_item, SearchView.this.mProductsLeft, false);
                TextView nameTextView = (TextView) layout.findViewById(R.id.item_name_tv);
                TextView tagTextView = (TextView) layout.findViewById(R.id.tag_tv);
                TextView priceTextView = (TextView) layout.findViewById(R.id.item_price_tv);
                ImageView productIcon = (ImageView) layout.findViewById(R.id.icon);
                DownloadImageTask dlTask = new DownloadImageTask(productIcon);
                dlTask.execute("https://pasabuy.com/displayImage/" + item.getImageUrl());

                nameTextView.setText(item.getName());
                tagTextView.setText(item.getTag());
                if ( item.getTag().equalsIgnoreCase("OPEN") ) {
                    tagTextView.setTextColor(Color.parseColor("#428bca"));
                } else {
                    tagTextView.setTextColor(Color.parseColor("#ff6961"));
                }

                if ( item.getRequestBool() ) {
                    priceTextView.setText("Price is around PHP " + item.getPrice());
                } else {
                    priceTextView.setText("Willing to sell for PHP " + item.getPrice());
                }

                if ( item.getLiked() ) {
                    ((ImageView) layout.findViewById(R.id.heart_image)).setImageDrawable(SearchView.this.getResources().getDrawable(R.drawable.heart_red));
                }

                layout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent productView = new Intent(SearchView.this.getActivity(),ProductItemView.class);
                        productView.putExtra("prodId", item.getProductId());
                        startActivity(productView);
                    }
                });

                SearchView.this.mProductsLeft.addView(layout);
            }
            for (int i = 1;i < SearchView.this.mProducts.size(); i+=2) {

                final ProductObject item = SearchView.this.mProducts.get(i);
                View layout = LayoutInflater.from(SearchView.this.getActivity()).inflate(R.layout.product_list_item, SearchView.this.mProductsRight, false);
                TextView nameTextView = (TextView) layout.findViewById(R.id.item_name_tv);
                TextView tagTextView = (TextView) layout.findViewById(R.id.tag_tv);
                TextView priceTextView = (TextView) layout.findViewById(R.id.item_price_tv);
                ImageView productIcon = (ImageView) layout.findViewById(R.id.icon);
                DownloadImageTask dlTask = new DownloadImageTask(productIcon);
                dlTask.execute("https://pasabuy.com/displayImage/" + item.getImageUrl() );

                nameTextView.setText(item.getName());
                tagTextView.setText(item.getTag());

                if ( item.getTag().equalsIgnoreCase("OPEN") ) {
                    tagTextView.setTextColor(Color.parseColor("#428bca"));
                } else {
                    tagTextView.setTextColor(Color.parseColor("#ff6961"));
                }

                if ( item.getRequestBool() ) {
                    priceTextView.setText("Price is around PHP " + item.getPrice());
                } else {
                    priceTextView.setText("Willing to sell for PHP " + item.getPrice());
                }

                if ( item.getLiked() ) {
                    ((ImageView) layout.findViewById(R.id.heart_image)).setImageDrawable(SearchView.this.getResources().getDrawable(R.drawable.heart_red));
                }

                layout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent productView = new Intent(SearchView.this.getActivity(),ProductItemView.class);
                        productView.putExtra("prodId", item.getProductId());
                        startActivity(productView);
                    }
                });

                SearchView.this.mProductsRight.addView(layout);
            }

            try {
                if (SearchView.this.mData.has("locations")) {
                    SearchView.this.mLocations = SearchView.this.mData.getJSONArray("locations");
                    SearchView.this.aLocations.add("Anywhere");
                    for ( int i = 0; i < SearchView.this.mLocations.length() ; i++ ) {
                        String locString = (SearchView.this.mLocations.getJSONObject(i).getString("city")+" " +SearchView.this.mLocations.getJSONObject(i).getString("country")).trim();
                        SearchView.this.aLocations.add(locString);
                    }
                }
                if (SearchView.this.mData.has("categories")) {
                    SearchView.this.mCategories = SearchView.this.mData.getJSONArray("categories");
                    SearchView.this.aCategories.add("Anything");
                    for ( int i = 0; i < SearchView.this.mCategories.length() ; i++ ) {
                        String locString = SearchView.this.mCategories.getJSONObject(i).getString("name");
                        SearchView.this.aCategories.add(locString);
                    }
                }

                if ( SearchView.this.mData.has("searchInput") ) {
                    JSONObject temp = SearchView.this.mData.getJSONObject("searchInput");
                    final TextView current = (TextView) SearchView.this.getView().findViewById(R.id.current);
                    TextView total = (TextView) SearchView.this.getView().findViewById(R.id.total);
                    final int currentPage = temp.getInt("pageNumber");
                    int totalPages = temp.getInt("pageCount");
                    current.setText(currentPage+"");
                    total.setText(totalPages+"");

                    if ( (currentPage -1) > 0 ) {
                        ImageView arrowLeft = (ImageView)SearchView.this.getView().findViewById(R.id.left);
                        arrowLeft.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                List<Pair<String,String>> pageParams = new ArrayList<Pair<String, String>>(SearchView.this.params);
                                pageParams.add(new Pair<>("pageNumber", (currentPage-1)+""));
                                new SearchTask(pageParams).execute();
                            }
                        });
                    } else {
                        ImageView arrowLeft = (ImageView)SearchView.this.getView().findViewById(R.id.left);
                        arrowLeft.setOnClickListener(null);
                    }

                    if ( (currentPage + 1) <= totalPages ) {
                        ImageView arrowRight = (ImageView)SearchView.this.getView().findViewById(R.id.right);
                        arrowRight.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                List<Pair<String,String>> pageParams = new ArrayList<Pair<String, String>>(SearchView.this.params);
                                pageParams.add(new Pair<>("pageNumber", (currentPage+1)+""));
                                new SearchTask(pageParams).execute();
                            }
                        });

                    } else {
                        ImageView arrowRight = (ImageView)SearchView.this.getView().findViewById(R.id.right);
                        arrowRight.setOnClickListener(null);
                    }
                }
            } catch (JSONException e) {
                Log.e("ERROR",e.getMessage());
            }
            if ( SearchView.this.mProgressDialog.isShowing() ) {
                SearchView.this.mProgressDialog.dismiss();
            }
            try {
                Log.d("DATA", SearchView.this.mData.toString());
            } catch (Exception e) {

            }

        }
    }

    public class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }
}
