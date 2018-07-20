package com.example.abc.random_videocall_application;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by CodeMaven3015 on 4/17/2018.
 */

public class VolleyAPICallJsonObject {

    String JsonURL ;
    Context context;
    RequestQueue requestQueue;
    private Map<String, String> header;
    SharedPreferences sharedpreferences ;

    public VolleyAPICallJsonObject(Context context, String JsonURL) {
        this.JsonURL = JsonURL;
        this.context = context;
        requestQueue = Volley.newRequestQueue(context);
        header = new HashMap<>();
        sharedpreferences = context.getSharedPreferences("UserDetails", 0);
    }

    public VolleyAPICallJsonObject(Context context, String JsonURL, Map<String, String> header) {
        this.JsonURL = JsonURL;
        this.context = context;
        requestQueue = Volley.newRequestQueue(context);
        this.header = header;
        sharedpreferences = context.getSharedPreferences("UserDetails", 0);

    }

    public void addHeader(String key, String value) {
        header.put(key, value);
    }

    public void executeRequest(int method, final VolleyAPICallJsonObject.VolleyCallback callback) {
        Log.e("info",(new JSONObject(header)).toString());
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(method, JsonURL,new JSONObject(header), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                  callback.getResponse(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                  Log.e("ONI","INSIDE ERROR CALLBACK");
                callback.getError(error);
            }
        }){

                @Override
                public Map<String, String> getHeaders() {
                HashMap<String, String> headers1 = new HashMap<String, String>();
                headers1.put("Content-Type", "application/json; charset=utf-8");
               // headers1.put("Content-Type", "application/x-www-form-urlencoded; charset=utf-8");

                return headers1;
            }
 
        }
        ;

        requestQueue.add(jsonObjectRequest);


//        StringRequest stringRequest = new StringRequest(method, JsonURL, new Response.Listener<String>() {
//            @Override
//            public void onResponse(String response) {
//                try {
//                    callback.getResponse(new JSONObject(response));
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//
//            }
//        }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                Log.e("Check","jdhsjch");
//            }
//        }){ihkn
//
//            @Override
//            protected Map<String, String> getParams()
//            {
//                return header;
//            }
//
//      };
//        requestQueue.add(stringRequest);

    }

    public interface VolleyCallback {
        public void getResponse(JSONObject response);
        public void getError(VolleyError error);
    }
}
