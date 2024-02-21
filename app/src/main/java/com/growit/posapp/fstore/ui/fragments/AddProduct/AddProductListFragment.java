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
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.growit.posapp.fstore.MainActivity;
import com.growit.posapp.fstore.R;
import com.growit.posapp.fstore.adapters.AddProductListAdapter;
import com.growit.posapp.fstore.adapters.AllAddProductListAdapter;
import com.growit.posapp.fstore.databinding.FragmentAddProductListBinding;
import com.growit.posapp.fstore.databinding.FragmentVendorBinding;
import com.growit.posapp.fstore.model.Product;
import com.growit.posapp.fstore.model.Purchase.PurchaseModel;
import com.growit.posapp.fstore.model.Purchase.PurchaseProductModel;
import com.growit.posapp.fstore.model.Value;
import com.growit.posapp.fstore.model.VendorModelList;
import com.growit.posapp.fstore.model.WarehouseModel;
import com.growit.posapp.fstore.ui.fragments.POSCategory.POSCategoryListFragment;
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
    ImageView gif_load;
    RecyclerView recycler;
    TextView noDataFound,totalCustomerText;
    SwipeRefreshLayout refreshLayout;
    ImageView backBtn;
    TextView addText;
    EditText seacrEditTxt;

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
//        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_add_product_list, container, false);
//        binding= DataBindingUtil.inflate(inflater, R.layout.fragment_add_product_list,container, false);
        View view = inflater.inflate(R.layout.fragment_add_product_list, container, false);


        if (getArguments() != null) {


        }
        init(view);
        return  view;
//        return binding.getRoot();
    }

    private void init(View view) {
        gif_load =view.findViewById(R.id.gif_load);
        recycler =view.findViewById(R.id.recycler);
        noDataFound =view.findViewById(R.id.noDataFound);
        totalCustomerText =view.findViewById(R.id.total_customer_text);
        refreshLayout =view.findViewById(R.id.refreshLayout);
        backBtn =view.findViewById(R.id.backBtn);
        addText =view.findViewById(R.id.add_text);
        seacrEditTxt =view.findViewById(R.id.seacrEditTxt);

        Glide.with(getActivity()).load(R.drawable.growit_gif_02).into(gif_load);
//        gif_load.setVisibility(View.VISIBLE);
        GridLayoutManager layoutManager = new GridLayoutManager(getActivity(), 1, LinearLayoutManager.VERTICAL, false);
       recycler.setLayoutManager(layoutManager);

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

//        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
//            @Override
//            public void onRefresh() {
//                refreshLayout.setRefreshing(false);
//                gif_load.setVisibility(View.VISIBLE);
//                if (Utility.isNetworkAvailable(getContext())) {
//                    if(product_data.equals("crop_product")) {
//                        crop_mode = (List<Value>) getArguments().getSerializable("crop_list");
//                        crop_id = String.valueOf(crop_mode.get(position).getValueId());
//                        getProductList(crop_id);
//                    }else {
//                        getAllProductList();
//                    }
//
//                } else {
//                    Toast.makeText(getContext(), R.string.NETWORK_GONE, Toast.LENGTH_SHORT).show();
//
//                }
//
//            }
//        });
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragment = POSCategoryListFragment.newInstance();
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.flContent, fragment).commit();
            }
        });

       addText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragment = AddProductFragment.newInstance();
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.flContent, fragment).commit();
            }
        });

       seacrEditTxt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s!=null) {
                    filterList(s.toString());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

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
        //String url = ApiConstants.BASE_URL + ApiConstants.GET_PRODUCT_LIST + "user_id=" + sm.getUserID() + "&" + "pos_category_id=" + id + "&" + "token=" + sm.getJWTToken();
        String url = ApiConstants.BASE_URL + ApiConstants.GET_PRODUCT_LIST + "user_id=" + sm.getUserID() + "&" + "pos_category_id=" + pos_category_id + "&" + "token=" + sm.getJWTToken();
        Log.d("product_list", url);
//        Utility.showDialoge("Please wait while a moment...", getActivity());
      gif_load.setVisibility(View.VISIBLE);
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
                        Toast.makeText(contexts,"Success", Toast.LENGTH_SHORT).show();

//                        Utility.dismissDialoge();
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
                           gif_load.setVisibility(View.GONE);
                            noDataFound.setVisibility(View.GONE);
                        } else {
                           gif_load.setVisibility(View.GONE);
                           noDataFound.setVisibility(View.GONE);
                           totalCustomerText.setText("Total: " + purchaseProductModel.size() + " " + "Products");

                            LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
                            layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
                            adapter = new AddProductListAdapter(getActivity(), purchaseProductModel,crop_id,crop_name);
                          recycler.setAdapter(adapter);
                           recycler.setLayoutManager(layoutManager);

                        }


                        //                               }
                    }
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
        }, error -> Toast.makeText(contexts, R.string.JSONDATA_NULL, Toast.LENGTH_SHORT).show());
