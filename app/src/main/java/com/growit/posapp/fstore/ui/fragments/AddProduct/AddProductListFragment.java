package com.growit.posapp.fstore.ui.fragments.AddProduct;

import android.content.Intent;
import android.os.Bundle;

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
import com.growit.posapp.fstore.adapters.StoreInventoryAdapters;
import com.growit.posapp.fstore.databinding.FragmentAddProductListBinding;
import com.growit.posapp.fstore.model.StockInventoryModel;
import com.growit.posapp.fstore.ui.fragments.AddCustomerFragment;
import com.growit.posapp.fstore.utils.ApiConstants;
import com.growit.posapp.fstore.utils.SessionManagement;
import com.growit.posapp.fstore.utils.Utility;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;


public class AddProductListFragment extends Fragment {
    FragmentAddProductListBinding binding;
    StockInventoryModel stockInventoryModel;
    AddProductListAdapter adapter;
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
        if (Utility.isNetworkAvailable(getContext())) {
            getProductList();
        } else {
            Toast.makeText(getContext(), R.string.NETWORK_GONE, Toast.LENGTH_SHORT).show();

        }
        binding.refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                binding.refreshLayout.setRefreshing(false);
                if (Utility.isNetworkAvailable(getContext())) {
                    getProductList();
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
    private void getProductList(){
        SessionManagement sm = new SessionManagement(getActivity());
        RequestQueue queue = Volley.newRequestQueue(getActivity());//162.246.254.203:8069
        String url = ApiConstants.BASE_URL + ApiConstants.GET_STOCK_QUANT + "user_id=" + sm.getUserID() + "&" + "token=" + sm.getJWTToken();
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
                        Type listType = new TypeToken<StockInventoryModel>() {
                        }.getType();

                        stockInventoryModel = gson.fromJson(response.toString(), listType);
                        if (stockInventoryModel.getData() == null || stockInventoryModel.getData().size() == 0) {
                            binding.noItem.setVisibility(View.VISIBLE);
                            binding.noItem.setVisibility(View.GONE);
                        } else {
                            binding.noItem.setVisibility(View.GONE);
                            binding.recycler.setVisibility(View.VISIBLE);
                            LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
                            layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
                            adapter = new AddProductListAdapter(getActivity(), stockInventoryModel.getData());
                            binding.recycler.setAdapter(adapter);
                            binding.recycler.setLayoutManager(layoutManager);

                        }
                    }
                }catch (JSONException e) {
                    throw new RuntimeException(e);
                }


            }
        }, error -> {
            binding.noItem.setVisibility(View.VISIBLE);
            binding.recycler.setVisibility(View.GONE);
        });
        queue.add(jsonObjectRequest);
    }
}