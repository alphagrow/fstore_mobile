package com.growit.posapp.fstore.volley;


import android.content.Context;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.growit.posapp.fstore.utils.ApiConstants;

import org.json.JSONException;

import java.util.HashMap;
import java.util.Map;

public class VolleyRequestHandler {

    private Context mContext;
    private Map<String, String>  params;
    public VolleyRequestHandler(Context context,Map<String, String>  map) {
        this.mContext = context;
        params=map;
    }

    public void createGetRequest(final String uri, final VolleyCallback callback) {
        CustomJasonObjectRequest rq = new CustomJasonObjectRequest(Request.Method.GET,
                ApiConstants.BASE_URL+uri, params,
                response -> {
                    try {
                        callback.onSuccess(response);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> {
                    try {
                        callback.onError(error.toString());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }) {

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String,String> headers = new HashMap<String, String>();
                headers.put("Content-Type","application/x-www-form-urlencoded");
                return headers;
            }

            @Override
            protected Map<String, String> getParams() {
                return params;
            }

        };


        // Request added to the RequestQueue
        VolleyController.getInstance(mContext).addToRequestQueue(rq);
    }

    // Custom JSON Request Handler
    public void createRequest(final String uri, final VolleyCallback callback) {
        CustomJasonObjectRequest rq = new CustomJasonObjectRequest(Request.Method.POST, ApiConstants.BASE_URL+uri, params,
                response -> {

                    try {
                        callback.onSuccess(response);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> {
                    try {
                        callback.onError(error.toString());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }) {

            @Override
            public Map<String, String> getHeaders() {
                Map<String,String> params = new HashMap<String, String>();
                params.put("Content-Type","application/x-www-form-urlencoded");
                return params;
            }

            @Override
            protected Map<String, String> getParams() {
                return params;
            }

        };

        // Request added to the RequestQueue
        rq.setRetryPolicy(new DefaultRetryPolicy( 50000, 5, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleyController.getInstance(mContext).addToRequestQueue(rq);
    }


    public void putRequest(final String uri, final VolleyCallback callback) {
        CustomJasonObjectRequest rq = new CustomJasonObjectRequest(Request.Method.PUT,
                ApiConstants.BASE_URL+uri, params,
                response -> {
                    try {
                        callback.onSuccess(response);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> {
                    try {
                        callback.onError(error.toString());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }) {

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String,String> params = new HashMap<String, String>();
                params.put("Content-Type","application/x-www-form-urlencoded");
//                params.put("Authorization", SessionManagement.getJWTToken(mContext));
                return params;
            }

            @Override
            protected Map<String, String> getParams() {
                return params;
            }

        };

        // Request added to the RequestQueue
//        String cookies = CookieManager.getInstance().getCookie(view.getUrl());
        //add line for time
        //*
        rq.setRetryPolicy(new DefaultRetryPolicy( 10 * 60 * 1000, 5, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
//*
        VolleyController.getInstance(mContext).addToRequestQueue(rq);
    }
}