package com.example.abc.random_videocall_application;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by CodeMaven3015 on 3/5/2018.
 */

public class VolleyAPICall {
        String JsonURL ;
        Context context;
        RequestQueue requestQueue;
        private Map<String, String> header;
    SharedPreferences sharedpreferences ;

public VolleyAPICall(Context context, String JsonURL) {
        this.JsonURL = JsonURL;
        this.context = context;
        requestQueue = Volley.newRequestQueue(context);
        header = new HashMap<>();
        sharedpreferences = context.getSharedPreferences("UserDetails", 0);

    }

    public VolleyAPICall(Context context, String JsonURL, Map<String, String> header) {
        this.JsonURL = JsonURL;
        this.context = context;
        requestQueue = Volley.newRequestQueue(context);
        this.header = header;
        sharedpreferences = context.getSharedPreferences("UserDetails", 0);

    }


    public void executeRequest(int method, final VolleyCallback callback, Response.ErrorListener errorListener) {
        JSONArray array = new JSONArray() ;
        try {
            array = new JSONArray(header);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.e("gdhg",array.toString()+"vhe");

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(method, JsonURL,array, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                callback.getResponse(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                  Log.e("ONI","INSIDE ERROR CALLBACK");
            }
        }){


            @Override
            public Map<String, String> getHeaders()  {
                HashMap<String, String> headers1 = new HashMap<String, String>();
                headers1.put("Content-Type", "application/json; charset=utf-8; application/x-www-form-urlencoded");
                headers1.put("X-CSRF-Token",sharedpreferences.getString("TOKEN",""));
                return headers1;
            }
//        public String getBodyContentType() {
//    return "application/x-www-form-urlencoded;" ;
}

        ;
        requestQueue.add(jsonArrayRequest);

    }

    public interface VolleyCallback {
        public void getResponse(JSONArray response);
    }
}
