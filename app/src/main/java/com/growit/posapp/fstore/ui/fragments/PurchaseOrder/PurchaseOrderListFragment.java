package com.growit.posapp.fstore.ui.fragments.PurchaseOrder;

import android.content.Intent;
import android.os.Bundle;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.text.Editable;
import android.text.TextWatcher;
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
import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.growit.posapp.fstore.MainActivity;
import com.growit.posapp.fstore.R;
import com.growit.posapp.fstore.adapters.PurchaseOrderAdapter;
import com.growit.posapp.fstore.databinding.FragmentPurchaseOrderListBinding;
import com.growit.posapp.fstore.model.Purchase.PurchaseModel;
import com.growit.posapp.fstore.model.Purchase.PurchaseOrder;
import com.growit.posapp.fstore.utils.ApiConstants;
import com.growit.posapp.fstore.utils.RecyclerItemClickListener;
import com.growit.posapp.fstore.utils.SessionManagement;
import com.growit.posapp.fstore.utils.Utility;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;


public class PurchaseOrderListFragment extends Fragment {

  FragmentPurchaseOrderListBinding binding;
    PurchaseModel model;
    PurchaseOrderAdapter adapter;
    private  String mResponse="";
    public PurchaseOrderListFragment() {
        // Required empty public constructor
    }


    public static PurchaseOrderListFragment newInstance() {
        return new PurchaseOrderListFragment();
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
//        return inflater.inflate(R.layout.fragment_purchase_order_list, container, false);
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_purchase_order_list, container, false);
      init();
        return binding.getRoot();
    }
    private  void init(){
        binding.toolbarLay.setVisibility(View.VISIBLE);
        Glide.with(getActivity()).load(R.drawable.growit_gif_02).into(binding.gif);
        binding.gif.setVisibility(View.VISIBLE);
        GridLayoutManager layoutManager = new GridLayoutManager(getActivity(), 1, LinearLayoutManager.VERTICAL, false);
        binding.transactionRecyclerView.setLayoutManager(layoutManager);
        if (Utility.isNetworkAvailable(getContext())) {
            getPurchaseList();
        } else {
            Toast.makeText(getContext(), R.string.NETWORK_GONE, Toast.LENGTH_SHORT).show();

        }
        binding.noDataFound.setOnClickListener(v -> getPurchaseList());
        binding.transactionRecyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(getActivity(),  binding.transactionRecyclerView, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        Bundle bundle = new Bundle();
                        bundle.putString("OrderDetail", mResponse);
                        bundle.putInt("position", position);
                        Fragment fragment = PurchaseOrderDetailFragment.newInstance();
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
        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), MainActivity.class));
                getActivity().finish();
            }
        });
        binding.searchTool.addTextChangedListener(new TextWatcher() {
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

    }
    private void getPurchaseList(){
        SessionManagement sm = new SessionManagement(getActivity());
        RequestQueue queue = Volley.newRequestQueue(getActivity());
        String url = ApiConstants.BASE_URL + ApiConstants.POST_PURCHASE_ORDER_LIST + "user_id=" + sm.getUserID() + "&" + "token=" + sm.getJWTToken();
        Log.v("url", url);
//        Utility.showDialoge("Please wait while a moment...", getActivity());
        binding.gif.setVisibility(View.VISIBLE);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.v("Response", response.toString());
                mResponse=response.toString();
                JSONObject obj = null;
                try {
                    obj = new JSONObject(response.toString());
                    int statusCode = obj.optInt("statuscode");
                    String status = obj.optString("status");
                    binding.gif.setVisibility(View.GONE);
                    if (statusCode == 200 && status.equalsIgnoreCase("success")) {
//                        Utility.dismissDialoge();
                        Gson gson = new Gson();
                        Type listType = new TypeToken<PurchaseModel>() {
                        }.getType();

                        model = gson.fromJson(response.toString(), listType);
                        if (model.getOrders() == null || model.getOrders().size() == 0) {
                            binding.noDataFound.setVisibility(View.VISIBLE);
                            binding.transactionRecyclerView.setVisibility(View.GONE);

                        } else {
                            binding.noDataFound.setVisibility(View.GONE);
                            binding.transactionRecyclerView.setVisibility(View.VISIBLE);
                            LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
                            layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
                            adapter = new PurchaseOrderAdapter(getActivity(), model.getOrders());
                            binding.transactionRecyclerView.setAdapter(adapter);
                            binding.transactionRecyclerView.setLayoutManager(layoutManager);

                        }

                    }
                }catch (JSONException e) {
                    throw new RuntimeException(e);
                }


            }
        }, error -> {
            binding.gif.setVisibility(View.GONE);
            binding.noDataFound.setVisibility(View.VISIBLE);
            binding.transactionRecyclerView.setVisibility(View.GONE);
        });
        queue.add(jsonObjectRequest);
    }
    private void filterList(String text){
        ArrayList<PurchaseOrder> model_list = new ArrayList<>();
        for (PurchaseOrder detail : model.getOrders()){
            if (detail.getName().toLowerCase().contains(text.toLowerCase()) || detail.getPartnerId().toLowerCase().contains(text.toLowerCase())){
                model_list.add(detail);
            }
        }

        adapter.updateList(model_list);
    }


}