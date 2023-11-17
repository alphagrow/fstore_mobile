package com.growit.posapp.fstore.ui.fragments;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


import androidx.fragment.app.Fragment;

import com.growit.posapp.fstore.R;
import com.growit.posapp.fstore.adapters.CustomSpinnerAdapter;
import com.growit.posapp.fstore.db.DatabaseClient;
import com.growit.posapp.fstore.interfaces.ApiResponseListener;
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


public class UpdateCustomerFragment extends Fragment implements ApiResponseListener, View.OnClickListener {


    private TextView addBtn;
    private EditText nameEditText, emailEditText, mobileEditText, addressEditText, et_pincode,customer_type,gst_no_edit,land_size;
    private ProgressBar progressBar;

    Spinner stateSpinner, citySpinner, talukaSpinner;
    List<StateModel> stateNames = new ArrayList<>();
    List<StateModel> districtNames = new ArrayList<>();
    List<StateModel> talukaNames = new ArrayList<>();
    LinearLayout lay_custom_type;
    private String str_land_size = "",nameStr = "", mobileStr = "", emailStr = "", districtStr = "", streetStr = "", zipStr = "", stateStr = "", talukaStr = "";
    boolean isAllFieldsChecked = false;
    public static UpdateCustomerFragment newInstance() {
        return new UpdateCustomerFragment();
    }

