package com.growit.posapp.fstore.ui.fragments.SaleManagement;

import android.content.Intent;
import android.os.Bundle;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Toast;

import com.growit.posapp.fstore.MainActivity;
import com.growit.posapp.fstore.R;
import com.growit.posapp.fstore.adapters.CustomSpinnerAdapter;
import com.growit.posapp.fstore.databinding.FragmentAddVendorBinding;
import com.growit.posapp.fstore.model.StateModel;
import com.growit.posapp.fstore.model.VendorModelList;
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


public class AddVendorAndCreateWareHouseFragment extends Fragment {
    FragmentAddVendorBinding binding;

    List<StateModel> stateNames = new ArrayList<>();
    List<StateModel> districtNames = new ArrayList<>();
    List<StateModel> talukaNames = new ArrayList<>();
    private String codeStr = "", cityStr ="",str_gst_no = "", nameStr = "", mobileStr = "", emailStr = "", districtStr = "", streetStr = "", zipStr = "", stateStr = "", talukaStr = "";
    boolean isAllFieldsChecked = false;
    List<VendorModelList> vendor_model=null;

   private String type_of_vendor_warehouse;
    int position;
    public AddVendorAndCreateWareHouseFragment() {
        // Required empty public constructor
    }

    public static AddVendorAndCreateWareHouseFragment newInstance() {
        return new AddVendorAndCreateWareHouseFragment();
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_add_vendor, container, false);
        if (getArguments() != null) {
            type_of_vendor_warehouse = getArguments().getString("type_of_vendor_warehouse");
        }
        init();

