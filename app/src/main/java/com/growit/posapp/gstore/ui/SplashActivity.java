package com.growit.posapp.gstore.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatDelegate;

import com.growit.posapp.gstore.MainActivity;
import com.growit.posapp.gstore.R;
import com.growit.posapp.gstore.utils.SessionManagement;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class SplashActivity extends Activity {
    Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.setStatusBarColor(getResources().getColor(R.color.white));
        setContentView(R.layout.activity_splash);
        handler = new Handler();
        handler.postDelayed(() -> {
            if (!SessionManagement.getToken(SplashActivity.this)) {
                Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            } else {
                Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }

        }, 3000);

    }

//    private void getAllMatchRequest() {
//        Map<String, String> params = new HashMap<String, String>();
//        params.put("Authorization", SessionManagement.getJWTToken(this));
//        new VolleyRequestHandler(this, params).createGetRequest(ApiConstants.GET_ALL_MATCH, new VolleyCallback() {
//            @Override
//            public void onSuccess(Object result) throws JSONException {
//                Log.v("Response", result.toString());
//                JSONObject obj = new JSONObject(result.toString());
//                int statusCode = obj.optInt("code");
//                if (statusCode == 200) {
//                    Intent intent = new Intent(SplashActivity.this, MainActivity.class);
////                    intent.putExtra("Tabvalue",1);
//                    startActivity(intent);
//                    finish();
//                }
//            }
//
//            @Override
//            public void onError(String result) throws Exception {
//                Log.v("Response", result.toString());
//                Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
//                startActivity(intent);
//                finish();
//            }
//        });
//    }
}