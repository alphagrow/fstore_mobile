package com.growit.posapp.gstore.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.growit.posapp.gstore.MainActivity;
import com.growit.posapp.gstore.R;
import com.growit.posapp.gstore.utils.ApiConstants;
import com.growit.posapp.gstore.utils.SessionManagement;
import com.growit.posapp.gstore.utils.Utility;
import com.growit.posapp.gstore.volley.VolleyCallback;
import com.growit.posapp.gstore.volley.VolleyRequestHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ResetPasswordActivity extends AppCompatActivity {

    protected EditText otpEditText, emailEditText, newPassEditText, confirmPassEditText;
    TextView resetBtn;
    private String mobile_no = "";
    private String jobId ;
    private String cust_number;
    ProgressDialog progressDialog;
    SessionManagement sm;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE); //will hide the title
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_reset_password);
        initViews();

    }

    private void initViews() {
        otpEditText = findViewById(R.id.et_otp);
        emailEditText = findViewById(R.id.et_email);
        newPassEditText = findViewById(R.id.et_newPassword);
        confirmPassEditText = findViewById(R.id.et_confirmPassword);
        resetBtn = findViewById(R.id.reset_btn);
        progressDialog = new ProgressDialog(ResetPasswordActivity.this);
        progressDialog.setMax(100);
        progressDialog.setMessage("Please wait while a moment...");
        progressDialog.setTitle("Authenticating");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        resetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (resetBtn.getText().toString().equalsIgnoreCase("Send OTP")) {
                    cust_number =  emailEditText.getText().toString().trim();
                    if (!Utility.isNetworkAvailable(ResetPasswordActivity.this)) {
                        Toast.makeText(ResetPasswordActivity.this, "Network not available.Please try later!", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (!cust_number.isEmpty()) {
                        getCustomerRequest(cust_number);
                        resetBtn.setText("Verify OTP");
                    }else {
                        Toast.makeText(ResetPasswordActivity.this, "Enter the Mobile Number", Toast.LENGTH_SHORT).show();

                    }
                }
                else if (resetBtn.getText().toString().equalsIgnoreCase("Verify OTP")) {
                    emailEditText.setVisibility(View.GONE);
                    String  str_otp =  otpEditText.getText().toString().trim();
                    if (!Utility.isNetworkAvailable(ResetPasswordActivity.this)) {
                        Toast.makeText(ResetPasswordActivity.this, "Network not available.Please try later!", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if(str_otp.equals(jobId)&& cust_number !=null) {
                        newPassEditText.setVisibility(View.VISIBLE);
                        confirmPassEditText.setVisibility(View.VISIBLE);
                        otpEditText.setVisibility(View.GONE);
                         resetBtn.setText("Done");

                    }else {
                        Toast.makeText(ResetPasswordActivity.this, "Enter the OTP", Toast.LENGTH_SHORT).show();

                    }
                }else if(resetBtn.getText().toString().equalsIgnoreCase("Done")){
                  String  str_new_password =  newPassEditText.getText().toString().trim();
                 String   str_confirmPass =  confirmPassEditText.getText().toString().trim();
                    if (!Utility.isNetworkAvailable(ResetPasswordActivity.this)) {
                        Toast.makeText(ResetPasswordActivity.this, "Network not available.Please try later!", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (!str_new_password.isEmpty()&&!str_confirmPass.isEmpty()&&str_new_password.equals(str_confirmPass)) {
                        Intent intent = new Intent(ResetPasswordActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                      //  getRestPassword(str_new_password);
                    }else {
                        Toast.makeText(ResetPasswordActivity.this, "Enter the new password", Toast.LENGTH_SHORT).show();

                    }
                }
            }
        });

    }


    private void getCustomerRequest(String number){
        RequestQueue queue = Volley.newRequestQueue(ResetPasswordActivity.this);
        String url = ApiConstants.BASE_URL_SMS + ApiConstants.GET_SMS + "user=" + "GROWIT" + "&" + "password=" + "123456" + "&" + "senderid=" + "GROFAR" + "&" + "channel=" + "Trans" + "&" + "DCS=" + "0" + "&" + "flashsms=" + "0" + "&" + "number=" + number + "&" + "text=" + " Dear User your OTP to register with Grow It is" + "&" + "route=" + "07" + "&" + "peid=" + "00";
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.v("Response", response.toString());
                JSONObject obj = null;

                try {
                    obj = new JSONObject(response.toString());
                    int statusCode = obj.optInt("ErrorCode");
                    String error_message = obj.optString("ErrorMessage");
                    jobId = obj.optString("JobId");
                    Log.v("otp", jobId);
                    if(statusCode == 000 && error_message.equalsIgnoreCase("Done")){
                        JSONArray jsonArray = obj.getJSONArray("MessageData");
                        getVerifyOTP(cust_number, jobId);

                    }


                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }


            }
        }, error -> {
            Toast.makeText(ResetPasswordActivity.this, "Fail to get data.."+error, Toast.LENGTH_SHORT).show();
            Log.v("otp", String.valueOf(error));
        });
        queue.add(jsonObjectRequest);
    }
    private void getVerifyOTP(String mobile_no,String otp){
        RequestQueue queue = Volley.newRequestQueue(ResetPasswordActivity.this);
        String url = ApiConstants.BASE_URL_SMS + ApiConstants.GET_SMS + "user=" + "GROWIT" + "&" + "password=" + "123456" + "&" + "senderid=" + "GROFAR" + "&" + "channel=" + "Trans" + "&" + "DCS=" + "0" + "&" + "flashsms=" + "0" + "&" + "number=" + mobile_no + "&" + "text=" + " Dear User your OTP to register with Grow It is "+otp;
        Log.v("url_verf", url);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.v("Response_verf", response.toString());
                JSONObject obj = null;

                try {
                    obj = new JSONObject(response.toString());
                    int statusCode = obj.optInt("ErrorCode");
                    String error_message = obj.optString("ErrorMessage");
                    String jobId = obj.optString("JobId");
                    Log.v("otp_verf", jobId);
                    if(statusCode == 000 && error_message.equalsIgnoreCase("Done")){
                        emailEditText.setVisibility(View.GONE);
                        otpEditText.setVisibility(View.VISIBLE);


                    }
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }


            }
        }, error -> {
            Toast.makeText(ResetPasswordActivity.this, "Fail to get data.."+error, Toast.LENGTH_SHORT).show();
            Log.v("otp", String.valueOf(error));
        });
        queue.add(jsonObjectRequest);
    }


