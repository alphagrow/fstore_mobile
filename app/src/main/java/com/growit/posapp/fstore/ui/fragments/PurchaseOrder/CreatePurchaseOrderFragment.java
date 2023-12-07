package com.growit.posapp.fstore.ui.fragments.PurchaseOrder;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

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
import com.growit.posapp.fstore.R;
import com.growit.posapp.fstore.adapters.CropAdapter;
import com.growit.posapp.fstore.adapters.ProductListAdapter;
import com.growit.posapp.fstore.databinding.FragmentCreatePurchaseOrderBinding;
import com.growit.posapp.fstore.model.Product;
import com.growit.posapp.fstore.model.Value;
import com.growit.posapp.fstore.ui.fragments.ProductDetailFragment;
import com.growit.posapp.fstore.ui.fragments.SaleManagement.VendorListFragment;
import com.growit.posapp.fstore.utils.ApiConstants;
import com.growit.posapp.fstore.utils.RecyclerItemClickListener;
import com.growit.posapp.fstore.utils.SessionManagement;
import com.growit.posapp.fstore.utils.Utility;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class CreatePurchaseOrderFragment extends Fragment {

    FragmentCreatePurchaseOrderBinding binding;


    Activity contexts;
    List<Value> cropList = new ArrayList<>();
    ProductListAdapter productListAdapter;
    protected List<Product> productList = new ArrayList<>();
    private String cropID = "";
    private String cropName = "";
    CropAdapter cropAdapter=null;
    public CreatePurchaseOrderFragment() {
        // Required empty public constructor
    }

    public static CreatePurchaseOrderFragment newInstance() {
        return new CreatePurchaseOrderFragment();
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
    }
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        contexts = getActivity();

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
      //  return inflater.inflate(R.layout.fragment_create_purchase_order, container, false);
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_create_purchase_order, container, false);
        init();
        return binding.getRoot();
    }
    private void init(){
        GridLayoutManager layoutManager = new GridLayoutManager(getActivity(), 3);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        binding.recyclerView.setLayoutManager(layoutManager);

        if (Utility.isNetworkAvailable(getActivity())) {
            getCropRequest();
        }else {
            Toast.makeText(getActivity(), R.string.NETWORK_GONE, Toast.LENGTH_SHORT).show();

        }
        binding.croplistView.addOnItemTouchListener(
                new RecyclerItemClickListener(getActivity(),  binding.croplistView, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        cropID = cropList.get(position).getValueId() + "";
                        cropName=cropList.get(position).getValueName();
                        GridLayoutManager layoutManager = new GridLayoutManager(getActivity(), 1);
                        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
                        cropAdapter = new CropAdapter(getActivity(), cropList,position);
                        binding.croplistView.setAdapter(cropAdapter);
                        binding.croplistView.setLayoutManager(layoutManager);
                        prepareProducts(cropID);
                    }

                    @Override
                    public void onLongItemClick(View view, int position) {
                        // do whatever
                    }
                })
        );
        binding.recyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(getActivity(),  binding.recyclerView, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        Bundle bundle = new Bundle();
                        bundle.putString("PID", productList.get(position).getProductID());
                        bundle.putString("CPID", cropID);
                        bundle.putString("CropName",cropName);
                        Fragment fragment = ProductDetailFragment.newInstance();
                        fragment.setArguments(bundle);
                        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                        fragmentManager.beginTransaction().replace(R.id.flContent, fragment).commit();
                    }

                    @Override
                    public void onLongItemClick(View view, int position) {
                        // do whatever
                    }
                })
        );

    }

    private void prepareProducts(String id) {
        SessionManagement sm = new SessionManagement(contexts);
        RequestQueue queue = Volley.newRequestQueue(contexts);
        Utility.showDialoge("Please wait while a moment...", getActivity());
        String url = ApiConstants.BASE_URL + ApiConstants.GET_PRODUCT_LIST + "user_id=" + sm.getUserID() + "&" + "pos_category_id=" + id + "&" + "token=" + sm.getJWTToken();
        Log.d("product_list",url);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.v("Response", response.toString());
                JSONObject obj = null;
                try {
                    Utility.dismissDialoge();
                    obj = new JSONObject(response.toString());
                    int statusCode = obj.optInt("statuscode");
                    String status = obj.optString("status");
                    if (statusCode == 200 && status.equalsIgnoreCase("success")) {
                        JSONArray jsonArray = obj.getJSONArray("data");
                        JSONArray productArray = jsonArray.getJSONObject(0).getJSONArray("products");
                         // productList = new ArrayList<>();
                        productList.clear();
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
                            productListAdapter = new ProductListAdapter(getActivity(), productList);
                            binding.recyclerView.setAdapter(productListAdapter);

                        }

                    }
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
        }, error -> Toast.makeText(contexts, R.string.JSONDATA_NULL, Toast.LENGTH_SHORT).show());
        queue.add(jsonObjectRequest);

    }
    private void getCropRequest() {
        SessionManagement sm = new SessionManagement(contexts);
        RequestQueue queue = Volley.newRequestQueue(contexts);
        String url = ApiConstants.BASE_URL + ApiConstants.GET_ALL_CROPS + "user_id=" + sm.getUserID() + "&" + "token=" + sm.getJWTToken();
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
                        JSONArray jsonArray = obj.getJSONArray("data");
                        cropList = new ArrayList<>();
                        if (jsonArray.length() > 0) {
                            for (int i = 0; i < jsonArray.length(); i++) {
                                Value cropPattern = new Value();
                                JSONObject data = jsonArray.getJSONObject(i);
                                Integer cropID = data.optInt("category_id");
                                String name = data.optString("name");
                                String image_url = data.optString("image_url");
                                cropPattern.setValueId(Integer.valueOf(cropID + ""));
                                cropPattern.setValueName(name);
                                cropPattern.setImage_url(image_url);
                                cropPattern.setSelectedPosition(0);
                                cropList.add(cropPattern);
                            }
                            GridLayoutManager layoutManager = new GridLayoutManager(getActivity(), 1);
                            layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
                            cropAdapter = new CropAdapter(getActivity(), cropList,-1);
                            binding.croplistView.setAdapter(cropAdapter);
                            binding.croplistView.setLayoutManager(layoutManager);

                            cropID = cropList.get(0).getValueId() + "";
                            cropName=cropList.get(0).getValueName();
                            prepareProducts(cropID);
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

