package com.growit.posapp.fstore.ui.fragments.CustomerManagement;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.growit.posapp.fstore.MainActivity;
import com.growit.posapp.fstore.R;
import com.growit.posapp.fstore.adapters.ConfigurationAdapter;
import com.growit.posapp.fstore.adapters.CustomSpinnerAdapter;
import com.growit.posapp.fstore.db.DatabaseClient;
import com.growit.posapp.fstore.interfaces.ApiResponseListener;
import com.growit.posapp.fstore.model.ConfigurationModel;
import com.growit.posapp.fstore.tables.Customer;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AddCustomerFragment extends Fragment implements
        ApiResponseListener,
        View.OnClickListener {

    private TextView addBtn;
    private EditText nameEditText, emailEditText, mobileEditText, addressEditText, et_pincode, gst_no_edit, land_size;
    private ProgressBar progressBar;

    Spinner stateSpinner, citySpinner, talukaSpinner, cstTypeSpinner;
    List<StateModel> stateNames = new ArrayList<>();
    List<StateModel> customer_type = new ArrayList<>();
    List<StateModel> districtNames = new ArrayList<>();
    List<StateModel> talukaNames = new ArrayList<>();

    private String str_land_size = "", str_gst_no = "", nameStr = "", mobileStr = "", emailStr = "", districtStr = "",cusTomerType ="", streetStr = "", zipStr = "", stateStr = "", talukaStr = "";
    int customerID = 0;
    String customerLineDiscount="";

    private String[] cstTpeArray = {"Farmer", "Franchisee", "Dealer"};
    private String[] cstTpeIDArray = {"1", "2", "3"};
    boolean isMobileExist = false;
    ProgressDialog progressDialog;
    boolean isAllFieldsChecked = false;
    ImageView backBtn;
    public static AddCustomerFragment newInstance() {
        return new AddCustomerFragment();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.add_customer_fragment, parent, false);
        initViews(view);
        /* Render State DropDown List  */
        if (Utility.isNetworkAvailable(getContext())) {
//            getCustomerTypesList();
            getStateData();
        } else {
            Toast.makeText(getContext(), R.string.NETWORK_GONE, Toast.LENGTH_SHORT).show();

        }


        return view;
    }

    private void showDialoge(String message) {
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMax(100);
        progressDialog.setMessage(message);
        progressDialog.setTitle("FStore");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.show();
    }

    private void dismissDialoge() {
        progressDialog.cancel();
    }

    private void initViews(View view) {
        nameEditText = view.findViewById(R.id.et_username);
        emailEditText = view.findViewById(R.id.et_useremail);
        mobileEditText = view.findViewById(R.id.et_usermobile);
        addressEditText = view.findViewById(R.id.et_useraddress);
        et_pincode = view.findViewById(R.id.et_pincode);
        talukaSpinner = view.findViewById(R.id.talukaSpinner);
//        cstTypeSpinner = view.findViewById(R.id.cstTypeSpinner);
        land_size = view.findViewById(R.id.land_size);
        gst_no_edit = view.findViewById(R.id.gst_edit);

        stateSpinner = view.findViewById(R.id.stateSpinner);
        citySpinner = view.findViewById(R.id.citySpinner);
        backBtn = view.findViewById(R.id.backBtn);
        progressBar = new ProgressBar(getActivity());
        progressBar.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        addBtn = view.findViewById(R.id.register_btn);
        addBtn.setOnClickListener(this);
        StateModel st=new StateModel();
        st.setName("--Select District--");
        districtNames.add(st);

        StateModel st2=new StateModel();
        st2.setName("--Select Taluka--");
        talukaNames.add(st2);
        if(getContext()!=null) {
            CustomSpinnerAdapter adapter = new CustomSpinnerAdapter(getContext(), districtNames);
            citySpinner.setAdapter(adapter);
        }
        if(getContext()!=null) {
            CustomSpinnerAdapter adapter = new CustomSpinnerAdapter(getContext(), talukaNames);
            talukaSpinner.setAdapter(adapter);
        }
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), MainActivity.class));
                getActivity().finish();
            }
        });
