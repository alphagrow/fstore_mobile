package com.growit.posapp.fstore.ui.fragments.Inventory;

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
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.growit.posapp.fstore.MainActivity;
import com.growit.posapp.fstore.R;
import com.growit.posapp.fstore.adapters.PurchaseOrderAdapter;
import com.growit.posapp.fstore.adapters.TransfersOrderAdapter;
import com.growit.posapp.fstore.databinding.FragmentPurchaseOrderListBinding;
import com.growit.posapp.fstore.databinding.FragmentTransfersOrderListBinding;
import com.growit.posapp.fstore.model.Purchase.PurchaseModel;
import com.growit.posapp.fstore.model.Purchase.PurchaseOrder;
import com.growit.posapp.fstore.model.TransfersModel;
import com.growit.posapp.fstore.model.TransfersModelList;
import com.growit.posapp.fstore.ui.fragments.PurchaseOrder.PurchaseOrderDetailFragment;
import com.growit.posapp.fstore.ui.fragments.PurchaseOrder.PurchaseOrderListFragment;
import com.growit.posapp.fstore.utils.ApiConstants;
import com.growit.posapp.fstore.utils.RecyclerItemClickListener;
import com.growit.posapp.fstore.utils.SessionManagement;
import com.growit.posapp.fstore.utils.Utility;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;


public class TransfersOrderListFragment extends Fragment {

    FragmentTransfersOrderListBinding binding;
    TransfersModel model;
    TransfersOrderAdapter adapter;
    private  String mResponse="";


    public TransfersOrderListFragment() {
        // Required empty public constructor
    }




    public static TransfersOrderListFragment newInstance() {
        return new TransfersOrderListFragment();
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
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_transfers_order_list, container, false);
        init();
        return binding.getRoot();
    }
    private  void init(){
        binding.toolbarLay.setVisibility(View.VISIBLE);

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
                        Fragment fragment = TransfersDetailsFragment.newInstance();
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
        String url = ApiConstants.BASE_URL + ApiConstants.GET_TRANSFER_LIST;
        //   String url = ApiConstants.BASE_URL + ApiConstants.GET_TRANSFER_LIST + "user_id=" + sm.getUserID() + "&" + "token=" + sm.getJWTToken();
        Log.v("url", url);
        Utility.showDialoge("Please wait while a moment...", getActivity());
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
                   // if (statusCode == 200 && status.equalsIgnoreCase("success")) {
                    if (status.equalsIgnoreCase("success")) {
                        Utility.dismissDialoge();
                        Gson gson = new Gson();
                        Type listType = new TypeToken<TransfersModel>() {
                        }.getType();

                        model = gson.fromJson(response.toString(), listType);
                        if (model.getData() == null || model.getData().size() == 0) {
                            binding.noDataFound.setVisibility(View.VISIBLE);
                            binding.transactionRecyclerView.setVisibility(View.GONE);

                        } else {
                            binding.noDataFound.setVisibility(View.GONE);
                            binding.transactionRecyclerView.setVisibility(View.VISIBLE);
                            LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
                            layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
                            adapter = new TransfersOrderAdapter(getActivity(), model.getData());
                            binding.transactionRecyclerView.setAdapter(adapter);
                            binding.transactionRecyclerView.setLayoutManager(layoutManager);

                        }

                    }
                }catch (JSONException e) {
                    throw new RuntimeException(e);
                }


            }
        }, error -> {
            binding.noDataFound.setVisibility(View.VISIBLE);
            binding.transactionRecyclerView.setVisibility(View.GONE);
        });
        queue.add(jsonObjectRequest);
    }
    private void filterList(String text){
        ArrayList<TransfersModelList> model_list = new ArrayList<>();
        for (TransfersModelList detail : model.getData()){
            if (detail.getName().toLowerCase().contains(text.toLowerCase()) || detail.getOrigin().toLowerCase().contains(text.toLowerCase())){
                model_list.add(detail);
            }
        }

        adapter.updateList(model_list);
    }


}