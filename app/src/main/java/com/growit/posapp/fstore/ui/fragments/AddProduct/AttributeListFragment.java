package com.growit.posapp.fstore.ui.fragments.AddProduct;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
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


import com.growit.posapp.fstore.adapters.AttributeAdapter;
import com.growit.posapp.fstore.databinding.FragmentAttributeListBinding;
import com.growit.posapp.fstore.model.AttributeModel;
import com.growit.posapp.fstore.model.AttributeValue;
import com.growit.posapp.fstore.model.ListAttributesModel;
import com.growit.posapp.fstore.utils.ApiConstants;
import com.growit.posapp.fstore.utils.SessionManagement;
import com.growit.posapp.fstore.utils.Utility;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;


public class AttributeListFragment extends Fragment {


    FragmentAttributeListBinding binding;
    AttributeAdapter adapter;

    Activity contexts;
    List<AttributeModel> model = new ArrayList<>();
    ListAttributesModel model_attribute;
    public AttributeListFragment() {
        // Required empty public constructor
    }

    public static AttributeListFragment newInstance() {
        return new AttributeListFragment();
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        binding= FragmentAttributeListBinding.inflate(inflater, container, false);

        init();
        return binding.getRoot();
    }
    private void init() {
        Glide.with(getActivity()).load(R.drawable.growit_gif_02).into(binding.gifLoad);
        binding.gifLoad.setVisibility(View.VISIBLE);
        model = new ArrayList<>();
        GridLayoutManager layoutManager = new GridLayoutManager(getActivity(), 1, LinearLayoutManager.VERTICAL, false);
        binding.recyclerAtt.setLayoutManager(layoutManager);


        if (Utility.isNetworkAvailable(getContext())) {
            getAttributeList();
        } else {
            Toast.makeText(getContext(), R.string.NETWORK_GONE, Toast.LENGTH_SHORT).show();

        }
//        binding.refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
//            @Override
//            public void onRefresh() {
//                binding.refreshLayout.setRefreshing(false);
//                if (Utility.isNetworkAvailable(getContext())) {
//                    binding.gifLoad.setVisibility(View.VISIBLE);
//                    getAttributeList();
//                } else {
//                    Toast.makeText(getContext(), R.string.NETWORK_GONE, Toast.LENGTH_SHORT).show();
//
//                }
//
//            }
//        });
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
                Fragment fragment = CreateAttributeFragment.newInstance();
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.flContent, fragment).commit();
            }
        });





    }

    private void getAttributeList() {
        SessionManagement sm = new SessionManagement(getActivity());
        RequestQueue queue = Volley.newRequestQueue(getActivity());
                String url = ApiConstants.BASE_URL + ApiConstants.GET_ATTRIBUTES_LIST + "user_id=" + sm.getUserID() +"&" + "token=" + sm.getJWTToken();
       // String url = ApiConstants.BASE_URL + ApiConstants.GET_ATTRIBUTES_LIST;
//        Utility.showDialoge("Please wait while a moment...", getActivity());
        binding.gifLoad.setVisibility(View.VISIBLE);
        Log.d("product_list",url);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.v("Response", response.toString());
                JSONObject obj = null;
                try {

                    obj = new JSONObject(response.toString());
                    int statusCode = obj.optInt("statuscode");
                    String status = obj.optString("status");
                    binding.gifLoad.setVisibility(View.GONE);
                    if (statusCode == 200 && status.equalsIgnoreCase("success")) {
//                        Utility.dismissDialoge();
                        model.clear();
                        JSONArray jsonArray = obj.getJSONArray("attributes");
                        Gson gson = new Gson();
                        Type listType = new TypeToken<ListAttributesModel>() {
                        }.getType();

                        model_attribute = gson.fromJson(response.toString(), listType);
                        model.addAll(model_attribute.getAttributes());

                        if (model_attribute.getAttributes() == null || model_attribute.getAttributes().size() == 0) {
                                binding.noDataFound.setVisibility(View.GONE);
                            } else {
                            binding.totalCustomerText.setText("Total: " + model_attribute.getAttributes().size() + " " + "Attributes");
                                binding.noDataFound.setVisibility(View.GONE);
                                LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
                                layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
                                adapter = new AttributeAdapter(getActivity(), model);
                                binding.recyclerAtt.setAdapter(adapter);
                                binding.recyclerAtt.setLayoutManager(layoutManager);

                            }

                    }
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
        }, error -> Toast.makeText(getActivity(), R.string.JSONDATA_NULL, Toast.LENGTH_SHORT).show());
        binding.gifLoad.setVisibility(View.GONE);
//        Utility.dismissDialoge();
        queue.add(jsonObjectRequest);

    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        contexts = getActivity();

    }


}