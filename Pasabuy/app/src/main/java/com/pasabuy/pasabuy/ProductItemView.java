package com.pasabuy.pasabuy;


import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.media.Image;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ProductItemView extends ActionBarActivity {

	public ProductItemView(){}
	private JSONObject json;
	private String mProductId;
	private boolean mLiked = false;
	private ProgressDialog mProgressDialog;

	private ImageView avatar;
	private TextView username;
	private ImageView prodImage;
	private ImageView prodLike;
	private TextView prodName;
	private TextView prodType;
	private TextView prodPrice;
	private TextView prodDesc;
	private TextView prodLoca;
	private RelativeLayout mRL;
	
	@Override
	public void onStart() {
		super.onStart();
	}


	@Override
	protected void onCreate(Bundle savedInstanceState) {
 		super.onCreate(savedInstanceState);
		setContentView(R.layout.product_dialog);
		android.support.v7.app.ActionBar bar = this.getSupportActionBar();
		//for color
		bar.setBackgroundDrawable(new ColorDrawable(0xCC2F6494));
		bar.setDisplayHomeAsUpEnabled(true);
		bar.setIcon(R.drawable.logo);

		this.mProductId = this.getIntent().getStringExtra("prodId");
		this.avatar = (ImageView) findViewById(R.id.avatar);
		this.username = (TextView) findViewById(R.id.username_product);
		this.prodImage = (ImageView) findViewById(R.id.product_image);
		this.prodName = (TextView) findViewById(R.id.product_title);
		this.prodDesc = (TextView) findViewById(R.id.product_description);
		this.prodType = (TextView) findViewById(R.id.product_type);
		this.prodPrice = (TextView) findViewById(R.id.product_price);
		this.prodLoca = (TextView) findViewById(R.id.product_location);
		this.prodLike = (ImageView) findViewById(R.id.heart_like);
		this.mRL = (RelativeLayout) findViewById(R.id.name_layout);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar actions click
		switch (item.getItemId()) {
			default:
				this.finish();
				return super.onOptionsItemSelected(item);
		}
	}


	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		if(isNetworkConnected()) {
			new getProductTask().execute();
		} else {
			Toast.makeText(this.getApplicationContext(), "No internet connection!", Toast.LENGTH_LONG).show();
		}
	}

	private boolean isNetworkConnected() {
		ConnectivityManager cm = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
		return cm.getActiveNetworkInfo() != null;
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
				RoundImage ri = new RoundImage(mIcon11);
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

	public class DownloadImageTaskRound extends AsyncTask<String, Void, RoundImage> {
		ImageView bmImage;

		public DownloadImageTaskRound(ImageView bmImage) {
			this.bmImage = bmImage;
		}

		protected RoundImage doInBackground(String... urls) {
			String urldisplay = urls[0];
			Bitmap mIcon11 = null;
			RoundImage ri = null;
			try {
				InputStream in = new java.net.URL(urldisplay).openStream();
				mIcon11 = BitmapFactory.decodeStream(in);
				ri = new RoundImage(mIcon11);
			} catch (Exception e) {
				Log.e("Error", e.getMessage());
				e.printStackTrace();
			}
			return ri;
		}

		protected void onPostExecute(RoundImage result) {
			bmImage.setImageDrawable(result);
		}
	}

	private class getProductTask extends AsyncTask<Void, Void, Void>
	{
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			//this method will be running on UI thread
			if (null == ProductItemView.this.mProgressDialog) {
				ProductItemView.this.mProgressDialog = new ProgressDialog(ProductItemView.this);
			}

			if(!(ProductItemView.this.mProgressDialog.isShowing())) {
				ProductItemView.this.mProgressDialog.setMessage("\tLoading...");
				ProductItemView.this.mProgressDialog.setCancelable(false);
				ProductItemView.this.mProgressDialog.show();
			}
		}
		@Override
		protected Void doInBackground(Void... params) {

			//do your long running http tasks here,you dont want to pass argument and u can access the parent class' variable url over here
			try {
				ProductItemView.this.json = Utility.get_product(ProductItemView.this.mProductId);
				if ( null != ProductItemView.this.json ) {
					ProductItemView.this.json = ProductItemView.this.json.getJSONObject("product");
					ProductItemView.this.mLiked = ProductItemView.this.json.getBoolean("isProductLiked");
					Log.e("Prod", ProductItemView.this.json.getBoolean("isProductLiked")+ ":");
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
			//Log.e("Prod", ProductItemView.this.json.getString("isProductLiked")+ ":");
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			//this method will be running on UI thread
			try {
				String city = ProductItemView.this.json.getJSONObject("userPlace").getJSONObject("locationDescription").getString("city");
				String country = ProductItemView.this.json.getJSONObject("userPlace").getJSONObject("locationDescription").getString("country");
				String location = "";
				if (null != city && !city.isEmpty()) {
					location = ("Shop in " + city + ", " + country).trim();
				} else {
					location = "Shop in " + country.trim();
				}
				String userImage = ProductItemView.this.json.getJSONObject("createdBy").getString("accountImageUrl");
				String productImage = ProductItemView.this.json.getJSONArray("imageDescriptions").getJSONObject(0).getString("url");
				ProductItemView.this.username.setText(ProductItemView.this.json.getJSONObject("createdBy").getString("name"));
				ProductItemView.this.prodName.setText(ProductItemView.this.json.getString("title"));
				ProductItemView.this.prodDesc.setText(Html.fromHtml(ProductItemView.this.json.getString("description")));
				ProductItemView.this.prodType.setText(ProductItemView.this.json.getString("type"));
				String price = "";
				if ("Recommend".equalsIgnoreCase(ProductItemView.this.json.getString("type"))){
					price = "Willing to sell for Php " + ProductItemView.this.json.getJSONArray("productPriceRules").getJSONObject(0).getString("listedPrice");
				} else {
					price = "Price is around Php " + ProductItemView.this.json.getJSONArray("productPriceRules").getJSONObject(0).getString("listedPrice");
				}
				if (ProductItemView.this.mLiked){
					ProductItemView.this.prodLike.setImageDrawable(ProductItemView.this.getResources().getDrawable(R.drawable.heart_red));
				} else {
					ProductItemView.this.prodLike.setImageDrawable(ProductItemView.this.getResources().getDrawable(R.drawable.heart_white));
				}
				ProductItemView.this.prodPrice.setText(price);
				ProductItemView.this.prodLoca.setText(location);
				new DownloadImageTask(ProductItemView.this.prodImage).execute("https://pasabuy.com/displayImage/" + productImage);
				new DownloadImageTaskRound(ProductItemView.this.avatar).execute("https://pasabuy.com/displayAccountImage/"+userImage);
				ProductItemView.this.mRL.setVisibility(View.VISIBLE);
			} catch (JSONException e) {

			}

			if ( ProductItemView.this.mProgressDialog.isShowing() ) {
				ProductItemView.this.mProgressDialog.dismiss();
			}
		}
	}
	
}
