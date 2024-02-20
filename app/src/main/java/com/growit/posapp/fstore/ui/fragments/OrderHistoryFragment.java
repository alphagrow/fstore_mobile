package com.growit.posapp.fstore.ui.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.growit.posapp.fstore.R;
import com.growit.posapp.fstore.adapters.OrderHistoryAdapter;
import com.growit.posapp.fstore.model.ProductDetail;
import com.growit.posapp.fstore.utils.ApiConstants;
import com.growit.posapp.fstore.utils.RecyclerItemClickListener;
import com.growit.posapp.fstore.utils.SessionManagement;
import com.growit.posapp.fstore.utils.Utility;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;


public class OrderHistoryFragment extends Fragment {

    ProductDetail productDetail;
    private RecyclerView recyclerView;
    OrderHistoryAdapter orderHistoryAdapter;
    TextView noDataFound,total_order_text;
    private  String mResponse="";
    ImageView gif_loader;
    public static OrderHistoryFragment newInstance() {
        OrderHistoryFragment fragment = new OrderHistoryFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_order_history, container, false);
        recyclerView = view.findViewById(R.id.orderRecyclerView);
        noDataFound = view.findViewById(R.id.noDataFound);
        total_order_text = view.findViewById(R.id.total_order_text);
        gif_loader =view.findViewById(R.id.gif_loader);
        Glide.with(getActivity()).load(R.drawable.growit_gif_02).into(gif_loader);
        gif_loader.setVisibility(View.VISIBLE);
        if (Utility.isNetworkAvailable(getActivity())) {
            getOrderHistory();
        }else {
            Toast.makeText(getActivity(), R.string.NETWORK_GONE, Toast.LENGTH_SHORT).show();

        }

        noDataFound.setOnClickListener(v -> getOrderHistory());
        recyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(getActivity(), recyclerView, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        Bundle bundle = new Bundle();
                        bundle.putString("OrderDetail", mResponse);
                        bundle.putInt("position", position);
                        Fragment fragment = OrderDetailFragment.newInstance();
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

    private void getOrderHistory() {
        SessionManagement sm = new SessionManagement(getActivity());
        RequestQueue queue = Volley.newRequestQueue(getActivity());//162.246.254.203:8069
        String url = ApiConstants.BASE_URL + ApiConstants.GET_ORDERS_HISTORY + "user_id=" + sm.getUserID() + "&" + "token=" + sm.getJWTToken();
        Log.v("url", url);
//        Utility.showDialoge("Please wait while a moment...", getActivity());
        gif_loader.setVisibility(View.VISIBLE);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
//                Log.v("Response", response.toString());
                mResponse=response.toString();
                JSONObject obj = null;
                try {
                    obj = new JSONObject(response.toString());
                    int statusCode = obj.optInt("statuscode");
                    String status = obj.optString("status");
                    gif_loader.setVisibility(View.GONE);
                    if (statusCode == 200 && status.equalsIgnoreCase("success")) {
//                        Utility.dismissDialoge();
                        Gson gson = new Gson();
                        Type listType = new TypeToken<ProductDetail>() {
                        }.getType();

                        productDetail = gson.fromJson(response.toString(), listType);
                        if (productDetail.getOrders() == null || productDetail.getOrders().size() == 0) {
                            noDataFound.setVisibility(View.VISIBLE);
                            recyclerView.setVisibility(View.GONE);
                        } else {
                            noDataFound.setVisibility(View.GONE);
                            recyclerView.setVisibility(View.VISIBLE);
                            LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
                            layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
                            total_order_text.setText("Total : "+productDetail.getOrders().size()+" Order ");
                            orderHistoryAdapter = new OrderHistoryAdapter(getActivity(), productDetail.getOrders());
                            recyclerView.setAdapter(orderHistoryAdapter);
                            recyclerView.setLayoutManager(layoutManager);

                        }
                    }
                }catch (JSONException e) {
                    throw new RuntimeException(e);
                }


            }
        }, error -> {
            gif_loader.setVisibility(View.GONE);
            noDataFound.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        });
        queue.add(jsonObjectRequest);
    }

}