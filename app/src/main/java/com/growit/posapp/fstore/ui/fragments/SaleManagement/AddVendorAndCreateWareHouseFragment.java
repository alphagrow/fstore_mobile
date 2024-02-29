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

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
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
    private String codeStr = "", cityStr ="",str_gst_no = "", company_id="",nameStr = "",str_lic_no="", mobileStr = "", emailStr = "", districtStr = "", streetStr = "", zipStr = "", stateStr = "", talukaStr = "";
    boolean isAllFieldsChecked = false;
    List<VendorModelList> vendor_model=null;

   private String type_of_vendor_warehouse;
    int position;
    Boolean checkbox = false;
    List<StateModel> company_list = new ArrayList<>();
    public AddVendorAndCreateWareHouseFragment() {
        // Required empty public constructor
    }

    public static AddVendorAndCreateWareHouseFragment newInstance() {
        return new AddVendorAndCreateWareHouseFragment();
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments() != null) {

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
            binding.textName.setHint("Ware House Name");
            binding.titleTxt.setText("Create Warehouse");
            binding.etCity.setVisibility(View.VISIBLE);
            binding.textCode.setVisibility(View.VISIBLE);
            binding.companyLay.setVisibility(View.VISIBLE);
            binding.checkBoxGst.setVisibility(View.GONE);
            binding.etGstNo.setVisibility(View.GONE);
            binding.textVendMobNo.setVisibility(View.GONE);
            binding.etUsermobile.setVisibility(View.GONE);
            binding.etUseremail.setVisibility(View.GONE);
            binding.vendorEmailText.setVisibility(View.GONE);
            binding.textLic.setVisibility(View.GONE);
            binding.etLicenseNumber.setVisibility(View.GONE);

        }else {
            binding.titleTxt.setText("Create Vendor");
            binding.textName.setHint("Vendor Name");
            binding.cityText.setVisibility(View.GONE);
            binding.etCity.setVisibility(View.GONE);
            binding.textCode.setVisibility(View.GONE);
            binding.etCode.setVisibility(View.GONE);
            binding.textVendMobNo.setVisibility(View.VISIBLE);
            binding.textLic.setVisibility(View.VISIBLE);
            binding.etLicenseNumber.setVisibility(View.VISIBLE);
            binding.etUseremail.setVisibility(View.VISIBLE);
            binding.checkBoxGst.setVisibility(View.VISIBLE);
            binding.etUsermobile.setVisibility(View.VISIBLE);
            binding.companyLay.setVisibility(View.GONE);

        }
        if (Utility.isNetworkAvailable(getContext())) {
            getCompanyList();
            getStateData();
        } else {
            Toast.makeText(getContext(), R.string.NETWORK_GONE, Toast.LENGTH_SHORT).show();
        }
        binding.checkBoxGst.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(binding.checkBoxGst.isChecked()) {
                    binding.etGstNo.setVisibility(View.VISIBLE);


                } else {
                    checkbox = false;
                    binding.etGstNo.setVisibility(View.GONE);
                    binding.etGstNo.setText("");
                }
            }
        });
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
        binding.compSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position != 0) {
                    company_id = company_list.get(position).getId() + "";

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

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
                str_lic_no = binding.etLicenseNumber.getText().toString();
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
//                if (checkbox=true) {
//                    if (str_gst_no.length() == 0) {
//                        Toast.makeText(getActivity(), "Enter valid GST No.", Toast.LENGTH_SHORT).show();
//                        return ;
//                    }
//
//                }

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


        // after all validation return true.
        return true;
    }

    private void VendorRequest() {
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
        params.put("zip", zipStr);
        params.put("country_id", ApiConstants.COUNTRY_ID);
        params.put("street", streetStr);
        params.put("vat", str_gst_no);
        params.put("license_number", str_lic_no);

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
        params.put("user_id", sm.getUserID()+"");
        params.put("token", sm.getJWTToken());
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
        params.put("company_id", sm.getCompanyID()+"");
//        params.put("company_id", company_id);

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

    private void getCompanyList() {
        SessionManagement sm = new SessionManagement(getActivity());
        RequestQueue queue = Volley.newRequestQueue(getActivity());
        //  String url = ApiConstants.BASE_URL + ApiConstants.GET_CUSTOMER_DISCOUNT_LIST;

        String url = ApiConstants.BASE_URL + ApiConstants.GET_COMPANIES + "user_id=" + sm.getUserID() + "&" + "token=" + sm.getJWTToken();
        //    Utility.showDialoge("Please wait while a moment...", getActivity());
        Log.d("ALL_CROPS_url",url);
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
                       // dismissDialoge();
                        JSONArray jsonArray = obj.getJSONArray("companies");
                        StateModel stateModel = new StateModel();
                        stateModel.setId(-1);
                        stateModel.setName("--Select Company--");
                        company_list.add(stateModel);
                        for (int i = 0; i < jsonArray.length(); i++) {
                            stateModel = new StateModel();
                            JSONObject data = jsonArray.getJSONObject(i);
                            int id = data.optInt("id");
                            String name = data.optString("name");
                            stateModel.setId(id);
                            stateModel.setName(name);
                            company_list.add(stateModel);
                        }
                        if(getContext()!=null) {
                            CustomSpinnerAdapter adapter = new CustomSpinnerAdapter(getContext(), company_list);
                            binding.compSpinner.setAdapter(adapter);
                        }
                    }
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
        }, error -> Toast.makeText(getActivity(), R.string.JSONDATA_NULL, Toast.LENGTH_SHORT).show());
        queue.add(jsonObjectRequest);
    }

    private void resetFields() {
        binding.etUsername.setText("");
        binding.etUsermobile.setText("");
        binding.etUseraddress.setText("");
        binding.etPincode.setText("");
        binding.etUseremail.setText("");
        binding.etCode.setText("");
        binding.etCity.setText("");
        binding.etGstNo.setText("");
        binding.etLicenseNumber.setText("");
        binding.stateSpinner.setSelection(0);
        binding.citySpinner.setSelection(0);
        binding.talukaSpinner.setSelection(0);
    }

}