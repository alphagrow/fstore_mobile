package com.growit.posapp.fstore.ui.fragments.Setting;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.growit.posapp.fstore.MainActivity;
import com.growit.posapp.fstore.R;
import com.growit.posapp.fstore.adapters.ConfigurationAdapter;
import com.growit.posapp.fstore.adapters.CustomSpinnerAdapter;
import com.growit.posapp.fstore.adapters.UOMAdapter;
import com.growit.posapp.fstore.databinding.FragmentAddDistrictBinding;
import com.growit.posapp.fstore.databinding.FragmentAddTalukaBinding;
import com.growit.posapp.fstore.databinding.FragmentAddTranferBinding;
import com.growit.posapp.fstore.model.ConfigurationModel;
import com.growit.posapp.fstore.model.ProductDetail;
import com.growit.posapp.fstore.model.StateModel;
import com.growit.posapp.fstore.utils.ApiConstants;
import com.growit.posapp.fstore.utils.SessionManagement;
import com.growit.posapp.fstore.utils.Utility;
import com.growit.posapp.fstore.volley.VolleyCallback;
import com.growit.posapp.fstore.volley.VolleyRequestHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddTalukaFragment extends Fragment {
    FragmentAddTalukaBinding binding;
    List<ConfigurationModel> list;
    Activity contexts;
    ConfigurationAdapter adapter;
    EditText shop_name_ed;
    boolean isAllFieldsChecked = false;
    ProductDetail productDetail;
    List<StateModel> stateNames = new ArrayList<>();
    List<StateModel> districtNames = new ArrayList<>();
    EditText tal_name_ed,tal_code_ed;
   String stateStr = "",districtStr="";
    Dialog dialog;
    List<StateModel> talukaNames = new ArrayList<>();
    public AddTalukaFragment() {
        // Required empty public constructor
    }
    public static AddTalukaFragment newInstance() {
        return new AddTalukaFragment();
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        //   return inflater.inflate(R.layout.fragment_add_shop_and_shop_list, container, false);
        binding = FragmentAddTalukaBinding.inflate(inflater, container, false);
        init();
        return binding.getRoot();
    }



    private void init () {
        list = new ArrayList<>();
        GridLayoutManager layoutManager = new GridLayoutManager(getActivity(), 1, LinearLayoutManager.VERTICAL, false);
        binding.recyclerVendor.setLayoutManager(layoutManager);
        if (Utility.isNetworkAvailable(getContext())) {
              getTalukaData();
        } else {
            Toast.makeText(getContext(), R.string.NETWORK_GONE, Toast.LENGTH_SHORT).show();

        }

        binding.refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                binding.refreshLayout.setRefreshing(false);
                if (Utility.isNetworkAvailable(getContext())) {
                    getTalukaData();
                } else {
                    Toast.makeText(getContext(), R.string.NETWORK_GONE, Toast.LENGTH_SHORT).show();

                }

            }
        });
        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), MainActivity.class));
                getActivity().finish();
            }
        });
        binding.addText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (Utility.isNetworkAvailable(getContext())) {
                    showDialogeReceiveProduct();
                } else {
                    Toast.makeText(getContext(), R.string.NETWORK_GONE, Toast.LENGTH_SHORT).show();

                }
            }
        });

