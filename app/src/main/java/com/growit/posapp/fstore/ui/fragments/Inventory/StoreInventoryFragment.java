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
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.growit.posapp.fstore.MainActivity;
import com.growit.posapp.fstore.R;
import com.growit.posapp.fstore.adapters.AddProductListAdapter;
import com.growit.posapp.fstore.adapters.StoreInventoryAdapters;
import com.growit.posapp.fstore.model.ExtraVariantData;
import com.growit.posapp.fstore.model.Product;
import com.growit.posapp.fstore.model.Purchase.PurchaseModel;
import com.growit.posapp.fstore.model.StockInventoryModel;
import com.growit.posapp.fstore.model.VendorModelList;
import com.growit.posapp.fstore.model.WarehouseModel;
import com.growit.posapp.fstore.utils.ApiConstants;
import com.growit.posapp.fstore.utils.RecyclerItemClickListener;
import com.growit.posapp.fstore.utils.SessionManagement;
import com.growit.posapp.fstore.utils.Utility;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;


public class StoreInventoryFragment extends Fragment {

    protected List<Product> productList = new ArrayList<>();
    private RecyclerView recyclerView;
    StoreInventoryAdapters orderHistoryAdapter;
    TextView noDataFound,total_order_text,add_text;
     EditText seacrEditTxt;
     ImageView backBtn,gif_load;
    private boolean isSearch = false;
    private List<Product> search_product=new ArrayList<>();
    public StoreInventoryFragment() {
        // Required empty public constructor
    }


    public static StoreInventoryFragment newInstance() {
        StoreInventoryFragment fragment = new StoreInventoryFragment();
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
        add_text = view.findViewById(R.id.add_text);
        seacrEditTxt = view.findViewById(R.id.seacrEditTxt);
        backBtn = view.findViewById(R.id.backBtn);
        gif_load = view.findViewById(R.id.gif_loader);
        Glide.with(getActivity()).load(R.drawable.growit_gif_02).into(gif_load);
        gif_load.setVisibility(View.VISIBLE);
        if (Utility.isNetworkAvailable(getActivity())) {
            getStoreInventory();
        }else {
            Toast.makeText(getActivity(), R.string.NETWORK_GONE, Toast.LENGTH_SHORT).show();
        }

        noDataFound.setOnClickListener(v -> getStoreInventory());
        recyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(getActivity(), recyclerView, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        Bundle bundle = new Bundle();

                        bundle.putString("PID", productList.get(position).getProductID());
                        Fragment fragment = StoreInventoryDetailFragment.newInstance();
                        if(isSearch) {
                            bundle.putSerializable("PID", (Serializable) search_product.get(position).getProductID());

                        }else{
                            bundle.putSerializable("PID", (Serializable) productList.get(position).getProductID());

                        }
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
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               startActivity(new Intent(getActivity(),MainActivity.class));
               getActivity().finish();
            }
        });
        seacrEditTxt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterList(s.toString());
                if (s.length() > 0) {
                    isSearch = true;
                } else {
                    isSearch = false;
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        return view;
    }

    private void getStoreInventory() {
        SessionManagement sm = new SessionManagement(getActivity());
        RequestQueue queue = Volley.newRequestQueue(getActivity());
        String url = ApiConstants.BASE_URL + ApiConstants.GET_STOCK_QUANT + "user_id=" + sm.getUserID() + "&" + "token=" + sm.getJWTToken();
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
                        Utility.dismissDialoge();
                        JSONArray jsonArray = obj.getJSONArray("data");
                        JSONArray productArray = jsonArray.getJSONObject(0).getJSONArray("products");
                        productList = new ArrayList<>();
                        if (productArray.length() > 0) {
                            for (int i = 0; i < productArray.length(); i++) {
                                Product product = new Product();
                                JSONObject data = productArray.getJSONObject(i);
                                int ID = data.optInt("product_id");
                                String name = data.optString("product_name");
                                product.setProductID(ID + "");
                                product.setProductName(name);
                                String image = "";
                                if (data.opt("image_url").equals(false)) {
                                    image = "";
                                } else {
                                    image = data.optString("image_url");
                                }
                                product.setProductImage(image);
                                productList.add(product);
                            }
                        }
                        if (productList == null || productList.size() == 0) {
                            noDataFound.setVisibility(View.VISIBLE);
                            recyclerView.setVisibility(View.GONE);
                        } else {
                            noDataFound.setVisibility(View.GONE);
                            recyclerView.setVisibility(View.VISIBLE);
                            LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
                            layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
                            total_order_text.setText("Total : "+productList.size()+" Products ");
                            orderHistoryAdapter = new StoreInventoryAdapters(getActivity(), productList);
                            recyclerView.setAdapter(orderHistoryAdapter);
                            recyclerView.setLayoutManager(layoutManager);
                            gif_load.setVisibility(View.GONE);
                        }
                    }
                }catch (JSONException e) {
                    throw new RuntimeException(e);
                }


            }
        }, error -> {
            noDataFound.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        });
        queue.add(jsonObjectRequest);
    }

//    private void filterList(String text){
//
//            ArrayList<Product> model = new ArrayList<>();
//            for (Product detail : productList){
//                if (detail.getProductName().toLowerCase().contains(text.toLowerCase())){
//                    model.add(detail);
//                }
//            }
//
//        orderHistoryAdapter.updateList(model);
//
//    }


    private void filterList(String text) {
        // creating a new array list to filter our data.
        ArrayList<Product> filteredList = new ArrayList<>();

        // running a for loop to compare elements.
        for (Product item : productList) {
            // checking if the entered string matched with any item of our recycler view.
            if (item.getProductName().toLowerCase().contains(text.toLowerCase())) {
                // if the item is matched we are
                // adding it to our filtered list.
                filteredList.add(item);
            }
        }
        if (filteredList.isEmpty()) {
            // if no item is added in filtered list we are
            // displaying a toast message as no data found.
            Toast.makeText(getActivity(), R.string.NO_DATA, Toast.LENGTH_SHORT).show();
        } else {
            // at last we are passing that filtered
            // list to our adapter class.

            search_product=filteredList;
            orderHistoryAdapter.updateList(filteredList);
        }
    }

}