         return binding.getRoot();
    }

    private void init() {
        if(type_of_vendor_warehouse.equals("warehouse")) {
            binding.titleTxt.setText("Create Warehouse");
            binding.etCity.setVisibility(View.VISIBLE);
            binding.etGstNo.setVisibility(View.GONE);
            binding.etUsermobile.setVisibility(View.GONE);
            binding.etUseremail.setVisibility(View.GONE);
            binding.etUsername.setHint("Ware House Name");
        }else {
            binding.titleTxt.setText("Create Vendor");
            binding.etCity.setVisibility(View.GONE);
            binding.etCode.setVisibility(View.GONE);
            binding.etUseremail.setVisibility(View.VISIBLE);
            binding.etGstNo.setVisibility(View.VISIBLE);
            binding.etUsermobile.setVisibility(View.VISIBLE);
        }
        if (Utility.isNetworkAvailable(getContext())) {
            getStateData();
        } else {
            Toast.makeText(getContext(), R.string.NETWORK_GONE, Toast.LENGTH_SHORT).show();
        }
        StateModel st = new StateModel();
        st.setName("--Select District--");
        districtNames.add(st);
        StateModel st2 = new StateModel();
        st2.setName("--Select Taluka--");
        talukaNames.add(st2);
        if (getContext() != null) {
            CustomSpinnerAdapter adapter = new CustomSpinnerAdapter(getContext(), districtNames);
            binding.citySpinner.setAdapter(adapter);
        }
        if (getContext() != null) {
            CustomSpinnerAdapter adapter = new CustomSpinnerAdapter(getContext(), talukaNames);
            binding.talukaSpinner.setAdapter(adapter);
        }
        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), MainActivity.class));
                getActivity().finish();
            }
        });

        binding.citySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position != 0) {
                    districtStr = districtNames.get(position).getId() + "";
                    if (districtStr != null) {
                        if (!Utility.isNetworkAvailable(getActivity())) {
                            Toast.makeText(getContext(), R.string.NETWORK_GONE, Toast.LENGTH_SHORT).show();
                            return;
                        }
                        getTalukaData();
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        binding.stateSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
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

        binding.talukaSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
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
        binding.submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                str_gst_no = binding.etGstNo.getText().toString();
                nameStr = binding.etUsername.getText().toString();
                mobileStr = binding.etUsermobile.getText().toString();
                emailStr = binding.etUseremail.getText().toString();
                zipStr = binding.etPincode.getText().toString();
                streetStr = binding.etUseraddress.getText().toString();
                cityStr = binding.etCity.getText().toString();
                codeStr = binding.etCode.getText().toString();
                if (stateStr.length() == 0) {
                    Toast.makeText(getActivity(), R.string.Select_state, Toast.LENGTH_SHORT).show();
                    return;
                }
                if (districtStr.length() == 0) {
                    Toast.makeText(getActivity(), R.string.Select_district, Toast.LENGTH_SHORT).show();
                    return;
                }
                if (talukaStr.length() == 0) {
                    Toast.makeText(getActivity(), R.string.Select_taluka, Toast.LENGTH_SHORT).show();
                    return;
                }


                if (!Utility.isNetworkAvailable(getActivity())) {
                    Toast.makeText(getContext(), R.string.NETWORK_GONE, Toast.LENGTH_SHORT).show();
                    return;
                }

                if(type_of_vendor_warehouse.equals("warehouse")) {

                    if (nameStr.length() == 0) {
                        Toast.makeText(getActivity(), "Enter the Ware House Name", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    WareHouseRequest();
                }else {
                    binding.titleTxt.setText("Create Vendor");

                    isAllFieldsChecked = CheckAllFields();
                    if (isAllFieldsChecked) {
                        VendorRequest();

                    }
                }


            }
        });

    }

    private void getStateData() {
        Utility.showDialoge("Please wait while a configuring State...", getActivity());
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
                    if (getContext() != null) {
                        CustomSpinnerAdapter adapter = new CustomSpinnerAdapter(getContext(), stateNames);
                        binding.stateSpinner.setAdapter(adapter);
                    }
                }
            }

            @Override
            public void onError(String result) {
                Log.v("Response", result.toString());
                Utility.dismissDialoge();
                Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getDistrictData() {
        Utility.showDialoge("Please wait while a configuring District...", getActivity());
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
                    Utility.dismissDialoge();
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
                    if (getContext() != null) {
                        CustomSpinnerAdapter adapter = new CustomSpinnerAdapter(getContext(), districtNames);
                        binding.citySpinner.setAdapter(adapter);
                    }
                }
            }

            @Override
            public void onError(String result) {
                Log.v("Response", result.toString());
                Utility.dismissDialoge();
                Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getTalukaData() {
        Utility.showDialoge("Please wait while a configuring Taluka...", getActivity());
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
                    Utility.dismissDialoge();
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
                    if (getContext() != null) {
                        CustomSpinnerAdapter adapter = new CustomSpinnerAdapter(getContext(), talukaNames);
                        binding.talukaSpinner.setAdapter(adapter);
                    }
                }
            }

            @Override
            public void onError(String result) {
                Utility.dismissDialoge();
                Log.v("Response", result.toString());
                Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean CheckAllFields() {
        if (binding.etUsermobile.length() != 10) {
            binding.etUsermobile.setError("Enter a 10-digit mobile number");
            Toast.makeText(getActivity(), R.string.customer_mobile, Toast.LENGTH_SHORT).show();
            return false;
        }

        if (binding.etUsername.length() == 0) {
            binding.etUsername.setError("This field is required");
            Toast.makeText(getActivity(), R.string.CUSTOMER_NAME, Toast.LENGTH_SHORT).show();

            return false;
        }
        if (binding.etUseraddress.length() == 0) {
            binding.etUseraddress.setError("Address is required");
            Toast.makeText(getActivity(), R.string.ADDRESS, Toast.LENGTH_SHORT).show();
            return false;
        }
        if (binding.etPincode.length() != 6) {
            binding.etPincode.setError("Enter a 6-digit Pin code");
            Toast.makeText(getActivity(), R.string.PIN_CODE, Toast.LENGTH_SHORT).show();
            return false;
        }
        if (binding.etGstNo.length() == 0) {
            binding.etGstNo.setError("This field is required");
            Toast.makeText(getActivity(), "Enter valid GST No.", Toast.LENGTH_SHORT).show();
            return false;
        }


        // after all validation return true.
        return true;
    }

    private void VendorRequest() {
        SessionManagement sm = new SessionManagement(getActivity());
        Map<String, String> params = new HashMap<>();
        params.put("user_id", sm.getUserID() + "");
        params.put("name", nameStr);
        params.put("mobile", mobileStr);
        params.put("email", emailStr);
        params.put("state_id", stateStr);
        params.put("district_id", districtStr);
        params.put("taluka_id", talukaStr);
        params.put("zip", zipStr);
        params.put("country_id", ApiConstants.COUNTRY_ID);
        params.put("street", streetStr);
        params.put("vat", str_gst_no);

        Utility.showDialoge("", getActivity());
        Log.v("add", String.valueOf(params));
        new VolleyRequestHandler(getActivity(), params).createRequest(ApiConstants.ADD_VENDOR, new VolleyCallback() {
            private String message = "Registration failed!!";

            @Override
            public void onSuccess(Object result) throws JSONException {
                Log.v("Response", result.toString());
                JSONObject obj = new JSONObject(result.toString());
                int statusCode = obj.optInt("statuscode");
                message = obj.optString("status");
               String error_message = obj.optString("error_message");
              String  str_message = obj.optString("message");
                if (statusCode == 200 && message.equalsIgnoreCase("success")) {
                    Utility.dismissDialoge();
                    resetFields();
                    Toast.makeText(getActivity(), str_message, Toast.LENGTH_SHORT).show();

                }else {
                    Utility.dismissDialoge();
                    Toast.makeText(getActivity(), error_message, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(String result) throws Exception {
                Utility.dismissDialoge();
                Log.v("Response", result);
                Toast.makeText(getActivity(), R.string.JSONDATA_NULL, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void WareHouseRequest() {
        SessionManagement sm = new SessionManagement(getActivity());
        Map<String, String> params = new HashMap<>();
        params.put("name", nameStr);
        params.put("code", codeStr);
        params.put("street", streetStr);
        params.put("city", cityStr);
        params.put("state_id", stateStr);
        params.put("country_id", ApiConstants.COUNTRY_ID);
        params.put("zip", zipStr);
        params.put("district_id", districtStr);
        params.put("taluka_id", talukaStr);
        params.put("l10n_in_purchase_journal_id", "2");
        params.put("l10n_in_purchase_journal_id", "1");
        params.put("company_id", "1");

        Utility.showDialoge("", getActivity());
        Log.v("create_ware_house", String.valueOf(params));
        new VolleyRequestHandler(getActivity(), params).createRequest(ApiConstants.CREATE_WAREHOUSE, new VolleyCallback() {
            private String message = "Registration failed!!";

            @Override
            public void onSuccess(Object result) throws JSONException {
                Log.v("Response", result.toString());
                JSONObject obj = new JSONObject(result.toString());
                int statusCode = obj.optInt("statuscode");
                message = obj.optString("status");
                String error_message = obj.optString("error_message");
                String  str_message = obj.optString("message");
                if (statusCode == 200 && message.equalsIgnoreCase("success")) {
                    Utility.dismissDialoge();
                    resetFields();
                    Toast.makeText(getActivity(), str_message, Toast.LENGTH_SHORT).show();

                }else {
                    Utility.dismissDialoge();
                    Toast.makeText(getActivity(), error_message, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(String result) throws Exception {
                Utility.dismissDialoge();
                Log.v("Response", result);
                Toast.makeText(getActivity(), R.string.JSONDATA_NULL, Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void resetFields() {
        binding.etUsername.setText("");
        binding.etUsermobile.setText("");
        binding.etUseraddress.setText("");
        binding.etPincode.setText("");
        binding.etUseremail.setText("");
        binding.etCode.setText("");
        binding.etCity.setText("");
        binding.stateSpinner.setSelection(0);
        binding.citySpinner.setSelection(0);
        binding.talukaSpinner.setSelection(0);
    }

}