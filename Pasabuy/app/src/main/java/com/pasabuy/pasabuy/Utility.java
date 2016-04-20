package com.pasabuy.pasabuy;

import android.os.AsyncTask;
import android.util.Log;
import android.util.Pair;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;


public class Utility {

    private static final String LOGIN_API = "https://pasabuy.com/api/authenticate";
    private static final String ADD_JOURNEY_API =  "https://pasabuy.com/api/journey";
    private static final String DELETE_JOURNEY_API =  "https://pasabuy.com/api/journey/delete/";
    private static final String GET_JOURNEY_API =  "https://pasabuy.com/api/journey/display/";
    private static final String GET_USER_API =  "https://pasabuy.com/api/user/display/";
    private static final String SEARCH_API = "https://pasabuy.com/api/search";
    private static final String GET_ALL_LOCATION = "https://pasabuy.com/api/location";
    private static final String GET_ALL_CATEGORY = "https://pasabuy.com/api/category";
    private static final String GET_PRODUCT = "https://pasabuy.com/api/product/display/";

    public static boolean login(String user_name, String password) throws JSONException {
        List<Pair<String,String>> params = new ArrayList<>();
        params.add(new Pair<>("username", user_name));
        params.add(new Pair<>("password", password));

        JSONObject result = null;
        try {
            result = jsonRequest(LOGIN_API,params,true);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if(result != null && result.has("status") && "OK".equalsIgnoreCase(result.getString("status"))) {
            return true;
        }else {
            return false;
        }
    }

    public static boolean add_journey(String city, String country, String returnDate) throws JSONException {
        List<Pair<String,String>> params = new ArrayList<>();
        params.add(new Pair<>("city", city));
        params.add(new Pair<>("country", country));
        params.add(new Pair<>("returnDate", returnDate));

        JSONObject result = null;
        try {
            result = jsonRequest(ADD_JOURNEY_API,params,true);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if(result != null && result.has("status") && "OK".equalsIgnoreCase(result.getString("status"))) {
            return true;
        }else {
            return false;
        }
    }

    public static boolean delete_journey(String journeyId) throws JSONException {
//        List<Pair<String,String>> params = new ArrayList<>();
//        params.add(new Pair<>("journeyId", journeyId));

        JSONObject result = null;
        try {
            result = jsonRequest(DELETE_JOURNEY_API+journeyId,null,true);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if(result != null && result.has("status") && "OK".equalsIgnoreCase(result.getString("status"))) {
            return true;
        }else {
            return false;
        }
    }

    public static JSONObject get_user(String userId) throws JSONException {

        JSONObject result = null;
        try {
            result = jsonRequest(GET_USER_API+userId,null,false);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if(result != null ) {
            return result;
        }else {
            return new JSONObject("JSON is null");
        }
    }

    public static JSONObject get_journey(String userId) throws JSONException {
//        List<Pair<String,String>> params = new ArrayList<>();
//        params.add(new Pair<>("journeyId", journeyId));

        JSONObject result = null;
        try {
            result = jsonRequest(GET_JOURNEY_API+userId,null,false);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if(result != null ) {
            return result;
        }else {
            return new JSONObject("JSON is null");
        }
    }

    public static JSONObject search_api(List<Pair<String,String>> search_params ) throws JSONException {
        //List<Pair<String,String>> params = search_params;

        JSONObject result = null;
        try {
            result = jsonRequest(SEARCH_API, search_params, true);
        } catch (IOException e) {
            Log.e("error",e.getMessage());
            e.printStackTrace();
        }

        if(result != null ) {
            return result;
        }else {
            return new JSONObject("JSON is null");
        }

    }

    public static JSONObject get_all_location() throws JSONException {

        JSONObject result = null;
        try {
            result = jsonRequest(GET_ALL_LOCATION,null,false);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if(result != null ) {
            return result;
        }else {
            return new JSONObject("JSON is null");
        }
    }

    public static JSONObject get_all_category() throws JSONException {

        JSONObject result = null;
        try {
            result = jsonRequest(GET_ALL_CATEGORY,null,false);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if(result != null ) {
            return result;
        }else {
            return new JSONObject("JSON is null");
        }
    }

    public static JSONObject get_product(String productId) throws JSONException {

        JSONObject result = null;
        try {
            result = jsonRequest(GET_PRODUCT+productId,null,false);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if(result != null ) {
            return result;
        }else {
            return new JSONObject("JSON is null");
        }
    }



    private static JSONObject jsonRequest(String urlString, List<Pair<String,String>> params, boolean isPost) throws IOException
    {
        URL url = new URL(urlString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setReadTimeout(10000 /* milliseconds */);
        conn.setConnectTimeout(15000 /* milliseconds */);
        if(isPost) {
            conn.setRequestMethod("POST");
        }else{
            conn.setRequestMethod("GET");
        }

        conn.setDoInput(true);

        if(params!=null) {
            OutputStream os = conn.getOutputStream();
            BufferedWriter writer = new BufferedWriter(
                    new OutputStreamWriter(os, "UTF-8"));
            writer.write(getQuery(params));
            writer.flush();
            writer.close();
            os.close();
        }
        conn.connect();

        JSONObject result = null;

        try {
            if(!urlString.contains(GET_JOURNEY_API) && !urlString.contains(GET_ALL_CATEGORY) && !urlString.contains(GET_ALL_LOCATION) && !urlString.contains(GET_PRODUCT)) {
                result = new JSONObject(convertStreamToString(conn.getInputStream()));
            }else{
                JSONArray result_temp = new JSONArray(convertStreamToString(conn.getInputStream()));
                result = new JSONObject();
                result.put("journey_array",result_temp);
            }

        } catch (Exception e) {
            Log.e("Exception", e.getMessage());
        }
        return result;
    }

    private static String convertStreamToString(InputStream is) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();

        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line).append('\n');
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return sb.toString();
    }

    //For creating query parameters
    private static String getQuery(List<Pair<String,String>> params) throws UnsupportedEncodingException
    {
        StringBuilder result = new StringBuilder();
        boolean first = true;

        for (Pair<String,String> pair : params)
        {
            if (first)
                first = false;
            else
                result.append("&");

            result.append(URLEncoder.encode(pair.first, "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(pair.second, "UTF-8"));
        }
        return result.toString();
    }
}
