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
import com.growit.posapp.fstore.databinding.FragmentUpdateVendorBinding;
import com.growit.posapp.fstore.model.StateModel;
import com.growit.posapp.fstore.model.VendorModelList;
import com.growit.posapp.fstore.tables.Customer;
import com.growit.posapp.fstore.ui.fragments.UpdateCustomerFragment;
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

public class UpdateVendorFragment extends Fragment {

   FragmentAddVendorBinding binding;
    List<StateModel> stateNames = new ArrayList<>();
    List<StateModel> districtNames = new ArrayList<>();
    List<StateModel> talukaNames = new ArrayList<>();
    private String str_gst_no = "", nameStr = "", mobileStr = "", emailStr = "", districtStr = "", streetStr = "", zipStr = "", stateStr = "", talukaStr = "";
    boolean isAllFieldsChecked = false;
    List<VendorModelList> vendor_model=null;
    private int position = 0;
    public UpdateVendorFragment() {
        // Required empty public constructor
    }


    public static UpdateVendorFragment newInstance() {
        return new UpdateVendorFragment();
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
        init();
        if (getArguments() != null) {
            position = getArguments().getInt("position");
            vendor_model = (List<VendorModelList>) getArguments().getSerializable("vendor_list");
            binding.etUsername.setText(vendor_model.get(position).getName());
            binding.etUsermobile.setText(vendor_model.get(position).getMobile());
            binding.etUseraddress.setText(vendor_model.get(position).getStreet());
            binding.etGstNo.setText(vendor_model.get(position).getVat());
            binding.etUseremail.setText(vendor_model.get(position).getEmail());
            binding.etPincode.setText(vendor_model.get(position).getZip());
            getStateData();
            getDistrictData(vendor_model.get(position).getStateId());
            getTalukaData(vendor_model.get(position).getDistrictId());
            binding.submitBtn.setVisibility(View.GONE);
            binding.update.setVisibility(View.VISIBLE);

        }
        return binding.getRoot();
    }
    private void init() {
        binding.titleTxt.setText("Update Vendor");
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
                        getTalukaData(districtStr);
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        binding.stateSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
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
        binding.update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                str_gst_no = binding.etGstNo.getText().toString();
                nameStr = binding.etUsername.getText().toString();
                mobileStr = binding.etUsermobile.getText().toString();
                emailStr = binding.etUseremail.getText().toString();
                zipStr = binding.etPincode.getText().toString();
                streetStr = binding.etUseraddress.getText().toString();

                isAllFieldsChecked = CheckAllFields();
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

                if (isAllFieldsChecked) {
                    updateVendorRequest();

                }

            }
        });

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
                        if (String.valueOf(id).equalsIgnoreCase(vendor_model.get(position).getStateId())) {
                            spinnerPosition = i + 1;
                        }
                        stateModel.setName(name);
                        stateNames.add(stateModel);

                    }
                    if(getContext()!=null) {
                        CustomSpinnerAdapter adapter = new CustomSpinnerAdapter(getContext(), stateNames);
                        binding.stateSpinner.setAdapter(adapter);
                        binding.stateSpinner.setSelection(spinnerPosition);
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
                        if (String.valueOf(id).equalsIgnoreCase(vendor_model.get(position).getDistrictId())) {
                            spinnerPosition = i + 1;
                        }
                        String name = data.optString("name");
                        stateModel.setId(id);
                        stateModel.setName(name);
                        districtNames.add(stateModel);
                    }
                    if(getContext()!=null) {
                        CustomSpinnerAdapter adapter = new CustomSpinnerAdapter(getContext(), districtNames);
                        binding.citySpinner.setAdapter(adapter);
                        binding.citySpinner.setSelection(spinnerPosition);
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
                        if (String.valueOf(id).equalsIgnoreCase(vendor_model.get(position).getTalukaId())) {
                            spinnerPosition = i + 1;
                        }
                        String name = data.optString("name");
                        stateModel.setId(id);
                        stateModel.setName(name);
                        talukaNames.add(stateModel);
                    }
                    if(getContext()!=null) {
                        CustomSpinnerAdapter adapter = new CustomSpinnerAdapter(getContext(), talukaNames);
                        binding.talukaSpinner.setAdapter(adapter);
                        binding.talukaSpinner.setSelection(spinnerPosition);
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

    private void updateVendorRequest() {
        SessionManagement sm = new SessionManagement(getActivity());
        Map<String, String> params = new HashMap<>();
        params.put("token", sm.getJWTToken());
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
        params.put("vendor_id", vendor_model.get(position).getVendorId()+"");

        Log.d("vendoe_update",ApiConstants.UPDATE_Vendor+params.toString());
        new VolleyRequestHandler(getActivity(), params).putRequest(ApiConstants.UPDATE_Vendor , new VolleyCallback() {
            private String message = "Update failed!!";

            @Override
            public void onSuccess(Object result) throws JSONException {
                JSONObject obj = new JSONObject(result.toString());
                String status = obj.optString("status");
                message = obj.optString("message");
                String error_message = obj.optString("error_message");
                String  str_message = obj.optString("message");
                if (status.equalsIgnoreCase("success")) {
                    Toast.makeText(getActivity(), "Vendor  Update successfully ", Toast.LENGTH_SHORT).show();

                    // resetFields();
                }else {
                    Toast.makeText(getActivity(), error_message, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(String result) throws Exception {
                Log.v("Response", result.toString());
                Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void resetFields() {
        binding.etUsername.setText("");
        binding.etUsermobile.setText("");
        binding.etUseraddress.setText("");
        binding.etPincode.setText("");
        binding.etUseremail.setText("");
        binding.stateSpinner.setSelection(0);
        binding.citySpinner.setSelection(0);
        binding.talukaSpinner.setSelection(0);
    }

}