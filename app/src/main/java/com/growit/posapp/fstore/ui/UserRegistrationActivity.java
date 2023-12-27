package com.growit.posapp.fstore.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Toast;

import com.growit.posapp.fstore.MainActivity;
import com.growit.posapp.fstore.R;
import com.growit.posapp.fstore.adapters.CustomSpinnerAdapter;
import com.growit.posapp.fstore.databinding.ActivityUserRegistrstionBinding;
import com.growit.posapp.fstore.model.StateModel;
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
import java.util.Map;

public class UserRegistrationActivity extends AppCompatActivity {
    ActivityUserRegistrstionBinding binding;
    List<StateModel> stateNames = new ArrayList<>();
    List<StateModel> districtNames = new ArrayList<>();
    List<StateModel> talukaNames = new ArrayList<>();
    private String login_id = "", str_password = "", str_comp_name = "", str_city = "", str_phone = "", str_website = "", str_insectLicNo = "", str_seedLicNo = "", str_fertLicNo = "", str_gst_no = "", nameStr = "", mobileStr = "", emailStr = "", districtStr = "", streetStr = "", zipStr = "", stateStr = "", talukaStr = "";
    boolean isAllFieldsChecked = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //  setContentView(R.layout.activity_user_registrstion);
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.setStatusBarColor(getResources().getColor(R.color.colorPrimaryVariant));
        binding = DataBindingUtil.setContentView(UserRegistrationActivity.this, R.layout.activity_user_registrstion);
        init();
    }

    private void init() {
        if (Utility.isNetworkAvailable(UserRegistrationActivity.this)) {
            getStateData();
        } else {
            Toast.makeText(UserRegistrationActivity.this, R.string.NETWORK_GONE, Toast.LENGTH_SHORT).show();
        }
        StateModel st = new StateModel();
        st.setName("--Select District--");
        districtNames.add(st);

        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(UserRegistrationActivity.this, LoginActivity.class));
                finish();
            }
        });



        binding.stateSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position != 0) {
                    stateStr = stateNames.get(position).getId() + "";
                    if (stateStr != null) {
                        if (!Utility.isNetworkAvailable(UserRegistrationActivity.this)) {
                            Toast.makeText(UserRegistrationActivity.this, R.string.NETWORK_GONE, Toast.LENGTH_SHORT).show();
                            return;
                        }
//                        getDistrictData();
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        binding.submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                str_gst_no = binding.etGstNo.getText().toString();
                nameStr = binding.etUsername.getText().toString();
                mobileStr = binding.etMobile.getText().toString();
                emailStr = binding.etUseremail.getText().toString();
                zipStr = binding.etPincode.getText().toString();
                streetStr = binding.etUseraddress.getText().toString();
                login_id = binding.edUserId.getText().toString();
                str_password = binding.etPassword.getText().toString();
                str_comp_name = binding.etCompanyName.getText().toString();
                str_city = binding.etCity.getText().toString();
                str_phone = binding.etPhone.getText().toString();
                str_website = binding.etWebsite.getText().toString();
                str_insectLicNo = binding.etInsectLicNo.getText().toString();
                str_fertLicNo = binding.etFertLicNo.getText().toString();
                str_seedLicNo = binding.etSeedLicNo.getText().toString();

                if (stateStr.length() == 0) {
                    Toast.makeText(UserRegistrationActivity.this, R.string.Select_state, Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!Utility.isNetworkAvailable(UserRegistrationActivity.this)) {
                    Toast.makeText(UserRegistrationActivity.this, R.string.NETWORK_GONE, Toast.LENGTH_SHORT).show();
                    return;
                }


                isAllFieldsChecked = CheckAllFields();
                if (isAllFieldsChecked) {
                    UserRegistration();

                }


            }
        });

    }

    private void getStateData() {
        Utility.showDialoge("Please wait while a configuring State...", UserRegistrationActivity.this);
        Map<String, String> params = new HashMap<>();
        params.put("country_id", ApiConstants.COUNTRY_ID);
        new VolleyRequestHandler(UserRegistrationActivity.this, params).createRequest(ApiConstants.GET_STATES, new VolleyCallback() {
            private String message = "Registration failed!!";

            @Override
            public void onSuccess(Object result) throws JSONException {
                Log.v("Response", result.toString());
                stateNames = new ArrayList<>();
                JSONObject obj = new JSONObject(result.toString());
                int statusCode = obj.optInt("statuscode");
                String status = obj.optString("status");
                if (statusCode == 200 && status.equalsIgnoreCase("success")) {
                    Utility.dismissDialoge();
                    JSONArray jsonArray = obj.getJSONArray("data");
                    StateModel stateModel = new StateModel();
                    stateModel.setId(-1);
                    stateModel.setName("--Select State--");
                    stateNames.add(stateModel);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        stateModel = new StateModel();
                        JSONObject data = jsonArray.getJSONObject(i);
                        int id = data.optInt("id");
                        String name = data.optString("name");
                        stateModel.setId(id);
                        stateModel.setName(name);
                        stateNames.add(stateModel);
                    }
                    if (UserRegistrationActivity.this != null) {
                        CustomSpinnerAdapter adapter = new CustomSpinnerAdapter(UserRegistrationActivity.this, stateNames);
                        binding.stateSpinner.setAdapter(adapter);
                    }
                }
            }

            @Override
            public void onError(String result) {
                Log.v("Response", result.toString());
                Utility.dismissDialoge();
                Toast.makeText(UserRegistrationActivity.this, message, Toast.LENGTH_SHORT).show();
            }
        });
    }


    private boolean CheckAllFields() {
        if (binding.etUsername.length() == 0) {
            binding.etUsername.setError("This field is required");
            Toast.makeText(UserRegistrationActivity.this, R.string.CUSTOMER_NAME, Toast.LENGTH_SHORT).show();

            return false;
        }
        if (binding.edUserId.length() == 0) {
            binding.edUserId.setError("This field is required");
            Toast.makeText(UserRegistrationActivity.this, "Enter the User Id", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (binding.etPassword.length() == 0) {
            binding.etPassword.setError("This field is required");
            Toast.makeText(UserRegistrationActivity.this, "Enter the Password", Toast.LENGTH_SHORT).show();

            return false;
        }
        if (binding.etCompanyName.length() == 0) {
            binding.etCompanyName.setError("This field is required");
            Toast.makeText(UserRegistrationActivity.this, "Enter the company Name", Toast.LENGTH_SHORT).show();

            return false;
        }
        if (binding.etPhone.length() == 0) {
            binding.etPhone.setError("This field is required");
            Toast.makeText(UserRegistrationActivity.this, "Enter the Phone Number", Toast.LENGTH_SHORT).show();

            return false;
        }
        if (binding.etCity.length() == 0) {
            binding.etCity.setError("This field is required");
            Toast.makeText(UserRegistrationActivity.this, "Enter the City", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (binding.etWebsite.length() == 0) {
            binding.etWebsite.setError("This field is required");
            Toast.makeText(UserRegistrationActivity.this, "Enter the Website", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (binding.etMobile.length() != 10) {
            binding.etMobile.setError("Enter a 10-digit mobile number");
            Toast.makeText(UserRegistrationActivity.this, R.string.customer_mobile, Toast.LENGTH_SHORT).show();
            return false;
        }


        if (binding.etUseraddress.length() == 0) {
            binding.etUseraddress.setError("Address is required");
            Toast.makeText(UserRegistrationActivity.this, R.string.ADDRESS, Toast.LENGTH_SHORT).show();
            return false;
        }
        if (binding.etPincode.length() != 6) {
            binding.etPincode.setError("Enter a 6-digit Pin code");
            Toast.makeText(UserRegistrationActivity.this, R.string.PIN_CODE, Toast.LENGTH_SHORT).show();
            return false;
        }
        if (binding.etGstNo.length() == 0) {
            binding.etGstNo.setError("This field is required");
            Toast.makeText(UserRegistrationActivity.this, "Enter valid GST No.", Toast.LENGTH_SHORT).show();
            return false;
        }


        // after all validation return true.
        return true;
    }

    private void UserRegistration() {
        SessionManagement sm = new SessionManagement(UserRegistrationActivity.this);
        Map<String, String> params = new HashMap<>();
        // params.put("user_id", sm.getUserID() + "");
        params.put("name", nameStr);
        params.put("login", login_id);
        //  params.put("company_logo", "");
        params.put("password", str_password);
        params.put("company_name", str_comp_name);
        params.put("street", streetStr);
        params.put("country_id", ApiConstants.COUNTRY_ID);
        params.put("state_id", stateStr);
        params.put("city", str_city);
        params.put("zip", zipStr);
        params.put("phone", str_phone);
        params.put("mobile", mobileStr);
        params.put("email", emailStr);
        params.put("website", str_website);
        params.put("insect_lic_no", str_insectLicNo);
        params.put("fert_lic_no", str_fertLicNo);
        params.put("seed_lic_no", str_seedLicNo);
        params.put("vat", str_gst_no);

        Utility.showDialoge("", UserRegistrationActivity.this);
        Log.v("add", String.valueOf(params));
        new VolleyRequestHandler(UserRegistrationActivity.this, params).createRequest(ApiConstants.ADD_COMPANY, new VolleyCallback() {
            private String message = "Registration failed!!";

            @Override
            public void onSuccess(Object result) throws JSONException {
                Log.v("Response", result.toString());
                JSONObject obj = new JSONObject(result.toString());
                int statusCode = obj.optInt("statuscode");
                message = obj.optString("status");
                String error_message = obj.optString("error_message");
                String str_message = obj.optString("message");
                if (statusCode == 200 && message.equalsIgnoreCase("success")) {
                    Utility.dismissDialoge();
                    resetFields();
                    Toast.makeText(UserRegistrationActivity.this, "Company Create", Toast.LENGTH_SHORT).show();
                    Intent intentLogin = new Intent(UserRegistrationActivity.this, LoginActivity.class);
                    startActivity(intentLogin);
                    finish();
                    //  Toast.makeText(UserRegistrationActivity.this, str_message, Toast.LENGTH_SHORT).show();

                } else {
                    Utility.dismissDialoge();
                    Toast.makeText(UserRegistrationActivity.this, error_message, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(String result) throws Exception {
                Utility.dismissDialoge();
                Log.v("Response", result);
                Toast.makeText(UserRegistrationActivity.this, R.string.JSONDATA_NULL, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void resetFields() {
        binding.etUsername.setText("");
        binding.etMobile.setText("");
        binding.etUseraddress.setText("");
        binding.etPincode.setText("");
        binding.etUseremail.setText("");
        binding.etCity.setText("");
        binding.etPassword.setText("");
        binding.edUserId.setText("");
        binding.etCompanyName.setText("");
        binding.etPhone.setText("");
        binding.etGstNo.setText("");
        binding.etInsectLicNo.setText("");
        binding.etFertLicNo.setText("");
        binding.etSeedLicNo.setText("");
        binding.stateSpinner.setSelection(0);

    }

}