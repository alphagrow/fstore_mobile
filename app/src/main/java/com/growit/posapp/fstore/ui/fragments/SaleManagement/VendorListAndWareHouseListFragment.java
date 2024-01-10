package com.growit.posapp.fstore.ui.fragments.SaleManagement;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.text.Editable;
import android.text.TextWatcher;
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
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.growit.posapp.fstore.MainActivity;
import com.growit.posapp.fstore.R;
import com.growit.posapp.fstore.adapters.VendorListAdapter;
import com.growit.posapp.fstore.adapters.WareHouseAdapter;
import com.growit.posapp.fstore.databinding.FragmentVendorBinding;
import com.growit.posapp.fstore.model.VendorModel;
import com.growit.posapp.fstore.model.VendorModelList;
import com.growit.posapp.fstore.model.WarehouseModel;
import com.growit.posapp.fstore.utils.ApiConstants;
import com.growit.posapp.fstore.utils.SessionManagement;
import com.growit.posapp.fstore.utils.Utility;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;


public class VendorListAndWareHouseListFragment extends Fragment {
    FragmentVendorBinding binding;
    VendorModel vendormodel;
    VendorListAdapter adapter;
    WareHouseAdapter wareHouseAdapter;
    String status="true";
    private  String type_of_vendor_warehouse;
    public VendorListAndWareHouseListFragment() {
        // Required empty public constructor
    }
    public static VendorListAndWareHouseListFragment newInstance() {
        return new VendorListAndWareHouseListFragment();
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
        binding= FragmentVendorBinding.inflate(inflater, container, false);
        if (getArguments() != null) {
            type_of_vendor_warehouse = getArguments().getString("type_of_vendor_warehouse");
            if (type_of_vendor_warehouse.equals("warehouse")){
                binding.seacrEditTxt.setHint("Search by Ware House name");
            }else {
                binding.seacrEditTxt.setHint("Search by Vendor name");
            }
        }
        init();

        return binding.getRoot();
    }

    private void init(){
        GridLayoutManager layoutManager = new GridLayoutManager(getActivity(), 1, LinearLayoutManager.VERTICAL, false);
        binding.recyclerVendor.setLayoutManager(layoutManager);
        if (Utility.isNetworkAvailable(getContext())) {
            if (type_of_vendor_warehouse.equals("warehouse")){
                getWareHouseList();
            }else {
                getVendorList();
            }

        } else {
            Toast.makeText(getContext(), R.string.NETWORK_GONE, Toast.LENGTH_SHORT).show();

        }

        binding.refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                binding.refreshLayout.setRefreshing(false);
                if (Utility.isNetworkAvailable(getContext())) {
                    if (type_of_vendor_warehouse.equals("warehouse")){
                        getWareHouseList();
                    }else {
                        getVendorList();
                    }

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
                Bundle bundle = new Bundle();
                bundle.putString("type_of_vendor_warehouse", type_of_vendor_warehouse);
                Fragment fragment = AddVendorAndCreateWareHouseFragment.newInstance();
                fragment.setArguments(bundle);
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.flContent, fragment).commit();
            }
        });
        binding.seacrEditTxt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterList(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });




    }
    private void getVendorList(){
        SessionManagement sm = new SessionManagement(getActivity());
        RequestQueue queue = Volley.newRequestQueue(getActivity());
        String url = ApiConstants.BASE_URL + ApiConstants.GET_VENDOR_LIST + "user_id=" + sm.getUserID() + "&" + "token=" + sm.getJWTToken();

        // String url = ApiConstants.BASE_URL + ApiConstants.GET_VENDOR_LIST + "user_id=" + sm.getUserID() + "&" + "token=" + sm.getJWTToken();
        Log.v("url", url);
        Utility.showDialoge("Please wait while a moment...", getActivity());
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
                        Utility.dismissDialoge();
                        Gson gson = new Gson();
                        Type listType = new TypeToken<VendorModel>() {
                        }.getType();

                        vendormodel = gson.fromJson(response.toString(), listType);
                        if (vendormodel.getVendors() == null || vendormodel.getVendors().size() == 0) {
                            binding.noDataFound.setVisibility(View.VISIBLE);
                            binding.recyclerVendor.setVisibility(View.GONE);
                        } else {
                            binding.noDataFound.setVisibility(View.GONE);
                            binding.recyclerVendor.setVisibility(View.VISIBLE);
                            LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
                            layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
                            adapter = new VendorListAdapter(getActivity(), vendormodel.getVendors());
                            binding.recyclerVendor.setAdapter(adapter);
                            binding.recyclerVendor.setLayoutManager(layoutManager);

                        }
                 //       adapter.notifyDataSetChanged();
                    }
                }catch (JSONException e) {
                    throw new RuntimeException(e);
                }


            }
        }, error -> {
            binding.noDataFound.setVisibility(View.VISIBLE);
            binding.recyclerVendor.setVisibility(View.GONE);
        });
        queue.add(jsonObjectRequest);
    }
    private void filterList(String text){
        if (type_of_vendor_warehouse.equals("warehouse")){
            ArrayList<WarehouseModel> model = new ArrayList<>();
            for (WarehouseModel detail : vendormodel.getWarehouses()){
                if (detail.getName().toLowerCase().contains(text.toLowerCase()) || detail.getCompanyId().toLowerCase().contains(text.toLowerCase())){
                    model.add(detail);
                }
            }

            wareHouseAdapter.updateList(model);
        }else {
            ArrayList<VendorModelList> model = new ArrayList<>();
            for (VendorModelList detail : vendormodel.getVendors()){
                if (detail.getName().toLowerCase().contains(text.toLowerCase()) || detail.getMobile().toLowerCase().contains(text.toLowerCase())){
                    model.add(detail);
                }
            }

            adapter.updateList(model);
        }

    }

    private void getWareHouseList(){
        SessionManagement sm = new SessionManagement(getActivity());
        RequestQueue queue = Volley.newRequestQueue(getActivity());
        String url = ApiConstants.BASE_URL + ApiConstants.GET_WareHouses + "user_id=" + sm.getUserID() + "&" + "token=" + sm.getJWTToken();

      //   String url = ApiConstants.BASE_URL + ApiConstants.GET_WareHouses;
        Log.v("url", url);
        Utility.showDialoge("Please wait while a moment...", getActivity());
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
                        Utility.dismissDialoge();
                        Gson gson = new Gson();
                        Type listType = new TypeToken<VendorModel>() {
                        }.getType();

                        vendormodel = gson.fromJson(response.toString(), listType);
                        if (vendormodel.getWarehouses() == null || vendormodel.getWarehouses().size() == 0) {
                            binding.noDataFound.setVisibility(View.VISIBLE);
                            binding.recyclerVendor.setVisibility(View.GONE);
                        } else {
                            binding.noDataFound.setVisibility(View.GONE);
                            binding.recyclerVendor.setVisibility(View.VISIBLE);
                            LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
                            layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
                            wareHouseAdapter = new WareHouseAdapter(getActivity(), vendormodel.getWarehouses());
                            binding.recyclerVendor.setAdapter(wareHouseAdapter);
                            binding.recyclerVendor.setLayoutManager(layoutManager);

                        }
                        wareHouseAdapter.notifyDataSetChanged();
                    }
                }catch (JSONException e) {
                    throw new RuntimeException(e);
                }


            }
        }, error -> {
            binding.noDataFound.setVisibility(View.VISIBLE);
            binding.recyclerVendor.setVisibility(View.GONE);
        });
        queue.add(jsonObjectRequest);
    }

}