package com.growit.posapp.fstore.ui.fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.growit.posapp.fstore.R;
import com.growit.posapp.fstore.adapters.CropAdapter;
import com.growit.posapp.fstore.adapters.ProductListAdapter;
import com.growit.posapp.fstore.model.Product;
import com.growit.posapp.fstore.model.Value;
import com.growit.posapp.fstore.utils.ApiConstants;
import com.growit.posapp.fstore.utils.RecyclerItemClickListener;
import com.growit.posapp.fstore.utils.SessionManagement;
import com.growit.posapp.fstore.utils.Utility;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ProductListFragment extends Fragment {
    protected List<Product> productList = new ArrayList<>();
    List<Value> cropList = new ArrayList<>();
    private RecyclerView recyclerView, croplist_view;
    ProductListAdapter productListAdapter;
    EditText searchEditTxt;
    private String cropID = "";
    private String cropName = "";
    CropAdapter cropAdapter=null;
    SwipeRefreshLayout refreshLayout;
    public static ProductListFragment newInstance() {
        return new ProductListFragment();
    }
    Activity contexts;
    TextView noDataFound;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.home_fragment, parent, false);
        recyclerView = view.findViewById(R.id.recycler_view);
        croplist_view = view.findViewById(R.id.croplist_view);
        searchEditTxt = view.findViewById(R.id.seacrEditTxt);
        refreshLayout = view.findViewById(R.id.refreshLayout);
        noDataFound = view.findViewById(R.id.noDataFound);


        GridLayoutManager layoutManager = new GridLayoutManager(getActivity(), 3, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        searchEditTxt.setHint("Search...");
        if (Utility.isNetworkAvailable(getActivity())) {
            getCropRequest();
        }else {
            Toast.makeText(getActivity(), R.string.NETWORK_GONE, Toast.LENGTH_SHORT).show();

        }
        searchEditTxt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filter(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshLayout.setRefreshing(false);
                if (!Utility.isNetworkAvailable(getActivity())) {
                    Toast.makeText(getActivity(), R.string.NETWORK_GONE, Toast.LENGTH_SHORT).show();
                    return;
                }
                getCropRequest();
            }
        });
        refreshLayout.setColorSchemeColors(0000);


        croplist_view.addOnItemTouchListener(
                new RecyclerItemClickListener(getActivity(), croplist_view, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        cropID = cropList.get(position).getValueId() + "";
                        cropName=cropList.get(position).getValueName();
                        GridLayoutManager layoutManager = new GridLayoutManager(getActivity(), 1);
                        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
                        cropAdapter = new CropAdapter(getActivity(), cropList,position);
                        croplist_view.setAdapter(cropAdapter);
                        croplist_view.setLayoutManager(layoutManager);
                        prepareProducts(cropID);
                    }

                    @Override
                    public void onLongItemClick(View view, int position) {
                        // do whatever
                    }
                })
        );
        recyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(getActivity(), recyclerView, new RecyclerItemClickListener.OnItemClickListener() {
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

        return view;
    }

    private void filter(String text) {
        // creating a new array list to filter our data.
        ArrayList<Product> filteredList = new ArrayList<>();
        // running a for loop to compare elements.
        for (Product item : productList) {
            if (item.getProductName().toLowerCase().contains(text.toLowerCase())) {
                filteredList.add(item);
            }
        }
        if (filteredList.isEmpty()) {

            Toast.makeText(getActivity(), R.string.NO_DATA, Toast.LENGTH_SHORT).show();
        } else {

            productListAdapter.filterList(filteredList);
        }
    }
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        contexts = getActivity();

    }
    private void prepareProducts(String id) {
        SessionManagement sm = new SessionManagement(contexts);
        RequestQueue queue = Volley.newRequestQueue(contexts);
        String url = ApiConstants.BASE_URL + ApiConstants.GET_PRODUCT_LIST + "user_id=" + sm.getUserID() + "&" + "pos_category_id=" + id + "&" + "token=" + sm.getJWTToken();
      Log.d("product_list",url);
    //    Utility.showDialoge("Please wait while a moment...", getActivity());
        long beginTime = System.currentTimeMillis();
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.v("Response", response.toString());

                long responseTime = System.currentTimeMillis() - beginTime;

                JSONObject obj = null;
                try {
                    obj = new JSONObject(response.toString());
                    int statusCode = obj.optInt("statuscode");
                    String status = obj.optString("status");
                    if (statusCode == 200 && status.equalsIgnoreCase("success")) {
                 //       Utility.dismissDialoge();
                        productList.clear();
                        JSONArray jsonArray = obj.getJSONArray("data");
                        JSONArray productArray = jsonArray.getJSONObject(0).getJSONArray("products");
//                        productList = new ArrayList<>();

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
                            Log.v("Response_time", String.valueOf(responseTime));
                            productListAdapter = new ProductListAdapter(getActivity(), productList);
                            recyclerView.setAdapter(productListAdapter);

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
                            croplist_view.setAdapter(cropAdapter);
                            croplist_view.setLayoutManager(layoutManager);

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

