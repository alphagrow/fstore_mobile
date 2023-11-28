package com.growit.posapp.fstore.ui.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
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
import com.growit.posapp.fstore.MainActivity;
import com.growit.posapp.fstore.R;
import com.growit.posapp.fstore.adapters.AddProductListAdapter;
import com.growit.posapp.fstore.adapters.AttributeAdapter;
import com.growit.posapp.fstore.adapters.POSAdapter;
import com.growit.posapp.fstore.databinding.FragmentAttributeListBinding;
import com.growit.posapp.fstore.databinding.FragmentCreateAttributeBinding;
import com.growit.posapp.fstore.databinding.FragmentPOSCategoryListBinding;
import com.growit.posapp.fstore.model.AttributeValueModel;
import com.growit.posapp.fstore.model.StockInventoryModel;
import com.growit.posapp.fstore.model.Value;
import com.growit.posapp.fstore.ui.fragments.POSCategory.AddPOSCategoryFragment;
import com.growit.posapp.fstore.ui.fragments.POSCategory.POSCategoryListFragment;
import com.growit.posapp.fstore.utils.ApiConstants;
import com.growit.posapp.fstore.utils.SessionManagement;
import com.growit.posapp.fstore.utils.Utility;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class AttributeListFragment extends Fragment {


    FragmentAttributeListBinding binding;
    AttributeAdapter adapter;

    Activity contexts;
    List<AttributeValueModel> model = new ArrayList<>();
    private ArrayList<String> languageList;
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
        model = new ArrayList<>();
        GridLayoutManager layoutManager = new GridLayoutManager(getActivity(), 1, LinearLayoutManager.VERTICAL, false);
        binding.recyclerAtt.setLayoutManager(layoutManager);


        if (Utility.isNetworkAvailable(getContext())) {
            getAttributeList();
        } else {
            Toast.makeText(getContext(), R.string.NETWORK_GONE, Toast.LENGTH_SHORT).show();

        }
        binding.refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                binding.refreshLayout.setRefreshing(false);
                if (Utility.isNetworkAvailable(getContext())) {
                    getAttributeList();
                } else {
                    Toast.makeText(getContext(), R.string.NETWORK_GONE, Toast.LENGTH_SHORT).show();

                }

            }
        });
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


        languageList = new ArrayList<>();
        // languages to our language list
        languageList.add("Java");
        languageList.add("Kotlin");
        languageList.add("C++");
        languageList.add("C");
        LinearLayout.LayoutParams txtLayoutParam = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT
        );

        txtLayoutParam.gravity = Gravity.CENTER;
        LinearLayout.LayoutParams spinnerLayoutParam = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        spinnerLayoutParam.gravity = Gravity.CENTER;
        TextView headingTV = new TextView(getActivity());
        headingTV.setText("Dynamic Spinner in Android");
        headingTV.setTextSize(20f);
        headingTV.setTextColor(getResources().getColor(R.color.black));
        headingTV.setTypeface(Typeface.DEFAULT_BOLD);
        headingTV.setPadding(20, 20, 20, 20);
        headingTV.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        headingTV.setLayoutParams(txtLayoutParam);
        Spinner spinner = new Spinner(getActivity());
        spinner.setLayoutParams(spinnerLayoutParam);
        binding.idLLContainer.addView(headingTV);
        binding.idLLContainer.addView(spinner);
        if (spinner != null) {
            ArrayAdapter adapter = new ArrayAdapter(getActivity(), android.R.layout.simple_spinner_item, languageList);
            spinner.setAdapter(adapter);
            spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    // in on selected listener we are displaying a toast message
                    Toast.makeText(getActivity(), "Selected Language is : " + languageList.get(position), Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
        }


    }

    private void getAttributeList() {
        SessionManagement sm = new SessionManagement(getActivity());
        RequestQueue queue = Volley.newRequestQueue(getActivity());
        //        String url = ApiConstants.BASE_URL + ApiConstants.GET_PRODUCT_LIST + "user_id=" + sm.getUserID() + "&" + "pos_category_id=" + id + "&" + "token=" + sm.getJWTToken();
        String url = ApiConstants.BASE_URL + ApiConstants.GET_ATTRIBUTES_LIST;
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
                    if (statusCode == 200 && status.equalsIgnoreCase("success")) {
                        JSONArray jsonArray = obj.getJSONArray("attributes");

                        if (jsonArray.length() > 0) {
                            for (int i = 0; i < jsonArray.length(); i++) {
                                Value cropPattern = new Value();
                                JSONObject data = jsonArray.getJSONObject(i);
                                Integer id = data.optInt("id");
                                String name = data.optString("name");
                                //AttributeValueModel   value = data.optJSONObject("values");

                            }

                            Log.d("jsonArray",jsonArray.toString());

//                            if (productList == null || productList.size() == 0) {
//                                binding.noItem.setVisibility(View.GONE);
//                            } else {
//                                binding.noItem.setVisibility(View.GONE);
//
//                                LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
//                                layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
//                                adapter = new AttributeAdapter(getActivity(), productList);
//                                binding.recyclerAttribute.setAdapter(adapter);
//                                binding.recyclerAttribute.setLayoutManager(layoutManager);
//
//                            }
                        }
                    }
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
        }, error -> Toast.makeText(getActivity(), R.string.JSONDATA_NULL, Toast.LENGTH_SHORT).show());
        queue.add(jsonObjectRequest);

    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        contexts = getActivity();

    }


}