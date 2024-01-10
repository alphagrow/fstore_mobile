package com.growit.posapp.fstore.ui.fragments.AddProduct;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
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
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.growit.posapp.fstore.MainActivity;
import com.growit.posapp.fstore.R;
import com.growit.posapp.fstore.adapters.AddProductListAdapter;
import com.growit.posapp.fstore.adapters.AllAddProductListAdapter;
import com.growit.posapp.fstore.databinding.FragmentAddProductListBinding;
import com.growit.posapp.fstore.model.Product;
import com.growit.posapp.fstore.model.Purchase.PurchaseModel;
import com.growit.posapp.fstore.model.Purchase.PurchaseProductModel;
import com.growit.posapp.fstore.model.Value;
import com.growit.posapp.fstore.utils.ApiConstants;
import com.growit.posapp.fstore.utils.SessionManagement;
import com.growit.posapp.fstore.utils.Utility;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;


public class AddProductListFragment extends Fragment {
    FragmentAddProductListBinding binding;

    AddProductListAdapter adapter;
    AllAddProductListAdapter all_adapter;
    protected List<Product> productList = new ArrayList<>();
    Activity contexts;
    List<Value> crop_mode = null;
    String crop_id,crop_name;

    PurchaseModel model;
    String product_data;
    List<PurchaseProductModel> purchaseProductModel;
    int position;
    public AddProductListFragment() {
        // Required empty public constructor
    }

    public static AddProductListFragment newInstance() {
        return new AddProductListFragment();
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
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_add_product_list, container, false);
        if (getArguments() != null) {


        }
        init();
        return binding.getRoot();
    }

    private void init() {
        GridLayoutManager layoutManager = new GridLayoutManager(getActivity(), 1, LinearLayoutManager.VERTICAL, false);
        binding.recycler.setLayoutManager(layoutManager);
        if (getArguments() != null) {
            product_data = getArguments().getString("product_list");
             position = getArguments().getInt("position");
            if (Utility.isNetworkAvailable(getContext())) {
                if(product_data.equals("crop_product")) {
                    crop_mode = (List<Value>) getArguments().getSerializable("crop_list");
                    crop_id = String.valueOf(crop_mode.get(position).getValueId());
                    getProductList(crop_id);
                }else {
                    getAllProductList();
                }
            } else {
                Toast.makeText(getContext(), R.string.NETWORK_GONE, Toast.LENGTH_SHORT).show();

            }
        }

        binding.refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                binding.refreshLayout.setRefreshing(false);
                if (Utility.isNetworkAvailable(getContext())) {
                    if(product_data.equals("crop_product")) {
                        crop_mode = (List<Value>) getArguments().getSerializable("crop_list");
                        crop_id = String.valueOf(crop_mode.get(position).getValueId());
                        getProductList(crop_id);
                    }else {
                        getAllProductList();
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
                Fragment fragment = AddProductFragment.newInstance();
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.flContent, fragment).commit();
            }
        });
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        contexts = getActivity();

    }


    private void getProductList(String pos_category_id) {
        SessionManagement sm = new SessionManagement(getActivity());
        RequestQueue queue = Volley.newRequestQueue(getActivity());
        //        String url = ApiConstants.BASE_URL + ApiConstants.GET_PRODUCT_LIST + "user_id=" + sm.getUserID() + "&" + "pos_category_id=" + id + "&" + "token=" + sm.getJWTToken();
        String url = ApiConstants.BASE_URL + ApiConstants.GET_PRODUCT_LIST + "user_id=" + sm.getUserID() + "&" + "pos_category_id=" + pos_category_id + "&" + "token=" + sm.getJWTToken();
        Log.d("product_list", url);
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
                        Type listType = new TypeToken<PurchaseModel>() {
                        }.getType();
                        model = gson.fromJson(response.toString(), listType);
                        for (int i = 0; i < model.getData().size(); i++) {
                            crop_id = String.valueOf(model.getData().get(i).getCategoryId());
                            crop_name = model.getData().get(i).getCategoryName();
                            purchaseProductModel = model.getData().get(i).getProducts();
                        }
                        if (purchaseProductModel == null || purchaseProductModel.size() == 0) {
                            binding.noDataFound.setVisibility(View.GONE);
                        } else {
                            binding.noDataFound.setVisibility(View.GONE);
                            LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
                            layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
                            adapter = new AddProductListAdapter(getActivity(), purchaseProductModel,crop_id,crop_name);
                            binding.recycler.setAdapter(adapter);
                            binding.recycler.setLayoutManager(layoutManager);

                        }


                        //                               }
                    }
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
        }, error -> Toast.makeText(contexts, R.string.JSONDATA_NULL, Toast.LENGTH_SHORT).show());
        queue.add(jsonObjectRequest);

    }
    private void getAllProductList() {
        SessionManagement sm = new SessionManagement(getActivity());
        RequestQueue queue = Volley.newRequestQueue(getActivity());
        //        String url = ApiConstants.BASE_URL + ApiConstants.GET_PRODUCT_LIST + "user_id=" + sm.getUserID() + "&" + "pos_category_id=" + id + "&" + "token=" + sm.getJWTToken();
        String url = ApiConstants.BASE_URL + ApiConstants.GET_PRODUCT_LIST + "user_id=" + sm.getUserID() + "&" + "token=" + sm.getJWTToken();
        Log.d("product_list", url);
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
                        Type listType = new TypeToken<PurchaseModel>() {
                        }.getType();
                        model = gson.fromJson(response.toString(), listType);
                        for (int i = 0; i < model.getData().size(); i++) {
                            crop_id = String.valueOf(model.getData().get(i).getCategoryId());
                            crop_name = model.getData().get(i).getCategoryName();
                            purchaseProductModel = model.getData().get(i).getProducts();
                        }
                        if (purchaseProductModel == null || purchaseProductModel.size() == 0) {
                            binding.noDataFound.setVisibility(View.GONE);
                        } else {
                            binding.noDataFound.setVisibility(View.GONE);
                            LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
                            layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
                            all_adapter = new AllAddProductListAdapter(getActivity(), purchaseProductModel);
                            binding.recycler.setAdapter(all_adapter);
                            binding.recycler.setLayoutManager(layoutManager);

                        }


                        //                               }
                    }
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
        }, error -> Toast.makeText(contexts, R.string.JSONDATA_NULL, Toast.LENGTH_SHORT).show());
        queue.add(jsonObjectRequest);

    }

}