package com.growit.posapp.fstore.ui.fragments.Inventory;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.growit.posapp.fstore.MainActivity;
import com.growit.posapp.fstore.R;
import com.growit.posapp.fstore.adapters.ConfigurationAdapter;
import com.growit.posapp.fstore.adapters.POSAdapter;
import com.growit.posapp.fstore.databinding.FragmentConfigurationBinding;
import com.growit.posapp.fstore.databinding.FragmentPOSCategoryListBinding;
import com.growit.posapp.fstore.model.ConfigurationModel;
import com.growit.posapp.fstore.model.StockInventoryModel;
import com.growit.posapp.fstore.model.Value;
import com.growit.posapp.fstore.ui.fragments.AddProduct.CreateAttributeFragment;
import com.growit.posapp.fstore.ui.fragments.POSCategory.AddPOSCategoryFragment;
import com.growit.posapp.fstore.utils.ApiConstants;
import com.growit.posapp.fstore.utils.SessionManagement;
import com.growit.posapp.fstore.utils.Utility;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class ConfigurationFragment extends Fragment {

   FragmentConfigurationBinding binding;

    ConfigurationAdapter adapter;
    Activity contexts;
    List<ConfigurationModel> list;
    private  String configuration_type;
    public ConfigurationFragment() {
        // Required empty public constructor
    }


    public static ConfigurationFragment newInstance() {
        return new ConfigurationFragment();
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
      //  return inflater.inflate(R.layout.fragment_configuration, container, false);
        binding= FragmentConfigurationBinding.inflate(inflater, container, false);

        if (getArguments() != null) {
            configuration_type = getArguments().getString("configuration_type");
            if (configuration_type.equals("location")){
                binding.seacrEditTxt.setHint("Search by location");
            }else if(configuration_type.equals("transfer")){
                binding.seacrEditTxt.setHint("Search by Operation name");
            }else {

            }
        }
            init();
          return binding.getRoot();
    }

    private void init(){
        list = new ArrayList<>();
        GridLayoutManager layoutManager = new GridLayoutManager(getActivity(), 1, LinearLayoutManager.VERTICAL, false);
        binding.recyclerPos.setLayoutManager(layoutManager);


        if (Utility.isNetworkAvailable(getContext())) {

            if (configuration_type.equals("location")){
                getLocation();
            }else if(configuration_type.equals("operation_types")){
                getOperationTypeList();
            }else {

            }

        } else {
            Toast.makeText(getContext(), R.string.NETWORK_GONE, Toast.LENGTH_SHORT).show();

        }
//        binding.refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
//            @Override
//            public void onRefresh() {
//                binding.refreshLayout.setRefreshing(false);
//                if (Utility.isNetworkAvailable(getContext())) {
//                    getCropRequest();
//                } else {
//                    Toast.makeText(getContext(), R.string.NETWORK_GONE, Toast.LENGTH_SHORT).show();
//
//                }
//
//            }
//        });
        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), MainActivity.class));
                getActivity().finish();
            }
        });
    }

    private void getLocation() {
        SessionManagement sm = new SessionManagement(contexts);
        RequestQueue queue = Volley.newRequestQueue(contexts);
        String url = ApiConstants.BASE_URL + ApiConstants.GET_LOCATION_LIST+ "user_id=" + sm.getUserID() + "&" + "token=" + sm.getJWTToken();

        //  String url = ApiConstants.BASE_URL + ApiConstants.GET_LOCATION_LIST + "user_id=" + sm.getUserID() + "&" + "token=" + sm.getJWTToken();
        Utility.showDialoge("Please wait while a moment...", getActivity());
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

                    if (status.equalsIgnoreCase("success")) {
                        Utility.dismissDialoge();
                        JSONArray jsonArray = obj.getJSONArray("data");
                        list.clear();
                        if (jsonArray.length() > 0) {
                            for (int i = 0; i < jsonArray.length(); i++) {
                                ConfigurationModel model = new ConfigurationModel();
                                JSONObject data = jsonArray.getJSONObject(i);
                                Integer id = data.optInt("id");
                                String name = data.optString("name");
                                String usage = data.optString("usage");
                                model.setId(id);
                                model.setName(name);
                                model.setUsage(usage);

                                list.add(model);

                            }
                            if (list == null || list.size() == 0) {
                                binding.noDataFound.setVisibility(View.VISIBLE);
                            } else {
                                binding.totalListText.setText("Total : "+list.size()+" Location ");
                                binding.noDataFound.setVisibility(View.GONE);
                                adapter = new ConfigurationAdapter(getActivity(), list);
                                binding.recyclerPos.setAdapter(adapter);

                            }

                        }
                    }
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
        }, error -> Toast.makeText(contexts, R.string.JSONDATA_NULL, Toast.LENGTH_SHORT).show());
        queue.add(jsonObjectRequest);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        contexts = getActivity();

    }
    private void getOperationTypeList() {
        SessionManagement sm = new SessionManagement(contexts);
        RequestQueue queue = Volley.newRequestQueue(contexts);
       // String url = ApiConstants.BASE_URL + ApiConstants.GET_OPERATION_TYPE_LIST;

          String url = ApiConstants.BASE_URL + ApiConstants.GET_OPERATION_TYPE_LIST + "user_id=" + sm.getUserID() + "&" + "token=" + sm.getJWTToken();
        Utility.showDialoge("Please wait while a moment...", getActivity());
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

                    if (status.equalsIgnoreCase("success")) {
                        Utility.dismissDialoge();
                        JSONArray jsonArray = obj.getJSONArray("operation_types");
                        list.clear();
                        if (jsonArray.length() > 0) {
                            for (int i = 0; i < jsonArray.length(); i++) {
                                ConfigurationModel model = new ConfigurationModel();
                                JSONObject data = jsonArray.getJSONObject(i);
                                Integer id = data.optInt("id");
                                String name = data.optString("name");
                                String code = data.optString("code");
                                String warehouse_id = data.optString("warehouse_id");
                                model.setId(id);
                                model.setName(name);
                                model.setUsage(code);
                                model.setParentId(warehouse_id);

                                list.add(model);

                            }
                            if (list == null || list.size() == 0) {
                                binding.noDataFound.setVisibility(View.VISIBLE);
                            } else {
                                binding.totalListText.setText("Total : "+list.size()+" Operation Types ");
                                binding.noDataFound.setVisibility(View.GONE);
                                adapter = new ConfigurationAdapter(getActivity(), list);
                                binding.recyclerPos.setAdapter(adapter);

                            }

                        }
                    }
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
        }, error -> Toast.makeText(contexts, R.string.JSONDATA_NULL, Toast.LENGTH_SHORT).show());
        queue.add(jsonObjectRequest);
    }


}