//        cstTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                if (position != 0) {
//                    cusTomerType = customer_type.get(position).getId() + "";
//
//                }
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> parent) {
//
//            }
//        });
        citySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position != 0) {
                    districtStr = districtNames.get(position).getId() + "";
                    if (districtStr != null) {
                        if (!Utility.isNetworkAvailable(getActivity())) {
                            Toast.makeText(getContext(), R.string.NETWORK_GONE, Toast.LENGTH_SHORT).show();                            return;
                        }
                        getTalukaData();
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        stateSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position != 0) {
                    stateStr = stateNames.get(position).getId() + "";
                    if (stateStr != null) {
                        if (!Utility.isNetworkAvailable(getActivity())) {
                            Toast.makeText(getContext(), R.string.NETWORK_GONE, Toast.LENGTH_SHORT).show();
                            return;
                        }
                        getDistrictData();
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        talukaSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position != 0) {
                    talukaStr = talukaNames.get(position).getId() + "";
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    private void resetFields() {
        land_size.setText("");
        gst_no_edit.setText("");
        nameEditText.setText("");
        mobileEditText.setText("");
        addressEditText.setText("");
        et_pincode.setText("");
        emailEditText.setText("");
        stateSpinner.setSelection(0);
        citySpinner.setSelection(0);
        talukaSpinner.setSelection(0);
    }


    @Override
    public void onResponse(String response, int requestCode) {
        Log.d("Response:=", response);

    }

    @Override
    public void onError(String error, int requestCode) {
        Log.e("error:=", error);
    }

    /**
     * Called when a view has been clicked.
     *
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.register_btn) {
            str_gst_no = gst_no_edit.getText().toString();
            nameStr = nameEditText.getText().toString();
            mobileStr = mobileEditText.getText().toString();
            emailStr = emailEditText.getText().toString();
            zipStr = et_pincode.getText().toString();
            streetStr = addressEditText.getText().toString();
            str_land_size = land_size.getText().toString();
//
            AsyncTask.execute(() -> {
                isMobileExist = DatabaseClient.getInstance(getActivity()).getAppDatabase()
                        .customerDao()
                        .isDataExist(mobileStr);

            });

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            if (isMobileExist) {
                Toast.makeText(getActivity(), R.string.MOBILE_NUMBER, Toast.LENGTH_SHORT).show();                return;
            }
//            if (mobileStr.length() == 0 || nameStr.length() == 0 || stateStr.length() == 0 || districtStr.length() == 0 || talukaStr.length() == 0 || zipStr.length() == 0) {
//                Toast.makeText(getActivity(), "Please add mandatory fields.", Toast.LENGTH_SHORT).show();
//                return;
//            }

            isAllFieldsChecked= CheckAllFields();
            if (stateStr.length() == 0) {
                Toast.makeText(getActivity(), R.string.Select_state, Toast.LENGTH_SHORT).show();
                return;
            }
            if(districtStr.length() == 0){
                Toast.makeText(getActivity(), R.string.Select_district, Toast.LENGTH_SHORT).show();
                return;
            }
            if(talukaStr.length() == 0){
                Toast.makeText(getActivity(), R.string.Select_taluka, Toast.LENGTH_SHORT).show();
                return;
            }

//            if(!cusTomerType.equalsIgnoreCase("1")&&!Utility.isValidGSTNo(str_gst_no)){
//                Toast.makeText(getActivity(), "Enter valid GST No.", Toast.LENGTH_SHORT).show();
//                return;
//            }
            if (!Utility.isNetworkAvailable(getActivity())) {
                Toast.makeText(getContext(), R.string.NETWORK_GONE, Toast.LENGTH_SHORT).show();
                return;
            }

            if (isAllFieldsChecked) {
                registrationRequest();
            }
        }
    }

    private void getStateData() {
        showDialoge("Please wait while a configuring State...");
        Map<String, String> params = new HashMap<>();
        params.put("country_id", ApiConstants.COUNTRY_ID);
        new VolleyRequestHandler(getActivity(), params).createRequest(ApiConstants.GET_STATES, new VolleyCallback() {
            private String message = "Registration failed!!";

            @Override
            public void onSuccess(Object result) throws JSONException {
                Log.v("Response", result.toString());
                stateNames = new ArrayList<>();
                JSONObject obj = new JSONObject(result.toString());
                int statusCode = obj.optInt("statuscode");
                String status = obj.optString("status");
                if (statusCode == 200 && status.equalsIgnoreCase("success")) {
                    dismissDialoge();
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
                    if(getContext()!=null) {
                        CustomSpinnerAdapter adapter = new CustomSpinnerAdapter(getContext(), stateNames);
                        stateSpinner.setAdapter(adapter);
                    }
                }
            }

            @Override
            public void onError(String result) {
                Log.v("Response", result.toString());
                dismissDialoge();
                Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
            }
        });
    }

//    private void getCustomerTypesList() {
//        SessionManagement sm = new SessionManagement(getActivity());
//        RequestQueue queue = Volley.newRequestQueue(getActivity());
//        //  String url = ApiConstants.BASE_URL + ApiConstants.GET_CUSTOMER_DISCOUNT_LIST;
//
//        String url = ApiConstants.BASE_URL + ApiConstants.GET_CUSTOMER_DISCOUNT_LIST + "user_id=" + sm.getUserID() + "&" + "token=" + sm.getJWTToken();
//        //    Utility.showDialoge("Please wait while a moment...", getActivity());
//        Log.d("ALL_CROPS_url",url);
//        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
//            @Override
//            public void onResponse(JSONObject response) {
//                Log.v("Response", response.toString());
//                JSONObject obj = null;
//                try {
//                    obj = new JSONObject(response.toString());
//                    int statusCode = obj.optInt("statuscode");
//                    String status = obj.optString("status");
//
//                    if (statusCode == 200 && status.equalsIgnoreCase("success")) {
//                        dismissDialoge();
//                        JSONArray jsonArray = obj.getJSONArray("customer_discounts");
//                        StateModel stateModel = new StateModel();
//                        stateModel.setId(-1);
//                        stateModel.setName("--Select Customer Types--");
//                        customer_type.add(stateModel);
//                        for (int i = 0; i < jsonArray.length(); i++) {
//                            stateModel = new StateModel();
//                            JSONObject data = jsonArray.getJSONObject(i);
//                            int id = data.optInt("id");
//                            String name = data.optString("name");
//                            stateModel.setId(id);
//                            stateModel.setName(name);
//                            customer_type.add(stateModel);
//                        }
//                        if(getContext()!=null) {
//                            CustomSpinnerAdapter adapter = new CustomSpinnerAdapter(getContext(), customer_type);
//                            cstTypeSpinner.setAdapter(adapter);
//                        }
//                    }
//                } catch (JSONException e) {
//                    throw new RuntimeException(e);
//                }
//            }
//        }, error -> Toast.makeText(getActivity(), R.string.JSONDATA_NULL, Toast.LENGTH_SHORT).show());
//        queue.add(jsonObjectRequest);
//    }


    private void getDistrictData() {
        showDialoge("Please wait while a configuring District...");
        Map<String, String> params = new HashMap<>();
        params.put("states_id", stateStr);
        new VolleyRequestHandler(getActivity(), params).createRequest(ApiConstants.GET_DISTRICT, new VolleyCallback() {
            private String message = "Registration failed!!";

            @Override
            public void onSuccess(Object result) throws JSONException {
                Log.v("Response", result.toString());
                districtNames = new ArrayList<>();
                JSONObject obj = new JSONObject(result.toString());
                int statusCode = obj.optInt("statuscode");
                String status = obj.optString("status");
                if (statusCode == 200 && status.equalsIgnoreCase("success")) {
                    dismissDialoge();
                    JSONArray jsonArray = obj.getJSONArray("data");
                    StateModel stateModel = new StateModel();
                    stateModel.setId(-1);
                    stateModel.setName("--Select District--");
                    districtNames.add(stateModel);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        stateModel = new StateModel();
                        JSONObject data = jsonArray.getJSONObject(i);
                        int id = data.optInt("id");
                        String name = data.optString("name");
                        stateModel.setId(id);
                        stateModel.setName(name);
                        districtNames.add(stateModel);
                    }
                    if(getContext()!=null) {
                        CustomSpinnerAdapter adapter = new CustomSpinnerAdapter(getContext(), districtNames);
                        citySpinner.setAdapter(adapter);
                    }
                }
            }

            @Override
            public void onError(String result) {
                Log.v("Response", result.toString());
                dismissDialoge();
                Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getTalukaData() {
        showDialoge("Please wait while a configuring Taluka...");
        Map<String, String> params = new HashMap<>();
        params.put("district_id", districtStr);
        new VolleyRequestHandler(getActivity(), params).createRequest(ApiConstants.GET_TALUKA, new VolleyCallback() {
            private String message = "Registration failed!!";

            @Override
            public void onSuccess(Object result) throws JSONException {
                Log.v("Response", result.toString());
                talukaNames = new ArrayList<>();
                JSONObject obj = new JSONObject(result.toString());
                int statusCode = obj.optInt("statuscode");
                String status = obj.optString("status");
                if (statusCode == 200 && status.equalsIgnoreCase("success")) {
                    dismissDialoge();
                    JSONArray jsonArray = obj.getJSONArray("data");
                    StateModel stateModel = new StateModel();
                    stateModel.setId(-1);
                    stateModel.setName("--Select Taluka--");
                    talukaNames.add(stateModel);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        stateModel = new StateModel();
                        JSONObject data = jsonArray.getJSONObject(i);
                        int id = data.optInt("id");
                        String name = data.optString("name");
                        stateModel.setId(id);
                        stateModel.setName(name);
                        talukaNames.add(stateModel);
                    }
                    if(getContext()!=null) {
                        CustomSpinnerAdapter adapter = new CustomSpinnerAdapter(getContext(), talukaNames);
                        talukaSpinner.setAdapter(adapter);
                    }
                }
            }

            @Override
            public void onError(String result) {
                dismissDialoge();
                Log.v("Response", result.toString());
                Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void registrationRequest() {
        SessionManagement sm = new SessionManagement(getActivity());
        Map<String, String> params = new HashMap<>();
        params.put("user_id", sm.getUserID() + "");
        params.put("token", sm.getJWTToken());
        params.put("name", nameStr);
        params.put("mobile", mobileStr);
        params.put("email", emailStr);
        params.put("state_id", stateStr);
        params.put("district_id", districtStr);
        params.put("taluka_id", talukaStr);
        params.put("street", streetStr);
        params.put("country_id", ApiConstants.COUNTRY_ID);
        params.put("zip", zipStr);
//        params.put("customer_type", cusTomerType);
//        if(!cusTomerType.equals("1")) {
//            params.put("vat", str_gst_no);
//        }
        params.put("land_size", str_land_size);
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMax(100);
        progressDialog.setMessage("Please wait while a moment...");
        progressDialog.setTitle("Registering");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.show();
        Log.v("add", String.valueOf(params));
        new VolleyRequestHandler(getActivity(), params).createRequest(ApiConstants.ADD_CUSTOMER, new VolleyCallback() {
            private String message = " failed!!";

            @Override
            public void onSuccess(Object result) throws JSONException {
                Log.v("Response", result.toString());
                JSONObject obj = new JSONObject(result.toString());
                int statusCode = obj.optInt("statuscode");
                message = obj.optString("status");
                customerID = obj.optInt("customer_id");
                String error_message = obj.optString("error_message");
                String discounts = obj.optString("discounts");
                customerLineDiscount=discounts.replace("%","");
                if (statusCode == 200 && message.equalsIgnoreCase("success")) {
                    int visibility = (progressBar.getVisibility() == View.GONE) ? View.VISIBLE : View.GONE;
                    progressBar.setVisibility(visibility);
                    progressDialog.cancel();
                    SaveTask st = new SaveTask();
                    st.execute();
                    resetFields();
                    Toast.makeText(getActivity(), R.string.cust_create_success, Toast.LENGTH_SHORT).show();

                }else {
                    Utility.dismissDialoge();
                    Toast.makeText(getActivity(), error_message, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(String result) throws Exception {
                progressDialog.cancel();
                Log.v("Response", result);
                Toast.makeText(getActivity(), R.string.JSONDATA_NULL, Toast.LENGTH_SHORT).show();
            }
        });
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
    }


    class SaveTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            if (customerID > 0) {
                Customer customer = new Customer();
                customer.setCustomer_id(customerID);
                customer.setName(nameStr);
                customer.setMobile(mobileStr);
                customer.setEmail(emailStr);
                customer.setZipcode(zipStr);
                customer.setState(stateStr);
                customer.setDistrict(districtStr);
                customer.setTaluka(talukaStr);
                customer.setStreet(streetStr);
                customer.setGst_no(str_gst_no);
                customer.setLand_size(str_land_size);
//                customer.setCustomer_type(cusTomerType);
                customer.setDiscounts(customerLineDiscount);
                DatabaseClient.getInstance(getActivity()).getAppDatabase()
                        .customerDao()
                        .insert(customer);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            // Toast.makeText(getActivity(), "Data has been saved.", Toast.LENGTH_SHORT).show();
            super.onPostExecute(aVoid);

        }
    }

    private boolean CheckAllFields() {
        if (mobileEditText.length()!=10) {
            mobileEditText.setError("Enter a 10-digit mobile number");
            Toast.makeText(getActivity(), R.string.customer_mobile, Toast.LENGTH_SHORT).show();

            return false;
        }


        if (land_size.length() == 0) {
            land_size.setError("Land size is required");
            Toast.makeText(getActivity(), R.string.land_size, Toast.LENGTH_SHORT).show();
            return false;
        }
        if (nameEditText.length() == 0) {
            nameEditText.setError("This field is required");
            Toast.makeText(getActivity(), R.string.CUSTOMER_NAME, Toast.LENGTH_SHORT).show();

            return false;
        }
        if (addressEditText.length() == 0) {
            addressEditText.setError("Address is required");
            Toast.makeText(getActivity(), R.string.ADDRESS, Toast.LENGTH_SHORT).show();
            return false;
        }
        if (et_pincode.length()!=6) {
            et_pincode.setError("Enter a 6-digit Pin code");
            Toast.makeText(getActivity(), R.string.PIN_CODE, Toast.LENGTH_SHORT).show();
            return false;
        }



        // after all validation return true.
        return true;
    }

    public  boolean isValidGSTNo(String str) {

        String regex = "^[0-9]{2}[A-Z]{5}[0-9]{4}"
                + "[A-Z]{1}[1-9A-Z]{1}"
                + "Z[0-9A-Z]{1}$";

        Pattern p = Pattern.compile(regex);
        if (str == null) {
            return false;
        }
        Matcher m = p.matcher(str);
        return m.matches();
    }

}
