package com.growit.posapp.fstore.ui.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.growit.posapp.fstore.R;
import com.growit.posapp.fstore.adapters.StoreInventoryAdapters;
import com.growit.posapp.fstore.model.StockInventoryModel;
import com.growit.posapp.fstore.utils.ApiConstants;
import com.growit.posapp.fstore.utils.SessionManagement;
import com.growit.posapp.fstore.utils.Utility;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;


public class StoreInventoryFragment extends Fragment {

    StockInventoryModel stockInventoryModel;
    private RecyclerView recyclerView;
    StoreInventoryAdapters orderHistoryAdapter;
    TextView noDataFound,total_order_text,add_text;

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
        if (Utility.isNetworkAvailable(getActivity())) {
            getStoreInventory();
        }else {
            Toast.makeText(getActivity(), R.string.NETWORK_GONE, Toast.LENGTH_SHORT).show();
        }

        noDataFound.setOnClickListener(v -> getStoreInventory());
        return view;
    }

    private void getStoreInventory() {
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
                            noDataFound.setVisibility(View.VISIBLE);
                            recyclerView.setVisibility(View.GONE);
                        } else {
                            noDataFound.setVisibility(View.GONE);
                            recyclerView.setVisibility(View.VISIBLE);
                            LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
                            layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
                            total_order_text.setText("Total : "+stockInventoryModel.getData().size()+" Products ");
                            orderHistoryAdapter = new StoreInventoryAdapters(getActivity(), stockInventoryModel.getData());
                            recyclerView.setAdapter(orderHistoryAdapter);
                            recyclerView.setLayoutManager(layoutManager);

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

}