//    private void getRestPassword(String new_password) {
//        Map<String, String> params = new HashMap<>();
//        params.put("login", storeID);
//        params.put("password", password);
//        new VolleyRequestHandler(this, params).createRequest(ApiConstants.SIGN_IN, new VolleyCallback() {
//            private String message = "Please enter correct StoreID/Password.";
//
//            @Override
//            public void onSuccess(Object result) throws JSONException {
//                JSONObject obj = new JSONObject(result.toString());
//                Log.i("obj", obj.toString());
//                progressDialog.cancel();
//                String status = obj.optString("status");
//                int statusCode = obj.optInt("statuscode");
//                String name = obj.optString("name");
//                String token = obj.optString("token");
//                int id = obj.optInt("user_id");
//                if (statusCode == 200 && status.equalsIgnoreCase("success")) {
//                    sm.createLoginSession(true, name, storeID, password, token);
//                    sm.saveUserId(id);
//                    getGSTRequest();
//                    getCustomerRequest();
//                }
//            }
//
//            @Override
//            public void onError(String result) throws Exception {
//                Log.v("Response", result.toString());
//                progressDialog.cancel();
//                Toast.makeText(ResetPasswordActivity.this, message, Toast.LENGTH_SHORT).show();
//            }
//        });
//
//
//    }


}