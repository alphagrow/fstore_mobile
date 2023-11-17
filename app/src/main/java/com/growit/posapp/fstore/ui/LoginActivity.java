package com.growit.posapp.fstore.ui;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.growit.posapp.fstore.MainActivity;
import com.growit.posapp.fstore.R;
import com.growit.posapp.fstore.db.DatabaseClient;
import com.growit.posapp.fstore.tables.Customer;
import com.growit.posapp.fstore.tables.GST;
import com.growit.posapp.fstore.utils.ApiConstants;
import com.growit.posapp.fstore.utils.SessionManagement;
import com.growit.posapp.fstore.utils.Utility;
import com.growit.posapp.fstore.volley.VolleyCallback;
import com.growit.posapp.fstore.volley.VolleyRequestHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    private Button loginBtn;
    EditText userStoreIDView, passwordView;
    private String storeID;
    private String password;
    ProgressDialog progressDialog;
    SessionManagement sm;
    TextView resetBtn,but_change_lang;
    Customer customerData = null;
    boolean isAllFieldsChecked = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.setStatusBarColor(getResources().getColor(R.color.white));
        setContentView(R.layout.activity_login);
        sm = new SessionManagement(this);
//        String jsonFileString = Utility.loadJSONFromAsset(getApplicationContext());
//        try {
//            JSONArray jsonArray = new JSONArray(jsonFileString);
//            for (int i = 0; i < jsonArray.length(); i++) {
//                JSONObject object = jsonArray.getJSONObject(i);
//                String state = object.getString("state");
//                Log.i("data", "\n" + state);
//            }
//        } catch (JSONException e) {
//            throw new RuntimeException(e);
//        }
        initViews();
    }


    private void initViews() {
        userStoreIDView = findViewById(R.id.et_storeIDView);
        passwordView = findViewById(R.id.et_password);
        resetBtn = findViewById(R.id.reset_btn);
        but_change_lang = findViewById(R.id.but_change_lang);
//        userStoreIDView.setText("admin");
//        passwordView.setText("admin");
        loadLocale();
        loginBtn = findViewById(R.id.loginBtn);
        progressDialog = new ProgressDialog(LoginActivity.this);
        progressDialog.setMax(100);
        progressDialog.setMessage("Please wait while a moment...");
        progressDialog.setTitle("Authenticating");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        resetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentLogin = new Intent(LoginActivity.this, ResetPasswordActivity.class);
                startActivity(intentLogin);
                finish();
            }
        });
        loginBtn.setOnClickListener(v -> {
            storeID = userStoreIDView.getText().toString().trim();
            password = passwordView.getText().toString().trim();
            if (!storeID.isEmpty() && !password.isEmpty()) {
                progressDialog.show();
                if (!Utility.isNetworkAvailable(LoginActivity.this)) {
                    Toast.makeText(LoginActivity.this, R.string.NETWORK_GONE, Toast.LENGTH_SHORT).show();
                    return;
                }
                isAllFieldsChecked= CheckAllFields();
                if (isAllFieldsChecked) {
                    loginRequest();
                }
            } else {
                progressDialog.cancel();
                Toast.makeText(LoginActivity.this, R.string.USER_ID_PASSWORD, Toast.LENGTH_SHORT).show();
            }
        });

        but_change_lang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeLanguage();
            }
        });
    }


    private void loginRequest() {
        Map<String, String> params = new HashMap<>();
        params.put("login", storeID);
        params.put("password", password);
        new VolleyRequestHandler(this, params).createRequest(ApiConstants.SIGN_IN, new VolleyCallback() {

            @Override
            public void onSuccess(Object result) throws JSONException {
                JSONObject obj = new JSONObject(result.toString());
                Log.i("obj", obj.toString());
                progressDialog.cancel();
                String status = obj.optString("status");
                int statusCode = obj.optInt("statuscode");
                String name = obj.optString("name");
                String token = obj.optString("token");
                String str_message = obj.optString("message");

                if(str_message.equals("Error: Access Denied")){

                }else {

                }
                int id = obj.optInt("user_id");
                if (statusCode == 200 && status.equalsIgnoreCase("success")) {
                    sm.createLoginSession(true, name, storeID, password, token);
                    sm.saveUserId(id);
                    Toast.makeText(LoginActivity.this, R.string.LOGIN_SUCCESS, Toast.LENGTH_SHORT).show();

                    getGSTRequest();
                    getCustomerRequest();

                }else {
                    Toast.makeText(LoginActivity.this, R.string.USER_ID_PASSWORD_INCORRECT, Toast.LENGTH_SHORT).show();

                }
            }

            @Override
            public void onError(String result) throws Exception {
                Log.v("Response", result.toString());
                progressDialog.cancel();
                Toast.makeText(LoginActivity.this, R.string.JSONDATA_NULL, Toast.LENGTH_SHORT).show();

            }
        });


    }

    private void getCustomerRequest() {
        SessionManagement sm = new SessionManagement(LoginActivity.this);
        RequestQueue queue = Volley.newRequestQueue(LoginActivity.this);
        String url = ApiConstants.BASE_URL + ApiConstants.GET_ALL_CUSTOMER + "user_id=" + sm.getUserID() + "&" + "token=" + sm.getJWTToken();
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.v("Response", url);
                JSONObject obj = null;
                try {
                    obj = new JSONObject(response.toString());
                    int statusCode = obj.optInt("statuscode");
                    String status = obj.optString("status");

                    if (statusCode == 200 && status.equalsIgnoreCase("success")) {
                        JSONArray jsonArray = obj.getJSONArray("data");
                        List<Customer> customerList = new ArrayList<>();
                        if (jsonArray.length() > 0) {
                            for (int i = 0; i < jsonArray.length(); i++) {
                                customerData = new Customer();
                                JSONObject data = jsonArray.getJSONObject(i);
                                int customerID = data.optInt("customer_id");
                                String name = data.optString("name");
                                String mobile = data.optString("mobile");
                                customerData.setCustomer_id(customerID);
                                customerData.setName(name);
                                customerData.setMobile(mobile);
                                String email = data.optString("email");
                                customerData.setEmail(email);
                                String zip = data.optString("zip");
                                customerData.setZipcode(zip);
                                String stateid = data.optString("state");
                                customerData.setState(stateid);
                                String districtid = data.optString("district");
                                customerData.setDistrict(districtid);
                                String taluka = data.optString("taluka");
                                customerData.setTaluka(taluka);
                                String street = data.optString("street");
                                customerData.setStreet(street);
                                String customer_type = data.optString("customer_type");
                                customerData.setCustomer_type(customer_type);
                                if(data.optString("gst_no")!=null) {
                                    String gstNo = data.optString("gst_no");
                                    customerData.setGst_no(gstNo);
                                }
                                String landSize = data.optString("land_size");
                                customerData.setLand_size(landSize);
                                String discounts = data.optString("discounts");
                                String disc=discounts.replace("%","");
                                customerData.setDiscounts(disc);
                                if(!customer_type.equalsIgnoreCase("4")){
                                    customerList.add(customerData);
                                }

                            }
                            AsyncTask.execute(() -> {
                                DatabaseClient.getInstance(LoginActivity.this).getAppDatabase()
                                        .customerDao()
                                        .insertAllCustomers(customerList);
                            });
                        }
                        Intent intentLogin = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(intentLogin);
                        finish();
                    }
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
        }, error -> Toast.makeText(LoginActivity.this, R.string.JSONDATA_NULL, Toast.LENGTH_SHORT).show());
        queue.add(jsonObjectRequest);
    }


    private void getGSTRequest() {
        SessionManagement sm = new SessionManagement(LoginActivity.this);
        RequestQueue queue = Volley.newRequestQueue(LoginActivity.this);
        String url = ApiConstants.BASE_URL + ApiConstants.GST_API + "user_id=" + sm.getUserID() + "&" + "token=" + sm.getJWTToken();
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.v("Response", response.toString());
                JSONObject obj = null;
                try {
                    obj = new JSONObject(response.toString());
                    int statusCode = obj.optInt("statuscode");
                    String status = obj.optString("status");
                    if (statusCode == 200 && status.equalsIgnoreCase("success")) {
                        JSONArray jsonArray = obj.getJSONArray("gst_tax_list");
                        List<GST> gstList = new ArrayList<>();
                        if (jsonArray.length() > 0) {
                            for (int i = 0; i < jsonArray.length(); i++) {
                                GST gstData = new GST();
                                JSONObject data = jsonArray.getJSONObject(i);
                                int ID = data.optInt("id");
                                String name = data.optString("name");
                                String value1 = name.replace("GST", "").trim();
                                String value2 = value1.replace("%", "");
                                gstData.setGstId(ID);
                                try {
                                    gstData.setGstValue(Integer.parseInt(value2));
                                } catch (NumberFormatException e) {

                                }
                                gstList.add(gstData);
                            }
                            AsyncTask.execute(() -> {
                                DatabaseClient.getInstance(LoginActivity.this).getAppDatabase()
                                        .gstDao()
                                        .insertGST(gstList);
                            });
                        }
                    }
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
        }, error -> Toast.makeText(LoginActivity.this, R.string.JSONDATA_NULL, Toast.LENGTH_SHORT).show());
        queue.add(jsonObjectRequest);
    }
    private void changeLanguage(){
        final String languages[] = {"English","Hindi","Marathi"};
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(this);
        mBuilder.setTitle("Choose Language");
        mBuilder.setSingleChoiceItems(languages, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(which == 0){
                    setLocale("en");
                    recreate();
                }else  if(which == 1){
                    setLocale("hi");
                    recreate();

                }else if(which == 2){
                    setLocale("mr");
                    recreate();
                }
            }
        });
        mBuilder.create();
        mBuilder.show();

    }
    private void setLocale(String language){
        Locale locale = new Locale(language);
        Locale.setDefault(locale);
        Configuration configuration = new Configuration();
        configuration.locale = locale;
        getBaseContext().getResources().updateConfiguration(configuration,getBaseContext().getResources().getDisplayMetrics());
        SharedPreferences.Editor editor = getSharedPreferences("Settings",MODE_PRIVATE).edit();
        editor.putString("app_lang",language);
        editor.apply();

    }
    private void loadLocale(){
        SharedPreferences preferences = getSharedPreferences("Settings",MODE_PRIVATE);
        String language = preferences.getString("app_lang","");
        setLocale(language);

    }

    private boolean CheckAllFields() {
        if (userStoreIDView.length() ==0) {
            userStoreIDView.setError("This field is required");
            Toast.makeText(LoginActivity.this, R.string.store_id, Toast.LENGTH_SHORT).show();
            return false;
        }

        if (passwordView.length() == 0) {
            passwordView.setError("Password is required");
            Toast.makeText(LoginActivity.this, R.string.Password, Toast.LENGTH_SHORT).show();
            return false;
        }
        // after all validation return true.
        return true;
    }

}