//        binding.recyclerVendor.addOnItemTouchListener(
//                new RecyclerItemClickListener(getActivity(),  binding.recyclerVendor, new RecyclerItemClickListener.OnItemClickListener() {
//                    @Override
//                    public void onItemClick(View view, int position) {
//                        String  cust_name = list.get(position).getName();
//                        int  _id = list.get(position).getId();
//                        showDialogeUpdateReceiveProduct(cust_name,_id);
//                    }
//
//
//                    @Override
//                    public void onLongItemClick(View view, int position) {
//                        // do whatever
//                    }
//                })
//        );

    }
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        contexts = getActivity();

    }

    private  void showDialogeReceiveProduct() {
         dialog = new Dialog(getActivity());
        dialog.setContentView(R.layout.taluka_dialoge);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(false);
        dialog.getWindow().getAttributes().windowAnimations = R.style.animation;
        tal_name_ed = dialog.findViewById(R.id.taluka_name_ed);
        tal_code_ed = dialog.findViewById(R.id.taluka_code_ed);
        Spinner stateSpinner = dialog.findViewById(R.id.stateSpinner);
        Spinner dis_Spinner = dialog.findViewById(R.id.dist_Spinner);
        TextView okay_text = dialog.findViewById(R.id.ok_text);
        TextView cancel_text = dialog.findViewById(R.id.cancel_text);
        if (!Utility.isNetworkAvailable(getActivity())) {
            Toast.makeText(getContext(), R.string.NETWORK_GONE, Toast.LENGTH_SHORT).show();
            return;
        }
        getStateData(stateSpinner);

        dis_Spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position != 0) {
                    districtStr = districtNames.get(position).getId() + "";
                    if (districtStr != null) {
                        if (!Utility.isNetworkAvailable(getActivity())) {
                            Toast.makeText(getContext(), R.string.NETWORK_GONE, Toast.LENGTH_SHORT).show();                            return;
                        }

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
                        getDistrictData(dis_Spinner);
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        okay_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String str_tal_name = tal_name_ed.getText().toString();
                String str_tal_code = tal_code_ed.getText().toString();
                isAllFieldsChecked= CheckAllFields();
                if (!Utility.isNetworkAvailable(getActivity())) {
                    Toast.makeText(getContext(), R.string.NETWORK_GONE, Toast.LENGTH_SHORT).show();
                    return;
                }
                if (isAllFieldsChecked) {

                    addTaluka(str_tal_name,str_tal_code);
                }
//                dialog.dismiss();

            }
        });
        cancel_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    private  void showDialogeUpdateReceiveProduct(String name,int id) {

         dialog = new Dialog(getActivity());
        dialog.setContentView(R.layout.taluka_dialoge);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(false);
        dialog.getWindow().getAttributes().windowAnimations = R.style.animation;
        shop_name_ed = dialog.findViewById(R.id.shop_name_ed);

        TextView okay_text = dialog.findViewById(R.id.ok_text);
        TextView cancel_text = dialog.findViewById(R.id.cancel_text);
        shop_name_ed.setText(name);


        okay_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String str_name = shop_name_ed.getText().toString();

                isAllFieldsChecked= CheckAllFields();
                if (!Utility.isNetworkAvailable(getActivity())) {
                    Toast.makeText(getContext(), R.string.NETWORK_GONE, Toast.LENGTH_SHORT).show();
                    return;
                }

                if (isAllFieldsChecked) {
                    //  DiscountUpdate(str_name,id);
                }
                dialog.dismiss();

            }
        });

        cancel_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    private boolean CheckAllFields() {
        if (tal_name_ed.length()== 0) {
            tal_name_ed.setError("This field is required");
            Toast.makeText(getActivity(), "Enter the Taluka Name", Toast.LENGTH_SHORT).show();

            return false;
        }
        if (tal_code_ed.length()== 0) {
            tal_code_ed.setError("This field is required");
            Toast.makeText(getActivity(), "Enter the Taluka Code", Toast.LENGTH_SHORT).show();

            return false;
        }
        return true;
    }

    private void addTaluka(String str_name,String str_code){
        SessionManagement sm = new SessionManagement(getActivity());
        Map<String, String> params = new HashMap<>();
        params.put("user_id", sm.getUserID()+"");
        params.put("token", sm.getJWTToken());
        params.put("name", str_name);
        params.put("code", str_code);
        params.put("state_id", stateStr);
        params.put("district_id", districtStr);

//        params.put("country_id", ApiConstants.COUNTRY_ID);
        Utility.showDialoge("Please wait while a moment...", getActivity());
        Log.v("add", String.valueOf(params));
        new VolleyRequestHandler(getActivity(), params).createRequest(ApiConstants.POST_CREATE_TALUKA, new VolleyCallback() {
            private String message = " failed!!";

            @Override
            public void onSuccess(Object result) throws JSONException {
                Log.v("Response", result.toString());
                JSONObject obj = new JSONObject(result.toString());
                int statusCode = obj.optInt("statuscode");
                String status = obj.optString("status");
                String message = obj.optString("message");
                String error_message = obj.optString("error_message");
                if (statusCode==200 && status.equalsIgnoreCase("success")) {
                    Utility.dismissDialoge();
                    Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
                    dialog.dismiss();

                    //                 getDistrictList();
                }else {
                    Utility.dismissDialoge();
                    Toast.makeText(getActivity(), error_message, Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onError(String result) throws Exception {
                Utility.dismissDialoge();
                Toast.makeText(getActivity(), R.string.JSONDATA_NULL, Toast.LENGTH_SHORT).show();
            }
        });

    }

//    private void DiscountUpdate(String name,int id) {
//        SessionManagement sm = new SessionManagement(getActivity());
//        Map<String, String> params = new HashMap<>();
//        params.put("user_id", sm.getUserID() + "");
//        params.put("token", sm.getJWTToken());
//        params.put("name", name);
//        params.put("shop_id", id+"");
//
//        new VolleyRequestHandler(getActivity(), params).putRequest(ApiConstants.PUT_UPDATE_SHOPS, new VolleyCallback() {
//            private String message = "Update failed!!";
//
//            @Override
//            public void onSuccess(Object result) throws JSONException {
//                Log.v("Response", result.toString());
//                JSONObject obj = new JSONObject(result.toString());
//                int statusCode = obj.optInt("statuscode");
//                String status = obj.optString("status");
//                String message = obj.optString("message");
//                String error_message = obj.optString("error_message");
//                if (statusCode==200 && status.equalsIgnoreCase("success")) {
//                    Utility.dismissDialoge();
//                    Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
//
//                    getDistrictList();
//                }else {
//                    Utility.dismissDialoge();
//                    Toast.makeText(getActivity(), error_message, Toast.LENGTH_SHORT).show();
//                }
//            }
//
//            @Override
//            public void onError(String result) throws Exception {
//                Log.v("Response", result.toString());
//                Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
//            }
//        });
//    }

    private void getDistrictData(Spinner dis_Spinner) {

        Map<String, String> params = new HashMap<>();
        params.put("states_id", stateStr);
        new VolleyRequestHandler(getActivity(), params).createRequest(ApiConstants.GET_DISTRICT, new VolleyCallback() {
            private String message = " failed!!";

            @Override
            public void onSuccess(Object result) throws JSONException {
                Log.v("Response", result.toString());
                districtNames = new ArrayList<>();
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
                        String name = data.optString("name");
                        stateModel.setId(id);
                        stateModel.setName(name);
                        districtNames.add(stateModel);
                    }
                    if(getContext()!=null) {
                        CustomSpinnerAdapter adapter = new CustomSpinnerAdapter(getContext(), districtNames);
                        dis_Spinner.setAdapter(adapter);
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
    private void getStateData(Spinner stateSpinner) {

        Map<String, String> params = new HashMap<>();
        params.put("country_id", ApiConstants.COUNTRY_ID);
        Utility.showDialoge("Please wait while a moment...", getActivity());
        new VolleyRequestHandler(getActivity(), params).createRequest(ApiConstants.GET_STATES, new VolleyCallback() {
            private String message = " failed!!";

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
                    if(getContext()!=null) {
                        CustomSpinnerAdapter adapter = new CustomSpinnerAdapter(getContext(), stateNames);
                        stateSpinner.setAdapter(adapter);
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

        Map<String, String> params = new HashMap<>();
        params.put("district_id", districtStr);
        new VolleyRequestHandler(getActivity(), params).createRequest(ApiConstants.GET_TALUKA, new VolleyCallback() {
            private String message = " failed!!";

            @Override
            public void onSuccess(Object result) throws JSONException {
                Log.v("Response", result.toString());
                talukaNames = new ArrayList<>();
                JSONObject obj = new JSONObject(result.toString());
                int statusCode = obj.optInt("statuscode");
                String status = obj.optString("status");
                if (statusCode == 200 && status.equalsIgnoreCase("success")) {
                    JSONArray jsonArray = obj.getJSONArray("data");

                    list.clear();
                    if (jsonArray.length() > 0) {
                        for (int i = 0; i < jsonArray.length(); i++) {
                            ConfigurationModel model = new ConfigurationModel();
                            JSONObject data = jsonArray.getJSONObject(i);
                            Integer id = data.optInt("id");
                            String name = data.optString("name");
                            model.setId(id);
                            model.setName(name);
                            list.add(model);
                        }
                        if (list == null || list.size() == 0) {
                            binding.noDataFound.setVisibility(View.VISIBLE);
                        } else {
                            binding.totalCustomerText.setText("Total : "+list.size()+" Talukas ");
                            binding.noDataFound.setVisibility(View.GONE);
                            adapter = new ConfigurationAdapter(getActivity(), list);
                            binding.recyclerVendor.setAdapter(adapter);

                        }

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


}