    List<Customer> customer = null;
    private int position = 0;
    String cstType="-1";
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.add_customer_fragment, parent, false);
        initViews(view);
        //Creating the ArrayAdapter instance having the bank name list
        if (getArguments() != null) {
            position = getArguments().getInt("position");
            customer = (List<Customer>) getArguments().getSerializable("CustomerOBJ");
            nameEditText.setText(customer.get(position).getName());
            mobileEditText.setText(customer.get(position).getMobile());
            addressEditText.setText(customer.get(position).getStreet());
            et_pincode.setText(customer.get(position).getZipcode());
            emailEditText.setText(customer.get(position).getEmail());
            land_size.setText(customer.get(position).getLand_size());
            cstType=customer.get(position).getCustomer_type();
            getStateData();
            getDistrictData(customer.get(position).getState());
            getTalukaData(customer.get(position).getDistrict());
            customer_type.setEnabled(false);
            gst_no_edit.setEnabled(false);
            if(cstType!=null&&cstType.equalsIgnoreCase("1")) {
                customer_type.setText("Farmer");
                gst_no_edit.setVisibility(View.GONE);
            }else  if(cstType!=null&&cstType.equalsIgnoreCase("2")) {
                gst_no_edit.setVisibility(View.VISIBLE);
                gst_no_edit.setText(customer.get(position).getGst_no());
                customer_type.setText("Franchise");
            }else  if(cstType!=null&&cstType.equalsIgnoreCase("3"))  {
                customer_type.setText("Dealer");
                gst_no_edit.setVisibility(View.VISIBLE);
                gst_no_edit.setText(customer.get(position).getGst_no());
            }
        }


        return view;
    }

    private void initViews(View view) {
        TextView titleTxt = getActivity().findViewById(R.id.titleTxt);
        titleTxt.setText("Update Customer");
        nameEditText = view.findViewById(R.id.et_username);
        emailEditText = view.findViewById(R.id.et_useremail);
        mobileEditText = view.findViewById(R.id.et_usermobile);
        addressEditText = view.findViewById(R.id.et_useraddress);
        et_pincode = view.findViewById(R.id.et_pincode);
        gst_no_edit = view.findViewById(R.id.gst_edit);
        land_size= view.findViewById(R.id.land_size);
        talukaSpinner = view.findViewById(R.id.talukaSpinner);
        stateSpinner = view.findViewById(R.id.stateSpinner);
        citySpinner = view.findViewById(R.id.citySpinner);
        customer_type =view.findViewById(R.id.customer_type);
//        customer_type_text = view.findViewById(R.id.customer_type_text);
//        customer_type_text.setVisibility(View.GONE);
        customer_type.setVisibility(View.VISIBLE);
        lay_custom_type = view.findViewById(R.id.lay_custom_type);
        lay_custom_type.setVisibility(View.GONE);
        land_size.setVisibility(View.GONE);
        progressBar = new ProgressBar(getActivity());
        progressBar.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        addBtn = view.findViewById(R.id.register_btn);
        addBtn.setText("Update");
        addBtn.setOnClickListener(this);


        citySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position != 0) {
                    districtStr = districtNames.get(position).getId() + "";
                    if (districtStr != null) {
                        getTalukaData(districtStr);
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
                        getDistrictData(stateStr);
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
            nameStr = nameEditText.getText().toString();
            mobileStr = mobileEditText.getText().toString();
            emailStr = emailEditText.getText().toString();
            zipStr = et_pincode.getText().toString();
            streetStr = addressEditText.getText().toString();
            str_land_size = land_size.getText().toString();
            isAllFieldsChecked= CheckAllFields();
            if (!Utility.isNetworkAvailable(getActivity())) {
                Toast.makeText(getActivity(), R.string.NETWORK_GONE, Toast.LENGTH_SHORT).show();
                return;
            }
            if (isAllFieldsChecked) {
                updateCustomerRequest(nameStr, mobileStr, emailStr, zipStr, streetStr);
            }

        }
    }

    private void updateCustomerRequest(String nameStr, String mobileStr, String emailStr, String zipStr, String streetStr) {
        SessionManagement sm = new SessionManagement(getActivity());
        Map<String, String> params = new HashMap<>();
        params.put("user_id", sm.getUserID() + "");
        params.put("name", nameStr);
        params.put("mobile", mobileStr);
        params.put("email", emailStr);
        params.put("state_id", stateStr);
        params.put("district_id", districtStr);
        params.put("taluka_id", talukaStr);
        params.put("street", streetStr);
        params.put("country_id", ApiConstants.COUNTRY_ID);
        params.put("zip", zipStr);
        params.put("token", sm.getJWTToken());

        new VolleyRequestHandler(getActivity(), params).putRequest(ApiConstants.UPDATE_CUSTOMER + customer.get(position).getCustomer_id() + "?", new VolleyCallback() {
            private String message = "Update failed!!";

            @Override
            public void onSuccess(Object result) throws JSONException {
                JSONObject obj = new JSONObject(result.toString());
                String status = obj.optString("status");
                message = obj.optString("message");
                if (status.equalsIgnoreCase("success")) {
                    int visibility = (progressBar.getVisibility() == View.GONE) ? View.VISIBLE : View.GONE;
                    progressBar.setVisibility(visibility);
                    SaveTask st = new SaveTask();
                    st.execute();
                    Toast.makeText(getActivity(), R.string.CUSTOMER_UADATE, Toast.LENGTH_SHORT).show();

                    // resetFields();
                }
            }

            @Override
            public void onError(String result) throws Exception {
                Log.v("Response", result.toString());
                Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
            }
        });
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
    }

    private void getStateData() {
        Map<String, String> params = new HashMap<>();
        params.put("country_id", ApiConstants.COUNTRY_ID);
        new VolleyRequestHandler(getActivity(), params).createRequest(ApiConstants.GET_STATES, new VolleyCallback() {
            private String message = "Registration failed!!";

            @Override
            public void onSuccess(Object result) throws JSONException {
                Log.v("Response", result.toString());
                stateNames = new ArrayList<>();
                int spinnerPosition = 0;
                JSONObject obj = new JSONObject(result.toString());
                int statusCode = obj.optInt("statuscode");
                String status = obj.optString("status");
                if (statusCode == 200 && status.equalsIgnoreCase("success")) {
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
                        if (String.valueOf(id).equalsIgnoreCase(customer.get(position).getState())) {
                            spinnerPosition = i + 1;
                        }
                        stateModel.setName(name);
                        stateNames.add(stateModel);

                    }
                    if(getContext()!=null) {
                        CustomSpinnerAdapter adapter = new CustomSpinnerAdapter(getContext(), stateNames);
                        stateSpinner.setAdapter(adapter);
                        stateSpinner.setSelection(spinnerPosition);
                    }
                }
            }

            @Override
            public void onError(String result) {
                Log.v("Response", result.toString());
                Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getDistrictData(String id) {
        Map<String, String> params = new HashMap<>();
        params.put("states_id", id);
        new VolleyRequestHandler(getActivity(), params).createRequest(ApiConstants.GET_DISTRICT, new VolleyCallback() {
            private String message = "Registration failed!!";

            @Override
            public void onSuccess(Object result) throws JSONException {
                Log.v("Response", result.toString());
                districtNames = new ArrayList<>();
                int spinnerPosition = 0;
                JSONObject obj = new JSONObject(result.toString());
                int statusCode = obj.optInt("statuscode");
                String status = obj.optString("status");
                if (statusCode == 200 && status.equalsIgnoreCase("success")) {
                    JSONArray jsonArray = obj.getJSONArray("data");
                    StateModel stateModel = new StateModel();
                    stateModel.setId(-1);
                    stateModel.setName("--Select District--");
                    districtNames.add(stateModel);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        stateModel = new StateModel();
                        JSONObject data = jsonArray.getJSONObject(i);
                        int id = data.optInt("id");
                        if (String.valueOf(id).equalsIgnoreCase(customer.get(position).getDistrict())) {
                            spinnerPosition = i + 1;
                        }
                        String name = data.optString("name");
                        stateModel.setId(id);
                        stateModel.setName(name);
                        districtNames.add(stateModel);
                    }
                    if(getContext()!=null) {
                        CustomSpinnerAdapter adapter = new CustomSpinnerAdapter(getContext(), districtNames);
                        citySpinner.setAdapter(adapter);
                        citySpinner.setSelection(spinnerPosition);
                    }
                }
            }

            @Override
            public void onError(String result) {
                Log.v("Response", result.toString());
                Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getTalukaData(String id) {
        Map<String, String> params = new HashMap<>();
        params.put("district_id", id);
        new VolleyRequestHandler(getActivity(), params).createRequest(ApiConstants.GET_TALUKA, new VolleyCallback() {
            private String message = "Registration failed!!";

            @Override
            public void onSuccess(Object result) throws JSONException {
                Log.v("Response", result.toString());
                int spinnerPosition = 0;
                talukaNames = new ArrayList<>();
                JSONObject obj = new JSONObject(result.toString());
                int statusCode = obj.optInt("statuscode");
                String status = obj.optString("status");
                if (statusCode == 200 && status.equalsIgnoreCase("success")) {
                    JSONArray jsonArray = obj.getJSONArray("data");
                    StateModel stateModel = new StateModel();
                    stateModel.setId(-1);
                    stateModel.setName("--Select Taluka--");
                    talukaNames.add(stateModel);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        stateModel = new StateModel();
                        JSONObject data = jsonArray.getJSONObject(i);
                        int id = data.optInt("id");
                        if (String.valueOf(id).equalsIgnoreCase(customer.get(position).getTaluka())) {
                            spinnerPosition = i + 1;
                        }
                        String name = data.optString("name");
                        stateModel.setId(id);
                        stateModel.setName(name);
                        talukaNames.add(stateModel);
                    }
                    if(getContext()!=null) {
                        CustomSpinnerAdapter adapter = new CustomSpinnerAdapter(getContext(), talukaNames);
                        talukaSpinner.setAdapter(adapter);
                        talukaSpinner.setSelection(spinnerPosition);
                    }
                }
            }

            @Override
            public void onError(String result) {
                Log.v("Response", result.toString());
                Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    class SaveTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            if (customer != null && customer.get(position).getCustomer_id() > 0) {
                Customer cstOBJ = new Customer();
                cstOBJ.setCustomer_id(customer.get(position).getCustomer_id());
                cstOBJ.setName(nameStr);
                cstOBJ.setMobile(mobileStr);
                cstOBJ.setEmail(emailStr);
                cstOBJ.setZipcode(zipStr);
                cstOBJ.setState(stateStr);
                cstOBJ.setDistrict(districtStr);
                cstOBJ.setTaluka(talukaStr);
                cstOBJ.setStreet(streetStr);
                cstOBJ.setCustomer_type(cstType);
                cstOBJ.setGst_no(gst_no_edit.getText().toString());
                cstOBJ.setLand_size(land_size.getText().toString());
                DatabaseClient.getInstance(getActivity()).getAppDatabase()
                        .customerDao()
                        .update(cstOBJ);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            //  Toast.makeText(getActivity(), "Data has been saved.", Toast.LENGTH_SHORT).show();
            super.onPostExecute(aVoid);

        }
    }

    private boolean CheckAllFields() {
        if (mobileEditText.length()!=10) {
            mobileEditText.setError("This field is required");
            Toast.makeText(getActivity(), R.string.customer_mobile, Toast.LENGTH_SHORT).show();

            return false;
        }


//        if (land_size.length() == 0) {
//            land_size.setError("Land size is required");
//            Toast.makeText(getActivity(), "Enter the Land size", Toast.LENGTH_SHORT).show();
//            return false;
//        }
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
            et_pincode.setError("Pin code is required");
            Toast.makeText(getActivity(), R.string.PIN_CODE, Toast.LENGTH_SHORT).show();
            return false;
        }

        // after all validation return true.
        return true;
    }


}