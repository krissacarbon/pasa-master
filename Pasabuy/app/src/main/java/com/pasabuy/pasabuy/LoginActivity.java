package com.pasabuy.pasabuy;

/**
 * Created by Krissa on 18/02/2016.
 */
import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

import butterknife.ButterKnife;
import butterknife.Bind;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";
    private static final int REQUEST_SIGNUP = 0;

    private CookieManager mCookieManager;

    @Bind(R.id.input_email) EditText _emailText;
    @Bind(R.id.input_password) EditText _passwordText;
    @Bind(R.id.btn_login) Button _loginButton;
//    @Bind(R.id.link_signup) TextView _signupLink;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        //For handling cookies
        this.mCookieManager = new CookieManager();
        CookieHandler.setDefault(this.mCookieManager);

        _loginButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                login();
            }
        });

//        _signupLink.setOnClickListener(new View.OnClickListener() {
//
//            @Override
//            public void onClick(View v) {
//                // Start the Signup activity
//                Intent intent = new Intent(getApplicationContext(), SignupActivity.class);
//                startActivityForResult(intent, REQUEST_SIGNUP);
//            }
//        });
    }

    @Override
    public void onResume(){
        super.onResume();
        //Clear cookies
        LoginActivity.this.mCookieManager.getCookieStore().removeAll();
    }

    public void login() {
        Log.d(TAG, "Login");

        if (!validate()) {
            onLoginFailed();
            return;
        }

        _loginButton.setEnabled(false);

//        final ProgressDialog progressDialog = new ProgressDialog(LoginActivity.this,
//                R.style.AppTheme_Dark_Dialog);
//        progressDialog.setIndeterminate(true);
//        progressDialog.setMessage("Authenticating...");
//        progressDialog.show();

        // TODO: Implement your own authentication logic here.
        if (isNetworkConnected()) {
            new LoginTask().execute();
        } else {
            _loginButton.setEnabled(true);
            Toast.makeText(this.getApplicationContext(), "No internet connection!", Toast.LENGTH_LONG).show();
        }
//        new android.os.Handler().postDelayed(
//                new Runnable() {
//                    public void run() {
//                        // On complete call either onLoginSuccess or onLoginFailed
//                        onLoginSuccess();
//                        // onLoginFailed();
//                        progressDialog.dismiss();
//                    }
//                }, 3000);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_SIGNUP) {
            if (resultCode == RESULT_OK) {

                // TODO: Implement successful signup logic here
                // By default we just finish the Activity and log them in automatically
                this.finish();
            }
        }
    }

    @Override
    public void onBackPressed() {
        // Disable going back to the MainActivity
        super.onBackPressed();
        //moveTaskToBack(true);
    }

    public void onLoginSuccess() {
        _loginButton.setEnabled(true);
        Intent homeScreen = new Intent(LoginActivity.this,MainActivity.class);
        startActivity(homeScreen);
        finish();
    }

    public void onLoginFailed() {
        Toast.makeText(getBaseContext(), "Login failed", Toast.LENGTH_LONG).show();

        _loginButton.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;

        String email = _emailText.getText().toString();
        String password = _passwordText.getText().toString();

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _emailText.setError("enter a valid email address");
            valid = false;
        } else {
            _emailText.setError(null);
        }

        if (password.isEmpty() || password.length() < 4 || password.length() > 10) {
            _passwordText.setError("between 4 and 10 alphanumeric characters");
            valid = false;
        } else {
            _passwordText.setError(null);
        }

        return valid;
    }

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null;
    }

    private class LoginTask extends AsyncTask<String, Void, Boolean> {
        final ProgressDialog progressDialog = new ProgressDialog(LoginActivity.this,R.style.AppTheme_Dark_Dialog);

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog.setIndeterminate(true);
            progressDialog.setMessage("Authenticating...");
            progressDialog.show();

        }

        @Override
        protected Boolean doInBackground(String... params) {
            boolean login_check = false;
            //Utility util = new Utility();
            try {
                login_check = Utility.login(_emailText.getText().toString(), _passwordText.getText().toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return login_check;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            if(result) {
                onLoginSuccess();
            } else {
                onLoginFailed();
            }
            progressDialog.dismiss();
        }
    }
}

