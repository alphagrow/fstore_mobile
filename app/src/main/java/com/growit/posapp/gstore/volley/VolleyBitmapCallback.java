package com.growit.posapp.gstore.volley;

import android.graphics.Bitmap;

import org.json.JSONException;

public interface VolleyBitmapCallback {
    void onSuccess(Bitmap result) throws JSONException;
    void onError(String result) throws Exception;
}
