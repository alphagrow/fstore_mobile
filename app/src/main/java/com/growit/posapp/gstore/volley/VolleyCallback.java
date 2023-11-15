package com.growit.posapp.gstore.volley;


import org.json.JSONException;

public interface VolleyCallback {
    void onSuccess(Object result) throws JSONException;
    void onError(String result) throws Exception;
}