//       gif_load.setVisibility(View.GONE);
        queue.add(jsonObjectRequest);

    }
    private void filterList(String text){
        if(product_data.equals("crop_product")) {
            ArrayList<PurchaseProductModel> model = new ArrayList<>();
            for (PurchaseProductModel detail : purchaseProductModel){
                if (detail.getProductName().toLowerCase().contains(text.toLowerCase())){
                    model.add(detail);
                }
            }

            adapter.updateList(model);
        } else {
            ArrayList<PurchaseProductModel> model = new ArrayList<>();
            for (PurchaseProductModel detail : purchaseProductModel){
                if (detail.getProductName().toLowerCase().contains(text.toLowerCase())){
                    model.add(detail);
                }
            }

            all_adapter.updateList(model);
        }

    }
    private void getAllProductList() {
        SessionManagement sm = new SessionManagement(getActivity());
        RequestQueue queue = Volley.newRequestQueue(getActivity());
        //        String url = ApiConstants.BASE_URL + ApiConstants.GET_PRODUCT_LIST + "user_id=" + sm.getUserID() + "&" + "pos_category_id=" + id + "&" + "token=" + sm.getJWTToken();
        String url = ApiConstants.BASE_URL + ApiConstants.GET_ALL_PRODUCT_LIST + "user_id=" + sm.getUserID() + "&" + "token=" + sm.getJWTToken();
        Log.d("product_list", url);
//      Utility.showDialoge("Please wait while a moment...", getActivity());
       gif_load.setVisibility(View.VISIBLE);
        long beginTime = System.currentTimeMillis();
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.v("Response", response.toString());
                JSONObject obj = null;
                try {
//                    Toast.makeText(contexts, "Success", Toast.LENGTH_SHORT).show();

                    obj = new JSONObject(response.toString());
                    int statusCode = obj.optInt("statuscode");
                    String status = obj.optString("status");
                    if (statusCode == 200 && status.equalsIgnoreCase("success")) {

//                        Utility.dismissDialoge();
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
                           noDataFound.setVisibility(View.GONE);
                        } else {
                           noDataFound.setVisibility(View.GONE);
                           totalCustomerText.setText("Total: " + purchaseProductModel.size() + " " + "All Products");
                            long responseTime = System.currentTimeMillis() - beginTime;
                            Log.v("Response_time", String.valueOf(responseTime));
                            LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
                            layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
                            all_adapter = new AllAddProductListAdapter(getActivity(), purchaseProductModel);
                           recycler.setAdapter(all_adapter);
                           recycler.setLayoutManager(layoutManager);
                               gif_load.setVisibility(View.GONE);

                        }


                        //                            }
                    }
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
        }, error -> Toast.makeText(contexts, R.string.JSONDATA_NULL, Toast.LENGTH_SHORT).show());
//       gif_load.setVisibility(View.GONE);
        queue.add(jsonObjectRequest);

    }

}