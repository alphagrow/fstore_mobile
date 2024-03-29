package com.growit.posapp.fstore.ui.fragments.Inventory;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import com.growit.posapp.fstore.MainActivity;
import com.growit.posapp.fstore.R;
import com.growit.posapp.fstore.adapters.StoreInventoryDetailAdapter;
import com.growit.posapp.fstore.model.Product;
import com.growit.posapp.fstore.ui.fragments.AddProduct.ProductExtraPriceListFragment;
import com.growit.posapp.fstore.utils.ApiConstants;
import com.growit.posapp.fstore.utils.SessionManagement;
import com.growit.posapp.fstore.utils.Utility;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class StoreInventoryDetailFragment extends Fragment {
    protected List<Product> productList = new ArrayList<>();
    private RecyclerView recyclerView;
    StoreInventoryDetailAdapter orderHistoryAdapter;
    TextView noDataFound,total_order_text,add_text;
    String productID="";
    ImageView backBtn,gif_load;
    EditText seacrEditTxt;
    public StoreInventoryDetailFragment() {
        // Required empty public constructor
    }


    public static StoreInventoryDetailFragment newInstance() {
        StoreInventoryDetailFragment fragment = new StoreInventoryDetailFragment();
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_store_inventory, container, false);
        recyclerView = view.findViewById(R.id.orderRecyclerView);
        noDataFound = view.findViewById(R.id.noDataFound);
        total_order_text = view.findViewById(R.id.total_order_text);
        backBtn = view.findViewById(R.id.backBtn);
        add_text = view.findViewById(R.id.add_text);
        seacrEditTxt = view.findViewById(R.id.seacrEditTxt);
        gif_load = view.findViewById(R.id.gif_loader);
        Glide.with(getActivity()).load(R.drawable.growit_gif_02).into(gif_load);
        gif_load.setVisibility(View.VISIBLE);
        if (getArguments() != null) {
            productID = getArguments().getString("PID");
            if (Utility.isNetworkAvailable(getActivity())) {
                getStoreInventory(productID);
            }else {
                Toast.makeText(getActivity(), R.string.NETWORK_GONE, Toast.LENGTH_SHORT).show();
            }

        }


        noDataFound.setOnClickListener(v -> getStoreInventory(productID));


        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragment = StoreInventoryFragment.newInstance();
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
                filterList(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        return view;
    }

    private void getStoreInventory(String productID) {
        SessionManagement sm = new SessionManagement(getActivity());
        RequestQueue queue = Volley.newRequestQueue(getActivity());
        String url = ApiConstants.BASE_URL + ApiConstants.GET_STOCK_Detail + "user_id=" + sm.getUserID() + "&"+"product_id=" + productID+"&"+ "token=" + sm.getJWTToken();
        gif_load.setVisibility(View.VISIBLE);
        //  String url = ApiConstants.BASE_URL + ApiConstants.GET_STOCK_Detail + "user_id=" + sm.getUserID() + "&" +  "shop_id=" + sm.getShopID() + "&"+"product_id=" + productID+"&"+ "token=" + sm.getJWTToken();
//        Utility.showDialoge("Please wait while a moment...", getActivity());
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
                        gif_load.setVisibility(View.GONE);
                        Utility.dismissDialoge();
                        JSONArray jsonArray = obj.getJSONArray("data");
                        JSONArray productArray = jsonArray.getJSONObject(0).getJSONArray("product_variant_quantities");
                        productList = new ArrayList<>();
                        if (productArray.length() > 0) {
                            for (int i = 0; i < productArray.length(); i++) {
                                Product product = new Product();
                                JSONObject data = productArray.getJSONObject(i);
                                int ID = data.optInt("variant_id");
                                String name = data.optString("variant_display_name");
                                String location_name = data.optString("location_name");
                                double qty = data.optDouble("quantity");
                                product.setProductID(ID + "");
                                product.setProductName(name);
                                product.setLocation_name(location_name);
                                product.setQuantity(qty);
//                                String image = "";
//                                if (data.opt("image_url").equals(false)) {
//                                    image = "";
//                                } else {
//                                    image = data.optString("image_url");
//                                }
//                                product.setProductImage(image);
                                productList.add(product);
                            }
                        }
                        if (productList == null || productList.size() == 0) {
                            noDataFound.setVisibility(View.VISIBLE);
                            noDataFound.setText("Currently Out Of Stock");
                            recyclerView.setVisibility(View.GONE);
                        } else {
                            noDataFound.setVisibility(View.GONE);
                            recyclerView.setVisibility(View.VISIBLE);
                            LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
                            layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
                            total_order_text.setText("Total : "+productList.size()+" Variants ");
                            orderHistoryAdapter = new StoreInventoryDetailAdapter(getActivity(), productList);
                            recyclerView.setAdapter(orderHistoryAdapter);
                            recyclerView.setLayoutManager(layoutManager);

                        }
                    }
                }catch (JSONException e) {
                    throw new RuntimeException(e);
                }


            }
        }, error -> {
            gif_load.setVisibility(View.GONE);
            noDataFound.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        });
        queue.add(jsonObjectRequest);
    }
    private void filterList(String text){
        ArrayList<Product> model = new ArrayList<>();
        for (Product detail : productList){
            if (detail.getProductName().toLowerCase().contains(text.toLowerCase())){
                model.add(detail);
            }
        }

        orderHistoryAdapter.updateList(model);

    }
}