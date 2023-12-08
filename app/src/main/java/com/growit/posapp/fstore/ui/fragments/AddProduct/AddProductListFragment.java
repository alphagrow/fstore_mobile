package com.growit.posapp.fstore.ui.fragments.AddProduct;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
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
import com.growit.posapp.fstore.adapters.ProductListAdapter;
import com.growit.posapp.fstore.adapters.StoreInventoryAdapters;
import com.growit.posapp.fstore.databinding.FragmentAddProductListBinding;
import com.growit.posapp.fstore.model.Product;
import com.growit.posapp.fstore.model.StockInventoryModel;
import com.growit.posapp.fstore.model.Value;
import com.growit.posapp.fstore.ui.fragments.AddCustomerFragment;
import com.growit.posapp.fstore.utils.ApiConstants;
import com.growit.posapp.fstore.utils.SessionManagement;
import com.growit.posapp.fstore.utils.Utility;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;


public class AddProductListFragment extends Fragment {
    FragmentAddProductListBinding binding;
    StockInventoryModel stockInventoryModel;
    AddProductListAdapter adapter;
    protected List<Product> productList = new ArrayList<>();
    Activity contexts;
    List<Value> crop_mode = null;
    String crop_id;
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding= DataBindingUtil.inflate(inflater,R.layout.fragment_add_product_list, container, false);
        init();
        return binding.getRoot();
    }
    private void init(){
        GridLayoutManager layoutManager = new GridLayoutManager(getActivity(), 1, LinearLayoutManager.VERTICAL, false);
        binding.recycler.setLayoutManager(layoutManager);
        if (getArguments() != null) {
            crop_mode = (List<Value>) getArguments().getSerializable("crop_list");
         int  position = getArguments().getInt("position");
            crop_id =String.valueOf(crop_mode.get(position).getValueId());
            if (Utility.isNetworkAvailable(getContext())) {
                getProductList(crop_id);
            } else {
                Toast.makeText(getContext(), R.string.NETWORK_GONE, Toast.LENGTH_SHORT).show();

            }
        }

        binding.refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                binding.refreshLayout.setRefreshing(false);
                if (Utility.isNetworkAvailable(getContext())) {
                    getProductList(crop_id);
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
        Log.d("product_list",url);
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
                        JSONArray jsonArray = obj.getJSONArray("data");
                        JSONArray productArray = jsonArray.getJSONObject(0).getJSONArray("products");
                        productList = new ArrayList<>();
                        if (productArray.length() > 0) {
                            for (int i = 0; i < productArray.length(); i++) {
                                Product product = new Product();
                                JSONObject data = productArray.getJSONObject(i);
                                int ID = data.optInt("product_id");
                                String name = data.optString("product_name");
                                double price = data.optDouble("list_price");
                                product.setProductID(ID + "");
                                product.setProductName(name);
                                product.setPrice(price);
                                String image = "";
                                if (data.opt("image_url").equals(false)) {
                                    image = "";
                                } else {
                                    image = data.optString("image_url");
                                }
                                product.setProductImage(image);
                                productList.add(product);
                            }
                            if (productList == null || productList.size() == 0) {
                                binding.noItem.setVisibility(View.GONE);
                            } else {
                                binding.noItem.setVisibility(View.GONE);

                                LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
                                layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
                                adapter = new AddProductListAdapter(getActivity(), productList);
                                binding.recycler.setAdapter(adapter);
                                binding.recycler.setLayoutManager(layoutManager);

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